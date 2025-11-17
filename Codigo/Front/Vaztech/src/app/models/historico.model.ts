export type HistoricoPessoaItem = {
  label: string;
  produto?: {
    id: number;
    numeroSerie: string;
    aparelho: string;
    modelo: string;
  };
  data: string;
  dataFim: string | null;
  funcionario?: {
    id: number;
    nome: string;
    cpf: string;
  };
  valor: number;
};

export type HistoricoProdutoItem = {
  label: string;
  pessoa?: {
    id: number;
    nome: string;
    cpfCnpj: string;
  };
  data: string;
  dataFim: string | null;
  funcionario?: {
    id: number;
    nome: string;
    cpf: string;
  };
  valor: number;
};
