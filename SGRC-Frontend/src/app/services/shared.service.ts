import { Injectable, EventEmitter } from '@angular/core';
import { User } from '../model/user';

@Injectable()
export class SharedService {

  public static instance: SharedService = null;
  user: User;
  token: String;
  notifications: Array<Notification>;
  
  showTemplate = new EventEmitter<Boolean>();

  constructor() {
    return SharedService.instance = SharedService.instance || this;
  }

  public static getInstance() {
    if (this.instance == null) {
      this.instance = new SharedService();
    }
    return this.instance;
  }

  isLoggedIn(): Boolean {
    if (!sessionStorage.getItem("token")) {
      console.log("false");
      return false;
    }
    console.log("alt")
    return true;
  }
}
