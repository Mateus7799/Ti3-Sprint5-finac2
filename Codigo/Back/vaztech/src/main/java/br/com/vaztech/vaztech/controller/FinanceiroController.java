package br.com.vaztech.vaztech.controller;

import br.com.vaztech.vaztech.dto.*;
import br.com.vaztech.vaztech.service.FinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/financeiro")
public class FinanceiroController {

    @Autowired
    private FinanceiroService financeiroService;

    @GetMapping("/faturamento-mensal")
    public ResponseEntity<FinanceiroFaturamentoResponseDTO> getFaturamentoMensal(
            @RequestParam("anoAtual") Integer anoAtual,
            @RequestParam("mesAtual") Integer mesAtual,
            @RequestParam(value = "anoComparacao", required = false) Integer anoComparacao,
            @RequestParam(value = "mesComparacao", required = false) Integer mesComparacao) {

        if ((anoComparacao != null && mesComparacao == null) || (anoComparacao == null && mesComparacao != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Para comparação, 'anoComparacao' e 'mesComparacao' devem ser fornecidos em conjunto.");
        }

        FinanceiroFaturamentoResponseDTO response = financeiroService.getFaturamentoMensal(
                anoAtual, mesAtual, anoComparacao, mesComparacao);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/faturamento-anual")
    public ResponseEntity<FinanceiroFaturamentoAnualResponseDTO> getFaturamentoAnual(
            @RequestParam("ano") Integer ano) {

        if (ano == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O parâmetro 'ano' é obrigatório.");
        }

        FinanceiroFaturamentoAnualResponseDTO response = financeiroService.getFaturamentoAnual(ano);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lucro-mensal")
    public ResponseEntity<FinanceiroLucroResponseDTO> getLucroMensal(
            @RequestParam("anoAtual") Integer anoAtual,
            @RequestParam("mesAtual") Integer mesAtual,
            @RequestParam(value = "anoComparacao", required = false) Integer anoComparacao,
            @RequestParam(value = "mesComparacao", required = false) Integer mesComparacao) {

        if ((anoComparacao != null && mesComparacao == null) || (anoComparacao == null && mesComparacao != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Para comparação, 'anoComparacao' e 'mesComparacao' devem ser fornecidos em conjunto.");
        }

        FinanceiroLucroResponseDTO response = financeiroService.getLucroMensal(
                anoAtual, mesAtual, anoComparacao, mesComparacao);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/custo-mensal")
    public ResponseEntity<FinanceiroCustoResponseDTO> getCustoMensal(
            @RequestParam("anoAtual") Integer anoAtual,
            @RequestParam("mesAtual") Integer mesAtual,
            @RequestParam(value = "anoComparacao", required = false) Integer anoComparacao,
            @RequestParam(value = "mesComparacao", required = false) Integer mesComparacao) {

        if ((anoComparacao != null && mesComparacao == null) || (anoComparacao == null && mesComparacao != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Para comparação, 'anoComparacao' e 'mesComparacao' devem ser fornecidos em conjunto.");
        }

        FinanceiroCustoResponseDTO response = financeiroService.getCustoMensal(
                anoAtual, mesAtual, anoComparacao, mesComparacao);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/custo-anual")
    public ResponseEntity<FinanceiroCustoAnualResponseDTO> getCustoAnual(
            @RequestParam("ano") Integer ano) {

        if (ano == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O parâmetro 'ano' é obrigatório.");
        }

        FinanceiroCustoAnualResponseDTO response = financeiroService.getCustoAnual(ano);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/lucro-anual")
    public ResponseEntity<FinanceiroLucroAnualResponseDTO> getLucroAnual(
            @RequestParam("ano") Integer ano) {

        if (ano == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O parâmetro 'ano' é obrigatório.");
        }

        FinanceiroLucroAnualResponseDTO response = financeiroService.getLucroAnual(ano);
        return ResponseEntity.ok(response);
    }
}