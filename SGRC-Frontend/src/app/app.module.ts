import { TicketService } from './services/ticket/ticket.service';
import { SharedService } from './services/shared.service';
import { UserService } from './services/user/user.service';
import { NotificationService } from './services/notification/notification.service'
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { MenuComponent } from './components/menu/menu.component';
import { LoginComponent } from './components/security/login/login.component';
import { routes } from './app.routes';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AuthInterceptor } from './components/security/auth.interceptor';
import { AuthGuard } from './components/security/auth.guard';
import { UserNewComponent } from './components/user-new/user-new.component';
import { UserListComponent } from './components/user-list/user-list.component';
import { DialogService } from './dialog.service';
import { TicketNewComponent } from './components/ticket-new/ticket-new.component';
import { TicketListComponent } from './components/ticket-list/ticket-list.component';
import { TicketDetailComponent } from './components/ticket-detail/ticket-detail.component';
import { SummaryComponent } from './components/summary/summary.component';
import { ChartModule } from 'primeng/primeng';
import { AvatarModule } from 'ng2-avatar';
import { CarouselModule } from 'primeng/carousel';
import { CardModule } from 'primeng/card';
import { DynamicDialogModule } from 'primeng/dynamicdialog';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ProfileComponent } from './components/profile/profile.component';
import { RegisterComponent } from './components/security/register/register.component';
import { TicketArchivedListComponent } from './components/ticket-archived-list/ticket-archived-list.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { VerifyEmailComponent } from './components/verify-email/verify-email.component';
import { ChangePasswordComponent } from './components/security/change-password/change-password.component';
@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    MenuComponent,
    LoginComponent,
    UserNewComponent,
    UserListComponent,
    TicketNewComponent,
    TicketListComponent,
    TicketDetailComponent,
    SummaryComponent,
    ProfileComponent,
    RegisterComponent,
    TicketArchivedListComponent,
    VerifyEmailComponent,
    ChangePasswordComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ChartModule,
    AvatarModule,
    CarouselModule,
    CardModule,
    DynamicDialogModule,
    NgbModule,
    NgSelectModule,
    routes
  ],
  providers: [
    UserService,
    TicketService,
    SharedService,
    NotificationService,
    AuthGuard,
    DialogService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
