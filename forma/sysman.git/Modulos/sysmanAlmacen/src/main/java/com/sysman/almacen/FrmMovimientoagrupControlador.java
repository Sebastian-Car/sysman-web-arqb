/*-
 * FrmMovimientoagrupControlador.java
 *
 * 1.0
 * 
 * 30/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.FrmMovimientoagrupControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 30/07/2018
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmMovimientoagrupControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Se almacena identificadores "General" y "Detallado" para
     * generar reportes
     */
    private int presentacion;
    /**
     * Variable que almacena la Agrupacion Inicial
     */
    private String agruparInicial;
    /**
     * Variable que almacena la Agrupacion Final
     */
    private String agruparFinal;
    /**
     * Variable que almacena la Fecha Inicial
     */
    private Date fechaInicial;
    /**
     * Variable que almacena la Fecha Final
     */
    private Date fechaFinal;
    /**
     * Variable que almacena el Nombre Agrupacion Inicial
     */
    private String nombreAgrupIni;
    /**
     * Variable que almacena el Nombre Agrupacion Final
     */
    private String nombreAgrupFin;
    /**
     * Variable que almacena el parametro DIGITOS AGRUPACION
     * INVENTARIO
     */
    private String digitosInventario;

    private String reporte;
    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete PCK_SYSMAN_UTL.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmMovimientoagrupControlador
     */
    public FrmMovimientoagrupControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        presentacion = 1;
        // agruparInicial = "4";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMMOVIMIENTOAGRUP_CONTROLADOR
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

        try {

            digitosInventario = ejbSysmanUtil.consultarParametro(compania,
                            "DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),
                            false);

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        cargarListacmbElementoDesde();

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
     * Carga la lista listacmbElementoDesde
     *
     */
    public void cargarListacmbElementoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmMovimientoagrupControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("DIGITOS", digitosInventario);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOELEMENTO");
    }

    /**
     * 
     * Carga la lista listacmbElementoHasta
     *
     */
    public void cargarListacmbElementoHasta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmMovimientoagrupControladorUrlEnum.URL0002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ELEMENTODESDE", agruparInicial);
        param.put("DIGITOS", digitosInventario);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOELEMENTO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdPantalla en la vista
     *
     *
     */
    public void oprimircmdPantalla() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
        generaInforme(FORMATOS.PDF);
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
        generaInforme(FORMATOS.EXCEL);
    }

    public void consultarReporte() {

        if (presentacion == 1) {
            reporte = "001838MovimientoAgrupaGen";

        }
        else if (presentacion == 2) {
            reporte = "001837MovimientoAgrupa";
        }

    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        try {

            consultarReporte();
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFechaCadena(fechaInicial,
                                            "DD/MM/YYYY"));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFechaCadena(fechaFinal,
                                            "DD/MM/YYYY"));
            reemplazar.put("agruparInicial", agruparInicial);
            reemplazar.put("agruparFinal", agruparFinal);
            reemplazar.put("digitos", digitosInventario);
            reemplazar.put("compania", compania);

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            parametros.put("PR_FORMS_MOVIMIENTOAGRUP_DESDE",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_MOVIMIENTOAGRUP_HASTA",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbElementoDesde que carga el combo
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        agruparInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOELEMENTO"), "")
                        .toString();

        nombreAgrupIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
        agruparFinal = null;
        nombreAgrupFin = null;
        cargarListacmbElementoHasta();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbElementoHasta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        agruparFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOELEMENTO"), "")
                        .toString();

        nombreAgrupFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable presentacion
     * 
     * @return presentacion
     */
    public int getPresentacion() {
        return presentacion;
    }

    /**
     * Asigna la variable presentacion
     * 
     * @param presentacion
     * Variable a asignar en presentacion
     */
    public void setPresentacion(int presentacion) {
        this.presentacion = presentacion;
    }

    /**
     * Retorna la variable agruparInicial
     * 
     * @return agruparInicial
     */
    public String getAgruparInicial() {
        return agruparInicial;
    }

    /**
     * Asigna la variable agruparInicial
     * 
     * @param agruparInicial
     * Variable a asignar en agruparInicial
     */
    public void setAgruparInicial(String agruparInicial) {
        this.agruparInicial = agruparInicial;
    }

    /**
     * Retorna la variable agruparFinal
     * 
     * @return agruparFinal
     */
    public String getAgruparFinal() {
        return agruparFinal;
    }

    /**
     * Asigna la variable agruparFinal
     * 
     * @param agruparFinal
     * Variable a asignar en agruparFinal
     */
    public void setAgruparFinal(String agruparFinal) {
        this.agruparFinal = agruparFinal;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable nombreAgrupIni
     * 
     * @return nombreAgrupIni
     */
    public String getNombreAgrupIni() {
        return nombreAgrupIni;
    }

    /**
     * Asigna la variable nombreAgrupIni
     * 
     * @param nombreAgrupIni
     * Variable a asignar en nombreAgrupIni
     */
    public void setNombreAgrupIni(String nombreAgrupIni) {
        this.nombreAgrupIni = nombreAgrupIni;
    }

    /**
     * Retorna la variable nombreAgrupFin
     * 
     * @return nombreAgrupFin
     */
    public String getNombreAgrupFin() {
        return nombreAgrupFin;
    }

    /**
     * Asigna la variable nombreAgrupFin
     * 
     * @param nombreAgrupFin
     * Variable a asignar en nombreAgrupFin
     */
    public void setNombreAgrupFin(String nombreAgrupFin) {
        this.nombreAgrupFin = nombreAgrupFin;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbElementoDesde
     * 
     * @return listacmbElementoDesde
     */
    public RegistroDataModelImpl getListacmbElementoDesde() {
        return listacmbElementoDesde;
    }

    /**
     * Asigna la lista listacmbElementoDesde
     * 
     * @param listacmbElementoDesde
     * Variable a asignar en listacmbElementoDesde
     */
    public void setListacmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listacmbElementoDesde = listacmbElementoDesde;
    }

    /**
     * Retorna la lista listacmbElementoHasta
     * 
     * @return listacmbElementoHasta
     */
    public RegistroDataModelImpl getListacmbElementoHasta() {
        return listacmbElementoHasta;
    }

    /**
     * Asigna la lista listacmbElementoHasta
     * 
     * @param listacmbElementoHasta
     * Variable a asignar en listacmbElementoHasta
     */
    public void setListacmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listacmbElementoHasta = listacmbElementoHasta;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * @return the digitosInventario
     */
    public String getDigitosInventario() {
        return digitosInventario;
    }

    /**
     * @param digitosInventario
     * the digitosInventario to set
     */
    public void setDigitosInventario(String digitosInventario) {
        this.digitosInventario = digitosInventario;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
