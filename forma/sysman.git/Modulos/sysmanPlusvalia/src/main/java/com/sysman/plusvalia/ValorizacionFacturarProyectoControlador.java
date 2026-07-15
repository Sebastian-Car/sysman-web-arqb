/*-
 * ValorizacionFacturarProyectoControlador.java
 *
 * 1.0
 * 
 * 08/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plusvalia;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plusvalia.ejb.EjbPlusvaliaCeroRemote;
import com.sysman.plusvalia.enums.PlusvaliaFacturarProyectoControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 08/03/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class ValorizacionFacturarProyectoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    private boolean ckReclasificar;
    private String beneficiarioInicial;
    private String beneficiarioFinal;
    private String proyecto;
    private String idProyecto;
    private String clase;
    private String numeroProceso;
    private String nombreInicial;
    private String nombreFinal;
    private String idBeneficiarioIni;
    private String idBeneficiarioFin;
    private String procesoRefacturacion;
    private String claseProyecto;
    private String etapa;
    private boolean etapaVisible;
    private String etapaRefacturar;
    private String actoRefacturar;
    private String idEtapa;
    private Registro rsRecaudo;
    private String valorTipoRecaudo;

    private String numeroActo;
    // </DECLARAR_ATRIBUTOS>

    private StreamedContent archivoDescarga;
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaBeneficiarioInicial;

    private RegistroDataModelImpl listaBeneficiarioFinal;
    private RegistroDataModelImpl listaProyecto;
    private RegistroDataModelImpl listaEtapa;

    @EJB
    private EjbPlusvaliaCeroRemote ejbPlusvaliaCeroRemote;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * ValorizacionFacturarProyectoControlador
     */
    public ValorizacionFacturarProyectoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        claseProyecto = "45";

        try {
            numFormulario = GeneralCodigoFormaEnum.VALORIZACION_FACTURAR_PROYECTO_CONTROLADOR
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaProyecto();

        cargarListaEtapa();
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
        etapaVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaEtapa
     *
     */
    public void cargarListaEtapa() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), claseProyecto);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaFacturarProyectoControladorUrlEnum.URL1795
                                                        .getValue());

        listaEtapa = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "CODIGO");
    }

    /**
     * 
     * Carga la lista listaBeneficiarioInicial
     *
     */
    public void cargarListaBeneficiarioInicial() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGO_PROYECTO", proyecto);
        param.put(GeneralParameterEnum.CLASE.getName(), clase);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaFacturarProyectoControladorUrlEnum.URL1768
                                                        .getValue());

        listaBeneficiarioInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "IP_CODIGO");

    }

    /**
     * 
     * Carga la lista listaBeneficiarioFinal
     *
     */
    public void cargarListaBeneficiarioFinal() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGO_PROYECTO", proyecto);
        param.put(GeneralParameterEnum.CLASE.getName(), clase);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaFacturarProyectoControladorUrlEnum.URL1768
                                                        .getValue());

        listaBeneficiarioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "IP_CODIGO");

    }

    /**
     * 
     * Carga la lista listaProyecto
     *
     */
    public void cargarListaProyecto() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CLASEVP", claseProyecto);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaFacturarProyectoControladorUrlEnum.URL1767
                                                        .getValue());

        listaProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton FacturarProyecto en la
     * vista
     *
     *
     */
    public void oprimirFacturarProyecto() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        ejecutarProcesoFacturar();

        if (numeroProceso != null) {
            genInforme(ReportesBean.FORMATOS.PDF);
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control Etapa
     * 
     * 
     */
    public void cambiarEtapa() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Etapa
     * 
     * 
     */
    public void cambiarEtapaRefacturar() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ckReclasificar
     * 
     * 
     */
    public void cambiarckReclasificar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarProcesoFacturar() {
        try {

            numeroProceso = ejbPlusvaliaCeroRemote.procesoFacturacion(
                            compania,
                            Long.parseLong(idProyecto),
                            Long.parseLong(idBeneficiarioIni),
                            Long.parseLong(idBeneficiarioFin),
                            Integer.parseInt(idEtapa),
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            "MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            String nombreReporte = "001998ProcesoFacturarProyecto";

            String codigoEan = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CODIGO EAN VALORIZACION",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "")
                            .toString();

            reemplazar.put("proceso", numeroProceso);
            reemplazar.put("porProceso", -1);
            reemplazar.put("proyecto", idProyecto);
            reemplazar.put("anulada", 0);
            reemplazar.put("facturaInicial", "0");
            reemplazar.put("facturaFinal", "9999999999");
            reemplazar.put("aplicacion", modulo);
            reemplazar.put("claseProyecto", "45");

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();

            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            if (ckReclasificar) {
                etapa = etapaRefacturar;
            }
            parametros.put("PR_ETAPA", etapa);

            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaEtapa
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEtapa(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();

        etapa = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

        idEtapa = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBeneficiarioInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBeneficiarioInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        beneficiarioInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("IP_CODIGO"), "")
                        .toString();

        nombreInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();

        idBeneficiarioIni = SysmanFunciones.nvl(
                        registroAux.getCampos().get("ID"),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBeneficiarioFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBeneficiarioFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        beneficiarioFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("IP_CODIGO"), "")
                        .toString();

        nombreFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();

        idBeneficiarioFin = SysmanFunciones.nvl(
                        registroAux.getCampos().get("ID"),
                        "").toString();
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
    public void seleccionarFilaProyecto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proyecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        idProyecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "")
                        .toString();

        clase = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CLASE"), "")
                        .toString();

        cargarListaBeneficiarioInicial();
        cargarListaBeneficiarioFinal();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ckReclasificar
     * 
     * @return ckReclasificar
     */
    public boolean getCkReclasificar() {
        return ckReclasificar;
    }

    /**
     * Asigna la variable ckReclasificar
     * 
     * @param ckReclasificar
     * Variable a asignar en ckReclasificar
     */
    public void setCkReclasificar(boolean ckReclasificar) {
        this.ckReclasificar = ckReclasificar;
    }

    /**
     * Retorna la variable beneficiarioInicial
     * 
     * @return beneficiarioInicial
     */
    public String getBeneficiarioInicial() {
        return beneficiarioInicial;
    }

    /**
     * Asigna la variable beneficiarioInicial
     * 
     * @param beneficiarioInicial
     * Variable a asignar en beneficiarioInicial
     */
    public void setBeneficiarioInicial(String beneficiarioInicial) {
        this.beneficiarioInicial = beneficiarioInicial;
    }

    /**
     * Retorna la variable beneficiarioFinal
     * 
     * @return beneficiarioFinal
     */
    public String getBeneficiarioFinal() {
        return beneficiarioFinal;
    }

    /**
     * Asigna la variable beneficiarioFinal
     * 
     * @param beneficiarioFinal
     * Variable a asignar en beneficiarioFinal
     */
    public void setBeneficiarioFinal(String beneficiarioFinal) {
        this.beneficiarioFinal = beneficiarioFinal;
    }

    /**
     * Retorna la variable proyecto
     * 
     * @return proyecto
     */
    public String getProyecto() {
        return proyecto;
    }

    /**
     * Asigna la variable proyecto
     * 
     * @param proyecto
     * Variable a asignar en proyecto
     */
    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * Retorna la variable numeroProceso
     * 
     * @return numeroProceso
     */
    public String getNumeroProceso() {
        return numeroProceso;
    }

    /**
     * Asigna la variable numeroProceso
     * 
     * @param numeroProceso
     * Variable a asignar en numeroProceso
     */
    public void setNumeroProceso(String numeroProceso) {
        this.numeroProceso = numeroProceso;
    }

    /**
     * Retorna la variable nombreInicial
     * 
     * @return nombreInicial
     */
    public String getNombreInicial() {
        return nombreInicial;
    }

    /**
     * Asigna la variable nombreInicial
     * 
     * @param nombreInicial
     * Variable a asignar en nombreInicial
     */
    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    /**
     * Retorna la variable nombreFinal
     * 
     * @return nombreFinal
     */
    public String getNombreFinal() {
        return nombreFinal;
    }

    /**
     * Asigna la variable nombreFinal
     * 
     * @param nombreFinal
     * Variable a asignar en nombreFinal
     */
    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaBeneficiarioInicial
     * 
     * @return listaBeneficiarioInicial
     */
    public RegistroDataModelImpl getListaBeneficiarioInicial() {
        return listaBeneficiarioInicial;
    }

    /**
     * @return the rsRecaudo
     */
    public Registro getRsRecaudo() {
        return rsRecaudo;
    }

    /**
     * @param rsRecaudo
     * the rsRecaudo to set
     */
    public void setRsRecaudo(Registro rsRecaudo) {
        this.rsRecaudo = rsRecaudo;
    }

    /**
     * @return the valorTipoRecaudo
     */
    public String getValorTipoRecaudo() {
        return valorTipoRecaudo;
    }

    /**
     * @param valorTipoRecaudo
     * the valorTipoRecaudo to set
     */
    public void setValorTipoRecaudo(String valorTipoRecaudo) {
        this.valorTipoRecaudo = valorTipoRecaudo;
    }

    /**
     * Asigna la lista listaBeneficiarioInicial
     * 
     * @param listaBeneficiarioInicial
     * Variable a asignar en listaBeneficiarioInicial
     */
    public void setListaBeneficiarioInicial(
        RegistroDataModelImpl listaBeneficiarioInicial) {
        this.listaBeneficiarioInicial = listaBeneficiarioInicial;
    }

    /**
     * Retorna la lista listaBeneficiarioFinal
     * 
     * @return listaBeneficiarioFinal
     */
    public RegistroDataModelImpl getListaBeneficiarioFinal() {
        return listaBeneficiarioFinal;
    }

    /**
     * Asigna la lista listaBeneficiarioFinal
     * 
     * @param listaBeneficiarioFinal
     * Variable a asignar en listaBeneficiarioFinal
     */
    public void setListaBeneficiarioFinal(
        RegistroDataModelImpl listaBeneficiarioFinal) {
        this.listaBeneficiarioFinal = listaBeneficiarioFinal;
    }

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
     * @return the idProyecto
     */
    public String getIdProyecto() {
        return idProyecto;
    }

    /**
     * @param idProyecto
     * the idProyecto to set
     */
    public void setIdProyecto(String idProyecto) {
        this.idProyecto = idProyecto;
    }

    /**
     * @return the clase
     */
    public String getClase() {
        return clase;
    }

    /**
     * @param clase
     * the clase to set
     */
    public void setClase(String clase) {
        this.clase = clase;
    }

    /**
     * @return the numeroActo
     */
    public String getNumeroActo() {
        return numeroActo;
    }

    /**
     * @param numeroActo
     * the numeroActo to set
     */
    public void setNumeroActo(String numeroActo) {
        this.numeroActo = numeroActo;
    }

    /**
     * @return the idBeneficiarioIni
     */
    public String getIdBeneficiarioIni() {
        return idBeneficiarioIni;
    }

    /**
     * @param idBeneficiarioIni
     * the idBeneficiarioIni to set
     */
    public void setIdBeneficiarioIni(String idBeneficiarioIni) {
        this.idBeneficiarioIni = idBeneficiarioIni;
    }

    /**
     * @return the idBeneficiarioFin
     */
    public String getIdBeneficiarioFin() {
        return idBeneficiarioFin;
    }

    /**
     * @param idBeneficiarioFin
     * the idBeneficiarioFin to set
     */
    public void setIdBeneficiarioFin(String idBeneficiarioFin) {
        this.idBeneficiarioFin = idBeneficiarioFin;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable procesoRefacturacion
     * 
     * @return procesoRefacturacion
     */
    public String getProcesoRefacturacion() {
        return procesoRefacturacion;
    }

    /**
     * Asigna la variable procesoRefacturacion
     * 
     * @param procesoRefacturacion
     * Variable a asignar en procesoRefacturacion
     */
    public void setProcesoRefacturacion(String procesoRefacturacion) {
        this.procesoRefacturacion = procesoRefacturacion;
    }

    /**
     * Retorna la lista listaEtapa
     * 
     * @return listaEtapa
     */
    public RegistroDataModelImpl getListaEtapa() {
        return listaEtapa;
    }

    /**
     * Asigna la lista listaEtapa
     * 
     * @param listaEtapa
     * Variable a asignar en listaEtapa
     */
    public void setListaEtapa(RegistroDataModelImpl listaEtapa) {
        this.listaEtapa = listaEtapa;
    }

    /**
     * @return the claseProyecto
     */
    public String getClaseProyecto() {
        return claseProyecto;
    }

    /**
     * @param claseProyecto
     * the claseProyecto to set
     */
    public void setClaseProyecto(String claseProyecto) {
        this.claseProyecto = claseProyecto;
    }

    /**
     * @return the etapa
     */
    public String getEtapa() {
        return etapa;
    }

    /**
     * @param etapa
     * the etapa to set
     */
    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    /**
     * @return the etapaVisible
     */
    public boolean isEtapaVisible() {
        return etapaVisible;
    }

    /**
     * @param etapaVisible
     * the etapaVisible to set
     */
    public void setEtapaVisible(boolean etapaVisible) {
        this.etapaVisible = etapaVisible;
    }

    /**
     * @return the etapaRefacturar
     */
    public String getEtapaRefacturar() {
        return etapaRefacturar;
    }

    /**
     * @param etapaRefacturar
     * the etapaRefacturar to set
     */
    public void setEtapaRefacturar(String etapaRefacturar) {
        this.etapaRefacturar = etapaRefacturar;
    }

    /**
     * @return the actoRefacturar
     */
    public String getActoRefacturar() {
        return actoRefacturar;
    }

    /**
     * @param actoRefacturar
     * the actoRefacturar to set
     */
    public void setActoRefacturar(String actoRefacturar) {
        this.actoRefacturar = actoRefacturar;
    }

    /**
     * @return the idEtapa
     */
    public String getIdEtapa() {
        return idEtapa;
    }

    /**
     * @param idEtapa
     * the idEtapa to set
     */
    public void setIdEtapa(String idEtapa) {
        this.idEtapa = idEtapa;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
