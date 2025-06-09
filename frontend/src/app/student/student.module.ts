import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { provideHttpClient } from "@angular/common/http";
import { RouterModule } from "@angular/router";

// Standalone Component imports...
import { StudentListComponent } from "./components/dkhp/student-list.component";
import { TeacherListComponent } from "./components/ctdt/teacher-list.component";
import { CourseListComponent } from "./components/course-list/course-list.component";
import { ClazzListComponent } from "./components/danh-sách-lớp-học/clazz-list.component";
import { SemesterListComponent } from "./components/tkb/semester-list.component";
import { InfoComponent } from "./components/info/info.component";
import { CreateClazzComponent } from "./components/dkl/create-clazz.component";
import { HeaderComponent } from "../header/header.component"; // Đảm bảo đường dẫn này đúng
import { LayoutStuComponent } from "./layout-stu/layout-stu.component";
import { StudentRoutingModule } from "./student-routing.module";
import { ReactiveFormsModule } from "@angular/forms"; // Import ReactiveFormsModule
import { DiemComponent } from "./components/điểm/diem.component";

@NgModule({
  declarations: [
    LayoutStuComponent,
    // Khai báo QldtComponent
    // HeaderComponent // KHÔNG cần khai báo lại nếu là standalone
  ],
  imports: [
    CommonModule,
    StudentRoutingModule,
    RouterModule, // ✅ Import RouterModule
    TeacherListComponent,
    CourseListComponent,
    ClazzListComponent,
    SemesterListComponent,
    DiemComponent,
    InfoComponent,
    CreateClazzComponent,
    HeaderComponent,
    ReactiveFormsModule, // Thêm ReactiveFormsModule vào đây
  ],
  providers: [provideHttpClient()],
  bootstrap: [LayoutStuComponent], // Bootstrap layout component
})
export class StuModule {}
