package com.example.servidor_final.controller;

import com.example.servidor_final.ChatService;
import com.example.servidor_final.ChatWebSocketHandler;
import com.example.servidor_final.model.Grupo;
import com.example.servidor_final.model.Mensaje;
import com.example.servidor_final.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final List<Mensaje> mensajes = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong();

    @Autowired
    private ChatService chatService;

    private final List<Mensaje> mensajesEnMemoria = new ArrayList<>(); // Almacén temporal

    public MensajeController(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @PostMapping("/enviar")
    public void enviarMensaje(@RequestParam String remitente, @RequestParam String destinatario, @RequestParam String mensaje) throws Exception {
        Mensaje nuevoMensaje = new Mensaje(remitente, destinatario, mensaje);
        mensajesEnMemoria.add(nuevoMensaje);
        System.out.println("Mensaje guardado: " + nuevoMensaje);

        // Enviar mensaje mediante WebSockets
        chatWebSocketHandler.handleTextMessage(null, new TextMessage(
                "{\"remitente\": \"" + remitente + "\", \"destinatario\": \"" + destinatario + "\", \"mensaje\": \"" + mensaje + "\"}"
        ));
    }

    @GetMapping("/conversacion")
    public List<Mensaje> obtenerConversacion(@RequestParam String remitente, @RequestParam String destinatario) {
        return mensajesEnMemoria.stream()
                .filter(m -> (m.getRemitente().equals(remitente) && m.getDestinatario().equals(destinatario)) ||
                        (m.getRemitente().equals(destinatario) && m.getDestinatario().equals(remitente)))
                .collect(Collectors.toList());
    }

    @GetMapping("/recibir/{destinatario}")
    public List<Mensaje> recibirMensajes(@PathVariable String destinatario) {
        return mensajes.stream()
                .filter(m -> m.getDestinatario().equals(destinatario))
                .toList();
    }


    @GetMapping("/history")
    public List<Mensaje> obtenerHistorialMensajes() {
        return mensajes;
    }

    @GetMapping("/private/{usuario}")
    public List<Mensaje> obtenerMensajesPrivados(@PathVariable String usuario) {
        return mensajes.stream()
                .filter(m -> m.getDestinatario().equals(usuario) || m.getRemitente().equals(usuario))
                .toList();
    }
}

