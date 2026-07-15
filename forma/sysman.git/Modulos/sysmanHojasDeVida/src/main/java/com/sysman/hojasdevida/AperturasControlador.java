/*-
 * AperturasControlador.java
 *
 * 1.0
 * 
 * 25/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.email.ApiRestClient;
import com.sysman.email.EmailPojo;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.AperturasControladorEnum;
import com.sysman.hojasdevida.enums.AperturasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
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
 * Clase encargada del gestionar (CRUD) las convocatorias para la seleccion del persona.
 *
 * @version 1.0, 25/01/2018
 * @author jeguerrero
 * 
 * 
 * @version 2.0, 06/07/2018, Por favor si se migra la forma a los componentes CP51101, CP51103, CP51105, CP51106 al pattern dejar el formato "dd/MM/yyyy HH:mm"
 * @author eamaya
 * 
 */
@ManagedBean
@ViewScoped
public class AperturasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del usuario que ingresa a la aplicación
     */
    private final String usuario;

    /**
     * Constante a nivel de clase que almacena el codigo del modulo por el cual incio sesion a la aplicacion
     */
    private final String modulo;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // <DECLARAR_ATRIBUTOS>

    /**
     */
    private String descripcion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Varibale encargada de almacenar temporalmente lo seleccionado en el combo Cargo en la parte grafica del formulario
     */
    private RegistroDataModelImpl listaCargo;

    /**
     * Lista que carga la lista manual
     */
    private RegistroDataModelImpl listaManual;

    /**
     * Lista que carga las dependencias de la compania
     */
    private RegistroDataModelImpl listaDependencia;
    /**
     * Variable encargada de almacenar temporlamente lo seleccionado en el combo clase en la interfaz grafica del formfulario
     */
    private List<Registro> listaClase;
    /**
     * Constante encargada de almacenar el String CODIGOCARGO
     */

    private final String codigoCargoCons;
    /**
     * Constante encargada de almacenar el String NOMBRE_CARGO
     */
    private final String nombreCargoCons;
    /**
     * Constante encargada de almacenar el String NRO_CONVOCATORIA
     */
    private final String nroConvocatoria;
    /**
     * Constante encargada de almacenar el String convocatoria
     */
    private final String convocatoriaLowerCons;
    /**
     * Variable para almacenar los correos destino
     */
    private String correosDestino;
    /**
     * Mensaje de envio de alerta
     */
    private String mensajeEnvio;
    /**
     * Descripcion del mensaje
     */
    private StringBuilder descripcionCorreo;
    /**
     * Asunto del correo
     */
    private String asunto;

    private boolean bloqueadoDep;

    private int anoConvocatoria;

    private String dependencia;

    private String cargo;

    private String manual;

    /**
     * Mensaje enviado correctamente
     */
    private String mensajeCorrecto;

    /**
     * Mensaje NO enviado
     */
    private String mensajeIncorrecto;

    private boolean ver;

    private boolean desbloquearCargo;

    @EJB
    EjbSysmanUtil ejbSysmanUtl;
    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de AperturasControlador
     */
    public AperturasControlador() {
        super();
        convocatoriaLowerCons = AperturasControladorEnum.CONVOCATORIA
                        .getValue();
        codigoCargoCons = AperturasControladorEnum.CODIGOCARGO.getValue();
        nombreCargoCons = AperturasControladorEnum.NOMBRE_CARGO.getValue();
        nroConvocatoria = AperturasControladorEnum.NRO_CONVOCATORIA.getValue();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();
        correosDestino = "";
        mensajeEnvio = "Alerta de email enviada correctamente.";
        descripcionCorreo = new StringBuilder();
        asunto = "";
        desbloquearCargo = true;
        bloqueadoDep = true;
        mensajeCorrecto = "Alerta de email enviada correctamente.";
        mensajeIncorrecto = "No se pudo enviar la alerta de email.";
        try {
            numFormulario = GeneralCodigoFormaEnum.APERTURAS_CONTROLADOR
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
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCargo();
        cargarListaDependencia();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaClase();

        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.NAT_APERTURA;
        buscarLlave();
        asignarOrigenDatos();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    public String obtenerCorreosDestino() {
        String salida = "";

        Map<String, Object> parametrosNombre = new TreeMap<>();
        parametrosNombre.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            Registro rsListadoCorreos = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AperturasControladorUrlEnum.URL0101
                                                                            .getValue())
                                            .getUrl(), parametrosNombre));

            salida = SysmanFunciones.toString(
                            rsListadoCorreos.getCampos().get("LISTADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return salida.isEmpty() || salida == null ? ""
                        : salida.substring(0, (salida.length() - 1));

    }

    /**
     * 
     * Carga la lista listaClase
     *
     */
    public void cargarListaClase() {

        try {
            listaClase = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AperturasControladorUrlEnum.URL5181
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 688001
    }

    /**
     * 
     * Carga la lista listaCargo
     *
     */
    public void cargarListaCargo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AperturasControladorUrlEnum.URL0003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        anoConvocatoria);

        String[] vec = { "CODIGOCARGO", "ANO", "GRADO", "ESCALAFON",
                        "ID_DE_CATEGORIA" };

        listaCargo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, vec);

    }

    /**
     * 
     * Carga la lista listaManual
     *
     * 
     */
    public void cargarListaManual() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AperturasControladorUrlEnum.URL0004
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependencia);

        param.put("CARGO", cargo);

        String[] vec = { "NUMERO_MANUAL",
                        "SIGLA",
                        "NOMBRE_MANUAL",
                        "VERSION" };

        listaManual = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, vec);

    }

    /**
     * 
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AperturasControladorUrlEnum.URL4545
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control HoraAtencionInicio
     * 
     * 
     */
    public void cambiarHoraAtencionInicio() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control HoraAtencionFin
     * 
     * 
     */
    public void cambiarHoraAtencionFin() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>

    public void cambiarFechaConvocatoria() {
        // <CODIGO_DESARROLLADO>

        Date fechaConvocatoria = (Date) registro.getCampos()
                        .get("FECHA_CONVOCATORIA");

        anoConvocatoria = SysmanFunciones
                        .ano(fechaConvocatoria);

        desbloquearCargo = false;

        cargarListaCargo();

        registro.getCampos().put(
                        AperturasControladorEnum.NOMBRE_CATEGORIA.getValue(),
                        null);

        registro.getCampos().put(codigoCargoCons, null);

        registro.getCampos().put(AperturasControladorEnum.SUELDO.getValue(),
                        null);

        registro.getCampos().put(AperturasControladorEnum.PLAZAS.getValue(),
                        null);

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCargo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        cargo = (String) registroAux.getCampos().get("CODIGOCARGO");

        registro.getCampos().put(codigoCargoCons,
                        registroAux.getCampos().get("CODIGOCARGO"));

        registro.getCampos().put(
                        AperturasControladorEnum.NOMBRE_CATEGORIA.getValue(),
                        registroAux.getCampos().get(
                                        AperturasControladorEnum.NOMBRE_CATEGORIA
                                                        .getValue()));

        registro.getCampos().put(AperturasControladorEnum.GRADOCARGO.getValue(),
                        registroAux.getCampos().get("GRADO"));

        registro.getCampos().put(AperturasControladorEnum.SUELDO.getValue(),
                        registroAux.getCampos().get("SALARIO_BASE"));

        registro.getCampos().put(AperturasControladorEnum.PLAZAS.getValue(),
                        registroAux.getCampos().get("PLAZAS"));

        cargarListaManual();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaDependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        // dependencia = (String)
        // registroAux.getCampos().get("CODIGO");

        registro.getCampos().put("DEPENDENCIA",
                        registroAux.getCampos().get("CODIGO"));

        registro.getCampos().put(
                        AperturasControladorEnum.NOMBRE_DEPENDENCIA.getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaManual
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaManual(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("MANUAL",
                        registroAux.getCampos().get("NUMERO_MANUAL"));

        try {
            ejbHojasDeVidaCero.cargarConvocatoriaManual(compania,
                            SysmanFunciones.nvl(
                                            registroAux.getCampos().get(
                                                            "NUMERO_MANUAL"),
                                            "").toString(),
                            SysmanFunciones.nvl(registroAux.getCampos()
                                            .get("VERSION"), "").toString(),
                            registro.getCampos().get("NRO_CONVOCATORIA")
                                            .toString(),
                            usuario);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarSeleccionarManual();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>

    public void cargarSeleccionarManual() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put("NRO_CONVOCATORIA",
                        registro.getCampos().get("NRO_CONVOCATORIA"));

        try {
            Registro regAux = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            AperturasControladorUrlEnum.URL0005
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));

            if (regAux != null) {

                registro.getCampos().put("REQUISITOEDUCACION",
                                regAux.getCampos().get("REQUISITOEDUCACION"));
                registro.getCampos().put("REQUISITOEXPERIENCIA",
                                regAux.getCampos().get("REQUISITOEXPERIENCIA"));
                registro.getCampos().put("REQUISITOEQUIVALENCIAS", regAux
                                .getCampos().get("REQUISITOEQUIVALENCIAS"));
                registro.getCampos().put("REQUISITOEQUIEXPERIENCIA", regAux
                                .getCampos().get("REQUISITOEQUIEXPERIENCIA"));
                registro.getCampos().put("OBSERVACIONESREQUISITOS", regAux
                                .getCampos().get("OBSERVACIONESREQUISITOS"));

            }
            else {

                registro.getCampos().put("REQUISITOEDUCACION", "");
                registro.getCampos().put("REQUISITOEXPERIENCIA", "");
                registro.getCampos().put("REQUISITOEQUIVALENCIAS", "");
                registro.getCampos().put("REQUISITOEQUIEXPERIENCIA", "");
                registro.getCampos().put("OBSERVACIONESREQUISITOS", "");

            }

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton MediosPublicacion en la vista
     *
     *
     */
    public void oprimirMediosPublicacion() {
        // <CODIGO_DESARROLLADO>

        String[] campos = { convocatoriaLowerCons, "permiteVer" };
        Object[] valores = { retornarString(registro, nroConvocatoria), ver };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.RELACIONDIARIOS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     */
    public void oprimirbtnEnviar() {
        try {
            if (accion.equals(ACCION_MODIFICAR)) {

                /** correosDestino = obtenerCorreosDestino(); */
                EmailPojo email = new EmailPojo();
                String nombreClase = service.buscarEnLista(
                                SysmanFunciones.toString(
                                                registro.getCampos()
                                                                .get("CLASE")),
                                "CONVOCATORIA",
                                "NOMBRE", listaClase);

                String sueldoFormateado = new java.text.DecimalFormat(
                                "$ #,##0.00")
                                                .format(registro.getCampos()
                                                                .get("SUELDO"));

                String fechaFormateada = "";
                fechaFormateada = SysmanFunciones.convertirAFechaCadena(
                                (Date) registro.getCampos()
                                                .get("FECHACIERREINSCRIPCION"));

                String nombreCargo;
                if (registro.getCampos()
                                .get(AperturasControladorEnum.NOMBRE_CATEGORIA
                                                .getValue()) == null) {

                    nombreCargo = " ";
                }
                else {
                    nombreCargo = SysmanFunciones.toString(
                                    registro.getCampos().get(
                                                    AperturasControladorEnum.NOMBRE_CATEGORIA
                                                                    .getValue()));
                }

                Map<String, Object> paramEnvio = new TreeMap<>();
                paramEnvio.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                Map<String, Object> paramEnvio2 = new TreeMap<>();
                paramEnvio2.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                Map<String, Object> remplazosDescripcion = new TreeMap<>();
                Map<String, Object> remplazosDescripcion2 = new TreeMap<>();

                String nombreReporte = "001792AperturaCargos";
                Map<String, Object> reemplazosReporte = new TreeMap<>();
                Map<String, Object> parametrosReporte = new TreeMap<>();

                reemplazosReporte.put("compania", compania);

                reemplazosReporte.put("convocatoria",
                                retornarString(registro, nroConvocatoria));

                parametrosReporte.put("PR_COMPANIA", compania);

                Reporteador.resuelveConsulta(nombreReporte,
                                Integer.parseInt(modulo), reemplazosReporte,
                                parametrosReporte);

                byte[] archivo = null;
                try {
                    archivo = JsfUtil.exportarStreamedSerializado(
                                    nombreReporte,
                                    parametrosReporte, ConectorPool.ESQUEMA_SYSMAN,
                                    FORMATOS.PDF);
                }
                catch (JRException | SysmanException | IOException e1) {
                    Logger.getLogger(AperturasControlador.class.getName()).log(Level.SEVERE, null, e1);

                    JsfUtil.agregarMensajeError(e1.getMessage());

                }

                if ("E".equals(registro.getCampos().get("CLASE").toString())) {
                    /**
                     * solo envía al correo de soporte institucional para publicar en la página (EXTERNA)
                     */
                    paramEnvio.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                    "10");
                    remplazosDescripcion.put("nombreCargo", nombreCargo);
                }
                else if ("M".equals(
                                registro.getCampos().get("CLASE").toString())) {

                    /**
                     * enviar al correo de los Funcionarios y al correo de soporte institucional (MIXTA) tambien el codigo 10
                     */
                    paramEnvio.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                    "11");
                    remplazosDescripcion.put("claseConvocatoria", nombreClase);
                    remplazosDescripcion.put("nombreCargo", nombreCargo);
                    remplazosDescripcion.put("nombreDependencia",
                                    registro.getCampos()
                                                    .get("NOMBRE_DEPENDENCIA")
                                                    .toString());
                    remplazosDescripcion.put("requisitosEducacion",
                                    registro.getCampos()
                                                    .get("REQUISITOEDUCACION")
                                                    .toString());
                    remplazosDescripcion.put("requisitosExperiencia",
                                    registro.getCampos()
                                                    .get("REQUISITOEXPERIENCIA")
                                                    .toString());
                    remplazosDescripcion.put("sueldo", sueldoFormateado);
                    remplazosDescripcion.put("fechaHoraCierre",
                                    fechaFormateada);

                    /**
                     *
                     */
                    paramEnvio2.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                    "10");
                    remplazosDescripcion2.put("nombreCargo", nombreCargo);

                    Registro rsEmail2 = RegistroConverter.toRegistro(
                                    requestManager.get(UrlServiceUtil
                                                    .getInstance()
                                                    .getUrlServiceByUrlByEnumID(
                                                                    "1663003")
                                                    .getUrl(),
                                                    paramEnvio2));

                    if (rsEmail2 != null) {

                        String descripcionFinal2 = remplazarVariable(
                                        rsEmail2.getCampos().get(
                                                        GeneralParameterEnum.DESCRIPCION
                                                                        .getName())
                                                        .toString(),
                                        remplazosDescripcion2);

                        email.setFrom(rsEmail2.getCampos().get("ORIGEN")
                                        .toString());
                        email.setTo(rsEmail2.getCampos().get("CORREOS_DESTINO")
                                        .toString());
                        email.setSubject(rsEmail2.getCampos().get("ASUNTO")
                                        .toString());
                        email.setBody(descripcionFinal2);
                        email.setReport(archivo);
                        email.setReportName("001792AperturaCargos");

                        ApiRestClient client = new ApiRestClient();
                        try {
                            client.postClient(email);
                        }
                        catch (Exception e) {
                            // Excepcion
                        }

                    }

                }
                else if ("I".equals(
                                registro.getCampos().get("CLASE").toString())
                                || "O".equals(registro.getCampos().get("CLASE")
                                                .toString())) {
                    /**
                     * enviar al correo de los Funcionarios (INTERNA) u (OTRAS)
                     */
                    paramEnvio.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                    "9");
                    remplazosDescripcion.put("claseConvocatoria", nombreClase);
                    remplazosDescripcion.put("nombreCargo", nombreCargo);
                    remplazosDescripcion.put("nombreDependencia",
                                    registro.getCampos()
                                                    .get("NOMBRE_DEPENDENCIA")
                                                    .toString());
                    remplazosDescripcion.put("requisitosEducacion",
                                    registro.getCampos()
                                                    .get("REQUISITOEDUCACION")
                                                    .toString());
                    remplazosDescripcion.put("requisitosExperiencia",
                                    registro.getCampos()
                                                    .get("REQUISITOEXPERIENCIA")
                                                    .toString());
                    remplazosDescripcion.put("sueldo", sueldoFormateado);
                    remplazosDescripcion.put("fechaHoraCierre",
                                    fechaFormateada);
                }

                Registro rsEmail = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                "1663003")
                                                .getUrl(),
                                                paramEnvio));

                if (rsEmail != null) {

                    String descripcionFinal = remplazarVariable(
                                    rsEmail.getCampos().get(
                                                    GeneralParameterEnum.DESCRIPCION
                                                                    .getName())
                                                    .toString(),
                                    remplazosDescripcion);

                    email.setFrom(rsEmail.getCampos().get("ORIGEN")
                                    .toString());
                    email.setTo(rsEmail.getCampos().get("CORREOS_DESTINO")
                                    .toString());
                    email.setSubject(rsEmail.getCampos().get("ASUNTO")
                                    .toString());
                    email.setBody(descripcionFinal);
                    email.setReport(archivo);
                    email.setReportName("001792AperturaCargos");

                    ApiRestClient client = new ApiRestClient();
                    client.postClient(email);
                    JsfUtil.agregarMensajeInformativo(
                                    mensajeCorrecto);
                    registro.getCampos().put("FECHA_ENVIO_CORREO", new Date());
                    ejbHojasDeVidaCero.actualizarEnvioCorreos(compania,
                                    retornarString(registro, nroConvocatoria),
                                    4);

                }
                else {
                    JsfUtil.agregarMensajeError(
                                    mensajeIncorrecto);
                }

            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que remplaza las variables de la descripcion del correo
     */
    public String remplazarVariable(String descripcion,
                    Map<String, Object> parametros) {
        String salida = descripcion;

        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            salida = salida.replace(
                            SysmanFunciones.concatenar("s$", entry.getKey(),
                                            "$s"),
                            SysmanFunciones.toString(entry.getValue()));
        }

        return salida;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton RelacionPruebas en la vista
     *
     *
     */
    public void oprimirRelacionPruebas() {
        // <CODIGO_DESARROLLADO>

        String[] campos = { convocatoriaLowerCons, "permiteVer" };
        Object[] valores = { retornarString(registro, nroConvocatoria), ver };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.RELACIONPRUEBAS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton JuradosSeleccion en la vista
     *
     *
     */
    public void oprimirJuradosSeleccion() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { convocatoriaLowerCons, "permiteVer" };
        Object[] valores = { retornarString(registro, nroConvocatoria), ver };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.COMITESELECCIONS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(FORMATOS formato) {
        Map<String, Object> reemplazos = new TreeMap<>();

        Map<String, Object> parametros = new TreeMap<>();
        try {
            reemplazos.put("compania", compania);

            reemplazos.put("convocatoria",
                            retornarString(registro, nroConvocatoria));

            parametros.put("PR_COMPANIA", compania);

            Reporteador.resuelveConsulta("001792AperturaCargos",
                            Integer.parseInt(modulo), reemplazos, parametros);

            archivoDescarga = JsfUtil.exportarStreamed("001792AperturaCargos",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
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

        ver = accion.equals(ACCION_VER);

        if (accion.equals(ACCION_MODIFICAR)) {

            Date fechaConvocatoria = (Date) registro.getCampos()
                            .get("FECHA_CONVOCATORIA");

            anoConvocatoria = SysmanFunciones
                            .ano(fechaConvocatoria);

            cargarListaCargo();

            dependencia = SysmanFunciones.nvl(registro.getCampos().get(
                            GeneralParameterEnum.DEPENDENCIA.getName()), "")
                            .toString();

            cargo = SysmanFunciones
                            .nvl(registro.getCampos().get("CODIGOCARGO"), "")
                            .toString();

            cargarListaManual();

        }

        precargarRegistro();
        registro.getCampos();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put(nroConvocatoria, generarConsecutivo());

        registro.getCampos().remove(nombreCargoCons);

        registro.getCampos().remove("NOMBRE_CATEGORIA");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(nombreCargoCons);
        registro.getCampos().remove("DENOMINACION_CARGOS");
        registro.getCampos().remove("NOMBRE_DEPENDENCIA");
        registro.getCampos().remove("NOMBRE_CATEGORIA");
        // registro.getCampos().remove("MANUAL");
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
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
     * Metodo ejecutado despues de realizar la eliminacion del registro
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>}
        listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable descripcion
     * 
     * @return descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Asigna la variable descripcion
     * 
     * @param descripcion
     * Variable a asignar en descripcion
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCargo
     * 
     * @return listaCargo
     */
    public RegistroDataModelImpl getListaCargo() {
        return listaCargo;
    }

    public List<Registro> getListaClase() {
        return listaClase;
    }

    public void setListaClase(List<Registro> listaClase) {
        this.listaClase = listaClase;
    }

    /**
     * Asigna la lista listaCargo
     * 
     * @param listaCargo
     * Variable a asignar en listaCargo
     */
    public void setListaCargo(RegistroDataModelImpl listaCargo) {
        this.listaCargo = listaCargo;
    }

    /**
     * Retorna la lista listaManual
     * 
     * @return listaManual
     */
    public RegistroDataModelImpl getListaManual() {
        return listaManual;
    }

    /**
     * Asigna la lista listaManual
     * 
     * @param listaManual
     * Variable a asignar en listaManual
     */
    public void setListaManual(RegistroDataModelImpl listaManual) {
        this.listaManual = listaManual;
    }

    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                        : reg.getCampos().get(campo).toString();
    }

    private Long generarConsecutivo() {

        String criterio = SysmanFunciones.concatenar("COMPANIA = ''", compania,
                        "''");
        Long consecutivo = null;
        try {
            consecutivo = ejbSysmanUtl.generarConsecutivoConValorInicial(
                            "NAT_APERTURA", criterio, nroConvocatoria, "1");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return consecutivo;
    }

    // private void envioAdjuntoSerializado() {
    // Map<String, Object> reemplazos = new TreeMap<>();
    //
    // String reporte = "001792AperturaCargos";
    //
    // Map<String, Object> parametros = new TreeMap<>();
    // try {
    // reemplazos.put("compania", compania);
    //
    // reemplazos.put("convocatoria",
    // retornarString(registro, nroConvocatoria));
    //
    // parametros.put("PR_COMPANIA", compania);
    //
    // Reporteador.resuelveConsulta(reporte,
    // Integer.parseInt(modulo), reemplazos, parametros);
    //
    // byte[] archivo = JsfUtil.exportarStreamedSerializado(reporte,
    // parametros, ConectorPool.ESQUEMA_SYSMAN,
    // FORMATOS.PDF);
    //
    // Map<String, Object> contexto = new HashMap<>();
    //
    // contexto.put(EnumParamProcesador.KEY_TIPO_RECEPCION.name(),
    // EnumParamProcesador.KEY_EMAIL.name());
    // // contexto.put(EnumParamProcesador.KEY_DESTINO.name(),
    // // correosDestino);
    // contexto.put(EnumParamProcesador.KEY_DESTINO.name(),
    // "diana.romero@ane.gov.co,williamgonzalez@sysman.com.co,anaabril@sysman.com.co,monica.martinez@ane.gov.co,miguelanrove.94@gmail.com");
    // contexto.put(EnumParamProcesador.KEY_ASUNTO.name(), asunto);
    // contexto.put(EnumParamProcesador.KEY_CUERPO_CORREO.name(),
    // descripcionCorreo.toString());
    // contexto.put(EnumParamProcesador.KEY_SERIALIZADO.name(),
    // archivo);
    // contexto.put(EnumParamProcesador.KEY_NOMBRE_REPORTE.name(),
    // reporte);
    //
    // procesador.setContexto(contexto);
    //
    // procesador.ejecutar();
    // JsfUtil.agregarMensajeInformativo(mensajeEnvio);
    // registro.getCampos().put("FECHA_ENVIO_CORREO", new Date());
    // ejbHojasDeVidaCero.actualizarEnvioCorreos(compania,
    // retornarString(registro, nroConvocatoria), 4);
    //
    // }
    // catch (NegocioExcepcion | JRException | SysmanException
    // | IOException | SystemException e) {
    // logger.error(e.getMessage(), e);
    // JsfUtil.agregarMensajeError(e.getMessage());
    // }
    // }

    /**
     * @return the bloqueadoDep
     */
    public boolean isBloqueadoDep() {
        return bloqueadoDep;
    }

    /**
     * @param bloqueadoDep
     * the bloqueadoDep to set
     */
    public void setBloqueadoDep(boolean bloqueadoDep) {
        this.bloqueadoDep = bloqueadoDep;
    }

    /**
     * @return the ver
     */
    public boolean isVer() {
        return ver;
    }

    /**
     * @param ver
     * the ver to set
     */
    public void setVer(boolean ver) {
        this.ver = ver;
    }

    /**
     * @return the manual
     */
    public String getManual() {
        return manual;
    }

    /**
     * @param manual
     * the manual to set
     */
    public void setManual(String manual) {
        this.manual = manual;
    }

    public boolean isDesbloquearCargo() {
        return desbloquearCargo;
    }

    public void setDesbloquearCargo(boolean desbloquearCargo) {
        this.desbloquearCargo = desbloquearCargo;
    }

}
