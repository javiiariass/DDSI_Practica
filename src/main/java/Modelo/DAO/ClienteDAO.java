package Modelo.DAO;

import org.hibernate.Session;

import Modelo.Cliente;

public class ClienteDAO {

    public void insertaCliente(Session sesion, Cliente cliente) {
        sesion.persist(cliente);
    }
}
