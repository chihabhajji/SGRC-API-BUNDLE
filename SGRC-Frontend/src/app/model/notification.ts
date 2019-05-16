export class Notification {
  constructor(
    public ticketId: String,
    public message: String,
    public createdAt : Date,
    public isRead: Boolean){}
}
