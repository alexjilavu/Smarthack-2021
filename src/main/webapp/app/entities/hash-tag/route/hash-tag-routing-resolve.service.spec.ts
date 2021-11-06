jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IHashTag, HashTag } from '../hash-tag.model';
import { HashTagService } from '../service/hash-tag.service';

import { HashTagRoutingResolveService } from './hash-tag-routing-resolve.service';

describe('HashTag routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: HashTagRoutingResolveService;
  let service: HashTagService;
  let resultHashTag: IHashTag | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(HashTagRoutingResolveService);
    service = TestBed.inject(HashTagService);
    resultHashTag = undefined;
  });

  describe('resolve', () => {
    it('should return IHashTag returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultHashTag = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultHashTag).toEqual({ id: 123 });
    });

    it('should return new IHashTag if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultHashTag = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultHashTag).toEqual(new HashTag());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as HashTag })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultHashTag = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultHashTag).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
