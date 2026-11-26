package Vista;

import Modelo.Cliente;
import java.util.List;

public class VistaCliente {

    public void muestraClientes_Id_Nombre(List<Cliente> clientes) {
        System.out.println("#Cliente" + "\t" + "Nombre");
        for (Cliente c : clientes) {
            System.out.println(c.getIdCliente() + "\t" + c.getNombre());
        }
    }
}
