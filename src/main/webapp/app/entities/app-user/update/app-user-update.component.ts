import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IAppUser, AppUser } from '../app-user.model';
import { AppUserService } from '../service/app-user.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IChallenge } from 'app/entities/challenge/challenge.model';
import { ChallengeService } from 'app/entities/challenge/service/challenge.service';

@Component({
  selector: 'jhi-app-user-update',
  templateUrl: './app-user-update.component.html',
})
export class AppUserUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  challengesSharedCollection: IChallenge[] = [];

  editForm = this.fb.group({
    id: [],
    walletAddress: [],
    walletPassword: [],
    appUser: [],
    completedChallenges: [],
  });

  constructor(
    protected appUserService: AppUserService,
    protected userService: UserService,
    protected challengeService: ChallengeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appUser }) => {
      this.updateForm(appUser);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appUser = this.createFromForm();
    if (appUser.id !== undefined) {
      this.subscribeToSaveResponse(this.appUserService.update(appUser));
    } else {
      this.subscribeToSaveResponse(this.appUserService.create(appUser));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackChallengeById(index: number, item: IChallenge): number {
    return item.id!;
  }

  getSelectedChallenge(option: IChallenge, selectedVals?: IChallenge[]): IChallenge {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppUser>>): void {
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

  protected updateForm(appUser: IAppUser): void {
    this.editForm.patchValue({
      id: appUser.id,
      walletAddress: appUser.walletAddress,
      walletPassword: appUser.walletPassword,
      appUser: appUser.appUser,
      completedChallenges: appUser.completedChallenges,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, appUser.appUser);
    this.challengesSharedCollection = this.challengeService.addChallengeToCollectionIfMissing(
      this.challengesSharedCollection,
      ...(appUser.completedChallenges ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('appUser')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.challengeService
      .query()
      .pipe(map((res: HttpResponse<IChallenge[]>) => res.body ?? []))
      .pipe(
        map((challenges: IChallenge[]) =>
          this.challengeService.addChallengeToCollectionIfMissing(challenges, ...(this.editForm.get('completedChallenges')!.value ?? []))
        )
      )
      .subscribe((challenges: IChallenge[]) => (this.challengesSharedCollection = challenges));
  }

  protected createFromForm(): IAppUser {
    return {
      ...new AppUser(),
      id: this.editForm.get(['id'])!.value,
      walletAddress: this.editForm.get(['walletAddress'])!.value,
      walletPassword: this.editForm.get(['walletPassword'])!.value,
      appUser: this.editForm.get(['appUser'])!.value,
      completedChallenges: this.editForm.get(['completedChallenges'])!.value,
    };
  }
}
