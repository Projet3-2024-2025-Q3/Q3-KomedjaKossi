import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { OfferRequest, OfferDialogData } from '../../../../../../models/interfaces';


@Component({
  selector: 'app-offer-dialog',
  standalone: false,
  templateUrl: './offer-dialog.html',
  styleUrls: ['./offer-dialog.css'] 
})
export class OfferDialog implements OnInit {
  form!: FormGroup;

  isEdit = false;
  titleText = 'Create Offer';
  actionText = 'Create';

  logoPreview?: string;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<OfferDialog, OfferRequest | undefined>,
    @Inject(MAT_DIALOG_DATA) public data: OfferDialogData
  ) {}

  ngOnInit(): void {
    this.isEdit = !!this.data?.isEdit;
    this.titleText = this.isEdit ? 'Edit Offer' : 'Create Offer';
    this.actionText = this.isEdit ? 'Save' : 'Create';

    const offer = this.data?.offer;

    this.form = this.fb.group({
      title: [offer?.title ?? '', [Validators.required, Validators.maxLength(150)]],
      description: [offer?.description ?? '', [Validators.required, Validators.maxLength(5000)]],
      logoUrl: [offer?.logoUrl ?? '', [Validators.maxLength(1024), this.optionalUrlValidator]],
      websiteUrl: [offer?.websiteUrl ?? '', [Validators.maxLength(1024), this.optionalUrlValidator]],
    });

    this.logoPreview = offer?.logoUrl || undefined;
  }

 
  optionalUrlValidator(control: AbstractControl): ValidationErrors | null {
    const val = (control.value || '').trim();
    if (!val) return null;
    const urlRegex = /^(https?:\/\/)[^\s]+$/i;
    return urlRegex.test(val) ? null : { url: true };
  }

  onLogoBlur(): void {
    const v = (this.form.get('logoUrl')?.value || '').trim();
    this.logoPreview = v || undefined;
  }

  cancel(): void {
    this.dialogRef.close(undefined);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const raw = this.form.value;
    const payload: OfferRequest = {
      title: (raw.title || '').trim(),
      description: (raw.description || '').trim(),
      logoUrl: raw.logoUrl ? String(raw.logoUrl).trim() : null,
      websiteUrl: raw.websiteUrl ? String(raw.websiteUrl).trim() : null,
    };
    this.dialogRef.close(payload);
  }

  get f() { return this.form.controls; }
}
