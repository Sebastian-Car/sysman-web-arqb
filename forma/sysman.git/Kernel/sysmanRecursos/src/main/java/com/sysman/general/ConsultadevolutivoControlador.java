package com.sysman.general;

import com.sysman.almacen.ejb.EjbAlmacenCeroGeneralRemote;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ConsultadevolutivoControladorEnum;
import com.sysman.general.enums.ConsultadevolutivoControladorUrlEnum;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ngomez
 * @version 1, 26/11/2015
 * 
 * @author jlramirez
 * @version 2, 26/04/2017, Se realizo refactoring y Manejo de EJBs
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de GeneralCodigoFormaEnum,
 *          para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class ConsultadevolutivoControlador extends BeanBaseDatosAcme{

	private final String compania;
	private final String modulo;
	/**
	 * Constante definida para almacenar la cadena "Placa:"
	 */
	private final String cPlaca;
	/**
	 * Constante definida para almacenar la cadena "serie"
	 */
	private final String cSerie;
	private String serieDevolutivo;
	private String orientacion;
	private String tituloCombo;
	private String tituloCombo2;
	private String tituloColumna2;
	private String anchoColumna1;
	private String anchoColumna2;
	private String buscaPor;
	private String formato;
	private String consultaCombo;
	private boolean bloqueaFormato;
	private boolean comboVisible;
	private boolean polizaVisible;
	private boolean manejaNiif;
	private boolean hojaVidaBloqueado;
	private boolean placaAnulada;
	private boolean verificaPlacas;
	private String elemento;
	private String placa;
	private String marca;
	private String serieD;
	private String valor;
	private String nombreLargo;
	private String serieAnterior;
	private String dependencia;
	private String responsable;
	private String numPoliza;
	private String vigenciaDesde;
	private String vigenciaHasta;
	private String aseguradora;
	private String salvamento;
	private String tipoActivo;
	private String estado;
	private String ubicacion;
	private String direccion;
	private String centroCosto;
	private String referenciado;
	private String nombreCentroCosto;
	private String nombreReferenciado;
	/**
	 * Variable para habilitar la visibilidad del campo observacion en el informe y la trazabilidad
	 */
	private boolean manejaObservacion;

	@EJB
	private EjbAlmacenCeroGeneralRemote almacenCero;
	@EJB
	private EjbSysmanUtilRemote sysmanUtil;
	/**
	 * Almacena el nombre de la dependencia asociada al devolutivo.
	 */
	private String nombreDependencia;
	/**
	 * Almacena el nombre del responsable asociado al devolutivo.
	 */
	private String nombreResponsable;
	private String colgaapNiif;
    private String entAplicaNiif;
    private String manNiifAlmacen;
	private StreamedContent archivoDescarga;
	private RegistroDataModelImpl listaSeriedevolutivo1;
	private String manejaUbicacion;
	private boolean visibleAux;
    private boolean manejaVlrLibros;
    private String vidaUtil;
    private String valorCorrecciones;
    private String valorDepreciacion;
    private String valorDeterioro;
    private String valorLibros;
    private String valorAcumulado;
    private Boolean verTrazabilidad = true;
	private String nombreInf;
	private Registro registroTemp;
	private String valorizacion;
    

	/**
	 * Creates a new instance of ConsultadevolutivoControlador
	 */
	public ConsultadevolutivoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		cPlaca = "Placa:";
		cSerie = "Placa";
		numFormulario = GeneralCodigoFormaEnum.CONSULTADEVOLUTIVO_CONTROLADOR.getCodigo();
		Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
		if (parametrosEntrada != null)
		{
			registroTemp = (Registro) parametrosEntrada.get("datos");
		}
		try {
			validarPermisos();
		} catch (Exception ex) {
			SessionUtil.redireccionarMenuPermisos();
			Logger.getLogger(ConsultadevolutivoControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@PostConstruct
	public void inicializar() {
		abrirFormulario();
		
		colgaapNiif = getParametro("EJECUTA COLGAAP y NIIF","NO");
        entAplicaNiif = getParametro("ENTIDAD APLICA NIIF", "NO");
        manNiifAlmacen = getParametro("MANEJA NIIF EN ALMACEN", "NO");
        manejaUbicacion = getParametro("MANEJA DEPENDENCIA-UBICACION","NO");
        visibleAux = "SI".equals(getParametro("MANEJA SALDO POR BODEGA Y AUXILIARES EN ALMACEN", "NO"));
        manejaVlrLibros = "SI".equals(getParametro("GENERAR INFORMES DE DEVOLUTIVOS CON VALOR EN LIBROS","NO"));
        if(registroTemp != null) {
        	cargarDatosDeRegistro(registroTemp);
        }
        
	}

	@Override
	public FormContinuoService getService() {
		return service;
	}

	@Override
	public void setService(FormContinuoService service) {
		this.service = service;
	}
	
	/**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
	@Override
	public void iniciarListasSubNulo(){
	//<CARGAR_LISTAS_SUBFORM_NULL>
	//</CARGAR_LISTAS_SUBFORM_NULL>
	}

	public void cargarListaseriedevolutivo1() {
		UrlBean urlBean = new UrlBean();
		boolean parametro = "SI".equals(getParametro("MANEJA SALDO POR BODEGA Y AUXILIARES EN ALMACEN", "NO"));
		switch (consultaCombo) {
		case "1":
			setAnchoColumna1("0%");
			setAnchoColumna2("50%");
			tituloColumna2 = "Serie Consecutivo";
			if(parametro) {
				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(ConsultadevolutivoControladorUrlEnum.URL3364.getValue());
			}else{
				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(ConsultadevolutivoControladorUrlEnum.URL2489.getValue());
			}
			break;
		case "2":
			setAnchoColumna1("50%");
			setAnchoColumna2("0%");
			tituloColumna2 = "Placa anterior";
			if(parametro) {
				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(ConsultadevolutivoControladorUrlEnum.URL3365.getValue());
			}else{
				urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ConsultadevolutivoControladorUrlEnum.URL3360.getValue());
			}
			break;
		case "3":
			setAnchoColumna1("0%");
			setAnchoColumna2("50%");
			tituloColumna2 = "Serie Consecutivo";
			if(parametro) {
				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(ConsultadevolutivoControladorUrlEnum.URL3366.getValue());
			}else{
				urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ConsultadevolutivoControladorUrlEnum.URL3361.getValue());
			}
			break;
		default:
			break;
		}
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaSeriedevolutivo1 = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.SERIE.getName());

	}

	public void oprimirIniciar() {
		// <CODIGO_DESARROLLADO>
		genInforme(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirComando51() {
		// <CODIGO_DESARROLLADO>
		genInforme(ReportesBean.FORMATOS.EXCEL97);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirRevisarPlacas() {
		// <CODIGO_DESARROLLADO>
		try {
			String textoArchivo = almacenCero.verificarPlaca(compania);
			if (textoArchivo != null) {
				ArchivosBean.generarPlano("Registros_procesados.txt", textoArchivo);
			} else {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1832"));
			}
		} catch (IOException | SystemException ex) {
			Logger.getLogger(ConsultadevolutivoControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
		// </CODIGO_DESARROLLADO>
	}
	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton verTrazabilidad
	 * en la vista
	 *
	 *
	 */
	public void oprimirverTrazabilidad() {
		//<CODIGO_DESARROLLADO>
		Map<String, Object> camposValores = new HashMap<>();
        camposValores.put("placa", placa);
        camposValores.put("datos", registroTemp);
        SessionUtil.setFlash(camposValores);
		SessionUtil.redireccionarFormularioRetorno("10040401","2536","10","10040401","378","10",true);
		//</CODIGO_DESARROLLADO>
	}

	public void genInforme(ReportesBean.FORMATOS formato) {
		archivoDescarga = null;

		String reporte = "";
		try {
			HashMap<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();
			if (polizaVisible) {
				parametros.put("PR_MANEJA_POLIZA", true);
			} else {
				parametros.put("PR_MANEJA_POLIZA", false);
			}
			parametros.put("PR_MANEJA_OBSERVACION", manejaObservacion);
			
			switch (this.formato) {
			case "1":
				if(manejaVlrLibros) {
         		   reporte = "002728cMovDevolutivoPolizasFl";
				} else {
					reporte = "000404cMovimientoDevolutivoPolizas";
				}
				break;
			case "2":
				reporte = "000405cMovimientoDevolutivoporplacaPolizas";
				reemplazar.put("modulo", modulo);

				break;
			case "3":
				reporte = "000407cMovimientoDevolutivoOrgPolizas";
				parametros.put("PR_TITULO", tituloCombo);
				parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
				break;
			default:
				break;
			}
			reemplazar.put(cSerie, placa);
			String parametro = getParametro("DIGITOS AGRUPACION INVENTARIO", "0");
			reemplazar.put("nivelGrupo", Integer.parseInt(parametro));
			
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar,parametros);
			

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (FileNotFoundException e) {
			JsfUtil.agregarMensajeError(
					idioma.getString("MSM_INFORME_VAR_NO_EXISTE").replace("s$reporte$s", reporte) + e.getMessage());
			logger.error(e.getMessage(), e);
		}

		catch (JRException | IOException | SysmanException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	private String getParametro(String nombreParametro, String valorDefault) {
		String parametro = null;
		try {
			parametro = sysmanUtil.consultarParametro(compania, nombreParametro, SessionUtil.getModulo(), new Date(),
					true);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}

	/**
	 * Retorna el titulo del combo Uno, se llama en el metodo cambiarMarco0
	 *
	 * @return validacion
	 */
	public String tituCombo() {
		String validacion = "";
		switch (buscaPor) {
		case "1":
		case "2":
			validacion = cPlaca;
			break;
		case "3":
			validacion = "Placa anterior:";
			break;
		case "4":
			validacion = "Consecutivo:";
			break;
		default:
			break;
		}
		return validacion;
	}

	/**
	 * Retorna el titulo de la primera columna de la grilla del combo ,se llama en
	 * el metodo cambiarMarco0
	 *
	 * @return tituCombo
	 */
	public String tituComboDos() {
		String validacion = "";
		switch (buscaPor) {
		case "1":
			validacion = cSerie;
			break;
		case "3":
			validacion = cSerie;
			break;
		case "4":
			validacion = cSerie;
			break;
		default:
			break;
		}
		return validacion;
	}

	public void cambiarMarco0() {
		// <CODIGO_DESARROLLADO>
		serieDevolutivo = null;
		elemento = null;
		marca = null;
		serieAnterior = null;
		valor = null;
		placa = null;
		serieD = null;
		estado = null;
		placaAnulada = false;
		nombreLargo = null;
		dependencia = null;
		nombreDependencia = null;
		responsable = null;
		nombreResponsable = null;
		tipoActivo = null;
		salvamento = null;
		numPoliza = null;
		aseguradora = null;
		vigenciaDesde = null;
		vigenciaHasta = null;
		centroCosto = null;
		referenciado = null;
		nombreCentroCosto = null;
		nombreReferenciado = null;

		comboVisible = true;
		cambiarformato();
		switch (buscaPor) {
		case "1":
			// Placa
			consultaCombo = "1";
			cargarListaseriedevolutivo1();
			bloqueaFormato = false;
			break;
		case "2":
			// Hoja de vida
			comboVisible = false;
			bloqueaFormato = true;
			orientacion = "";
			break;
		case "3":
			// Placa anterior
			consultaCombo = "2";
			cargarListaseriedevolutivo1();
			bloqueaFormato = false;
			break;
		case "4":
			// Serie-Consecutivo
			consultaCombo = "3";
			cargarListaseriedevolutivo1();
			bloqueaFormato = false;
			break;
		default:
			break;
		}
		tituloCombo = tituCombo();
		tituloCombo2 = tituComboDos();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarformato() {
		// <CODIGO_DESARROLLADO>
		if ("1".equals(formato)) {
			orientacion = idioma.getString("TB_TB1833");
		} else {
			orientacion = idioma.getString("TB_TB1834");
		}
		// </CODIGO_DESARROLLADO>
	}

	public void seleccionarFilaSeriedevolutivo1(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		cargarDatosDeRegistro(registroAux);
		registroTemp = registroAux;
	}

	public void cargarDatosDeRegistro(Registro registroAux) {
		elemento = null;
		marca = null;
		serieAnterior = null;
		valor = null;
		placa = null;
		serieD = null;
		estado = null;
		placaAnulada = false;
		nombreLargo = null;
		dependencia = null;
		nombreDependencia = null;
		responsable = null;
		nombreResponsable = null;
		tipoActivo = null;
		salvamento = null;
		numPoliza = null;
		aseguradora = null;
		vigenciaDesde = null;
		vigenciaHasta = null;
		centroCosto = null;
		referenciado = null;
		nombreCentroCosto = null;
		nombreReferenciado = null;

		serieDevolutivo = SysmanFunciones.nvl(registroAux.getCampos().get("SERIE"), "").toString();
		elemento = SysmanFunciones.nvl(registroAux.getCampos().get("ELEMENTO"), "").toString();
		marca = SysmanFunciones.nvl(registroAux.getCampos().get("MARCA"), "").toString();

		if (colgaapNiif.equals("NO")
				&& entAplicaNiif.equals("NO")
				&& manNiifAlmacen.equals("NO")) {
			serieAnterior = SysmanFunciones.nvl(registroAux.getCampos().get("PLACA_NIIF"), "").toString();
		} else {
			serieAnterior = SysmanFunciones.nvl(registroAux.getCampos().get("SERIEANTERIOR"), "").toString();
		}
		valor = SysmanFunciones.nvl(registroAux.getCampos().get("VALOR"), "0").toString();
		placa = SysmanFunciones.nvl(registroAux.getCampos().get("SERIE1"), "").toString();
		serieD = SysmanFunciones.nvl(registroAux.getCampos().get("SERIEDEVOLUTIVO"), "").toString();
		estado = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBREESTADO"), "").toString();
		placaAnulada = (Boolean) SysmanFunciones.nvl(registroAux.getCampos().get("PLACAANULADA"), true);
		nombreLargo = SysmanFunciones.nvl(registroAux.getCampos().get("TXTNOM"), "").toString();
		dependencia = SysmanFunciones.nvl(registroAux.getCampos().get("DEPENDENCIA"), "").toString();
		responsable = SysmanFunciones.nvl(registroAux.getCampos().get("RESPONSABLE"), "").toString();
		nombreDependencia = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE_DEPENDENCIA"), "").toString();
		nombreResponsable = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE_RESPONSABLE"), "").toString();
		verTrazabilidad = (placa == null || placa.isEmpty());
		if(visibleAux) {
			centroCosto = SysmanFunciones.toString(SysmanFunciones.nvl(registroAux.getCampos().get("CENTRODECOSTO"), ""));
			referenciado = SysmanFunciones.toString(SysmanFunciones.nvl(registroAux.getCampos().get("REFERENCIA_CNT"), ""));
			nombreCentroCosto = SysmanFunciones.toString(SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRECENTRO"), ""));
			nombreReferenciado = SysmanFunciones.toString(SysmanFunciones.nvl(registroAux.getCampos().get("NOMBREREF"), ""));
		}
		if(manejaUbicacion.equals("SI")) {

			ubicacion = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO_UBICACION"), "").toString();
			direccion = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE_UBICACION"), "")
					.toString();
		}else {	

			ubicacion = SysmanFunciones.nvl(registroAux.getCampos().get("UBICACION"), "").toString();
			direccion = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.DIRECCION.getName()), "")
					.toString();
		}

		if (manejaNiif) {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);
			param.put(ConsultadevolutivoControladorEnum.PARAM0.getValue(), placa);

			try {
				List<Registro> aux = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										ConsultadevolutivoControladorUrlEnum.URL3362.getValue())
								.getUrl(),
								param));
				if (!aux.isEmpty()) {
					tipoActivo = SysmanFunciones.nvl(aux.get(0).getCampos().get("NOMBRE_TIPOACTIVO"), "").toString();
					salvamento = SysmanFunciones.nvl(aux.get(0).getCampos().get("SALVAMENTO"), "").toString();
				}
			} catch (SystemException e) {
				Logger.getLogger(SubpcontratosControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
		if (polizaVisible) {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);
			param.put(ConsultadevolutivoControladorEnum.PARAM0.getValue(), placa);

			try {
				List<Registro> aux = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										ConsultadevolutivoControladorUrlEnum.URL3363.getValue())
								.getUrl(),
								param));
				if (!aux.isEmpty()) {
					numPoliza = SysmanFunciones.nvl(aux.get(0).getCampos().get("NUMERO_POLIZA"), "").toString();
					aseguradora = SysmanFunciones.nvl(aux.get(0).getCampos().get("NOMBRE"), "").toString();

					vigenciaDesde = SysmanFunciones.convertirAFechaCadena((Date) aux.get(0).getCampos().get("FECHAI"));
					vigenciaHasta = SysmanFunciones.convertirAFechaCadena((Date) aux.get(0).getCampos().get("FECHAF"));
				}
			} catch (ParseException | SystemException e) {
				Logger.getLogger(SubpcontratosControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}

		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);
			param.put(ConsultadevolutivoControladorEnum.PARAM0.getValue(), placa);


			Registro reg = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ConsultadevolutivoControladorUrlEnum.URL141162.getValue())
							.getUrl(),
							param));

			if(reg != null) {
				vidaUtil = SysmanFunciones.toString(reg.getCampos().get("NIIF_VIDA_UTIL"));
				valorAcumulado = SysmanFunciones.toString(reg.getCampos().get("NIIF_DEPACUMULADA"));
				valorCorrecciones = SysmanFunciones.toString(reg.getCampos().get("VALOR_CORRECION"));
				valorDepreciacion = SysmanFunciones.toString(reg.getCampos().get("NIIF_VLRDEPRECIACION"));
				valorDeterioro = SysmanFunciones.toString(reg.getCampos().get("DETERIORO"));
				valorLibros = SysmanFunciones.toString(reg.getCampos().get("NIIF_VLRLIBROS"));
				valorizacion = SysmanFunciones.toString(reg.getCampos().get("VALORIZACION"));
			}else {
				vidaUtil = "0";
				valorAcumulado = "0";
				valorCorrecciones = "0";
				valorDepreciacion = "0";
				valorDeterioro = "0";
				valorLibros = "0";
				valorizacion = "0";
			}


		} catch (SystemException e) {
			Logger.getLogger(SubpcontratosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public String getSerieDevolutivo() {
		return serieDevolutivo;
	}

	public void setSerieDevolutivo(String serieDevolutivo) {
		this.serieDevolutivo = serieDevolutivo;
	}

	public String getOrientacion() {
		return orientacion;
	}

	public void setOrientacion(String orientacion) {
		this.orientacion = orientacion;
	}

	public RegistroDataModelImpl getListaSeriedevolutivo1() {
		return listaSeriedevolutivo1;
	}

	public void setListaSeriedevolutivo1(RegistroDataModelImpl listaseriedevolutivo1) {
		this.listaSeriedevolutivo1 = listaseriedevolutivo1;
	}

	public String getTituloCombo() {
		return tituloCombo;
	}

	public void setTituloCombo(String tituloCombo) {
		this.tituloCombo = tituloCombo;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	public String getBuscaPor() {
		return buscaPor;
	}

	public void setBuscaPor(String buscaPor) {
		this.buscaPor = buscaPor;
	}

	public boolean isBloqueaFormato() {
		return bloqueaFormato;
	}

	public void setBloqueaFormato(boolean bloqueaFormato) {
		this.bloqueaFormato = bloqueaFormato;
	}

	public boolean isComboVisible() {
		return comboVisible;
	}

	public void setComboVisible(boolean comboVisible) {
		this.comboVisible = comboVisible;
	}

	public boolean isPolizaVisible() {
		return polizaVisible;
	}

	public void setPolizaVisible(boolean polizaVisible) {
		this.polizaVisible = polizaVisible;
	}

	public boolean isHojaVidaBloqueado() {
		return hojaVidaBloqueado;
	}

	public void setHojaVidaBloqueado(boolean hojaVidaBloqueado) {
		this.hojaVidaBloqueado = hojaVidaBloqueado;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public String getConsultaCombo() {
		return consultaCombo;
	}

	public void setConsultaCombo(String consultaCombo) {
		this.consultaCombo = consultaCombo;
	}

	public boolean isPlacaAnulada() {
		return placaAnulada;
	}

	public void setPlacaAnulada(boolean placaAnulada) {
		this.placaAnulada = placaAnulada;
	}

	public String getElemento() {
		return elemento;
	}

	public void setElemento(String elemento) {
		this.elemento = elemento;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getSerieD() {
		return serieD;
	}

	public void setSerieD(String serieD) {
		this.serieD = serieD;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getNombreLargo() {
		return nombreLargo;
	}

	public void setNombreLargo(String nombreLargo) {
		this.nombreLargo = nombreLargo;
	}

	public String getSerieAnterior() {
		return serieAnterior;
	}

	/**
	 * Trae el nombre de dependencia.
	 *
	 * @return Nombre de la dependencia.
	 */
	public String getNombreDependencia() {
		return nombreDependencia;
	}

	/**
	 * Asigna el valor a la variable nombreDependencia.
	 *
	 * @param nombreDependencia Nombre de la dependencia
	 */
	public void setNombreDependencia(String nombreDependencia) {
		this.nombreDependencia = nombreDependencia;
	}

	/**
	 * Trae el nombre del responsable
	 *
	 * @return nombre del responsable
	 */
	public String getNombreResponsable() {
		return nombreResponsable;
	}

	/**
	 * Asigna el valor a la variable nombreResponsable
	 *
	 * @param nombreResponsable Nombre del responsable.
	 */
	public void setNombreResponsable(String nombreResponsable) {
		this.nombreResponsable = nombreResponsable;
	}

	public void setSerieAnterior(String serieAnterior) {
		this.serieAnterior = serieAnterior;
	}

	public String getDependencia() {
		return dependencia;
	}

	public void setDependencia(String dependencia) {
		this.dependencia = dependencia;
	}

	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	public String getNumPoliza() {
		return numPoliza;
	}

	public void setNumPoliza(String numPoliza) {
		this.numPoliza = numPoliza;
	}

	public String getVigenciaDesde() {
		return vigenciaDesde;
	}

	public void setVigenciaDesde(String vigenciaDesde) {
		this.vigenciaDesde = vigenciaDesde;
	}

	public String getVigenciaHasta() {
		return vigenciaHasta;
	}

	public void setVigenciaHasta(String vigenciaHasta) {
		this.vigenciaHasta = vigenciaHasta;
	}

	public String getAseguradora() {
		return aseguradora;
	}

	public void setAseguradora(String aseguradora) {
		this.aseguradora = aseguradora;
	}

	public String getSalvamento() {
		return salvamento;
	}

	public void setSalvamento(String salvamento) {
		this.salvamento = salvamento;
	}

	public String getTipoActivo() {
		return tipoActivo;
	}

	public void setTipoActivo(String tipoActivo) {
		this.tipoActivo = tipoActivo;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public boolean isManejaNiif() {
		return manejaNiif;
	}

	public void setManejaNiif(boolean manejaNiif) {
		this.manejaNiif = manejaNiif;
	}

	public String getTituloCombo2() {
		return tituloCombo2;
	}

	public void setTituloCombo2(String tituloCombo2) {
		this.tituloCombo2 = tituloCombo2;
	}

	public boolean isVerificaPlacas() {
		return verificaPlacas;
	}

	public void setVerificaPlacas(boolean verificaPlacas) {
		this.verificaPlacas = verificaPlacas;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		buscaPor = "1";
		tituloCombo = cPlaca;
		tituloCombo2 = "Placa";
		formato = "1";
		orientacion = idioma.getString("TB_TB1833");
		bloqueaFormato = false;
		comboVisible = true;
		Object aux;
		Object aux1;
		try {
			aux = sysmanUtil.consultarParametro(compania, "MANEJA POLIZAS", modulo, new Date(), true);
			if (aux == null) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1835"));
				polizaVisible = false;
			} else {
				polizaVisible = "SI".equals(aux.toString());
			}

			aux1 = sysmanUtil.consultarParametro(compania, "MANEJA NIIF EN ALMACEN", modulo, new Date(), true);
			if (aux1 == null) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1836"));
				manejaNiif = false;
			} else {
				manejaNiif = "SI".equals(aux1.toString());
			}
			
			manejaObservacion = "SI".equals(SysmanFunciones
 					.nvl(sysmanUtil.consultarParametro(compania, "MOSTRAR CAMPO OBSERVACION EN INFORMES CONSULTA POR PLACA",
 							modulo, new Date(), true), "NO"));
		} catch (SystemException ex) {
			Logger.getLogger(ConsultadevolutivoControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
		verificaPlacas = false;
		hojaVidaBloqueado = !"800.005.900-9".equals(SessionUtil.getCompaniaIngreso().getNit());
		consultaCombo = "1";
		cargarListaseriedevolutivo1();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * @return the tituloColumna2
	 */
	public String getTituloColumna2() {
		return tituloColumna2;
	}

	/**
	 * @param tituloColumna2 the tituloColumna2 to set
	 */
	public void setTituloColumna2(String tituloColumna2) {
		this.tituloColumna2 = tituloColumna2;
	}

	/**
	 * @return the anchoColumna2
	 */
	public String getAnchoColumna2() {
		return anchoColumna2;
	}

	/**
	 * @param anchoColumna2 the anchoColumna2 to set
	 */
	public void setAnchoColumna2(String anchoColumna2) {
		this.anchoColumna2 = anchoColumna2;
	}

	/**
	 * @return the anchoColumna1
	 */
	public String getAnchoColumna1() {
		return anchoColumna1;
	}

	/**
	 * @param anchoColumna1 the anchoColumna1 to set
	 */
	public void setAnchoColumna1(String anchoColumna1) {
		this.anchoColumna1 = anchoColumna1;
	}

	/**
	 * @return the centroCosto
	 */
	public String getCentroCosto() {
		return centroCosto;
	}
	
	public String getNombreCentroCosto() {
		return nombreCentroCosto;
	}


	/**
	 * @param centroCosto the centroCosto to set
	 */
	public void setCentroCosto(String centroCosto) {
		this.centroCosto = centroCosto;
	}
	public void setNombreCentroCosto(String nombreCentroCosto) {
		this.nombreCentroCosto = nombreCentroCosto;
	}

	/**
	 * @return the referenciado
	 */
	public String getReferenciado() {
		return referenciado;
	}
	
	public String getNombreReferenciado() {
		return nombreReferenciado;
	}

	/**
	 * @param referenciado the referenciado to set
	 */
	public void setReferenciado(String referenciado) {
		this.referenciado = referenciado;
	}
	
	public void setNombreReferenciado(String nombrReferenciado) {
		this.nombreReferenciado = nombreReferenciado;
	}

	/**
	 * @return the visibleAux
	 */
	public boolean isVisibleAux() {
		return visibleAux;
	}

	/**
	 * @param visibleAux the visibleAux to set
	 */
	public void setVisibleAux(boolean visibleAux) {
		this.visibleAux = visibleAux;
	}

	/**
	 * @return the vidaUtil
	 */
	public String getVidaUtil() {
		return vidaUtil;
	}

	/**
	 * @param vidaUtil the vidaUtil to set
	 */
	public void setVidaUtil(String vidaUtil) {
		this.vidaUtil = vidaUtil;
	}

	/**
	 * @return the valorCorrecciones
	 */
	public String getValorCorrecciones() {
		return valorCorrecciones;
	}

	/**
	 * @param valorCorrecciones the valorCorrecciones to set
	 */
	public void setValorCorrecciones(String valorCorrecciones) {
		this.valorCorrecciones = valorCorrecciones;
	}

	/**
	 * @return the valorDepreciacion
	 */
	public String getValorDepreciacion() {
		return valorDepreciacion;
	}

	/**
	 * @param valorDepreciacion the valorDepreciacion to set
	 */
	public void setValorDepreciacion(String valorDepreciacion) {
		this.valorDepreciacion = valorDepreciacion;
	}

	/**
	 * @return the valorDeterioro
	 */
	public String getValorDeterioro() {
		return valorDeterioro;
	}

	/**
	 * @param valorDeterioro the valorDeterioro to set
	 */
	public void setValorDeterioro(String valorDeterioro) {
		this.valorDeterioro = valorDeterioro;
	}

	/**
	 * @return the valorLibros
	 */
	public String getValorLibros() {
		return valorLibros;
	}

	/**
	 * @param valorLibros the valorLibros to set
	 */
	public void setValorLibros(String valorLibros) {
		this.valorLibros = valorLibros;
	}

	/**
	 * @return the valorAcumulado
	 */
	public String getValorAcumulado() {
		return valorAcumulado;
	}

	/**
	 * @param valorAcumulado the valorAcumulado to set
	 */
	public void setValorAcumulado(String valorAcumulado) {
		this.valorAcumulado = valorAcumulado;
	}

	/**
	 * @return the verTrazabilidad
	 */
	public Boolean getVerTrazabilidad() {
		return verTrazabilidad;
	}

	/**
	 * @param verTrazabilidad the verTrazabilidad to set
	 */
	public void setVerTrazabilidad(Boolean verTrazabilidad) {
		this.verTrazabilidad = verTrazabilidad;
	}
	
	

	/**
	 * @return the nombreInf
	 */
	public String getNombreInf() {
		return nombreInf;
	}

	/**
	 * @param nombreInf the nombreInf to set
	 */
	public void setNombreInf(String nombreInf) {
		this.nombreInf = nombreInf;
	}

	/**
	 * @return the valorizacion
	 */
	public String getValorizacion() {
		return valorizacion;
	}

	/**
	 * @param valorizacion the valorizacion to set
	 */
	public void setValorizacion(String valorizacion) {
		this.valorizacion = valorizacion;
	}

	@Override
	public void cargarRegistro() {
	}

	@Override
	public void iniciarListasSub() {
	}

	@Override
	public void iniciarListas() {
	}

	@Override
	public void reasignarOrigenGrilla() {
	}

	@Override
	public void asignarOrigenDatos() {
	}

	@Override
	public boolean insertarAntes() {
		return false;
	}

	@Override
	public boolean insertarDespues() {
		return false;
	}

	@Override
	public boolean actualizarAntes() {
		return false;
	}

	@Override
	public boolean actualizarDespues() {
		return false;
	}

	@Override
	public boolean eliminarAntes() {
		return false;
	}

	@Override
	public boolean eliminarDespues() {
		return false;
	}

	public boolean isManejaObservacion() {
		return manejaObservacion;
	}

	public void setManejaObservacion(boolean manejaObservacion) {
		this.manejaObservacion = manejaObservacion;
	}
    
}
