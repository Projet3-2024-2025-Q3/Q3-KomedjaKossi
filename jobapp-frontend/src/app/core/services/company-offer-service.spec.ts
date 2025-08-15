import { TestBed } from '@angular/core/testing';

import { CompanyOfferService } from './company-offer-service';

describe('CompanyOfferService', () => {
  let service: CompanyOfferService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CompanyOfferService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
