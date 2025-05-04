import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { CourseService } from "../../../services/course.service";
import { Course } from "../../models/course.model";
@Component({
  selector: "app-course-list",
  templateUrl: "./course-list.component.html",
  styleUrls: ["./course-list.component.css"],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule], // Đảm bảo các module này đã được import
})
export class CourseListComponent implements OnInit {
  courses: Course[] = []; // Danh sách các khóa học
  searchTerm: string = ""; // Từ khóa tìm kiếm
  p: number = 1;
  mahocphanSearch: string = "";

  isSearchVisible = false; // Mặc định là ẩn phần tìm kiếm

  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    // Gọi API để lấy danh sách khóa học khi component được khởi tạo
    this.loadCourses();
  }

  toggleSearch() {
    this.isSearchVisible = !this.isSearchVisible; // Chuyển đổi trạng thái hiển thị
  }
  // Phương thức tải danh sách khóa học từ dịch vụ
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
    if (!this.mahocphanSearch.trim()) return;

    this.courseService.searchCourse(this.mahocphanSearch).subscribe({
      next: (course) => {
        this.courses = [course];
      },
      error: (error) => {
        alert("Không tìm thấy hoc phan");
        console.error(error);
      },
    });
  }
}
