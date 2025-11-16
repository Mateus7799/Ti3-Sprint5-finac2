import { PessoaResponse } from './pessoa.model';
import { Produto } from './produto.model';

export enum TiposServico {
  EXTERNO = 1,
  INTERNO = 2,
}

export type StatusServico = {
  id: number;
  nome: string;
};

export type Servico = {
  id: number;
  formaPagamento: number;
  produto: Produto;
  tipo: TiposServico;
  valor: number;
  pessoa: PessoaResponse;
  dataInicio: Date;
  dataFim: Date;
  observacoes: string;
  status: number;
};

export type PessoaServico = {
  id: number;
  nome: string;
  cpfCnpj: string;
};

export type ProdutoServico = {
  id: number;
  numeroSerie: string;
  aparelho: string;
  modelo: string;
};

export type AdicionarServicoDTO = {
  numeroSerieProduto: string | null;
  valor: number;
  idPessoa?: number;
  metodoPagamento: number;
  tipo: TiposServico;
  observacoes?: string;
  produto?: Produto;
};

export type EditarServicoDTO = {
  valor?: number;
  metodoPagamento?: number;
  observacoes?: string;
};

export type ServicosReqDTO = {
  content: Servico[];
  totalElements: number;
  totalPages: number;
  size: number;
};
