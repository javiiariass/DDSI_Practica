package Aplicacion;

import Controlador.ConsultadorCRUD;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
/**
 *
 * @author javi
 */
public class DDSI_Practica {

    public static void main(String[] args) {
        ConsultadorCRUD consultador = new ConsultadorCRUD();
        
        consultador.consulta();
        System.out.println();
        consultador.consultaNativa();
        System.out.println();
        consultador.consultaHibernate();
    }
}
