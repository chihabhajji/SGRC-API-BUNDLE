import { Component, OnInit } from '@angular/core';
import { SharedService } from '../../services/shared.service';
import { UserService } from './../../services/user/user.service';
import { User } from './../../model/user';
import { ResponseApi } from './../../model/response-api';
@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})

export class ProfileComponent implements OnInit {
  shared: SharedService;
  data: any;
  year: Number;
  constructor(private userService: UserService) {
    this.shared = SharedService.getInstance();
    this.year = new Date().getFullYear();
    this.data = {
      labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November','December'],
      datasets: [
        {
          label: 'Solved tickets',
          backgroundColor: '#00ff00',
          borderColor: '#1E88E5',
          data: [65, 59, 80, 81, 56, 55, 40,50,60,30,50,80]
        },
        {
          label: 'Unsolved tickets',
          backgroundColor: '#ff0000',
          borderColor: '#ff9980',
          data: [28, 48, 40, 19, 86, 27, 90,50,40,90,70,20]
        }
      ]
    }
  }

  ngOnInit() {
    this.year = new Date().getFullYear();
    console.log(this.year);
    this.userService.summary(this.shared.user.id,this.year).subscribe((responseApi: ResponseApi) => {
      console.log(responseApi.data);
      //this.data.datasets[0].data = [this.summary.amountNew, this.summary.amountAssigned, this.summary.amountClosed, this.summary.amountResolved, this.summary.amountApproved, this.summary.amountDisapproved]
    });
  }





}
