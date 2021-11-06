import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { HashTagComponent } from './list/hash-tag.component';
import { HashTagDetailComponent } from './detail/hash-tag-detail.component';
import { HashTagUpdateComponent } from './update/hash-tag-update.component';
import { HashTagDeleteDialogComponent } from './delete/hash-tag-delete-dialog.component';
import { HashTagRoutingModule } from './route/hash-tag-routing.module';

@NgModule({
  imports: [SharedModule, HashTagRoutingModule],
  declarations: [HashTagComponent, HashTagDetailComponent, HashTagUpdateComponent, HashTagDeleteDialogComponent],
  entryComponents: [HashTagDeleteDialogComponent],
})
export class HashTagModule {}
