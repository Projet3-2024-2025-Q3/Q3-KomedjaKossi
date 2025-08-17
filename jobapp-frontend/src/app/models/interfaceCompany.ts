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