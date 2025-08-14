import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface DeleteConfirmDialogData {
  userName: string;
}

@Component({
  selector: 'app-delete-confirm-dialog',
  standalone: false,
  templateUrl: './delete-confirm-dialog.html',
  styleUrls: ['./delete-confirm-dialog.scss']
})
export class DeleteConfirmDialog {
constructor(
    public dialogRef: MatDialogRef<DeleteConfirmDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DeleteConfirmDialogData
  ) {}

  onCancel(): void {
    this.dialogRef.close(false);
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }
}
