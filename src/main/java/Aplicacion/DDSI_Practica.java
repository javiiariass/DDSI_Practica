package Aplicacion;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
/**
 *
 * @author javi
 */
import Config.HibernateUtil;
import Modelo.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class DDSI_Practica {

    static Scanner scanner = new Scanner(System.in);

    //TODO refactor -> Crear clase aplicar RN. Regla R1
    public static void comprobarFechaNacimientoValida(LocalDate fechaNacimiento) 
                throws IllegalArgumentException{
        if(fechaNacimiento == null || !fechaNacimiento.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de nacimiento debe ser anterior a hoy (R1).");
    } 

    //TODO refactor -> Crear clase aplicar RN. Regla R2
    public static void  comprobarCategoriaValida(LocalDate fechaNacimiento,String categoria) 
                throws IllegalArgumentException{
        int edadCalculada = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        switch(categoria){
            case "INFANTIL" -> {
                if(edadCalculada > 14)
                    throw new IllegalArgumentException("La categoría " + categoria + " exige tener máximo 14 años (R2)");
                
            }
            case "GENERAL", "ESTUDIANTE" -> {
                
            }
            case "SENIOR" -> {
                if(edadCalculada < 65)
                    throw new IllegalArgumentException("La categoría " + categoria + " exige tener al menos 65 años (R2)");
                
            }
            default -> 
                throw new IllegalArgumentException("Categoría no válida: " + categoria);
        }
    }

    //TODO refactor -> Crear clase aplicar RN. Regla R13
    public static String siguienteId(String prefijo, int numDigitos, String ultimoId) {
        int siguiente = (ultimoId == null)
                ? 1
                : Integer.parseInt(ultimoId.substring(prefijo.length())) + 1;
        return String.format("%s%0" + numDigitos + "d", prefijo, siguiente);
    }

    //TODO refactor -> Crear clase aplicar RN. Restricción UNIQUE email en CLIENTE
    public static void comprobarEmailClienteUnico(Session sesion, String email)
            throws IllegalArgumentException {
        if (email != null) {
            Long conEmail = sesion.createQuery(
                    "SELECT COUNT(c) FROM Cliente c WHERE LOWER(c.email) = LOWER(:email)",
                    Long.class)
                    .setParameter("email", email)
                    .uniqueResult();
            if (conEmail != null && conEmail > 0) {
                throw new IllegalArgumentException("Ya existe un cliente con el email: " + email);
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
//                            System.out.println("└── telefono: " + guia[1]);
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
                                + "AND t.capacidad >= :capacidadMinima", Turno.class)
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
                            System.out.println("└── guía: " + turnoActual.getGuia().getNombre() + " " + turnoActual.getGuia().getApellidos());
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
                            System.out.println("├── valoración: " + (reserva.getValoracion() != null ? reserva.getValoracion() : "Sin valoración"));
                            System.out.println("├── cliente: " + reserva.getCliente().getNombre() + " " + reserva.getCliente().getApellidos());
                            System.out.println("└── turno: " + reserva.getTurno().getIdTurno());
                        }
                    }
                    case 10 -> {
                        System.out.print("Introduzca el idioma preferido: ");
                        String idioma = scanner.nextLine();

                        Query<Cliente> consulta = sesion.createNamedQuery("Cliente.findByIdiomaPreferidoNative", Cliente.class)
                                .setParameter("idiomaPreferido", idioma);
                        List<Cliente> clientes = consulta.getResultList();

                        System.out.println("""
                                   --------------------------------------
                                   |        Listado de Clientes         |
                                   --------------------------------------""");

                        for (Cliente cliente : clientes) {
                            System.out.println("*******************");
                            System.out.println(cliente.getIdCliente() + ": " + cliente.getNombre() + " " + cliente.getApellidos());
                            System.out.println("├── teléfono: " + (cliente.getTelefono() != null ? cliente.getTelefono() : "Sin teléfono"));
                            System.out.println("├── fecha nacimiento: " + cliente.getFechaNacimiento());
                            System.out.println("├── email: " + (cliente.getEmail() != null ? cliente.getEmail() : "Sin email"));
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
                    }
                    case 13 -> {
                        System.out.print("Introduzca el email del guia: ");
                        String email = scanner.nextLine().trim();
                    }
                    case 0 -> {
                    }
                    default ->
                        System.out.println("Opcion no valida.");
                }

                // commiteamos la transaccion
                tr.commit();

            } catch (Exception e) {
                if (tr != null && tr.isActive()) {
                    //rollback en caso de error o consulta no completa
                    tr.rollback();
                }
                System.out.println("Error en la recuperación "
                        + e.getMessage());
            } finally {
                if (sesion != null && sesion.isOpen()) {
                    sesion.close();
                }

            }
//            System.out.println("Pulse \"Enter\" para continuar");
//            scanner.next(); 
//            System.out.print("\033[H\033[2J");
//            System.out.flush();

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
