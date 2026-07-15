/*-
 * FacturasControlador.java
 *
 * 1.0
 *
 * 20/09/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
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
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCuatroRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosSieteRemote;
import com.sysman.serviciospublicos.enums.FacturasControladorEnum;
import com.sysman.serviciospublicos.enums.FacturasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador para la vista del formulario de consulta de
 * facturaciï¿½n (Factura).
 *
 * @author vmolano
 * @version 1.0, 20/09/2016
 * 
 * @modifier amonroy
 * @version 2, 30/05/2017 Proceso de Refactoring e implementacion de
 * EJBs para consulta de parametros
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Se cambia el llamado del codigo del
 * formulario
 * 
 * @modifier amonroy
 * @version 4, 21/09/2017 Se adiciona la funcionalidad al oprimir los
 * botones "Vista Preliminar", "Imprimir" y "Tarifas"
 * 
 * @modifier vmolano
 * @version 5, 18/10/2017 se remueve el campo MODIFICA del registro
 * del subFacturado. Se actualizan los titulos de los subformularios,
 * se cargan los datos de auditoria.
 */
@ManagedBean
@ViewScoped
public class FacturasControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String cNombre;
    private final String cValorFacturado;
    private final String cCodigoInterno;
    private final String cConcepto;
    private final String cDeuda;

    private int indiceSubfacturado;
    private final String strTB1732;
    private final String strCodigoRuta;
    private final String strCodigoRuta1;
    private final String strCodigoRuta2;
    private final String strBancoPerProceso;
    private final String strBancoPerProceso1;
    private final String strLectura;
    private final String strValorAseo;
    private final String strComentarios;
    private final String strCodigo;
    private final String strSpFacturado;
    private final String strPeriodo;
    private final String strPeriodo1;
    private final String strCiclo;
    private final String strChapetas;
    private final String strDesdeSuscriptor;
    private final String strPeriodosNoCobroFac;
    private final String strPeriodosNoCobroFin;
    private final String strMsmRegistroModificado;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Almacena el ciclo en el que se encuenta el suscriptor.
     */
    private String cicloActual;

    /**
     * Almacena el aï¿½o en el que se encuentra el suscriptor.
     */
    private String anoActual;

    /**
     * Almacena el periodo en el que se encuentra el suscriptor.
     */
    private String periodoActual;

    /**
     * Variable en la que se almacena el nombre del periodo anterior
     * del suscriptor.
     */
    private String nombrePeriodoAnterior;

    /**
     * Variable que recibe el cï¿½digo de ruta del suscriptor actual.
     */
    private String codigoRutaActual;

    /**
     * Variable para almacenar el uso del suscriptor actual.
     */
    private String usoActual;

    /**
     * Variable para el calculo de la deuda del periodo actual
     */
    private double deudaActual;

    /**
     * Variable para el calculo del total facturado del periodo
     * actual.
     */
    private double totalFacturadoActual;

    /**
     * Variable para calcular el total del periodo incluido convenios
     * y tercerizado
     */
    private double granTotalActual;

    /**
     * Variable para calcular la deuda del periodo anterior
     */
    private double deudaAnterior;

    /**
     * Variable para calcular el facturado del periodo anterior
     */
    private double totalFacturadoAnterior;

    /**
     * Variable para calcular el total del periodo anterior incluidos
     * convenios y tercerizado
     */
    private double granTotalAnterior;

    /**
     * Variable para el total de aseo en la pestaï¿½a de subsidios
     */
    private double totalAseoSubsidios;

    /**
     * Define si la entidad maneja resoluciï¿½n 720.
     */
    private boolean maneja720;

    /**
     * Define si la entidad maneja unidades adicionales de aseo.
     */
    private boolean manejaUnidades;

    /**
     * Define si la entidad tiene proceso de desactivar aseo urbano.
     */
    private boolean desactivaAseoUrbano;

    /**
     * Nombre que emplea la entidad para referirse al servicio de
     * acueducto.
     */
    private String nombreAcueducto;

    /**
     * Nombre que emplea la entidad para referirse al servicio de
     * alcantarillado.
     */
    private String nombreAlcantarillado;

    /**
     * Nombre que emplea la entidad para referirse al servicio de
     * aseo.
     */
    private String nombreAseo;

    /**
     * Nombre que emplea la entidad para referirse al servicio de
     * alumbrado.
     */
    private String nombreAlumbrado;
    /**
     * Almacena el valor del campo FIMM en el registro(si el usuario
     * esta en terreno)
     */
    private String fimm;

    /**
     * Nï¿½mero de modulo actual.
     */
    private String modulo;
    /**
     * Almacena el valor que ha sido definido en el parametro
     * "NUMERO MAXIMO CONCEPTOS"
     */
    private String numeroConceptos;
    /**
     * Almacena el valor que ha sido definido en el parametro
     * "GRUPOS MODIFICADORES DEUDA"
     */
    private String grupoModificadorDeuda;

    /**
     *
     */
    private String unidadAcueducto;

    /**
     *
     */
    private String unidadAlcantarillado;
    /**
     * Indica si el usuario de ingreso puede modificar la deuda de los
     * conceptos, basandose en la informacion del parametro
     * "GRUPOS MODIFICADORES DEUDA"
     */
    private boolean modificaDeuda;
    /**
     * Indica si es posible la edicion en el subformulario
     * "SufFacturado"
     */
    private boolean activaEdicion;

    /**
     *
     */
    private boolean cambiarAcueducto;

    /**
     *
     */
    private boolean cambiarAlcantarillado;

    /**
     *
     */
    private boolean cambiarAseo;

    /**
     *
     */
    private boolean cambiarAlumbrado;

    /**
     *
     */
    private String periodoInicio720;

    /**
     *
     */
    private String cicloExcluido720;

    /**
     *
     */
    private String historicoProblemas;

    /**
     *
     */
    private boolean manejaRiego;

    /**
     *
     */
    private boolean aplicaRes151;

    /**
     *
     */
    private boolean desviaEnSitio;

    /**
     *
     */
    private String usoEspecialAlumbrado;

    /**
     *
     */
    private boolean separarAlumbrado;

    /**
     *
     */
    private boolean modificarUsoEstrato;

    /**
     *
     */
    private String usoAlumbrado;

    /**
     *
     */
    private String usoTasaCar;

    /**
     *
     */
    private String grupoModificador;

    /**
     *
     */
    private boolean visibleDeshabitado;

    /**
     *
     */
    private boolean permiteDesactivarDescuento;

    /**
     *
     */
    private boolean autorizaDesviacion;

    /**
     *
     */
    private boolean autorizaMicromedicion;

    /**
     *
     */
    private boolean manejaFechaCambioEstado;

    /**
     *
     */
    private boolean manejaTercerizado;

    /**
     *
     */
    private boolean manejaConceptosExternos;

    /**
     *
     */
    private boolean autorizaConvenios;

    /**
     *
     */
    private boolean cobrarAseoPorPeso;

    /**
     *
     */
    private boolean manejaRes351;

    /**
     *
     */
    private boolean manejaSubSobreDehabitados;

    /**
     *
     */
    private boolean borrarComentarios;

    /**
     *
     */
    private String lbIndicadorAcueducto;

    /**
     *
     */
    private String lbIndicadorAlcantarillado;

    /**
     *
     */
    private String lbIndicadorAseo;

    /**
     *
     */
    private String lbIndicadorAlumbrado;

    /**
     *
     */
    private String lbSubsidioAcueducto;

    /**
     *
     */
    private String lbSubsidioAlcantarillado;

    /**
     *
     */
    private String lbSubsidioAseo;

    /**
     *
     */
    private String lbUnidadAcueducto;

    /**
     *
     */
    private String lbUnidadAlcantarillado;

    /**
     *
     */
    private String lbAcueductoAbrev;

    /**
     *
     */
    private String lbAlcantarilladoAbrev;

    /**
     *
     */
    private String lbAseoAbrev;

    /**
     *
     */
    private String lbAlumbradoAbrev;
    /**
    *
    */
    private String lblDispoFinal;
    /**
    *
    */
    private String lblTrataLixivi;
    /**
    *
    */
    private boolean lblBaseAprovecha;

    /**
     *
     */
    private boolean esTotalizador;

    /**
     * Esta variable se valida desde la forma para determinar el
     * comportamiento del boton volver
     */
    private boolean varVolver;
    /**
     * Almacena el nombre del concepto que ha sido seleccionado en el
     * subformulario "SubFacturado"
     */
    private String nombreConcepto;
    /**
     * Almacena el valor del concepto que ha sido seleccionado en el
     * subformulario "SubFacturado"
     */
    private double valorConcepto;
    /**
     * Permite la visualizacion del dialogo actualizarChapetas
     */
    private boolean actualizarChapetasVisible;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Indicada si la compania con la que se esta trabajando posee
     * permisos para generar copia de la factura generada
     */
    private boolean autorizaFacturaPagada;

    Map<String, Object> ridUsuario;
    Map<String, Object> ridFinanciable;
    Map<String, Object> ridAbono;
    Map<String, Object> ridOrdenTrabajo;

    /**
     * Implementacion del EJB de EjbServiciosPublicosCeroRemote para
     * hacer el llamado a las funciones que se invocan dentro del
     * Controlador y se encuentran almacenadas en el paquete
     * PCK_SERVICIOS_PUBLICOS
     */
    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicosCero;
    /**
     * Implementacion del EJB de EjbServiciosPublicosDosRemote para
     * hacer el llamado a las funciones que se invocan dentro del
     * Controlador y se encuentran almacenadas en el paquete
     * PCK_SERVICIOS_PUBLICOS_COM2
     */
    @EJB
    private EjbServiciosPublicosDosRemote ejbServiciosPublicosDos;
    /**
     * Implementacion del EJB de EjbServiciosPublicosCuatroRemote para
     * hacer el llamado a las funciones que se invocan dentro del
     * Controlador y se encuentran almacenadas en el paquete
     * PCK_SERVICIOS_PUBLICOS_COM4
     */
    @EJB
    private EjbServiciosPublicosCuatroRemote ejbServiciosPublicosCuatro;
    /**
     * Implementacion del EJB de EjbServiciosPublicosSieteRemote para
     * hacer el llamado a las funciones que se invocan dentro del
     * Controlador y se encuentran almacenadas en el paquete
     * PCK_SERVICIOS_PUBLICOS_COM7
     */
    @EJB
    private EjbServiciosPublicosSieteRemote ejbServiciosPublicosSiete;
    /**
     * Implementacion del EJB de EjbServiciosPublicosOchoRemote para
     * hacer el llamado a las funciones que se invocan dentro del
     * Controlador y se encuentran almacenadas en el paquete
     * PCK_SERVICIOS_PUBLICOS_COM8
     */
    @EJB
    private EjbServiciosPublicosOchoRemote ejbServiciosPublicosOcho;
    /**
     * Implementacion del EJB de SysmanUtil para hacer el llamado a la
     * funcion FC_PAR
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaTipoCobro;
    private List<Registro> listaConceptoAnterior;
    private List<Registro> listaNombreAnterior;
    private List<Registro> listaCODTOTALIZADOR;
    private List<Registro> listaCmbTotMacro;
    private List<Registro> listaCmbUso;
    private List<Registro> listaCmbEstrato;
    private List<Registro> listaCmbEstratoAseo;
    private List<Registro> listaEstratoAlumbrado;
    private List<Registro> listaTasaCar;
    private List<Registro> listaAFORADOR;
    private List<Registro> listacmbFechaCambioEstado;
    private List<Registro> listaCmbarrio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaConcepto;
    private RegistroDataModelImpl listaConceptoE;
    private RegistroDataModelImpl listaNombre;
    private RegistroDataModelImpl listaNombreE;
    private String auxiliar;
    private RegistroDataModelImpl listatxtEmpresaAseo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaSubfacturado;
    private List<Registro> listaSubfacturadoant;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>

    private Registro registroSubSubFacturado;
    private Registro registroSubSubFacturadoAnt;
    private Registro registroSubFacturadoAux;

    // </DECLARAR_ADICIONALES>
    @SuppressWarnings("unchecked")
    public FacturasControlador() {
        super();
        compania = SessionUtil.getCompania();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cValorFacturado = "VALOR_FACTURADO";
        cCodigoInterno = "CODIGOINTERNO";
        cConcepto = GeneralParameterEnum.CONCEPTO.getName();
        cDeuda = GeneralParameterEnum.DEUDA.getName();

        strTB1732 = "TB_TB1732";
        strCodigoRuta1 = "codigoruta";
        strCodigoRuta = "codigoRuta";
        strBancoPerProceso = "bancoperproceso";
        strBancoPerProceso1 = "BANCOPERPROCESO";
        strLectura = "LECTURA";
        strValorAseo = "VALORASEO";
        strComentarios = "COMENTARIOS";
        strCodigo = GeneralParameterEnum.CODIGO.getName();
        strSpFacturado = "SP_FACTURADO";
        strCodigoRuta2 = GeneralParameterEnum.CODIGORUTA.getName();
        strPeriodo = GeneralParameterEnum.PERIODO.getName();
        strPeriodo1 = "periodo";
        strCiclo = "ciclo";
        strDesdeSuscriptor = "desdeSuscriptor";
        strChapetas = "CHAPETAS";
        strPeriodosNoCobroFac = "PERIODOSNOCOBROFAC";
        strPeriodosNoCobroFin = "PERIODOSNOCOBROFIN";
        strMsmRegistroModificado = "MSM_REGISTRO_MODIFICADO";
        indiceSubfacturado = -1;

        try {
            numFormulario = GeneralCodigoFormaEnum.FACTURA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            modulo = SessionUtil.getModulo();

            registroSubSubFacturado = new Registro(
                            new HashMap<String, Object>());
            registroSubSubFacturadoAnt = new Registro(
                            new HashMap<String, Object>());

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                cicloActual = parametrosEntrada.get(strCiclo).toString();
                anoActual = parametrosEntrada.get("ano").toString();
                periodoActual = parametrosEntrada.get(strPeriodo1).toString();

                usoAlumbrado = "99";

                ridUsuario = (Map<String, Object>) parametrosEntrada
                                .get("ridUsuario");
                ridFinanciable = (Map<String, Object>) parametrosEntrada
                                .get("ridFinanciable");
                ridAbono = (Map<String, Object>) parametrosEntrada
                                .get("ridAbono");
                ridOrdenTrabajo = (Map<String, Object>) parametrosEntrada
                                .get("ridOrdenTrabajo");
                rid = (Map<String, Object>) parametrosEntrada.get("rid");

            }
            else {
                SessionUtil.redireccionarMenu();
            }

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

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaConcepto();
        cargarListaConceptoE();
        cargarListaNombre();
        cargarListaNombreE();
        cargarListatxtEmpresaAseo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaTipoCobro();
        cargarListaConceptoAnterior();
        cargarListaNombreAnterior();

        cargarListaCmbUso();
        if (separarAlumbrado) {
            usoAlumbrado = usoEspecialAlumbrado;
            cargarListaEstratoAlumbrado();
        }
        cargarListaTasaCar();
        cargarListaAFORADOR();
        cargarListaCmbarrio();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubfacturado();
        cargarListaSubfacturadoant();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubfacturado = null;
        listaSubfacturadoant = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>

    }

    @PostConstruct
    public void inicializar() {
        tabla = FacturasControladorEnum.SP_USUARIO.getValue();
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(FacturasControladorEnum.CICLOACTUAL.getValue(),
                        cicloActual);
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturasControladorUrlEnum.URL001
                                                        .getValue());
        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturasControladorUrlEnum.URL002
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturasControladorUrlEnum.URL005
                                                        .getValue());
    }

    public void cargarListaSubfacturado() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FacturasControladorEnum.CICLOACTUAL.getValue(),
                            cicloActual);
            param.put(FacturasControladorEnum.CODIGORUTA2.getValue(), registro
                            .getCampos().get(strCodigoRuta2).toString());
            param.put(GeneralParameterEnum.ANO.getName(),
                            registro.getCampos().get("ANO").toString());
            param.put(GeneralParameterEnum.PERIODO.getName(),
                            registro.getCampos().get(strPeriodo).toString());
            param.put(GeneralParameterEnum.NUMERO.getName(), numeroConceptos);

            listaSubfacturado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL26016
                                                                            .getValue())
                                            .getUrl(),
                                            param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            strSpFacturado));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaSubfacturadoant() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FacturasControladorEnum.CICLOACTUAL.getValue(),
                            registro.getCampos().get("CICLO").toString());
            param.put(FacturasControladorEnum.CODIGORUTA2.getValue(), registro
                            .getCampos().get(strCodigoRuta2).toString());
            param.put(GeneralParameterEnum.ANO.getName(),
                            registro.getCampos().get("ANO").toString());
            param.put(GeneralParameterEnum.PERIODO.getName(),
                            registro.getCampos().get(strPeriodo).toString());

            listaSubfacturadoant = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL29241
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            strSpFacturado));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTipoCobro() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoCobro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL28449
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaConceptoAnterior() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaConceptoAnterior = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL28871
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNombreAnterior() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaNombreAnterior = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL29466
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCODTOTALIZADOR() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FacturasControladorEnum.CICLOACTUAL.getValue(), cicloActual);
        param.put(FacturasControladorEnum.CODIGORUTAACTUAL.getValue(),
                        codigoRutaActual);
        param.put(FacturasControladorEnum.TIPO_TOTALIZADOR.getValue(), "T");

        try {
            listaCODTOTALIZADOR = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL30109
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCmbTotMacro() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FacturasControladorEnum.CICLOACTUAL.getValue(), cicloActual);
        param.put(FacturasControladorEnum.CODIGORUTAACTUAL.getValue(),
                        codigoRutaActual);
        param.put(FacturasControladorEnum.TIPO_TOTALIZADOR.getValue(), "M");

        try {
            listaCmbTotMacro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL31017
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCmbUso() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCmbUso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL31957
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCmbEstrato() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FacturasControladorEnum.USO_ACTUAL.getValue(), usoActual);

        try {
            listaCmbEstrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL33983
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCmbEstratoAseo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FacturasControladorEnum.USO_ACTUAL.getValue(), usoActual);

        try {
            listaCmbEstratoAseo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL33983
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaEstratoAlumbrado() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FacturasControladorEnum.USO_ACTUAL.getValue(), usoAlumbrado);

        try {
            listaEstratoAlumbrado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL33983
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTasaCar() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FacturasControladorEnum.USO_ACTUAL.getValue(), usoTasaCar);

        try {
            listaTasaCar = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL33983
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAFORADOR() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAFORADOR = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL35536
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbFechaCambioEstado() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FacturasControladorEnum.CODIGORUTAACTUAL.getValue(),
                        codigoRutaActual);
        param.put(GeneralParameterEnum.ESTADO.getName(), "A");

        try {
            listacmbFechaCambioEstado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL36229
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCmbarrio() {
        try {
            listaCmbarrio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL37069
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaConcepto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturasControladorUrlEnum.URL37303
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), numeroConceptos);

        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
        nombreConcepto = null;
    }

    public void cargarListaConceptoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturasControladorUrlEnum.URL38112
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), numeroConceptos);

        listaConceptoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaNombre() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturasControladorUrlEnum.URL38919
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNombre = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaNombreE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturasControladorUrlEnum.URL39813
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNombreE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cancelarEdicionSubfacturadoant() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarEdicionSubfacturado() {
        // <CODIGO_DESARROLLADO>
        cargarListaSubfacturado();
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListatxtEmpresaAseo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturasControladorUrlEnum.URL40981
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatxtEmpresaAseo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubFacturado.getCampos().put(cConcepto, SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigo), ""));
        nombreConcepto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
        valorConcepto = Double.parseDouble(SysmanFunciones
                        .nvl(registroAux.getCampos().get("VALOR"), 0.0)
                        .toString());
        registroSubSubFacturado.getCampos().put(cNombre, nombreConcepto);
        registroSubSubFacturado.getCampos().put(cValorFacturado,
                        valorConcepto);
        registroSubSubFacturado.getCampos().put(cDeuda, 0.0);
    }

    public void seleccionarFilaConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigo), "")
                        .toString();
        nombreConcepto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
        valorConcepto = Double.parseDouble(SysmanFunciones
                        .nvl(registroAux.getCampos().get("VALOR"), 0.0)
                        .toString());
        registroSubSubFacturado.getCampos().put(cNombre, nombreConcepto);
        registroSubSubFacturado.getCampos().put(cValorFacturado,
                        valorConcepto);
    }

    public void seleccionarFilaNombre(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubFacturado.getCampos().put(cConcepto,
                        registroAux.getCampos().get(strCodigo));
    }

    public void seleccionarFilaNombreE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigo), "")
                        .toString();
    }

    public void seleccionarFilatxtEmpresaAseo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("EMPRESAASEOEXT",
                        registroAux.getCampos().get("ID"));
    }

    public void ejecutarrcVolver() {

        if ("740301".equals(SessionUtil.getMenuActual())
            || "740302".equals(SessionUtil.getMenuActual())) {
            if ((ridOrdenTrabajo != null) && !ridOrdenTrabajo.isEmpty()) {

                String[] campos = { "rid" };
                Object[] valores = { ridOrdenTrabajo };

                SessionUtil.redireccionarPorFormulario(modulo,
                                Integer.toString(
                                                GeneralCodigoFormaEnum.ORDENTRABAJOS_CONTROLADOR
                                                                .getCodigo()),
                                campos, valores, true);

            }
        }
        else {
            if ((ridFinanciable != null) && !ridFinanciable.isEmpty()) {

                String[] campos = { "ridFinanciable" };
                Object[] valores = { ridFinanciable };

                SessionUtil.redireccionarPorFormulario(modulo,
                                Integer.toString(
                                                GeneralCodigoFormaEnum.FINANCIABLESDEDEUDAS_CONTROLADOR
                                                                .getCodigo()),
                                campos, valores, true);

            }
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    /**
     * Evalua si la compania de ingreso posee permisos para realizar
     * copia de la factura y si ya se generó factura para el usuario
     * con el que se está trabajando
     * 
     * @return Verdadero si se cumplen las validaciones anteriormente
     * definidas
     */
    private boolean evaluarFacturaPaga() {
        boolean respuesta;
        if (autorizaFacturaPagada) {
            respuesta = true;
        }
        else {
            respuesta = SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            strBancoPerProceso1) ? true : false;
            if (!respuesta) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3612"));
            }
        }
        return respuesta;
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        if (evaluarFacturaPaga()) {
            // Evalua si la factura esta en terreno
            if ("P".equals(fimm)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3613"));
                return;
            }

            // Verifica que la fecha actual sea mayor a la fecha de
            // preparacion del ciclo
            if (evaluarDiferenciaFechas() < 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3614"));
                return;
            }

            // Direcciona a impresion Facturas
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(strCiclo, cicloActual);
            parametros.put("ano", anoActual);
            parametros.put(strPeriodo1, periodoActual);
            parametros.put("codigoInicial", codigoRutaActual);
            parametros.put("codigoFinal", codigoRutaActual);
            parametros.put("marca", 1);
            parametros.put("marcaIni", 1);
            parametros.put("noReciboInicial", obtenerConsecutivoReal());
            parametros.put(strDesdeSuscriptor, true);
            parametros.put("rid", css);
            parametros.put("nombreInicial",
                            registro.getCampos().get(cNombre));
            parametros.put("nombreFinal",
                            registro.getCampos().get(cNombre));

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.IMPRESIONFACTURAS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            modulo);

        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirVistaPrevia() {
        // <CODIGO_DESARROLLADO>
        // Evalua si se ha generado factura
        if (Integer.parseInt(
                        registro.getCampos().get("FACTURA")
                                        .toString()) == 0) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB3575"));
            return;
        }

        // Evalua si la factura esta en terreno
        if ("P".equals(fimm)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3576"));
            return;
        }

        if (autorizaFacturaPagada || SysmanFunciones.validarCampoVacio(
                        registro.getCampos(), strBancoPerProceso1)) {

            // Direcciona a impresion Facturas
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(strCiclo, cicloActual);
            parametros.put("ano", anoActual);
            parametros.put(strPeriodo1, periodoActual);
            parametros.put("codigoInicial", codigoRutaActual);
            parametros.put("codigoFinal", codigoRutaActual);
            parametros.put("marca", 2);
            parametros.put("marcaIni", 2);
            parametros.put("noReciboInicial", obtenerConsecutivoReal());
            parametros.put(strDesdeSuscriptor, true);
            parametros.put("rid", css);
            parametros.put("nombreInicial",
                            registro.getCampos().get(cNombre));
            parametros.put("nombreFinal",
                            registro.getCampos().get(cNombre));

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.IMPRESIONFACTURAS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            modulo);
        }
        else {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB3579"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdComentarios() {
        // <CODIGO_DESARROLLADO>
        // 1150
        String comentario = SysmanFunciones
                        .nvl(registro.getCampos().get(strComentarios), "")
                        .toString();
        String[] campos = { "ano", strPeriodo1, "comentario", "ridFactura" };
        Object[] valores = { anoActual, periodoActual, comentario,
                             registro.getLlave() };

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRM_COMENTARIOS_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control chapetas
     * 
     * Actualiza el valor del atributo "actualizarChapetasVisible"
     * para la visualizacion del dialogo "actualizarChapetas"
     * 
     */
    public void cambiarchapetas() {
        actualizarChapetasVisible = true;

        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioCmdComentarios(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        if (event.getObject() != null) {
            String comentariosActuales = event.getObject().toString();
            String comentarioAnterior = SysmanFunciones
                            .nvl(registro.getCampos().get(strComentarios), "")
                            .toString();
            if (!comentariosActuales.equals(comentarioAnterior)) {
                registro.getCampos().put(strComentarios, comentariosActuales);
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton btConvenios. Vuelve a
     * cargar el registro para que se reflejen los cambios en la
     * forma.
     * 
     */
    public void retornarFormulariobtConvenios() {
        // <CODIGO_DESARROLLADO>
        cargarRegistro(css, ACCION_MODIFICAR);
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormulariobtFinanciables(SelectEvent event) {
        if (event.getObject() != null) {
            cargarRegistro(css, ACCION_MODIFICAR);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control cmbAseoUrbano
     *
     * Se actualiza el total general quitanto el valor anterior y
     * aï¿½adiendo el nuevo. Se carga el valor de convenios al
     * registro.
     *
     */
    public void retornarFormulariocmbAseoUrbano(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        if (event.getObject() != null) {
            double nuevoValor = Double
                            .parseDouble(event.getObject().toString());
            double totalTercerizadoActual = Double
                            .parseDouble(String.valueOf(registro.getCampos()
                                            .get(strValorAseo)));

            granTotalActual = (granTotalActual - totalTercerizadoActual)
                + nuevoValor;
            registro.getCampos().put(strValorAseo, nuevoValor);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * actualizarChapetas en la vista
     *
     * Realiza el llamado a la funcion
     * "PCK_SERVICIOS_PUBLICOS.FC_ACTUALIZAAUDI" que registra la
     * informacion de cambios en la auditoria de usuario, para este
     * caso registra el cambio realizado en el campo CHAPETAS
     *
     */
    public void aceptaractualizarChapetas() {
        // <CODIGO_DESARROLLADO>
        boolean chapetas = Boolean.parseBoolean(
                        registro.getCampos().get(strChapetas).toString());

        try {
            ejbServiciosPublicosCero.insertarAuditoriaDelUsuario(compania,
                            Integer.parseInt(cicloActual),
                            codigoRutaActual,
                            chapetas,
                            !chapetas,
                            strChapetas,
                            "CHAPETAS_ANT",
                            periodoActual,
                            SessionUtil.getUser().getCodigo());
            agregarRegistroNuevo(false);
            actualizarChapetasVisible = false;
            cargarRegistro();
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Concepto en la fila
     * seleccionada dentro de la grilla
     * 
     * Actualiza el valor del campo "NOMBRE" cuando se selcciona un
     * nuevo concepto en el combo de Concepto
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarConceptoC(int rowNum) {
        // Para el cambio en una fila seleccionada (PARA
        // SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        listaSubfacturado.get(rowNum).getCampos().put(cNombre, nombreConcepto);
        listaSubfacturado.get(rowNum).getCampos().put(cValorFacturado,
                        valorConcepto);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdTarifas() {
        /*
         * Pendiente de migraciï¿½n por la implementaciï¿½n de RES 668
         * en fabrica. Se debe mostrar el mismo formulario de tarifas
         * desplegado desde \ARCHIVO\TARIFAS\ pero para solo lectura.
         * 
         * 21/09/2017 --> Se adiciona el redireccionamiento al
         * formulario "Tarifassp"
         */

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("cicloSuscriptor", cicloActual);
        parametros.put("anoSuscriptor", anoActual);
        parametros.put("periodoSuscriptor", periodoActual);
        parametros.put("uso", usoActual);
        parametros.put("estrato", registro.getCampos().get("ESTRATO"));
        parametros.put("ridSuscriptor", css);
        parametros.put(strDesdeSuscriptor, true);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.TARIFASSP_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador,
                        modulo);

    }

    public void oprimirComando378() {
        // pantalla

        archivoDescarga = null;
        String reporte = "001271PantallaConsultaFacturacion";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put(strCiclo, cicloActual);
            reemplazar.put(strCodigoRuta1, codigoRutaActual);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_GETUSER", SessionUtil.getUser());
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Date fechaActual = new Date();

            parametros.put("PR_FECHAG", SysmanFunciones
                            .convertirAFechaCadena(fechaActual, "dd/MM/yyyy"));
            parametros.put("PR_HORAG", SysmanFunciones
                            .convertirAFechaCadena(fechaActual, "HH:mm:ss"));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirCalcular() {
        /**
         * MZANGUNA 29/03/2017 Se adiciona llamado a funcion de
         * calculo
         */
        try {
            String respuesta;
            respuesta = ejbServiciosPublicosSiete.calcularFacturacion(compania,
                            Integer.parseInt(cicloActual),
                            codigoRutaActual,
                            codigoRutaActual,
                            false,
                            false,
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(respuesta);
            cargarRegistro(registro.getLlave(), "m");
            cargarListaSubfacturado();
        }

        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirSalir() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.cleanFlash();
        SessionUtil.redireccionarMenu();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtFinanciables() {
        // <CODIGO_DESARROLLADO>
        String codigoInterno = registro.getCampos().get(cCodigoInterno)
                        .toString();
        String bancoperproceso = registro.getCampos()
                        .get(strBancoPerProceso1) == null
                            ? ""
                            : registro.getCampos().get(strBancoPerProceso1)
                                            .toString();
        String periodosNoCobradosFac = registro.getCampos()
                        .get(strPeriodosNoCobroFac) == null
                            ? ""
                            : registro.getCampos().get(strPeriodosNoCobroFac)
                                            .toString();
        String lectura = registro.getCampos().get(strLectura) == null
            ? ""
            : registro.getCampos().get(strLectura).toString();
        String periodosNoCobroFin = registro.getCampos()
                        .get(strPeriodosNoCobroFin) == null
                            ? ""
                            : registro.getCampos().get(strPeriodosNoCobroFin)
                                            .toString();
        String numeroFactura = registro.getCampos().get("FACTURA").toString();

        String[] campos = { strCiclo, strCodigoRuta, "ano", strPeriodo1,
                            "codigoInterno", strBancoPerProceso,
                            "periodosNoCobradosFac", "txtFimm",
                            "numeroFactura", "lectura", "periodosNoCobroFin" };
        String[] valores = { cicloActual, codigoRutaActual,
                             anoActual,
                             periodoActual, codigoInterno,
                             bancoperproceso, periodosNoCobradosFac, fimm,
                             numeroFactura, lectura, periodosNoCobroFin };

        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FINANCIABLESFACTURAS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtSaldoCredito() {
        String notacredito = SysmanFunciones
                        .nvl(registro.getCampos().get("NOTACREDITO"), "")
                        .toString();
        String bancoperproceso = SysmanFunciones
                        .nvl(registro.getCampos().get(strBancoPerProceso1), " ")
                        .toString();
        String[] campos = { strCiclo, strCodigoRuta1, "ano",
                            strPeriodo1,
                            "notacredito", strBancoPerProceso };
        Object[] valores = { cicloActual, codigoRutaActual,
                             anoActual,
                             periodoActual, notacredito,
                             bancoperproceso };
        SessionUtil.cargarModalDatosFlashCerrar(Integer
                        .toString(GeneralCodigoFormaEnum.FACTURASALDOCREDITOS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos,
                        valores);
    }

    /**
     * Metodo usado para enviar parametros y redireccionar al
     * formulario Abonos
     */
    public void oprimirbtAbonos() {
        // <CODIGO_DESARROLLADO>

        boolean autorizarBorrado = (boolean) registro.getCampos()
                        .get("AUTORIZARBORRADO");

        String totFacturaPerActual = SysmanFunciones
                        .nvl(registro.getCampos().get("TOTFACTURAPERACTUAL"),
                                        "")
                        .toString();

        String bancoperproceso = SysmanFunciones
                        .nvl(registro.getCampos().get(strBancoPerProceso1), " ")
                        .toString();

        String codigoInternoActual = SysmanFunciones
                        .nvl(registro.getCampos().get(cCodigoInterno), "")
                        .toString();

        HashMap<String, Object> param = new HashMap<>();

        param.put(strCiclo, cicloActual);

        param.put(strCodigoRuta1, codigoRutaActual);

        param.put("ano", anoActual);

        param.put(strPeriodo1, periodoActual);

        param.put(strBancoPerProceso, bancoperproceso);

        param.put("codigoInterno", codigoInternoActual);

        param.put("txtFimm", fimm);

        param.put("autorizarBorrado", autorizarBorrado);

        param.put("totFacturaPerActual", totFacturaPerActual);

        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.ABONOSFACTURAS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);

        SessionUtil.redireccionarForma(direccionador, modulo);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtPQR() {

        if (historicoProblemas == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1631"));
            return;
        }

        String[] campos = { strCiclo, strCodigoRuta, "ano", strPeriodo1 };
        String[] valores = { cicloActual, codigoRutaActual, anoActual,
                             periodoActual };

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.PQRFACTURAS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos,
                        valores);
    }

    public void oprimirbtConvenios() {
        // <CODIGO_DESARROLLADO>

        String fechaPagoPerProceso = SysmanFunciones
                        .nvl(registro.getCampos().get("FECHAPAGOPERPROCESO"),
                                        "")
                        .toString();
        String[] campos = { strCiclo, strCodigoRuta1, "ano", strPeriodo1,
                            "fechaPagoPerProceso" };
        Object[] valores = { cicloActual, codigoRutaActual, anoActual,
                             periodoActual,
                             fechaPagoPerProceso };

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FACTURA_CONVENIOS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos,
                        valores);
        // </CODIGO_DESARROLLADO>+
    }

    public void oprimirbtOtros() {
        // <CODIGO_DESARROLLADO>
        String lectura = registro.getCampos().get(strLectura) == null
            ? ""
            : registro.getCampos().get(strLectura).toString();
        String[] campos = { strCiclo, strCodigoRuta, "ano",
                            strPeriodo1,
                            "lectura" };
        Object[] valores = { cicloActual, codigoRutaActual,
                             anoActual,
                             periodoActual, lectura };

        SessionUtil.cargarModalDatosFlashCerrar(Integer
                        .toString(GeneralCodigoFormaEnum.FACTURA_OTROS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtHistoria() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { strCiclo, strCodigoRuta, "ano", strPeriodo1,
                            "comentarios" };
        Object[] valores = { cicloActual, codigoRutaActual, anoActual,
                             periodoActual,
                             registro.getCampos().get(strComentarios) };

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.HISTORIAFACTURADOS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtUnidades() {
        // <CODIGO_DESARROLLADO>

        String[] campos = { strCiclo, strCodigoRuta };
        Object[] valores = { cicloActual, codigoRutaActual };

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.UNIDADESRESIDENCIALES_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmbAseoUrbano() {
        // <CODIGO_DESARROLLADO>
        String bancoperproceso = registro.getCampos()
                        .get(strBancoPerProceso1) == null
                            ? ""
                            : registro.getCampos()
                                            .get(strBancoPerProceso1)
                                            .toString();

        String[] campos = { strCodigoRuta, "bancoPerProceso", "ano",
                            strPeriodo1,
                            strCiclo };
        Object[] valores = { codigoRutaActual, bancoperproceso, anoActual,
                             periodoActual, cicloActual };

        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.ASEO_URBANO_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtDesvio() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { strCodigoRuta, strCiclo, "ano", strPeriodo1 };
        Object[] valores = { codigoRutaActual, cicloActual, anoActual,
                             periodoActual };

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.DESVIACIONHISTORIAS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirDetalleMicro() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { strCodigoRuta, strCiclo, "ano", strPeriodo1 };
        Object[] valores = { codigoRutaActual, cicloActual, anoActual,
                             periodoActual };

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.MICROMEDICIONHISTORIAS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubSubfacturado() {
        try {

            registroSubSubFacturado.getCampos()
                            .remove(GeneralParameterEnum.NOMBRE.getName());
            registroSubSubFacturado.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubSubFacturado.getCampos().put(
                            GeneralParameterEnum.CICLO.getName(), cicloActual);
            registroSubSubFacturado.getCampos().put(strCodigoRuta2,
                            registro.getCampos().get(strCodigoRuta2));
            registroSubSubFacturado.getCampos()
                            .put(GeneralParameterEnum.ANO.getName(), anoActual);
            registroSubSubFacturado.getCampos().put(
                            GeneralParameterEnum.PERIODO.getName(),
                            periodoActual);
            registroSubSubFacturado.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubSubFacturado.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            Parameter params = new Parameter();
            params.setFields(registroSubSubFacturado.getCampos());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FacturasControladorUrlEnum.URL58322
                                                            .getValue());
            requestManager.save(urlCreate.getUrl(),
                            urlCreate.getMetodo(),
                            params);

            cargarListaSubfacturado();
            calcularTotales();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1945"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubSubFacturado = new Registro(
                            new HashMap<String, Object>());
            nombreConcepto = null;
        }
    }

    public void editarRegSubSubfacturado(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.CICLO.getName());
            reg.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
            reg.getCampos().remove(GeneralParameterEnum.ANO.getName());
            reg.getCampos().remove(GeneralParameterEnum.PERIODO.getName());
            reg.getCampos().remove(FacturasControladorEnum.MODIFICA.getValue());

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FacturasControladorUrlEnum.URL59503
                                                            .getValue());

            int conteo = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(strMsmRegistroModificado));
            }
            actualizarDespuesRegSubSubfacturado(reg);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            cargarListaSubfacturado();
        }
    }

    /**
     * Ejecuta el procedimiento
     * PCK_SERVICIOS_PUBLICOS_COM8.PR_ACTUALIZARCONCEPTO en el que se
     * registran las modificaciones realizadas al valor de deuda o al
     * valor facturado en los conceptos
     * 
     * @param reg
     * Registro del subformulario Subfacturado en el que se estan
     * realizando las modificaciones
     */
    private void actualizarDespuesRegSubSubfacturado(Registro reg) {
        try {
            ejbServiciosPublicosOcho.actualizarConcepto(compania,
                            Integer.parseInt(cicloActual),
                            Integer.parseInt(anoActual),
                            periodoActual,
                            codigoRutaActual,
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get(cCodigoInterno),
                                            "").toString(),
                            Integer.parseInt(reg.getCampos().get(cConcepto)
                                            .toString()),
                            SessionUtil.getUser().getCodigo(),
                            BigDecimal.valueOf(Double.parseDouble(
                                            registroSubFacturadoAux.getCampos()
                                                            .get(cDeuda)
                                                            .toString())),
                            BigDecimal.valueOf(Double.parseDouble(
                                            reg.getCampos().get(cDeuda)
                                                            .toString())),
                            BigDecimal.valueOf(Double.parseDouble(
                                            registroSubFacturadoAux.getCampos()
                                                            .get(cValorFacturado)
                                                            .toString())),
                            BigDecimal.valueOf(Double.parseDouble(
                                            reg.getCampos().get(cValorFacturado)
                                                            .toString())));
            calcularTotales();
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void eliminarRegSubSubfacturado(Registro reg) {
        try {
            int conteo = Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN,
                            strSpFacturado, reg.getLlave());
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            cargarListaSubfacturado();
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void onCancelSubfacturado() {
        cargarListaSubfacturado();
        cargarListaSubfacturadoant();
    }

    public void agregarRegistroSubSubfacturadoant() {
        try {
            int conteo = Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN,
                            strSpFacturado,
                            registroSubSubFacturadoAnt.getCampos());
            cargarListaSubfacturadoant();
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1945"));
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubSubFacturadoAnt = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubfacturadoant(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            int conteo = Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN,
                            strSpFacturado, reg.getCampos(),
                            reg.getLlave());
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(strMsmRegistroModificado));
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubfacturadoant();
        }
    }

    public void eliminarRegSubSubfacturadoant(Registro reg) {
        try {
            int conteo = Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN,
                            strSpFacturado, reg.getLlave());
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            cargarListaSubfacturadoant();
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void onCancelSubfacturadoant() {
        cargarListaSubfacturadoant();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     * Retorna el valor dado por la funcion de obtener parametro a
     * partir del nombre del mismo.
     *
     * @param parametro
     * - Identificador del properties con el nombre del parametro.
     * @return el valor del parametro
     */
    private String parStr(String parametro) {
        String par = null;
        try {
            par = ejbSysmanUtil.consultarParametro(compania,
                            parametros.getString(parametro),
                            modulo,
                            new Date(),
                            false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return par;
    }

    /**
     * Devuelve el valor del parametro o el valor defecto segun
     * corresponda.
     *
     * @param parametro
     * - Identificador del properties con el nombre del parametro.
     * @param defecto
     * - Valor que se retorna si el parametro fue nulo.
     * @return
     */
    private String parStrDefault(String parametro, String defecto) {
        return SysmanFunciones.nvlStr(parStr(parametro), defecto);
    }

    /**
     * Evalua un determinado parametro y retorna su equivalente en
     * boolean
     *
     * @param parametro
     * - Identificador del properties con el nombre del parametro.
     * @return
     * @throws NamingException
     * @throws SQLException
     */
    private boolean parSiNo(String parametro) {

        String parFinal = parStrDefault(parametro, "NO");

        return "SI".equals(parFinal) ? true : false;
    }

    public void cargarParametros() {
        maneja720 = parSiNo("PR_MANEJA_720");
        manejaRiego = parSiNo("PR_MANEJA_RIEGO");
        cambiarAseo = parSiNo("PR_CAMBIAR_NOMBRE_ASEO");
        manejaRes351 = parSiNo("PR_MANEJA351");
        aplicaRes151 = parSiNo("PR_APLICA_RES_151");
        desviaEnSitio = parSiNo("PR_DESVIACION_SITIO");
        manejaUnidades = parSiNo("PR_UNIDADES_INDEPENDIENTES");
        cambiarAcueducto = parSiNo("PR_CAMBIAR_NOMBRE_ACUEDUCTO");
        cambiarAlumbrado = parSiNo("PR_CAMBIAR_NOMBRE_ALUMBRADO");
        separarAlumbrado = parSiNo("PR_SEPARAR_ALUMBRADO");
        cobrarAseoPorPeso = parSiNo("PR_ASEO_POR_PESO");
        manejaTercerizado = parSiNo("PR_MANEJA_TERCERIZADO");
        borrarComentarios = parSiNo("PR_BORRA_COMENTARIOS");
        desactivaAseoUrbano = parSiNo("PR_DESACTIVA_URBANO");
        cambiarAlcantarillado = parSiNo("PR_CAMBIAR_NOMBRE_ALCANTARILLADO");
        manejaFechaCambioEstado = parSiNo("PR_CAMBIO_ESTADO");
        manejaConceptosExternos = parSiNo("PR_CONCEPTOS_EXTERNOS");
        manejaSubSobreDehabitados = parSiNo("PR_SUB_SOBRE_DESHABITADOS");

        nombreAseo = parStr("PR_NOMBRE_ASEO");
        usoTasaCar = parStr("PR_USO_ESPECIAL_CAR");
        nombreAcueducto = parStr("PR_NOMBRE_ACUEDUCTO");
        nombreAlumbrado = parStr("PR_NOMBRE_ALUMBRADO");
        unidadAcueducto = parStr("PR_UNIDAD_ACUEDUCTO");
        grupoModificador = parStr("PR_GRUPO_MODIFICADOR");
        cicloExcluido720 = parStr("PR_CICLO_EXCLUIDO_720");
        historicoProblemas = parStr("PR_HISTORICOS_AFORO");
        nombreAlcantarillado = parStr("PR_NOMBRE_ALCANTARILLADO");
        unidadAlcantarillado = parStr("PR_UNIDAD_ALCANTARILLADO");
        usoEspecialAlumbrado = parStr("PR_USO_ESPECIAL_ALUMBRADO");
        numeroConceptos = parStr("PR_NUMERO_MAXIMO_CONCEPTOS");
        grupoModificadorDeuda = parStrDefault("PR_GRUPOS_MODIFICADORES_DEUDA",
                        "");

        periodoInicio720 = parStrDefault("PR_PERIODO_INICIO_720", "0");

        validarAutoriza();
    }

    /**
     * Realiza el llamado a las diferentes funciones que definen el
     * valor de los indicadores que autorizan convenios, desviacion,
     * micromedicion, copia de factura y descuentos
     */
    private void validarAutoriza() {
        String nit = SessionUtil.getCompaniaIngreso().getNit();
        try {
            autorizaConvenios = ejbServiciosPublicosCuatro
                            .autorizarConvenios(compania, nit);

            autorizaDesviacion = ejbServiciosPublicosDos
                            .autorizarDesviacion(compania, nit);

            autorizaMicromedicion = ejbServiciosPublicosCero
                            .autorizarMicromedicion(compania, nit);

            autorizaFacturaPagada = ejbServiciosPublicosCuatro
                            .autorizarFacturaPagada(compania, nit);

            permiteDesactivarDescuento = ejbServiciosPublicosCuatro
                            .permitirAccion(compania,
                                            "USUARIOS QUE DESACTIVAN DESCUENTO PORCENTUAL",
                                            SessionUtil.getUser().getCodigo(),
                                            "CALCULAR DESCUENTO CON PORCENTAJE POR CONCEPTO");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void gestionarNombreAcueducto() {

        lbIndicadorAcueducto = idioma.getString(strTB1732) + " ";
        if (cambiarAcueducto) {
            lbIndicadorAcueducto += nombreAcueducto;
            lbSubsidioAcueducto = nombreAcueducto;
            lbUnidadAcueducto = unidadAcueducto;
        }
        else {
            lbIndicadorAcueducto += idioma.getString("TB_TB1737");
            lbSubsidioAcueducto = idioma.getString("TB_TB1737");
            lbUnidadAcueducto = idioma.getString("TT_LB26284");
        }
        lbAcueductoAbrev = lbSubsidioAcueducto.substring(0, 3);

    }

    public void gestionarNombreAlcantarillado() {

        lbIndicadorAlcantarillado = idioma.getString(strTB1732) + " ";
        if (cambiarAlcantarillado) {
            lbIndicadorAlcantarillado += nombreAlcantarillado;
            lbSubsidioAlcantarillado = nombreAlcantarillado;
            lbUnidadAlcantarillado = unidadAlcantarillado;
        }
        else {
            lbIndicadorAlcantarillado += idioma.getString("TB_TB1738");
            lbSubsidioAlcantarillado = idioma.getString("TB_TB1738");
            lbUnidadAlcantarillado = idioma.getString("TT_LB26284");
        }
        lbAlcantarilladoAbrev = lbSubsidioAlcantarillado.substring(0, 3);
    }

    public void gestionarNombreAseo() {

        lbIndicadorAseo = idioma.getString(strTB1732) + " ";
        if (cambiarAseo) {
            lbIndicadorAseo += nombreAseo;
            lbSubsidioAseo = nombreAseo;
        }
        else {
            lbIndicadorAseo += idioma.getString("TB_TB1739");
            lbSubsidioAseo = idioma.getString("TB_TB1739");
        }
        lbAseoAbrev = lbSubsidioAseo.substring(0, 3);
    }

    public void gestionarNombreAlumbrado() {

        lbIndicadorAlumbrado = idioma.getString(strTB1732) + " ";
        if (cambiarAlumbrado) {
            lbIndicadorAlumbrado += nombreAlumbrado;
            lbAlumbradoAbrev = nombreAlumbrado.substring(0, 3);
        }
        else {
            lbIndicadorAlumbrado += idioma.getString("TB_TB1740");
            lbAlumbradoAbrev = idioma.getString("TB_TB1740").substring(0, 3);
        }
    }

    /**
     * Evalua si la palabra que ingresa por parametro se encuentra en
     * la cadena que tambien ingresa por parametro
     * 
     * @param cadena
     * Texto completo en el que desea evaluar si contiene la palabra
     * @param palabra
     * Texto especifico que se desea buscar dentro de la cadena
     * @return Verdadero si la palabra se encuentra dentro de la
     * cadena
     */
    public boolean evaluarContiene(String cadena, String palabra) {
        if (cadena.isEmpty()) {
            return false;
        }
        String[] valores;
        ArrayList<String> arrayGrupos = new ArrayList<>();
        if (cadena.contains(",")) {
            valores = cadena.split(",");
            for (String grupo : valores) {
                arrayGrupos.add(grupo);
            }
        }
        else {
            arrayGrupos.add(cadena);
        }
        return arrayGrupos.contains(palabra);
    }

    public void activarEdicionSubfacturado(Registro r) {
        indiceSubfacturado = listaSubfacturado.indexOf(r);
        registroSubFacturadoAux = new Registro(new HashMap<>(r.getCampos()));
    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {

        cargarParametros();
        if (maneja720
            && (((SysmanFunciones.concatenar(anoActual, ",", periodoActual))
                            .compareTo(periodoInicio720) < 0)
                || cicloActual.equals(cicloExcluido720))) {
            maneja720 = false;
        }

        gestionarNombreAcueducto();
        gestionarNombreAlcantarillado();
        gestionarNombreAseo();
        gestionarNombreAlumbrado();

        modificarUsoEstrato = false;
        String grupoActual = SessionUtil.getGrupo(modulo).getCodigo();

        if ((grupoModificador != null)
            && (grupoActual.indexOf(grupoModificador) != -1)) {
            modificarUsoEstrato = true;
        }

        visibleDeshabitado = true;

        if (!manejaSubSobreDehabitados && !maneja720) {
            visibleDeshabitado = false;
        }

        registroUsuario();
        registroAbono();

        modificaDeuda = evaluarContiene(grupoModificadorDeuda,
                        SessionUtil.getUser().getCodigo());

        maneja720EtiquetasAseo();

    }

    private void registroUsuario() {
        if ((ridUsuario != null) && !ridUsuario.isEmpty()) {
            varVolver = true;
            cargarRegistro(ridUsuario, "m");
        }
    }

    private void registroAbono() {
        if ((ridAbono != null) && !ridAbono.isEmpty()) {
            cargarRegistro(ridAbono, "m");
        }
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        registroSubSubFacturado = new Registro();

        codigoRutaActual = SysmanFunciones
                        .nvl(registro.getCampos().get(strCodigoRuta2), "")
                        .toString();
        anoActual = SysmanFunciones.nvl(registro.getCampos().get("ANO"), "")
                        .toString();
        periodoActual = SysmanFunciones
                        .nvl(registro.getCampos().get(strPeriodo), "")
                        .toString();
        usoActual = SysmanFunciones.nvl(registro.getCampos().get("USO"), "")
                        .toString();
        esTotalizador = "T"
                        .equals(SysmanFunciones
                                        .nvl(registro.getCampos()
                                                        .get("TOTALIZADOR"), "")
                                        .toString())
                                            ? true : false;
        fimm = SysmanFunciones
                        .nvl(registro.getCampos().get("FIMM"), "").toString();

        try {
            String anoAnterior = ejbServiciosPublicosCero
                            .prepararAnoPeriodoAnterior(compania,
                                            Integer.parseInt(anoActual),
                                            periodoActual,
                                            "0",
                                            null);

            String periodoAnterior = ejbServiciosPublicosCero
                            .prepararAnoPeriodoAnterior(compania,
                                            Integer.parseInt(anoActual),
                                            periodoActual,
                                            "1",
                                            null);

            nombrePeriodoAnterior = ejbServiciosPublicosCero
                            .asignarNombrePeriodo(compania,
                                            Integer.parseInt(anoAnterior),
                                            periodoAnterior,
                                            null);

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        calcularTotales();

        cargarListaCODTOTALIZADOR();
        cargarListaCmbTotMacro();
        cargarListaCmbEstrato();
        cargarListaCmbEstratoAseo();
        cargarListacmbFechaCambioEstado();

        activaEdicion = !(modificaDeuda
            && (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "BANCOPERPROCESO")
                || "P".equalsIgnoreCase(fimm)));

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Se definen los campos que pueden ser modificados en el
     * formulario y se adicionan a un registro auxiliar
     * "registroActualizar" para enviar este registro al proceso de
     * actualizacion
     */
    private void prepararActualizarAntes() {
        // Registro auxiliar para almacenar los campos que son
        // editables en el formulario
        Registro registroActualizar;
        String[] camposActulizar = { "AREA", "ASEOBARRIDO", "ESTRATOASEO",
                                     "NOTACREDITO", "MEDIDOR", "NUMERODIGITOS",
                                     "LECTURA", "CONSUMOAFORO",
                                     strPeriodosNoCobroFin,
                                     strPeriodosNoCobroFac,
                                     "ACUMULADO",
                                     "INQUILINATOS", "TOTALIZADOR",
                                     "ESTADOMEDIDOR", strChapetas,
                                     "FRECUENCIARECOLECCION", "TIPOCOBRO",
                                     "TASACAR", "NOUNDMULTIUSUARIO",
                                     "NOTADEBITO", "SEPARACIONENFUENTE",
                                     "NOPUERTAPUERTA", "APARTAMENTO", "PLANTA",
                                     "DESVIOSIGNIFICATIVO", "CODTOTALIZADOR",
                                     "FREC_RECO", "FREC_BARRI",
                                     "HOGAR_COMUNITARIO", "CARACTERIZACION_ALC",
                                     "CODMACROMEDIDOR", "PESOASEO", "ACUEDUCTO",
                                     "ALCANTARILLADO", "ALUMBRADO", "ASEO",
                                     "INDDESHABITADO", "PORCENTAJEAPLICAR" };

        registroActualizar = new Registro();
        for (String campo : camposActulizar) {
            registroActualizar.getCampos().put(campo,
                            registro.getCampos().get(campo));
        }

        String estratoAlumbradoActualizar = SysmanFunciones
                        .nvl(registro.getCampos().get("ESTRATOALUMBRADO"), "0")
                        .toString();
        String tipoAforoActualizar = SysmanFunciones
                        .nvl(registro.getCampos().get("TIPODEAFORO"), "0")
                        .toString();

        registroActualizar.getCampos().put("ESTRATOALUMBRADO",
                        estratoAlumbradoActualizar);
        registroActualizar.getCampos().put("TIPODEAFORO",
                        tipoAforoActualizar);

        registroActualizar.setLlave(registro.getLlave());
        registroActualizar.getCampos().put(
                        GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        registroActualizar.getCampos().put(
                        GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());
        registro = registroActualizar;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        prepararActualizarAntes();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void calcularTotales() {

        double totalTercerizadoActual = Double.parseDouble(
                        registro.getCampos().get(strValorAseo).toString());
        double totalConveniosActual = Double.parseDouble(
                        registro.getCampos().get("TOTAL_CONV").toString());
        double totalTercerizadoAnterior = Double.parseDouble(
                        registro.getCampos().get("VALORASEO_ANT").toString());
        double totalConveniosAnterior = Double.parseDouble(
                        registro.getCampos().get("TOTAL_CONV_ANT").toString());

        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(FacturasControladorEnum.CICLOACTUAL.getValue(), cicloActual);
        params.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRutaActual);
        params.put(GeneralParameterEnum.ANO.getName(), anoActual);
        params.put(GeneralParameterEnum.PERIODO.getName(), periodoActual);

        Registro rTotales = null;
        try {
            rTotales = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL003
                                                                            .getValue())
                                            .getUrl(), params));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rTotales != null) {
            totalFacturadoActual = Double
                            .parseDouble(SysmanFunciones.nvl(
                                            rTotales.getCampos().get(
                                                            "TOTAL_FACTURADO"),
                                            "0").toString());
            deudaActual = Double
                            .parseDouble(SysmanFunciones.nvl(
                                            rTotales.getCampos().get(
                                                            "TOTAL_DEUDA"),
                                            "0").toString());
        }

        params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(FacturasControladorEnum.CICLOACTUAL.getValue(), cicloActual);
        params.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRutaActual);
        params.put(GeneralParameterEnum.ANO.getName(), anoActual);
        params.put(GeneralParameterEnum.PERIODO.getName(), periodoActual);
        Registro rTotalesAnt = null;
        try {
            rTotalesAnt = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasControladorUrlEnum.URL004
                                                                            .getValue())
                                            .getUrl(), params));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rTotalesAnt != null) {
            totalFacturadoAnterior = Double
                            .parseDouble(SysmanFunciones.nvl(
                                            rTotalesAnt.getCampos()
                                                            .get("TOTAL_FACTURADO_ANT"),
                                            "0").toString());
            deudaAnterior = Double
                            .parseDouble(SysmanFunciones.nvl(
                                            rTotalesAnt.getCampos()
                                                            .get("TOTAL_DEUDA_ANT"),
                                            "0").toString());
        }

        granTotalActual = totalFacturadoActual + totalTercerizadoActual
            + totalConveniosActual;
        granTotalAnterior = totalFacturadoAnterior + totalTercerizadoAnterior
            + totalConveniosAnterior;
    }

    /**
     * Obtiene el valor del consecutivo de la factura
     * 
     * @return Numero de factura real
     */
    private String obtenerConsecutivoReal() {
        // 231004
        String consecutivo = null;
        try {
            HashMap<String, Object> rsAux;
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FacturasControladorUrlEnum.URL006
                                                            .getValue());

            rsAux = (HashMap<String, Object>) requestManager
                            .get(urlReg.getUrl(), parametros).getFields();

            if (!rsAux.isEmpty()) {
                consecutivo = SysmanFunciones
                                .nvl(rsAux.get("REAL"),
                                                SysmanFunciones.padl("1", 10,
                                                                "0"))
                                .toString();
            }
            else {
                consecutivo = "0";
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return consecutivo;
    }

    /**
     * Retorna la diferencia en dias entre la fecha actual y la fecha
     * de preparacion del ciclo
     * 
     * @return Dias de diferencia
     */
    private int evaluarDiferenciaFechas() {
        int diferencia = 0;
        try {
            HashMap<String, Object> rs;
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.CICLO.getName(), cicloActual);
            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FacturasControladorUrlEnum.URL007
                                                            .getValue());

            rs = (HashMap<String, Object>) requestManager
                            .get(urlReg.getUrl(), parametros).getFields();

            if (!rs.isEmpty()) {
                diferencia = Integer.parseInt(rs.get("DIFERENCIA").toString());
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return diferencia;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // <SET_GET_ATRIBUTOS>

    public double getDeudaActual() {
        return deudaActual;
    }

    public void setDeudaActual(double deudaActual) {
        this.deudaActual = deudaActual;
    }

    public double getTotalFacturadoActual() {
        return totalFacturadoActual;
    }

    public void setTotalFacturadoActual(double totalFacturadoActual) {
        this.totalFacturadoActual = totalFacturadoActual;
    }

    public boolean isManejaTercerizado() {
        return manejaTercerizado;
    }

    public void setManejaTercerizado(boolean manejaTercerizado) {
        this.manejaTercerizado = manejaTercerizado;
    }

    public boolean isVisibleDeshabitado() {
        return visibleDeshabitado;
    }

    public void setVisibleDeshabitado(boolean visibleDeshabitado) {
        this.visibleDeshabitado = visibleDeshabitado;
    }

    public boolean isManejaConceptosExternos() {
        return manejaConceptosExternos;
    }

    public void setManejaConceptosExternos(boolean manejaConceptosExternos) {
        this.manejaConceptosExternos = manejaConceptosExternos;
    }

    public boolean isAutorizaConvenios() {
        return autorizaConvenios;
    }

    public void setAutorizaConvenios(boolean autorizaConvenios) {
        this.autorizaConvenios = autorizaConvenios;
    }

    public boolean isCobrarAseoPorPeso() {
        return cobrarAseoPorPeso;
    }

    public void setCobrarAseoPorPeso(boolean cobrarAseoPorPeso) {
        this.cobrarAseoPorPeso = cobrarAseoPorPeso;
    }

    public boolean isManejaRes351() {
        return manejaRes351;
    }

    public void setManejaRes351(boolean manejaRes351) {
        this.manejaRes351 = manejaRes351;
    }

    public boolean isPermiteDesactivarDescuento() {
        return permiteDesactivarDescuento;
    }

    public void setPermiteDesactivarDescuento(
        boolean permiteDesactivarDescuento) {
        this.permiteDesactivarDescuento = permiteDesactivarDescuento;
    }

    public boolean isAutorizaDesviacion() {
        return autorizaDesviacion;
    }

    public void setAutorizaDesviacion(boolean autorizaDesviacion) {
        this.autorizaDesviacion = autorizaDesviacion;
    }

    public boolean isAutorizaMicromedicion() {
        return autorizaMicromedicion;
    }

    public boolean isManejaFechaCambioEstado() {
        return manejaFechaCambioEstado;
    }

    public void setManejaFechaCambioEstado(boolean manejaFechaCambioEstado) {
        this.manejaFechaCambioEstado = manejaFechaCambioEstado;
    }

    public void setAutorizaMicromedicion(boolean autorizaMicromedicion) {
        this.autorizaMicromedicion = autorizaMicromedicion;
    }

    public boolean isManejaSubSobreDehabitados() {
        return manejaSubSobreDehabitados;
    }

    public void setManejaSubSobreDehabitados(
        boolean manejaSubSobreDehabitados) {
        this.manejaSubSobreDehabitados = manejaSubSobreDehabitados;
    }

    public double getGranTotalActual() {
        return granTotalActual;
    }

    public void setGranTotalActual(double granTotalActual) {
        this.granTotalActual = granTotalActual;
    }

    public double getTotalAseoSubsidios() {
        return totalAseoSubsidios;
    }

    public void setTotalAseoSubsidios(double totalAseoSubsidios) {
        this.totalAseoSubsidios = totalAseoSubsidios;
    }

    public boolean isManeja720() {
        return maneja720;
    }

    public void setManeja720(boolean maneja720) {
        this.maneja720 = maneja720;
    }

    public boolean isModificarUsoEstrato() {
        return modificarUsoEstrato;
    }

    public void setModificarUsoEstrato(boolean modificarUsoEstrato) {
        this.modificarUsoEstrato = modificarUsoEstrato;
    }

    public double getDeudaAnterior() {
        return deudaAnterior;
    }

    public boolean isManejaUnidades() {
        return manejaUnidades;
    }

    public void setManejaUnidades(boolean manejaUnidades) {
        this.manejaUnidades = manejaUnidades;
    }

    public void setDeudaAnterior(double deudaAnterior) {
        this.deudaAnterior = deudaAnterior;
    }

    public boolean isDesactivaAseoUrbano() {
        return desactivaAseoUrbano;
    }

    public void setDesactivaAseoUrbano(boolean desactivaAseoUrbano) {
        this.desactivaAseoUrbano = desactivaAseoUrbano;
    }

    public String getNombreAcueducto() {
        return nombreAcueducto;
    }

    public void setNombreAcueducto(String nombreAcueducto) {
        this.nombreAcueducto = nombreAcueducto;
    }

    public String getNombreAlcantarillado() {
        return nombreAlcantarillado;
    }

    public void setNombreAlcantarillado(String nombreAlcantarillado) {
        this.nombreAlcantarillado = nombreAlcantarillado;
    }

    public String getNombreAseo() {
        return nombreAseo;
    }

    public String getLbIndicadorAcueducto() {
        return lbIndicadorAcueducto;
    }

    public void setLbIndicadorAcueducto(String lbIndicadorAcueducto) {
        this.lbIndicadorAcueducto = lbIndicadorAcueducto;
    }

    public String getGrupoModificador() {
        return grupoModificador;
    }

    public void setGrupoModificador(String grupoModificador) {
        this.grupoModificador = grupoModificador;
    }

    public String getLbIndicadorAlcantarillado() {
        return lbIndicadorAlcantarillado;
    }

    public void setLbIndicadorAlcantarillado(String lbIndicadorAlcantarillado) {
        this.lbIndicadorAlcantarillado = lbIndicadorAlcantarillado;
    }

    public String getLbIndicadorAseo() {
        return lbIndicadorAseo;
    }

    public void setLbIndicadorAseo(String lbIndicadorAseo) {
        this.lbIndicadorAseo = lbIndicadorAseo;
    }

    public String getLbIndicadorAlumbrado() {
        return lbIndicadorAlumbrado;
    }

    public void setLbIndicadorAlumbrado(String lbIndicadorAlumbrado) {
        this.lbIndicadorAlumbrado = lbIndicadorAlumbrado;
    }

    public void setNombreAseo(String nombreAseo) {
        this.nombreAseo = nombreAseo;
    }

    public String getNombreAlumbrado() {
        return nombreAlumbrado;
    }

    public void setNombreAlumbrado(String nombreAlumbrado) {
        this.nombreAlumbrado = nombreAlumbrado;
    }

    public String getLbSubsidioAcueducto() {
        return lbSubsidioAcueducto;
    }

    public void setLbSubsidioAcueducto(String lbSubsidioAcueducto) {
        this.lbSubsidioAcueducto = lbSubsidioAcueducto;
    }

    public String getLbSubsidioAlcantarillado() {
        return lbSubsidioAlcantarillado;
    }

    public void setLbSubsidioAlcantarillado(String lbSubsidioAlcantarillado) {
        this.lbSubsidioAlcantarillado = lbSubsidioAlcantarillado;
    }

    public String getLbSubsidioAseo() {
        return lbSubsidioAseo;
    }

    public void setLbSubsidioAseo(String lbSubsidioAseo) {
        this.lbSubsidioAseo = lbSubsidioAseo;
    }

    public String getLbUnidadAcueducto() {
        return lbUnidadAcueducto;
    }

    public void setLbUnidadAcueducto(String lbUnidadAcueducto) {
        this.lbUnidadAcueducto = lbUnidadAcueducto;
    }

    public String getLbUnidadAlcantarillado() {
        return lbUnidadAlcantarillado;
    }

    public void setLbUnidadAlcantarillado(String lbUnidadAlcantarillado) {
        this.lbUnidadAlcantarillado = lbUnidadAlcantarillado;
    }

    public String getLbAcueductoAbrev() {
        return lbAcueductoAbrev;
    }

    public boolean isManejaRiego() {
        return manejaRiego;
    }

    public void setManejaRiego(boolean manejaRiego) {
        this.manejaRiego = manejaRiego;
    }

    public boolean isAplicaRes151() {
        return aplicaRes151;
    }

    public void setAplicaRes151(boolean aplicaRes151) {
        this.aplicaRes151 = aplicaRes151;
    }

    public boolean isDesviaEnSitio() {
        return desviaEnSitio;
    }

    public void setDesviaEnSitio(boolean desviaEnSitio) {
        this.desviaEnSitio = desviaEnSitio;
    }

    public boolean isBorrarComentarios() {
        return borrarComentarios;
    }

    public void setBorrarComentarios(boolean borrarComentarios) {
        this.borrarComentarios = borrarComentarios;
    }

    public String getUsoEspecialAlumbrado() {
        return usoEspecialAlumbrado;
    }

    public void setUsoEspecialAlumbrado(String usoEspecialAlumbrado) {
        this.usoEspecialAlumbrado = usoEspecialAlumbrado;
    }

    public boolean isSepararAlumbrado() {
        return separarAlumbrado;
    }

    public void setSepararAlumbrado(boolean separarAlumbrado) {
        this.separarAlumbrado = separarAlumbrado;
    }

    public void setLbAcueductoAbrev(String lbAcueductoAbrev) {
        this.lbAcueductoAbrev = lbAcueductoAbrev;
    }

    public String getLbAlcantarilladoAbrev() {
        return lbAlcantarilladoAbrev;
    }

    public void setLbAlcantarilladoAbrev(String lbAlcantarilladoAbrev) {
        this.lbAlcantarilladoAbrev = lbAlcantarilladoAbrev;
    }

    public String getLbAseoAbrev() {
        return lbAseoAbrev;
    }

    public void setLbAseoAbrev(String lbAseoAbrev) {
        this.lbAseoAbrev = lbAseoAbrev;
    }

    public String getLbAlumbradoAbrev() {
        return lbAlumbradoAbrev;
    }

    public boolean isEsTotalizador() {
        return esTotalizador;
    }

    public void setEsTotalizador(boolean esTotalizador) {
        this.esTotalizador = esTotalizador;
    }

    public void setLbAlumbradoAbrev(String lbAlumbradoAbrev) {
        this.lbAlumbradoAbrev = lbAlumbradoAbrev;
    }

    public double getTotalFacturadoAnterior() {
        return totalFacturadoAnterior;
    }

    public void setTotalFacturadoAnterior(double totalFacturadoAnterior) {
        this.totalFacturadoAnterior = totalFacturadoAnterior;
    }

    public double getGranTotalAnterior() {
        return granTotalAnterior;
    }

    public void setGranTotalAnterior(double granTotalAnterior) {
        this.granTotalAnterior = granTotalAnterior;
    }

    public String getNombrePeriodoAnterior() {
        return nombrePeriodoAnterior;
    }

    public void setNombrePeriodoAnterior(String nombrePeriodoAnterior) {
        this.nombrePeriodoAnterior = nombrePeriodoAnterior;
    }

    public String getCicloActual() {
        return cicloActual;
    }

    public void setCicloActual(String cicloActual) {
        this.cicloActual = cicloActual;
    }

    public String getAnoActual() {
        return anoActual;
    }

    public void setAnoActual(String anoActual) {
        this.anoActual = anoActual;
    }

    public String getPeriodoActual() {
        return periodoActual;
    }

    public void setPeriodoActual(String periodoActual) {
        this.periodoActual = periodoActual;
    }

    public String getCodigoRutaActual() {
        return codigoRutaActual;
    }

    public void setCodigoRutaActual(String codigoRutaActual) {
        this.codigoRutaActual = codigoRutaActual;
    }

    public String getUsoActual() {
        return usoActual;
    }

    public void setUsoActual(String usoActual) {
        this.usoActual = usoActual;
    }

    public String getCompania() {
        return compania;
    }

    public String getNombreConcepto() {
        return nombreConcepto;
    }

    public void setNombreConcepto(String nombreConcepto) {
        this.nombreConcepto = nombreConcepto;
    }

    public String getNumeroConceptos() {
        return numeroConceptos;
    }

    public void setNumeroConceptos(String numeroConceptos) {
        this.numeroConceptos = numeroConceptos;
    }

    public String getGrupoModificadorDeuda() {
        return grupoModificadorDeuda;
    }

    public void setGrupoModificadorDeuda(String grupoModificadorDeuda) {
        this.grupoModificadorDeuda = grupoModificadorDeuda;
    }

    /**
     * Retorna la variable varVolver
     *
     * @return var
     */
    public boolean isVarVolver() {
        return varVolver;
    }

    /**
     * Asigna la variable varVolver
     *
     * @param var
     * Variable a asignar en varVolver
     */
    public void setVarVolver(boolean varVolver) {
        this.varVolver = varVolver;
    }

    public boolean isModificaDeuda() {
        return modificaDeuda;
    }

    public void setModificaDeuda(boolean modificaDeuda) {
        this.modificaDeuda = modificaDeuda;
    }

    public boolean isActivaEdicion() {
        return activaEdicion;
    }

    public void setActivaEdicion(boolean activaEdicion) {
        this.activaEdicion = activaEdicion;
    }

    public double getValorConcepto() {
        return valorConcepto;
    }

    public void setValorConcepto(double valorConcepto) {
        this.valorConcepto = valorConcepto;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaTipoCobro() {
        return listaTipoCobro;
    }

    public void setListaTipoCobro(List<Registro> listaTipoCobro) {
        this.listaTipoCobro = listaTipoCobro;
    }

    public List<Registro> getListaConceptoAnterior() {
        return listaConceptoAnterior;
    }

    public void setListaConceptoAnterior(List<Registro> listaConceptoAnterior) {
        this.listaConceptoAnterior = listaConceptoAnterior;
    }

    public List<Registro> getListaNombreAnterior() {
        return listaNombreAnterior;
    }

    public void setListaNombreAnterior(List<Registro> listaNombreAnterior) {
        this.listaNombreAnterior = listaNombreAnterior;
    }

    public List<Registro> getListaCODTOTALIZADOR() {
        return listaCODTOTALIZADOR;
    }

    public void setListaCODTOTALIZADOR(List<Registro> listaCODTOTALIZADOR) {
        this.listaCODTOTALIZADOR = listaCODTOTALIZADOR;
    }

    public List<Registro> getListaCmbTotMacro() {
        return listaCmbTotMacro;
    }

    public void setListaCmbTotMacro(List<Registro> listaCmbTotMacro) {
        this.listaCmbTotMacro = listaCmbTotMacro;
    }

    public List<Registro> getListaCmbUso() {
        return listaCmbUso;
    }

    public void setListaCmbUso(List<Registro> listaCmbUso) {
        this.listaCmbUso = listaCmbUso;
    }

    public List<Registro> getListaCmbEstrato() {
        return listaCmbEstrato;
    }

    public void setListaCmbEstrato(List<Registro> listaCmbEstrato) {
        this.listaCmbEstrato = listaCmbEstrato;
    }

    public List<Registro> getListaCmbEstratoAseo() {
        return listaCmbEstratoAseo;
    }

    public void setListaCmbEstratoAseo(List<Registro> listaCmbEstratoAseo) {
        this.listaCmbEstratoAseo = listaCmbEstratoAseo;
    }

    public List<Registro> getListaEstratoAlumbrado() {
        return listaEstratoAlumbrado;
    }

    public void setListaEstratoAlumbrado(List<Registro> listaEstratoAlumbrado) {
        this.listaEstratoAlumbrado = listaEstratoAlumbrado;
    }

    public List<Registro> getListaTasaCar() {
        return listaTasaCar;
    }

    public void setListaTasaCar(List<Registro> listaTasaCar) {
        this.listaTasaCar = listaTasaCar;
    }

    public List<Registro> getListaAFORADOR() {
        return listaAFORADOR;
    }

    public void setListaAFORADOR(List<Registro> listaAFORADOR) {
        this.listaAFORADOR = listaAFORADOR;
    }

    public List<Registro> getListacmbFechaCambioEstado() {
        return listacmbFechaCambioEstado;
    }

    public void setListacmbFechaCambioEstado(
        List<Registro> listacmbFechaCambioEstado) {
        this.listacmbFechaCambioEstado = listacmbFechaCambioEstado;
    }

    public List<Registro> getListaCmbarrio() {
        return listaCmbarrio;
    }

    public void setListaCmbarrio(List<Registro> listaCmbarrio) {
        this.listaCmbarrio = listaCmbarrio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaConcepto() {
        return listaConcepto;
    }

    public void setListaConcepto(RegistroDataModelImpl listaConcepto) {
        this.listaConcepto = listaConcepto;
    }

    public RegistroDataModelImpl getListaConceptoE() {
        return listaConceptoE;
    }

    public void setListaConceptoE(RegistroDataModelImpl listaConceptoE) {
        this.listaConceptoE = listaConceptoE;
    }

    public RegistroDataModelImpl getListaNombre() {
        return listaNombre;
    }

    public void setListaNombre(RegistroDataModelImpl listaNombre) {
        this.listaNombre = listaNombre;
    }

    public RegistroDataModelImpl getListaNombreE() {
        return listaNombreE;
    }

    public void setListaNombreE(RegistroDataModelImpl listaNombreE) {
        this.listaNombreE = listaNombreE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListatxtEmpresaAseo() {
        return listatxtEmpresaAseo;
    }

    public void setListatxtEmpresaAseo(
        RegistroDataModelImpl listatxtEmpresaAseo) {
        this.listatxtEmpresaAseo = listatxtEmpresaAseo;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public List<Registro> getListaSubfacturado() {
        return listaSubfacturado;
    }

    public void setListaSubfacturado(List<Registro> listaSubfacturado) {
        this.listaSubfacturado = listaSubfacturado;
    }

    public List<Registro> getListaSubfacturadoant() {
        return listaSubfacturadoant;
    }

    public void setListaSubfacturadoant(List<Registro> listaSubfacturadoant) {
        this.listaSubfacturadoant = listaSubfacturadoant;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroSubSubFacturado() {
        return registroSubSubFacturado;
    }

    public void setRegistroSubSubFacturado(Registro registroSubSubFacturado) {
        this.registroSubSubFacturado = registroSubSubFacturado;
    }

    public Registro getRegistroSubSubFacturadoAnt() {
        return registroSubSubFacturadoAnt;
    }

    public void setRegistroSubSubFacturadoAnt(
        Registro registroSubSubFacturadoAnt) {
        this.registroSubSubFacturadoAnt = registroSubSubFacturadoAnt;
    }

    public int getIndiceSubfacturado() {
        return indiceSubfacturado;
    }

    public void setIndiceSubfacturado(int indiceSubfacturado) {
        this.indiceSubfacturado = indiceSubfacturado;
    }

    public boolean isActualizarChapetasVisible() {
        return actualizarChapetasVisible;
    }

    public void setActualizarChapetasVisible(
        boolean actualizarChapetasVisible) {
        this.actualizarChapetasVisible = actualizarChapetasVisible;
    }

    public String getLblDispoFinal() {
        return lblDispoFinal;
    }

    public void setLblDispoFinal(String lblDispoFinal) {
        this.lblDispoFinal = lblDispoFinal;
    }

    public String getLblTrataLixivi() {
        return lblTrataLixivi;
    }

    public void setLblTrataLixivi(String lblTrataLixivi) {
        this.lblTrataLixivi = lblTrataLixivi;
    }

    public boolean getLblBaseAprovecha() {
        return lblBaseAprovecha;
    }

    public void setLblBaseAprovecha(boolean lblBaseAprovecha) {
        this.lblBaseAprovecha = lblBaseAprovecha;
    }

    private void maneja720EtiquetasAseo() {

        if (maneja720) {
            lblDispoFinal = idioma.getString("TB_TB3336");
            lblTrataLixivi = idioma.getString("TB_TB3337");
            lblBaseAprovecha = true;
        }
        else {
            lblDispoFinal = idioma.getString("TB_TB3338");
            lblTrataLixivi = idioma.getString("TB_TB3339");
            lblBaseAprovecha = false;

        }

    }

    // </SET_GET_ADICIONALES>
}
