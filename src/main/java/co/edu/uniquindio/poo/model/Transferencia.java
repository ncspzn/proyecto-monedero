package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.model.excepciones.DatosInvalidosException;
import co.edu.uniquindio.poo.model.excepciones.MonederoException;

import java.time.LocalDateTime;

public class Transferencia extends Transaccion {
    private final Monedero destino;

    public Transferencia(Monedero origen, Monedero destino, double monto, double comision, String categoria)
            throws MonederoException {
        super(origen, monto, comision, categoria, LocalDateTime.now());
        exigirDestinoDistinto(origen, destino);
        this.destino = destino;
    }

    private static void exigirDestinoDistinto(Monedero origen, Monedero destino) throws DatosInvalidosException {
        if (origen.getId().equals(destino.getId())) {
            throw new DatosInvalidosException("El monedero de origen y destino no pueden ser el mismo.");
        }
    }

    public Monedero getDestino() { return destino; }

    @Override public TipoTransaccion getTipo() { return TipoTransaccion.TRANSFERENCIA; }
    @Override protected int puntosPorCada100() { return 3; }
    @Override public boolean esGasto() { return true; }

    @Override public void ejecutar() throws MonederoException {
        getMonedero().retirar(getMonto() + getComision());
        destino.depositar(getMonto());
    }

    @Override protected void deshacer() throws MonederoException {
        destino.retirar(getMonto());
        getMonedero().depositar(getMonto() + getComision());
    }
}
