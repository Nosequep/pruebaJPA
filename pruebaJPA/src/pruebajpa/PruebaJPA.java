/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebajpa;

import controladores.SocioJpaController;
import entidades.Socio;


/**
 *
 * @author labcisco
 */
public class PruebaJPA {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            SocioJpaController c = new SocioJpaController();
//            c.create(new Socio(6, "Panfilomeno Pancracio", "Soda pozole", 23, "Algún lugar del mundo."));
            System.out.println(c.findSocio(6).getApellidos());
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    
}
