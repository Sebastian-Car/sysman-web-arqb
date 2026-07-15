/*-
 * UsuarioLdap.java
 *
 * 1.0
 * 
 * 17/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.identificacion;

/**
 * Clase que permite el manejo del objeto usuario con sus atributos de
 * inicio de sesion, para el caso del login.
 * 
 * @version 1.0
 * @author José Pascual Gómez Blanco
 * @fecha 17/09/2018
 *
 */
public class UsuarioLdap {
    /**
     * username
     */
    private String user;
    /**
     * contraseña
     */
    private String password;
    /**
     * si e usuario logueado es correcto o no retorna true o false
     */
    private boolean userValido;
    /**
     * nombre del usuario conectado
     */
    private String name;
    /**
     * Tipo de directorio para la conexión JNDI
     * (adirectory,ldap,ldaps)
     */
    private String typeDirectory;

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user
     * the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     * the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the userValido
     */
    public boolean isUserValido() {
        return userValido;
    }

    /**
     * @param userValido
     * the userValido to set
     */
    public void setUserValido(boolean userValido) {
        this.userValido = userValido;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the typeDirectory
     */
    public String getTypeDirectory() {
        return typeDirectory;
    }

    /**
     * @param typeDirectory
     * the typeDirectory to set
     */
    public void setTypeDirectory(String typeDirectory) {
        this.typeDirectory = typeDirectory;
    }

}