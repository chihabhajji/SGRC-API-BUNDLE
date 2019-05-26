import { Component, OnInit } from '@angular/core';
import { SharedService } from '../../services/shared.service';
import { YearlySummary } from './../../model/YearlySummary'
import { ResponseApi } from './../../model/response-api';
import { UserService } from './../../services/user/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Ticket } from '../../model/ticket';
import { TicketService } from '../../services/ticket/ticket.service';
@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})

export class ProfileComponent implements OnInit {
  shared: SharedService;
  data: any;
  year: Number;
  yearlySummary: YearlySummary;
  total : number;
  userProfile : Boolean ;
  assignedToMe: Boolean = false;
  count: number;
  page: number;
  listTicket = [];
  ticketFilter = new Ticket('', null, '', '', '', '', null, null, '', null, null, null, '', false, false, false, false, false, false, false, 0);
  pages: Array<Number>;
  userId: string;
  
  constructor(private userService: UserService, 
    private route: ActivatedRoute,
    private ticketService: TicketService,
    private router: Router
    ) {
    this.page = 0;
    this.count = 5;
    this.shared = SharedService.getInstance();
    this.year = new Date().getFullYear();
    this.data = {
      labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November','December'],
      datasets: [
        {
          label: 'Solved tickets',
          backgroundColor: '#00ff00',
          borderColor: '#1E88E5',
        },
        {
          label: 'Unsolved tickets',
          backgroundColor: '#ff0000',
          borderColor: '#ff9980',

        }
      ]
    }
  }

  ngOnInit() {
    this.year = new Date().getFullYear();
    const id: string = this.route.snapshot.params['id'];

    if (id !== undefined) {
      this.userProfile=false;
      this.userId = id;
      this.findAll(this.page, this.count);
      this.userService.summary(id, this.year).subscribe((responseApi: ResponseApi) => {
        this.yearlySummary = responseApi.data;
        this.data.datasets[0].data = [
          this.yearlySummary.january.amountApproved,
          this.yearlySummary.february.amountApproved,
          this.yearlySummary.march.amountApproved,
          this.yearlySummary.april.amountApproved,
          this.yearlySummary.may.amountApproved,
          this.yearlySummary.june.amountApproved,
          this.yearlySummary.july.amountApproved,
          this.yearlySummary.august.amountApproved,
          this.yearlySummary.september.amountApproved,
          this.yearlySummary.october.amountApproved,
          this.yearlySummary.november.amountApproved,
          this.yearlySummary.december.amountApproved
        ]
        this.data.datasets[1].data = [
          this.yearlySummary.january.amountDisaproved,
          this.yearlySummary.february.amountDisaproved,
          this.yearlySummary.march.amountDisaproved,
          this.yearlySummary.april.amountDisaproved,
          this.yearlySummary.may.amountDisaproved,
          this.yearlySummary.june.amountDisaproved,
          this.yearlySummary.july.amountDisaproved,
          this.yearlySummary.august.amountDisaproved,
          this.yearlySummary.september.amountDisaproved,
          this.yearlySummary.october.amountDisaproved,
          this.yearlySummary.november.amountDisaproved,
          this.yearlySummary.december.amountDisaproved
        ]
      });
    }else {
      this.userProfile = true;
      this.userService.summary(this.shared.user.id, this.year).subscribe((responseApi: ResponseApi) => {
        this.yearlySummary = responseApi.data;
        this.data.datasets[0].data = [
          this.yearlySummary.january.amountApproved,
          this.yearlySummary.february.amountApproved,
          this.yearlySummary.march.amountApproved,
          this.yearlySummary.april.amountApproved,
          this.yearlySummary.may.amountApproved,
          this.yearlySummary.june.amountApproved,
          this.yearlySummary.july.amountApproved,
          this.yearlySummary.august.amountApproved,
          this.yearlySummary.september.amountApproved,
          this.yearlySummary.october.amountApproved,
          this.yearlySummary.november.amountApproved,
          this.yearlySummary.december.amountApproved
        ]
        this.data.datasets[1].data = [
          this.yearlySummary.january.amountDisaproved,
          this.yearlySummary.february.amountDisaproved,
          this.yearlySummary.march.amountDisaproved,
          this.yearlySummary.april.amountDisaproved,
          this.yearlySummary.may.amountDisaproved,
          this.yearlySummary.june.amountDisaproved,
          this.yearlySummary.july.amountDisaproved,
          this.yearlySummary.august.amountDisaproved,
          this.yearlySummary.september.amountDisaproved,
          this.yearlySummary.october.amountDisaproved,
          this.yearlySummary.november.amountDisaproved,
          this.yearlySummary.december.amountDisaproved
        ]
      });
    }
  }



  findAll(page: number, count: number) {
    this.ticketService.findAllProfile(this.userId,page, count).subscribe((responseApi: ResponseApi) => {
      this.listTicket = responseApi['data']['content'];
      this.pages = new Array(responseApi['data']['totalPages']);
    });
  }

  detail(id: string) {
    this.router.navigate(['/ticket-detail', id]);
  }

  setNextPage(event: any) {
    event.preventDefault();
    if (this.page + 1 < this.pages.length) {
      this.page = this.page + 1;
      this.findAll(this.page, this.count);
    }
  }

  setPreviousPage(event: any) {
    event.preventDefault();
    if (this.page > 0) {
      this.page = this.page - 1;
      this.findAll(this.page, this.count);
    }
  }

  setPage(i, event: any) {
    event.preventDefault();
    this.page = i;
    this.findAll(this.page, this.count);
  }

}
