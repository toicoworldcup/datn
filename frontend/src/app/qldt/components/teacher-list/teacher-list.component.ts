import { Component, OnInit } from "@angular/core";
import { TeacherService } from "../../../services/teacher.service";
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { Teacher } from "../../../models/teacher.model";

@Component({
  selector: "app-teacher-list",
  templateUrl: "./teacher-list.component.html",
  styleUrls: ["./teacher-list.component.css"],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class TeacherListComponent implements OnInit {
  teachers: Teacher[] = [];
  p: number = 1;
  maGvSearch: string = "";
  isAddTeacherModalVisible: boolean = false;
  isSearchVisible = false; // Mặc định là ẩn phần tìm kiếm
  isFileUploadVisible: boolean = false;
  selectedFile: File | null = null;
  importMessage: string = '';
  importError: string = '';


  newTeacher: any = {
    // 👈 Thêm dòng này
    maGv: "",
    name: "",
    email: "",
    phoneNumber: "",
    dateOfBirth: "",
    address: "",
    departmentName: "",
    gender: "",
    cccd: "",
  };
  resetNewTeacher(): void {
    this.newTeacher = {
      maGv: "",
      name: "",
      email: "",
      phoneNumber: "",
      dateOfBirth: "",
      address: "",
      departmentName: "",
      gender: "Nam",
      cccd: "",
    };
  }

  constructor(private teacherService: TeacherService) {}

  ngOnInit(): void {
    this.loadTeachers();
  }
  toggleAddTeacherModal(): void {
    this.isAddTeacherModalVisible = !this.isAddTeacherModalVisible;
    console.log("Modal visibility:", this.isAddTeacherModalVisible);

    // Nếu đóng modal, reset lại biểu mẫu
    if (!this.isAddTeacherModalVisible) {
      this.resetNewTeacher();
    }
  }
  toggleSearch() {
    this.isSearchVisible = !this.isSearchVisible; // Chuyển đổi trạng thái hiển thị
  }

  toggleFileUploadModal(): void {
    this.isFileUploadVisible = !this.isFileUploadVisible;
    this.selectedFile = null; // Reset selected file when modal is closed
    this.importMessage = '';
    this.importError = '';
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  uploadFile(): void {
    if (this.selectedFile) {
      this.teacherService.importTeachers(this.selectedFile).subscribe({
        next: (response) => {
          this.importMessage = response.message; // Lấy thông báo từ response.message
          this.importError = '';
          this.loadTeachers(); // Tải lại danh sách giáo viên sau khi import thành công
        },
        error: (err) => {
          this.importError = err.error?.message || err.error; // Cố gắng lấy err.error.message, nếu không thì dùng err.error
          this.importMessage = '';
          console.error("Lỗi import giáo viên", err);
        },
      });
    }
  }

  loadTeachers(): void {
    this.teacherService.getAllTeachers().subscribe((data) => {
      this.teachers = data;
    });
  }
  pageChanged(page: number): void {
    this.p = page;
    this.loadTeachers(); // Tải lại danh sách giáo viên sau khi thay đổi trang
  }

  deleteTeacher(maGv: string): void {
    if (confirm("Bạn có chắc chắn muốn xóa giáo viên này không?")) {
      this.teacherService.deleteTeacher(maGv).subscribe(() => {
        this.loadTeachers();
      });
    }
  }
  searchTeacher(): void {
    if (!this.maGvSearch.trim()) return;

    this.teacherService.searchTeacher(this.maGvSearch).subscribe({
      next: (teacher) => {
        this.teachers = [teacher];
        this.isSearchVisible = false; // Thêm dòng này để đóng modal khi tìm thấy giáo viên
      },
      error: (error) => {
        alert("Không tìm thấy giáo viên");
        console.error(error);
      },
    });
  }
  addTeacher(): void {
    this.teacherService.addTeacher(this.newTeacher).subscribe({
      next: (data) => {
        this.teachers.push(data); // Thêm giáo viên mới vào danh sách
        this.toggleAddTeacherModal(); // Đóng modal
        this.resetNewTeacher(); // Làm sạch form
        this.loadTeachers(); // Reload the list
      },
      error: (error) => {
        console.error("Có lỗi khi thêm giáo viên", error);
      },
    });
  }
}