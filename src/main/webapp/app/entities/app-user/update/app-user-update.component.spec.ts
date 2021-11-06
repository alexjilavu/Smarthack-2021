jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { AppUserService } from '../service/app-user.service';
import { IAppUser, AppUser } from '../app-user.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IChallenge } from 'app/entities/challenge/challenge.model';
import { ChallengeService } from 'app/entities/challenge/service/challenge.service';

import { AppUserUpdateComponent } from './app-user-update.component';

describe('Component Tests', () => {
  describe('AppUser Management Update Component', () => {
    let comp: AppUserUpdateComponent;
    let fixture: ComponentFixture<AppUserUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let appUserService: AppUserService;
    let userService: UserService;
    let challengeService: ChallengeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AppUserUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(AppUserUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AppUserUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      appUserService = TestBed.inject(AppUserService);
      userService = TestBed.inject(UserService);
      challengeService = TestBed.inject(ChallengeService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const appUser: IAppUser = { id: 456 };
        const appUser: IUser = { id: 80166 };
        appUser.appUser = appUser;

        const userCollection: IUser[] = [{ id: 18619 }];
        jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [appUser];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ appUser });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Challenge query and add missing value', () => {
        const appUser: IAppUser = { id: 456 };
        const completedChallenges: IChallenge[] = [{ id: 4133 }];
        appUser.completedChallenges = completedChallenges;

        const challengeCollection: IChallenge[] = [{ id: 13582 }];
        jest.spyOn(challengeService, 'query').mockReturnValue(of(new HttpResponse({ body: challengeCollection })));
        const additionalChallenges = [...completedChallenges];
        const expectedCollection: IChallenge[] = [...additionalChallenges, ...challengeCollection];
        jest.spyOn(challengeService, 'addChallengeToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ appUser });
        comp.ngOnInit();

        expect(challengeService.query).toHaveBeenCalled();
        expect(challengeService.addChallengeToCollectionIfMissing).toHaveBeenCalledWith(challengeCollection, ...additionalChallenges);
        expect(comp.challengesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const appUser: IAppUser = { id: 456 };
        const appUser: IUser = { id: 29309 };
        appUser.appUser = appUser;
        const completedChallenges: IChallenge = { id: 35132 };
        appUser.completedChallenges = [completedChallenges];

        activatedRoute.data = of({ appUser });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(appUser));
        expect(comp.usersSharedCollection).toContain(appUser);
        expect(comp.challengesSharedCollection).toContain(completedChallenges);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<AppUser>>();
        const appUser = { id: 123 };
        jest.spyOn(appUserService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ appUser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: appUser }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(appUserService.update).toHaveBeenCalledWith(appUser);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<AppUser>>();
        const appUser = new AppUser();
        jest.spyOn(appUserService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ appUser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: appUser }));
        saveSubject.complete();

        // THEN
        expect(appUserService.create).toHaveBeenCalledWith(appUser);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<AppUser>>();
        const appUser = { id: 123 };
        jest.spyOn(appUserService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ appUser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(appUserService.update).toHaveBeenCalledWith(appUser);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackChallengeById', () => {
        it('Should return tracked Challenge primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackChallengeById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedChallenge', () => {
        it('Should return option if no Challenge is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedChallenge(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Challenge for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedChallenge(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Challenge is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedChallenge(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
