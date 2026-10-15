/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Config.HibernateUtil;
import Modelo.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author javi
 */
public class ConsultadorCRUD {

    SessionFactory sessionFactory;

    public ConsultadorCRUD() {
        try {
// Se obtiene la SessionFactory definida en HibernateUtil
            sessionFactory = HibernateUtil.getSessionFactory();
            System.out.println("Hibernate inicializado correctamente");
        } catch (ExceptionInInitializerError e) {
            Throwable cause = e.getCause();
            System.out.println("Error al inicializar Hibernate. Revise el fichero .cfg.xml: " + cause.getMessage());
        }
    }

    public void consulta() {
        Session sesion = null;
        Transaction tr = null;
        try {
            sesion = this.sessionFactory.openSession();
            tr = sesion.beginTransaction();
            Cliente cliente = sesion.find(Cliente.class, "CL001");
            System.out.println(cliente.getNombre());
            tr.commit();
        } catch (Exception e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
            }
            System.out.println("Error en la recuperación "
                    + e.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) {
                sesion.close();
            }
        }
    }

    public void consultaNativa() {
        Session sesion = null;
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();

            // comienza query
            Query<Cliente> consulta = sesion.createNativeQuery(
                    "SELECT * FROM CLIENTE C WHERE C.email = 'ana.tomia@example.com'", Cliente.class);

            Cliente cliente = (Cliente) consulta.getSingleResult();

            System.out.println(cliente.getNombre());
            tr.commit();
        } catch (Exception e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
            }
            System.out.println("Error en la recuperación "
                    + e.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) {
                sesion.close();
            }
        }
    }

    /**
     * consulta en HQL
     */
    public void consultaHibernate() {
        Session sesion = null;
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();

            // comienza query
            Query<Cliente> consulta = sesion.createQuery(
                    "FROM Cliente C WHERE C.email = 'ana.tomia@example.com'", Cliente.class);

            Cliente cliente = (Cliente) consulta.getSingleResult();

            System.out.println(cliente.getNombre());
            tr.commit();
        } catch (Exception e) {
            if (tr != null && tr.isActive()) {
                tr.rollback();
            }
            System.out.println("Error en la recuperación "
                    + e.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) {
                sesion.close();
            }
        }
    }

}
