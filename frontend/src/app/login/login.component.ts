import { Component } from '@angular/core';
import { AuthService } from '../core/services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms'; // ⚠️ cần thiết


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule], // ⚠️ thêm dòng này
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    console.log('Đang gửi login:', this.username, this.password);

    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.role);
        localStorage.setItem('username', this.username);  // ✅ thêm dòng này


        if (response.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/home']);
        }
      },
      error: (err) => {
        console.error('Login failed:', err);
        alert('Tài khoản hoặc mật khẩu sai!');
      }
    });
  }
}
