import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReward, getRewardIdentifier } from '../reward.model';

export type EntityResponseType = HttpResponse<IReward>;
export type EntityArrayResponseType = HttpResponse<IReward[]>;

@Injectable({ providedIn: 'root' })
export class RewardService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/rewards');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(reward: IReward): Observable<EntityResponseType> {
    return this.http.post<IReward>(this.resourceUrl, reward, { observe: 'response' });
  }

  update(reward: IReward): Observable<EntityResponseType> {
    return this.http.put<IReward>(`${this.resourceUrl}/${getRewardIdentifier(reward) as number}`, reward, { observe: 'response' });
  }

  partialUpdate(reward: IReward): Observable<EntityResponseType> {
    return this.http.patch<IReward>(`${this.resourceUrl}/${getRewardIdentifier(reward) as number}`, reward, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IReward>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IReward[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRewardToCollectionIfMissing(rewardCollection: IReward[], ...rewardsToCheck: (IReward | null | undefined)[]): IReward[] {
    const rewards: IReward[] = rewardsToCheck.filter(isPresent);
    if (rewards.length > 0) {
      const rewardCollectionIdentifiers = rewardCollection.map(rewardItem => getRewardIdentifier(rewardItem)!);
      const rewardsToAdd = rewards.filter(rewardItem => {
        const rewardIdentifier = getRewardIdentifier(rewardItem);
        if (rewardIdentifier == null || rewardCollectionIdentifiers.includes(rewardIdentifier)) {
          return false;
        }
        rewardCollectionIdentifiers.push(rewardIdentifier);
        return true;
      });
      return [...rewardsToAdd, ...rewardCollection];
    }
    return rewardCollection;
  }
}
