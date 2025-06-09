// src/app/admin/services/schedule.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Schedule } from '../models/schedule.model'; // Import model

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {
  private baseUrl = 'http://localhost:8080/schedule'; // Điều chỉnh URL nếu cần

  constructor(private http: HttpClient) { }

  generateSchedule(semesterName: string): Observable<Schedule[]> { // Sử dụng Schedule model
    return this.http.post<Schedule[]>(`${this.baseUrl}/generate/${semesterName}`, {});
  }

  getScheduleBySemester(semesterName: string): Observable<Schedule[]> { // Sử dụng Schedule model
    return this.http.get<Schedule[]>(`${this.baseUrl}/semester/${semesterName}`);
  }
  getScheduleByClazzAndSemester(maLop: string, semesterName: string): Observable<Schedule[]> {
    return this.http.get<Schedule[]>(`${this.baseUrl}/class/${maLop}/semester/${semesterName}`);
  }
}