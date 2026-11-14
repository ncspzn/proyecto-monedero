package co.edu.uniquindio.poo.model;

import java.time.LocalDateTime;

public record Notificacion(LocalDateTime fecha, CanalNotificacion canal, String mensaje) { }
