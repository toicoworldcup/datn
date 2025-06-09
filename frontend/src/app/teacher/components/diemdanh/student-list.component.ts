import { Component, OnInit } from "@angular/core";
import { StudentService } from "../../../services/student.service";
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { Semester } from "../../../models/semester.model";
import { StudentWithGrades } from "../../../models/StudentWithGrades.model";
import { SemesterService } from "../../../services/semester.service";
import { TeacherService } from "../../../services/teacher.service";
import { AttendanceService } from "../../../services/attendance.service";
import { AttendanceRequest } from "../../../models/attendance-request.model";
import { Attendance } from "../../../models/attendance.model";
import { forkJoin, of, Observable } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

interface StudentAttendanceInfo {
  student: StudentWithGrades & { absentCount?: number };
  attendanceStatus: { [date: string]: 'DU' | 'VANG' | null };
}

@Component({
  selector: "app-student-list",
  templateUrl: "./student-list.component.html",
  styleUrls: ["./student-list.component.css"],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class StudentListComponent implements OnInit {
  selectedHocKi: string = '';
  selectedMaLop: string = '';
  semesters: Semester[] = [];
  maLops: string[] = [];
  studentsWithAttendance: StudentAttendanceInfo[] = [];
  studentsForQuickAttendance: StudentAttendanceInfo[] = [];
  loading: boolean = false;
  error: string = '';
  detailedAttendanceColumns: Date[] = [];
  showQuickAttendance: boolean = false;
  showDetailedAttendance: boolean = false;
  quickAttendanceStatus: { [mssv: string]: 'DU' | 'VANG' | null } = {};
  today = new Date();

  constructor(
    private semesterService: SemesterService,
    private teacherService: TeacherService,
    private studentService: StudentService,
    private attendanceService: AttendanceService
  ) { }

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
          this.studentsWithAttendance = [];
          this.studentsForQuickAttendance = [];
          this.detailedAttendanceColumns = [];
          this.showQuickAttendance = false;
          this.showDetailedAttendance = false;
          if (this.selectedMaLop) {
            this.loadStudentsForBothViews();
          }
        },
        error: (error) => {
          console.error('Lỗi khi tải danh sách lớp', error);
          this.error = 'Lỗi khi tải danh sách lớp.';
          this.loading = false;
        }
      });
    } else {
      this.maLops = [];
      this.studentsWithAttendance = [];
      this.studentsForQuickAttendance = [];
      this.detailedAttendanceColumns = [];
      this.showQuickAttendance = false;
      this.showDetailedAttendance = false;
    }
  }

  loadStudentsForBothViews(): void {
    if (this.selectedMaLop && this.selectedHocKi) {
      this.loading = true;
      this.studentsWithAttendance = [];
      this.studentsForQuickAttendance = [];
      this.studentService.getStudentsByClazzAndSemester(this.selectedMaLop, this.selectedHocKi).subscribe({
        next: (students: StudentWithGrades[]) => {
          const today = new Date();
          const formattedToday = this.formatDate(today);
          const absentCountRequests = students.map(student =>
            this.attendanceService.getAbsentCount(this.selectedMaLop, student.mssv, formattedToday)
          );

          forkJoin(absentCountRequests).subscribe({
            next: (absentCounts: number[]) => {
              this.studentsWithAttendance = students.map((student, index) => ({
                student: { ...student, absentCount: absentCounts[index] },
                attendanceStatus: {}
              }));
              this.studentsForQuickAttendance = students.map(student => ({
                student: student,
                attendanceStatus: { [formattedToday]: null }
              }));
              this.loadDetailedAttendanceData();
              this.loading = false;
            },
            error: (error) => {
              console.error('Lỗi khi tải số buổi vắng', error);
              this.studentsWithAttendance = students.map(student => ({
                student: { ...student, absentCount: -1 },
                attendanceStatus: {}
              }));
              this.studentsForQuickAttendance = students.map(student => ({
                student: student,
                attendanceStatus: { [formattedToday]: null }
              }));
              this.loading = false;
              this.error = 'Lỗi khi tải số buổi vắng.';
              this.loadDetailedAttendanceData();
            }
          });
        },
        error: (error) => {
          console.error('Lỗi khi tải danh sách sinh viên', error);
          this.error = 'Lỗi khi tải danh sách sinh viên.';
          this.loading = false;
        }
      });
    } else {
      this.studentsWithAttendance = [];
      this.studentsForQuickAttendance = [];
      this.detailedAttendanceColumns = [];
    }
  }

  loadDetailedAttendanceData(): void {
    if (this.selectedMaLop && this.selectedHocKi && this.studentsWithAttendance.length > 0) {
      this.loading = true;
      this.attendanceService.getAttendanceDatesByClazzAndSemester(this.selectedMaLop, this.selectedHocKi).subscribe({
        next: (dates: string[]) => {
          this.detailedAttendanceColumns = dates.map(dateStr => new Date(dateStr));
          this.detailedAttendanceColumns.sort((a, b) => a.getTime() - b.getTime());
          const allAttendanceRequests = this.studentsWithAttendance.flatMap(studentInfo => {
            return this.detailedAttendanceColumns.map(date => {
              const formattedDate = this.formatDate(date);
              return this.attendanceService.getAttendanceByClazzAndStudentAndSemester(
                this.selectedMaLop,
                studentInfo.student.mssv,
                this.selectedHocKi,
                formattedDate
              ).pipe(
                catchError(() => of([])),
                map((attendances: Attendance[]) => {
                  const attendanceOnDate = attendances.find(att => this.formatDate(new Date(att.attendanceDate)) === formattedDate);
                  return {
                    mssv: studentInfo.student.mssv,
                    date: formattedDate,
                    status: attendanceOnDate ? attendanceOnDate.status as 'DU' | 'VANG' : null
                  };
                })
              );
            });
          });

          forkJoin(allAttendanceRequests).subscribe({
            next: (results) => {
              this.studentsWithAttendance.forEach(studentInfo => {
                this.detailedAttendanceColumns.forEach(date => {
                  const formattedDate = this.formatDate(date);
                  const result = results.find(r => r?.mssv === studentInfo.student.mssv && r?.date === formattedDate);
                  if (!studentInfo.attendanceStatus) {
                    studentInfo.attendanceStatus = {};
                  }
                  studentInfo.attendanceStatus[formattedDate] = result?.status || null;
                });
              });
              this.loading = false;
            },
            error: (error) => {
              console.error('Lỗi khi tải dữ liệu điểm danh chi tiết', error);
              this.loading = false;
            }
          });
        },
        error: (error) => {
          console.error('Lỗi khi tải ngày điểm danh', error);
          this.loading = false;
        }
      });
    } else {
      this.loading = false;
      this.detailedAttendanceColumns = [];
    }
  }

  kiemTraDiemDanhDaTonTai(): Observable<boolean> {
    const todayFormatted = this.formatDate(this.today);
    return this.attendanceService.checkAttendanceExists(this.selectedMaLop, this.selectedHocKi, todayFormatted);
  }

  taoDiemDanhNhanh(): void {
    this.kiemTraDiemDanhDaTonTai().pipe(
      switchMap(exists => {
        if (exists) {
          alert('Điểm danh cho lớp này vào ngày hôm nay đã tồn tại.');
          return of(null); // Không thực hiện tạo mới
        } else {
          this.showQuickAttendance = true; // Hiển thị bảng điểm danh nhanh
          return of(null);
        }
      })
    ).subscribe();
  }

  saveQuickAttendance(): void {
    if (this.selectedMaLop && this.selectedHocKi) {
      this.loading = true;
      const attendanceRequests: AttendanceRequest[] = [];
      const todayFormatted = this.formatDate(this.today);

      this.studentsForQuickAttendance.forEach(studentInfo => {
        const status = this.quickAttendanceStatus[studentInfo.student.mssv];
        if (status) {
          attendanceRequests.push({
            maLop: this.selectedMaLop,
            hocKi: this.selectedHocKi,
            mssv: studentInfo.student.mssv,
            attendanceDate: todayFormatted,
            status: status
          });
        }
      });

      this.attendanceService.markBulkAttendance(attendanceRequests).subscribe({
        next: (response) => {
          console.log('Lưu điểm danh nhanh thành công', response);
          this.loading = false;
          alert('Lưu điểm danh nhanh thành công!');
          this.loadStudentsForBothViews(); // Reload data for both views
          this.showQuickAttendance = false;
        },
        error: (error) => {
          console.error('Lỗi khi lưu điểm danh nhanh', error);
          this.loading = false;
          if (error.status === 400) {
            alert(error.error); // Hiển thị thông báo lỗi từ backend
          } else {
            this.error = 'Lỗi khi lưu điểm danh nhanh.';
            alert(this.error);
          }
        }
      });
    } else {
      alert('Vui lòng chọn học kỳ và lớp.');
    }
  }

  formatDate(date: Date): string {
    if (date instanceof Date) {
      const year = date.getFullYear();
      const month = ('0' + (date.getMonth() + 1)).slice(-2);
      const day = ('0' + date.getDate()).slice(-2);
      return `${year}-${month}-${day}`;
    }
    return '';
  }
}