jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { IconService } from '../service/icon.service';
import { IIcon, Icon } from '../icon.model';

import { IconUpdateComponent } from './icon-update.component';

describe('Component Tests', () => {
  describe('Icon Management Update Component', () => {
    let comp: IconUpdateComponent;
    let fixture: ComponentFixture<IconUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let iconService: IconService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [IconUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(IconUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(IconUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      iconService = TestBed.inject(IconService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const icon: IIcon = { id: 456 };

        activatedRoute.data = of({ icon });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(icon));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Icon>>();
        const icon = { id: 123 };
        jest.spyOn(iconService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ icon });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: icon }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(iconService.update).toHaveBeenCalledWith(icon);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Icon>>();
        const icon = new Icon();
        jest.spyOn(iconService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ icon });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: icon }));
        saveSubject.complete();

        // THEN
        expect(iconService.create).toHaveBeenCalledWith(icon);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Icon>>();
        const icon = { id: 123 };
        jest.spyOn(iconService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ icon });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(iconService.update).toHaveBeenCalledWith(icon);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
