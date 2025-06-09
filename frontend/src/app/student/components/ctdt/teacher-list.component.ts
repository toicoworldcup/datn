import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { StudentService } from "../../../services/student.service";
import { Course } from "../../../models/course.model";

@Component({
  selector: "app-teacher-list",
  templateUrl: "./teacher-list.component.html",
  styleUrls: ["./teacher-list.component.css"],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class TeacherListComponent implements OnInit {
  allCourses: Course[] = [];
  loading: boolean = false;
  error: string = "";
  p: number = 1; // For pagination
  itemsPerPage: number = 10;

  showGraduationModal: boolean = false;
  graduationResult: any;
  graduationLoading: boolean = false;
  graduationError: string = "";

  constructor(private studentService: StudentService) {}

  ngOnInit(): void {
    this.loadAllCourses();
  }

  loadAllCourses(): void {
    this.loading = true;
    this.error = "";
    this.studentService.getCtdtCourses().subscribe({
      next: (data) => {
        this.allCourses = data.all.sort((a, b) => {
          const semesterA =
            a.suggestedSemester === undefined ? Infinity : a.suggestedSemester;
          const semesterB =
            b.suggestedSemester === undefined ? Infinity : b.suggestedSemester;
          return semesterA - semesterB;
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = "Lỗi khi tải chương trình đào tạo: " + err.message;
        this.loading = false;
      },
    });
  }

  openGraduationModal(): void {
    this.showGraduationModal = true;
    this.xetTotNghiep(); // Gọi API khi mở modal
  }

  closeGraduationModal(): void {
    this.showGraduationModal = false;
    this.graduationResult = null;
    this.graduationError = "";
    this.graduationLoading = false;
  }

  xetTotNghiep(): void {
    this.graduationLoading = true;
    this.graduationError = "";
    this.studentService.xetTotNghiep().subscribe({
      next: (result) => {
        this.graduationResult = result;
        this.graduationLoading = false;
      },
      error: (err) => {
        this.graduationError = "Lỗi khi xét tốt nghiệp: " + err.message;
        this.graduationLoading = false;
        this.graduationResult = { eligible: false, message: this.graduationError };
      },
    });
  }
}