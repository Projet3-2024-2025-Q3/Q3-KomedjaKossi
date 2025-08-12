import { Component, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  Validators
} from '@angular/forms';

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
    email: FormControl<string>;
    password: FormControl<string>;
  }>;

  constructor(private fb: NonNullableFormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      email: this.fb.control('', [Validators.required, Validators.email]),
      password: this.fb.control('', [Validators.required])
    });

    document.title = 'Helha Job App — Sign in';
  }

  onSubmit(): void {
    if (this.form.invalid || this.loading) return;
    this.loading = true;
    setTimeout(() => (this.loading = false), 1000);
  }
}
