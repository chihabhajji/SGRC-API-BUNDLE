import { UserService } from './../../../services/user/user.service';
import { User } from './../../../model/user';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { SharedService } from '../../../services/shared.service';
import { ResponseApi } from './../../../model/response-api';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {
  @ViewChild('form')
  form: NgForm;

  submited: Boolean=true;
  user = new User('','', '', '', '', false,false,false,null);
  shared: SharedService;
  message: {};
  classCss: {};
  confirmpasswordvalue: {};
  
  constructor(
    private userService: UserService,
    private router: Router) { this.shared = SharedService.getInstance();}

  ngOnInit() {
    if(this.shared.isLoggedIn()){
      this.user=this.shared.user;
    }else {
      // check if he has code form router param 
      this.router.navigate(['/']);
    }

  }
  register() {
    this.submited = !this.submited;
    this.message = {};
    const role: string = "ROLE_";
    this.user.profile = role.concat(this.user.profile.toString());
    this.userService.createOrUpdate(this.user).subscribe((responseApi: ResponseApi) => {
      this.showMessage({
        type: 'success',
        text: `Succesfully changed password`
      });
      setTimeout(() => {
         this.signOut();
      }, 5000);
    }, err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
      setTimeout(() => {
        this.submited = true;
      }, 5000);
    });
    this.user.profile = this.user.profile.substring(5);
  }

  signOut(): void {
    this.shared.token = null;
    this.shared.user = null;
    sessionStorage.clear();
    localStorage.clear();
    window.location.href = '/login';
    window.location.reload();
  }
  private showMessage(message: { type: String, text: String }): void {
    this.message = message;
    this.buildClasses(message.type);
    setTimeout(() => {
      this.message = undefined;
    }, 3000);
  }

  private buildClasses(type: String) {
    this.classCss = {
      'alert': true
    };
    this.classCss['alert-' + type] = true;
  }
  getFormGroupClass(isInvalid: boolean, isDirty: Boolean): {} {
    return {
      'form-group': true,
      'has-error': isInvalid && isDirty,
      'has-success': !isInvalid && isDirty
    };
  }

}
