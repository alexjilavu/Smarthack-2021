import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IIcon } from '../icon.model';
import { IconService } from '../service/icon.service';

@Component({
  templateUrl: './icon-delete-dialog.component.html',
})
export class IconDeleteDialogComponent {
  icon?: IIcon;

  constructor(protected iconService: IconService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.iconService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
