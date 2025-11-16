package br.com.vaztech.vaztech.service;

import br.com.vaztech.vaztech.dto.*;
import br.com.vaztech.vaztech.entity.Pessoa;
import br.com.vaztech.vaztech.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class PessoaService {

    @Autowired
    PessoaRepository pessoaRepository;

    public List<PessoaBuscarResponseDTO> buscarPessoas(String query) {
        return pessoaRepository.findTop50ByNomeOrCpfLike(query).stream().map(PessoaBuscarResponseDTO::new).toList();
    }

    public Page<PessoaResponseDTO> buscarPessoasPaginadas(String searchTerm, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "id");

        if (searchTerm == null || searchTerm.isBlank()) {
            return pessoaRepository.findAll(pageRequest)
                    .map(PessoaResponseDTO::new);
        }

        return pessoaRepository.buscarPessoasPaginadas(searchTerm.toLowerCase(), pageRequest)
                .map(PessoaResponseDTO::new);
    }

    public List<PessoaAniversarioResponseDTO> buscarAniversariantesDaSemana() {

        LocalDate hoje = LocalDate.now();

        // Segunda-feira da semana atual
        LocalDate start = hoje.with(DayOfWeek.MONDAY);

        // Domingo da semana atual
        LocalDate end = hoje.with(DayOfWeek.SUNDAY);

        // Se o range cruzar dois meses (ex: final de janeiro → início de fevereiro)
        if (start.getMonthValue() != end.getMonthValue()) {
            List<PessoaAniversarioResponseDTO> lista = new java.util.ArrayList<>();

            // Buscar no mês do início
            lista.addAll(pessoaRepository.findAniversariantesNoMes(
                    start.getDayOfMonth(),
                    start.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(),
                    start.getMonthValue()
            ));

            // Buscar no mês do fim
            lista.addAll(pessoaRepository.findAniversariantesNoMes(
                    1,
                    end.getDayOfMonth(),
                    end.getMonthValue()
            ));

            return lista;
        }

        // Semana no mesmo mês
        return pessoaRepository.findAniversariantesNoMes(
                start.getDayOfMonth(),
                end.getDayOfMonth(),
                start.getMonthValue()
        );
    }

    @Transactional
    public PessoaResponseDTO criarPessoa(PessoaAddRequestDTO dto) throws ResponseStatusException {
        try {
            if(pessoaRepository.existsByCpfCnpj(dto.cpfCnpj())){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF/CNPJ já cadastrado.");
            }

            Pessoa novaPessoa = new Pessoa();
            novaPessoa.setNome(dto.nome());
            novaPessoa.setCpfCnpj(dto.cpfCnpj());
            novaPessoa.setDataNascimento(dto.dataNascimento());
            novaPessoa.setOrigem(dto.origem());
            novaPessoa.setEndereco(dto.endereco());
            novaPessoa.setContato(dto.contato());
            novaPessoa.setObservacoes(dto.observacoes());

            Pessoa pessoaSalva = pessoaRepository.save(novaPessoa);

            return new PessoaResponseDTO(pessoaSalva);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar pessoa: " + e.getMessage(), e);
        }
    }

    @Transactional
    public PessoaResponseDTO atualizarPessoa(Integer id, PessoaUpdateRequestDTO dto) throws ResponseStatusException {
        try {
            Pessoa pessoa = pessoaRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa não encontrada com ID: " + id));

            if (dto.cpfCnpj() != null && pessoaRepository.existsByCpfCnpjAndIdNot(dto.cpfCnpj(), id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF/CNPJ já cadastrado para outra pessoa.");
            }

            if (dto.nome() != null) {
                pessoa.setNome(dto.nome());
            }
            if (dto.cpfCnpj() != null) {
                pessoa.setCpfCnpj(dto.cpfCnpj());
            }
            if (dto.dataNascimento() != null) {
                pessoa.setDataNascimento(dto.dataNascimento());
            }
            if (dto.origem() != null) {
                pessoa.setOrigem(dto.origem());
            }
            if (dto.endereco() != null) {
                pessoa.setEndereco(dto.endereco());
            }
            if (dto.contato() != null) {
                pessoa.setContato(dto.contato());
            }
            if (dto.observacoes() != null) {
                pessoa.setObservacoes(dto.observacoes());
            }

            Pessoa pessoaAtualizada = pessoaRepository.save(pessoa);

            return new PessoaResponseDTO(pessoaAtualizada);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar pessoa: " + e.getMessage(), e);
        }
    }
}
