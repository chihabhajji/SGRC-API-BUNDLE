import { routes } from './../../app.routes';
import { SharedService } from './../../services/shared.service';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable()
export class AuthGuard implements CanActivate {

  public shared: SharedService;

  constructor(private router: Router) {
    this.shared = SharedService.getInstance();
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
    if (this.shared.isLoggedIn()) {
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
