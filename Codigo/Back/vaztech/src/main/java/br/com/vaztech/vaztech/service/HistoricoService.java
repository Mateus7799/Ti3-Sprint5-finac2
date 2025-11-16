package br.com.vaztech.vaztech.service;

import br.com.vaztech.vaztech.dto.FuncionarioHistoricoDTO;
import br.com.vaztech.vaztech.dto.PessoaHistoricoDTO;
import br.com.vaztech.vaztech.dto.ProdutoHistoricoDTO;
import br.com.vaztech.vaztech.entity.Operacao;
import br.com.vaztech.vaztech.entity.Servico;
import br.com.vaztech.vaztech.repository.FuncionarioRepository;
import br.com.vaztech.vaztech.repository.OperacaoRepository;
import br.com.vaztech.vaztech.repository.PessoaRepository;
import br.com.vaztech.vaztech.repository.ProdutoRepository;
import br.com.vaztech.vaztech.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricoService {

    @Autowired
    private OperacaoRepository operacaoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Transactional
    public List<FuncionarioHistoricoDTO> getHistoricoFuncionario(Integer funcionarioId, LocalDate dataInicio, LocalDate dataFim) {
        funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));

        LocalDateTime dataInicioTime = dataInicio.atStartOfDay();
        LocalDateTime dataFimTime = dataFim.atTime(23, 59, 59);

        List<Operacao> operacoes = operacaoRepository.findByFuncionarioAndDateRange(
                funcionarioId, dataInicioTime, dataFimTime);

        return operacoes.stream()
                .map(op -> new FuncionarioHistoricoDTO(
                        "Operação de " + (op.getTipo() == 0 ? "Venda" : "Compra") + " #" + op.getId(),
                        new FuncionarioHistoricoDTO.PessoaDTO(
                                op.getPessoa().getId(),
                                op.getPessoa().getNome(),
                                op.getPessoa().getCpfCnpj()
                        ),
                        new FuncionarioHistoricoDTO.ProdutoDTO(
                                op.getProduto().getId(),
                                op.getProduto().getNumeroSerie(),
                                op.getProduto().getAparelho(),
                                op.getProduto().getModelo()
                        ),
                        op.getDataHoraTransacao().toLocalDate(),
                        null,
                        op.getValor()
                ))
                .collect(Collectors.toList());
    }

    public List<ProdutoHistoricoDTO> getHistoricoProduto(Integer produtoId) {
        produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        List<ProdutoHistoricoDTO> historico = new ArrayList<>();

        List<Operacao> operacoes = operacaoRepository.findByProdutoId(produtoId);
        for (Operacao op : operacoes) {
            historico.add(new ProdutoHistoricoDTO(
                    "Operação de " + (op.getTipo() == 0 ? "Venda" : "Compra") + " #" + op.getId(),
                    new ProdutoHistoricoDTO.PessoaDTO(
                            op.getPessoa().getId(),
                            op.getPessoa().getNome(),
                            op.getPessoa().getCpfCnpj()
                    ),
                    op.getDataHoraTransacao().toLocalDate(),
                    null,
                    new ProdutoHistoricoDTO.FuncionarioDTO(
                            op.getFuncionario().getId(),
                            op.getFuncionario().getNome(),
                            op.getFuncionario().getCpf()
                    ),
                    op.getValor()
            ));
        }

        List<Servico> servicos = servicoRepository.findByProdutoId(produtoId);
        for (Servico servico : servicos) {
            historico.add(new ProdutoHistoricoDTO(
                    "Serviço " + (servico.getTipo() == 1 ? "Externo" : "Interno") + " #" + servico.getId(),
                    servico.getPessoa() != null ? new ProdutoHistoricoDTO.PessoaDTO(
                            servico.getPessoa().getId(),
                            servico.getPessoa().getNome(),
                            servico.getPessoa().getCpfCnpj()
                    ) : null,
                    servico.getDataInicio(),
                    servico.getDataFim(),
                    null,
                    servico.getValor()
            ));
        }

        historico.sort((a, b) -> {
            LocalDate dataA = a.data();
            LocalDate dataB = b.data();
            return dataB.compareTo(dataA);
        });

        return historico;
    }

    public List<PessoaHistoricoDTO> getHistoricoPessoa(Integer pessoaId) {
        pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa não encontrado"));

        List<PessoaHistoricoDTO> historico = new ArrayList<>();

        List<Operacao> operacoes = operacaoRepository.findByPessoaId(pessoaId);
        for (Operacao op : operacoes) {
            historico.add(new PessoaHistoricoDTO(
                    "Operação de " + (op.getTipo() == 0 ? "Venda" : "Compra") + " #" + op.getId(),
                    new PessoaHistoricoDTO.ProdutoDTO(
                            op.getProduto().getId(),
                            op.getProduto().getNumeroSerie(),
                            op.getProduto().getAparelho(),
                            op.getProduto().getModelo()
                    ),
                    op.getDataHoraTransacao().toLocalDate(),
                    null,
                    new PessoaHistoricoDTO.FuncionarioDTO(
                            op.getFuncionario().getId(),
                            op.getFuncionario().getNome(),
                            op.getFuncionario().getCpf()
                    ),
                    op.getValor()
            ));
        }

        List<Servico> servicos = servicoRepository.findByPessoaId(pessoaId);
        for (Servico servico : servicos) {
            historico.add(new PessoaHistoricoDTO(
                    "Serviço " + (servico.getTipo() == 1 ? "Externo" : "Interno") + " #" + servico.getId(),
                    new PessoaHistoricoDTO.ProdutoDTO(
                            servico.getProduto().getId(),
                            servico.getProduto().getNumeroSerie(),
                            servico.getProduto().getAparelho(),
                            servico.getProduto().getModelo()
                    ),
                    servico.getDataInicio(),
                    servico.getDataFim(),
                    null,
                    servico.getValor()
            ));
        }

        historico.sort((a, b) -> {
            LocalDate dataA = a.data();
            LocalDate dataB = b.data();
            return dataB.compareTo(dataA);
        });

        return historico;
    }
}

