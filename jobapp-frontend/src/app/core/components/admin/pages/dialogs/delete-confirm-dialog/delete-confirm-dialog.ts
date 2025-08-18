import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DeleteConfirmDialogData } from '../../../../../../models/interfaces';

@Component({
  selector: 'app-delete-confirm-dialog',
  standalone: false,
  templateUrl: './delete-confirm-dialog.html',
  styleUrls: ['./delete-confirm-dialog.css']
})
export class DeleteConfirmDialog {
  loading = false;

  constructor(
    public dialogRef: MatDialogRef<DeleteConfirmDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DeleteConfirmDialogData,
  ) {}

  onCancel(): void {
    this.dialogRef.close(false);
  }

   onConfirm(): void {
    this.dialogRef.close(true); 
  }
}
