import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService, RegisterRequest, Role, UserResponse } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class Register implements OnInit {
  loading = false;
  roles: Role[] = [ 'COMPANY', 'STUDENT'];

  form!: FormGroup<{
    username: FormControl<string>;
    email: FormControl<string>;
    password: FormControl<string>;
    confirmPassword: FormControl<string>;
    role: FormControl<Role>;
    firstName: FormControl<string>;
    lastName: FormControl<string>;
    street: FormControl<string>;
    number: FormControl<string>;
    postalCode: FormControl<string>;
    phoneNumber: FormControl<string>;
    companyName: FormControl<string | null>;
  }>;

  constructor(
    private fb: NonNullableFormBuilder,
    private auth: AuthService,
    private snack: MatSnackBar,
    private router :Router
  ) {}

  /** Valide que control.value === matchTo.value */
  private matchControl(matchTo: AbstractControl<string>): ValidatorFn {
    return (control: AbstractControl<string>) => {
      const same = control.value === matchTo.value;
      return same ? null : { passwordMismatch: true };
    };
  }

  ngOnInit(): void {
    // On crée d'abord les 2 contrôles pour les relier entre eux
    const password = this.fb.control('', [Validators.required]);
    const confirmPassword = this.fb.control('', [Validators.required]);

    // Ajoute le validateur "doit égaler password"
    confirmPassword.addValidators(this.matchControl(password));
    // Revalide confirmPassword à chaque changement de password
    password.valueChanges.subscribe(() => confirmPassword.updateValueAndValidity({ onlySelf: true }));

    this.form = this.fb.group({
      username: this.fb.control('', [Validators.required]),
      email: this.fb.control('', [Validators.required, Validators.email]),
      password,
      confirmPassword,
      role: this.fb.control<Role>('STUDENT', [Validators.required]),
      firstName: this.fb.control('', [Validators.required]),
      lastName: this.fb.control('', [Validators.required]),
      street: this.fb.control('', [Validators.required]),
      number: this.fb.control('', [Validators.required]),
      postalCode: this.fb.control('', [Validators.required]),
      phoneNumber: this.fb.control('', [Validators.required]),
      companyName: this.fb.control<string | null>(null)
    });

    // companyName requis seulement si role = COMPANY
    this.form.controls.role.valueChanges.subscribe(role => {
      const ctrl = this.form.controls.companyName;
      if (role === 'COMPANY') {
        ctrl.setValidators([Validators.required]);
      } else {
        ctrl.clearValidators();
        ctrl.setValue(null);
      }
      ctrl.updateValueAndValidity({ emitEvent: false });
    });

    document.title = 'Helha Job App — Create account';
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched(); // ← affiche immédiatement les erreurs
      return;
    }
    this.loading = true;

    const raw = this.form.getRawValue();
    const address = `${raw.street.trim()} ${raw.number.trim()}, ${raw.postalCode.trim()}`;

    const payload: RegisterRequest = {
      username: raw.username,
      email: raw.email,
      password: raw.password,
      role: raw.role,
      firstName: raw.firstName,
      lastName: raw.lastName,
      address,
      phoneNumber: raw.phoneNumber,
      ...(raw.role === 'COMPANY' && raw.companyName ? { companyName: raw.companyName } : {})
    };

    this.auth.register(payload)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (_res: UserResponse) => {
          this.snack.open('Account created successfully. You can sign in now.', 'Close', { duration: 3500 });
          this.router.navigate(['/login']);
        },
        error: (err) => {
          const msg = err?.error?.message ?? 'Registration failed.';
          this.snack.open(msg, 'Close', { duration: 5000 });
        }
      });
  }
}
