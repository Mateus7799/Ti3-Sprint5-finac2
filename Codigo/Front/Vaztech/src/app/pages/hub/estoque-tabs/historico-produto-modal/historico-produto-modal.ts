import { Component, EventEmitter, inject, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { AvatarModule } from 'primeng/avatar';
import { MessageModule } from 'primeng/message';
import { TabsModule } from 'primeng/tabs';
import { Produto } from '../../../../models/produto.model';
import { HistoricoProdutoItem } from '../../../../models/historico.model';
import { HistoricoService } from '../../../../services/historico.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-historico-produto-modal',
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
  templateUrl: './historico-produto-modal.html',
  styleUrl: './historico-produto-modal.css',
})
export class HistoricoProdutoModal implements OnChanges {
  @Input() produto: Produto | undefined;
  @Input() visible: boolean = false;
  @Output() visibleChange = new EventEmitter<boolean>();

  historicoService = inject(HistoricoService);
  toastService = inject(MessageService);

  historico: HistoricoProdutoItem[] = [];
  carregando: boolean = false;
  abaAtual: number = 0;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible && this.produto) {
      this.carregarHistorico();
    }
  }

  fecharModal() {
    this.visible = false;
    this.visibleChange.emit(false);
  }

  carregarHistorico() {
    if (!this.produto || !this.produto.id) return;

    this.carregando = true;
    this.historico = [];

    this.historicoService.buscarHistoricoProduto(this.produto.id).subscribe({
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
          detail: 'Não foi possível carregar o histórico do produto.',
          severity: 'error',
        });
        this.carregando = false;
      },
    });
  }

  get operacoes(): HistoricoProdutoItem[] {
    return this.historico.filter((item) => item.label === 'Venda' || item.label === 'Compra' || item.label === 'Troca');
  }

  get servicos(): HistoricoProdutoItem[] {
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
