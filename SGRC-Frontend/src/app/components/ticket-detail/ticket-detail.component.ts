import { ResponseApi } from './../../model/response-api';
import { ActivatedRoute } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Ticket } from './../../model/ticket';
import { SharedService } from './../../services/shared.service';
import { NgForm } from '@angular/forms';
import { TicketService } from './../../services/ticket/ticket.service';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CarouselModule } from 'primeng/carousel';
import { User } from '../../model/user';
import { UserService } from './../../services/user/user.service';
@Component({
  selector: 'app-ticket-detail',
  templateUrl: './ticket-detail.component.html',
  styleUrls: ['./ticket-detail.component.css'],
  styles: [`
        .ui-grid-row {
            text-align: center;
        }
        .ui-grid {
            margin: 10px 0px;
        }
        .ui-grid-row > div {
            padding: 4px 10px;
        }
    `]
})
export class TicketDetailComponent implements OnInit {

  @ViewChild('form')
  form: NgForm;
  page: number;
  count: number;
  pages: Array<Number>;
  ticket = new Ticket('', 0, '', '', '', '', null, null, '', null);
  shared: SharedService;
  message: {};
  classCss: {};
  agents : User[];
  constructor(
    private ticketService: TicketService,
    private userService: UserService,
    private route: ActivatedRoute) {
    this.page = 0;
    this.count = 3;
    this.shared = SharedService.getInstance();
  }

  ngOnInit() {
    const id: string = this.route.snapshot.params['id'];
    if (id !== undefined) {
      this.findById(id);
    }
    this.findAllTechnicians(this.page, this.count);
  }

  findAllTechnicians(page: Number, count: Number) {
    this.userService.findAllTechnicians(page, count).subscribe((responseApi: ResponseApi) => {
      console.log('Querying');
      this.agents = responseApi['data']['content'];
      this.pages = new Array(responseApi['data']['totalPages']);
      console.log(this.agents[0].email);
    }, err => {
      this.showMessage({
        type: 'error',
        text: ['error']['errors'][0]
      });
    });
  }

  findById(id: string) {
    console.log('id --> ', id);
    this.ticketService.findById(id).subscribe((responseApi: ResponseApi) => {
      console.log('responseApi -->  ', responseApi);
      this.ticket = responseApi.data;
      this.ticket.data = new Date(this.ticket.data).toISOString();
  } , err => {
    this.showMessage({
      type: 'error',
      text: err['error']['errors'][0]
    });
  });
  }

  register() {
    this.message = {};
    this.ticketService.createOrUpdate(this.ticket).subscribe((responseApi: ResponseApi) => {
        this.ticket = new Ticket('', 0, '', '', '', '', null, null, '', null);
        const ticket: Ticket = responseApi.data;
        this.form.resetForm();
        this.showMessage({
          type: 'success',
          text: `Registered ${ticket.title} successfully`
        });
    } , err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
    });
  }

  getFormGroupClass(isInvalid: boolean, isDirty: boolean): {} {
    return {
      'form-group': true,
      'has-error' : isInvalid  && isDirty,
      'has-success' : !isInvalid  && isDirty
    };
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

  onFileChange(event): void {
    if (event.target.files[0].size > 2000000) {
      this.showMessage({
        type: 'error',
        text: 'Maximum image size is 2 MB'
      });
    } else {
      this.ticket.image = '';
      const reader = new FileReader();
      reader.onloadend = (e: Event) => {
        this.ticket.image = reader.result as string;;
      };
      reader.readAsDataURL(event.target.files[0]);
    }
  }

  changeStatus(status: string): void {
    this.ticketService.changeStatus(status, this.ticket).subscribe((responseApi: ResponseApi) => {
        this.ticket = responseApi.data;
        this.ticket.data = new Date(this.ticket.data).toISOString();
        this.showMessage({
          type: 'success',
          text: 'Successfully changed status'
        });
    } , err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
    });
  }

  selectAgent(agent : User) {
    this.showMessage({type : 'success', text : 'Agent selected :'+ agent.email});
    this.ticket.assignedUser = agent ;
  }
  

  setNextPage(event: any) {
    event.preventDefault();
    if (this.page + 1 < this.pages.length) {
      this.page = this.page + 1;
      this.findAllTechnicians(this.page, this.count);
    }
  }

  setPreviousPage(event: any) {
    event.preventDefault();
    if (this.page > 0) {
      this.page = this.page - 1;
      this.findAllTechnicians(this.page, this.count);
    }
  }

  setPage(i, event: any) {
    event.preventDefault();
    this.page = i;
    this.findAllTechnicians(this.page, this.count);
  }


}

