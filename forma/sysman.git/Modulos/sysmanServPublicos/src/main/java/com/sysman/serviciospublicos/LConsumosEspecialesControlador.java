/*-
 * LConsumosEspecialesControlador.java
 *
 * 1.0
 *
 * 12/10/2016
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LConsumosEspecialesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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
 * Controlador del formulario: Usuarios con Consumos Especiales.
 *
 * @version 1.0, 12/10/2016
 * @author Pablo Espitia Cuca
 * @version 2.0, 05/06/2017
 * @author spina - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class LConsumosEspecialesControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesión
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /** Contiene el ciclo seleccionado del combo. */
    private String ciclo;

    /** Contiene el valor de consumo ingresado. */
    private String consumo;

    /** Contiene el periodo del ciclo. */
    private String periodo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    /** Items del combo ciclo. */
    private RegistroDataModelImpl listaCiclo;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LConsumosEspecialesControlador
     */
    public LConsumosEspecialesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.L_CONSUMOS_ESPECIALES_CONTROLADOR
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
        cargarListaCiclo();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista del combo ciclo.
     */
    public void cargarListaCiclo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LConsumosEspecialesControladorUrlEnum.URL4633
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NUMERO");
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
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
    }

    /**
     * Genera un reporte.
     *
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato)
    {
        /* Verifica si el parametro esta en SI. */
        String reporte = null;
        try
        {
            boolean key = "SI".equals(
                            SysmanFunciones.nvlStr(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            "FORMATO CALIDAD",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            "NO"));

            reporte = key ? "001151LUsuarioConsumoEspCOS"
                : "001150LUsuarioConsumoEsp";
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            // <VARIABLES A REEMPLAZAR EN LA CONSULTA>
            reemplazar.put("ciclo", ciclo);
            reemplazar.put("consumo", consumo);
            // </VARIABLES A REEMPLAZAR EN LA CONSULTA>

            // <PARAMETROS A REEMPLZAR EN EL REPORTE>
            parametros.put("PR_CONSUMOS", consumo);
            parametros.put("PR_PERIODO", periodo);
            // </PARAMETROS A REEMPLZAR EN EL REPORTE>

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE") + " "
                                + ex.getMessage() + " " + reporte);
            Logger.getLogger(LConsumosEspecialesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException ex)
        {
            Logger.getLogger(LConsumosEspecialesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al seleccionar un item de la combo ciclo.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get("NUMERO").toString();
        periodo = registroAux.getCampos().get("NOMBREPERIODO").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
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
     * Retorna la variable consumo
     *
     * @return consumo
     */
    public String getConsumo()
    {
        return consumo;
    }

    /**
     * Asigna la variable consumo
     *
     * @param consumo
     * Variable a asignar en consumo
     */
    public void setConsumo(String consumo)
    {
        this.consumo = consumo;
    }

    /** Retorna el periodo */
    public String getPeriodo()
    {
        return periodo;
    }

    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     *
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo()
    {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }
}