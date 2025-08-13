import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser'; // ← BrowserModule (pas CommonModule)
import { AppRoutingModule } from './app-routing-module';
import { AppComponent } from './app.component';
import { Login } from './core/components/auth/login/login';
import { HttpClientModule } from '@angular/common/http';
import { Register } from './core/components/auth/register/register';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { ReactiveFormsModule } from '@angular/forms';



@NgModule({
  declarations: [
    AppComponent, 
    Login, Register
  ],
  imports: [
    BrowserModule,     
    AppRoutingModule,
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSelectModule     
  ],
  bootstrap: [AppComponent] 
})
export class AppModule { }
