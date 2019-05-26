import { User } from './user';
import { Change } from './Change';
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
    public date : Date,
    public changes: Array<Change>,
    public reminders: Array<String>,
    public message : String,
    public deleted : Boolean ,
    public reminded : Boolean,
    public archived : Boolean,
    public overdue : Boolean,
    public flagged : Boolean,
    public changesEmpty : Boolean,
    public remindersEmpty : Boolean,
    public rating : Number
  ) {}
}
