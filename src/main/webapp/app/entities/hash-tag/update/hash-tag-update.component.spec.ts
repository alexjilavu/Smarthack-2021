jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { HashTagService } from '../service/hash-tag.service';
import { IHashTag, HashTag } from '../hash-tag.model';

import { HashTagUpdateComponent } from './hash-tag-update.component';

describe('Component Tests', () => {
  describe('HashTag Management Update Component', () => {
    let comp: HashTagUpdateComponent;
    let fixture: ComponentFixture<HashTagUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let hashTagService: HashTagService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [HashTagUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(HashTagUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(HashTagUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      hashTagService = TestBed.inject(HashTagService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const hashTag: IHashTag = { id: 456 };

        activatedRoute.data = of({ hashTag });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(hashTag));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<HashTag>>();
        const hashTag = { id: 123 };
        jest.spyOn(hashTagService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ hashTag });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: hashTag }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(hashTagService.update).toHaveBeenCalledWith(hashTag);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<HashTag>>();
        const hashTag = new HashTag();
        jest.spyOn(hashTagService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ hashTag });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: hashTag }));
        saveSubject.complete();

        // THEN
        expect(hashTagService.create).toHaveBeenCalledWith(hashTag);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<HashTag>>();
        const hashTag = { id: 123 };
        jest.spyOn(hashTagService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ hashTag });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(hashTagService.update).toHaveBeenCalledWith(hashTag);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
