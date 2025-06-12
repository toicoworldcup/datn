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
  selectedSemester: string = ""; // Học kỳ được chọn để hiển thị danh sách
  selectedSemesterForModal: string = ""; // Học kỳ được chọn trong modal đăng ký
  errorMessage: string = "";
  successMessage: string = "";
  selectedClassesToDelete: number[] = [];

  showRegistrationModal: boolean = false;

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
        if (this.semesters.length > 0) {
          // Gán học kỳ đầu tiên cho cả hai biến nếu chưa có giá trị
          if (!this.selectedSemester) {
            this.selectedSemester = this.semesters[0].name;
            this.loadRegisteredClasses(); // Tải danh sách lớp đã đăng ký ngay khi có học kỳ mặc định
          }
          if (!this.selectedSemesterForModal) {
            this.selectedSemesterForModal = this.semesters[0].name;
          }
        }
      },
      error: (error) => {
        this.errorMessage = "Lỗi khi tải danh sách học kỳ.";
        console.error("Lỗi tải học kỳ:", error);
        this.semesters = [];
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
            this.errorMessage = ""; // Clear error khi tải lại thành công
            this.selectedClassesToDelete = [];
          },
          error: (error) => {
            this.errorMessage = `Lỗi khi tải danh sách lớp đã đăng ký cho học kỳ ${this.selectedSemester}.`;
            console.error("Lỗi tải danh sách lớp đã đăng ký:", error);
            this.registeredClasses = [];
          },
        });
    } else {
      this.registeredClasses = [];
    }
  }

  registerNewClass(): void {
    this.errorMessage = ""; // Clear lỗi cũ mỗi khi đăng ký mới
    this.successMessage = ""; // Clear thông báo thành công cũ mỗi khi đăng ký mới
    this.newRegistration.semesterName = this.selectedSemesterForModal;

    if (!this.newRegistration.semesterName) {
      this.errorMessage = "Vui lòng chọn học kỳ để đăng ký.";
      console.log("Error (form validation):", this.errorMessage);
      return;
    }
    if (!this.newRegistration.maLop) {
      this.errorMessage = "Vui lòng nhập Mã Lớp.";
      console.log("Error (form validation):", this.errorMessage);
      return;
    }

    this.dangKiLopService.registerClass(this.newRegistration).subscribe({
      next: (response: any) => {
        console.log("API Response (SUCCESS):", response); // RẤT QUAN TRỌNG: Kiểm tra dữ liệu response
        
        // --- LOGIC XỬ LÝ THÔNG BÁO THÀNH CÔNG ---
        if (response && response.message) {
          // Trường hợp backend trả về message trong phản hồi thành công (200 OK)
          if (response.message.includes("Yêu cầu đăng ký lớp cho học phần chưa đăng ký đã được gửi")) {
            this.successMessage = "Yêu cầu bổ sung đã được gửi cho QLDT.";
          } else {
            this.successMessage = response.message;
          }
        } else if (response && response.maLop) { // Nếu backend trả về đối tượng DklDTO thành công
          this.successMessage = `Đăng ký lớp ${response.maLop} thành công trong học kỳ ${this.selectedSemesterForModal}!`;
        } else {
          this.successMessage = "Đăng ký thành công!"; // Fallback message
        }
        console.log("Success Message Set:", this.successMessage); // Log thông báo thành công đã được gán

        // Nếu học kỳ đăng ký trùng với học kỳ đang xem, tải lại danh sách
        if (this.selectedSemester === this.selectedSemesterForModal) {
          this.loadRegisteredClasses();
        }
        this.newRegistration = { maLop: "", semesterName: "" }; // Reset form

        // --- ĐỂ DEBUG: ĐÓNG MODAL SAU MỘT KHOẢNG THỜI GIAN NHẤT ĐỊNH ---
        // Giúp bạn thấy thông báo trong modal trước khi nó biến mất
        setTimeout(() => {
          this.closeRegistrationModal(); // Đóng modal
        }, 2000); // Đóng sau 2 giây
      },
      error: (error: any) => {
        console.error("API Response (ERROR):", error); // RẤT QUAN TRỌNG: Kiểm tra đối tượng lỗi
        this.successMessage = ""; // Xóa thông báo thành công nếu có lỗi

        let backendMessage = "Đã xảy ra lỗi không xác định khi đăng ký lớp.";

        // --- LOGIC XỬ LÝ THÔNG BÁO LỖI ---
        if (error.error && error.error.message) {
          backendMessage = error.error.message;
        } else if (typeof error.error === 'string') {
          backendMessage = error.error;
        } else if (error.message) {
          backendMessage = error.message;
        }

        if (backendMessage.includes("trùng lịch với thời khóa biểu")) {
          this.errorMessage = "Lỗi: Lớp học này bị trùng lịch với thời khóa biểu của bạn.";
        } else if (backendMessage.includes("Bạn đã đăng ký lớp")) {
          this.errorMessage = backendMessage; // Backend đã trả về thông báo đầy đủ
        } else if (backendMessage.includes("Không tìm thấy lớp")) {
          this.errorMessage = `Lỗi: Không tìm thấy lớp ${this.newRegistration.maLop} trong học kỳ ${this.newRegistration.semesterName}. Vui lòng kiểm tra lại mã lớp.`;
        } else if (backendMessage.includes("Không tìm thấy học kỳ")) {
          this.errorMessage = `Lỗi: Không tìm thấy học kỳ với tên: ${this.newRegistration.semesterName}.`;
        } else if (backendMessage.includes("Không tìm thấy sinh viên")) {
          this.errorMessage = "Lỗi: Không tìm thấy thông tin sinh viên của bạn. Vui lòng thử đăng nhập lại.";
        } else if (backendMessage.includes("Lớp đã đầy")) { // Thêm trường hợp lớp đầy nếu backend có trả về
          this.errorMessage = "Lỗi: Lớp đã đầy, không thể đăng ký thêm sinh viên.";
        }
        else {
          this.errorMessage = `Lỗi hệ thống: ${backendMessage}`; // Thông báo lỗi chung nếu không khớp
        }
        console.log("Error Message Set:", this.errorMessage); // Log thông báo lỗi đã được gán
        // Giữ modal mở để người dùng có thể xem lỗi trong modal
      },
    });
  }

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

  bulkUnregisterClasses(): void {
    if (this.selectedClassesToDelete.length > 0) {
      this.errorMessage = "";
      this.successMessage = "";
      this.dangKiLopService
        .bulkDeleteDangKiLop(this.selectedClassesToDelete)
        .subscribe({
          next: (response) => {
            this.successMessage = "Hủy đăng ký các lớp đã chọn thành công!";
            this.loadRegisteredClasses();
            this.selectedClassesToDelete = [];
          },
          error: (error) => {
            this.errorMessage = "Lỗi khi hủy đăng ký các lớp đã chọn.";
            console.error("Lỗi hủy đăng ký nhiều lớp:", error);
            if (error.error) {
              this.errorMessage += ` Chi tiết: ${error.error}`;
            }
            this.successMessage = "";
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
          console.error("Lỗi hủy đăng ký lớp:", error);
          this.successMessage = "";
        },
      });
    }
  }

  onSemesterChange(): void {
    this.loadRegisteredClasses();
    this.errorMessage = ""; // Clear thông báo khi đổi học kỳ
    this.successMessage = ""; // Clear thông báo khi đổi học kỳ
  }

  openRegistrationModal(): void {
    this.showRegistrationModal = true;
    this.errorMessage = ""; // Luôn clear lỗi/thành công cũ khi mở modal mới
    this.successMessage = ""; // Luôn clear lỗi/thành công cũ khi mở modal mới
    this.newRegistration = { maLop: "", semesterName: "" };
    // Đảm bảo selectedSemesterForModal có giá trị mặc định nếu có học kỳ
    if (this.semesters.length > 0 && !this.selectedSemesterForModal) {
      this.selectedSemesterForModal = this.semesters[0].name;
    }
  }

  closeRegistrationModal(): void {
    this.showRegistrationModal = false;
    // KHÔNG clear errorMessage/successMessage ở đây để chúng có thể hiển thị ngoài modal
    // nếu showRegistrationModal là false. Chúng sẽ được clear khi openModal hoặc registerNewClass() gọi lại.
  }
}