import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IHashTag, getHashTagIdentifier } from '../hash-tag.model';

export type EntityResponseType = HttpResponse<IHashTag>;
export type EntityArrayResponseType = HttpResponse<IHashTag[]>;

@Injectable({ providedIn: 'root' })
export class HashTagService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/hash-tags');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(hashTag: IHashTag): Observable<EntityResponseType> {
    return this.http.post<IHashTag>(this.resourceUrl, hashTag, { observe: 'response' });
  }

  update(hashTag: IHashTag): Observable<EntityResponseType> {
    return this.http.put<IHashTag>(`${this.resourceUrl}/${getHashTagIdentifier(hashTag) as number}`, hashTag, { observe: 'response' });
  }

  partialUpdate(hashTag: IHashTag): Observable<EntityResponseType> {
    return this.http.patch<IHashTag>(`${this.resourceUrl}/${getHashTagIdentifier(hashTag) as number}`, hashTag, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IHashTag>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IHashTag[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addHashTagToCollectionIfMissing(hashTagCollection: IHashTag[], ...hashTagsToCheck: (IHashTag | null | undefined)[]): IHashTag[] {
    const hashTags: IHashTag[] = hashTagsToCheck.filter(isPresent);
    if (hashTags.length > 0) {
      const hashTagCollectionIdentifiers = hashTagCollection.map(hashTagItem => getHashTagIdentifier(hashTagItem)!);
      const hashTagsToAdd = hashTags.filter(hashTagItem => {
        const hashTagIdentifier = getHashTagIdentifier(hashTagItem);
        if (hashTagIdentifier == null || hashTagCollectionIdentifiers.includes(hashTagIdentifier)) {
          return false;
        }
        hashTagCollectionIdentifiers.push(hashTagIdentifier);
        return true;
      });
      return [...hashTagsToAdd, ...hashTagCollection];
    }
    return hashTagCollection;
  }
}
