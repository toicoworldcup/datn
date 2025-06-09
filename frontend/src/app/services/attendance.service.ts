import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AttendanceRequest } from '../models/attendance-request.model';
import { Attendance } from '../models/attendance.model';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private apiUrl = 'http://localhost:8080/attendance';

  constructor(private http: HttpClient) { }

  getAllAttendances(): Observable<Attendance[]> {
    return this.http.get<Attendance[]>(this.apiUrl);
  }

  getAttendanceByClazzAndStudentAndSemester(maLop: string, mssv: string, hocki: string, attendanceDate: string): Observable<Attendance[]> {
    return this.http.get<Attendance[]>(`${this.apiUrl}/clazz/${maLop}/student/${mssv}/semester/${hocki}/${attendanceDate}`);
  }

  markAttendance(attendanceRequest: AttendanceRequest): Observable<Attendance> {
    return this.http.post<Attendance>(this.apiUrl, attendanceRequest);
  }

  markBulkAttendance1(requests: AttendanceRequest[]): Observable<any> {
    return this.http.post(this.apiUrl, requests);
  }

  markBulkAttendance(requests: AttendanceRequest[]): Observable<any> {
    return this.http.post(`${this.apiUrl}/bulk`, requests, { responseType: 'text' });
  }

  getAttendanceByClazzAndSemesterAndDate(maLop: string, hocki: string, date: string): Observable<Attendance[]> {
    return this.http.get<Attendance[]>(`${this.apiUrl}/clazz/${maLop}/semester/${hocki}/date/${date}`);
  }

  // API endpoint để lấy số buổi vắng (chỉ dùng path variable)
  getAbsentCount(maLop: string, mssv: string, toDate: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/absent-count/${mssv}/clazz/${maLop}/to-date/${toDate}`);
  }

  // API endpoint mới để lấy danh sách các ngày đã điểm danh
  getAttendanceDatesByClazzAndSemester(maLop: string, hocKi: string): Observable<string[]> {
    const url = `${this.apiUrl}/clazz/${maLop}/semester/${hocKi}/dates`;
    return this.http.get<string[]>(url);
  }

  // API endpoint mới để kiểm tra xem điểm danh cho ngày đã tồn tại chưa
  checkAttendanceExists(maLop: string, hocKi: string, attendanceDate: string): Observable<boolean> {
    const url = `${this.apiUrl}/check-exists/${maLop}/${hocKi}/${attendanceDate}`;
    return this.http.get<boolean>(url);
  }
}