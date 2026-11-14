package co.edu.uniquindio.poo.model;

public class MonederoGastos extends Monedero {
    public MonederoGastos(String nombre, String idPropietario) {
        super(nombre, idPropietario);
    }

    @Override public String getTipo() { return "Gastos diarios"; }
    @Override public double getSaldoMinimo() { return 0.0; }
    @Override public double getUmbralSaldoBajo() { return 20.0; }
}
