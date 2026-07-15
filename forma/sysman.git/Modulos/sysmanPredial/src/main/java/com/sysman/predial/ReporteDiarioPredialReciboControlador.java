/*-
 * ReporteDiarioPredialReciboControlador.java
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.ReporteDiarioPredialReciboControladorEnum;
import com.sysman.predial.enums.ReporteDiarioPredialReciboControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que permite generar el Reporte diario de predial por recibo
 *
 * @version 1, 16/02/2017 12:57:05 -- Modificado por jrodriguezr
 * @author jrodriguezr
 *
 * @author spina
 * @version 2, 17/07/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class ReporteDiarioPredialReciboControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Valor del atributo correspondiente a anulados
     */
    private boolean anulados;
    /**
     * Valor del atributo correspondiente a bancoInicial
     */
    private String bancoInicial;
    /**
     * Valor del atributo correspondiente a bancoFinal
     */
    private String bancoFinal;
    /**
     * Valor del atributo correspondiente a paqueteInicial
     */
    private String paqueteInicial;
    /**
     * Valor del atributo correspondiente a paqueteFinal
     */
    private String paqueteFinal;
    /**
     * Valor del atributo correspondiente a fechaInicial
     */
    private Date fechaInicial;
    /**
     * Valor del atributo correspondiente a fechaFinal
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Lista de objetos pertenecientes al combo paquete Inicial
     */
    private List<Registro> listapaqueteInicial;
    /**
     * Lista de objetos pertenecientes al combo paquete Inicial
     */
    private List<Registro> listapaqueteFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de valores pertenecientes al combo grande banco Inicial
     */
    private RegistroDataModelImpl listabancoInicial;
    /**
     * Lista de valores pertenecientes al combo grande banco Final
     */
    private RegistroDataModelImpl listabancoFinal;
    private boolean anuladosVisible;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ReporteDiarioPredialReciboControlador
     */
    public ReporteDiarioPredialReciboControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.REPORTE_DIARIO_PREDIAL_RECIBO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {

        fechaInicial = fechaFinal = new Date();
        cargarListapaqueteInicial();
        cargarListapaqueteFinal();
        cargarListabancoInicial();
        cargarListabancoFinal();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        anuladosVisible = !"8912800003"
                        .equals(SessionUtil.getCompaniaIngreso().getNit());

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listapaqueteInicial
     *
     */
    public void cargarListapaqueteInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(), fechaInicial);
        param.put(GeneralParameterEnum.FECHAFINAL.getName(), fechaFinal);
        param.put(ReporteDiarioPredialReciboControladorEnum.BANCOINICIAL
                        .getValue(), bancoInicial);
        param.put(ReporteDiarioPredialReciboControladorEnum.BANCOFINAL
                        .getValue(), bancoFinal);

        try
        {
            listapaqueteInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteDiarioPredialReciboControladorUrlEnum.URL6580
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
     * Carga la lista listapaqueteFinal
     */
    public void cargarListapaqueteFinal()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(), fechaInicial);
        param.put(GeneralParameterEnum.FECHAFINAL.getName(), fechaFinal);
        param.put(ReporteDiarioPredialReciboControladorEnum.BANCOINICIAL
                        .getValue(), bancoInicial);
        param.put(ReporteDiarioPredialReciboControladorEnum.BANCOFINAL
                        .getValue(), bancoFinal);
        param.put(ReporteDiarioPredialReciboControladorEnum.PAQUETEINICIAL
                        .getValue(), paqueteInicial);

        try
        {
            listapaqueteFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteDiarioPredialReciboControladorUrlEnum.URL6581
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
     * Carga la lista listabancoInicial
     */
    public void cargarListabancoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReporteDiarioPredialReciboControladorUrlEnum.URL6582
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listabancoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        ReporteDiarioPredialReciboControladorEnum.CODIGOBANCO
                                        .getValue());
    }

    /**
     *
     * Carga la lista listabancoFinal
     *
     */
    public void cargarListabancoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReporteDiarioPredialReciboControladorUrlEnum.URL6583
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ReporteDiarioPredialReciboControladorEnum.BANCOINICIAL
                        .getValue(), bancoInicial);

        listabancoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        ReporteDiarioPredialReciboControladorEnum.CODIGOBANCO
                                        .getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Se genera el informe en Excel.
     *
     * @param formato
     * en Excel
     *
     */
    private void generaReporte(FORMATOS formato)
    {
        // <CODIGO_DESARROLLADO>

        try
        {

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("bancoInicial", bancoInicial);
            reemplazar.put("bancoFinal", bancoFinal);
            reemplazar.put("paqueteInicial", paqueteInicial);
            reemplazar.put("paqueteFinal", paqueteFinal);

            String reporte = !anuladosVisible
                ? "001406PREDIALDIARECAUPORRECIBOPTO"
                : anulados
                    ? "001407PREDIALDIARECAUPORRECIBOANULADOS"
                    : "001405PREDIALDIARECAUPORRECIBOPAQ";
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_ANULADO", anulados);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal));
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE") + " "
                                + ex.getMessage());
            Logger.getLogger(ReporteDiarioPredialReciboControlador.class
                            .getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | OutOfMemoryError | JRException | IOException
                        | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarfechaini()
    {
        // <CODIGO_DESARROLLADO>
        bancoInicial = bancoFinal = paqueteInicial = paqueteFinal = null;
        cargarListabancoInicial();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarpaqueteInicial()
    {
        // <CODIGO_DESARROLLADO>
        cargarListapaqueteFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarfechafin()
    {
        // <CODIGO_DESARROLLADO>
        bancoInicial = bancoFinal = paqueteInicial = paqueteFinal = null;
        cargarListabancoInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listabancoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilabancoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = registroAux.getCampos()
                        .get(ReporteDiarioPredialReciboControladorEnum.CODIGOBANCO
                                        .getValue())
                        .toString();
        bancoFinal = paqueteInicial = paqueteFinal = null;
        cargarListabancoFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listabancoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilabancoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = registroAux.getCampos()
                        .get(ReporteDiarioPredialReciboControladorEnum.CODIGOBANCO
                                        .getValue())
                        .toString();
        paqueteInicial = paqueteFinal = null;
        cargarListapaqueteInicial();
        cargarListapaqueteFinal();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable anulados
     *
     * @return anulados
     */
    public boolean getAnulados()
    {
        return anulados;
    }

    /**
     * Asigna la variable anulados
     *
     * @param anulados
     * Variable a asignar en anulados
     */
    public void setAnulados(boolean anulados)
    {
        this.anulados = anulados;
    }

    /**
     * Retorna la variable bancoInicial
     *
     * @return bancoInicial
     */
    public String getBancoInicial()
    {
        return bancoInicial;
    }

    /**
     * Asigna la variable bancoInicial
     *
     * @param bancoInicial
     * Variable a asignar en bancoInicial
     */
    public void setBancoInicial(String bancoInicial)
    {
        this.bancoInicial = bancoInicial;
    }

    /**
     * Retorna la variable bancoFinal
     *
     * @return bancoFinal
     */
    public String getBancoFinal()
    {
        return bancoFinal;
    }

    /**
     * Asigna la variable bancoFinal
     *
     * @param bancoFinal
     * Variable a asignar en bancoFinal
     */
    public void setBancoFinal(String bancoFinal)
    {
        this.bancoFinal = bancoFinal;
    }

    /**
     * Retorna la variable paqueteInicial
     *
     * @return paqueteInicial
     */
    public String getPaqueteInicial()
    {
        return paqueteInicial;
    }

    /**
     * Asigna la variable paqueteInicial
     *
     * @param paqueteInicial
     * Variable a asignar en paqueteInicial
     */
    public void setPaqueteInicial(String paqueteInicial)
    {
        this.paqueteInicial = paqueteInicial;
    }

    /**
     * Retorna la variable paqueteFinal
     *
     * @return paqueteFinal
     */
    public String getPaqueteFinal()
    {
        return paqueteFinal;
    }

    /**
     * Asigna la variable paqueteFinal
     *
     * @param paqueteFinal
     * Variable a asignar en paqueteFinal
     */
    public void setPaqueteFinal(String paqueteFinal)
    {
        this.paqueteFinal = paqueteFinal;
    }

    /**
     * Retorna la variable fechaInicial
     *
     * @return fechaInicial
     */
    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     *
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     *
     * @return fechaFinal
     */
    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     *
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public boolean isAnuladosVisible()
    {
        return anuladosVisible;
    }

    public void setAnuladosVisible(boolean anuladosVisible)
    {
        this.anuladosVisible = anuladosVisible;
    }

    /**
     * Retorna la lista listapaqueteInicial
     *
     * @return listapaqueteInicial
     */
    public List<Registro> getListapaqueteInicial()
    {
        return listapaqueteInicial;
    }

    /**
     * Asigna la lista listapaqueteInicial
     *
     * @param listapaqueteInicial
     * Variable a asignar en listapaqueteInicial
     */
    public void setListapaqueteInicial(List<Registro> listapaqueteInicial)
    {
        this.listapaqueteInicial = listapaqueteInicial;
    }

    /**
     * Retorna la lista listapaqueteFinal
     *
     * @return listapaqueteFinal
     */
    public List<Registro> getListapaqueteFinal()
    {
        return listapaqueteFinal;
    }

    /**
     * Asigna la lista listapaqueteFinal
     *
     * @param listapaqueteFinal
     * Variable a asignar en listapaqueteFinal
     */
    public void setListapaqueteFinal(List<Registro> listapaqueteFinal)
    {
        this.listapaqueteFinal = listapaqueteFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listabancoInicial
     *
     * @return listabancoInicial
     */
    public RegistroDataModelImpl getListabancoInicial()
    {
        return listabancoInicial;
    }

    /**
     * Asigna la lista listabancoInicial
     *
     * @param listabancoInicial
     * Variable a asignar en listabancoInicial
     */
    public void setListabancoInicial(RegistroDataModelImpl listabancoInicial)
    {
        this.listabancoInicial = listabancoInicial;
    }

    /**
     * Retorna la lista listabancoFinal
     *
     * @return listabancoFinal
     */
    public RegistroDataModelImpl getListabancoFinal()
    {
        return listabancoFinal;
    }

    /**
     * Asigna la lista listabancoFinal
     *
     * @param listabancoFinal
     * Variable a asignar en listabancoFinal
     */
    public void setListabancoFinal(RegistroDataModelImpl listabancoFinal)
    {
        this.listabancoFinal = listabancoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
