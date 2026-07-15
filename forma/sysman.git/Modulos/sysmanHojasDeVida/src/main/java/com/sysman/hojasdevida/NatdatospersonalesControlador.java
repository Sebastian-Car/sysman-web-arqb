/*-
 * NatdatospersonalesControlador.java
 *
 * 1.0
 *
 * 16/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida;

import com.lowagie.text.pdf.codec.Base64;
import com.sun.tools.javac.util.Paths;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.FrmAnexosControladorUrlEnum;
import com.sysman.hojasdevida.enums.NatdatospersonalesControladorEnum;
import com.sysman.hojasdevida.enums.NatdatospersonalesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import org.apache.commons.io.IOUtils;

/**
 * Formulario que administra los datos básicos del módulo de hojas de
 * vida
 *
 * @version 1.0, 26/03/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 * 
 * @version 2.0 21/06/2018, Se realiza PY.ASEGURAMIENTO E INTEGRACION
 * descrito en el tar 1000081152.Se organizan campos, se crea
 * validacion de campos de acuerda a parametros, se valida campo
 * obligatorio por medio de la cofiguracion del banco seleccionado en
 * la pestaña "DATOS PERSONALES"
 * @author eamaya
 * 
 * @version 3.0 25/06/2018 @asana se realiza llamado al procedimiento
 * DocumentosPresentados desde el metodo OprimirRequisitos en donde Se
 * agregan los registros que no existan en la tabla
 * documentos_presentados
 * 
 * 
 * @version 4.0 26/06/2018 asana Se realiza llamado al procedimiento
 * experienciaLaboral en el método oprimirexpLaboral() para actualiza
 * la experiencia laboral del empleado desde el módulo de Nomina
 */
