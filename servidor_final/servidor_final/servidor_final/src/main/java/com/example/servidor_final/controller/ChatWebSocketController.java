package com.example.servidor_final.controller;
import com.example.servidor_final.model.Mensaje;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;

@Controller
public class ChatWebSocketController {

    @MessageMapping("/mensaje") // Cliente envía a "/app/mensaje"
    @SendTo("/topic/chat") // Se reenvía a todos los suscriptores de "/topic/chat"
    public Mensaje enviarMensaje(Mensaje mensaje) {
        mensaje.setFechaHora(LocalDateTime.now()); // Añadir timestamp
        return mensaje; // Se envía a todos los suscriptores de "/topic/chat"
    }
}