import { Component, EventEmitter, inject, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { AvatarModule } from 'primeng/avatar';
import { MessageModule } from 'primeng/message';
import { TabsModule } from 'primeng/tabs';
import { PessoaResponse } from '../../../../models/pessoa.model';
import { HistoricoPessoaItem } from '../../../../models/historico.model';
import { HistoricoService } from '../../../../services/historico.service';
import { MessageService } from 'primeng/api';

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

  historicoService = inject(HistoricoService);
  toastService = inject(MessageService);

  historico: HistoricoPessoaItem[] = [];
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

    this.historicoService.buscarHistoricoPessoa(this.pessoa.id).subscribe({
      next: (resultado) => {
        this.historico = resultado.sort(
          (a, b) => new Date(a.data).getTime() - new Date(b.data).getTime(),
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

  get operacoes(): HistoricoPessoaItem[] {
    return this.historico.filter((item) => item.label === 'Venda' || item.label === 'Compra' || item.label === 'Troca');
  }

  get servicos(): HistoricoPessoaItem[] {
    return this.historico.filter((item) => item.label !== 'Venda' && item.label !== 'Compra' && item.label !== 'Troca');
  }

  getSeveridadeLabel(label: string): 'success' | 'error' | 'warn' | 'info' {
    if (label === 'Venda') return 'success';
    if (label === 'Compra') return 'error';
    if (label === 'Troca') return 'warn';
    return 'info';
  }

  getCardSeverity(label: string): 'success' | 'danger' | undefined {
    if (label === 'Venda') return 'success';
    if (label === 'Compra') return 'danger';
    return undefined;
  }
}
