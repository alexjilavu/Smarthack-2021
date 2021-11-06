import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IIcon, Icon } from '../icon.model';
import { IconService } from '../service/icon.service';

@Injectable({ providedIn: 'root' })
export class IconRoutingResolveService implements Resolve<IIcon> {
  constructor(protected service: IconService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IIcon> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((icon: HttpResponse<Icon>) => {
          if (icon.body) {
            return of(icon.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Icon());
  }
}
