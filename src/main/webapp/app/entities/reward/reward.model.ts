import { IIcon } from 'app/entities/icon/icon.model';
import { ICompany } from 'app/entities/company/company.model';

export interface IReward {
  id?: number;
  value?: number | null;
  content?: string | null;
  icon?: IIcon | null;
  company?: ICompany | null;
}

export class Reward implements IReward {
  constructor(
    public id?: number,
    public value?: number | null,
    public content?: string | null,
    public icon?: IIcon | null,
    public company?: ICompany | null
  ) {}
}

export function getRewardIdentifier(reward: IReward): number | undefined {
  return reward.id;
}
