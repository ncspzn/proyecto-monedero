package co.edu.uniquindio.poo.model.servicio;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.TipoTransaccion;
import co.edu.uniquindio.poo.model.Transaccion;

import java.util.List;
import java.util.Optional;
import java.util.stream.DoubleStream;

public class DetectorAtipicas {
    public static final int MUESTRA_MINIMA = 5;
    public static final double FACTOR_DESVIACION = 2.0;

    public record Estadisticas(double promedio, double desviacion, double umbral, int n) { }

    public Optional<Estadisticas> calcular(List<Transaccion> historial, TipoTransaccion tipo) {
        double[] montos = historial.stream()
                .filter(t -> !t.isRevertida() && t.getTipo() == tipo)
                .mapToDouble(Transaccion::getMonto)
                .toArray();
        if (montos.length < MUESTRA_MINIMA) {
            return Optional.empty();
        }
        double promedio = DoubleStream.of(montos).average().orElse(0);
        double varianza = DoubleStream.of(montos)
                .map(m -> (m - promedio) * (m - promedio))
                .sum() / montos.length;
        double desviacion = Math.sqrt(varianza);
        return Optional.of(new Estadisticas(promedio, desviacion,
                promedio + FACTOR_DESVIACION * desviacion, montos.length));
    }

    public boolean esAtipica(Cliente cliente, TipoTransaccion tipo, double monto) {
        return calcular(cliente.getTransacciones(), tipo)
                .map(e -> monto > e.umbral())
                .orElse(false);
    }

    public List<Transaccion> detectar(Cliente cliente) {
        return java.util.Arrays.stream(TipoTransaccion.values())
                .flatMap(tipo -> calcular(cliente.getTransacciones(), tipo)
                        .map(e -> cliente.getTransacciones().stream()
                                .filter(t -> !t.isRevertida() && t.getTipo() == tipo && t.getMonto() > e.umbral()))
                        .orElseGet(java.util.stream.Stream::empty))
                .sorted(java.util.Comparator.comparingInt(Transaccion::getId))
                .toList();
    }
}
