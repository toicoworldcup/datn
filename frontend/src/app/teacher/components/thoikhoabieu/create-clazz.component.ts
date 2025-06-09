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

@Component({
  selector: "app-create-clazz",
  templateUrl: "./create-clazz.component.html",
  styleUrls: ["./create-clazz.component.css"],
  standalone: true,
  imports: [CommonModule, FormsModule, NgxPaginationModule],
})
export class CreateClazzComponent implements OnInit {
  selectedSemester: string = '';
  semesters: any[] = [];
  schedule: Schedule[] = [];
  loading: boolean = false;
  error: string = '';
  p: number = 1; // Trang hiện tại cho phân trang
  itemsPerPage: number = 10;

  constructor(
    private semesterService: SemesterService,
    private teacherService: TeacherService
  ) { }

  ngOnInit(): void {
    this.loadSemesters();
  }

  loadSemesters(): void {
    this.loading = true;
    this.error = '';
    this.semesterService.getAllSemesters().subscribe({
      next: (data) => {
        this.semesters = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Lỗi khi tải danh sách học kỳ: ' + err.message;
        this.loading = false;
      }
    });
  }

  loadSchedule(): void {
    if (!this.selectedSemester) {
      this.schedule = [];
      return;
    }

    this.loading = true;
    this.error = '';
    this.teacherService.getTeacherScheduleBySemester(this.selectedSemester).subscribe({
      next: (data) => {
        this.schedule = data.sort((a, b) => {
          const dayOrder = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'Chủ Nhật'];
          const dayAIndex = dayOrder.indexOf(this.formatDayOfWeek(a.dayOfWeek));
          const dayBIndex = dayOrder.indexOf(this.formatDayOfWeek(b.dayOfWeek));

          if (dayAIndex !== dayBIndex) {
            return dayAIndex - dayBIndex;
          }

          // Cần thông tin về tiết bắt đầu/kết thúc từ API response để sắp xếp theo thời gian
          // Hiện tại chỉ sắp xếp theo thứ
          return 0;
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Lỗi khi tải thời khóa biểu: ' + err.message;
        this.schedule = [];
        this.loading = false;
      }
    });
  }

  formatDayOfWeek(day: string): string {
    switch (day.toLowerCase()) {
      case 'monday': return 'Thứ 2';
      case 'tuesday': return 'Thứ 3';
      case 'wednesday': return 'Thứ 4';
      case 'thursday': return 'Thứ 5';
      case 'friday': return 'Thứ 6';
      case 'saturday': return 'Thứ 7';
      case 'sunday': return 'Chủ Nhật';
      default: return day;
    }
  }
}