/*-
 * LFacturacionControlador.java
 *
 * 1.0
 * 
 * 13/12/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LFacturacionControladorEnum;
import com.sysman.serviciospublicos.enums.LFacturacionControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase que permite obtener informes o reportes de facturacion a
 * partir de la seleccion de una serie de atributos
 *
 * @version 1.0, 13/12/2016
 * @author jreina
 * @version 2.0, 05/06/2017
 * @author jcrodriguez=>Refactoring, creacion de dss y depuracion del
 * controlador
 */
@ManagedBean
@ViewScoped
public class LFacturacionControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor asignado al radiobutton de la
     * forma del formulario.
     */
    private String radio;
    /**
     * Atributo que contiene el valor asignado al ciclo en la forma
     * del formulario.
     */
    private String ciclo;
    /**
     * Atributo que contiene el valor asignado al tipo de informe en
     * la forma del formulario.
     */
    private String tipo;
    /**
     * Atributo que contiene el valor asignado al uso inicial en la
     * forma del formulario.
     */
    private String usoIni;
    /**
     * Atributo que contiene el valor asignado al estrato inicial en
     * la forma del formulario.
     */
    private String estratoIni;
    /**
     * Atributo que contiene el valor asignado al uso final informe en
     * la forma del formulario.
     */
    private String usoFin;
    /**
     * Atributo que contiene el valor asignado al estrato final en la
     * forma del formulario.
     */
    private String estratoFin;
    /**
     * Atributo que contiene el valor asignado a la cedula en la forma
     * del formulario.
     */
    private String cedula;
    /**
     * Atributo que contiene el valor asignado al codigo inicial en la
     * forma del formulario.
     */
    private String codigoInicial;
    /**
     * Atributo que contiene el valor asignado al codigo final en la
     * forma del formulario.
     */
    private String codigoFinal;
    /**
     * Atributo que contiene el valor asignado al barrio inicial en la
     * forma del formulario.
     */
    private String barrioI;
    /**
     * Atributo que contiene el valor asignado al barrio final en la
     * forma del formulario.
     */
    private String barrioF;
    /**
     * Atributo que contiene el valor asignado al nombre para el
     * servicio de Acueducto que se debe imprimir en los reportes
     */
    private String nombreServicioAcueducto;
    /**
     * Atributo que contiene el valor asignado al nombre para el
     * servicio de alcantarillado que se debe imprimir en los reportes
     */
    private String nombreServicioAlcantarillado;
    /**
     * Atributo que contiene el valor asignado al nombre para el
     * servicio de Aseo que se debe imprimir en los reportes
     */
    private String nombreServicioAseo;
    /**
     * Atributo que contiene el valor asignado al nombre para el
     * servicio de Alumbrado que se debe imprimir en los reportes
     */
    private String nombreServicioAlumbrado;
    /**
     * Atributo que contiene el valor asignado para el cambio de
     * nombre en los reportes
     */
    private boolean estado;
    /**
     * Atributo que contiene el valor asignado para el cambio de
     * nombre en los reportes
     */
    private boolean estadoConsumo;
    /**
     * Atributo que contiene el valor asignado para el cambio de
     * nombre del servicio aseo en los reportes
     */
    private boolean estadoAseo;
    /**
     * Atributo que contiene el valor asignado para el numero de
     * letras que debe tener el nombre de un servicio
     */
    private int numCorte;
    /**
     * Atributo que contiene el valor asignado para el cambio a
     * mayusculas en el nombre de los servicios asignados en el
     * reporte.
     */
    private boolean mayuscula;

    /**
     * Atributo que contiene el valor asignado al parametro
     * facturacion riego.
     */
    private boolean facturacionRiego;

    /**
     * Atributo que contiene el valor asignado al parametro maneja
     * barrio suscriptor.
     */
    private boolean manejaBarrioSuscriptor;

    /**
     * Atributo que contiene el valor asignado al parametro cambiar
     * nombre acueducto.
     */
    private boolean cambiarNombreAcueducto;

    /**
     * Atributo que contiene el valor asignado al parametro nombre
     * reemplazo acueducto.
     */
    private String nombreReemplazoAcueducto;

    /**
     * Atributo que contiene el valor asignado al parametro cambiar
     * nombre alcantarillado.
     */
    private boolean cambiarNombreAlcantarillado;

    /**
     * Atributo que contiene el valor asignado al parametro nombre
     * reemplazo acueducto.
     */
    private String nombreReemplazoAlcantarillado;

    /**
     * Atributo que contiene el valor asignado al parametro cambiar
     * nombre aseo.
     */
    private boolean cambiarNombreAseo;

    /**
     * Atributo que contiene el valor asignado al parametro nombre
     * reemplazo aseo.
     */
    private String nombreReemplazoAseo;

    /**
     * Atributo que contiene el valor asignado al parametro cambiar
     * nombre alumbrado.
     */
    private boolean cambiarNombreAlumbrado;

    /**
     * Atributo que contiene el valor asignado al parametro nombre
     * reemplazo alumbrado.
     */
    private String nombreReemplazoAlumbrado;

    /**
     * Atributo que contiene el valor asignado al parametro formato de
     * calidad.
     */
    private boolean formatoCalidad;

    /**
     * Atributo que contiene el valor asignado al parametro manejar
     * resolucion.
     */
    private boolean manejaResolucion;

    /**
     * Atributo que contiene el valor asignado al tamaño para el
     * ajuste del formulario.
     */
    private int ajusteFormulario;

    /**
     * Atributo que contiene el valor asignado a la visibilidad del
     * campo cedula.
     */
    private boolean visibleCedula;

    /**
     * Atributo que contiene el valor asignado a la visibilidad de los
     * radiobutton asignados a la cedula
     */
    private boolean visibleRadioCedula;

    /**
     * Atributo que contiene el valor asignado a la visibilidad del
     * boton que permite descargar en formato excel
     */
    private boolean visibleBotonExcel;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Atributo usado para agregar una condicion And en la consulta
     * del informe
     */
    private String condicion;

    /**
     * Atributo usado para agregar una condicion de orden por ciclo en
     * la consulta del informe
     */
    private String condicionOrden;

    /**
     * Atributo usado para agregar una condicion and en la consulta
     * del informe para el filtado de barrios
     */
    private String condicionBarrio;

    /**
     * Atributo usado para agregar una condicion para traer otra
     * columna en la consulta del informe
     */
    private String condicionCiclo;

    /**
     * Atributo usado para agregar una condicion de order por uso y
     * ciclo en la consulta del informe
     */
    private String condicionOrdenUso;

    /**
     * Atributo usado para agregar una condicion de order por ciclo y
     * o codigo de ruta en la consulta del informe
     */
    private String condicionOrdenCodRuta;

    // </DECLARAR_ATRIBUTOS>

    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>

    // <DECLARAR_LISTAS>
    /** Lista que contiene los detalles del combo ciclo */
    private List<Registro> listaCiclo;

    /** Lista que contiene los detalles del combo uso inicial. */
    private List<Registro> listauSOIni;

    /** Lista que contiene los detalles del combo estrato inicial. */
    private List<Registro> listaEstratoIni;

    /** Lista que contiene los detalles del combo uso final. */
    private List<Registro> listaUsoFin;

    /** Lista que contiene los detalles del combo estrato final. */
    private List<Registro> listaEstratoFin;

    // </DECLARAR_LISTAS>

    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /** Lista que contiene los detalles del combo barrio inicial. */
    private RegistroDataModelImpl listaCmbBarrioI;

    /** Lista que contiene los detalles del combo cedula. */
    private RegistroDataModelImpl listacedula;

    /** Lista que contiene los detalles del combo codigo inicial. */
    private RegistroDataModelImpl listaCodigoInicial;

    /** Lista que contiene los detalles del combo codigo final. */
    private RegistroDataModelImpl listaCodigoFinal;

    /** Lista que contiene los detalles del combo barrio final. */
    private RegistroDataModelImpl listaCmbBarrioF;
    /**
     * EJB
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de LFacturacionControlador
     */
    public LFacturacionControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        radio = "1";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.L_FACTURACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
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
    public void inicializar()
    {
        cargarListaCiclo();
        cargarListauSOIni();
        cargarListaUsoFin();
        cargarListaCmbBarrioI();
        cargarListacedula();
        cargarListaCodigoFinal();
        cargarListaCmbBarrioF();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        cargarParametros();
        nombreServicioAcueducto = "Acuedu.";
        nombreServicioAlcantarillado = "Alcant.";
        nombreServicioAseo = "Aseo";
        nombreServicioAlumbrado = "Alum.";
        estado = false;
        estadoConsumo = false;
        estadoAseo = false;
        numCorte = 6;
        visibleRadioCedula = true;
        visibleBotonExcel = true;

        if (!facturacionRiego && manejaBarrioSuscriptor)
        {
            ajusteFormulario = 304;
        }
        else if (facturacionRiego && !manejaBarrioSuscriptor)
        {
            ajusteFormulario = 240;
        }
        else
        {
            ajusteFormulario = 305;
        }

        if (!facturacionRiego)
        {
            visibleCedula = false;
            visibleRadioCedula = false;
        }
        visibleBotonExcel = manejaBarrioSuscriptor;

    }

    /**
     * 
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFacturacionControladorUrlEnum.URL13660
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listauSOIni
     *
     */
    public void cargarListauSOIni()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listauSOIni = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFacturacionControladorUrlEnum.URL14203
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaEstratoIni
     *
     * 
     */
    public void cargarListaEstratoIni()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LFacturacionControladorEnum.USO_ACTUAL.getValue(), usoIni);
        try
        {
            listaEstratoIni = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFacturacionControladorUrlEnum.URL15001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * consutlar un parametro del sistema con ejb
     * 
     * @param nombre
     * @param indMayus
     * @return
     */
    private String getParametro(String nombre, boolean indMayus)
    {
        try
        {
            return ejbSysmanUtilRemote.consultarParametro(compania, nombre,
                            SessionUtil.getModulo(), new Date(), indMayus);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * 
     * Carga la lista listaUsoFin
     *
     */
    public void cargarListaUsoFin()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), usoIni);
        try
        {
            listaUsoFin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFacturacionControladorUrlEnum.URL16008
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaEstratoFin
     *
     */
    public void cargarListaEstratoFin()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LFacturacionControladorEnum.USO_ACTUAL.getValue(), usoFin);

        try
        {
            listaEstratoFin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFacturacionControladorUrlEnum.URL16796
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCmbBarrioI
     *
     */
    public void cargarListaCmbBarrioI()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LFacturacionControladorUrlEnum.URL17839
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCmbBarrioI = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacedula
     *
     */
    public void cargarListacedula()
    {
        // 243142
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LFacturacionControladorUrlEnum.URL19207
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        "T".equals(ciclo) ? -1 : ciclo);
        param.put(LFacturacionControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);
        param.put(LFacturacionControladorEnum.CODIGOFINAL.getValue(),
                        codigoFinal);
        param.put(LFacturacionControladorEnum.USOINICIAL.getValue(), usoIni);
        param.put(LFacturacionControladorEnum.USOFIN.getValue(), usoFin);
        param.put(LFacturacionControladorEnum.ESTRATOINI.getValue(),
                        estratoIni);
        param.put(LFacturacionControladorEnum.ESTRATOFIN.getValue(),
                        estratoFin);

        listacedula = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, LFacturacionControladorEnum.NIT.getValue());
    }

    /**
     * 
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LFacturacionControladorUrlEnum.URL20914
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        "T".equals(ciclo) ? -1 : ciclo);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LFacturacionControladorUrlEnum.URL22012
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        "T".equals(ciclo) ? -1 : ciclo);
        param.put(LFacturacionControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());

    }

    /**
     * 
     * Carga la lista listaCmbBarrioF
     *
     */
    public void cargarListaCmbBarrioF()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LFacturacionControladorUrlEnum.URL22996
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LFacturacionControladorEnum.CODIGO_BARRIO.getValue(), barrioI);
        listaCmbBarrioF = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }
    // </METODOS_CARGAR_LISTA>

    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Impresora en la vista
     *
     *
     */
    public void oprimirImpresora()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (tipo != null)
        {
            generarReporte(FORMATOS.PDF);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton OtrosExcel en la vista
     *
     *
     */
    public void oprimirOtrosExcel()
    {
        archivoDescarga = null;
        generarExcel();
    }

    // </METODOS_BOTONES>
    /**
     * Obtiene el valor de determinados parametros para su posterior
     * evaluacion
     */
    public void cargarParametros()
    {
        facturacionRiego = ("SI")
                        .equals(getParametro("FACTURACION DE RIEGO", true));
        manejaBarrioSuscriptor = ("SI").equals(
                        getParametro("MANEJA BARRIOS EN SUSCRIPTORES", true));
        cambiarNombreAcueducto = ("SI").equals(getParametro(
                        "CAMBIAR NOMBRE SERVICIO ACUEDUCTO", true));
        nombreReemplazoAcueducto = getParametro(
                        "NOMBRE SERVICIO A REMPLAZAR ACUEDUCTO", false);
        cambiarNombreAlcantarillado = ("SI").equals(getParametro(
                        "CAMBIAR NOMBRE SERVICIO ALCANTARILLADO", true));
        nombreReemplazoAlcantarillado = getParametro(
                        "NOMBRE SERVICIO A REMPLAZAR ALCANTARILLADO", false);
        cambiarNombreAseo = ("SI").equals(
                        getParametro("CAMBIAR NOMBRE SERVICIO ASEO", true));
        nombreReemplazoAseo = getParametro("NOMBRE SERVICIO A REMPLAZAR ASEO",
                        false);
        cambiarNombreAlumbrado = ("SI").equals(getParametro(
                        "CAMBIAR NOMBRE SERVICIO ALUMBRADO", true));
        nombreReemplazoAlumbrado = getParametro(
                        "NOMBRE SERVICIO A REMPLAZAR ALUMBRADO", false);
        formatoCalidad = ("SI").equals(getParametro("FORMATO CALIDAD", true));
        manejaResolucion = ("SI").equals(getParametro("MANEJA RES/ 351", true));
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Extension o tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato)
    {
        String reporte = "";
        try
        {
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            condicion = " AND USUARIO.CICLO = " + ciclo;
            condicionOrden = " ORDER BY    USUARIO.CICLO ";
            condicionBarrio = " AND USUARIO.BARRIO BETWEEN '" + barrioI
                + "' AND '" + barrioF + "' ";
            condicionCiclo = " USUARIO.CICLO, ";
            condicionOrdenUso = " ORDER BY USUARIO.USO, USUARIO.CICLO";
            reporte = seleccionarReporte();

            reemplazar = reemplazarVariables(reemplazar);
            parametros = reemplazarParametros(parametros, reporte);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ", ex.getMessage(), " ", reporte));
            Logger.getLogger(LFacturacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public Map<String, Object> reemplazarVariables(
        Map<String, Object> reemplazar)
    {
        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("condicion", "T".equals(ciclo) ? "" : condicion);
        reemplazar.put("condicionOrden",
                        "T".equals(ciclo) ? condicionOrden : "");

        reemplazar.put("barrioIni",
                        manejaBarrioSuscriptor ? SysmanFunciones.validarVariableVacio(barrioI) ? "'0'" : "'" + barrioI + "'" : "'0'");
        reemplazar.put("barrioFin",
                        manejaBarrioSuscriptor
                            ? SysmanFunciones.validarVariableVacio(barrioF)
                                ? "'" + LFacturacionControladorEnum.NUMERO_NUEVE.getValue() + "'" : "'" + barrioF + "'"
                            : "'" + LFacturacionControladorEnum.NUMERO_NUEVE.getValue() + "'");

        reemplazar.put("condicionCiclo",
                        "T".equals(ciclo) ? "'T'," : condicionCiclo);
        reemplazar.put("condicionOrdenUso", "T".equals(ciclo)
            ? " ORDER BY USUARIO.USO" : condicionOrdenUso);
        reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
        reemplazar.put("codigoFinal", "'" + codigoFinal + "'");
        reemplazar.put("usoIni", "'" + usoIni + "'");
        reemplazar.put("usoFin", "'" + usoFin + "'");
        reemplazar.put("estratoIni", "'" + estratoIni + "'");
        reemplazar.put("estratoFin", "'" + estratoFin + "'");
        reemplazar.put("cedula", "'" + cedula + "'");
        // </REEMPLAZAR VARIABLES EN CONSULTA>
        return reemplazar;
    }

    public Map<String, Object> reemplazarParametros(
        Map<String, Object> parametros, String reporte)
    {
        // <ENVIAR PARAMETROS AL REPORTE>
        parametros.put("PR_CICLO", ciclo);
        parametros.put("PR_COMPANIA", compania);
        parametros.put("PR_PERIODO", recuperarPeriodo());
        parametros.put("PR_NOMSERVICIOAC", nombreServicioAcueducto);
        parametros.put("PR_NOMSERVICIOAL", nombreServicioAlcantarillado);
        parametros.put("PR_NOMSERVICIOAS", nombreServicioAseo);
        parametros.put("PR_NOMSERVICIOALU", nombreServicioAlumbrado);
        parametros.put("PR_VALOR", "001325LFacturacionAl".equals(reporte) ? "Valor Vertimiento" : "Valor Consumo");
        parametros.put("PR_ESTADO", String.valueOf(estado));
        parametros.put("PR_ESTADOASEO", String.valueOf(estadoAseo));
        parametros.put("PR_ESTADOCONSUMO", String.valueOf(estadoConsumo));
        parametros.put("PR_RESOLUCION", String.valueOf(manejaResolucion));

        // </ENVIAR PARAMETROS AL REPORTE>
        return parametros;
    }

    /**
     * Genera un reporte con formato excel
     */
    private void generarExcel()
    {
        Map<String, Object> reemplazos = new HashMap<>();
        condicion = " AND USUARIO.CICLO = " + ciclo;
        condicionCiclo = " USUARIO.CICLO, ";
        condicionOrdenCodRuta = " ORDER BY USUARIO.CICLO, USUARIO.CODIGORUTA";
        condicionBarrio = " AND USUARIO.BARRIO BETWEEN '" + barrioI + "' AND '"
            + barrioF + "' ";

        String reporte = seleccionarExcel();
        reemplazos = reemplazarVariablesExcel(reemplazos);

        String strSql = Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazos);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            String nombre = "InformeServiciosCausacion";
            if ("7".equals(tipo) || "8".equals(tipo))
            {
                Workbook workbook = new HSSFWorkbook(
                                JsfUtil.exportarHojaDatosStreamed(strSql,
                                                ConectorPool.ESQUEMA_SYSMAN,
                                                FORMATOS.EXCEL97).getStream());
                Sheet sheet = workbook.getSheet("Report");
                sheet.shiftRows(0, sheet.getLastRowNum(), 9);

                sheet.createFreezePane(1, 10, 1, 10);
                int nColumnas = Math.max(sheet.getRow(9).getLastCellNum(), 0)
                    - 1;

                Font font = workbook.createFont();
                font.setFontName("Calibri");
                font.setFontHeightInPoints((short) 14);

                CellStyle style = workbook.createCellStyle();
                style.setAlignment(CellStyle.ALIGN_CENTER);
                style.setFont(font);

                Font font2 = workbook.createFont();
                font2.setFontName("Calibri");
                font2.setFontHeightInPoints((short) 11);
                font2.setBold(true);

                CellStyle style2 = workbook.createCellStyle();
                style2.setAlignment(CellStyle.ALIGN_CENTER);
                style2.setFont(font2);
                style2.setBorderBottom((short) 1);
                style2.setBorderLeft((short) 1);
                style2.setBorderTop((short) 1);
                style2.setBorderRight((short) 1);

                nombre = "7".equals(tipo)
                    ? generarEncabezado(sheet, style, style2, nColumnas)
                    : nombre;

                String d2 = sheet.getRow(10)
                                .getCell(2, Row.RETURN_BLANK_AS_NULL)
                                .getStringCellValue();

                ArrayList<Integer> lista = new ArrayList<>();

                /* Generacion de subtotales por uso */
                generarSubtotalesPorUso(sheet, d2, lista, workbook, style2);

                sheet.shiftRows(sheet.getLastRowNum(), sheet.getLastRowNum(),
                                3);
                Row rt = sheet.createRow(sheet.getLastRowNum());
                Cell cellTotales = rt.createCell(3);
                cellTotales.setCellValue("TOTALES");
                cellTotales.setCellStyle(style2);

                /* Generacion de Totales finales */
                generarTotalesFinales(sheet, rt, lista, style2, workbook);

                workbook.write(out);
                out.close();

                archivoDescarga = JsfUtil
                                .getArchivoDescarga(new ByteArrayInputStream(
                                                out.toByteArray()),
                                                nombre + ".xls");
                workbook.close();

            }
            else
            {
                List<Registro> rs = service.getListado(
                                ConectorPool.ESQUEMA_SYSMAN,
                                strSql);
                if (!rs.isEmpty())
                {
                    archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    FORMATOS.EXCEL);
                }
                else
                {
                    JsfUtil.agregarMensajeError(idioma.getString(
                                    "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
                }

            }
        }
        catch (JRException | IOException | DRException | SQLException
                        | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (NullPointerException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            Logger.getLogger(LFacturacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }
    }

    public void generarSubtotalesPorUso(Sheet sheet, String d2,
        List<Integer> lista, Workbook workbook, CellStyle style2)
    {
        /* Generacion de subtotales por uso */
        String d3;
        String d1 = d2;
        int num = 10;
        for (int i = 10; i <= sheet.getLastRowNum(); i++)
        {
            d3 = sheet.getRow(i).getCell(2, Row.RETURN_BLANK_AS_NULL) != null
                ? sheet.getRow(i).getCell(2, Row.RETURN_BLANK_AS_NULL)
                                .getStringCellValue()
                : null;

            if (d1 != null && !d1.equals(d3) && d3 != null)
            {
                sheet.shiftRows(i, sheet.getLastRowNum(), 3);
                generarTotalUso(sheet, i, num, d1, lista, workbook, style2);
                num = i + 3;
                d1 = d3;
                i = i + 4;
            }
        }
        sheet.createRow(sheet.getLastRowNum() + 1);
        sheet.shiftRows(sheet.getLastRowNum(), sheet.getLastRowNum(), 3);
        generarTotalUso(sheet, sheet.getLastRowNum() - 2, num, d2, lista,
                        workbook, style2);
    }

    public Map<String, Object> reemplazarVariablesExcel(
        Map<String, Object> reemplazos)
    {
        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazos.put("condicionCiclo",
                        "T".equals(ciclo) ? "'T'," : condicionCiclo);
        reemplazos.put("condicion", "T".equals(ciclo) ? "" : condicion);
        reemplazos.put("condicionOrdenCodRuta", "T".equals(ciclo)
            ? condicionOrdenCodRuta : " ORDER BY USUARIO.CODIGORUTA");
        reemplazos.put("barrioIni",
                        manejaBarrioSuscriptor ? SysmanFunciones.validarVariableVacio(barrioI) ? "'0'" : "'" + barrioI + "'" : "'0'");
        reemplazos.put("barrioFin",
                        manejaBarrioSuscriptor
                            ? SysmanFunciones.validarVariableVacio(barrioF)
                                ? "'" + LFacturacionControladorEnum.NUMERO_NUEVE.getValue() + "'" : "'" + barrioF + "'"
                            : "'" + LFacturacionControladorEnum.NUMERO_NUEVE.getValue() + "'");

        reemplazos.put("compania", "'" + compania + "'");
        reemplazos.put("codigoInicial", "'" + codigoInicial + "'");
        reemplazos.put("codigoFinal", "'" + codigoFinal + "'");
        reemplazos.put("usoIni", "'" + usoIni + "'");
        reemplazos.put("usoFin", "'" + usoFin + "'");
        reemplazos.put("estratoIni", "'" + estratoIni + "'");
        reemplazos.put("estratoFin", "'" + estratoFin + "'");
        reemplazos.put("barrioInicial", "'" + barrioI + "'");
        reemplazos.put("barrioFinal", "'" + barrioF + "'");

        // </REEMPLAZAR VARIABLES EN CONSULTA>
        return reemplazos;
    }

    public String generarEncabezado(Sheet sheet, CellStyle style,
        CellStyle style2, int nColumnas)
    {

        CellReference celdaTitulo1Ini = new CellReference(4, 0);
        String titulo1Ini = celdaTitulo1Ini.formatAsString();
        CellReference celdaTitulo1Fin = new CellReference(5, nColumnas);
        String titulo1Fin = celdaTitulo1Fin.formatAsString();
        CellRangeAddress region = CellRangeAddress
                        .valueOf("" + titulo1Ini + ":" + titulo1Fin);
        sheet.addMergedRegion(region);

        CellReference celdaSubTitulo1Ini = new CellReference(8, 6);
        String subTitulo1Ini = celdaSubTitulo1Ini.formatAsString();
        CellReference celdaSubTitulo1Fin = new CellReference(8, 12);
        String subTitulo1Fin = celdaSubTitulo1Fin.formatAsString();
        CellRangeAddress region2 = CellRangeAddress
                        .valueOf("" + subTitulo1Ini + ":" + subTitulo1Fin);
        sheet.addMergedRegion(region2);

        CellReference celdaSubTitulo2Ini = new CellReference(8, 13);
        String subTitulo2Ini = celdaSubTitulo2Ini.formatAsString();
        CellReference celdaSubTitulo2Fin = new CellReference(8, 20);
        String subTitulo2Fin = celdaSubTitulo2Fin.formatAsString();
        CellRangeAddress region3 = CellRangeAddress
                        .valueOf("" + subTitulo2Ini + ":" + subTitulo2Fin);
        sheet.addMergedRegion(region3);

        /* Titulo 1 */
        Cell cell1 = sheet.createRow(4).createCell(0);
        cell1.setCellValue("FACTURACIÓN GENERAL CON RECAUDOS");
        cell1.setCellStyle(style);
        cell1.getStringCellValue();

        Row r = sheet.createRow(8);

        /* Subtitulos */
        Cell cell2 = r.createCell(6);
        cell2.setCellValue("VALORES FACTURADOS");
        cell2.setCellStyle(style2);

        Cell cell3 = r.createCell(13);
        cell3.setCellValue("VALORES RECAUDADOS");
        cell3.setCellStyle(style2);

        Cell cell4 = sheet.createRow(6).createCell(0);
        try
        {
            cell4.setCellValue(SysmanFunciones.convertirAFechaCadena(new Date(),
                            "dd' - 'MM' - 'yyyy"));
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cell4.setCellStyle(style2);

        return "InformeServicios";
    }

    public void generarTotalesFinales(Sheet sheet, Row rt, List<Integer> lista,
        CellStyle style2, Workbook workbook)
    {
        for (int i = 4; i <= (Math.max(sheet.getRow(9).getLastCellNum(), 0)
            - 1); i++)
        {
            Cell celda = rt.createCell(i);
            celda.setCellType(Cell.CELL_TYPE_FORMULA);
            StringBuilder celdaIni = new StringBuilder("");
            for (int j = 0; j < lista.size(); j++)
            {
                if (sheet.getRow(lista.get(j)).getCell(i,
                                Row.RETURN_BLANK_AS_NULL) != null)
                {
                    CellReference cellRefIni = new CellReference(lista.get(j),
                                    i);
                    celdaIni.append(cellRefIni.formatAsString() + ",");
                }
            }
            celda.setCellFormula("SUM(" + celdaIni.toString() + ")");
            style2.setDataFormat(workbook.createDataFormat()
                            .getFormat("#,##0.00"));
            celda.setCellStyle(style2);

        }
    }

    /**
     * Genera subtotales para un reporte en excel teniendo en cuenta
     * un determinado uso
     * 
     * @param sheet
     * Hoja del excel a modificar
     * @param i
     * Fila de la hoja del excel donde termina los valores para un uso
     * @param num
     * Fila de la hoja del excel donde comienza los valores para un
     * uso
     * @param d2
     * Nombre del uso
     * @param lista
     * Lista en donde se almacena las posiciones donde se encuentran
     * los subtotales
     * @param workbook
     * @param style2
     * Estilo aplicado a las celdas
     */
    public void generarTotalUso(Sheet sheet, int i, int num, String d2,
        List<Integer> lista, Workbook workbook, CellStyle style2)
    {
        Cell cellTotales1 = sheet.createRow(i + 1).createCell(3);
        cellTotales1.setCellValue("TOTAL USO " + d2);
        cellTotales1.setCellStyle(style2);

        lista.add(cellTotales1.getRowIndex());

        for (int j = 4; j <= (Math.max(sheet.getRow(9).getLastCellNum(), 0)
            - 1); j++)
        {
            Cell cellTotalesFormula = sheet.getRow(i + 1)
                            .createCell(j);
            cellTotalesFormula.setCellType(Cell.CELL_TYPE_FORMULA);
            CellReference cellRefIni = new CellReference(num, j);
            CellReference cellRefFin = new CellReference(i, j);
            String celdaIni = cellRefIni.formatAsString();
            String celdaFin = cellRefFin.formatAsString();
            cellTotalesFormula.setCellFormula(
                            "SUM(" + celdaIni + ":" + celdaFin + ")");
            style2.setDataFormat(workbook.createDataFormat()
                            .getFormat("#,##0.00"));

            cellTotalesFormula.setCellStyle(style2);
        }
    }

    /**
     * obtener el reporte
     */
    private String getReporte(boolean opcion)
    {
        return opcion ? ("1".equals(radio)
            ? "001294LFacturacionNormalAreayCedula"
            : "001301LFacturacionNOrmalAgrupadoxCedula")
            : (formatoCalidad ? "001305LFacturacionCOS"
                : "001310LFacturacionNormal");
    }

    /**
     * Determina cual reporte se debe generar dependiendo del valor de
     * la variable tipo.
     * 
     * @return El nombre del reporte a generar.
     */
    public String seleccionarReporte()
    {
        String reporte = null;

        switch (tipo)
        {
        case "1":
            reporte = facturacionRiego ? getReporte(true) : getReporte(false);
            break;
        case "2":
            reporte = formatoCalidad ? "001315LFacturacionAcCOS"
                : "001318LFacturacionAc";
            break;
        case "3":
            reporte = formatoCalidad ? "001323LFacturacionAlCOS"
                : "001325LFacturacionAl";
            break;
        case "4":
            reporte = formatoCalidad ? "001314LFacturacionAsCOS"
                : "001329LfacturacionAs";
            estado = !formatoCalidad;
            break;
        case "5":
            reporte = formatoCalidad ? "001331LFacturacionGrandeCOS"
                : "001338LFacturacionGrande";
            break;
        case "6":
            reporte = "001345LFacturacionsub";
            break;
        case "7":
            reporte = "001346LFacturacionConRecaudos";
            estadoAseo = true;
            break;
        case "8":
            reporte = "001347LfacturacionConSub";
            mayuscula = true;
            estadoAseo = true;
            break;
        case "9":
            reporte = "001348LFacturacionSinPagos";
            break;
        default:
            break;
        }
        reemplazarNombre();
        return reporte;
    }

    /**
     * Reemplaza el nombre de un determinado servicio segun los
     * parametros configurados previamente
     */
    public void reemplazarNombre()
    {

        if (cambiarNombreAcueducto)
        {
            nombreServicioAcueducto = obtenerParteCadena(nombreReemplazoAcueducto);

        }
        if (cambiarNombreAlcantarillado)
        {
            nombreServicioAlcantarillado = obtenerParteCadena(nombreReemplazoAlcantarillado);

            estadoConsumo = true;
        }
        if (cambiarNombreAseo)
        {
            nombreServicioAseo = obtenerParteCadena(nombreReemplazoAseo);
            if (estado)
            {
                nombreServicioAseo = nombreReemplazoAseo;
            }
        }
        else
        {
            estado = false;
            estadoAseo = false;
        }

        if (cambiarNombreAlumbrado)
        {
            nombreServicioAlumbrado = obtenerParteCadena(nombreReemplazoAlumbrado);

        }

        if (mayuscula)
        {
            nombreServicioAcueducto = nombreServicioAcueducto.toUpperCase();
            nombreServicioAlcantarillado = nombreServicioAlcantarillado
                            .toUpperCase();
            nombreServicioAseo = nombreServicioAseo.toUpperCase();
            mayuscula = false;
        }

    }

    private String obtenerParteCadena(String nombreReemplazo)
    {
        if (nombreReemplazo.length() >= numCorte)
        {
            return SysmanFunciones.concatenar(nombreReemplazo.substring(0, numCorte), ".");
        }
        else
        {
            return SysmanFunciones.concatenar(nombreReemplazo.substring(0, nombreReemplazo.length()), ".");
        }
    }

    /**
     * Determina cual reporte se debe generar dependiendo del valor de
     * la variable tipo.
     * 
     * @return El nombre del reporte a generar.
     */
    public String seleccionarExcel()
    {
        String excel = null;

        if ("1".equals(tipo))
        {
            excel = "800077ListadoFacturacionNormal";
        }
        else if ("2".equals(tipo))
        {
            excel = "800078ListadoFacturacionAc";
        }
        else if ("3".equals(tipo))
        {
            excel = "800079ListadoFacturacionAlc";
        }
        else if ("4".equals(tipo))
        {
            excel = "800080ListadoFacturacionAseo";
        }
        else if ("5".equals(tipo))
        {
            excel = "800081ListadoFacturacionExtendido";
        }
        else if ("6".equals(tipo))
        {
            excel = "800082ListadoFacturacionSub_Sob";
        }
        else if ("7".equals(tipo))
        {
            excel = "800085InformeServicios";
        }
        else if ("8".equals(tipo))
        {
            excel = "800084InformeServiciosCausacion";
        }
        else if ("9".equals(tipo))
        {
            excel = "800083ListadoFacturacionSinPagos";
        }
        return excel;
    }

    /**
     * Obtiene el nombre del periodo para un determinado ciclo
     * 
     * @return Nombre del periodo
     */
    public String recuperarPeriodo()
    {
        /* Consulta para recuperar el periodo del ciclo. */
        if (("T").equals(ciclo))
        {
            return LFacturacionControladorEnum.TODOS.getValue();
        }
        else
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

            Registro registro = null;
            try
            {
                registro = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                LFacturacionControladorUrlEnum.URL13622
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            /* Nombre del periodo. */
            String periodo = registro == null ? null
                : registro.getCampos()
                                .get(LFacturacionControladorEnum.NOMBREPERIODO
                                                .getValue())
                                .toString();
            return periodo == null
                ? LFacturacionControladorEnum.TODOS.getValue() : periodo;
        }
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     * 
     * 
     */
    public void cambiarCiclo()
    {
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        codigoInicial = "";
        codigoFinal = "";
    }

    /**
     * Metodo ejecutado al cambiar el control UsoIni
     * 
     * 
     */
    public void cambiaruSOIni()
    {
        cargarListaUsoFin();
        cargarListaEstratoIni();
        cargarListacedula();
    }

    /**
     * Metodo ejecutado al cambiar el control UsoFin
     * 
     * 
     */
    public void cambiarUsoFin()
    {
        cargarListaEstratoFin();
        cargarListacedula();
    }

    /**
     * Metodo ejecutado al cambiar el control (Marco50) RadioButton
     * 
     * 
     */
    public void cambiarMarco50()
    {
        if ("2".equals(radio))
        {
            visibleCedula = true;
            cargarListacedula();
        }
        else
        {
            visibleCedula = false;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control EstratoIni
     * 
     * 
     */
    public void cambiarEstratoIni()
    {
        cargarListacedula();
    }

    /**
     * Metodo ejecutado al cambiar el control EstratoFin
     * 
     * 
     */
    public void cambiarEstratoFin()
    {
        cargarListacedula();
    }

    /**
     * Metodo ejecutado al cambiar el control cmbTipo
     * 
     * 
     */
    public void cambiarcmbTipo()
    {
        if ("1".equals(tipo))
        {
            if (!facturacionRiego)
            {
                visibleCedula = false;
                visibleRadioCedula = false;
            }
            else
            {
                visibleRadioCedula = true;
            }
        }
        else
        {
            visibleRadioCedula = false;
            visibleCedula = false;
        }
        if ("7".equals(tipo) || "8".equals(tipo))
        {
            visibleBotonExcel = manejaBarrioSuscriptor;
        }
        else
        {
            visibleBotonExcel = true;
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacedula
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacedula(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cedula = validarCadena(registroAux.getCampos(), LFacturacionControladorEnum.NIT.getValue());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = validarCadena(registroAux.getCampos(), LFacturacionControladorEnum.CODIGORUTA.getValue());
        cargarListaCodigoFinal();
        cargarListacedula();
    }

    private String validarCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = validarCadena(registroAux.getCampos(), LFacturacionControladorEnum.CODIGORUTA.getValue());
        cargarListacedula();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbBarrioF
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbBarrioF(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        barrioF = validarCadena(registroAux.getCampos(), GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbBarrioI
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbBarrioI(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        barrioI = validarCadena(registroAux.getCampos(), GeneralParameterEnum.CODIGO.getName());
        barrioF = null;
        cargarListaCmbBarrioF();
    }

    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo()
    {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable tipo
     * 
     * @return tipo
     */
    public String getTipo()
    {
        return tipo;
    }

    /**
     * Asigna la variable tipo
     * 
     * @param tipo
     * Variable a asignar en tipo
     */
    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    /**
     * Retorna la variable usoIni
     * 
     * @return usoIni
     */
    public String getUsoIni()
    {
        return usoIni;
    }

    /**
     * Asigna la variable usoIni
     * 
     * @param usoIni
     * Variable a asignar en usoIni
     */
    public void setUsoIni(String usoIni)
    {
        this.usoIni = usoIni;
    }

    /**
     * Retorna la variable estratoIni
     * 
     * @return estratoIni
     */
    public String getEstratoIni()
    {
        return estratoIni;
    }

    /**
     * Asigna la variable estratoIni
     * 
     * @param estratoIni
     * Variable a asignar en estratoIni
     */
    public void setEstratoIni(String estratoIni)
    {
        this.estratoIni = estratoIni;
    }

    /**
     * Retorna la variable usoFin
     * 
     * @return usoFin
     */
    public String getUsoFin()
    {
        return usoFin;
    }

    /**
     * Asigna la variable usoFin
     * 
     * @param usoFin
     * Variable a asignar en usoFin
     */
    public void setUsoFin(String usoFin)
    {
        this.usoFin = usoFin;
    }

    /**
     * Retorna la variable estratoFin
     * 
     * @return estratoFin
     */
    public String getEstratoFin()
    {
        return estratoFin;
    }

    /**
     * Asigna la variable estratoFin
     * 
     * @param estratoFin
     * Variable a asignar en estratoFin
     */
    public void setEstratoFin(String estratoFin)
    {
        this.estratoFin = estratoFin;
    }

    /**
     * Retorna la variable cedula
     * 
     * @return cedula
     */
    public String getCedula()
    {
        return cedula;
    }

    /**
     * Asigna la variable cedula
     * 
     * @param cedula
     * Variable a asignar en cedula
     */
    public void setCedula(String cedula)
    {
        this.cedula = cedula;
    }

    /**
     * Retorna la variable codigoInicial
     * 
     * @return codigoInicial
     */
    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     * 
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     * 
     * @return codigoFinal
     */
    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     * 
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable barrioI
     * 
     * @return barrioI
     */
    public String getBarrioI()
    {
        return barrioI;
    }

    /**
     * Asigna la variable barrioI
     * 
     * @param barrioI
     * Variable a asignar en barrioI
     */
    public void setBarrioI(String barrioI)
    {
        this.barrioI = barrioI;
    }

    /**
     * Retorna la variable barrioF
     * 
     * @return barrioF
     */
    public String getBarrioF()
    {
        return barrioF;
    }

    /**
     * Asigna la variable barrioF
     * 
     * @param barrioF
     * Variable a asignar en barrioF
     */
    public void setBarrioF(String barrioF)
    {
        this.barrioF = barrioF;
    }

    /**
     * Retorna la variable facturacionRiego
     * 
     * @return facturacionRiego
     */
    public boolean isFacturacionRiego()
    {
        return facturacionRiego;
    }

    /**
     * Asigna el valor a facturacion riego
     * 
     * @param facturacionRiego
     * 
     */
    public void setFacturacionRiego(boolean facturacionRiego)
    {
        this.facturacionRiego = facturacionRiego;
    }

    /**
     * Retorna la variable manejaBarrioSuscriptor
     * 
     * @return manejaBarrioSuscriptor
     */
    public boolean isManejaBarrioSuscriptor()
    {
        return manejaBarrioSuscriptor;
    }

    /**
     * Asigna el valor a manejaBarrioSuscriptor
     * 
     * @param manejaBarrioSuscriptor
     * 
     */
    public void setManejaBarrioSuscriptor(boolean manejaBarrioSuscriptor)
    {
        this.manejaBarrioSuscriptor = manejaBarrioSuscriptor;
    }

    /**
     * Retorna la variable ajusteFormulario
     * 
     * @return ajusteFormulario
     */
    public int getAjusteFormulario()
    {
        return ajusteFormulario;
    }

    /**
     * Asigna el valor ha ajusteFormulario
     * 
     * @param ajusteFormulario
     * 
     */
    public void setAjusteFormulario(int ajusteFormulario)
    {
        this.ajusteFormulario = ajusteFormulario;
    }

    /**
     * Retorna la variable visibleCedula
     * 
     * @return visibleCedula
     */
    public boolean isVisibleCedula()
    {
        return visibleCedula;
    }

    /**
     * Asigna el valor a visibleCedula
     * 
     * @param visibleCedula
     * 
     */
    public void setVisibleCedula(boolean visibleCedula)
    {
        this.visibleCedula = visibleCedula;
    }

    /**
     * Retorna la variable visibleRadioCedula
     * 
     * @return visibleRadioCedula
     */
    public boolean isVisibleRadioCedula()
    {
        return visibleRadioCedula;
    }

    /**
     * Asigna el valor a visibleRadioCedula
     * 
     * @param visibleRadioCedula
     * 
     */
    public void setVisibleRadioCedula(boolean visibleRadioCedula)
    {
        this.visibleRadioCedula = visibleRadioCedula;
    }

    /**
     * Retorna la variable archivoDescarga
     * 
     * @return archivoDescarga
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * Retorna la variable visibleBotonExcel
     * 
     * @return visibleBotonExcel
     */
    public boolean isVisibleBotonExcel()
    {
        return visibleBotonExcel;
    }

    /**
     * Asigna el valor ha visibleBotonExcel
     * 
     * @param visibleBotonExcel
     * 
     */
    public void setVisibleBotonExcel(boolean visibleBotonExcel)
    {
        this.visibleBotonExcel = visibleBotonExcel;
    }

    /**
     * Asigna el valor ha archivoDescarga
     * 
     * @param archivoDescarga
     * 
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * Retorna la variable condicion
     * 
     * @return condicion
     */
    public String getCondicion()
    {
        return condicion;
    }

    /**
     * Asigna el valor a condicion
     * 
     * @param condicion
     * 
     */
    public void setCondicion(String condicion)
    {
        this.condicion = condicion;
    }

    /**
     * Retorna la variable condicionCiclo
     * 
     * @return condicionCiclo
     */
    public String getCondicionCiclo()
    {
        return condicionCiclo;
    }

    /**
     * Asigna el valor a condicionCiclo
     * 
     * @param condicionCiclo
     * 
     */
    public void setCondicionCiclo(String condicionCiclo)
    {
        this.condicionCiclo = condicionCiclo;
    }

    /**
     * Retorna la variable cambiarNombreAcueducto
     * 
     * @return cambiarNombreAcueducto
     */
    public boolean isCambiarNombreAcueducto()
    {
        return cambiarNombreAcueducto;
    }

    /**
     * Asigna el valor a cambiarNombreAcueducto
     * 
     * @param cambiarNombreAcueducto
     * 
     */
    public void setCambiarNombreAcueducto(boolean cambiarNombreAcueducto)
    {
        this.cambiarNombreAcueducto = cambiarNombreAcueducto;
    }

    /**
     * Retorna la variable cambiarNombreAlcantarillado
     * 
     * @return cambiarNombreAlcantarillado
     */
    public boolean isCambiarNombreAlcantarillado()
    {
        return cambiarNombreAlcantarillado;
    }

    /**
     * Asigna el valor a cambiarNombreAlcantarillado
     * 
     * @param cambiarNombreAlcantarillado
     * 
     */
    public void setCambiarNombreAlcantarillado(
        boolean cambiarNombreAlcantarillado)
    {
        this.cambiarNombreAlcantarillado = cambiarNombreAlcantarillado;
    }

    /**
     * Retorna la variable cambiarNombreAseo
     * 
     * @return cambiarNombreAseo
     */
    public boolean isCambiarNombreAseo()
    {
        return cambiarNombreAseo;
    }

    /**
     * Asigna el valor a cambiarNombreAseo
     * 
     * @param cambiarNombreAseo
     * 
     */
    public void setCambiarNombreAseo(boolean cambiarNombreAseo)
    {
        this.cambiarNombreAseo = cambiarNombreAseo;
    }

    /**
     * Retorna la variable condicionOrden
     * 
     * @return condicionOrden
     */
    public String getCondicionOrden()
    {
        return condicionOrden;
    }

    /**
     * Asigna el valor a condicionOrden
     * 
     * @param condicionOrden
     * 
     */
    public void setCondicionOrden(String condicionOrden)
    {
        this.condicionOrden = condicionOrden;
    }

    /**
     * Retorna la variable condicionBarrio
     * 
     * @return condicionBarrio
     */
    public String getCondicionBarrio()
    {
        return condicionBarrio;
    }

    /**
     * Asigna el valor a condicionBarrio
     * 
     * @param condicionBarrio
     * 
     */
    public void setCondicionBarrio(String condicionBarrio)
    {
        this.condicionBarrio = condicionBarrio;
    }

    /**
     * Retorna la variable condicionOrdenUso
     * 
     * @return condicionOrdenUso
     */
    public String getCondicionOrdenUso()
    {
        return condicionOrdenUso;
    }

    /**
     * Asigna el valor a condicionOrdenUso
     * 
     * @param condicionOrdenUso
     * 
     */
    public void setCondicionOrdenUso(String condicionOrdenUso)
    {
        this.condicionOrdenUso = condicionOrdenUso;
    }

    // </SET_GET_ATRIBUTOS>

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>

    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo()
    {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la variable radio
     * 
     * @return radio
     */
    public String getRadio()
    {
        return radio;
    }

    /**
     * Asigna la variable radio
     * 
     * @param radio
     * Variable a asignar en radio
     */
    public void setRadio(String radio)
    {
        this.radio = radio;
    }

    /**
     * Retorna la lista listauSOIni
     * 
     * @return listauSOIni
     */
    public List<Registro> getListauSOIni()
    {
        return listauSOIni;
    }

    /**
     * Asigna la lista listauSOIni
     * 
     * @param listauSOIni
     * Variable a asignar en listauSOIni
     */
    public void setListauSOIni(List<Registro> listauSOIni)
    {
        this.listauSOIni = listauSOIni;
    }

    /**
     * Retorna la lista listaEstratoIni
     * 
     * @return listaEstratoIni
     */
    public List<Registro> getListaEstratoIni()
    {
        return listaEstratoIni;
    }

    /**
     * Asigna la lista listaEstratoIni
     * 
     * @param listaEstratoIni
     * Variable a asignar en listaEstratoIni
     */
    public void setListaEstratoIni(List<Registro> listaEstratoIni)
    {
        this.listaEstratoIni = listaEstratoIni;
    }

    /**
     * Retorna la lista listaUsoFin
     * 
     * @return listaUsoFin
     */
    public List<Registro> getListaUsoFin()
    {
        return listaUsoFin;
    }

    /**
     * Asigna la lista listaUsoFin
     * 
     * @param listaUsoFin
     * Variable a asignar en listaUsoFin
     */
    public void setListaUsoFin(List<Registro> listaUsoFin)
    {
        this.listaUsoFin = listaUsoFin;
    }

    /**
     * Retorna la lista listaEstratoFin
     * 
     * @return listaEstratoFin
     */
    public List<Registro> getListaEstratoFin()
    {
        return listaEstratoFin;
    }

    /**
     * Asigna la lista listaEstratoFin
     * 
     * @param listaEstratoFin
     * Variable a asignar en listaEstratoFin
     */
    public void setListaEstratoFin(List<Registro> listaEstratoFin)
    {
        this.listaEstratoFin = listaEstratoFin;
    }

    // </SET_GET_LISTAS>

    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacedula
     * 
     * @return listacedula
     */
    public RegistroDataModelImpl getListacedula()
    {
        return listacedula;
    }

    /**
     * Asigna la lista listacedula
     * 
     * @param listacedula
     * Variable a asignar en listacedula
     */
    public void setListacedula(RegistroDataModelImpl listacedula)
    {
        this.listacedula = listacedula;
    }

    /**
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial)
    {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal()
    {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal)
    {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    /**
     * Retorna la lista listaCmbBarrioF
     * 
     * @return listaCmbBarrioF
     */
    public RegistroDataModelImpl getListaCmbBarrioF()
    {
        return listaCmbBarrioF;
    }

    /**
     * Asigna la lista listaCmbBarrioF
     * 
     * @param listaCmbBarrioF
     * Variable a asignar en listaCmbBarrioF
     */
    public void setListaCmbBarrioF(RegistroDataModelImpl listaCmbBarrioF)
    {
        this.listaCmbBarrioF = listaCmbBarrioF;
    }

    /**
     * Retorna la lista listaCmbBarrioI
     * 
     * @return listaCmbBarrioI
     */
    public RegistroDataModelImpl getListaCmbBarrioI()
    {
        return listaCmbBarrioI;
    }

    /**
     * Asigna la lista listaCmbBarrioI
     * 
     * @param listaCmbBarrioI
     * Variable a asignar en listaCmbBarrioI
     */
    public void setListaCmbBarrioI(RegistroDataModelImpl listaCmbBarrioI)
    {
        this.listaCmbBarrioI = listaCmbBarrioI;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>

}