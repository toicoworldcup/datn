import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { TeacherRoutingModule } from "./teacher-routing.module";
import { provideHttpClient } from "@angular/common/http";
import { RouterModule } from "@angular/router";

// Standalone Component imports...
import { StudentListComponent } from "./components/diemdanh/student-list.component";
import { TeacherListComponent } from "./components/teacher-list/teacher-list.component";
import { CourseListComponent } from "./components/hocphan/course-list.component";
import { ClazzListComponent } from "./components/lopday/clazz-list.component";
import { SemesterListComponent } from "./components/nhapdiem/semester-list.component";
import { CreateClazzComponent } from "./components/thoikhoabieu/create-clazz.component";
import { HeaderComponent } from "../header/header.component"; // Đảm bảo đường dẫn này đúng
import { LayoutTeaComponent } from "./layout-tea/layout-tea.component";
import { TeacherComponent } from "./teacher.component";

@NgModule({
  declarations: [
    LayoutTeaComponent,
    // Khai báo QldtComponent
    // HeaderComponent // KHÔNG cần khai báo lại nếu là standalone
  ],
  imports: [
    CommonModule,
    TeacherRoutingModule,
    RouterModule, // ✅ Import RouterModule
    StudentListComponent, // Import trực tiếp các standalone component
    TeacherListComponent,
    CourseListComponent,
    ClazzListComponent,
    SemesterListComponent,
    CreateClazzComponent,
    HeaderComponent,
    TeacherComponent, // ✅ Import HeaderComponent
  ],
  providers: [provideHttpClient()],
  bootstrap: [LayoutTeaComponent], // Bootstrap layout component
})
export class TeaModule {}
