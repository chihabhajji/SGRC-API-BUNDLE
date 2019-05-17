import { UserService } from '../../../services/user/user.service';
import { User } from '../../../model/user';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { SharedService } from '../../../services/shared.service';
import { ResponseApi } from '../../../model/response-api';
import { CurrentUser } from '../../../model/currentUser';
import { Router } from '@angular/router';
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  @ViewChild('form')
  form: NgForm;

  user = new User('', '', '', '', '',false);
  shared: SharedService;
  message: {};
  classCss: {};
  confirmpasswordvalue: {};
  submited: Boolean;

  constructor(
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.shared = SharedService.getInstance();
  }

  ngOnInit() {
    this.submited = true;
    this.user = this.shared.user;
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

  register() {
    this.message = {};
    this.submited = false;
    this.user.profile = 'ROLE_CUSTOMER';
    this.userService.createOrUpdate(this.user).subscribe((responseApi: ResponseApi) => {
      const userRet: User = responseApi.data;
      this.submited = false;
      this.showMessage({
        type: 'success',
        text: `Registered ${userRet.email} successfully`
      });
      setTimeout(() => {
        this.login();
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
  }

  login() {
    this.message = '';
    this.userService.login(this.user).subscribe((userAuthentication: CurrentUser) => {
      this.shared.token = userAuthentication.token;
      this.shared.user = userAuthentication.user;
      console.log(this.shared.user)
      console.log(this.shared.token)
      this.shared.user.profile = this.shared.user.profile.substring(5);
      this.shared.showTemplate.emit(true);
      this.form.resetForm();
      this.router.navigate(['/']);
    }, err => {
      this.shared.token = null;
      this.shared.user = null;
      this.shared.showTemplate.emit(false);
      this.message = 'Erro';
    });
  }
  getFormGroupClass(isInvalid: boolean, isDirty: Boolean): {} {
    return {
      'form-group': true,
      'has-error': isInvalid && isDirty,
      'has-success': !isInvalid && isDirty
    };
  }

}
