package com.pokedex.pokedex_api.controllers;

import java.util.List;
import java.util.Optional;

import com.pokedex.pokedex_api.entities.Role;
import com.pokedex.pokedex_api.entities.User;
import com.pokedex.pokedex_api.entities.dtos.CreateUserDto;
import com.pokedex.pokedex_api.entities.dtos.UserDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pokedex.pokedex_api.entities.ApiResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.pokedex.pokedex_api.repositories.RoleRepository;
import com.pokedex.pokedex_api.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users") // Ruta base
public class UserController {

    @Autowired // Instanciar un objeto y autorizarlo
    private RoleRepository roleRepository;

    @GetMapping("/roles") // End point
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() { // ResponseEntity es una tarea
        try { // Se utliza el try-catch cuando se hacen operaciones con la BD
            List<Role> roles = roleRepository.findAll();
            if (roles.isEmpty()) {
                ApiResponse<List<Role>> emptyResponse = new ApiResponse<>(false, "No se encontraron roles.", null);
                return ResponseEntity.status(404).body(emptyResponse);
            }
            ApiResponse<List<Role>> response = new ApiResponse<>(true,
                    "Lista de roles obtenida correctamente.", roles);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<Role>> errorResponse = new ApiResponse<>(false,
                    "Error al obtener los roles: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            List<UserDto> userResponses = users.stream()
                    .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getRole()))
                    .toList();
            ApiResponse<List<UserDto>> response = new ApiResponse<>(true, "Usuarios listados correctamente",
                    userResponses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<UserDto>> response = new ApiResponse<>(false, "Error al obtener usuarios", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        try {
            return userRepository.findById(id)
                    .map(user -> {
                        UserDto userDto = new UserDto(
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getRole());
                        ApiResponse<UserDto> response = new ApiResponse<>(true, "Usuario encontrado", userDto);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        ApiResponse<UserDto> response = new ApiResponse<>(false, "Usuario no encontrado", null);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    });
        } catch (Exception e) {
            ApiResponse<UserDto> response = new ApiResponse<>(false, "Error al buscar el usuario",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody CreateUserDto request) {
        try {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                ApiResponse<User> response = new ApiResponse<>(false,
                        "El nombre 	de usuario ya existe", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            Role role = roleRepository.findById(request.getRoleId()).orElse(null);
            if (role == null) {
                ApiResponse<User> response = new ApiResponse<>(false, "Rol no encontrado", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(role);
            User savedUser = userRepository.save(user);
            ApiResponse<User> response = new ApiResponse<>(true, "Usuario creado correctamente",
                    savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ApiResponse<User> response = new ApiResponse<>(false, "Error al crear el usuario",
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Usuario no encontrado", null));
            }
            User user = optionalUser.get();
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            Role role = roleRepository.findById(userDto.getRole().getId()).orElse(null);
            if (role == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Rol no encontrado", null));
            }
            user.setRole(role);
            User updatedUser = userRepository.save(user);
            UserDto updatedDto = new UserDto(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail(),
                    updatedUser.getRole()); // Se muestran los datos que se actualizaron.
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Usuario actualizado correctamente", updatedDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error al actualizar el usuario", null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {  //Pathvariable busca el ID en el Endpoint
        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Usuario no encontrado", null));
            }
            userRepository.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Usuario eliminado correctamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error al eliminar el usuario", null));
        }
    }

}