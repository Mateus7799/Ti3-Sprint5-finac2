import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { FormaPagamento } from '../models/forma-pagamento.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UtilsService {
  http = inject(HttpClient);

  buscarFormasPagamento(): Observable<FormaPagamento[]> {
    const apiRoute = 'api/metodo-pagamento';
    return this.http.get<FormaPagamento[]>(`${environment.apiURL}/${apiRoute}`);
  }
}
