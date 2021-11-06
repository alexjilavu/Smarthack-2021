import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IIcon, Icon } from '../icon.model';

import { IconService } from './icon.service';

describe('Icon Service', () => {
  let service: IconService;
  let httpMock: HttpTestingController;
  let elemDefault: IIcon;
  let expectedResult: IIcon | IIcon[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(IconService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      url: 'AAAAAAA',
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

    it('should create a Icon', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Icon()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Icon', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          url: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Icon', () => {
      const patchObject = Object.assign(
        {
          url: 'BBBBBB',
        },
        new Icon()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Icon', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          url: 'BBBBBB',
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

    it('should delete a Icon', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addIconToCollectionIfMissing', () => {
      it('should add a Icon to an empty array', () => {
        const icon: IIcon = { id: 123 };
        expectedResult = service.addIconToCollectionIfMissing([], icon);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(icon);
      });

      it('should not add a Icon to an array that contains it', () => {
        const icon: IIcon = { id: 123 };
        const iconCollection: IIcon[] = [
          {
            ...icon,
          },
          { id: 456 },
        ];
        expectedResult = service.addIconToCollectionIfMissing(iconCollection, icon);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Icon to an array that doesn't contain it", () => {
        const icon: IIcon = { id: 123 };
        const iconCollection: IIcon[] = [{ id: 456 }];
        expectedResult = service.addIconToCollectionIfMissing(iconCollection, icon);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(icon);
      });

      it('should add only unique Icon to an array', () => {
        const iconArray: IIcon[] = [{ id: 123 }, { id: 456 }, { id: 55130 }];
        const iconCollection: IIcon[] = [{ id: 123 }];
        expectedResult = service.addIconToCollectionIfMissing(iconCollection, ...iconArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const icon: IIcon = { id: 123 };
        const icon2: IIcon = { id: 456 };
        expectedResult = service.addIconToCollectionIfMissing([], icon, icon2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(icon);
        expect(expectedResult).toContain(icon2);
      });

      it('should accept null and undefined values', () => {
        const icon: IIcon = { id: 123 };
        expectedResult = service.addIconToCollectionIfMissing([], null, icon, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(icon);
      });

      it('should return initial array if no Icon is added', () => {
        const iconCollection: IIcon[] = [{ id: 123 }];
        expectedResult = service.addIconToCollectionIfMissing(iconCollection, undefined, null);
        expect(expectedResult).toEqual(iconCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
