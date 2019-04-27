import { User } from './user';
export class Ticket {
  constructor (
    public id: String,
    public number: Number,
    public title: String,
    public status: String,
    public priority: String,
    public image: String,
    public user: User,
    public assignedUser: User,
    public data: string,
    public changes: Array<String>
  ) {}
}
