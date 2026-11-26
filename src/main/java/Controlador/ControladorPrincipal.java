package Controlador;

import org.hibernate.SessionFactory;

public class ControladorPrincipal {

    private final SessionFactory sesionFactory;

    public ControladorPrincipal(SessionFactory sesionFactory) {
        this.sesionFactory = sesionFactory;

        menu();
    }

    private void menu() {
        String temp = "1";
        switch (temp) {
            case "1" -> {
                new ControladorCliente(sesionFactory);
            }
            case "2" -> {

            }
            case "3" -> {

            }
        }
    }
}
