import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { IconService } from '../service/icon.service';

import { IconComponent } from './icon.component';

describe('Component Tests', () => {
  describe('Icon Management Component', () => {
    let comp: IconComponent;
    let fixture: ComponentFixture<IconComponent>;
    let service: IconService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [IconComponent],
      })
        .overrideTemplate(IconComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(IconComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(IconService);

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
      expect(comp.icons?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
