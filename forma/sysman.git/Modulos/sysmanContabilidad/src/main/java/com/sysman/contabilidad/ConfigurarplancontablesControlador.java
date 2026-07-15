/*-
 * ConfigurarplancontablesControlador.java
 *
 * 1.0
 * 
 * 25/11/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.ConfigurarplancontablesControladorEnum;
import com.sysman.contabilidad.enums.ConfigurarplancontablesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Controlador que gestiona los eventos de la vista
 * configurarplancontable.
 *
 * @version 1.0, 24/11/2016
 * @author pespitia
 * @author yrojas
 * @version 2, 10/04/2017 Se cambiaron las consultas por la invocacion
 * de los DSS. Se cambio controlador segun especificaciones del
 * SonarLint.
 * @author yrojas
 * @version 3, 20/04/2017 Se cambiaron los llamados de Acciones por
 * las invocaciones de los ejb.
 * @author spina
 * @version 4, 18/05/2017 se agregaron campos al combo de cuentas
 * segun access y se agrega validacion para las cuentas que no manejan
 * movimiento
 * 
 */
@ManagedBean
@ViewScoped
public class ConfigurarplancontablesControlador
                extends BeanBaseContinuoAcmeImpl {

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor del a�o del registro que se esta
     * modificado en la vista.
     */
    private String anio;

    /** Atributo que contiene el nombre equivalente NIIF. */
    private String nombre;

    /**
     * Atributo que contiene el codigo de la compania equivalente
     * NIIF.
     */
    private String companianiif;

    private Registro registroAux;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista que contiene los elementos del combo C�digo Equivalente
     * NIIF en la vista.
     */
    private RegistroDataModelImpl listaCodEquivalenteE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se almacena el identificador del registro que se
     * selecciono.
     */
    private String auxiliar;

    private boolean bloqueadoCodEquiv;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConfigurarplancontablesControlador
     */
    public ConfigurarplancontablesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONFIGURARPLANCONTABLES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            /* Recupera el codigo de la compania asociada al niif */

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
        try {
            tabla = ConfigurarplancontablesControladorEnum.TABLA.getValue();
            buscarLlave();
            companianiif = ejbSysmanUtil.consultarParametro(compania,
                            idioma.getString("TB_TB2248"),
                            SessionUtil.getModulo(), new Date(), true);
            registro = new Registro();
            reasignarOrigen();
            // <CARGAR_LISTA>
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            // </CARGAR_LISTA_COMBO_GRANDE>
            abrirFormulario();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplancontablesControladorUrlEnum.URL161
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplancontablesControladorUrlEnum.URL162
                                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado
                        .put(ConfigurarplancontablesControladorEnum.COMPANIANIIF
                                        .getValue(), companianiif);

    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    // <METODOS_CARGAR_LISTA>

    /** Carga la lista de elementos del combo Codigo equivalente. */
    public void cargarListaCodEquivalenteE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplancontablesControladorUrlEnum.URL7546
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.COMPANIA.getName(), companianiif);

        listaCodEquivalenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CodEquivalente en la
     * fila seleccionada dentro de la grilla.
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodEquivalenteC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        /* Asigne el valor al campo NOMBRECODEQUIV de la grilla */
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("NOMBRECODEQUIV", nombre);
        mostrarAlertaNoMovimiento();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    private boolean mostrarAlertaNoMovimiento() {
        boolean actualizar = validarIndicadores(registroAux);
        if (actualizar) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3179")
                            .replace("s$auxiliar$s", auxiliar));
        }
        return actualizar;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodEquivalente. Gestiona los eventos del mismo.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaCodEquivalenteE(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombre = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        mostrarAlertaNoMovimiento();

    }

    private boolean validarIndicadores(Registro registroAux) {
        return validarIndPrincipales(registroAux)
            && !(Boolean) SysmanFunciones.nvl(
                            registroAux.getCampos().get("MAN_AUX_FUE"), true)
            && !(Boolean) SysmanFunciones.nvl(
                            registroAux.getCampos().get("MAN_AUX_REF"), true);
    }

    private boolean validarIndPrincipales(Registro registroAux) {
        return !(Boolean) SysmanFunciones
                        .nvl(registroAux.getCampos().get("MOVIMIENTO"), true)
            && !(Boolean) SysmanFunciones.nvl(
                            registroAux.getCampos().get("MAN_AUX_TER"), true)
            && !(Boolean) SysmanFunciones.nvl(
                            registroAux.getCampos().get("MAN_CEN_CTO"), true)
            && !(Boolean) SysmanFunciones.nvl(
                            registroAux.getCampos().get("MAN_AUX_GEN"), true);
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        bloqueadoCodEquiv = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return VARIABLE
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return VARIABLE
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro.
     * 
     * @return VARIABLE
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        /*
         * Remover los campos que no son propios de la tabla
         * PLAN_CONTABLE
         */
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove("NOMBRECODEQUIV");
        registro.getCampos().remove("CLASECUENTA");

        // </CODIGO_DESARROLLADO>
        return !mostrarAlertaNoMovimiento();
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return VARIABLE
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
     * @return VARIABLE
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
     * @return VARIABLE
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario.
     * 
     * @param registro
     * registro del cual se activo la edicion en la grilla.
     */
    public void activarEdicion(Registro registro) // parametro
                                                  // necesario en la
                                                  // vista
    {
        indice = listaInicial.getRowIndex();
        // Recuperar el anio del registro seleccionado.
        anio = listaInicial.getDatasource().get(indice).getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString();
        // Cargar la lista del combo Codigo Equivalente.
        cargarListaCodEquivalenteE();
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaCodEquivalente
     * 
     * @return listaCodEquivalente
     */
    public RegistroDataModelImpl getListaCodEquivalenteE() {
        return listaCodEquivalenteE;
    }

    /**
     * Asigna la lista listaCodEquivalente
     * 
     * @param listaCodEquivalente
     * Variable a asignar en listaCodEquivalente
     */
    public void setListaCodEquivalenteE(
        RegistroDataModelImpl listaCodEquivalenteE) {
        this.listaCodEquivalenteE = listaCodEquivalenteE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public boolean isBloqueadoCodEquiv() {
        return bloqueadoCodEquiv;
    }

    public void setBloqueadoCodEquiv(boolean bloqueadoCodEquiv) {
        this.bloqueadoCodEquiv = bloqueadoCodEquiv;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}