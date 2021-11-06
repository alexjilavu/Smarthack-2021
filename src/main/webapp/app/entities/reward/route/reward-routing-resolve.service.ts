import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReward, Reward } from '../reward.model';
import { RewardService } from '../service/reward.service';

@Injectable({ providedIn: 'root' })
export class RewardRoutingResolveService implements Resolve<IReward> {
  constructor(protected service: RewardService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IReward> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((reward: HttpResponse<Reward>) => {
          if (reward.body) {
            return of(reward.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Reward());
  }
}
