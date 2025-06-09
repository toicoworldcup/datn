import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { StudentService } from '../../../services/student.service';
import { CommonModule } from '@angular/common';
import { NgxPaginationModule } from 'ngx-pagination';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-taikhoan',
  templateUrl: './taikhoan.component.html',
  styleUrls: ['./taikhoan.component.css'] ,// Nếu bạn có file CSS riêng,
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class TaikhoanComponent {
  oldPassword = '';
  newPassword = '';
  confirmNewPassword = '';
  message = '';
  error = '';

  constructor(
    private studentService: StudentService,
    private router: Router
  ) {}

  changePassword() {
    this.message = '';
    this.error = '';

    if (!this.oldPassword || !this.newPassword || !this.confirmNewPassword) {
      this.error = 'Vui lòng nhập đầy đủ thông tin.';
      return;
    }

    if (this.newPassword !== this.confirmNewPassword) {
      this.error = 'Mật khẩu mới và xác nhận mật khẩu không khớp.';
      return;
    }

    this.studentService.changePassword(this.oldPassword, this.newPassword).subscribe({
      next: (response) => {
        this.message = response;
        // Có thể reset form sau khi đổi mật khẩu thành công
        this.oldPassword = '';
        this.newPassword = '';
        this.confirmNewPassword = '';
      },
      error: (err) => {
        this.error = err.error || 'Đã xảy ra lỗi khi đổi mật khẩu.';
      },
    });
  }

  // Ví dụ về nút đăng xuất nếu bạn cần
  logout() {
    // Xử lý logic đăng xuất tại đây (ví dụ: xóa token, clear local storage)
    this.router.navigate(['/login']); // Chuyển hướng về trang đăng nhập
  }
}