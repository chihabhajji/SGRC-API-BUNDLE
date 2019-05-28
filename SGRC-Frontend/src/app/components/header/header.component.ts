import { Component, OnInit } from '@angular/core';
import { SharedService } from '../../services/shared.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  size : Number=0;
  Notifications = [];
  message: {};
  classCss: {};
  public shared: SharedService;

  constructor(private router: Router) {
    this.shared = SharedService.getInstance();

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
  
  detail(id: string) {
    this.router.navigate(['/ticket-detail', id]);
  }
}
