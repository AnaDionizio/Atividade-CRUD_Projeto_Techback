package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.UsuarioDTO;
import br.uniesp.si.techback.model.Usuario;
import br.uniesp.si.techback.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> listar() {
        log.info("Listando todos os usuários");
        List<Usuario> usuarios = usuarioRepository.findAll();
        log.debug("Total de usuários encontrados: {}", usuarios.size());

        return usuarios.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO buscarPorId(Long id) {
        try {
            log.info("Buscando usuário com ID: {}", id);

            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

            log.debug("Usuário encontrado: {}", usuario);
            return converterParaDTO(usuario);

        } catch (Exception e) {
            log.error("Erro ao buscar usuário com ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public UsuarioDTO salvar(UsuarioDTO usuarioDTO) {
        try {
            log.info("Salvando novo usuário: {}", usuarioDTO.getUsername());

            // Verificar se username já existe
            if (usuarioRepository.findByUsername(usuarioDTO.getUsername()).isPresent()) {
                throw new RuntimeException("Username já existe: " + usuarioDTO.getUsername());
            }

            // Verificar se email já existe
            if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
                throw new RuntimeException("Email já existe: " + usuarioDTO.getEmail());
            }

            Usuario usuario = converterParaEntidade(usuarioDTO);
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            log.info("Usuário salvo com sucesso. ID: {}", usuarioSalvo.getId());
            return converterParaDTO(usuarioSalvo);

        } catch (Exception e) {
            log.error("Erro ao salvar usuário: {}", e.getMessage(), e);
            throw e;
        }
    }

    public UsuarioDTO atualizar(Long id, UsuarioDTO usuarioDTO) {
        try {
            log.info("Atualizando usuário com ID {}: {}", id, usuarioDTO);

            Usuario usuarioExistente = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

            // Verificar se username já existe para outro usuário
            usuarioRepository.findByUsername(usuarioDTO.getUsername()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new RuntimeException("Username já existe: " + usuarioDTO.getUsername());
                }
            });

            // Verificar se email já existe para outro usuário
            usuarioRepository.findByEmail(usuarioDTO.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new RuntimeException("Email já existe: " + usuarioDTO.getEmail());
                }
            });

            usuarioExistente.setUsername(usuarioDTO.getUsername());
            usuarioExistente.setEmail(usuarioDTO.getEmail());
            usuarioExistente.setRoles(usuarioDTO.getRoles());

            // Se senha foi fornecida, atualizar
            if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
                usuarioExistente.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
            }

            Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);

            log.debug("Usuário ID {} atualizado com sucesso", id);
            return converterParaDTO(usuarioAtualizado);

        } catch (Exception e) {
            log.error("Erro ao atualizar usuário ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void excluir(Long id) {
        try {
            log.info("Excluindo usuário com ID: {}", id);

            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

            usuarioRepository.delete(usuario);

            log.debug("Usuário com ID {} excluído com sucesso", id);

        } catch (Exception e) {
            log.error("Erro ao excluir usuário com ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    UsuarioDTO converterParaDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setRoles(usuario.getRoles());
        // Não incluir senha
        return dto;
    }

    Usuario converterParaEntidade(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioDTO.getId());
        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setPassword(usuarioDTO.getPassword());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setRoles(usuarioDTO.getRoles());
        return usuario;
    }
}
