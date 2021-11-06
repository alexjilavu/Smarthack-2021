import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IChallenge } from '../challenge.model';
import { ChallengeService } from '../service/challenge.service';

@Component({
  templateUrl: './challenge-delete-dialog.component.html',
})
export class ChallengeDeleteDialogComponent {
  challenge?: IChallenge;

  constructor(protected challengeService: ChallengeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.challengeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
