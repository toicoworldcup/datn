import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { CourseService } from "../../../services/course.service";
import { Course } from "../../../models/course.model";

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

  isCombinedSearchModalVisible = false; // Biến điều khiển modal tìm kiếm kết hợp

  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    this.loadCourses();
  }

  openCombinedSearchModal(): void {
    this.isCombinedSearchModalVisible = true;
  }

  closeCombinedSearchModal(): void {
    this.isCombinedSearchModalVisible = false;
  }

  toggleAdvancedSearchModal(): void {
    this.isAdvancedSearchModalVisible = !this.isAdvancedSearchModalVisible;
    this.searchProgram = '';
    this.searchKhoa = '';
    this.searchAdvancedError = '';
    this.isCombinedSearchModalVisible = false; // Đóng modal kết hợp khi mở modal nâng cao
  }

  searchCoursesByProgramAndKhoa(): void {
    this.searchAdvancedError = '';

    this.courseService.searchCoursesByCtdtAndKhoa(this.searchProgram, this.searchKhoa)
      .subscribe({
        next: (courses) => {
          this.courses = courses;
          this.isAdvancedSearchModalVisible = false;
          this.p = 1;
          this.searchResultCourse = null;
          this.isSearchModalVisible = false;
        },
        error: (error) => {
          this.searchAdvancedError = 'Đã xảy ra lỗi khi tìm kiếm.';
          console.error('Lỗi tìm kiếm nâng cao:', error);
          this.courses = [];
          this.searchResultCourse = null;
          this.isSearchModalVisible = false;
        }
      });
  }

  toggleSearch() {
    this.isSearchVisible = !this.isSearchVisible;
    this.isSearchModalVisible = false;
    this.searchResultCourse = null;
    this.searchError = '';
    if (!this.isSearchVisible) {
      this.loadCourses();
    }
  }

  openSearchModal(): void {
    this.isSearchModalVisible = true;
    this.isSearchVisible = false;
    this.mahocphanSearch = '';
    this.searchResultCourse = null;
    this.searchError = '';
    this.isCombinedSearchModalVisible = false; // Đóng modal kết hợp khi mở modal tìm kiếm theo mã
  }

  closeSearchModal(): void {
    this.isSearchModalVisible = false;
    this.loadCourses();
  }

  loadCourses(): void {
    this.courseService.getAllCourses().subscribe({
      next: (data) => {
        this.courses = data;
        console.log(data);
      },
      error: (error) => {
        console.error("Có lỗi khi gọi API", error);
      },
    });
  }

  searchCourse(): void {
    if (!this.mahocphanSearch.trim()) {
      this.courses = [...this.courses];
      this.searchError = 'Vui lòng nhập mã học phần.';
      return;
    }

    this.searchError = '';
    this.courseService.searchCourse(this.mahocphanSearch).subscribe({
      next: (course) => {
        if (course) {
          this.courses = [course];
          this.p = 1;
          this.isSearchModalVisible = false;
        } else {
          this.courses = [];
          this.searchError = "Không tìm thấy học phần có mã: " + this.mahocphanSearch;
        }
        this.searchResultCourse = null;
      },
      error: (error) => {
        this.courses = [];
        this.searchError = "Đã xảy ra lỗi khi tìm kiếm.";
        console.error(error);
        this.searchResultCourse = null;
      },
    });
  }
}