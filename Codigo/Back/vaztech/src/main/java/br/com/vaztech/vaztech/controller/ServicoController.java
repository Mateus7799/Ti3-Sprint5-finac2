package br.com.vaztech.vaztech.controller;

import br.com.vaztech.vaztech.dto.*;
import br.com.vaztech.vaztech.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/servico")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;

    @GetMapping
    public Page<ServicoResponseDTO> listarServicosPaginados(@RequestParam(value = "emProgresso", required = false) Integer emProgresso,
                                                            @RequestParam(value = "searchTerm", required = false) String searchTerm,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return servicoService.listarServicosPaginados(emProgresso, searchTerm, page, size);
    }

    @GetMapping("/status")
    public ResponseEntity<List<ServicoStatusDTO>> listarServicoStatus() {
        List<ServicoStatusDTO> response = servicoService.listarServicoStatus();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> buscarPorId(@PathVariable Integer id) throws ResponseStatusException {
        ServicoResponseDTO response = servicoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> criarServico(@Valid @RequestBody ServicoAddRequestDTO dto) throws ResponseStatusException {
        ServicoResponseDTO response = servicoService.criarServico(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/concluir")
    public ResponseEntity<?> concluirSerico(@PathVariable Integer id) {
        return servicoService.concluirServico(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizarServico(@PathVariable Integer id, @Valid @RequestBody ServicoUpdateRequestDTO dto) throws ResponseStatusException {
        ServicoResponseDTO response = servicoService.atualizarServico(id, dto);
        return ResponseEntity.ok(response);
    }
}
