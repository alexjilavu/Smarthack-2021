import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IIcon, getIconIdentifier } from '../icon.model';

export type EntityResponseType = HttpResponse<IIcon>;
export type EntityArrayResponseType = HttpResponse<IIcon[]>;

@Injectable({ providedIn: 'root' })
export class IconService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/icons');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(icon: IIcon): Observable<EntityResponseType> {
    return this.http.post<IIcon>(this.resourceUrl, icon, { observe: 'response' });
  }

  update(icon: IIcon): Observable<EntityResponseType> {
    return this.http.put<IIcon>(`${this.resourceUrl}/${getIconIdentifier(icon) as number}`, icon, { observe: 'response' });
  }

  partialUpdate(icon: IIcon): Observable<EntityResponseType> {
    return this.http.patch<IIcon>(`${this.resourceUrl}/${getIconIdentifier(icon) as number}`, icon, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IIcon>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IIcon[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addIconToCollectionIfMissing(iconCollection: IIcon[], ...iconsToCheck: (IIcon | null | undefined)[]): IIcon[] {
    const icons: IIcon[] = iconsToCheck.filter(isPresent);
    if (icons.length > 0) {
      const iconCollectionIdentifiers = iconCollection.map(iconItem => getIconIdentifier(iconItem)!);
      const iconsToAdd = icons.filter(iconItem => {
        const iconIdentifier = getIconIdentifier(iconItem);
        if (iconIdentifier == null || iconCollectionIdentifiers.includes(iconIdentifier)) {
          return false;
        }
        iconCollectionIdentifiers.push(iconIdentifier);
        return true;
      });
      return [...iconsToAdd, ...iconCollection];
    }
    return iconCollection;
  }
}
