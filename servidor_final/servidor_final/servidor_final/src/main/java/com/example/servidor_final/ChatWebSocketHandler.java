package com.example.servidor_final;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {


    private final ChatUserSessionManager userSessionManager;
    private final Map<String, List<String>> grupos = new ConcurrentHashMap<>(); // Almacena grupos y sus miembros

    public void agregarGrupoAlMapa(String nombreGrupo, List<String> miembros) {
        grupos.put(nombreGrupo, miembros);
    }

    @Autowired
    public ChatWebSocketHandler(ChatUserSessionManager userSessionManager) {
        this.userSessionManager = userSessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        String query = session.getUri().getQuery();
        String username = null;

        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair[0].equals("username")) {
                    username = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                    break;
                }
            }
        }

        if (username == null) {
            throw new IllegalArgumentException("El nombre de usuario no puede ser null");
        }

        userSessionManager.addSession(username, session);
        System.out.println("Usuario conectado: " + username);

        // Enviar notificación a otros usuarios
        try {
            notificarUsuarios(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(message.getPayload());

        String remitente = jsonNode.has("remitente") ? jsonNode.get("remitente").asText() : null;
        String destinatario = jsonNode.has("destinatario") ? jsonNode.get("destinatario").asText() : null;
        String mensaje = jsonNode.has("mensaje") ? jsonNode.get("mensaje").asText() : null;

        // 🔹 LOG: Mostrar el mensaje recibido
        System.out.println("📩 Mensaje recibido -> Remitente: " + remitente + ", Destinatario: " + destinatario + ", Mensaje: " + mensaje);

        //Terceraprueba

        if (remitente != null && destinatario != null && mensaje != null) {

            // 🔹 LOG: Verificar si el destinatario es un grupo
            System.out.println("🔍 Lista de grupos actuales: " + grupos.keySet());

            if (grupos.keySet().contains(destinatario)) {
                // 🔹 LOG: Se detectó que el mensaje es grupal
                System.out.println("📢 Enviando mensaje grupal a " + destinatario);
                // Si el destinatario es un grupo, enviar mensaje a todos los miembros
                List<String> miembros = grupos.get(destinatario);
                for (String miembro : miembros) {
                    // Verificar que el miembro esté conectado antes de enviar el mensaje
                    WebSocketSession miembroSession = userSessionManager.getSession(miembro);
                    if (miembroSession != null && miembroSession.isOpen()) {
                        String mensajeJson = objectMapper.writeValueAsString(Map.of(
                                "remitente", remitente,
                                "destinatario", miembro,  // Aquí cambiamos para enviar el mensaje al miembro individualmente
                                "mensaje", mensaje,
                                "esGrupo", true  // Indicamos que es un mensaje grupal
                        ));
                        miembroSession.sendMessage(new TextMessage(mensajeJson));
                        // 🔹 LOG: Confirmar que se envió al miembro del grupo
                        System.out.println("✅ Mensaje enviado a " + miembro + " en el grupo " + destinatario);
                    } else {
                        // 🔹 LOG: Si el miembro del grupo no está conectado
                        System.out.println("⚠️ Miembro " + miembro + " no está conectado.");
                    }
                }
                System.out.println("✅ Mensaje enviado a todos los miembros del grupo " + destinatario);
            } else {
                // 🔹 LOG: No es un grupo, verificar usuario individual
                System.out.println("💬 Enviando mensaje privado a " + destinatario);
                // Si no es un grupo, enviar al destinatario individual
                WebSocketSession recipientSession = userSessionManager.getSession(destinatario);
                if (recipientSession != null && recipientSession.isOpen()) {
                    String mensajeJson = objectMapper.writeValueAsString(Map.of(
                            "remitente", remitente,
                            "destinatario", destinatario,  // Aquí es un mensaje privado
                            "mensaje", mensaje
                    ));
                    recipientSession.sendMessage(new TextMessage(mensajeJson));
                    System.out.println("Mensaje enviado a " + destinatario);
                } else {
                    System.out.println("Destinatario no conectado: " + destinatario);
                }
            }
        } else {
            System.out.println("Error: JSON recibido incompleto.");
        }


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        String query = session.getUri().getQuery();
        String username = null;

        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair[0].equals("username")) {
                    username = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                    break;
                }
            }
        }

        if (username != null) {
            userSessionManager.removeSession(username);
            System.out.println("El usuario " + username+" salió de la conversación");
        }
    }
    // Método para crear un grupo
    public void crearGrupo(String nombreGrupo, List<String> miembros) {
        grupos.put(nombreGrupo, new ArrayList<>(miembros));
        System.out.println("Grupo creado: " + nombreGrupo + " con miembros: " + miembros);
    }


    private void notificarUsuarios(String nuevoUsuario) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String mensajeJson = objectMapper.writeValueAsString(Map.of(
                "tipo", "notificacion",
                "mensaje", "El usuario " + nuevoUsuario + " se ha conectado"
        ));

        // Enviar a todos los usuarios conectados
        for (WebSocketSession session : userSessionManager.getAllSessions()) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(mensajeJson));
            }
        }
        System.out.println("Notificación enviada a todos los usuarios");
    }
}