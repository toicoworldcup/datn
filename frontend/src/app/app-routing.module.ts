import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminModule } from './admin/admin.module'; // Import AdminModule
import { LoginComponent } from './login/login.component';

const routes: Routes = [
  { path: 'admin', loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule) } ,
  { path: 'login', component: LoginComponent },  // Đường dẫn tới LoginComponent
  { path: '', redirectTo: '/login', pathMatch: 'full' }  // Đảm bảo redirect đến login khi truy cập vào trang gốc

  // Lazy load Admin module
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
