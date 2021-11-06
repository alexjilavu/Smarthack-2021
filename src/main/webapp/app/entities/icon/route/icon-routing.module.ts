import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { IconComponent } from '../list/icon.component';
import { IconDetailComponent } from '../detail/icon-detail.component';
import { IconUpdateComponent } from '../update/icon-update.component';
import { IconRoutingResolveService } from './icon-routing-resolve.service';

const iconRoute: Routes = [
  {
    path: '',
    component: IconComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: IconDetailComponent,
    resolve: {
      icon: IconRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: IconUpdateComponent,
    resolve: {
      icon: IconRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: IconUpdateComponent,
    resolve: {
      icon: IconRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(iconRoute)],
  exports: [RouterModule],
})
export class IconRoutingModule {}
