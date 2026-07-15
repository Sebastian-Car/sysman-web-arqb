/*-
 * FrmConfAportesSindicatosControlador.java
 *
 * 1.0
 * 
 * 2 abr. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmConfAportesSindicatosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite configuara los aportes y Sindicato
 *
 * @version 1.0, 02/04/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmConfAportesSindicatosControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Variable que almacena el nombre del fondo de sindicato
     * seleccionado
     */
    private String nombreFondo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga las clase fondo
     */
    private RegistroDataModelImpl listaSindicato;
    /**
     * Lista que carga las clase fondo en la grilla
     */
    private RegistroDataModelImpl listaSindicatoE;
    /**
     * Lista que carga los conceptos
     */
    private RegistroDataModelImpl listaCodigoCobro;
    /**
     * Lista que carga los conceptos en la grilla
     */
    private RegistroDataModelImpl listaCodigoCobroE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmConfAportesSindicatosControlador
     */
    public FrmConfAportesSindicatosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 2053
            numFormulario = GeneralCodigoFormaEnum.FRMCONF_APORTES_SINDICATOS_CONTROLADOR
                            .getCodigo();
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
        enumBase = GenericUrlEnum.CLASEFONDOAPORTE;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaSindicato();
        cargarListaSindicatoE();
        cargarListaCodigoCobro();
        cargarListaCodigoCobroE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaSindicato
     *
     */
    public void cargarListaSindicato() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfAportesSindicatosControladorUrlEnum.URL5214
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaSindicato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaSindicato
     *
     */
    public void cargarListaSindicatoE() {
        listaSindicatoE = listaSindicato;
    }

    /**
     * 
     * Carga la lista listaCodigoCobro
     *
     */
    public void cargarListaCodigoCobro() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfAportesSindicatosControladorUrlEnum.URL7302
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CLASE.getName(), 5);

        listaCodigoCobro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ID_DE_CONCEPTO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoCobro
     *
     */
    public void cargarListaCodigoCobroE() {
        listaCodigoCobroE = listaCodigoCobro;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton ConceptosBase
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirConceptosBase(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "clase_id_fondo", "codigo" };

        Object[] valores = { reg.getCampos().get("CLASE_ID_DE_FONDO")
                        .toString(),
                             reg.getCampos().get(
                                             GeneralParameterEnum.CODIGO
                                                             .getName())
                                             .toString() };
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRMCONCEPTOS_BASE_APORTES_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(), campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Sindicato en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarSindicatoC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("NOMBRE_FONDO", nombreFondo);

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSindicato
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSindicato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CLASE_ID_DE_FONDO",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSindicato
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSindicatoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("ID").toString();

        nombreFondo = registroAux.getCampos().get("NOMBRE_FONDO").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoCobro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoCobro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CONCEPTO",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_CONCEPTO
                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoCobro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoCobroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.ID_DE_CONCEPTO.getName())
                        .toString();
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
     */
    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        generarConsecutivo());
        return true;
    }

    private Object generarConsecutivo() {
        long consecutivo = 0;

        try {
            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "CLASEFONDOAPORTE",
                            "COMPANIA = " + compania,
                            GeneralParameterEnum.CODIGO.getName());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return consecutivo;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR2053-DESPUES_INSERTAR Private Sub Form_AfterInsert()
         * Predecesor_Auxiliar End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove("NOMBRE_FONDO");
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("NOMBRE_FONDO");
        registro.getCampos().remove("NOMBRE_FORMA");
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_UTILZIADO
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaSindicato
     * 
     * @return listaSindicato
     */
    public RegistroDataModelImpl getListaSindicato() {
        return listaSindicato;
    }

    /**
     * Asigna la lista listaSindicato
     * 
     * @param listaSindicato
     * Variable a asignar en listaSindicato
     */
    public void setListaSindicato(RegistroDataModelImpl listaSindicato) {
        this.listaSindicato = listaSindicato;
    }

    /**
     * Retorna la lista listaSindicato
     * 
     * @return listaSindicato
     */
    public RegistroDataModelImpl getListaSindicatoE() {
        return listaSindicatoE;
    }

    /**
     * Asigna la lista listaSindicato
     * 
     * @param listaSindicato
     * Variable a asignar en listaSindicato
     */
    public void setListaSindicatoE(RegistroDataModelImpl listaSindicatoE) {
        this.listaSindicatoE = listaSindicatoE;
    }

    /**
     * Retorna la lista listaCodigoCobro
     * 
     * @return listaCodigoCobro
     */
    public RegistroDataModelImpl getListaCodigoCobro() {
        return listaCodigoCobro;
    }

    /**
     * Asigna la lista listaCodigoCobro
     * 
     * @param listaCodigoCobro
     * Variable a asignar en listaCodigoCobro
     */
    public void setListaCodigoCobro(RegistroDataModelImpl listaCodigoCobro) {
        this.listaCodigoCobro = listaCodigoCobro;
    }

    /**
     * Retorna la lista listaCodigoCobro
     * 
     * @return listaCodigoCobro
     */
    public RegistroDataModelImpl getListaCodigoCobroE() {
        return listaCodigoCobroE;
    }

    /**
     * Asigna la lista listaCodigoCobro
     * 
     * @param listaCodigoCobro
     * Variable a asignar en listaCodigoCobro
     */
    public void setListaCodigoCobroE(RegistroDataModelImpl listaCodigoCobroE) {
        this.listaCodigoCobroE = listaCodigoCobroE;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
