/*-
 * InformeabonosperiodoControlador.java
 *
 * 1.0
 * 
 * 04/11/2016
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
import com.sysman.serviciospublicos.enums.InformeabonosperiodoControladorEnum;
import com.sysman.serviciospublicos.enums.InformeabonosperiodoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

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
 * Controlador del formulario InformeabonosperiodoControlador
 *
 * @version 1.0, 04/11/2016
 * @author cperez
 * 
 * @version 2, 02/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos.
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class InformeabonosperiodoControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el ciclo de la consulta
     */
    private String ciclo;
    /**
     * Obtiene el codigo Inicial de la consulta
     */
    private String codigoInicial;
    /**
     * Obtiene el codigol Final de la consulta
     */
    private String codigoFinal;
    /**
     * Obtiene el periodo Inicial de la consulta
     */
    private String periodoInicial;
    /**
     * Obtiene el periodo Inicial de la consulta para enviar
     */
    private String periodoInicialEnvio;
    /**
     * Obtiene el periodo Inicial de la consulta para enviar
     */
    private String periodoFinalEnvio;
    /**
     * Obtiene el periodo Inicial de la consulta
     */
    private String periodoFinal;
    /**
     * Obtiene la fecha Inicial de la consulta
     */
    private Date fechaInicial;
    /**
     * Obtiene el fecha Inicial de la consulta
     */
    private Date fechaFinal;
    /**
     * Variable para generar mensaje de error
     */
    private String mensajeError;
    /*
     * Nombre constante para el "CODIGORUTA"
     */
    private String codigoRuta;
    /*
     * Nombre constante para el "FILTRO"
     */
    private String filtro;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Necesario para obtener y mandar la lista del Ciclo
     */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Necesario para obtener y mandar la lista del Codigo Inicial
     */
    private RegistroDataModelImpl listacmbCodigoInicial;
    /**
     * Necesario para obtener y mandar la lista del Codigo Final
     */
    private RegistroDataModelImpl listaCmbCodigoFinal;
    /**
     * Necesario para obtener y mandar la lista del Periodo Inicial
     */
    private RegistroDataModelImpl listacmbPeriodoInicial;
    /**
     * Necesario para obtener y mandar la lista del Periodo Final
     */
    private RegistroDataModelImpl listacmbPeriodoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformeabonosperiodoControlador
     */
    public InformeabonosperiodoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INFORMEABONOSPERIODO_CONTROLADOR.getCodigo();
            mensajeError = "MSM_INFORME_NO_EXISTE";
            codigoRuta = "CODIGORUTA";
            filtro = "FILTRO";
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {

        // <CARGAR_LISTA>
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbCodigoInicial();
        cargarListaCmbCodigoFinal();
        cargarListacmbPeriodoInicial();
        cargarListacmbPeriodoFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
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
                                                            InformeabonosperiodoControladorUrlEnum.URL6461
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
                        .getUrlServiceByUrlByEnumID(InformeabonosperiodoControladorUrlEnum.URL7167.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listacmbCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, codigoRuta);
    }

    /**
     * 
     * Carga la lista listaCmbCodigoFinal
     */
    public void cargarListaCmbCodigoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(InformeabonosperiodoControladorUrlEnum.URL8190.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(InformeabonosperiodoControladorEnum.PARAM0.getValue(), codigoInicial);

        listaCmbCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, codigoRuta);

    }

    /**
     * 
     * Carga la lista listacmbPeriodoInicial
     */
    public void cargarListacmbPeriodoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(InformeabonosperiodoControladorUrlEnum.URL9303.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbPeriodoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, filtro);
    }

    /**
     * 
     * Carga la lista listacmbPeriodoFinal
     */
    public void cargarListacmbPeriodoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(InformeabonosperiodoControladorUrlEnum.URL9310.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodoInicialEnvio);

        listacmbPeriodoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, filtro);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        long periodoFin = Long.parseLong(periodoFinalEnvio);
        long periodoIni = Long.parseLong(periodoInicialEnvio);
        if (periodoIni > periodoFin)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1793"));
        }
        else
        {
            genInforme(FORMATOS.PDF, "001215rptAbonosPeriodo");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        long codigoIn = Long.parseLong(codigoInicial);
        long codigoFin = Long.parseLong(codigoFinal);
        long periodoFin = Long.parseLong(periodoFinalEnvio);
        long periodoIni = Long.parseLong(periodoInicialEnvio);
        if (codigoIn > codigoFin)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1794"));
        }
        else if (periodoIni > periodoFin)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1793"));
        }
        else
        {
            genInforme(FORMATOS.EXCEL, "001215rptAbonosPeriodo");
        }
        // </CODIGO_DESARROLLADO>
    }

    /*
     * Metodo para generar el informe ya sea de pdf o excel
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte)
    {

        try
        {
            archivoDescarga = null;
            HashMap<String, Object> reemplazar = new HashMap<>();
            String condicionCiclo;
            if ("T".equals(ciclo))
            {
                condicionCiclo = "";

            }
            else
            {
                condicionCiclo = "AND SP_ABONOS.CICLO =  '" + ciclo + "'";
            }
            reemplazar.put("compania", compania);
            reemplazar.put("condicionCiclo", condicionCiclo);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("periodoInicialEnvio", periodoInicialEnvio);
            reemplazar.put("periodoFinalEnvio", periodoFinalEnvio);
            reemplazar.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_FORMS_INFORMEABONOSPERIODO_CICLO", ciclo);
            parametros.put("PR_FORMS_INFORMEABONOSPERIODO_CMBCODIGOINICIAL", codigoInicial);
            parametros.put("PR_FORMS_INFORMEABONOSPERIODO_CMBCODIGOFINAL", codigoFinal);
            parametros.put("PR_FORMS_INFORMEABONOSPERIODO_FECHAINICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FORMS_INFORMEABONOSPERIODO_FECHAFINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_FORMS_INFORMEABONOSPERIODO_CMBPERIODOINICIAL_COLUMN(1)", periodoInicial);
            parametros.put("PR_FORMS_INFORMEABONOSPERIODO_CMBPERIODOFINAL_COLUMN(1)", periodoFinal);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SysmanException | JRException | IOException | ParseException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + " "
                                + ex.getMessage());
            Logger.getLogger(InformeabonosperiodoControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarCiclo()
    {
        // <CODIGO_DESARROLLADO>
        codigoInicial = "";
        codigoFinal = "";
        cargarListacmbCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbCodigoInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigoRuta).toString();
        cargarListaCmbCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCmbCodigoFinal
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigoRuta).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbPeriodoInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbPeriodoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        periodoInicial = registroAux.getCampos().get("PER").toString();
        periodoInicialEnvio = registroAux.getCampos().get(filtro).toString();
        cargarListacmbPeriodoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbPeriodoFinal
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbPeriodoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        periodoFinal = registroAux.getCampos().get("PER").toString();
        periodoFinalEnvio = registroAux.getCampos().get(filtro).toString();
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListacmbCodigoInicial()
    {
        return listacmbCodigoInicial;
    }

    public void setListacmbCodigoInicial(
        RegistroDataModelImpl listacmbCodigoInicial)
    {
        this.listacmbCodigoInicial = listacmbCodigoInicial;
    }

    public RegistroDataModelImpl getListaCmbCodigoFinal()
    {
        return listaCmbCodigoFinal;
    }

    public void setListaCmbCodigoFinal(RegistroDataModelImpl listaCmbCodigoFinal)
    {
        this.listaCmbCodigoFinal = listaCmbCodigoFinal;
    }

    public RegistroDataModelImpl getListacmbPeriodoInicial()
    {
        return listacmbPeriodoInicial;
    }

    public void setListacmbPeriodoInicial(
        RegistroDataModelImpl listacmbPeriodoInicial)
    {
        this.listacmbPeriodoInicial = listacmbPeriodoInicial;
    }

    public RegistroDataModelImpl getListacmbPeriodoFinal()
    {
        return listacmbPeriodoFinal;
    }

    public void setListacmbPeriodoFinal(
        RegistroDataModelImpl listacmbPeriodoFinal)
    {
        this.listacmbPeriodoFinal = listacmbPeriodoFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
