/*-
 * Fmrsegpwd.java
 *
 * 1.0
 * 
 * 22/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.chipfut;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped; import java.util.Map;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.jsfutil.JsfUtil;

/**
 * Formulario que pide una contrase�a para acceder al formulario
 * Ecuaciones
 * 
 * CLASE NO IMPLEMEMNTADA
 *
 * @version 1.0, 22/03/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FmrsegpwdControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Variable que almacena la cade de car�cteres ingresada
     */
    private String cadena;
    /**
     * 
     *
     */
    private String tituloPaginaEmpresaParametrizado;
    /**
     * 
     */
    private String labelPaginaEmpresaParametrizada;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FmrsegpwdControlador
     */
    public FmrsegpwdControlador() {
        super();
        compania = SessionUtil.getCompania();
        
        tituloPaginaEmpresaParametrizado = idioma.getString("EM_FR1366");
        tituloPaginaEmpresaParametrizado = tituloPaginaEmpresaParametrizado.replace("s$empresaparam$s", JsfUtil.getTituloPaginaEmpresaParametrizada());
        
        labelPaginaEmpresaParametrizada = idioma.getString("TT_LB36133");
        labelPaginaEmpresaParametrizada = labelPaginaEmpresaParametrizada.replace("s$empresaparam$s", JsfUtil.getTituloPaginaEmpresaParametrizada());
        try {
            numFormulario = 1366;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1366-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * Me.txtPWD = "" StrPwd = "" End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnAceptar en la vista
     *
     */
    public void oprimirBtnAceptar() {
        // <CODIGO_DESARROLLADO>
        String comparacadena = "212116";

        if (!cadena.equals(comparacadena)) {
            JsfUtil.agregarMensajeFatal(
                            "No est� autorizado para ingresar con este Usuario"
                                + "Comun�quese con el administrador del Sistema en su empresa.");
        }
        else {
            // SessionUtil.cargarFormulario("1369");

            SessionUtil.redireccionar("/ecuaciones.sysman");

        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable cadena
     * 
     * @return cadena
     */
    public String getCadena() {
        return cadena;
    }

    /**
     * Asigna la variable cadena
     * 
     * @param cadena
     * Variable a asignar en cadena
     */
    public void setCadena(String cadena) {
        this.cadena = cadena;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

	public String getTituloPaginaEmpresaParametrizado() {
		return tituloPaginaEmpresaParametrizado;
	}

	public void setTituloPaginaEmpresaParametrizado(String tituloPaginaEmpresaParametrizado) {
		this.tituloPaginaEmpresaParametrizado = tituloPaginaEmpresaParametrizado;
	}

	public String getLabelPaginaEmpresaParametrizada() {
		return labelPaginaEmpresaParametrizada;
	}

	public void setLabelPaginaEmpresaParametrizada(String labelPaginaEmpresaParametrizada) {
		this.labelPaginaEmpresaParametrizada = labelPaginaEmpresaParametrizada;
	}
}
