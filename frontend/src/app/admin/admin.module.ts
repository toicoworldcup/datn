import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminRoutingModule } from './admin-routing.module';
import { provideHttpClient } from '@angular/common/http'; // ✅ Dùng cái này
import { RouterModule } from '@angular/router'; // ✅ THÊM VÀO

// Component imports...
import { LayoutAdminComponent } from './layout-admin/layout-admin.component';
import { StudentListComponent } from './components/student-list/student-list.component';
import { TeacherListComponent } from './components/teacher-list/teacher-list.component';
import { CourseListComponent } from './components/course-list/course-list.component';
import { ClazzListComponent } from './components/clazz-list/clazz-list.component';
import { SemesterListComponent } from './components/semester-list/semester-list.component';
import { AssignTeacherComponent } from './components/assign-teacher/assign-teacher.component';
import { CreateClazzComponent } from './components/create-clazz/create-clazz.component';

@NgModule({
  declarations: [
    LayoutAdminComponent,
   
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    StudentListComponent,
    TeacherListComponent,
    CourseListComponent,
    ClazzListComponent,
    SemesterListComponent,
    AssignTeacherComponent,
    CreateClazzComponent,
    RouterModule
  ],
  providers: [
    provideHttpClient() // ✅ Dùng cái này thay HttpClientModule
  ],
  bootstrap: [LayoutAdminComponent]
})
export class AdminModule { }
