jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { RewardService } from '../service/reward.service';
import { IReward, Reward } from '../reward.model';
import { IIcon } from 'app/entities/icon/icon.model';
import { IconService } from 'app/entities/icon/service/icon.service';
import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';

import { RewardUpdateComponent } from './reward-update.component';

describe('Component Tests', () => {
  describe('Reward Management Update Component', () => {
    let comp: RewardUpdateComponent;
    let fixture: ComponentFixture<RewardUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let rewardService: RewardService;
    let iconService: IconService;
    let companyService: CompanyService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [RewardUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(RewardUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RewardUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      rewardService = TestBed.inject(RewardService);
      iconService = TestBed.inject(IconService);
      companyService = TestBed.inject(CompanyService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Icon query and add missing value', () => {
        const reward: IReward = { id: 456 };
        const icon: IIcon = { id: 25993 };
        reward.icon = icon;

        const iconCollection: IIcon[] = [{ id: 20134 }];
        jest.spyOn(iconService, 'query').mockReturnValue(of(new HttpResponse({ body: iconCollection })));
        const additionalIcons = [icon];
        const expectedCollection: IIcon[] = [...additionalIcons, ...iconCollection];
        jest.spyOn(iconService, 'addIconToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ reward });
        comp.ngOnInit();

        expect(iconService.query).toHaveBeenCalled();
        expect(iconService.addIconToCollectionIfMissing).toHaveBeenCalledWith(iconCollection, ...additionalIcons);
        expect(comp.iconsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Company query and add missing value', () => {
        const reward: IReward = { id: 456 };
        const company: ICompany = { id: 4363 };
        reward.company = company;

        const companyCollection: ICompany[] = [{ id: 12061 }];
        jest.spyOn(companyService, 'query').mockReturnValue(of(new HttpResponse({ body: companyCollection })));
        const additionalCompanies = [company];
        const expectedCollection: ICompany[] = [...additionalCompanies, ...companyCollection];
        jest.spyOn(companyService, 'addCompanyToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ reward });
        comp.ngOnInit();

        expect(companyService.query).toHaveBeenCalled();
        expect(companyService.addCompanyToCollectionIfMissing).toHaveBeenCalledWith(companyCollection, ...additionalCompanies);
        expect(comp.companiesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const reward: IReward = { id: 456 };
        const icon: IIcon = { id: 18380 };
        reward.icon = icon;
        const company: ICompany = { id: 91576 };
        reward.company = company;

        activatedRoute.data = of({ reward });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(reward));
        expect(comp.iconsSharedCollection).toContain(icon);
        expect(comp.companiesSharedCollection).toContain(company);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Reward>>();
        const reward = { id: 123 };
        jest.spyOn(rewardService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ reward });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: reward }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(rewardService.update).toHaveBeenCalledWith(reward);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Reward>>();
        const reward = new Reward();
        jest.spyOn(rewardService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ reward });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: reward }));
        saveSubject.complete();

        // THEN
        expect(rewardService.create).toHaveBeenCalledWith(reward);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Reward>>();
        const reward = { id: 123 };
        jest.spyOn(rewardService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ reward });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(rewardService.update).toHaveBeenCalledWith(reward);
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

      describe('trackCompanyById', () => {
        it('Should return tracked Company primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCompanyById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
