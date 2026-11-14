package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.model.excepciones.MonederoException;
import co.edu.uniquindio.poo.model.excepciones.MontoInvalidoException;
import co.edu.uniquindio.poo.model.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.poo.model.servicio.Validador;

public abstract class Monedero {
    private static int secuencia = 1;

    private final String id;
    private final String nombre;
    private final String idPropietario;
    private double saldo;

    protected Monedero(String nombre, String idPropietario) {
        this.id = String.format("M-%04d", secuencia++);
        this.nombre = nombre;
        this.idPropietario = idPropietario;
    }

    public abstract String getTipo();

    public abstract double getSaldoMinimo();

    public abstract double getUmbralSaldoBajo();

    public void depositar(double monto) throws MontoInvalidoException {
        Validador.validarMonto(monto);
        saldo += monto;
    }

    public void retirar(double monto) throws MonederoException {
        Validador.validarMonto(monto);
        exigirSaldoSuficiente(monto);
        saldo -= monto;
    }

    private void exigirSaldoSuficiente(double monto) throws SaldoInsuficienteException {
        double disponible = saldo - getSaldoMinimo();
        if (monto > disponible + 1e-9) {
            throw new SaldoInsuficienteException(Math.max(disponible, 0), monto);
        }
    }

    public boolean tieneSaldoBajo() { return saldo < getUmbralSaldoBajo(); }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getIdPropietario() { return idPropietario; }
    public double getSaldo() { return saldo; }

    @Override
    public String toString() {
        return String.format("%s [%s] – $%,.2f", nombre, getTipo(), saldo);
    }
}
