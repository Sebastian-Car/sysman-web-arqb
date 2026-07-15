/*-
 * FrmPagoEfectuadosControlador.java
 *
 * 1.0
 * 
 * 05/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.FrmPagoEfectuadosControladorEnum;
import com.sysman.contabilidad.enums.FrmPagoEfectuadosControladorUrlEnum;
import com.sysman.contabilidad.enums.RelacionPagoDescuentosControladorUrlEnum;
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
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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
 * Se migro formulario PlagosEfectuador del modulo contabilidad y
 * tesoreria SysmanCT2018.06.07
 *
 * @version 1.0, 09/07/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped
public class FrmPagoEfectuadosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el Nit del Tercero Inicial
     */
    private String terceroInicial;
    /**
     * Atributo que contiene el Nit del Tercero Final
     */
    private String terceroFinal;
    /**
     * Atributo que contiene el Codigo del Tipo Inicial
     */
    private String tipoInicial;
    /**
     * Atributo que contiene el Codigo del Tipo Final
     */
    private String tipoFinal;
    /**
     * Atributo que contiene la Fecha Inicial
     */
    private Date fechaInicial;
    /**
     * Atributo que contiene la Fecha Final
     */
    private Date fechaFinal;
    /**
     * Atributo que contiene el Nombre del Tercero Inicial
     */
    private String nombreInicial;
    /**
     * Atributo que contiene el nombre del Tercero Final
     */
    private String nombreFinal;
    /**
     * Atributo que contiene el nombre del Tipo Inicial
     */
    private String nomTipoIni;
    /**
     * Atributo que contiene el nombre del tipo Final
     */
    private String nomTipoFinal;
    /**
     * Atributo que almacena el año extraido de la variable de la
     * Fecha inicial
     */
    private String anoInicial;
    /**
     * Atributo que almacena el año extraido de la variable de la
     * fecha final
     */
    private String anoFinal;
    /**
     * Atributo que almacena el numero del modulo por que cual se esta
     * ingresando al formulario
     */
    private String modulo = SessionUtil.getModulo().toString();
    
    private Boolean formatoEspecial = false;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los detalles del combo listaTerceroInicial
     * (CB6071)
     */
    private RegistroDataModelImpl listaTerceroInicial;
    /**
     * Lista que contiene los detalles del combo listaTerceroFinal
     * (CB6072)
     */
    private RegistroDataModelImpl listaTerceroFinal;
    /**
     * Lista que contiene los detalles del combo listaTipoInicial
     * (CB6073)
     */
    private RegistroDataModelImpl listaTipoInicial;
    /**
     * Lista que contiene los detalles del combo listaTipoFinal
     * (CB6074)
     */
    private RegistroDataModelImpl listaTipoFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmPagoEfectuadosControlador
     */
    public FrmPagoEfectuadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1844
            numFormulario = GeneralCodigoFormaEnum.FRM_PAGOS_EFECTUADOS_CONTROLADOR
                            .getCodigo();
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

        fechaInicial = fechaFinal = new Date();
        anoInicial = anoFinal = String
                        .valueOf(SysmanFunciones.ano(new Date()));
        cargarListaTerceroInicial();

        cargarListaTipoInicial();
        cargarListaTipoFinal();
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
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Metodo que Carga la lista listaTerceroInicial
     */
    public void cargarListaTerceroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPagoEfectuadosControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmPagoEfectuadosControladorEnum.NIT.getValue());

    }

    /**
     * 
     * Metodo que Carga la lista listaTerceroFinal
     */
    public void cargarListaTerceroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionPagoDescuentosControladorUrlEnum.URL5344
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TECEROINICIAL", String.valueOf(terceroInicial));

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmPagoEfectuadosControladorEnum.NIT.getValue());

    }

    /**
     * 
     * Metodo que Carga la lista listaTipoInicial
     */
    public void cargarListaTipoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPagoEfectuadosControladorUrlEnum.URL0003
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmPagoEfectuadosControladorEnum.CODIGO.getValue());

    }

    /**
     * 
     * Metodo que Carga la lista listaTipoFinal
     */
    public void cargarListaTipoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPagoEfectuadosControladorUrlEnum.URL0003
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmPagoEfectuadosControladorEnum.CODIGO.getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>

        anoInicial = String.valueOf(SysmanFunciones.ano(fechaInicial));

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFinal
     * 
     *
     * 
     */
    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        anoFinal = String.valueOf(SysmanFunciones.ano(fechaFinal));

        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(FORMATOS formato) {
        // <CODIGO_DESARROLLADO>

        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "001818PagosEfectuados";   
            if(formatoEspecial) {
            	reporte = "002944RelacionDeOrdenesDePagoCanceladas";
            	parametros.put("PR_NOMBRE_ENCARGADO_TESORERIA", SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "NOMBRE ENCARGADO DE TESORERIA",
                                        modulo,
                                        new Date(), true), " "));
            	
            }
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(
                                            fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);

            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoFinal", tipoFinal);

            reemplazar.put("anoInicial", anoInicial);
            reemplazar.put("anoFinal", anoFinal);

            // PARAMETROS DEL REPORTE            
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial, "dd/MM/yyyy"));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, "dd/MM/yyyy"));
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            parametros.put("PR_CEDULA_PAGADOR", SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "CEDULA PAGADOR",
                                            modulo,
                                            new Date(), true), " "));

            parametros.put("PR_NOMBRE_PAGADOR", SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE_PAGADOR", modulo,
                                            new Date(), true), " "));

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e) {
            logger.error(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), "<br>",
                            e.getMessage()), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroInicial
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos()
                        .get(FrmPagoEfectuadosControladorEnum.NIT.getValue())
                        .toString();
        nombreInicial = registroAux.getCampos()
                        .get(FrmPagoEfectuadosControladorEnum.NOMBRE.getValue())
                        .toString();
        cargarListaTerceroFinal();
        terceroFinal = null;

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroFinal
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos()
                        .get(FrmPagoEfectuadosControladorEnum.NIT.getValue())
                        .toString();
        nombreFinal = registroAux.getCampos()
                        .get(FrmPagoEfectuadosControladorEnum.NOMBRE.getValue())
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoInicial
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = registroAux.getCampos()
                        .get(FrmPagoEfectuadosControladorEnum.CODIGO.getValue())
                        .toString();
        nomTipoIni = registroAux.getCampos()
                        .get(FrmPagoEfectuadosControladorEnum.NOMBRE.getValue())
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoFinal
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = registroAux.getCampos()
                        .get(FrmPagoEfectuadosControladorEnum.CODIGO.getValue())
                        .toString();
        nomTipoFinal = registroAux.getCampos()
                        .get(FrmPagoEfectuadosControladorEnum.NOMBRE.getValue())
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable terceroInicial
     * 
     * @return terceroInicial
     */
    public String getTerceroInicial() {
        return terceroInicial;
    }

    /**
     * Asigna la variable terceroInicial
     * 
     * @param terceroInicial
     * Variable a asignar en terceroInicial
     */
    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    /**
     * Retorna la variable terceroFinal
     * 
     * @return terceroFinal
     */
    public String getTerceroFinal() {
        return terceroFinal;
    }

    /**
     * Asigna la variable terceroFinal
     * 
     * @param terceroFinal
     * Variable a asignar en terceroFinal
     */
    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    /**
     * Retorna la variable tipoInicial
     * 
     * @return tipoInicial
     */
    public String getTipoInicial() {
        return tipoInicial;
    }

    /**
     * Asigna la variable tipoInicial
     * 
     * @param tipoInicial
     * Variable a asignar en tipoInicial
     */
    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    /**
     * Retorna la variable tipoFinal
     * 
     * @return tipoFinal
     */
    public String getTipoFinal() {
        return tipoFinal;
    }

    /**
     * Asigna la variable tipoFinal
     * 
     * @param tipoFinal
     * Variable a asignar en tipoFinal
     */
    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable nombreInicial
     * 
     * @return nombreInicial
     */
    public String getNombreInicial() {
        return nombreInicial;
    }

    /**
     * Asigna la variable nombreInicial
     * 
     * @param nombreInicial
     * Variable a asignar en nombreInicial
     */
    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    /**
     * Retorna la variable nombreFinal
     * 
     * @return nombreFinal
     */
    public String getNombreFinal() {
        return nombreFinal;
    }

    /**
     * Asigna la variable nombreFinal
     * 
     * @param nombreFinal
     * Variable a asignar en nombreFinal
     */
    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    /**
     * Retorna la variable nomTipoIni
     * 
     * @return nomTipoIni
     */
    public String getNomTipoIni() {
        return nomTipoIni;
    }

    /**
     * Asigna la variable nomTipoIni
     * 
     * @param nomTipoIni
     * Variable a asignar en nomTipoIni
     */
    public void setNomTipoIni(String nomTipoIni) {
        this.nomTipoIni = nomTipoIni;
    }

    /**
     * Retorna la variable nomTipoFinal
     * 
     * @return nomTipoFinal
     */
    public String getNomTipoFinal() {
        return nomTipoFinal;
    }

    /**
     * Asigna la variable nomTipoFinal
     * 
     * @param nomTipoFinal
     * Variable a asignar en nomTipoFinal
     */
    public void setNomTipoFinal(String nomTipoFinal) {
        this.nomTipoFinal = nomTipoFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTerceroInicial
     * 
     * @return listaTerceroInicial
     */
    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    /**
     * Asigna la lista listaTerceroInicial
     * 
     * @param listaTerceroInicial
     * Variable a asignar en listaTerceroInicial
     */
    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    /**
     * Retorna la lista listaTerceroFinal
     * 
     * @return listaTerceroFinal
     */
    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    /**
     * Asigna la lista listaTerceroFinal
     * 
     * @param listaTerceroFinal
     * Variable a asignar en listaTerceroFinal
     */
    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    /**
     * Retorna la lista listaTipoInicial
     * 
     * @return listaTipoInicial
     */
    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    /**
     * Asigna la lista listaTipoInicial
     * 
     * @param listaTipoInicial
     * Variable a asignar en listaTipoInicial
     */
    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    /**
     * Retorna la lista listaTipoFinal
     * 
     * @return listaTipoFinal
     */
    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    /**
     * Asigna la lista listaTipoFinal
     * 
     * @param listaTipoFinal
     * Variable a asignar en listaTipoFinal
     */
    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

    /**
     * @return the anoInicial
     */
    public String getAnoInicial() {
        return anoInicial;
    }

    /**
     * @param anoInicial
     * the anoInicial to set
     */
    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    /**
     * @return the anoFinal
     */
    public String getAnoFinal() {
        return anoFinal;
    }

    /**
     * @param anoFinal
     * the anoFinal to set
     */
    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }

	public Boolean getFormatoEspecial() {
		return formatoEspecial;
	}

	public void setFormatoEspecial(Boolean formatoEspecial) {
		this.formatoEspecial = formatoEspecial;
	}

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
