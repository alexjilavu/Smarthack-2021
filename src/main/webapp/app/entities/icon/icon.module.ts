import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { IconComponent } from './list/icon.component';
import { IconDetailComponent } from './detail/icon-detail.component';
import { IconUpdateComponent } from './update/icon-update.component';
import { IconDeleteDialogComponent } from './delete/icon-delete-dialog.component';
import { IconRoutingModule } from './route/icon-routing.module';

@NgModule({
  imports: [SharedModule, IconRoutingModule],
  declarations: [IconComponent, IconDetailComponent, IconUpdateComponent, IconDeleteDialogComponent],
  entryComponents: [IconDeleteDialogComponent],
})
export class IconModule {}
