package Controlador;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import Modelo.Cliente;
import Modelo.DAO.ClienteDAO;

public class ControladorCliente {
    private final SessionFactory sessionFactory;
    private final ClienteDAO clienteDAO;

    public ControladorCliente(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.clienteDAO = new ClienteDAO();
    }

    public void altaCliente() {
        Session sesion = null;
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            // Petición de datos del cliente
            Cliente cliente = new Cliente();
            clienteDAO.insertaCliente(sesion, cliente);
            tr.commit();
            

        } catch (HibernateException e) {
            if (tr != null && tr.isActive())
                tr.rollback();
            
        } finally {
            if (sesion != null && sesion.isOpen()) {
                sesion.close();
            }
        }
    }

}
