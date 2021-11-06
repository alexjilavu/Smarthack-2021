import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IReward } from '../reward.model';
import { RewardService } from '../service/reward.service';
import { RewardDeleteDialogComponent } from '../delete/reward-delete-dialog.component';

@Component({
  selector: 'jhi-reward',
  templateUrl: './reward.component.html',
})
export class RewardComponent implements OnInit {
  rewards?: IReward[];
  isLoading = false;

  constructor(protected rewardService: RewardService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.rewardService.query().subscribe(
      (res: HttpResponse<IReward[]>) => {
        this.isLoading = false;
        this.rewards = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IReward): number {
    return item.id!;
  }

  delete(reward: IReward): void {
    const modalRef = this.modalService.open(RewardDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.reward = reward;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
