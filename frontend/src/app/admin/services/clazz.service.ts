import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Clazz } from '../models/clazz.model';

@Injectable({
  providedIn: 'root',
})
export class ClazzService {
  private baseUrl = 'http://localhost:8080/clazzes';

  constructor(private http: HttpClient) {}

  // GET /clazzes
  getAllClazzes(): Observable<Clazz[]> {
    return this.http.get<Clazz[]>(`${this.baseUrl}`);
  }

  // GET /clazzes/{maLop}/{hocki}
  getClazzByMaLopAndHocKi(maLop: string, hocki: string): Observable<Clazz> {
    return this.http.get<Clazz>(`${this.baseUrl}/${maLop}/${hocki}`);
  }

  // POST /clazzes
  addClazz(request: Clazz): Observable<Clazz> {
    return this.http.post<Clazz>(`${this.baseUrl}`, request);
  }

  // DELETE /clazzes/{id}
  deleteClazz(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }

  // GET /clazzes/clazz/teacher-class-count
  getClazzCountPerTeacher(): Observable<{ [teacherId: string]: number }> {
    return this.http.get<{ [teacherId: string]: number }>(
      `${this.baseUrl}/clazz/teacher-class-count`
    );
  }

  // POST /clazzes/malop/{maLop}/semester/{hocKi}/assign-teacher/teacher/{maGv}
  assignTeacherToClazz(
    maLop: string,
    hocKi: string,
    maGv: string
  ): Observable<string> {
    return this.http.post(
      `${this.baseUrl}/malop/${maLop}/semester/${hocKi}/assign-teacher/teacher/${maGv}`,
      null,
      {
        responseType: 'text',
      }
    );
  }
  // POST /clazzes/generate?maHocPhan=...&hocKi=...
  generateClazzes(maHocPhan: string, hocKi: string): Observable<string> {
    const params = new HttpParams()
      .set('maHocPhan', maHocPhan)
      .set('hocKi', hocKi);
    return this.http.post(`${this.baseUrl}/generate`, null, {
      params,
      responseType: 'text',
    });
  }
}
