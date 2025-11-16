package br.com.vaztech.vaztech.controller;

import br.com.vaztech.vaztech.dto.AniversarianteResponseDTO;
import br.com.vaztech.vaztech.service.AniversarianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/aniversariantes")
@RequiredArgsConstructor
public class AniversarianteController {

    private final AniversarianteService aniversarianteService;

    @GetMapping("/semana")
    public ResponseEntity<List<AniversarianteResponseDTO>> getAniversariantesSemana() {
        List<AniversarianteResponseDTO> lista = aniversarianteService.buscarAniversariantesDaSemana();
        return ResponseEntity.ok(lista);
    }
}

