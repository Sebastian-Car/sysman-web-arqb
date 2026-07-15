/*-
 * FrmListadoElementosControlador.java
 *
 * 1.0
 * 
 * 25/11/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.context.RequestContext;

import com.sysman.almacen.enums.FrmlistadoelementosUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 25/11/2019
 * @author jalfonsop
 */
@ManagedBean
@ViewScoped
public class  FrmListadoElementosControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
//<DECLARAR_ATRIBUTOS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private BigInteger placaIni;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private BigInteger placaFin;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>

private StreamedContent archivoDescarga;

    /**
     * TODO DOCUMENTACION NECESARIA
     */
 private RegistroDataModelImpl listaPlacaini;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
 private RegistroDataModelImpl listaPlacaFin;
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmListadoElementosControlador
     */
    public FrmListadoElementosControlador() {
  super();
            compania = SessionUtil.getCompania();
        try {
    		numFormulario = GeneralCodigoFormaEnum.FRM_LISTADO_ELEMENTOS.getCodigo();
            validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        }finally{
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
    public void inicializar(){
//<CARGAR_LISTA>
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		 cargarListaPlacaini(); 
		
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
  @Override
	public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
//<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaPlacaini
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListaPlacaini(){
	UrlBean urlBean = UrlServiceUtil.getInstance()
			.getUrlServiceByUrlByEnumID(
					FrmlistadoelementosUrlEnum.URL1538
					.getValue());
	Map<String, Object> param = new TreeMap<>();
	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

	listaPlacaini = new RegistroDataModelImpl(urlBean.getUrl(),
			urlBean.getUrlConteo().getUrl(), param,
			true, GeneralParameterEnum.SERIE.getName());
}
    /**
     * 
     * Carga la lista listaPlacaFin
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListaPlacaFin(){
	UrlBean urlBean = UrlServiceUtil.getInstance()
			.getUrlServiceByUrlByEnumID(
					FrmlistadoelementosUrlEnum.URL1538
					.getValue());
	Map<String, Object> param = new TreeMap<>();
	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	param.put("SERIEINICIAL", placaIni);
	

	listaPlacaFin = new RegistroDataModelImpl(urlBean.getUrl(),
			urlBean.getUrlConteo().getUrl(), param,
			true, GeneralParameterEnum.SERIE.getName());
}
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdPantalla
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirExcel() {
         //<CODIGO_DESARROLLADO>
	genInforme(ReportesBean.FORMATOS.EXCEL);
        //</CODIGO_DESARROLLADO>
    }

public void genInforme(ReportesBean.FORMATOS formato) {
	archivoDescarga = null;
	String reporte = "800400ListadoElementos";

	HashMap<String, Object> reemplazar = new HashMap<>();
	reemplazar.put("Placaini", placaIni);
	reemplazar.put("Placafin", placaFin);
	// MANEJO DE PARAMETROS DE REEMPLAZO
	Map<String, Object> parametros = new HashMap<>();
	// MANEJO DE PARAMETROS DEL REPORTE
	String strSql = Reporteador.resuelveConsulta(reporte,
			Integer.parseInt(SessionUtil.getModulo()),
			reemplazar);
	
	parametros.put("PR_NOMBRECOMPANIA",
			SessionUtil.getCompaniaIngreso().getNombre());

	try {
		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN,
				FORMATOS.EXCEL,reporte);
	}
	catch (JRException | IOException | SQLException | DRException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}
}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPlacaini
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaPlacaini(SelectEvent event) {
Registro registroAux = (Registro) event.getObject();
        placaIni=  (BigInteger) registroAux.getCampos().get("SERIE");
        cargarListaPlacaFin();
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPlacaFin
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaPlacaFin(SelectEvent event) {
Registro registroAux = (Registro) event.getObject();
        placaFin= (BigInteger) registroAux.getCampos().get("SERIE");
}
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaPlacaini
     * 
     * @return listaPlacaini
     */
    public RegistroDataModelImpl getListaPlacaini() {
        return listaPlacaini;
    }
    public BigInteger getPlacaIni() {
		return placaIni;
	}
	public void setPlacaIni(BigInteger placaIni) {
		this.placaIni = placaIni;
	}
	public BigInteger getPlacaFin() {
		return placaFin;
	}
	public void setPlacaFin(BigInteger placaFin) {
		this.placaFin = placaFin;
	}
	
	public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
	/**
     * Asigna la lista listaPlacaini
     * 
     * @param listaPlacaini
     * Variable a asignar en  listaPlacaini
     */
    public void setListaPlacaini(RegistroDataModelImpl listaPlacaini) {
        this.listaPlacaini = listaPlacaini;
    }
    /**
     * Retorna la lista listaPlacaFin
     * 
     * @return listaPlacaFin
     */
    public RegistroDataModelImpl getListaPlacaFin() {
        return listaPlacaFin;
    }
    /**
     * Asigna la lista listaPlacaFin
     * 
     * @param listaPlacaFin
     * Variable a asignar en  listaPlacaFin
     */
    public void setListaPlacaFin(RegistroDataModelImpl listaPlacaFin) {
        this.listaPlacaFin = listaPlacaFin;
    }
//</SET_GET_LISTAS_COMBO_GRANDE>
}
