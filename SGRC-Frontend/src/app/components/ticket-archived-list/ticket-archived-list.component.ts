import { Component, OnInit } from '@angular/core';
import { Ticket } from './../../model/ticket';
import { ResponseApi } from './../../model/response-api';
import { SharedService } from './../../services/shared.service';
import { Router } from '@angular/router';
import { DialogService } from '../../dialog.service';
import { TicketService } from '../../services/ticket/ticket.service';
@Component({
  selector: 'app-ticket-archived-list',
  templateUrl: './ticket-archived-list.component.html',
  styleUrls: ['./ticket-archived-list.component.css']
})
export class TicketArchivedListComponent implements OnInit {  message: {};
  page: number;
  count: number;
  pages: Array<Number>;
  listTicket = [];
  classCss: {};
  shared: SharedService;
  
  constructor(private dialogService: DialogService,
    private ticketService: TicketService,
    private router: Router) {
    this.shared = SharedService.getInstance();
    this.page = 0;
    this.count = 5;
     }

  ngOnInit() {
    this.findAllArchived(this.page, this.count);
  }
  
  findAllArchived(page: number, count: number) {
    this.ticketService.findAllArchived(page, count).subscribe((responseApi: ResponseApi) => {
      this.listTicket = responseApi['data']['content'];
      this.pages = new Array(responseApi['data']['totalPages']);
    }, err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
    });
  }

  detail(id: string) {
    this.router.navigate(['/ticket-detail', id]);
  }
  
  setNextPage(event: any) {
    event.preventDefault();
    if (this.page + 1 < this.pages.length) {
      this.page = this.page + 1;
      this.findAllArchived(this.page, this.count);
    }
  }

  setPreviousPage(event: any) {
    event.preventDefault();
    if (this.page > 0) {
      this.page = this.page - 1;
      this.findAllArchived(this.page, this.count);
    }
  }

  setPage(i, event: any) {
    event.preventDefault();
    this.page = i;
    this.findAllArchived(this.page, this.count);
  }

  private showMessage(message: { type: string, text: string }): void {
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
    this.classCss['alert-' + type] = true;
  }

}
