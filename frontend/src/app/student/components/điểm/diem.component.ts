import { Component, OnInit } from "@angular/core";
import { ClazzService } from "../../../services/clazz.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { SemesterService } from "../../../services/semester.service";
import { CourseService } from "../../../services/course.service";
import { Ctdt } from "../../../models/ctdt.model";
import { CTDTService } from "../../../services/ctdt.service";
import { Schedule } from "../../../models/schedule.model";
import { TeacherService } from "../../../services/teacher.service";
import { NgxPaginationModule } from "ngx-pagination";
import { StudentService } from "../../../services/student.service";
import { Semester } from "../../../models/semester.model";

@Component({
  selector: "app-diem",
  templateUrl: "./diem.component.html",
  styleUrls: ["./diem.component.css"],
  standalone: true,
  imports: [CommonModule, FormsModule, NgxPaginationModule],
})
export class DiemComponent implements OnInit {
  selectedSemester: string = '';
  grades: any[] = [];
  errorMessage: string = '';
  isDataLoaded: boolean = false; // Thêm biến này

  hocKyOptions: Semester[] = []; // Sử dụng mảng Semester
  gpaSemester: string | number | null = null; // THÊM MỚI: Biến để lưu trữ GPA kỳ

  constructor(
    private studentService: StudentService,
    private semesterService: SemesterService // Inject SemesterService
  ) { }

  ngOnInit(): void {
    this.loadHocKyOptions();

    // Bạn có thể load danh sách học kỳ ở đây nếu cần
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

  loadGrades(): void {
    this.isDataLoaded = true;
    this.grades = []; // Reset grades
    this.gpaSemester = null; // Reset GPA khi tải điểm mới

    if (this.selectedSemester) {
      this.studentService.getMyGradesBySemester(this.selectedSemester).subscribe({
        next: (response) => {
          if (response === 'Chưa có điểm cho kỳ này.') {
            this.grades = [];
            this.errorMessage = 'Không có điểm nào trong học kỳ này.';
          } else {
            try {
              this.grades = JSON.parse(response);
              this.errorMessage = '';
            } catch (error) {
              console.error('Lỗi parse JSON:', error);
              this.errorMessage = 'Lỗi khi xử lý dữ liệu điểm.';
              this.grades = [];
            }
          }
          this.loadGPAForSemester(); // THÊM MỚI: Gọi phương thức tải GPA sau khi tải điểm
        },
        error: (error) => {
          this.errorMessage = 'Không thể tải điểm.';
          this.grades = [];
          console.error('Lỗi tải điểm:', error);
          this.loadGPAForSemester(); // THÊM MỚI: Gọi phương thức tải GPA ngay cả khi có lỗi tải điểm
        }
      });
    } else {
      this.grades = [];
      this.errorMessage = 'Vui lòng chọn học kỳ.';
      this.isDataLoaded = false; // THÊM MỚI: Reset isDataLoaded khi không có học kỳ được chọn
    }
  }

  // THÊM MỚI: Phương thức để tải GPA cho học kỳ đã chọn
  loadGPAForSemester(): void {
    if (!this.selectedSemester) {
      this.gpaSemester = null;
      return;
    }

    // Giả sử StudentService có phương thức getGPABySemester
    // Bạn cần thêm phương thức này vào StudentService nếu chưa có
    this.studentService.getGPABySemester(this.selectedSemester).subscribe({
      next: (gpaResponse: any) => {
        // Giả định API trả về trực tiếp giá trị GPA (number hoặc string)
        // Hoặc một đối tượng JSON có trường 'gpa' (ví dụ: { "gpa": 3.5 })
        if (typeof gpaResponse === 'number' || typeof gpaResponse === 'string') {
          this.gpaSemester = gpaResponse;
        } else if (gpaResponse && typeof gpaResponse.gpa === 'number' || typeof gpaResponse.gpa === 'string') {
          this.gpaSemester = gpaResponse.gpa;
        } else {
          this.gpaSemester = 'N/A'; // Hoặc giá trị mặc định khác nếu không nhận được GPA hợp lệ
        }
      },
      error: (error) => {
        console.error('Lỗi tải GPA:', error);
        this.gpaSemester = 'Không có'; // Hiển thị thông báo lỗi nếu không tải được GPA
      }
    });
  }
}