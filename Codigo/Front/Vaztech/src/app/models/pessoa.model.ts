export type PessoaResponse = {
  id: number;
  nome: string;
  contato: string;
  endereco?: string;
  observacoes?: string;
  cpfCnpj: string;
  dataNascimento: Date | null;
  origem: string | null;
};

export type CadastrarPessoaBody = {
  nome: string;
  cpfCnpj: string;
  dataNascimento: Date | null;
  origem: string | null;
  contato: string;
  endereco: string | null;
  observacoes: string | null;
};

export type AlterarPessoaBody = {
  id: number;
  nome: string;
  cpfCnpj: string;
  dataNascimento: Date | null;
  origem: string | null;
  contato: string;
  endereco: string | null;
  observacoes: string | null;
};

export type PessoasReqDTO = {
  content: PessoaResponse[];
  totalElements: number;
};
