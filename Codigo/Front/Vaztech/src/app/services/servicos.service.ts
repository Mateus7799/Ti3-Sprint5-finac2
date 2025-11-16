import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ProdutoStatus } from '../models/produto.model';
import {
  AdicionarServicoDTO,
  EditarServicoDTO,
  Servico,
  ServicosReqDTO,
  StatusServico,
  TiposServico,
} from '../models/servico.model';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ServicosService {
  http = inject(HttpClient);
  apiRoute = 'api/servico';

  listarServicosStatus(): Observable<StatusServico[]> {
    return this.http.get<StatusServico[]>(`${environment.apiURL}/${this.apiRoute}/status`);
  }

  buscarServicos(
    pagina: number = 0,
    size: number = 6,
    searchText?: string,
    emProgresso: boolean = false,
  ): Observable<ServicosReqDTO> {
    return this.http.get<ServicosReqDTO>(
      `${environment.apiURL}/${this.apiRoute}?page=${pagina}&size=${size}${(searchText?.length ?? 0 > 0) ? '&searchTerm=' + searchText : ''}${emProgresso ? '&emProgresso=1' : ''}`,
    );
  }

  adicionarServico(servico: AdicionarServicoDTO) {
    return this.http.post(`${environment.apiURL}/${this.apiRoute}`, servico);
  }

  concluirServico(servico: Servico) {
    return this.http.post(`${environment.apiURL}/${this.apiRoute}/${servico.id}/concluir`, null);
  }

  editarServico(editServico: EditarServicoDTO, id: number) {
    return this.http.put(`${environment.apiURL}/${this.apiRoute}/${id}`, editServico);
  }
}
