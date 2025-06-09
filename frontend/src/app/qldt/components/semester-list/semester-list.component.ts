import { Component, OnInit } from "@angular/core";
import { SemesterService } from "../../../services/semester.service";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Semester } from "../../../models/semester.model";

@Component({
  selector: "app-semester-list",
  templateUrl: "./semester-list.component.html",
  styleUrls: ["./semester-list.component.css"],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class SemesterListComponent implements OnInit {
  semesters: Semester[] = [];
  newSemester: Semester = { name: "", isOpen: false };
  editingSemester: Semester | null = null;
  isSearchVisible = false; // Mặc định là ẩn phần tìm kiếm
  p: number = 1;

  constructor(private semesterService: SemesterService) {}

  ngOnInit(): void {
    this.loadSemesters();
  }

  loadSemesters(): void {
    this.semesterService.getAllSemestersNoFilter().subscribe({
      next: (data) => {
        // Giả sử rằng isOpen đã là boolean từ backend, không cần phải chuyển đổi
        this.semesters = data;
      },
      error: (err) => console.error("Lỗi khi tải học kỳ:", err),
    });
  }
  toggleSearch() {
    this.isSearchVisible = !this.isSearchVisible; // Chuyển đổi trạng thái hiển thị
  }

  addSemester(): void {
    if (!this.newSemester.name.trim()) {
      alert("Vui lòng nhập tên học kỳ.");
      return;
    }

    this.semesterService.createSemester(this.newSemester).subscribe({
      next: (semester) => {
        this.semesters.push(semester);
        this.newSemester = { name: "", isOpen: false };
      },
      error: (err) => console.error("Lỗi khi thêm học kỳ:", err),
    });
  }

  startEdit(semester: Semester): void {
    this.editingSemester = { ...semester }; // clone object để tránh sửa trực tiếp
  }

  updateSemester(): void {
    if (!this.editingSemester) return;

    this.semesterService
      .updateSemester(this.editingSemester.name, this.editingSemester)
      .subscribe({
        next: (updated) => {
          const index = this.semesters.findIndex(
            (s) => s.name === updated.name
          );
          if (index > -1) this.semesters[index] = updated;
          this.editingSemester = null;
        },
        error: (err) => console.error("Lỗi khi cập nhật học kỳ:", err),
      });
  }

  cancelEdit(): void {
    this.editingSemester = null;
  }

  openRegistration(name: string): void {
    this.semesterService.openRegistration(name).subscribe({
      next: (updated) => this.loadSemesters(),
      error: (err) => console.error("Lỗi khi mở đăng ký:", err),
    });
  }

  closeRegistration(name: string): void {
    this.semesterService.closeRegistration(name).subscribe({
      next: (updated) => this.loadSemesters(),
      error: (err) => console.error("Lỗi khi đóng đăng ký:", err),
    });
  }
}
