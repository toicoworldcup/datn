import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { Semester } from "../models/semester.model";

@Injectable({
  providedIn: "root",
})
export class SemesterService {
  private baseUrl = "http://localhost:8080/semesters";

  constructor(private http: HttpClient) {}

  // GET /semesters
  getAllSemesters(): Observable<Semester[]> {
    return this.http.get<any[]>(this.baseUrl).pipe(
      map((data) =>
        data.map((s) => ({
          ...s,
          isOpen: s.open,
        }))
      )
    );
  }

 // GET /semesters/all (không filter isOpen)
  getAllSemestersNoFilter(): Observable<Semester[]> {
    return this.http.get<any[]>(`${this.baseUrl}/all`).pipe(
      map((data) =>
        data.map((s) => ({
          name: s.name,
          isOpen: s.open,
        } as Semester))
      )
    );
  }
  // GET /semesters/{name}
  getSemesterByName(name: string): Observable<Semester> {
    return this.http.get<Semester>(`${this.baseUrl}/${name}`);
  }

  // POST /semesters
  createSemester(semester: Semester): Observable<Semester> {
    return this.http.post<Semester>(this.baseUrl, semester);
  }

  // PUT /semesters/{name}
  updateSemester(
    name: string,
    updatedSemester: Semester
  ): Observable<Semester> {
    return this.http.put<Semester>(`${this.baseUrl}/${name}`, updatedSemester);
  }

  // PUT /semesters/{name}/open
  openRegistration(name: string): Observable<Semester> {
    return this.http.put<Semester>(`${this.baseUrl}/${name}/open`, {});
  }

  // PUT /semesters/{name}/close
  closeRegistration(name: string): Observable<Semester> {
    return this.http.put<Semester>(`${this.baseUrl}/${name}/close`, {});
  }
}
