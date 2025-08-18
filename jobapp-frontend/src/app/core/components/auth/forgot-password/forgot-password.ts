import { Component, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  Validators
} from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: false,
  templateUrl: './forgot-password.html',
  styleUrls: ['./forgot-password.css']
})
export class ForgotPassword implements OnInit {
  loading = false;

  form!: FormGroup<{
    email: FormControl<string>;
  }>;

  constructor(
    private fb: NonNullableFormBuilder,
    private auth: AuthService,
    private snack: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      email: this.fb.control('', [Validators.required, Validators.email])
    });

    document.title = 'Helha Job App — Forgot password';
  }

  submit(): void {
    if (this.form.invalid || this.loading) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const { email } = this.form.getRawValue();

    this.auth
      .forgotPassword(email) 
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (msg: string) => {
          this.snack.open(
            msg || 'A new password has been sent to your email.',
            'Close',
            { duration: 15000 }
          );

        },
        error: (err) => {
          const message =
            err?.error?.message ??
            'Unable to reset password. Please try again.';
          this.snack.open(message, 'Close', { duration: 10000 });
        }
      });
  }
}
