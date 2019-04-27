import { Component } from '@angular/core';
import { SharedService } from './services/shared.service';
import { UserService } from './services/user/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  showTemplate: Boolean = false;
  public shared: SharedService;

  constructor(private userService: UserService) {
    this.shared = SharedService.getInstance();
  }

  // tslint:disable-next-line:use-life-cycle-interface
  ngOnInit() {
    this.shared.showTemplate.subscribe(
      show => this.showTemplate = show
    );
  }

  showContentWrapper() {
    return {
      'content-wrapper': this.shared.isLoggedIn()
    };
  }
}
