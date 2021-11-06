import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RewardComponent } from './list/reward.component';
import { RewardDetailComponent } from './detail/reward-detail.component';
import { RewardUpdateComponent } from './update/reward-update.component';
import { RewardDeleteDialogComponent } from './delete/reward-delete-dialog.component';
import { RewardRoutingModule } from './route/reward-routing.module';

@NgModule({
  imports: [SharedModule, RewardRoutingModule],
  declarations: [RewardComponent, RewardDetailComponent, RewardUpdateComponent, RewardDeleteDialogComponent],
  entryComponents: [RewardDeleteDialogComponent],
})
export class RewardModule {}
