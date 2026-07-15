/*-
 * VolantesDePagoControlador.java
 *
 * 1.0
 * 
 * 17/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralRemote;
import com.sysman.nomina.enums.volantesDePagoControladorEnum;
import com.sysman.nomina.enums.volantesDePagoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.services.ServidorCorreo;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
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
import javax.mail.MessagingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Permite la impresi�n de los volantes de pago por empleado, centro
 * de costo y todos.
 *
 * @version 1.0, 17/01/2018
 * @author vmolano
 */
@ManagedBean
@ViewScoped
public class VolantesDePagoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Id del empleado actualmente seleccionado
     */
    private String idEmpleado;

    /**
     * N�mero del a�o actualmente seleccionado
     */
    private String ano;

    private String periodo;
    private String mes;

    private String centroCosto;
    private String observacion;
    private String observacion1;
    private String proceso;

    private String opcion;

    private String nombreCompleto;
    private String encabezado;

    private ServidorCorreo correo;
    private String servidor;
    private String usuario;
    private String clave;
    private boolean visibleVolantesInst;
    private boolean visibleVolantes;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de centros de costo cuando se genera por rangos.
     */
    private List<Registro> listaidCentroDeCosto;
    /**
     * Listado de procesos sobre los cuales se puede imprimir
     * volantes.
     */
    private List<Registro> listaProceso;
    /**
     * Listado de a�os para los cuales se puede imprimir volantes.
     */
    private List<Registro> listaAno;
    /**
     * Listado de meses para los cuales se puede imprimir volantes.
     */
    private List<Registro> listaMes;
    /**
     * Listado de periodos para los cuales se puede imprimir volantes.
     */
    private List<Registro> listaPeriodo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de empleados activos a los que se les puede imprimir
     * volantes.
     */
    private RegistroDataModelImpl listaidDeEmpleado;

    private StreamedContent archivoDescarga;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbNominaCeroGeneralRemote ejbNominaCero;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de VolantesDePagoControlador
     */
    public VolantesDePagoControlador() {
        super();
        compania = SessionUtil.getCompania();
        correo = new ServidorCorreo();
        try {
            // 1603
            numFormulario = GeneralCodigoFormaEnum.VOLANTES_DE_PAGO_CONTROLADOR
                            .getCodigo();

            opcion = "2";

            validarPermisos();
            // <INI_ADICIONAL>
            proceso = SessionUtil.getSessionVar("procesoNomina").toString();
            ano = SessionUtil.getSessionVar("anioNomina").toString();
            mes = SessionUtil.getSessionVar("mesNomina").toString();
            periodo = SessionUtil.getSessionVar("periodoNomina").toString();
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
        cargarListaidCentroDeCosto();
        cargarListaProceso();
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaidDeEmpleado();
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
        try {
            String nomProceso = service.buscarEnLista(proceso,
                            GeneralParameterEnum.ID_DE_PROCESO.getName(),
                            "NOMBRE_PROCESO", listaProceso);

            String nomPeriodo = service.buscarEnLista(periodo,
                            GeneralParameterEnum.PERIODO.getName(),
                            GeneralParameterEnum.NOMBRE.getName(),
                            listaPeriodo);

            encabezado = idioma.getString("TB_TB4046").replace("s$proceso$s",
                            nomProceso).replace("s$mes$s",
                                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                            .parseInt(mes)])
                            .replace("s$ano$s", ano)
                            .replace("s$periodo$s", nomPeriodo);

            servidor = ejbSysmanUtil.consultarParametro(compania,
                            "SERVIDOR PARA ENVIO EMAIL",
                            SessionUtil.getModulo(), new Date(), false);

            usuario = ejbSysmanUtil.consultarParametro(compania,
                            "USUARIO PARA ENVIO EMAIL",
                            SessionUtil.getModulo(), new Date(), false);

            visibleVolantesInst = "SI"
                            .equals(ejbSysmanUtil.consultarParametro(compania,
                                            "PERMITIR GENERAR VOLANTES EN PDF AL CORREO INSTITUCIONAL",
                                            SessionUtil.getModulo(), new Date(),
                                            true));

            visibleVolantes = "SI"
                            .equals(ejbSysmanUtil.consultarParametro(compania,
                                            "PERMITIR GENERAR VOLANTES EN PDF AL CORREO",
                                            SessionUtil.getModulo(), new Date(),
                                            true));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista de centros de costo.
     */
    public void cargarListaidCentroDeCosto() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaidCentroDeCosto = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            volantesDePagoControladorUrlEnum.URL0006
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
     * Carga la lista proceso disponibles para impresi�n de volantes.
     */
    public void cargarListaProceso() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaProceso = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            volantesDePagoControladorUrlEnum.URL0001
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
     * Carga la lista de a�os
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            volantesDePagoControladorUrlEnum.URL0002
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
     * Carga la lista de meses para el a�o actualmente seleccionado
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listaMes = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            volantesDePagoControladorUrlEnum.URL0003
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
     * Carga la lista de periodos para el a�o, mes y proceso actual.
     */
    public void cargarListaPeriodo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.MES.getName(), mes);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try {
            listaPeriodo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            volantesDePagoControladorUrlEnum.URL0004
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
     * Carga la lista empleados
     */
    public void cargarListaidDeEmpleado() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        volantesDePagoControladorUrlEnum.URL0005
                                                        .getValue());
        listaidDeEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, volantesDePagoControladorEnum.ID_DE_EMPLEADO
                                        .getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * Metodo que valida si el IdEmpleado o IdProceso viene vacio , o
     * nulo o 0
     *
     * @return true o false
     */
    private boolean validarVacios() {

        boolean vacios = false;

        boolean procesoVacio = "0".equals(proceso)
            || SysmanFunciones.validarVariableVacio(proceso);
        boolean anoVacio = "0".equals(ano)
            || SysmanFunciones.validarVariableVacio(ano);
        boolean mesVacio = "0".equals(mes)
            || SysmanFunciones.validarVariableVacio(mes);
        boolean periodoVacio = "0".equals(periodo)
            || SysmanFunciones.validarVariableVacio(periodo);

        boolean validaEmpleado = idEmpleado.isEmpty();
        boolean validaCentroCosto = "0".equals(centroCosto)
            || SysmanFunciones.validarVariableVacio(centroCosto);

        boolean basicosVacios = procesoVacio || anoVacio || mesVacio
            || periodoVacio;

        boolean especificosVacios = "1".equals(opcion) && validaEmpleado
            || ("3".equals(opcion) && validaCentroCosto);

        if (basicosVacios || especificosVacios) {
            vacios = true;
        }
        return vacios;
    }

    private String getReporte() {

        String parReporte = getParametro("FORMATO VOLANTE DE PAGO", false,
                        new Date());

        if (SysmanFunciones.validarVariableVacio(parReporte)) {
            return volantesDePagoControladorEnum.REPORTE000141.getValue();
        }
        else {
            return parReporte;
        }

    }

    private String getParametro(String nombre, boolean indMayus, Date date) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre,
                            SessionUtil.getModulo(), date, indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return "";
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        if (validarVacios()) {
            return;
        }

        String parReporte = getReporte();

        try {
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            Date fechaFin = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(proceso), Integer.parseInt(ano),
                            Integer.parseInt(mes), Integer.parseInt(periodo),
                            false, true);
            Date fechaIni = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(proceso), Integer.parseInt(ano),
                            Integer.parseInt(mes), Integer.parseInt(periodo),
                            true, true);

            String nombreEmpresa = ejbNominaCero.getDatoEmpresa(compania, 0);
            String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();

            HashMap<String, Object> reemplazar = new HashMap<>();

            String empleadoIni = "0";
            String empleadoFin = "999999999";
            String centroCostoIni = SysmanConstantes.DEFECTOINICIAL_STRING;
            String centroCostoFin = SysmanConstantes.DEFECTOFINAL_STRING;

            if ("1".equals(opcion)) { // Por empleado
                empleadoIni = idEmpleado;
                empleadoFin = idEmpleado;
            }
            else if ("3".equals(opcion)) { // por centro de costo
                centroCostoIni = centroCosto;
                centroCostoFin = centroCosto;
            }

            reemplazar.put(volantesDePagoControladorEnum.EMPLEADOINI.getValue(),
                            empleadoIni);

            reemplazar.put(volantesDePagoControladorEnum.EMPLEADOFIN.getValue(),
                            empleadoFin);

            reemplazar.put(volantesDePagoControladorEnum.CENTROCOSTOINI
                            .getValue(), centroCostoIni);

            reemplazar.put(volantesDePagoControladorEnum.CENTROCOSTOFIN
                            .getValue(), centroCostoFin);

            reemplazar.put(volantesDePagoControladorEnum.PROCESO.getValue()
                            .toLowerCase(), proceso);
            reemplazar.put(GeneralParameterEnum.ANO.getName().toLowerCase(),
                            ano);
            reemplazar.put(GeneralParameterEnum.MES.getName().toLowerCase(),
                            mes);
            reemplazar.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                            periodo);

            Reporteador.resuelveConsulta(parReporte.toUpperCase(),
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            String tituloVolante = SysmanFunciones.concatenar(
                            SysmanFunciones.convertirAFechaCadena(fechaIni),
                            " ",
                            idioma.getString("TB_TB3685"), " ",
                            SysmanFunciones.convertirAFechaCadena(fechaFin));

            parametros.put("PR_TITULO", tituloVolante);
            parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
            boolean bugCompania = SessionUtil.getCompaniaIngreso().getNit()
                            .equals("890201222") ? true : false;
            parametros.put("PR_COMPANIA_BUG", bugCompania);
            String rutaEncabezado = ejbSysmanUtil.consultarParametro(compania,
                            "RUTA IMAGEN ENCABEZADO VOLANTE DE PAGOS",
                            SessionUtil.getModulo(), new Date(), false);
            String rutaPiePagina = ejbSysmanUtil.consultarParametro(compania,
                            "RUTA IMAGEN PIEPAGINA VOLANTE DE PAGOS",
                            SessionUtil.getModulo(), new Date(), false);
            String nombreAutorizaPago =  ejbSysmanUtil.consultarParametro(compania,
                    "NOMBRE AUTORIZACION PAGO",
                    SessionUtil.getModulo(), new Date(), false);
            
            String cargoAutorizaPago =  ejbSysmanUtil.consultarParametro(compania,
                    "CARGO AUTORIZACION PAGO",
                    SessionUtil.getModulo(), new Date(), false);
            
            String nombreRecursosH = ejbSysmanUtil.consultarParametro(compania,
                    "NOMBRE JEFE RECURSOS HUMANOS",
                    SessionUtil.getModulo(), new Date(), false);
		    String cargoRecursosH = ejbSysmanUtil.consultarParametro(compania,
		            "CARGO JEFE RECURSOS HUMANOS",
		            SessionUtil.getModulo(), new Date(), false);
		    String nomLiqNomina = ejbSysmanUtil.consultarParametro(compania,
		            "NOMBRE DE QUIEN LIQUIDA NOMINA",
		            SessionUtil.getModulo(), new Date(), false);
		    String cargoLiqNomina = ejbSysmanUtil.consultarParametro(compania,
		            "CARGO DE QUIEN LIQUIDA NOMINA",
		            SessionUtil.getModulo(), new Date(), false);
            
            parametros.put("PR_NOMBREAUTORIZAPAGO",nombreAutorizaPago);
            
            parametros.put("PR_CARGOAUTORIZAPAGO",cargoAutorizaPago);
            
            parametros.put("PR_NOMBRECOMPANIA",nombreCompania);
            
            parametros.put("PR_FORMS_VOLANTES_DE_PAGO_OBSERVACION", observacion);
            
            parametros.put("PR_FORMS_VOLANTES_DE_PAGO_OBSERVACION1", observacion1);
            
            parametros.put("PR_NOMBRE_DE_QUIEN_LIQUIDA_NOMINA",nomLiqNomina);
            
			parametros.put("PR_CARGO_DE_QUIEN_LIQUIDA_NOMINA",cargoLiqNomina);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA            

            if (rutaEncabezado == null) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB999")
                                .replace("#parametro#",
                                                "RUTA IMAGEN ENCABEZADO VOLANTE DE PAGOS"));
                return;
            }
            if (rutaPiePagina == null) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB999")
                                .replace("#parametro#",
                                                "RUTA IMAGEN PIEPAGINA VOLANTE DE PAGOS"));
                return;
            }
            parametros.put("PR_ENCABEZADO",
                            rutaEncabezado);
            parametros.put("PR_PIEPAGINA", rutaPiePagina);
            parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS", nombreRecursosH);
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoRecursosH);

            long contar = service.getConteoConsulta(
                            parametros.get("PR_STRSQL").toString());
            if (contar > 0) {
                archivoDescarga = JsfUtil.exportarStreamed(parReporte,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.PDF);
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma
                                .getString(volantesDePagoControladorEnum.TG_NO_EXISTE
                                                .getValue()));
            }
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ", parReporte));
            Logger.getLogger(VolantesDePagoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        catch (SystemException | ParseException
                        | IOException | JRException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton ListadoEnviados en la
     * vista
     *
     *
     */
    public void oprimirListadoEnviados() {
        // <CODIGO_DESARROLLADO>
        try {
            String reporte = "001756Listadovolantescorreos";
            Map<String, Object> parametros = new HashMap<>();
            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar.put(volantesDePagoControladorEnum.PROCESO.getValue()
                            .toLowerCase(), proceso);
            reemplazar.put(GeneralParameterEnum.ANO.getName().toLowerCase(),
                            ano);
            reemplazar.put(GeneralParameterEnum.MES.getName().toLowerCase(),
                            mes);
            reemplazar.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                            periodo);

            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton VolanteCorreo en la vista
     * @throws MessagingException 
     *
     *
     */
    public void oprimirVolanteCorreo() throws MessagingException {
        // <CODIGO_DESARROLLADO>
        try {
            archivoDescarga = null;
            String parReporte = getReporte();

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE

            Map<String, Object> reemplazar = new HashMap<>();

            Date fechaFin = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(proceso), Integer.parseInt(ano),
                            Integer.parseInt(mes), Integer.parseInt(periodo),
                            false, true);
            Date fechaIni = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(proceso), Integer.parseInt(ano),
                            Integer.parseInt(mes), Integer.parseInt(periodo),
                            true, true);
            String nombreEmpresa = ejbNominaCero.getDatoEmpresa(compania, 0);

            
            reemplazar.put(volantesDePagoControladorEnum.PROCESO.getValue()
                            .toLowerCase(), proceso);
            reemplazar.put(GeneralParameterEnum.ANO.getName().toLowerCase(),
                            ano);
            reemplazar.put(GeneralParameterEnum.MES.getName().toLowerCase(),
                            mes);
            reemplazar.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                            periodo);
            String rutaEncabezado = ejbSysmanUtil.consultarParametro(compania,
                            "RUTA IMAGEN ENCABEZADO VOLANTE DE PAGOS",
                            SessionUtil.getModulo(), new Date(), false);
            String rutaPiePagina = ejbSysmanUtil.consultarParametro(compania,
                            "RUTA IMAGEN PIEPAGINA VOLANTE DE PAGOS",
                            SessionUtil.getModulo(), new Date(), false); 
            String nombreRecursosH = ejbSysmanUtil.consultarParametro(compania,
		                    "NOMBRE JEFE RECURSOS HUMANOS",
		                    SessionUtil.getModulo(), new Date(), false);
            String cargoRecursosH = ejbSysmanUtil.consultarParametro(compania,
		                    "CARGO JEFE RECURSOS HUMANOS",
		                    SessionUtil.getModulo(), new Date(), false);
            int puertoEmail = Integer.parseInt(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                    "CONFIGURAR PUERTO PARA ENVIO DE CORREOS",
                    SessionUtil.getModulo(), new Date(), false),"587").toString());
            

            if (rutaEncabezado == null) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB999")
                                .replace("#parametro#",
                                                "RUTA IMAGEN ENCABEZADO VOLANTE DE PAGOS"));
                return;
            }
            if (rutaPiePagina == null) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB999")
                                .replace("#parametro#",
                                                "RUTA IMAGEN PIEPAGINA VOLANTE DE PAGOS"));
                return;
            }
            String tituloVolante = SysmanFunciones.concatenar(
                            SysmanFunciones.convertirAFechaCadena(fechaIni),
                            " ",
                            idioma.getString("TB_TB3685"), " ",
                            SysmanFunciones.convertirAFechaCadena(fechaFin));
            

            parametros.put("PR_TITULO", tituloVolante);
            parametros.put("PR_ENCABEZADO",
                            rutaEncabezado);
            parametros.put("PR_PIEPAGINA", rutaPiePagina);
            parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
            parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS", nombreRecursosH);
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoRecursosH);

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(GeneralParameterEnum.MES.getName(), mes);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

            if ("1".equals(opcion)) {
                param.put(GeneralParameterEnum.ESTADO.getName(), "-1");
                param.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                                idEmpleado);
            }
            else {
                param.put(GeneralParameterEnum.ESTADO.getName(), "0");
                param.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(), "0");
            }

            List<Registro> listaCorreos = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            volantesDePagoControladorUrlEnum.URL0007
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            String nomPeriodo = service.buscarEnLista(periodo,
                            GeneralParameterEnum.PERIODO.getName(),
                            GeneralParameterEnum.NOMBRE.getName(),
                            listaPeriodo);

            correo.setSmtpHostName(servidor);
            correo.setSmtpHostPort(puertoEmail);
            correo.setSmtpAuthUser(usuario);
            correo.setSmtpAuthName(usuario);
            correo.setSmtpAuthPwd(clave);
            long totalRegistros = 0;
			long contarRe       = 0;
			for (Registro registro : listaCorreos) {
				totalRegistros++;
			}
            for (Registro registro : listaCorreos) {
            	contarRe++;
                if (registro.getCampos().get("EMAIL_CORPORATIVO") != null) {

                   reemplazar.put("empleadoIni",
                                    registro.getCampos()
                                                    .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                                    .getName()));
                    reemplazar.put("empleadoFin",
                                    registro.getCampos()
                                                    .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                                    .getName()));

                    reemplazar.put("centroCostoIni",
                                    SysmanConstantes.DEFECTOINICIAL_STRING);

                    reemplazar.put("centroCostoFin",
                                    SysmanConstantes.DEFECTOFINAL_STRING);

                    Reporteador.resuelveConsulta(parReporte.toUpperCase(),
                                    Integer.parseInt(SessionUtil.getModulo()),
                                    reemplazar,
                                    parametros);

                    ByteArrayInputStream reporteSerializado = JsfUtil
                                    .serializarReporteConstrasenia(parReporte,
                                                    parametros,
                                                    ConectorPool.ESQUEMA_SYSMAN,
                                                    FORMATOS.PDF,
                                                    registro.getCampos()
                                                                    .get(GeneralParameterEnum.NUMERO_DCTO
                                                                                    .getName())
                                                                    .toString());

                    String strAsunto = idioma.getString("TB_TB3684")
                                    .replace("s$anioNomina$s", ano)
                                    .replace("s$nombreMes$s",
                                                    SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                                    .parseInt(mes)])
                                    .replace("s$periodoNomina$s", periodo)
                                    .replace("s$nombrePeriodo$s", nomPeriodo);
                    
                    

                    String strMensaje = idioma.getString("TB_TB3686")
                                    .replace("s$companiaNombre$s", SessionUtil
                                                    .getCompaniaIngreso()
                                                    .getNombre())
                                    .replace("s$companiaNit$s",
                                                    SessionUtil.getCompaniaIngreso()
                                                                    .getNit())
                                    .replace("s$anioNomina$s", ano)
                                    .replace("s$nombreMes$s",
                                                    SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                                    .parseInt(mes)])
                                    .replace("s$periodoNomina$s", periodo)
                                    .replace("s$nombrePeriodo$s", nomPeriodo);

                    correo.enviarAdjunto(registro.getCampos()
                                    .get("EMAIL_CORPORATIVO").toString(),
                                    strAsunto, strMensaje,
                                    idioma.getString("TB_TB3712")
                                                    .concat(".pdf"),
                                    reporteSerializado,
                                    "application/pdf"); 
                    //se comenta para que no se haga el envio del correo 2 veces,  ya que se habilito enviarTransporte 
                    //en la funcion enviarAdjunto porque interrumpia el proceso de envio de certificados dian
                    //correo.enviarTransporteMasivoC(contarRe,totalRegistros);  
                    Map<String, Object> paramActualizacion = new HashMap<>();
                    paramActualizacion.put(
                                    GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    paramActualizacion.put(GeneralParameterEnum.ID_DE_PROCESO
                                    .getName(), proceso);
                    paramActualizacion.put(GeneralParameterEnum.ANO.getName(),
                                    ano);
                    paramActualizacion.put(GeneralParameterEnum.MES.getName(),
                                    mes);
                    paramActualizacion.put(
                                    GeneralParameterEnum.PERIODO.getName(),
                                    periodo);
                    paramActualizacion.put(
                                    GeneralParameterEnum.ID_DE_EMPLEADO
                                                    .getName(),
                                    registro.getCampos()
                                                    .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                                    .getName()));
                    Parameter parameter = new Parameter();
                    parameter.setFields(paramActualizacion);

                    UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    volantesDePagoControladorUrlEnum.URL0008
                                                                    .getValue());
                    requestManager.update(urlUpdate.getUrl(),
                                    urlUpdate.getMetodo(),
                                    parameter); 
                }
            }
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (JRException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB4059"));
        }
        catch (SystemException | IOException
                        | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Abrir en la vista
     *
     */
    public void oprimirAbrir() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarProceso() {
        // <CODIGO_DESARROLLADO>
        ano = null;
        mes = null;
        periodo = null;
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarOpcion() {
        // <CODIGO_DESARROLLADO>
        if ("1".equals(opcion)) {
            centroCosto = null;

        }
        else {
            idEmpleado = null;
            nombreCompleto = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        periodo = null;
        cargarListaMes();
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaidDeEmpleado
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaidDeEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        idEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID_DE_EMPLEADO"), "")
                        .toString();
        nombreCompleto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRES"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable idEmpleado
     * 
     * @return idEmpleado
     */
    public String getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * Asigna la variable idEmpleado
     * 
     * @param idEmpleado
     * Variable a asignar en idEmpleado
     */
    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getObservacion1() {
        return observacion1;
    }

    public void setObservacion1(String observacion1) {
        this.observacion1 = observacion1;
    }

    public String getCompania() {
        return compania;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getEncabezado() {
        return encabezado;
    }

    public void setEncabezado(String encabezado) {
        this.encabezado = encabezado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaidCentroDeCosto
     * 
     * @return listaidCentroDeCosto
     */
    public List<Registro> getListaidCentroDeCosto() {
        return listaidCentroDeCosto;
    }

    /**
     * Asigna la lista listaidCentroDeCosto
     * 
     * @param listaidCentroDeCosto
     * Variable a asignar en listaidCentroDeCosto
     */
    public void setListaidCentroDeCosto(List<Registro> listaidCentroDeCosto) {
        this.listaidCentroDeCosto = listaidCentroDeCosto;
    }

    /**
     * Retorna la lista listaProceso
     * 
     * @return listaProceso
     */
    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    /**
     * Asigna la lista listaProceso
     * 
     * @param listaProceso
     * Variable a asignar en listaProceso
     */
    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    /**
     * Retorna la lista listaAno1
     * 
     * @return listaAno1
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno1
     * 
     * @param listaAno1
     * Variable a asignar en listaAno1
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    /**
     * Retorna la lista listaMes1
     * 
     * @return listaMes1
     */
    public List<Registro> getListaMes() {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes1
     * 
     * @param listaMes1
     * Variable a asignar en listaMes1
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    /**
     * Retorna la lista listaPeriodo1
     * 
     * @return listaPeriodo1
     */
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo1
     * 
     * @param listaPeriodo1
     * Variable a asignar en listaPeriodo1
     */
    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaidDeEmpleado
     * 
     * @return listaidDeEmpleado
     */
    public RegistroDataModelImpl getListaidDeEmpleado() {
        return listaidDeEmpleado;
    }

    /**
     * Asigna la lista listaidDeEmpleado
     * 
     * @param listaidDeEmpleado
     * Variable a asignar en listaidDeEmpleado
     */
    public void setListaidDeEmpleado(RegistroDataModelImpl listaidDeEmpleado) {
        this.listaidDeEmpleado = listaidDeEmpleado;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public boolean isVisibleVolantesInst() {
        return visibleVolantesInst;
    }

    public void setVisibleVolantesInst(boolean visibleVolantesInst) {
        this.visibleVolantesInst = visibleVolantesInst;
    }

    public boolean isVisibleVolantes() {
        return visibleVolantes;
    }

    public void setVisibleVolantes(boolean visibleVolantes) {
        this.visibleVolantes = visibleVolantes;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
