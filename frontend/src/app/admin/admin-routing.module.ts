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
import { LayoutAdminComponent } from "./layout-admin/layout-admin.component";
import { ScheduleListComponent } from "./components/schedule-list/schedule-list.component";
import { AuthGuard } from "../core/guards/auth.guard";

const routes: Routes = [
  {
    path: "",
    component: LayoutAdminComponent,
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
        path: "schedule",
        component: ScheduleListComponent,
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
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}
