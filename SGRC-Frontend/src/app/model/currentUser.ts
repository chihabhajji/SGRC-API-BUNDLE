import { User } from './user';

export class CurrentUser {
  public token: String;
  public user: User;
  public notifications: Array<Notification>;
}
