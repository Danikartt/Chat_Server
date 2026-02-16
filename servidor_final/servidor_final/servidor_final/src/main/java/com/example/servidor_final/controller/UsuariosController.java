package com.example.servidor_final.controller;

import com.example.servidor_final.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/users")
public class UsuariosController {

    private final List<Usuario> usuarios = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong();


    @PostMapping("/register")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        // Verificar si el usuario ya existe
        boolean existe = usuarios.stream().anyMatch(u -> u.getNombre().equals(usuario.getNombre()));
        if (existe) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        usuario.setId(idCounter.incrementAndGet());
        usuarios.add(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> iniciarSesion(@RequestBody Usuario usuario) {
        System.out.println("Intento de login para: " + usuario.getNombre());
        return usuarios.stream()
                .filter(u -> u.getNombre().equals(usuario.getNombre()) && u.getClave().equals(usuario.getClave()))
                .findFirst()
                .map(u -> {
                    u.setOnline(true);
                    System.out.println("Usuario conectado: " + u.getNombre() + " - Online: " + u.isOnline());
                    return ResponseEntity.ok(u);
                })
                .orElseGet(() -> {
                    System.out.println("Login fallido para: " + usuario.getNombre());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                });
    }

    @GetMapping("/online")
    public ResponseEntity<List<Usuario>> obtenerUsuariosConectados() {
        System.out.println("Usuarios en memoria: " + usuarios.size());
        usuarios.forEach(u -> System.out.println(u.getNombre() + " - Online: " + u.isOnline()));
        List<Usuario> conectados = usuarios.stream()
                .filter(Usuario::isOnline)
                .toList();
        System.out.println("Usuarios conectados: " + conectados.size());
        return ResponseEntity.ok(conectados);
    }

}
