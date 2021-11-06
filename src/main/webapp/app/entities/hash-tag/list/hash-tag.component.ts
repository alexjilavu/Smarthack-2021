import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IHashTag } from '../hash-tag.model';
import { HashTagService } from '../service/hash-tag.service';
import { HashTagDeleteDialogComponent } from '../delete/hash-tag-delete-dialog.component';

@Component({
  selector: 'jhi-hash-tag',
  templateUrl: './hash-tag.component.html',
})
export class HashTagComponent implements OnInit {
  hashTags?: IHashTag[];
  isLoading = false;

  constructor(protected hashTagService: HashTagService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.hashTagService.query().subscribe(
      (res: HttpResponse<IHashTag[]>) => {
        this.isLoading = false;
        this.hashTags = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IHashTag): number {
    return item.id!;
  }

  delete(hashTag: IHashTag): void {
    const modalRef = this.modalService.open(HashTagDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.hashTag = hashTag;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
