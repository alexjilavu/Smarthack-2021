export interface IPost {
  id?: number;
  content?: string | null;
  imageUrlContentType?: string | null;
  imageUrl?: string | null;
  publishedBy?: string | null;
  noOfLikes?: number | null;
  noOfShares?: number | null;
}

export class Post implements IPost {
  constructor(
    public id?: number,
    public content?: string | null,
    public imageUrlContentType?: string | null,
    public imageUrl?: string | null,
    public publishedBy?: string | null,
    public noOfLikes?: number | null,
    public noOfShares?: number | null
  ) {}
}

export function getPostIdentifier(post: IPost): number | undefined {
  return post.id;
}
