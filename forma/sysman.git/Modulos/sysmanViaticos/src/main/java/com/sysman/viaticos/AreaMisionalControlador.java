/*-
 * AreaMisionalControlador.java
 *
 * 1.0
 * 
 * 19/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.viaticos;

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
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.enums.AreaMisionalControladorEnum;
import com.sysman.viaticos.enums.AreaMisionalControladorUrlEnum;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite generar un informe de areas misionales
 *
 * @version 1.0, 19/01/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class AreaMisionalControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * lista que muestra el área misional inicial
     */
    private List<Registro> listaCmbAreaMisiInic;
    /**
     * lista que muestra el área misional inicial
     */
    private List<Registro> listaCmbAreaMisiFin;
    /**
     * Valor seleccionado en combo de área inicial
     */
    private String codigoInicial;
    /**
     * Valor seleccionado en combo de área final
     */
    private String codigoFinal;
    /**
     * Valor seleccionado en combo de fecha Inicial
     */
    private Date fechaInicial;
    /**
     * Valor seleccionado en combo de fecha Final
     */
    private Date fechaFinal;

    private StreamedContent archivoDescarga;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de AreaMisionalControlador
     */
    public AreaMisionalControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {

            numFormulario = GeneralCodigoFormaEnum.AREAMISIONAL_CONTROLADOR.getCodigo();
            validarPermisos();
            fechaInicial = new Date();
            fechaFinal = new Date();
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
        cargarListaCmbAreaMisiInic();
        cargarListaCmbAreaMisiFin();
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
        // < CODIGO DESARROLLADO>
        // </CODIGO DESARROLLADO>
    }

    public void cargarListaCmbAreaMisiInic()
    {
        HashMap<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaCmbAreaMisiInic = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AreaMisionalControladorUrlEnum.URL184.getValue())
                                            .getUrl(),
                            parametros));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCmbAreaMisiFin
     */
    public void cargarListaCmbAreaMisiFin()
    {
        HashMap<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(AreaMisionalControladorEnum.PARAM0.getValue(), codigoInicial);

        try
        {
            listaCmbAreaMisiFin = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(AreaMisionalControladorUrlEnum.URL213.getValue())
                                            .getUrl(),
                            parametros));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdInforme en la vista
     *
     */
    public void oprimirCmdInforme()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando10()
    {
        archivoDescarga = null;
        getInforme(FORMATOS.EXCEL);
    }

    public void getInforme(FORMATOS formato)
    {
        if (fechaFinal.compareTo(fechaInicial) < 0)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB515"));
        }
        else
        {
            Map<String, Object> parametros = new HashMap<>();
            Map<String, Object> parametro = new HashMap<>();

            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put("areainicial", codigoInicial);
            parametros.put("areafinal", codigoFinal);
            parametros.put("fechainicio", SysmanFunciones.formatearFecha(fechaInicial));
            parametros.put("fechafinal", SysmanFunciones.formatearFecha(fechaFinal));

            parametro.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta("001655InfAreaMisional", Integer.parseInt(SessionUtil.getModulo()), parametros, parametro);

            try
            {
                archivoDescarga = JsfUtil.exportarStreamed("001655InfAreaMisional", parametro, ConectorPool.ESQUEMA_SYSMAN, formato);
            }

            catch (JRException | IOException | SysmanException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

    }

    public void cambiarCmbAreaMisiInic()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaCmbAreaMisiFin();
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
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCmbAreaMisiInic
     * 
     * @return listaCmbAreaMisiInic
     */
    public List<Registro> getListaCmbAreaMisiInic()
    {
        return listaCmbAreaMisiInic;
    }

    /**
     * Asigna la lista listaCmbAreaMisiInic
     * 
     * @param listaCmbAreaMisiInic
     * Variable a asignar en listaCmbAreaMisiInic
     */
    public void setListaCmbAreaMisiInic(List<Registro> listaCmbAreaMisiInic)
    {
        this.listaCmbAreaMisiInic = listaCmbAreaMisiInic;
    }

    /**
     * Retorna la lista listaCmbAreaMisiFin
     * 
     * @return listaCmbAreaMisiFin
     */
    public List<Registro> getListaCmbAreaMisiFin()
    {
        return listaCmbAreaMisiFin;
    }

    /**
     * Asigna la lista listaCmbAreaMisiFin
     * 
     * @param listaCmbAreaMisiFin
     * Variable a asignar en listaCmbAreaMisiFin
     */
    public void setListaCmbAreaMisiFin(List<Registro> listaCmbAreaMisiFin)
    {
        this.listaCmbAreaMisiFin = listaCmbAreaMisiFin;
    }

    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
