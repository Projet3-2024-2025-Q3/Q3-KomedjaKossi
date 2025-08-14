import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../../../services/auth.service';


export type Role = 'company' | 'student' | 'admin';

export interface User {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  role: Role;
  companyName?: string;
  address?: string;
  phoneNumber?: string;
}

@Component({
  selector: 'app-user-dialog',
  standalone: false,
  templateUrl: './user-dialog.html',
  styleUrls: ['./user-dialog.css']
})
export class UserDialog implements OnInit {
  userForm: FormGroup;
  currentAdminUsername?: string;

  constructor(
    public dialogRef: MatDialogRef<UserDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { user: User; isEdit: boolean },
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    const payload = this.authService.decodePayload();
    this.currentAdminUsername = payload?.username;
    this.userForm = this.createForm();
  }

  ngOnInit(): void {
    if (this.data.user && this.data.isEdit) {
      this.userForm.patchValue(this.data.user);
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      username: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      role: ['', Validators.required],
      companyName: [''],
      address: [''],
      phoneNumber: ['']
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.userForm.valid) {
      this.dialogRef.close(this.userForm.value);
    }
  }
}
