import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHashTag, HashTag } from '../hash-tag.model';
import { HashTagService } from '../service/hash-tag.service';

@Injectable({ providedIn: 'root' })
export class HashTagRoutingResolveService implements Resolve<IHashTag> {
  constructor(protected service: HashTagService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IHashTag> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((hashTag: HttpResponse<HashTag>) => {
          if (hashTag.body) {
            return of(hashTag.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new HashTag());
  }
}
