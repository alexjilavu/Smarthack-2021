import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { IconDetailComponent } from './icon-detail.component';

describe('Component Tests', () => {
  describe('Icon Management Detail Component', () => {
    let comp: IconDetailComponent;
    let fixture: ComponentFixture<IconDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [IconDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ icon: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(IconDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(IconDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load icon on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.icon).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
