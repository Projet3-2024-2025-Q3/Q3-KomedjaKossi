import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';

import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

import { DeleteConfirmDialog } from '../dialogs/delete-confirm-dialog/delete-confirm-dialog';
import { UserDialog } from '../dialogs/user-dialog/user-dialog';

import { adminservice, RegisterRequest, UserResponse } from '../../../../services/adminservice';
import { AuthService } from '../../../../services/auth.service';
import { ChangePasswordCompoment } from '../../../../../shared/change-password-compoment/change-password-compoment';

type Role = 'company' | 'student' | 'admin';

@Component({
  selector: 'app-admin-dashboard',
  standalone: false,
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})
export class AdminDashboard implements OnInit {

  displayedColumns: string[] = ['fullName', 'username', 'email', 'role', 'actions'];
  dataSource: MatTableDataSource<UserResponse> = new MatTableDataSource<UserResponse>([]);

  searchText = '';
  selectedRoles: Role[] = [];
  isFiltered = false;
  settingsOpen = false;

  profileName?: string;
  profileEmail?: string;
  profileRole?: string;

  constructor(
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private adminUserService: adminservice,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.setupFilters();

    const payload = this.authService.decodePayload();
    if (payload) {
      this.profileName = `${payload.firstName ?? ''} ${payload.lastName ?? ''}`.trim();
      this.profileEmail = payload.email;
      this.profileRole = payload.role ?? payload.authorities?.[0];
    }
  }

  loadUsers(): void {
    this.adminUserService.getAll().subscribe({
      next: (users) => {
             const payload = this.authService.decodePayload();
      this.dataSource.data = payload
        ? users.filter(u => u.username !== payload.username)
        : users;
      },
      error: () => {
        this.snackBar.open('Error loading users', 'Close', { duration: 3000 });
      }
    });
  }

  setupFilters(): void {
    this.dataSource.filterPredicate = (user: UserResponse, filter: string) => {
      const filters = JSON.parse(filter);

      if (filters.text) {
        const fullName = `${user.firstName} ${user.lastName}`.toLowerCase();
        const username = (user.username || '').toLowerCase();
        const email = (user.email || '').toLowerCase();
        const q = filters.text.toLowerCase();
        if (!fullName.includes(q) && !username.includes(q) && !email.includes(q)) return false;
      }

      if (filters.roles && filters.roles.length > 0) {
        const uiRole = (user.role || '').toString().toLowerCase();
        if (!filters.roles.includes(uiRole)) return false;
      }

      return true;
    };
  }

  applyFilters(): void {
    const filters = {
      text: this.searchText,
      roles: this.selectedRoles
    };
    this.dataSource.filter = JSON.stringify(filters);
    this.isFiltered = !!(this.searchText || this.selectedRoles.length > 0);
  }

  resetFilters(): void {
    this.searchText = '';
    this.selectedRoles = [];
    this.applyFilters();
  }

  getTotalUsers(): number {
    return this.dataSource.data.length;
  }

  formatRole(role: string): string {
    const roleMap: Record<string, string> = {
      company: 'Company',
      student: 'Student',
      admin: 'Admin',
      COMPANY: 'Company',
      STUDENT: 'Student',
      ADMIN: 'Admin'
    };
    return roleMap[role] || role;
  }

  openEditDialog(user: UserResponse): void {
    const dialogRef = this.dialog.open(UserDialog, {
      width: '500px',
      data: { user: { ...user }, isEdit: true }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (!result) return;

      this.adminUserService.update(user.id, result as RegisterRequest).subscribe({
        next: (updated) => {
          const idx = this.dataSource.data.findIndex(u => u.id === user.id);
          if (idx !== -1) {
            const nextData = [...this.dataSource.data];
            nextData[idx] = updated;
            this.dataSource.data = nextData;
          }
          this.snackBar.open('Changes saved', 'Close', { duration: 2500 });
        },
        error: () => {
          this.snackBar.open('Error saving changes', 'Close', { duration: 3000 });
        }
      });
    });
  }

 openDeleteDialog(user: UserResponse): void {
  const dialogRef = this.dialog.open(DeleteConfirmDialog, {
    width: '400px',
    data: { userName: `${user.firstName} ${user.lastName}` } // ⬅ remove userId (not needed)
  });

  dialogRef.afterClosed().subscribe(result => {
    if (!result) return; // only if user confirmed

    this.adminUserService.delete(user.id).subscribe({
      next: () => {
        this.dataSource.data = this.dataSource.data.filter(u => u.id !== user.id);
        this.snackBar.open('User deleted', 'Close', { duration: 2500 });
      },
      error: () => {
        this.snackBar.open('Error deleting user', 'Close', { duration: 3000 });
      }
    });
  });
}

  navigate(route: string): void {
    this.router.navigate([route]);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  

  toggleSettings(): void {
    this.settingsOpen = !this.settingsOpen;
  }
  
  openChangePasswordDialog(): void {
  this.dialog.open(ChangePasswordCompoment, {
    width: '400px'
  });
}

}
