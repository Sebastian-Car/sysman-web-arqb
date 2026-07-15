/*-
 * EalmacencontabilidadmControlador.java
 *
 * 1.0
 * 
 * 20/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilizar.ejb.EjbContabilizarAlmacenCeroRemote;
import com.sysman.contabilizar.enums.EalmacencontabilidadMControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase reservada para gestionar la opción Contabilización Mensual
 * Almacen.
 *
 * @version 1.0, 20/06/2018
 * @author dnino
 */
@ManagedBean
@ViewScoped
public class EalmacencontabilidadmControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por la
     * que se ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el año seleccionado.
     */
    private int ano;
    /**
     * Variable que almacena el mes seleccionado.
     */
    private int mes;

    /**
     * Variabale que almacena el tercero seleccionado
     */
    private String tercero;

    /**
     * Variabale que almacena la sucursal del tercero seleccionado
     */
    private String sucursal;

    /**
     * Variable que almacena el centro de costo seleccionado
     */
    private String centroCosto;

    /**
     * Variable que almacena el tipo de comprobante.
     */
    private String tipo;
    /**
     * Variable que almacena el tipo de comprobante para el
     * comprobante de retiro de activos.
     */
    private String tipoRetiroActivo;
    /**
     * Variable que almacena el numero de comprobante.
     */
    private String numero;
    /**
     * Variable que almacena la fecha del comprobante.
     */
    private String fechaInterface;
    /**
     * Variable que almacena la cadena de fecha inicial del año y mes
     * seleccionados.
     */
    private String fecha;
    /**
     * Variable que construye la fecha con año y mes seleccionado.
     */
    private Date fechaP;
    /**
     * Variable que almacena el proceso obtenido mediante parámetro.
     */
    private String proceso;
    /**
     * Variable que obtiene el día de la fecha seleccionada.
     */
    private int dia;
    /**
     * Variable que obtiene el último día de mes de la fecha
     * seleccionada.
     */
    private int diaU;
    /**
     * Variable que almacena el parámetro MANEJA NIIF EN ALMACEN.
     */
    private String niif;
    /**
     * Variable que almacena el numero de comprobante.
     */
    private String centro;
    /**
     * Variable que almacena el valor del check Pasar a Niif.
     */
    private boolean pasarNiif;
    /**
     * Variable que almacena el valor del check Diferidos Niif.
     */
    private boolean diferidosNiif;

    /**
     * Variable que gestiona la visibilidad de combos
     */
    private boolean visibleUnicoTercero;

    /**
     * Variable que almacena el parámetro "MANEJA INTERFAZ POR
     * CRITERIOS ESP".
     */
    private String criterios;
    /**
     * Variable que almacena el parámetro "MANEJA NIIF EN ALMACEN".
     */
    private String distribucion;
    /**
     * Variable que almacena el parámetro "MANEJA DISTRIBUCION POR
     * AUXILIARES EN GASTOS".
     */
    private String configuracion;

    private String manejaInterfazTransicion;

    private String transicion;
    /**
     * Variable que controla la visibilidad de CK y LB relacionados
     * con NIIF.
     */
    private boolean visNiif;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /*
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbContabilizarAlmacenCeroRemote ejbContabilizarAlmacenCero;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que despliega los años activos.
     */
    private List<Registro> listaAno;
    /**
     * Lista de registros de los meses del anio
     */
    private List<Registro> listaMes;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista de registros de terceros
     */
    private RegistroDataModelImpl listaTercero;
    /**
     * Lista de registros de los centros de costo
     */
    private RegistroDataModelImpl listaCentroCosto;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de EalmacencontabilidadmControlador
     */
    public EalmacencontabilidadmControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.E_ALMACEN_CONTABILIDAD_M_CONTROLADOR
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
        abrirFormulario();
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaMes();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTercero();
        cargarListaCentroCosto();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {

        try {
            ano = SysmanFunciones.ano(new Date());
            mes = SysmanFunciones.mes(new Date());
            generarNumFecha();
            pasarNiif = true;
            criterios = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA INTERFAZ POR CRITERIOS ESP",
                                            modulo,
                                            new Date(), true), "NO");

            distribucion = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA DISTRIBUCION POR AUXILIARES EN GASTOS",
                                            modulo,
                                            new Date(), true), "NO");
            proceso = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA PROCESO ESPECIAL INTERFAZ ALMACEN",
                                            modulo,
                                            new Date(), true), "NO");
            centro = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA INTERFACE ALMACEN MENSUAL POR CENTRO COSTO",
                                            modulo,
                                            new Date(), true), "IMN");
            configuracion = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA CONFIGURACION ESPECIAL DE TRASLADOS EN ALMACEN",
                                            modulo,
                                            new Date(), true), "IMN");
            niif = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA NIIF EN ALMACEN",
                                            modulo,
                                            new Date(), true), "NO");
            tipo = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "TIPO COMPROBANTE INTERFASE MENSUAL ALMACEN",
                                            modulo,
                                            new Date(), true), "AL1");
            tipoRetiroActivo = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "TIPO COMPROBANTE INTERFAZ RETIRO DE ACTIVOS",
                                            modulo,
                                            new Date(), true), "AL2");

            manejaInterfazTransicion = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA INTERFAZ DE TRANSICION EN ALMACEN",
                                            modulo,
                                            new Date(), true), "NO");
            transicion = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "TIPO COMPROBANTE INTERFAZ DE TRANSICION",
                                            modulo,
                                            new Date(), true), "AL3");

            visibleUnicoTercero = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "INTERFAZ ALMACEN CON UNICO TERCERO",
                                            modulo,
                                            new Date(), true), "NO"));

            if ("SI".equals(niif)) {
                visNiif = true;
            }
            else {
                visNiif = false;
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaANO
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EalmacencontabilidadMControladorUrlEnum.URL4410
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
     * Carga la lista listaMes
     *
     *
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EalmacencontabilidadMControladorUrlEnum.URL4420
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
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTercero() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EalmacencontabilidadMControladorUrlEnum.URL4440
                                                        .getValue());

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NIT.getName());

    }

    /**
     * 
     * Carga la lista listaCentroCosto
     *
     */
    public void cargarListaCentroCosto() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EalmacencontabilidadMControladorUrlEnum.URL4430
                                                        .getValue());

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     *
     */
    public void oprimirAceptar() {
        
    	if (visibleUnicoTercero) {
    		if (visNiif && tercero.isEmpty()
	            || tercero == null && centroCosto.isEmpty()
	            || centroCosto == null) {
	
	            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4356"));
	            return;
	        }
    	} else {
    		
    		centroCosto = SysmanConstantes.CONS_CENTRO;
    		tercero = SysmanConstantes.CONS_TERCERO;
    		sucursal = SysmanConstantes.CONS_SUCURSAL;
    		
    	}
        	iniciarInterface();
            interfazTransicion();
        
    }

    private void interfazTransicion() {
        if ("SI".equals(manejaInterfazTransicion)) {
            try {
                ejbContabilizarAlmacenCero.insertarComprobTransicion(compania,
                                ano, mes, transicion,
                                SysmanFunciones.ultimoDiaDate(SysmanFunciones
                                                .convertirAFecha(fecha)),
                                SessionUtil.getUser().toString());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB4264"));
            }
            catch (SystemException | ParseException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Metodo que al ser invocado valida que funcion de interfaz se
     * debe ejecutar
     */
    public void iniciarInterface() {
        /**
         * Variable que almacena la cadena que retorna la funcion del
         * ejbContabilizarAlmacenCero
         */
        String respuesta = "";
        String respuestaRetiro = "";
        String nombreSalida = "Normal";
        String companiaDestino = compania;

        // variables que contiene el nombre de los reportes a generar
        // segun corresponda
        String nombrePDF = "001501INT";
        Map<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> reemplazosRetiro = new HashMap<>();
        Map<String, Object> parametro = new HashMap<>();
        Map<String, Object> parametroRetiro = new HashMap<>();
        reemplazos.put("compania", compania);
        reemplazos.put("fecha", fechaInterface);

        reemplazos.put("ano", ano);
        reemplazos.put("tipoCpte", tipo);
        reemplazos.put("numeroPptoInicial", numero);
        reemplazos.put("numeroPptoFinal", numero);

        parametro.put("PR_TITULO_CER_REG", "NOMDIS");
        parametro.put("PR_TITULO_RP_RO", "NOMRESERVA");
        parametro.put("PR_DESCRIPCION", "ALMACEN");
        try {
            if ("SI".equals(criterios)) {
                // --> InterfaceAlmacenXcriterio(Compania, Me!Ano,
                // Me!Mes,
                // Me!FechaInterface, Me!Tipo, Me!Numero)
            }
            else {
                if ("SI".equals(distribucion) && "SI".equals(proceso)) {
                    // -->
                    // InterfaceAlmacenAjustesNivelesconCC(Compania,
                    // Me.Ano, Me.Mes, Me.FechaInterface, Me.Tipo,
                    // Me.Numero)
                }
                else {
                    if (pasarNiif) {

                        String companiaInsertar;

                        companiaInsertar = ejbSysmanUtil
                                        .consultarParametro(compania,
                                                        "COMPANIA PARA INSERTAR COMPROBANTE ALMACEN",
                                                        modulo, new Date(),
                                                        true);

                        if (companiaInsertar == null) {
                            companiaInsertar = compania;
                        }
                        if ("100".equals(companiaInsertar)) {
                            companiaDestino = ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            idioma.getString(
                                                                            "TB_TB4178"),
                                                            modulo,
                                                            new Date(),
                                                            true);
                        }
                        respuesta = ejbContabilizarAlmacenCero
                                        .contabilizarAlmcnH(compania,
                                                        (!"100".equals(companiaInsertar))
                                                            ? companiaInsertar
                                                            : companiaDestino,
                                                        SysmanFunciones.convertirAFecha(
                                                                        fechaInterface),
                                                        tipo,
                                                        Integer.valueOf(numero),
                                                        tercero, sucursal,
                                                        centroCosto,
                                                        SessionUtil.getUser()
                                                                        .getCodigo());
                    }
                    else if (diferidosNiif) {
                        // -->
                        // InterfaceAlmacenDiferidosNIIF(par("COMPANIA
                        // PARA INSERTAR COMPROBANTE ALMACEN",
                        // Getcompany()), Me!Ano, Me!Mes,
                        // Me!FechaInterface, Me!Tipo, Me!Numero)
                    }
                    else {
                        // -->
                        // InterfaceAlmacenAjustesNiveles(Compania,
                        // Me.Ano, Me.Mes, Me.FechaInterface,
                        // Me.Tipo,
                        // Me.Numero)
                    }
                }

            }
            if ("SI".equals(configuracion)) {
                // --> InterfaceAlmacenRetiroActivos(Compania, Me!Ano,
                // Me!Mes, Me!FechaInterface, Nz(TraerParametro("TIPO
                // COMPROBANTE INTERFAZ RETIRO DE ACTIVOS",
                // Getcompany()),
                // "AL2"), Me!Numero)
                reemplazosRetiro.putAll(reemplazos);
                reemplazosRetiro.put("tipoCpte", tipoRetiroActivo);
                parametroRetiro.putAll(parametro);
                Reporteador.resuelveConsulta(nombrePDF,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazosRetiro, parametroRetiro);
                respuestaRetiro = ejbContabilizarAlmacenCero
                                .contabilizarRetiroActivos(compania,
                                                ano, mes,
                                                SysmanFunciones.convertirAFecha(
                                                                fechaInterface),
                                                tipoRetiroActivo,
                                                Integer.valueOf(numero),
                                                SessionUtil.getUser()
                                                                .getCodigo());
            }
        }
        catch (SystemException | NumberFormatException | ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
            return;

        }

        ByteArrayInputStream[] salida = new ByteArrayInputStream[3];

        ByteArrayInputStream salidaTxt = null;
        ByteArrayInputStream salidaNombrePDF = null;
        ByteArrayInputStream salidaNombrePDFRetiro = null;
        int cantidad = 0;
        String[] nombresInformes = new String[3];
        String respuestaFinal = "";
        try {
            if (!("".equals(respuesta))) {
                respuestaFinal = respuesta;
            }
            if (!("".equals(respuestaRetiro))) {
                String[] con = { respuestaFinal,
                                 System.lineSeparator(), respuestaRetiro };
                respuestaFinal = SysmanFunciones.concatenar(con);
            }

            if (!("".equals(respuestaFinal))) {
                salidaTxt = JsfUtil.serializarPlano(
                                respuestaFinal);
            }

        }
        catch (JRException | IOException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

        Reporteador.resuelveConsulta(nombrePDF,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazos, parametro);
        try {

            salidaNombrePDF = JsfUtil.serializarReporte(
                            nombrePDF, parametro,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);

        }
        catch (JRException | IOException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            if (("".equals(respuesta))) {
                String[] con = { e.getMessage().toString(),
                                 System.lineSeparator(), respuestaFinal };
                respuestaFinal = SysmanFunciones.concatenar(con);
            }
            else if (!("No existen datos")
                            .equals(e.getMessage().toString())) {
                logger.error(e.getMessage(), e);
            }
        }
        finally {
            try {
                if ("SI".equals(configuracion)) {
                    salidaNombrePDFRetiro = JsfUtil.serializarReporte(
                                    nombrePDF, parametroRetiro,
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    ReportesBean.FORMATOS.PDF);
                }
            }
            catch (JRException | IOException | SysmanException e1) {
                JsfUtil.agregarMensajeError(e1.getMessage());
            }
            finally {

                if (!(salidaTxt == null)) {
                    salida[cantidad] = salidaTxt;
                    nombresInformes[cantidad] = "interfazMensual.txt";
                    cantidad++;
                }
                if (!(salidaNombrePDF == null)) {
                    salida[cantidad] = salidaNombrePDF;
                    nombresInformes[cantidad] = nombrePDF + ".pdf";
                    cantidad++;
                }
                if (!(salidaNombrePDFRetiro == null)) {
                    salida[cantidad] = salidaNombrePDFRetiro;
                    nombresInformes[cantidad] = nombrePDF + "Retiro.pdf";
                    cantidad++;
                }

                if (cantidad > 0) {
                    try {
                        archivoDescarga = JsfUtil
                                        .exportarComprimidoGeneralStreamed(
                                                        salida,
                                                        nombresInformes,
                                                        nombreSalida);
                    }
                    catch (JRException | IOException | SQLException
                                    | DRException e) {
                        JsfUtil.agregarMensajeError(e.getMessage());
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes();
        cargarListaCentroCosto();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes
     * 
     * 
     */
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        generarNumFecha();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Niif
     * 
     */
    public void cambiarNiif() {
        // <CODIGO_DESARROLLADO>
        if (pasarNiif) {
            diferidosNiif = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control NiifDiferido
     * 
     * 
     */
    public void cambiarNiifDiferido() {
        // <CODIGO_DESARROLLADO>

        try {
            if (diferidosNiif) {
                pasarNiif = false;

                tipo = SysmanFunciones
                                .nvlStr(ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "TIPO COMPROBANTE INTERFASE DIFERIDOS ACTIVO NIIF",
                                                modulo,
                                                new Date(), true), "ADI");

            }
            else {

                tipo = SysmanFunciones
                                .nvlStr(ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "TIPO COMPROBANTE INTERFASE MENSUAL ALMACEN",
                                                modulo,
                                                new Date(), true), "AL1");

            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tercero = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NIT.getName()), "")
                        .toString();

        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.SUCURSAL
                                                        .getName()),
                                        "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCosto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCosto = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    /**
     * Metodo que al ser invocadp genera el numero y fecha de interfaz
     */
    public void generarNumFecha() {
        try {

            fecha = SysmanFunciones.concatenar("01/",
                            ejbSysmanUtil.generarCerosIzquierda(mes, 2),
                            "/" + ano);

            fechaP = SysmanFunciones.convertirAFecha(fecha);
            diaU = SysmanFunciones.ultimoDiaInt(fechaP);
            dia = SysmanFunciones.dia(fechaP);
            fechaInterface = ejbSysmanUtil.generarCerosIzquierda(diaU, 2) + "/"
                + ejbSysmanUtil.generarCerosIzquierda(mes, 2) + "/" + ano;
            numero = ano + ejbSysmanUtil.generarCerosIzquierda(mes, 2)
                + ejbSysmanUtil.generarCerosIzquierda(dia, 4);
        }
        catch (SystemException | ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable tercero
     * 
     * @return tercero
     */
    public String getTercero() {
        return tercero;
    }

    /**
     * Asigna la variable tercero
     * 
     * @param tercero
     * Variable a asignar en tercero
     */
    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    /**
     * Retorna la variable centroCosto
     * 
     * @return centroCosto
     */
    public String getCentroCosto() {
        return centroCosto;
    }

    /**
     * Asigna la variable centroCosto
     * 
     * @param centroCosto
     * Variable a asignar en centroCosto
     */
    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    /**
     * Retorna la variable tipo
     * 
     * @return tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Asigna la variable tipo
     * 
     * @param tipo
     * Variable a asignar en tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna la variable numero
     * 
     * @return numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Asigna la variable numero
     * 
     * @param numero
     * Variable a asignar en numero
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * Retorna la variable fechaInterface
     * 
     * @return fechaInterface
     */
    public String getFechaInterface() {
        return fechaInterface;
    }

    /**
     * Asigna la variable fechaInterface
     * 
     * @param fechaInterface
     * Variable a asignar en fechaInterface
     */
    public void setFechaInterface(String fechaInterface) {
        this.fechaInterface = fechaInterface;
    }

    /**
     * Retorna la variable pasarNiif
     * 
     * @return pasarNiif
     */
    public boolean isPasarNiif() {
        return pasarNiif;
    }

    /**
     * Asigna la variable pasarNiif
     * 
     * @param pasarNiif
     * Variable a asignar en pasarNiif
     */
    public void setPasarNiif(boolean pasarNiif) {
        this.pasarNiif = pasarNiif;
    }

    /**
     * Retorna la variable diferidosNiif
     * 
     * @return diferidosNiif
     */
    public boolean isDiferidosNiif() {
        return diferidosNiif;
    }

    /**
     * Asigna la variable diferidosNiif
     * 
     * @param diferidosNiif
     * Variable a asignar en diferidosNiif
     */
    public void setDiferidosNiif(boolean diferidosNiif) {
        this.diferidosNiif = diferidosNiif;
    }

    /**
     * Retorna la variable proceso
     * 
     * @return proceso
     */
    public String getProceso() {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     * 
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable niif
     * 
     * @return niif
     */
    public String getNiif() {
        return niif;
    }

    /**
     * Asigna la variable niif
     * 
     * @param niif
     * Variable a asignar en niif
     */
    public void setNiif(String niif) {
        this.niif = niif;
    }

    /**
     * Retorna la variable centro
     * 
     * @return centro
     */
    public String getCentro() {
        return centro;
    }

    /**
     * Asigna la variable centro
     * 
     * @param centro
     * Variable a asignar en centro
     */
    public void setCentro(String centro) {
        this.centro = centro;
    }

    /**
     * Retorna la variable criterios
     * 
     * @return criterios
     */
    public String getCriterios() {
        return criterios;
    }

    /**
     * Asigna la variable criterios
     * 
     * @param criterios
     * Variable a asignar en criterios
     */
    public void setCriterios(String criterios) {
        this.criterios = criterios;
    }

    /**
     * Retorna la variable distribucion
     * 
     * @return distribucion
     */
    public String getDistribucion() {
        return distribucion;
    }

    /**
     * Asigna la variable distribucion
     * 
     * @param distribucion
     * Variable a asignar en distribucion
     */
    public void setDistribucion(String distribucion) {
        this.distribucion = distribucion;
    }

    /**
     * Retorna la variable configuracion
     * 
     * @return configuracion
     */
    public String getConfiguracion() {
        return configuracion;
    }

    /**
     * Asigna la variable configuracion
     * 
     * @param configuracion
     * Variable a asignar en configuracion
     */
    public void setConfiguracion(String configuracion) {
        this.configuracion = configuracion;
    }

    public String getManejaInterfazTransicion() {
        return manejaInterfazTransicion;
    }

    public void setManejaInterfazTransicion(String manejaInterfazTransicion) {
        this.manejaInterfazTransicion = manejaInterfazTransicion;
    }

    public String getTransicion() {
        return transicion;
    }

    public void setTransicion(String transicion) {
        this.transicion = transicion;
    }

    /**
     * Retorna la variable visNiif
     * 
     * @return visNiif
     */
    public boolean getVisNiif() {
        return visNiif;
    }

    /**
     * Asigna la variable visNiif
     * 
     * @param visNiif
     * Variable a asignar en visNiif
     */
    public void setVisNiif(boolean visNiif) {
        this.visNiif = visNiif;
    }

    /**
     * Retorna la variable fecha
     * 
     * @return fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Asigna la variable fecha
     * 
     * @param fecha
     * Variable a asignar en fecha
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Retorna la variable dia
     * 
     * @return dia
     */
    public int getDia() {
        return dia;
    }

    /**
     * Asigna la variable dia
     * 
     * @param dia
     * Variable a asignar en dia
     */
    public void setDia(int dia) {
        this.dia = dia;
    }

    /**
     * Retorna la variable diaU
     * 
     * @return diaU
     */
    public int getDiaU() {
        return diaU;
    }

    /**
     * Asigna la variable diaU
     * 
     * @param diaU
     * Variable a asignar en diaU
     */
    public void setDiaU(int diaU) {
        this.diaU = diaU;
    }

    /**
     * Retorna la variable fechaP
     * 
     * @return fechaP
     */
    public Date getFechaP() {
        return fechaP;
    }

    /**
     * Asigna la variable fechaP
     * 
     * @param fechaP
     * Variable a asignar en fechaP
     */
    public void setFechaP(Date fechaP) {
        this.fechaP = fechaP;
    }

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
    /* @return the nombreConcepto */
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
    public List<Registro> getListaMes() {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en listaMes
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    /**
     * Retorna la lista listaCentroCosto
     * 
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCosto() {
        return listaCentroCosto;
    }

    /**
     * Asigna la lista listaCentroCosto
     * 
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
        this.listaCentroCosto = listaCentroCosto;
    }

    public boolean isVisibleUnicoTercero() {
        return visibleUnicoTercero;
    }

    public void setVisibleUnicoTercero(boolean visibleUnicoTercero) {
        this.visibleUnicoTercero = visibleUnicoTercero;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
