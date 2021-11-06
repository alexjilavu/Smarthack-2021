import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { HashTagComponent } from '../list/hash-tag.component';
import { HashTagDetailComponent } from '../detail/hash-tag-detail.component';
import { HashTagUpdateComponent } from '../update/hash-tag-update.component';
import { HashTagRoutingResolveService } from './hash-tag-routing-resolve.service';

const hashTagRoute: Routes = [
  {
    path: '',
    component: HashTagComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: HashTagDetailComponent,
    resolve: {
      hashTag: HashTagRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: HashTagUpdateComponent,
    resolve: {
      hashTag: HashTagRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: HashTagUpdateComponent,
    resolve: {
      hashTag: HashTagRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(hashTagRoute)],
  exports: [RouterModule],
})
export class HashTagRoutingModule {}
