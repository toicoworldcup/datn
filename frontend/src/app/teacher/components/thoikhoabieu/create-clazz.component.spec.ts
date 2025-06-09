import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateClazzComponent } from './create-clazz.component';

describe('CreateClazzComponent', () => {
  let component: CreateClazzComponent;
  let fixture: ComponentFixture<CreateClazzComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateClazzComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateClazzComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
