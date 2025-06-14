import { Component, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { SemesterService } from "../../../services/semester.service";
import { Semester } from "../../../models/semester.model";
import { ScheduleService } from "../../../services/schedule.service";
import { Schedule } from "../../../models/schedule.model";
import { NgxPaginationModule } from "ngx-pagination";

@Component({
  selector: 'app-schedule-list',
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
  templateUrl: './schedule-list.component.html',
  styleUrl: './schedule-list.component.css',
})
export class ScheduleListComponent implements OnInit {
  semesters: Semester[] = [];
  selectedSemester: string = '';
    p: number = 1;
  schedule: Schedule[] = [];
  errorMessage: string = '';
  generationSuccess: boolean = false;
  searchMaLop: string = '';
  isSearchScheduleModalVisible = false;

  constructor(
    private scheduleService: ScheduleService,
    private semesterService: SemesterService
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
        this.errorMessage = 'Lỗi khi tải danh sách học kỳ: ' + error.message;
      }
    });
  }

  generateSchedule(): void {
    if (this.selectedSemester) {
      this.scheduleService.generateSchedule(this.selectedSemester).subscribe({
        next: (data) => {
          this.schedule = data;
          this.errorMessage = '';
          this.generationSuccess = true;
          setTimeout(() => this.generationSuccess = false, 3000);
          this.loadSchedule();
        },
        error: (error) => {
          this.errorMessage = 'Lỗi khi tạo thời khóa biểu.';
          console.error('Lỗi tạo thời khóa biểu', error);
          this.schedule = [];
        }
      });
    } else {
      this.errorMessage = 'Vui lòng chọn học kỳ để tạo thời khóa biểu.';
      this.schedule = [];
    }
  }

  loadSchedule(): void {
    if (this.selectedSemester) {
      this.scheduleService.getScheduleBySemester(this.selectedSemester).subscribe({
        next: (data) => {
          this.schedule = data;
          this.errorMessage = '';
        },
        error: (error) => {
          this.errorMessage = 'Lỗi khi tải thời khóa biểu.';
          console.error('Lỗi tải thời khóa biểu', error);
          this.schedule = [];
        }
      });
    } else {
      this.schedule = [];
    }
  }

  findScheduleByClazzAndSemester(): void {
    if (this.searchMaLop && this.selectedSemester) {
      this.scheduleService.getScheduleByClazzAndSemester(this.searchMaLop, this.selectedSemester).subscribe({
        next: (data) => {
          this.schedule = data;
          this.errorMessage = '';
          this.isSearchScheduleModalVisible = false;
          this.searchMaLop = '';
        },
        error: (error) => {
          this.errorMessage = `Không tìm thấy thời khóa biểu cho mã lớp '${this.searchMaLop}' trong học kỳ '${this.selectedSemester}'.`;
          this.schedule = [];
          console.error('Lỗi tìm kiếm thời khóa biểu', error);
        }
      });
    } else {
      this.errorMessage = 'Vui lòng chọn học kỳ và nhập mã lớp để tìm kiếm.';
      this.schedule = [];
    }
  }

  toggleSearchScheduleModal() {
    this.isSearchScheduleModalVisible = !this.isSearchScheduleModalVisible;
  }
}

