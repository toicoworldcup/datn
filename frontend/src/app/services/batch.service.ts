import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Batch } from '../models/batch.model';

@Injectable({
  providedIn: 'root'
})
export class BatchService {
  private baseUrl = 'http://localhost:8080/batches'; // Điều chỉnh URL API của bạn

  constructor(private http: HttpClient) { }

  // GET /batches
  getAllBatches(): Observable<Batch[]> {
    return this.http.get<Batch[]>(`${this.baseUrl}`);
  }

  // GET /batches/{id}
  getBatchById(id: number): Observable<Batch> {
    return this.http.get<Batch>(`${this.baseUrl}/${id}`);
  }

  // POST /batches
  addBatch(batch: Batch): Observable<Batch> {
    return this.http.post<Batch>(`${this.baseUrl}`, batch);
  }

  // PUT /batches/{id}
  updateBatch(id: number, batch: Batch): Observable<Batch> {
    return this.http.put<Batch>(`${this.baseUrl}/${id}`, batch);
  }

  // DELETE /batches/{id}
  deleteBatch(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}