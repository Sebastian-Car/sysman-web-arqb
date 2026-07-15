package com.sysman.recursos.ejb;

import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.logica.Formulario;
import com.sysman.logica.Grupo;
import com.sysman.logica.Usuario;

import java.util.Map;

import javax.ejb.Remote;

@Remote
public interface EjbAutorizacionRemote {

    /**
     * Obtiene los grupos a los pertenece el usuario en determinada
     * compania. Estan estructurados en un Map
     * 
     * @param usuario
     * Codigo del usuario del cual se desean obtener los grupos a los
     * cuales pertenece
     * @param compania
     * Compania en la que se ha logeado el usuario
     * @return Map donde la llave es un identificador del Grupo y el
     * valor es un objeto de la clase grupo al cual pertenece el
     * ususario
     * @throws SysmanException
     */
    Map<String, Grupo> getGruposAutorizacion(String usuario, String compania)
                    throws SysmanException;

    /**
     * Obtiene informacion necesaria para la creacion de opciones de
     * menu, pude incluir xml de menus, listas de excluidos y demas
     * dado el retorno como un vector de objetos
     * 
     * @param compania
     * Compania en la que se ha logeado el usuario
     * @param usuario
     * Codigo del usuario del cual se desean obtener las opciones de
     * menu
     * @return Informacion requerida por el ususario para el procesos
     * estructurada en un vector de objetos
     * @throws SysmanException
     */
    Object[] getXMLMenus(String compania,
        Usuario usuario)
                    throws SysmanException;

    /**
     * Obtiene los formularios con sus respectivos permisos para el
     * usuario logeado en determinada compania
     * 
     * @param usuario
     * Codigo del usuario del cual se desean obtener los permisos para
     * cada formulario
     * @param compania
     * Compania en la que se ha logeado el usuario
     * @return Map donde la llave es un identificador del formulario y
     * el valor es un objeto de la clase Formulario donde estan
     * encapsulados todos los permisos para dicho formulario
     * @throws SysmanException
     */
    Map<String, Formulario> getPermisos(String compania, String usuario)
                    throws SysmanException;

}
