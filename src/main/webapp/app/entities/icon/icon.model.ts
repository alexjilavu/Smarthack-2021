export interface IIcon {
  id?: number;
  name?: string | null;
  url?: string | null;
}

export class Icon implements IIcon {
  constructor(public id?: number, public name?: string | null, public url?: string | null) {}
}

export function getIconIdentifier(icon: IIcon): number | undefined {
  return icon.id;
}
