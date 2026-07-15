/*-
 * FutCierreFiscalControlador.java
 *
 * 1.0
 * 
 * 27/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.FutCierreFiscalControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Gestiona los eventos y procesos de la forma futcierrefiscal.
 *
 * @version 1.0, 27/03/2017
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FutCierreFiscalControlador extends BeanBaseModal {
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
     * Atributo que gestiona el valor del check:
     * {@code En miles de pesos}
     */
    private boolean ckPesos;

    /**
     * Atributo que gestiona el valor del check:
     * {@code Generar archivo plano a partir de un Excel}
     */
    private boolean ckGenerar;

    /**
     * Atributo que gestiona el valor seleccionado en el combo:
     * {@code Trimestre}
     */
    private int trimestre;

    /**
     * Atributo que gestiona el valor seleccionado en el combo:
     * {@code Año}
     */
    private int anio;
    /**
     * Variable que almacena el nombre del archivo
     */
    private String nombreArchivo;

    /**
     * Atributo que gestiona el valor ingresado en el campo:
     * {@code Código Entidad}
     */
    private String codigoEntidad;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /** Lista que contiene los items del combo {@code Año} */
    private List<Registro> listaAnoTrabajo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FutCierreFiscalControlador
     */
    public FutCierreFiscalControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoEntidad = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
        try {
            // 1387
            numFormulario = GeneralCodigoFormaEnum.FUT_CIERRE_FISCAL_CONTROLADOR
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
        cargarListaAnoTrabajo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
        anio = SysmanFunciones.ano(SysmanFunciones.hoy().getTime());
        ckPesos = true;
        trimestre = 4;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista {@code listaAnoTrabajo} asociada al combo
     * {@code Año}.
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FutCierreFiscalControladorUrlEnum.URL4719
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Generar en la vista
     *
     *
     */
    public void oprimirGenerar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton VerificarConfiguracion en
     * la vista
     *
     */
    public void oprimirVerificarConfiguracion() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        ByteArrayInputStream salidaNombreExcel = null;
        ByteArrayInputStream salidaNombreExcel2 = null;
        ByteArrayInputStream salidaNombreExcel3 = null;
        ByteArrayInputStream salidaNombreExcel4 = null;
        ByteArrayInputStream salidaNombreExcel5 = null;
        ByteArrayInputStream[] salida = new ByteArrayInputStream[5];
        String[] nombres = new String[5];

        Map<String, Object> reemplazos = new TreeMap<>();

        reemplazos.put("compania", compania);
        reemplazos.put("anioTrabajo", anio);

        String consulta1 = Reporteador.resuelveConsulta(
                        "800369CONFIGURACION_FUENTE_RECURSOS_PARA_CIERRE_FISCAL",
                        Integer.parseInt(modulo),
                        reemplazos);

        String consulta2 = Reporteador.resuelveConsulta(
                        "800370REVISION_CONFIGURACION_CUENTAS_BANCARIAS_PARA_CIERRE_FISCAL",
                        Integer.parseInt(modulo),
                        reemplazos);

        String consulta3 = Reporteador.resuelveConsulta(
                        "800371REVISION_CONCILIACION_BANCARIA_PARA_CIERRE_FISCAL",
                        Integer.parseInt(modulo),
                        reemplazos);

        String consulta4 = Reporteador.resuelveConsulta(
                        "800372REVISION_RECURSOS_TERCEROS_CIERRE_FISCAL",
                        Integer.parseInt(modulo),
                        reemplazos);

        String consulta5 = Reporteador.resuelveConsulta(
                        "800373REVISION_INFORMACION_PRESUPUESTAL_PARA_CIERRE_FISCAL",
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

        try {
            salidaNombreExcel4 = JsfUtil.serializarHojaDatos(consulta4,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);

        }
        catch (SysmanException | JRException | IOException | DRException
                        | SQLException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        try {
            salidaNombreExcel5 = JsfUtil.serializarHojaDatos(consulta5,
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
            nombres[cantidad] = "RevisionInformacionPresupuestalParaCierreFiscal.xlsx";
            cantidad++;
        }

        if (salidaNombreExcel2 != null) {
            salida[cantidad] = salidaNombreExcel2;
            nombres[cantidad] = "RevisionRecursosTercerosCierreFiscal.xlsx";
            cantidad++;
        }

        if (salidaNombreExcel3 != null) {
            salida[cantidad] = salidaNombreExcel3;
            nombres[cantidad] = "RevisionConciliacionBancariaParaCierreFiscal.xlsx";
            cantidad++;
        }

        if (salidaNombreExcel4 != null) {
            salida[cantidad] = salidaNombreExcel4;
            nombres[cantidad] = "RevisionConfiguracionCuentasBancariasParaCierreFiscal.xlsx";
            cantidad++;
        }

        if (salidaNombreExcel5 != null) {
            salida[cantidad] = salidaNombreExcel5;
            nombres[cantidad] = "RevisionConfiguracionFuenteRecursosParaCierreFiscal.xlsx";
            cantidad++;
        }

        try {

            if (cantidad > 0) {
                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salida,
                                nombres, "VerificarConfiguracionCierreFiscal");

            }

        }
        catch (JRException | IOException | SQLException | DRException e2) {
            logger.error(e2.getMessage(), e2);
            JsfUtil.agregarMensajeError(e2.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public boolean isCkPesos() {
        return ckPesos;
    }

    public void setCkPesos(boolean ckPesos) {
        this.ckPesos = ckPesos;
    }

    public boolean isCkGenerar() {
        return ckGenerar;
    }

    public void setCkGenerar(boolean ckGenerar) {
        this.ckGenerar = ckGenerar;
    }

    public int getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(int trimestre) {
        this.trimestre = trimestre;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable nombreArchivo
     * 
     * @return nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * Asigna la variable nombreArchivo
     * 
     * @param nombreArchivo
     * Variable a asignar en nombreArchivo
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
