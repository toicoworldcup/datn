// admin/admin-routing.module.ts
import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { StudentListComponent } from "./components/student-list/student-list.component";
import { TeacherListComponent } from "./components/teacher-list/teacher-list.component";
import { CourseListComponent } from "./components/course-list/course-list.component";
import { ClazzListComponent } from "./components/clazz-list/clazz-list.component";
import { SemesterListComponent } from "./components/semester-list/semester-list.component";
import { AssignTeacherComponent } from "./components/assign-teacher/assign-teacher.component";
import { CreateClazzComponent } from "./components/create-clazz/create-clazz.component";
import { AuthGuard } from "../core/guards/auth.guard"; // Import AuthGuard
import { LayoutQldtComponent } from "./layout-qldt/layout-qldt.component";
import { DangkilopComponent } from "./components/dangkilop/dangkilop.component";
import { CthComponent } from "./components/cth/cth.component";

const routes: Routes = [
  {
    path: "",
    component: LayoutQldtComponent,
    children: [
      {
        path: "student",
        component: StudentListComponent,
        canActivate: [AuthGuard],
      },
      {
        path: "teacher",
        component: TeacherListComponent,
        canActivate: [AuthGuard],
      },
      {
        path: "course",
        component: CourseListComponent,
        canActivate: [AuthGuard],
      },
      {
        path: "clazz",
        component: ClazzListComponent,
        canActivate: [AuthGuard],
      },
      {
        path: "semester",
        component: SemesterListComponent,
        canActivate: [AuthGuard],
      },
      {
        path: "assign-teacher",
        component: AssignTeacherComponent,
        canActivate: [AuthGuard],
      },
      {
        path: "create-clazz",
        component: CreateClazzComponent,
        canActivate: [AuthGuard],
      },
      {
        path: "dangkilop",
        component: DangkilopComponent,
        canActivate: [AuthGuard],
      },
      {
        path: "cth",
        component: CthComponent,
        canActivate: [AuthGuard],
      },
      {
        path: "teacher",
        component: TeacherListComponent,
        canActivate: [AuthGuard],
      },

    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class QldtRoutingModule {}
