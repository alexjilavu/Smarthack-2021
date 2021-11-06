import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { RewardService } from '../service/reward.service';

import { RewardComponent } from './reward.component';

describe('Component Tests', () => {
  describe('Reward Management Component', () => {
    let comp: RewardComponent;
    let fixture: ComponentFixture<RewardComponent>;
    let service: RewardService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [RewardComponent],
      })
        .overrideTemplate(RewardComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RewardComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(RewardService);

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
      expect(comp.rewards?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
