// create-clazz.component.ts
import { Component, OnInit } from "@angular/core";
import { ClazzService } from "../../../services/clazz.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { SemesterService } from "../../../services/semester.service";
import { CourseService } from "../../../services/course.service";

@Component({
  selector: "app-create-clazz",
  templateUrl: "./create-clazz.component.html",
  styleUrls: ["./create-clazz.component.css"],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class CreateClazzComponent implements OnInit {
  maHocPhan: string = "";
  hocki: string = "";
  message: string = "";
  error: string = "";
  isSearchVisible = false; // Mặc định là ẩn phần tìm kiếm

  courseList: string[] = [];
  classList: string[] = [];
  semesterList: string[] = [];
  MaGv: string = "";
  MaLop: string = "";
  HocKi: string = "";

  constructor(
    private semesterService: SemesterService,
    private courseService: CourseService,
    private clazzService: ClazzService
  ) {}

  ngOnInit(): void {
    this.courseService
      .getAllCourses()
      .subscribe((data) => (this.courseList = data.map((c) => c.maHocPhan)));
    this.semesterService
      .getAllSemesters()
      .subscribe((data) => (this.semesterList = data.map((s) => s.name)));
  }
  toggleSearch() {
    this.isSearchVisible = !this.isSearchVisible; // Chuyển đổi trạng thái hiển thị
  }

  generateClazz() {
    this.clazzService.generateClazzes(this.maHocPhan, this.hocki).subscribe({
      next: (response) => {
        this.message = `Đã tạo lớp thành công!`;
        this.error = "";
      },
      error: (error) => {
        this.error = `Lỗi: ${error.message}`;
        this.message = "";
      },
    });
  }
}
