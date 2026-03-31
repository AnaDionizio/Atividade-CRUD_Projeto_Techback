package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.UsuarioDTO;
import br.uniesp.si.techback.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
@DisplayName("Testes do UsuarioController")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioDTO usuarioDTO;
    private UsuarioDTO usuarioSalvoDTO;

    @BeforeEach
    void setUp() {
        usuarioDTO = UsuarioDTO.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .build();

        usuarioSalvoDTO = UsuarioDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .build();
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosOsUsuarios() throws Exception {
        List<UsuarioDTO> usuarios = Arrays.asList(usuarioSalvoDTO);
        when(usuarioService.listar()).thenReturn(usuarios);

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void deveBuscarUsuarioPorId() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(usuarioSalvoDTO);

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando buscar usuário inexistente")
    void deveRetornar404QuandoBuscarUsuarioInexistente() throws Exception {
        when(usuarioService.buscarPorId(999L)).thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(get("/usuarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve criar novo usuário")
    void deveCriarNovoUsuario() throws Exception {
        when(usuarioService.salvar(any(UsuarioDTO.class))).thenReturn(usuarioSalvoDTO);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("Deve atualizar usuário")
    void deveAtualizarUsuario() throws Exception {
        UsuarioDTO usuarioAtualizadoDTO = UsuarioDTO.builder()
                .username("updateduser")
                .email("updated@example.com")
                .roles(Set.of("ADMIN"))
                .build();

        UsuarioDTO usuarioAtualizado = UsuarioDTO.builder()
                .id(1L)
                .username("updateduser")
                .email("updated@example.com")
                .roles(Set.of("ADMIN"))
                .build();

        when(usuarioService.atualizar(eq(1L), any(UsuarioDTO.class))).thenReturn(usuarioAtualizado);

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioAtualizadoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"));
    }

    @Test
    @DisplayName("Deve excluir usuário")
    void deveExcluirUsuario() throws Exception {
        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 404 quando atualizar usuário inexistente")
    void deveRetornar404QuandoAtualizarUsuarioInexistente() throws Exception {
        when(usuarioService.atualizar(eq(999L), any(UsuarioDTO.class)))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(put("/usuarios/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 quando excluir usuário inexistente")
    void deveRetornar404QuandoExcluirUsuarioInexistente() throws Exception {
        doThrow(new RuntimeException("Usuário não encontrado")).when(usuarioService).excluir(999L);

        mockMvc.perform(delete("/usuarios/999"))
                .andExpect(status().isNotFound());
    }
}
