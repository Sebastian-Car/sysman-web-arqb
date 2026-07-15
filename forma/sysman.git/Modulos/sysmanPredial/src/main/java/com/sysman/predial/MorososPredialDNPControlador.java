/*-
 * MorososPredialDNPControlador.java
 *
 * 1.0
 * 
 * 14/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.predial.enums.MorososPredialDNPControladorEnum;
import com.sysman.predial.enums.MorososPredialDNPControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma MorososPredialDNP asociado al formulario
 * "Informe de Cartera Morosa"
 *
 * @version 1.0, 14/02/2017
 * @author yrojas
 * 
 * @author ybecerra
 * @version 2, 10/07/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class MorososPredialDNPControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente.
     */
    private final String compania;

    /**
     * Constante que almacena el valor del mensaje
     * MSM_TRANS_INTERRUMPIDA.
     */
    private final String msgTransInterrumpidaCons;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo asociado al check del formulario y que es usado para
     * determinar la forma en la que se genera el informe.
     */
    private boolean vigenciasAcuerdos;

    /**
     * Atributo asociado al valor del anio inicial que se va a manejar
     * en la consulta del combo del reporte.
     */
    private String anioInicial;

    /**
     * Atributo asociado al valor del anio final que se va a manejar
     * en la consulta del combo del reporte.
     */
    private String anioFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>

    /**
     * Atributo asociado al parametro "TITULO UNO OFICIOS" y que es
     * usado en el encabezado de los reportes.
     */
    private String parTituloUnoOficios;

    /**
     * Atributo asociado al parametro "TITULO DOS OFICIOS" y que es
     * usado en el encabezado de los reportes.
     */
    private String parTituloDosOficios;

    /**
     * Atributo asociado al parametro "TITULO TRES OFICIOS" y que es
     * usado en el encabezado de los reportes.
     */
    private String parTituloTresOficios;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que contiene la informacion de los detalles del combo
     * anio inicial.
     */
    private List<Registro> listaAnioInicial;

    /**
     * Lista que contiene la informacion de los detalles del combo
     * anio final.
     */
    private List<Registro> listaAnioFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    @EJB
    private EjbPredialCuatroRemote ejbPredialCuatro;

    /**
     * Crea una nueva instancia de MorososPredialDNPControlador
     */
    public MorososPredialDNPControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        msgTransInterrumpidaCons = MorososPredialDNPControladorEnum.PARAM0
                        .getValue();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.MOROSOS_PREDIAL_DNPCONTROLADOR
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
        // <CARGAR_LISTA>
        cargarListaAnioInicial();

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
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            parTituloUnoOficios = SysmanFunciones
                            .nvlStr(ejbSysmanUtl.consultarParametro(compania,
                                            "TITULO UNO OFICIOS",
                                            SessionUtil.getModulo(),
                                            new Date(), true), "");

            parTituloDosOficios = SysmanFunciones
                            .nvlStr(ejbSysmanUtl.consultarParametro(compania,
                                            "TITULO DOS OFICIOS",
                                            SessionUtil.getModulo(),
                                            new Date(), true), "");

            parTituloTresOficios = SysmanFunciones
                            .nvlStr(ejbSysmanUtl.consultarParametro(compania,
                                            "TITULO TRES OFICIOS",
                                            SessionUtil.getModulo(),
                                            new Date(), true), "");

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAnioInicial
     */
    public void cargarListaAnioInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAnioInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MorososPredialDNPControladorUrlEnum.URL8370
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Carga la lista listaAnioFinal
     */
    public void cargarListaAnioFinal()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        try
        {
            listaAnioFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MorososPredialDNPControladorUrlEnum.URL8745
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     */
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que ejecuta la funcion que arma la consulta que sera
     * enviada al reporte.
     * 
     * @param parametrosFuncion
     * Cadena con los parametros que seran enviados a la funcion de
     * acuerdo a el valor del check.
     * @return consulta Cadena con la consulta armada.
     */
    public String ejecutarFuncion()
    {
        String consulta = " ";

        try
        {
            consulta = ejbPredialCuatro.armarConsultaMorososPredial(compania,
                            Integer.parseInt(anioInicial),
                            Integer.parseInt(anioFinal), vigenciasAcuerdos);
        }
        catch (SystemException | NumberFormatException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            consulta = null;
            return consulta;
        }

        return consulta;
    }

    /**
     * Metodo que genera reporte de acuerdo a un formato recibido.
     * 
     * @param formato
     * Parametro que determina la extension y formato del reporte que
     * se va a generar
     */
    public void generarReporte(FORMATOS formato)
    {
        String consulta;
        Map<String, Object> parametros = new HashMap<>();
        try
        {
            consulta = ejecutarFuncion();

            if (SysmanFunciones.validarVariableVacio(consulta))
            {
                return;
            }

            parametros.put("PR_UNO_OFICIOS", parTituloUnoOficios);
            parametros.put("PR_DOS_OFICIOS", parTituloDosOficios);
            parametros.put("PR_TRES_OFICIOS", parTituloTresOficios);
            parametros.put("PR_ANOINI", anioInicial);
            parametros.put("PR_ANOFIN", anioFinal);
            parametros.put("PR_NIT", SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_STRSQL", consulta);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001404INFMOROSOSPREDIALDNP", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(msgTransInterrumpidaCons), " ",
                            e.getMessage()));
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnioInicial
     * 
     */
    public void cambiarAnioInicial()
    {
        cargarListaAnioFinal();
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Asigna la variable del boolean vigenciasAcuerdos
     * 
     * @return vigenciasAcuerdos
     */
    public boolean isVigenciasAcuerdos()
    {
        return vigenciasAcuerdos;
    }

    /**
     * Asigna la variable booleana vigenciasAcuerdos
     * 
     * @param vigenciasAcuerdos
     * Variable a asignar en vigenciasAcuerdos
     */
    public void setVigenciasAcuerdos(boolean vigenciasAcuerdos)
    {
        this.vigenciasAcuerdos = vigenciasAcuerdos;
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
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnioInicial
     * 
     * @return listaAnioInicial
     */
    public List<Registro> getListaAnioInicial()
    {
        return listaAnioInicial;
    }

    /**
     * Asigna la lista listaAnioInicial
     * 
     * @param listaAnioInicial
     * Variable a asignar en listaAnioInicial
     */
    public void setListaAnioInicial(List<Registro> listaAnioInicial)
    {
        this.listaAnioInicial = listaAnioInicial;
    }

    /**
     * Retorna la lista listaAnioFinal
     * 
     * @return listaAnioFinal
     */
    public List<Registro> getListaAnioFinal()
    {
        return listaAnioFinal;
    }

    /**
     * Asigna la lista listaAnioFinal
     * 
     * @param listaAnioFinal
     * Variable a asignar en listaAnioFinal
     */
    public void setListaAnioFinal(List<Registro> listaAnioFinal)
    {
        this.listaAnioFinal = listaAnioFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
