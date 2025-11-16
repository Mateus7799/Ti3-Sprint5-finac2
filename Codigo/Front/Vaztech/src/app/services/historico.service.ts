import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { HistoricoPessoaItem, HistoricoProdutoItem } from '../models/historico.model';

@Injectable({
  providedIn: 'root',
})
export class HistoricoService {
  http = inject(HttpClient);
  apiRoute = 'api/historico';

  buscarHistoricoPessoa(idPessoa: number): Observable<HistoricoPessoaItem[]> {
    return this.http.get<HistoricoPessoaItem[]>(
      `${environment.apiURL}/${this.apiRoute}/pessoa/${idPessoa}`,
    );
  }

  buscarHistoricoProduto(idProduto: number): Observable<HistoricoProdutoItem[]> {
    return this.http.get<HistoricoProdutoItem[]>(
      `${environment.apiURL}/${this.apiRoute}/produto/${idProduto}`,
    );
  }
}
