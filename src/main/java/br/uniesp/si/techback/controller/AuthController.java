package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.JwtResponse;
import br.uniesp.si.techback.dto.LoginRequest;
import br.uniesp.si.techback.model.Usuario;
import br.uniesp.si.techback.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            Usuario userDetails = (Usuario) authentication.getPrincipal();
            JwtResponse jwtResponse = new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getRoles());

            log.info("Usuário {} logado com sucesso", loginRequest.getUsername());
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            log.error("Erro no login para usuário {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body("Credenciais inválidas");
        }
    }
}
