/*-
 * InformeIngresosCarUnificadoControlador.java
 *
 * 1.0
 * 
 * 15/02/2017
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.InformeIngresosCarUnificadoControladorEnum;
import com.sysman.predial.enums.InformeIngresosCarUnificadoControladorUrlEnum;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 15/02/2017
 * @author spina
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * @author jcrodriguez - Refactoring y depuracion del controlador
 * @version 3, 07/07/2017
 */
@ManagedBean
@ViewScoped

public class InformeIngresosCarUnificadoControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    /**
     * numero de compania
     */
    private final String compania;
    /**
     * captura el valor del combo en el modal
     */
    private String bancoInicial;
    /**
     * captura el valor del combo en el modal
     */
    private String bancoFinal;
    /**
     * carga los datos en el combo bancoInicial
     */
    private RegistroDataModelImpl listaBancoI;
    /**
     * cargar los datos en el combo bancoFinal
     */
    private RegistroDataModelImpl listaBancoF;
    /**
     * permite descargar el informe
     */
    private StreamedContent archivoDescarga;
    /**
     * toma el valor del combo fechaIncial
     **/
    private Date fechaInicial;
    /**
     * toma el valor del combo fechaFinal
     **/
    private Date fechaFinal;
    /**
     * modulo de la compania
     **/
    private final String modulo;

    /**
     * Crea una nueva instancia de
     * InformeIngresosCarUnificadoControlador
     */
    public InformeIngresosCarUnificadoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INFORME_INGRESOS_CAR_UNIFICADO_CONTROLADOR.getCodigo();
            validarPermisos();
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
        cargarListaBancoI();
        cargarListaBancoF();
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
        Date fechaActual = new Date();
        fechaInicial = fechaActual;
        fechaFinal = fechaActual;
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaBancoI
     *
     */
    public void cargarListaBancoI()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(InformeIngresosCarUnificadoControladorUrlEnum.URL5070.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBancoI = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, InformeIngresosCarUnificadoControladorEnum.CODIGOBANCO.getValue());

    }

    /**
     * 
     * Carga la lista listaBancoF
     *
     */
    public void cargarListaBancoF()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(InformeIngresosCarUnificadoControladorUrlEnum.URL5654.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformeIngresosCarUnificadoControladorEnum.BANCOINICIAL.getValue(), bancoInicial);
        listaBancoF = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, InformeIngresosCarUnificadoControladorEnum.CODIGOBANCO.getValue());

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdPreliminar en la vista
     *
     *
     */
    public void oprimirCmdPdf()
    {
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdImprimir en la vista
     *
     *
     */
    public void oprimirCmdExcel()
    {

        archivoDescarga = null;
        generaInforme(FORMATOS.EXCEL);
    }

    public void generaInforme(FORMATOS formato)
    {
        if (fechaFinal.before(fechaInicial))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB147"));
            return;
        }
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        try
        {
            reemplazar.put("fechaInicial", convertirFecha(fechaInicial));
            reemplazar.put("fechaFinal", convertirFecha(fechaFinal));
            reemplazar.put("bancoInicial", bancoInicial);
            reemplazar.put("bancoFinal", bancoFinal);

            parametros.put(InformeIngresosCarUnificadoControladorEnum.PR_NITCOMPANIA.getValue(),
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put(InformeIngresosCarUnificadoControladorEnum.PR_NOMBRECOMPANIA.getValue(),
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put(InformeIngresosCarUnificadoControladorEnum.PR_FECHAI.getValue(),
                            SysmanFunciones
                                            .convertirAFechaCadena(fechaInicial,
                                                            InformeIngresosCarUnificadoControladorEnum.FORMATOFECHA.getValue()));
            parametros.put(InformeIngresosCarUnificadoControladorEnum.PR_FECHAF.getValue(),
                            SysmanFunciones
                                            .convertirAFechaCadena(fechaFinal,
                                                            InformeIngresosCarUnificadoControladorEnum.FORMATOFECHA.getValue()));

            Reporteador.resuelveConsulta(InformeIngresosCarUnificadoControladorEnum.REPORTE001411.getValue(),
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            InformeIngresosCarUnificadoControladorEnum.REPORTE001411.getValue(), parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (OutOfMemoryError | JRException | IOException | ParseException | SysmanException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                            e.getMessage()));
        }

    }

    private String convertirFecha(Date fecha)
    {
        StringBuilder cadenaFecha = new StringBuilder();

        try
        {
            cadenaFecha.append("TO_DATE('");
            cadenaFecha.append(SysmanFunciones
                            .convertirAFechaCadena(fecha,
                                            "dd/MM/yyyy"));
            cadenaFecha.append("','DD/MM/YYYY')");
            return cadenaFecha.toString();
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoI
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoI(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = registroAux.getCampos().get(InformeIngresosCarUnificadoControladorEnum.CODIGOBANCO.getValue()).toString();
        bancoFinal = null;
        cargarListaBancoF();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoF
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoF(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = registroAux.getCampos().get(InformeIngresosCarUnificadoControladorEnum.CODIGOBANCO.getValue()).toString();
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
     * Retorna la lista listaBancoI
     * 
     * @return listaBancoI
     */
    public RegistroDataModelImpl getListaBancoI()
    {
        return listaBancoI;
    }

    /**
     * Asigna la lista listaBancoI
     * 
     * @param listaBancoI
     * Variable a asignar en listaBancoI
     */
    public void setListaBancoI(RegistroDataModelImpl listaBancoI)
    {
        this.listaBancoI = listaBancoI;
    }

    /**
     * Retorna la lista listaBancoF
     * 
     * @return listaBancoF
     */
    public RegistroDataModelImpl getListaBancoF()
    {
        return listaBancoF;
    }

    /**
     * Asigna la lista listaBancoF
     * 
     * @param listaBancoF
     * Variable a asignar en listaBancoF
     */
    public void setListaBancoF(RegistroDataModelImpl listaBancoF)
    {
        this.listaBancoF = listaBancoF;
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

    public String getCompania()
    {
        return compania;
    }

    public String getModulo()
    {
        return modulo;
    }

}
