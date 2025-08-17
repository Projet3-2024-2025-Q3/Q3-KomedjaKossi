import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing-module'; 
import { AppComponent } from './app.component';
import { Login } from './core/components/auth/login/login';
import { Register } from './core/components/auth/register/register';
import { ForgotPassword } from './core/components/auth/forgot-password/forgot-password';
import { AdminDashboard } from './core/components/admin/pages/admin-dashboard/admin-dashboard';
import { DeleteConfirmDialog } from './core/components/admin/pages/dialogs/delete-confirm-dialog/delete-confirm-dialog';
import { AuthInterceptor } from './core/interceptors/interceptor-interceptor';
import { UserDialog } from './core/components/admin/pages/dialogs/user-dialog/user-dialog';
import { ChangePasswordCompoment } from './shared/change-password-compoment/change-password-compoment';
import { CompanyDasboard } from './core/components/admin/pages/company-dasboard/company-dasboard';
import { OfferDialog } from './core/components/admin/pages/dialogs/offer-dialog/offer-dialog';
import { StudentDashboard } from './core/components/admin/pages/student-dashboard/student-dashboard';
import { ApplyDialog } from './core/components/admin/pages/dialogs/apply-dialog/apply-dialog';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

@NgModule({
  declarations: [
    AppComponent,
    Login,
    Register,
    ForgotPassword,
    AdminDashboard,
    DeleteConfirmDialog,
    UserDialog,
    ChangePasswordCompoment,
    CompanyDasboard,
    OfferDialog,
    StudentDashboard,
    ApplyDialog
   
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatSnackBarModule,
  ],
   providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
