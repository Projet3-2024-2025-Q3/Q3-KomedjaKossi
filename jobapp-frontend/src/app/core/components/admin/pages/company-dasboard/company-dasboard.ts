import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../../../services/auth.service';
import { DeleteConfirmDialog } from '../dialogs/delete-confirm-dialog/delete-confirm-dialog';
import { OfferResponse, OfferRequest } from '../../../../../models/interfaceCompany';
import { ChangePasswordCompoment } from '../../../../../shared/change-password-compoment/change-password-compoment';
import { CompanyOfferService } from '../../../../services/company-offer-service';
import { OfferDialog } from '../dialogs/offer-dialog/offer-dialog';


@Component({
  selector: 'app-company-dasboard',
  standalone: false,
  templateUrl: './company-dasboard.html',
  styleUrls: ['./company-dasboard.css'] 
})
export class CompanyDasboard implements OnInit {

 
  profileName?: string;
  profileEmail?: string;
  profileRole?: string;

  
  settingsOpen = false;


  offers: OfferResponse[] = [];
  filteredOffers: OfferResponse[] = [];

  searchText = '';
  isFiltered = false;

  constructor(
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private authService: AuthService,
    private companyOfferService: CompanyOfferService
  ) {}

  ngOnInit(): void {
    const payload = this.authService.decodePayload?.();
    if (payload) {
      this.profileName = `${payload.firstName ?? ''} ${payload.lastName ?? ''}`.trim() || payload.companyName || payload.username;
      this.profileEmail = payload.email;
      this.profileRole = payload.role ?? payload.authorities?.[0];
    }
    this.loadOffers();
  }

  // ====== Data ======
  private loadOffers(): void {
    this.companyOfferService.getMyOffers().subscribe({
      next: (offers) => {
        // tri du + récent au + ancien
        this.offers = [...offers].sort((a, b) => {
          const da = new Date(a.createdAt).getTime();
          const db = new Date(b.createdAt).getTime();
          return db - da;
        });
        this.applyFilters();
      },
      error: () => {
        this.snackBar.open('Erreur lors du chargement des offres', 'Fermer', { duration: 3000 });
      }
    });
  }

  // ====== Filtres ======
  applyFilters(): void {
    const q = (this.searchText || '').trim().toLowerCase();

    if (!q) {
      this.filteredOffers = [...this.offers];
      this.isFiltered = false;
      return;
    }

    this.filteredOffers = this.offers.filter(o => {
      const title = (o.title || '').toLowerCase();
      const desc = (o.description || '').toLowerCase();
      return title.includes(q) || desc.includes(q);
    });
    this.isFiltered = true;
  }

  resetFilters(): void {
    this.searchText = '';
    this.applyFilters();
  }

  // ====== Actions ======
  createOffer(): void {
    const dialogRef = this.dialog.open(OfferDialog, {
      width: '600px',
      data: { isEdit: false }
    });

    dialogRef.afterClosed().subscribe((result?: OfferRequest) => {
      if (!result) { return; }

      this.companyOfferService.createOffer(result).subscribe({
        next: (created) => {
          // on ajoute en tête de liste
          this.offers = [created, ...this.offers];
          this.applyFilters();
          this.snackBar.open('Offer created', 'Close', { duration: 2500 });
        },
        error: () => {
          this.snackBar.open('Error creating offer', 'Close', { duration: 3000 });
        }
      });
    });
  }

  editOffer(offer: OfferResponse): void {
    const dialogRef = this.dialog.open(OfferDialog, {
      width: '600px',
      data: {
        isEdit: true,
        offer: { ...offer } as OfferResponse
      }
    });

    dialogRef.afterClosed().subscribe((payload?: OfferRequest) => {
      if (!payload) { return; }

      this.companyOfferService.updateOffer(offer.id, payload).subscribe({
        next: (updated) => {
          const idx = this.offers.findIndex(o => o.id === offer.id);
          if (idx !== -1) {
            const next = [...this.offers];
            next[idx] = updated;
            // re-trier après mise à jour
            this.offers = next.sort((a, b) =>
              new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
            );
            this.applyFilters();
          }
          this.snackBar.open('Changes saved', 'Close', { duration: 2500 });
        },
        error: () => {
          this.snackBar.open('Error saving changes', 'Close', { duration: 3000 });
        }
      });
    });
  }

  deleteOffer(offer: OfferResponse): void {
    const dialogRef = this.dialog.open(DeleteConfirmDialog, {
      width: '420px',
      data: { userName: offer.title } // on réutilise la même prop pour afficher le nom
    });

    dialogRef.afterClosed().subscribe((confirmed?: boolean) => {
      if (!confirmed) { return; }

      this.companyOfferService.deleteOffer(offer.id).subscribe({
        next: () => {
          this.offers = this.offers.filter(o => o.id !== offer.id);
          this.applyFilters();
          this.snackBar.open('Offer deleted', 'Close', { duration: 2500 });
        },
        error: () => {
          this.snackBar.open('Error deleting offer', 'Close', { duration: 3000 });
        }
      });
    });
  }

  // ====== Sidebar / nav ======
  navigate(route: string): void {
    this.router.navigate([route]);
  }

  logout(): void {
    this.authService.logout?.();
    this.router.navigate(['/login']);
  }

  toggleSettings(): void {
    this.settingsOpen = !this.settingsOpen;
  }

  openChangePasswordDialog(): void {
    this.dialog.open(ChangePasswordCompoment, { width: '400px' });
  }

  // ====== Utils ======
  trackByOfferId(_: number, item: OfferResponse) {
    return item.id;
  }
}
