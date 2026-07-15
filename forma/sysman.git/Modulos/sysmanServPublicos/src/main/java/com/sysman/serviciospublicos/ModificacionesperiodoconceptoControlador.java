/*-
 * ModificacionesperiodoconceptoControlador.java
 *
 * 1.0
 *
 * 27/10/2016
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
import com.sysman.serviciospublicos.enums.ModificacionesperiodoconceptoControladorEnum;
import com.sysman.serviciospublicos.enums.ModificacionesperiodoconceptoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario modificacionesperiodoconcepto para
 * generar informes de modificaciones por concepto
 *
 * @version 1.0, 27/10/2016
 * @author NGOMEZ
 * 
 * @author eamaya
 * @version 2.0, 09/06/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 * 
 */
@ManagedBean
@ViewScoped

public class ModificacionesperiodoconceptoControlador
                extends BeanBaseModal
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
     * Variable que representa el valor del check soloResumen
     */
    private boolean soloResumen;
    /**
     * Variable que representa el valor del check soloResumen
     */
    private boolean porUsuario;
    /**
     * Variable que representa el ciclo para generar el informe
     */
    private String ciclo;
    /**
     * Variable que representa el anio inicial para generar el informe
     */
    private String anioInicial;
    /**
     * Variable que representa el periodo inicial para generar el
     * informe
     */
    private String periodoInicial;
    /**
     * Variable que representa el anio final para generar el informe
     */
    private String anioFinal;
    /**
     * Variable que representa el periodo final para generar el
     * informe
     */
    private String periodoFinal;
    /**
     * Variable que representa el codigo inicial para generar el
     * informe
     */
    private String codigoInicial;
    /**
     * Variable que representa el codigo final para generar el informe
     */
    private String codigoFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista del combo ciclo
     */
    private List<Registro> listaCiclo;
    /**
     * Lista del combo AnoInicial
     */
    private List<Registro> listaAnoInicial;
    /**
     * Lista del combo PeriodoInicial
     */
    private List<Registro> listaPeriodoInicial;
    /**
     * Lista del combo AnoFinal
     */
    private List<Registro> listaAnoFinal;
    /**
     * Lista del combo PeriodoFinal
     */
    private List<Registro> listaPeriodoFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * RegistroDataModel para el combo grande CodigoInicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * RegistroDataModel para el combo grande CodigoFinal
     */
    private RegistroDataModelImpl listaCodigoFinal;

    /**
     * Variable que identifica si los campos para seleccionar el rango
     * de usuario estan visibles o no
     */
    private boolean usuariosVisible;

    /**
     * Variable que especifica cuando mostrar el dialogo para ingresar
     * las observaciones
     */
    private boolean observacionesCuadroVisible;

    /**
     * Variable que especifica el campo del dialogo para las
     * observaciones que se van a mostrar en el reporte
     */
    private String observaciones;

    /**
     * Variable que identifica el formato en que se va a generar el
     * reporte
     */
    private FORMATOS formato;
    /**
     * Constante que representa el campo CODIGORUTA
     */
    private final String codigoRutaCons;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de
     * ModificacionesperiodoconceptoControlador
     */
    public ModificacionesperiodoconceptoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        ciclo = "T";
        codigoRutaCons = GeneralParameterEnum.CODIGORUTA.getName();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.MODIFICACIONESPERIODOCONCEPTO_CONTROLADOR
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

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ModificacionesperiodoconceptoControladorUrlEnum.URL6191
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
     * Carga la lista listaAnoInicial
     *
     */
    public void cargarListaAnoInicial()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ModificacionesperiodoconceptoControladorUrlEnum.URL6806
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
     * Carga la lista listaPeriodoInicial
     *
     */
    public void cargarListaPeriodoInicial()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ModificacionesperiodoconceptoControladorUrlEnum.URL7278
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
     * Carga la lista listaAnoFinal
     *
     */
    public void cargarListaAnoFinal()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.ANO.getName(),
                            String.valueOf(anioFinal));

            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ModificacionesperiodoconceptoControladorUrlEnum.URL7842
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
     * Carga la lista listaPeriodoFinal
     *
     */
    public void cargarListaPeriodoFinal()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.ANO.getName(), anioFinal);

            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ModificacionesperiodoconceptoControladorUrlEnum.URL8408
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
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ModificacionesperiodoconceptoControladorUrlEnum.URL8937
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);
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
                                        ModificacionesperiodoconceptoControladorUrlEnum.URL9666
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(ModificacionesperiodoconceptoControladorEnum.PARAM0
                        .getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control ciclo
     *
     *
     */
    public void cambiarCiclo()
    {
        // <CODIGO_DESARROLLADO>
        codigoInicial = null;
        codigoFinal = null;
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoInicial
     *
     *
     */
    public void cambiarAnoInicial()
    {
        // <CODIGO_DESARROLLADO>
        periodoInicial = null;
        periodoFinal = null;
        anioFinal = anioInicial;
        cargarListaAnoFinal();
        cargarListaPeriodoInicial();
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoFinal
     *
     *
     */
    public void cambiarAnoFinal()
    {
        // <CODIGO_DESARROLLADO>
        periodoFinal = null;
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PeriodoInicial
     *
     *
     */
    public void cambiarPeriodoInicial()
    {
        // <CODIGO_DESARROLLADO>
        periodoFinal = periodoInicial;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PorUsuario
     *
     *
     */
    public void cambiarPorUsuario()
    {
        // <CODIGO_DESARROLLADO>
        usuariosVisible = porUsuario;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
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
        codigoInicial = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(codigoRutaCons).toString(),
                        "");
        codigoFinal = null;
        cargarListaCodigoFinal();
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
        codigoFinal = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(codigoRutaCons).toString(),
                        "");
    }
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>

    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        formato = FORMATOS.PDF;
        determinarTipoReporte();
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        formato = FORMATOS.EXCEL;
        determinarTipoReporte();
        // </CODIGO_DESARROLLADO>
    }

    public void determinarTipoReporte()
    {
        archivoDescarga = null;
        String reporte = "001182LModificacionesPeriodoResumen";

        if (soloResumen)
        {
            genInforme(reporte, reporte);
        }
        else
        {
            try
            {
                if ("SI".equals(SysmanFunciones.nvl(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "FORMATO PERSONALIZADO EN INFORME MODIFICACIONES",
                                                SessionUtil.getModulo(),
                                                new Date(), false),
                                "NO")))
                {
                    observaciones = null;
                    observacionesCuadroVisible = true;
                }
                else
                {
                    genInforme("001181LModificacionesPeriodoConcepto", reporte);
                }
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        porUsuario = true;
        usuariosVisible = true;

        // </CODIGO_DESARROLLADO>
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
        // <CARGAR_LISTA>
        ciclo = "T";
        anioInicial = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        anioFinal = String.valueOf(SysmanFunciones
                        .ano(new Date()));

        periodoInicial = String.valueOf(SysmanFunciones
                        .mes(new Date()));

        periodoFinal = String.valueOf(SysmanFunciones
                        .mes(new Date()));
        cargarListaCiclo();
        cargarListaAnoInicial();
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        cargarListaPeriodoFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        cargarListaCodigoInicial();
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable soloResumen
     *
     * @return soloResumen
     */
    public boolean isSoloResumen()
    {
        return soloResumen;
    }

    /**
     * Asigna la variable soloResumen
     *
     * @param soloResumen
     * Variable a asignar en soloResumen
     */
    public void setSoloResumen(boolean soloResumen)
    {
        this.soloResumen = soloResumen;
    }

    /**
     * Retorna la variable porUsuario
     *
     * @return porUsuario
     */
    public boolean isPorUsuario()
    {
        return porUsuario;
    }

    /**
     * Asigna la variable porUsuario
     *
     * @param porUsuario
     * Variable a asignar en porUsuario
     */
    public void setPorUsuario(boolean porUsuario)
    {
        this.porUsuario = porUsuario;
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
     * Retorna la variable anioInicial
     *
     * @return anioInicial
     */
    public String getAnioInicial()
    {
        return anioInicial;
    }

    /**
     * Asigna la variable anioInicial
     *
     * @param anioInicial
     * Variable a asignar en anioInicial
     */
    public void setAnioInicial(String anioInicial)
    {
        this.anioInicial = anioInicial;
    }

    /**
     * Retorna la variable periodoInicial
     *
     * @return periodoInicial
     */
    public String getPeriodoInicial()
    {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     *
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial)
    {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable anioFinal
     *
     * @return anioFinal
     */
    public String getAnioFinal()
    {
        return anioFinal;
    }

    /**
     * Asigna la variable anioFinal
     *
     * @param anioFinal
     * Variable a asignar en anioFinal
     */
    public void setAnioFinal(String anioFinal)
    {
        this.anioFinal = anioFinal;
    }

    /**
     * Retorna la variable periodoFinal
     *
     * @return periodoFinal
     */
    public String getPeriodoFinal()
    {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     *
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal)
    {
        this.periodoFinal = periodoFinal;
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
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
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
     * Retorna la lista listaAnoInicial
     *
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial()
    {
        return listaAnoInicial;
    }

    /**
     * Asigna la lista listaAnoInicial
     *
     * @param listaAnoInicial
     * Variable a asignar en listaAnoInicial
     */
    public void setListaAnoInicial(List<Registro> listaAnoInicial)
    {
        this.listaAnoInicial = listaAnoInicial;
    }

    /**
     * Retorna la lista listaPeriodoInicial
     *
     * @return listaPeriodoInicial
     */
    public List<Registro> getListaPeriodoInicial()
    {
        return listaPeriodoInicial;
    }

    /**
     * Asigna la lista listaPeriodoInicial
     *
     * @param listaPeriodoInicial
     * Variable a asignar en listaPeriodoInicial
     */
    public void setListaPeriodoInicial(List<Registro> listaPeriodoInicial)
    {
        this.listaPeriodoInicial = listaPeriodoInicial;
    }

    /**
     * Retorna la lista listaAnoFinal
     *
     * @return listaAnoFinal
     */
    public List<Registro> getListaAnoFinal()
    {
        return listaAnoFinal;
    }

    /**
     * Asigna la lista listaAnoFinal
     *
     * @param listaAnoFinal
     * Variable a asignar en listaAnoFinal
     */
    public void setListaAnoFinal(List<Registro> listaAnoFinal)
    {
        this.listaAnoFinal = listaAnoFinal;
    }

    /**
     * Retorna la lista listaPeriodoFinal
     *
     * @return listaPeriodoFinal
     */
    public List<Registro> getListaPeriodoFinal()
    {
        return listaPeriodoFinal;
    }

    /**
     * Asigna la lista listaPeriodoFinal
     *
     * @param listaPeriodoFinal
     * Variable a asignar en listaPeriodoFinal
     */
    public void setListaPeriodoFinal(List<Registro> listaPeriodoFinal)
    {
        this.listaPeriodoFinal = listaPeriodoFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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

    public boolean isUsuariosVisible()
    {
        return usuariosVisible;
    }

    public void setUsuariosVisible(boolean usuariosVisible)
    {
        this.usuariosVisible = usuariosVisible;
    }

    public boolean isObservacionesCuadroVisible()
    {
        return observacionesCuadroVisible;
    }

    public void setObservacionesCuadroVisible(
        boolean observacionesCuadroVisible)
    {
        this.observacionesCuadroVisible = observacionesCuadroVisible;
    }

    public String getObservaciones()
    {
        return observaciones;
    }

    public void setObservaciones(String observaciones)
    {
        this.observaciones = observaciones;
    }

    public FORMATOS getFormato()
    {
        return formato;
    }

    public void setFormato(FORMATOS formato)
    {
        this.formato = formato;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    /**
     * Proceso en que se genera el reporte
     *
     * @param Reporte
     * Reporte que se debe descargar
     * @param Identificador
     * de consulta a resolver
     *
     */
    public void genInforme(String reporte, String reporteConsulta)
    {
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", "T".equals(ciclo) ? ""
                : " AND SP_USUARIO.CICLO=" + ciclo + " ");
            reemplazar.put("periodoInicial", anioInicial + periodoInicial);
            reemplazar.put("periodoFinal", anioFinal + periodoFinal);
            if (!soloResumen && porUsuario)
            {
                reemplazar.put("codigoRuta",
                                " AND SP_USUARIO.CODIGORUTA BETWEEN '"
                                    + codigoInicial + "' AND '" + codigoFinal
                                    + "'");
            }
            else
            {
                reemplazar.put("codigoRuta", "");
            }
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_VIGILADOPOR", SessionUtil.getCompaniaIngreso()
                            .getRutaVigiladoPor());
            parametros.put("PR_FORMS_MODIFICACIONESPERIODOCONCEPTO_CICLO",
                            "T".equals(ciclo) ? "Todos" : ciclo);
            parametros.put("PR_SIGLACOMPANIA",
                            SessionUtil.getCompaniaIngreso().getSigla());
            parametros.put("PR_CUENTANOMBRE",
                            SysmanFunciones.nvlStr(
                                            SessionUtil.getUser().getNombre1(),
                                            "")
                                + SysmanFunciones.nvlStr(
                                                SessionUtil.getUser()
                                                                .getNombre2(),
                                                "")
                                + SysmanFunciones.nvlStr(
                                                SessionUtil.getUser()
                                                                .getApellido1(),
                                                "")
                                + SysmanFunciones.nvlStr(
                                                SessionUtil.getUser()
                                                                .getApellido2(),
                                                ""));
            parametros.put("PR_CUENTACARGO",
                            SessionUtil.getUser().getTituloProfesional());
            parametros.put("PR_NOMBRE_DEL_JEFE_DE_COMERCIAL",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "NOMBRE DEL JEFE DE COMERCIAL",
                                                            SessionUtil.getModulo(),
                                                            new Date(), false)

            ,
                                            ""));
            parametros.put("PR_CARGO_DIRECTOR_COMERCIAL",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "CARGO DIRECTOR COMERCIAL",
                                                            SessionUtil.getModulo(),
                                                            new Date(), false),
                                            ""));
            parametros.put("PR_NOMBRE_FUNCIONARIO_PQR",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "NOMBRE FUNCIONARIO PQR",
                                                            SessionUtil.getModulo(),
                                                            new Date(), false),
                                            ""));
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_OBSERVACIONES",
                            SysmanFunciones.nvl(observaciones, ""));
            Reporteador.resuelveConsulta(reporteConsulta,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        catch (JRException | IOException | SysmanException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * observacionesCuadro en la vista
     *
     *
     */
    public void aceptarobservacionesCuadro()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme("001180INFMODIFICACIONESPERIODO",
                        "001180INFMODIFICACIONESPERIODO");
        observacionesCuadroVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * observacionesCuadro en la vista
     *
     *
     */
    public void cancelarobservacionesCuadro()
    {
        // <CODIGO_DESARROLLADO>
        observacionesCuadroVisible = false;
        // </CODIGO_DESARROLLADO>
    }

}
