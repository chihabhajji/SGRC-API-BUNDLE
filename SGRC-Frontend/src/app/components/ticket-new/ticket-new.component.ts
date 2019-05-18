import { Router, ActivatedRoute } from '@angular/router';
import { TicketService } from './../../services/ticket/ticket.service';
import { SharedService } from './../../services/shared.service';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Ticket } from '../../model/ticket';
import { ResponseApi } from '../../model/response-api';
import { NgForm } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-ticket-new',
  templateUrl: './ticket-new.component.html',
  styleUrls: ['./ticket-new.component.css']
})
export class TicketNewComponent implements OnInit {

  @ViewChild('form')
  form: NgForm;

  ticket = new Ticket('', null, '', '', '', '', null, null, '', null, null, '',false, false, false, false, false, false, false);
  shared: SharedService;
  message: {};
  classCss: {};
  submited: Boolean = true;
  constructor(
    private ticketService: TicketService,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
    private router: Router
  ) {
    this.shared = SharedService.getInstance();
  }

  ngOnInit() {
    const id: String = this.route.snapshot.params['id'];
    if (id !== undefined) {
      this.findById(id);
    }
  }

  transform(html) {
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

  findById(id: String) {
    this.ticketService.findById(id).subscribe((responseApi: ResponseApi) => {
      this.ticket = responseApi.data;
    } , err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
    });
  }

  register() {
    this.submited=false;
    this.message = {};
    this.ticketService.createOrUpdate(this.ticket).subscribe((responseApi: ResponseApi) => {
      this.form.resetForm;
      this.ticket = new Ticket('', 0, '', '', '', '', null, null, '', null, null, '',false, false, false, false, false, false, false);
        const ticket: Ticket = responseApi.data;
        this.form.resetForm();
        this.showMessage({
          type: 'success',
          text: `Registered ${ticket.title} successfully`
        });
      setTimeout(() => {
        this.router.navigate(['/']);
      }, 5000);
    } , err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
      this.submited = true;
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
        
          if (this.ticket.image instanceof ArrayBuffer) {
          // throw an error, 'cause you can't handle this
          } else {
          this.ticket.image = reader.result as string;
          } 
        //this.ticket.image = reader.result as string;
      };
      reader.readAsDataURL(event.target.files[0]);
    }
  }

}
