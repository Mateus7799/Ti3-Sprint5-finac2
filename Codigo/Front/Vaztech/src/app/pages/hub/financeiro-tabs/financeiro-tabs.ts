import { ChangeDetectorRef, Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { CommonModule, CurrencyPipe, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { SelectModule } from 'primeng/select';
import { ToastModule } from 'primeng/toast';
import { DividerModule } from 'primeng/divider';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { MessageService } from 'primeng/api';
import { FinanceiroService } from '../../../services/financeiro.service';
import { ChartModule } from 'primeng/chart';
import {
  ComparacaoMeses,
  DadosAnoVisaoGeral,
  DadosFinanceirosMes,
} from '../../../models/financeiro.model';

@Component({
  selector: 'app-financeiro-tabs',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ButtonModule,
    CardModule,
    ChartModule,
    DialogModule,
    SelectModule,
    ToastModule,
    DividerModule,
    ProgressSpinnerModule,
    CurrencyPipe,
  ],
  templateUrl: './financeiro-tabs.html',
  providers: [MessageService],
})
export class FinanceiroTabsComponent implements OnInit {
  platformId = inject(PLATFORM_ID);
  financeiroService = inject(FinanceiroService);
  toastService = inject(MessageService);

  dadosMesAtual: DadosFinanceirosMes | null = null;
  dadosMesAnterior: DadosFinanceirosMes | null = null;
  dadosComparacao: ComparacaoMeses | null = null;

  modalSeletorMesAberto: boolean = false;
  modalComparacaoAberto: boolean = false;

  anoSelecionado: number | null = null;
  mesSelecionado: number | null = null;

  dadosVisaoGeral: DadosAnoVisaoGeral | undefined;

  infoGrafico: any;
  optsGrafico: any;

  comparacao = {
    ano1: null as number | null,
    mes1: null as number | null,
    ano2: null as number | null,
    mes2: null as number | null,
  };

  anosDisponiveis: number[] = [];

  constructor(private cd: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.inicializarAnos();
    this.carregarMesAtual();
  }

  inicializarAnos(): void {
    const anoAtual = new Date().getFullYear();
    this.anosDisponiveis = [];
    for (let i = anoAtual; i >= anoAtual - 5; i--) {
      this.anosDisponiveis.push(i);
    }
  }

  calcularMesAnterior(ano: number, mes: number): { ano: number; mes: number } {
    if (mes === 1) {
      return { ano: ano - 1, mes: 12 };
    }
    return { ano, mes: mes - 1 };
  }

  carregarMesAtual(): void {
    const dataAtual = new Date();
    const mesAtual = dataAtual.getMonth() + 1;
    const anoAtual = dataAtual.getFullYear();
    this.mesSelecionado = mesAtual;
    this.anoSelecionado = anoAtual;

    const mesAnterior = this.calcularMesAnterior(anoAtual, mesAtual);

    this.financeiroService.buscarDadosMes(anoAtual, mesAtual).subscribe({
      next: (dados) => {
        this.dadosMesAtual = dados;
      },
      error: (err) => {
        console.error('Erro ao carregar dados financeiros:', err);
        this.dadosMesAtual = {
          faturamento: 0,
          custo: 0,
          lucro: 0,
          mes: mesAtual,
          ano: anoAtual,
        };
        this.toastService.add({
          severity: 'warn',
          summary: 'Dados não disponíveis',
          detail: 'Exibindo valores zerados. Verifique a conexão com o servidor.',
        });
      },
    });

    this.financeiroService.buscarDadosMes(mesAnterior.ano, mesAnterior.mes).subscribe({
      next: (dados) => {
        this.dadosMesAnterior = dados;
      },
      error: (err) => {
        console.error('Erro ao carregar dados do mês anterior:', err);
        this.dadosMesAnterior = {
          faturamento: 0,
          custo: 0,
          lucro: 0,
          mes: mesAnterior.mes,
          ano: mesAnterior.ano,
        };
      },
    });
  }

  abrirSeletorMes(): void {
    const dataAtual = new Date();
    this.anoSelecionado = dataAtual.getFullYear();
    this.mesSelecionado = dataAtual.getMonth() + 1;
    this.modalSeletorMesAberto = true;
  }

  confirmarSelecaoMes(): void {
    if (!this.anoSelecionado || !this.mesSelecionado) return;

    if (this.vendoVisaoGeral) {
      this.dadosMesAtual = null;
      this.dadosMesAnterior = null;
      this.buscarDadosVisaoGeral(this.anoSelecionado);
      return;
    }

    this.dadosVisaoGeral = undefined;

    const mesAnterior = this.calcularMesAnterior(this.anoSelecionado, this.mesSelecionado);

    this.financeiroService.buscarDadosMes(this.anoSelecionado, this.mesSelecionado).subscribe({
      next: (dados) => {
        this.dadosMesAtual = dados;
        this.modalSeletorMesAberto = false;
        this.toastService.add({
          severity: 'success',
          summary: 'Mês alterado',
          detail: `Exibindo dados de ${this.getNomeMes(this.mesSelecionado!)}/${this.anoSelecionado}`,
        });
      },
      error: (err) => {
        console.error('Erro ao carregar dados do mês selecionado:', err);
        this.dadosMesAtual = {
          faturamento: 0,
          custo: 0,
          lucro: 0,
          mes: this.mesSelecionado!,
          ano: this.anoSelecionado!,
        };
        this.modalSeletorMesAberto = false;
        this.toastService.add({
          severity: 'warn',
          summary: 'Dados não disponíveis',
          detail: 'Exibindo valores zerados para o mês selecionado.',
        });
      },
    });

    this.financeiroService.buscarDadosMes(mesAnterior.ano, mesAnterior.mes).subscribe({
      next: (dados) => {
        this.dadosMesAnterior = dados;
      },
      error: (err) => {
        console.error('Erro ao carregar dados do mês anterior:', err);
        this.dadosMesAnterior = {
          faturamento: 0,
          custo: 0,
          lucro: 0,
          mes: mesAnterior.mes,
          ano: mesAnterior.ano,
        };
      },
    });
  }

  buscarDadosVisaoGeral(ano: number) {
    this.financeiroService.buscarDadosAno(ano).subscribe({
      next: (dados) => {
        this.dadosVisaoGeral = { ...dados };
        this.carregarGrafico();
        console.log(this.dadosVisaoGeral);
      },
      error: (err) => {
        this.toastService.add({
          severity: 'error',
          summary: 'Ocorreu um erro!',
          detail: err.error.message,
        });
      },
    });
  }

  abrirModalComparacao(): void {
    this.comparacao = {
      ano1: null,
      mes1: null,
      ano2: null,
      mes2: null,
    };
    this.dadosComparacao = null;
    this.modalComparacaoAberto = true;
  }

  carregarGrafico() {
    if (!isPlatformBrowser(this.platformId)) return;
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--p-text-color');
    const textColorSecondary = documentStyle.getPropertyValue('--p-text-muted-color');
    const surfaceBorder = documentStyle.getPropertyValue('--p-content-border-color');
    this.infoGrafico = {
      labels: this.dadosVisaoGeral?.faturamento.faturamentoMensal.map((fm) => fm.mesNome),
      datasets: [
        {
          label: 'Faturamento',
          data: this.dadosVisaoGeral?.faturamento.faturamentoMensal.map((fm) => fm.total),
          fill: false,
          borderColor: documentStyle.getPropertyValue('--p-green-500'),
          tension: 0.4,
        },
        {
          label: 'Custos',
          data: this.dadosVisaoGeral?.custo.custoMensal.map((fm) => fm.total),
          fill: false,
          borderColor: documentStyle.getPropertyValue('--p-red-500'),
          tension: 0.4,
        },
        {
          label: 'Lucro',
          data: this.dadosVisaoGeral?.lucro.lucroMensal.map((fm) => fm.total),
          fill: false,
          borderColor: documentStyle.getPropertyValue('--p-primary-500'),
          tension: 0.4,
        },
      ],
    };
    this.optsGrafico = {
      responsive: true,
      maintainAspectRatio: false,
      aspectRatio: 0.9,
      plugins: {
        legend: {
          labels: {
            color: textColor,
          },
        },
      },
      scales: {
        x: {
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false,
          },
        },
        y: {
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false,
          },
        },
      },
    };
    this.cd.markForCheck();
  }

  realizarComparacao(): void {
    if (
      !this.comparacao.ano1 ||
      !this.comparacao.mes1 ||
      !this.comparacao.ano2 ||
      !this.comparacao.mes2
    ) {
      return;
    }

    if (
      this.comparacao.ano1 === this.comparacao.ano2 &&
      this.comparacao.mes1 === this.comparacao.mes2
    ) {
      this.toastService.add({
        severity: 'warn',
        summary: 'Meses iguais',
        detail: 'Selecione meses diferentes para comparação.',
      });
      return;
    }

    this.financeiroService.buscarDadosMes(this.comparacao.ano1, this.comparacao.mes1).subscribe({
      next: (dados1) => {
        this.financeiroService
          .buscarDadosMes(this.comparacao.ano2!, this.comparacao.mes2!)
          .subscribe({
            next: (dados2) => {
              this.dadosComparacao = this.calcularComparacao(dados1, dados2);
            },
            error: (err) => {
              console.error('Erro ao carregar dados do segundo mês:', err);
              const dados2Zerado: DadosFinanceirosMes = {
                faturamento: 0,
                custo: 0,
                lucro: 0,
                mes: this.comparacao.mes2!,
                ano: this.comparacao.ano2!,
              };
              this.dadosComparacao = this.calcularComparacao(dados1, dados2Zerado);
              this.toastService.add({
                severity: 'warn',
                summary: 'Dados parciais',
                detail: 'Segundo mês com valores zerados.',
              });
            },
          });
      },
      error: (err) => {
        console.error('Erro ao carregar dados do primeiro mês:', err);
        const dados1Zerado: DadosFinanceirosMes = {
          faturamento: 0,
          custo: 0,
          lucro: 0,
          mes: this.comparacao.mes1!,
          ano: this.comparacao.ano1!,
        };
        this.financeiroService
          .buscarDadosMes(this.comparacao.ano2!, this.comparacao.mes2!)
          .subscribe({
            next: (dados2) => {
              this.dadosComparacao = this.calcularComparacao(dados1Zerado, dados2);
              this.toastService.add({
                severity: 'warn',
                summary: 'Dados parciais',
                detail: 'Primeiro mês com valores zerados.',
              });
            },
            error: (err2) => {
              console.error('Erro ao carregar dados do segundo mês:', err2);
              const dados2Zerado: DadosFinanceirosMes = {
                faturamento: 0,
                custo: 0,
                lucro: 0,
                mes: this.comparacao.mes2!,
                ano: this.comparacao.ano2!,
              };
              this.dadosComparacao = this.calcularComparacao(dados1Zerado, dados2Zerado);
              this.toastService.add({
                severity: 'warn',
                summary: 'Dados não disponíveis',
                detail: 'Exibindo valores zerados para ambos os meses.',
              });
            },
          });
      },
    });
  }

  calcularComparacao(dados1: DadosFinanceirosMes, dados2: DadosFinanceirosMes): ComparacaoMeses {
    const calcularPercentual = (base: number, comparacao: number): number => {
      if (base === 0) return comparacao > 0 ? 100 : 0;
      return ((comparacao - base) / base) * 100;
    };

    return {
      mes1: dados1,
      mes2: dados2,
      diferencaPercentual: {
        faturamento: calcularPercentual(dados1.faturamento, dados2.faturamento),
        custo: calcularPercentual(dados1.custo, dados2.custo),
        lucro: calcularPercentual(dados1.lucro, dados2.lucro),
      },
    };
  }

  fecharModalComparacao(): void {
    this.modalComparacaoAberto = false;
    this.dadosComparacao = null;
  }

  getNomeMes(mes: number): string {
    return this.mesesDisponiveis.find((m) => m.valor === mes)?.nome || '';
  }

  get mesesDisponiveis() {
    return [
      { nome: 'Janeiro', valor: 1 },
      { nome: 'Fevereiro', valor: 2 },
      { nome: 'Março', valor: 3 },
      { nome: 'Abril', valor: 4 },
      { nome: 'Maio', valor: 5 },
      { nome: 'Junho', valor: 6 },
      { nome: 'Julho', valor: 7 },
      { nome: 'Agosto', valor: 8 },
      { nome: 'Setembro', valor: 9 },
      { nome: 'Outubro', valor: 10 },
      { nome: 'Novembro', valor: 11 },
      { nome: 'Dezembro', valor: 12 },
    ];
  }

  get mesesDisponiveisVisaoGeral() {
    return [
      { nome: 'Janeiro', valor: 1 },
      { nome: 'Fevereiro', valor: 2 },
      { nome: 'Março', valor: 3 },
      { nome: 'Abril', valor: 4 },
      { nome: 'Maio', valor: 5 },
      { nome: 'Junho', valor: 6 },
      { nome: 'Julho', valor: 7 },
      { nome: 'Agosto', valor: 8 },
      { nome: 'Setembro', valor: 9 },
      { nome: 'Outubro', valor: 10 },
      { nome: 'Novembro', valor: 11 },
      { nome: 'Dezembro', valor: 12 },
      { nome: 'Visão Geral', valor: 13 },
    ];
  }

  get vendoVisaoGeral() {
    return this.mesSelecionado === 13;
  }
}
