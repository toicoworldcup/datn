import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Course } from '../models/course.model';

@Injectable({
  providedIn: 'root',
})
export class CourseService {
  private apiUrl = 'http://localhost:8080/courses'; // Địa chỉ API của bạn

  constructor(private http: HttpClient) {}

  // Lấy tất cả các khóa học
  getAllCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(this.apiUrl);
  }

  // Lấy khóa học theo mã học phần
  getCourseByMaHocPhan(maHocPhan: string): Observable<Course> {
    return this.http.get<Course>(`${this.apiUrl}/${maHocPhan}`);
  }
  getCourses(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }

  // Lấy các lớp học theo mã học phần và tên học kỳ
  getClazzesByMaHocPhan(
    maHocPhan: string,
    semesterName: string
  ): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/clazzes/${maHocPhan}-${semesterName}`
    );
  }

  // Tạo mới khóa học
  createCourse(course: Course): Observable<Course> {
    return this.http.post<Course>(this.apiUrl, course);
  }
  searchCourse(maHocPhan: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${maHocPhan}`);
  }

  // Cập nhật khóa học
  updateCourse(id: number, course: Course): Observable<Course> {
    return this.http.put<Course>(`${this.apiUrl}/${id}`, course);
  }

  // Xóa khóa học
  deleteCourse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
