import { IIcon } from 'app/entities/icon/icon.model';
import { IHashTag } from 'app/entities/hash-tag/hash-tag.model';
import { IAppUser } from 'app/entities/app-user/app-user.model';

export interface IChallenge {
  id?: number;
  title?: string | null;
  message?: string | null;
  iconUrl?: string | null;
  rewardAmount?: number | null;
  icon?: IIcon | null;
  hashTags?: IHashTag[] | null;
  usersThatCompleteds?: IAppUser[] | null;
}

export class Challenge implements IChallenge {
  constructor(
    public id?: number,
    public title?: string | null,
    public message?: string | null,
    public iconUrl?: string | null,
    public rewardAmount?: number | null,
    public icon?: IIcon | null,
    public hashTags?: IHashTag[] | null,
    public usersThatCompleteds?: IAppUser[] | null
  ) {}
}

export function getChallengeIdentifier(challenge: IChallenge): number | undefined {
  return challenge.id;
}
