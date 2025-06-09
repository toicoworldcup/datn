import { Component, OnInit } from "@angular/core";
import { StudentService } from "../../../services/student.service";
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";

@Component({
  selector: "app-student-list",
  templateUrl: "./student-list.component.html",
  styleUrls: ["./student-list.component.css"],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class StudentListComponent implements OnInit {
  isSearchVisible = false; // Mặc định là ẩn phần tìm kiếm
  isFileVisible: boolean = false;
  isTableVisible: boolean = false; // Thêm biến trạng thái hiển thị bảng

  students: any[] = [];
  p: number = 1;
  mssvSearch: string = "";
  maCtSearch: string = "";
  batchSearch: string = "";
  selectedFile: File | null = null;

  toggleSearch() {
    this.isSearchVisible = !this.isSearchVisible; // Chuyển đổi trạng thái hiển thị
  }

  toggleFile() {
    this.isFileVisible = !this.isFileVisible;
  }

  // Hàm chuyển đổi trạng thái hiển thị bảng
  toggleTableVisibility(): void {
    this.isTableVisible = !this.isTableVisible;
  }

  constructor(private studentService: StudentService) {}

  ngOnInit(): void {
    this.getAllStudents();
  }

  getAllStudents(): void {
    this.studentService.getStudents().subscribe({
      next: (data) => {
        this.students = data;
        console.log(data);
      },
      error: (error) => {
        console.error("Có lỗi khi gọi API", error);
      },
    });
  }

  searchStudent(): void {
    if (!this.mssvSearch.trim()) return;

    this.studentService.searchStudent(this.mssvSearch).subscribe({
      next: (student) => {
        this.students = [student];
      },
      error: (error) => {
        alert("Không tìm thấy sinh viên");
        console.error(error);
      },
    });
  }

  searchByBatch(): void {
    if (!this.batchSearch || this.batchSearch.trim() === "") return;

    this.studentService.getStudentsByBatch(this.batchSearch).subscribe({
      next: (data) => {
        this.students = data;
      },
      error: (error) => {
        alert("Không tìm thấy sinh viên theo khoá");
        console.error("Lỗi khi tìm kiếm theo khoá:", error);
      },
    });
  }

  searchByCtdt(): void {
    if (!this.maCtSearch || this.maCtSearch.trim() === "") return;

    this.studentService.getStudentsByCtdt(this.maCtSearch).subscribe({
      next: (data) => {
        this.students = data;
      },
      error: (error) => {
        alert("Không tìm thấy sinh viên theo Ctdt");
        console.error("Lỗi khi tìm kiếm theo ctdt:", error);
      },
    });
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  importStudents(): void {
    if (!this.selectedFile) return;

    this.studentService.importStudents(this.selectedFile).subscribe({
      next: (res) => {
        alert("Import thành công!");
        this.getAllStudents();
      },
      error: (err) => {
        alert("Lỗi khi import file");
        console.error(err);
      },
    });
  }

  exportStudents(): void {
    this.studentService.exportStudents().subscribe({
      next: (blob) => {
        const link = document.createElement("a");
        link.href = window.URL.createObjectURL(blob);
        link.download = "students.xlsx";
        link.click();
      },
      error: (err) => {
        alert("Lỗi khi export file");
        console.error(err);
      },
    });
  }
}
