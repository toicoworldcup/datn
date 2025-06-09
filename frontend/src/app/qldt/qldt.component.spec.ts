import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QldtComponent } from './qldt.component';

describe('QldtComponent', () => {
  let component: QldtComponent;
  let fixture: ComponentFixture<QldtComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [QldtComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QldtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
