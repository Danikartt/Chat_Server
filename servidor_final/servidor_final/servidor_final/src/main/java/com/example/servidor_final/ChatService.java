package com.example.servidor_final;

import com.example.servidor_final.model.Mensaje;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChatService {
    private final Map<String, List<Mensaje>> mensajesEnMemoria = new HashMap<>();

    // Guardar el mensaje
    public void guardarMensaje(Mensaje mensaje) {
        String key = generarClave(mensaje.getRemitente(), mensaje.getDestinatario());
        mensajesEnMemoria.putIfAbsent(key, new ArrayList<>());
        mensajesEnMemoria.get(key).add(mensaje);
    }

    // Obtener los mensajes entre dos usuarios
    public List<Mensaje> obtenerMensajes(String remitente, String destinatario) {
        String key1 = generarClave(remitente, destinatario);
        return mensajesEnMemoria.getOrDefault(key1, new ArrayList<>());
    }

    // Generar una clave única para la conversación entre dos usuarios
    private String generarClave(String usuario1, String usuario2) {
        // Verificar que ninguno de los usuarios sea null
        if (usuario1 == null || usuario2 == null) {
            throw new IllegalArgumentException("Los usuarios no pueden ser null");
        }
        List<String> usuarios = Arrays.asList(usuario1, usuario2);
        Collections.sort(usuarios);
        return usuarios.get(0) + "-" + usuarios.get(1);
    }

}
