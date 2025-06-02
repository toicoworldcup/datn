import { Component, OnInit } from '@angular/core';
import { ClazzService } from '../../../services/clazz.service';
import { TeacherService } from '../../../services/teacher.service';
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { Clazz } from '../../../models/clazz.model';
import { Teacher } from '../../../models/teacher.model';

@Component({
  selector: 'app-clazz-list',
  templateUrl: './clazz-list.component.html',
  styleUrls: ['./clazz-list.component.css'],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class ClazzListComponent implements OnInit {
  clazzes: Clazz[] = [];
  teachers: Teacher[] = [];
  p: number = 1;
  isAddClazzModalVisible: boolean = false;
  newClazz: Clazz = { maLop: '', maHocPhan: '', hocki: '' };

  // Các biến cho tìm kiếm
  isSearchClazzModalVisible: boolean = false;
  searchMaHocPhan: string = '';
  searchHocki: string = '';
  searchClazzResults: Clazz[] | null = null;
  searchClazzError: string = '';

  constructor(
    private clazzService: ClazzService,
    private teacherService: TeacherService
  ) { }

  ngOnInit(): void {
    this.loadClazzes();
    this.loadTeachers(); // Tải danh sách giáo viên để có thể hiển thị tên
  }

  loadClazzes(): void {
    this.clazzService.getAllClazzes().subscribe(
      (data: Clazz[]) => {
        this.clazzes = data;
      },
      (error) => {
        console.error('Lỗi khi tải danh sách lớp học:', error);
      }
    );
  }

  loadTeachers(): void {
    this.teacherService.getAllTeachers().subscribe(
      (data: Teacher[]) => {
        this.teachers = data;
      },
      (error) => {
        console.error('Lỗi khi tải danh sách giáo viên:', error);
      }
    );
  }

  toggleAddClazzModal(): void {
    this.isAddClazzModalVisible = !this.isAddClazzModalVisible;
    this.newClazz = { maLop: '', maHocPhan: '', hocki: '' }; // Reset form khi mở
  }

  addClazz(): void {
    this.clazzService.addClazz(this.newClazz).subscribe(
      (response) => {
        console.log('Thêm lớp thành công:', response);
        this.loadClazzes(); // Tải lại danh sách lớp sau khi thêm
        this.toggleAddClazzModal(); // Đóng modal
      },
      (error) => {
        console.error('Lỗi khi thêm lớp:', error);
      }
    );
  }

  openSearchClazzModal(): void {
    this.isSearchClazzModalVisible = true;
    this.searchMaHocPhan = '';
    this.searchHocki = '';
    this.searchClazzResults = null;
    this.searchClazzError = '';
  }

  closeSearchClazzModal(): void {
    this.isSearchClazzModalVisible = false;
    this.searchClazzResults = null; // Reset kết quả tìm kiếm khi đóng modal
    this.searchClazzError = '';
    this.loadClazzes(); // Tải lại toàn bộ danh sách khi đóng modal
  }

  searchClazzes(): void {
    if (!this.searchMaHocPhan.trim() || !this.searchHocki.trim()) {
      this.searchClazzError = 'Vui lòng nhập Mã Học Phần và Học Kỳ.';
      this.searchClazzResults = null;
      this.clazzes = [...this.clazzes]; // Giữ nguyên danh sách hiện tại
      return;
    }

    this.searchClazzError = '';
    this.clazzService.searchClazzes(this.searchMaHocPhan, this.searchHocki).subscribe(
      (results: Clazz[]) => {
        this.clazzes = results; // Gán trực tiếp kết quả vào mảng clazzes để hiển thị ở bảng chính
        this.p = 1; // Reset về trang đầu tiên của phân trang
        this.isSearchClazzModalVisible = false; // Đóng modal sau khi tìm kiếm thành công
        this.searchClazzResults = null; // Không cần hiển thị kết quả riêng trong modal nữa
        if (results && results.length === 0) {
          this.searchClazzError = 'Không tìm thấy lớp học nào phù hợp với thông tin đã nhập.';
        }
      },
      (error: any) => {
        console.error('Lỗi khi tìm kiếm lớp học:', error);
        this.searchClazzError = 'Đã xảy ra lỗi khi tìm kiếm lớp học.';
        this.searchClazzResults = null;
        this.clazzes = []; // Xóa dữ liệu hiện tại trong bảng khi có lỗi
      }
    );
  }

  getTeacherName(teachers: any[] | undefined): string {
    if (!teachers || teachers.length === 0) {
      return 'Chưa có';
    }
    const teacherNames = teachers.map(teacher => teacher.name);
    return teacherNames.join(', '); // Nối tên các giáo viên bằng dấu phẩy và khoảng trắng
  }
}