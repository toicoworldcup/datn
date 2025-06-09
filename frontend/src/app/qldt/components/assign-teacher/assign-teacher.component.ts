import { Component, OnInit } from '@angular/core';
import { ClazzService, AssignmentRequest } from '../../../services/clazz.service';
import { CTDTService } from '../../../services/ctdt.service';
import { SemesterService } from '../../../services/semester.service';
import { Clazz } from '../../../models/clazz.model';
import { Ctdt } from '../../../models/ctdt.model';
import { Teacher } from '../../../models/teacher.model';
import { TeacherService } from '../../../services/teacher.service';
import { BatchService } from '../../../services/batch.service';
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { AssignmentService } from '../../../services/assignment.service'; // Import AssignmentService

interface SearchCriteria {
  ctdtCode: string | null;
  khoa: string | null;
  hocKi: string | null;
}

@Component({
  selector: 'app-assign-teacher',
  templateUrl: './assign-teacher.component.html',
  styleUrls: ['./assign-teacher.component.css'],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class AssignTeacherComponent implements OnInit {
  allClazzes: (Clazz)[] = []; // Lưu trữ tất cả các lớp sau khi tìm kiếm
  clazzList: (Clazz)[] = []; // Danh sách lớp hiển thị (có thể đã lọc)
  ctdtList: Ctdt[] = [];
  khoaList: string[] = [];
  semesterList: string[] = [];
  teacherList: Teacher[] = [];
  availableTeachers: { [maLop: string]: Teacher[] } = {}; // Danh sách giáo viên khả dụng cho mỗi lớp
  searchCriteria: SearchCriteria = { ctdtCode: null, khoa: null, hocKi: null };
  assignment: { [maLop: string]: string } = {};
  message: string = '';
  error: string = '';
  showUnassignedOnly: boolean = false;

  constructor(
    private clazzService: ClazzService,
    private ctdtService: CTDTService,
    private semesterService: SemesterService,
    private giangVienService: TeacherService,
    private batchService: BatchService,
    private assignmentService: AssignmentService // Inject AssignmentService
  ) { }

  ngOnInit(): void {
    this.loadInitialData();
  }

  loadInitialData(): void {
    this.loadCTDTs();
    this.loadKhoaList();
    this.loadSemesterList();
    this.loadAllTeachers(); // Tải toàn bộ danh sách giáo viên một lần
    // Không tải danh sách lớp ban đầu
  }

  loadCTDTs(): void {
    this.ctdtService.getAllCTDTs().subscribe({
      next: (data) => {
        this.ctdtList = data;
      },
      error: (error) => {
        this.error = 'Lỗi khi tải danh sách CTĐT: ' + error.message;
      }
    });
  }

  loadKhoaList(): void {
    this.batchService.getAllBatches().subscribe({
      next: (data) => {
        this.khoaList = data.map(batch => batch.name);
      },
      error: (error) => {
        this.error = 'Lỗi khi tải danh sách khóa: ' + error.message;
      }
    });
  }

  loadSemesterList(): void {
    this.semesterService.getAllSemesters().subscribe({
      next: (data) => {
        this.semesterList = data.map(semester => semester.name);
      },
      error: (error) => {
        this.error = 'Lỗi khi tải danh sách học kỳ: ' + error.message;
      }
    });
  }

  loadAllTeachers(): void {
    this.giangVienService.getAllTeachers().subscribe({
      next: (data) => {
        this.teacherList = data;
      },
      error: (error) => {
        this.error = 'Lỗi khi tải danh sách giáo viên: ' + error.message;
      }
    });
  }

  searchClazzes(): void {
    if (this.searchCriteria.ctdtCode && this.searchCriteria.khoa && this.searchCriteria.hocKi) {
      this.clazzService.searchClazzes2(
        this.searchCriteria.ctdtCode!,
        this.searchCriteria.khoa!,
        this.searchCriteria.hocKi!
      ).subscribe({
        next: (data) => {
          this.allClazzes = data;
          this.clazzList = [...this.allClazzes];
          this.assignment = {};
          this.availableTeachers = {}; // Reset danh sách giáo viên khả dụng
          data.forEach(clazz => {
            this.assignment[clazz.maLop] = '';
            this.loadAvailableTeachersForClazz(clazz.maLop, this.searchCriteria.hocKi!);
          });
          this.showUnassignedOnly = false;
        },
        error: (error) => {
          this.error = 'Lỗi khi tìm kiếm lớp học: ' + error.message;
        }
      });
    } else {
      this.error = 'Vui lòng chọn CTĐT, Khóa và Học kỳ để tìm kiếm.';
    }
  }

  loadAvailableTeachersForClazz(maLop: string, hocKi: string): void {
    this.assignmentService.getAvailableTeachersForClazz(maLop, hocKi).subscribe({
      next: (teachers) => {
        this.availableTeachers[maLop] = teachers;
      },
      error: (error) => {
        console.error(`Lỗi khi tải giáo viên khả dụng cho lớp ${maLop}:`, error);
        this.error = `Lỗi khi tải giáo viên khả dụng cho lớp ${maLop}: ${error.message}`;
        this.availableTeachers[maLop] = [];
      }
    });
  }

  getTeachersForClass(maLop: string): Teacher[] {
    return this.availableTeachers[maLop] || [];
  }

  showUnassignedClazzes(): void {
    this.showUnassignedOnly = true;
    this.clazzService.getUnassignedClazzes(
      this.searchCriteria.ctdtCode,
      this.searchCriteria.khoa,
      this.searchCriteria.hocKi
    ).subscribe({
      next: (data) => {
        this.clazzList = data;
        this.assignment = {};
        this.availableTeachers = {};
        data.forEach(clazz => {
          this.assignment[clazz.maLop] = '';
          this.loadAvailableTeachersForClazz(clazz.maLop, this.searchCriteria.hocKi!);
        });
      },
      error: (error) => {
        this.error = 'Lỗi khi tải danh sách lớp chưa phân công: ' + error.message;
      }
    });
  }

  showAllClazzes(): void {
    this.showUnassignedOnly = false;
    this.clazzList = [...this.allClazzes];
    this.clazzList.forEach(clazz => {
      this.loadAvailableTeachersForClazz(clazz.maLop, this.searchCriteria.hocKi!);
    });
  }

  hasTeacherAssigned(clazz: Clazz): boolean {
    return this.assignment[clazz['maLop']] !== undefined && this.assignment[clazz['maLop']] !== '';
  }
  hasAssignments(): boolean {
    return Object.values(this.assignment).some(maGv => !!maGv);
  }

  saveAssignments(): void {
    const assignmentsToSave: AssignmentRequest[] = Object.keys(this.assignment)
      .filter(maLop => this.assignment[maLop])
      .map(maLop => ({
        maLop: maLop,
        maGv: this.assignment[maLop],
        hocKi: this.searchCriteria.hocKi!,
      }));

    if (assignmentsToSave.length > 0) {
      this.clazzService.assignTeachersToClazzes(assignmentsToSave).subscribe({
        next: (response) => {
          this.message = 'Đã lưu lại phân công thành công.';
          this.error = '';
          this.searchClazzes(); // Tải lại để cập nhật
        },
        error: (error) => {
          this.error = 'Lỗi khi lưu phân công: ' + error.error; // Lấy thông báo lỗi từ backend
          this.message = '';
        }
      });
    } else {
      this.message = 'Không có phân công nào để lưu.';
      this.error = '';
    }
  }

  assignSingleTeacher(maLop: string, maGv: string): void {
    if (maGv) {
      this.assignmentService.assignTeacherClazz(maLop, this.searchCriteria.hocKi!, maGv).subscribe({
        next: (response) => {
          this.message = `Đã phân công giáo viên ${maGv} cho lớp ${maLop} thành công.`;
          this.error = '';
          this.searchClazzes(); // Tải lại danh sách lớp và giáo viên khả dụng
        },
        error: (error) => {
          this.error = `Lỗi khi phân công giáo viên ${maGv} cho lớp ${maLop}: ${error.error}`;
          this.message = '';
          // Optionally, reset the dropdown for this class if assignment fails
          this.assignment[maLop] = '';
          this.loadAvailableTeachersForClazz(maLop, this.searchCriteria.hocKi!);
        }
      });
    } else {
      this.error = 'Vui lòng chọn một giáo viên.';
      this.message = '';
    }
  }

  trackByClazz(index: number, clazz: Clazz): string {
    return clazz.maLop;
  }
}