@ManagedBean
@ViewScoped
public class NatdatospersonalesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    
    /**
     * Constante a nivel de clase que almacena el modulo en el cual
     * inicio sesion el usuario, el valor de esta constante es
     * asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String modulo;

    private static final String MENSAJE_ALERTA_BOTONES = "TB_TB3992";
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que almacena el pais de nacimiento
     */
    private String paisNcto;

    /**
     * Atributo que almacena el departamento de nacimiento
     */
    private String dptoNcto;

    /**
     * Atributo que almacena el pais de residencia
     */
    private String paisReside;
    /**
     * Atributo que almacena el departamento de residencia
     */
    private String dptoReside;

    /**
     * Atributo que almacena el pais donde labora
     */
    private String paisLabora;
    /**
     * Atributo que almacena el departamento donde labora
     */
    private String dptoLabora;
    /**
     * Atributo que almacena el pais de expedicion del documento
     */
    private String paisExpdicion;
    /**
     * Atributo que almacena el departamento de expedicion del
     * documento
     */
    private String dptoExpdicion;
    /**
     * Atributo utilizado para almacenar el total de anios de
     * experiencia
     */
    private String totalExperienciaAnios;
    /**
     * Atributo utilizado para almacenar el total de msese de
     * experiencia
     */
    private String totalExperienciaMeses;
    /**
     * Atributo utilizado para almacenar el total de dias de
     * experiencia
     */
    private String totalExperienciaDias;
    
    private int sumaExperienciaAnios;
    private int sumaExperienciaMeses;
    
    /**
     * Atributo utilizado para almacenar la ruta donde se esta
     * guardando la imagen 
     */
    private String fotoPerfil;
    // </DECLARAR_ATRIBUTOS>

    // <DECLARAR_LISTAS>
    /**
     * Lista que almacena los paises de nacimiento
     */
    private List<Registro> listaPAISNCTO;
    /**
     * Lista que almacena el departamento de nacimiento
     */
    private List<Registro> listaDEPTONCTO;
    /**
     * Lista que almacena el municipio de nacimiento
     */
    private List<Registro> listaMUNICIPIONCTO;
    /**
     * Lista que almacena los paises de residencia
     */
    private List<Registro> listaPAISRESIDE;
    /**
     * Lista que almacena los paises laborales
     */
    private List<Registro> listapaisLabora;
    /**
     * Lista que almacena los nuneros de documento de identidad
     */
    private List<Registro> listadctoIdentidad;
    /**
     * Lista que almacena los departamentos de residencia
     */
    private List<Registro> listaDEPTORESIDE;
    /**
     * Lista que almacena los departamentos laborales
     */
    private List<Registro> listadepartamentoLabora;
    /**
     * Lista que almacena los municipios de residencia
     */
    private List<Registro> listaMUNICIPIORESIDE;
    /**
     * Lista que almacena las ciudades laborales
     */
    private List<Registro> listaciudadLabora;
    /**
     * Lista que almacena los paises de expedicion de documento
     */
    private List<Registro> listaPAISEXTRANJERO;
    /**
     * Lista que almacena los departamentps de expedicion de documento
     */
    private List<Registro> listaDptoExpedidaCedula;
    /**
     * Lista que almacena las ciudades de expedicion de documento
     */
    private List<Registro> listaExpedida;
    /**
     * Lista que almacena los tipos de estado civil
     */
    private List<Registro> listaESTADOCIVIL;
    /**
     * Lista que almacena los fondos de sindicato
     */
    private List<Registro> listafondoSindicato;
    /**
     * Lista que almacena los patronales
     */
    private List<Registro> listaNumeroPatronal;
    /**
     * Lista que almacena los tipos de documento de identidad
     */
    private List<Registro> listaidDeTipo;
    /**
     * Lista que almacena las sedes
     */
    private List<Registro> listaSEDE;

    /**
     * Lista que almacena los Regimen de Cesantías
     */
    private List<Registro> listaRegimen;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacena los bancos
     */
    private RegistroDataModelImpl listaBanco;
    /**
     * Lista que almacena los centros de costo
     */
    private RegistroDataModelImpl listacentroCosto;
    /**
     * Lista que almacena la ubicacion de trabajo
     */
    private RegistroDataModelImpl listaubicacionTrabajo;
    
    /**
     * Variable que almacena el valor recibido por el parametro de
     * codigo del formulario conceptos
     */
    private String codigo;

    /**
     * Lista que almacena los numeros de documentos
     */
    private RegistroDataModelImpl listanumeroDocumento;

    private boolean libMilitarVisible;

    private boolean certJudicialVisible;

    private boolean dialgDependiente;
    
    private boolean asignarAnexos;

    private StreamedContent archivoDescarga;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de NatdatospersonalesControlador
     */
    public NatdatospersonalesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();


        try {
            numFormulario = GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
            }
            libMilitarVisible = false;
            certJudicialVisible = false;
            dialgDependiente = false;
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

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE> 
        cargarListaBanco();
        cargarListacentroCosto();
        cargarListaubicacionTrabajo();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaPAISNCTO();
        cargarListaPAISRESIDE();
        cargarListapaisLabora();
        cargarListadctoIdentidad();
        cargarListaPAISEXTRANJERO();
        cargarListaESTADOCIVIL();
        cargarListafondoSindicato();
        cargarListaNumeroPatronal();
        cargarListaidDeTipo();
        cargarListaSEDE();
        cargarlistaRegimen();

        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {

        paisNcto = retornarString(registro, "PAISNCTO");
        cargarListaDEPTONCTO();
        dptoNcto = retornarString(registro,
                        NatdatospersonalesControladorEnum.DEPTONCTO.getValue());
        cargarListaMUNICIPIONCTO();

        paisReside = retornarString(registro,
                        NatdatospersonalesControladorEnum.PAISRESIDE
                                        .getValue());
        cargarListaDEPTORESIDE();

        dptoReside = retornarString(registro,
                        NatdatospersonalesControladorEnum.DEPTORESIDE
                                        .getValue());
        cargarListaMUNICIPIORESIDE();
        paisExpdicion = retornarString(registro, "PAISEXTRANJERO");
        cargarListaDptoExpedidaCedula();

        dptoExpdicion = retornarString(registro,
                        NatdatospersonalesControladorEnum.DPTOEXPCEDULA
                                        .getValue());
        cargarListaExpedida();
        paisLabora = retornarString(registro, "PAIS_LABORA");
        cargarListadepartamentoLabora();

        dptoLabora = retornarString(registro,
                        NatdatospersonalesControladorEnum.DEPARAMENTO_LABORA
                                        .getValue());
        cargarListaciudadLabora();

    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.NAT_DATOS_PERSONALES;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     *
     */
    @Override
    public void asignarOrigenDatos() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaPAISNCTO
     *
     */
    public void cargarListaPAISNCTO() {

        try {
            listaPAISNCTO = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL10355
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaDEPTONCTO
     *
     */
    public void cargarListaDEPTONCTO() {

        Map<String, Object> param = new TreeMap<>();
        param.put(NatdatospersonalesControladorEnum.PAIS.getValue(), paisNcto);

        try {
            listaDEPTONCTO = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL10877
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaMUNICIPIONCTO
     *
     */
    public void cargarListaMUNICIPIONCTO() {

        Map<String, Object> param = new TreeMap<>();
        param.put(NatdatospersonalesControladorEnum.PAIS.getValue(),
                        String.valueOf(paisNcto));
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), dptoNcto);

        try {
            listaMUNICIPIONCTO = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL11638
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaPAISRESIDE
     *
     */
    public void cargarListaPAISRESIDE() {

        try {
            listaPAISRESIDE = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL12562
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listapaisLabora
     *
     */
    public void cargarListapaisLabora() {

        try {
            listapaisLabora = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL13088
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listadctoIdentidad
     *
     */
    public void cargarListadctoIdentidad() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listadctoIdentidad = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL13664
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaDEPTORESIDE
     *
     */
    public void cargarListaDEPTORESIDE() {

        Map<String, Object> param = new TreeMap<>();
        param.put(NatdatospersonalesControladorEnum.PAIS.getValue(),
                        paisReside);

        try {
            listaDEPTORESIDE = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL14298
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listadepartamentoLabora
     *
     */
    public void cargarListadepartamentoLabora() {

        Map<String, Object> param = new TreeMap<>();
        param.put(NatdatospersonalesControladorEnum.PAIS.getValue(),
                        paisLabora);

        try {
            listadepartamentoLabora = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL1345
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaMUNICIPIORESIDE
     *
     */
    public void cargarListaMUNICIPIORESIDE() {

        Map<String, Object> param = new TreeMap<>();
        param.put(NatdatospersonalesControladorEnum.PAIS.getValue(),
                        paisReside);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), dptoReside);

        try {
            listaMUNICIPIORESIDE = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL15724
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaciudadLabora
     *
     */
    public void cargarListaciudadLabora() {

        Map<String, Object> param = new TreeMap<>();
        param.put(NatdatospersonalesControladorEnum.PAIS.getValue(),
                        paisLabora);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), dptoLabora);

        try {
            listaciudadLabora = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL16717
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaPAISEXTRANJERO
     *
     */
    public void cargarListaPAISEXTRANJERO() {

        try {
            listaPAISEXTRANJERO = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL17634
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaDptoExpedidaCedula
     *
     */
    public void cargarListaDptoExpedidaCedula() {

        Map<String, Object> param = new TreeMap<>();

        param.put(NatdatospersonalesControladorEnum.PAIS.getValue(),
                        paisExpdicion);

        try {
            listaDptoExpedidaCedula = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL1858
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaExpedida
     *
     */
    public void cargarListaExpedida() {

        Map<String, Object> param = new TreeMap<>();
        param.put(NatdatospersonalesControladorEnum.PAIS.getValue(),
                        paisExpdicion);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), dptoExpdicion);

        try {
            listaExpedida = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL18785
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaESTADOCIVIL
     *
     */
    public void cargarListaESTADOCIVIL() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaESTADOCIVIL = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL19718
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listafondoSindicato
     *
     */
    public void cargarListafondoSindicato() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listafondoSindicato = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL20266
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaNumeroPatronal
     *
     */
    public void cargarListaNumeroPatronal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaNumeroPatronal = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL20843
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaidDeTipo
     *
     */
    public void cargarListaidDeTipo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaidDeTipo = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL21460
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaSEDE
     *
     */
    public void cargarListaSEDE() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaSEDE = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL21907
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaRegimen
     *
     */

    public void cargarlistaRegimen() {

        HashMap<String, Object> param = new HashMap<>();

        try {
            listaRegimen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NatdatospersonalesControladorUrlEnum.URL20270
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
     * Carga la lista listaBanco
     *
     */
    public void cargarListaBanco() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatdatospersonalesControladorUrlEnum.URL22219
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.BANCO.getName());
    }

    /**
     *
     * Carga la lista listacentroCosto
     *
     */
    public void cargarListacentroCosto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatdatospersonalesControladorUrlEnum.URL22799
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaubicacionTrabajo
     *
     */
    public void cargarListaubicacionTrabajo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatdatospersonalesControladorUrlEnum.URL23462
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaubicacionTrabajo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listanumeroDocumento
     *
     */
    public void cargarListanumeroDocumento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatdatospersonalesControladorUrlEnum.URL24079
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.CLASE.getName(), "E");

        param.put("TIPO", registro.getCampos().get("DCTO_IDENTIDAD"));

        listanumeroDocumento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");
    }

    /**
     * Metodo ejecutado al cambiar el control correoCorporativo
     *
     */
    public boolean cambiarcorreoCorporativo() {

        return true;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control PAISNCTO
     *
     */
    public void cambiarPAISNCTO() {

        registro.getCampos().put(
                        NatdatospersonalesControladorEnum.DEPTONCTO.getValue(),
                        "");
        registro.getCampos().put("MUNICIPIONCTO", "");
        paisNcto = registro.getCampos().get("PAISNCTO").toString();
        cargarListaDEPTONCTO();

    }

    /**
     * Metodo ejecutado al cambiar el control DEPTONCTO
     *
     */
    public void cambiarDEPTONCTO() {
        registro.getCampos().put("MUNICIPIONCTO", "");
        dptoNcto = registro.getCampos().get(
                        NatdatospersonalesControladorEnum.DEPTONCTO.getValue())
                        .toString();
        cargarListaMUNICIPIONCTO();
    }

    /**
     * Metodo ejecutado al cambiar el control PAISRESIDE
     *
     */
    public void cambiarPAISRESIDE() {
        registro.getCampos().put(NatdatospersonalesControladorEnum.DEPTORESIDE
                        .getValue(), "");
        registro.getCampos()
                        .put(NatdatospersonalesControladorEnum.MUNICIPIORESIDE
                                        .getValue(), "");
        paisReside = registro.getCampos().get(
                        NatdatospersonalesControladorEnum.PAISRESIDE.getValue())
                        .toString();
        cargarListaDEPTORESIDE();

    }

    /**
     * Metodo ejecutado al cambiar el control DEPTORESIDE
     *
     */
    public void cambiarDEPTORESIDE() {
        registro.getCampos()
                        .put(NatdatospersonalesControladorEnum.MUNICIPIORESIDE
                                        .getValue(), "");
        dptoReside = registro.getCampos()
                        .get(NatdatospersonalesControladorEnum.DEPTORESIDE
                                        .getValue())
                        .toString();
        cargarListaMUNICIPIORESIDE();
    }

    /**
     * Metodo ejecutado al cambiar el control PAISEXTRANJERO
     *
     *
     */
    public void cambiarPAISEXTRANJERO() {
        registro.getCampos().put(NatdatospersonalesControladorEnum.DPTOEXPCEDULA
                        .getValue(), "");
        registro.getCampos().put("EXPEDIDA", "");
        paisExpdicion = registro.getCampos().get("PAISEXTRANJERO").toString();
        cargarListaDptoExpedidaCedula();
    }

    /**
     * Metodo ejecutado al cambiar el control DptoExpedidaCedula
     *
     *
     */
    public void cambiarDptoExpedidaCedula() {
        registro.getCampos().put("EXPEDIDA", "");
        dptoExpdicion = registro.getCampos()
                        .get(NatdatospersonalesControladorEnum.DPTOEXPCEDULA
                                        .getValue())
                        .toString();
        cargarListaExpedida();
    }

    /**
     * Metodo ejecutado al cambiar el control paisLabora
     *
     *
     */
    public void cambiarpaisLabora() {
        registro.getCampos().put(
                        NatdatospersonalesControladorEnum.DEPARAMENTO_LABORA
                                        .getValue(),
                        "");
        registro.getCampos().put("CIUDAD_LABORA", "");
        paisLabora = registro.getCampos().get("PAIS_LABORA").toString();
        cargarListadepartamentoLabora();
    }

    /**
     * Metodo ejecutado al cambiar el control dctoIdentidad
     * 
     */
    public void cambiardctoIdentidad() {
        registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        "");
        cargarListanumeroDocumento();
    }

    /**
     * Metodo ejecutado al cambiar el control departamentoLabora
     *
     *
     */
    public void cambiardepartamentoLabora() {
        registro.getCampos().put("CIUDAD_LABORA", "");
        dptoLabora = registro.getCampos().get(
                        NatdatospersonalesControladorEnum.DEPARAMENTO_LABORA
                                        .getValue())
                        .toString();
        cargarListaciudadLabora();
    }

    /**
     * Metodo ejecutado al cambiar el control FechaDeRetiro
     *
     */
    public void cambiarFechaDeRetiro() {
        Date fechaIngreso = (Date) registro.getCampos().get("FECHAINGRESO");
        Date fechaRetiro = (Date) registro.getCampos().get("FECHARETIRO");

        if (fechaRetiro.before(fechaIngreso)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3863"));
            registro.getCampos().put("FECHARETIRO", null);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control DEPENDIENTES384
     *
     */
    public void cambiarDEPENDIENTES384() {
        registro.getCampos().put("FECHA_DEPENDIENTES384", new Date());
    }

    /**
     * Metodo ejecutado al cambiar el control DECLARANTES384
     *
     */
    public void cambiarDECLARANTES384() {
        registro.getCampos().put("FECHA_DECLARANTES384", new Date());
    }

    /**
     * Metodo ejecutado al cambiar el control RETEMINIMA
     *
     */
    public void cambiarRETEMINIMA() {
        registro.getCampos().put("FECHA_RETE_MINIMA", new Date());
    }

    /**
     * Metodo ejecutado al cambiar el control CajasCompensacion
     *
     */
    public void retornarFormularioCajasCompensacion(SelectEvent event) {
        cargarRegistro(registro.getLlave(), accion, registro.getIndice());
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaBanco
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.BANCO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.BANCO.getName()));

        registro.getCampos().put("CIUDAD_CUENTA", "");

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacentroCosto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ID_CENTROS_DE_COSTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaubicacionTrabajo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaubicacionTrabajo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIT_ESTABLECIMIENTO_DOCENTES",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listanumeroDocumento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilanumeroDocumento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        registroAux.getCampos().get("NIT"));

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

        registro.getCampos().put("DCTO_IDENTIDAD",
                        registroAux.getCampos().get("TIPOID"));

        registro.getCampos().put("DIRECCIONRESIDENCIA",
                        registroAux.getCampos().get("DIRECCION"));

        registro.getCampos().put("TELEFONORESIDENCIA",
                        registroAux.getCampos().get("TELEFONOS"));

        registro.getCampos().put(
                        NatdatospersonalesControladorEnum.PAISRESIDE.getValue(),
                        registroAux.getCampos().get("PAIS"));

        registro.getCampos().put(
                        NatdatospersonalesControladorEnum.DEPTORESIDE
                                        .getValue(),
                        registroAux.getCampos().get("DEPARTAMENTO"));

        registro.getCampos()
                        .put(NatdatospersonalesControladorEnum.MUNICIPIORESIDE
                                        .getValue(),
                                        registroAux.getCampos().get("CIUDAD"));

        registro.getCampos().put("APELLIDO1",
                        registroAux.getCampos().get("APELLIDO1"));

        registro.getCampos().put("APELLIDO2",
                        registroAux.getCampos().get("APELLIDO2"));

        registro.getCampos().put("NOMBRES",
                        registroAux.getCampos().get("NOMBRES"));

        iniciarListasSub();

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * dependientes en la vista
     *
     * Se realiza zla validacion si el usuario desea seguir con el
     * registro verificando si tiene o no dependencias
     *
     */
    public void aceptardependientes() {
        // <CODIGO_DESARROLLADO>
        try {
            dialgDependiente = false;
            int codigo = ejbHojasDeVidaCero.registrarPersonal(compania, registro
                            .getCampos()
                            .get(GeneralParameterEnum.NUMERO_DCTO.getName())
                            .toString(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString(),
                            SessionUtil.getUser().toString());
            registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                            codigo);
            cambiarFondos();
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4139")
                            .replace("s$codigo$s",
                                            SysmanFunciones.toString(codigo)));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>

    /**
     *
     * Metodo ejecutado al oprimir el boton publicaciones en la vista
     *
     */
    public void oprimirpublicaciones() {

        if (css != null) {
            String[] campos = { NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                                NatdatospersonalesControladorEnum.SUCURSAL
                                                .getValue(),
                                NatdatospersonalesControladorEnum.CODIGO
                                                .getValue() };
            Object[] valores = { registro.getCampos()
                            .get(GeneralParameterEnum.NUMERO_DCTO.getName())
                            .toString(),
                                 registro.getCampos().get(
                                                 GeneralParameterEnum.SUCURSAL
                                                                 .getName())
                                                 .toString(),
                                 registro.getCampos().get(
                                                 GeneralParameterEnum.CODIGO
                                                                 .getName())
                                                 .toString() };
            SessionUtil.cargarModalDatosFlash(
                            Integer.toString(
                                            GeneralCodigoFormaEnum.PUBLICACIONES_CONTROLADOR
                                                            .getCodigo()),
                            SessionUtil.getModulo(), campos, valores);

        }

        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton idiomas en la vista
     *
     */
    public void oprimiridiomas() {
        if (css != null) {
            String[] campos = { "rid",
                                NatdatospersonalesControladorEnum.NUMEMRODCTO
                                                .getValue(),
                                NatdatospersonalesControladorEnum.SUCURSAL
                                                .getValue(),
                                "niCodPersona" };
            Object[] valores = { css, registro.getCampos()
                            .get(GeneralParameterEnum.NUMERO_DCTO.getName())
                            .toString(),
                                 registro.getCampos().get(
                                                 GeneralParameterEnum.SUCURSAL
                                                                 .getName())
                                                 .toString(),
                                 registro.getCampos().get(
                                                 GeneralParameterEnum.CODIGO
                                                                 .getName())
                                                 .toString() };
            SessionUtil.cargarModalDatosFlash(Integer
                            .toString(GeneralCodigoFormaEnum.IDIOMAS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(), campos, valores);
        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton incentivos en la vista
     *
     */
    public void oprimirincentivos() {
        if (css != null) {
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.SUB_INCENTIVOS_CONTROLADOR
                                            .getCodigo()));
            Map<String, Object> parametrosEntrada = new HashMap<>();
            parametrosEntrada.put("rid", css);
            parametrosEntrada.put(
                            NatdatospersonalesControladorEnum.DPNUMEDOCU
                                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName())
                                            .toString());
            parametrosEntrada.put(
                            NatdatospersonalesControladorEnum.SUCURSAL
                                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString());
            parametrosEntrada.put("id_de_empleado",
                            registro.getCampos().get(
                                            GeneralParameterEnum.CODIGO
                                                            .getName()));

            direccionador.setParametros(parametrosEntrada);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Tercero en la vista
     *
     *
     */
    public void oprimirTercero() {

        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4136"));

        HashMap<String, Object> parametros = new HashMap<>();

        parametros.put("rid", css);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.TERCEROS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    public void oprimirtraerDatosNomina() {

        if (css != null) {

            if ("0".equals(SysmanFunciones
                            .toString(registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())))) {
                dialgDependiente = true;
            }
            else {
                dialgDependiente = false;
                cambiarFondos();
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4138")
                                .replace("s$documento$s", registro.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName())
                                                .toString()));
            }

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton expLaboral en la vista
     *
     *
     */
    public void oprimirexpLaboral() {

        if (css != null) {

            try {
                ejbHojasDeVidaCero.experienciaLaboral(compania,
                                registro.getCampos().get(
                                                GeneralParameterEnum.NUMERO_DCTO
                                                                .getName())
                                                .toString(),
                                registro.getCampos().get(
                                                GeneralParameterEnum.SUCURSAL
                                                                .getName())
                                                .toString(),
                                SessionUtil.getUser().toString());
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            HashMap<String, Object> parametros = new HashMap<>();

            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.CODIGO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));
            Direccionador direccionador = new Direccionador();
            direccionador
                            .setNumForm(Integer.toString(
                                            GeneralCodigoFormaEnum.EXPERIENCIA_LABORALS_CONTROLADOR
                                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton requisitos en la vista
     *
     */
    public void oprimirrequisitos() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {

            try {

                ejbHojasDeVidaCero.documentosPresentados(compania, registro
                                .getCampos()
                                .get(GeneralParameterEnum.SUCURSAL.getName())
                                .toString(),
                                registro.getCampos().get(
                                                GeneralParameterEnum.NUMERO_DCTO
                                                                .getName())
                                                .toString(),
                                SessionUtil.getUser().toString());
            }
            catch (SystemException e) {
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            String[] campos = { NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                                NatdatospersonalesControladorEnum.SUCURSAL
                                                .getValue(),
                                "rid" };
            Object[] valores = { registro.getCampos()
                            .get(GeneralParameterEnum.NUMERO_DCTO.getName()),
                                 registro.getCampos().get(
                                                 GeneralParameterEnum.SUCURSAL
                                                                 .getName()),
                                 css };
            SessionUtil.cargarModalDatosFlashCerrar("1526", modulo, campos,
                            valores);

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton actividades en la vista
     *
     *
     */
    public void oprimiractividades() {

        if (css != null) {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));

            Direccionador direccionador = new Direccionador();
            direccionador
                            .setNumForm(Integer.toString(
                                            GeneralCodigoFormaEnum.NAT_SUBACTIVIDADES_CONTROLADOR
                                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton actualizartitutoprofesion
     * en la vista
     *
     */
    public void oprimiractualizartitutoprofesion() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {

            try {
                ejbHojasDeVidaCero.ActualizarDetallesProfesiones(compania,
                                SysmanFunciones.toString(registro.getCampos()
                                                .get(GeneralParameterEnum.NUMERO_DCTO
                                                                .getName())),
                                SysmanFunciones.toString(registro.getCampos()
                                                .get(GeneralParameterEnum.SUCURSAL
                                                                .getName())),
                                new BigDecimal(SysmanFunciones
                                                .toString(registro.getCampos()
                                                                .get(GeneralParameterEnum.CODIGO
                                                                                .getName()))),
                                SysmanFunciones.toString(SessionUtil.getUser()
                                                .getCodigo()));

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB4018"));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4019"));
            }

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton educacion en la vista
     *
     *
     */
    public void oprimireducacion() {
        if (css != null) {
            String[] campos = { "rid", NatdatospersonalesControladorEnum.CODIGO
                            .getValue() };
            Object[] valores = { registro.getLlave(), registro.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()) };
            SessionUtil.redireccionarPorFormulario(modulo,
                            Integer.toString(
                                            GeneralCodigoFormaEnum.ACADEMICA_CONTROLADOR
                                                            .getCodigo()),
                            campos, valores, true);

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Anexos en la vista
     *
     */
    public void oprimirAnexos() {
        if (css != null) {
            Direccionador direccionador = new Direccionador();
            if(asignarAnexos)
            {
            	direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.FRM_ANEXOSHV_CONTROLADOR
                                        .getCodigo()));
            }
            else
            {
	            direccionador.setNumForm(Integer.toString(
	                            GeneralCodigoFormaEnum.FRM_ANEXOS_CONTROLADOR
	                                            .getCodigo()));
            }
            Map<String, Object> parametrosEntrada = new HashMap<>();
            parametrosEntrada.put("rid", css);
            parametrosEntrada.put(
                            NatdatospersonalesControladorEnum.DPNUMEDOCU
                                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName())
                                            .toString());
            parametrosEntrada.put(
                            NatdatospersonalesControladorEnum.SUCURSAL
                                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString());
            parametrosEntrada.put("id_de_empleado",
                            registro.getCampos().get(
                                            GeneralParameterEnum.CODIGO
                                                            .getName()));

            direccionador.setParametros(parametrosEntrada);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton sedes en la vista
     *
     *
     */
    public void oprimirsedes() {
        if (css != null) {

            SessionUtil.cargarModalDatos(String
                            .valueOf(GeneralCodigoFormaEnum.SEDES_CONTROLADOR
                                            .getCodigo()),
                            modulo);

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }
    
    /**
    *
    * Metodo ejecutado al oprimir el boton sedes en la vista
    *
    *
    */
   public void oprimirHistoriaLaboral() {
       if (css != null) {

    	   String[] campos = { NatdatospersonalesControladorEnum.NUMEMRODCTO
                   .getValue(),
                       NatdatospersonalesControladorEnum.SUCURSAL
                                       .getValue(),
                       "rid" };
		   Object[] valores = { registro.getCampos()
		                   .get(GeneralParameterEnum.NUMERO_DCTO.getName()),
		                        registro.getCampos().get(
		                                        GeneralParameterEnum.SUCURSAL
		                                                        .getName()),
		                        css };
		   SessionUtil.cargarModalDatosFlash(
					Integer.toString(GeneralCodigoFormaEnum.FRM_INFORME_HISLABORAL_CONTROLADOR.getCodigo()), modulo, campos,
					valores);

       }
       else {
           JsfUtil.agregarMensajeAlerta(
                           idioma.getString(MENSAJE_ALERTA_BOTONES));
       }
   }

    /**
     *
     * Metodo ejecutado al oprimir el boton ActualizarCampos en la
     * vista
     *
     *
     */
    public void oprimirActualizarCampos() {
        if (css != null) {
            try {
                ejbHojasDeVidaCero.actualizarCamposNulosPersonal(compania,
                                registro.getCampos().get(
                                                GeneralParameterEnum.NUMERO_DCTO
                                                                .getName())
                                                .toString());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));

            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton escanner en la vista
     *
     *
     */
    public void oprimirescanner() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton nombreArchivo en la vista
     *
     *
     */
    public void oprimirnombreArchivo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton nombramiento en la vista
     *
     *
     */
    public void oprimirnombramiento() {
        if (css != null) {
            Direccionador direccionador = new Direccionador();
            direccionador
                            .setNumForm(Integer.toString(
                                            GeneralCodigoFormaEnum.SUB_NOMBRAMIENTOS_CONTROLADOR
                                                            .getCodigo()));
            Map<String, Object> parametrosEntrada = new HashMap<>();
            parametrosEntrada.put("ridDatos", css);
            parametrosEntrada.put(
                            NatdatospersonalesControladorEnum.SUCURSAL
                                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));
            parametrosEntrada.put(
                            NatdatospersonalesControladorEnum.DPNUMEDOCU
                                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));
            parametrosEntrada.put(
                            NatdatospersonalesControladorEnum.CODIGO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));
            direccionador.setParametros(parametrosEntrada);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton encargosTraslados en la
     * vista
     *
     *
     */
    public void oprimirencargosTraslados() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put("idEmpleado", registro.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName())
                            .toString());
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.SUBENCARGOS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton funciones en la vista
     *
     *
     */
    public void oprimirfunciones() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {
            if (!validarEstadosPersonal()) {

                Map<String, Object> parametros = new HashMap<>();
                parametros.put("ridDatos", css);
                parametros.put("nroDocumento", registro.getCampos().get(
                                GeneralParameterEnum.NUMERO_DCTO.getName()));
                parametros.put("sucursal", registro.getCampos()
                                .get(GeneralParameterEnum.SUCURSAL.getName()));

                Direccionador direccionador = new Direccionador();
                // "1739"
                direccionador.setNumForm(Integer.toString(
                                GeneralCodigoFormaEnum.ULTIMAFUNCIONS_CONTROLADOR
                                                .getCodigo()));
                direccionador.setParametros(parametros);

                SessionUtil.redireccionarForma(direccionador,
                                SessionUtil.getModulo());
            }
            else {

                String mensaje = idioma.getString("TB_TB4015");
                mensaje = mensaje.replace("s$documento$s", registro.getCampos()
                                .get("NUMERO_DCTO").toString());
                mensaje = mensaje.replace("s$sucursal$s", registro.getCampos()
                                .get("SUCURSAL").toString());

                JsfUtil.agregarMensajeAlerta(mensaje);
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton retiros en la vista
     *
     */
    public void oprimirretiros() {
        if (css != null) {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.NAT_SUBRENUNCIAS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton sanciones en la vista
     *
     *
     */
    public void oprimirsanciones() {
        if (css != null) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.IDEMPLEADO
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString());
            parametros.put(NatdatospersonalesControladorEnum.REMUNERACION
                            .getValue(), "2");
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.NAT_SUBLICREMUNS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton comisiones en la vista
     *
     */
    public void oprimircomisiones() {
        if (css != null) {

            String[] campos = { "rid", };
            Object[] valores = { css };
            SessionUtil.redireccionarPorFormulario(modulo,
                            Integer.toString(
                                            GeneralCodigoFormaEnum.COMISION_CONTROLADOR
                                                            .getCodigo()),
                            campos, valores, true);

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton funcionesPorAreas en la
     * vista
     *
     */
    public void oprimirfuncionesPorAreas() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {
            String[] campos = { NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                                NatdatospersonalesControladorEnum.SUCURSAL
                                                .getValue() };
            String[] valores = { registro.getCampos()
                            .get(GeneralParameterEnum.NUMERO_DCTO.getName())
                            .toString(),
                                 registro.getCampos().get(
                                                 GeneralParameterEnum.SUCURSAL
                                                                 .getName())
                                                 .toString() };

            String form = Integer.toString(
                            GeneralCodigoFormaEnum.ULTIMA_FUNCION_AREAS_CONTROLADOR
                                            .getCodigo());

            SessionUtil.cargarModalDatosFlash(form, modulo, campos, valores);

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton PrimasTecnicas en la vista
     *
     */
    public void oprimirPrimasTecnicas() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {

            Direccionador direccionador = new Direccionador();
            direccionador
                            .setNumForm(Integer.toString(
                                            GeneralCodigoFormaEnum.NATSUBPRIMATECNICAS_CONTROLADOR
                                                            .getCodigo()));
            Map<String, Object> parametrosEntrada = new HashMap<>();
            parametrosEntrada.put("rid", css);
            parametrosEntrada.put(
                            NatdatospersonalesControladorEnum.DPNUMEDOCU
                                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName())
                                            .toString());
            parametrosEntrada.put(
                            NatdatospersonalesControladorEnum.SUCURSAL
                                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString());
            parametrosEntrada.put("id_de_empleado",
                            registro.getCampos().get(
                                            GeneralParameterEnum.ID_DE_EMPLEADO
                                                            .getName()));
            parametrosEntrada.put("it_codigopersona",
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString());
            direccionador.setParametros(parametrosEntrada);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton PrimasSecretarial en la
     * vista
     *
     * Redirecciona al formulario PrimaSecretarialsControlador(1546)
     *
     */
    public void oprimirPrimasSecretarial() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {

            Direccionador direccionador = new Direccionador();
            direccionador
                            .setNumForm(Integer.toString(
                                            GeneralCodigoFormaEnum.PRIMA_SECRETARIALS_CONTROLADOR
                                                            .getCodigo()));
            Map<String, Object> parametrosEntrada = new HashMap<>();
            parametrosEntrada.put("rid", css);
            parametrosEntrada.put(
                            NatdatospersonalesControladorEnum.NUMEMRODCTO
                                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName())
                                            .toString());
            parametrosEntrada.put(
                            NatdatospersonalesControladorEnum.SUCURSAL
                                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString());
            parametrosEntrada.put(
                            NatdatospersonalesControladorEnum.IDEMPLEADO
                                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString());
            parametrosEntrada.put("codigoPersona",
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString());
            direccionador.setParametros(parametrosEntrada);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton LicNoRemuneradas en la
     * vista
     *
     *
     */
    public void oprimirLicNoRemuneradas() {

        if (css != null) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(NatdatospersonalesControladorEnum.IDEMPLEADO
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString());

            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.REMUNERACION
                            .getValue(), "0");
            Direccionador direccionador = new Direccionador();
            direccionador.setParametros(parametros);
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.NAT_SUBLICREMUNS_CONTROLADOR
                                            .getCodigo()));

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Compensatorios en la vista
     *
     *
     */
    public void oprimirCompensatorios() {
        if (css != null) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.CODIGO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));

            Direccionador direccionador = new Direccionador();
            direccionador
                            .setNumForm(Integer.toString(
                                            GeneralCodigoFormaEnum.NAT_SUBCOMPENSATORIOS_CONTROLADOR
                                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton LicRemuneradas en la vista
     *
     *
     */
    public void oprimirLicRemuneradas() {
        if (css != null) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.IDEMPLEADO
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString());
            parametros.put(NatdatospersonalesControladorEnum.REMUNERACION
                            .getValue(), "-1");
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.NAT_SUBLICREMUNS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CajasCompensacion en la
     * vista
     *
     */
    public void oprimirCajasCompensacion() {

        if (css != null) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("numeroDocu",
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName())
                                            .toString());
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString());
            parametros.put("codigoPersona", registro.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName())
                            .toString());
            parametros.put("rid", css);

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.NATSUBCAJAS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton RiesgosProfesionales en la
     * vista
     *
     */
    public void oprimirRiesgosProfesionales() {
        if (css != null) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.CODIGO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.SEGURIDAD
                            .getValue(), "0");

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.NAT_SUBARPS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Pensiones en la vista
     *
     */
    public void oprimirPensiones() {
        if (css != null) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.CODIGO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.NAT_SUB_PENSION_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton FondosCesantias en la
     * vista
     *
     */
    public void oprimirFondosCesantias() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.CODIGO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));

            parametros.put(NatdatospersonalesControladorEnum.SEGURIDAD
                            .getValue(), "2");

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.NAT_SUBARPS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Vacaciones en la vista
     *
     */
    public void oprimirVacaciones() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.IDEMPLEADO
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString());
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.VACACIONESHV_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Incapacidades en la vista
     *
     */
    public void oprimirIncapacidades() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.IDEMPLEADO
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString());
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.INCAPACIDADESHV_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton FamiliaresBeneficiarios en
     * la vista
     *
     */
    public void oprimirFamiliaresBeneficiarios() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName())
                                            .toString());
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString());
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.FAMILIARES_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton MedicinaPrepagada en la
     * vista
     *
     */
    public void oprimirMedicinaPrepagada() {
        if (css != null) {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));
            Direccionador direccionador = new Direccionador();
            direccionador
                            .setNumForm(Integer.toString(
                                            GeneralCodigoFormaEnum.SUB_MEDICINAPREPAGADA_CONTROLADOR
                                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cesantias en la vista
     *
     */
    public void oprimirCesantias() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {
            try {
                String fecha = SysmanFunciones.validarCampoVacio(
                                registro.getCampos(), "FECHA_CESANTIAS") ? ""
                                    : SysmanFunciones.convertirAFechaCadena(
                                                    (Date) registro.getCampos()
                                                                    .get("FECHA_CESANTIAS"));

                Map<String, Object> parametros = new HashMap<>();
                parametros.put("rid", css);
                parametros.put("fecha", fecha);
                parametros.put("idDeEmpleado",
                                registro.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName())
                                                .toString());
                Direccionador direccionador = new Direccionador();
                direccionador.setNumForm(String.valueOf(
                                GeneralCodigoFormaEnum.CESANTIASHVS_CONTROLADOR
                                                .getCodigo()));
                direccionador.setParametros(parametros);
                SessionUtil.redireccionarForma(direccionador,
                                SessionUtil.getModulo());
            }
            catch (ParseException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Quinquenios en la vista
     *
     */
    public void oprimirQuinquenios() {

        if (css != null) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString());
            parametros.put(NatdatospersonalesControladorEnum.CODIGO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));

            Direccionador direccionador = new Direccionador();
            direccionador
                            .setNumForm(Integer.toString(
                                            GeneralCodigoFormaEnum.NAT_SUB_QUINQUENIO_CONTROLADOR
                                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Salud en la vista
     *
     */
    public void oprimirSalud() {
        // <CODIGO_DESARROLLADO>

        if (css != null) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));

            parametros.put(NatdatospersonalesControladorEnum.CODIGO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));

            parametros.put(NatdatospersonalesControladorEnum.SEGURIDAD
                            .getValue(), "1");

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.NAT_SUBARPS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MENSAJE_ALERTA_BOTONES));
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    public void camposVisibles() {

        try {
            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "VISUALIZAR DATOS LIBRETA MILITAR",
                            SessionUtil.getModulo(), new Date(), false))) {
                libMilitarVisible = true;
            }
            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "VISUALIZAR CERTIFICADO JUDICIAL",
                            SessionUtil.getModulo(), new Date(), false))) {
                certJudicialVisible = true;
            }
            
            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                    "PERMITE ASIGNAR ANEXOS",
                    SessionUtil.getModulo(), new Date(), false))) {
            	asignarAnexos = true;
    }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public boolean campoCodigoCiudadBanco() {

        boolean codigocta = false;

        HashMap<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put("BANCO", registro.getCampos().get("BANCO"));

        Registro registroVar = null;
        try {
            registroVar = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            NatdatospersonalesControladorUrlEnum.URL20269
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametros));

            codigocta = (registroVar != null
                ? (boolean) registroVar.getCampos().get("IND_CODCIUDADCTA")
                : false);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (codigocta && SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "CIUDAD_CUENTA")) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4128"));

            return false;
        }
        else {
            return true;
        }

    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        if ((rid != null) && !rid.isEmpty()) {
            cargarRegistro(rid, ACCION_MODIFICAR);
        }
        camposVisibles();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        if (!accion.equals(ACCION_INSERTAR)) {
            totalExperienciaDias = Integer
                            .toString(calcularTotalDiasExperiencia());

            totalExperienciaMeses = Integer
                            .toString(calcularTotalMesesExperiencia());

            totalExperienciaAnios = Integer
                            .toString(calcularTotalAniosExperiencia());

            cargarListanumeroDocumento();
            cargarFoto();
        }
        else {
            totalExperienciaDias = "";
            totalExperienciaMeses = "";
            totalExperienciaAnios = "";
        }
        // </CODIGO_DESARROLLADO>
    }

    private int calcularTotalDiasExperiencia() {
        int diasPb = Integer.parseInt(SysmanFunciones
                        .nvl(registro.getCampos().get("DIASPB"), "0")
                        .toString());

        int diasPv = Integer.parseInt(SysmanFunciones
                        .nvl(registro.getCampos().get("DIASPV"), "0")
                        .toString());

        int diasTi = Integer.parseInt(SysmanFunciones
                        .nvl(registro.getCampos().get("DIASTI"), "0")
                        .toString());
        
        int totalDias = diasPb + diasPv + diasTi;
        sumaExperienciaMeses = 0;
        
        if(totalDias >= 30)
        {
        	sumaExperienciaMeses = (int) Math.floor(totalDias/30);
        	totalDias =  totalDias%30;
        }

        return totalDias;
    }

    private int calcularTotalMesesExperiencia() {
        int mesPb = Integer.parseInt(SysmanFunciones
                        .nvl(registro.getCampos().get("MESPB"), "0")
                        .toString());
        int mesPv = Integer.parseInt(SysmanFunciones
                        .nvl(registro.getCampos().get("MESPV"), "0")
                        .toString());
        int mesTi = Integer.parseInt(SysmanFunciones
                        .nvl(registro.getCampos().get("MESTI"), "0")
                        .toString());

        int totalMeses = mesPb + mesPv + mesTi + sumaExperienciaMeses;
        sumaExperienciaAnios = 0;
        if(totalMeses >=12)
        {
        	sumaExperienciaAnios = (int) Math.floor(totalMeses/12);
        	totalMeses = totalMeses%12;
        }

        return totalMeses;
    }

    private int calcularTotalAniosExperiencia() {

        int anioPb = Integer.parseInt(SysmanFunciones
                        .nvl(registro.getCampos().get("ANOPB"), "0")
                        .toString());

        int anioPv = Integer.parseInt(SysmanFunciones
                        .nvl(registro.getCampos().get("ANOPV"), "0")
                        .toString());

        int anioTi = Integer.parseInt(SysmanFunciones
                        .nvl(registro.getCampos().get("ANOTI"), "0")
                        .toString());

        int totalMeses = anioPb + anioPv + anioTi + sumaExperienciaAnios;

        return totalMeses;
    }
    
    public void cambiarFondos() {
    	try {
    		Long idEmpleado = Long.parseLong(SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.CODIGO.getName())));
    		ejbHojasDeVidaCero.actualizarFondosHV(compania, idEmpleado);
    	} catch (SystemException e) {
    		 logger.error(e.getMessage(), e);
             JsfUtil.agregarMensajeError(e.getMessage());
    	}
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);

        registro.getCampos().remove("SS_FECHVINC");

        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        registro.getCampos().put("INHABILIDADES", false);

        registro.getCampos().put("FIRMAJEFEPER", false);

        registro.getCampos().put("CODIGO", "0");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

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

        if (campoCodigoCiudadBanco()) {
            registro.getCampos().remove(
                            GeneralParameterEnum.ID_DE_EMPLEADO.getName());

            registro.getCampos().remove("SS_FECHVINC");

            if (accion.equals(ACCION_MODIFICAR)) {
                registro.getCampos().remove(
                                GeneralParameterEnum.COMPANIA.getName());

                registro.getCampos().put("INHABILIDADES", false);

                registro.getCampos().put("FIRMAJEFEPER", false);

            }
            return true;
        }
        else {
            return false;
        }

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
     * Retorna la variable totalExperienciaAnios
     *
     * @return totalExperienciaAnios
     */
    public String getTotalExperienciaAnios() {
        return totalExperienciaAnios;
    }

    /**
     * Asigna la variable totalExperienciaAnios
     *
     * @param totalExperienciaAnios
     * Variable a asignar en totalExperienciaAnios
     */
    public void setTotalExperienciaAnios(String totalExperienciaAnios) {
        this.totalExperienciaAnios = totalExperienciaAnios;
    }

    /**
     * Retorna la variable totalExperienciaMeses
     *
     * @return totalExperienciaMeses
     */
    public String getTotalExperienciaMeses() {
        return totalExperienciaMeses;
    }

    /**
     * Asigna la variable totalExperienciaMeses
     *
     * @param totalExperienciaMeses
     * Variable a asignar en totalExperienciaMeses
     */
    public void setTotalExperienciaMeses(String totalExperienciaMeses) {
        this.totalExperienciaMeses = totalExperienciaMeses;
    }

    /**
     * Retorna la variable totalExperienciaDias
     *
     * @return totalExperienciaDias
     */
    public String getTotalExperienciaDias() {
        return totalExperienciaDias;
    }

    /**
     * Asigna la variable totalExperienciaDias
     *
     * @param totalExperienciaDias
     * Variable a asignar en totalExperienciaDias
     */
    public void setTotalExperienciaDias(String totalExperienciaDias) {
        this.totalExperienciaDias = totalExperienciaDias;
    }

    // <SET_GET_ATRIBUTOS>
    public String getPaisNcto() {
        return paisNcto;
    }

    public void setPaisNcto(String paisNcto) {
        this.paisNcto = paisNcto;
    }

    public String getDptoNcto() {
        return dptoNcto;
    }

    public void setDptoNcto(String dptoNcto) {
        this.dptoNcto = dptoNcto;
    }

    public String getPaisReside() {
        return paisReside;
    }

    public void setPaisReside(String paisReside) {
        this.paisReside = paisReside;
    }

    public String getDptoReside() {
        return dptoReside;
    }

    public void setDptoReside(String dptoReside) {
        this.dptoReside = dptoReside;
    }

    public String getPaisLabora() {
        return paisLabora;
    }

    public void setPaisLabora(String paisLabora) {
        this.paisLabora = paisLabora;
    }

    public String getDptoLabora() {
        return dptoLabora;
    }

    public void setDptoLabora(String dptoLabora) {
        this.dptoLabora = dptoLabora;
    }

    public String getPaisExpdicion() {
        return paisExpdicion;
    }

    public void setPaisExpdicion(String paisExpdicion) {
        this.paisExpdicion = paisExpdicion;
    }

    public String getDptoExpdicion() {
        return dptoExpdicion;
    }

    public void setDptoExpdicion(String dptoExpdicion) {
        this.dptoExpdicion = dptoExpdicion;
    }

    public boolean getLibMilitarVisible() {
        return libMilitarVisible;
    }

    public void setLibMilitarVisible(boolean libMilitarVisible) {
        this.libMilitarVisible = libMilitarVisible;
    }

    public boolean getCertJudicialVisible() {
        return certJudicialVisible;
    }

    public void setCertJudicialVisible(boolean certJudicialVisible) {
        this.certJudicialVisible = certJudicialVisible;
    }

    public boolean isDialgDependiente() {
        return dialgDependiente;
    }

    public void setDialgDependiente(boolean dialgDependiente) {
        this.dialgDependiente = dialgDependiente;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaPAISNCTO
     *
     * @return listaPAISNCTO
     */
    public List<Registro> getListaPAISNCTO() {
        return listaPAISNCTO;
    }

    /**
     * Asigna la lista listaPAISNCTO
     *
     * @param listaPAISNCTO
     * Variable a asignar en listaPAISNCTO
     */
    public void setListaPAISNCTO(List<Registro> listaPAISNCTO) {
        this.listaPAISNCTO = listaPAISNCTO;
    }

    /**
     * Retorna la lista listaDEPTONCTO
     *
     * @return listaDEPTONCTO
     */
    public List<Registro> getListaDEPTONCTO() {
        return listaDEPTONCTO;
    }

    /**
     * Asigna la lista listaDEPTONCTO
     *
     * @param listaDEPTONCTO
     * Variable a asignar en listaDEPTONCTO
     */
    public void setListaDEPTONCTO(List<Registro> listaDEPTONCTO) {
        this.listaDEPTONCTO = listaDEPTONCTO;
    }

    /**
     * Retorna la lista listaMUNICIPIONCTO
     *
     * @return listaMUNICIPIONCTO
     */
    public List<Registro> getListaMUNICIPIONCTO() {
        return listaMUNICIPIONCTO;
    }

    /**
     * Asigna la lista listaMUNICIPIONCTO
     *
     * @param listaMUNICIPIONCTO
     * Variable a asignar en listaMUNICIPIONCTO
     */
    public void setListaMUNICIPIONCTO(List<Registro> listaMUNICIPIONCTO) {
        this.listaMUNICIPIONCTO = listaMUNICIPIONCTO;
    }

    /**
     * Retorna la lista listaPAISRESIDE
     *
     * @return listaPAISRESIDE
     */
    public List<Registro> getListaPAISRESIDE() {
        return listaPAISRESIDE;
    }

    /**
     * Asigna la lista listaPAISRESIDE
     *
     * @param listaPAISRESIDE
     * Variable a asignar en listaPAISRESIDE
     */
    public void setListaPAISRESIDE(List<Registro> listaPAISRESIDE) {
        this.listaPAISRESIDE = listaPAISRESIDE;
    }

    /**
     * Retorna la lista listapaisLabora
     *
     * @return listapaisLabora
     */
    public List<Registro> getListapaisLabora() {
        return listapaisLabora;
    }

    /**
     * Asigna la lista listapaisLabora
     *
     * @param listapaisLabora
     * Variable a asignar en listapaisLabora
     */
    public void setListapaisLabora(List<Registro> listapaisLabora) {
        this.listapaisLabora = listapaisLabora;
    }

    /**
     * Retorna la lista listadctoIdentidad
     *
     * @return listadctoIdentidad
     */
    public List<Registro> getListadctoIdentidad() {
        return listadctoIdentidad;
    }

    /**
     * Asigna la lista listadctoIdentidad
     *
     * @param listadctoIdentidad
     * Variable a asignar en listadctoIdentidad
     */
    public void setListadctoIdentidad(List<Registro> listadctoIdentidad) {
        this.listadctoIdentidad = listadctoIdentidad;
    }

    /**
     * Retorna la lista listaDEPTORESIDE
     *
     * @return listaDEPTORESIDE
     */
    public List<Registro> getListaDEPTORESIDE() {
        return listaDEPTORESIDE;
    }

    /**
     * Asigna la lista listaDEPTORESIDE
     *
     * @param listaDEPTORESIDE
     * Variable a asignar en listaDEPTORESIDE
     */
    public void setListaDEPTORESIDE(List<Registro> listaDEPTORESIDE) {
        this.listaDEPTORESIDE = listaDEPTORESIDE;
    }

    /**
     * Retorna la lista listadepartamentoLabora
     *
     * @return listadepartamentoLabora
     */
    public List<Registro> getListadepartamentoLabora() {
        return listadepartamentoLabora;
    }

    /**
     * Asigna la lista listadepartamentoLabora
     *
     * @param listadepartamentoLabora
     * Variable a asignar en listadepartamentoLabora
     */
    public void setListadepartamentoLabora(
        List<Registro> listadepartamentoLabora) {
        this.listadepartamentoLabora = listadepartamentoLabora;
    }

    /**
     * Retorna la lista listaMUNICIPIORESIDE
     *
     * @return listaMUNICIPIORESIDE
     */
    public List<Registro> getListaMUNICIPIORESIDE() {
        return listaMUNICIPIORESIDE;
    }

    /**
     * Asigna la lista listaMUNICIPIORESIDE
     *
     * @param listaMUNICIPIORESIDE
     * Variable a asignar en listaMUNICIPIORESIDE
     */
    public void setListaMUNICIPIORESIDE(List<Registro> listaMUNICIPIORESIDE) {
        this.listaMUNICIPIORESIDE = listaMUNICIPIORESIDE;
    }

    /**
     * Retorna la lista listaciudadLabora
     *
     * @return listaciudadLabora
     */
    public List<Registro> getListaciudadLabora() {
        return listaciudadLabora;
    }

    /**
     * Asigna la lista listaciudadLabora
     *
     * @param listaciudadLabora
     * Variable a asignar en listaciudadLabora
     */
    public void setListaciudadLabora(List<Registro> listaciudadLabora) {
        this.listaciudadLabora = listaciudadLabora;
    }

    /**
     * Retorna la lista listaPAISEXTRANJERO
     *
     * @return listaPAISEXTRANJERO
     */
    public List<Registro> getListaPAISEXTRANJERO() {
        return listaPAISEXTRANJERO;
    }

    /**
     * Asigna la lista listaPAISEXTRANJERO
     *
     * @param listaPAISEXTRANJERO
     * Variable a asignar en listaPAISEXTRANJERO
     */
    public void setListaPAISEXTRANJERO(List<Registro> listaPAISEXTRANJERO) {
        this.listaPAISEXTRANJERO = listaPAISEXTRANJERO;
    }

    /**
     * Retorna la lista listaDptoExpedidaCedula
     *
     * @return listaDptoExpedidaCedula
     */
    public List<Registro> getListaDptoExpedidaCedula() {
        return listaDptoExpedidaCedula;
    }

    /**
     * Asigna la lista listaDptoExpedidaCedula
     *
     * @param listaDptoExpedidaCedula
     * Variable a asignar en listaDptoExpedidaCedula
     */
    public void setListaDptoExpedidaCedula(
        List<Registro> listaDptoExpedidaCedula) {
        this.listaDptoExpedidaCedula = listaDptoExpedidaCedula;
    }

    /**
     * Retorna la lista listaExpedida
     *
     * @return listaExpedida
     */
    public List<Registro> getListaExpedida() {
        return listaExpedida;
    }

    /**
     * Asigna la lista listaExpedida
     *
     * @param listaExpedida
     * Variable a asignar en listaExpedida
     */
    public void setListaExpedida(List<Registro> listaExpedida) {
        this.listaExpedida = listaExpedida;
    }

    /**
     * Retorna la lista listaESTADOCIVIL
     *
     * @return listaESTADOCIVIL
     */
    public List<Registro> getListaESTADOCIVIL() {
        return listaESTADOCIVIL;
    }

    /**
     * Asigna la lista listaESTADOCIVIL
     *
     * @param listaESTADOCIVIL
     * Variable a asignar en listaESTADOCIVIL
     */
    public void setListaESTADOCIVIL(List<Registro> listaESTADOCIVIL) {
        this.listaESTADOCIVIL = listaESTADOCIVIL;
    }

    /**
     * Retorna la lista listafondoSindicato
     *
     * @return listafondoSindicato
     */
    public List<Registro> getListafondoSindicato() {
        return listafondoSindicato;
    }

    /**
     * Asigna la lista listafondoSindicato
     *
     * @param listafondoSindicato
     * Variable a asignar en listafondoSindicato
     */
    public void setListafondoSindicato(List<Registro> listafondoSindicato) {
        this.listafondoSindicato = listafondoSindicato;
    }

    /**
     * Retorna la lista listaNumeroPatronal
     *
     * @return listaNumeroPatronal
     */
    public List<Registro> getListaNumeroPatronal() {
        return listaNumeroPatronal;
    }

    /**
     * Asigna la lista listaNumeroPatronal
     *
     * @param listaNumeroPatronal
     * Variable a asignar en listaNumeroPatronal
     */
    public void setListaNumeroPatronal(List<Registro> listaNumeroPatronal) {
        this.listaNumeroPatronal = listaNumeroPatronal;
    }

    /**
     * Retorna la lista listaidDeTipo
     *
     * @return listaidDeTipo
     */
    public List<Registro> getListaidDeTipo() {
        return listaidDeTipo;
    }

    /**
     * Asigna la lista listaidDeTipo
     *
     * @param listaidDeTipo
     * Variable a asignar en listaidDeTipo
     */
    public void setListaidDeTipo(List<Registro> listaidDeTipo) {
        this.listaidDeTipo = listaidDeTipo;
    }

    /**
     * Retorna la lista listaSEDE
     *
     * @return listaSEDE
     */
    public List<Registro> getListaSEDE() {
        return listaSEDE;
    }

    /**
     * Asigna la lista listaSEDE
     *
     * @param listaSEDE
     * Variable a asignar en listaSEDE
     */
    public void setListaSEDE(List<Registro> listaSEDE) {
        this.listaSEDE = listaSEDE;
    }

    public List<Registro> getListaRegimen() {
        return listaRegimen;
    }

    public void setListaRegimen(List<Registro> listaRegimen) {
        this.listaRegimen = listaRegimen;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaBanco
     *
     * @return listaBanco
     */
    public RegistroDataModelImpl getListaBanco() {
        return listaBanco;
    }

    /**
     * Asigna la lista listaBanco
     *
     * @param listaBanco
     * Variable a asignar en listaBanco
     */
    public void setListaBanco(RegistroDataModelImpl listaBanco) {
        this.listaBanco = listaBanco;
    }

    /**
     * Retorna la lista listacentroCosto
     *
     * @return listacentroCosto
     */
    public RegistroDataModelImpl getListacentroCosto() {
        return listacentroCosto;
    }

    /**
     * Asigna la lista listacentroCosto
     *
     * @param listacentroCosto
     * Variable a asignar en listacentroCosto
     */
    public void setListacentroCosto(RegistroDataModelImpl listacentroCosto) {
        this.listacentroCosto = listacentroCosto;
    }

    /**
     * Retorna la lista listaubicacionTrabajo
     *
     * @return listaubicacionTrabajo
     */
    public RegistroDataModelImpl getListaubicacionTrabajo() {
        return listaubicacionTrabajo;
    }

    /**
     * Asigna la lista listaubicacionTrabajo
     *
     * @param listaubicacionTrabajo
     * Variable a asignar en listaubicacionTrabajo
     */
    public void setListaubicacionTrabajo(
        RegistroDataModelImpl listaubicacionTrabajo) {
        this.listaubicacionTrabajo = listaubicacionTrabajo;
    }

    /**
     * Retorna la lista listanumeroDocumento
     *
     * @return listanumeroDocumentol
     */
    public RegistroDataModelImpl getListanumeroDocumento() {
        return listanumeroDocumento;
    }

    /**
     * Asigna la lista listanumeroDocumento
     *
     * @param listanumeroDocumento
     * Variable a asignar en listanumeroDocumento
     */
    public void setListanumeroDocumento(
        RegistroDataModelImpl listanumeroDocumento) {
        this.listanumeroDocumento = listanumeroDocumento;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    private boolean validarEstadosPersonal() {
        boolean rta = false;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                        .getName()));
        param.put(GeneralParameterEnum.SUCURSAL.getName(),
                        registro.getCampos().get(GeneralParameterEnum.SUCURSAL
                                        .getName()));

        try {
            Registro reg = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatdatospersonalesControladorUrlEnum.URL20267
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));

            if (reg != null) {
                int cantidad = Integer.parseInt(
                                reg.getCampos().get("CANTIDAD").toString());
                if (cantidad > 1) {

                    rta = true;
                }
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return rta;
    }
    
    
    /**
     * Metodo Carga la foto de perfil desde el sistema de archivos. 
     * 
     * Se crea un mapa de parámetros para consultar el registro y obtener 
     * la ruta de la imagen. Si la ruta es válida y el archivo existe, 
     * se codifica en base64 para su uso en la interfaz. 
     * Si la imagen no existe o la ruta es nula, se asigna un valor vacío 
     * a la variable de la foto de perfil. 
     * 
     * Maneja excepciones de entrada/salida y asegura el cierre del flujo 
     * de entrada en un bloque `finally`.
     */
    public void cargarFoto() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.MODULO.getName(), SessionUtil.getModulo());
        param.put("NIVEL_AGRUPAMIENTO", 20);
        param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get(GeneralParameterEnum.NUMERO_DCTO.getName()));

        InputStream archivo = null;
        fotoPerfil = null;
        try {
            Registro rs = RegistroConverter.toRegistro(
                requestManager.get(UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                        NatdatospersonalesControladorUrlEnum.URL20271.getValue())
                    .getUrl(), param));

            // Verificar si el campo "RUTA" existe y no es nulo
            if (rs != null) {
            	// Obtiene la ruta de la imagen desde el registro y crea un objeto File, manejando posibles valores nulos convirtiendolo en un string.
                String rutaBanner = SysmanFunciones.nvlStr(SysmanFunciones.toString(rs.getCampos().get("RUTA")), "").toString();
                File ficheroImagen = new File(rutaBanner);

                if (!ficheroImagen.exists()) {
                    throw new IOException("El archivo no se encontró: " + rutaBanner);
                }
                
                // asigna la ruta de la imagen a la variable: fotoPerfil
                archivo = new FileInputStream(ficheroImagen);
                fotoPerfil = JsfUtil.encodeImage(IOUtils.toByteArray(archivo));
               
                //ejecuta la expecion de javascrpit para eliminar el parametro: pfdrid_c=true
                JsfUtil.ejecutarJavaScript("cargarImagen('FR1519_nuevo:TS66:IM1497')");
                
            } else {
                logger.warn("El campo 'RUTA' no está disponible o es nulo.");
                fotoPerfil = ""; // Asignar un valor por defecto cuando se elimine la foto 
            }
        } catch (SystemException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error al procesar el archivo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (archivo != null) {
                try {
                    archivo.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar el archivo: " + e.getMessage());
                }
            }
        }
    }


    
	/**
	 * @return the fotoPerfil
	 */
	public String getFotoPerfil() {
		return fotoPerfil;
	}

	/**
	 * @param fotoPerfil the fotoPerfil to set
	 */
	public void setFotoPerfil(String fotoPerfil) {
		this.fotoPerfil = fotoPerfil;
	}

	public boolean isAsignarAnexos() {
		return asignarAnexos;
	}

	public void setAsignarAnexos(boolean asignarAnexos) {
		this.asignarAnexos = asignarAnexos;
	}

	public int getSumaExperienciaMeses() {
		return sumaExperienciaMeses;
	}

	public void setSumaExperienciaMeses(int sumaExperienciaMeses) {
		this.sumaExperienciaMeses = sumaExperienciaMeses;
	}

	public int getSumaExperienciaAnios() {
		return sumaExperienciaAnios;
	}

	public void setSumaExperienciaAnios(int sumaExperienciaAnios) {
		this.sumaExperienciaAnios = sumaExperienciaAnios;
	}

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
