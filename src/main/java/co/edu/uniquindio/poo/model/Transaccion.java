package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.model.excepciones.MonederoException;
import co.edu.uniquindio.poo.model.excepciones.MontoInvalidoException;
import co.edu.uniquindio.poo.model.servicio.Validador;

import java.time.LocalDateTime;

public abstract class Transaccion implements Puntuable, Reversible {
    private static int secuencia = 1;

    private final int id;
    private final LocalDateTime fecha;
    private final double monto;
    private final double comision;
    private final Monedero monedero;
    private final String categoria;

    private int puntosGenerados;
    private boolean revertida;
    private boolean sospechosa;

    protected Transaccion(Monedero monedero, double monto, double comision, String categoria, LocalDateTime fecha)
            throws MonederoException {
        Validador.validarMonto(monto);
        exigirComisionValida(comision);
        this.id = secuencia++;
        this.monedero = monedero;
        this.monto = monto;
        this.comision = comision;
        this.categoria = (categoria == null || categoria.isBlank()) ? "General" : categoria.trim();
        this.fecha = fecha;
    }

    public abstract TipoTransaccion getTipo();

    protected abstract int puntosPorCada100();

    public abstract void ejecutar() throws MonederoException;

    protected abstract void deshacer() throws MonederoException;

    public boolean esGasto() { return false; }

    @Override
    public int calcularPuntos(Rango rango) {
        int base = (int) (monto / 100) * puntosPorCada100();
        return (int) Math.round(base * (1 + bonoPuntos(rango)));
    }

    public static double bonoPuntos(Rango rango) {
        return switch (rango) {
            case BRONCE -> 0.00;
            case PLATA -> 0.05;
            case ORO -> 0.10;
            case PLATINO -> 0.20;
        };
    }

    public int asignarPuntos(Rango rango) {
        puntosGenerados = calcularPuntos(rango);
        return puntosGenerados;
    }

    @Override
    public final void revertir() throws MonederoException {
        exigirNoRevertida();
        deshacer();
        revertida = true;
    }

    private static void exigirComisionValida(double comision) throws MontoInvalidoException {
        if (comision < 0) {
            throw new MontoInvalidoException("La comisión no puede ser negativa.");
        }
    }

    private void exigirNoRevertida() throws MonederoException {
        if (revertida) {
            throw new MonederoException("La transacción #" + id + " ya fue revertida.");
        }
    }

    @Override public boolean isRevertida() { return revertida; }

    public void marcarSospechosa() { this.sospechosa = true; }

    public int getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public double getMonto() { return monto; }
    public double getComision() { return comision; }
    public Monedero getMonedero() { return monedero; }
    public String getCategoria() { return categoria; }
    public int getPuntosGenerados() { return puntosGenerados; }
    public boolean isSospechosa() { return sospechosa; }
}
