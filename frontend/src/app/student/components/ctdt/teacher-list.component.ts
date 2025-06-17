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

  // --- NEW PROPERTIES FOR CPA ---
  cpa: number | null = null; // Variable to store CPA value
  cpaLoading: boolean = false; // To track CPA loading state
  cpaError: string = ""; // To store CPA error messages
  // --- END NEW PROPERTIES ---

  constructor(private studentService: StudentService) {}

  ngOnInit(): void {
    this.loadAllCourses();
    this.loadCPA(); // <--- Call the new method to load CPA
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

  // --- NEW METHOD TO LOAD CPA ---
  loadCPA(): void {
    this.cpaLoading = true;
    this.cpaError = "";
    this.studentService.getCPA().subscribe({
      next: (cpaValue) => {
        this.cpa = cpaValue;
        this.cpaLoading = false;
      },
      error: (err) => {
        console.error('Lỗi khi tải CPA:', err); // Log the full error for debugging
        this.cpaError = "Không thể tải CPA. Vui lòng thử lại.";
        this.cpaLoading = false;
      }
    });
  }
  // --- END NEW METHOD ---

  openGraduationModal(): void {
    this.showGraduationModal = true;
    this.xetTotNghiep(); // Call API when opening modal
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