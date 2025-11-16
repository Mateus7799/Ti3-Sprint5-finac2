import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormularioServicos } from './formulario-servicos/formulario-servicos';
import { MessageService } from 'primeng/api';
import { Servico, ServicosReqDTO, StatusServico } from '../../../models/servico.model';
import { ServicosService } from '../../../services/servicos.service';
import { ToastModule } from 'primeng/toast';
import { ToolbarModule } from 'primeng/toolbar';
import { IconFieldModule } from 'primeng/iconfield';
import { InputTextModule } from 'primeng/inputtext';
import { InputIconModule } from 'primeng/inputicon';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TabsModule } from 'primeng/tabs';
import { CardModule } from 'primeng/card';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { AvatarModule } from 'primeng/avatar';
import { MessageModule } from 'primeng/message';
import { FluidModule } from 'primeng/fluid';
import { FieldsetModule } from 'primeng/fieldset';
import { TruncatePipe } from '../../../pipes/truncate.pipe';
import { ToggleSwitchModule } from 'primeng/toggleswitch';

@Component({
  selector: 'app-servicos-tabs',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ButtonModule,
    FormularioServicos,
    MessageModule,
    FluidModule,
    ToastModule,
    ToolbarModule,
    IconFieldModule,
    InputTextModule,
    ToggleSwitchModule,
    TabsModule,
    CardModule,
    PaginatorModule,
    FieldsetModule,
    InputIconModule,
    CurrencyPipe,
    TruncatePipe,
    AvatarModule,
  ],
  templateUrl: './servicos-tabs.html',
  styleUrl: './servicos-tabs.css',
  providers: [MessageService],
})
export class ServicosTabsComponent implements OnInit {
  servicoService = inject(ServicosService);
  toastService = inject(MessageService);

  statusServico: StatusServico[] = [];
  servicos: Servico[] = [];
  servicosEdit: Servico[] = [];

  abaAtual: number = 0;

  searchText: string = '';

  paginaAtual: number = 0;
  itensPorPagina: number = 6;
  totalRegistros: number = 0;

  apenasEmProgresso: boolean = false;

  ngOnInit(): void {
    this.buscarStatusServico();
    this.buscarServicos();
  }

  onEditarServico(servico: Servico) {
    this.servicosEdit.push(structuredClone(servico));
    this.abaAtual = this.servicosEdit.length + 1;
  }

  buscarServicos(pagina?: number, size?: number) {
    this.servicoService
      .buscarServicos(
        pagina ?? this.paginaAtual,
        size ?? this.itensPorPagina,
        this.searchText,
        this.apenasEmProgresso,
      )
      .subscribe({
        next: (servicosReq: ServicosReqDTO) => {
          console.log(servicosReq);
          this.servicos = servicosReq.content.map((s) => {
            if (s.dataInicio) s.dataInicio = new Date(s.dataInicio + 'T00:00:00');
            if (s.dataFim) s.dataFim = new Date(s.dataFim + 'T00:00:00');
            return s;
          });
          this.totalRegistros = servicosReq.totalElements;
        },
        error: (err) => {
          console.error(err);
          this.toastService.add({
            severity: 'error',
            summary: 'O carregamento dos serviços falhou',
            detail: err.error.message,
          });
        },
      });
  }

  buscarStatusServico() {
    this.servicoService.listarServicosStatus().subscribe({
      next: (status: StatusServico[]) => {
        this.statusServico = status;
      },
      error: (err) => {
        console.error(err);
        this.toastService.add({
          severity: 'error',
          summary: 'Ocorreu um erro!',
          detail: err.error.message,
        });
      },
    });
  }

  onPesquisar() {
    this.buscarServicos();
  }

  onLimparPesquisa() {
    this.searchText = '';
    this.buscarServicos();
  }

  onPageChange(event: PaginatorState) {
    this.paginaAtual = event.page || 0;
    this.itensPorPagina = event.rows || 4;
    if (this.searchText.length <= 0) {
      this.buscarServicos();
    }
  }

  fecharAba(index: number, ev: any) {
    if (index >= 0) this.servicosEdit.splice(index, 1);
    console.log(index);
    console.log(this.servicosEdit);
    if (ev?.reload) {
      this.buscarServicos();
    }
    if (ev?.toast) {
      this.toastService.add(ev.toast);
    }
    this.abaAtual = 0;
  }

  irParaCadastro() {
    this.abaAtual = 1;
  }

  get finalizadoStatusId() {
    return (
      this.statusServico.find((status) => status.nome.toLowerCase().includes('finalizado'))?.id ?? 1
    );
  }

  onConcluirServico(servico: Servico) {
    this.servicoService.concluirServico(servico).subscribe({
      next: () => {
        this.buscarServicos();
        this.toastService.add({
          severity: 'success',
          summary: 'Serviço concluído!',
        });
      },
      error: (err) => {
        console.log(err);
        this.toastService.add({
          severity: 'error',
          summary: 'Não foi possível concluir o serviço',
          detail: err.error.message,
        });
      },
    });
  }

  getStatusLabel(id: number) {
    return this.statusServico.find((status) => status.id === id)?.nome ?? 'Indefinido';
  }
}
