export interface OfferRequest {
  title: string;
  description: string;
  logoUrl?: string | null;
  websiteUrl?: string | null;
}

/** Réponse renvoyée par le backend */
export interface OfferResponse {
  id: number;
  title: string;
  description: string;
  logoUrl?: string | null;
  websiteUrl?: string | null;
  createdAt: string;     // ISO string
  createdBy?: any;       // éventuellement renvoyé, non requis côté UI
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