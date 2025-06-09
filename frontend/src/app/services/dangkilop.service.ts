import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DklDTO, DklRequest } from '../models/dkl.model';
import { SpecialClassRequestDTO } from '../models/SpecialClassRequestDTO.model';
import { UpdateRequestList } from '../models/UpdateRequestList.model';

@Injectable({
  providedIn: 'root'
})
export class DangKiLopService {
  private baseUrl = 'http://localhost:8080/dangkilop';

  constructor(private http: HttpClient) { }

  getAllDangKiLopBySemester(semester: string): Observable<DklDTO[]> {
    return this.http.get<DklDTO[]>(`${this.baseUrl}/hocki/${semester}`);
  }

  getDangKiLopById(id: number): Observable<DklDTO> {
    return this.http.get<DklDTO>(`${this.baseUrl}/${id}`);
  }

  registerClass(dklRequest: DklRequest): Observable<DklDTO> {
    return this.http.post<DklDTO>(`${this.baseUrl}/register`, dklRequest);
  }

  deleteDangKiLop(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // API mới để xóa nhiều đăng ký lớp
  bulkDeleteDangKiLop(registrationIds: number[]): Observable<any> {
    return this.http.delete(`${this.baseUrl}/bulk-delete`, { body: registrationIds, responseType: 'text' });
  }

  getPendingSpecialRequests(): Observable<SpecialClassRequestDTO[]> {
    return this.http.get<SpecialClassRequestDTO[]>(`${this.baseUrl}/special-requests/pending`);
  }

  approveSpecialRequest(requestId: number): Observable<any> { // Thay đổi kiểu trả về
    return this.http.post(`${this.baseUrl}/special-requests/${requestId}/approve`, null, { responseType: 'text' });
  }

  rejectSpecialRequest(requestId: number): Observable<any> { // Thay đổi kiểu trả về
    return this.http.post(`${this.baseUrl}/special-requests/${requestId}/reject`, null, { responseType: 'text' });
  }

  approveMultipleSpecialRequests(requestIds: number[]): Observable<any> { // Thay đổi kiểu trả về
    const body: UpdateRequestList = { requestIds };
    return this.http.post(`${this.baseUrl}/special-requests/approve-multiple`, body, { responseType: 'text' });
  }

  rejectMultipleSpecialRequests(requestIds: number[]): Observable<any> { // Thay đổi kiểu trả về
    const body: UpdateRequestList = { requestIds };
    return this.http.post(`${this.baseUrl}/special-requests/reject-multiple`, body, { responseType: 'text' });
  }
}