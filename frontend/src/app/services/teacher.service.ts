import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { Teacher } from "../models/teacher.model";
import { Schedule } from "../models/schedule.model";

export interface TeacherInfoResponse {
  fullName: string;
  className: string;
}
interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

// Định nghĩa một interface cho payload khi cập nhật giáo viên
// để đảm bảo rằng bạn gửi đúng các trường mà backend mong đợi
export interface TeacherUpdatePayload {
  id?: number; // Optional vì nó có thể nằm trong URL
  maGv: string;
  name: string;
  email: string;
  phoneNumber: string;
  dateOfBirth: string; // Hoặc Date
  address: string;
  gender: string;
  cccd: string;
  departmentName: string; // <-- Đây là trường mới bạn muốn gửi dưới dạng chuỗi
  // Thêm các trường khác nếu có, ví dụ: user, clazzes nếu bạn muốn update chúng
}

@Injectable({
  providedIn: "root",
})
export class TeacherService {
  private apiUrl = "http://14.225.204.150:8080/teachers"; // Đảm bảo đúng IP và Port của Backend

  constructor(private http: HttpClient) {}

  getAllTeachers(): Observable<Teacher[]> {
    return this.http.get<Teacher[]>(this.apiUrl);
  }

  getTeacherById(maGv: string): Observable<Teacher> {
    return this.http.get<Teacher>(`${this.apiUrl}/${maGv}`);
  }

  addTeacher(teacher: Teacher): Observable<Teacher> {
    // Phương thức này có thể vẫn gửi Teacher entity bình thường
    // nếu convertToEntity ở backend của bạn đã xử lý DepartmentName
    return this.http.post<Teacher>(this.apiUrl, teacher);
  }

  // Sửa đổi phương thức updateTeacher để nhận TeacherUpdatePayload
  updateTeacher(id: number, payload: TeacherUpdatePayload): Observable<Teacher> {
    return this.http.put<Teacher>(`${this.apiUrl}/${id}`, payload);
  }

  deleteTeacher(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchTeacher(maGv: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${maGv}`);
  }

  getLoggedInTeacherInfo(): Observable<Teacher> {
    return this.http.get<Teacher>(`${this.apiUrl}/myinfo`);
  }

  getTeacherClassesBySemester(semesterName: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clazzes/${semesterName}`);
  }

  getTeacherModulesBySemester(semesterName: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/course/${semesterName}`);
  }

  getTeacherScheduleBySemester(semesterName: string): Observable<Schedule[]> {
    return this.http.get<Schedule[]>(`${this.apiUrl}/teacher/semester/${semesterName}`);
  }

  importTeachers(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/import`, formData);
  }

  changePassword(oldPassword: string, newPassword: string): Observable<any> {
    const body: ChangePasswordRequest = { oldPassword, newPassword };
    return this.http.post(`${this.apiUrl}/change-password`, body, { responseType: 'text' });
  }
}