package Aplicacion;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
/**
 *
 * @author javi
 */
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import Config.HibernateUtil;
import Modelo.Aventura;
import Modelo.Cliente;
import Modelo.Guia;
import Modelo.Reserva;
import Modelo.Turno;

public class DDSI_Practica {

    static Scanner scanner = new Scanner(System.in);

    // TODO refactor -> Crear clase aplicar RN. Regla R1
    static void comprobarFechaNacimientoValida(LocalDate fechaNacimiento)
            throws IllegalArgumentException {
        if (fechaNacimiento == null || !fechaNacimiento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento debe ser anterior a hoy (R1).");
        }
    }

    // TODO refactor -> Crear clase aplicar RN. Regla R2
    static void comprobarCategoriaValida(LocalDate fechaNacimiento, String categoria)
            throws IllegalArgumentException {
        int edadCalculada = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        switch (categoria) {
            case "INFANTIL" -> {
                if (edadCalculada > 14) {
                    throw new IllegalArgumentException(
                            "La categoría " + categoria + " exige tener máximo 14 años (R2)");
                }

            }
            case "GENERAL", "ESTUDIANTE" -> {

            }
            case "SENIOR" -> {
                if (edadCalculada < 65) {
                    throw new IllegalArgumentException(
                            "La categoría " + categoria + " exige tener al menos 65 años (R2)");
                }

            }
            default ->
                throw new IllegalArgumentException("Categoría no válida: " + categoria);
        }
    }

    // TODO refactor -> Crear clase aplicar RN. Regla RN6
    static void comprobarFechaReservaValida(LocalDateTime fechaReserva, LocalDateTime fechaTurno)
            throws IllegalStateException {

        if (!fechaReserva.isBefore(fechaTurno)) {
            throw new IllegalStateException("La fecha de la reserva debe ser anterior a la fecha del turno");
        }
    }

    // TODO refactor -> Crear clase aplicar RN. Regla RN7
    static void comprobarEdadMinimaClienteParaAventura(Turno turno, LocalDate fechaNacimientoCliente)
            throws IllegalStateException {
        int edadMinimaAventura = turno.getAventura().getEdadMinima();
        int edadCliente = Period.between(fechaNacimientoCliente, turno.getFechaHora().toLocalDate())
                .getYears();

        if (edadCliente < edadMinimaAventura) {
            throw new IllegalStateException("La edad del cliente (" + edadCliente
                    + ") no cumple la edad mínima de la aventura (" + edadMinimaAventura + ").");
        }
    }

    // TODO refactor -> Crear clase aplicar RN. Regla RN8
    static void comprobarCapacidadTurno(Turno turno) {

        int contador = 0;
        Set<Reserva> reservas = turno.getReservas();
        for (Reserva reservaActual : reservas) {
            if ("CONFIRMADA".equals(reservaActual.getEstado())) {
                contador++;
            }
        }

        if (contador >= turno.getCapacidad()) {
            throw new IllegalStateException(
                    "El turno ya tiene " + contador + "/" + turno.getCapacidad() + " plazas ocupadas");
        }

    }

    // TODO refactor -> Crear clase aplicar RN. Regla R13
    static String siguienteId(String prefijo, int numDigitos, String ultimoId) {
        int siguiente = (ultimoId == null)
                ? 1
                : Integer.parseInt(ultimoId.substring(prefijo.length())) + 1;
        return String.format("%s%0" + numDigitos + "d", prefijo, siguiente);
    }

    // TODO refactor -> Crear clase aplicar RN. Restricción UNIQUE email en CLIENTE
    static void comprobarEmailClienteUnico(Session sesion, String email)
            throws IllegalStateException {
        if (email != null) {
            Long conEmail = sesion.createQuery(
                    "SELECT COUNT(c) FROM Cliente c WHERE LOWER(c.email) = LOWER(:email)",
                    Long.class)
                    .setParameter("email", email)
                    .uniqueResult();
            if (conEmail != null && conEmail > 0) {
                throw new IllegalStateException("Ya existe un cliente con el email: " + email);
            }
        }
    }

