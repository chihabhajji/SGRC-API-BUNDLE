import { UserService } from './../../services/user/user.service';
import { User } from './../../model/user';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router,ActivatedRoute } from '@angular/router';
import { SharedService } from '../../services/shared.service';
import { ResponseApi } from './../../model/response-api';

@Component({
  selector: 'app-user-new',
  templateUrl: './user-new.component.html',
  styleUrls: ['./user-new.component.css']
})
export class UserNewComponent implements OnInit {

  @ViewChild('form')
  form: NgForm;
  user = new User('', '', '', '','',false,false,false);
  shared: SharedService;
  message: {};
  classCss: {};
  addinterface: Boolean = true;
  submited: Boolean = true ;

  constructor(
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.shared = SharedService.getInstance();
  }

  ngOnInit() {
    const id: String = this.route.snapshot.params['id'];
    if (id != null){
      this.findById(id);
    }
  }

  findById(id: String) {
    this.userService.findById(id).subscribe((responseApi: ResponseApi) => {
      this.user = responseApi.data;
      this.user.password = '';
    }, err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
    });
  }

  private showMessage(message: {type: String, text: String}): void {
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
    this.classCss['alert-' + type] =  true;
  }

  register() {
    this.submited = !this.submited;
    this.message = {};
    this.userService.createOrUpdate(this.user).subscribe((responseApi: ResponseApi) => {
      this.user = new User('', '', '', '','',false,false,false);
      const userRet: User = responseApi.data;
      this.form.resetForm();
      this.showMessage({
        type: 'success',
        text: `Updated ${userRet.email} successfully`
      });
        setTimeout(() => {
          this.router.navigate(['/user-list']);
        }, 5000);
    }, err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
      this.submited = !this.submited;
    });
  }

  getFormGroupClass(isInvalid: boolean, isDirty: Boolean): {} {
    return {
      'form-group': true,
      'has-error' : isInvalid  && isDirty,
      'has-success' : !isInvalid  && isDirty
    };
  }
}
