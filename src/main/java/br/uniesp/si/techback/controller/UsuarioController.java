package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.UsuarioDTO;
import br.uniesp.si.techback.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioDTO> listar() {
        log.info("Listando todos os usuários");
        List<UsuarioDTO> usuarios = usuarioService.listar();
        log.debug("Total de usuários encontrados: {}", usuarios.size());
        return usuarios;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        try {
            UsuarioDTO usuario = usuarioService.buscarPorId(id);
            log.debug("Usuário encontrado: {}", usuario);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            log.error("Erro ao buscar usuário com ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        log.info("Recebida requisição para criar novo usuário");
        try {
            UsuarioDTO usuarioSalvo = usuarioService.salvar(usuarioDTO);
            log.info("Usuário criado com sucesso. ID: {}", usuarioSalvo.getId());

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(usuarioSalvo.getId())
                    .toUri();

            log.debug("URI de localização do novo usuário: {}", location);

            return ResponseEntity.created(location).body(usuarioSalvo);
        } catch (Exception e) {
            log.error("Erro ao criar usuário: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id,
                                                @Valid @RequestBody UsuarioDTO usuarioDTO) {
        log.info("Atualizando usuário com ID {}: {}", id, usuarioDTO);
        try {
            UsuarioDTO usuarioAtualizado = usuarioService.atualizar(id, usuarioDTO);
            log.debug("Usuário ID {} atualizado com sucesso", id);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (Exception e) {
            log.error("Erro ao atualizar usuário ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Excluindo usuário com ID: {}", id);
        try {
            usuarioService.excluir(id);
            log.debug("Usuário com ID {} excluído com sucesso", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao excluir usuário com ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}
