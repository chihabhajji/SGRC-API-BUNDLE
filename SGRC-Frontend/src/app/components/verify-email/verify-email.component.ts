import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from './../../services/user/user.service';
import { Router } from '@angular/router';
import { ResponseApi } from './../../model/response-api';
@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.css']
})
export class VerifyEmailComponent implements OnInit {

  constructor(private route: ActivatedRoute, private router: Router, private userService: UserService) { }
  classCss: {};
  message: {};

  ngOnInit() {
    const code: string = this.route.snapshot.params['code'];
    this.route.queryParams.subscribe(params => {
      const code = params['code'];
      if (code !== null) {
        this.verify(code);
        console.log(code);
      } else {
        this.showMessage({ type: 'error', text: 'Invalid verification token' })
      }
    });
  }

  verify(code:String){
    this.userService.verify(code).subscribe((responseApi: ResponseApi) => {
      this.showMessage({
        type: 'success',
        text: `Successfully verified`
      });
      setTimeout(() => {
        this.router.navigate(['/']);
      },5000);
    }, err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 5000);
    });
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

}
