/*-
 * ConfigurarPlanPptalCuipoControlador.java
 *
 * 1.0
 * 
 * 21/06/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.cgr.ejb.impl.EjbCGRCero;
import com.sysman.cgr.enums.ConfigurarPlanPptalCuipoControladorUrlEnum;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite configurar rubros presupuestales cuipo
 *
 * @version 1.0, 21/06/2021
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class ConfigurarPlanPptalCuipoControlador
extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la
	 * cual inicio sesion el usuario, el valor de esta constante es asignado en
	 * el constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>

	private String regalias;

	private String naturaleza;

	private String anio;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>

	private List<Registro> listaAnio;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>

	private RegistroDataModelImpl listaCodigoCCEPT;

	private RegistroDataModelImpl listaCodigoCCEPTE;

	private RegistroDataModelImpl listaSector;

	private RegistroDataModelImpl listaSectorE;

	private RegistroDataModelImpl listaProgramaCuipo;

	private RegistroDataModelImpl listaProgramaCuipoE;

	private RegistroDataModelImpl listaVigenciaTesoreria;

	private RegistroDataModelImpl listaVigenciaTesoreriaE;

	private RegistroDataModelImpl listaPoliticaPublica;

	private RegistroDataModelImpl listaPoliticaPublicaE;

	private RegistroDataModelImpl listaFuenteFinanciacion;

	private RegistroDataModelImpl listaFuenteFinanciacionE;

	private RegistroDataModelImpl listaVigenciaGasto;

	private RegistroDataModelImpl listaVigenciaGastoE;

	private RegistroDataModelImpl listaTerceroChip;

	private RegistroDataModelImpl listaTerceroChipE;

	private RegistroDataModelImpl listaCodigoProducto;

	private RegistroDataModelImpl listaCodigoProductoE;

	private RegistroDataModelImpl listaCodigoCPC;

	private RegistroDataModelImpl listaCodigoCPCE;

	private RegistroDataModelImpl listaSeccionPresupuestal;

	private RegistroDataModelImpl listaSeccionPresupuestalE;

	private RegistroDataModelImpl listaTipoRecursoSGR;

	private RegistroDataModelImpl listaTipoRecursoSGRE;

	private List<Registro> listaTipoClasificador;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se
	 * alamcena el identificador del registro que se selecciono
	 */
	private String auxiliar;

	private String campoActualizar;

	private boolean aplicaDestEspecifica;

	private boolean visibleActualizar;

	private boolean visibleEjecutarProceso;

	private boolean visibleEjecutarProcesoClas;


	private boolean visibleSeleccionTercero;

	private int filaSeleccionada;

	private int modeloA;

	private boolean bloqCodCCEPT;

	private boolean bloqFuente;

	private boolean bloqSeccionPptal;

	private boolean bloqSector;

	private boolean bloqProgCuipo;

	private boolean bloqCodProducto;

	private boolean bloqCodCpc;

	private boolean bloqDetalleSectorial;

	private int indice;

	private int aplicacion;

	private String vigencia;

	private String codigoCcept;

	private String fuenteFinan;

	private String seccionPptal;

	private String sector;

	private String progCuipo;

	private String codProducto;

	private String codCpc;

	@EJB
	private EjbCGRCero ejbCgrCero;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;



	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ConfigurarPlanPptalCuipoControlador
	 */
	public ConfigurarPlanPptalCuipoControlador() {
		super();
		compania = SessionUtil.getCompania();
		anio = Integer.toString(SysmanFunciones.ano(new Date()));	
		try {
			// 2303
			numFormulario = GeneralCodigoFormaEnum.CONF_PLAN_PPTAL_CUIPO
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
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del
	 * Bean ha sido creado, en este se realizan las asignaciones iniciales
	 * necesarias para la visualizacion del formulario, como son tablas,
	 * origenes de datos, inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {

		enumBase = GenericUrlEnum.PLAN_PPTAL_CONFIG;
		reasignarOrigen();
		buscarLlave();

		registro = new Registro();
		// <CARGAR_LISTA>
		cargarListaAnio();
		cargarListaTerceroChip();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		//cargarListaCodigoCCEPT();
		//cargarListaCodigoCCEPTE();
		cargarListaSector();
		cargarListaSectorE();
		cargarListaProgramaCuipo();
		cargarListaProgramaCuipoE();
		cargarListaVigenciaTesoreria();
		cargarListaVigenciaTesoreriaE();
		cargarListaPoliticaPublica();
		cargarListaPoliticaPublicaE();
		//cargarListaFuenteFinanciacion();
		//cargarListaFuenteFinanciacionE();
		cargarListaVigenciaGasto();
		cargarListaVigenciaGastoE();
		cargarListaTerceroChip();
		cargarListaTerceroChipE();
		cargarListaCodigoProducto();
		cargarListaCodigoProductoE();
		cargarListaCodigoCPC();
		cargarListaCodigoCPCE();
		cargarListaSeccionPresupuestal();
		cargarListaSeccionPresupuestalE();
		cargarListaTipoRecursoSGR();
		cargarListaTipoRecursoSGRE();
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();

		aplicacion = 0;
		vigencia = "";
	}

	/**
	 * Metodo que permite cargar todas las listas de los combos de las columnas
	 */
	public void cargarListas() {

		cargarListaTerceroChip();
		cargarListaSector();
		cargarListaSectorE();
		cargarListaProgramaCuipo();
		cargarListaProgramaCuipoE();
		cargarListaVigenciaTesoreria();
		cargarListaVigenciaTesoreriaE();
		cargarListaPoliticaPublica();
		cargarListaPoliticaPublicaE();
		cargarListaVigenciaGasto();
		cargarListaVigenciaGastoE();
		cargarListaTerceroChip();
		cargarListaTerceroChipE();
		cargarListaCodigoProducto();
		cargarListaCodigoProductoE();
		cargarListaCodigoCPC();
		cargarListaCodigoCPCE();
		cargarListaSeccionPresupuestal();
		cargarListaSeccionPresupuestalE();
	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor
	 * de la consulta del formulario. Tambien carga la lista del formulario por
	 * primera vez
	 */
	@Override
	public void reasignarOrigen() {
		buscarUrls();
		parametrosListado.put("COMPANIA", compania);
		parametrosListado.put("ANO", anio);
		parametrosListado.put("NATURALEZA", naturaleza);
		parametrosListado.put("REGALIAS", regalias);

		try 
		{
			modeloA = ejbSysmanUtil.consultarModeloAno(compania,anio);
		} 
		catch (SystemException e) 
		{
			e.printStackTrace();
		}
	}

	public void activarEdicion(Registro registro) 
	{
		indice = listaInicial.getRowIndex();

		codigoCcept = SysmanFunciones.nvl(registro.getCampos().get("CODIGO_CCEPT"), "").toString();
		fuenteFinan = SysmanFunciones.nvl(registro.getCampos().get("FUENTE_CUIPO"), "").toString();
		seccionPptal = SysmanFunciones.nvl(registro.getCampos().get("SECCION_PPTAL_CUIPO"), "").toString();
		sector = SysmanFunciones.nvl(registro.getCampos().get("SECTOR_CUIPO"), "").toString();
		progCuipo = SysmanFunciones.nvl(registro.getCampos().get("PROGRAMA_CUIPO"), "").toString();
		codProducto = SysmanFunciones.nvl(registro.getCampos().get("CODIGO_PRODUCTO_CUIPO"), "").toString();
		codCpc = SysmanFunciones.nvl(registro.getCampos().get("CODIGO_CPC_CUIPO"), "").toString();

		String [] campos = cargarConfiguracionCuipo(registro.getCampos().get("CODIGO").toString());
		
		if(regalias.equals("R")) {
			bloqSector = false;
			bloqProgCuipo = false;
			bloqCodProducto = false;
			bloqCodCpc = false;
			bloqCodCCEPT = false;
			bloqFuente = false;
			bloqSeccionPptal = false;
		}
		
		if(registro.getCampos().get("CODIGO_CCEPT") != null)
		{
			registro.getCampos().put("CODIGO_CCEPT", codigoCcept);
		}else 
		{
			registro.getCampos().put("CODIGO_CCEPT", campos[0]);
		}
		
		if(registro.getCampos().get("FUENTE_CUIPO") != null)
		{
			registro.getCampos().put("FUENTE_CUIPO", fuenteFinan);
		}else 
		{
			registro.getCampos().put("FUENTE_CUIPO", campos[1]);
		}
	
		if(registro.getCampos().get("SECCION_PPTAL_CUIPO") != null)
		{
			registro.getCampos().put("SECCION_PPTAL_CUIPO", seccionPptal);
		}else
		{
			registro.getCampos().put("SECCION_PPTAL_CUIPO", campos[2]);
		}
		
		if(registro.getCampos().get("SECTOR_CUIPO") != null)
		{
			registro.getCampos().put("SECTOR_CUIPO", sector);
		}else
		{
			registro.getCampos().put("SECTOR_CUIPO", campos[3]);
		}
		
		if(registro.getCampos().get("PROGRAMA_CUIPO") != null)
		{
			registro.getCampos().put("PROGRAMA_CUIPO", progCuipo);	
		}else
		{
			registro.getCampos().put("PROGRAMA_CUIPO", campos[4]);
		}
		
		if(registro.getCampos().get("CODIGO_PRODUCTO_CUIPO") != null)
		{
			registro.getCampos().put("CODIGO_PRODUCTO_CUIPO", codProducto);
		}else
		{
			registro.getCampos().put("CODIGO_PRODUCTO_CUIPO", campos[5]);
		}
		
		if(registro.getCampos().get("CODIGO_CPC_CUIPO") != null)
		{
			registro.getCampos().put("CODIGO_CPC_CUIPO", codCpc);
		}else
		{
			registro.getCampos().put("CODIGO_CPC_CUIPO", campos[6]);
		}
	}

	public String validaClasificadorCcpet() 
	{		
		String codCcept = "";

		if(regalias.equals("R"))
		{
			codCcept = "010";
		}
		else
		{
			codCcept = "006";
		}

		return codCcept;
	}
	/**
	 * cargar lista Modelo  combos cuipo
	 * @throws SystemException 
	 */
	public String[] cargarConfiguracionCuipo(String codigo)
	{ 
		if(modeloA == 3)
		{	
			vigencia = validaVigencia(codigo);

			if(vigencia.equals("VA"))
			{	    
				if(!(codigoCcept.equals("1.1.02.05.001.08.08")
						|| codigoCcept.equals("1.1.02.05.001.08.08.01")
						|| codigoCcept.equals("1.1.02.05.001.08.08.02")
						|| codigoCcept.equals("1.1.02.05.001.08.08.03")))
				{
					bloqDetalleSectorial = true;
				}
				else 
				{
					bloqDetalleSectorial = false;
				}

				String codigoCceptC = "";
				String fuenteFinanC = "";
				String seccionPptalC = "";
				String sectorC = "";
				String progCuipoC = "";
				String codProductoC = "";
				String codCpcC = "";

				Map<String, Object> param = new HashMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put("ANIO", anio);
				param.put("CUENTA", codigo);

				try {
					listaTipoClasificador = RegistroConverter
							.toListRegistro(
									requestManager.getList(
											UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ConfigurarPlanPptalCuipoControladorUrlEnum.URL155105.getValue())
											.getUrl(),
											param));
				} catch (SystemException e) {
					e.printStackTrace();
				}

				for (Registro option : listaTipoClasificador) 
				{
					if(option.getCampos().get("CLASECLASIFICADOR").toString().equals(validaClasificadorCcpet())) 
					{
						codigoCceptC = option.getCampos().get("TIPOCLASIFICADOR").toString();
					};
					if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("009")) 
					{
						fuenteFinanC = option.getCampos().get("TIPOCLASIFICADOR").toString();
					};
					if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("008")) 
					{
						seccionPptalC = option.getCampos().get("TIPOCLASIFICADOR").toString();
					};
					if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("001")) 
					{
						sectorC = option.getCampos().get("TIPOCLASIFICADOR").toString();
					};
					if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("002")) 
					{
						progCuipoC = option.getCampos().get("TIPOCLASIFICADOR").toString();
					};
					if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("004")) 
					{
						codProductoC = option.getCampos().get("TIPOCLASIFICADOR").toString();
					};
					if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("007")) 
					{
						codCpcC = option.getCampos().get("TIPOCLASIFICADOR").toString();
					};
				}

				aplicacion = validaAplicacion(validaClasificadorCcpet());

				if(aplicacion == 0)
				{
					registro.getCampos().put("CODIGO_CCEPT", "0 ");
					bloqCodCCEPT = true;
				}
				else if(aplicacion == 1)
				{
					registro.getCampos().put("CODIGO_CCEPT", codigoCceptC);
					bloqCodCCEPT = true;
				}
				else
				{
					registro.getCampos().put("CODIGO_CCEPT", codigoCcept);
					bloqCodCCEPT = false;
				}

				aplicacion = validaAplicacion("009");

				if(aplicacion == 0)
				{
					registro.getCampos().put("FUENTE_CUIPO", "0 ");
					bloqFuente = true;
				}
				else if(aplicacion == 1)
				{
					registro.getCampos().put("FUENTE_CUIPO", fuenteFinanC);
					bloqFuente = true;
				}
				else
				{
					registro.getCampos().put("FUENTE_CUIPO", fuenteFinan);
					bloqFuente = false;
				}

				aplicacion = validaAplicacion("008");

				if(aplicacion == 0)
				{
					registro.getCampos().put("SECCION_PPTAL_CUIPO", "0 ");
					bloqSeccionPptal = true;
				}
				else if(aplicacion == 1)
				{
					registro.getCampos().put("SECCION_PPTAL_CUIPO", seccionPptalC);
					bloqSeccionPptal = true;
				}
				else
				{
					registro.getCampos().put("SECCION_PPTAL_CUIPO", seccionPptal);
					bloqSeccionPptal = false;
				}

				aplicacion = validaAplicacion("001");

				if(aplicacion == 0)
				{
					registro.getCampos().put("SECTOR_CUIPO", "0 ");
					bloqSector = true;
				}
				else if(aplicacion == 1)
				{
					registro.getCampos().put("SECTOR_CUIPO", sectorC);
					bloqSector = true;
				}
				else
				{
					registro.getCampos().put("SECTOR_CUIPO", sector);
					bloqSector = false;
				}

				aplicacion = validaAplicacion("002");

				if(aplicacion == 0)
				{
					registro.getCampos().put("PROGRAMA_CUIPO", "0 ");
					bloqProgCuipo = true;
				}
				else if(aplicacion == 1)
				{
					registro.getCampos().put("PROGRAMA_CUIPO", progCuipoC);
					bloqProgCuipo = true;
				}
				else
				{
					registro.getCampos().put("PROGRAMA_CUIPO", progCuipo);
					bloqProgCuipo = false;
				}

				aplicacion = validaAplicacion("004");

				if(aplicacion == 0)
				{
					registro.getCampos().put("CODIGO_PRODUCTO_CUIPO", "0 ");
					bloqCodProducto = true;
				}
				else if(aplicacion == 1)
				{
					registro.getCampos().put("CODIGO_PRODUCTO_CUIPO", codProductoC);
					bloqCodProducto = true;
				}
				else
				{
					registro.getCampos().put("CODIGO_PRODUCTO_CUIPO", codProducto);
					bloqCodProducto = false;
				}

				aplicacion = validaAplicacion("007");

				if(aplicacion == 0)
				{
					registro.getCampos().put("CODIGO_CPC_CUIPO", "0 ");
					bloqCodCpc = true;
				}
				else if(aplicacion == 1)
				{
					registro.getCampos().put("CODIGO_CPC_CUIPO", codCpcC);
					bloqCodCpc = true;
				}
				else
				{
					registro.getCampos().put("CODIGO_CPC_CUIPO", codCpc);
					bloqCodCpc = false;
				}
			}
		}

		String [] campos = {SysmanFunciones.nvl(registro.getCampos().get("CODIGO_CCEPT"),"").toString(),
				SysmanFunciones.nvl(registro.getCampos().get("FUENTE_CUIPO"),"").toString(),
				SysmanFunciones.nvl(registro.getCampos().get("SECCION_PPTAL_CUIPO"),"").toString(),
				SysmanFunciones.nvl(registro.getCampos().get("SECTOR_CUIPO"),"").toString(),
				SysmanFunciones.nvl(registro.getCampos().get("PROGRAMA_CUIPO"),"").toString(),
				SysmanFunciones.nvl(registro.getCampos().get("CODIGO_PRODUCTO_CUIPO"),"").toString(),
				SysmanFunciones.nvl(registro.getCampos().get("CODIGO_CPC_CUIPO"),"").toString(),
		};

		return campos;
	}

	public String validaVigencia(String codigo)
	{
		UrlBean url = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarPlanPptalCuipoControladorUrlEnum.URL155103.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.CODIGO.getName(), codigo);

		try 
		{
			Registro regAux = RegistroConverter.toRegistro(
					requestManager.get(url.getUrl(), param));

			vigencia = regAux.getCampos().get("TIPOVIGENCIA").toString();
		}
		catch (SystemException e)
		{
			e.printStackTrace();
		}

		return vigencia;
	}

	public int validaAplicacion(String clasificador)
	{
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put("CLASIFICADOR", clasificador);

		UrlBean url = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarPlanPptalCuipoControladorUrlEnum.URL155104.getValue());

		try 
		{
			Registro regAux = RegistroConverter.toRegistro(
					requestManager.get(url.getUrl(), param));

			aplicacion = (int) regAux.getCampos().get("APLICACION");
		} 
		catch (SystemException e) 
		{
			e.printStackTrace();
		}

		return aplicacion;
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 */
	public void cargarListaAnio() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL7900
							.getValue())
					.getUrl(),
					param));
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaTerceroChip
	 *
	 */
	public void cargarListaTerceroChip() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ConfigurarPlanPptalCuipoControladorUrlEnum.URL8222
						.getValue());

		listaTerceroChip = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "NIT_CEDULA");

	}

	/**
	 * 
	 * Carga la lista listaTerceroChip
	 *
	 */
	public void cargarListaTerceroChipE() {
		listaTerceroChipE = listaTerceroChip;
	}

	/**
	 * 
	 * Carga la lista listaCodigoCCEPT
	 *
	 */
	public void cargarListaCodigoCCEPT() {

		Map<String, Object> param = new TreeMap<>();
		UrlBean urlBean = null;

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		if(modeloA == 3)
		{
			param.put("CLASECLASIFICADOR", validaClasificadorCcpet());

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL155102
							.getValue());
		}
		else
		{
			if(regalias.equals("R")) {

				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								ConfigurarPlanPptalCuipoControladorUrlEnum.URL8688
								.getValue());        	

			}else {

				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								ConfigurarPlanPptalCuipoControladorUrlEnum.URL8687
								.getValue());
			}
		}

		listaCodigoCCEPT = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCodigoCCEPT
	 *
	 */
	public void cargarListaCodigoCCEPTE() {
		listaCodigoCCEPTE = listaCodigoCCEPT;
	}

	/**
	 * 
	 * Carga la lista listaSector
	 *
	 */
	public void cargarListaSector() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean;

		if(modeloA == 3)
		{
			param.put("CLASECLASIFICADOR", "001");

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL155102
							.getValue());
		}
		else
		{
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL9751
							.getValue());
		}

		listaSector = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaSector
	 *
	 */
	public void cargarListaSectorE() {
		listaSectorE = listaSector;
	}

	/**
	 * 
	 * Carga la lista listaProgramaCuipo
	 *
	 */
	public void cargarListaProgramaCuipo() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean;

		if(modeloA == 3)
		{
			param.put("CLASECLASIFICADOR", "002");

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL155102
							.getValue());
		}
		else
		{
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL10801
							.getValue());
		}

		listaProgramaCuipo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaProgramaCuipo
	 *
	 */
	public void cargarListaProgramaCuipoE() {
		listaProgramaCuipoE = listaProgramaCuipo;
	}

	/**
	 * 
	 * Carga la lista listaVigenciaTesoreria
	 *
	 */
	public void cargarListaVigenciaTesoreria() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ConfigurarPlanPptalCuipoControladorUrlEnum.URL11887
						.getValue());
		listaVigenciaTesoreria = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), null,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaVigenciaTesoreria
	 *
	 */
	public void cargarListaVigenciaTesoreriaE() {
		listaVigenciaTesoreriaE = listaVigenciaTesoreria;
	}

	/**
	 * 
	 * Carga la lista listaPoliticaPublica
	 *
	 */
	public void cargarListaPoliticaPublica() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ConfigurarPlanPptalCuipoControladorUrlEnum.URL13301
						.getValue());
		listaPoliticaPublica = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaPoliticaPublica
	 *
	 */
	public void cargarListaPoliticaPublicaE() {
		listaPoliticaPublicaE = listaPoliticaPublica;
	}

	/**
	 * 
	 * Carga la lista listaFuenteFinanciacion
	 *
	 */
	public void cargarListaFuenteFinanciacion() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean;

		if(modeloA == 3)
		{
			param.put("CLASECLASIFICADOR", "009");
			param.put("REGALIAS", regalias);

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL155101
							.getValue());
		}
		else
		{
			if(regalias.equals("R")) {

				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								ConfigurarPlanPptalCuipoControladorUrlEnum.URL15599
								.getValue());
			}else {

				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								ConfigurarPlanPptalCuipoControladorUrlEnum.URL14449
								.getValue());

			}
		}

		listaFuenteFinanciacion = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaFuenteFinanciacion
	 *
	 */
	public void cargarListaFuenteFinanciacionE() {
		listaFuenteFinanciacionE = listaFuenteFinanciacion;
	}

	/**
	 * 
	 * Carga la lista listaVigenciaGasto
	 *
	 */
	public void cargarListaVigenciaGasto() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ConfigurarPlanPptalCuipoControladorUrlEnum.URL15597
						.getValue());
		listaVigenciaGasto = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), null,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaVigenciaGasto
	 *
	 */
	public void cargarListaVigenciaGastoE() {
		listaVigenciaGastoE = listaVigenciaGasto;
	}

	/**
	 * 
	 * Carga la lista listaCodigoProducto
	 *
	 */
	public void cargarListaCodigoProducto() {
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean;

		if(modeloA == 3)
		{
			param.put("CLASECLASIFICADOR", "004");

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL155102
							.getValue());
		}
		else
		{
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL9224
							.getValue());
		}

		listaCodigoProducto = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaCodigoProducto
	 *
	 */
	public void cargarListaCodigoProductoE() {
		listaCodigoProductoE = listaCodigoProducto;
	}

	/**
	 * 
	 * Carga la lista listaCodigoCPC
	 *
	 */
	public void cargarListaCodigoCPC() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean;

		if(modeloA == 3)
		{
			param.put("CLASECLASIFICADOR", "007");

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL155102
							.getValue());
		}
		else
		{
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL16256
							.getValue());
		}

		listaCodigoCPC = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCodigoCPC
	 *
	 */
	public void cargarListaCodigoCPCE() {
		listaCodigoCPCE = listaCodigoCPC;
	}

	/**
	 * 
	 * Carga la lista listaSeccionPresupuestal
	 *
	 */
	public void cargarListaSeccionPresupuestal() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean;

		if(modeloA == 3)
		{
			param.put("CLASECLASIFICADOR", "008");

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL155102
							.getValue());
		}
		else
		{
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ConfigurarPlanPptalCuipoControladorUrlEnum.URL15028
							.getValue());
		}

		listaSeccionPresupuestal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaSeccionPresupuestal
	 *
	 */
	public void cargarListaSeccionPresupuestalE() {
		listaSeccionPresupuestalE = listaSeccionPresupuestal;
	}

	/**
	 * 
	 * Carga la lista listaTipoRecursoSGR
	 *
	 */
	public void cargarListaTipoRecursoSGR(){

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ConfigurarPlanPptalCuipoControladorUrlEnum.URL15598
						.getValue());

		listaTipoRecursoSGR = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaTipoRecursoSGR
	 *
	 */
	public void  cargarListaTipoRecursoSGRE(){

		listaTipoRecursoSGRE = listaTipoRecursoSGR;

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CopiarAnioAnterior en la vista
	 *
	 *
	 */
	public void oprimirCopiarAnioAnterior() {
		// <CODIGO_DESARROLLADO>
		visibleActualizar = true;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConfigurarCodigoCPCDANE en la vista
	 *
	 *
	 * 
	 */
	public void oprimirConfigurarCodigoCPCDANE() {
		// <CODIGO_DESARROLLADO>

		Map<String, Object> parametros = new HashMap<>();

		parametros.put("anio", anio);

		Direccionador direccionador = new Direccionador();
		direccionador.setNumForm(Integer.toString(
				GeneralCodigoFormaEnum.CONFIGURAR_CODIGOS_CUIPO_CONTROLADOR
				.getCodigo()));
		direccionador.setParametros(parametros);

		SessionUtil.redireccionarForma(direccionador,
				SessionUtil.getModulo());
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ActualizarCodigoCPC en la vista
	 *
	 *
	 */
	public void oprimirActualizarCodigoCPC() {

		campoActualizar = "CPC";
		visibleEjecutarProceso = true;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ActualizarCodigoProd en la vista
	 *
	 *
	 */
	public void oprimirActualizarCodigoProd() {
		campoActualizar = "PRODUCTO";
		visibleEjecutarProceso = true;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ActualizarFuente en la vista
	 *
	 *
	 */
	public void oprimirActualizarFuente() {
		campoActualizar = "FUENTE";
		visibleEjecutarProceso = true;

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ActualizarCCEPT en la vista
	 *
	 */
	public void oprimirActualizarCCEPT() {
		try {
			ejbCgrCero.actualizarCodigoCCEPT(compania,
					SessionUtil.getUser().getCodigo());

			JsfUtil.agregarMensajeInformativo(
					idioma.getString(
							"MSM_PROCESO_EJECUTADO"));
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ActualizarBpin en la vista
	 *
	 */
	public void oprimirActualizarBpin() {
		campoActualizar = "CODIGOBPIN";
		visibleEjecutarProceso = true;
	}
	/**	
	 * 
	 * Metodo ejecutado al oprimir el boton ActuClasDetalle
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirActuClasDetalle() {

		//<CODIGO_DESARROLLADO>
		visibleEjecutarProcesoClas =  true;

		//</CODIGO_DESARROLLADO>
	}
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>

	/**
	 * Metodo ejecutado al cambiar el control Regalias
	 * 
	 * 
	 */
	public void cambiarRegalias() {
		// <CODIGO_DESARROLLADO>
		
		reasignarOrigen();
		cargarListas();
		cargarListaCodigoCCEPT();
		cargarListaCodigoCCEPTE();
		cargarListaFuenteFinanciacion();
		cargarListaFuenteFinanciacionE();
		
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Naturaleza
	 * 
	 * 
	 */
	public void cambiarNaturaleza() {
		// <CODIGO_DESARROLLADO>
		reasignarOrigen();
		cargarListas();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Anio
	 * 
	 * 
	 */
	public void cambiarAnio() {
		// <CODIGO_DESARROLLADO>
		reasignarOrigen();
		cargarListas();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control AplicaDestEspecifica
	 * 
	 * 
	 */
	public void cambiarAplicaDestEspecifica() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo actualizar en la
	 * vista
	 *
	 *
	 * 
	 */
	public void aceptaractualizar() {
		try {

			BigDecimal actu = ejbCgrCero.actualizarConfiguracionPptal(compania,
					Integer.parseInt(anio),
					SessionUtil.getUser().getCodigo());

			JsfUtil.agregarMensajeInformativo(
					idioma.getString("TB_TB2912").replace("#$actu$#",
							String.valueOf(actu)));
			visibleActualizar = false;
		}
		catch (NumberFormatException | SystemException e) {
			Logger.getLogger(ConfigurarplanpptalsControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar del dialogo actualizar en
	 * la vista
	 *
	 *
	 * 
	 */
	public void cancelaractualizar() {
		// <CODIGO_DESARROLLADO>
		visibleActualizar = false;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo EjecutarProceso
	 * en la vista
	 *
	 *
	 */
	public void aceptarEjecutarProceso() {
		try {
			ejbCgrCero.actCamposCuipo(compania, Integer.parseInt(anio),
					campoActualizar, SessionUtil.getUser().getCodigo());

			JsfUtil.agregarMensajeInformativo(
					idioma.getString(
							"MSM_PROCESO_EJECUTADO"));

			visibleEjecutarProceso = false;

		}
		catch (NumberFormatException | SystemException e) {
			Logger.getLogger(ConfigurarplanpptalsControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Metodo ejecutado al cambiar el control EjecutarActuDetalleClasi
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarEjecutarActuDetalleClasi() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}



	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar del dialogo EjecutarProceso
	 * en la vista
	 *
	 *
	 */
	public void cancelarEjecutarProceso() {
		visibleEjecutarProceso = false;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo SeleccionTercero
	 * en la vista
	 *
	 *
	 */
	public void aceptarSeleccionTercero() {
		visibleSeleccionTercero = true;
	}


	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar del dialogo
	 * SeleccionTercero en la vista
	 *
	 *
	 */
	public void cancelarSeleccionTercero() {
		listaInicial.getDatasource().get(filaSeleccionada % 10).getCampos()
		.put("TERCERO_CHIP", null);

		visibleSeleccionTercero = false;
	}

	/**
	 * Metodo ejecutado al cambiar el control TerceroChip en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarTerceroChipC(int rowNum) {

		filaSeleccionada = rowNum;

	}

	/**
	 * Metodo ejecutado al cambiar el control AplicaDestEspecifica en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarAplicaDestEspecificaC(int rowNum) {

		if ((boolean) listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.get("APLICA_DEST_ESPECIFICA")) {
			aplicaDestEspecifica = true;
		}
		else {
			aplicaDestEspecifica = false;

			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put("TIPO_NORMA", "");

			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put("NUMERO_NORMA", "");

			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put("FECHA_NORMA", null);

		}
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * del dialogo EjecutarActuDetalleClasi en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void aceptarEjecutarActuDetalleClasi() {
		//         <CODIGO_DESARROLLADO>
		try {
			boolean aux = ejbCgrCero.actualizarClasificadoresPptalCuipo(
					compania,
					Integer.parseInt(anio),
					SessionUtil.getUser().getCodigo());

			if (aux) {
				JsfUtil.agregarMensajeInformativo(
						idioma.getString(
								"MSM_PROCESO_EJECUTADO"));
			}


		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar
	 * del dialogo EjecutarActuDetalleClasi en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void cancelarEjecutarActuDetalleClasi() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCCEPT
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCCEPT(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_CCEPT",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCCEPT
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCCEPTE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaSector
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSector(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("SECTOR_CUIPO",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaSector
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSectorE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaProgramaCuipo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProgramaCuipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("PROGRAMA_CUIPO",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaProgramaCuipo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProgramaCuipoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaVigenciaTesoreria
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaVigenciaTesoreria(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("VIGENCIATESORERIASCHIP",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaVigenciaTesoreria
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaVigenciaTesoreriaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaPoliticaPublica
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPoliticaPublica(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("POLITCA_PUBLICA_CUIPO",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaPoliticaPublica
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPoliticaPublicaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteFinanciacion
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteFinanciacion(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("FUENTE_CUIPO",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteFinanciacion
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteFinanciacionE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaVigenciaGasto
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaVigenciaGasto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("VIGENCIAGASTO",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaVigenciaGasto
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaVigenciaGastoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTerceroChip
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroChip(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("TERCERO_CHIP",
				registroAux.getCampos().get("NIT_CEDULA"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTerceroChip
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroChipE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get("NIT_CEDULA"), "")
				.toString();

		visibleSeleccionTercero = true;
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoProducto
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoProducto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_PRODUCTO_CUIPO",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoProducto
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoProductoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCPC
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCPC(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_CPC_CUIPO",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCPC
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCPCE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSeccionPresupuestal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSeccionPresupuestal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("SECCION_PPTAL_CUIPO",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSeccionPresupuestal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSeccionPresupuestalE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTipoRecursoSGR
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoRecursoSGR(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("TIPO_RECURSO_SGR", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTipoRecursoSGR
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoRecursoSGRE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGO");
	}

	// </METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 * 
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 * 
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro
	 */
	@Override
	public void removerCombos() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().remove("MOVIMIENTO");
		registro.getCampos().remove("NOMBRE");
		registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
		registro.getCampos().remove("CENTRO_COSTO");
		registro.getCampos().remove("TERCERO");
		registro.getCampos().remove("SUCURSAL");
		registro.getCampos().remove("FUENTE_RECURSO");
		registro.getCampos().remove("REFERENCIA");
		registro.getCampos().remove("AUXILIAR");
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable regalias
	 * 
	 * @return regalias
	 */
	public String getRegalias() {
		return regalias;
	}

	/**
	 * Asigna la variable regalias
	 * 
	 * @param regalias
	 * Variable a asignar en regalias
	 */
	public void setRegalias(String regalias) {
		this.regalias = regalias;
	}

	/**
	 * Retorna la variable naturaleza
	 * 
	 * @return naturaleza
	 */
	public String getNaturaleza() {
		return naturaleza;
	}

	/**
	 * Asigna la variable naturaleza
	 * 
	 * @param naturaleza
	 * Variable a asignar en naturaleza
	 */
	public void setNaturaleza(String naturaleza) {
		this.naturaleza = naturaleza;
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
	 * @param anio
	 * Variable a asignar en anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable bloqCodCCEPT
	 * 
	 * @return bloqCodCCEPT
	 */
	public boolean getBloqCodCCEPT() {
		return bloqCodCCEPT;
	}
	/**
	 * Asigna la variable bloqCodCCEPT
	 * 
	 * @param bloqCodCCEPT
	 * Variable a asignar en bloqCodCCEPT
	 */
	public void setBloqCodCCEPT(boolean bloqCodCCEPT) {
		this.bloqCodCCEPT = bloqCodCCEPT;
	}
	/**
	 * Retorna la variable bloqFuente
	 * 
	 * @return bloqFuente
	 */
	public boolean getBloqFuente() {
		return bloqFuente;
	}
	/**
	 * Asigna la variable bloqFuente
	 * 
	 * @param bloqFuente
	 * Variable a asignar en bloqFuente
	 */
	public void setBloqFuente(boolean bloqFuente) {
		this.bloqFuente = bloqFuente;
	}
	/**
	 * Retorna la variable bloqSeccionPptal
	 * 
	 * @return bloqSeccionPptal
	 */
	public boolean getBloqSeccionPptal() {
		return bloqSeccionPptal;
	}
	/**
	 * Asigna la variable bloqSeccionPptal
	 * 
	 * @param bloqSeccionPptal
	 * Variable a asignar en bloqSeccionPptal
	 */
	public void setBloqSeccionPptal(boolean bloqSeccionPptal) {
		this.bloqSeccionPptal = bloqSeccionPptal;
	}
	/**
	 * Retorna la variable bloqSector
	 * 
	 * @return bloqSector
	 */
	public boolean getBloqSector() {
		return bloqSector;
	}
	/**
	 * Asigna la variable bloqSector
	 * 
	 * @param bloqSector
	 * Variable a asignar en bloqSector
	 */
	public void setBloqSector(boolean bloqSector) {
		this.bloqSector = bloqSector;
	}
	/**
	 * Retorna la variable bloqProgCuipo
	 * 
	 * @return bloqProgCuipo
	 */
	public boolean getBloqProgCuipo() {
		return bloqProgCuipo;
	}
	/**
	 * Asigna la variable bloqProgCuipo
	 * 
	 * @param bloqProgCuipo
	 * Variable a asignar en bloqProgCuipo
	 */
	public void setBloqProgCuipo(boolean bloqProgCuipo) {
		this.bloqProgCuipo = bloqProgCuipo;
	}
	/**
	 * Retorna la variable bloqCodProducto
	 * 
	 * @return bloqCodProducto
	 */
	public boolean getBloqCodProducto() {
		return bloqCodProducto;
	}
	/**
	 * Asigna la variable bloqCodProducto
	 * 
	 * @param bloqCodProducto
	 * Variable a asignar en bloqCodProducto
	 */
	public void setBloqCodProducto(boolean bloqCodProducto) {
		this.bloqCodProducto = bloqCodProducto;
	}
	/**
	 * Retorna la variable bloqCodCpc
	 * 
	 * @return bloqCodCpc
	 */
	public boolean getBloqCodCpc() {
		return bloqCodCpc;
	}
	/**
	 * Asigna la variable bloqCodCpc
	 * 
	 * @param bloqCodCpc
	 * Variable a asignar en bloqCodCpc
	 */
	public void setBloqCodCpc(boolean bloqCodCpc) {
		this.bloqCodCpc = bloqCodCpc;
	}
	/**
	 * Retorna la variable bloqDetalleSectorial
	 * 
	 * @return bloqDetalleSectorial
	 */
	public boolean getBloqDetalleSectorial() {
		return bloqDetalleSectorial;
	}
	/**
	 * Asigna la variable bloqDetalleSectorial
	 * 
	 * @param bloqDetalleSectorial
	 * Variable a asignar en bloqDetalleSectorial
	 */
	public void setbloqDetalleSectorial(boolean bloqDetalleSectorial) {
		this.bloqDetalleSectorial = bloqDetalleSectorial;
	}
	/**
	 * Retorna la variable modeloA
	 * 
	 * @return modeloA
	 */
	public int getModeloA() {
		return modeloA;
	}
	/**
	 * Asigna la variable modeloA
	 * 
	 * @param modeloA
	 * Variable a asignar en modeloA
	 */
	public void setModeloA(int modeloA) {
		this.modeloA = modeloA;
	}
	/**
	 * Retorna la variable indice
	 * 
	 * @return indice
	 */
	public int getIndice() {
		return indice;
	}
	/**
	 * Asigna la variable indice
	 * 
	 * @param indice
	 * Variable a asignar en indice
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
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
	 * @param listaAnio
	 * Variable a asignar en listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaCodigoCCEPT
	 * 
	 * @return listaCodigoCCEPT
	 */
	public RegistroDataModelImpl getListaCodigoCCEPT() {
		return listaCodigoCCEPT;
	}

	/**
	 * Asigna la lista listaCodigoCCEPT
	 * 
	 * @param listaCodigoCCEPT
	 * Variable a asignar en listaCodigoCCEPT
	 */
	public void setListaCodigoCCEPT(RegistroDataModelImpl listaCodigoCCEPT) {
		this.listaCodigoCCEPT = listaCodigoCCEPT;
	}

	/**
	 * Retorna la lista listaCodigoCCEPT
	 * 
	 * @return listaCodigoCCEPT
	 */
	public RegistroDataModelImpl getListaCodigoCCEPTE() {
		return listaCodigoCCEPTE;
	}

	/**
	 * Asigna la lista listaCodigoCCEPT
	 * 
	 * @param listaCodigoCCEPT
	 * Variable a asignar en listaCodigoCCEPT
	 */
	public void setListaCodigoCCEPTE(RegistroDataModelImpl listaCodigoCCEPTE) {
		this.listaCodigoCCEPTE = listaCodigoCCEPTE;
	}

	/**
	 * Retorna la lista listaSector
	 * 
	 * @return listaSector
	 */
	public RegistroDataModelImpl getListaSector() {
		return listaSector;
	}

	/**
	 * Asigna la lista listaSector
	 * 
	 * @param listaSector
	 * Variable a asignar en listaSector
	 */
	public void setListaSector(RegistroDataModelImpl listaSector) {
		this.listaSector = listaSector;
	}

	/**
	 * Retorna la lista listaSector
	 * 
	 * @return listaSector
	 */
	public RegistroDataModelImpl getListaSectorE() {
		return listaSectorE;
	}

	/**
	 * Asigna la lista listaSector
	 * 
	 * @param listaSector
	 * Variable a asignar en listaSector
	 */
	public void setListaSectorE(RegistroDataModelImpl listaSectorE) {
		this.listaSectorE = listaSectorE;
	}

	/**
	 * Retorna la lista listaProgramaCuipo
	 * 
	 * @return listaProgramaCuipo
	 */
	public RegistroDataModelImpl getListaProgramaCuipo() {
		return listaProgramaCuipo;
	}

	/**
	 * Asigna la lista listaProgramaCuipo
	 * 
	 * @param listaProgramaCuipo
	 * Variable a asignar en listaProgramaCuipo
	 */
	public void setListaProgramaCuipo(
			RegistroDataModelImpl listaProgramaCuipo) {
		this.listaProgramaCuipo = listaProgramaCuipo;
	}

	/**
	 * Retorna la lista listaProgramaCuipo
	 * 
	 * @return listaProgramaCuipo
	 */
	public RegistroDataModelImpl getListaProgramaCuipoE() {
		return listaProgramaCuipoE;
	}

	/**
	 * Asigna la lista listaProgramaCuipo
	 * 
	 * @param listaProgramaCuipo
	 * Variable a asignar en listaProgramaCuipo
	 */
	public void setListaProgramaCuipoE(
			RegistroDataModelImpl listaProgramaCuipoE) {
		this.listaProgramaCuipoE = listaProgramaCuipoE;
	}

	/**
	 * Retorna la lista listaVigenciaTesoreria
	 * 
	 * @return listaVigenciaTesoreria
	 */
	public RegistroDataModelImpl getListaVigenciaTesoreria() {
		return listaVigenciaTesoreria;
	}

	/**
	 * Asigna la lista listaVigenciaTesoreria
	 * 
	 * @param listaVigenciaTesoreria
	 * Variable a asignar en listaVigenciaTesoreria
	 */
	public void setListaVigenciaTesoreria(
			RegistroDataModelImpl listaVigenciaTesoreria) {
		this.listaVigenciaTesoreria = listaVigenciaTesoreria;
	}

	/**
	 * Retorna la lista listaVigenciaTesoreria
	 * 
	 * @return listaVigenciaTesoreria
	 */
	public RegistroDataModelImpl getListaVigenciaTesoreriaE() {
		return listaVigenciaTesoreriaE;
	}

	/**
	 * Asigna la lista listaVigenciaTesoreria
	 * 
	 * @param listaVigenciaTesoreria
	 * Variable a asignar en listaVigenciaTesoreria
	 */
	public void setListaVigenciaTesoreriaE(
			RegistroDataModelImpl listaVigenciaTesoreriaE) {
		this.listaVigenciaTesoreriaE = listaVigenciaTesoreriaE;
	}

	/**
	 * Retorna la lista listaPoliticaPublica
	 * 
	 * @return listaPoliticaPublica
	 */
	public RegistroDataModelImpl getListaPoliticaPublica() {
		return listaPoliticaPublica;
	}

	/**
	 * Asigna la lista listaPoliticaPublica
	 * 
	 * @param listaPoliticaPublica
	 * Variable a asignar en listaPoliticaPublica
	 */
	public void setListaPoliticaPublica(
			RegistroDataModelImpl listaPoliticaPublica) {
		this.listaPoliticaPublica = listaPoliticaPublica;
	}

	/**
	 * Retorna la lista listaPoliticaPublica
	 * 
	 * @return listaPoliticaPublica
	 */
	public RegistroDataModelImpl getListaPoliticaPublicaE() {
		return listaPoliticaPublicaE;
	}

	/**
	 * Asigna la lista listaPoliticaPublica
	 * 
	 * @param listaPoliticaPublica
	 * Variable a asignar en listaPoliticaPublica
	 */
	public void setListaPoliticaPublicaE(
			RegistroDataModelImpl listaPoliticaPublicaE) {
		this.listaPoliticaPublicaE = listaPoliticaPublicaE;
	}

	/**
	 * Retorna la lista listaFuenteFinanciacion
	 * 
	 * @return listaFuenteFinanciacion
	 */
	public RegistroDataModelImpl getListaFuenteFinanciacion() {
		return listaFuenteFinanciacion;
	}

	/**
	 * Asigna la lista listaFuenteFinanciacion
	 * 
	 * @param listaFuenteFinanciacion
	 * Variable a asignar en listaFuenteFinanciacion
	 */
	public void setListaFuenteFinanciacion(
			RegistroDataModelImpl listaFuenteFinanciacion) {
		this.listaFuenteFinanciacion = listaFuenteFinanciacion;
	}

	/**
	 * Retorna la lista listaFuenteFinanciacion
	 * 
	 * @return listaFuenteFinanciacion
	 */
	public RegistroDataModelImpl getListaFuenteFinanciacionE() {
		return listaFuenteFinanciacionE;
	}

	/**
	 * Asigna la lista listaFuenteFinanciacion
	 * 
	 * @param listaFuenteFinanciacion
	 * Variable a asignar en listaFuenteFinanciacion
	 */
	public void setListaFuenteFinanciacionE(
			RegistroDataModelImpl listaFuenteFinanciacionE) {
		this.listaFuenteFinanciacionE = listaFuenteFinanciacionE;
	}

	/**
	 * Retorna la lista listaVigenciaGasto
	 * 
	 * @return listaVigenciaGasto
	 */
	public RegistroDataModelImpl getListaVigenciaGasto() {
		return listaVigenciaGasto;
	}

	/**
	 * Asigna la lista listaVigenciaGasto
	 * 
	 * @param listaVigenciaGasto
	 * Variable a asignar en listaVigenciaGasto
	 */
	public void setListaVigenciaGasto(
			RegistroDataModelImpl listaVigenciaGasto) {
		this.listaVigenciaGasto = listaVigenciaGasto;
	}

	/**
	 * Retorna la lista listaVigenciaGasto
	 * 
	 * @return listaVigenciaGasto
	 */
	public RegistroDataModelImpl getListaVigenciaGastoE() {
		return listaVigenciaGastoE;
	}

	/**
	 * Asigna la lista listaVigenciaGasto
	 * 
	 * @param listaVigenciaGasto
	 * Variable a asignar en listaVigenciaGasto
	 */
	public void setListaVigenciaGastoE(
			RegistroDataModelImpl listaVigenciaGastoE) {
		this.listaVigenciaGastoE = listaVigenciaGastoE;
	}

	/**
	 * Retorna la lista listaTerceroChip
	 * 
	 * @return listaTerceroChip
	 */
	public RegistroDataModelImpl getListaTerceroChip() {
		return listaTerceroChip;
	}

	/**
	 * Asigna la lista listaTerceroChip
	 * 
	 * @param listaTerceroChip
	 * Variable a asignar en listaTerceroChip
	 */
	public void setListaTerceroChip(RegistroDataModelImpl listaTerceroChip) {
		this.listaTerceroChip = listaTerceroChip;
	}

	/**
	 * Retorna la lista listaTerceroChip
	 * 
	 * @return listaTerceroChip
	 */
	public RegistroDataModelImpl getListaTerceroChipE() {
		return listaTerceroChipE;
	}

	/**
	 * Asigna la lista listaTerceroChip
	 * 
	 * @param listaTerceroChip
	 * Variable a asignar en listaTerceroChip
	 */
	public void setListaTerceroChipE(RegistroDataModelImpl listaTerceroChipE) {
		this.listaTerceroChipE = listaTerceroChipE;
	}

	/**
	 * Retorna la lista listaCodigoProducto
	 * 
	 * @return listaCodigoProducto
	 */
	public RegistroDataModelImpl getListaCodigoProducto() {
		return listaCodigoProducto;
	}

	/**
	 * Asigna la lista listaCodigoProducto
	 * 
	 * @param listaCodigoProducto
	 * Variable a asignar en listaCodigoProducto
	 */
	public void setListaCodigoProducto(
			RegistroDataModelImpl listaCodigoProducto) {
		this.listaCodigoProducto = listaCodigoProducto;
	}

	/**
	 * Retorna la lista listaCodigoProducto
	 * 
	 * @return listaCodigoProducto
	 */
	public RegistroDataModelImpl getListaCodigoProductoE() {
		return listaCodigoProductoE;
	}

	/**
	 * Asigna la lista listaCodigoProducto
	 * 
	 * @param listaCodigoProducto
	 * Variable a asignar en listaCodigoProducto
	 */
	public void setListaCodigoProductoE(
			RegistroDataModelImpl listaCodigoProductoE) {
		this.listaCodigoProductoE = listaCodigoProductoE;
	}

	/**
	 * Retorna la lista listaCodigoCPC
	 * 
	 * @return listaCodigoCPC
	 */
	public RegistroDataModelImpl getListaCodigoCPC() {
		return listaCodigoCPC;
	}

	/**
	 * Asigna la lista listaCodigoCPC
	 * 
	 * @param listaCodigoCPC
	 * Variable a asignar en listaCodigoCPC
	 */
	public void setListaCodigoCPC(RegistroDataModelImpl listaCodigoCPC) {
		this.listaCodigoCPC = listaCodigoCPC;
	}

	/**
	 * Retorna la lista listaCodigoCPC
	 * 
	 * @return listaCodigoCPC
	 */
	public RegistroDataModelImpl getListaCodigoCPCE() {
		return listaCodigoCPCE;
	}

	/**
	 * Asigna la lista listaCodigoCPC
	 * 
	 * @param listaCodigoCPC
	 * Variable a asignar en listaCodigoCPC
	 */
	public void setListaCodigoCPCE(RegistroDataModelImpl listaCodigoCPCE) {
		this.listaCodigoCPCE = listaCodigoCPCE;
	}

	/**
	 * Retorna la lista listaSeccionPresupuestal
	 * 
	 * @return listaSeccionPresupuestal
	 */
	public RegistroDataModelImpl getListaSeccionPresupuestal() {
		return listaSeccionPresupuestal;
	}

	/**
	 * Asigna la lista listaSeccionPresupuestal
	 * 
	 * @param listaSeccionPresupuestal
	 * Variable a asignar en listaSeccionPresupuestal
	 */
	public void setListaSeccionPresupuestal(
			RegistroDataModelImpl listaSeccionPresupuestal) {
		this.listaSeccionPresupuestal = listaSeccionPresupuestal;
	}

	/**
	 * Retorna la lista listaSeccionPresupuestal
	 * 
	 * @return listaSeccionPresupuestal
	 */
	public RegistroDataModelImpl getListaSeccionPresupuestalE() {
		return listaSeccionPresupuestalE;
	}

	/**
	 * Asigna la lista listaSeccionPresupuestal
	 * 
	 * @param listaSeccionPresupuestal
	 * Variable a asignar en listaSeccionPresupuestal
	 */
	public void setListaSeccionPresupuestalE(
			RegistroDataModelImpl listaSeccionPresupuestalE) {
		this.listaSeccionPresupuestalE = listaSeccionPresupuestalE;
	}

	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}

	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar
	 * Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	public boolean isAplicaDestEspecifica() {
		return aplicaDestEspecifica;
	}

	public void setAplicaDestEspecifica(boolean aplicaDestEspecifica) {
		this.aplicaDestEspecifica = aplicaDestEspecifica;
	}

	public boolean isVisibleActualizar() {
		return visibleActualizar;
	}

	public void setVisibleActualizar(boolean visibleActualizar) {
		this.visibleActualizar = visibleActualizar;
	}

	public boolean isVisibleEjecutarProceso() {
		return visibleEjecutarProceso;
	}

	public void setVisibleEjecutarProceso(boolean visibleEjecutarProceso) {
		this.visibleEjecutarProceso = visibleEjecutarProceso;
	}


	public boolean isVisibleEjecutarProcesoClas() {
		return visibleEjecutarProcesoClas;
	}

	public void setVisibleEjecutarProcesoClas(boolean visibleEjecutarProcesoClas) {
		this.visibleEjecutarProcesoClas = visibleEjecutarProcesoClas;
	}

	public boolean isVisibleSeleccionTercero() {
		return visibleSeleccionTercero;
	}

	public void setVisibleSeleccionTercero(boolean visibleSeleccionTercero) {
		this.visibleSeleccionTercero = visibleSeleccionTercero;
	}

	/**
	 * @return the listaTipoRecursoSGR
	 */
	public RegistroDataModelImpl getListaTipoRecursoSGR() {
		return listaTipoRecursoSGR;
	}

	/**
	 * @param listaTipoRecursoSGR the listaTipoRecursoSGR to set
	 */
	public void setListaTipoRecursoSGR(RegistroDataModelImpl listaTipoRecursoSGR) {
		this.listaTipoRecursoSGR = listaTipoRecursoSGR;
	}

	/**
	 * @return the listaTipoRecursoSGRE
	 */
	public RegistroDataModelImpl getListaTipoRecursoSGRE() {
		return listaTipoRecursoSGRE;
	}

	/**
	 * @param listaTipoRecursoSGRE the listaTipoRecursoSGRE to set
	 */
	public void setListaTipoRecursoSGRE(RegistroDataModelImpl listaTipoRecursoSGRE) {
		this.listaTipoRecursoSGRE = listaTipoRecursoSGRE;
	}
	/**
	 * @return the listaTipoClasificador
	 */
	public List<Registro> getListaTipoClasificador() {
		return listaTipoClasificador;
	}
	/**
	 * @param listaTipoClasificador the listaTipoClasificador to set
	 */
	public void setListaTipoClasificador(List<Registro> listaTipoClasificador) {
		this.listaTipoClasificador = listaTipoClasificador;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
}
