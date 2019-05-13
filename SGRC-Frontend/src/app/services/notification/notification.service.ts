import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HELP_DESK_API } from './../helpdesk.api';
@Injectable()
export class NotificationService {

  constructor(private http: HttpClient) { }

  findAll(userId : String) {
    return this.http.get(`${HELP_DESK_API}/api/notifications/${userId}`);
  }
}

