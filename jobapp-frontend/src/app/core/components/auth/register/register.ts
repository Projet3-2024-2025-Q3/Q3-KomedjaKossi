import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class Register implements OnInit {
  form!: FormGroup;
  loading = false;
  roles = ['STUDENT', 'COMPANY'];

  // ⬇️ Toggle simple (clic)
  hidePassword = true;
  hideConfirm = true;

  constructor(private fb: FormBuilder, private snackBar: MatSnackBar,private router: Router , private auth: AuthService,) {}

  ngOnInit(): void {
    const NAME_PATTERN = /^[\p{L}\s'’.\-]+$/u;   
    const STREET_PATTERN = NAME_PATTERN;
    const DIGITS_ONLY = /^\d+$/;
    const POSTAL_PATTERN = /^\d{4}$/;
    const USERNAME_PATTERN = /^[a-z0-9._-]{3,}$/;

    this.form = this.fb.group(
      {
        username: ['', [Validators.required, Validators.pattern(USERNAME_PATTERN)]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', [Validators.required]],
        firstName: ['', [Validators.required, Validators.pattern(NAME_PATTERN)]],
        lastName: ['', [Validators.required, Validators.pattern(NAME_PATTERN)]],
        street: ['', [Validators.required, Validators.pattern(STREET_PATTERN)]],
        number: ['', [Validators.required, Validators.pattern(DIGITS_ONLY)]],
        postalCode: ['', [Validators.required, Validators.pattern(POSTAL_PATTERN), this.postalRangeValidator]],
        phoneNumber: ['', [Validators.required, this.belgianPhoneValidator]],
        role: ['', [Validators.required]],
        companyName: [''] // requis dynamiquement si role = COMPANY
      },
      { validators: this.passwordsMatchValidator }
    );

    // username en lowercase en live
    this.f.username.valueChanges.subscribe((v: any) => {
      const lv = (v || '').toString().trim().toLowerCase();
      if (v !== lv) this.f.username.setValue(lv, { emitEvent: false });
    });

    // email en lowercase en live
    this.f.email.valueChanges.subscribe((v: any) => {
      const lv = (v || '').toString().trim().toLowerCase();
      if (v !== lv) this.f.email.setValue(lv, { emitEvent: false });
    });

    // companyName requis si role = COMPANY
    this.f.role.valueChanges.subscribe((role: string) => {
      if (role === 'COMPANY') {
        this.f.companyName.addValidators([Validators.required, Validators.pattern(NAME_PATTERN)]);
      } else {
        this.f.companyName.clearValidators();
        this.f.companyName.setValue('');
      }
      this.f.companyName.updateValueAndValidity();
    });
  }

  get f() { return this.form.controls as any; }

  // ===== Normalisations UI =====
  normalizeUsername(): void {
    const v = (this.f.username.value || '').toString().trim().toLowerCase();
    this.f.username.setValue(v);
  }

  lowercaseControl(name: 'email'): void {
    const v = (this.f[name].value || '').toString().trim().toLowerCase();
    this.f[name].setValue(v);
  }

  stripExtraSpaces(name: 'street'): void {
    const v = (this.f[name].value || '').toString().replace(/\s+/g, ' ').trim();
    this.f[name].setValue(v);
  }

  capFirst(name: 'firstName' | 'lastName' | 'companyName'): void {
    const raw = (this.f[name].value || '').toString().replace(/\s+/g, ' ').trim();
    if (!raw) { this.f[name].setValue(''); return; }
    const v = raw.charAt(0).toUpperCase() + raw.slice(1).toLowerCase();
    this.f[name].setValue(v);
  }

  digitsOnly(name: 'number' | 'postalCode'): void {
    const v = (this.f[name].value || '').toString().replace(/\D+/g, '');
    this.f[name].setValue(v);
  }

  normalizePhone(): void {
    const v = (this.f.phoneNumber.value || '').toString().trim();
    const norm = v.replace(/\s+/g, ' ').replace(/[()\-\.]/g, '').trim();
    this.f.phoneNumber.setValue(norm);
    this.f.phoneNumber.updateValueAndValidity();
  }

  // ===== Validators =====
  private passwordsMatchValidator(group: AbstractControl): ValidationErrors | null {
    const p = group.get('password')?.value;
    const c = group.get('confirmPassword')?.value;
    return p && c && p !== c ? { passwordMismatch: true } : null;
  }

  private postalRangeValidator = (control: AbstractControl): ValidationErrors | null => {
    const v = (control.value || '').toString();
    if (!/^\d{4}$/.test(v)) return null; // pattern s'en charge
    const n = parseInt(v, 10);
    return n >= 1000 && n <= 9999 ? null : { range: true };
  };

  /** Numéro belge : +32… ou 0…
   *  après retrait du préfixe :
   *   - 9 chiffres et commence par 4 => mobile OK
   *   - 8 chiffres => fixe OK
   */
  private belgianPhoneValidator = (control: AbstractControl): ValidationErrors | null => {
    let raw = (control.value || '').toString();
    if (!raw) return null;

    const cleaned = raw.replace(/[^\d+]/g, '');
    let digits = cleaned.startsWith('+') ? cleaned.slice(1) : cleaned;

    if (digits.startsWith('32'))      digits = digits.slice(2);
    else if (digits.startsWith('0'))  digits = digits.slice(1);
    else return { bePhone: true };

    if (!/^\d+$/.test(digits)) return { bePhone: true };
    if (digits.length === 9 && digits.startsWith('4')) return null; // mobile
    if (digits.length === 8) return null;                            // fixe
    return { bePhone: true };
  };

  // ===== Submit =====
  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    // normalisation finale
    this.normalizeUsername();
    this.lowercaseControl('email');
    this.stripExtraSpaces('street');
    this.capFirst('firstName');
    this.capFirst('lastName');
    if (this.f.role.value === 'COMPANY') this.capFirst('companyName');
    this.normalizePhone();

    const v = this.form.value;
    const address = `${v.street} ${v.number}, ${v.postalCode}`;

    const payload = {
      username: v.username,
      email: v.email,
      password: v.password,
      role: v.role,
      firstName: v.firstName,
      lastName: v.lastName,
      address,
      companyName: v.role === 'COMPANY' ? (v.companyName || null) : null,
      phoneNumber: v.phoneNumber
    };

    this.loading = true;
    this.loading = true;

  this.auth.register(payload)
    .pipe(finalize(() => this.loading = false))
    .subscribe({
      next: () => {
        this.snackBar.open('Account created', 'Close', { duration: 2500 });
        this.router.navigate(['/login']);
      },
      error: (err) => {
        const msg = err?.message || 'Registration failed';
        this.snackBar.open(msg, 'Close', { duration: 3500 });
      }
    });

  }
}
