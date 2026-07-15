/*-
 * AcumFinanciablesControlador.java
 *
 * 1.0
 * 
 * 21/10/2016
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
import com.sysman.serviciospublicos.enums.AcumFinanciablesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
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
 * Controlador del formulario acumfinanciables.
 *
 * @version 1.0, 21/10/2016
 * @author Pablo A. Espitia Cuca.
 * 
 * @author eamaya
 * @version 2, 15/05/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 */
@ManagedBean
@ViewScoped

public class AcumFinanciablesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /** Contiene el item seleccionado del combo ciclo. */
    private String ciclo;
    /** Contiene el item seleccionado del combo ANO INICIAL. */
    private String anioInicial;
    /** Contiene el item seleccionado del combo PERIODO INICIAL. */
    private String periodoInicial;
    /** Contiene el item seleccionado del combo ANO FINAL. */
    private String anioFinal;
    /** Contiene el item seleccionado del combo PERIODO FINAL. */
    private String periodoFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga; 
    /** Contiene la lista de items del combo ANO INICIAL. */
    private List<Registro> listaAnoInicial;
    /** Contiene la lista de items del combo PERIODO INICIAL. */
    private List<Registro> listaPeriodoInicial;
    /** Contiene la lista de items del combo ANO FINAL. */
    private List<Registro> listaAnoFinal;
    /** Contiene la lista de items del combo PERIODO FINAL. */
    private List<Registro> listaPeriodoFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Contiene la lista de items del combo CICLO. */
    private RegistroDataModelImpl listaCiclo;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;
    /**
     * Crea una nueva instancia de AcumFinanciablesControlador
     */
    public AcumFinanciablesControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            anioFinal = SysmanFunciones.convertirAFechaCadena(new Date(),
                            "YYYY");
            numFormulario = GeneralCodigoFormaEnum.ACUM_FINANCIABLES_CONTROLADOR.getCodigo();
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
        cargarListaAnoInicial();
        cargarListaAnoFinal();
        cargarListaPeriodoFinal();
        cargarListaCiclo();
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
        /*
         * FR1146-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 74, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /** Carga la lista de items del combo Anio Inicial. */
    public void cargarListaAnoInicial() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumFinanciablesControladorUrlEnum.URL5272
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Carga la lista de items del combo periodo inicial. */
    public void cargarListaPeriodoInicial() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.ANO.getName(),
                            anioInicial);
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumFinanciablesControladorUrlEnum.URL5673
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /** Carga la lista de items del combo anio final. */
    public void cargarListaAnoFinal() {
        listaAnoFinal = listaAnoInicial;

    }

    /** Carga la lista de items del combo periodo final. */
    public void cargarListaPeriodoFinal() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.ANO.getName(),
                            anioFinal);
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumFinanciablesControladorUrlEnum.URL6478
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Carga la lista de items del combo ciclo. */
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AcumFinanciablesControladorUrlEnum.URL6877
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Gestiona el evento de presionar el boton EXCEL en el
     * formulario.
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Gestiona el evento de presionar el boton PDF en el formulario.
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {
    
        if (Integer.parseInt(anioInicial) > Integer.parseInt(anioFinal)) {
            anioFinal = "";
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1771"));

            return;
        }
        String reporte = "001157FinanciablesAcum";
        try {
 

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            /*
             * Verifica el valor del parametro MANEJA TOTAL
             * FINANCIABLE EN LA INTERFAZ SP
             */
            boolean key;

            key = "SI".equals(SysmanFunciones.nvlStr(ejbParametro
                            .consultarParametro(compania,
                                            "MANEJA TOTAL FINANCIABLE EN LA INTERFAZ SP",
                                            SessionUtil.getModulo(),
                                            new Date(), false),
                            "NO"));   

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("ciclo", ciclo);
            reemplazar.put("anioInicial", anioInicial);
            reemplazar.put("periodoInicial", periodoInicial);
            reemplazar.put("anioFinal", anioFinal);
            reemplazar.put("periodoFinal", periodoFinal);
            /*
             * Mostrar solo los usuarios con acumulado financiables,
             * si key es TRUE.
             */
            reemplazar.put("acumfin", key ? "AND C.ACUMFIN NOT IN(0)" : " ");
            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(AcumFinanciablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        } 
        catch (JRException | IOException
                        | SystemException ex) {
            Logger.getLogger(AcumFinanciablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException e) {        
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }       
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /** Gestiona los eventos del combo ANO INICIAL. */
    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        if (Integer.parseInt(anioInicial) > Integer.parseInt(anioFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1771"));
            return;
        }
        /* Cargue la lista del combo PERIODO FINAL. */
        cargarListaPeriodoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /** Gestiona los eventos del combo ANO FINAL. */
    public void cambiarAnoFinal() {
        // <CODIGO_DESARROLLADO>
        /* Cargue las lista del combo PERIODO FINAL. */
        if (Integer.parseInt(anioInicial) > Integer.parseInt(anioFinal)) {
            anioFinal = "";
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1771"));

            return;
        }

        cargarListaPeriodoFinal();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista del combo.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = SysmanFunciones.nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable anioInicial
     * 
     * @return anioInicial
     */
    public String getAnioInicial() {
        return anioInicial;
    }

    /**
     * Asigna la variable anioInicial
     * 
     * @param anioInicial
     * Variable a asignar en anioInicial
     */
    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    /**
     * Retorna la variable periodoInicial
     * 
     * @return periodoInicial
     */
    public String getPeriodoInicial() {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     * 
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable anioFinal
     * 
     * @return anioFinal
     */
    public String getAnioFinal() {
        return anioFinal;
    }

    /**
     * Asigna la variable anioFinal
     * 
     * @param anioFinal
     * Variable a asignar en anioFinal
     */
    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
    }

    /**
     * Retorna la variable periodoFinal
     * 
     * @return periodoFinal
     */
    public String getPeriodoFinal() {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     * 
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    /**
     * Retorna la lista listaAnoInicial
     * 
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    /**
     * Asigna la lista listaAnoInicial
     * 
     * @param listaAnoInicial
     * Variable a asignar en listaAnoInicial
     */
    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
    }

    /**
     * Retorna la lista listaPeriodoInicial
     * 
     * @return listaPeriodoInicial
     */
    public List<Registro> getListaPeriodoInicial() {
        return listaPeriodoInicial;
    }

    /**
     * Asigna la lista listaPeriodoInicial
     * 
     * @param listaPeriodoInicial
     * Variable a asignar en listaPeriodoInicial
     */
    public void setListaPeriodoInicial(List<Registro> listaPeriodoInicial) {
        this.listaPeriodoInicial = listaPeriodoInicial;
    }

    /**
     * Retorna la lista listaAnoFinal
     * 
     * @return listaAnoFinal
     */
    public List<Registro> getListaAnoFinal() {
        return listaAnoFinal;
    }

    /**
     * Asigna la lista listaAnoFinal
     * 
     * @param listaAnoFinal
     * Variable a asignar en listaAnoFinal
     */
    public void setListaAnoFinal(List<Registro> listaAnoFinal) {
        this.listaAnoFinal = listaAnoFinal;
    }

    /**
     * Retorna la lista listaPeriodoFinal
     * 
     * @return listaPeriodoFinal
     */
    public List<Registro> getListaPeriodoFinal() {
        return listaPeriodoFinal;
    }

    /**
     * Asigna la lista listaPeriodoFinal
     * 
     * @param listaPeriodoFinal
     * Variable a asignar en listaPeriodoFinal
     */
    public void setListaPeriodoFinal(List<Registro> listaPeriodoFinal) {
        this.listaPeriodoFinal = listaPeriodoFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
