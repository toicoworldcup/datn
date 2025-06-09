// src/app/services/ctdt.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ctdt } from '../models/ctdt.model'; // Import model CTDT

@Injectable({
  providedIn: 'root',
})
export class CTDTService {
  private apiUrl = 'http://localhost:8080/Ctdt'; // Adjust the API endpoint if needed

  constructor(private http: HttpClient) {}

  getAllCTDTs(): Observable<Ctdt[]> {
    return this.http.get<Ctdt[]>(this.apiUrl);
  }

  getCTDTByCode(maCt: string): Observable<Ctdt> {
    return this.http.get<Ctdt>(`${this.apiUrl}/search/${maCt}`);
  }

  addCTDT(ctdtRequest: { name: string; maCt: string; khoa: string }): Observable<Ctdt> {
    return this.http.post<Ctdt>(this.apiUrl, ctdtRequest);
  }

  getCoursesByMaCTAndKhoa(maCt: string, khoa: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${maCt}/${khoa}`);
  }

  assignCoursesToCTDT(maCt: string, khoa: string, maHocPhanList: string[]): Observable<any[]> {
    return this.http.post<any[]>(`${this.apiUrl}/ctdt/assign/${khoa}`, { maCt, maHocPhanList });
  }

  importCoursesFromExcel(maCTDT: string, khoa: string, file: File): Observable<any> {
  const formData: FormData = new FormData();
  formData.append('file', file, file.name);
  return this.http.post(
    `${this.apiUrl}/import-courses/${maCTDT}/${khoa}`,
    formData,
    { responseType: 'text' } // Thêm option này
  );
}
}