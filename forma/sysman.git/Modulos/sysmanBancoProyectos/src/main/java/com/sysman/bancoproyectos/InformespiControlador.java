/*-
 * InformespiControlador.java
 *
 * 1.0
 * 
 * 17/02/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import com.sysman.bancoproyectos.enums.InformespiControladorEnum;
import com.sysman.bancoproyectos.enums.InformespiControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 17/02/2022
 * @author ljdiaz
 */
@ManagedBean
@ViewScoped
public class InformespiControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	private String mesInicial;
	private String mesFinal;
	private String anio;
	private String cod;
	private StreamedContent archivoDescarga;
	private String modulo;

//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	
	private List<Registro> listamesInicial;
	
	private List<Registro> listamesFinal;
	
	private List<Registro> listaanio;
	
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de InformespiControlador
	 */
	public InformespiControlador() {
		super();
		compania = SessionUtil.getCompania();
		cod = GeneralParameterEnum.CODIGO.getName();
        modulo = SessionUtil.getModulo();
        
		try {
			numFormulario = 2340;
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
//<CARGAR_LISTA>
		cargarListaanio();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		anio = "2022";
		mesInicial = "Enero";
		mesFinal = "Diciembre";
		cambiaranio();
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listamesInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListamesInicial() {
// listamesInicial = service.getListado(conectorPool, "select * from dual");
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        try {
            listamesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		InformespiControladorUrlEnum.URL002
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}

	/**
	 * 
	 * Carga la lista listamesFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListamesFinal() {
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(InformespiControladorEnum.PARAM0.getValue(), mesInicial);
        try {
            listamesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		InformespiControladorUrlEnum.URL003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
// listamesFinal = service.getListado(conectorPool, "select * from dual");
	}

	/**
	 * 
	 * Carga la lista listaanio
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaanio() {
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		InformespiControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton pdf en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 * @throws SysmanException 
	 *
	 */
	// <METODOS_BOTONES>
    public void oprimirpdf() throws SysmanException {
        // <CODIGO_DESARROLLADO>
        setArchivoDescarga(null);
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al oprimir el boton excel en la vista
     * @throws SysmanException 
     */
    public void oprimirexcel() throws SysmanException {
        // <CODIGO_DESARROLLADO>
        setArchivoDescarga(null);
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * metodo que se ejecuta al cambiar el año
	 */
	public void cambiaranio() {
		cargarListamesInicial();
	}
	/**
	 * metodo que se ejecuta al cambiar el mes inicial
	 */
	public void cambiarmesInicial() {
		cargarListamesFinal();
	}
	/**
	 * metodo que se ejecuta al cambiar el mes Final
	 */
	public void cambiarmesFinal() {
		
	}
//</METODOS_CAMBIAR>
	 public void obtenerReporte(FORMATOS formatos) throws SysmanException {
	        String reporte = null;
	        String excelSalida = null;

	        try {
	            HashMap<String, Object> reemplazar = new HashMap<>();
	            reemplazar.put("compania", compania);
	            reemplazar.put("ano", anio);
	            reemplazar.put("mesInicial", mesInicial);
	            reemplazar.put("mesFinal", mesFinal);
	            

	            // MANEJO DE PARAMETROS DE REEMPLAZO
	            Map<String, Object> parametros = new HashMap<>();
	            // MANEJO DE PARAMETROS DEL REPORTE
	            if(formatos.name().equals("PDF")) {
	            	reporte = "002340PlantillaSpi";
	            }else {
	            	reporte = "800521PantillaSPI";
	            	excelSalida = "800522PantillaSPIExport";
	            }
	            
	            setArchivoDescarga(JsfUtil.exportarExcelPlano(reporte, excelSalida,
	                            ConectorPool.ESQUEMA_SYSMAN, formatos, reemplazar,
	                            parametros, Integer.valueOf(modulo)));

	        }
	        catch (FileNotFoundException e) {
	            JsfUtil.agregarMensajeError(
	                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
	                                            .replace("s$reporte$s", reporte)
	                                + e.getMessage());
	            logger.error(e.getMessage(), e);
	        }

	        catch (JRException | IOException | SysmanException e) {
	            JsfUtil.agregarMensajeError(e.getMessage());
	            logger.error(e.getMessage(), e);

	        } catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    }

//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable mesInicial
	 * 
	 * @return mesInicial
	 */
	public String getMesInicial() {
		return mesInicial;
	}

	/**
	 * Asigna la variable mesInicial
	 * 
	 * @param mesInicial Variable a asignar en mesInicial
	 */
	public void setMesInicial(String mesInicial) {
		this.mesInicial = mesInicial;
	}

	/**
	 * Retorna la variable mesFinal
	 * 
	 * @return mesFinal
	 */
	public String getMesFinal() {
		return mesFinal;
	}

	/**
	 * Asigna la variable mesFinal
	 * 
	 * @param mesFinal Variable a asignar en mesFinal
	 */
	public void setMesFinal(String mesFinal) {
		this.mesFinal = mesFinal;
	}

	/**
	 * Retorna la variable anio
	 * 
	 * @return anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * Asigna la variable anio
	 * 
	 * @param anio Variable a asignar en anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listamesInicial
	 * 
	 * @return listamesInicial
	 */
	public List<Registro> getListamesInicial() {
		return listamesInicial;
	}

	/**
	 * Asigna la lista listamesInicial
	 * 
	 * @param listamesInicial Variable a asignar en listamesInicial
	 */
	public void setListamesInicial(List<Registro> listamesInicial) {
		this.listamesInicial = listamesInicial;
	}

	/**
	 * Retorna la lista listamesFinal
	 * 
	 * @return listamesFinal
	 */
	public List<Registro> getListamesFinal() {
		return listamesFinal;
	}

	/**
	 * Asigna la lista listamesFinal
	 * 
	 * @param listamesFinal Variable a asignar en listamesFinal
	 */
	public void setListamesFinal(List<Registro> listamesFinal) {
		this.listamesFinal = listamesFinal;
	}

	/**
	 * Retorna la lista listaanio
	 * 
	 * @return listaanio
	 */
	public List<Registro> getListaanio() {
		return listaanio;
	}

	/**
	 * Asigna la lista listaanio
	 * 
	 * @param listaanio Variable a asignar en listaanio
	 */
	public void setListaanio(List<Registro> listaanio) {
		this.listaanio = listaanio;
	}
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
}
