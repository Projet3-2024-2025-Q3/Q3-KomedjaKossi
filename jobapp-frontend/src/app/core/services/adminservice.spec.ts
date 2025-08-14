import { TestBed } from '@angular/core/testing';

import { adminservice } from './adminservice';

describe('Adminservice', () => {
  let service: adminservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(adminservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
