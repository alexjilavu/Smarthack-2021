import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ChallengeService } from '../service/challenge.service';

import { ChallengeComponent } from './challenge.component';

describe('Component Tests', () => {
  describe('Challenge Management Component', () => {
    let comp: ChallengeComponent;
    let fixture: ComponentFixture<ChallengeComponent>;
    let service: ChallengeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ChallengeComponent],
      })
        .overrideTemplate(ChallengeComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ChallengeComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ChallengeService);

      const headers = new HttpHeaders().append('link', 'link;link');
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.challenges?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
