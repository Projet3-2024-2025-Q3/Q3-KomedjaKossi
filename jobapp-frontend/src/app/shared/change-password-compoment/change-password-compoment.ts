import { Component } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../core/services/auth.service';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-change-password-compoment',
  standalone: false , 
  templateUrl: './change-password-compoment.html',
  styleUrl: './change-password-compoment.css'
})
export class ChangePasswordCompoment {
passwordForm: FormGroup;
hide = { old: true, new: true, confirm: true };

  constructor(
    public dialogRef: MatDialogRef<ChangePasswordCompoment>,
    private fb: FormBuilder,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.passwordForm = this.fb.group({
      oldPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordsMatchValidator });
}
     passwordsMatchValidator(group: FormGroup) {
    const newPass = group.get('newPassword')?.value;
    const confirmPass = group.get('confirmPassword')?.value;
    return newPass === confirmPass ? null : { passwordsMismatch: true };

  
  }

  onSubmit() {
    if (this.passwordForm.invalid) return;
    
    const { oldPassword, newPassword } = this.passwordForm.value;
    
    this.authService.changePassword(oldPassword, newPassword).subscribe({
      next: (response) => {
        // La réponse est maintenant du texte, pas du JSON
        console.log('Server response:', response);
        this.passwordForm.reset();
        this.snackBar.open('Password changed successfully', 'Close', { 
          duration: 3000 
        });
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Error:', error);
        // Gérer l'erreur qui peut aussi être du texte
        const errorMessage = error.error || 'Failed to change password';
        this.snackBar.open(errorMessage, 'Close', { 
          duration: 3000 
        });
      }
    });
  }
  onCancel(): void {
    this.dialogRef.close();
  }
}
