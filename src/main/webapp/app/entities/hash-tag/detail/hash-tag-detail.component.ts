import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IHashTag } from '../hash-tag.model';

@Component({
  selector: 'jhi-hash-tag-detail',
  templateUrl: './hash-tag-detail.component.html',
})
export class HashTagDetailComponent implements OnInit {
  hashTag: IHashTag | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ hashTag }) => {
      this.hashTag = hashTag;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
