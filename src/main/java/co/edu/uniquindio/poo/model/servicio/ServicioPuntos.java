package co.edu.uniquindio.poo.model.servicio;

import co.edu.uniquindio.poo.model.excepciones.MonederoException;
import co.edu.uniquindio.poo.model.excepciones.PuntosInsuficientesException;
import co.edu.uniquindio.poo.model.Beneficio;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.Transaccion;

import java.time.LocalDate;

public class ServicioPuntos {
    public static final double VALOR_BONO = 50.0;
    public static final int DIAS_VIGENCIA = 30;

    public static int costoDe(Beneficio b) {
        return switch (b) {
            case DESCUENTO_TRANSFERENCIA -> 100;
            case MES_SIN_RETIROS -> 500;
            case BONO_SALDO -> 1000;
        };
    }

    public static String descripcionDe(Beneficio b) {
        return switch (b) {
            case DESCUENTO_TRANSFERENCIA -> "10% menos en la comisión de transferencias (30 días)";
            case MES_SIN_RETIROS -> "Un mes sin cargos por retiros (30 días)";
            case BONO_SALDO -> "Bono de 50 unidades en el monedero";
        };
    }

    public int acreditar(Cliente cliente, Transaccion t) {
        int puntos = t.asignarPuntos(cliente.getRango());
        if (puntos > 0) {
            cliente.registrarPuntos(Cliente.movimiento(puntos,
                    "Transacción #" + t.getId() + " (" + t.getTipo() + ")", false));
        }
        return puntos;
    }

    public void revertir(Cliente cliente, Transaccion t) {
        int puntos = t.getPuntosGenerados();
        if (puntos > 0) {
            cliente.registrarPuntos(Cliente.movimiento(-puntos,
                    "Reversión de la transacción #" + t.getId(), false));
        }
    }

    public void canjear(Cliente cliente, Beneficio beneficio, Monedero destinoBono) throws MonederoException {
        exigirPuntosSuficientes(cliente, beneficio);
        switch (beneficio) {
            case BONO_SALDO -> destinoBono.depositar(VALOR_BONO);
            case DESCUENTO_TRANSFERENCIA, MES_SIN_RETIROS ->
                    cliente.activarBeneficio(beneficio, LocalDate.now().plusDays(DIAS_VIGENCIA));
        }
        cliente.registrarPuntos(Cliente.movimiento(-costoDe(beneficio),
                "Canje: " + descripcionDe(beneficio), true));
    }

    private void exigirPuntosSuficientes(Cliente cliente, Beneficio beneficio) throws PuntosInsuficientesException {
        int disponibles = cliente.getPuntosDisponibles();
        if (disponibles < costoDe(beneficio)) {
            throw new PuntosInsuficientesException(disponibles, costoDe(beneficio));
        }
    }
}
