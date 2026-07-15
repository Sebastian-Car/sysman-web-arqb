package com.sysman.plandesarrollo;

/*-
 * FrmpiindicadorsControlador.java
 *
 * 1.0
 * 
 * 26/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.enums.FrmpiindicadorsControladorEnum;
import com.sysman.plandesarrollo.enums.FrmpiindicadorsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 26/02/2018
 * @author jhernandez
 */
@ManagedBean
@ViewScoped
public class FrmpiindicadorsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el año de vigencia
     * inicial del plan indicativo
     */
    private String vigencia;
    /**
     * Variable de clase para almacenar el valor de meta producción
     * 
     */
    private String metaProd;
    /**
     * Variable de clase para almacenar el valor de meta resultado
     */
    private String metaRes;
    /**
     * Variable de clase para almacenar el valor de descripción
     */
    private String descripcion;
    /**
     * Variable de clase para almacenar el valor del codigo
     */
    private Long codigo;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * variable para el manejo de la lista unidad de medida
     */
    private List<Registro> listaUnidadMedida;
    /**
     * Variable para el manejo de la listaSector
     */
    private List<Registro> listaSector;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable para manejo de combos grande idplan
     */
    private RegistroDataModelImpl listaIdPlan;
    /**
     * Variable para manejo de combos grande idplan
     */
    private RegistroDataModelImpl listaIdPlanE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se almacena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Lista de objetivos de desarrollo sostenible.
     */
    private RegistroDataModelImpl listaODS;
    /**
     * Lista de objetivos de desarrollo sostenible. Vista de edición.
     */
    private RegistroDataModelImpl listaODSE;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmpiindicadorsControlador
     */
    public FrmpiindicadorsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMPIINDICADORS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            traerParametrosFlash();
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    private void traerParametrosFlash() {
        Map<String, Object> valores = SessionUtil.getFlash();
        if (!valores.isEmpty()) {
            vigencia = SysmanFunciones.toString(valores.get("vigencia"));
            metaProd = SysmanFunciones.toString(valores.get("digMetaProd"));
            metaRes = SysmanFunciones.toString(valores.get("digMetaRes"));
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
        enumBase = GenericUrlEnum.PI_INDICADOR;
        buscarLlave();
        reasignarOrigen();

        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaUnidadMedida();
        cargarListaSector();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaIdPlan();
        cargarListaIdPlanE();
        cargarListaODS();
        cargarListaODSE();
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
        parametrosListado.put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);
        parametrosListado.put(
                        FrmpiindicadorsControladorEnum.GETMPRO.getValue(),
                        metaProd);
        parametrosListado.put(
                        FrmpiindicadorsControladorEnum.GETMRES.getValue(),
                        metaRes);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaUNIDAD_MEDIDA
     *
     */
    public void cargarListaUnidadMedida() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaUnidadMedida = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmpiindicadorsControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaSector
     *
     */
    public void cargarListaSector() {
        try {
            listaSector = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmpiindicadorsControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista lista IdPlan
     *
     */
    public void cargarListaIdPlan() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpiindicadorsControladorUrlEnum.URL0002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);
        param.put(FrmpiindicadorsControladorEnum.GETMPRO.getValue(),
                        metaProd);
        param.put(FrmpiindicadorsControladorEnum.GETMRES.getValue(),
                        metaRes);

        listaIdPlan = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "ID");
    }

    /**
     * 
     * Carga la lista lista IdPlanE
     *
     */
    public void cargarListaIdPlanE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpiindicadorsControladorUrlEnum.URL0002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);
        param.put(FrmpiindicadorsControladorEnum.GETMPRO.getValue(),
                        metaProd);
        param.put(FrmpiindicadorsControladorEnum.GETMRES.getValue(),
                        metaRes);

        listaIdPlanE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "ID");
    }

    /** Carga la lista listaODS para dialogo de nuevo registro. */
    public void cargarListaODS() {
        Map<String, Object> params = new HashMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);

        String urlEnumId = FrmpiindicadorsControladorUrlEnum.URL0004.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        listaODS = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), params, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /** Carga la lista listaODS para editar un registro. */
    public void cargarListaODSE() {
        Map<String, Object> params = new HashMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);

        String urlEnumId = FrmpiindicadorsControladorUrlEnum.URL0004.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        listaODSE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), params, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control txtParticipacion
     * 
     * 
     */
    public void cambiartxtParticipacion() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IdPlan en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarIdPlanC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.DESCRIPCION
                                        .getName(), descripcion);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmpiindicadorsControladorEnum.CODIGO.getValue(),
                                        codigo);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaID_PLAN
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIdPlan(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmpiindicadorsControladorEnum.ID_PLAN.getValue(),
                        registroAux.getCampos().get("ID"));
        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION
                        .getName(),
                        registroAux.getCampos().get("DESCRIPCION"));
        registro.getCampos().put(
                        FrmpiindicadorsControladorEnum.CODIGO.getValue(),
                        generarCodigo(vigencia, registroAux.getCampos()
                                        .get("ID").toString()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista IdPlanE
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIdPlanE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
        descripcion = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()),
                                        "")
                        .toString();
        codigo = generarCodigo(vigencia, auxiliar);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaODS.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaODS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(FrmpiindicadorsControladorEnum.ODS.getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaODS.
     * Vista de edicion de registro.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaODSE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_COMBOS_GRANDES>
    public Long generarCodigo(String vigencia, String plan) {
        Long codigoCon = null;
        try {
            codigoCon = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            GenericUrlEnum.PI_INDICADOR.getTable(),
                            SysmanFunciones.concatenar("COMPANIA = ''",
                                            compania,
                                            "'' AND VIGENCIA_INICIAL = ",
                                            vigencia,
                                            "AND ID_PLAN = ''", plan, "'' "),
                            GeneralParameterEnum.CODIGO.getName(), "1");
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return codigoCon;

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
         * FR1039-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 67, Me.Name DoCmd.Maximize End Sub
         */
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
     * 
     * @return VARIABLE
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1039-ANTES_INSERTAR Private Sub Form_BeforeInsert(Cancel
         * As Integer) GenCodigo End Sub
         */
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(
                        GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                        vigencia);
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION
                        .getName());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
     * del registro
     * 
     * 
     * @return VARIABLE
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
     * 
     * @return VARIABLE
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1039-DESPUES_ACTUALIZAR Private Sub Form_AfterUpdate()
         * '(vmolano - 02/06/2016): Para controlar que no queden datos
         * corruptos cuando se cambia un indicador de subprograma a
         * programa. 'EJEMPLO: Si un subprograma tiene 0,5 de
         * participaciÃ³n y se cambia a programa entonces no puede
         * quedar 0,5 sino 1. If Nz(Me!txtClasificacion, "") =
         * "Programa" And Nz(Me!txtParticipacion, 1) < 1 Then
         * Me!txtParticipacion = 1 End If End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(
                        GeneralParameterEnum.VIGENCIA_INICIAL.getName());
        registro.getCampos().remove("CLASIFICACION");
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaUnidadMedida
     * 
     * @return listaUnidadMedida
     */
    public List<Registro> getListaUnidadMedida() {
        return listaUnidadMedida;
    }

    /**
     * Asigna la lista listaUnidadMedida
     * 
     * @param listaUnidadMedida
     * Variable a asignar en listaUnidadMedida
     */
    public void setListaUnidadMedida(List<Registro> listaUnidadMedida) {
        this.listaUnidadMedida = listaUnidadMedida;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaIdPlan
     * 
     * @return listaIdPlan
     */
    public RegistroDataModelImpl getListaIdPlan() {
        return listaIdPlan;
    }

    /**
     * Asigna la lista listaIdPlan
     * 
     * @param listaIdPlan
     * Variable a asignar en listaIdPlan
     */
    public void setListaIdPlan(RegistroDataModelImpl listaIdPlan) {
        this.listaIdPlan = listaIdPlan;
    }

    /**
     * Retorna la lista listaIdPlan
     * 
     * @return listaIdPlan
     */
    public RegistroDataModelImpl getListaIdPlanE() {
        return listaIdPlanE;
    }

    /**
     * Asigna la lista listaIdPlan
     * 
     * @param listaIdPlan
     * Variable a asignar en listaIdPlan
     */
    public void setListaIdPlanE(RegistroDataModelImpl listaIdPlanE) {
        this.listaIdPlanE = listaIdPlanE;
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

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion
     * the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the codigo
     */
    public Long getCodigo() {
        return codigo;
    }

    /**
     * @param codigo
     * the codigo to set
     */
    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    /**
     * Retorna la lista listaSector
     * 
     * @return listaSector
     */
    public List<Registro> getListaSector() {
        return listaSector;
    }

    /**
     * Asigna la lista listaSector
     * 
     * @param listaSector
     * Variable a asignar en listaSector
     */
    public void setListaSector(List<Registro> listaSector) {
        this.listaSector = listaSector;
    }

    /**
     * @return the listaODS
     */
    public RegistroDataModelImpl getListaODS() {
        return listaODS;
    }

    /**
     * @param listaODS
     * the listaODS to set
     */
    public void setListaODS(RegistroDataModelImpl listaODS) {
        this.listaODS = listaODS;
    }

    /**
     * @return the listaODSE
     */
    public RegistroDataModelImpl getListaODSE() {
        return listaODSE;
    }

    /**
     * @param listaODSE
     * the listaODSE to set
     */
    public void setListaODSE(RegistroDataModelImpl listaODSE) {
        this.listaODSE = listaODSE;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
