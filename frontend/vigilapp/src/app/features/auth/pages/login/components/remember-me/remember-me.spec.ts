import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RememberMe } from './remember-me';

describe('RememberMe', () => {
  let component: RememberMe;
  let fixture: ComponentFixture<RememberMe>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RememberMe],
    }).compileComponents();

    fixture = TestBed.createComponent(RememberMe);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
