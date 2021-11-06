import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IReward } from '../reward.model';

@Component({
  selector: 'jhi-reward-detail',
  templateUrl: './reward-detail.component.html',
})
export class RewardDetailComponent implements OnInit {
  reward: IReward | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reward }) => {
      this.reward = reward;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
