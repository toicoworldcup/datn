// src/app/cth/cth.component.ts

import { Component, OnInit } from '@angular/core';
import { CTDTService } from '../../../services/ctdt.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Ctdt } from '../../../models/ctdt.model';
import { CthDTO } from '../../../models/CthDTO.model';
import { NgxPaginationModule } from 'ngx-pagination'; // Import module phân trang

@Component({
  selector: 'app-cth',
  standalone: true,
  imports: [CommonModule, FormsModule, NgxPaginationModule],
  templateUrl: './cth.component.html',
  styleUrls: ['./cth.component.css']
})
export class CthComponent implements OnInit {
  // Modal visibility flags
  isCreateCtdtModalVisible: boolean = false;
  isImportExcelModalVisible: boolean = false;
  // BẮT ĐẦU THAY ĐỔI: Thêm biến mới cho modal tìm kiếm
  isSearchCriteriaModalVisible: boolean = false; // Biến điều khiển hiển thị modal tìm kiếm
  // KẾT THÚC THAY ĐỔI

  // Dữ liệu tạo mới CTĐT
  newCtdtName: string = '';
  newCtdtMaCt: string = '';
  newCtdtKhoa: string = '';
  createCtdtMessage: string = '';
  createCtdtError: string = '';

  // Dữ liệu import Excel
  selectedFile: File | null = null;
  ctdtCodeExcel: string = '';
  khoaExcel: string = '';
  uploadMessage: string = '';
  uploadError: string = '';

  // Dữ liệu hiển thị học phần
  ctdtCodeSearchFilter: string = '';
  khoaSearchFilter: string = '';
  courses: CthDTO[] = [];
  filteredCourses: CthDTO[] = [];
  searchError: string = '';
  ctdtList: Ctdt[] = [];
  p: number = 1; // Trang hiện tại cho phân trang

  constructor(private ctdtService: CTDTService) {}

  ngOnInit(): void {
    this.loadCTDTs();
    // GIỮ NGUYÊN searchCourses() ở ngOnInit() nếu bạn muốn bảng hiển thị dữ liệu mặc định ban đầu
    // Nếu bạn muốn bảng trống và chỉ hiển thị sau khi người dùng tìm kiếm qua modal, thì hãy comment/xóa dòng này.
    this.searchCourses();
  }

  loadCTDTs() {
    this.ctdtService.getAllCTDTs().subscribe({
      next: (data: Ctdt[]) => {
        this.ctdtList = data;
      },
      error: (error) => {
        console.error('Lỗi khi tải danh sách CTĐT', error);
        this.searchError = 'Lỗi khi tải danh sách CTĐT';
      }
    });
  }

  // Các hàm điều khiển hiển thị modal (GIỮ NGUYÊN)
  openCreateCtdtModal(): void {
    this.isCreateCtdtModalVisible = true;
    this.createCtdtMessage = '';
    this.createCtdtError = '';
  }

  closeCreateCtdtModal(): void {
    this.isCreateCtdtModalVisible = false;
  }

  openImportExcelModal(): void {
    this.isImportExcelModalVisible = true;
    this.uploadMessage = '';
    this.uploadError = '';
  }

  closeImportExcelModal(): void {
    this.isImportExcelModalVisible = false;
  }

  // BẮT ĐẦU THAY ĐỔI: Thêm phương thức mới cho modal tìm kiếm
  openSearchCriteriaModal(): void {
    this.isSearchCriteriaModalVisible = true;
    this.searchError = ''; // Xóa thông báo lỗi cũ khi mở modal
    // Có thể giữ nguyên giá trị ctdtCodeSearchFilter và khoaSearchFilter để người dùng không phải nhập lại
    // Nếu bạn muốn các ô input trong modal luôn trống khi mở:
    // this.ctdtCodeSearchFilter = '';
    // this.khoaSearchFilter = '';
  }

  closeSearchCriteriaModal(): void {
    this.isSearchCriteriaModalVisible = false;
  }

