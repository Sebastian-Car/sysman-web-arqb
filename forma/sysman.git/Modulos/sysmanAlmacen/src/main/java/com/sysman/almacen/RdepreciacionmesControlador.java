/*-
 * RdepreciacionmesControlador.java
 *
 * 1.0
 * 
 * 08/02/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.almacen.enums.RdepreciacionmesControladorEnum;
import com.sysman.almacen.enums.RdepreciacionmesControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * Se crea controlador para la generacion de cada uno de los informes de depresion
 * por elemento
 *
 * @version 1.0, 08/02/2021
 * @author dcastiblanco
 */
@ManagedBean
@ViewScoped
public class  RdepreciacionmesControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	/**
	 * Constante a nivel de clase que almacena el codigo del usuario
	 * que inicio sesion
	 */
	private final String serie;
	/**
	 * Constante que valida la serie de la placa
	 */
	private final String modulo;
	/**
	 * constante que almacena el modulo
	 */
	private final String cCodigo;
	private final String cNombre;
	private boolean visibleNiif;
	private boolean visibleEspecial;
	private boolean visibleFiltrar;
	private boolean ckNiff;
	/**
	 * variable de validacion ckNiff
	 */
	private boolean ckformatoEspecial;
	/**
	 * variable de validacion ckformatoEspecial
	 */
	private String placaInicial;
	/**
	 * variable que almacena la placa inicial
	 */
	private String placaFinal;
	/**
	 * variable que almacena la placa final
	 */
	private String filtroPor;
	/**
	 * variable que almacena el filtro
	 */
	private String elementoInicial;
	/**
	 * variable que almacena el elemento inicial
	 */
	private String elementoFinal;
	/**
	 * variabe que almcena el elemento final
	 */
	private String ano;
	/**
	 * variable que alamcena el año
	 */
	private String mes;
	/**
	 * variable que almcena el mes
	 */
	private String nombreElementoIni;
	/**
	 * variable que alamacena el nombreelEmentoInicial
	 */
	private String nombreElementoFin;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;

	private String paramfiltroPor;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 */
	private List<Registro> listaAno;
	/**
	 * variable que almacena la  listaAno
	 */
	private List<Registro> listaMes;
	private List<Registro> listaFiltradoPor;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * variable que almacena la  listaMes
	 */
	private RegistroDataModelImpl listaplacaInicial;
	/**
	 * variable que almacena la  listaplacaInicial
	 */
	private RegistroDataModelImpl listaplacaFinal;
	/**
	 * variable que almacena la  listaplacaFinal
	 */
	private RegistroDataModelImpl listaElementoInicial;
	/**
	 * variable que almacena la  listaElementoInicial
	 */
	private RegistroDataModelImpl listaElementoFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de RdepreciacionmesControlador
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	private String niff;
	private String formatoEspecial;
	private static final String SERVICIO = "Servicio";
	private static final String BODEGA = "Bodega";
	private static final String CONSOLIDADO = "Consolidado";
	private static final String INSERVIBLES = "Inservibles";
	private static final String RESPONSABILIDADES = "Responsabilidades";


	public RdepreciacionmesControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		serie = "SERIE";
		cCodigo = "CODIGO";
		cNombre = "NOMBRE";
		try {
			//2238;
			numFormulario = GeneralCodigoFormaEnum.R_DEPRECIACION_MES_CONTROLADOR
					.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
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
		//<CARGAR_LISTA>


		ano = String.valueOf(SysmanFunciones.getParteFecha(new Date(),
				Calendar.YEAR));
		mes = String.valueOf(
				SysmanFunciones.getParteFecha(new Date(),
						Calendar.MONTH) + 1);
		cargarListaAno();
		cargarListaMes();
		cargarListaplacaInicial();
		//cargarListaplacaFinal(); 
		cargarListaElementoInicial(); 
		cargarListaElementoFinal();
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
		try {
			niff = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA NIIF EN ALMACEN", modulo, new Date(), true),"NO");

			formatoEspecial = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA DEPRECIACION CMJ", modulo,
					new Date(), true),"NO");

			paramfiltroPor = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"FORMATO RELACION DE DEPRECIACIONES POR ELEMENTO",
					modulo, new Date(), false),"002187IDEPRECIARMES");


		} catch (Exception e) {
		}	

		if (paramfiltroPor.equals("002190IDEPRECIARMESBODEGAS")) {

			visibleFiltrar = true;
			visibleEspecial = false;
		} else {

			visibleFiltrar = false;
			visibleEspecial = true;
		}

		if (formatoEspecial.equals("SI")) {

			visibleEspecial = true;
		} else {
			visibleEspecial = false;

		}

		if (niff.equals("SI")) {
			visibleNiif = true;
		} else {
			visibleNiif = false;
		}

		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		try
		{
			listaAno = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									RdepreciacionmesControladorUrlEnum.URL5600
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}		
	/**
	 * 
	 * Carga la lista listaMes
	 *
	 */
	public void cargarListaMes(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(GeneralParameterEnum.ANO.name(), ano);

		try {
			listaMes = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									RdepreciacionmesControladorUrlEnum.URL5230
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
	 * Carga la lista listaplacaInicial
	 *
	 */
	public void cargarListaplacaInicial(){
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RdepreciacionmesControladorUrlEnum.URL2591
						.getValue());
		listaplacaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, serie);
	}
	/**
	 * 
	 * Carga la lista listaplacaFinal
	 * 
	 */
	public void cargarListaplacaFinal(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(RdepreciacionmesControladorEnum.PARAM2.getValue(), placaInicial);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RdepreciacionmesControladorUrlEnum.URL2901
						.getValue());
		listaplacaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, serie);
	}
	/**
	 * 
	 * Carga la lista listaElementoInicial
	 *
	 */
	public void cargarListaElementoInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RdepreciacionmesControladorUrlEnum.URL11959
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
	public void cargarListaElementoFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RdepreciacionmesControladorUrlEnum.URL12000
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(RdepreciacionmesControladorEnum.PARAM1.getValue(),
				elementoInicial);

		listaElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGOELEMENTO.getName());
	}


	/* public void cargarRegistrofiltroPor(int posicion, String elemento) {
	        // Carga un elemento al combo de filtros
	        HashMap<String, Object> lista = new HashMap<>();
	        lista.put(cCodigo, elemento);
	        lista.put(cNombre, elemento);
	        listaFiltradoPor.add(new Registro(posicion, lista));

	    }*/
	public void cargarListaFiltradoPor()
	{
		listaFiltradoPor = new ArrayList<>();
		listaFiltradoPor.add(new Registro(5, crearFila(BODEGA)));
		listaFiltradoPor.add(new Registro(6, crearFila(SERVICIO)));
		listaFiltradoPor.add(new Registro(7, crearFila(INSERVIBLES)));
		listaFiltradoPor.add(new Registro(8, crearFila(RESPONSABILIDADES)));
		listaFiltradoPor.add(new Registro(9, crearFila(CONSOLIDADO)));



	}
	private Map<String, Object> crearFila(String valor)
	{
		HashMap<String, Object> map = new HashMap<>();
		map.put(cCodigo, valor);
		map.put(cNombre, valor);
		return map;
	}

	/*public void cargarListaFiltradoPor() {
	        int posicion;
	        listaFiltradoPor = new ArrayList<>();
	        posicion = 1;
	        cargarRegistrofiltroPor(posicion, BODEGA);
	        posicion += 1;
	        cargarRegistrofiltroPor(posicion, SERVICIO);
	        posicion += 1;
	        cargarRegistrofiltroPor(posicion, INSERVIBLES);
	        posicion += 1;
	        cargarRegistrofiltroPor(posicion, RESPONSABILIDADES);
	        posicion += 1;
	        cargarRegistrofiltroPor(posicion, CONSOLIDADO);

	    }*/

	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton imprimirPdf
	 * en la vista
	 *
	 *
	 */
	public void oprimirimprimirPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		generarInforme(ReportesBean.FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton imprimirExcel
	 * en la vista
	 *
	 *
	 */
	public void oprimirimprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		generarInforme(ReportesBean.FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}

	public void generarInforme(ReportesBean.FORMATOS formato) {

		String reporte = null;
		String ultimoDia = "";
		String agrupacion="";
		try {
			agrupacion = ejbSysmanUtil.consultarParametro(compania,
					"DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),
					true);

			ultimoDia = SysmanFunciones.convertirAFechaCadena(
					SysmanFunciones.ultimoDiaDate(SysmanFunciones
							.convertirAFecha("01/" + mes + "/" +
									ano)),
					"dd/MM/yyyy");
		}
		catch (ParseException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}


		try {


			Map<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();
			reemplazar.put("agrupacion",agrupacion == null ? "3" : agrupacion);
			reemplazar.put("ultimoDia", ultimoDia);
			reemplazar.put("compania", compania);
			reemplazar.put("elementoInicial", elementoInicial);
			reemplazar.put("elementoFinal", elementoFinal);
			reemplazar.put("placaInicial", placaInicial);
			reemplazar.put("placaFinal", placaFinal);
			reemplazar.put("mes", mes);
			reemplazar.put("ano",ano);
			
			String filtro = null;
			String dependencia = null;	
			if(visibleFiltrar) {
			switch(filtroPor) {
			case "5":
				filtro = "BODEGA";
				dependencia = "AND DEPRECIAR.DEPENDENCIA = '000000000000'";
				break;
			case "6":
				filtro = "SERVICIO";
				dependencia = "AND DEPRECIAR.DEPENDENCIA NOT IN ('000000000000','999999999999','999999999902')";
				break;
			case "7":
				filtro = "INSERVIBLES";
				dependencia = "AND DEPRECIAR.DEPENDENCIA = '999999999999'";
				break;
			case "8":
				filtro = "RESPONSABILIDADES";
				dependencia = "AND DEPRECIAR.DEPENDENCIA = '999999999902'";
				break;
			case "9":
				filtro = "CONSOLIDADO";
				dependencia = "";
				break;
			default:
				filtro = " ";
				break;
			}
	
			reemplazar.put("dependencia",dependencia);
			}
			
			parametros.put("PR_FILTRADOPOR", filtro);
			parametros.put("PR_FORMS_RDEPRECIACIONMES_MES",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
					                                           .parseInt(mes)].toUpperCase());
			parametros.put("PR_FORMS_RDEPRECIACIONMES_ANO", ano);

			if (paramfiltroPor.equals("002190IDEPRECIARMESBODEGAS")) {

				reporte = "002190IDEPRECIARMESBODEGAS";

			}else {

				if (ckformatoEspecial){ 
					reporte = "002188IDEPRECIARMESCCCF";

				}else {

					if(ckNiff) {
						reporte = "002189IDEPRECIARMESNIIF";

					}else {
						reporte = "002187IDEPRECIARMES"; 

					}

				}
			}

			Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazar,parametros);

			archivoDescarga = JsfUtil.exportarStreamed(
					reporte, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		}
		catch (FileNotFoundException ex) {
			JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
					idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
					.replace("s$reporte$s", reporte),
					ex.getMessage()));
			logger.error(ex.getMessage(), ex);
		}
		catch (JRException | IOException | SysmanException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaplacaInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaplacaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		placaInicial = registroAux.getCampos().get(serie).toString();

		cargarListaplacaFinal();


	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaplacaFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaplacaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		placaFinal = registroAux.getCampos().get(serie).toString();
	}
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
		elementoInicial = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO
						.getName()),   "")
				.toString();

		nombreElementoIni = SysmanFunciones
				.nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
				.toString();


		elementoFinal= null;
		nombreElementoFin = null;
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
		elementoFinal = SysmanFunciones
				.nvl(registroAux.getCampos() .get(GeneralParameterEnum.CODIGOELEMENTO
						.getName()),  "")
				.toString();

		nombreElementoFin = SysmanFunciones
				.nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
				.toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ckNiff
	 * 
	 * @return  ckNiff
	 */

	/**
	 * Retorna la variable placaInicial
	 * 
	 * @return  placaInicial
	 */
	public String getPlacaInicial() {
		return placaInicial;
	}
	/**
	 * Asigna la variable  placaInicial
	 * 
	 * @param  placaInicial
	 * Variable a asignar en  placaInicial
	 */
	public void setPlacaInicial(String placaInicial) {
		this.placaInicial = placaInicial;
	}
	/**
	 * Retorna la variable placaFinal
	 * 
	 * @return  placaFinal
	 */
	public String getPlacaFinal() {
		return placaFinal;
	}
	/**
	 * Asigna la variable  placaFinal
	 * 
	 * @param  placaFinal
	 * Variable a asignar en  placaFinal
	 */
	public void setPlacaFinal(String placaFinal) {
		this.placaFinal = placaFinal;
	}

	/**
	 * Retorna la variable elementoInicial
	 * 
	 * @return  elementoInicial
	 */
	public String getElementoInicial() {
		return elementoInicial;
	}
	/**
	 * Asigna la variable  elementoInicial
	 * 
	 * @param  elementoInicial
	 * Variable a asignar en  elementoInicial
	 */
	public void setElementoInicial(String elementoInicial) {
		this.elementoInicial = elementoInicial;
	}
	/**
	 * Retorna la variable elementoFinal
	 * 
	 * @return  elementoFinal
	 */
	public String getElementoFinal() {
		return elementoFinal;
	}
	/**
	 * Asigna la variable  elementoFinal
	 * 
	 * @param  elementoFinal
	 * Variable a asignar en  elementoFinal
	 */
	public void setElementoFinal(String elementoFinal) {
		this.elementoFinal = elementoFinal;
	}
	/**
	 * Retorna la variable ano
	 * 
	 * @return  ano
	 */
	public String getAno() {
		return ano;
	}
	/**
	 * Asigna la variable  ano
	 * 
	 * @param  ano
	 * Variable a asignar en  ano
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}
	/**
	 * Retorna la variable mes
	 * 
	 * @return  mes
	 */
	public String getMes() {
		return mes;
	}
	/**
	 * Asigna la variable  mes
	 * 
	 * @param  mes
	 * Variable a asignar en  mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}
	/**
	 * Retorna la variable nombreElementoIni
	 * 
	 * @return  nombreElementoIni
	 */
	public String getNombreElementoIni() {
		return nombreElementoIni;
	}
	/**
	 * Asigna la variable  nombreElementoIni
	 * 
	 * @param  nombreElementoIni
	 * Variable a asignar en  nombreElementoIni
	 */
	public void setNombreElementoIni(String nombreElementoIni) {
		this.nombreElementoIni = nombreElementoIni;
	}
	/**
	 * Retorna la variable nombreElementoFin
	 * 
	 * @return  nombreElementoFin
	 */
	public String getNombreElementoFin() {
		return nombreElementoFin;
	}
	/**
	 * Asigna la variable  nombreElementoFin
	 * 
	 * @param  nombreElementoFin
	 * Variable a asignar en  nombreElementoFin
	 */
	public void setNombreElementoFin(String nombreElementoFin) {
		this.nombreElementoFin = nombreElementoFin;
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
	 * @param listaAno
	 * Variable a asignar en  listaAno
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
	 * @param listaMes
	 * Variable a asignar en  listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaplacaInicial
	 * 
	 * @return listaplacaInicial
	 */
	public RegistroDataModelImpl getListaplacaInicial() {
		return listaplacaInicial;
	}
	/**
	 * Asigna la lista listaplacaInicial
	 * 
	 * @param listaplacaInicial
	 * Variable a asignar en  listaplacaInicial
	 */
	public void setListaplacaInicial(RegistroDataModelImpl listaplacaInicial) {
		this.listaplacaInicial = listaplacaInicial;
	}
	/**
	 * Retorna la lista listaplacaFinal
	 * 
	 * @return listaplacaFinal
	 */
	public RegistroDataModelImpl getListaplacaFinal() {
		return listaplacaFinal;
	}
	/**
	 * Asigna la lista listaplacaFinal
	 * 
	 * @param listaplacaFinal
	 * Variable a asignar en  listaplacaFinal
	 */
	public void setListaplacaFinal(RegistroDataModelImpl listaplacaFinal) {
		this.listaplacaFinal = listaplacaFinal;
	}
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
	 * Variable a asignar en  listaElementoInicial
	 */
	public void setListaElementoInicial(RegistroDataModelImpl listaElementoInicial) {
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
	 * Variable a asignar en  listaElementoFinal
	 */
	public void setListaElementoFinal(RegistroDataModelImpl listaElementoFinal) {
		this.listaElementoFinal = listaElementoFinal;
	}
	/**
	 * @return the visibleNiif
	 */
	public boolean isVisibleNiif() {
		return visibleNiif;
	}
	/**
	 * @param visibleNiif the visibleNiif to set
	 */
	public void setVisibleNiif(boolean visibleNiif) {
		this.visibleNiif = visibleNiif;
	}
	/**
	 * @return the visibleEspecial
	 */
	public boolean isVisibleEspecial() {
		return visibleEspecial;
	}
	/**
	 * @param visibleEspecial the visibleEspecial to set
	 */
	public void setVisibleEspecial(boolean visibleEspecial) {
		this.visibleEspecial = visibleEspecial;
	}
	/**
	 * @return the visibleFiltrar
	 */
	public boolean isVisibleFiltrar() {
		return visibleFiltrar;
	}
	/**
	 * @param visibleFiltrar the visibleFiltrar to set
	 */
	public void setVisibleFiltrar(boolean visibleFiltrar) {
		this.visibleFiltrar = visibleFiltrar;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * @return the ckNiff
	 */
	public boolean isCkNiff() {
		return ckNiff;
	}
	/**
	 * @param ckNiff the ckNiff to set
	 */
	public void setCkNiff(boolean ckNiff) {
		this.ckNiff = ckNiff;
	}
	/**
	 * @return the ckformatoEspecial
	 */
	public boolean isCkformatoEspecial() {
		return ckformatoEspecial;
	}
	/**
	 * @param ckformatoEspecial the ckformatoEspecial to set
	 */
	public void setCkformatoEspecial(boolean ckformatoEspecial) {
		this.ckformatoEspecial = ckformatoEspecial;
	}
	/**
	 * @return the niff
	 */
	public String getNiff() {
		return niff;
	}
	/**
	 * @param niff the niff to set
	 */
	public void setNiff(String niff) {
		this.niff = niff;
	}
	/**
	 * @return the formatoEspecial
	 */
	public String getFormatoEspecial() {
		return formatoEspecial;
	}
	/**
	 * @param formatoEspecial the formatoEspecial to set
	 */
	public void setFormatoEspecial(String formatoEspecial) {
		this.formatoEspecial = formatoEspecial;
	}
	/**
	 * @return the listaFiltradoPor
	 */
	public List<Registro> getListaFiltradoPor() {
		return listaFiltradoPor;
	}
	/**
	 * @param listaFiltradoPor the listaFiltradoPor to set
	 */
	public void setListaFiltradoPor(List<Registro> listaFiltradoPor) {
		this.listaFiltradoPor = listaFiltradoPor;
	}
	/**
	 * @return the filtroPor
	 */
	public String getFiltroPor() {
		return filtroPor;
	}
	/**
	 * @param filtroPor the filtroPor to set
	 */
	public void setFiltroPor(String filtroPor) {
		this.filtroPor = filtroPor;
	}

}
