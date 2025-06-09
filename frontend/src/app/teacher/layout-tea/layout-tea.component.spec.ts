import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutTeaComponent } from './layout-tea.component';

describe('LayoutAdminComponent', () => {
  let component: LayoutTeaComponent;
  let fixture: ComponentFixture<LayoutTeaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LayoutTeaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutTeaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
