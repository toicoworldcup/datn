// admin/admin-routing.module.ts
import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { StudentListComponent } from "./components/dkhp/student-list.component";
import { TeacherListComponent } from "./components/ctdt/teacher-list.component";
import { CourseListComponent } from "./components/course-list/course-list.component";
import { ClazzListComponent } from "./components/danh-sách-lớp-học/clazz-list.component";
import { SemesterListComponent } from "./components/tkb/semester-list.component";
import { CreateClazzComponent } from "./components/dkl/create-clazz.component";
import { AuthGuard } from "../core/guards/auth.guard"; // Import AuthGuard
import { LayoutStuComponent } from "./layout-stu/layout-stu.component";
import { InfoComponent } from "./components/info/info.component";
import { DiemComponent } from "./components/điểm/diem.component";
import { TaikhoanComponent } from "./components/taikhoan/taikhoan.component";

const routes: Routes = [
  {
    path: "",
    component: LayoutStuComponent,
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
        path: "info",
        component: InfoComponent,
        canActivate: [AuthGuard],
      },
      {
        path: "create-clazz",
        component: CreateClazzComponent,
        canActivate: [AuthGuard],
      },
      {
        path: "diem",
        component: DiemComponent,
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
export class StudentRoutingModule {}
