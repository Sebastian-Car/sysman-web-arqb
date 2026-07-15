/*-
 * FmrbalancetrimestralControlador.java
 *
 * 1.0
 * 
 * 18/05/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;


import com.sysman.contabilidad.enums.FmrbalancetrimestralControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 18/05/2022
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class FmrbalancetrimestralControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>

	private String codigoInicial;
	private String codigoFinal;
	private String anio; 
    private String mes ;
    String mesInicial;
	String mesFinal;
	String mesIntermedio;
	private String digitos;
	private String modulo;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>

	private List<Registro> listaAnio;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>

	private RegistroDataModelImpl listaCodigoInicial;
	private RegistroDataModelImpl listaCodigoFinal;
//</DECLARAR_LISTAS_COMBO_GRANDE>

	@EJB
	EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Crea una nueva instancia de FmrbalancetrimestralControlador
	 */
	public FmrbalancetrimestralControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		try {
			
			numFormulario = GeneralCodigoFormaEnum.FMR_BALANCE_TRIMESTRAL_CONTROLADOR.getCodigo();
			validarPermisos();

		} catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        
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
		cargarListaAnio();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCodigoInicial();
		cargarListaCodigoFinal();
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

		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 */
	public void cargarListaAnio() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAnio = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FmrbalancetrimestralControladorUrlEnum.URL001.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaCodigoInicial
	 *
	 */
	public void cargarListaCodigoInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FmrbalancetrimestralControladorUrlEnum.URL002.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), 
				urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCodigoFinal
	 *
	 */
	public void cargarListaCodigoFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FmrbalancetrimestralControladorUrlEnum.URL003
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put("CODIGOINICIAL", codigoInicial);

		listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), 
				urlBean.getUrlConteo().getUrl(), param, 
				true,GeneralParameterEnum.CODIGO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf en la vista
	 *
	 *
	 */
	public void oprimirPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	public void generarInforme(FORMATOS formato) {
		
		
		if (mes.equals("1"))
        {
           mesInicial = "1";
           mesIntermedio= "2";
           mesFinal = "3";
        }
        else if (mes.equals("2"))
        {
            mesInicial = "4";
            mesIntermedio= "5";
            mesFinal = "6";
        }
        else if (mes.equals("3"))
        {
            mesInicial = "7";
            mesIntermedio= "8";
            mesFinal = "9";
        }
        else if (mes.equals("4"))
        {
           mesInicial = "10";
           mesIntermedio= "11";
           mesFinal = "12";
        }

  
        try
        {
            String reporte = "002373BALANCETRIMESTRAL";
            String sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();
            String nombreMesInicial = ejbSysmanUtil
                    .mostrarNombreDeMes(Integer.parseUnsignedInt(mesInicial))
                    .toUpperCase();

            String nombreMesFinal = ejbSysmanUtil
                    .mostrarNombreDeMes(Integer.parseUnsignedInt(mesFinal))
                    .toUpperCase();
            
            String nombreMesIntermedio = ejbSysmanUtil
                    .mostrarNombreDeMes(Integer.parseInt(mesIntermedio))
                    .toUpperCase();

            // MANEJO DE PARAMETROS DE REEMPLAZO
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("mes", mes);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("mesIntermedio", mesIntermedio);
            reemplazar.put("anio", anio);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("digitos", digitos);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("mesIntermedio", mesIntermedio);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_CODIGOINICIAL", (codigoInicial));
            parametros.put("PR_CODIGOFINAL", (codigoFinal));
            parametros.put("PR_ANO", (anio));
            parametros.put("PR_TRIMESTRE", (mes));
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_MESINICIAL", nombreMesInicial);
            parametros.put("PR_MESFINAL", nombreMesFinal);
            parametros.put("PR_MESINTERMEDIO", nombreMesIntermedio);
            parametros.put("PR_IMAGENES", sticker);
            parametros.put("PR_DEPARTAMENTOCOMPANIA", SessionUtil.getCompaniaIngreso().getDepartamento());

			Reporteador.resuelveConsulta(reporte, 
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazar, 
					parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                    ConectorPool.ESQUEMA_SYSMAN, formato);
			
			} catch (JRException | IOException | SysmanException
                    | NumberFormatException | SystemException e) {
        logger.error(e.getMessage(), e);
        JsfUtil.agregarMensajeError(e.getMessage());
			}

        }
        
	
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Anio
	 * 
	 * 
	 */
	public void cambiarAnio() {
		cargarListaCodigoInicial();
		cargarListaCodigoFinal();
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoInicial = registroAux.getCampos().get("CODIGO").toString();
		cargarListaCodigoFinal();
		
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoFinal = registroAux.getCampos().get("CODIGO").toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable codigoInicial
	 * 
	 * @return codigoInicial
	 */
	public String getCodigoInicial() {
		return codigoInicial;
	}

	/**
	 * Asigna la variable codigoInicial
	 * 
	 * @param codigoInicial Variable a asignar en codigoInicial
	 */
	public void setCodigoInicial(String codigoInicial) {
		this.codigoInicial = codigoInicial;
	}

	/**
	 * Retorna la variable codigoFinal
	 * 
	 * @return codigoFinal
	 */
	public String getCodigoFinal() {
		return codigoFinal;
	}

	/**
	 * Asigna la variable codigoFinal
	 * 
	 * @param codigoFinal Variable a asignar en codigoFinal
	 */
	public void setCodigoFinal(String codigoFinal) {
		this.codigoFinal = codigoFinal;
	}

	/**
	 * Retorna la variable mes
	 * 
	 * @return mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * Asigna la variable mes
	 * 
	 * @param mes Variable a asignar en mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
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

	/**
	 * Retorna la variable digitos
	 * 
	 * @return digitos
	 */
	public String getDigitos() {
		return digitos;
	}

	/**
	 * Asigna la variable digitos
	 * 
	 * @param digitos Variable a asignar en digitos
	 */
	public void setDigitos(String digitos) {
		this.digitos = digitos;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnio
	 * 
	 * @return listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}

	/**
	 * Asigna la lista listaAnio
	 * 
	 * @param listaAnio Variable a asignar en listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCodigoInicial
	 * 
	 * @return listaCodigoInicial
	 */
	public RegistroDataModelImpl getListaCodigoInicial() {
		return listaCodigoInicial;
	}

	/**
	 * Asigna la lista listaCodigoInicial
	 * 
	 * @param listaCodigoInicial Variable a asignar en listaCodigoInicial
	 */
	public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial) {
		this.listaCodigoInicial = listaCodigoInicial;		
	}

	/**
	 * Retorna la lista listaCodigoFinal
	 * 
	 * @return listaCodigoFinal
	 */
	public RegistroDataModelImpl getListaCodigoFinal() {
		return listaCodigoFinal;
	}

	/**
	 * Asigna la lista listaCodigoFinal
	 * 
	 * @param listaCodigoFinal Variable a asignar en listaCodigoFinal
	 */
	public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
		this.listaCodigoFinal = listaCodigoFinal;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>
}
