package co.edu.uniquindio.poo.model.servicio;

import co.edu.uniquindio.poo.model.CanalNotificacion;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.Notificable;
import co.edu.uniquindio.poo.model.Notificacion;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

public class ServicioNotificaciones {
    public interface Notificador {
        CanalNotificacion canal();
        void enviar(Notificable destinatario, String mensaje);
    }

    private abstract static class CanalSimulado implements Notificador {
        @Override
        public void enviar(Notificable destinatario, String mensaje) {
            destinatario.recibirNotificacion(new Notificacion(LocalDateTime.now(), canal(), mensaje));
        }
    }

    public static class CanalCorreo extends CanalSimulado {
        @Override public CanalNotificacion canal() { return CanalNotificacion.CORREO; }
    }

    public static class CanalWhatsApp extends CanalSimulado {
        @Override public CanalNotificacion canal() { return CanalNotificacion.WHATSAPP; }
    }

    public static class CanalSms extends CanalSimulado {
        @Override public CanalNotificacion canal() { return CanalNotificacion.SMS; }
    }

    private final Map<CanalNotificacion, Notificador> canales = new EnumMap<>(CanalNotificacion.class);

    public ServicioNotificaciones() {
        for (Notificador n : new Notificador[]{new CanalCorreo(), new CanalWhatsApp(), new CanalSms()}) {
            canales.put(n.canal(), n);
        }
    }

    public void notificar(Cliente cliente, String mensaje) {
        canales.get(cliente.getCanalPreferido()).enviar(cliente, mensaje);
    }

    public void alertarSaldoBajo(Cliente cliente, Monedero monedero) {
        notificar(cliente, String.format("Saldo bajo en '%s': $%,.2f.", monedero.getNombre(), monedero.getSaldo()));
    }

    public void alertarSospechosa(Cliente cliente, double monto, double umbral) {
        notificar(cliente, String.format(
                "Transacción potencialmente sospechosa: $%,.2f supera tu umbral habitual de $%,.2f.", monto, umbral));
    }
}
