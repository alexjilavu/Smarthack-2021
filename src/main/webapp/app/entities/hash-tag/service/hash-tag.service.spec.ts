import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IHashTag, HashTag } from '../hash-tag.model';

import { HashTagService } from './hash-tag.service';

describe('HashTag Service', () => {
  let service: HashTagService;
  let httpMock: HttpTestingController;
  let elemDefault: IHashTag;
  let expectedResult: IHashTag | IHashTag[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(HashTagService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      company: 'AAAAAAA',
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

    it('should create a HashTag', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new HashTag()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a HashTag', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          company: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a HashTag', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
        },
        new HashTag()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of HashTag', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          company: 'BBBBBB',
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

    it('should delete a HashTag', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addHashTagToCollectionIfMissing', () => {
      it('should add a HashTag to an empty array', () => {
        const hashTag: IHashTag = { id: 123 };
        expectedResult = service.addHashTagToCollectionIfMissing([], hashTag);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(hashTag);
      });

      it('should not add a HashTag to an array that contains it', () => {
        const hashTag: IHashTag = { id: 123 };
        const hashTagCollection: IHashTag[] = [
          {
            ...hashTag,
          },
          { id: 456 },
        ];
        expectedResult = service.addHashTagToCollectionIfMissing(hashTagCollection, hashTag);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a HashTag to an array that doesn't contain it", () => {
        const hashTag: IHashTag = { id: 123 };
        const hashTagCollection: IHashTag[] = [{ id: 456 }];
        expectedResult = service.addHashTagToCollectionIfMissing(hashTagCollection, hashTag);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(hashTag);
      });

      it('should add only unique HashTag to an array', () => {
        const hashTagArray: IHashTag[] = [{ id: 123 }, { id: 456 }, { id: 79848 }];
        const hashTagCollection: IHashTag[] = [{ id: 123 }];
        expectedResult = service.addHashTagToCollectionIfMissing(hashTagCollection, ...hashTagArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const hashTag: IHashTag = { id: 123 };
        const hashTag2: IHashTag = { id: 456 };
        expectedResult = service.addHashTagToCollectionIfMissing([], hashTag, hashTag2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(hashTag);
        expect(expectedResult).toContain(hashTag2);
      });

      it('should accept null and undefined values', () => {
        const hashTag: IHashTag = { id: 123 };
        expectedResult = service.addHashTagToCollectionIfMissing([], null, hashTag, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(hashTag);
      });

      it('should return initial array if no HashTag is added', () => {
        const hashTagCollection: IHashTag[] = [{ id: 123 }];
        expectedResult = service.addHashTagToCollectionIfMissing(hashTagCollection, undefined, null);
        expect(expectedResult).toEqual(hashTagCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
