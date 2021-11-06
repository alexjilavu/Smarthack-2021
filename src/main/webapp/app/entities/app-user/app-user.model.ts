import { IUser } from 'app/entities/user/user.model';
import { IChallenge } from 'app/entities/challenge/challenge.model';

export interface IAppUser {
  id?: number;
  walletAddress?: string | null;
  walletPassword?: string | null;
  appUser?: IUser | null;
  completedChallenges?: IChallenge[] | null;
}

export class AppUser implements IAppUser {
  constructor(
    public id?: number,
    public walletAddress?: string | null,
    public walletPassword?: string | null,
    public appUser?: IUser | null,
    public completedChallenges?: IChallenge[] | null
  ) {}
}

export function getAppUserIdentifier(appUser: IAppUser): number | undefined {
  return appUser.id;
}
