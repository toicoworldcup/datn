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
    this.searchCourses(); // Tải học phần ban đầu (có thể điều chỉnh logic này)
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

  // Các hàm điều khiển hiển thị modal
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
        },
        error: (error) => {
          console.error('Lỗi tải lên file', error);
          this.uploadError = 'Lỗi tải lên file: ' + (error.error.message || error.message);
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
          this.searchCourses(); // Tải lại danh sách học phần sau khi tạo CTĐT mới (nếu cần)
        },
        error: (error) => {
          console.error('Lỗi khi tạo mới CTĐT', error);
          this.createCtdtError = 'Lỗi tạo mới CTĐT: ' + (error.error.message || error.message);
          this.createCtdtMessage = '';
        }
      });
    } else {
      this.createCtdtError = 'Vui lòng nhập đầy đủ thông tin CTĐT.';
      this.createCtdtMessage = '';
    }
  }

  searchCourses(): void {
    if (this.ctdtCodeSearchFilter && this.khoaSearchFilter) {
      this.searchError = '';
      this.ctdtService.getCoursesByMaCTAndKhoa(this.ctdtCodeSearchFilter, this.khoaSearchFilter).subscribe({
        next: (courses: CthDTO[]) => {
          this.courses = courses;
          this.filteredCourses = [...courses]; // Copy để không ảnh hưởng đến mảng gốc
        },
        error: (error) => {
          console.error('Lỗi khi tải học phần', error);
          this.searchError = 'Lỗi khi tải học phần: ' + (error.error.message || error.message);
          this.courses = [];
          this.filteredCourses = [];
        }
      });
    } else {
      this.filteredCourses = []; // Hoặc có thể tải tất cả nếu không có filter
    }
  }
}