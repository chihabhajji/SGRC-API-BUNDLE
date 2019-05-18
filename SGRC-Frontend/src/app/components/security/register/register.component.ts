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
    private router: Router
  ) {
  }

  ngOnInit() {
    this.submited = true;
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
    this.userService.register(this.user).subscribe((responseApi: ResponseApi) => {
      this.user = responseApi.data;
      this.showMessage({
        type: 'success',
        text: `Registered ${this.user.email} successfully`
      });
      setTimeout(() => {
        this.submited = true;
      }, 3000);
      this.form.resetForm();
      this.form.reset();
      this.router.navigate(['/login']);
    }, err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
        setTimeout(() => {
          this.submited = true;
        }, 3000);
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
