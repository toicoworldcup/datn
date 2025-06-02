import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './admin/home/home.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent }, // Example: Assuming you have a HomeComponent
  { path: 'admin', loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule) } ,
  { path: 'login', component: LoginComponent },  // Đường dẫn tới LoginComponent
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'qldt', loadChildren: () => import('./qldt/qldt.module').then(m => m.QldtModule) },
  { path: 'teacher', loadChildren: () => import('./teacher/teacher.module').then(m => m.TeaModule) },
  { path: 'student', loadChildren: () => import('./student/student.module').then(m => m.StuModule) }  // Đảm bảo redirect đến login khi truy cập vào trang gốc

  // Lazy load Admin module
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
