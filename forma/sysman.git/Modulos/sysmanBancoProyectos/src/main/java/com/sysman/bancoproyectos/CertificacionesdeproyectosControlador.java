/*-
 * CertificacionesdeproyectosControlador.java
 *
 * 1.0
 * 
 * 09/08/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 09/08/2019
 * @author obarragan
 */
@ManagedBean
@ViewScoped
public class  CertificacionesdeproyectosControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private final String modulo;
	private Date fechaInicial;
	private Date fechaFinal;
	private StreamedContent archivoDescarga;
	
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

	private String digitosprog;
	private String digitosle;
	private String digitometres;
	
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	
	
	
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de CertificacionesdeproyectosControlador
	 */
	public CertificacionesdeproyectosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			// 2099
			numFormulario=GeneralCodigoFormaEnum.CERTIFICACION_DE_PROYECTOS.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
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
		fechaInicial = new Date();
		fechaFinal = new Date();
		
		try {
			digitosprog  = ejbSysmanUtil.consultarParametro(compania, "NUMERO DE DIGITOS PROGRAMA", modulo, new Date(), false);
            digitosle    = ejbSysmanUtil.consultarParametro(compania, "NUMERO DE DIGITOS EJE", modulo, new Date(), false);
            digitometres = ejbSysmanUtil.consultarParametro(compania, "NUMERO DE DIGITOS META-RESULTADO", modulo, new Date(), false);            

		}catch (SystemException e) {
            	logger.error(e.getMessage(), e);
            	JsfUtil.agregarMensajeError(e.getMessage());
        }
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
	
    public void obtenerReporte(FORMATOS formatos) {
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB985"));
            return;
        }
        int anioActual = SysmanFunciones.ano(new Date());
        int anioFechaInicial = SysmanFunciones.ano(fechaInicial);
        int anioFechaFinal = SysmanFunciones.ano(fechaFinal);
        if(anioFechaInicial != anioActual || anioFechaFinal != anioActual) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4334"));
            return;
        }
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("digitosprog", digitosprog);
            reemplazar.put("digitosle", digitosle);
            reemplazar.put("digitometres",digitometres);

            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta("002034CertificacionesDeProyectos",
                            Integer.parseInt(modulo), reemplazar, parametros);
            parametros.put("PR_COMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_FECHA_INICIAL", fechaInicial);
            parametros.put("PR_FECHA_FINAL", fechaFinal);
            parametros.put("NRO_DIG_PROG", digitosprog);  
            parametros.put("NRO_DIG_EJE", digitosle);    
            parametros.put("NRO_DIG_METARESUL", digitometres);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "002034CertificacionesDeProyectos", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (ParseException | JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }	
	
	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton PDF
	 * en la vista
	 */
	public void oprimirPDF() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		obtenerReporte(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BT_EXCEL
	 * en la vista
	 */
	public void oprimirBT_EXCEL() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		obtenerReporte(FORMATOS.EXCEL);
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
	 * Retorna la variable fechaInicial
	 * 
	 * @return  fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * Asigna la variable  fechaInicial
	 * 
	 * @param  fechaInicial
	 * Variable a asignar en  fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return  fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * Asigna la variable  fechaFinal
	 * 
	 * @param  fechaFinal
	 * Variable a asignar en  fechaFinal
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
	public String getDigitosprog() {
		return digitosprog;
	}
	public void setDigitosprog(String digitosprog) {
		this.digitosprog = digitosprog;
	}
	public String getDigitosle() {
		return digitosle;
	}
	public void setDigitosle(String digitosle) {
		this.digitosle = digitosle;
	}
	public String getDigitometres() {
		return digitometres;
	}
	public void setDigitometres(String digitometres) {
		this.digitometres = digitometres;
	}
	
	

	
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
