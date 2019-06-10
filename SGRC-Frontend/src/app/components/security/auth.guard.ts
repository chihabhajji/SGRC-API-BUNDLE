import { routes } from './../../app.routes';
import { SharedService } from './../../services/shared.service';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { UserService } from './../../services/user/user.service';
import { CurrentUser } from './../../model/currentUser';

@Injectable()
export class AuthGuard implements CanActivate {

  public shared: SharedService;
  userService: UserService;

  constructor(private router: Router) {
    this.shared = SharedService.getInstance();
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
    if (this.shared.isLoggedIn()) {
      if (!this.shared.user) {
        this.shared.showTemplate.emit(true);

        this.userService.refresh().subscribe((userAuthentication: CurrentUser) => {
          this.shared.token = userAuthentication.token;
          this.shared.user = userAuthentication.user;
          this.shared.user.notifications = userAuthentication.notifications;
          this.shared.user.profile = this.shared.user.profile.substring(5);
          sessionStorage.setItem("token", userAuthentication.token.toString());
        });
        
      }
      if (this.shared.user.isDue == true){
        this.router.navigate(['/changepassword']);
        return false; 
      }
      return true;
    }
    this.router.navigate(['/login']);
    return false;
  }
}
