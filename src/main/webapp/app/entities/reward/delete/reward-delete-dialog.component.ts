import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IReward } from '../reward.model';
import { RewardService } from '../service/reward.service';

@Component({
  templateUrl: './reward-delete-dialog.component.html',
})
export class RewardDeleteDialogComponent {
  reward?: IReward;

  constructor(protected rewardService: RewardService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.rewardService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
