/*-
 * FrmmodificacionesdeudasControlador.java
 *
 * 1.0
 * 
 * 27/12/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved. 
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.FrmmodificacionesdeudasControladorEnum;
import com.sysman.serviciospublicos.enums.FrmmodificacionesdeudasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que contiene la migraci�n del formualario Modificaciones a
 * la deuda.
 * 
 * @version 1.0, 27/12/2016
 * @author acaceres
 * 
 * @author eamaya
 * @version 2, 30/05/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 */
@ManagedBean
@ViewScoped
public class FrmmodificacionesdeudasControlador extends BeanBaseDatosAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    private final String compania;

    /**
     * Contante a nivel de clase que almacena el numero del m�dulo
     * en el cual inicio sesion el usuario, el valor de esta constante
     * es asignado en el constructor a la variable
     */

    private final String modulo;

    /**
     * Constante que almacenara el codigo de ruta seleccionado
     */
    private final String codigoRutaC;

    /**
     * Constante que almacenara el codigo seleccionado
     */
    private final String codigoC;

    /**
     * Constante que almacenara el concepto seleccionado
     */
    private final String conceptoC;

    /**
     * Constante que almacenara la cadena SP_MODIFICACIONESDEUDA
     */
    private final String spModificacionesDeudaC;

    /**
     * Constante que almacenara la cadena PERIODO
     */
    private final String periodoC;

    /**
     * Constante que almacenara la cadena NOMBRECONCEPTO
     */
    private final String nombreConceptoC;

    /**
     * Constante que almacenara la cadena NOMBRE
     */
    private final String nombreC;

    /**
     * Constante que almacenara la cadena FECHA
     */
    private final String fechaC;

    /**
     * Constante que almacenara la cadena VRNUE;
     */
    private final String vrnuevoC;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que almacenara el codigo interno del rubro
     * seleccionado
     */
    private String codigoInterno;

    /**
     * Atributo que almacenara el nombre del codigo interno
     * seleccionado
     */
    private String nombreCodigoRuta;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacenara los conceptos a seleccionar
     */
    private RegistroDataModelImpl listaconcepto;

    /**
     * Lista que almacenara los conceptos a seleccionar en la grilla
     */
    private RegistroDataModelImpl listaconceptoE;

    /**
     * Lista que almacenara la lista de conceptos en el subformulario
     * modificaciones sub
     */
    private RegistroDataModelImpl listaTexto14;

    /**
     * Lista que almacenara la lista de conceptos en la grilla del
     * subformulario
     */
    private RegistroDataModelImpl listaTexto14E;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Atributo que almacenara el ciclo del registro seleccionado
     */
    private String ciclo;

    /**
     * Atributo que almacenara el anio del registro que se selecciono
     */
    private String anio;

    /**
     * Atributo que almacenara el periodo del ciclo seleccionado
     */
    private String periodo;

    /**
     * Atributo que almacenara el valor del campo FIMM del codigo de
     * ruta seleccionado
     */
    private String fimm;

    /**
     * Atributo que almacenara el nombre del concepto seleccionado en
     * el subformulario
     */
    private String nombreConcepto;

    /**
     * Variable encargada de almacenar temporalmente la cosulta para
     * el combo concepto, esta depende te un parametro
     */

    private String tasaRecargo;

    /**
     * Atributo que almacenara el valor Anterior al cambiar el
     * concepto en el subformulario
     */
    private String valorAnteriorSub;

    /**
     * Atributo que almacenara el valor Nuevo al cambiar el concepto
     * en el subformulario
     */
    private String valorNuevoSub;
    /**
     * Atributo que almacenara la suma total del valor nuevo en la
     * grilla del subformulario frmmodificacionesSub
     */
    private String totalValorAnterior;

    /**
     * Atributo que almacenara la suma total del valor Anterior en la
     * grilla del subformulario frmmodificacionesSub
     */
    private String totalValorNuevo;

    /**
     * Atributo usado para bloquear el campo c�digo ruta del
     * formulario.
     */
    private boolean bloqueaCodigoRuta;

    /**
     * Atributo usado para bloquear el campo Codigo del formulario
     */
    private boolean bloqueaCodigo;

    /**
     * Atributo usado para bloquear el campo observaciones del
     * formulario
     */
    private boolean bloqueaObservaciones;

    /**
     * Lista de registros usada para mostrar la lista de los codigos
     */
    private List<Registro> listaCodigo;
    /**
     * Registro usado para cargar la lista de los codigos de ruta
     */
    private RegistroDataModelImpl listaCodigoRuta;

    /**
     * Registro que almacenara el codigo Interno a seleccionar
     */
    private RegistroDataModelImpl listacmbCodInterno;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>

    /**
     * Lista que almacenara los registros del subFormulario
     * modificacionesSub
     */
    private List<Registro> listaFrmmodificacionessub;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private boolean conceptoFueraDerango;
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    @EJB
    private EjbSysmanUtilRemote ejbUtil;

    @EJB
    private EjbServiciosPublicosCeroRemote ejbSPCero;

    // </DECLARAR_ADICIONALES>

    /**
     * Crea una nueva instancia de FrmmodificacionesdeudasControlador
     */
    public FrmmodificacionesdeudasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoRutaC = "CODIGORUTA";
        codigoC = "CODIGO";
        conceptoC = "CONCEPTO";
        spModificacionesDeudaC = "SP_MODIFICACIONESDEUDA";
        periodoC = "PERIODO";
        nombreConceptoC = "NOMBRECONCEPTO";
        nombreC = "NOMBRE";
        fechaC = "FECHA";
        vrnuevoC = "VRNUE";
        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ciclo = parametrosEntrada.get("ciclo").toString();
                anio = parametrosEntrada.get("anio").toString();
                periodo = parametrosEntrada.get("periodo").toString();
                tasaRecargo = parametrosEntrada.get("TASARECARGO").toString();
            }
            else {
                SessionUtil.redireccionarMenu();
            }
            numFormulario = GeneralCodigoFormaEnum.FRMMODIFICACIONESDEUDAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());

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
        cargarListaCodigoRuta();
        ;
        cargarListaTexto14();
        cargarListaTexto14E();
        cargarListacmbCodInterno();
        cargarListaCodigo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaFrmmodificacionessub();
        cargarListaconcepto();
        cargarListaconceptoE();
        cargarTotales();
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
        listaFrmmodificacionessub = null;
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
        enumBase = GenericUrlEnum.SP_MODIFICACIONES;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario.
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        parametrosListado.put("TIPO", "1");
    }

    /**
     * 
     * Carga la lista listaFrmmodificacionessub
     */
    public void cargarListaFrmmodificacionessub() {
        try {

            Date fecha = (Date) registro.getCampos().get(fechaC);

            Date hora = (Date) registro.getCampos().get("HORA");

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos().get(codigoRutaC));

            param.put(GeneralParameterEnum.FECHA.getName(), fecha);

            param.put(FrmmodificacionesdeudasControladorEnum.PARAM0.getValue(),
                            hora);

            listaFrmmodificacionessub = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmodificacionesdeudasControladorUrlEnum.URL11910
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            spModificacionesDeudaC));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaconcepto
     *
     */
    public void cargarListaconcepto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmodificacionesdeudasControladorUrlEnum.URL13314
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(GeneralParameterEnum.PERIODO.getName(),
                        periodo);

        if (accion.equals(ACCION_INSERTAR)) {
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), "");
        }
        else {
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos().get(codigoRutaC));
        }

        listaconcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, conceptoC);

        if (listaconcepto.getDatasource().isEmpty()) {
            conceptoFueraDerango = true;
        }

    }

    /**
     * 
     * Carga la lista listaconcepto
     *
     */
    public void cargarListaconceptoE() {
        listaconceptoE = listaconcepto;
    }

    /**
     * 
     * Carga la lista listaTexto14
     *
     */
    public void cargarListaTexto14() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmodificacionesdeudasControladorUrlEnum.URL16629
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTexto14 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoC);
    }

    /**
     * 
     * Carga la lista listaTexto14
     *
     */
    public void cargarListaTexto14E() {
        listaTexto14E = listaTexto14;

    }

    /**
     * 
     * Carga la lista listaCodigoRuta
     *
     */
    public void cargarListaCodigoRuta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmodificacionesdeudasControladorUrlEnum.URL17915
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);

        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaC);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCodigo
     *
     */
    public void cargarListaCodigo() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaCodigo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmodificacionesdeudasControladorUrlEnum.URL16915
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
     * Carga la lista listacmbCodInterno
     *
     */
    public void cargarListacmbCodInterno() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmodificacionesdeudasControladorUrlEnum.URL20405
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);

        listacmbCodInterno = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOINTERNO");
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaconcepto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaconcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(conceptoC,
                        registroAux.getCampos().get(conceptoC));
        registroSub.getCampos().put(nombreConceptoC,
                        registroAux.getCampos().get(nombreC));
        registroSub.getCampos().put("VRANT",
                        registroAux.getCampos().get("VALOR_FACTURADO"));
        registroSub.getCampos().put(vrnuevoC,
                        registroAux.getCampos().get("DEUDA"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaconcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaconceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(conceptoC).toString(), "");
        registroSub.getCampos().put(nombreConceptoC,
                        registroAux.getCampos().get(nombreC));
        nombreConcepto = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(nombreC).toString(), "");
        valorAnteriorSub = SysmanFunciones.nvlStr(registroAux.getCampos()
                        .get("VALOR_FACTURADO").toString(), "");
        valorNuevoSub = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get("DEUDA").toString(), "");

    }

    /**
     * Metodo ejecutado al cambiar el control concepto en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarconceptoC(int rowNum) {

        listaFrmmodificacionessub.get(rowNum % 10).getCampos()
                        .put(nombreConceptoC, nombreConcepto);
        listaFrmmodificacionessub.get(rowNum % 10).getCampos().put("VRANT",
                        valorAnteriorSub);
        listaFrmmodificacionessub.get(rowNum % 10).getCampos().put(vrnuevoC,
                        valorNuevoSub);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTexto14
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTexto14(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(conceptoC,
                        registroAux.getCampos().get(codigoC));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTexto14
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTexto14E(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(codigoC).toString(), "");
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoRuta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRuta(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(codigoRutaC,
                        registroAux.getCampos().get(codigoRutaC));

        registro.getCampos().put(nombreC,
                        registroAux.getCampos().get("EXPR1").toString());

        registro.getCampos().put("USO", registroAux.getCampos().get("USO"));
        registro.getCampos().put("ESTRATO",
                        registroAux.getCampos().get("ESTRATO"));

        if (registroAux.getCampos().get("FIMM").toString() != null) {
            fimm = registroAux.getCampos().get("FIMM").toString();
        }

        if (validacionesCodigoRuta() == 1) {

            registro.getCampos().put(codigoRutaC, "");
            registro.getCampos().put(nombreC, "");

            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2713"));
            return;

        }

        if (validacionesCodigoRuta() == 2) {

            registro.getCampos().put(codigoRutaC, "");
            registro.getCampos().put(nombreC, "");

            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2712"));
            return;

        }

    }

    private int validacionesCodigoRuta() {
        try {
            if ("SI".equals(SysmanFunciones
                            .nvl(ejbUtil.consultarParametro(compania,
                                            "FACTURACION EN SITIO",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "NO"))
                && "P".equals(fimm)) {

                return 1;
            }

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.CICLO.getName(),
                            ciclo);

            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos().get(codigoRutaC));

            Registro reg = RegistroConverter
                            .toRegistro(requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmodificacionesdeudasControladorUrlEnum.URL34598
                                                                            .getValue())
                                            .getUrl(), param));

            if (reg != null) {

                return 2;
            }

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return 0;

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(codigoC,
                        registroAux.getCampos().get(codigoC));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodInterno
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodInterno(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInterno = registroAux.getCampos().get("CODIGOINTERNO").toString();
    }
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>

    public void obtenerReporte(ReportesBean.FORMATOS formato) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        List<String> infromeParametros = generarParametrosReporte();
        String reporte = "001339INFMODIFICACIONES";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigo", registro.getCampos().get(codigoC));
            reemplazar.put("fecha",
                            SysmanFunciones.formatearFecha((Date) registro
                                            .getCampos().get(fechaC)));
            reemplazar.put("hora",
                            SysmanFunciones.formatearFecha((Date) registro
                                            .getCampos().get("HORA")));

            reemplazar.put("companiaNit",
                            SessionUtil.getCompaniaIngreso().getNit());
            reemplazar.put("companiaSigla",
                            SessionUtil.getCompaniaIngreso().getSigla());

            reemplazar.put("companiaNombre",
                            SessionUtil.getCompaniaIngreso().getNombre());

            reemplazar.put("tipo", 1);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRE DEL JEFE DE COMERCIAL",
                            infromeParametros.get(0));
            parametros.put("PR_CARGO DIRECTOR COMERCIAL",
                            infromeParametros.get(1));
            parametros.put("PR_NOMBRE FUNCIONARIO PQR",
                            infromeParametros.get(2));
            parametros.put("CARGO FUNCIONARIO PQR", infromeParametros.get(3));
            parametros.put("PR_CARGO", infromeParametros.get(4));
            parametros.put("PR_NOMBRE", infromeParametros.get(4));
            parametros.put("PR_TITULO", infromeParametros.get(5));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), "<br>",
                            reporte));
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), "<br>",
                            e.getMessage()));
        }
        // </CODIGO_DESARROLLADO>
    }

    public List<String> generarParametrosReporte() {

        ArrayList<String> parametros = new ArrayList<>();
        try {

            parametros.add(ejbUtil.consultarParametro(compania,
                            "NOMBRE DEL JEFE DE COMERCIAL",
                            SessionUtil.getModulo(), new Date(), false));

            parametros.add(ejbUtil.consultarParametro(compania,
                            "CARGO DIRECTOR COMERCIAL", SessionUtil.getModulo(),
                            new Date(), false));
            parametros.add(ejbUtil.consultarParametro(compania,
                            "NOMBRE FUNCIONARIO PQR", SessionUtil.getModulo(),
                            new Date(), false));
            parametros.add(ejbUtil.consultarParametro(compania,
                            "CARGO FUNCIONARIO PQR", SessionUtil.getModulo(),
                            new Date(), false));

            parametros.add(SessionUtil.getUser().getCodigo());

            parametros.add(SessionUtil.getCompaniaIngreso().getNit());

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametros;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprime en la vista,
     * genera el informe en formato Pdf
     *
     */
    public void oprimirImprime() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista, genera
     * el informe en formato excel
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>

    /**
     * Metodo de insercion del formulario Frmmodificacionessub
     * 
     */
    public void agregarRegistroSubFrmmodificacionessub() {
        try {

            cargarRegistroSub();

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_MODIFICACIONESDEUDA
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            cargarListaFrmmodificacionessub();
            cargarTotales();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
            actualizarTotatlesSub();
        }
    }

    /**
     * Metodo de edicion del formulario Frmmodificacionessub
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubFrmmodificacionessub(RowEditEvent event) {

        Registro reg = (Registro) event.getObject();

        double valorNuevo;
        int concepto = Integer
                        .parseInt(reg.getCampos().get(conceptoC).toString());
        reg.getCampos().remove(nombreConceptoC);
        valorNuevo = Double
                        .parseDouble(reg.getCampos().get(vrnuevoC).toString());
        try {
            if (valorNuevo >= 0 || concepto == 249) {

                Map<String, Object> parametros = new HashMap<>();
                parametros.put(GeneralParameterEnum.DEUDA.getName(),
                                valorNuevo);
                parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());

                parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros.put(GeneralParameterEnum.CICLO.getName(), ciclo);

                parametros.put(GeneralParameterEnum.CODIGORUTA.getName(),
                                registro.getCampos().get(codigoRutaC));

                parametros.put(GeneralParameterEnum.ANO.getName(), anio);

                parametros.put(GeneralParameterEnum.PERIODO.getName(), periodo);

                parametros.put(GeneralParameterEnum.CONCEPTO.getName(),
                                concepto);

                Parameter parameter = new Parameter();
                parameter.setFields(parametros);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmmodificacionesdeudasControladorUrlEnum.URL47256
                                                                .getValue());

                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                parameter);

            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2725"));
                reg.getCampos().put(vrnuevoC, 0);
            }

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_MODIFICACIONESDEUDA
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
            cargarListaFrmmodificacionessub();
            cargarTotales();

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaFrmmodificacionessub();
            cargarTotales();
            actualizarTotatlesSub();
        }
    }

    /**
     * Metodo de eliminacion del formulario Frmmodificacionessub
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubFrmmodificacionessub(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_MODIFICACIONESDEUDA
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

            cargarListaFrmmodificacionessub();
            cargarTotales();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Frmmodificacionessub
     */
    private void cargarRegistroSub() {
        registroSub.getCampos().put(codigoRutaC,
                        registro.getCampos().get(codigoRutaC));

        registroSub.getCampos().put("TIPOMODIFICACION",
                        1);
        registroSub.getCampos().put("CAUSAMODIFICACION",
                        1);
        registroSub.getCampos().put("ANO",
                        anio);

        registroSub.getCampos().put(periodoC,
                        periodo);

        registroSub.getCampos().put("COMPANIA",
                        compania);
        registroSub.getCampos().put("CICLO", ciclo);

        registroSub.getCampos().put(fechaC,
                        registro.getCampos().get(fechaC));

        registroSub.getCampos().put("HORA",
                        registro.getCampos().get("HORA"));

        registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
                        SessionUtil.getUser().getCodigo());

        registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(),
                        new Date());

        registroSub.getCampos().remove(nombreConceptoC);
    }

    public void cancelarEdicionFrmmodificacionessub() {
        cargarListaFrmmodificacionessub();
        cargarTotales();
    }
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
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        String nomPeriodo = "";
        try {
            if (css == null) {
                registro.getCampos().put(fechaC, new Date());

                registro.getCampos().put("HORA", new Date());

                registro.getCampos().put("OPERADOR",
                                SessionUtil.getUser().getCodigo());
                registro.getCampos().put("CREATED_BY",
                                SessionUtil.getUser().getNombre1());

                bloqueaCodigoRuta = false;
                bloqueaCodigo = false;
                bloqueaObservaciones = false;

                nomPeriodo = ejbSPCero.asignarNombrePeriodo(compania,
                                Integer.parseInt(anio),
                                periodo, null);

                registro.getCampos().put(periodoC, nomPeriodo);

                totalValorAnterior = "0";
                totalValorNuevo = "0";

            }
            else {
                registro.getCampos().put("MODIFIED_BY",
                                SessionUtil.getUser().getNombre1());

                cargarListaconcepto();

                nomPeriodo = ejbSPCero.asignarNombrePeriodo(compania,
                                Integer.parseInt(registro.getCampos().get(
                                                "ANO").toString()),
                                registro.getCampos()
                                                .get(periodoC).toString(),
                                null);
                registro.getCampos().put(periodoC, nomPeriodo);

                bloqueaCodigoRuta = true;
                bloqueaCodigo = true;
                bloqueaObservaciones = true;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ANO", anio);
        registro.getCampos().put(periodoC, periodo);
        registro.getCampos().put("CICLO", ciclo);
        registro.getCampos().put("TIPO", 1);
        registro.getCampos().remove(nombreC);

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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(periodoC, periodo);

        // </CODIGO_DESARROLLADO>
        return true;
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

    private void cargarTotales() {

        Registro registroTotales;
        try {

            Date fecha = (Date) registro.getCampos().get(fechaC);
            Date hora = (Date) registro.getCampos().get("HORA");

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos().get(codigoRutaC));
            param.put(GeneralParameterEnum.FECHA.getName(), fecha);

            param.put(FrmmodificacionesdeudasControladorEnum.PARAM0.getValue(),
                            hora);

            registroTotales = RegistroConverter
                            .toRegistro(requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmodificacionesdeudasControladorUrlEnum.URL15030
                                                                            .getValue())
                                            .getUrl(), param));

            if (registroTotales != null) {
                totalValorAnterior = registroTotales.getCampos().get("VALORANT")
                                .toString();
                totalValorNuevo = registroTotales.getCampos().get("VALORNUEVO")
                                .toString();

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void actualizarTotatlesSub() {

        try {
            double totSegFecha;

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.CICLO.getName(),
                            ciclo);

            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos().get(codigoRutaC));

            Registro reg = RegistroConverter
                            .toRegistro(requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmodificacionesdeudasControladorUrlEnum.URL17269
                                                                            .getValue())
                                            .getUrl(), param));

            if ((reg.getCampos().get("SVF") != null)
                && (valorAnteriorSub != reg.getCampos().get("SVF"))) {
                String valorNuevo = reg.getCampos().get("SVF").toString();

                if ("SI".equals(SysmanFunciones
                                .nvl(ejbUtil.consultarParametro(compania,
                                                "RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                                "NO"))) {
                    totSegFecha = Double.parseDouble(valorNuevo);

                }
                else {
                    totSegFecha = Double.parseDouble(valorNuevo) * porRecargo();
                }

                Map<String, Object> parametros = new HashMap<>();

                parametros.put(FrmmodificacionesdeudasControladorEnum.PARAM1
                                .getValue(),
                                valorNuevo);
                parametros.put(FrmmodificacionesdeudasControladorEnum.PARAM2
                                .getValue(),
                                totSegFecha);

                parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());

                parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());

                parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros.put(GeneralParameterEnum.CICLO.getName(), ciclo);

                parametros.put(GeneralParameterEnum.CODIGORUTA.getName(),
                                registro.getCampos().get(codigoRutaC));

                Parameter parameter = new Parameter();
                parameter.setFields(parametros);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmmodificacionesdeudasControladorUrlEnum.URL33932
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                parameter);

            }
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public double porRecargo() {
        double recargo = Double.parseDouble(tasaRecargo);
        if (recargo > 0) {
            return 1 + (recargo / 100);
        }
        else {
            return 1;

        }

    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto mostrarMensaje en
     * la vista
     *
     *
     */
    public void ejecutarmostrarMensaje() {
        // <CODIGO_DESARROLLADO>
        if (conceptoFueraDerango) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2750"));
        }
        // </CODIGO_DESARROLLADO>
    }
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable codigoInterno
     * 
     * @return codigoInterno
     */
    public String getCodigoInterno() {
        return codigoInterno;
    }

    /**
     * Asigna la variable codigoInterno
     * 
     * @param codigoInterno
     * Variable a asignar en codigoInterno
     */
    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    /**
     * Retorna la variable nombreCodigoRuta
     * 
     * @return nombreCodigoRuta
     */
    public String getNombreCodigoRuta() {
        return nombreCodigoRuta;
    }

    /**
     * Asigna la variable nombreCodigoRuta
     * 
     * @param nombreCodigoRuta
     * Variable a asignar en nombreCodigoRuta
     */
    public void setNombreCodigoRuta(String nombreCodigoRuta) {
        this.nombreCodigoRuta = nombreCodigoRuta;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaconcepto
     * 
     * @return listaconcepto
     */
    public RegistroDataModelImpl getListaconcepto() {
        return listaconcepto;
    }

    /**
     * Asigna la lista listaconcepto
     * 
     * @param listaconcepto
     * Variable a asignar en listaconcepto
     */
    public void setListaconcepto(RegistroDataModelImpl listaconcepto) {
        this.listaconcepto = listaconcepto;
    }

    /**
     * Retorna la lista listaconcepto
     * 
     * @return listaconcepto
     */
    public RegistroDataModelImpl getListaconceptoE() {
        return listaconceptoE;
    }

    /**
     * Asigna la lista listaconcepto
     * 
     * @param listaconcepto
     * Variable a asignar en listaconcepto
     */
    public void setListaconceptoE(RegistroDataModelImpl listaconceptoE) {
        this.listaconceptoE = listaconceptoE;
    }

    /**
     * Retorna la lista listaTexto14
     * 
     * @return listaTexto14
     */
    public RegistroDataModelImpl getListaTexto14() {
        return listaTexto14;
    }

    /**
     * Asigna la lista listaTexto14
     * 
     * @param listaTexto14
     * Variable a asignar en listaTexto14
     */
    public void setListaTexto14(RegistroDataModelImpl listaTexto14) {
        this.listaTexto14 = listaTexto14;
    }

    /**
     * Retorna la lista listaTexto14
     * 
     * @return listaTexto14
     */
    public RegistroDataModelImpl getListaTexto14E() {
        return listaTexto14E;
    }

    /**
     * Asigna la lista listaTexto14
     * 
     * @param listaTexto14
     * Variable a asignar en listaTexto14
     */
    public void setListaTexto14E(RegistroDataModelImpl listaTexto14E) {
        this.listaTexto14E = listaTexto14E;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    public String getTotalValorAnterior() {
        return totalValorAnterior;
    }

    public void setTotalValorAnterior(String totalValorAnterior) {
        this.totalValorAnterior = totalValorAnterior;
    }

    public String getTotalValorNuevo() {
        return totalValorNuevo;
    }

    public void setTotalValorNuevo(String totalValorNuevo) {
        this.totalValorNuevo = totalValorNuevo;
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

    public boolean isBloqueaCodigoRuta() {
        return bloqueaCodigoRuta;
    }

    public void setBloqueaCodigoRuta(boolean bloqueaCodigoRuta) {
        this.bloqueaCodigoRuta = bloqueaCodigoRuta;
    }

    /**
     * Retorna la lista listaCodigoRuta
     * 
     * @return listaCodigoRuta
     */
    public RegistroDataModelImpl getListaCodigoRuta() {
        return listaCodigoRuta;
    }

    /**
     * Retorna la lista listaCodigo
     * 
     * @return listaCodigo
     */
    public List<Registro> getListaCodigo() {
        return listaCodigo;
    }

    /**
     * Asigna la lista listaCodigo
     * 
     * @param listaCodigo
     * Variable a asignar en listaCodigo
     */
    public void setListaCodigo(List<Registro> listaCodigo) {
        this.listaCodigo = listaCodigo;
    }

    /**
     * Retorna la lista listacmbCodInterno
     * 
     * @return listacmbCodInterno
     */
    public RegistroDataModelImpl getListacmbCodInterno() {
        return listacmbCodInterno;
    }

    /**
     * Asigna la lista listacmbCodInterno
     * 
     * @param listacmbCodInterno
     * Variable a asignar en listacmbCodInterno
     */
    public void setListacmbCodInterno(
        RegistroDataModelImpl listacmbCodInterno) {
        this.listacmbCodInterno = listacmbCodInterno;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>

    public boolean isBloqueaCodigo() {
        return bloqueaCodigo;
    }

    public void setBloqueaCodigo(boolean bloqueaCodigo) {
        this.bloqueaCodigo = bloqueaCodigo;
    }

    public boolean isBloqueaObservaciones() {
        return bloqueaObservaciones;
    }

    public void setBloqueaObservaciones(boolean bloqueaObservaciones) {
        this.bloqueaObservaciones = bloqueaObservaciones;
    }

    /**
     * Retorna la lista listaFrmmodificacionessub
     * 
     * @return listaFrmmodificacionessub
     */
    public List<Registro> getListaFrmmodificacionessub() {
        return listaFrmmodificacionessub;
    }

    /**
     * Asigna la lista listaFrmmodificacionessub
     * 
     * @param listaFrmmodificacionessub
     * Variable a asignar en listaFrmmodificacionessub
     */
    public void setListaFrmmodificacionessub(
        List<Registro> listaFrmmodificacionessub) {
        this.listaFrmmodificacionessub = listaFrmmodificacionessub;
    }
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getModulo() {
        return modulo;
    }

    // </SET_GET_ADICIONALES>
}
