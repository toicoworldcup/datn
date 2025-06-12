import { Component, OnInit } from "@angular/core";
import { StudentService } from "../../../services/student.service";
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { Semester } from "../../../models/semester.model";
import { Course } from "../../../models/course.model";
import { SemesterService } from "../../../services/semester.service";
import { DkhpDTO } from "../../../models/dkhp.model";
import { DkhpRequest } from "../../../models/dkhpRequest.model";

interface CourseWithSuggestion extends Course {
  suggestedSemester?: number;
}

interface CreateDkhpResponse {
  id: number;
  maHocPhan: string;
  mssv: string;
  nameCourse: string;
  semesterName: string;
  tinchi: number;
}

@Component({
  selector: "app-student-list",
  templateUrl: "./student-list.component.html",
  styleUrls: ["./student-list.component.css"],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class StudentListComponent implements OnInit {
  hocKyOptions: Semester[] = [];
  selectedSemester: string = ''; // Học kỳ được chọn trong modal đăng ký
  errorMessage: string = '';
  successMessage: string = '';
  availableCourses: CourseWithSuggestion[] = []; // Này vẫn dùng cho mục "Học phần chưa đăng ký trong CTĐT"
  registeredCourses: Course[] = [];
  missingCourses: Course[] = []; // Đây là biến chúng ta sẽ dùng cho bảng trong modal đăng ký

  maHocPhanDangKy: string = '';
  showRegisterModal: boolean = false;
  showUnregisteredModal: boolean = false; // Modal học phần chưa đăng ký trong CTĐT
  showRegisterForm: boolean = false;

  selectedSemesterForView: string = '';
  registeredCoursesInSelectedSemester: DkhpDTO[] = [];
  loadingRegisteredCourses: boolean = false;
  hasLoadedRegisteredCourses: boolean = false;

  constructor(
    private studentService: StudentService,
    private semesterService: SemesterService,
  ) { }

  ngOnInit(): void {
    this.loadHocKyOptions();
    // Bỏ gọi loadCtdtCourses() và loadMissingCourses() ở đây
    // vì chúng sẽ được gọi khi mở modal tương ứng để đảm bảo dữ liệu mới nhất.
    // this.loadCtdtCourses();
    // this.loadMissingCourses();
  }

  toggleRegisterForm(): void {
    this.showRegisterForm = !this.showRegisterForm;
  }

  loadHocKyOptions(): void {
    this.semesterService.getAllSemesters().subscribe({
      next: (data: Semester[]) => {
        this.hocKyOptions = data;
      },
      error: (error) => {
        this.errorMessage = 'Không thể tải danh sách học kỳ.';
        console.error('Lỗi tải danh sách học kỳ', error);
        this.hocKyOptions = [];
      }
    });
  }

  // Phương thức này có vẻ dành cho tổng quan học phần đã/chưa đăng ký trong CTĐT,
  // nên vẫn giữ nguyên nếu nó được dùng ở nơi khác trên trang chính.
  // Nếu chỉ dùng trong modal "Học phần chưa đăng ký trong CTĐT", có thể gọi nó trong openUnregisteredModal.
  loadCtdtCourses(): void {
    this.studentService.getCtdtCourses().subscribe({
      next: (data: { registered: Course[]; unregistered: CourseWithSuggestion[] }) => {
        this.registeredCourses = data.registered;
        this.availableCourses = data.unregistered.sort((a, b) => {
          const semesterA = a.suggestedSemester === undefined || a.suggestedSemester === null ? Infinity : a.suggestedSemester;
          const semesterB = b.suggestedSemester === undefined || b.suggestedSemester === null ? Infinity : b.suggestedSemester;
          return semesterA - semesterB;
        });
        console.log('Học phần chưa đăng ký (tổng hợp CTĐT):', this.availableCourses);
      },
      error: (error) => {
        this.errorMessage = 'Không thể tải danh sách học phần CTĐT.';
        console.error('Lỗi tải danh sách học phần CTĐT', error);
        this.availableCourses = [];
        this.registeredCourses = [];
      }
    });
  }

  loadMissingCourses(): void {
    this.studentService.getMissingCourses().subscribe({
      next: (data: Course[]) => {
        this.missingCourses = data;
        console.log('Học phần thiếu điểm:', this.missingCourses);
      },
      error: (error) => {
        this.errorMessage = 'Không thể tải danh sách học phần thiếu điểm.';
        console.error('Lỗi tải học phần thiếu điểm', error);
        this.missingCourses = [];
      }
    });
  }

  onDangKyHocPhan(maHocPhan: string): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (maHocPhan && this.selectedSemester) {
      const request: DkhpRequest = {
        maHocPhan: maHocPhan,
        semesterName: this.selectedSemester,
      };
      this.studentService.createDangkihocphan(request).subscribe({
        next: (response: CreateDkhpResponse) => {
          const msg = `Đăng ký học phần "${response.nameCourse}" (Mã HP: ${response.maHocPhan}) cho học kỳ "${response.semesterName}" thành công!`;
          alert(msg); // Hiển thị alert với chuỗi thông báo cụ thể
          this.successMessage = msg;

          console.log('Đăng ký học phần thành công', response);

          // Cập nhật lại danh sách dữ liệu sau khi thao tác
          // Cập nhật học phần thiếu điểm
          this.loadMissingCourses();
          // Cập nhật danh sách học phần đã đăng ký trong học kỳ đang xem
          if (this.selectedSemesterForView === this.selectedSemester) {
            this.loadRegisteredCoursesBySemester();
          }
          this.closeRegisterModal();
        },
        error: (error) => {
          console.error('Lỗi đăng ký học phần', error);
          this.errorMessage = error.error?.message || 'Đăng ký học phần không thành công. Vui lòng thử lại.';
          this.successMessage = '';
        }
      });
    } else {
      this.errorMessage = 'Vui lòng chọn học kỳ và mã học phần.';
    }
  }

  onDeleteDkhp(id: number): void {
    if (!confirm('Bạn có chắc chắn muốn xóa đăng ký học phần này không?')) {
      return;
    }
    this.errorMessage = '';
    this.successMessage = '';

    this.studentService.deleteDangkihocphan(id).subscribe({
      next: (response: string | DkhpDTO) => {
        let msg: string;
        if (typeof response === 'string') {
          msg = response;
        } else {
          msg = `Xóa đăng ký học phần "${response.nameCourse}" (Mã HP: ${response.maHocPhan}) thành công!`;
        }
        alert(msg);
        this.successMessage = msg;

        console.log('Xóa đăng ký học phần thành công', response);

        // Cập nhật lại danh sách dữ liệu sau khi xóa
        this.loadMissingCourses(); // Cập nhật học phần thiếu điểm
        if (this.selectedSemesterForView) {
            this.loadRegisteredCoursesBySemester();
        }
      },
      error: (error) => {
        console.error('Lỗi xóa đăng ký học phần', error);
        this.errorMessage = error.error?.message || 'Không thể xóa đăng ký học phần. Vui lòng thử lại.';
        this.successMessage = '';
      }
    });
  }

  openRegisterModal(): void {
    this.showRegisterModal = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.maHocPhanDangKy = '';
    this.selectedSemester = '';
    this.loadMissingCourses(); // THAY ĐỔI: Tải học phần thiếu điểm khi mở modal Đăng ký
    // Không cần loadCtdtCourses() ở đây nữa nếu nó chỉ dùng cho modal "Học phần chưa đăng ký trong CTĐT"
  }

  closeRegisterModal(): void {
    this.showRegisterModal = false;
    this.maHocPhanDangKy = '';
    this.selectedSemester = '';
    this.errorMessage = '';
    this.successMessage = '';
    this.missingCourses = []; // Reset dữ liệu trong modal
  }

  openUnregisteredModal(): void {
    this.showUnregisteredModal = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.loadCtdtCourses(); // Vẫn dùng loadCtdtCourses cho modal này vì nó là "Học phần chưa đăng ký trong CTĐT"
  }

  closeUnregisteredModal(): void {
    this.showUnregisteredModal = false;
    this.errorMessage = '';
    this.successMessage = '';
    this.availableCourses = []; // Reset dữ liệu trong modal này
  }

  dangKyHocPhan(): void {
    if (this.maHocPhanDangKy && this.selectedSemester) {
      this.onDangKyHocPhan(this.maHocPhanDangKy);
    } else {
      this.errorMessage = 'Vui lòng nhập mã học phần và chọn học kỳ.';
    }
  }

  loadRegisteredCoursesBySemester(): void {
    if (!this.selectedSemesterForView) {
      this.registeredCoursesInSelectedSemester = [];
      this.errorMessage = 'Vui lòng chọn một học kỳ để xem danh sách.';
      this.hasLoadedRegisteredCourses = false;
      return;
    }

    this.loadingRegisteredCourses = true;
    this.registeredCoursesInSelectedSemester = [];
    this.errorMessage = '';

    this.studentService.getDangkihocphanForLoggedInStudentBySemester(this.selectedSemesterForView).subscribe({
      next: (data: DkhpDTO[]) => {
        this.registeredCoursesInSelectedSemester = data;
        this.loadingRegisteredCourses = false;
        this.errorMessage = '';
        this.hasLoadedRegisteredCourses = true;
      },
      error: (error) => {
        console.error('Lỗi khi tải danh sách học phần đã đăng ký theo kỳ:', error);
        this.errorMessage = 'Không thể tải danh sách học phần đã đăng ký trong học kỳ này.';
        this.registeredCoursesInSelectedSemester = [];
        this.loadingRegisteredCourses = false;
        this.hasLoadedRegisteredCourses = true;
      },
    });
  }
}