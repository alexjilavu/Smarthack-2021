import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAppUser, AppUser } from '../app-user.model';

import { AppUserService } from './app-user.service';

describe('AppUser Service', () => {
  let service: AppUserService;
  let httpMock: HttpTestingController;
  let elemDefault: IAppUser;
  let expectedResult: IAppUser | IAppUser[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AppUserService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      walletAddress: 'AAAAAAA',
      walletPassword: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a AppUser', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new AppUser()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AppUser', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          walletAddress: 'BBBBBB',
          walletPassword: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AppUser', () => {
      const patchObject = Object.assign(
        {
          walletAddress: 'BBBBBB',
          walletPassword: 'BBBBBB',
        },
        new AppUser()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AppUser', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          walletAddress: 'BBBBBB',
          walletPassword: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a AppUser', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAppUserToCollectionIfMissing', () => {
      it('should add a AppUser to an empty array', () => {
        const appUser: IAppUser = { id: 123 };
        expectedResult = service.addAppUserToCollectionIfMissing([], appUser);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(appUser);
      });

      it('should not add a AppUser to an array that contains it', () => {
        const appUser: IAppUser = { id: 123 };
        const appUserCollection: IAppUser[] = [
          {
            ...appUser,
          },
          { id: 456 },
        ];
        expectedResult = service.addAppUserToCollectionIfMissing(appUserCollection, appUser);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AppUser to an array that doesn't contain it", () => {
        const appUser: IAppUser = { id: 123 };
        const appUserCollection: IAppUser[] = [{ id: 456 }];
        expectedResult = service.addAppUserToCollectionIfMissing(appUserCollection, appUser);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(appUser);
      });

      it('should add only unique AppUser to an array', () => {
        const appUserArray: IAppUser[] = [{ id: 123 }, { id: 456 }, { id: 62455 }];
        const appUserCollection: IAppUser[] = [{ id: 123 }];
        expectedResult = service.addAppUserToCollectionIfMissing(appUserCollection, ...appUserArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const appUser: IAppUser = { id: 123 };
        const appUser2: IAppUser = { id: 456 };
        expectedResult = service.addAppUserToCollectionIfMissing([], appUser, appUser2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(appUser);
        expect(expectedResult).toContain(appUser2);
      });

      it('should accept null and undefined values', () => {
        const appUser: IAppUser = { id: 123 };
        expectedResult = service.addAppUserToCollectionIfMissing([], null, appUser, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(appUser);
      });

      it('should return initial array if no AppUser is added', () => {
        const appUserCollection: IAppUser[] = [{ id: 123 }];
        expectedResult = service.addAppUserToCollectionIfMissing(appUserCollection, undefined, null);
        expect(expectedResult).toEqual(appUserCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
