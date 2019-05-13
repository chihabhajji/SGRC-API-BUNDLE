import { ResponseApi } from './../../model/response-api';
import { Summary } from './../../model/summary';
import { Component, OnInit } from '@angular/core';
import { TicketService } from '../../services/ticket/ticket.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  styleUrls: ['./summary.component.css']
})
export class SummaryComponent implements OnInit {
  data: any;
  summary: Summary = new Summary();
  message: {};
  classCss: {};
  public stats : number[];
  constructor(
    private ticketService: TicketService,
  ) { }

  ngOnInit() {

    this.data = {
      labels: ['Newly created tickets', 'Assigned tickets', 'Closed tickets', 'Resolved tickets', 'Approved tickets', 'Dissaproved tickets'],
      datasets: [{
        data: [0, 0, 0, 0, 0, 0],
        backgroundColor: ["#FF6384", "#36A2EB", "#FFCE56", "#FA02FC", "#BACC25", "#69FACD"],
        hoverBackgroundColor: ["#FF6384", "#36A2EB", "#FFCE56", "#000000", "#FFFFFF", "#ABD25D"]
      }]
    };

    this.ticketService.summary().subscribe((responseApi: ResponseApi) => {
        this.summary = responseApi.data;
        this.data.datasets[0].data = [this.summary.amountNew, this.summary.amountAssigned, this.summary.amountClosed, this.summary.amountResolved, this.summary.amountApproved, this.summary.amountDisapproved]
        
      },
      err => { 
        this.showMessage({ type: 'error', text: err['error']['errors'][0] 
        }); 
    });
  }

  private showMessage(message: {type: string, text: string}): void {
      this.message = message;
      this.buildClasses(message.type);
      setTimeout(() => {
        this.message = undefined;
      }, 3000);
  }

  private buildClasses(type: string): void {
    this.classCss = {
      'alert': true
    };
    this.classCss['alert-' + type] =  true;
  }

 

}
