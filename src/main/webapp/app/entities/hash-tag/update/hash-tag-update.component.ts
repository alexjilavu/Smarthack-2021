import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IHashTag, HashTag } from '../hash-tag.model';
import { HashTagService } from '../service/hash-tag.service';

@Component({
  selector: 'jhi-hash-tag-update',
  templateUrl: './hash-tag-update.component.html',
})
export class HashTagUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    company: [],
  });

  constructor(protected hashTagService: HashTagService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ hashTag }) => {
      this.updateForm(hashTag);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const hashTag = this.createFromForm();
    if (hashTag.id !== undefined) {
      this.subscribeToSaveResponse(this.hashTagService.update(hashTag));
    } else {
      this.subscribeToSaveResponse(this.hashTagService.create(hashTag));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHashTag>>): void {
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

  protected updateForm(hashTag: IHashTag): void {
    this.editForm.patchValue({
      id: hashTag.id,
      name: hashTag.name,
      company: hashTag.company,
    });
  }

  protected createFromForm(): IHashTag {
    return {
      ...new HashTag(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      company: this.editForm.get(['company'])!.value,
    };
  }
}
