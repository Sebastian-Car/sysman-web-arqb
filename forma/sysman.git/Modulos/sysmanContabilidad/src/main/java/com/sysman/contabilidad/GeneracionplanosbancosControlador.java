/*-
 * GeneracionplanosbancosControlador.java
 *
 * 1.0
 * 
 * 16/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.contabilidad.enums.GeneracionplanosbancosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase permite gestionar el formulario de generacion de reportes para los bancos Occidente, Bogotá, Popular y Caja social
 *
 * @version 1.0, 16/10/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class GeneracionplanosbancosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el valor de codigoVerficacion
     */
    private boolean codigoVerficacion;
    /**
     * Variable que almacena el valor de egresoInicial
     */
    private String egresoInicial;
    /**
     * Variable que almacena el valor de egresoFinal
     */
    private String egresoFinal;
    /**
     * Variable que almacena el valor de tipoEgreso
     */
    private String tipoEgreso;
    /**
     * Variable que almacena el valor de terceroInicial
     */
    private String terceroInicial;
    /**
     * Variable que almacena el valor de terceroFinal
     */
    private String terceroFinal;
    /**
     * Variable que almacena el valor de numeroBanco
     */
    private String numeroBanco;
    /**
     * Variable que almacena el valor de cuentaInicial
     */
    private String cuentaInicial;
    /**
     * Variable que almacena el valor de cuentaFinal
     */
    private String cuentaFinal;
    /**
     * Variable que almacena el valor de ano
     */
    private String ano;
    /**
     * Variable que almacena el valor de nombreBanco
     */
    private String nombreBanco;
    /**
     * Variable que almacena el valor de fechaPago
     */
    private Date fechaPago;
    /**
     * Variable que almacena el valor de cuentaClienteDebitar
     */
    private String cuentaClienteDebitar;
    /**
     * Variable que almacena el valor de cuentaPrincipalAfiliada
     */
    private String cuentaPrincipalAfiliada;
    /**
     * Variable que almacena el valor de identificador
     */
    private String identificador;
    /*
     * Variable que indica si muestra los registros con el check de enviado o no
     */
    private boolean todosFiltro;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * lista que almacena listacmbNumeroBanco
     */
    private RegistroDataModelImpl listacmbNumeroBanco;
    /**
     * lista que almacena listacmbAno
     */
    private List<Registro> listacmbAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * lista que almacena listacmbNumeroInicial
     */
    private RegistroDataModelImpl listacmbNumeroInicial;
    /**
     * lista que almacena listacmbNumeroFinal
     */
    private RegistroDataModelImpl listacmbNumeroFinal;
    /**
     * lista que almacena listacmbTipo
     */
    private RegistroDataModelImpl listacmbTipo;
    /**
     * lista que almacena listacmbTerceroInicial
     */
    private RegistroDataModelImpl listacmbTerceroInicial;
    /**
     * lista que almacena listacmbTerceroFinal
     */
    private RegistroDataModelImpl listacmbTerceroFinal;
    /**
     * lista que almacena listacmbCuentaInicial
     */
    private RegistroDataModelImpl listacmbCuentaInicial;
    /**
     * lista que almacena listacmbCuentaFinal
     */
    private RegistroDataModelImpl listacmbCuentaFinal;

    /**
     * Constante que almacena la palabra TIPOEGRESO
     */
    private String cTipoEgreso;
    /**
     * Constante que almacena la palabra NUMEROINICIAL
     */
    private String cNumeroInicial;
    
    private String claseTransaccion;
    
    private String tipoCuentaCliente;
    /**
     * variable que almacena el valor del parametro = MANEJA PLANO BANCO OCCIDENTE
     */
    private boolean paramManejaPlanoBanOccidente;
    /**
     * Constante que almacena la palabra NIT
     */
    private String cNit;
    /**
     * Variable que indica el lugar desde el que se intenta mostrar una alerta para que el comando remoto sepa que alerta enviar
     */
    private int origen;
    @EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbContabilidadSieteRemote ejbContabilidadSiete;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de GeneracionplanosbancosControlador
     */
    public GeneracionplanosbancosControlador() {
        super();
        compania = SessionUtil.getCompania();
        /**
         * Valores por defecto en access
         */
        tipoEgreso = "EGR";
        ano = SysmanFunciones.toString(SysmanFunciones.ano(new Date()));
        fechaPago = new Date();
        egresoInicial = "1";
        egresoFinal = "9999999999";
        identificador = "0";
        codigoVerficacion = true;
        /**
         * Palabras usadas en el controlador
         */
        cTipoEgreso = "TIPOEGRESO";
        cNumeroInicial = "NUMEROINICIAL";
        cNit = "NIT";
        try {
            numFormulario = 1965;
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListacmbNumeroBanco();
        cargarListacmbAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

        cargarListacmbTipo();
        cargarListacmbTerceroInicial();
        /**
         * Esta 2 listas se inicializan dado que se asignan 2 valores por defecto al ano y al tipo que son los combos que afectan estas 2 listas
         */
        cargarListacmbCuentaInicial();
        cargarListacmbNumeroInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
    	
    	try {
    		
    	    paramManejaPlanoBanOccidente = "SI".equals(SysmanFunciones
    	            .nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA PLANO BANCO OCCIDENTE",
    	                "1", new Date(), true), "NO"));
    		
    	    
    	} catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        // <CODIGO_DESARROLLADO>
        /*
         * FR1965-AL_ABRIR Private Sub Form_Open(Cancel As Integer) formularioAbrir 2, Me.Name Me!CuentaCliente = TraerParametro("CUENTA CLIENTE A DEBITAR", Getcompany()) DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    	
    }
    

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbNumeroBanco
     *
     */
    public void cargarListacmbNumeroBanco() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL001.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbNumeroBanco = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.BANCO.getName());
    }

    /**
     * 
     * Carga la lista listacmbAno
     *
     */
    public void cargarListacmbAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listacmbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL002.getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listacmbNumeroInicial
     *
     */
    public void cargarListacmbNumeroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL003.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(cTipoEgreso, tipoEgreso);

        listacmbNumeroInicial = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * 
     * Carga la lista listacmbNumeroFinal
     *
     */
    public void cargarListacmbNumeroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL004.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(cTipoEgreso, tipoEgreso);
        param.put(cNumeroInicial, egresoInicial);

        listacmbNumeroFinal = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * 
     * Carga la lista listacmbTipo
     *
     */
    public void cargarListacmbTipo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL005.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbTipo = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacmbTerceroInicial
     *
     */
    public void cargarListacmbTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL006.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbTerceroInicial = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNit);

    }

    /**
     * 
     * Carga la lista listacmbTerceroFinal
     *
     */
    public void cargarListacmbTerceroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL007.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TERCEROINICIAL", terceroInicial);

        listacmbTerceroFinal = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNit);

    }

    /**
     * 
     * Carga la lista listacmbCuentaInicial
     *
     */
    public void cargarListacmbCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL008.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listacmbCuentaInicial = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listacmbCuentaFinal
     *
     */
    public void cargarListacmbCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GeneracionplanosbancosControladorUrlEnum.URL009.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put("CUENTAINICIAL", cuentaInicial);

        listacmbCuentaFinal = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton btnGenerar en la vista
     *
     *
     */
    
    /**
     * Genera el archivo plano para Banco.
     * 
     * Ajuste: Se agregan validaciones y valores predeterminados para los campos opcionales
     * (cuentaPrincipalAfiliada, identificador, tipoCuentaCliente, claseTransaccion) en caso de que lleguen nulos o vacíos.
     * Estos campos pueden ser habilitados o utilizados en el futuro según el parámetro de configuración
     * MANEJA PLANO BANCO OCCIDENTE.
     * 
     * Si los campos opcionales no son suministrados, se asignan los siguientes valores por defecto:
     * - cuentaPrincipalAfiliada: 0L
     * - identificador: 0
     * - tipoCuentaCliente: "" (cadena vacía)
     * - claseTransaccion: "" (cadena vacía)
     * - codigoVerficacion: false
     * 
     */
    public void oprimirbtnGenerar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try {
            if (Long.parseLong(cuentaInicial) <= Long.parseLong(cuentaFinal)) {
                if (Long.parseLong(egresoInicial) <= Long.parseLong(egresoFinal)) {
                    // Asignar valores predeterminados para los parámetros opcionales
                    long cuentaPrincipalAfiliadaParsed = (cuentaPrincipalAfiliada != null && !cuentaPrincipalAfiliada.isEmpty())
                            ? Long.parseLong(cuentaPrincipalAfiliada)
                            : 0L; 

                    int identificadorParsed = (identificador != null && !identificador.isEmpty())
                            ? Integer.parseInt(identificador)
                            : 0; 

                    boolean codigoVerficacionParsed = false; 

                    String tipoCuentaClienteParsed = (tipoCuentaCliente != null && !tipoCuentaCliente.isEmpty())
                            ? tipoCuentaCliente
                            : ""; 

                    String claseTransaccionParsed = (claseTransaccion != null && !claseTransaccion.isEmpty())
                            ? claseTransaccion
                            : ""; 

                    
                    String salidaPlano = ejbContabilidadSiete.generarBancosPlanos(
                            compania,
                            Integer.parseInt(ano),
                            new BigInteger(egresoInicial),
                            new BigInteger(egresoFinal),
                            fechaPago,
                            Long.parseLong(cuentaClienteDebitar),
                            cuentaPrincipalAfiliadaParsed,
                            numeroBanco,
                            tipoEgreso,
                            cuentaInicial,
                            cuentaFinal,
                            identificadorParsed,
                            codigoVerficacionParsed,
                            tipoCuentaClienteParsed,
                            claseTransaccionParsed
                    );

                    if (salidaPlano != null && !salidaPlano.equals("ERROR") && !salidaPlano.isEmpty()) {
                        archivoDescarga = JsfUtil.getArchivoDescarga(
                                JsfUtil.serializarPlano(salidaPlano),
                                SessionUtil.getCompaniaIngreso().getNombre() + " BancoOccidente.txt"
                        );

                        origen = 1;
                        ejecutaralertas();
                    } else {
                        origen = 2;
                        ejecutaralertas();
                    }
                } else {
                    throw new SystemException("El egreso inicial no puede ser mayor al egreso final");
                }
            } else {
                throw new SystemException("La cuenta inicial no puede ser mayor a la cuenta final");
            }
        } catch (NumberFormatException | SystemException | JRException | IOException e) {
            Logger.getLogger(GeneracionplanosbancosControlador.class.getName()).log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto alertas en la vista
     *
     *
     */
    public void ejecutaralertas() {
        // <CODIGO_DESARROLLADO>
        if (origen == 1) {
            JsfUtil.agregarMensajeInformativo("Plano Generado Exitosamente.");
        }
        else if (origen == 2) {
            JsfUtil.agregarMensajeError(
                            "Se podrujo un error al intentar generar el archivo plano, No se encontraron los comprobantes contables");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btnImpirmir en la vista
     *
     *
     */
    public void oprimirbtnImpirmir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }
    
    private void generaReporte(FORMATOS formato) {
		try {
			HashMap<String, Object> reemplazar = new HashMap<>();
			String reporte = "002627INFORMEBANCOOCCIDENTE";
			String fechaCorte = SysmanFunciones.convertirAFechaCadena(fechaPago, "YYYYMMdd");
			reemplazar.put("compania", compania);
			reemplazar.put("ano",Integer.parseInt(ano));
			reemplazar.put("tipoEgreso", tipoEgreso);
			reemplazar.put("cuentaCliente", cuentaClienteDebitar);
			reemplazar.put("numeroBanco", numeroBanco);
			reemplazar.put("cuentaInicial", cuentaInicial);
			reemplazar.put("cuentaFinal", cuentaFinal);
			reemplazar.put("egresoInicial", egresoInicial);
			reemplazar.put("egresoFinal", egresoFinal);
			String filtro = todosFiltro ? "" : "AND COMPROBANTE_CNT.ENVIADO IN (0)";
			reemplazar.put("filtro", filtro);
			Map<String, Object> parametros = new HashMap<>();
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()), reemplazar, parametros);
			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_NITCOMPANIA", SessionUtil.getCompaniaIngreso().getNit());
			parametros.put("PR_FECHACORTE", fechaCorte);
			parametros.put("PR_CUENTAPPAL", cuentaClienteDebitar);
		
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | SysmanException | ParseException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control cmbAno
     * 
     * 
     */
    public void cambiarcmbAno() {
        // <CODIGO_DESARROLLADO>
        listacmbNumeroInicial = null;
        egresoInicial = "";
        listacmbNumeroFinal = null;
        egresoFinal = null;
        listacmbCuentaInicial = null;
        cuentaInicial = "";
        listacmbCuentaFinal = null;
        cuentaFinal = "";
        cargarListacmbCuentaInicial();
        cargarListacmbNumeroInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbNumeroInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbNumeroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        egresoInicial = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
        listacmbNumeroFinal = null;
        egresoFinal = "";
        cargarListacmbNumeroFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbNumeroFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbNumeroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        egresoFinal = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbTipo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoEgreso = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        listacmbNumeroInicial = null;
        egresoInicial = "";
        listacmbNumeroFinal = null;
        egresoFinal = "";
        cargarListacmbNumeroInicial();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbTerceroInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = SysmanFunciones.toString(registroAux.getCampos().get(cNit));
        listacmbTerceroFinal = null;
        terceroFinal = "";
        cargarListacmbTerceroFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbTerceroFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = SysmanFunciones.toString(registroAux.getCampos().get(cNit));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbCuentaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        listacmbCuentaFinal = null;
        cuentaFinal = null;
        cargarListacmbCuentaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbCuentaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbNumeroBanco
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbNumeroBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroBanco = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.BANCO.getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigoVerficacion
     * 
     * @return codigoVerficacion
     */
    public boolean getCodigoVerficacion() {
        return codigoVerficacion;
    }

    /**
     * Asigna la variable codigoVerficacion
     * 
     * @param codigoVerficacion
     * Variable a asignar en codigoVerficacion
     */
    public void setCodigoVerficacion(boolean codigoVerficacion) {
        this.codigoVerficacion = codigoVerficacion;
    }

    /**
     * Retorna la variable egresoInicial
     * 
     * @return egresoInicial
     */
    public String getEgresoInicial() {
        return egresoInicial;
    }

    /**
     * Asigna la variable egresoInicial
     * 
     * @param egresoInicial
     * Variable a asignar en egresoInicial
     */
    public void setEgresoInicial(String egresoInicial) {
        this.egresoInicial = egresoInicial;
    }

    /**
     * Retorna la variable egresoFinal
     * 
     * @return egresoFinal
     */
    public String getEgresoFinal() {
        return egresoFinal;
    }

    /**
     * Asigna la variable egresoFinal
     * 
     * @param egresoFinal
     * Variable a asignar en egresoFinal
     */
    public void setEgresoFinal(String egresoFinal) {
        this.egresoFinal = egresoFinal;
    }

    /**
     * Retorna la variable tipoEgreso
     * 
     * @return tipoEgreso
     */
    public String getTipoEgreso() {
        return tipoEgreso;
    }

    /**
     * Asigna la variable tipoEgreso
     * 
     * @param tipoEgreso
     * Variable a asignar en tipoEgreso
     */
    public void setTipoEgreso(String tipoEgreso) {
        this.tipoEgreso = tipoEgreso;
    }

    /**
     * Retorna la variable terceroInicial
     * 
     * @return terceroInicial
     */
    public String getTerceroInicial() {
        return terceroInicial;
    }

    /**
     * Asigna la variable terceroInicial
     * 
     * @param terceroInicial
     * Variable a asignar en terceroInicial
     */
    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    /**
     * Retorna la variable terceroFinal
     * 
     * @return terceroFinal
     */
    public String getTerceroFinal() {
        return terceroFinal;
    }

    /**
     * Asigna la variable terceroFinal
     * 
     * @param terceroFinal
     * Variable a asignar en terceroFinal
     */
    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    /**
     * Retorna la variable numeroBanco
     * 
     * @return numeroBanco
     */
    public String getNumeroBanco() {
        return numeroBanco;
    }

    /**
     * Asigna la variable numeroBanco
     * 
     * @param numeroBanco
     * Variable a asignar en numeroBanco
     */
    public void setNumeroBanco(String numeroBanco) {
        this.numeroBanco = numeroBanco;
    }

    /**
     * Retorna la variable cuentaInicial
     * 
     * @return cuentaInicial
     */
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     * 
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     * 
     * @return cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     * 
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable nombreBanco
     * 
     * @return nombreBanco
     */
    public String getNombreBanco() {
        return nombreBanco;
    }

    /**
     * Asigna la variable nombreBanco
     * 
     * @param nombreBanco
     * Variable a asignar en nombreBanco
     */
    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    /**
     * Retorna la variable fechaPago
     * 
     * @return fechaPago
     */
    public Date getFechaPago() {
        return fechaPago;
    }

    /**
     * Asigna la variable fechaPago
     * 
     * @param fechaPago
     * Variable a asignar en fechaPago
     */
    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    /**
     * Retorna la variable cuentaClienteDebitar
     * 
     * @return cuentaClienteDebitar
     */
    public String getCuentaClienteDebitar() {
        return cuentaClienteDebitar;
    }

    /**
     * Asigna la variable cuentaClienteDebitar
     * 
     * @param cuentaClienteDebitar
     * Variable a asignar en cuentaClienteDebitar
     */
    public void setCuentaClienteDebitar(String cuentaClienteDebitar) {
        this.cuentaClienteDebitar = cuentaClienteDebitar;
    }

    /**
     * Retorna la variable cuentaPrincipalAfiliada
     * 
     * @return cuentaPrincipalAfiliada
     */
    public String getCuentaPrincipalAfiliada() {
        return cuentaPrincipalAfiliada;
    }

    /**
     * Asigna la variable cuentaPrincipalAfiliada
     * 
     * @param cuentaPrincipalAfiliada
     * Variable a asignar en cuentaPrincipalAfiliada
     */
    public void setCuentaPrincipalAfiliada(String cuentaPrincipalAfiliada) {
        this.cuentaPrincipalAfiliada = cuentaPrincipalAfiliada;
    }

    /**
     * Retorna la variable identificador
     * 
     * @return identificador
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Asigna la variable identificador
     * 
     * @param identificador
     * Variable a asignar en identificador
     */
    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbNumeroBanco
     * 
     * @return listacmbNumeroBanco
     */
    public RegistroDataModelImpl getListacmbNumeroBanco() {
        return listacmbNumeroBanco;
    }

    /**
     * Asigna la lista listacmbNumeroBanco
     * 
     * @param listacmbNumeroBanco
     * Variable a asignar en listacmbNumeroBanco
     */
    public void setListacmbNumeroBanco(RegistroDataModelImpl listacmbNumeroBanco) {
        this.listacmbNumeroBanco = listacmbNumeroBanco;
    }

    /**
     * Retorna la lista listacmbAno
     * 
     * @return listacmbAno
     */
    public List<Registro> getListacmbAno() {
        return listacmbAno;
    }

    /**
     * Asigna la lista listacmbAno
     * 
     * @param listacmbAno
     * Variable a asignar en listacmbAno
     */
    public void setListacmbAno(List<Registro> listacmbAno) {
        this.listacmbAno = listacmbAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbNumeroInicial
     * 
     * @return listacmbNumeroInicial
     */
    public RegistroDataModelImpl getListacmbNumeroInicial() {
        return listacmbNumeroInicial;
    }

    /**
     * Asigna la lista listacmbNumeroInicial
     * 
     * @param listacmbNumeroInicial
     * Variable a asignar en listacmbNumeroInicial
     */
    public void setListacmbNumeroInicial(RegistroDataModelImpl listacmbNumeroInicial) {
        this.listacmbNumeroInicial = listacmbNumeroInicial;
    }

    /**
     * Retorna la lista listacmbNumeroFinal
     * 
     * @return listacmbNumeroFinal
     */
    public RegistroDataModelImpl getListacmbNumeroFinal() {
        return listacmbNumeroFinal;
    }

    /**
     * Asigna la lista listacmbNumeroFinal
     * 
     * @param listacmbNumeroFinal
     * Variable a asignar en listacmbNumeroFinal
     */
    public void setListacmbNumeroFinal(RegistroDataModelImpl listacmbNumeroFinal) {
        this.listacmbNumeroFinal = listacmbNumeroFinal;
    }

    /**
     * Retorna la lista listacmbTipo
     * 
     * @return listacmbTipo
     */
    public RegistroDataModelImpl getListacmbTipo() {
        return listacmbTipo;
    }

    /**
     * Asigna la lista listacmbTipo
     * 
     * @param listacmbTipo
     * Variable a asignar en listacmbTipo
     */
    public void setListacmbTipo(RegistroDataModelImpl listacmbTipo) {
        this.listacmbTipo = listacmbTipo;
    }

    /**
     * Retorna la lista listacmbTerceroInicial
     * 
     * @return listacmbTerceroInicial
     */
    public RegistroDataModelImpl getListacmbTerceroInicial() {
        return listacmbTerceroInicial;
    }

    /**
     * Asigna la lista listacmbTerceroInicial
     * 
     * @param listacmbTerceroInicial
     * Variable a asignar en listacmbTerceroInicial
     */
    public void setListacmbTerceroInicial(RegistroDataModelImpl listacmbTerceroInicial) {
        this.listacmbTerceroInicial = listacmbTerceroInicial;
    }

    /**
     * Retorna la lista listacmbTerceroFinal
     * 
     * @return listacmbTerceroFinal
     */
    public RegistroDataModelImpl getListacmbTerceroFinal() {
        return listacmbTerceroFinal;
    }

    /**
     * Asigna la lista listacmbTerceroFinal
     * 
     * @param listacmbTerceroFinal
     * Variable a asignar en listacmbTerceroFinal
     */
    public void setListacmbTerceroFinal(RegistroDataModelImpl listacmbTerceroFinal) {
        this.listacmbTerceroFinal = listacmbTerceroFinal;
    }

    /**
     * Retorna la lista listacmbCuentaInicial
     * 
     * @return listacmbCuentaInicial
     */
    public RegistroDataModelImpl getListacmbCuentaInicial() {
        return listacmbCuentaInicial;
    }

    /**
     * Asigna la lista listacmbCuentaInicial
     * 
     * @param listacmbCuentaInicial
     * Variable a asignar en listacmbCuentaInicial
     */
    public void setListacmbCuentaInicial(RegistroDataModelImpl listacmbCuentaInicial) {
        this.listacmbCuentaInicial = listacmbCuentaInicial;
    }

    /**
     * Retorna la lista listacmbCuentaFinal
     * 
     * @return listacmbCuentaFinal
     */
    public RegistroDataModelImpl getListacmbCuentaFinal() {
        return listacmbCuentaFinal;
    }

    /**
     * Asigna la lista listacmbCuentaFinal
     * 
     * @param listacmbCuentaFinal
     * Variable a asignar en listacmbCuentaFinal
     */
    public void setListacmbCuentaFinal(RegistroDataModelImpl listacmbCuentaFinal) {
        this.listacmbCuentaFinal = listacmbCuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * @return the claseTransaccion
	 */
	public String getClaseTransaccion() {
		return claseTransaccion;
	}

	/**
	 * @param claseTransaccion the claseTransaccion to set
	 */
	public void setClaseTransaccion(String claseTransaccion) {
		this.claseTransaccion = claseTransaccion;
	}

	/**
	 * @return the tipoCuentaCliente
	 */
	public String getTipoCuentaCliente() {
		return tipoCuentaCliente;
	}

	/**
	 * @param tipoCuentaCliente the tipoCuentaCliente to set
	 */
	public void setTipoCuentaCliente(String tipoCuentaCliente) {
		this.tipoCuentaCliente = tipoCuentaCliente;
	}

	/**
	 * @return the paramManejaPlanoBanOccidente
	 */
	public boolean isParamManejaPlanoBanOccidente() {
		return paramManejaPlanoBanOccidente;
	}

	/**
	 * @param paramManejaPlanoBanOccidente the paramManejaPlanoBanOccidente to set
	 */
	public void setParamManejaPlanoBanOccidente(boolean paramManejaPlanoBanOccidente) {
		this.paramManejaPlanoBanOccidente = paramManejaPlanoBanOccidente;
	}

	public boolean isTodosFiltro() {
		return todosFiltro;
	}

	public void setTodosFiltro(boolean todosFiltro) {
		this.todosFiltro = todosFiltro;
	}
   
}
