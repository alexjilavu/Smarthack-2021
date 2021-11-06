import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ChallengeComponent } from '../list/challenge.component';
import { ChallengeDetailComponent } from '../detail/challenge-detail.component';
import { ChallengeUpdateComponent } from '../update/challenge-update.component';
import { ChallengeRoutingResolveService } from './challenge-routing-resolve.service';

const challengeRoute: Routes = [
  {
    path: '',
    component: ChallengeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ChallengeDetailComponent,
    resolve: {
      challenge: ChallengeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ChallengeUpdateComponent,
    resolve: {
      challenge: ChallengeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ChallengeUpdateComponent,
    resolve: {
      challenge: ChallengeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(challengeRoute)],
  exports: [RouterModule],
})
export class ChallengeRoutingModule {}
