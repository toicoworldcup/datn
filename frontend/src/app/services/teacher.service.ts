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

@Injectable({
  providedIn: "root",
})
export class TeacherService {
  private apiUrl = "http://localhost:8080/teachers";

  constructor(private http: HttpClient) {}

  getAllTeachers(): Observable<Teacher[]> {
    return this.http.get<Teacher[]>(this.apiUrl);
  }

  getTeacherById(maGv: string): Observable<Teacher> {
    return this.http.get<Teacher>(`${this.apiUrl}/${maGv}`);
  }

  addTeacher(teacher: Teacher): Observable<Teacher> {
    return this.http.post<Teacher>(this.apiUrl, teacher);
  }

  updateTeacher(maGv: string, teacher: Teacher): Observable<Teacher> {
    return this.http.put<Teacher>(`${this.apiUrl}/${maGv}`, teacher);
  }

  deleteTeacher(maGv: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${maGv}`);
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