/*-
 * FrmdescuentonivelesControlador.java
 *
 * 1.0
 * 
 * 11/09/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
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
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ResumenPorCentroCostoControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 11/09/2020
 * @author lmosquera
 */
@ManagedBean
@ViewScoped
public class  FrmdescuentonivelesControlador extends BeanBaseModal{
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
	private String ano1;
	    /**
	     * TODO DOCUMENTACION NECESARIA
	     */
	private String mes1;
	    /**
	     * TODO DOCUMENTACION NECESARIA
	     */
	private String periodo1;
	    /**
	     * Atributo usado para descargar contenidos de archivos desde la
	     * vista
	     */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	    /**
	     * TODO DOCUMENTACION NECESARIA
	     */
	private List<Registro> listalistaAno;
	    /**
	     * TODO DOCUMENTACION NECESARIA
	     */
	private List<Registro> listalistaMes;
	    /**
	     * TODO DOCUMENTACION NECESARIA
	     */
	private List<Registro> listalistaPeriodo;
	private String modulo;
	private String proceso;
	private String nombrePeriodoNomina;
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmdescuentonivelesControlador
     */
    public FrmdescuentonivelesControlador() {
    	super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
		proceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
    	ano1 = "2019";
		mes1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
		periodo1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
		nombrePeriodoNomina = SysmanFunciones.nvl(SessionUtil.getSessionVar("nombrePeriodoNomina"), "").toString();
        try {
        	numFormulario=2187;
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
		 cargarListalistaAno();
 cargarListalistaMes();
 cargarListalistaPeriodo();
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
    /**
     * 
     * Carga la lista listalistaAno
     *
     * TODO DOCUMENTACION ADICIONAL
     */
  public void cargarListalistaAno(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listalistaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ResumenPorCentroCostoControladorUrlEnum.URL4751.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	    /**
	     * 
	     * Carga la lista listalistaMes
	     *
	     * TODO DOCUMENTACION ADICIONAL
	     */
	public void cargarListalistaMes(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano1);
		try {
			listalistaMes = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ResumenPorCentroCostoControladorUrlEnum.URL4752.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	    /**
	     * 
	     * Carga la lista listalistaPeriodo
	     *
	     * TODO DOCUMENTACION ADICIONAL
	     */
	public void cargarListalistaPeriodo(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano1);
		param.put(GeneralParameterEnum.MES.getName(), mes1);
		try {
			listalistaPeriodo = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ResumenPorCentroCostoControladorUrlEnum.URL4753.getValue())
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
     * Metodo ejecutado al oprimir el boton BT291
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
	public void oprimirBT291() {
         //<CODIGO_DESARROLLADO>
         archivoDescarga=null;            
        //</CODIGO_DESARROLLADO>
         generarPlanilla(FORMATOS.PDF);
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton BT292
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
	public void oprimirBT292() {
         //<CODIGO_DESARROLLADO>
         archivoDescarga=null; 
         generarPlanilla(FORMATOS.EXCEL);
        //</CODIGO_DESARROLLADO>
    }
	
	public void generarPlanilla(ReportesBean.FORMATOS formato) {
		archivoDescarga = null;
		try {
			String nombreReporte = ejbSysmanUtil.consultarParametro(compania,
					"FORMATO DESCUENTO POR NIVELES", modulo, new Date(), false);
			
			Map<String, Object> reemplazar = new HashMap<>();
            
            reemplazar.put("proceso", proceso);
            reemplazar.put("ano", ano1);
            reemplazar.put("mes", mes1);
            reemplazar.put("periodo", periodo1);
            
            Map<String, Object> parametros = new HashMap<>();
			
            String jefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
                    "NOMBRE JEFE DESARROLLO HUMANO", modulo, new Date(), false);
            String cargoJefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
                    "CARGO JEFE DESARROLLO HUMANO", modulo, new Date(), false); 
            String jefeNomina = ejbSysmanUtil.consultarParametro(compania,
                    "NOMBRE JEFE NOMINA", modulo, new Date(), false);
            String cargoResponsableNomina = ejbSysmanUtil.consultarParametro(compania,
                    "CARGO RESPONSABLE DE NOMINA", modulo, new Date(), false);
            parametros.put("PR_PERIODO", periodo1);
            parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeDesarrolloHumano);
            parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
            parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
            parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);
            
            String strsql = Reporteador.resuelveConsulta(nombreReporte, Integer.parseInt(modulo), reemplazar);

            parametros.put("PR_STRSQL", strsql);
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
			
		}catch (SystemException | SysmanException | JRException | IOException e) {
		    JsfUtil.agregarMensajeError(e.getMessage());
		    logger.error(e.getMessage(), e);
		
		}
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
     * Retorna la variable ano1
     * 
     * @return  ano1
     */
	public String getAno1() {
        return ano1;
    }
    /**
     * Asigna la variable  ano1
     * 
     * @param  ano1
     * Variable a asignar en  ano1
     */
    public void setAno1(String ano1) {
        this.ano1 = ano1;
    }
    /**
     * Retorna la variable mes1
     * 
     * @return  mes1
     */
    public String getMes1() {
        return mes1;
    }
    /**
     * Asigna la variable  mes1
     * 
     * @param  mes1
     * Variable a asignar en  mes1
     */
    public void setMes1(String mes1) {
        this.mes1 = mes1;
    }
    /**
     * Retorna la variable periodo1
     * 
     * @return  periodo1
     */
    public String getPeriodo1() {
        return periodo1;
    }
    /**
     * Asigna la variable  periodo1
     * 
     * @param  periodo1
     * Variable a asignar en  periodo1
     */
    public void setPeriodo1(String periodo1) {
        this.periodo1 = periodo1;
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
     * Retorna la lista listalistaAno
     * 
     * @return listalistaAno
     */
    public List<Registro> getListalistaAno() {
        return listalistaAno;
    }
    /**
     * Asigna la lista listalistaAno
     * 
     * @param listalistaAno
     * Variable a asignar en  listalistaAno
     */
    public void setListalistaAno(List<Registro> listalistaAno) {
        this.listalistaAno = listalistaAno;
    }
    /**
     * Retorna la lista listalistaMes
     * 
     * @return listalistaMes
     */
    public List<Registro> getListalistaMes() {
        return listalistaMes;
    }
    /**
     * Asigna la lista listalistaMes
     * 
     * @param listalistaMes
     * Variable a asignar en  listalistaMes
     */
    public void setListalistaMes(List<Registro> listalistaMes) {
        this.listalistaMes = listalistaMes;
    }
    /**
     * Retorna la lista listalistaPeriodo
     * 
     * @return listalistaPeriodo
     */
    public List<Registro> getListalistaPeriodo() {
        return listalistaPeriodo;
    }
    /**
     * Asigna la lista listalistaPeriodo
     * 
     * @param listalistaPeriodo
     * Variable a asignar en  listalistaPeriodo
     */
    public void setListalistaPeriodo(List<Registro> listalistaPeriodo) {
        this.listalistaPeriodo = listalistaPeriodo;
    }
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
