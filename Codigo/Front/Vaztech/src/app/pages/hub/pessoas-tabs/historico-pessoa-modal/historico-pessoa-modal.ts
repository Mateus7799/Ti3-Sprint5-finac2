import { Component, EventEmitter, inject, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { AvatarModule } from 'primeng/avatar';
import { MessageModule } from 'primeng/message';
import { TabsModule } from 'primeng/tabs';
import { PessoaResponse } from '../../../../models/pessoa.model';
import { Operacao } from '../../../../models/operacao.model';
import { Servico } from '../../../../models/servico.model';
import { OperacoesService } from '../../../../services/operacoes.service';
import { ServicosService } from '../../../../services/servicos.service';
import { MessageService } from 'primeng/api';
import { forkJoin } from 'rxjs';

type HistoricoItem = {
  tipo: 'operacao' | 'servico';
  data: Date;
  dados: Operacao | Servico;
};

@Component({
  selector: 'app-historico-pessoa-modal',
  standalone: true,
  imports: [
    CommonModule,
    DialogModule,
    ButtonModule,
    CardModule,
    AvatarModule,
    MessageModule,
    TabsModule,
    DatePipe,
    CurrencyPipe,
  ],
  templateUrl: './historico-pessoa-modal.html',
  styleUrl: './historico-pessoa-modal.css',
})
export class HistoricoPessoaModal implements OnChanges {
  @Input() pessoa: PessoaResponse | undefined;
  @Input() visible: boolean = false;
  @Output() visibleChange = new EventEmitter<boolean>();

  operacoesService = inject(OperacoesService);
  servicosService = inject(ServicosService);
  toastService = inject(MessageService);

  historico: HistoricoItem[] = [];
  carregando: boolean = false;
  abaAtual: number = 0;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible && this.pessoa) {
      this.carregarHistorico();
    }
  }

  fecharModal() {
    this.visible = false;
    this.visibleChange.emit(false);
  }

  carregarHistorico() {
    if (!this.pessoa) return;

    this.carregando = true;
    this.historico = [];

    const cpfCnpj = this.pessoa.cpfCnpj;

    forkJoin({
      operacoes: this.operacoesService.listarOperacoes('vendas', 0, 999, cpfCnpj),
      compras: this.operacoesService.listarOperacoes('compras', 0, 999, cpfCnpj),
      servicos: this.servicosService.buscarServicos(0, 999, cpfCnpj),
    }).subscribe({
      next: (resultado) => {
        const operacoesVenda = resultado.operacoes.content
          .filter((op) => op.pessoa.id === this.pessoa?.id)
          .map((op) => ({
            tipo: 'operacao' as const,
            data: new Date(op.dataHoraTransacao),
            dados: op,
          }));

        const operacoesCompra = resultado.compras.content
          .filter((op) => op.pessoa.id === this.pessoa?.id)
          .map((op) => ({
            tipo: 'operacao' as const,
            data: new Date(op.dataHoraTransacao),
            dados: op,
          }));

        const servicos = resultado.servicos.content
          .filter((serv) => serv.pessoa?.id === this.pessoa?.id)
          .map((serv) => ({
            tipo: 'servico' as const,
            data: new Date(serv.dataInicio),
            dados: serv,
          }));

        this.historico = [...operacoesVenda, ...operacoesCompra, ...servicos].sort(
          (a, b) => a.data.getTime() - b.data.getTime(),
        );

        this.carregando = false;
      },
      error: (err) => {
        console.error(err);
        this.toastService.add({
          summary: 'Erro ao carregar histórico!',
          detail: 'Não foi possível carregar o histórico da pessoa.',
          severity: 'error',
        });
        this.carregando = false;
      },
    });
  }

  get operacoes(): HistoricoItem[] {
    return this.historico.filter((item) => item.tipo === 'operacao');
  }

  get servicos(): HistoricoItem[] {
    return this.historico.filter((item) => item.tipo === 'servico');
  }

  getOperacao(item: HistoricoItem): Operacao {
    return item.dados as Operacao;
  }

  getServico(item: HistoricoItem): Servico {
    return item.dados as Servico;
  }

  getTipoOperacao(tipo: number): string {
    if (tipo === 0) return 'Venda';
    if (tipo === 1) return 'Compra';
    return 'Troca';
  }

  getSeveridadeOperacao(tipo: number): 'success' | 'error' | 'warn' {
    if (tipo === 0) return 'success';
    if (tipo === 1) return 'error';
    return 'warn';
  }
}
