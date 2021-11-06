import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IChallenge, Challenge } from '../challenge.model';
import { ChallengeService } from '../service/challenge.service';

@Injectable({ providedIn: 'root' })
export class ChallengeRoutingResolveService implements Resolve<IChallenge> {
  constructor(protected service: ChallengeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IChallenge> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((challenge: HttpResponse<Challenge>) => {
          if (challenge.body) {
            return of(challenge.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Challenge());
  }
}
