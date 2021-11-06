import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IChallenge } from '../challenge.model';
import { ChallengeService } from '../service/challenge.service';
import { ChallengeDeleteDialogComponent } from '../delete/challenge-delete-dialog.component';

@Component({
  selector: 'jhi-challenge',
  templateUrl: './challenge.component.html',
})
export class ChallengeComponent implements OnInit {
  challenges?: IChallenge[];
  isLoading = false;

  constructor(protected challengeService: ChallengeService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.challengeService.query().subscribe(
      (res: HttpResponse<IChallenge[]>) => {
        this.isLoading = false;
        this.challenges = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IChallenge): number {
    return item.id!;
  }

  delete(challenge: IChallenge): void {
    const modalRef = this.modalService.open(ChallengeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.challenge = challenge;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
