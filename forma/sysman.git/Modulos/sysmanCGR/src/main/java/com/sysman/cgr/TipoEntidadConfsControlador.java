/*-
 * TipoEntidadConfsControlador.java
 *
 * 1.0
 * 
 * 03/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped; import java.util.Map;

import org.primefaces.event.SelectEvent;

/**
 * Esta clase es el controlador para el formulario que permite
 * actualizar informacion basica de la entidad de ingreso en Access
 * "TipoEntidad", el cual es llamado desde Entes de Control\Schip -
 * CGR\Configuraci�n / Informes\Configuraci�n B�sica Entidad
 *
 * 
 * @version 1.0, 03/03/2017
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class TipoEntidadConfsControlador extends BeanBaseDatosAcme {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO en el formulario, almacena el texto
     * CODIGO el cual es un campo del registro
     */
    private final String cCodigo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NOMBRECODSCHIP en el formulario, almacena el
     * texto NOMBRECODSCHIP el cual es un campo del registro
     */
    private final String cNombreCod;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el comboBox TipoEntidad
     */
    private List<Registro> listaTipoEntidad;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el comboBox CodigoSchip
     */
    private RegistroDataModel listaCodigoSchip;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de TipoEntidadConfsControlador
     */
    public TipoEntidadConfsControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCodigo = "CODIGO";
        cNombreCod = "NOMBRECODSCHIP";
        try {
            numFormulario = 1337;
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoSchip();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaTipoEntidad();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        tabla = "COMPANIA";
        buscarLlave();
        asignarOrigenDatos();
        reasignarOrigenGrilla();
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = "SELECT COMPANIA.CODIGO," +
            "   COMPANIA.NOMBRE," +
            "   COMPANIA.TIPOENTIDAD," +
            "   COMPANIA.CODIGOSCHIP," +
            "   CODENTIDADES_SCHIP.NOMBRE NOMBRECODSCHIP" +
            "  FROM COMPANIA " +
            " INNER JOIN CODENTIDADES_SCHIP" +
            "    ON COMPANIA.CODIGOSCHIP = CODENTIDADES_SCHIP.CODIGO";
    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     * 
     */
    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = "";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenGrilla);
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoEntidad Permite visualizat los tipos de
     * entidades que han sido definidos en la tabla ENTIDADES_SCHIP
     *
     */
    public void cargarListaTipoEntidad() {
        listaTipoEntidad = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "SELECT " +
                            "     ENTIDADES_SCHIP.CODIGO, " +
                            "     ENTIDADES_SCHIP.NOMBRE " +
                            " FROM " +
                            "     ENTIDADES_SCHIP " +
                            " ORDER BY " +
                            "     ENTIDADES_SCHIP.CODIGO");
    }

    /**
     * 
     * Carga la lista listaCodigoSchip Codigos que han sido
     * almacenados en la tabla CODENTIDADES_SCHIP
     */
    public void cargarListaCodigoSchip() {
        listaCodigoSchip = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR1337_nuevo:TBCB4349", "SELECT " +
                            "     CODENTIDADES_SCHIP.CODIGO, " +
                            "     CODENTIDADES_SCHIP.NOMBRE NOMBRECODSCHIP" +
                            " FROM " +
                            "     CODENTIDADES_SCHIP " +
                            " ORDER BY " +
                            "     CODENTIDADES_SCHIP.CODIGO",
                        true, cCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoSchip Obtiene el valor del codigo Schip que ha sido
     * seleccionado en el formulario y lo almacena en el registo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoSchip(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGOSCHIP",
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put("NOMBRECODSCHIP",
                        registroAux.getCampos().get(cNombreCod));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> key = new HashMap<>();
        key.put(cCodigo, compania);
        rid = key;
        precargarRegistro();
        cargarRegistro(rid, ACCION_MODIFICAR);
        registroIni = new HashMap<>(registro.getCampos());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return Si el proceso previo a la insercion fue exitoso
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return Si el proceso de insercion fue exitoso
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return Si el proceso previo a la actualizacion fue exitoso
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cNombreCod);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return Si el proceso previo a la actualizacion fue exitoso
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        abrirFormulario();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return Si el proceso previo a la eliminacion fue exitoso
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return Si el proceso de eliminacion fue exitoso
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTipoEntidad
     * 
     * @return listaTipoEntidad
     */
    public List<Registro> getListaTipoEntidad() {
        return listaTipoEntidad;
    }

    /**
     * Asigna la lista listaTipoEntidad
     * 
     * @param listaTipoEntidad
     * Variable a asignar en listaTipoEntidad
     */
    public void setListaTipoEntidad(List<Registro> listaTipoEntidad) {
        this.listaTipoEntidad = listaTipoEntidad;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoSchip
     * 
     * @return listaCodigoSchip
     */
    public RegistroDataModel getListaCodigoSchip() {
        return listaCodigoSchip;
    }

    /**
     * Asigna la lista listaCodigoSchip
     * 
     * @param listaCodigoSchip
     * Variable a asignar en listaCodigoSchip
     */
    public void setListaCodigoSchip(RegistroDataModel listaCodigoSchip) {
        this.listaCodigoSchip = listaCodigoSchip;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
