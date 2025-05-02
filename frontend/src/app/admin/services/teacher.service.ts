import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Teacher } from '../models/teacher.model';
export interface TeacherInfoResponse {
  fullName: string;
  className: string;
}

@Injectable({
  providedIn: 'root',
})
export class TeacherService {
  private apiUrl = 'http://localhost:8080/teachers';
  private apiUrl2 = 'http://localhost:8080/api/teacherinfo'; // URL của API backend


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
  getTeacherInfo(): Observable<TeacherInfoResponse> {
      return this.http.get<TeacherInfoResponse>(this.apiUrl2);
    }
  
}
