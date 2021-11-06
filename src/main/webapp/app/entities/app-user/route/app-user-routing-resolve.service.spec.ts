jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IAppUser, AppUser } from '../app-user.model';
import { AppUserService } from '../service/app-user.service';

import { AppUserRoutingResolveService } from './app-user-routing-resolve.service';

describe('AppUser routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: AppUserRoutingResolveService;
  let service: AppUserService;
  let resultAppUser: IAppUser | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(AppUserRoutingResolveService);
    service = TestBed.inject(AppUserService);
    resultAppUser = undefined;
  });

  describe('resolve', () => {
    it('should return IAppUser returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAppUser = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultAppUser).toEqual({ id: 123 });
    });

    it('should return new IAppUser if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAppUser = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultAppUser).toEqual(new AppUser());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as AppUser })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAppUser = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultAppUser).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
