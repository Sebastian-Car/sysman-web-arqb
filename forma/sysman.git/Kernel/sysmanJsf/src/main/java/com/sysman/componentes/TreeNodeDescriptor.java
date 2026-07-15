/*-
 * Tree.java
 *
 * 1.0
 * 
 * 3/10/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.componentes;

/**
 * Clase que contiene los atributos de los que se compone un elemento,
 * representado como un nodo de arbol en el componente Tree de
 * Primefaces.
 * 
 * @version 1.0, 3/10/2016
 * @author jrodrigueza
 *
 */
public class TreeNodeDescriptor {

    /**
     * Identificador / Codigo unico.
     */
    private String id;
    /**
     * Nombre / Texto descriptivo.
     */
    private String name;

    /**
     * Crea un nodo representado por un codigo unico y un texto.
     * 
     * @param id
     * Identificador / Codigo unico.
     * @param name
     * Nombre / Texto descriptivo.
     */
    public TreeNodeDescriptor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Trae el identificador del nodo.
     * 
     * @return identificador de nodo
     */
    public String getId() {
        return id;
    }

    /**
     * Asigna el codigo asociado a un nodo.
     * 
     * @param id
     * Identificador
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Trae el texto asociado al nodo.
     * 
     * @return texto del nodo.
     */
    public String getName() {
        return name;
    }

    /**
     * Asigna el texto que describe al nodo.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
