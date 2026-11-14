package co.edu.uniquindio.poo.model;

import java.time.LocalDateTime;

public record RegistroPuntos(LocalDateTime fecha, int delta, String motivo, boolean esCanje) { }
