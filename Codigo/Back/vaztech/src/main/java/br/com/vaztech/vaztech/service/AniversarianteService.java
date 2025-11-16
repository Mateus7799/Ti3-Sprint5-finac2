package br.com.vaztech.vaztech.service;

import br.com.vaztech.vaztech.dto.AniversarianteResponseDTO;
import br.com.vaztech.vaztech.entity.Funcionario;
import br.com.vaztech.vaztech.entity.Pessoa;
import br.com.vaztech.vaztech.repository.FuncionarioRepository;
import br.com.vaztech.vaztech.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AniversarianteService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    public List<AniversarianteResponseDTO> buscarAniversariantesDaSemana() {
        LocalDate hoje = LocalDate.now();

        LocalDate start = hoje.with(DayOfWeek.MONDAY);

        LocalDate end = hoje.with(DayOfWeek.SUNDAY);

        List<AniversarianteResponseDTO> aniversariantes = new ArrayList<>();

        if (start.getMonthValue() != end.getMonthValue()) {
            List<Pessoa> pessoasMes1 = pessoaRepository.findAniversariantesNoMes(
                    start.getDayOfMonth(),
                    start.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(),
                    start.getMonthValue()
            );
            pessoasMes1.forEach(p -> aniversariantes.add(new AniversarianteResponseDTO(p)));

            List<Pessoa> pessoasMes2 = pessoaRepository.findAniversariantesNoMes(
                    1,
                    end.getDayOfMonth(),
                    end.getMonthValue()
            );
            pessoasMes2.forEach(p -> aniversariantes.add(new AniversarianteResponseDTO(p)));

            List<Funcionario> funcMes1 = funcionarioRepository.findAniversariantesNoMes(
                    start.getDayOfMonth(),
                    start.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(),
                    start.getMonthValue()
            );
            funcMes1.forEach(f -> aniversariantes.add(new AniversarianteResponseDTO(f)));

            List<Funcionario> funcMes2 = funcionarioRepository.findAniversariantesNoMes(
                    1,
                    end.getDayOfMonth(),
                    end.getMonthValue()
            );
            funcMes2.forEach(f -> aniversariantes.add(new AniversarianteResponseDTO(f)));

        } else {
            List<Pessoa> pessoas = pessoaRepository.findAniversariantesNoMes(
                    start.getDayOfMonth(),
                    end.getDayOfMonth(),
                    start.getMonthValue()
            );
            pessoas.forEach(p -> aniversariantes.add(new AniversarianteResponseDTO(p)));

            List<Funcionario> funcionarios = funcionarioRepository.findAniversariantesNoMes(
                    start.getDayOfMonth(),
                    end.getDayOfMonth(),
                    start.getMonthValue()
            );
            funcionarios.forEach(f -> aniversariantes.add(new AniversarianteResponseDTO(f)));
        }

        aniversariantes.sort(Comparator
                .comparing((AniversarianteResponseDTO a) -> a.dataNascimento().getMonthValue())
                .thenComparing(a -> a.dataNascimento().getDayOfMonth()));

        return aniversariantes;
    }
}

