export class User {
  constructor (
    public id: String,
    public name: String,
    public email: String,
    public password: String,
    public profile: String,
    public isDue: Boolean,
    public isActive : Boolean,
    public feedbackDue : Boolean,
    public notifications: Array<Notification>
  ) {}
}
