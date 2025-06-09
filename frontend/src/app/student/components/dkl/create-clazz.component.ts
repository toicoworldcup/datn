import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { DklDTO, DklRequest } from "../../../models/dkl.model";
import { DangKiLopService } from "../../../services/dangkilop.service";
import { SemesterService } from "../../../services/semester.service";
import { Semester } from "../../../models/semester.model";

@Component({
  selector: "app-create-clazz",
  templateUrl: "./create-clazz.component.html",
  styleUrls: ["./create-clazz.component.css"],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class CreateClazzComponent implements OnInit {
  registeredClasses: DklDTO[] = [];
  newRegistration: DklRequest = { maLop: "", semesterName: "" };
  semesters: Semester[] = [];
  selectedSemester: string = "";
  errorMessage: string = "";
  successMessage: string = "";
  selectedClassesToDelete: number[] = []; // Mảng lưu ID các lớp được chọn để xóa

  constructor(
    private dangKiLopService: DangKiLopService,
    private semesterService: SemesterService
  ) {}

  ngOnInit(): void {
    this.loadSemesters();
  }

  loadSemesters(): void {
    this.semesterService.getAllSemesters().subscribe({
      next: (data) => {
        this.semesters = data;
      },
      error: (error) => {
        this.errorMessage = "Lỗi khi tải danh sách học kỳ.";
        console.error("Lỗi tải học kỳ", error);
      },
    });
  }

  loadRegisteredClasses(): void {
    if (this.selectedSemester) {
      this.dangKiLopService
        .getAllDangKiLopBySemester(this.selectedSemester)
        .subscribe({
          next: (data) => {
            this.registeredClasses = data;
            this.errorMessage = "";
            this.selectedClassesToDelete = []; // Reset danh sách chọn khi tải lại lớp
          },
          error: (error) => {
            this.errorMessage = `Lỗi khi tải danh sách lớp đã đăng ký cho học kỳ ${this.selectedSemester}.`;
            console.error("Lỗi tải danh sách lớp đã đăng ký", error);
            this.registeredClasses = [];
          },
        });
    } else {
      this.registeredClasses = [];
    }
  }

  registerNewClass(): void {
    this.errorMessage = "";
    this.successMessage = "";
    this.newRegistration.semesterName = this.selectedSemester;
    if (!this.newRegistration.semesterName) {
      this.errorMessage = "Vui lòng chọn học kỳ để đăng ký.";
      return;
    }

    this.dangKiLopService.registerClass(this.newRegistration).subscribe({
      next: (response: any) => {
        if (response && response.message) {
          this.successMessage = response.message;
        } else if (response && response.maLop) {
          this.successMessage = `Đăng ký lớp ${response.maLop} thành công trong học kỳ ${this.selectedSemester}!`;
        } else {
          this.successMessage = "Đăng ký thành công!"; // Fallback message
        }
        this.loadRegisteredClasses();
        this.newRegistration = { maLop: "", semesterName: "" };
      },
      error: (error: any) => {
        this.errorMessage = "Lỗi khi đăng ký lớp.";
        console.error("Lỗi đăng ký lớp", error);
        if (error.error && error.error.message) {
          this.errorMessage = error.error.message; // Lấy message từ response JSON lỗi
        } else if (typeof error.error === "string") {
          this.errorMessage = `Lỗi: ${error.error}`;
        } else if (error.message) {
          this.errorMessage = `Lỗi: ${error.message}`;
        } else {
          this.errorMessage = "Đã xảy ra lỗi khi đăng ký lớp.";
        }
      },
    });
  }

  // Hàm xử lý sự kiện khi checkbox thay đổi
  onCheckboxChange(id: number, event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    if (inputElement.checked) {
      this.selectedClassesToDelete.push(id);
    } else {
      const index = this.selectedClassesToDelete.indexOf(id);
      if (index > -1) {
        this.selectedClassesToDelete.splice(index, 1);
      }
    }
    console.log("Các lớp đã chọn để xóa:", this.selectedClassesToDelete);
  }

  // Hàm gọi API để xóa các lớp đã chọn
  bulkUnregisterClasses(): void {
    if (this.selectedClassesToDelete.length > 0) {
      this.errorMessage = "";
      this.successMessage = "";
      this.dangKiLopService
        .bulkDeleteDangKiLop(this.selectedClassesToDelete)
        .subscribe({
          next: (response) => {
            this.successMessage = "Hủy đăng ký các lớp đã chọn thành công!";
            this.loadRegisteredClasses(); // Tải lại danh sách sau khi xóa
          },
          error: (error) => {
            this.errorMessage = "Lỗi khi hủy đăng ký các lớp đã chọn.";
            console.error("Lỗi hủy đăng ký nhiều lớp", error);
            if (error.error) {
              this.errorMessage += ` Chi tiết: ${error.error}`;
            }
          },
        });
    } else {
      this.errorMessage = "Vui lòng chọn các lớp bạn muốn hủy đăng ký.";
    }
  }

  unregisterClass(id?: number): void {
    if (id) {
      this.errorMessage = "";
      this.successMessage = "";
      this.dangKiLopService.deleteDangKiLop(id).subscribe({
        next: () => {
          this.successMessage = "Hủy đăng ký lớp thành công!";
          this.loadRegisteredClasses();
        },
        error: (error) => {
          this.errorMessage = "Lỗi khi hủy đăng ký lớp.";
          console.error("Lỗi hủy đăng ký lớp", error);
        },
      });
    }
  }

  onSemesterChange(): void {
    this.loadRegisteredClasses();
  }
}
