import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../../model/user';
import { HELP_DESK_API } from '../../services/helpdesk.api';

@Injectable()
export class UserService {

  constructor(private http: HttpClient) { }

  login(user: User) {
    return this.http.post(`${HELP_DESK_API}/api/auth`, user);
  }
  createOrUpdate(user: User) {
    if (user.id != null && user.id !== '') {
      console.log("id not null, updating");
      return this.http.put(`${HELP_DESK_API}/api/user`, user);
    } else {
      user.id = null;
      console.log("id null, creating");
      return this.http.post(`${HELP_DESK_API}/api/user`, user);
    }
  }

  register(user: User) {
    return this.http.post(`${HELP_DESK_API}/api/auth/register`, user);
  }

  findAll(page: Number, count: Number) {
    return this.http.get(`${HELP_DESK_API}/api/user/${page}/${count}`);
  }

  findAllTechnicians(){
    return this.http.get(`${HELP_DESK_API}/api/user/techlist/`);
  }

  findById(id: String) {
    return this.http.get(`${HELP_DESK_API}/api/user/${id}`);
  }
  
  verify(code: String){
    return this.http.get(`${HELP_DESK_API}/api/auth/verify-email/${code}`);
  }

  delete(id: String) {
    return this.http.delete(`${HELP_DESK_API}/api/user/${id}`);
  }

  findByParams(page: Number, count: Number, u: User) {
    u.email = u.email === '' ? 'uninformed' : u.email;
    u.name = u.name === '' ? 'uninformed' : u.name;
    u.profile = u.profile === '' ? 'uninformed' : u.profile;
    return this.http.get(`${HELP_DESK_API}/api/user/${page}/${count}/${u.name}/${u.profile}/${u.email}`);
  }

  summary(id : String , year : Number) {
    return this.http.get(`${HELP_DESK_API}/api/user/summary`);
  }
}
