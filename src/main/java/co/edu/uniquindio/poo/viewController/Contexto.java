package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.servicio.Banco;
import co.edu.uniquindio.poo.model.servicio.DatosDemo;
import co.edu.uniquindio.poo.model.servicio.Planificador;
import co.edu.uniquindio.poo.model.servicio.ServicioReportes;

import java.util.ArrayList;
import java.util.List;

public final class Contexto {
    private static final Contexto INSTANCIA = new Contexto();

    private final Banco banco = DatosDemo.crear();
    private final Planificador planificador = new Planificador(banco);
    private final ServicioReportes reportes = new ServicioReportes();
    private final List<Runnable> observadores = new ArrayList<>();
    private Cliente clienteActual;

    private Contexto() { }

    public static Contexto get() { return INSTANCIA; }

    public Banco getBanco() { return banco; }
    public Planificador getPlanificador() { return planificador; }
    public ServicioReportes getReportes() { return reportes; }
    public Cliente getClienteActual() { return clienteActual; }

    public void setClienteActual(Cliente cliente) {
        this.clienteActual = cliente;
        refrescar();
    }

    public void suscribir(Runnable observador) { observadores.add(observador); }

    public void refrescar() {
        if (clienteActual != null) {
            observadores.forEach(Runnable::run);
        }
    }
}
