import { Component, OnInit } from '@angular/core';
import {
  FormControl, FormGroup, NonNullableFormBuilder, Validators
} from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class Login implements OnInit {
  hide = true;
  loading = false;

  form!: FormGroup<{
    username: FormControl<string>;
    password: FormControl<string>;
  }>;

  constructor(
    private fb: NonNullableFormBuilder,
    private auth: AuthService,
    private snack: MatSnackBar,
     private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      username: this.fb.control('', [Validators.required]),
      password: this.fb.control('', [Validators.required])
    });
    document.title = 'Helha Job App — Sign in';
  }

  onSubmit(): void {
    if (this.form.invalid || this.loading) return;
    this.loading = true;

    const { username, password } = this.form.getRawValue();

    this.auth.login({ username, password })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (res) => {
          this.auth.setToken(res.token);
          this.snack.open('Signed in successfully.', 'Close', { duration: 3000 });
          const role = this.auth.getRole();
          if (role === 'ADMIN') {
            this.router.navigate(['/admin']);
          } else if (role === 'COMPANY') {
            this.router.navigate(['/company']); 
          }else {
            this.router.navigate(['/dashboard']);
          }
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Sign-in failed. Check your credentials.';
          this.snack.open(msg, 'Close', { duration: 5000 });
        }
      });
  }
}
