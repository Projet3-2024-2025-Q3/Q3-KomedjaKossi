import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserDialog } from '../dialogs/user-dialog/user-dialog';
import { DeleteConfirmDialog } from '../dialogs/delete-confirm-dialog/delete-confirm-dialog';

// Types
type Role = 'company' | 'student' | 'admin';

interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  createdAt: Date;
  role: Role;
  password?: string;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: false,
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css'
})
export class AdminDashboard implements OnInit {
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  // Colonnes affichées dans la table
  displayedColumns: string[] = ['fullName', 'email', 'createdAt', 'role', 'actions'];
  
  // Source de données pour la table
  dataSource: MatTableDataSource<User>;
  
  // Variables de filtrage
  searchText: string = '';
  selectedRoles: Role[] = [];
  startDate: Date | null = null;
  endDate: Date | null = null;
  isFiltered: boolean = false;
  
  // Données initiales (simulées)
  private users: User[] = [
    { id: 1, firstName: 'Marie', lastName: 'Martin', email: 'marie.martin@company.com', createdAt: new Date('2024-01-15'), role: 'company' },
    { id: 2, firstName: 'Pierre', lastName: 'Dubois', email: 'pierre.dubois@student.edu', createdAt: new Date('2024-02-20'), role: 'student' },
    { id: 3, firstName: 'Sophie', lastName: 'Bernard', email: 'sophie.bernard@admin.com', createdAt: new Date('2024-01-10'), role: 'admin' },
    { id: 4, firstName: 'Lucas', lastName: 'Moreau', email: 'lucas.moreau@company.com', createdAt: new Date('2024-03-05'), role: 'company' },
    { id: 5, firstName: 'Emma', lastName: 'Rousseau', email: 'emma.rousseau@student.edu', createdAt: new Date('2024-04-12'), role: 'student' },
    { id: 6, firstName: 'Thomas', lastName: 'Garcia', email: 'thomas.garcia@company.com', createdAt: new Date('2024-02-28'), role: 'company' },
    { id: 7, firstName: 'Léa', lastName: 'Martinez', email: 'lea.martinez@admin.com', createdAt: new Date('2024-01-25'), role: 'admin' },
    { id: 8, firstName: 'Antoine', lastName: 'Leroy', email: 'antoine.leroy@student.edu', createdAt: new Date('2024-05-10'), role: 'student' },
    { id: 9, firstName: 'Julie', lastName: 'Simon', email: 'julie.simon@company.com', createdAt: new Date('2024-03-18'), role: 'company' },
    { id: 10, firstName: 'Maxime', lastName: 'Laurent', email: 'maxime.laurent@student.edu', createdAt: new Date('2024-06-01'), role: 'student' },
    { id: 11, firstName: 'Camille', lastName: 'Michel', email: 'camille.michel@company.com', createdAt: new Date('2024-04-22'), role: 'company' },
    { id: 12, firstName: 'Nicolas', lastName: 'Lefebvre', email: 'nicolas.lefebvre@admin.com', createdAt: new Date('2024-02-14'), role: 'admin' },
    { id: 13, firstName: 'Sarah', lastName: 'David', email: 'sarah.david@student.edu', createdAt: new Date('2024-05-28'), role: 'student' },
    { id: 14, firstName: 'Alexandre', lastName: 'Bertrand', email: 'alexandre.bertrand@company.com', createdAt: new Date('2024-03-30'), role: 'company' },
    { id: 15, firstName: 'Manon', lastName: 'Roux', email: 'manon.roux@student.edu', createdAt: new Date('2024-06-15'), role: 'student' }
  ];

