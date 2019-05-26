import { AuthGuard } from './components/security/auth.guard';
import { Component, ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/security/login/login.component';
import { UserNewComponent } from './components/user-new/user-new.component';
import { UserListComponent } from './components/user-list/user-list.component';
import { TicketNewComponent } from './components/ticket-new/ticket-new.component';
import { TicketListComponent } from './components/ticket-list/ticket-list.component';
import { TicketDetailComponent } from './components/ticket-detail/ticket-detail.component';
import { SummaryComponent } from './components/summary/summary.component';
import { RegisterComponent } from './components/security/register/register.component';
import { ProfileComponent } from './components/profile/profile.component';
import { TicketArchivedListComponent } from './components/ticket-archived-list/ticket-archived-list.component'
import { VerifyEmailComponent } from './components/verify-email/verify-email.component'
import { ChangePasswordComponent } from './components/security/change-password/change-password.component'
export const ROUTES: Routes = [
  { path: '', component: TicketListComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'user-new', component: UserNewComponent, canActivate: [AuthGuard]},
  { path: 'user-new/:id', component: UserNewComponent, canActivate: [AuthGuard]},
  { path: 'user-list', component: UserListComponent, canActivate: [AuthGuard]},
  { path: 'ticket-new' , component: TicketNewComponent, canActivate: [AuthGuard] },
  { path: 'ticket-new/:id' , component: TicketNewComponent, canActivate: [AuthGuard] },
  { path: 'ticket-list' , component: TicketListComponent, canActivate: [AuthGuard] },
  { path: 'ticket-detail/:id' , component: TicketDetailComponent, canActivate: [AuthGuard] },
  { path: 'summary' , component: SummaryComponent, canActivate: [AuthGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'profile/:id', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'changepassword', component: ChangePasswordComponent},
  { path: 'verify-email', component: VerifyEmailComponent},
  { path: 'archived', component: TicketArchivedListComponent, canActivate: [AuthGuard] }
  
];

export const routes: ModuleWithProviders = RouterModule.forRoot(ROUTES);


