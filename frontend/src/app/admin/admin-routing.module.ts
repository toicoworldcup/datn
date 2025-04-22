import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LayoutAdminComponent } from './layout-admin/layout-admin.component';
import { StudentListComponent } from './components/student-list/student-list.component';
import { TeacherListComponent } from './components/teacher-list/teacher-list.component';
import { CourseListComponent } from './components/course-list/course-list.component';
import { ClazzListComponent } from './components/clazz-list/clazz-list.component';
import { SemesterListComponent } from './components/semester-list/semester-list.component';
import { AssignTeacherComponent } from './components/assign-teacher/assign-teacher.component';
import { CreateClazzComponent } from './components/create-clazz/create-clazz.component';

const routes: Routes = [
  {
    path: '', component: LayoutAdminComponent, children: [
      { path: 'student', component: StudentListComponent },
      { path: 'teacher', component: TeacherListComponent },
      { path: 'course', component: CourseListComponent },
      { path: 'clazz', component: ClazzListComponent },
      { path: 'semester', component: SemesterListComponent },
      { path: 'assign-teacher', component: AssignTeacherComponent },
      { path: 'create-clazz', component: CreateClazzComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule {}
