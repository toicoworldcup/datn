// src/app/core/services/auth.service.ts
import { Injectable, PLATFORM_ID, Inject } from '@angular/core'; // Thêm PLATFORM_ID, Inject
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common'; // Thêm isPlatformBrowser

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // Thay đổi apiUrl này nếu backend của bạn không chạy trên localhost
  // Khi deploy lên Vercel, bạn cần backend có URL công khai, không phải localhost
  private apiUrl = 'http://localhost:8080/api/auth/login'; 

  private isBrowser: boolean; // Thêm biến để kiểm tra môi trường

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object // Inject PLATFORM_ID
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId); // Xác định môi trường
  }

  login(username: string, password: string): Observable<any> {
    return this.http.post(this.apiUrl, { username, password });
  }

  getToken(): string | null {
    if (this.isBrowser) { // Chỉ truy cập localStorage nếu đang ở trình duyệt
      return localStorage.getItem('token');
    }
    return null; // Trả về null nếu không phải trình duyệt (trong quá trình build)
  }

  getRole(): string | null {
    if (this.isBrowser) { // Chỉ truy cập localStorage nếu đang ở trình duyệt
      return localStorage.getItem('role');
    }
    return null; // Trả về null nếu không phải trình duyệt
  }

  isLoggedIn(): boolean {
    // Phương thức này giờ sẽ an toàn vì getToken() đã xử lý kiểm tra môi trường
    return !!this.getToken();
  }

  logout(): void {
    if (this.isBrowser) { // Chỉ truy cập localStorage nếu đang ở trình duyệt
      localStorage.removeItem('token');
      localStorage.removeItem('role');
    }
  }

  isAuthenticated(): boolean {
    // Phương thức này cũng sẽ an toàn
    return !!this.getToken();
  }
}