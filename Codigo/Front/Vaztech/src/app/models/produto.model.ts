// export type ItemEstoqueStatus = 'EM ESTOQUE' | 'VENDIDO' | 'ATENÇÃO';

export type ProdutoStatus = {
  id: number;
  nome: string;
};

export type Produto = {
  id?: number;
  numeroSerie: string;
  aparelho: string;
  modelo?: string;
  observacoes?: string;
  status?: number;
  cor?: string;
};

export type ProdutosReqDTO = {
  content: Produto[];
  totalElements: number;
  totalPages: number;
  size: number;
};
