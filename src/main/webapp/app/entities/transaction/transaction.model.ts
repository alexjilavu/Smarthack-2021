export interface ITransaction {
  id?: number;
  senderAddress?: string | null;
  receiverAddress?: string | null;
  amount?: number | null;
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public senderAddress?: string | null,
    public receiverAddress?: string | null,
    public amount?: number | null
  ) {}
}

export function getTransactionIdentifier(transaction: ITransaction): number | undefined {
  return transaction.id;
}
