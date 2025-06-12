import { Component, OnInit } from "@angular/core";
import { SemesterService } from "../../../services/semester.service";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Semester } from "../../../models/semester.model";
import { StudentService } from "../../../services/student.service";
import { TeacherService } from "../../../services/teacher.service";
import { Grade } from "../../../models/grade.model";
import { StudentWithGrades } from "../../../models/StudentWithGrades.model";

@Component({
  selector: "app-semester-list",
  templateUrl: "./semester-list.component.html",
  styleUrls: ["./semester-list.component.css"],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class SemesterListComponent implements OnInit {
  selectedMaLop: string = "";
  selectedHocKi: string = "";
  students: StudentWithGrades[] = [];
  loading: boolean = false;
  error: string = ""; // Vẫn giữ thuộc tính này nếu bạn có chỗ hiển thị lỗi trên UI, nhưng sẽ không gán trực tiếp lỗi từ API vào nữa.
  maLops: string[] = [];
  semesters: Semester[] = [];
  isHistoryModalVisible: boolean = false;
  historyContent: string = "";
  parsedHistory: { timestamp: string; change: string }[] = [];

  constructor(
    private semesterService: SemesterService,
    private studentService: StudentService,
    private teacherService: TeacherService
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
        console.error("Lỗi khi tải học kỳ", error);
        alert("Có lỗi xảy ra khi tải danh sách học kỳ. Vui lòng thử lại sau."); // Thay đổi ở đây
        // this.error = "Lỗi khi tải học kỳ."; // Có thể bỏ dòng này nếu không dùng để hiển thị lỗi
      },
    });
  }

  loadClazzesBySemester(): void {
    if (this.selectedHocKi) {
      this.loading = true;
      this.maLops = [];
      this.teacherService.getTeacherClassesBySemester(this.selectedHocKi).subscribe({
        next: (data) => {
          this.maLops = data.map((clazz: any) => clazz.maLop);
          this.loading = false;
          // alert("Đã tải danh sách lớp học thành công."); // Có thể thêm thông báo thành công nếu cần
        },
        error: (error) => {
          console.error("Lỗi khi tải danh sách lớp", error);
          alert("Có lỗi xảy ra khi tải danh sách lớp học. Vui lòng thử lại."); // Thay đổi ở đây
          // this.error = "Lỗi khi tải danh sách lớp."; // Có thể bỏ dòng này
          this.loading = false;
        },
      });
    } else {
      this.maLops = [];
    }
  }

  loadStudentsByClazz(): void {
    if (this.selectedMaLop && this.selectedHocKi) {
      this.loading = true;
      this.students = [];
      this.studentService
        .getStudentsByClazzAndSemester(this.selectedMaLop, this.selectedHocKi)
        .subscribe({
          next: (data: StudentWithGrades[]) => {
            this.students = data;
            this.loading = false;
            // alert("Đã tải danh sách sinh viên và điểm thành công."); // Có thể thêm thông báo thành công
          },
          error: (error) => {
            console.error("Lỗi khi tải danh sách sinh viên và điểm", error);
            alert("Có lỗi xảy ra khi tải danh sách sinh viên và điểm. Vui lòng thử lại."); // Thay đổi ở đây
            // this.error = "Lỗi khi tải danh sách sinh viên và điểm."; // Có thể bỏ dòng này
            this.loading = false;
          },
        });
    } else {
      this.students = [];
    }
  }

  saveGrades(): void {
    if (this.selectedMaLop && this.selectedHocKi) {
      this.loading = true;
      let successCount = 0;
      const totalStudents = this.students.length;
      let hasError = false; // Biến để theo dõi có lỗi trong quá trình lưu hay không

      if (totalStudents === 0) { // Xử lý trường hợp không có sinh viên để lưu
        alert("Không có sinh viên nào để lưu điểm.");
        this.loading = false;
        return;
      }

      this.students.forEach((student) => {
        const gkiToSend = typeof student.diemGk === "number" ? student.diemGk : -1;
        const ckiToSend = typeof student.diemCk === "number" ? student.diemCk : -1;

        const gradeRequest: Grade = {
          mssv: student.mssv,
          maLop: this.selectedMaLop,
          semesterName: this.selectedHocKi,
          gki: gkiToSend,
          cki: ckiToSend,
        };
        this.studentService.updateGrade(student.mssv, this.selectedMaLop, this.selectedHocKi, gradeRequest).subscribe({
          next: (response) => {
            console.log("Điểm đã lưu cho sinh viên:", student.name, response);
            successCount++;
            if (successCount === totalStudents) {
              this.loading = false;
              if (!hasError) { // Chỉ hiển thị thông báo thành công nếu không có lỗi nào xảy ra
                alert("Đã lưu điểm thành công cho tất cả sinh viên đã nhập.");
              }
              this.loadStudentsByClazz();
            }
          },
          error: (error) => {
            console.error("Lỗi khi lưu điểm cho sinh viên:", student.name, error);
            hasError = true; // Đặt biến lỗi thành true
            this.loading = false;
            alert(`Có lỗi xảy ra khi lưu điểm cho sinh viên ${student.name}. Vui lòng kiểm tra lại.`); // Thông báo lỗi cụ thể
            // this.error = "Lỗi khi lưu điểm."; // Có thể bỏ dòng này
          },
        });
      });
    } else {
      alert("Vui lòng chọn học kỳ và mã lớp trước khi lưu điểm.");
    }
  }

  validateGradeInput(event: any) {
    const value = Number(event.target.value);
    if (value < 0 || value > 10) {
      event.target.value = '';
    }
  }

  isSaveDisabled(): boolean {
    if (!this.students || this.students.length === 0) {
      return true;
    }
    for (const student of this.students) {
      if (
        (student.diemGk !== undefined && student.diemGk !== null && student.diemGk >= 0 && student.diemGk <= 10) ||
        (student.diemCk !== undefined && student.diemCk !== null && student.diemCk >= 0 && student.diemCk <= 10)
      ) {
        return false;
      }
    }
    return true;
  }

  showHistory(history: string | undefined): void {
    this.historyContent = history || "";
    this.parsedHistory = this.parseHistoryString(this.historyContent);
    this.isHistoryModalVisible = true;
  }

  closeHistoryModal(): void {
    this.isHistoryModalVisible = false;
  }

  parseHistoryString(historyString: string): { timestamp: string; change: string }[] {
    if (!historyString) {
      return [];
    }
    const logs: { timestamp: string; change: string }[] = [];
    const lines = historyString.trim().split("\n");
    let previousGk: number | null = null;
    let previousCk: number | null = null;

    lines.forEach((line) => {
      const match = line.match(/\[(.*?)\] GK: (.*?) -> (.*?), CK: (.*?) -> (.*?)/);
      if (match) {
        const timestamp = match[1];
        const oldGkStr = match[2].trim();
        const newGkStr = match[3].trim();
        const oldCkStr = match[4].trim();
        const newCkStr = match[5].trim();

        const oldGk = isNaN(parseFloat(oldGkStr)) ? null : parseFloat(oldGkStr);
        const newGk = isNaN(parseFloat(newGkStr)) ? null : parseFloat(newGkStr);
        const oldCk = isNaN(parseFloat(oldCkStr)) ? null : parseFloat(oldCkStr);
        const newCk = isNaN(parseFloat(newCkStr)) ? null : parseFloat(newCkStr);

        const changeDescriptionParts: string[] = [];
        if (previousGk === null || oldGk !== newGk) {
          changeDescriptionParts.push(`GK: ${oldGkStr} -> ${newGkStr}`);
        }
        if (previousCk === null || oldCk !== newCk) {
          changeDescriptionParts.push(`CK: ${oldCkStr} -> ${newCkStr}`);
        }

        if (changeDescriptionParts.length > 0) {
          logs.push({ timestamp: timestamp, change: changeDescriptionParts.join(", ") });
        }

        previousGk = newGk;
        previousCk = newCk;
      } else {
        // Nếu không khớp với định dạng GK/CK, cứ thêm vào (có thể là thông tin khác)
        const generalMatch = line.match(/\[(.*?)\] (.*)/);
        if (generalMatch) {
          logs.push({ timestamp: generalMatch[1], change: generalMatch[2].trim() });
        }
      }
    });
    return logs;
  }
}