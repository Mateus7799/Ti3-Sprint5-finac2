import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable, forkJoin, map, of, catchError } from 'rxjs';
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
    return forkJoin({
      faturamento: this.http.get<FinanceiroFaturamentoResponseDTO>(
        `${environment.apiURL}/${this.apiRoute}/faturamento-mensal?anoAtual=${ano}&mesAtual=${mes}`,
      ).pipe(
        map(result => result.faturamentoMesAtual || 0),
        catchError(() => of(0))
      ),
      custo: this.http.get<FinanceiroFaturamentoResponseDTO>(
        `${environment.apiURL}/${this.apiRoute}/faturamento-mensal?anoAtual=${ano}&mesAtual=${mes}`,
      ).pipe(
        map(() => 0),
        catchError(() => of(0))
      )
    }).pipe(
      map(({ faturamento, custo }) => {
        const lucro = faturamento - custo;

        return {
          faturamento,
          custo,
          lucro,
          mes,
          ano,
          mesNome: this.getNomeMes(mes),
        };
      })
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
