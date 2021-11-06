import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IIcon } from '../icon.model';
import { IconService } from '../service/icon.service';
import { IconDeleteDialogComponent } from '../delete/icon-delete-dialog.component';

@Component({
  selector: 'jhi-icon',
  templateUrl: './icon.component.html',
})
export class IconComponent implements OnInit {
  icons?: IIcon[];
  isLoading = false;

  constructor(protected iconService: IconService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.iconService.query().subscribe(
      (res: HttpResponse<IIcon[]>) => {
        this.isLoading = false;
        this.icons = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IIcon): number {
    return item.id!;
  }

  delete(icon: IIcon): void {
    const modalRef = this.modalService.open(IconDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.icon = icon;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
