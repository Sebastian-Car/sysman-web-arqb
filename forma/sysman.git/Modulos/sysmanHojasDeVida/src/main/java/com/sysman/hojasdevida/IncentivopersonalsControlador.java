/*-
 * IncentivopersonalsControlador.java
 *
 * 1.0
 * 
 * 01/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped; import java.util.Map;

import org.primefaces.event.SelectEvent;

/**
 * Controlador de la forma incentivopersonal asociada al formulario
 * Incentivo Personal. Que permite almacenar detalles de los
 * incentivos que se le otorgan a los empleados de la compa�ia.
 *
 * @version 1.0, 01/03/2017
 * @author jlramirez
 */
@ManagedBean
@ViewScoped
public class IncentivopersonalsControlador extends BeanBaseDatosAcme {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente.
     */
    private final String compania;
    /**
     * Constante que almacena la cadena de texto "SUCURSAL".
     */
    private final String txtSucursal;
    /**
     * Constante que almacena la cadena de texto "NOMBRE".
     */
    private final String txtNombre;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el numero de documento de identidad del
     * empleado seleccionado en la cmbCedula.
     */
    private String docIdentidad;
    /**
     * Atributo que almacena el nombre del empleado seleccionado en la
     * cmbCedula.
     */
    private String nombre;
    /**
     * Atributo que almacena el codigo del empleado seleccionado en la
     * cmbCedula
     */
    private String codigo;
    /**
     * Atributo que almacena un valor true o false, dependiendo si se
     * desea bloquear los campos de los detalles del incentivo.
     */
    private boolean bloqueado;
    /**
     * Atributo que almacena un valor true o false, dependiendo si se
     * desea bloquear la cmbCedula, en la que se elige el empleado al
     * que se le asignara el incentivo.
     */
    private boolean bloqueadoCmb;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene la informaci�n de los detalles del combo
     * cedula, en la que se elige el empleado al que se le asignara el
     * incentivo.
     */
    private RegistroDataModel listacmbcedula;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de IncentivopersonalsControlador
     */
    public IncentivopersonalsControlador() {
        super();
        compania = SessionUtil.getCompania();
        txtSucursal = "SUCURSAL";
        txtNombre = "NOMBRE";
        try {
            numFormulario = 1321;
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
        cargarListaCmbpersona();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios. Se bloquean algunos
     * campos del subFormulario, segun la accion que se esta
     * realizando
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        bloqueadoCmb = true;
        if ("v".equals(accion)) {
            bloqueado = true;
        }
        if ("m".equals(accion)) {
            bloqueado = false;
        }
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
        bloqueado = false;
        bloqueadoCmb = false;
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
        tabla = "NAT_INCENTIVOS";
        buscarLlave();
        asignarOrigenDatos();
        reasignarOrigenGrilla();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = "SELECT   NAT_INCENTIVOS.COMPANIA," +
            "         NAT_INCENTIVOS.DP_NUMEDOCU," +
            "         NAT_INCENTIVOS.SUCURSAL," +
            "         NAT_INCENTIVOS.ID_DE_EMPLEADO," +
            "         NAT_INCENTIVOS.IN_TIPODOCUACTO," +
            "         NAT_INCENTIVOS.IN_NUME," +
            "         NAT_INCENTIVOS.IN_FECHRESODECR," +
            "         NAT_INCENTIVOS.IT_CODIGOPERSONA," +
            "         NAT_INCENTIVOS.IN_TIPOINCENT," +
            "         NAT_INCENTIVOS.IN_CICLO," +
            "         NAT_INCENTIVOS.IN_VALOR," +
            "         NAT_INCENTIVOS.IN_COMPLEMENTO, " +
            "         NAT_DATOS_PERSONALES.APELLIDO1||' '||" +
            "         NAT_DATOS_PERSONALES.APELLIDO2||' '||" +
            "         NAT_DATOS_PERSONALES.NOMBRES NOMBRE" +
            " FROM    NAT_INCENTIVOS  " +
            "   INNER JOIN NAT_DATOS_PERSONALES" +
            "      ON NAT_INCENTIVOS.COMPANIA         = NAT_DATOS_PERSONALES.COMPANIA"
            +
            "     AND NAT_INCENTIVOS.DP_NUMEDOCU      = NAT_DATOS_PERSONALES.NUMERO_DCTO"
            +
            "     AND NAT_INCENTIVOS.SUCURSAL         = NAT_DATOS_PERSONALES.SUCURSAL"
            +
            "     AND NAT_INCENTIVOS.IT_CODIGOPERSONA = NAT_DATOS_PERSONALES.CODIGO";
    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     */
    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = "SELECT NAT_INCENTIVOS.COMPANIA," +
            "       NAT_INCENTIVOS.DP_NUMEDOCU," +
            "       NAT_INCENTIVOS.SUCURSAL," +
            "       NAT_INCENTIVOS.ID_DE_EMPLEADO," +
            "       NAT_INCENTIVOS.IN_TIPODOCUACTO," +
            "       NAT_INCENTIVOS.IN_NUME," +
            "       NAT_INCENTIVOS.IN_FECHRESODECR," +
            "       NAT_INCENTIVOS.IT_CODIGOPERSONA," +
            "       NAT_INCENTIVOS.IN_TIPOINCENT," +
            "       NAT_DATOS_PERSONALES.APELLIDO1||' '||" +
            "       NAT_DATOS_PERSONALES.APELLIDO2||' '||" +
            "       NAT_DATOS_PERSONALES.NOMBRES NOMBRE," +
            "       NAT_INCENTIVOS.IN_COMPLEMENTO" +
            " FROM NAT_INCENTIVOS " +
            "   INNER JOIN NAT_DATOS_PERSONALES" +
            "      ON NAT_INCENTIVOS.COMPANIA         = NAT_DATOS_PERSONALES.COMPANIA"
            +
            "     AND NAT_INCENTIVOS.DP_NUMEDOCU      = NAT_DATOS_PERSONALES.NUMERO_DCTO"
            +
            "     AND NAT_INCENTIVOS.SUCURSAL         = NAT_DATOS_PERSONALES.SUCURSAL"
            +
            "     AND NAT_INCENTIVOS.IT_CODIGOPERSONA = NAT_DATOS_PERSONALES.CODIGO"
            +
            " ORDER BY NAT_INCENTIVOS.DP_NUMEDOCU";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenDatos);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenDatos);
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaCmbpersona
     */
    public void cargarListaCmbpersona() {
        listacmbcedula = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR1316_nuevo:TBCB4327",
                        "SELECT   DISTINCT NAT_DATOS_PERSONALES.NUMERO_DCTO," +
                            "         NAT_DATOS_PERSONALES.APELLIDO1||' '||" +
                            "         NAT_DATOS_PERSONALES.APELLIDO2||' '||" +
                            "         NAT_DATOS_PERSONALES.NOMBRES NOMBRE," +
                            "         NAT_DATOS_PERSONALES.CODIGO," +
                            "         NAT_DATOS_PERSONALES.SUCURSAL" +
                            " FROM     NAT_DATOS_PERSONALES" +
                            "   LEFT JOIN NAT_NOMBRAMIENTO" +
                            "     ON NAT_DATOS_PERSONALES.COMPANIA    = NAT_NOMBRAMIENTO.COMPANIA"
                            +
                            "    AND NAT_DATOS_PERSONALES.NUMERO_DCTO = NAT_NOMBRAMIENTO.DP_NUMEDOCU"
                            +
                            "    AND NAT_DATOS_PERSONALES.SUCURSAL    = NAT_NOMBRAMIENTO.SUCURSAL"
                            /*
                             * + "   LEFT JOIN TIPO_NOMBRAMIENTO" +
                             * "     ON      NAT_NOMBRAMIENTO.NO_TIPO = TIPO_NOMBRAMIENTO.TN_CODIGO"
                             * +
                             * " WHERE    TIPO_NOMBRAMIENTO.ESCARRERA <> 0"
                             */ +
                            " ORDER BY NAT_DATOS_PERSONALES.NUMERO_DCTO",
                        true, "NUMERO_DCTO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbpersona
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbcedula(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("DP_NUMEDOCU",
                        registroAux.getCampos().get("NUMERO_DCTO"));
        registro.getCampos().put(txtNombre,
                        registroAux.getCampos().get(txtNombre));
        registro.getCampos().put("IT_CODIGOPERSONA",
                        registroAux.getCampos().get("CODIGO"));
        registro.getCampos().put(txtSucursal,
                        registroAux.getCampos().get(txtSucursal));
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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
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
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().remove(txtNombre);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
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
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
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
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable docIdentidad
     * 
     * @return docIdentidad
     */
    public String getDocIdentidad() {
        return docIdentidad;
    }

    /**
     * Asigna la variable docIdentidad
     * 
     * @param docIdentidad
     * Variable a asignar en docIdentidad
     */
    public void setDocIdentidad(String docIdentidad) {
        this.docIdentidad = docIdentidad;
    }

    /**
     * Retorna la variable nombre
     * 
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna la variable nombre
     * 
     * @param nombre
     * Variable a asignar en nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna la variable codigo
     * 
     * @return codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Asigna la variable codigo
     * 
     * @param codigo
     * Variable a asignar en codigo
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Retorna la variable bloqueado
     * 
     * @return bloqueado
     */
    public boolean isBloqueado() {
        return bloqueado;
    }

    /**
     * Asigna la variable bloqueado
     * 
     * @param bloqueado
     * Variable a asignar en bloqueado
     */
    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    /**
     * Retorna la variable bloqueadoCmb
     * 
     * @return bloqueadoCmb
     */
    public boolean isBloqueadoCmb() {
        return bloqueadoCmb;
    }

    /**
     * Asigna la variable bloqueadoCmb
     * 
     * @param bloqueadoCmb
     * Variable a asignar en bloqueadoCmb
     */
    public void setBloqueadoCmb(boolean bloqueadoCmb) {
        this.bloqueadoCmb = bloqueadoCmb;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbpersona
     * 
     * @return listacmbpersona
     */
    public RegistroDataModel getListacmbcedula() {
        return listacmbcedula;
    }

    /**
     * Asigna la lista listacmbpersona
     * 
     * @param listacmbpersona
     * Variable a asignar en listaCmbpersona
     */
    public void setListacmbcedula(RegistroDataModel listacmbcedula) {
        this.listacmbcedula = listacmbcedula;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
