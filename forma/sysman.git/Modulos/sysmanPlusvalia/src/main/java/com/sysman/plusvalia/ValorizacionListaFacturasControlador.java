/*-
 * ValorizacionListaFacturasControlador.java
 *
 * 1.0
 * 
 * 21/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plusvalia;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plusvalia.enums.ValorizacionListaFacturasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
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
 * @version 1.0, 21/03/2019
 * @author bcardenas
 */

/*
 * En la parte del selectItem de combo estadoFactura siempre dejarlo
 * de esta manera
 * 
 * 
 * <f:selectItem itemValue="0" itemLabel="#{idm.TB_TB4303}"/>
 * <f:selectItem itemValue="1" itemLabel="#{idm.TB_TB4304}"/>
 * <f:selectItem itemValue="2" itemLabel="#{idm.TG_TODAS}"/>
 * 
 * 
 */

@ManagedBean
@ViewScoped
public class ValorizacionListaFacturasControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    // <DECLARAR_ATRIBUTOS>
    /**
     */
    private String proyectos;
    private String clase;
    private String numero;
    private String factura;
    private BigInteger idProyecto;
    private BigInteger idBeneficiario;
    private String reemplazo;
    private String proyecto;
    private Registro rsCodigo;
    private String codigoEan;
    private String estadoFactura;
    private String anulado;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     */
    private RegistroDataModelImpl listaProyecto;
    /**
     */
    private RegistroDataModelImpl listaProyectoE;
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
     * Crea una nueva instancia de
     * ValorizacionListaFacturasControlador
     */
    public ValorizacionListaFacturasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        clase = "45";
        reemplazo = "0";
        estadoFactura = "0";
        try {
            // 2048
            numFormulario = 2048;
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

        tabla = "VP_PROCESO_FACTURACION";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaProyecto();
        cargarListaProyectoE();
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
        parametrosListado.put(GeneralParameterEnum.CLASE.getName(), clase);

        parametrosListado.put("REEMPLAZO", reemplazo);

        parametrosListado.put("PORESTADO",
                        estadoFactura);

        parametrosListado.put(GeneralParameterEnum.PROYECTO.getName(),
                        proyecto);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ValorizacionListaFacturasControladorUrlEnum.URL1781
                                                        .getValue());

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
    /**
     * 
     * Carga la lista listaProyecto
     *
     */
    public void cargarListaProyecto() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), clase);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ValorizacionListaFacturasControladorUrlEnum.URL1767
                                                        .getValue());

        listaProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID");

    }

    /**
     * 
     * Carga la lista listaProyecto
     *
     */
    public void cargarListaProyectoE() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), clase);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ValorizacionListaFacturasControladorUrlEnum.URL1767
                                                        .getValue());

        listaProyectoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

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
        obtenerCampos(reg);
        validarCodigoEAN(reg);

        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato, Registro reg) {

        try {
            // <CODIGO_DESARROLLADO>

            String nombreReporte = "001998ProcesoFacturarProyecto";

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("proceso", numero);
            reemplazar.put("porProceso", -1);
            reemplazar.put("proyecto", idProyecto);
            reemplazar.put("anulada", anulado);
            reemplazar.put("facturaInicial", "0");
            reemplazar.put("facturaFinal", "9999999999");
            reemplazar.put("aplicacion", modulo);
            reemplazar.put("claseProyecto", "45");

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

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

    public void validarCodigoEAN(Registro reg) {
        try {
            Map<String, Object> paramCodigo = new HashMap<>();
            paramCodigo.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            paramCodigo.put(GeneralParameterEnum.PROYECTO.getName(),
                            idProyecto);
            rsCodigo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ValorizacionListaFacturasControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(),
                                            paramCodigo));
            if (rsCodigo != null) {
                codigoEan = SysmanFunciones
                                .nvl(rsCodigo
                                                .getCampos()
                                                .get("CODIGO_EAN"),
                                                "")
                                .toString();
                if (codigoEan == null || codigoEan.isEmpty()) {

                    JsfUtil.agregarMensajeAlerta(
                                    "El proyecto no tiene configurado el codigo EAN");
                    // Mensaje de Alerta
                }
                else {

                    generarInforme(ReportesBean.FORMATOS.PDF, reg);
                }
            }
        }
        catch (SystemException | NullPointerException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void obtenerCampos(Registro reg) {
        numero = reg.getCampos().get("ID").toString();
        idProyecto = (BigInteger) reg.getCampos().get("ID_PROYECTO");
        idBeneficiario = (BigInteger) reg.getCampos()
                        .get("ID_BENEFICIARIOS");
        factura = reg.getCampos()
                        .get("NUMERO_FACTURA").toString();

        anulado = reg.getCampos()
                        .get("ESTADO").toString();
        anulado = anulado.equals("false") ? "-1" : "0";
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Estado
     * 
     * 
     */
    public void cambiarEstado() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProyecto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProyecto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proyecto = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), " ")
                        .toString();

        proyectos = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), " ")
                        .toString();

        if (proyecto.equals(" ")) {
            reemplazo = "0";
        }
        else {

            reemplazo = "1";
        }

        reasignarOrigen();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProyecto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProyectoE(SelectEvent event) {
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
     * @return TODO VARIABLE
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

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
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

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaProyecto
     * 
     * @return listaProyecto
     */
    public RegistroDataModelImpl getListaProyecto() {
        return listaProyecto;
    }

    /**
     * Asigna la lista listaProyecto
     * 
     * @param listaProyecto
     * Variable a asignar en listaProyecto
     */
    public void setListaProyecto(RegistroDataModelImpl listaProyecto) {
        this.listaProyecto = listaProyecto;
    }

    /**
     * Retorna la lista listaProyecto
     * 
     * @return listaProyecto
     */
    public RegistroDataModelImpl getListaProyectoE() {
        return listaProyectoE;
    }

    /**
     * Asigna la lista listaProyecto
     * 
     * @param listaProyecto
     * Variable a asignar en listaProyecto
     */
    public void setListaProyectoE(RegistroDataModelImpl listaProyectoE) {
        this.listaProyectoE = listaProyectoE;
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
     * @param indice
     * the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * @return the proyectos
     */
    public String getProyectos() {
        return proyectos;
    }

    /**
     * @param proyectos
     * the proyectos to set
     */
    public void setProyectos(String proyectos) {
        this.proyectos = proyectos;
    }

    /**
     * @return the proyecto
     */
    public String getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto
     * the proyecto to set
     */
    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the estadoFactura
     */
    public String getEstadoFactura() {
        return estadoFactura;
    }

    /**
     * @param estadoFactura
     * the estadoFactura to set
     */
    public void setEstadoFactura(String estadoFactura) {
        this.estadoFactura = estadoFactura;
    }

    /**
     * @return the anulado
     */
    public String getAnulado() {
        return anulado;
    }

    /**
     * @param anulado
     * the anulado to set
     */
    public void setAnulado(String anulado) {
        this.anulado = anulado;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
