package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.UsuarioDTO;
import br.uniesp.si.techback.model.Usuario;
import br.uniesp.si.techback.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
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
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosOsUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        List<UsuarioDTO> resultado = usuarioService.listar();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsername()).isEqualTo("testuser");
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar usuário por ID quando existir")
    void deveBuscarUsuarioPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioDTO resultado = usuarioService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando buscar usuário por ID inexistente")
    void deveLancarExcecaoQuandoBuscarUsuarioPorIdInexistente() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado com ID: 999");
    }

    @Test
    @DisplayName("Deve salvar novo usuário com sucesso")
    void deveSalvarNovoUsuario() {
        UsuarioDTO novoUsuarioDTO = UsuarioDTO.builder()
                .username("newuser")
                .password("password123")
                .email("new@example.com")
                .roles(Set.of("USER"))
                .build();

        Usuario novoUsuario = Usuario.builder()
                .username("newuser")
                .password("encodedPassword")
                .email("new@example.com")
                .roles(Set.of("USER"))
                .build();

        Usuario usuarioSalvo = Usuario.builder()
                .id(2L)
                .username("newuser")
                .password("encodedPassword")
                .email("new@example.com")
                .roles(Set.of("USER"))
                .build();

        UsuarioDTO usuarioSalvoDTO = UsuarioDTO.builder()
                .id(2L)
                .username("newuser")
                .email("new@example.com")
                .roles(Set.of("USER"))
                .build();

        when(usuarioRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        UsuarioDTO resultado = usuarioService.salvar(novoUsuarioDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getUsername()).isEqualTo("newuser");
        assertThat(resultado.getEmail()).isEqualTo("new@example.com");
        assertThat(resultado.getRoles()).isEqualTo(Set.of("USER"));
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando salvar usuário com username existente")
    void deveLancarExcecaoQuandoSalvarUsuarioComUsernameExistente() {
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.salvar(usuarioDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Username já existe: testuser");
    }

    @Test
    @DisplayName("Deve lançar exceção quando salvar usuário com email existente")
    void deveLancarExcecaoQuandoSalvarUsuarioComEmailExistente() {
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> usuarioService.salvar(usuarioDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email já existe: test@example.com");
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuario() {
        UsuarioDTO usuarioAtualizadoDTO = UsuarioDTO.builder()
                .username("updateduser")
                .email("updated@example.com")
                .roles(Set.of("ADMIN"))
                .build();

        Usuario usuarioAtualizado = Usuario.builder()
                .id(1L)
                .username("updateduser")
                .password("encodedPassword")
                .email("updated@example.com")
                .roles(Set.of("ADMIN"))
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioAtualizado);

        UsuarioDTO resultado = usuarioService.atualizar(1L, usuarioAtualizadoDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo("updateduser");
        assertThat(resultado.getEmail()).isEqualTo("updated@example.com");
        assertThat(resultado.getRoles()).isEqualTo(Set.of("ADMIN"));
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve excluir usuário com sucesso")
    void deveExcluirUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.excluir(1L);

        verify(usuarioRepository).delete(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção quando excluir usuário inexistente")
    void deveLancarExcecaoQuandoExcluirUsuarioInexistente() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.excluir(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado com ID: 999");
    }
}
