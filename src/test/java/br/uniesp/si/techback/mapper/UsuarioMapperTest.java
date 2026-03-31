package br.uniesp.si.techback.mapper;

import br.uniesp.si.techback.dto.UsuarioDTO;
import br.uniesp.si.techback.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes do UsuarioMapper")
class UsuarioMapperTest {

    private UsuarioMapper usuarioMapper;
    private Usuario usuario;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioMapper = new UsuarioMapper();

        usuario = Usuario.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .build();

        usuarioDTO = UsuarioDTO.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .build();
    }

    @Test
    @DisplayName("Deve converter Entity para DTO")
    void deveConverterEntityParaDTO() {
        UsuarioDTO resultado = usuarioMapper.toDTO(usuario);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(usuario.getId());
        assertThat(resultado.getUsername()).isEqualTo(usuario.getUsername());
        assertThat(resultado.getEmail()).isEqualTo(usuario.getEmail());
        assertThat(resultado.getRoles()).isEqualTo(usuario.getRoles());
        assertThat(resultado.getPassword()).isNull(); // Senha não deve ser retornada
    }

    @Test
    @DisplayName("Deve converter DTO para Entity")
    void deveConverterDTOParaEntity() {
        Usuario resultado = usuarioMapper.toEntity(usuarioDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(usuarioDTO.getId());
        assertThat(resultado.getUsername()).isEqualTo(usuarioDTO.getUsername());
        assertThat(resultado.getPassword()).isEqualTo(usuarioDTO.getPassword());
        assertThat(resultado.getEmail()).isEqualTo(usuarioDTO.getEmail());
        assertThat(resultado.getRoles()).isEqualTo(usuarioDTO.getRoles());
    }

    @Test
    @DisplayName("Deve retornar null quando converter Entity null para DTO")
    void deveRetornarNullQuandoConverterEntityNullParaDTO() {
        UsuarioDTO resultado = usuarioMapper.toDTO(null);

        assertThat(resultado).isNull();
    }

    @Test
    @DisplayName("Deve retornar null quando converter DTO null para Entity")
    void deveRetornarNullQuandoConverterDTONullParaEntity() {
        Usuario resultado = usuarioMapper.toEntity(null);

        assertThat(resultado).isNull();
    }

    @Test
    @DisplayName("Deve manter consistência na conversão bidirecional")
    void deveManterConsistenciaNaConversaoBidirecional() {
        UsuarioDTO dtoConvertido = usuarioMapper.toDTO(usuario);
        Usuario entityReconvertida = usuarioMapper.toEntity(dtoConvertido);

        assertThat(entityReconvertida.getId()).isEqualTo(usuario.getId());
        assertThat(entityReconvertida.getUsername()).isEqualTo(usuario.getUsername());
        assertThat(entityReconvertida.getEmail()).isEqualTo(usuario.getEmail());
        assertThat(entityReconvertida.getRoles()).isEqualTo(usuario.getRoles());
        // Nota: senha não é incluída na conversão bidirecional pois não é retornada no DTO
    }
}
