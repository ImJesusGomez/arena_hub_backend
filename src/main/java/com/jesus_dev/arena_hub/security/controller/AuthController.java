package com.jesus_dev.arena_hub.security.controller;

import com.jesus_dev.arena_hub.mapper.UserMapper;
import com.jesus_dev.arena_hub.model.Role;
import com.jesus_dev.arena_hub.model.User;
import com.jesus_dev.arena_hub.model.enums.RoleName;
import com.jesus_dev.arena_hub.repository.RoleRepository;
import com.jesus_dev.arena_hub.repository.UserRepository;
import com.jesus_dev.arena_hub.security.dto.request.LoginRequestDTO;
import com.jesus_dev.arena_hub.security.dto.request.RegisterRequestDTO;
import com.jesus_dev.arena_hub.security.dto.response.JwtAuthResponseDTO;
import com.jesus_dev.arena_hub.security.jwt.JwtGenerator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthController(AuthenticationManager authenticationManager, JwtGenerator jwtGenerator, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        // 1. Autenticamos en base a las credenciales (Si falla, Spring lanza una excepción automáticamente)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password())
        );

        // 2. Establecemos el contexto de seguridad (Opcional en endpoints de login, pero buena práctica)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Obtenemos el email
        String email = authentication.getName();

        // 4. Obtenemos el usuario en base al email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 5. Generamos el token usando el objeto 'authentication' que ya tenemos en memoria
        String token = jwtGenerator.generateToken(authentication);

        // 6. Retornamos la respuesta
        return ResponseEntity.ok(new JwtAuthResponseDTO(token, userMapper.toUserResponseDTO(user)));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.email())) {
            return new ResponseEntity<>("User already exists with email: " + registerRequestDTO.email(), HttpStatus.BAD_REQUEST);
        }

        User user = userMapper.registerDtoToUser(registerRequestDTO);
        user.setPassword(passwordEncoder.encode(registerRequestDTO.password()));

        Set<Role> roles = new HashSet<>();

        for (RoleName role : registerRequestDTO.roles()) {
            // TODO: add exception to role not found
            Role newRole = roleRepository.findByName(role).orElseThrow(() -> new RuntimeException("Role not found"));
            roles.add(newRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>("User registered", HttpStatus.OK);
    }
}