  constructor(
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {
    this.dataSource = new MatTableDataSource(this.users);
    this.setupFilters();
  }

  ngOnInit(): void {
    // Initialisation si nécessaire
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    
    // Configuration du tri personnalisé
    this.dataSource.sortingDataAccessor = (user: User, property: string) => {
      switch (property) {
        case 'fullName':
          return `${user.firstName} ${user.lastName}`.toLowerCase();
        case 'email':
          return user.email.toLowerCase();
        case 'createdAt':
          return user.createdAt.getTime();
        case 'role':
          return user.role;
        default:
          return '';
      }
    };
  }

  // Configuration du filtre personnalisé
  setupFilters(): void {
    this.dataSource.filterPredicate = (user: User, filter: string) => {
      // Parse du filtre JSON
      const filters = JSON.parse(filter);
      
      // Filtre par texte (nom ou email)
      if (filters.text) {
        const fullName = `${user.firstName} ${user.lastName}`.toLowerCase();
        const email = user.email.toLowerCase();
        const searchText = filters.text.toLowerCase();
        
        if (!fullName.includes(searchText) && !email.includes(searchText)) {
          return false;
        }
      }
      
      // Filtre par rôles
      if (filters.roles && filters.roles.length > 0) {
        if (!filters.roles.includes(user.role)) {
          return false;
        }
      }
      
      // Filtre par date
      if (filters.startDate || filters.endDate) {
        const userDate = user.createdAt.getTime();
        
        if (filters.startDate && userDate < new Date(filters.startDate).getTime()) {
          return false;
        }
        
        if (filters.endDate) {
          const endDate = new Date(filters.endDate);
          endDate.setHours(23, 59, 59, 999);
          if (userDate > endDate.getTime()) {
            return false;
          }
        }
      }
      
      return true;
    };
  }

  // Application des filtres
  applyFilters(): void {
    const filters = {
      text: this.searchText,
      roles: this.selectedRoles,
      startDate: this.startDate,
      endDate: this.endDate
    };
    
    this.dataSource.filter = JSON.stringify(filters);
    this.isFiltered = !!(this.searchText || this.selectedRoles.length > 0 || this.startDate || this.endDate);
    
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  // Réinitialisation des filtres
  resetFilters(): void {
    this.searchText = '';
    this.selectedRoles = [];
    this.startDate = null;
    this.endDate = null;
    this.applyFilters();
  }

  // Obtenir le nombre total d'utilisateurs
  getTotalUsers(): number {
    return this.users.length;
  }

  // Formater le rôle pour l'affichage
  formatRole(role: Role): string {
    const roleMap: Record<Role, string> = {
      company: 'Company',
      student: 'Student',
      admin: 'Admin'
    };
    return roleMap[role];
  }

  // Ouvrir le dialogue de création
  openCreateDialog(): void {
    const dialogRef = this.dialog.open(UserDialog, {
      width: '500px',
      data: { user: null, isEdit: false }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const newUser: User = {
          ...result,
          id: Math.max(...this.users.map(u => u.id)) + 1,
          createdAt: new Date()
        };
        this.users.push(newUser);
        this.dataSource.data = [...this.users];
        this.snackBar.open('Utilisateur créé', 'Fermer', { duration: 3000 });
      }
    });
  }

  // Ouvrir le dialogue d'édition
  openEditDialog(user: User): void {
    const dialogRef = this.dialog.open(UserDialog, {
      width: '500px',
      data: { user: { ...user }, isEdit: true }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const index = this.users.findIndex(u => u.id === user.id);
        if (index !== -1) {
          this.users[index] = { ...this.users[index], ...result };
          this.dataSource.data = [...this.users];
          this.snackBar.open('Modifications enregistrées', 'Fermer', { duration: 3000 });
        }
      }
    });
  }

  // Ouvrir le dialogue de suppression
  openDeleteDialog(user: User): void {
    const dialogRef = this.dialog.open(DeleteConfirmDialog, {
      width: '400px',
      data: { userName: `${user.firstName} ${user.lastName}` }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.users = this.users.filter(u => u.id !== user.id);
        this.dataSource.data = [...this.users];
        this.snackBar.open('Utilisateur supprimé', 'Fermer', { duration: 3000 });
      }
    });
  }

  // Navigation (placeholder)
  navigate(route: string): void {
    console.log(`Navigation vers: ${route}`);
  }

  // Déconnexion (placeholder)
  logout(): void {
    console.log('Déconnexion');
  }
}