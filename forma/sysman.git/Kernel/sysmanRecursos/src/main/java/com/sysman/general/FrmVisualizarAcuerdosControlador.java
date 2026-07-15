/*-
 * FrmVisualizarAcuerdosControlador.java
 *
 * 1.0
 * 
 * 20/05/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmVisualizarAcuerdosControladorEnum;
import com.sysman.general.enums.FrmVisualizarAcuerdosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plusvalia.ejb.EjbPlusvaliaCeroGeneralRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 20/05/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmVisualizarAcuerdosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;

    private String factura;
    private String idfactura;
    private String reemplazo;
    private String acuerdo;
    private String cuota;
    private String idRecibo;
    private String referencia;

    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaFacturas;
    private RegistroDataModelImpl listaFacturasE;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    @EJB
    private EjbPlusvaliaCeroGeneralRemote ejbPlusvaliaCeroRemote;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmVisualizarAcuerdosControlador
     */
    public FrmVisualizarAcuerdosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {

            // 2076
            numFormulario = GeneralCodigoFormaEnum.FRM_VISUALIZAR_ACUERDOS_CONTROLADOR
                            .getCodigo();
            referencia = "45";
            validarPermisos();
            reemplazo = "0";
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
        tabla = "GN_ACUERDOCUOTA";

        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaFacturas();
        cargarListaFacturasE();
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
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(
                        FrmVisualizarAcuerdosControladorEnum.APLICACION
                                        .getValue(),
                        modulo);
        parametrosListado.put(
                        FrmVisualizarAcuerdosControladorEnum.VALIDAR.getValue(),
                        reemplazo);
        parametrosListado.put(
                        FrmVisualizarAcuerdosControladorEnum.FACTURA.getValue(),
                        idfactura);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmVisualizarAcuerdosControladorUrlEnum.URL282
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaFacturas
     *
     */
    public void cargarListaFacturas() {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(),
                        FrmVisualizarAcuerdosControladorEnum.CLASE_PROYECTO
                                        .getValue());

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmVisualizarAcuerdosControladorUrlEnum.URL1796
                                                        .getValue());

        listaFacturas = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmVisualizarAcuerdosControladorEnum.ID
                                        .getValue());

    }

    /**
     * 
     * Carga la lista listaFacturas
     *
     */
    public void cargarListaFacturasE() {

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Imprimir
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirImprimir(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerValores(reg);
        facturarAcuerdo();
        // </CODIGO_DESARROLLADO>

    }

    private void facturarAcuerdo() {

        try {

            idRecibo = ejbPlusvaliaCeroRemote
                            .prorrateoGeneral(Long.parseLong(acuerdo),
                                            Integer.parseInt(cuota),
                                            SessionUtil.getUser().getCodigo())
                            .toString();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            "MSM_PROCESO_EJECUTADO"));
            if (idRecibo != null) {
                generarInforme(ReportesBean.FORMATOS.PDF, idRecibo);
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirDetalle(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        obtenerValores(reg);

        String[] campos = { "acuerdo", "cuota", };
        Object[] valores = { acuerdo, cuota, };

        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.FRM_ACUERDOS_DETALLE_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato, String recibo) {
        try {
            // <CODIGO_DESARROLLADO>

            String nombreReporte = "002011FORMATOSTDACUERDOTCPA";

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("idRecibo", recibo);
            reemplazar.put("aplicacion", modulo);
            reemplazar.put("referencia", referencia);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", JsfUtil.obtenerParametroMarcaBlanca("TITULOLOGIN"));
            // FIN IMPLEMENTACION MARCA_BLANCA

            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);

        }
        catch (JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void obtenerValores(Registro reg) {
        acuerdo = reg.getCampos().get("ACUERDO").toString();
        cuota = reg.getCampos().get("CUOTA").toString();
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFacturas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFacturas(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        factura = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        FrmVisualizarAcuerdosControladorEnum.ID
                                                        .getValue()),
                                        "")
                        .toString();

        idfactura = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        FrmVisualizarAcuerdosControladorEnum.ID
                                                        .getValue()),
                                        "0")
                        .toString();

        reemplazo = idfactura.equals("0") ? "0" : "-1";

        reasignarOrigen();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFacturas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFacturasE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("ID");
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
        // </CODIGO_DESARROLLADO>
        return true;
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
     * 
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
    }

    @Override
    public void asignarValoresRegistro() {

    }

    /**
     * @return the modulo
     */
    public String getModulo() {
        return modulo;
    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * @return the listaFacturas
     */
    public RegistroDataModelImpl getListaFacturas() {
        return listaFacturas;
    }

    /**
     * @param listaFacturas
     * the listaFacturas to set
     */
    public void setListaFacturas(RegistroDataModelImpl listaFacturas) {
        this.listaFacturas = listaFacturas;
    }

    /**
     * @return the listaFacturasE
     */
    public RegistroDataModelImpl getListaFacturasE() {
        return listaFacturasE;
    }

    /**
     * @param listaFacturasE
     * the listaFacturasE to set
     */
    public void setListaFacturasE(RegistroDataModelImpl listaFacturasE) {
        this.listaFacturasE = listaFacturasE;
    }

    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar
     * the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the factura
     */
    public String getFactura() {
        return factura;
    }

    /**
     * @param factura
     * the factura to set
     */
    public void setFactura(String factura) {
        this.factura = factura;
    }

    // <SET_GET_ATRIBUTOS>

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
