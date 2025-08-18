export interface OfferRequest {
  title: string;
  description: string;
  logoUrl?: string | null;
  websiteUrl?: string | null;
}
export interface OfferResponse {
expanded: any;
  id: number;
  title: string;
  description: string;
  logoUrl: string | null;
  websiteUrl: string | null;
  createdAt: string;       
  companyName: string;     
  applied: boolean;        
}
export interface Offer {
  id: number;
  title: string;
  description: string;
  logoUrl?: string;
  websiteUrl?: string;
  createdAt: string; 
  createdBy?: any;  
}

export interface ApplyDialogPayload {
  cv: File;
  motivation: File;
}
export type Role = 'COMPANY' | 'STUDENT' | 'ADMIN';


export interface DeleteConfirmDialogData {
  userId: number;
  userName: string;
}

export interface OfferDialogData {
  isEdit: boolean;
  offer?: OfferResponse;
}
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


export interface AuthResponse { token: string }
export interface ErrorResponse { message: string }

export interface UserResponse {
  createdAt: any;
  id: number;
  username: string;
  email: string;
  role: Role;
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
  companyName?: string;
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  role: Role;
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
  companyName?: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  role: Role;
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
  companyName?: string;
}