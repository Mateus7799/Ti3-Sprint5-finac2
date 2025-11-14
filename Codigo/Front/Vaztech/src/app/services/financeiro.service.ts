import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable, forkJoin, map } from 'rxjs';
import { catchError, of } from 'rxjs';
import {
  CustoAnualVisaoGeral,
  DadosAnoVisaoGeral,
  DadosFinanceirosMes,
  FaturamentoAnualVisaoGeral,
  FinanceiroFaturamentoResponseDTO,
  LucroAnualVisaoGeral,
} from '../models/financeiro.model';

interface FinanceiroCustoResponseDTO {
  custoMesAtual: number;
  anoMesAtual: string;
  custoMesComparacao: number | null;
  anoMesComparacao: string | null;
  margem: number | null;
}

interface FinanceiroLucroResponseDTO {
  lucroAtual: number;
  anoMesAtualStr: string;
  lucroComparacao: number | null;
  anoMesComparacaoStr: string | null;
  margem: number | null;
}

@Injectable({
  providedIn: 'root',
})
export class FinanceiroService {
  http = inject(HttpClient);
  apiRoute = 'api/financeiro';

  buscarDadosMes(ano: number, mes: number): Observable<DadosFinanceirosMes> {
    const faturamentoUrl = `${environment.apiURL}/${this.apiRoute}/faturamento-mensal?anoAtual=${ano}&mesAtual=${mes}`;
    const custoUrl = `${environment.apiURL}/${this.apiRoute}/custo-mensal?anoAtual=${ano}&mesAtual=${mes}`;
    const lucroUrl = `${environment.apiURL}/${this.apiRoute}/lucro-mensal?anoAtual=${ano}&mesAtual=${mes}`;

    return forkJoin({
      faturamento: this.http
        .get<FinanceiroFaturamentoResponseDTO>(faturamentoUrl)
        .pipe(catchError(() => of(null))),
      custo: this.http.get<FinanceiroCustoResponseDTO>(custoUrl).pipe(catchError(() => of(null))),
      lucro: this.http.get<FinanceiroLucroResponseDTO>(lucroUrl).pipe(catchError(() => of(null))),
    }).pipe(
      map((result) => {
        const faturamento = result.faturamento?.faturamentoMesAtual ?? 0;
        const custo = result.custo?.custoMesAtual ?? 0;
        const lucro = result.lucro?.lucroAtual ?? 0;

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

  buscarDadosAno(ano: number): Observable<DadosAnoVisaoGeral> {
    const faturamentoUrl = `${environment.apiURL}/${this.apiRoute}/faturamento-anual?ano=${ano}`;
    const custoUrl = `${environment.apiURL}/${this.apiRoute}/custo-anual?ano=${ano}`;
    const lucroUrl = `${environment.apiURL}/${this.apiRoute}/lucro-anual?ano=${ano}`;

    return forkJoin({
      faturamento: this.http.get<FaturamentoAnualVisaoGeral>(faturamentoUrl),
      custo: this.http.get<CustoAnualVisaoGeral>(custoUrl),
      lucro: this.http.get<LucroAnualVisaoGeral>(lucroUrl),
    });
  }

  private getNomeMes(mes: number): string {
    const meses = [
      'Janeiro',
      'Fevereiro',
      'Março',
      'Abril',
      'Maio',
      'Junho',
      'Julho',
      'Agosto',
      'Setembro',
      'Outubro',
      'Novembro',
      'Dezembro',
    ];
    return meses[mes - 1] || '';
  }
}