    public static void main(String[] args) {

        // obtenemos fabrica
        SessionFactory sessionFactory;
        try {
            // Se obtiene la SessionFactory definida en HibernateUtil
            sessionFactory = HibernateUtil.getSessionFactory();
            System.out.println("Hibernate inicializado correctamente");

            System.out.println("Iniciando consultas");
            consultas(sessionFactory);

        } catch (ExceptionInInitializerError e) {
            Throwable cause = e.getCause();
            System.out.println("Error al inicializar Hibernate. Revise el fichero .cfg.xml: " + cause.getMessage());
        } finally {
            HibernateUtil.close();
        }

    }

    private static void consultas(SessionFactory sessionFactory) {

        int opcion;
        do {
            opcion = mostrarMenu();

            // empezamos transacción
            Session sesion = null;
            Transaction tr = null;
            try {
                sesion = sessionFactory.openSession();
                tr = sesion.beginTransaction();

                switch (opcion) {
                    case 1 -> {
                        Query<Guia> consulta = sesion.createQuery(
                                "FROM Guia g", Guia.class);
                        System.out.println("""
                                --------------------------------------
                                |         Listado de guias           |
                                --------------------------------------
                                """);
                        List<Guia> guias = consulta.getResultList();
                        for (Guia guia : guias) {
                            System.out.println("*******************");
                            System.out.println(guia.getIdGuia() + ": " + guia.getNombre() + " " + guia.getApellidos());
                            System.out.println("├── telefono: " + guia.getTelefono());
                            System.out.println("├── mail: " + guia.getEmail());
                            System.out.println("└── fechaAlta: " + guia.getFechaAlta());

                        }
                    }
                    case 2 -> {
                        Query<Guia> consulta = sesion.createNativeQuery(
                                "SELECT * FROM GUIA g", Guia.class);
                        System.out.println("""
                                --------------------------------------
                                |         Listado de guias           |
                                --------------------------------------
                                """);
                        List<Guia> guias = consulta.getResultList();
                        for (Guia guia : guias) {
                            System.out.println("*******************");
                            System.out.println(guia.getIdGuia() + ": " + guia.getNombre() + " " + guia.getApellidos());
                            System.out.println("├── telefono: " + guia.getTelefono());
                            System.out.println("├── mail: " + guia.getEmail());
                            System.out.println("└── fechaAlta: " + guia.getFechaAlta());
                        }
                    }
                    case 3 -> {
                        Query<Guia> consulta = sesion.createNamedQuery(
                                "Guia.findAll", Guia.class);
                        System.out.println("""
                                --------------------------------------
                                |         Listado de guias           |
                                --------------------------------------
                                """);
                        List<Guia> guias = consulta.getResultList();
                        for (Guia guia : guias) {
                            System.out.println("*******************");
                            System.out.println(guia.getIdGuia() + ": " + guia.getNombre() + " " + guia.getApellidos());
                            System.out.println("├── telefono: " + guia.getTelefono());
                            System.out.println("├── mail: " + guia.getEmail());
                            System.out.println("└── fechaAlta: " + guia.getFechaAlta());
                        }

                    }

                    case 4 -> {
                        Query<Object[]> consulta = sesion.createNativeQuery(
                                "select g.nombre, g.telefono FROM GUIA g", Object[].class);
                        System.out.println("""
                                --------------------------------------
                                |       Listado nombres / tlf         |
                                --------------------------------------
                                """);
                        List<Object[]> guias = consulta.getResultList();

                        for (Object[] guia : guias) {
                            System.out.println("*******************");
                            System.out.println(guia[0] + ": " + guia[1]);
                            // System.out.println("└── telefono: " + guia[1]);
                        }
                    }
                    case 5 -> {

                        System.out.print("Introduzca el tipo de las aventuras a mostrar: ");
                        String tipoAventura = scanner.nextLine();

                        Query<Object[]> consulta = sesion.createNativeQuery(
                                "select a.nombre, a.dificultad FROM AVENTURA a WHERE a.tipo = :tipo",
                                Object[].class).setParameter("tipo", tipoAventura);

                        System.out.println("""
                                --------------------------------------
                                |   Listado aventuras / dificultad   |
                                --------------------------------------
                                """);
                        List<Object[]> guias = consulta.getResultList();

                        if (guias.isEmpty()) {
                            System.out.println("No hay ninguna aventura de ese tipo");
                            break;
                        }

                        System.out.println("*******************");
                        for (Object[] guia : guias) {
                            System.out.println(guia[0] + ": " + guia[1]);
                        }
                    }
                    case 6 -> {
                        System.out.print("Introduzca número de teléfono del guía: ");
                        String numTel = scanner.nextLine();

                        // como telefono no es unico en este sistema, podría repetirse
                        Query<Guia> consulta = sesion.createNamedQuery(
                                "Guia.findByTelefono", Guia.class)
                                .setParameter("telefono", numTel);

                        System.out.println("""
                                --------------------------------------
                                |         Listado de guias           |
                                --------------------------------------
                                """);
                        List<Guia> guias = consulta.getResultList();
                        System.out.println("*******************");
                        for (Guia guia : guias) {
                            System.out.println(guia.getIdGuia() + ": " + guia.getNombre() + " " + guia.getApellidos());
                        }
                    }
                    case 7 -> {
                        System.out.print("Introduzca el nombre de la aventura: ");
                        String nombreAventura = scanner.nextLine();

                        // como nombre de aventura no es unico en este sistema, podría repetirse
                        Query<Aventura> consulta = sesion.createNamedQuery(
                                "Aventura.findByNombre", Aventura.class)
                                .setParameter("nombre", nombreAventura);

                        System.out.println("""
                                --------------------------------------
                                |        Listado de aventuras        |
                                --------------------------------------
                                """);
                        List<Aventura> aventuras = consulta.getResultList();

                        for (Aventura aventura : aventuras) {
                            System.out.println("*******************");
                            System.out.println(aventura.getIdAventura() + ": " + aventura.getNombre());
                            System.out.println("├── descripción: " + aventura.getDescripcion());
                            System.out.println("├── tipo: " + aventura.getTipo());
                            System.out.println("├── dificultad: " + aventura.getDificultad());
                            System.out.println("├── duración: " + aventura.getDuracion());
                            System.out.println("├── precio: " + aventura.getPrecio());
                            System.out.println("└── edad mínima: " + aventura.getEdadMinima());
                        }
                    }
                    case 8 -> {
                        System.out.print("Introduzca id de la aventura: ");
                        String idAventura = scanner.nextLine();
                        System.out.print("Introduzca la capacidad mínima de la aventura: ");
                        int numCap = scanner.nextInt();
                        scanner.nextLine();

                        Query<Turno> consulta = sesion.createNativeQuery(
                                "SELECT * "
                                + "FROM TURNO t "
                                + "WHERE t.idAventura = :idAventura "
                                + "AND t.capacidad >= :capacidadMinima",
                                Turno.class)
                                .setParameter("capacidadMinima", numCap)
                                .setParameter("idAventura", idAventura);

                        List<Turno> turnos = consulta.getResultList();
                        System.out.println("""
                                --------------------------------------
                                |         Listado de Turnos          |
                                --------------------------------------""");
                        System.out.println("Aventura: " + idAventura);
                        for (Turno turnoActual : turnos) {
                            System.out.println("*******************");
                            System.out.println("Turno: " + turnoActual.getIdTurno());
                            System.out.println("├── fecha y hora: " + turnoActual.getFechaHora());
                            System.out.println("├── capacidad: " + turnoActual.getCapacidad());
                            System.out.println("└── guía: " + turnoActual.getGuia().getNombre() + " "
                                    + turnoActual.getGuia().getApellidos());
                        }
                    }
                    case 9 -> {
                        System.out.print("Introduzca estado de la reserva: ");
                        String estadoReserva = scanner.nextLine();

                        Query<Reserva> consulta = sesion.createNamedQuery("Reserva.findByEstado", Reserva.class)
                                .setParameter("estado", estadoReserva);
                        List<Reserva> reservas = consulta.getResultList();

                        System.out.println("""
                                --------------------------------------
                                |        Listado de Reservas         |
                                --------------------------------------""");

                        for (Reserva reserva : reservas) {
                            System.out.println("*******************");
                            System.out.println("Reserva: " + reserva.getIdReserva());
                            System.out.println("├── fecha: " + reserva.getFechaReserva());
                            System.out.println("├── importe: " + reserva.getImporte());
                            System.out.println("├── estado: " + reserva.getEstado());
                            System.out.println("├── valoración: "
                                    + (reserva.getValoracion() != null ? reserva.getValoracion() : "Sin valoración"));
                            System.out.println("├── cliente: " + reserva.getCliente().getNombre() + " "
                                    + reserva.getCliente().getApellidos());
                            System.out.println("└── turno: " + reserva.getTurno().getIdTurno());
                        }
                    }
                    case 10 -> {
                        System.out.print("Introduzca el idioma preferido: ");
                        String idioma = scanner.nextLine();

                        Query<Cliente> consulta = sesion
                                .createNamedQuery("Cliente.findByIdiomaPreferidoNative", Cliente.class)
                                .setParameter("idiomaPreferido", idioma);
                        List<Cliente> clientes = consulta.getResultList();

                        System.out.println("""
                                --------------------------------------
                                |        Listado de Clientes         |
                                --------------------------------------""");

                        for (Cliente cliente : clientes) {
                            System.out.println("*******************");
                            System.out.println(
                                    cliente.getIdCliente() + ": " + cliente.getNombre() + " " + cliente.getApellidos());
                            System.out.println("├── teléfono: "
                                    + (cliente.getTelefono() != null ? cliente.getTelefono() : "Sin teléfono"));
                            System.out.println("├── fecha nacimiento: " + cliente.getFechaNacimiento());
                            System.out.println(
                                    "├── email: " + (cliente.getEmail() != null ? cliente.getEmail() : "Sin email"));
                            System.out.println("├── idioma preferido: " + cliente.getIdiomaPreferido());
                            System.out.println("└── categoría: " + cliente.getCategoria());
                        }
                    }
                    case 11 -> {
                        System.out.print("Introduzca nombre: ");
                        String nombre = scanner.nextLine().trim();
                        System.out.print("Introduzca apellidos: ");
                        String apellidos = scanner.nextLine().trim();
                        System.out.print("Introduzca telefono (opcional, pulse Enter para omitir): ");
                        String telefonoInput = scanner.nextLine().trim();
                        String telefono = telefonoInput.isEmpty() ? null : telefonoInput;
                        System.out.print("Introduzca fecha de nacimiento (dd/MM/yyyy): ");
                        LocalDate fechaNacimiento = LocalDate.parse(
                                scanner.nextLine().trim(),
                                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        System.out.print("Introduzca email (opcional, pulse Enter para omitir): ");
                        String emailInput = scanner.nextLine().trim();
                        String email = emailInput.isEmpty() ? null : emailInput;
                        System.out.print("Introduzca idioma preferido (ES, EN, FR, DE): ");
                        String idiomaPreferido = scanner.nextLine().trim().toUpperCase();
                        System.out.print("Introduzca categoria (GENERAL, INFANTIL, ESTUDIANTE, SENIOR): ");
                        String categoria = scanner.nextLine().trim().toUpperCase();

                        if (nombre.isEmpty() || apellidos.isEmpty()) {
                            throw new IllegalArgumentException("El nombre y los apellidos son obligatorios.");
                        }
                        if (!Set.of("ES", "EN", "FR", "DE").contains(idiomaPreferido)) {
                            throw new IllegalArgumentException("Idioma preferido no valido: " + idiomaPreferido);
                        }

                        comprobarFechaNacimientoValida(fechaNacimiento);
                        comprobarCategoriaValida(fechaNacimiento, categoria);
                        comprobarEmailClienteUnico(sesion, email);

                        String ultimoId = sesion.createQuery(
                                "SELECT MAX(c.idCliente) FROM Cliente c", String.class)
                                .uniqueResult();
                        String idCliente = siguienteId("CL", 3, ultimoId);

                        Cliente nuevoCliente = new Cliente(
                                idCliente, nombre, apellidos, telefono,
                                fechaNacimiento, email, idiomaPreferido, categoria);
                        sesion.persist(nuevoCliente);
                        System.out.println("Cliente insertado correctamente con ID: " + idCliente);
                    }
                    case 12 -> {
                        System.out.print("Introduzca el email del cliente a eliminar: ");
                        String email = scanner.nextLine().trim();
                        if (email.isEmpty()) {
                            throw new IllegalArgumentException("Introduce un email");
                        }

                        Cliente cliente = sesion.createNamedQuery("Cliente.findByEmail", Cliente.class)
                                .setParameter("email", email)
                                .uniqueResult();

                        if (cliente == null) {
                            System.out.println("No existe ningun cliente con el email: " + email);
                        } else {
                            sesion.remove(cliente);
                            sesion.flush(); // hace que se ejecute DELETE ya, antes de llegar al commit
                            System.out.println("Cliente eliminado correctamente: " + cliente.getIdCliente());
                        }

                    }
                    case 13 -> {
                        System.out.print("Introduzca el email del guia: ");
                        String email = scanner.nextLine().trim();
                        if (email.isEmpty()) {
                            throw new IllegalArgumentException("Introduce un email");
                        }

                        Guia guia = sesion.createNamedQuery("Guia.findByEmail", Guia.class)
                                .setParameter("email", email)
                                .uniqueResult();

                        if (guia == null) {
                            System.out.println("No existe ningun guia con el email: " + email);
                        } else if (guia.getTurnos().isEmpty()) {
                            System.out.println("El guia " + guia.getNombre() + " " + guia.getApellidos()
                                    + " no tiene turnos asignados.");
                        } else {
                            System.out.println("""
                                    --------------------------------------
                                    |      Turnos asignados al guia      |
                                    --------------------------------------""");
                            System.out.println("Guia: " + guia.getNombre() + " " + guia.getApellidos());
                            for (Turno turno : guia.getTurnos()) {
                                System.out.println("*******************");
                                System.out.println("Turno: " + turno.getIdTurno());
                                System.out.println("├── aventura: " + turno.getAventura().getNombre());
                                System.out.println("├── fecha y hora: " + turno.getFechaHora());
                                System.out.println("└── capacidad: " + turno.getCapacidad());
                            }
                        }
                    }
                    case 14 -> {
                        System.out.print("Introduzca el email del cliente: ");
                        String email = scanner.nextLine().trim();
                        if (email.isEmpty()) {
                            throw new IllegalArgumentException("Introduce un email");
                        }

                        Cliente cliente = sesion.createNamedQuery("Cliente.findByEmail", Cliente.class)
                                .setParameter("email", email)
                                .uniqueResult();

                        if (cliente == null) {
                            System.out.println("No existe ningun cliente con el email: " + email);
                        } else {
                            Set<Aventura> aventuras = new LinkedHashSet<>();
                            for (Reserva reserva : cliente.getReservas()) {
                                if (!"CANCELADA".equals(reserva.getEstado())) {
                                    aventuras.add(reserva.getTurno().getAventura());
                                }
                            }

                            if (aventuras.isEmpty()) {
                                System.out.println("El cliente " + cliente.getNombre() + " " + cliente.getApellidos()
                                        + " no tiene reservas activas (no canceladas).");
                            } else {
                                System.out.println("""
                                        --------------------------------------
                                        |   Aventuras reservadas (activas)   |
                                        --------------------------------------""");
                                for (Aventura aventura : aventuras) {
                                    System.out.println("*******************");
                                    System.out.println(aventura.getIdAventura() + ": " + aventura.getNombre());
                                    System.out.println("├── descripcion: "
                                            + (aventura.getDescripcion() != null ? aventura.getDescripcion()
                                            : "Sin descripcion"));
                                    System.out.println("├── tipo: " + aventura.getTipo());
                                    System.out.println("├── dificultad: " + aventura.getDificultad());
                                    System.out.println("├── duracion: " + aventura.getDuracion());
                                    System.out.println("├── precio: " + aventura.getPrecio());
                                    System.out.println("└── edad minima: " + aventura.getEdadMinima());
                                }
                            }
                        }
                    }
                    case 15 -> {
                        System.out.print("Introduzca el nombre de la aventura: ");
                        String nombreAventura = scanner.nextLine().trim();
                        if (nombreAventura.isEmpty()) {
                            throw new IllegalArgumentException("Introduce el nombre de la aventura");
                        }

                        // como nombre de aventura no es único, puede haber varias aventuras con mismo
                        // nombre
                        List<Aventura> aventuras = sesion.createNamedQuery("Aventura.findByNombre", Aventura.class)
                                .setParameter("nombre", nombreAventura).getResultList();

                        // por cada aventura, vemos los clientes de cada uno de sus turnos
                        if (aventuras.isEmpty()) {
                            System.out.println("No existe ninguna aventura con el nombre: " + nombreAventura);
                        } else {
                            for (Aventura aventura : aventuras) {

                                // linkedHashSet para evitar duplicados dentro de cada aventura
                                LinkedHashSet<Cliente> listaClientes = new LinkedHashSet<>();
                                for (Turno turnoActual : aventura.getTurnos()) {
                                    for (Reserva reservaActual : turnoActual.getReservas()) {
                                        if (!"CANCELADA".equals(reservaActual.getEstado())) // getEstado != "CANCELADA"
                                        {
                                            listaClientes.add(reservaActual.getCliente());
                                        }

                                    }
                                }

                                System.out.println("""
                                        --------------------------------------
                                        |    Clientes con reserva activa     |
                                        --------------------------------------""");
                                System.out.println(
                                        "Aventura: " + aventura.getIdAventura() + " - " + aventura.getNombre());

                                if (listaClientes.isEmpty()) {
                                    System.out.println("No hay clientes con reservas no canceladas en esta aventura.");
                                } else {
                                    for (Cliente cliente : listaClientes) {
                                        System.out.println("*******************");
                                        System.out.println(cliente.getIdCliente() + ": " + cliente.getNombre() + " "
                                                + cliente.getApellidos());
                                        System.out.println("├── telefono: "
                                                + (cliente.getTelefono() != null ? cliente.getTelefono()
                                                : "Sin telefono"));
                                        System.out.println("├── fecha nacimiento: " + cliente.getFechaNacimiento());
                                        System.out.println("├── email: "
                                                + (cliente.getEmail() != null ? cliente.getEmail() : "Sin email"));
                                        System.out.println("├── idioma preferido: " + cliente.getIdiomaPreferido());
                                        System.out.println("└── categoria: " + cliente.getCategoria());
                                    }
                                }
                            }
                        }
                    }
                    case 16 -> {
                        System.out.print("Introduzca el identificador del cliente (ej. CL001): ");
                        String idCliente = scanner.nextLine().trim().toUpperCase();
                        System.out.print("Introduzca el identificador del turno (ej. T0001): ");
                        String idTurno = scanner.nextLine().trim().toUpperCase();
                        if (idCliente.isEmpty() || idTurno.isEmpty()) {
                            throw new IllegalArgumentException(
                                    "El identificador de cliente y de turno son obligatorios.");
                        }

                        Cliente clienteBuscado = sesion.find(Cliente.class, idCliente);
                        Turno turnoBuscado = sesion.find(Turno.class, idTurno);

                        if (clienteBuscado == null) {
                            throw new IllegalArgumentException(
                                    "El cliente con id " + idCliente + " no se encuentra en la base de datos");
                        }
                        if (turnoBuscado == null) {
                            throw new IllegalArgumentException(
                                    "El turno con id " + idTurno + " no se encuentra en la base de datos");
                        }

                        // comprobar si el cliente tiene reserva
                        boolean encontrado = false;
                        for (Reserva reservaActual : turnoBuscado.getReservas()) {
                            if (reservaActual.getCliente().equals(clienteBuscado)) {
                                encontrado = true;
                                break;
                            }
                        }

                        if (encontrado) {
                            throw new IllegalStateException("El cliente " + idCliente
                                    + " ya tiene una reserva en el turno " + turnoBuscado.getIdTurno() + ".");
                        }

                        LocalDateTime fechaReserva = LocalDateTime.now();
                        comprobarFechaReservaValida(fechaReserva, turnoBuscado.getFechaHora());

                        comprobarEdadMinimaClienteParaAventura(turnoBuscado,
                                clienteBuscado.getFechaNacimiento());

                        String estadoReserva = "CONFIRMADA";
                        comprobarCapacidadTurno(turnoBuscado);

                        BigDecimal precioAventura = turnoBuscado.getAventura().getPrecio();

                        // Añadir cliente
                        String ultimoId = sesion.createQuery(
                                "SELECT MAX(r.idReserva) FROM Reserva r", String.class)
                                .uniqueResult();
                        String idReserva = siguienteId("R", 4, ultimoId);

                        Reserva reservaCliente = new Reserva(idReserva, fechaReserva, precioAventura, estadoReserva,
                                null, clienteBuscado, turnoBuscado);

                        clienteBuscado.getReservas().add(reservaCliente);
                        turnoBuscado.getReservas().add(reservaCliente);
                        sesion.persist(reservaCliente);

                        System.out.println("Reserva creada correctamente:");
                        System.out.println("Reserva: " + reservaCliente.getIdReserva());
                        System.out.println("├── cliente: " + clienteBuscado.getIdCliente() + " - "
                                + clienteBuscado.getNombre() + " " + clienteBuscado.getApellidos());
                        System.out.println("├── turno: " + turnoBuscado.getIdTurno() + " ("
                                + turnoBuscado.getAventura().getNombre() + " - " + turnoBuscado.getFechaHora() + ")");
                        System.out.println("├── fecha reserva: " + reservaCliente.getFechaReserva());
                        System.out.println("├── importe: " + reservaCliente.getImporte());
                        System.out.println("└── estado: " + reservaCliente.getEstado());
                    }
                    case 17 -> {
                        System.out.print("Introduzca el identificador del cliente (ej. CL001): ");
                        String idCliente = scanner.nextLine().trim().toUpperCase();
                        System.out.print("Introduzca el identificador del turno (ej. T0001): ");
                        String idTurno = scanner.nextLine().trim().toUpperCase();
                        if (idCliente.isEmpty() || idTurno.isEmpty()) {
                            throw new IllegalArgumentException(
                                    "El identificador de cliente y de turno son obligatorios.");
                        }

                        Cliente clienteBuscado = sesion.find(Cliente.class, idCliente);
                        Turno turnoBuscado = sesion.find(Turno.class, idTurno);

                        if (clienteBuscado == null) {
                            throw new IllegalArgumentException(
                                    "El cliente con id " + idCliente + " no se encuentra en la base de datos");
                        }
                        if (turnoBuscado == null) {
                            throw new IllegalArgumentException(
                                    "El turno con id " + idTurno + " no se encuentra en la base de datos");
                        }

                        // comprobar si el cliente tiene reserva
                        Reserva reservaCliente = null;
                        for (Reserva reservaActual : turnoBuscado.getReservas()) {
                            if (reservaActual.getCliente().equals(clienteBuscado)) {
                                reservaCliente = reservaActual;
                                break;
                            }
                        }

                        if (reservaCliente == null) {
                            throw new IllegalStateException("El cliente " + idCliente
                                    + " no tiene una reserva en el turno " + turnoBuscado.getIdTurno() + ".");
                        }

                        switch (reservaCliente.getEstado()) {
                            case "CONFIRMADA", "PENDIENTE" -> {
                                String estadoAnterior = reservaCliente.getEstado();
                                reservaCliente.setEstado("CANCELADA");

                                System.out.println("Reserva cancelada correctamente:");
                                System.out.println("Reserva: " + reservaCliente.getIdReserva());
                                System.out.println("├── cliente: " + clienteBuscado.getIdCliente() + " - "
                                        + clienteBuscado.getNombre() + " " + clienteBuscado.getApellidos());
                                System.out.println("├── turno: " + turnoBuscado.getIdTurno() + " ("
                                        + turnoBuscado.getAventura().getNombre() + " - " + turnoBuscado.getFechaHora()
                                        + ")");
                                System.out.println("├── estado anterior: " + estadoAnterior);
                                System.out.println("└── estado actual: " + reservaCliente.getEstado());
                            }
                            case "CANCELADA" -> {
                                System.out.println("El cliente " + idCliente + " ya tenía cancelada la reserva "
                                        + reservaCliente.getIdReserva() + " en el turno " + idTurno);
                            }
                            default -> {
                                throw new IllegalStateException(
                                        "Estado de reserva no válido: " + reservaCliente.getEstado());
                            }
                        }

                    }
                    case 18 -> {
                        System.out.print("Introduzca el email del guia: ");
                        String email = scanner.nextLine().trim();
                        if (email.isEmpty()) {
                            throw new IllegalArgumentException("Introduce un email");
                        }

                        Query<Object[]> consulta = sesion.createQuery(
                                "SELECT t.aventura.nombre, t.fechaHora, t.aventura.duracion "
                                + "FROM Turno t "
                                + "WHERE t.guia.email = :email "
                                + "ORDER BY t.fechaHora ASC",
                                Object[].class)
                                .setParameter("email", email);

                        List<Object[]> agenda = consulta.getResultList();

                        if (agenda.isEmpty()) {
                            System.out.println("No existen turnos asignados para el guia con email: " + email);
                        } else {
                            System.out.println("""
                                    --------------------------------------
                                    |          Agenda del guia           |
                                    --------------------------------------""");
                            System.out.println("Guia: " + email);
                            for (Object[] fila : agenda) {
                                String nombreAventura = (String) fila[0];
                                LocalDateTime fechaHoraInicio = (LocalDateTime) fila[1];
                                short duracion = (Short) fila[2];
                                LocalDateTime fechaHoraFin = fechaHoraInicio.plusMinutes(duracion);

                                System.out.println("*******************");
                                System.out.println("Aventura: " + nombreAventura);
                                System.out.println("├── fecha y hora de inicio: " + fechaHoraInicio);
                                System.out.println("└── hora de finalizacion: " + fechaHoraFin.toLocalTime());
                            }
                        }
                    }
                    case 0 -> {
                    }
                    default ->
                        System.out.println("Opcion no valida.");
                }

                // commiteamos la transaccion
                tr.commit();

            } catch (org.hibernate.exception.ConstraintViolationException e) {
                if (tr != null && tr.isActive()) {
                    tr.rollback();
                }
                System.out.println(
                        "No se puede completar la operacion por restriccion de integridad (clave ajena/unica).");
            } catch (Exception e) {
                if (tr != null && tr.isActive()) {
                    // rollback en caso de error o consulta no completa
                    tr.rollback();
                }
                System.out.println("Error en la recuperación "
                        + e.getMessage());
            } finally {
                if (sesion != null && sesion.isOpen()) {
                    sesion.close();
                }

            }
            // System.out.println("Pulse \"Enter\" para continuar");
            // scanner.next();
            // System.out.print("\033[H\033[2J");
            // System.out.flush();

        } while (opcion != 0);
    }

    private static int mostrarMenu() {
        int opcion;
        do {

            System.out.println();
            System.out.println("MENU DE CONSULTAS");
            System.out.println("1. Informacion de los guias (HQL)");
            System.out.println("2. Informacion de los guias (SQL Nativo)");
            System.out.println("3. Informacion de los guias (Consulta nombrada)");
            System.out.println("4. Nombre y telefono de los guias");
            System.out.println("5. Nombre y dificultad de las aventuras de un tipo");
            System.out.println("6. Nombre y apellidos de un guia por telefono");
            System.out.println("7. Informacion de aventura por nombre");
            System.out.println("8. Turnos de una aventura por capacidad minima");
            System.out.println("9. Informacion de reservas por estado (HQL)");
            System.out.println("10. Informacion de clientes por idioma preferido (SQL nativo)");
            System.out.println("11. Insercion de cliente");
            System.out.println("12. Borrado de cliente");
            System.out.println("13. Turnos asignados a un guia");
            System.out.println("14. Aventuras reservadas por un cliente");
            System.out.println("15. Clientes con reserva en una aventura");
            System.out.println("16. Reserva de un cliente en un turno");
            System.out.println("17. Cancelacion de una reserva");
            System.out.println("18. Agenda de un guia");
            System.out.println("19. Reservas e importes de un cliente");
            System.out.println("20. Clientes mayores de una edad");
            System.out.println("0. Salir");
            System.out.print("Elige una opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();// limpiar '\n' de buffer
        } while (opcion < 0 || opcion > 20);

        return opcion;
    }
}
