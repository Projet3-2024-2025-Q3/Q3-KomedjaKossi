import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface ApplyDialogPayload {
  cv: File;
  motivation: File;
}

@Component({
  selector: 'app-apply-dialog',
  standalone: false,
  templateUrl: './apply-dialog.html',
  styleUrls: ['./apply-dialog.css']
})
export class ApplyDialog {
  cv?: File | null;
  motivation?: File | null;

  cvName = '';
  motivationName = '';

  errors: { cv?: string; motivation?: string } = {};

  constructor(
    private ref: MatDialogRef<ApplyDialog, ApplyDialogPayload | undefined>,
    @Inject(MAT_DIALOG_DATA) public data: { offerTitle: string }
  ) {}

  private validType(file: File) {
    const ok = ['pdf','doc','docx'];
    const ext = file.name.split('.').pop()?.toLowerCase() || '';
    return ok.includes(ext);
  }
  private validSize(file: File) {
    return file.size <= 5 * 1024 * 1024; // 5 MB
  }

  pick(kind: 'cv' | 'motivation', file: File | null) {
    this.errors[kind] = undefined;
    if (!file) {
      if (kind === 'cv') { this.cv = null; this.cvName = ''; }
      else { this.motivation = null; this.motivationName = ''; }
      return;
    }
    if (!this.validType(file)) {
      this.errors[kind] = 'Type de fichier non supporté (PDF/DOC/DOCX)';
      return;
    }
    if (!this.validSize(file)) {
      this.errors[kind] = 'Fichier trop volumineux (max 5 MB)';
      return;
    }
    if (kind === 'cv') { this.cv = file; this.cvName = file.name; }
    else { this.motivation = file; this.motivationName = file.name; }
  }

  cancel() { this.ref.close(undefined); }

  submit() {
    this.errors = {};
    if (!this.cv) this.errors.cv = 'CV requis';
    if (!this.motivation) this.errors.motivation = 'Lettre de motivation requise';
    if (this.errors.cv || this.errors.motivation) return;

    this.ref.close({ cv: this.cv!, motivation: this.motivation! });
  }
}
