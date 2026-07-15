/*-
 * InformesFormulariosControlador.java
 *
 * 1.0
 *
 * 24/03/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.InformesFormulariosControladorEnum;
import com.sysman.chipfut.enums.InformesFormulariosControladorUrlEnum;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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
 * Clase que permite generar los informes desde los formularios excel.
 *
 * @version 1.0, 24/03/2017
 * @author jrodriguezr
 */
@ManagedBean
@ViewScoped
public class InformesFormulariosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del modulo
     * por el cual se ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Valor del atributo correspondiente a pesos
     */
    private boolean pesos;
    /**
     * Valor del atributo correspondiente a trimestre
     */
    private String trimestre;
    /**
     * Valor del atributo correspondiente a trimestre
     */
    private String anioTrabajo;
    /**
     * Valor del atributo correspondiente a trimestre
     */
    private String formulario;
    /**
     * Valor del atributo correspondiente a trimestre
     */
    private String tipoActoAdmtivo;
    /**
     * Valor del atributo correspondiente a trimestre
     */
    private String codigoEntidad;
    /**
     * Valor del atributo correspondiente a trimestre
     */
    private String nroActoAdmitivo;
    /**
     * Valor del atributo correspondiente a trimestre
     */
    private Date fechaDocumento;
    /**
     * Atributo que almacena el nombre de la consulta según informe a
     * generar
     */
    private String nombreConsulta;
    /**
     * Atributo que almacena el numero de mes final para filtro en la
     * consulta
     */
    private String mesFinal;
    /**
     * Atributo que almacena el numero de mes inicial para filtro en
     * la consulta
     */
    private String mesInicial;
    /**
     * Atributo que identifica si el informe genera regsitro de
     * totales
     */
    private boolean totales;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que almacena el codigo del periodo a generar del
     * informe
     */
    private String periodo;
    /**
     * Atributo que almacena el nombre del informe seleccioando en el
     * combo de Formulario a generar
     */
    private String nombreInforme;
    /**
     * Atributo que almacena el nombre de la consulta a generar
     * seleccionado en el combo de formulario a generar
     */
    private String consultaInforme;
    /**
     * Atributo que almacena el nombre del encabezado a generar
     * seleccionado en el combo de formulario a generar
     */
    private String encabezadoInforme;
    /**
     * Atributi que almacena la cadena para la segunda fila del
     * archivo plano del txt
     */
    private String cadenaAgrupacion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de objetos pertenecientes al combo Ano Trabajo
     */
    private List<Registro> listaAnoTrabajo;
    /**
     * Lista de valores del acto administrativo
     */
    private List<Registro> listaTipoActoAdmtivo;
    private boolean actoAdmtivoVisible;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaFormulario;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformesFormulariosControlador
     */
    public InformesFormulariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoEntidad = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
        try {
            // 1383
            numFormulario = GeneralCodigoFormaEnum.INFORMES_FORMULARIOS_CONTROLADOR
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
        anioTrabajo = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaAnoTrabajo();
        cargarListaTipoActoAdmtivo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaFormulario();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAnoTrabajo
     *
     */
    public void cargarListaAnoTrabajo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformesFormulariosControladorUrlEnum.URL189
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaFormulario
     */
    public void cargarListaFormulario() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformesFormulariosControladorUrlEnum.URL214
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPO.getName(), "1");
        param.put(InformesFormulariosControladorEnum.SUBTIPO.getValue(), "0");

        listaFormulario = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTipoActoAdmtivo
     *
     */
    public void cargarListaTipoActoAdmtivo() {
        try {
            listaTipoActoAdmtivo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformesFormulariosControladorUrlEnum.URL251
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Generar en la vista
     *
     */
    public void oprimirGenerar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.CSV);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton VerificarConfiguracion en
     * la vista
     *
     */
    public void oprimirVerificarConfiguracion() {

        archivoDescarga = null;

        ByteArrayInputStream salidaNombreExcel = null;
        ByteArrayInputStream salidaNombreExcel2 = null;
        ByteArrayInputStream salidaNombreExcel3 = null;
        ByteArrayInputStream[] salida = new ByteArrayInputStream[3];
        String[] nombres = new String[3];

        Map<String, Object> reemplazos = new TreeMap<>();

        reemplazos.put("compania", compania);
        reemplazos.put("anioTrabajo", anioTrabajo);

        String consulta1 = Reporteador.resuelveConsulta(
                        "800345RUBROS_NOCONFIGURADOS_FUT_CUENTASXPAGAR",
                        Integer.parseInt(modulo),
                        reemplazos);

        String consulta2 = Reporteador.resuelveConsulta(
                        "800346RUBROS_NOCONFIGURADOS_FUT_INGRESOS_PARARESERVA",
                        Integer.parseInt(modulo),
                        reemplazos);

        String consulta3 = Reporteador.resuelveConsulta(
                        "800347RUBROS_CONFIGURADOS_FUT_INGRESOS_RESERVA_Y_FUT_INGRESOS",
                        Integer.parseInt(modulo),
                        reemplazos);

        try {

            salidaNombreExcel = JsfUtil.serializarHojaDatos(consulta1,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
        }
        catch (SysmanException | JRException | IOException | DRException
                        | SQLException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        try {
            salidaNombreExcel2 = JsfUtil.serializarHojaDatos(consulta2,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);

        }
        catch (SysmanException | JRException | IOException | DRException
                        | SQLException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        try {
            salidaNombreExcel3 = JsfUtil.serializarHojaDatos(consulta3,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);

        }
        catch (SysmanException | JRException | IOException | DRException
                        | SQLException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        int cantidad = 0;
        if (salidaNombreExcel != null) {
            salida[cantidad] = salidaNombreExcel;
            nombres[cantidad] = "RubrosNoConfiguradosFutCuentasXPagar.xlsx";
            cantidad++;
        }

        if (salidaNombreExcel2 != null) {
            salida[cantidad] = salidaNombreExcel2;
            nombres[cantidad] = "RubrosNoConfiguradosFutIngresosParaReserva.xlsx";
            cantidad++;
        }

        if (salidaNombreExcel3 != null) {
            salida[cantidad] = salidaNombreExcel3;
            nombres[cantidad] = "RubrosConfiguradosFutIngresosReservaFutIngreso.xlsx";
            cantidad++;
        }
        try {

            if (cantidad > 0) {
                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salida,
                                nombres, "VerificarConfiguracion");

            }

        }
        catch (JRException | IOException | SQLException | DRException e2) {
            logger.error(e2.getMessage(), e2);
            JsfUtil.agregarMensajeError(e2.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Formulario
     */
    public void cambiarFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFormulario
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFormulario(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        formulario = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreInforme = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
        nombreConsulta = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        InformesFormulariosControladorEnum.CONSULTA
                                                        .getValue()),
                        "").toString();
        encabezadoInforme = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        InformesFormulariosControladorEnum.ENCABEZADO
                                                        .getValue()),
                        "").toString();
        if ("2".equals(formulario)) {
            actoAdmtivoVisible = true;
        }
        else {
            actoAdmtivoVisible = false;
        }
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     */
    private void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            generarVariables();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anioTrabajo", anioTrabajo);
            reemplazar.put("mesfinal", mesFinal);
            reemplazar.put("mesinicial", mesInicial);
            reemplazar.put("mesanterior", Integer.parseInt(mesInicial) - 1);
            try {
                String parametro = ejbSysmanUtil.consultarParametro(
                                compania,
                                "DIGITO REDONDEO DE INFORMES FUT",
                                SessionUtil.getModulo(),
                                new Date(), true);

                parametro = SysmanFunciones.validarVariableVacio(parametro)
                    ? "0"
                    : parametro;
                reemplazar.put("redondeo", parametro);
            }
            catch (SystemException e1) {
                logger.error(e1.getMessage(), e1);
                JsfUtil.agregarMensajeError(e1.getMessage());
            }
            if ("2".equals(formulario)) {
                reemplazar.put("acto", tipoActoAdmtivo);
                reemplazar.put("numeroActo", nroActoAdmitivo);
                reemplazar.put("fechaActo", SysmanFunciones
                                .convertirAFechaCadena(fechaDocumento));
            }

            try {
                archivoDescarga = JsfUtil.reportesFut(
                                nombreConsulta, reemplazar,
                                generarEncabezado(),
                                formato,
                                nombreInforme,
                                modulo, totales);
            }
            catch (JRException | IOException | SQLException | DRException
                            | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        catch (ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    private String generarEncabezado() {

        String retorno = "";
        try {
            String separadorColumnas = "\t";

            switch (trimestre) {
            case "1":
                periodo = "0103";
                break;
            case "2":
                periodo = "0406";
                break;
            case "3":
                periodo = "0709";
                break;
            case "4":
                periodo = "1012";
                break;
            }

            retorno = SysmanFunciones.concatenar("S",
                            separadorColumnas,
                            codigoEntidad,
                            separadorColumnas, "1", periodo,
                            separadorColumnas,
                            anioTrabajo,
                            separadorColumnas,
                            encabezadoInforme,
                            separadorColumnas,
                            SysmanFunciones.convertirAFechaCadena(
                                            new Date(),
                                            "dd-MM-yyyy"));
        }
        catch (ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return retorno;
    }

    /**
     * Metodo que dependiendo el trimestre, genera el valor del mes
     * final para la consulta seleccionada
     * 
     * @return
     */
    private void generarVariables() {
        switch (trimestre) {
        case "1":
            mesInicial = "1";
            mesFinal = "3";
            periodo = "0103";
            break;
        case "2":
            mesInicial = "4";
            mesFinal = "6";
            periodo = "0406";
            break;
        case "3":
            mesInicial = "7";
            mesFinal = "9";
            periodo = "0709";
            break;
        default:
            mesInicial = "10";
            mesFinal = "12";
            periodo = "1012";
            break;

        }
    }

    /**
     * Metodo que arma la cadena de la segunda fila del archivo txt
     * 
     * @return
     */
    private String generarCadenaAgrupacion() {
        try {
            switch (formulario) {
            case "1":
                cadenaAgrupacion = "";
                break;
            case "2":

                String parTipoActo = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "TIPO DE ACTO ADMINISTRATIVO VAL PARA LAS CUENTAS POR PAGAR",
                                                modulo, new Date(), true),
                                " ");
                String cifraControl = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "FUENTE FUT PARA CIFRA DE CONTROL VAL",
                                                modulo, new Date(), true),
                                " ");
                String fechaDoc = SysmanFunciones.convertirAFechaCadena(
                                fechaDocumento, "dd-MM-yyyy");
                cadenaAgrupacion = SysmanFunciones.concatenar("D", "\t", "VAL",
                                "\t", parTipoActo, "\t", nroActoAdmitivo, "\t",
                                fechaDoc,
                                "\t", cifraControl, "\t", "AGRUPAR DEFIN", "\t",
                                "Agrupar PAGO", "\r\n");
                break;
            default:
                break;
            }
        }
        catch (SystemException | ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return cadenaAgrupacion;
    };

    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable pesos
     *
     * @return pesos
     */
    public boolean getPesos() {
        return pesos;
    }

    /**
     * Asigna la variable pesos
     *
     * @param pesos
     * Variable a asignar en pesos
     */
    public void setPesos(boolean pesos) {
        this.pesos = pesos;
    }

    /**
     * Retorna la variable trimestre
     *
     * @return trimestre
     */
    public String getTrimestre() {
        return trimestre;
    }

    /**
     * Asigna la variable trimestre
     *
     * @param trimestre
     * Variable a asignar en trimestre
     */
    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
    }

    /**
     * Retorna la variable anioTrabajo
     *
     * @return anioTrabajo
     */
    public String getAnioTrabajo() {
        return anioTrabajo;
    }

    /**
     * Asigna la variable anioTrabajo
     *
     * @param anioTrabajo
     * Variable a asignar en anioTrabajo
     */
    public void setAnioTrabajo(String anioTrabajo) {
        this.anioTrabajo = anioTrabajo;
    }

    /**
     * Retorna la variable formulario
     *
     * @return formulario
     */
    public String getFormulario() {
        return formulario;
    }

    /**
     * Asigna la variable formulario
     *
     * @param formulario
     * Variable a asignar en formulario
     */
    public void setFormulario(String formulario) {
        this.formulario = formulario;
    }

    /**
     * Retorna la variable tipoActoAdmtivo
     *
     * @return tipoActoAdmtivo
     */
    public String getTipoActoAdmtivo() {
        return tipoActoAdmtivo;
    }

    /**
     * Asigna la variable tipoActoAdmtivo
     *
     * @param tipoActoAdmtivo
     * Variable a asignar en tipoActoAdmtivo
     */
    public void setTipoActoAdmtivo(String tipoActoAdmtivo) {
        this.tipoActoAdmtivo = tipoActoAdmtivo;
    }

    /**
     * Retorna la variable codigoEntidad
     *
     * @return codigoEntidad
     */
    public String getCodigoEntidad() {
        return codigoEntidad;
    }

    /**
     * Asigna la variable codigoEntidad
     *
     * @param codigoEntidad
     * Variable a asignar en codigoEntidad
     */
    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
    }

    /**
     * Retorna la variable nroActoAdmitivo
     *
     * @return nroActoAdmitivo
     */
    public String getNroActoAdmitivo() {
        return nroActoAdmitivo;
    }

    /**
     * Asigna la variable nroActoAdmitivo
     *
     * @param nroActoAdmitivo
     * Variable a asignar en nroActoAdmitivo
     */
    public void setNroActoAdmitivo(String nroActoAdmitivo) {
        this.nroActoAdmitivo = nroActoAdmitivo;
    }

    /**
     * Retorna la variable nombreInforme
     *
     * @return nombreInforme
     */
    public String getNombreInforme() {
        return nombreInforme;
    }

    /**
     * Asigna la variable nombreInforme
     *
     * @param nombreInforme
     * Variable a asignar en nombreInforme
     */
    public void setNombreInforme(String nombreInforme) {
        this.nombreInforme = nombreInforme;
    }

    /**
     * Retorna la variable fechaDocumento
     *
     * @return fechaDocumento
     */
    public Date getFechaDocumento() {
        return fechaDocumento;
    }

    /**
     * Asigna la variable fechaDocumento
     *
     * @param fechaDocumento
     * Variable a asignar en fechaDocumento
     */
    public void setFechaDocumento(Date fechaDocumento) {
        this.fechaDocumento = fechaDocumento;
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
    /**
     * Retorna la lista listaAnoTrabajo
     *
     * @return listaAnoTrabajo
     */
    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    /**
     * Asigna la lista listaAnoTrabajo
     *
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    /**
     * Retorna la lista listaTipoActoAdmtivo
     * 
     * @return listaTipoActoAdmtivo
     */
    public List<Registro> getListaTipoActoAdmtivo() {
        return listaTipoActoAdmtivo;
    }

    /**
     * Asigna la lista listaTipoActoAdmtivo
     * 
     * @param listaTipoActoAdmtivo
     * Variable a asignar en listaTipoActoAdmtivo
     */
    public void setListaTipoActoAdmtivo(List<Registro> listaTipoActoAdmtivo) {
        this.listaTipoActoAdmtivo = listaTipoActoAdmtivo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFormulario
     * 
     * @return listaFormulario
     */
    public RegistroDataModelImpl getListaFormulario() {
        return listaFormulario;
    }

    /**
     * Asigna la lista listaFormulario
     * 
     * @param listaFormulario
     * Variable a asignar en listaFormulario
     */
    public void setListaFormulario(RegistroDataModelImpl listaFormulario) {
        this.listaFormulario = listaFormulario;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public boolean isActoAdmtivoVisible() {
        return actoAdmtivoVisible;
    }

    public void setActoAdmtivoVisible(boolean actoAdmtivoVisible) {
        this.actoAdmtivoVisible = actoAdmtivoVisible;
    }
}
