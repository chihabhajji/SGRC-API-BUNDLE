import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DialogService } from '../../dialog.service';
import { ChangePasswordComponent } from './../security/change-password/change-password.component';
@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})

export class ProfileComponent implements OnInit {
  
  constructor(private dialogService: DialogService,
    private router: Router) {

     }

  ngOnInit() {

  }


}
