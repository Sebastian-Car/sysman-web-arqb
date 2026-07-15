package com.sysman.recursos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbMenukLocal {

    /**
     * Retorna la cadena de texto con el XML de las opciones de menu a
     * las cuales tiene acceso el usuario logeado
     * 
     * @param compania
     * Compania en la que se ha logeado el usuario
     * @param usuario
     * Codigo del usuario del cual se desean obtener las opciones de
     * menu
     * @return cadena de texto con el XML de las opciones de menu
     * @throws SystemException
     */
    String retornarXMLMenus(String compania, String usuario)
                    throws SystemException;

    void asignarAccesoMenus(
        String compania,
        String grupo,
        String menu,
        String usuario,
        boolean indVer,
        int modulo) throws SystemException;

    String generarClave(int lon) throws SystemException;

    boolean generarClaveUsuario(
        String usuario,
        int tiempo,
        int lon)
                    throws SystemException;

    boolean autorizarAccesoUsuario(String compania, String usuario)
                    throws SystemException;

}
