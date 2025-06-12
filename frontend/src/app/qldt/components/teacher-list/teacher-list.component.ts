// src/app/components/teacher/teacher-list/teacher-list.component.ts
import { Component, OnInit } from "@angular/core";
import { TeacherService, TeacherUpdatePayload } from "../../../services/teacher.service"; // <-- Import TeacherUpdatePayload
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { Teacher } from "../../../models/teacher.model"; // Import Teacher interface

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
  importMessage: string = "";
  importError: string = "";

  // New properties for edit functionality
  isEditTeacherModalVisible: boolean = false;
  // editedTeacher sẽ có kiểu Teacher để dễ dàng bind dữ liệu hiển thị từ API GET
  editedTeacher: Teacher = {
    maGv: "",
    name: "",
    email: "",
    phone: "",
    dateOfBirth: "",
    address: "",
    // departmentName sẽ được lấy từ department.name khi load từ API
    departmentName: "", // Giữ lại để bind vào input của form
    gender: "",
    cccd: "",
  };

  newTeacher: Teacher = {
    maGv: "",
    name: "",
    email: "",
    phone: "",
    dateOfBirth: "",
    address: "",
    departmentName: "",
    gender: "Nam", // Default gender to Nam
    cccd: "",
  };

  constructor(private teacherService: TeacherService) {}

  ngOnInit(): void {
    this.loadTeachers();
  }

  // Helper to reset new teacher form
  resetNewTeacher(): void {
    this.newTeacher = {
      maGv: "",
      name: "",
      email: "",
      phone: "",
      dateOfBirth: "",
      address: "",
      departmentName: "",
      gender: "Nam",
      cccd: "",
    };
  }

  // Helper to reset edited teacher form
  resetEditedTeacher(): void {
    this.editedTeacher = {
      id: undefined, // Đặt lại ID là undefined
      maGv: "",
      name: "",
      email: "",
      phone: "",
      dateOfBirth: "",
      address: "",
      departmentName: "",
      gender: "",
      cccd: "",
    };
  }

  // Toggle add teacher modal
  toggleAddTeacherModal(): void {
    this.isAddTeacherModalVisible = !this.isAddTeacherModalVisible;
    if (!this.isAddTeacherModalVisible) {
      this.resetNewTeacher();
    }
  }

  // Toggle search modal
  toggleSearch() {
    this.isSearchVisible = !this.isSearchVisible;
    if (!this.isSearchVisible) {
      this.maGvSearch = ""; // Clear search input when closing
      this.loadTeachers(); // Reload all teachers when closing search
    }
  }

  // Toggle file upload modal
  toggleFileUploadModal(): void {
    this.isFileUploadVisible = !this.isFileUploadVisible;
    this.selectedFile = null; // Reset selected file when modal is closed
    this.importMessage = "";
    this.importError = "";
  }

  // Handle file selection for import
  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  // Upload file for import
  uploadFile(): void {
    if (this.selectedFile) {
      this.teacherService.importTeachers(this.selectedFile).subscribe({
        next: (response) => {
          this.importMessage = response.message;
          this.importError = "";
          this.loadTeachers();
          this.toggleFileUploadModal(); // Close modal on success
        },
        error: (err) => {
          this.importError = err.error?.message || err.error;
          this.importMessage = "";
          console.error("Lỗi import giáo viên", err);
        },
      });
    }
  }

  // Load all teachers
  loadTeachers(): void {
    this.teacherService.getAllTeachers().subscribe((data) => {
      this.teachers = data;
    });
  }

  // Handle page change for pagination
  pageChanged(page: number): void {
    this.p = page;
  }

  // Delete a teacher
  deleteTeacher(id: number | undefined): void {
    if (id === undefined) {
      alert("Không tìm thấy ID giáo viên để xóa.");
      return;
    }

    if (confirm("Bạn có chắc chắn muốn xóa giáo viên này không?")) {
      this.teacherService.deleteTeacher(id).subscribe({
        next: () => {
          this.loadTeachers();
        },
        error: (error) => {
          console.error("Có lỗi khi xóa giáo viên", error);
          alert("Có lỗi khi xóa giáo viên: " + (error.error?.message || error.message));
        },
      });
    }
  }

  // Search for a teacher by maGv
  searchTeacher(): void {
    if (!this.maGvSearch.trim()) {
      this.loadTeachers(); // If search input is empty, load all teachers
      return;
    }

    this.teacherService.searchTeacher(this.maGvSearch).subscribe({
      next: (teacher) => {
        // Tùy thuộc vào việc searchTeacher trả về 1 đối tượng hay 1 mảng
        this.teachers = Array.isArray(teacher) ? teacher : [teacher];
        this.isSearchVisible = false; // Close search modal
      },
      error: (error) => {
        alert("Không tìm thấy giáo viên");
        console.error(error);
        this.teachers = []; // Clear teachers list if not found
      },
    });
  }

  // Add a new teacher
  addTeacher(): void {
    // Đảm bảo rằng newTeacher.departmentName có giá trị trước khi gửi
    if (!this.newTeacher.departmentName) {
      alert('Vui lòng nhập tên khoa cho giáo viên mới.');
      return;
    }

    this.teacherService.addTeacher(this.newTeacher).subscribe({
      next: (data) => {
        this.toggleAddTeacherModal(); // Close modal
        this.resetNewTeacher(); // Clear form
        this.loadTeachers(); // Reload the list to include the new teacher
      },
      error: (error) => {
        console.error("Có lỗi khi thêm giáo viên", error);
        alert("Có lỗi khi thêm giáo viên: " + (error.error?.message || error.message));
      },
    });
  }

  // ***** New methods for Edit functionality *****

  editTeacher(teacher: Teacher): void {
    // Sao chép đối tượng để tránh thay đổi trực tiếp trên danh sách
    this.editedTeacher = { ...teacher };
    // LỖI NẰM Ở DÒNG NÀY:
    // this.editedTeacher.departmentName = teacher.department ? teacher.department.name : '';
    // Sửa thành:
    this.editedTeacher.departmentName = teacher.departmentName || ''; // <-- SỬA ĐỔI CHÍNH TẠI ĐÂY
    this.isEditTeacherModalVisible = true;
  }

  // Toggle edit teacher modal
  toggleEditTeacherModal(): void {
    this.isEditTeacherModalVisible = !this.isEditTeacherModalVisible;
    if (!this.isEditTeacherModalVisible) {
      this.resetEditedTeacher(); // Reset editedTeacher when modal is closed
    }
  }

  // Update teacher information
  updateTeacher(): void {
    // Kiểm tra ID trước khi gửi yêu cầu
    if (this.editedTeacher.id === undefined || this.editedTeacher.id === null) {
      alert("Không tìm thấy ID giáo viên để cập nhật.");
      return;
    }

    // Tạo payload theo TeacherUpdatePayload interface
    const payload: TeacherUpdatePayload = {
      id: this.editedTeacher.id, // ID của giáo viên
      maGv: this.editedTeacher.maGv,
      name: this.editedTeacher.name,
      email: this.editedTeacher.email,
      phoneNumber: this.editedTeacher.phone, // Đảm bảo trường này là phoneNumber chứ không phải phone
      dateOfBirth: this.editedTeacher.dateOfBirth,
      address: this.editedTeacher.address,
      gender: this.editedTeacher.gender,
      cccd: this.editedTeacher.cccd,
      departmentName: this.editedTeacher.departmentName // Gửi tên khoa dưới dạng chuỗi
    };

    this.teacherService.updateTeacher(this.editedTeacher.id, payload).subscribe({ // <-- Sử dụng payload ở đây
      next: (data) => {
        console.log("Giáo viên đã được cập nhật:", data);
        this.toggleEditTeacherModal(); // Close the modal
        this.loadTeachers(); // Reload the list to show updated data
      },
      error: (error) => {
        console.error("Có lỗi khi cập nhật giáo viên", error);
        alert("Có lỗi khi cập nhật giáo viên: " + (error.error?.message || error.message));
      },
    });
  }
}