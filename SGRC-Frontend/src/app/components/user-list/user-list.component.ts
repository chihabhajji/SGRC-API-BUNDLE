import { UserService } from './../../services/user/user.service';
import { SharedService } from './../../services/shared.service';
import { Component, OnInit } from '@angular/core';
import { DialogService } from '../../dialog.service';
import { Router } from '@angular/router';
import { ResponseApi } from '../../model/response-api';


@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {

  page: number;
  count: number;
  pages: Array<Number>;
  shared: SharedService;
  message: {};
  classCss: {};
  listUser = [];

  constructor(
    private dialogService: DialogService,
    private userService: UserService,
    private router: Router
  ) {
    this.page = 0;
    this.count = 2;
  }

  ngOnInit() {
    this.findAll(this.page, this.count);
  }

  findAll(page: Number, count: Number) {
    this.userService.findAll(page, count).subscribe((responseApi: ResponseApi) => {
      this.listUser = responseApi['data']['content'];
      this.pages = new Array(responseApi['data']['totalPages']);

    }, err => {
      this.showMessage({
        type: 'error',
        text: ['error']['errors'][0]
      });
    });
  }

  edit(id: String) {
    this.router.navigate(['/user-new', id]);
  }

  delete(id: String) {
    this.dialogService.confirm(' Do you want to delete the user?')
      .then((canDelete: Boolean) => {
        if (canDelete) {
          this.message = {};
          this.userService.delete(id).subscribe((responseApi: ResponseApi) => {
            this.showMessage({
              type: 'success',
              text: `Record ${id} deleted`
            });
            this.findAll(this.page, this.count);
          }, err => {
            this.showMessage({
              type: 'error',
              text: ['error']['errors'][0]
            });
          });
        }
      });
  }

  setNextPage(event: any) {
    event.preventDefault();
    if ( this.page + 1 < this.pages.length) {
      this.page =  this.page + 1;
      this.findAll(this.page, this.count);
    }
  }

  setPreviousPage(event: any) {
    event.preventDefault();
    if (this.page > 0) {
      this.page =  this.page - 1;
      this.findAll(this.page, this.count);
    }
  }

  setPage(i, event: any) {
    event.preventDefault();
    this.page = i;
    this.findAll(this.page, this.count);
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

}
