/*-
 * CicloserviciosnofacturadosControlador.java
 *
 * 1.0
 *
 * 29/09/2016
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.CicloserviciosnofacturadosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite generar el reporte "Servicios facturados y en reclamacion"
 *
 * @version 1.0, 29/09/2016
 * @author jlozano
 *
 * Modificado por lcortes 17/05/2017. Refactorizacion de codigo de las lista para utilizar dss.
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class CicloserviciosnofacturadosControlador extends BeanBaseModal
{
    /**
     * Controlador que permite generar el reporte "Servicios facturados y en reclamacion" para un ciclo seleccionado
     */

    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Ciclo para el que se genera el reporte
     */
    private String ciclo;
    /**
     * Archivo generado para el reporte
     */
    private StreamedContent archivoDescarga;

    private String fecha;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de ciclos disponibles para seleccionar
     */
    private RegistroDataModelImpl listaCiclo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Creates a new instance of CicloserviciosnofacturadosControlador
     */
    public CicloserviciosnofacturadosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CICLOSERVICIOSNOFACTURADOS_CONTROLADOR.getCodigo();
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

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCiclo()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CicloserviciosnofacturadosControladorUrlEnum.URL3558
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * Metodo ue se ejecuta al presionar el boton Aceptar. Verifica que el ciclo no sea nulo y que tenga fecha de preparacion
     */
    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        if ("".equals(SysmanFunciones.nvl(ciclo, "")))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1680"));
            return;
        }

        if ("null".equals(SysmanFunciones.nvl(fecha, "null")))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1681"));
            return;
        }

        archivoDescarga = null;
        generarReporte("001113rptServiciosNoReclamados",
                        ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que genera el reporte
     *
     * @param nombreInforme
     * Nombre del reporte que se va a generar
     * @param formato
     * Formato en el que se va a generar el reporte
     */
    private void generarReporte(String nombreInforme,
        ReportesBean.FORMATOS formato)
    {
        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        reemplazos.put("ciclo", ciclo);

        parametros.put("PR_CICLO", ciclo);

        Reporteador.resuelveConsulta(nombreInforme,
                        Integer.parseInt(SessionUtil.getModulo()), reemplazos,
                        parametros);
        try
        {
            archivoDescarga = JsfUtil.exportarStreamed(
                            nombreInforme, parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que se ejecuta al oprimir el boton cerrar. Cierra el formulario modal
     */
    public void oprimirCancelar()
    {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void seleccionarFilaCiclo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get("NUMERO").toString();
        fecha = ((Date) registroAux.getCampos().get("FECHA_PREPARACION"))
                        .toString();
    }
    // </METODOS_COMBOS_GRANDES>

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getCiclo()
    {
        return ciclo;
    }

    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public RegistroDataModelImpl getListaCiclo()
    {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
