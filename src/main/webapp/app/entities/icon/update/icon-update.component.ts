import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IIcon, Icon } from '../icon.model';
import { IconService } from '../service/icon.service';

@Component({
  selector: 'jhi-icon-update',
  templateUrl: './icon-update.component.html',
})
export class IconUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    url: [],
  });

  constructor(protected iconService: IconService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ icon }) => {
      this.updateForm(icon);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const icon = this.createFromForm();
    if (icon.id !== undefined) {
      this.subscribeToSaveResponse(this.iconService.update(icon));
    } else {
      this.subscribeToSaveResponse(this.iconService.create(icon));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IIcon>>): void {
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

  protected updateForm(icon: IIcon): void {
    this.editForm.patchValue({
      id: icon.id,
      name: icon.name,
      url: icon.url,
    });
  }

  protected createFromForm(): IIcon {
    return {
      ...new Icon(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      url: this.editForm.get(['url'])!.value,
    };
  }
}
