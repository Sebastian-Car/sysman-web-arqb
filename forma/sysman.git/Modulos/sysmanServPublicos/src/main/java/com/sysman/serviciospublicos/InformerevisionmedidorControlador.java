/*-
 * InformerevisionmedidorControlador.java
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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.InformerevisionmedidorControladorEnum;
import com.sysman.serviciospublicos.enums.InformerevisionmedidorControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario InformerevisionmedidorControlador
 *
 * @version 1.0, 27/10/2016
 * @author cperez
 * @version 2.0, 05/06/2017
 * @author jcrodriguez=>Refactoring,creacion de DSS y Depruacion del
 * controlador
 */
@ManagedBean
@ViewScoped
public class InformerevisionmedidorControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Obtiene el ciclo de la consulta
     */
    private String ciclo;
    /**
     * Obtiene el codigoInicial de la consulta
     */
    private String codigoIncial;
    /**
     * Obtiene el codigoFinal de la consulta
     */
    private String codigoFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private String codigoRutaConstante;
    /**
     * constante para el nombre Codigo ruta
     */
    private StreamedContent archivoDescarga;
    /**
     * Obtiene el ciclo de la consulta
     */
    private List<Registro> listaCiclo;
    /**
     * Lista el combo de codigo inicial
     */
    private RegistroDataModelImpl listacmbCodigoInicial;
    /**
     * Lista el combo de codigo final
     */
    private RegistroDataModelImpl listaCmbCodigoFinal;
    /**
     * Cambia el estado de visible a false del codigo final y el
     * inicial
     */
    private boolean bloquearCodigo;

    /**
     * Crea una nueva instancia de InformerevisionmedidorControlador
     */
    public InformerevisionmedidorControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INFORMEREVISIONMEDIDOR_CONTROLADOR
                            .getCodigo();
            codigoRutaConstante = "CODIGORUTA";
            validarPermisos();
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
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
        cargarListacmbCodigoInicial();
        cargarListaCmbCodigoFinal();
        ciclo = "T";
        codigoIncial = "000";
        codigoFinal = "999";
        cambiarCiclo();
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
        bloquearCodigo = true;
    }

    /**
     * 
     * Carga la lista listaCiclo
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
                                                            InformerevisionmedidorControladorUrlEnum.URL5007
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
     * Carga la lista listacmbCodigoInicial
     */
    public void cargarListacmbCodigoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformerevisionmedidorControladorUrlEnum.URL5426
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        "T".equals(ciclo) ? -1 : ciclo);
        listacmbCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        codigoRutaConstante);
    }

    /**
     * 
     * Carga la lista listaCmbCodigoFinal
     */
    public void cargarListaCmbCodigoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformerevisionmedidorControladorUrlEnum.URL6135
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        "T".equals(ciclo) ? -1 : ciclo);
        param.put(InformerevisionmedidorControladorEnum.CODIGOINICIAL
                        .getValue(), codigoIncial);
        listaCmbCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        codigoRutaConstante);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf()
    {
        archivoDescarga = null;
        if (codigoIncial.compareTo(codigoFinal) >= 1)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1778"));
        }
        else
        {
            genInforme(FORMATOS.PDF,
                            InformerevisionmedidorControladorEnum.REPORTE001174
                                            .getValue());
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel()
    {
        archivoDescarga = null;
        if (codigoIncial.compareTo(codigoFinal) >= 1)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1778"));
        }
        else
        {
            genInforme(FORMATOS.EXCEL,
                            InformerevisionmedidorControladorEnum.REPORTE001174
                                            .getValue());
        }
    }

    public void genInforme(ReportesBean.FORMATOS formato, String reporte)
    {
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoInicialSeleccion", codigoIncial);
            reemplazar.put("codigoFinalSeleccion", codigoFinal);
            reemplazar.put("ciclo", "T".equals(ciclo) ? ""
                : " AND SP_USUARIO.CICLO =" + ciclo);

            reemplazar.put(GeneralParameterEnum.COMPANIA.getName()
                            .toLowerCase(), compania);
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al cambiar el control Ciclo
     * 
     */
    public void cambiarCiclo()
    {
        // <CODIGO_DESARROLLADO>
        cargarListacmbCodigoInicial();
        cargarListaCmbCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoIncial = SysmanFunciones.validarCampoVacio(registroAux.getCampos(), codigoRutaConstante) ? ""
            : registroAux.getCampos().get(codigoRutaConstante)
                            .toString();
        codigoFinal = null;
        cargarListaCmbCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.validarCampoVacio(registroAux.getCampos(), codigoRutaConstante) ? ""
            : registroAux.getCampos().get(codigoRutaConstante)
                            .toString();
    }

    /**
     * metodos get y set
     */
    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo()
    {
        return ciclo;
    }

    public boolean isBloquearCodigo()
    {
        return bloquearCodigo;
    }

    public void setBloquearCodigo(boolean bloquearCodigo)
    {
        this.bloquearCodigo = bloquearCodigo;
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
     * Retorna la variable codigoIncial
     * 
     * @return codigoIncial
     */
    public String getCodigoIncial()
    {
        return codigoIncial;
    }

    /**
     * Asigna la variable codigoIncial
     * 
     * @param codigoIncial
     * Variable a asignar en codigoIncial
     */
    public void setCodigoIncial(String codigoIncial)
    {
        this.codigoIncial = codigoIncial;
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
     * Retorna la lista listacmbCodigoInicial
     * 
     * @return listacmbCodigoInicial
     */
    public RegistroDataModelImpl getListacmbCodigoInicial()
    {
        return listacmbCodigoInicial;
    }

    /**
     * Asigna la lista listacmbCodigoInicial
     * 
     * @param listacmbCodigoInicial
     * Variable a asignar en listacmbCodigoInicial
     */
    public void setListacmbCodigoInicial(
        RegistroDataModelImpl listacmbCodigoInicial)
    {
        this.listacmbCodigoInicial = listacmbCodigoInicial;
    }

    /**
     * Retorna la lista listaCmbCodigoFinal
     * 
     * @return listaCmbCodigoFinal
     */
    public RegistroDataModelImpl getListaCmbCodigoFinal()
    {
        return listaCmbCodigoFinal;
    }

    /**
     * Asigna la lista listaCmbCodigoFinal
     * 
     * @param listaCmbCodigoFinal
     * Variable a asignar en listaCmbCodigoFinal
     */
    public void setListaCmbCodigoFinal(
        RegistroDataModelImpl listaCmbCodigoFinal)
    {
        this.listaCmbCodigoFinal = listaCmbCodigoFinal;
    }

}
