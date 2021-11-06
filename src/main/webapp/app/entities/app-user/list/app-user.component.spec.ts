import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { AppUserService } from '../service/app-user.service';

import { AppUserComponent } from './app-user.component';

describe('Component Tests', () => {
  describe('AppUser Management Component', () => {
    let comp: AppUserComponent;
    let fixture: ComponentFixture<AppUserComponent>;
    let service: AppUserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AppUserComponent],
      })
        .overrideTemplate(AppUserComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AppUserComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(AppUserService);

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
      expect(comp.appUsers?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
