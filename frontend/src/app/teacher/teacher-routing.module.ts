// admin/admin-routing.module.ts
import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { StudentListComponent } from "./components/diemdanh/student-list.component";
import { TeacherListComponent } from "./components/teacher-list/teacher-list.component";
import { CourseListComponent } from "./components/hocphan/course-list.component";
import { ClazzListComponent } from "./components/lopday/clazz-list.component";
import { SemesterListComponent } from "./components/nhapdiem/semester-list.component";
import { CreateClazzComponent } from "./components/thoikhoabieu/create-clazz.component";
import { AuthGuard } from "../core/guards/auth.guard"; // Import AuthGuard
import { LayoutTeaComponent } from "./layout-tea/layout-tea.component";
import { InfoComponent } from "./components/info/info.component";
import { TaikhoanComponent } from "./components/taikhoan/taikhoan.component";

const routes: Routes = [
  {
    path: "",
    component: LayoutTeaComponent,
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
        path: "info",
        component: InfoComponent,
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
        path: "create-clazz",
        component: CreateClazzComponent,
        canActivate: [AuthGuard],
      },
      {
        path: "taikhoan",
        component: TaikhoanComponent,
        canActivate: [AuthGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TeacherRoutingModule {}
