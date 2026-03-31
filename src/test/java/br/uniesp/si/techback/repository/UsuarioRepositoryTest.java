package br.uniesp.si.techback.repository;

import br.uniesp.si.techback.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do UsuarioRepository")
class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioTeste;

    @BeforeEach
    void setUp() {
        usuarioTeste = Usuario.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .build();
    }

    @Test
    @DisplayName("Deve salvar um usuário com sucesso")
    void deveSalvarUsuario() {
        Usuario usuarioSalvo = usuarioRepository.save(usuarioTeste);

        assertThat(usuarioSalvo).isNotNull();
        assertThat(usuarioSalvo.getId()).isNotNull();
        assertThat(usuarioSalvo.getUsername()).isEqualTo(usuarioTeste.getUsername());
        assertThat(usuarioSalvo.getEmail()).isEqualTo(usuarioTeste.getEmail());
        assertThat(usuarioSalvo.getRoles()).isEqualTo(usuarioTeste.getRoles());
    }

    @Test
    @DisplayName("Deve encontrar usuário por ID quando existir")
    void deveEncontrarUsuarioPorId() {
        Usuario usuarioSalvo = entityManager.persistAndFlush(usuarioTeste);

        Optional<Usuario> usuarioEncontrado = usuarioRepository.findById(usuarioSalvo.getId());

        assertThat(usuarioEncontrado).isPresent();
        assertThat(usuarioEncontrado.get().getId()).isEqualTo(usuarioSalvo.getId());
        assertThat(usuarioEncontrado.get().getUsername()).isEqualTo(usuarioTeste.getUsername());
    }

    @Test
    @DisplayName("Deve retornar vazio quando buscar por ID inexistente")
    void deveRetornarVazioQuandoBuscarPorIdInexistente() {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findById(999L);

        assertThat(usuarioEncontrado).isEmpty();
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosOsUsuarios() {
        entityManager.persistAndFlush(usuarioTeste);

        Usuario usuario2 = Usuario.builder()
                .username("user2")
                .password("pass2")
                .email("user2@example.com")
                .roles(Set.of("USER"))
                .build();
        entityManager.persistAndFlush(usuario2);

        List<Usuario> usuarios = usuarioRepository.findAll();

        assertThat(usuarios).hasSize(2);
        assertThat(usuarios).extracting(Usuario::getUsername)
                .containsExactlyInAnyOrder(usuarioTeste.getUsername(), usuario2.getUsername());
    }

    @Test
    @DisplayName("Deve verificar se usuário existe por ID")
    void deveVerificarSeUsuarioExistePorId() {
        Usuario usuarioSalvo = entityManager.persistAndFlush(usuarioTeste);

        boolean existe = usuarioRepository.existsById(usuarioSalvo.getId());
        boolean naoExiste = usuarioRepository.existsById(999L);

        assertThat(existe).isTrue();
        assertThat(naoExiste).isFalse();
    }

    @Test
    @DisplayName("Deve deletar usuário por ID")
    void deveDeletarUsuarioPorId() {
        Usuario usuarioSalvo = entityManager.persistAndFlush(usuarioTeste);

        usuarioRepository.deleteById(usuarioSalvo.getId());

        Optional<Usuario> usuarioDeletado = usuarioRepository.findById(usuarioSalvo.getId());
        assertThat(usuarioDeletado).isEmpty();
    }

    @Test
    @DisplayName("Deve contar total de usuários")
    void deveContarTotalDeUsuarios() {
        entityManager.persistAndFlush(usuarioTeste);

        Usuario usuario2 = Usuario.builder()
                .username("user2")
                .password("pass2")
                .email("user2@example.com")
                .roles(Set.of("USER"))
                .build();
        entityManager.persistAndFlush(usuario2);

        long totalUsuarios = usuarioRepository.count();

        assertThat(totalUsuarios).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve encontrar usuário por username")
    void deveEncontrarUsuarioPorUsername() {
        entityManager.persistAndFlush(usuarioTeste);

        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByUsername("testuser");

        assertThat(usuarioEncontrado).isPresent();
        assertThat(usuarioEncontrado.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Deve encontrar usuário por email")
    void deveEncontrarUsuarioPorEmail() {
        entityManager.persistAndFlush(usuarioTeste);

        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail("test@example.com");

        assertThat(usuarioEncontrado).isPresent();
        assertThat(usuarioEncontrado.get().getEmail()).isEqualTo("test@example.com");
    }
}