  // Phương thức để gọi searchCourses() và đóng modal sau khi tìm kiếm
  performSearchAndCloseModal(): void {
    // Gọi phương thức searchCourses() hiện có của bạn
    this.searchCourses();
    // Đóng modal sau khi tìm kiếm
    this.closeSearchCriteriaModal();
  }
  // KẾT THÚC THAY ĐỔI

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  uploadFile(): void {
    if (this.selectedFile && this.ctdtCodeExcel && this.khoaExcel) {
      this.uploadMessage = 'Đang tải lên...';
      this.uploadError = '';
      this.ctdtService.importCoursesFromExcel(this.ctdtCodeExcel, this.khoaExcel, this.selectedFile).subscribe({
        next: (response: any) => {
          this.uploadMessage = response; // Gán trực tiếp response (có thể là text)
          this.selectedFile = null;
          this.ctdtCodeExcel = '';
          this.khoaExcel = '';
          // Có thể gọi searchCourses() ở đây để làm mới bảng sau khi import
          this.searchCourses();
          this.closeImportExcelModal();
        },
        error: (error) => {
          console.error('Lỗi tải lên file', error);
          this.uploadError = 'Lỗi tải lên file: ' + (error.error?.message || error.message);
          this.uploadMessage = '';
        }
      });
    } else {
      this.uploadError = 'Vui lòng chọn file, mã CTĐT và khóa.';
      this.uploadMessage = '';
    }
  }

  createCtdt(): void {
    if (this.newCtdtName && this.newCtdtMaCt && this.newCtdtKhoa) {
      this.createCtdtMessage = 'Đang tạo mới...';
      this.createCtdtError = '';
      this.ctdtService.addCTDT({
        name: this.newCtdtName,
        maCt: this.newCtdtMaCt,
        khoa: this.newCtdtKhoa
      }).subscribe({
        next: (response: Ctdt) => {
          this.createCtdtMessage = `Tạo mới CTĐT "${response.name}" thành công!`;
          this.newCtdtName = '';
          this.newCtdtMaCt = '';
          this.newCtdtKhoa = '';
          this.closeCreateCtdtModal();
          this.loadCTDTs(); // Tải lại danh sách CTĐT sau khi tạo mới (nếu cần)
          this.searchCourses(); // Tải lại danh sách học phần sau khi tạo CTĐT mới (nếu cần)
        },
        error: (error) => {
          console.error('Lỗi khi tạo mới CTĐT', error);
          this.createCtdtError = 'Lỗi tạo mới CTĐT: ' + (error.error?.message || error.message);
          this.createCtdtMessage = '';
        }
      });
    } else {
      this.createCtdtError = 'Vui lòng nhập đầy đủ thông tin CTĐT.';
      this.createCtdtMessage = '';
    }
  }

  // PHƯƠNG THỨC NÀY ĐƯỢC GIỮ NGUYÊN, KHÔNG THAY ĐỔI NỘI DUNG
  searchCourses(): void {
    if (this.ctdtCodeSearchFilter && this.khoaSearchFilter) {
      this.searchError = '';
      this.ctdtService.getCoursesByMaCTAndKhoa(this.ctdtCodeSearchFilter, this.khoaSearchFilter).subscribe({
        next: (courses: CthDTO[]) => {
          this.courses = courses;
          this.filteredCourses = [...courses]; // Copy để không ảnh hưởng đến mảng gốc
          this.p = 1; // Reset về trang đầu tiên
        },
        error: (error) => {
          console.error('Lỗi khi tải học phần', error);
          this.searchError = 'Lỗi khi tải học phần: ' + (error.error?.message || error.message);
          this.courses = [];
          this.filteredCourses = [];
        }
      });
    } else {
      // BẮT ĐẦU THAY ĐỔI: Thêm điều kiện để xử lý khi filter rỗng (ví dụ, khi mở modal mà không nhập gì)
      // Nếu không có filter, bạn có thể quyết định hiển thị tất cả hoặc không hiển thị gì.
      // Với yêu cầu "đừng thay đổi phương thức", chúng ta sẽ giữ nguyên hành vi cũ là không hiển thị gì.
      this.searchError = 'Vui lòng nhập Mã CTĐT và Khóa để tìm kiếm.'; // Thêm lỗi nếu rỗng
      this.filteredCourses = [];
      // KẾT THÚC THAY ĐỔI
    }
  }
}