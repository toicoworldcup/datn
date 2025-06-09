import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LayoutQldtComponent } from './layout-qldt.component';


describe('LayoutAdminComponent', () => {
  let component: LayoutQldtComponent;
  let fixture: ComponentFixture<LayoutQldtComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LayoutQldtComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutQldtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
