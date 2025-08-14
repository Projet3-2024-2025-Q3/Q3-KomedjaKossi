import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

export type Role = 'company' | 'student' | 'admin';

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  createdAt: Date;
  role: Role;
  password?: string;
}

@Component({
  selector: 'app-user-dialog',
  standalone:false,
  templateUrl: './user-dialog.html',
  styleUrls: ['./user-dialog.scss']
})
export class UserDialog implements OnInit {
  userForm: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<UserDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { user: User | null, isEdit: boolean },
    private fb: FormBuilder
  ) {
    this.userForm = this.createForm();
  }

  ngOnInit(): void {
    if (this.data.user && this.data.isEdit) {
      this.userForm.patchValue({
        firstName: this.data.user.firstName,
        lastName: this.data.user.lastName,
        email: this.data.user.email,
        role: this.data.user.role
      });
    }
  }

  createForm(): FormGroup {
    if (this.data.isEdit) {
      return this.fb.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', Validators.minLength(8)],
        role: ['', Validators.required]
      });
    } else {
      return this.fb.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        role: ['', Validators.required]
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.userForm.valid) {
      const formValue = { ...this.userForm.value };
      if (this.data.isEdit && !formValue.password) {
        delete formValue.password;
      }
      this.dialogRef.close(formValue);
    }
  }
}
