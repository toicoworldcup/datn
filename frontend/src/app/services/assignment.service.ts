import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Teacher } from '../models/teacher.model';

@Injectable({
  providedIn: 'root'
})
export class AssignmentService {

  private baseUrl = 'http://localhost:8080/clazzes'; // Cập nhật URL tùy backend

  constructor(private http: HttpClient) {}

  // POST /clazzes/malop/{maLop}/semester/{hocKi}/assign-teacher/teacher/{maGv}
  assignTeacherClazz(maLop: string, hocKi: string, maGv: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/malop/${maLop}/semester/${hocKi}/assign-teacher/teacher/${maGv}`, null, {
      responseType: 'text'
    });
  }

  // GET /clazzes/malop/{maLop}/semester/{hocKi}/available-teachers
  getAvailableTeachersForClazz(maLop: string, hocKi: string): Observable<Teacher[]> {
    return this.http.get<Teacher[]>(`${this.baseUrl}/malop/${maLop}/semester/${hocKi}/available-teachers`);
  }

  // (Tùy chọn) lấy danh sách các phân công hiện tại
  getAllAssignments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}`);
  }

  // (Tùy chọn) xóa phân công
  deleteAssignment(maGv: string, maLop: string, hocKi: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}?maGv=${maGv}&maLop=${maLop}&hocKi=${hocKi}`);
  }
}