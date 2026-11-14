package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.model.excepciones.DatosInvalidosException;
import co.edu.uniquindio.poo.model.servicio.Validador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Cliente implements Notificable {
    private static int secuencia = 1;

    private final String id;
    private final String nombre;
    private final String email;
    private final String telefono;
    private CanalNotificacion canalPreferido = CanalNotificacion.CORREO;

    private final List<Monedero> monederos = new ArrayList<>();
    private final List<Transaccion> transacciones = new ArrayList<>();
    private final List<RegistroPuntos> historialPuntos = new ArrayList<>();
    private final List<Notificacion> notificaciones = new ArrayList<>();
    private final Map<Beneficio, LocalDate> beneficios = new EnumMap<>(Beneficio.class);

    public Cliente(String nombre, String email, String telefono) throws DatosInvalidosException {
        this.nombre = Validador.validarNombre(nombre);
        this.email = Validador.validarEmail(email);
        this.telefono = Validador.validarTelefono(telefono);
        this.id = String.format("C-%03d", secuencia++);
    }

    public void agregarMonedero(Monedero m) { monederos.add(m); }
    public List<Monedero> getMonederos() { return Collections.unmodifiableList(monederos); }
    public Monedero getMonederoPrincipal() { return monederos.get(0); }

    public double getSaldoTotal() {
        return monederos.stream().mapToDouble(Monedero::getSaldo).sum();
    }

    public void agregarTransaccion(Transaccion t) { transacciones.add(t); }
    public List<Transaccion> getTransacciones() { return Collections.unmodifiableList(transacciones); }

    public void registrarPuntos(RegistroPuntos r) { historialPuntos.add(r); }
    public List<RegistroPuntos> getHistorialPuntos() { return Collections.unmodifiableList(historialPuntos); }

    public int getPuntosDisponibles() {
        return Math.max(0, historialPuntos.stream().mapToInt(RegistroPuntos::delta).sum());
    }

    public int getPuntosAcumulados() {
        return Math.max(0, historialPuntos.stream()
                .filter(r -> !r.esCanje())
                .mapToInt(RegistroPuntos::delta)
                .sum());
    }

    public Rango getRango() { return calcularRango(getPuntosAcumulados()); }

    public static int puntosMinimos(Rango rango) {
        return switch (rango) {
            case BRONCE -> 0;
            case PLATA -> 501;
            case ORO -> 1001;
            case PLATINO -> 5001;
        };
    }

    public static Rango calcularRango(int puntos) {
        return java.util.Arrays.stream(Rango.values())
                .filter(r -> puntos >= puntosMinimos(r))
                .reduce((menor, mayor) -> mayor)
                .orElse(Rango.BRONCE);
    }

    public static Optional<Rango> siguienteRango(Rango rango) {
        Rango[] todos = Rango.values();
        return rango.ordinal() + 1 < todos.length ? Optional.of(todos[rango.ordinal() + 1]) : Optional.empty();
    }

    public void activarBeneficio(Beneficio b, LocalDate vence) { beneficios.put(b, vence); }

    public boolean tieneBeneficio(Beneficio b) {
        LocalDate vence = beneficios.get(b);
        return vence != null && !vence.isBefore(LocalDate.now());
    }

    public LocalDate getVencimiento(Beneficio b) { return beneficios.get(b); }

    @Override
    public void recibirNotificacion(Notificacion n) { notificaciones.add(n); }

    @Override
    public CanalNotificacion getCanalPreferido() { return canalPreferido; }

    public void setCanalPreferido(CanalNotificacion canal) { this.canalPreferido = canal; }

    public List<Notificacion> getNotificaciones() { return Collections.unmodifiableList(notificaciones); }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }

    @Override
    public String toString() { return nombre + " (" + id + ")"; }

    public static RegistroPuntos movimiento(int delta, String motivo, boolean esCanje) {
        return new RegistroPuntos(LocalDateTime.now(), delta, motivo, esCanje);
    }
}
