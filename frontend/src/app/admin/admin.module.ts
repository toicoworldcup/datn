import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { AdminRoutingModule } from "./admin-routing.module";
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
import { AdminComponent } from "./admin.component";
import { HeaderComponent } from "../header/header.component";
import { LayoutAdminComponent } from "./layout-admin/layout-admin.component";
import { ScheduleListComponent } from "./components/schedule-list/schedule-list.component";

@NgModule({
  declarations: [
    LayoutAdminComponent,
    // ✅ Đảm bảo HeaderComponent được khai báo ở đây

    // Các component standalone KHÔNG được khai báo ở đây
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    ScheduleListComponent,
    RouterModule,
    StudentListComponent, // Import trực tiếp các standalone component
    TeacherListComponent,
    CourseListComponent,
    ClazzListComponent,
    SemesterListComponent,
    AssignTeacherComponent,
    CreateClazzComponent,
    AdminComponent,
    HeaderComponent, // LayoutAdminComponent có thể không phải là standalone
  ],
  providers: [provideHttpClient()],
  bootstrap: [LayoutAdminComponent], // Vẫn bootstrap module nếu cần, hoặc có thể bootstrap một standalone component ở AppModule
})
export class AdminModule {}
