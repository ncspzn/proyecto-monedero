package co.edu.uniquindio.poo.model;

public interface Notificable {
    void recibirNotificacion(Notificacion notificacion);
    CanalNotificacion getCanalPreferido();
}
