export type DadosFinanceirosMes = {
  faturamento: number;
  custo: number;
  lucro: number;
  mes: number;
  ano: number;
  mesNome?: string;
};

export type ComparacaoMeses = {
  mes1: DadosFinanceirosMes;
  mes2: DadosFinanceirosMes;
  diferencaPercentual: {
    faturamento: number;
    custo: number;
    lucro: number;
  };
};

export type DadosAnoVisaoGeral = {
  faturamento: FaturamentoAnualVisaoGeral;
  custo: CustoAnualVisaoGeral;
  lucro: LucroAnualVisaoGeral;
};

export type FaturamentoAnualVisaoGeral = {
  ano: number;
  faturamentoTotalAnual: number;
  faturamentoMensal: DadosMesVisaoGeral[];
};

export type CustoAnualVisaoGeral = {
  ano: number;
  custoTotalAnual: number;
  custoMensal: DadosMesVisaoGeral[];
};

export type LucroAnualVisaoGeral = {
  ano: number;
  lucroTotalAnual: number;
  lucroMensal: DadosMesVisaoGeral[];
};

export type DadosMesVisaoGeral = {
  mes: number;
  mesNome: string;
  total: number;
};

export type FinanceiroFaturamentoResponseDTO = {
  faturamentoMesAtual: number;
  anoMesAtual: string;
  faturamentoMesComparacao: number | null;
  anoMesComparacao: string | null;
  margem: number | null;
};
