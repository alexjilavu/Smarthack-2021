import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ChallengeComponent } from './list/challenge.component';
import { ChallengeDetailComponent } from './detail/challenge-detail.component';
import { ChallengeUpdateComponent } from './update/challenge-update.component';
import { ChallengeDeleteDialogComponent } from './delete/challenge-delete-dialog.component';
import { ChallengeRoutingModule } from './route/challenge-routing.module';

@NgModule({
  imports: [SharedModule, ChallengeRoutingModule],
  declarations: [ChallengeComponent, ChallengeDetailComponent, ChallengeUpdateComponent, ChallengeDeleteDialogComponent],
  entryComponents: [ChallengeDeleteDialogComponent],
})
export class ChallengeModule {}
