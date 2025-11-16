package br.com.vaztech.vaztech.repository;

import br.com.vaztech.vaztech.dto.FaturamentoPorMesDTO; // Importar DTO
import br.com.vaztech.vaztech.entity.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Integer> {

    @Query("SELECT s FROM Servico s " +
            "WHERE ( :emProgresso IS NULL OR s.status.id = :emProgresso ) " +
            "AND ( :searchTerm IS NULL OR :searchTerm = '' " +
            "OR CAST(s.id AS string) = :searchTerm " +
            "OR LOWER(s.pessoa.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(s.produto.numeroSerie) LIKE LOWER(CONCAT(:searchTerm, '%')) " +
            ") " +
            "ORDER BY CASE WHEN :searchTerm IS NOT NULL AND CAST(s.id AS string) = :searchTerm THEN 0 ELSE 1 END, s.id DESC"
    )
    Page<Servico> buscarServicosPaginados(@Param("emProgresso") Integer emProgresso,
                                          @Param("searchTerm") String searchTerm,
                                          Pageable pageable);

    @Query("SELECT COALESCE(SUM(s.valor), 0.0) FROM Servico s " +
            "WHERE s.tipo = :tipo " +
            "AND s.dataFim IS NOT NULL " +
            "AND YEAR(s.dataFim) = :ano " +
            "AND MONTH(s.dataFim) = :mes")
    BigDecimal sumValorByTipoAndAnoAndMes(@Param("tipo") Integer tipo,
                                          @Param("ano") Integer ano,
                                          @Param("mes") Integer mes);

    @Query("SELECT MONTH(s.dataFim) as mes, COALESCE(SUM(s.valor), 0.0) as total " +
            "FROM Servico s " +
            "WHERE s.tipo = :tipo AND s.dataFim IS NOT NULL AND YEAR(s.dataFim) = :ano " +
            "GROUP BY MONTH(s.dataFim) " +
            "ORDER BY MONTH(s.dataFim) ASC")
    List<FaturamentoPorMesDTO> findValorByAnoGroupByMes(@Param("tipo") Integer tipo,
                                                        @Param("ano") Integer ano);
}