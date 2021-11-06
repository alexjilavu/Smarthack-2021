import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IIcon } from '../icon.model';

@Component({
  selector: 'jhi-icon-detail',
  templateUrl: './icon-detail.component.html',
})
export class IconDetailComponent implements OnInit {
  icon: IIcon | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ icon }) => {
      this.icon = icon;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
