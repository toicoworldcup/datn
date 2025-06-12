// src/app/course-list/course-list.component.ts

import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { CourseService } from "../../../services/course.service";
import { Course } from "../../../models/course.model";
// Import CourseRequest và CourseDTO nếu bạn đang sử dụng chúng trong service
// import { CourseRequest } from "../../../models/course-request.model";
// import { CourseDTO } from "../../../models/course-dto.model";

@Component({
  selector: "app-course-list",
  templateUrl: "./course-list.component.html",
  styleUrls: ["./course-list.component.css"],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class CourseListComponent implements OnInit {
  courses: Course[] = [];
  searchTerm: string = "";
  p: number = 1;
  mahocphanSearch: string = "";

  isSearchVisible = false;
  isSearchModalVisible = false;
  searchResultCourse: Course | null = null;
  searchError: string = '';

  isAdvancedSearchModalVisible = false;
  searchProgram: string = '';
  searchKhoa: string = '';
  searchAdvancedError: string = '';

  isCombinedSearchModalVisible = false;

  isAddCourseModalVisible: boolean = false;
  newCourse: Course = { maHocPhan: '', tenMonHoc: '', soTinChi: 0, khoiLuong: '', suggestedSemester: 0, gradeRatio: '' };

  // --- Biến mới cho chức năng Sửa ---
  isEditCourseModalVisible: boolean = false;
  editedCourse: Course | null = null; // Đối tượng Course đang được chỉnh sửa

  // --- Biến thông báo chung cho tất cả các thao tác (Thêm/Sửa/Xóa) ---
  successMessage: string = ''; // Thông báo thành công
  errorMessage: string = '';   // Thông báo lỗi

  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    this.loadCourses();
  }

  // --- Phương thức quản lý thông báo chung ---
  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }

  private showSuccessMessage(message: string): void {
    this.successMessage = message;
    setTimeout(() => this.successMessage = '', 5000); // Tự động ẩn sau 5 giây
  }

  private showErrorMessage(message: string): void {
    this.errorMessage = message;
    setTimeout(() => this.errorMessage = '', 7000); // Tự động ẩn sau 7 giây
  }

  // --- Chức năng Thêm Học phần ---
  openAddCourseModal(): void {
    this.clearMessages(); // Xóa các thông báo cũ khi mở modal
    this.isAddCourseModalVisible = true;
    this.newCourse = { maHocPhan: '', tenMonHoc: '', soTinChi: 0, khoiLuong: '', suggestedSemester: 0, gradeRatio: '' };
  }

  closeAddCourseModal(): void {
    this.isAddCourseModalVisible = false;
    this.clearMessages(); // Xóa thông báo lỗi trong modal khi đóng
  }

  addCourse(): void {
    this.clearMessages(); // Reset thông báo trước khi gửi request mới

    if (!this.newCourse.maHocPhan || !this.newCourse.tenMonHoc || this.newCourse.soTinChi === null || this.newCourse.soTinChi <= 0) {
      this.showErrorMessage('Vui lòng nhập đầy đủ thông tin và số tín chỉ phải lớn hơn 0.');
      return;
    }

    this.courseService.createCourse(this.newCourse).subscribe({
      next: (responseCourse) => {
        this.showSuccessMessage('Thêm học phần "' + responseCourse.tenMonHoc + '" thành công!');
        this.loadCourses();
        this.closeAddCourseModal();
      },
      error: (error) => {
        console.error('Lỗi khi thêm học phần:', error);
        let errorMessage = 'Có lỗi xảy ra khi thêm học phần. Vui lòng thử lại.';
        if (error.error) {
            if (typeof error.error === 'string') {
                errorMessage = error.error;
            } else if (error.error.message) {
                errorMessage = error.error.message;
            } else if (error.error.error) {
              errorMessage = error.error.error + ': ' + error.error.message;
            }
        } else if (error.message) {
          errorMessage = error.message;
        }
        this.showErrorMessage(errorMessage);
      }
    });
  }

  // --- Chức năng Sửa Học phần ---
  openEditCourseModal(course: Course): void {
    this.clearMessages(); // Xóa các thông báo cũ khi mở modal
    this.isEditCourseModalVisible = true;
    // Tạo một bản sao sâu của đối tượng để tránh chỉnh sửa trực tiếp dữ liệu trong bảng
    this.editedCourse = { ...course };
  }

  closeEditCourseModal(): void {
    this.isEditCourseModalVisible = false;
    this.editedCourse = null;
    this.clearMessages(); // Xóa thông báo lỗi trong modal khi đóng
  }

  updateCourse(): void {
    this.clearMessages(); // Reset thông báo trước khi gửi request mới

    if (!this.editedCourse || !this.editedCourse.id) {
      this.showErrorMessage('Không tìm thấy học phần để cập nhật.');
      return;
    }
    if (!this.editedCourse.maHocPhan || !this.editedCourse.tenMonHoc || this.editedCourse.soTinChi === null || this.editedCourse.soTinChi <= 0) {
      this.showErrorMessage('Vui lòng nhập đầy đủ thông tin và số tín chỉ phải lớn hơn 0.');
      return;
    }

    this.courseService.updateCourse(this.editedCourse.id, this.editedCourse).subscribe({
      next: (responseCourse) => {
        this.showSuccessMessage('Cập nhật học phần "' + responseCourse.tenMonHoc + '" thành công!');
        this.loadCourses(); // Tải lại danh sách sau khi cập nhật
        this.closeEditCourseModal(); // Đóng modal sau khi thành công
      },
      error: (error) => {
        console.error('Lỗi khi cập nhật học phần:', error);
        let errorMessage = 'Có lỗi xảy ra khi cập nhật học phần. Vui lòng thử lại.';
        if (error.error) {
            if (typeof error.error === 'string') {
                errorMessage = error.error;
            } else if (error.error.message) {
                errorMessage = error.error.message;
            } else if (error.error.error) {
              errorMessage = error.error.error + ': ' + error.error.message;
            }
        } else if (error.message) {
          errorMessage = error.message;
        }
        this.showErrorMessage(errorMessage);
      }
    });
  }

  // --- Chức năng Xóa Học phần ---
  deleteCourse(id: number): void {
    this.clearMessages(); // Reset thông báo trước khi thực hiện xóa

    if (confirm('Bạn có chắc chắn muốn xóa học phần này không? Hành động này không thể hoàn tác!')) {
      this.courseService.deleteCourse(id).subscribe({
        next: () => {
          this.showSuccessMessage('Xóa học phần thành công!');
          this.loadCourses(); // Tải lại danh sách sau khi xóa
        },
        error: (error) => {
          console.error('Lỗi khi xóa học phần:', error);
          let errorMessage = 'Có lỗi xảy ra khi xóa học phần. Vui lòng thử lại.';
          if (error.status === 404) { // Xử lý lỗi 404 Not Found
            errorMessage = 'Không tìm thấy học phần để xóa. Có thể học phần đã bị xóa bởi người khác.';
          } else if (error.error) {
              if (typeof error.error === 'string') {
                  errorMessage = error.error;
              } else if (error.error.message) {
                  errorMessage = error.error.message;
              }
          }
          this.showErrorMessage(errorMessage);
        }
      });
    }
  }

  // --- Các phương thức tìm kiếm và tải danh sách (giữ nguyên hoặc có chút chỉnh sửa nhỏ về thông báo) ---
  openCombinedSearchModal(): void {
    this.clearMessages();
    this.isCombinedSearchModalVisible = true;
  }

  closeCombinedSearchModal(): void {
    this.isCombinedSearchModalVisible = false;
  }

  toggleAdvancedSearchModal(): void {
    this.clearMessages();
    this.isAdvancedSearchModalVisible = !this.isAdvancedSearchModalVisible;
    this.searchProgram = '';
    this.searchKhoa = '';
    this.searchAdvancedError = ''; // Có thể thay thế bằng this.errorMessage
    this.isCombinedSearchModalVisible = false;
  }

  searchCoursesByProgramAndKhoa(): void {
    this.clearMessages();
    this.searchAdvancedError = ''; // Có thể thay thế bằng this.errorMessage
    this.courseService.searchCoursesByCtdtAndKhoa(this.searchProgram, this.searchKhoa)
      .subscribe({
        next: (courses) => {
          if (courses && courses.length > 0) {
            this.courses = courses;
            this.isAdvancedSearchModalVisible = false;
            this.p = 1;
            this.searchResultCourse = null;
            this.isSearchModalVisible = false;
            this.showSuccessMessage(`Tìm thấy ${courses.length} học phần.`);
          } else {
            this.courses = [];
            this.showErrorMessage('Không tìm thấy học phần nào khớp với tiêu chí tìm kiếm nâng cao.');
          }
        },
        error: (error) => {
          this.showErrorMessage('Đã xảy ra lỗi khi tìm kiếm nâng cao.');
          console.error('Lỗi tìm kiếm nâng cao:', error);
          this.courses = [];
          this.searchResultCourse = null;
          this.isSearchModalVisible = false;
        }
      });
  }

  toggleSearch() { // Có vẻ phương thức này không còn được sử dụng trực tiếp trên HTML từ đoạn code bạn gửi
    this.clearMessages();
    this.isSearchVisible = !this.isSearchVisible;
    this.isSearchModalVisible = false;
    this.searchResultCourse = null;
    this.searchError = ''; // Có thể thay thế bằng this.errorMessage
    if (!this.isSearchVisible) {
      this.loadCourses();
    }
  }

  openSearchModal(): void {
    this.clearMessages();
    this.isSearchModalVisible = true;
    this.isSearchVisible = false;
    this.mahocphanSearch = '';
    this.searchResultCourse = null;
    this.searchError = ''; // Có thể thay thế bằng this.errorMessage
    this.isCombinedSearchModalVisible = false;
  }

  closeSearchModal(): void {
    this.isSearchModalVisible = false;
    this.loadCourses(); // Tải lại danh sách khi đóng modal tìm kiếm theo mã
    this.clearMessages(); // Xóa thông báo khi đóng modal
  }

  loadCourses(): void {
    this.clearMessages(); // Xóa thông báo khi tải lại danh sách
    this.courseService.getAllCourses().subscribe({
      next: (data) => {
        this.courses = data;
        console.log('Danh sách học phần đã tải:', data);
        if (data.length === 0) {
          this.showErrorMessage('Không có học phần nào trong hệ thống.');
        }
      },
      error: (error) => {
        console.error("Có lỗi khi tải danh sách học phần", error);
        this.showErrorMessage('Không thể tải danh sách học phần. Vui lòng thử lại sau.');
      },
    });
  }

  searchCourse(): void {
    this.clearMessages(); // Reset thông báo

    if (!this.mahocphanSearch.trim()) {
      // this.courses = [...this.courses]; // Không cần dòng này nếu chỉ muốn hiển thị lỗi
      this.showErrorMessage('Vui lòng nhập mã học phần để tìm kiếm.');
      return;
    }

    this.courseService.searchCourse(this.mahocphanSearch).subscribe({
      next: (course) => {
        if (course) {
          this.courses = [course];
          this.p = 1;
          this.isSearchModalVisible = false; // Đóng modal tìm kiếm nếu tìm thấy
          this.showSuccessMessage('Tìm thấy học phần có mã: ' + course.maHocPhan);
        } else {
          this.courses = [];
          this.showErrorMessage("Không tìm thấy học phần có mã: " + this.mahocphanSearch);
        }
        this.searchResultCourse = null; // Reset kết quả tìm kiếm chi tiết
      },
      error: (error) => {
        this.courses = [];
        this.showErrorMessage("Đã xảy ra lỗi khi tìm kiếm học phần.");
        console.error(error);
        this.searchResultCourse = null;
      },
    });
  }
}