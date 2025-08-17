import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApplyDialogPayload, OfferResponse } from '../../../../../models/interfaces';
import { ChangePasswordCompoment } from '../../../../../shared/change-password-compoment/change-password-compoment';
import { AuthService } from '../../../../services/auth.service';
import { StudentOfferService } from '../../../../services/studentservice';
import { ApplyDialog } from '../dialogs/apply-dialog/apply-dialog';

@Component({
  selector: 'app-student-dashboard',
  standalone: false,
  templateUrl: './student-dashboard.html',
  styleUrls: ['./student-dashboard.css']
})
export class StudentDashboard implements OnInit {
  profileName?: string;
  profileEmail?: string;
  profileRole?: string;

  settingsOpen = false;

  offers: OfferResponse[] = [];
  filteredOffers: OfferResponse[] = [];

  searchText = '';
  companies: string[] = [];
  selectedCompanies: string[] = [];
  statusFilter: 'all' | 'applied' | 'not' = 'all';

  isFiltered = false;
  loading = false;

  constructor(
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private authService: AuthService,
    private api: StudentOfferService
  ) {}

  ngOnInit(): void {
    const payload = this.authService.decodePayload?.();
    if (payload) {
      this.profileName =
        `${payload.firstName ?? ''} ${payload.lastName ?? ''}`.trim() ||
        payload.username;
      this.profileEmail = payload.email;
      this.profileRole = payload.role ?? payload.authorities?.[0] ?? 'STUDENT';
    }
    this.loadOffers();
  }

  private loadOffers(): void {
    this.loading = true;
    this.api.getAll().subscribe({
      next: (offers) => {
        this.offers = [...offers].sort(
          (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        this.refreshCompanies();
        this.applyFilters();
        this.loading = false;
      },
      error: () => {
        this.snackBar.open('Error loading offers', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  private refreshCompanies(): void {
    const set = new Set<string>();
    for (const o of this.offers) {
      if (o.companyName) set.add(o.companyName);
    }
    this.companies = Array.from(set).sort((a, b) => a.localeCompare(b));
  }

  applyFilters(): void {
    const q = (this.searchText || '').trim().toLowerCase();
    let list = [...this.offers];

    if (this.statusFilter === 'applied')      list = list.filter(o => !!o.applied);
    else if (this.statusFilter === 'not')     list = list.filter(o => !o.applied);

    if (this.selectedCompanies.length) {
      const sel = new Set(this.selectedCompanies.map(s => s.toLowerCase()));
      list = list.filter(o => o.companyName && sel.has(o.companyName.toLowerCase()));
    }

    if (q) {
      list = list.filter(o =>
        (o.title || '').toLowerCase().includes(q) ||
        (o.description || '').toLowerCase().includes(q)
      );
    }

    this.filteredOffers = list;
    this.isFiltered = !!q || this.statusFilter !== 'all' || this.selectedCompanies.length > 0;
  }

  resetFilters(): void {
    this.searchText = '';
    this.selectedCompanies = [];
    this.statusFilter = 'all';
    this.applyFilters();
  }

  openApplyDialog(offer: OfferResponse): void {
    const ref = this.dialog.open<ApplyDialog, { offerTitle: string }, ApplyDialogPayload | undefined>(
      ApplyDialog,
      { width: '520px', data: { offerTitle: offer.title } }
    );

    ref.afterClosed().subscribe((payload) => {
      if (!payload) return;

      this.api.apply(offer.id, { cv: payload.cv, motivation: payload.motivation }).subscribe({
        next: () => {
          offer.applied = true;
          this.applyFilters();
          this.snackBar.open('Application sent 🎉', 'Close', { duration: 2500 });
        },
        error: (e) => {
          this.snackBar.open(e?.message || 'Failed to send application', 'Close', { duration: 3000 });
        }
      });
    });
  }

  navigate(route: string): void { this.router.navigate([route]); }
  
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
  toggleSettings(): void { this.settingsOpen = !this.settingsOpen; }
  openChangePasswordDialog(): void { this.dialog.open(ChangePasswordCompoment, { width: '400px' }); }
}
