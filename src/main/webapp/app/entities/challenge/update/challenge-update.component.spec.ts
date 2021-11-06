jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ChallengeService } from '../service/challenge.service';
import { IChallenge, Challenge } from '../challenge.model';
import { IIcon } from 'app/entities/icon/icon.model';
import { IconService } from 'app/entities/icon/service/icon.service';
import { IHashTag } from 'app/entities/hash-tag/hash-tag.model';
import { HashTagService } from 'app/entities/hash-tag/service/hash-tag.service';

import { ChallengeUpdateComponent } from './challenge-update.component';

describe('Component Tests', () => {
  describe('Challenge Management Update Component', () => {
    let comp: ChallengeUpdateComponent;
    let fixture: ComponentFixture<ChallengeUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let challengeService: ChallengeService;
    let iconService: IconService;
    let hashTagService: HashTagService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ChallengeUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ChallengeUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ChallengeUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      challengeService = TestBed.inject(ChallengeService);
      iconService = TestBed.inject(IconService);
      hashTagService = TestBed.inject(HashTagService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Icon query and add missing value', () => {
        const challenge: IChallenge = { id: 456 };
        const icon: IIcon = { id: 95401 };
        challenge.icon = icon;

        const iconCollection: IIcon[] = [{ id: 77223 }];
        jest.spyOn(iconService, 'query').mockReturnValue(of(new HttpResponse({ body: iconCollection })));
        const additionalIcons = [icon];
        const expectedCollection: IIcon[] = [...additionalIcons, ...iconCollection];
        jest.spyOn(iconService, 'addIconToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ challenge });
        comp.ngOnInit();

        expect(iconService.query).toHaveBeenCalled();
        expect(iconService.addIconToCollectionIfMissing).toHaveBeenCalledWith(iconCollection, ...additionalIcons);
        expect(comp.iconsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call HashTag query and add missing value', () => {
        const challenge: IChallenge = { id: 456 };
        const hashTags: IHashTag[] = [{ id: 46506 }];
        challenge.hashTags = hashTags;

        const hashTagCollection: IHashTag[] = [{ id: 96849 }];
        jest.spyOn(hashTagService, 'query').mockReturnValue(of(new HttpResponse({ body: hashTagCollection })));
        const additionalHashTags = [...hashTags];
        const expectedCollection: IHashTag[] = [...additionalHashTags, ...hashTagCollection];
        jest.spyOn(hashTagService, 'addHashTagToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ challenge });
        comp.ngOnInit();

        expect(hashTagService.query).toHaveBeenCalled();
        expect(hashTagService.addHashTagToCollectionIfMissing).toHaveBeenCalledWith(hashTagCollection, ...additionalHashTags);
        expect(comp.hashTagsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const challenge: IChallenge = { id: 456 };
        const icon: IIcon = { id: 60673 };
        challenge.icon = icon;
        const hashTags: IHashTag = { id: 67572 };
        challenge.hashTags = [hashTags];

        activatedRoute.data = of({ challenge });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(challenge));
        expect(comp.iconsSharedCollection).toContain(icon);
        expect(comp.hashTagsSharedCollection).toContain(hashTags);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Challenge>>();
        const challenge = { id: 123 };
        jest.spyOn(challengeService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ challenge });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: challenge }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(challengeService.update).toHaveBeenCalledWith(challenge);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Challenge>>();
        const challenge = new Challenge();
        jest.spyOn(challengeService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ challenge });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: challenge }));
        saveSubject.complete();

        // THEN
        expect(challengeService.create).toHaveBeenCalledWith(challenge);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Challenge>>();
        const challenge = { id: 123 };
        jest.spyOn(challengeService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ challenge });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(challengeService.update).toHaveBeenCalledWith(challenge);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackIconById', () => {
        it('Should return tracked Icon primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackIconById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackHashTagById', () => {
        it('Should return tracked HashTag primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackHashTagById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedHashTag', () => {
        it('Should return option if no HashTag is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedHashTag(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected HashTag for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedHashTag(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this HashTag is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedHashTag(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
