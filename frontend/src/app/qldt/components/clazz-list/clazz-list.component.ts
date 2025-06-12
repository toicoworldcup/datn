import { Component, OnInit } from '@angular/core';
import { ClazzService } from '../../../services/clazz.service';
import { TeacherService } from '../../../services/teacher.service';
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { Clazz } from '../../../models/clazz.model';
import { Teacher } from '../../../models/teacher.model';

@Component({
  selector: 'app-clazz-list',
  templateUrl: './clazz-list.component.html',
  styleUrls: ['./clazz-list.component.css'],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class ClazzListComponent implements OnInit {
  clazzes: Clazz[] = [];
  teachers: Teacher[] = [];
  p: number = 1;
  isAddClazzModalVisible: boolean = false;
  // Đảm bảo Clazz có id và lichThi (mặc dù lichThi ban đầu có thể null ở backend)
  newClazz: Clazz = { maLop: '', maHocPhan: '', hocki: '', lichThi: '' };

  // Các biến cho tìm kiếm tổng quát (maHocPhan và hocki)
  isSearchClazzModalVisible: boolean = false;
  searchMaHocPhan: string = '';
  searchHocki: string = ''; // Biến này vẫn dùng cho tìm kiếm tổng quát
  searchClazzResults: Clazz[] | null = null;
  searchClazzError: string = '';

  // === Biến mới cho tìm kiếm chỉ theo Học Kỳ ===
  isSearchBySemesterModalVisible: boolean = false; // Biến này có vẻ chưa dùng trong HTML cũ của bạn, cân nhắc việc thêm modal hoặc dùng input trực tiếp
  searchOnlySemesterName: string = '';
  searchBySemesterError: string = '';

  // === Biến mới cho chức năng Sửa (Update Lịch Thi) ===
  isEditLichThiModalVisible: boolean = false;
  editingClazz: Clazz | null = null; // Lớp học đang được chỉnh sửa
  newExamDate: string = ''; // Giá trị lịch thi mới (dạng string cho input type="date")
  editLichThiError: string = '';


  constructor(
    private clazzService: ClazzService,
    private teacherService: TeacherService
  ) { }

  ngOnInit(): void {
    this.loadClazzes();
    this.loadTeachers(); // Tải danh sách giáo viên để có thể hiển thị tên
  }

  loadClazzes(): void {
    this.clazzService.getAllClazzes().subscribe(
      (data: Clazz[]) => {
        this.clazzes = data;
      },
      (error) => {
        console.error('Lỗi khi tải danh sách lớp học:', error);
      }
    );
  }

  loadTeachers(): void {
    this.teacherService.getAllTeachers().subscribe(
      (data: Teacher[]) => {
        this.teachers = data;
      },
      (error) => {
        console.error('Lỗi khi tải danh sách giáo viên:', error);
      }
    );
  }

  toggleAddClazzModal(): void {
    this.isAddClazzModalVisible = !this.isAddClazzModalVisible;
    this.newClazz = { maLop: '', maHocPhan: '', hocki: '', lichThi: '' }; // Reset form khi mở, thêm lichThi
  }

  addClazz(): void {
    this.clazzService.addClazz(this.newClazz).subscribe(
      (response) => {
        console.log('Thêm lớp thành công:', response);
        this.loadClazzes(); // Tải lại danh sách lớp sau khi thêm
        this.toggleAddClazzModal(); // Đóng modal
      },
      (error) => {
        console.error('Lỗi khi thêm lớp:', error);
      }
    );
  }

  openSearchClazzModal(): void {
    this.isSearchClazzModalVisible = true;
    this.searchMaHocPhan = '';
    this.searchHocki = '';
    this.searchClazzResults = null;
    this.searchClazzError = '';
  }

  closeSearchClazzModal(): void {
    this.isSearchClazzModalVisible = false;
    this.searchClazzResults = null; // Reset kết quả tìm kiếm khi đóng modal
    this.searchClazzError = '';
    this.loadClazzes(); // Tải lại toàn bộ danh sách khi đóng modal
  }

  searchClazzes(): void {
    // Dựa trên ClazzService của bạn, searchClazzes đang gọi đến baseUrl2 (courses)
    // và có format là maHocPhan-hocki.
    // Nếu bạn muốn tìm kiếm theo MaHocPhan và HocKi trên API Clazzes, bạn cần:
    // 1. Đảm bảo backend có endpoint tương ứng (ví dụ: /clazzes/search-by-mahocphan-hocki?maHocPhan=X&hocki=Y)
    // 2. Sử dụng phương thức đó trong ClazzService của Angular.
    // Nếu bạn đang muốn dùng `searchClazzes2` cho mục đích này (tức là /clazzes/search/{ctdtCode}/{khoa}/{hocKi})
    // thì logic kiểm tra và gọi API sẽ khác.
    // Tôi sẽ giữ nguyên cách gọi searchClazzes của bạn nhưng lưu ý về endpoint.
    if (!this.searchMaHocPhan.trim() || !this.searchHocki.trim()) {
      this.searchClazzError = 'Vui lòng nhập Mã Học Phần và Học Kỳ.';
      return;
    }

    this.searchClazzError = '';
    this.clazzService.searchClazzes(this.searchMaHocPhan, this.searchHocki).subscribe( // Sử dụng searchClazzes của bạn
      (results: Clazz[]) => {
        this.clazzes = results;
        this.p = 1;
        this.isSearchClazzModalVisible = false;
        this.searchClazzResults = null;
        if (results && results.length === 0) {
          this.searchClazzError = 'Không tìm thấy lớp học nào phù hợp với thông tin đã nhập.';
        }
      },
      (error: any) => {
        console.error('Lỗi khi tìm kiếm lớp học:', error);
        this.searchClazzError = 'Đã xảy ra lỗi khi tìm kiếm lớp học.';
        this.searchClazzResults = null;
        this.clazzes = [];
      }
    );
  }

  // === Phương thức cho tìm kiếm chỉ theo Học Kỳ ===
  // openSearchBySemesterModal(): void { // Bạn có thể không cần modal riêng cho chức năng này
  //   this.isSearchBySemesterModalVisible = true;
  //   this.searchOnlySemesterName = '';
  //   this.searchBySemesterError = '';
  // }

  // closeSearchBySemesterModal(): void { // Nếu không có modal thì không cần phương thức này
  //   this.isSearchBySemesterModalVisible = false;
  //   this.searchOnlySemesterName = '';
  //   this.searchBySemesterError = '';
  //   this.loadClazzes();
  // }

  searchClazzesBySemester(): void {
    if (!this.searchOnlySemesterName.trim()) {
      this.searchBySemesterError = 'Vui lòng nhập tên Học Kỳ.';
      return;
    }

    this.searchBySemesterError = '';
    this.clazzService.getClazzesBySemesterName(this.searchOnlySemesterName).subscribe(
      (results: Clazz[]) => {
        this.clazzes = results;
        this.p = 1;
        // this.isSearchBySemesterModalVisible = false; // Bỏ dòng này nếu không dùng modal
        if (results && results.length === 0) {
          this.searchBySemesterError = 'Không tìm thấy lớp học nào cho học kỳ này.';
        }
      },
      (error: any) => {
        console.error('Lỗi khi tìm kiếm theo học kỳ:', error);
        if (error.status === 404) {
          this.searchBySemesterError = 'Không tìm thấy học kỳ này trong hệ thống.';
        } else if (error.status === 400) {
          this.searchBySemesterError = 'Yêu cầu không hợp lệ. Vui lòng kiểm tra lại tên học kỳ.';
        } else {
          this.searchBySemesterError = 'Đã xảy ra lỗi khi tìm kiếm lớp học theo học kỳ.';
        }
        this.clazzes = [];
      }
    );
  }

  clearSemesterSearch(): void {
    this.searchOnlySemesterName = '';
    this.searchBySemesterError = '';
    this.loadClazzes();
  }

  getTeacherName(teachers: any[] | undefined): string {
    if (!teachers || teachers.length === 0) {
      return 'Chưa có';
    }
    const teacherNames = teachers.map(teacher => teacher.name);
    return teacherNames.join(', ');
  }

  // === Phương thức cho chức năng Sửa (Update Lịch Thi) ===

  openEditLichThiModal(clazz: Clazz): void {
    if (clazz.id === undefined || clazz.id === null) {
      console.error('Lớp học không có ID. Không thể chỉnh sửa lịch thi.');
      this.editLichThiError = 'Lớp học không có ID. Không thể chỉnh sửa lịch thi.';
      return;
    }
    this.editingClazz = { ...clazz }; // Tạo bản sao để tránh thay đổi trực tiếp dữ liệu trong bảng
    // Định dạng ngày để hiển thị trong input type="date" (YYYY-MM-DD)
    this.newExamDate = clazz.lichThi ? this.formatDate(clazz.lichThi) : '';
    this.editLichThiError = ''; // Reset lỗi
    this.isEditLichThiModalVisible = true;
  }

  closeEditLichThiModal(): void {
    this.isEditLichThiModalVisible = false;
    this.editingClazz = null;
    this.newExamDate = '';
    this.editLichThiError = '';
  }

  updateLichThi(): void {
    if (!this.editingClazz || this.editingClazz.id === undefined || this.editingClazz.id === null) {
      this.editLichThiError = 'Không có lớp học nào được chọn để cập nhật hoặc thiếu ID.';
      return;
    }
    // newExamDate có thể là chuỗi rỗng nếu người dùng không nhập ngày thi (tức là muốn xóa lịch thi)
    // Backend sẽ xử lý chuỗi rỗng thành null LocalDate
    this.editLichThiError = '';

    // GỌI PHƯƠNG THỨC MỚI VÀ CHUYÊN DỤNG TỪ ClazzService
    this.clazzService.updateClazzExamDate(this.editingClazz.id, this.newExamDate).subscribe(
      (updatedClazz) => {
        console.log('Cập nhật lịch thi thành công:', updatedClazz);
        this.loadClazzes(); // Tải lại danh sách để thấy thay đổi
        this.closeEditLichThiModal(); // Đóng modal
      },
      (error) => {
        console.error('Lỗi khi cập nhật lịch thi:', error);
        if (error.status === 404) {
          this.editLichThiError = 'Không tìm thấy lớp học để cập nhật lịch thi.';
        } else if (error.status === 400) {
          this.editLichThiError = 'Dữ liệu lịch thi không hợp lệ. Vui lòng kiểm tra định dạng ngày.';
        } else {
          this.editLichThiError = 'Đã xảy ra lỗi khi cập nhật lịch thi. Vui lòng thử lại.';
        }
      }
    );
  }

  // Hàm trợ giúp để định dạng ngày thành 'YYYY-MM-DD' cho input type="date"
  private formatDate(dateString: string): string {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) { // Kiểm tra nếu ngày không hợp lệ
      console.error("Invalid date string for formatting:", dateString);
      return ''; // Trả về chuỗi rỗng nếu không hợp lệ
    }
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2); // Month is 0-indexed
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }
}