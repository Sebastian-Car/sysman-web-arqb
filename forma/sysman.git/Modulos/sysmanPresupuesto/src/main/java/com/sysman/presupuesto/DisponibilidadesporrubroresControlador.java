/*-
 * DisponibilidadesporrubroresControlador.java
 *
 * 1.0
 * 
 * 21/06/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.DisponibilidadesporrubroresControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 21/06/2024
 * @author TIFFON
 */
@ManagedBean
@ViewScoped
public class DisponibilidadesporrubroresControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo; 
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String cuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String cuentaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private int anio;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private int mes;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAno;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaMes;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCuentaFinal;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de DisponibilidadesporrubroresControlador
	 */
	public DisponibilidadesporrubroresControlador() {
		super();
		compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        anio = SysmanFunciones.getParteFecha(new Date(), Calendar.YEAR);
        mes = SysmanFunciones.getParteFecha(new Date(), Calendar.MONTH) + 1;
		try {
			numFormulario = GeneralCodigoFormaEnum.DISPONIBILIDADES_POR_RUBRO.getCodigo();
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
		cargarListaAno();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCuentaInicial();
		cargarListaCuentaFinal();
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
		/*
		 * FR2474-AL_ABRIR Private Sub Form_Open(Cancel As Integer) formularioAbrir 3,
		 * Me.Name DoCmd.Restore End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
     * 
     * Carga la lista listaAno
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListaAno(){
	Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    try {
        listaAno = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                        		DisponibilidadesporrubroresControladorUrlEnum.URL3655
                                                                        .getValue())
                                        .getUrl(), param));
    }
    catch (SystemException e) {
        JsfUtil.agregarMensajeError(e.getMessage());
        logger.error(e.getMessage(), e);

    }
}



	/**
	 * 
	 * Carga la lista listaCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCuentaInicial() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									DisponibilidadesporrubroresControladorUrlEnum
															.URL4084.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
				param, true,GeneralParameterEnum.CODIGO.getName());
//		cargarListaCuentaFinal();
// 94077
	}

	/**
	 * 
	 * Carga la lista listaCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCuentaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DisponibilidadesporrubroresControladorUrlEnum.URL4833.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put("CUENTAINICIAL", cuentaInicial);

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
				param, true,GeneralParameterEnum.CODIGO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirImprimirPdf() {
        //<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInforme(ReportesBean.FORMATOS.PDF);          
       //</CODIGO_DESARROLLADO>
   }
   /**
    * 
    * Metodo ejecutado al oprimir el boton imprimirExcel
    * en la vista
    *
    * TODO DOCUMENTACION ADICIONAL
    *
    */
public void oprimirimprimirExcel() {
        //<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInforme(ReportesBean.FORMATOS.EXCEL);      
       //</CODIGO_DESARROLLADO>
   }
	public void generaInforme(ReportesBean.FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
            reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");
            parametros.put("PR_ANO", anio);
            parametros.put("PR_MES",mes);
            parametros.put("PR_CUENTAINICIAL",cuentaInicial);
            parametros.put("PR_CUENTAFINAL",cuentaFinal);
            Reporteador.resuelveConsulta("002598DisponibilidadesporRubroRes",
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed("002598DisponibilidadesporRubroRes",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                            e.getMessage()));
        }
    }
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	public void cambiarAno() {
        //<CODIGO_DESARROLLADO>
		mes = SysmanFunciones.getParteFecha(new Date(), Calendar.MONTH) + 1;
		cuentaInicial = "";
        cuentaFinal = "";
        cargarListaCuentaInicial();
       //</CODIGO_DESARROLLADO>
   }
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                        		GeneralParameterEnum.CODIGO.getName()), "").toString();
        cuentaFinal = "";
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get(
        		GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable cuentaInicial
	 * 
	 * @return cuentaInicial
	 */
	public String getCuentaInicial() {
		return cuentaInicial;
	}

	/**
	 * Asigna la variable cuentaInicial
	 * 
	 * @param cuentaInicial Variable a asignar en cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}

	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}

	/**
	 * Asigna la variable cuentaFinal
	 * 
	 * @param cuentaFinal Variable a asignar en cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}

	/**
	 * Retorna la variable anio
	 * 
	 * @return anio
	 */
	public int getAnio() {
		return anio;
	}

	/**
	 * Asigna la variable anio
	 * 
	 * @param anio Variable a asignar en anio
	 */
	public void setAnio(int anio) {
		this.anio = anio;
	}

	/**
	 * Retorna la variable mes
	 * 
	 * @return mes
	 */
	public int getMes() {
		return mes;
	}

	/**
	 * Asigna la variable mes
	 * 
	 * @param mes Variable a asignar en mes
	 */
	public void setMes(int mes) {
		this.mes = mes;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}

	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	/**
	 * Retorna la lista listaMes
	 * 
	 * @return listaMes
	 */
	public List<Registro> getListaMes() {
		return listaMes;
	}

	/**
	 * Asigna la lista listaMes
	 * 
	 * @param listaMes Variable a asignar en listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}





//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}

	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}

	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}

	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>
}
