package br.com.vaztech.vaztech.controller;

import br.com.vaztech.vaztech.dto.FuncionarioHistoricoDTO;
import br.com.vaztech.vaztech.dto.PessoaHistoricoDTO;
import br.com.vaztech.vaztech.dto.ProdutoHistoricoDTO;
import br.com.vaztech.vaztech.service.HistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/historico")
@CrossOrigin(origins = "*")
public class HistoricoController {

    @Autowired
    private HistoricoService historicoService;

    @GetMapping("/funcionario/{id}")
    public ResponseEntity<List<FuncionarioHistoricoDTO>> getHistoricoFuncionario(@PathVariable Integer id,
                                                                                 @RequestParam LocalDate dataInicio,
                                                                                 @RequestParam LocalDate dataFim) {
        try {
            List<FuncionarioHistoricoDTO> historico = historicoService.getHistoricoFuncionario(id, dataInicio, dataFim);
            return ResponseEntity.ok(historico);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/produto/{id}")
    public ResponseEntity<List<ProdutoHistoricoDTO>> getHistoricoProduto(@PathVariable Integer id) {
        try {
            List<ProdutoHistoricoDTO> historico = historicoService.getHistoricoProduto(id);
            return ResponseEntity.ok(historico);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pessoa/{id}")
    public ResponseEntity<List<PessoaHistoricoDTO>> getHistoricoPessoa(@PathVariable Integer id) {
        try {
            List<PessoaHistoricoDTO> historico = historicoService.getHistoricoPessoa(id);
            return ResponseEntity.ok(historico);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

