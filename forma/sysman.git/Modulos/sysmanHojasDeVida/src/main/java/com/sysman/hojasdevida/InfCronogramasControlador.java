/*-
 * InfCronogramasControlador.java
 *
 * 1.0
 * 
 * 03/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.InfCronogramasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * Clase migrada para generar el informe del cronograma segun
 * actividad y responsable
 *
 * @version 1.0, 03/01/2018
 * @author ybecerra
 * 
 * @version 2.0, 29/05/2018
 * @author dnino
 * 
 * Se modifica para permitir intervalo de Actividades Inicial y Final,
 * al igual que Responsables Inicial y Final. También se deja como
 * campo obligatorio únicamente la Fecha.
 */

@ManagedBean
@ViewScoped

public class InfCronogramasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingreso a al aplicacion
     */
    private String condicion;
    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingreso a al aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el codigo de empleado del responsable
     * inicial seleccionado
     */
    private String responsableInicial;
    /**
     * Variable que almacena el codigo de empleado del responsable
     * final seleccionado
     */
    private String responsableFinal;
    /**
     * Variable que almacena el consecutivo de la actividad inicial
     * seleccionada
     */
    private String codigoActividadInicial;
    /**
     * Variable que almacena el consecutivo de la actividad final
     * seleccionada
     */
    private String codigoActividadFinal;
    /**
     * Variable que almacena la final inicial seleccionada
     */
    private Date fechaInicial;
    /**
     * Variable que almacena la final final seleccionada
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Variable que almacena el tipo de transacción.
     */
    private String nombreTipoTransaccion;
    /**
     * Variable que almacena el tipo de transacción.
     */
    private String tipoTransaccion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de empleado inicial
     */
    private RegistroDataModelImpl listaResponsableInicial;
    /**
     * Lista de empleado final
     */
    private RegistroDataModelImpl listaResponsableFinal;
    /**
     * Lista de registros de las actividades iniciales
     */
    private RegistroDataModelImpl listaActividadInicial;
    /**
     * Lista de registros de las actividades finales
     */
    private RegistroDataModelImpl listaActividadFinal;
    /**
     * Lista de Tipos de Transacción
     */
    private RegistroDataModelImpl listaTipoTransaccion;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InfCronogramasControlador
     */
    public InfCronogramasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 1575
            numFormulario = GeneralCodigoFormaEnum.INF_CRONOGRAMAS_SST_CONTROLADOR
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

        cargarListaTipoTransaccion();
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
        fechaInicial = fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoTransaccion
     *
     */
    public void cargarListaTipoTransaccion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfCronogramasControladorUrlEnum.URL4150
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoTransaccion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    /**
     * 
     * Carga la lista listaActividad Inicial
     */
    public void cargarListaActividadInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPO_TRANSACCION", tipoTransaccion);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfCronogramasControladorUrlEnum.URL4994
                                                        .getValue());

        listaActividadInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaActividad Final
     */
    public void cargarListaActividadFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfCronogramasControladorUrlEnum.URL5012
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ACTIVIDAD.getName(),
                        codigoActividadInicial);
        param.put("TIPO_TRANSACCION", tipoTransaccion);

        listaActividadFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaResponsable
     */
    public void cargarListaResponsableInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ACTIVIDADI", codigoActividadInicial);
        param.put("ACTIVIDADF", codigoActividadFinal);
       

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfCronogramasControladorUrlEnum.URL4381
                                                        .getValue());
        listaResponsableInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.RESPONSABLE.getName());
    }

    /**
     * 
     * Carga la lista listaResponsable
     */
    public void cargarListaResponsableFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ACTIVIDADI", codigoActividadInicial);
        param.put("ACTIVIDADF", codigoActividadFinal);
        param.put("REPONSABLEI", responsableInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfCronogramasControladorUrlEnum.URL4522
                                                        .getValue());
        listaResponsableFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.RESPONSABLE.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
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
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoTransaccion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoTransaccion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoTransaccion = SysmanFunciones
                        .toString(registroAux.getCampos().get("CODIGO"));
        nombreTipoTransaccion = SysmanFunciones.toString(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
       
        codigoActividadInicial = null;
        codigoActividadFinal = null;
        responsableInicial = null;
        responsableFinal = null;
        cargarListaActividadInicial();
        cargarListaActividadFinal();
        cargarListaResponsableInicial();
        cargarListaResponsableFinal();
        
       
        
        
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaActividadI
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaActividadInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoActividadInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName())
                        .toString();
        responsableInicial = null;
        responsableFinal = null;
           
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaActividadF
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaActividadFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoActividadFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName())
                        .toString();
        responsableInicial = null;
        responsableFinal = null;
        cargarListaResponsableInicial();
        cargarListaResponsableFinal();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        responsableInicial = registroAux.getCampos().get(GeneralParameterEnum.RESPONSABLE.getName()).toString();
        cargarListaResponsableFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        responsableFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.RESPONSABLE.getName())
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */

    public void generarInforme(ReportesBean.FORMATOS formato) {

        HashMap<String, Object> reemplazar = new HashMap<>();
        
        reemplazar.put("codigoActividadInicial", codigoActividadInicial);
        reemplazar.put("codigoActividadFinal", codigoActividadFinal);
        reemplazar.put("responsableInicial", responsableInicial);
        reemplazar.put("responsableFinal", responsableFinal);
        if("".equals(codigoActividadInicial) && "".equals(codigoActividadFinal) && "".equals(responsableInicial) && "".equals(responsableFinal))
        {
        condicion =  ""; 
        	}else {
        	if("".equals(responsableInicial) && "".equals(responsableFinal)) {
        		condicion = " AND SST_CRONOGRAMA.CODIGO_ACTIVIDAD BETWEEN "+ codigoActividadInicial + " AND "+ codigoActividadFinal;
		
        	}else {
        		condicion = " AND SST_CRONOGRAMA.CODIGO_ACTIVIDAD BETWEEN "+ codigoActividadInicial + " AND "+ codigoActividadFinal + 
                 		" AND SST_CRONOGRAMA.RESPONSABLE BETWEEN " + responsableInicial + " AND " + responsableFinal;
        		
        	}
        	
        }
        
        reemplazar.put("condicion", condicion);
        reemplazar.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));
        reemplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));
        reemplazar.put("tipoTransaccion", tipoTransaccion);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("PR_NOMBREEMPRESA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        Reporteador.resuelveConsulta("001646Infsstcronogramas",
                        Integer.parseInt(modulo), reemplazar,
                        parametros);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001646Infsstcronogramas",
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * @return the responsableInicial
     */
    public String getResponsableInicial() {
        return responsableInicial;
    }

    /**
     * @param responsableInicial
     * the responsableInicial to set
     */
    public void setResponsableInicial(String responsableInicial) {
        this.responsableInicial = responsableInicial;
    }

    /**
     * @return the responsableFinal
     */
    public String getResponsableFinal() {
        return responsableFinal;
    }

    /**
     * @param responsableFinal
     * the responsableFinal to set
     */
    public void setResponsableFinal(String responsableFinal) {
        this.responsableFinal = responsableFinal;
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
     * @return the tipoTransaccion
     */
    public String getTipoTransaccion() {
        return tipoTransaccion;
    }

    /**
     * @param tipoTransaccion
     * the tipoTransaccion to set
     */
    public void setTipoTransaccion(String tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    /**
     * @return the nombreTipoTransaccion
     */
    public String getNombreTipoTransaccion() {
        return nombreTipoTransaccion;
    }

    /**
     * @param nombreTipoTransaccion
     * the nombreTipoTransaccion to set
     */
    public void setNombreTipoTransaccion(String nombreTipoTransaccion) {
        this.nombreTipoTransaccion = nombreTipoTransaccion;
    }

    /**
     * @return the codigoActividadInicial
     */
    public String getCodigoActividadInicial() {
        return codigoActividadInicial;
    }

    /**
     * @param codigoActividadInicial
     * the codigoActividadInicial to set
     */
    public void setCodigoActividadInicial(String codigoActividadInicial) {
        this.codigoActividadInicial = codigoActividadInicial;
    }

    /**
     * @return the codigoActividadFinal
     */
    public String getCodigoActividadFinal() {
        return codigoActividadFinal;
    }

    /**
     * @param codigoActividadFinal
     * the codigoActividadFinal to set
     */
    public void setCodigoActividadFinal(String codigoActividadFinal) {
        this.codigoActividadFinal = codigoActividadFinal;
    }

    /**
     * @return the listaActividadInicial
     */
    public RegistroDataModelImpl getListaActividadInicial() {
        return listaActividadInicial;
    }

    /**
     * @param listaActividadInicial
     * the listaActividadInicial to set
     */
    public void setListaActividadInicial(
        RegistroDataModelImpl listaActividadInicial) {
        this.listaActividadInicial = listaActividadInicial;
    }

    /**
     * @return the listaActividadFinal
     */
    public RegistroDataModelImpl getListaActividadFinal() {
        return listaActividadFinal;
    }

    /**
     * @param listaActividadFinal
     * the listaActividadFinal to set
     */
    public void setListaActividadFinal(
        RegistroDataModelImpl listaActividadFinal) {
        this.listaActividadFinal = listaActividadFinal;
    }

    /**
     * @return the listaResponsableInicial
     */
    public RegistroDataModelImpl getListaResponsableInicial() {
        return listaResponsableInicial;
    }

    /**
     * @param listaResponsableInicial
     * the listaResponsableInicial to set
     */
    public void setListaResponsableInicial(
        RegistroDataModelImpl listaResponsableInicial) {
        this.listaResponsableInicial = listaResponsableInicial;
    }

    /**
     * @return the listaResponsableFinal
     */
    public RegistroDataModelImpl getListaResponsableFinal() {
        return listaResponsableFinal;
    }

    /**
     * @param listaResponsableFinal
     * the listaResponsableFinal to set
     */
    public void setListaResponsableFinal(
        RegistroDataModelImpl listaResponsableFinal) {
        this.listaResponsableFinal = listaResponsableFinal;
    }

    /**
     * @return the listaTipoTransaccion
     */
    public RegistroDataModelImpl getListaTipoTransaccion() {
        return listaTipoTransaccion;
    }

    /**
     * @param listaTipoTransaccion
     * the listaTipoTransaccion to set
     */
    public void setListaTipoTransaccion(
        RegistroDataModelImpl listaTipoTransaccion) {
        this.listaTipoTransaccion = listaTipoTransaccion;
    }

	public String getCondicion() {
		return condicion;
	}

	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
