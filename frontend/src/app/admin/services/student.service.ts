import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface StudentInfoResponse {
  fullName: string;
  className: string;
}

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private apiUrl = 'http://localhost:8080/students';
  private apiUrl2 = 'http://localhost:8080/api/studentinfo'; // URL của API backend


  constructor(private http: HttpClient) {}

  getStudents(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }

  searchStudent(mssv: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/search/${mssv}`);
  }
  getStudentsByBatch(batch: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/batch/${batch}`);
  }
  getStudentsByCtdt(maCt: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search/by-code/${maCt}`);
  }
  

  importStudents(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/import`, formData, {
      responseType: 'text' 
    });
  }

  exportStudents(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/export`, {
      responseType: 'blob'
    });
  }
  getStudentInfo(): Observable<StudentInfoResponse> {
    return this.http.get<StudentInfoResponse>(this.apiUrl2);
  }
}
