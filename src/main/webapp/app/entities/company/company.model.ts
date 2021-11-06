import { IReward } from 'app/entities/reward/reward.model';

export interface ICompany {
  id?: number;
  name?: string | null;
  rewards?: IReward[] | null;
}

export class Company implements ICompany {
  constructor(public id?: number, public name?: string | null, public rewards?: IReward[] | null) {}
}

export function getCompanyIdentifier(company: ICompany): number | undefined {
  return company.id;
}
