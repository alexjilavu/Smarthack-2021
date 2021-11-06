import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { HashTagDetailComponent } from './hash-tag-detail.component';

describe('Component Tests', () => {
  describe('HashTag Management Detail Component', () => {
    let comp: HashTagDetailComponent;
    let fixture: ComponentFixture<HashTagDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [HashTagDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ hashTag: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(HashTagDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(HashTagDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load hashTag on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.hashTag).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
