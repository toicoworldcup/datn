import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { Student } from "../models/student.model";
import { Dkl } from "../models/dkl.model";
import { Course } from "../models/course.model";
import { DkhpDTO } from "../models/dkhp.model";
import { DkhpRequest } from "../models/dkhpRequest.model";
import { Grade } from "../models/grade.model";
import { StudentWithGrades } from "../models/StudentWithGrades.model";
import { Schedule } from "../models/schedule.model";
import { CthDTO } from "../models/CthDTO.model";

export interface StudentInfoResponse {
  fullName: string;
  className: string;
}

interface CtdtCoursesResponse {
  registered: any[];
  unregistered: any[];
}

interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

@Injectable({
  providedIn: "root",
})
export class StudentService {
  private apiUrl = "http://localhost:8080/students";
  private apiUrl2 = "http://localhost:8080/api/studentinfo";
  private apiUrl3 = "http://localhost:8080/dangkilop";
  private apiUrlDkhp = "http://localhost:8080/dangkihocphan";
  private clazzesUrl = "http://localhost:8080/clazzes";
  private gradesUrl = "http://localhost:8080/grades";

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
  getStudentByMssv(mssv: string): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/${mssv}`);
  }

  importStudents(file: File): Observable<any> {
    const formData = new FormData();
    formData.append("file", file);
    return this.http.post(`${this.apiUrl}/import`, formData, {
      responseType: "text" as "json",
    });
  }

  exportStudents(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/export`, {
      responseType: "blob",
    });
  }
  getStudentInfo(): Observable<StudentInfoResponse> {
    return this.http.get<StudentInfoResponse>(this.apiUrl2);
  }
  getCurrentStudentInfo(): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/me`);
  }
  getAllDangkilopByMSSV(semester: string): Observable<Dkl[]> {
    return this.http.get<Dkl[]>(`${this.apiUrl3}/hocki/${semester}`);
  }
  createDangkihocphan(request: DkhpRequest): Observable<any> {
    return this.http.post<any>(this.apiUrlDkhp, request);
  }

  deleteDangkihocphan(id: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrlDkhp}/${id}`, {
      responseType: "text" as "json",
    });
  }

  getCtdtCourses(): Observable<{ all: Course[]; registered: Course[]; unregistered: Course[] }> {
    return this.http
      .get<{ registered: any[]; unregistered: any[] }>(`${this.apiUrlDkhp}/ctdt-courses`)
      .pipe(
        map((response) => {
          const registeredCourses: Course[] = response.registered.map(
            (course: any) =>
              ({
                maHocPhan: course.maHocPhan,
                tenHocPhan: course.tenHocPhan,
                soTinChi: course.soTinChi,
                tenMonHoc: course.tenMonHoc,
                khoiLuong: course.khoiLuong,
                suggestedSemester:course.suggestedSemester,
                finalGrade:course.finalGrade,
                gradeLetter:course.gradeLetter


              } as Course)
          );
          const unregisteredCourses: Course[] = response.unregistered.map(
            (course: any) =>
              ({
                maHocPhan: course.maHocPhan,
                tenHocPhan: course.tenHocPhan,
                soTinChi: course.soTinChi,
                tenMonHoc: course.tenMonHoc,
                khoiLuong: course.khoiLuong,
                suggestedSemester:course.suggestedSemester

              } as Course)
          );
          const allCourses: Course[] = [...registeredCourses, ...unregisteredCourses];
          return {
            all: allCourses,
            registered: registeredCourses,
            unregistered: unregisteredCourses,
          };
        })
      );
  }

  getMyDangkihocphan(semester: string): Observable<DkhpDTO[]> {
    return this.http.get<DkhpDTO[]>(
      `${this.apiUrlDkhp}/my-registrations/hocki/${semester}`
    );
  }

  getMissingCourses(): Observable<Course[]> {
    return this.http.get<any[]>(`${this.apiUrlDkhp}/missing-grades`).pipe(
      map((courses: any[]) =>
        courses.map(
          (course: any) =>
            ({
              maHocPhan: course.maHocPhan,
              tenHocPhan: course.tenHocPhan,
              soTinChi: course.soTinChi,
              tenMonHoc: course.tenMonHoc,
              khoiLuong: course.khoiLuong,
            } as Course)
        )
      )
    );
  }
  getMyAllDangkihocphan(): Observable<Course[]> {
    return this.http
      .get<{ registered: any[]; unregistered: any[] }>(`${this.apiUrlDkhp}/ctdt-courses`)
      .pipe(
        map((response) =>
          response.registered.map(
            (course: any) =>
              ({
                maHocPhan: course.maHocPhan,
                tenHocPhan: course.tenHocPhan,
                soTinChi: course.soTinChi,
                tenMonHoc: course.tenMonHoc,
                khoiLuong: course.khoiLuong,
                // Thêm các thuộc tính khác nếu cần
              } as Course)
          )
        )
      );
  }

  getStudentsByClazzAndSemester(
    maLop: string,
    hocki: string
  ): Observable<StudentWithGrades[]> {
    return this.http.get<StudentWithGrades[]>(
      `${this.clazzesUrl}/${maLop}/${hocki}/students`
    );
  }

  addGrade(grade: Grade): Observable<any> {
    return this.http.post(this.gradesUrl, grade);
  }

    updateGrade(mssv: string, maLop: string, semesterName: string, grade: Grade): Observable<Grade> {
    return this.http.put<Grade>(`${this.gradesUrl}/${mssv}/${maLop}/${semesterName}`, grade);
  }
    getGradeByStudentAndClassAndSemester(mssv: string, maLop: string, semesterName: string): Observable<Grade> {
    return this.http.get<Grade>(`${this.gradesUrl}/student/${mssv}/class/${maLop}/semester/${semesterName}`);
  }
  getMyScheduleBySemester(semesterName: string): Observable<Schedule[]> {
        return this.http.get<Schedule[]>(`${this.apiUrl}/schedule/semester/${semesterName}`);

  }
  getMyGradesBySemester(semesterName: string): Observable<any> { // Thay any[] bằng any
    return this.http.get(`${this.gradesUrl}/student/me/semester/${semesterName}`, { responseType: 'text' });
  }
    getChuongTrinhDaoTaoVaDiem(): Observable<CthDTO[]> {
    return this.http.get<CthDTO[]>(`${this.apiUrl}/me/chuong-trinh-dao-tao-va-diem`);
  }
  xetTotNghiep(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me/xet-tot-nghiep`);
  }

  changePassword(oldPassword: string, newPassword: string): Observable<any> {
    const body: ChangePasswordRequest = { oldPassword, newPassword };
    return this.http.post(`${this.apiUrl}/change-password`, body, { responseType: 'text' });
  }
}