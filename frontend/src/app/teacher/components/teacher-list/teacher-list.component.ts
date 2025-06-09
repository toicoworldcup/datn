import { Component, OnInit } from "@angular/core";
import { TeacherService } from "../../../services/teacher.service";
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { Teacher } from "../../../models/teacher.model";
import { StudentWithGrades } from "../../../models/StudentWithGrades.model";
import { SemesterService } from "../../../services/semester.service";
import { AttendanceService } from "../../../services/attendance.service";
import { Semester } from "../../../models/semester.model";
import { Attendance } from "../../../models/attendance.model";
import { StudentService } from "../../../services/student.service";
import { forkJoin } from 'rxjs';
import { Student } from "../../../models/student.model";

@Component({
  selector: "app-teacher-list",
  templateUrl: "./teacher-list.component.html",
  styleUrls: ["./teacher-list.component.css"],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class TeacherListComponent implements OnInit {
  selectedHocKi: string = '';
  selectedMaLop: string = '';
  viewAttendanceDate: string = '';
  semesters: Semester[] = [];
  maLops: string[] = [];
  attendanceList: { student: StudentWithGrades; status: string }[] = [];
  loading: boolean = false;
  error: string = '';
  isViewButtonClicked: boolean = false; // Thêm biến cờ

  constructor(
    private semesterService: SemesterService,
    private teacherService: TeacherService,
    private attendanceService: AttendanceService,
    private studentService: StudentService
  ) {}

  ngOnInit(): void {
    this.loadSemesters();
  }

  loadSemesters(): void {
    this.semesterService.getAllSemesters().subscribe({
      next: (data) => {
        this.semesters = data;
      },
      error: (error) => {
        console.error('Lỗi khi tải học kỳ', error);
        this.error = 'Lỗi khi tải học kỳ.';
      }
    });
  }

  loadClazzesBySemester(): void {
    if (this.selectedHocKi) {
      this.loading = true;
      this.maLops = [];
      this.teacherService.getTeacherClassesBySemester(this.selectedHocKi).subscribe({
        next: (data) => {
          this.maLops = data.map((clazz: any) => clazz.maLop);
          this.loading = false;
        },
        error: (error) => {
          console.error('Lỗi khi tải danh sách lớp', error);
          this.error = 'Lỗi khi tải danh sách lớp.';
          this.loading = false;
        }
      });
    } else {
      this.maLops = [];
      this.attendanceList = [];
    }
  }

  viewAttendanceList(): void {
    this.isViewButtonClicked = true; // Đặt cờ thành true khi nút được nhấn
    if (this.selectedMaLop && this.selectedHocKi && this.viewAttendanceDate) {
      this.loading = true;
      this.error = '';
      const formattedDate = this.formatDate(this.viewAttendanceDate);
      this.attendanceService.getAttendanceByClazzAndSemesterAndDate(
        this.selectedMaLop,
        this.selectedHocKi,
        formattedDate
      ).subscribe({
        next: (data: Attendance[]) => {
          if (data && data.length > 0) {
            const studentObservables = data.map(attendance =>
              this.studentService.getStudentByMssv(attendance.mssv || '')
            );

            forkJoin(studentObservables).subscribe({
              next: (students: Student[]) => {
                this.attendanceList = data.map((attendance, index) => ({
                  student: {
                    mssv: attendance.mssv || '',
                    name: students[index]?.name || 'N/A'
                  } as StudentWithGrades,
                  status: attendance.status || 'Chưa có'
                }));
                this.loading = false;
              },
              error: (error) => {
                console.error('Lỗi khi tải thông tin sinh viên', error);
                this.attendanceList = data.map(attendance => ({
                  student: { mssv: attendance.mssv || '', name: 'N/A' } as StudentWithGrades,
                  status: attendance.status || 'Chưa có'
                }));
                this.loading = false;
                this.error = 'Lỗi khi tải thông tin sinh viên.';
              }
            });
          } else {
            this.attendanceList = [];
            this.loading = false;
          }
        },
        error: (error) => {
          console.error('Lỗi khi tải danh sách điểm danh', error);
          this.error = 'Lỗi khi tải danh sách điểm danh.';
          this.loading = false;
          this.attendanceList = [];
        }
      });
    } else {
      alert('Vui lòng chọn học kỳ, lớp và ngày xem.');
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }
}