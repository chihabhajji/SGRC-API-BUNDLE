import { ResponseApi } from './../../model/response-api';
import { ActivatedRoute } from '@angular/router';
import { Ticket } from './../../model/ticket';
import { SharedService } from './../../services/shared.service';
import { NgForm } from '@angular/forms';
import { TicketService } from './../../services/ticket/ticket.service';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NotificationService } from '../../services/notification/notification.service';
import { User } from '../../model/user';
import { UserService } from './../../services/user/user.service';
import { Router } from '@angular/router';

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
  ticket = new Ticket('', 0, '', '', '', '', null, null, '', null, null, '',false, false, false, false, false, false, false);
  shared: SharedService;
  message: {};
  classCss: {};
  agents : User[];
  submited: Boolean = true;
  msg: String;
  constructor(
    private ticketService: TicketService,
    private userService: UserService,
    private route: ActivatedRoute,
    private notificationService: NotificationService,
    private router: Router) {
    this.shared = SharedService.getInstance();
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
  }

  ngOnInit() {
    const id: string = this.route.snapshot.params['id'];
    if (id !== undefined) {
      this.findById(id);
    }
    this.findAllTechnicians();
  }

  findAllTechnicians() {
    this.userService.findAllTechnicians().subscribe((responseApi: ResponseApi) => {  
      this.agents = responseApi.data;
      console.log(this.agents)
    }, err => {
      this.showMessage({
        type: 'error',
        text: ['error']['errors'][0]
      });
    });
  }

  findById(id: string) {
    this.ticketService.findById(id).subscribe((responseApi: ResponseApi) => {
      this.ticket = responseApi.data;
      this.ticket.data = new Date(this.ticket.data).toISOString();
      console.log(this.ticket);
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
      this.ticket = new Ticket('', 0, '', '', '', '', null, null, '', null, null, '',false, false, false, false, false, false, false);
        const ticket: Ticket = responseApi.data;
        this.form.resetForm();
        this.showMessage({
          type: 'success',
          text: `Registered ${ticket.title} successfully`
        });
        this.router.navigate(['']);
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
    if (status =="Assigned")
    this.ticket.reminded = false;
    this.ticketService.changeStatus(status, this.ticket).subscribe((responseApi: ResponseApi) => {
        this.ticket = responseApi.data;
        this.ticket.data = new Date(this.ticket.data).toISOString();
        this.showMessage({
          type: 'success',
          text: 'Successfully changed status'
        });
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 5000);
    } , err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
        setTimeout(() => {
          this.submited=true;
        }, 5000);
    });
  }


  selectAgent(id : String) {
    this.userService.findById(id).subscribe((responseApi: ResponseApi) => {
      this.ticket.assignedUser = responseApi.data;
      this.ticket.assignedUser.password = '';
    }, err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
    });
  }

  delete() {
    this.ticketService.delete(this.ticket.id);
    this.router.navigate(['']);
  }

  remind(){
    this.notificationService.remind(this.ticket, this.msg).subscribe((responseApi: ResponseApi) => {
      this.showMessage({
        type: 'success',
        text: 'Succesfully notified technicians'
      });
    }, err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
    });
  }
}

