import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { QldtRoutingModule } from "./qldt-routing.module";
import { provideHttpClient } from "@angular/common/http";
import { RouterModule } from "@angular/router";

// Standalone Component imports...
import { StudentListComponent } from "./components/student-list/student-list.component";
import { TeacherListComponent } from "./components/teacher-list/teacher-list.component";
import { CourseListComponent } from "./components/course-list/course-list.component";
import { ClazzListComponent } from "./components/clazz-list/clazz-list.component";
import { SemesterListComponent } from "./components/semester-list/semester-list.component";
import { AssignTeacherComponent } from "./components/assign-teacher/assign-teacher.component";
import { CreateClazzComponent } from "./components/create-clazz/create-clazz.component";
import { HeaderComponent } from "../header/header.component"; // Đảm bảo đường dẫn này đúng
import { LayoutQldtComponent } from "./layout-qldt/layout-qldt.component";
import { QldtComponent } from "./qldt.component";

@NgModule({
  declarations: [
    LayoutQldtComponent,
     // Khai báo QldtComponent
    // HeaderComponent // KHÔNG cần khai báo lại nếu là standalone
  ],
  imports: [
    CommonModule,
    QldtRoutingModule,
    RouterModule, // ✅ Import RouterModule
    StudentListComponent, // Import trực tiếp các standalone component
    TeacherListComponent,
    CourseListComponent,
    ClazzListComponent,
    SemesterListComponent,
    AssignTeacherComponent,
    CreateClazzComponent,
    HeaderComponent,
    QldtComponent, // ✅ Import HeaderComponent
  ],
  providers: [provideHttpClient()],
  bootstrap: [LayoutQldtComponent], // Bootstrap layout component
})
export class QldtModule {}