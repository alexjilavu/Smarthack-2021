import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IChallenge, Challenge } from '../challenge.model';
import { ChallengeService } from '../service/challenge.service';
import { IIcon } from 'app/entities/icon/icon.model';
import { IconService } from 'app/entities/icon/service/icon.service';
import { IHashTag } from 'app/entities/hash-tag/hash-tag.model';
import { HashTagService } from 'app/entities/hash-tag/service/hash-tag.service';

@Component({
  selector: 'jhi-challenge-update',
  templateUrl: './challenge-update.component.html',
})
export class ChallengeUpdateComponent implements OnInit {
  isSaving = false;

  iconsSharedCollection: IIcon[] = [];
  hashTagsSharedCollection: IHashTag[] = [];

  editForm = this.fb.group({
    id: [],
    title: [],
    message: [],
    iconUrl: [],
    rewardAmount: [],
    requiredTags: [],
    icon: [],
    hashTags: [],
  });

  constructor(
    protected challengeService: ChallengeService,
    protected iconService: IconService,
    protected hashTagService: HashTagService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ challenge }) => {
      this.updateForm(challenge);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const challenge = this.createFromForm();
    if (challenge.id !== undefined) {
      this.subscribeToSaveResponse(this.challengeService.update(challenge));
    } else {
      this.subscribeToSaveResponse(this.challengeService.create(challenge));
    }
  }

  trackIconById(index: number, item: IIcon): number {
    return item.id!;
  }

  trackHashTagById(index: number, item: IHashTag): number {
    return item.id!;
  }

  getSelectedHashTag(option: IHashTag, selectedVals?: IHashTag[]): IHashTag {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IChallenge>>): void {
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

  protected updateForm(challenge: IChallenge): void {
    this.editForm.patchValue({
      id: challenge.id,
      title: challenge.title,
      message: challenge.message,
      iconUrl: challenge.iconUrl,
      rewardAmount: challenge.rewardAmount,
      requiredTags: challenge.requiredTags,
      icon: challenge.icon,
      hashTags: challenge.hashTags,
    });

    this.iconsSharedCollection = this.iconService.addIconToCollectionIfMissing(this.iconsSharedCollection, challenge.icon);
    this.hashTagsSharedCollection = this.hashTagService.addHashTagToCollectionIfMissing(
      this.hashTagsSharedCollection,
      ...(challenge.hashTags ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.iconService
      .query()
      .pipe(map((res: HttpResponse<IIcon[]>) => res.body ?? []))
      .pipe(map((icons: IIcon[]) => this.iconService.addIconToCollectionIfMissing(icons, this.editForm.get('icon')!.value)))
      .subscribe((icons: IIcon[]) => (this.iconsSharedCollection = icons));

    this.hashTagService
      .query()
      .pipe(map((res: HttpResponse<IHashTag[]>) => res.body ?? []))
      .pipe(
        map((hashTags: IHashTag[]) =>
          this.hashTagService.addHashTagToCollectionIfMissing(hashTags, ...(this.editForm.get('hashTags')!.value ?? []))
        )
      )
      .subscribe((hashTags: IHashTag[]) => (this.hashTagsSharedCollection = hashTags));
  }

  protected createFromForm(): IChallenge {
    return {
      ...new Challenge(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      message: this.editForm.get(['message'])!.value,
      iconUrl: this.editForm.get(['iconUrl'])!.value,
      rewardAmount: this.editForm.get(['rewardAmount'])!.value,
      requiredTags: this.editForm.get(['requiredTags'])!.value,
      icon: this.editForm.get(['icon'])!.value,
      hashTags: this.editForm.get(['hashTags'])!.value,
    };
  }
}
