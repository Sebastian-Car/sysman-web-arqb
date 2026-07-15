/*-
 * ReservaspptalesControlador.java
 *
 * 1.0
 * 
 * 31/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.ejb.EjbChipFutUnoRemote;
import com.sysman.chipfut.enums.ReservaspptalesfutControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * 
 *
 * @version 1.0, 31/03/2017
 * @author jlramirez
 * 
 * @author amonroy
 * @version 2, 25/07/2018. Se crea la logica del formulario, es decir
 * la generación del reporte, se realiza la documentación del
 * controlador, implementación de Refactory y llamados a EJBs
 */
@ManagedBean
@ViewScoped
public class ReservaspptalesfutControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del indicador <i>En miles de
     * pesos</i>, para aplicar el redondeo o no en la consulta del
     * informe
     */
    private boolean pesos;
    /**
     * Atributo que almacena el valor del indicador <i>Informe de
     * Verificacion</i>
     */
    private boolean verificacion;
    /**
     * Atributo que almacena el valor del indicador <i>Separadas</i>,
     * el cual es utilizado para definir el valor de un campo que se
     * visualiza en el informe
     */
    private boolean separadas;
    /**
     * Atributo que almacena el trimestre seleccionado para generar el
     * reporte
     */
    private String trimestre;
    /**
     * Anio de trabajo para generar el infore
     */
    private int anio;
    /**
     * Tipo de acto administrativo que ha sido seleccionado en el
     * formulario
     */
    private String actoAdmin;
    /**
     * Codigo Chip de la entidad
     */
    private String codEntidad;
    /**
     * Numero de documento administrativo que ha sido definido en el
     * formulario
     */
    private String nroActoadmin;
    /**
     * Fecha en la que se genera el documento
     */
    private Date fechaDoc;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para seleccionar el anio de trabajo
     */
    private List<Registro> listaAnoTrabajo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Implementacion del EJB de EjbChipFutUnoRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_CHIPFUT1
     */
    @EJB
    private EjbChipFutUnoRemote ejbChipFutUno;

    /**
     * Crea una nueva instancia de ReservaspptalesfutControlador
     */
    public ReservaspptalesfutControlador() {
        super();
        compania = SessionUtil.getCompania();
        codEntidad = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
        try {
            numFormulario = GeneralCodigoFormaEnum.RESERVASPPTALESFUT_CONTROLADOR
                            .getCodigo();// 1393
            validarPermisos();
            // <INI_ADICIONAL>
            anio = SysmanFunciones.ano(new Date());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ReservaspptalesfutControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAnoTrabajo
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReservaspptalesfutControladorUrlEnum.URL4184
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
     * Metodo ejecutado al oprimir el boton Generar en la vista
     */
    public void oprimirGenerar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try {
            String nombreArchivo = SysmanFunciones.concatenar(SessionUtil
                            .getCompaniaIngreso().getNombre().length() > 20
                                ? SessionUtil.getCompaniaIngreso().getNombre()
                                                .substring(0, 20)
                                : SessionUtil.getCompaniaIngreso().getNombre(),
                            "_REPORTE_RESERVAS_PRESUPUESTALES.txt");
            archivoDescarga = JsfUtil
                            .getArchivoDescarga(JsfUtil.serializarPlano(
                                            generarPlano(false)),
                                            nombreArchivo);
        }
        catch (NumberFormatException | JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String archivoPlano = generarPlano(true);
        String separadorRegistros = System.getProperty("line.separator");
        String separadorColumnas = "\t";
        String nombreHoja = "RESERVAS PRESUPUESTALES FUT";
        String nombreDocumento = "RESERVAS PRESUPUESTALES FUT";
        archivoDescarga = JsfUtil.armarExcel(archivoPlano, separadorRegistros,
                        separadorColumnas, nombreHoja, nombreDocumento);
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

        String sql = "";
        Map<String, Object> reemplazar = new TreeMap<>();

        reemplazar.put("compania", compania);
        reemplazar.put("anio", anio);

        sql = Reporteador.resuelveConsulta(
                        "800344RUBROS_NOCONFIGURADOS_FUT_RESERVA",
                        Integer.parseInt(SessionUtil.getModulo()), reemplazar);

        try {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
                            "RubrosNoConfiguradosFutReserva");
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Realiza el llamado a la funcion
     * <b>PCK_CHIPFUT1.FC_GENERARPLANORESERVASPPTALES</b> la cual
     * genera un CLOB con la informacion a plasmar en el informe
     * 
     * @param exportaExcel
     * Indica si el formato a generar es Excel
     * @return Cadena con la informacion a generar en el informe
     */
    private String generarPlano(boolean exportaExcel) {
        try {
            return ejbChipFutUno.generarPlanoReservasPptales(compania,
                            codEntidad,
                            Integer.parseInt(trimestre),
                            anio,
                            Integer.parseInt(actoAdmin),
                            Integer.parseInt(nroActoadmin),
                            fechaDoc,
                            pesos,
                            separadas, exportaExcel);
        }
        catch (NumberFormatException | SystemException e)

        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return null;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Verificacion
     * 
     */
    public void cambiarVerificacion() {
        // <CODIGO_DESARROLLADO>
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
     * Retorna la variable verificacion
     * 
     * @return verificacion
     */
    public boolean getVerificacion() {
        return verificacion;
    }

    /**
     * Asigna la variable verificacion
     * 
     * @param verificacion
     * Variable a asignar en verificacion
     */
    public void setVerificacion(boolean verificacion) {
        this.verificacion = verificacion;
    }

    /**
     * Retorna la variable separadas
     * 
     * @return separadas
     */
    public boolean getSeparadas() {
        return separadas;
    }

    /**
     * Asigna la variable separadas
     * 
     * @param separadas
     * Variable a asignar en separadas
     */
    public void setSeparadas(boolean separadas) {
        this.separadas = separadas;
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
     * Retorna la variable anio
     * 
     * @return anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable actoAdmin
     * 
     * @return actoAdmin
     */
    public String getActoAdmin() {
        return actoAdmin;
    }

    /**
     * Asigna la variable actoAdmin
     * 
     * @param actoAdmin
     * Variable a asignar en actoAdmin
     */
    public void setActoAdmin(String actoAdmin) {
        this.actoAdmin = actoAdmin;
    }

    /**
     * Retorna la variable codEntidad
     * 
     * @return codEntidad
     */
    public String getCodEntidad() {
        return codEntidad;
    }

    /**
     * Asigna la variable codEntidad
     * 
     * @param codEntidad
     * Variable a asignar en codEntidad
     */
    public void setCodEntidad(String codEntidad) {
        this.codEntidad = codEntidad;
    }

    /**
     * Retorna la variable nroActoadmin
     * 
     * @return nroActoadmin
     */
    public String getNroActoadmin() {
        return nroActoadmin;
    }

    /**
     * Asigna la variable nroActoadmin
     * 
     * @param nroActoadmin
     * Variable a asignar en nroActoadmin
     */
    public void setNroActoadmin(String nroActoadmin) {
        this.nroActoadmin = nroActoadmin;
    }

    /**
     * Retorna la variable fechaDoc
     * 
     * @return fechaDoc
     */
    public Date getFechaDoc() {
        return fechaDoc;
    }

    /**
     * Asigna la variable fechaDoc
     * 
     * @param fechaDoc
     * Variable a asignar en fechaDoc
     */
    public void setFechaDoc(Date fechaDoc) {
        this.fechaDoc = fechaDoc;
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
