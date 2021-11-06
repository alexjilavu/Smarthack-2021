import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { HashTagService } from '../service/hash-tag.service';

import { HashTagComponent } from './hash-tag.component';

describe('Component Tests', () => {
  describe('HashTag Management Component', () => {
    let comp: HashTagComponent;
    let fixture: ComponentFixture<HashTagComponent>;
    let service: HashTagService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [HashTagComponent],
      })
        .overrideTemplate(HashTagComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(HashTagComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(HashTagService);

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
      expect(comp.hashTags?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
