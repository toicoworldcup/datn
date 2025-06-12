import { Component, OnInit } from "@angular/core";
import { ClazzService } from "../../../services/clazz.service";
import { FormsModule } from "@angular/forms";
import { CommonModule, DatePipe } from "@angular/common";
import { SemesterService } from "../../../services/semester.service";
import { CourseService } from "../../../services/course.service";
import { Ctdt } from "../../../models/ctdt.model";
import { CTDTService } from "../../../services/ctdt.service";
import { Clazz } from "../../../models/clazz.model";
import { Semester } from "../../../models/semester.model"; // Đảm bảo import Semester model

@Component({
  selector: "app-create-clazz",
  templateUrl: "./create-clazz.component.html",
  styleUrls: ["./create-clazz.component.css"],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class CreateClazzComponent implements OnInit {
  selectedCTDTCode: string | null = null;
  selectedKhoa: string | null = null; // Sẽ là giá trị nhập từ người dùng
  selectedHocKi: string | null = null;
  message: string = "";
  error: string = "";
  ctdtList: Ctdt[] = [];
  uniqueCtdtNames: { code: string, name: string }[] = [];

  // KHÔNG CẦN KHOALIST NỮA VÌ NGƯỜI DÙNG TỰ NHẬP
  // khoaList: string[] = []; // Bỏ hoặc để mảng rỗng nếu không dùng

  semesterList: string[] = []; // Sẽ được đổ dữ liệu từ SemesterService

  newlyCreatedClazzes: Clazz[] = [];

  constructor(
    private semesterService: SemesterService,
    private courseService: CourseService, // Giữ lại nếu bạn có dùng ở nơi khác
    private clazzService: ClazzService,
    private ctdtService: CTDTService
  ) {}

  ngOnInit(): void {
    this.loadCTDTs();
    this.loadSemesters(); // Gọi để tải danh sách học kỳ
    // loadKhoas() không cần gọi vì khóa sẽ do người dùng nhập
  }

  loadCTDTs() {
    this.ctdtService.getAllCTDTs().subscribe({
      next: (data: Ctdt[]) => {
        this.ctdtList = data;
        this.filterUniqueCtdtNames();
      },
      error: (error) => {
        this.error = `Lỗi khi tải danh sách CTĐT: ${error.message}`;
      },
    });
  }

  loadSemesters(): void {
    this.semesterService.getAllSemesters().subscribe({
      next: (data: Semester[]) => {
        this.semesterList = data.map(s => s.name); // Giả định Semester model có thuộc tính 'name'
      },
      error: (error) => {
        this.error = `Lỗi khi tải danh sách Học kỳ: ${error.message}`;
        console.error("Lỗi khi tải danh sách Học kỳ:", error);
      }
    });
  }

  filterUniqueCtdtNames(): void {
    const seenNames = new Set<string>();
    this.uniqueCtdtNames = [];

    this.ctdtList.forEach(ctdt => {
      // Dùng ctdt.maCt nếu đó là trường mã CTĐT trong model của bạn
      if (ctdt.name && !seenNames.has(ctdt.name)) {
        seenNames.add(ctdt.name);
        this.uniqueCtdtNames.push({ code: ctdt.maCt, name: ctdt.name });
      }
    });
  }

  generateClazzesForCTDTKhoa() {
    this.message = "";
    this.error = "";
    this.newlyCreatedClazzes = [];

    if (!this.selectedCTDTCode || !this.selectedKhoa || !this.selectedHocKi) {
      this.error = "Vui lòng chọn đầy đủ CTĐT, Khóa và Học kỳ.";
      return;
    }

    this.clazzService
      .generateClazzes(this.selectedCTDTCode, this.selectedKhoa, this.selectedHocKi)
      .subscribe({
        next: (response: Clazz[]) => {
          if (response && response.length > 0) {
            this.message = `Đã tạo thành công ${response.length} lớp học.`;
            this.newlyCreatedClazzes = response;
          } else {
            this.message = "Không có lớp học nào được tạo thêm (có thể đã tồn tại hoặc không đủ điều kiện).";
          }
          this.error = "";
        },
        error: (error) => {
          console.error("Lỗi khi tạo lớp học:", error);
          this.error = `Lỗi tạo lớp: ${error.error?.message || error.message || 'Lỗi không xác định'}`;
          this.message = "";
          this.newlyCreatedClazzes = [];
        },
      });
  }
}