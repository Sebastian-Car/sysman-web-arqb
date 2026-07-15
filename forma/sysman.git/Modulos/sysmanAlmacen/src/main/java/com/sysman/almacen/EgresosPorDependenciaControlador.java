/*-
 * EgresosPorDependenciaControlador.java
 *
 * 1.0
 * 
 * 01/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.EgresosPorDependenciaControladorUrlEnum;
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
 * Formulario que permite generar el informe de egresos de consumo por
 * dependencia.
 *
 * @version 1.0, 01/08/2018
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class EgresosPorDependenciaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private String elementoInicial;
    private String elementoFinal;
    private String dependenciaInicial;
    private String dependenciaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreElementoIni;
    private String nombreElementoFin;
    private String nombreDependenciaIni;
    private String nombreDependenciaFin;
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
    private RegistroDataModelImpl listaElementoInicial;
    private RegistroDataModelImpl listaElementoFinal;
    private RegistroDataModelImpl listaDependenciaInicial;
    private RegistroDataModelImpl listaDependenciaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de EgresosPorDependenciaControlador
     */
    public EgresosPorDependenciaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.EGRESOS_PORDEPENDENCIA_CONTROLADOR
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
        cargarListaElementoInicial();
        cargarListaElementoFinal();
        cargarListaDependenciaInicial();
        cargarListaDependenciaFinal();
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
     * Carga la lista listaElementoInicial
     *
     */
    public void cargarListaElementoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EgresosPorDependenciaControladorUrlEnum.URL11959
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElementoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    /**
     * 
     * Carga la lista listaElementoFinal
     *
     */
    public void cargarListaElementoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EgresosPorDependenciaControladorUrlEnum.URL12000
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ELEMENTOINICIAL", elementoInicial);

        listaElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    /**
     * 
     * Carga la lista listaDependenciaInicial
     *
     */
    public void cargarListaDependenciaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EgresosPorDependenciaControladorUrlEnum.URL12012
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaDependenciaFinal
     *
     */
    public void cargarListaDependenciaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EgresosPorDependenciaControladorUrlEnum.URL1812
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGOINICIAL", dependenciaInicial);

        listaDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
    	generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
	public void oprimirExcel() {
	        generarInforme(FORMATOS.EXCEL);
	    }
	
	
	private void generarInforme(FORMATOS formato){
		
		try {
            archivoDescarga = null;
            String reporte = "001842InfEGRConsumoXDep";
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("elementoInicial", elementoInicial);
            reemplazar.put("elementoFinal", elementoFinal);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("dependenciaInicial", dependenciaInicial);
            reemplazar.put("dependenciaFinal", dependenciaFinal);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            
            parametros.put("PR_FECHAINICIAL",
                            fechaInicial);
            
            parametros.put("PR_FECHAFINAL",
                            fechaFinal);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
		
	}

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaElementoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElementoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoInicial = registroAux.getCampos().get("CODIGOELEMENTO")
                        .toString();
        nombreElementoIni = registroAux.getCampos().get("NOMBRELARGO")
                        .toString();
        elementoFinal = nombreElementoFin = null;
        cargarListaElementoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaElementoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElementoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoFinal = registroAux.getCampos().get("CODIGOELEMENTO")
                        .toString();
        nombreElementoFin = registroAux.getCampos().get("NOMBRELARGO")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependenciaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaInicial = registroAux.getCampos().get("CODIGO").toString();
        nombreDependenciaIni = registroAux.getCampos().get("NOMBRE").toString();
        dependenciaFinal = nombreDependenciaFin = null;
        cargarListaDependenciaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependenciaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaFinal = registroAux.getCampos().get("CODIGO").toString();
        nombreDependenciaFin = registroAux.getCampos().get("NOMBRE").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable elementoInicial
     * 
     * @return elementoInicial
     */
    public String getElementoInicial() {
        return elementoInicial;
    }

    /**
     * Asigna la variable elementoInicial
     * 
     * @param elementoInicial
     * Variable a asignar en elementoInicial
     */
    public void setElementoInicial(String elementoInicial) {
        this.elementoInicial = elementoInicial;
    }

    /**
     * Retorna la variable elementoFinal
     * 
     * @return elementoFinal
     */
    public String getElementoFinal() {
        return elementoFinal;
    }

    /**
     * Asigna la variable elementoFinal
     * 
     * @param elementoFinal
     * Variable a asignar en elementoFinal
     */
    public void setElementoFinal(String elementoFinal) {
        this.elementoFinal = elementoFinal;
    }

    /**
     * Retorna la variable dependenciaInicial
     * 
     * @return dependenciaInicial
     */
    public String getDependenciaInicial() {
        return dependenciaInicial;
    }

    /**
     * Asigna la variable dependenciaInicial
     * 
     * @param dependenciaInicial
     * Variable a asignar en dependenciaInicial
     */
    public void setDependenciaInicial(String dependenciaInicial) {
        this.dependenciaInicial = dependenciaInicial;
    }

    /**
     * Retorna la variable dependenciaFinal
     * 
     * @return dependenciaFinal
     */
    public String getDependenciaFinal() {
        return dependenciaFinal;
    }

    /**
     * Asigna la variable dependenciaFinal
     * 
     * @param dependenciaFinal
     * Variable a asignar en dependenciaFinal
     */
    public void setDependenciaFinal(String dependenciaFinal) {
        this.dependenciaFinal = dependenciaFinal;
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
     * Retorna la variable nombreElementoIni
     * 
     * @return nombreElementoIni
     */
    public String getNombreElementoIni() {
        return nombreElementoIni;
    }

    /**
     * Asigna la variable nombreElementoIni
     * 
     * @param nombreElementoIni
     * Variable a asignar en nombreElementoIni
     */
    public void setNombreElementoIni(String nombreElementoIni) {
        this.nombreElementoIni = nombreElementoIni;
    }

    /**
     * Retorna la variable nombreElementoFin
     * 
     * @return nombreElementoFin
     */
    public String getNombreElementoFin() {
        return nombreElementoFin;
    }

    /**
     * Asigna la variable nombreElementoFin
     * 
     * @param nombreElementoFin
     * Variable a asignar en nombreElementoFin
     */
    public void setNombreElementoFin(String nombreElementoFin) {
        this.nombreElementoFin = nombreElementoFin;
    }

    /**
     * Retorna la variable nombreDependenciaIni
     * 
     * @return nombreDependenciaIni
     */
    public String getNombreDependenciaIni() {
        return nombreDependenciaIni;
    }

    /**
     * Asigna la variable nombreDependenciaIni
     * 
     * @param nombreDependenciaIni
     * Variable a asignar en nombreDependenciaIni
     */
    public void setNombreDependenciaIni(String nombreDependenciaIni) {
        this.nombreDependenciaIni = nombreDependenciaIni;
    }

    /**
     * Retorna la variable nombreDependenciaFin
     * 
     * @return nombreDependenciaFin
     */
    public String getNombreDependenciaFin() {
        return nombreDependenciaFin;
    }

    /**
     * Asigna la variable nombreDependenciaFin
     * 
     * @param nombreDependenciaFin
     * Variable a asignar en nombreDependenciaFin
     */
    public void setNombreDependenciaFin(String nombreDependenciaFin) {
        this.nombreDependenciaFin = nombreDependenciaFin;
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
     * Retorna la lista listaElementoInicial
     * 
     * @return listaElementoInicial
     */
    public RegistroDataModelImpl getListaElementoInicial() {
        return listaElementoInicial;
    }

    /**
     * Asigna la lista listaElementoInicial
     * 
     * @param listaElementoInicial
     * Variable a asignar en listaElementoInicial
     */
    public void setListaElementoInicial(
        RegistroDataModelImpl listaElementoInicial) {
        this.listaElementoInicial = listaElementoInicial;
    }

    /**
     * Retorna la lista listaElementoFinal
     * 
     * @return listaElementoFinal
     */
    public RegistroDataModelImpl getListaElementoFinal() {
        return listaElementoFinal;
    }

    /**
     * Asigna la lista listaElementoFinal
     * 
     * @param listaElementoFinal
     * Variable a asignar en listaElementoFinal
     */
    public void setListaElementoFinal(
        RegistroDataModelImpl listaElementoFinal) {
        this.listaElementoFinal = listaElementoFinal;
    }

    /**
     * Retorna la lista listaDependenciaInicial
     * 
     * @return listaDependenciaInicial
     */
    public RegistroDataModelImpl getListaDependenciaInicial() {
        return listaDependenciaInicial;
    }

    /**
     * Asigna la lista listaDependenciaInicial
     * 
     * @param listaDependenciaInicial
     * Variable a asignar en listaDependenciaInicial
     */
    public void setListaDependenciaInicial(
        RegistroDataModelImpl listaDependenciaInicial) {
        this.listaDependenciaInicial = listaDependenciaInicial;
    }

    /**
     * Retorna la lista listaDependenciaFinal
     * 
     * @return listaDependenciaFinal
     */
    public RegistroDataModelImpl getListaDependenciaFinal() {
        return listaDependenciaFinal;
    }

    /**
     * Asigna la lista listaDependenciaFinal
     * 
     * @param listaDependenciaFinal
     * Variable a asignar en listaDependenciaFinal
     */
    public void setListaDependenciaFinal(
        RegistroDataModelImpl listaDependenciaFinal) {
        this.listaDependenciaFinal = listaDependenciaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
