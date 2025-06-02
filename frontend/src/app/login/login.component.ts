import { Component } from '@angular/core';
import { AuthService } from '../core/services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule], // Thêm CommonModule vào đây
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessage: string = ''; // Thêm biến để hiển thị lỗi

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    console.log('Đang gửi login:', this.username, this.password);
    this.errorMessage = ''; // Reset thông báo lỗi

    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.role);
        localStorage.setItem('username', this.username);

        if (response.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        }
        if (response.role === 'QLDT') {
          this.router.navigate(['/qldt']);
        }
        if (response.role === 'TEACHER') {
          this.router.navigate(['/teacher']);
        }
        if (response.role === 'STUDENT') {
          this.router.navigate(['/student']);
        }
      },
      error: (err) => {
        console.error('Login failed:', err);
        this.errorMessage = 'Tài khoản hoặc mật khẩu sai!'; // Hiển thị thông báo lỗi
      }
    });
  }
}