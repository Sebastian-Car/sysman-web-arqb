/*-
 * FrmejecucionplandesarrolloControlador.java
 *
 * 1.0
 * 
 * 24/09/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
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
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plandesarrollo.enums.FrmejecucionplandesarrolloControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 24/09/2019
 * @author jalfonso
 */
@ManagedBean
@ViewScoped
public class  FrmejecucionplandesarrolloControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    private int anio;

private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
    
private List<Registro> listacmbAno;

private final String modulo;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmejecucionplandesarrolloControlador
     */
    public FrmejecucionplandesarrolloControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		anio = SysmanFunciones.ano(new Date());

		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_EJECUCION_PLAN_DESARROLLO_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
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
    public void inicializar(){
//<CARGAR_LISTA>
		 cargarListacmbAno();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
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
   
public void cargarListacmbAno(){

	Map<String, Object> param = new TreeMap<>();
	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

	try {
		listacmbAno = RegistroConverter.toListRegistro(requestManager.getList(
				UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								FrmejecucionplandesarrolloControladorUrlEnum.URL001.getValue())
						.getUrl(),
				param));
	} catch (SystemException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}
}
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton btnExcel
     * en la vista
     *
     */
public void oprimirbtnExcel() {
         //<CODIGO_DESARROLLADO>
	
	Map<String, Object> reemplazo = new TreeMap<>();
	reemplazo.put(GeneralParameterEnum.COMPANIA.getName(), SysmanFunciones.concatenar("'", compania, "'"));
	reemplazo.put(GeneralParameterEnum.ANO.getName().toLowerCase(), anio);

	String datosExcel = Reporteador.resuelveConsulta("800355EJECUCIONPLANDESARROLLO", Integer.parseInt(modulo),
			reemplazo);

	try {
		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(datosExcel, ConectorPool.ESQUEMA_SYSMAN,
				FORMATOS.EXCEL,"800355EJECUCIONPLANDESARROLLO");
	} catch (JRException | IOException | SQLException | DRException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
		JsfUtil.agregarMensajeError(e.getMessage());
		logger.error(e.getMessage(), e);
	}           
        //</CODIGO_DESARROLLADO>
    }
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anio
     * 
     * @return  anio
     */
public int getAnio() {
        return anio;
    }
    /**
     * Asigna la variable  anio
     * 
     * @param  anio
     * Variable a asignar en  anio
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbAno
     * 
     * @return listacmbAno
     */
public List<Registro> getListacmbAno() {
        return listacmbAno;
    }
    /**
     * Asigna la lista listacmbAno
     * 
     * @param listacmbAno
     * Variable a asignar en  listacmbAno
     */
public void setListacmbAno(List<Registro> listacmbAno) {
        this.listacmbAno = listacmbAno;
    }
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
