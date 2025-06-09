import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { CourseService } from "../../../services/course.service";
import { TeacherService } from "../../../services/teacher.service";
import { SemesterService } from "../../../services/semester.service";
import { NgxPaginationModule } from "ngx-pagination";
import { Observable, forkJoin, of } from 'rxjs'; // Import forkJoin
import { catchError, map } from 'rxjs/operators';

export interface Course {
  maHocPhan: string;
  tenMonHoc: string;
}

@Component({
  selector: "app-course-list",
  templateUrl: "./course-list.component.html",
  styleUrls: ["./course-list.component.css"],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class CourseListComponent implements OnInit {
  teacherModules: Course[] = [];
  selectedSemester: string = "";
  semesters: any[] = [];
  loading: boolean = false;
  error: string = "";

  constructor(
    private courseService: CourseService,
    private semesterService: SemesterService,
    private teacherService: TeacherService
  ) {}

  ngOnInit(): void {
    this.loadSemesters();
  }

  loadSemesters(): void {
    this.loading = true;
    this.error = "";
    this.semesterService.getAllSemesters().subscribe({
      next: (data) => {
        this.semesters = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = "Lỗi khi tải danh sách học kỳ: " + err.message;
        this.loading = false;
      },
    });
  }

  loadTeacherModules(): void {
    if (!this.selectedSemester) {
      this.error = "Vui lòng chọn một học kỳ.";
      return;
    }

    this.loading = true;
    this.error = "";
    this.teacherService
      .getTeacherModulesBySemester(this.selectedSemester)
      .subscribe({
        next: (maHocPhans: string[]) => {
          // Fetch course details for each maHocPhan
          const courseObservables: Observable<Course>[] = maHocPhans.map(maHocPhan =>
            this.courseService.getCourseByMaHocPhan(maHocPhan).pipe(
              catchError(err => {
                console.error(`Error fetching course ${maHocPhan}:`, err);
                //  Return a default Course object or an Observable of it.
                return of({ maHocPhan: maHocPhan, tenMonHoc: 'N/A' }); // Or return of(null)
              })
            )
          );

          // Use forkJoin to wait for all observables to complete
          forkJoin(courseObservables).subscribe((courses: Course[]) => { // Change to Course[]
            this.teacherModules = courses;
            this.loading = false;
          },
          (err) => {
            this.error = "Failed to load course details: " + err.message;
            this.loading = false;
            this.teacherModules = [];
          });
        },
        error: (err) => {
          this.error = "Failed to load teacher modules: " + err.message;
          this.loading = false;
          this.teacherModules = [];
        },
      });
  }
}

