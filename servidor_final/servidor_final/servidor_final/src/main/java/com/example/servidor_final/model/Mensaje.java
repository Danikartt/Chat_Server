package com.example.servidor_final.model;

import java.time.LocalDateTime;

public class Mensaje {
    private Long id;
    private String remitente;
    private String destinatario; // Para mensajes privados
    private String grupoId; // Para mensajes en grupo
    private String mensaje;
    private LocalDateTime timestamp;
    private boolean leido;

    public Mensaje(Long id, String remitente, String destinatario, String grupoId, String mensaje, LocalDateTime timestamp) {
        this.id = id;
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.grupoId = grupoId;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
        this.leido = false;
    }

    public Mensaje(String remitente, String destinatario, String mensaje) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRemitente() { return remitente; }
    public void setRemitente(String remitente) { this.remitente = remitente; }
    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
    public String getGrupoId() { return grupoId; }
    public void setGrupoId(String grupoId) { this.grupoId = grupoId; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }

    public void setFechaHora(LocalDateTime now) {
    }
}
