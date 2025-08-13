import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Login } from './core/components/auth/login/login';
import { Register } from './core/components/auth/register/register';
import { ForgotPassword } from './core/components/auth/forgot-password/forgot-password';
import { AdminDashboard } from './core/components/admin/pages/admin-dashboard/admin-dashboard';
import { AuthGuard } from './core/guards/auth-guard';
import { RoleGuard } from './core/guards/role-guard';

const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register }, 
  { path: 'auth/forgot-password', component: ForgotPassword },
  {
    path: 'admin',
    component: AdminDashboard,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] }
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
