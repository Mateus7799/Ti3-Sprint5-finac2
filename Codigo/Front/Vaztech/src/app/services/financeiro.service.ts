import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable, map } from 'rxjs';
import {
  DadosFinanceirosMes,
  FinanceiroFaturamentoResponseDTO,
} from '../models/financeiro.model';

@Injectable({
  providedIn: 'root',
})
export class FinanceiroService {
  http = inject(HttpClient);
  apiRoute = 'api/financeiro';

  buscarDadosMes(ano: number, mes: number): Observable<DadosFinanceirosMes> {
    return this.http.get<FinanceiroFaturamentoResponseDTO>(
      `${environment.apiURL}/${this.apiRoute}/faturamento-mensal?anoAtual=${ano}&mesAtual=${mes}`,
    ).pipe(
      map((result) => {
        const faturamento = result.faturamentoMesAtual || 0;
        const custo = 0;
        const lucro = faturamento - custo;

        return {
          faturamento,
          custo,
          lucro,
          mes,
          ano,
          mesNome: this.getNomeMes(mes),
        };
      }),
    );
  }

  private getNomeMes(mes: number): string {
    const meses = [
      'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
      'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
    ];
    return meses[mes - 1] || '';
  }
}
