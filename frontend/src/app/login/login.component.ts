import { Component } from '@angular/core';

@Component({
  selector: 'app-login',
  standalone: true,  // Thêm dòng này để đánh dấu là standalone component
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  onLogin(): void {
    if (this.username && this.password) {
      console.log('Đăng nhập với:', this.username, this.password);
    } else {
      console.log('Vui lòng nhập tên đăng nhập và mật khẩu!');
    }
  }
}
