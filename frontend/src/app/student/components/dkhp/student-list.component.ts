import { Component, OnInit } from "@angular/core";
import { StudentService } from "../../../services/student.service";
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { Semester } from "../../../models/semester.model";
import { Course } from "../../../models/course.model";
import { SemesterService } from "../../../services/semester.service";
import { DkhpRequest } from "../../../models/dkhpRequest.model";
import { DkhpDTO } from "../../../models/dkhp.model";

interface CourseWithSuggestion extends Course { // Giả sử Course model đã có suggestedSemester
  suggestedSemester?: number;
}

@Component({
  selector: "app-student-list",
  templateUrl: "./student-list.component.html",
  styleUrls: ["./student-list.component.css"],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class StudentListComponent implements OnInit {
  danhSachDangKy: DkhpDTO[] = [];
  hocKyOptions: Semester[] = [];
  selectedSemester: string = '';
  errorMessage: string = '';
  availableCourses: CourseWithSuggestion[] = []; // Sử dụng interface mới
  registeredCourses: Course[] = [];
  missingCourses: Course[] = [];
  allRegisteredCourses: Course[] = [];

  maHocPhanDangKy: string = '';
  showRegisterModal: boolean = false;
  showUnregisteredModal: boolean = false;
  showRegisterForm: boolean = false;

  constructor(
    private studentService: StudentService,
    private semesterService: SemesterService,
  ) { }

  ngOnInit(): void {
    this.loadHocKyOptions();
    this.loadCtdtCourses();
    this.loadMissingCourses();
    this.loadAllRegisteredCourses();
    this.loadMyRegistrations();
  }

  loadAllRegisteredCourses(): void {
    this.studentService.getMyAllDangkihocphan().subscribe({
      next: (data: Course[]) => {
        this.allRegisteredCourses = data;
        console.log('Toàn bộ học phần đã đăng ký:', this.allRegisteredCourses);
      },
      error: (error) => {
        console.error('Lỗi khi tải toàn bộ học phần đã đăng ký', error);
      }
    });
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

  loadCtdtCourses(): void {
    this.studentService.getCtdtCourses().subscribe({
      next: (data: { registered: Course[]; unregistered: CourseWithSuggestion[] }) => {
        this.registeredCourses = data.registered;
        this.availableCourses = data.unregistered.sort((a, b) => { // Sắp xếp ở đây
          const semesterA = a.suggestedSemester === undefined || a.suggestedSemester === null ? Infinity : a.suggestedSemester;
          const semesterB = b.suggestedSemester === undefined || b.suggestedSemester === null ? Infinity : b.suggestedSemester;
          return semesterA - semesterB;
        });
        console.log('Học phần chưa đăng ký (đã sắp xếp):', this.availableCourses);
      },
      error: (error) => {
        this.errorMessage = 'Không thể tải danh sách học phần.';
        console.error('Lỗi tải danh sách học phần', error);
        this.availableCourses = [];
        this.registeredCourses = [];
      }
    });
  }

  loadMyRegistrations(): void {
    if (this.selectedSemester) {
      this.studentService.getMyDangkihocphan(this.selectedSemester).subscribe({
        next: (data: DkhpDTO[]) => {
          this.danhSachDangKy = data;
          this.errorMessage = '';
        },
        error: (error) => {
          this.errorMessage = 'Không thể tải danh sách đăng ký học phần của bạn.';
          console.error('Lỗi tải danh sách đăng ký', error);
          this.danhSachDangKy = [];
        }
      });
    } else {
      this.danhSachDangKy = [];
    }
  }

  loadMissingCourses(): void {
    this.studentService.getMissingCourses().subscribe({
      next: (data: Course[]) => {
        this.missingCourses = data;
      },
      error: (error) => {
        this.errorMessage = 'Không thể tải danh sách học phần thiếu điểm.';
        console.error('Lỗi tải học phần thiếu điểm', error);
        this.missingCourses = [];
      }
    });
  }

  onDangKyHocPhan(maHocPhan: string): void {
    if (maHocPhan && this.selectedSemester) {
      const request: DkhpRequest = {
        maHocPhan: maHocPhan,
        semesterName: this.selectedSemester,
      };
      this.studentService.createDangkihocphan(request).subscribe({
        next: (response) => {
          console.log('Đăng ký học phần thành công', response);
          this.loadCtdtCourses();
          this.loadMyRegistrations();
          this.closeRegisterModal();
        },
        error: (error) => {
          this.errorMessage = error.error || 'Đăng ký học phần không thành công.';
          console.error('Lỗi đăng ký học phần', error);
        }
      });
    } else {
      this.errorMessage = 'Vui lòng chọn học kỳ và mã học phần.';
    }
  }

  onDeleteDkhp(id: number): void {
    this.studentService.deleteDangkihocphan(id).subscribe({
      next: (response) => {
        console.log(response);
        this.loadMyRegistrations();
      },
      error: (error) => {
        this.errorMessage = error.error || 'Không thể xóa đăng ký học phần.';
        console.error('Lỗi xóa đăng ký học phần', error);
      }
    });
  }

  openRegisterModal(): void {
    this.showRegisterModal = true;
  }

  closeRegisterModal(): void {
    this.showRegisterModal = false;
    this.maHocPhanDangKy = '';
  }

  openUnregisteredModal(): void {
    this.showUnregisteredModal = true;
    this.loadCtdtCourses();
  }

  closeUnregisteredModal(): void {
    this.showUnregisteredModal = false;
  }

  dangKyHocPhan(): void {
    if (this.maHocPhanDangKy && this.selectedSemester) {
      this.onDangKyHocPhan(this.maHocPhanDangKy);
    } else {
      this.errorMessage = 'Vui lòng nhập mã học phần và chọn học kỳ.';
    }
  }
}