import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IHashTag } from '../hash-tag.model';
import { HashTagService } from '../service/hash-tag.service';

@Component({
  templateUrl: './hash-tag-delete-dialog.component.html',
})
export class HashTagDeleteDialogComponent {
  hashTag?: IHashTag;

  constructor(protected hashTagService: HashTagService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.hashTagService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
