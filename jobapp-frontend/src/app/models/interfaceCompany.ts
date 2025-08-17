export interface OfferRequest {
  title: string;
  description: string;
  logoUrl?: string | null;
  websiteUrl?: string | null;
}
export interface OfferResponse {
  id: number;
  title: string;
  description: string;
  logoUrl: string | null;
  websiteUrl: string | null;
  createdAt: string;       // ISO date
  companyName: string;     // exposé par OfferService.toDto(...)
  applied: boolean;        // calculé pour l’étudiant connecté
}
export interface Offer {
  id: number;
  title: string;
  description: string;
  logoUrl?: string;
  websiteUrl?: string;
  createdAt: string; // ISO
  createdBy?: any;   // si le backend la renvoie; non utilisé en édition
}