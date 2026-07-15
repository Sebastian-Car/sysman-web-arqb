/*-
 * PlusvaliaFacturarProyectoControlador.java
 *
 * 1.0
 * 
 * 21/02/2019
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
import java.math.BigInteger;
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
 * @version 1.0, 21/02/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class PlusvaliaFacturarProyectoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private boolean ckReclasificar;
    private String proyecto;
    private String idProyecto;
    private String clase;
    private String numeroProceso;
    private String nombre;
    private String idBeneficiario;
    private String claseProyecto;
    private String beneficiario;
    private String procesoCalculo;
    private String etapa;
    private String idEtapa;
    private String etapaRefacturar;

    private String numeroActo;
    // </DECLARAR_ATRIBUTOS>

    private StreamedContent archivoDescarga;
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaBeneficiario;
    private RegistroDataModelImpl listaProyecto;
    private RegistroDataModelImpl listaNumeroProceso;
    private RegistroDataModelImpl listaEtapa;
    @EJB
    private EjbPlusvaliaCeroRemote ejbPlusvaliaCeroRemote;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * PlusvaliaFacturarProyectoControlador
     */
    public PlusvaliaFacturarProyectoControlador() {
        super();
        compania = SessionUtil.getCompania();
        // claseProyecto =
        // SessionUtil.getSessionVar("claseVp").toString();
        claseProyecto = "44";

        try {
            numFormulario = GeneralCodigoFormaEnum.PLUSVALIA_FACTURAR_PROYECTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
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
        cargarListaNumeroProceso();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
        cargarListaEtapa();
    }
    
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
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaNumeroProceso
     *
     */
    public void cargarListaNumeroProceso() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), claseProyecto);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaFacturarProyectoControladorUrlEnum.URL1774
                                                        .getValue());

        listaNumeroProceso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO.getName());

    }

    /**
     * 
     * Carga la lista listaBeneficiarioInicial
     *
     */
    public void cargarListaBeneficiario() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGO_PROYECTO", proyecto);
        param.put(GeneralParameterEnum.CLASE.getName(), claseProyecto);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaFacturarProyectoControladorUrlEnum.URL1768
                                                        .getValue());

        listaBeneficiario = new RegistroDataModelImpl(urlBean.getUrl(),
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
    public void oprimirGenerarResolucion() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;

        ejecutarProcesoFacturar();
        genInforme(ReportesBean.FORMATOS.PDF, null);

        // </CODIGO_DESARROLLADO>
    }
    
    
    public void oprimirLiquidar() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;

        ejecutarProcesoFacturar();
        genInforme(ReportesBean.FORMATOS.PDF, "002004LiquidacionPlusvalia");

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    public void ejecutarProcesoFacturar() {
        try {
            int reclasificar;
            if (ckReclasificar) {
                reclasificar = -1;
                idBeneficiario = "0";
                numeroActo = "0";
            }
            else {
                reclasificar = 0;
                numeroProceso = "0";
                numeroActo = "0";
            }
//TODO añadir parametro etapa - cesar
            procesoCalculo = ejbPlusvaliaCeroRemote.calculoPlusvalia(
                            compania,
                            Long.parseLong(idProyecto),
                            Long.parseLong(idBeneficiario),
                            reclasificar,
                            Long.parseLong(numeroProceso),
                            SessionUtil.getUser().getCodigo()).toString();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            "MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    


    public void genInforme(ReportesBean.FORMATOS formato, String nombreRpt) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            String nombreReporte = nombreRpt == null ?"002000LiquidacionPlusvalia": nombreRpt;

            Map<String, Object> parametros = new HashMap<>();

            String codigoEan = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CODIGO EAN PLUSVALIA",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "")
                            .toString();

            String secretariaPlaneacion = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE DE SECRETARIA DE PLANEACION",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "")
                            .toString();

            String profesionalUniversitario = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE DEL PROFESIONAL UNIVERSITARIO",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "")
                            .toString();

            // MANEJO DE PARAMETROS DE REEMPLAZO
            reemplazar.put("idProyecto", idProyecto);
            reemplazar.put("numeroProceso", procesoCalculo);
            reemplazar.put("validar", 0);
            reemplazar.put("idBeneficiario", 0);
            reemplazar.put("codigoEan", codigoEan);

            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_NOMBRE_SECRETARIA_PLANEACION",
                            secretariaPlaneacion);

            parametros.put("PR_NOMBRE_PROFESIONAL_UNIVERSITARIO",
                            profesionalUniversitario);

            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);
            //TODO cálculo con la etapa correspondiente - Cesar
//            if (ckReclasificar) {
//                etapa = etapaRefacturar;
//            }
//            parametros.put("PR_ETAPA", etapa);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control ckReclasificar
     *
     * 
     */
    public void cambiarckReclasificar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
    public void seleccionarFilaBeneficiario(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        beneficiario = SysmanFunciones
                        .nvl(registroAux.getCampos().get("IP_CODIGO"), "")
                        .toString();

        nombre = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();

        idBeneficiario = SysmanFunciones.nvl(
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

        cargarListaBeneficiario();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNumeroProceso
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroProceso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroProceso = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();

        idProyecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID_PROYECTO"), "")
                        .toString();

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
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre
     * the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
    public RegistroDataModelImpl getListaBeneficiario() {
        return listaBeneficiario;
    }

    /**
     * Asigna la lista listaBeneficiarioInicial
     * 
     * @param listaBeneficiarioInicial
     * Variable a asignar en listaBeneficiarioInicial
     */
    public void setListaBeneficiario(
        RegistroDataModelImpl listaBeneficiario) {
        this.listaBeneficiario = listaBeneficiario;
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
        return idBeneficiario;
    }

    /**
     * @param idBeneficiarioIni
     * the idBeneficiarioIni to set
     */
    public void setIdBeneficiarioIni(String idBeneficiario) {
        this.idBeneficiario = idBeneficiario;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la lista listaNumeroProceso
     * 
     * @return listaNumeroProceso
     */
    public RegistroDataModelImpl getListaNumeroProceso() {
        return listaNumeroProceso;
    }

    /**
     * Asigna la lista listaNumeroProceso
     * 
     * @param listaNumeroProceso
     * Variable a asignar en listaNumeroProceso
     */
    public void setListaNumeroProceso(
        RegistroDataModelImpl listaNumeroProceso) {
        this.listaNumeroProceso = listaNumeroProceso;
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
     * @return the beneficiario
     */
    public String getBeneficiario() {
        return beneficiario;
    }

    /**
     * @param beneficiario
     * the beneficiario to set
     */
    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
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

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
