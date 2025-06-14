import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { Clazz } from "../models/clazz.model";

// Thêm từ khóa export để có thể import interface này ở các file khác
export interface AssignmentRequest {
  maLop: string;
  maGv: string;
  hocKi: string;
}

@Injectable({
  providedIn: "root",
})
export class ClazzService {
 private baseUrl = "http://localhost:8080/clazzes";
private baseUrl2 = "http://localhost:8080/courses";

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
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: "text" });
  }

  // GET /clazzes/clazz/teacher-class-count
  getClazzCountPerTeacher(): Observable<{ [teacherId: string]: number }> {
    return this.http.get<{ [teacherId: string]: number }>(
      `${this.baseUrl}/clazz/teacher-class-count`
    );
  }

  // POST /clazzes/assign-teachers
  assignTeachersToClazzes(assignments: AssignmentRequest[]): Observable<any> {
    return this.http.post(`${this.baseUrl}/assign-teachers`, assignments);
  }

  generateClazzes(ctdtCode: string, khoa: string, hocKi: string): Observable<Clazz[]> {
    return this.http.post<Clazz[]>(`${this.baseUrl}/generate-by-ctdt-khoa/${ctdtCode}/${khoa}/${hocKi}`, null);
  }


  getClazzesByCTDTKhoaHocKi(ctdtCode: string | null, khoa: string | null, hocKi: string | null): Observable<Clazz[]> {
    let params = new HttpParams();
    if (ctdtCode) {
      params = params.set('ctdtCode', ctdtCode);
    }
    if (khoa) {
      params = params.set('khoa', khoa);
    }
    if (hocKi) {
      params = params.set('hocKi', hocKi);
    }
    return this.http.get<Clazz[]>(`${this.baseUrl}/search`, { params });
  }

   searchClazzes(maHocPhan: string, hocki: string): Observable<Clazz[]> {
     return this.http.get<Clazz[]>(`${this.baseUrl2}/clazzes/${maHocPhan}-${hocki}`);
  }
  searchClazzes2(ctdtCode: string, khoa: string, hocKi: string): Observable<Clazz[]> {
    return this.http.get<Clazz[]>(`${this.baseUrl}/search/${ctdtCode}/${khoa}/${hocKi}`);
  }
  getClazzesBySemesterName(semesterName: string): Observable<Clazz[]> {
    return this.http.get<Clazz[]>(`${this.baseUrl}/by-semester-name/${semesterName}`);
  }
  // Gọi đến endpoint PUT /clazzes/{clazzId}/update-exam-date
  updateClazzExamDate(clazzId: number, newExamDate: string | null): Observable<Clazz> {
    let params = new HttpParams();
    if (newExamDate !== null && newExamDate !== '') {
      params = params.append('lichThi', newExamDate);
    } else if (newExamDate === '') { // Nếu muốn xóa lịch thi, gửi chuỗi rỗng
      params = params.append('lichThi', '');
    }
    // Tham số thứ hai là `null` vì dữ liệu được gửi qua query parameters, không phải request body
    return this.http.put<Clazz>(`${this.baseUrl}/${clazzId}/update-exam-date`, null, { params: params });
  }
  // GET /clazzes/unassigned
  getUnassignedClazzes(ctdtCode: string | null, khoa: string | null, hocKi: string | null): Observable<Clazz[]> {
    let params = new HttpParams();
    if (ctdtCode) {
        params = params.set('ctdtCode', ctdtCode);
    }
    if (khoa) {
        params = params.set('khoa', khoa);
    }
    if (hocKi) {
        params = params.set('hocKi', hocKi);
    }
    return this.http.get<Clazz[]>(`${this.baseUrl}/unassigned`, { params });
}
}