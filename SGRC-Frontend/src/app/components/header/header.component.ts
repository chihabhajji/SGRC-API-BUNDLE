import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user/user.service';
import { NotificationService } from '../../services/notification/notification.service';
import { SharedService } from '../../services/shared.service';
import { Router } from '@angular/router';
import { ResponseApi } from './../../model/response-api';
import { User } from '../../model/user';
@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  Notifications = [];
  message: {};
  classCss: {};

  public shared: SharedService;

  constructor(private userService: UserService,
              private router: Router,
              private notificationService: NotificationService) {
    this.shared = SharedService.getInstance();
    this.shared.user = new User('', '', '', '','',false,false,false);
  }

  ngOnInit() {
  }
  
  signOut(): void {
    this.shared.token = null;
    this.shared.user = null;
    sessionStorage.clear();
    localStorage.clear();
    window.location.href = '/login';
    window.location.reload();
  }

  findAllNotifications(userId:String) {
    this.notificationService.findAll(userId).subscribe((responseApi: ResponseApi) => {
      this.Notifications = responseApi.data;
    }, err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
    });
  }

  private showMessage(message: { type: string, text: string }): void {
    this.message = message;
    this.buildClasses(message.type);
    setTimeout(() => {
      this.message = undefined;
    }, 3000);
  }
  private buildClasses(type: string): void {
    this.classCss = {
      'alert': true
    };
    this.classCss['alert-' + type] = true;
  }
  
  detail(id: string) {
    this.router.navigate(['/ticket-detail', id]);
  }
}
