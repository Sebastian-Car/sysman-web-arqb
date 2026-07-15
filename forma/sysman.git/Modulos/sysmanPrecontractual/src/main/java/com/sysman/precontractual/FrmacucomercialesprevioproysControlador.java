/*-
 * FrmacucomercialesprevioproysControlador.java
 *
 * 1.0
 * 
 * 17/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.precontractual;

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
import com.sysman.precontractual.enums.FrmacumcomercialesprevioproysEnum;
import com.sysman.precontractual.enums.FrmacumcomercialesprevioproysUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que se encarga de enlazar un estudio previo con uno o
 * varios acuerdos comerciales
 *
 * @version 1.0, 17/07/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class FrmacucomercialesprevioproysControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * lista de acuerdos comerciales
     */
    private RegistroDataModelImpl listacodigo;
    /**
     * listado de acuerdos comerciales ala editar
     */
    private RegistroDataModelImpl listacodigoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * variable que recibe el numero del estudio
     */
    private String numeroEstudio;

    /**
     * variable que almacena el valor del tipo del contrato
     */
    private String tipoContrato;
    /**
     * parametros enviados desde el controlador que se llama este
     * formulario
     */
    private Map<String, Object> parametrosRecibidos;
    /**
     * variable que almacena el codigo del pais
     */
    private String codigoPais;
    /**
     * variable que almacena el nombre del pais
     */
    private String nombrePais;

    /**
     * variable que almacena la descripcion del acuerdo comercial
     */
    private String descripcionAcuerdoComercial;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmacucomercialesprevioproysControlador
     */
    public FrmacucomercialesprevioproysControlador() {
        super();
        compania = SessionUtil.getCompania();

        parametrosRecibidos = SessionUtil.getFlash();

        if (parametrosRecibidos != null) {
            numeroEstudio = SysmanFunciones
                            .nvl(parametrosRecibidos.get("numeroEstudio"), "0")
                            .toString();

            tipoContrato = SysmanFunciones
                            .nvl(parametrosRecibidos.get("tipoContrato"), "0")
                            .toString();
        }

        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ACUCOMERCIALESPREVIOPROY_CONTROLADOR
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

        try {
            enumBase = GenericUrlEnum.ACUERDOS_COMERCIALES_P3;
            reasignarOrigen();
            buscarLlave();
            registro = new Registro();
            // <CARGAR_LISTA>
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            cargarListacodigo();
            cargarListacodigoE();
            // </CARGAR_LISTA_COMBO_GRANDE>
            abrirFormulario();
        }
        catch (Exception ex) {
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
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put("TIPOCONTRATO",
                        tipoContrato);

        parametrosListado.put(FrmacumcomercialesprevioproysEnum.NUMERO_ESTUDIO.getValue(),
                        numeroEstudio);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacodigo
     *
     */
    public void cargarListacodigo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmacumcomercialesprevioproysUrlEnum.URL0001.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listacodigo
     *
     */
    public void cargarListacodigoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmacumcomercialesprevioproysUrlEnum.URL0001.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacodigoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("PAIS", registroAux.getCampos().get(FrmacumcomercialesprevioproysEnum.NOMBRE_PAIS.getValue()));
        registro.getCampos().put(FrmacumcomercialesprevioproysEnum.DESCRIPCION_ACUERDO.getValue(),
                        registroAux.getCampos().get(FrmacumcomercialesprevioproysEnum.DESCRIPCION_ACUERDO.getValue()));
        codigoPais = registroAux.getCampos().get("PAIS").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigo
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombrePais = registroAux.getCampos().get(FrmacumcomercialesprevioproysEnum.NOMBRE_PAIS.getValue()).toString();
        codigoPais = registroAux.getCampos().get("PAIS").toString();
        descripcionAcuerdoComercial = registroAux.getCampos().get(FrmacumcomercialesprevioproysEnum.DESCRIPCION_ACUERDO.getValue())
                        .toString();
    }

    /**
     * Metodo ejecutado al cambiar el control codigo en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcodigoC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put("PAIS", nombrePais);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(FrmacumcomercialesprevioproysEnum.DESCRIPCION_ACUERDO.getValue(),
                        descripcionAcuerdoComercial);

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
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("PAIS");
        registro.getCampos().put("PAIS", codigoPais);

        registro.getCampos().put("TIPOCONTRATO",
                        tipoContrato);

        registro.getCampos().put(FrmacumcomercialesprevioproysEnum.NUMERO_ESTUDIO.getValue(),
                        numeroEstudio);

        registro.getCampos().put("COMPANIA",
                        compania);

        String indicadorEntidadEstatal = convertirValor(
                        registro.getCampos().get(FrmacumcomercialesprevioproysEnum.ENTIDAD_ESTATAL.getValue()).toString());
        registro.getCampos().put(FrmacumcomercialesprevioproysEnum.ENTIDAD_ESTATAL.getValue(), indicadorEntidadEstatal);
        String indicadorpresupuestoMayor = convertirValor(
                        registro.getCampos().get(FrmacumcomercialesprevioproysEnum.PRESUPUESTO_MAYOR_AC.getValue()).toString());
        registro.getCampos().put(FrmacumcomercialesprevioproysEnum.PRESUPUESTO_MAYOR_AC.getValue(), indicadorpresupuestoMayor);
        String indicadorExpedicionAplicable = convertirValor(
                        registro.getCampos().get(FrmacumcomercialesprevioproysEnum.EXPEDICION_APLICABLE.getValue()).toString());
        registro.getCampos().put(FrmacumcomercialesprevioproysEnum.EXPEDICION_APLICABLE.getValue(), indicadorExpedicionAplicable);
        String indicadorContratacionCubierta = convertirValor(
                        registro.getCampos().get(FrmacumcomercialesprevioproysEnum.CONTRATACION_CUBIERTA.getValue()).toString());
        registro.getCampos().put(FrmacumcomercialesprevioproysEnum.CONTRATACION_CUBIERTA.getValue(), indicadorContratacionCubierta);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo que convierte el valor del indicador
     */
    public String convertirValor(String indicador) {

        if (!indicador.equals("") && indicador != null) {
            if (indicador.equals("No Aplica")) {
                return "NA";
            }
            else {
                return indicador.toUpperCase();
            }
        }
        else {
            return null;
        }
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        String indicadorEntidadEstatal = convertirValor(
                        registro.getCampos().get(FrmacumcomercialesprevioproysEnum.ENTIDAD_ESTATAL.getValue()).toString());
        registro.getCampos().put(FrmacumcomercialesprevioproysEnum.ENTIDAD_ESTATAL.getValue(), indicadorEntidadEstatal);
        String indicadorpresupuestoMayor = convertirValor(
                        registro.getCampos().get(FrmacumcomercialesprevioproysEnum.PRESUPUESTO_MAYOR_AC.getValue()).toString());
        registro.getCampos().put(FrmacumcomercialesprevioproysEnum.PRESUPUESTO_MAYOR_AC.getValue(), indicadorpresupuestoMayor);
        String indicadorExpedicionAplicable = convertirValor(
                        registro.getCampos().get(FrmacumcomercialesprevioproysEnum.EXPEDICION_APLICABLE.getValue()).toString());
        registro.getCampos().put(FrmacumcomercialesprevioproysEnum.EXPEDICION_APLICABLE.getValue(), indicadorExpedicionAplicable);
        String indicadorContratacionCubierta = convertirValor(
                        registro.getCampos().get(FrmacumcomercialesprevioproysEnum.CONTRATACION_CUBIERTA.getValue()).toString());
        registro.getCampos().put(FrmacumcomercialesprevioproysEnum.CONTRATACION_CUBIERTA.getValue(), indicadorContratacionCubierta);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    public String obtenerCodigoPais(String nombrePais) {
        String salida = "";

        Map<String, Object> parametros = new TreeMap<>();

        parametros.put(FrmacumcomercialesprevioproysEnum.NOMBRE_PAIS.getValue(), nombrePais);

        Registro rsPais;
        try {
            rsPais = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID("1008")
                                            .getUrl(),
                                            parametros));
            salida = SysmanFunciones.toString(
                            rsPais.getCampos()
                                            .get("PAIS"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return salida;

    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
        codigoPais = obtenerCodigoPais(registro.getCampos().get("PAIS").toString());
        registro.getCampos().put("PAIS", codigoPais);

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.TIPOCONTRATO.getName());
        registro.getCampos().remove(FrmacumcomercialesprevioproysEnum.NUMERO_ESTUDIO.getValue());
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // Auto-generated method stub
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacodigo
     * 
     * @return listacodigo
     */
    public RegistroDataModelImpl getListacodigo() {
        return listacodigo;
    }

    /**
     * Asigna la lista listacodigo
     * 
     * @param listacodigo
     * Variable a asignar en listacodigo
     */
    public void setListacodigo(RegistroDataModelImpl listacodigo) {
        this.listacodigo = listacodigo;
    }

    /**
     * Retorna la lista listacodigo
     * 
     * @return listacodigo
     */
    public RegistroDataModelImpl getListacodigoE() {
        return listacodigoE;
    }

    /**
     * Asigna la lista listacodigo
     * 
     * @param listacodigo
     * Variable a asignar en listacodigo
     */
    public void setListacodigoE(RegistroDataModelImpl listacodigoE) {
        this.listacodigoE = listacodigoE;
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
