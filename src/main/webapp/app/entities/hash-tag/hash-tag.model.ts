import { IChallenge } from 'app/entities/challenge/challenge.model';

export interface IHashTag {
  id?: number;
  name?: string | null;
  company?: string | null;
  challenges?: IChallenge[] | null;
}

export class HashTag implements IHashTag {
  constructor(public id?: number, public name?: string | null, public company?: string | null, public challenges?: IChallenge[] | null) {}
}

export function getHashTagIdentifier(hashTag: IHashTag): number | undefined {
  return hashTag.id;
}
