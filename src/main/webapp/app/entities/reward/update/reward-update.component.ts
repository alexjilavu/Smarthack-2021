import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IReward, Reward } from '../reward.model';
import { RewardService } from '../service/reward.service';
import { IIcon } from 'app/entities/icon/icon.model';
import { IconService } from 'app/entities/icon/service/icon.service';
import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';

@Component({
  selector: 'jhi-reward-update',
  templateUrl: './reward-update.component.html',
})
export class RewardUpdateComponent implements OnInit {
  isSaving = false;

  iconsSharedCollection: IIcon[] = [];
  companiesSharedCollection: ICompany[] = [];

  editForm = this.fb.group({
    id: [],
    value: [],
    content: [],
    icon: [],
    company: [],
  });

  constructor(
    protected rewardService: RewardService,
    protected iconService: IconService,
    protected companyService: CompanyService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reward }) => {
      this.updateForm(reward);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reward = this.createFromForm();
    if (reward.id !== undefined) {
      this.subscribeToSaveResponse(this.rewardService.update(reward));
    } else {
      this.subscribeToSaveResponse(this.rewardService.create(reward));
    }
  }

  trackIconById(index: number, item: IIcon): number {
    return item.id!;
  }

  trackCompanyById(index: number, item: ICompany): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReward>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(reward: IReward): void {
    this.editForm.patchValue({
      id: reward.id,
      value: reward.value,
      content: reward.content,
      icon: reward.icon,
      company: reward.company,
    });

    this.iconsSharedCollection = this.iconService.addIconToCollectionIfMissing(this.iconsSharedCollection, reward.icon);
    this.companiesSharedCollection = this.companyService.addCompanyToCollectionIfMissing(this.companiesSharedCollection, reward.company);
  }

  protected loadRelationshipsOptions(): void {
    this.iconService
      .query()
      .pipe(map((res: HttpResponse<IIcon[]>) => res.body ?? []))
      .pipe(map((icons: IIcon[]) => this.iconService.addIconToCollectionIfMissing(icons, this.editForm.get('icon')!.value)))
      .subscribe((icons: IIcon[]) => (this.iconsSharedCollection = icons));

    this.companyService
      .query()
      .pipe(map((res: HttpResponse<ICompany[]>) => res.body ?? []))
      .pipe(
        map((companies: ICompany[]) => this.companyService.addCompanyToCollectionIfMissing(companies, this.editForm.get('company')!.value))
      )
      .subscribe((companies: ICompany[]) => (this.companiesSharedCollection = companies));
  }

  protected createFromForm(): IReward {
    return {
      ...new Reward(),
      id: this.editForm.get(['id'])!.value,
      value: this.editForm.get(['value'])!.value,
      content: this.editForm.get(['content'])!.value,
      icon: this.editForm.get(['icon'])!.value,
      company: this.editForm.get(['company'])!.value,
    };
  }
}
