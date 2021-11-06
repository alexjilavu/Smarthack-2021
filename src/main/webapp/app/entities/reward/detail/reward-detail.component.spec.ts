import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RewardDetailComponent } from './reward-detail.component';

describe('Component Tests', () => {
  describe('Reward Management Detail Component', () => {
    let comp: RewardDetailComponent;
    let fixture: ComponentFixture<RewardDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [RewardDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ reward: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(RewardDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(RewardDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load reward on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.reward).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
