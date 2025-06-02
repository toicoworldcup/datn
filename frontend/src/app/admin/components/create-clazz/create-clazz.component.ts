import { Component, OnInit } from "@angular/core";
import { ClazzService } from "../../../services/clazz.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { SemesterService } from "../../../services/semester.service";
import { CourseService } from "../../../services/course.service";
import { Ctdt } from "../../../models/ctdt.model";
import { CTDTService } from "../../../services/ctdt.service";

@Component({
  selector: "app-create-clazz",
  templateUrl: "./create-clazz.component.html",
  styleUrls: ["./create-clazz.component.css"],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class CreateClazzComponent implements OnInit {
  selectedCTDTCode: string | null = null;
  selectedKhoa: string | null = null;
  selectedHocKi: string | null = null; // Thêm biến selectedHocKi
  message: string = "";
  error: string = "";
  ctdtList: Ctdt[] = []; // Danh sách CTĐT
  khoaList: string[] = ['66', '67', '68']; // Ví dụ danh sách khóa
  semesterList: string[] = ['20253', '20261', '20262']; // Ví dụ danh sách học kỳ

  constructor(
    private semesterService: SemesterService,
    private courseService: CourseService,
    private clazzService: ClazzService,
    private ctdtService: CTDTService // Inject CTDT service
  ) {}

  ngOnInit(): void {
    this.loadCTDTs();
  }

  loadCTDTs() {
    this.ctdtService.getAllCTDTs().subscribe({
      next: (data: Ctdt[]) => { // Explicitly type 'data' as Ctdt[]
        this.ctdtList = data;
      },
      error: (error) => {
        this.error = `Lỗi khi tải danh sách CTĐT: ${error.message}`;
      },
    });
  }

  generateClazzesForCTDTKhoa() {
    if (this.selectedCTDTCode && this.selectedKhoa && this.selectedHocKi) {
      this.clazzService
        .generateClazzesForCTDTCodeAndKhoa(this.selectedCTDTCode, this.selectedKhoa, this.selectedHocKi)
        .subscribe({
          next: (response) => {
            this.message = response; // Hiển thị thông báo từ backend
            this.error = "";
          },
          error: (error) => {
            this.error = `Lỗi tạo lớp: ${error.message}`;
            this.message = "";
          },
        });
    } else {
      this.error = "Vui lòng chọn CTĐT, Khóa và Học kỳ.";
      this.message = "";
    }
  }
}