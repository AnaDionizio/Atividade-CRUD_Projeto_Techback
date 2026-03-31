package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.UsuarioDTO;
import br.uniesp.si.techback.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(dto.getPassword());
        usuario.setEmail(dto.getEmail());
        usuario.setRoles(dto.getRoles());

        return usuario;
    }

    public UsuarioDTO toDTO(Usuario entity) {
        if (entity == null) {
            return null;
        }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPassword(null); // Não retornar senha no DTO
        dto.setEmail(entity.getEmail());
        dto.setRoles(entity.getRoles());

        return dto;
    }
}
