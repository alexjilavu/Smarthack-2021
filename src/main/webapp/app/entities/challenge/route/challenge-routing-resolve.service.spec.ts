jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IChallenge, Challenge } from '../challenge.model';
import { ChallengeService } from '../service/challenge.service';

import { ChallengeRoutingResolveService } from './challenge-routing-resolve.service';

describe('Challenge routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ChallengeRoutingResolveService;
  let service: ChallengeService;
  let resultChallenge: IChallenge | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(ChallengeRoutingResolveService);
    service = TestBed.inject(ChallengeService);
    resultChallenge = undefined;
  });

  describe('resolve', () => {
    it('should return IChallenge returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultChallenge = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultChallenge).toEqual({ id: 123 });
    });

    it('should return new IChallenge if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultChallenge = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultChallenge).toEqual(new Challenge());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Challenge })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultChallenge = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultChallenge).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
