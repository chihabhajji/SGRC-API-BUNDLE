import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketArchivedListComponent } from './ticket-archived-list.component';

describe('TicketArchivedListComponent', () => {
  let component: TicketArchivedListComponent;
  let fixture: ComponentFixture<TicketArchivedListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TicketArchivedListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TicketArchivedListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
