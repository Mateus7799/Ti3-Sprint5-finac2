package br.com.vaztech.vaztech.service;

import br.com.vaztech.vaztech.dto.*;
import br.com.vaztech.vaztech.entity.*;
import br.com.vaztech.vaztech.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ServicoService {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private StatusServicoRepository statusServicoRepository;

    @Autowired
    private StatusProdutoRepository statusProdutoRepository;

    @Autowired
    private MetodoPagamentoRepository metodoPagamentoRepository;

    private static final Integer EM_ANDAMENTO = 1;
    private static final Integer CONCLUIDO = 2;

    private static final Integer EXTERNO = 1;
    private static final Integer INTERNO = 2;

    private static final Integer EM_SERVICO = 2;

    public Page<ServicoResponseDTO> listarServicosPaginados(Integer emProgresso, String searchTerm, int page, int size) {
        PageRequest pageRequestUnsorted = PageRequest.of(page, size);

        if ((searchTerm == null || searchTerm.isBlank()) && emProgresso == null) {
            PageRequest pageRequestSorted = PageRequest.of(page, size, Sort.Direction.DESC, "id");
            return servicoRepository.findAll(pageRequestSorted)
                    .map(ServicoResponseDTO::new);
        }

        return servicoRepository.buscarServicosPaginados(emProgresso, (searchTerm == null || searchTerm.isBlank()) ? null : searchTerm, pageRequestUnsorted)
                .map(ServicoResponseDTO::new);
    }

    public List<ServicoStatusDTO> listarServicoStatus() {
        return statusServicoRepository.findAll()
                .stream()
                .map(status -> new ServicoStatusDTO(status.getId(), status.getNome()))
                .toList();
    }

    public ServicoResponseDTO buscarPorId(Integer id) throws ResponseStatusException {
        try {
            Servico servico = servicoRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado com ID: " + id));
            return new ServicoResponseDTO(servico);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar serviço: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ServicoResponseDTO criarServico(ServicoAddRequestDTO dto) throws ResponseStatusException {
        try {
            Produto produto = produtoRepository.findByNumeroSerie(dto.numeroSerieProduto())
                    .orElse(null);

            if (produto == null) {
                if (dto.produto() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Produto não encontrado e dados do produto não foram enviados para criação automática.");
                }

                ProdutoResponseDTO novoProduto = produtoService.produtoAdd(dto.produto());

                produto = produtoRepository.findByNumeroSerie(dto.produto().numeroSerie())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Erro ao criar o produto automaticamente."));
            }

            Pessoa pessoa;
            if (dto.tipo().equals(EXTERNO)) {
                if (dto.idPessoa() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Serviço externo exige o ID da pessoa.");
                }

                pessoa = pessoaRepository.findById(dto.idPessoa())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Pessoa não encontrada com ID: " + dto.idPessoa()));
            } else if (dto.tipo().equals(INTERNO)) {
                pessoa = null;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Tipo de serviço inválido. Use 1 (externo) ou 2 (interno).");
            }

            MetodoPagamento metodoPagamento = metodoPagamentoRepository.findById(dto.metodoPagamento())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Método de pagamento não encontrado com ID: " + dto.metodoPagamento()));

            StatusServico statusServico = statusServicoRepository.findById(EM_ANDAMENTO)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro com status do serviço."));

            StatusProduto statusProduto = statusProdutoRepository.findById(EM_SERVICO)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro com status do produto."));

            produto.setStatus(statusProduto);

            produtoRepository.save(produto);

            Servico servico = new Servico();
            servico.setProduto(produto);
            servico.setTipo(dto.tipo());
            servico.setValor(dto.valor());
            servico.setPessoa(pessoa);
            servico.setDataInicio(LocalDate.now());
            servico.setObservacoes(dto.observacoes());
            servico.setStatus(statusServico);
            servico.setMetodoPagamento(metodoPagamento);

            Servico servicoSalvo = servicoRepository.save(servico);

            return new ServicoResponseDTO(servicoSalvo);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar serviço: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ResponseEntity<?> concluirServico (Integer id) {
        Servico servico = servicoRepository.findById(id).
                orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado com ID: " + id));

        StatusServico status = statusServicoRepository.findById(CONCLUIDO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro com status do serviço."));

        Produto produto = produtoRepository.findById(servico.getProduto().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro com produto."));


        servico.setStatus(status);
        servico.setDataFim(LocalDate.now());

        produto.setStatus(null);

        produtoRepository.save(produto);
        servicoRepository.save(servico);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Transactional
    public ServicoResponseDTO atualizarServico(Integer id, ServicoUpdateRequestDTO dto) throws ResponseStatusException {
        try {
            Servico servico = servicoRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado com ID: " + id));


            if (dto.valor() != null) {
                servico.setValor(dto.valor());
            }
            if (dto.observacoes() != null) {
                servico.setObservacoes(dto.observacoes());
            }
            if (dto.idStatus() != null) {
                StatusServico status = statusServicoRepository.findById(dto.idStatus())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status não encontrado com ID: " + dto.idStatus()));
                servico.setStatus(status);
            }

            if (dto.metodoPagamento() != null) {
                MetodoPagamento metodoPagamento = metodoPagamentoRepository.findById(dto.metodoPagamento())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Método de pagamento não encontrado com ID: " + dto.metodoPagamento()));
                servico.setMetodoPagamento(metodoPagamento);
            }

            Servico servicoAtualizado = servicoRepository.save(servico);

            return new ServicoResponseDTO(servicoAtualizado);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar serviço: " + e.getMessage(), e);
        }
    }
}
