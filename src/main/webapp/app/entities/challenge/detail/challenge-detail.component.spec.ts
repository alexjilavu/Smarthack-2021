import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ChallengeDetailComponent } from './challenge-detail.component';

describe('Component Tests', () => {
  describe('Challenge Management Detail Component', () => {
    let comp: ChallengeDetailComponent;
    let fixture: ComponentFixture<ChallengeDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ChallengeDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ challenge: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ChallengeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ChallengeDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load challenge on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.challenge).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
