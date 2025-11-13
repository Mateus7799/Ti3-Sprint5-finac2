package br.com.vaztech.vaztech.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class FinanceiroLucroResponseDTO {

    private BigDecimal lucroAtual;
    private String anoMesAtualStr;
    private BigDecimal lucroComparacao;
    private String anoMesComparacaoStr;

    //A margem de crescimento/redução percentual.
    //Fórmula: ((lucroAtual - lucroComparacao) / lucroComparacao) * 100
    //Será nulo se nenhum mês de comparação for solicitado.
    private BigDecimal margem;
}