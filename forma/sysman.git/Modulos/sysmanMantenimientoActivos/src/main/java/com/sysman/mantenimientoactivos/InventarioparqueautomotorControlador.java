package com.sysman.mantenimientoactivos;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.mantenimientoactivos.ejb.EjbMantenimientoActivosCeroRemote;
import com.sysman.mantenimientoactivos.enums.InventarioparqueautomotorControladorEnum;
import com.sysman.mantenimientoactivos.enums.InventarioparqueautomotorControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ngomez
 * @version 1, 22/09/2015
 *
 * @author spina
 * @version 2, 16/08/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class InventarioparqueautomotorControlador extends BeanBaseDatosAcmeImpl {

	private final String compania;
	private final String modulo;
	private final String usuario;
	private final String txtopcion;

	private List<Registro> listaMunicipiodeplacas;
	private List<Registro> listaTipo;
	private List<Registro> listaMarca;
	private List<Registro> listaAsignacion;
	private List<Registro> listaLugarParqueo;
	private List<Registro> listaCmbCentroCosto;
	private List<Registro> listaSerieelemento;
	private List<Registro> listaPais;
	private List<Registro> listaDepartamento;
	private List<Registro> listaCodProcedencia;
	private List<Registro> listaPlacasAnteriores;
	private List<Registro> listaTipodecombustible;
	private List<Registro> listaParteFuncional;
	private List<Registro> listaEscojerScanner;
	private List<Registro> listaCuadrocombinado161;
	private List<Registro> listaVehiculoseguro;
	private List<Registro> listaVehiculotanque;
	private List<Registro> listaSecundario141;
	private List<Registro> listaSubvehiculopartes;
	private List<Registro> listaImagenvehiculos;
	private List<Registro> listaSecundario160;
	private List<Registro> listaFotosaccidentes;
	private RegistroDataModelImpl listaNIT;
	private RegistroDataModelImpl listaNITE;
	private String auxiliar;
	private RegistroDataModelImpl listaNumeroPoliza;
	private RegistroDataModelImpl listaNumeroPolizaE;
	private RegistroDataModelImpl listaCondAccid;
	private RegistroDataModelImpl listaCondAccidE;
	private RegistroDataModelImpl listaConductor;
	private RegistroDataModelImpl listaCodigoelemento;
	private RegistroDataModelImpl listaEntidadcomodataria;
	private RegistroDataModelImpl listaAseguradora;
	private RegistroDataModelImpl listaAseguradoraE;
	private String codigoElemento;
	private String opcion;
	private String registroauxSucursalAccidentalidad;
	private String registroauxSucursalAseguradora;
	private String registroauxSucursalSeguro;
	private String registroauxNombreSeguro;
	private String registroauxSucursalGases;
	private String registroauxNombreGases;
	private Registro registroSubVehiculoSeguro;
	private Registro registroSubVehiculoTanque;
	private Registro registroSubSecundario141;
	private Registro registroSubSubVehiculoPartes;
	private Registro registroSubImagenVehiculos;
	private Registro registroSubSecundario160;
	private Registro registroSubfotosaccidentes;
	private StreamedContent archivoDescarga;
	private String parametroVehiculo = "RUTA DIGITALIZADO VEHICULOS";
	private String parametroAccidentes = "RUTA DIGITALIZADO ACCIDENTES";
	private String nombreConductorAux;
	private String nombreComodatariaAux;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	@EJB
	private EjbMantenimientoActivosCeroRemote ejbMantenimientoActivosCero;

	@SuppressWarnings("unchecked")
	public InventarioparqueautomotorControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		usuario = SessionUtil.getUser().getCodigo();
		txtopcion = "opcion";
		try {
			numFormulario = GeneralCodigoFormaEnum.INVENTARIOPARQUEAUTOMOTOR_CONTROLADOR.getCodigo();
			validarPermisos();
			registro = new Registro(new HashMap<String, Object>());
			registroSubVehiculoSeguro = new Registro(new HashMap<String, Object>());
			registroSubVehiculoTanque = new Registro(new HashMap<String, Object>());
			registroSubSecundario141 = new Registro(new HashMap<String, Object>());
			registroSubSubVehiculoPartes = new Registro(new HashMap<String, Object>());
			registroSubImagenVehiculos = new Registro(new HashMap<String, Object>());
			registroSubSecundario160 = new Registro(new HashMap<String, Object>());
			registroSubfotosaccidentes = new Registro(new HashMap<String, Object>());

			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			SessionUtil.cleanFlash();
			if (parametrosEntrada != null) {
				rid = (Map<String, Object>) parametrosEntrada.get("rid");
				opcion = SysmanFunciones.nvl(parametrosEntrada.get(txtopcion), "1").toString();
			}
		} catch (Exception ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {
		tabla = InventarioparqueautomotorControladorEnum.INVENTARIO_PARQUE_AUTOMOTOR.getValue();
		enumBase = GenericUrlEnum.INVENTARIO_PARQUE_AUTOMOTOR;
		buscarLlave();
		asignarOrigenDatos();
	}

	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(InventarioparqueautomotorControladorEnum.PARESTADO.getValue(), opcion);
		parametrosListado.put("ESAUTOMOTOR", -1);
	}

	public void cargarListaAseguradora() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17912.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaAseguradora = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				InventarioparqueautomotorControladorEnum.NITASEGURADORA.getValue());
	}

	public void cargarListaAseguradoraE() {
		listaAseguradoraE = listaAseguradora;
	}

	public void cargarListaVehiculoseguro() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		try {
			listaVehiculoseguro = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17913.getValue())
									.getUrl(),
							param),
					CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
							InventarioparqueautomotorControladorEnum.VEHICULOSEGURO.getValue()));
		} catch (SystemException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaVehiculotanque() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PLACA.getName(), registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		try {
			listaVehiculotanque = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17914.getValue())
									.getUrl(),
							param),
					CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
							InventarioparqueautomotorControladorEnum.VEHICULOTANQUE.getValue()));
		} catch (SystemException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaSecundario141() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PLACA.getName(), registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		try {
			listaSecundario141 = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17915.getValue())
									.getUrl(),
							param),
					CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
							InventarioparqueautomotorControladorEnum.VEHICULOGASES.getValue()));

		} catch (SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaSubvehiculopartes() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PLACA.getName(), registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		try {
			listaSubvehiculopartes = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17916.getValue())
									.getUrl(),
							param),
					CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
							InventarioparqueautomotorControladorEnum.VEHICULO_PARTES.getValue()));
		} catch (SysmanException | SystemException e) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaImagenvehiculos() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PLACA.getName(), registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		param.put(InventarioparqueautomotorControladorEnum.SEPARADOR.getValue(), File.separator);
		try {
			listaImagenvehiculos = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17917.getValue())
									.getUrl(),
							param),
					CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
							InventarioparqueautomotorControladorEnum.FOTOSVEHICULOS.getValue()));
		} catch (SystemException | SysmanException e) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaSecundario160() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PLACA.getName(), registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		try {
			listaSecundario160 = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17918.getValue())
									.getUrl(),
							param),
					CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
							InventarioparqueautomotorControladorEnum.ACCIDENTALIDAD.getValue()));
		} catch (SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaFotosaccidentes() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PLACA.getName(), registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		param.put(InventarioparqueautomotorControladorEnum.SEPARADOR.getValue(), File.separator);
		try {
			listaFotosaccidentes = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17919.getValue())
									.getUrl(),
							param),
					CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
							InventarioparqueautomotorControladorEnum.FOTOSACCIDENTES.getValue()));
		} catch (SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaMunicipiodeplacas() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
				registro.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()));
		param.put(InventarioparqueautomotorControladorEnum.PAIS.getValue(),
				registro.getCampos().get(InventarioparqueautomotorControladorEnum.PAIS.getValue()));
		try {
			listaMunicipiodeplacas = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17920.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cargarListaTipo() {
		try {
			listaTipo = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17921.getValue())
									.getUrl(),
							null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cargarListaMarca() {
		try {
			listaMarca = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17922.getValue())
									.getUrl(),
							null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaAsignacion() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAsignacion = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17923.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaLugarParqueo() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaLugarParqueo = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17924.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaCmbCentroCosto() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));
		try {
			listaCmbCentroCosto = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17925.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cargarListaSerieelemento() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ELEMENTO.getName(), registro.getCampos().get("CODIGO_ELEMENTO"));
		try {
			listaSerieelemento = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17926.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaPais() {
		try {
			listaPais = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17927.getValue())
									.getUrl(),
							null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaDepartamento() {
		Map<String, Object> param = new TreeMap<>();
		param.put(InventarioparqueautomotorControladorEnum.PAIS.getValue(), registro.getCampos().get("PAIS"));
		try {
			listaDepartamento = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17928.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaCodProcedencia() {
		try {
			listaCodProcedencia = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17929.getValue())
									.getUrl(),
							null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaPlacasAnteriores() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaPlacasAnteriores = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17930.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaTipodecombustible() {
		try {
			listaTipodecombustible = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17931.getValue())
									.getUrl(),
							null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaParteFuncional() {
		try {
			listaParteFuncional = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17932.getValue())
									.getUrl(),
							null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaNIT() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17933.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaNIT = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				InventarioparqueautomotorControladorEnum.NIT.getValue());
	}

	public void cargarListaNITE() {
		listaNITE = listaNIT;
	}

	public void cargarListaNumeroPoliza() {
		listaNumeroPoliza = listaNIT;
	}

	public void cargarListaNumeroPolizaE() {
		listaNumeroPolizaE = listaNumeroPoliza;
	}

	public void cargarListaCondAccid() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17934.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaCondAccid = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				InventarioparqueautomotorControladorEnum.NIT.getValue());
	}

	public void cargarListaCondAccidE() {
		listaCondAccidE = listaCondAccid;
	}

	public void cargarListaConductor() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17935.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaConductor = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				InventarioparqueautomotorControladorEnum.NIT.getValue());
	}

	public void cargarListaCodigoelemento() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17936.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaCodigoelemento = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGOELEMENTO.getName());
	}

	public void cargarListaEntidadcomodataria() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17937.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaEntidadcomodataria = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, InventarioparqueautomotorControladorEnum.NIT.getValue());
	}

	public void agregarRegistroSubVehiculoseguro() {
		try {
			if ((registroSubVehiculoSeguro.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()) != null)
					&& (registroSubVehiculoSeguro.getCampos().get(GeneralParameterEnum.FECHAINICIAL.getName()) != null)
					&& ((Date) registroSubVehiculoSeguro.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()))
							.before((Date) registroSubVehiculoSeguro.getCampos()
									.get(GeneralParameterEnum.FECHAINICIAL.getName()))) {
				JsfUtil.agregarMensajeAlerta(
						idioma.getString(InventarioparqueautomotorControladorEnum.TB_TB49.getValue()));
				cargarListaVehiculoseguro();
				return;
			}

			registroSubVehiculoSeguro.getCampos()
					.remove(InventarioparqueautomotorControladorEnum.FECHAINICIALM.getValue());
			registroSubVehiculoSeguro.getCampos()
					.remove(InventarioparqueautomotorControladorEnum.FECHAFINALM.getValue());
			registroSubVehiculoSeguro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSubVehiculoSeguro.getCampos().put(GeneralParameterEnum.PLACA.getName(),
					registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));

			registroSubVehiculoSeguro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			registroSubVehiculoSeguro.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17938.getValue());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubVehiculoSeguro.getCampos());
			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_INGRESADO));

		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		registroSubVehiculoSeguro.setCampos(new HashMap<String, Object>());
		cargarListaVehiculoseguro();
	}

	public void editarRegSubVehiculoseguro(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().remove(InventarioparqueautomotorControladorEnum.FECHAINICIALM.getValue());
			reg.getCampos().remove(InventarioparqueautomotorControladorEnum.FECHAFINALM.getValue());
			reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			reg.getCampos().remove(GeneralParameterEnum.PLACA.getName());
			reg.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			if ((reg.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()) != null)
					&& (reg.getCampos().get(GeneralParameterEnum.FECHAINICIAL.getName()) != null)
					&& ((Date) reg.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()))
							.before((Date) reg.getCampos().get(GeneralParameterEnum.FECHAINICIAL.getName()))) {
				cargarListaVehiculoseguro();
				JsfUtil.agregarMensajeAlerta(
						idioma.getString(InventarioparqueautomotorControladorEnum.TB_TB49.getValue()));
				return;

			}

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17939.getValue());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_MODIFICADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaVehiculoseguro();
	}

	public void eliminarRegSubVehiculoseguro(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17940.getValue());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_ELIMINADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaVehiculoseguro();
	}

	public void cancelarEdicionVehiculoseguro() {
		cargarListaVehiculoseguro();
		cargarListaVehiculotanque();
		cargarListaSecundario141();
		cargarListaSubvehiculopartes();
		cargarListaImagenvehiculos();
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
	}

	public String sumaTanque() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PLACA.getName(),
				String.valueOf(registro.getCampos().get(GeneralParameterEnum.PLACA.getName())));

		String suma = "0";
		try {

			List<Registro> regAux = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17959.getValue())
									.getUrl(),
							param));

			suma = new BigDecimal(SysmanFunciones.nvl(regAux.get(0).getCampos().get("SUMA"), "0").toString())
					.toString();

			Registro reg = new Registro();
			reg.getCampos().put(InventarioparqueautomotorControladorEnum.CAPTANQUECOMBUSTIBLE.getValue(), suma);
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
			reg.getLlave().put("KEY_COMPANIA", compania);
			reg.getLlave().put("KEY_PLACA",
					String.valueOf(registro.getCampos().get(GeneralParameterEnum.PLACA.getName())));

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17960.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}

		return suma;
	}

	public void agregarRegistroSubVehiculotanque() {
		try {
			registroSubVehiculoTanque.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSubVehiculoTanque.getCampos().put(GeneralParameterEnum.PLACA.getName(),
					registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
			registroSubVehiculoTanque.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			registroSubVehiculoTanque.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17941.getValue());

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubVehiculoTanque.getCampos());

			registro.getCampos().put(InventarioparqueautomotorControladorEnum.CAPTANQUECOMBUSTIBLE.getValue(),
					sumaTanque());
			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_INGRESADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		registroSubVehiculoTanque.setCampos(new HashMap<String, Object>());
		cargarListaVehiculotanque();
	}

	public void editarRegSubVehiculotanque(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17942.getValue());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			registro.getCampos().put(InventarioparqueautomotorControladorEnum.CAPTANQUECOMBUSTIBLE.getValue(),
					sumaTanque());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_MODIFICADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaVehiculotanque();
	}

	public void eliminarRegSubVehiculotanque(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17943.getValue());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			registro.getCampos().put(InventarioparqueautomotorControladorEnum.CAPTANQUECOMBUSTIBLE.getValue(),
					sumaTanque());
			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_ELIMINADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaVehiculotanque();
	}

	public void cancelarEdicionVehiculotanque() {
		cargarListaVehiculotanque();
		cargarListaSecundario141();
		cargarListaSubvehiculopartes();
		cargarListaImagenvehiculos();
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
	}

	public void agregarRegistroSubSecundario141() {
		try {
			registroSubSecundario141.getCampos()
					.remove(InventarioparqueautomotorControladorEnum.FECHAINICIALM.getValue());
			registroSubSecundario141.getCampos()
					.remove(InventarioparqueautomotorControladorEnum.FECHAFINALM.getValue());
			registroSubSecundario141.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSubSecundario141.getCampos().put(GeneralParameterEnum.PLACA.getName(),
					registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
			registroSubSecundario141.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			registroSubSecundario141.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			if ((registroSubSecundario141.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()) != null)
					&& (registroSubSecundario141.getCampos().get(GeneralParameterEnum.FECHAINICIAL.getName()) != null)
					&& ((Date) registroSubSecundario141.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()))
							.before((Date) registroSubSecundario141.getCampos()
									.get(GeneralParameterEnum.FECHAINICIAL.getName()))) {
				cargarListaSecundario141();
				JsfUtil.agregarMensajeAlerta(
						idioma.getString(InventarioparqueautomotorControladorEnum.TB_TB49.getValue()));
				return;
			}

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17944.getValue());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubSecundario141.getCampos());
			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_INGRESADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		registroSubSecundario141.setCampos(new HashMap<String, Object>());
		cargarListaSecundario141();
	}

	public void editarRegSubSecundario141(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().remove(InventarioparqueautomotorControladorEnum.FECHAINICIALM.getValue());
			reg.getCampos().remove(InventarioparqueautomotorControladorEnum.FECHAFINALM.getValue());
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			if ((reg.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()) != null)
					&& (reg.getCampos().get(GeneralParameterEnum.FECHAINICIAL.getName()) != null)
					&& ((Date) reg.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()))
							.before((Date) reg.getCampos().get(GeneralParameterEnum.FECHAINICIAL.getName()))) {
				JsfUtil.agregarMensajeAlerta(
						idioma.getString(InventarioparqueautomotorControladorEnum.TB_TB49.getValue()));
				cargarListaSecundario141();
				return;
			}

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17945.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_MODIFICADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}

		cargarListaSecundario141();
	}

	public void eliminarRegSubSecundario141(Registro reg) {
		try {

			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17946.getValue());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_ELIMINADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaSecundario141();
	}

	public void cancelarEdicionSecundario141() {
		cargarListaSecundario141();
		cargarListaSubvehiculopartes();
		cargarListaImagenvehiculos();
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
	}

	public void agregarRegistroSubSubvehiculopartes() {
		try {
			registroSubSubVehiculoPartes.getCampos().remove("NOMBRESTADO");
			registroSubSubVehiculoPartes.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
			registroSubSubVehiculoPartes.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSubSubVehiculoPartes.getCampos().put(GeneralParameterEnum.PLACA.getName(),
					registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
			registroSubSubVehiculoPartes.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			registroSubSubVehiculoPartes.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17947.getValue());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubSubVehiculoPartes.getCampos());
			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_INGRESADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		registroSubSubVehiculoPartes.setCampos(new HashMap<String, Object>());
		cargarListaSubvehiculopartes();
	}

	public void editarRegSubSubvehiculopartes(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().remove("NOMBRESTADO");
			reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
			reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			reg.getCampos().remove(GeneralParameterEnum.PLACA.getName());
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17948.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_MODIFICADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaSubvehiculopartes();
	}

	public void eliminarRegSubSubvehiculopartes(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17949.getValue());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_ELIMINADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaSubvehiculopartes();
	}

	public void cancelarEdicionSubvehiculopartes() {
		cargarListaSubvehiculopartes();
		cargarListaImagenvehiculos();
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
	}

	public void agregarRegistroSubImagenvehiculos() throws SystemException {
		registroSubImagenVehiculos.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registroSubImagenVehiculos.getCampos().put(InventarioparqueautomotorControladorEnum.REGISTRO.getValue(),
				registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		registroSubImagenVehiculos.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(),
				SysmanFunciones.getParteFecha(new Date(), Calendar.YEAR));
		registroSubImagenVehiculos.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
		registroSubImagenVehiculos.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

		UrlBean urlCreate = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17950.getValue());
		requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubImagenVehiculos.getCampos());

		JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_INGRESADO));
		registroSubImagenVehiculos.setCampos(new HashMap<String, Object>());
		cargarListaImagenvehiculos();
	}

	public void editarRegSubImagenvehiculos(RowEditEvent event) {
		/* Metodo no implementado */
	}

	public void eliminarRegSubImagenvehiculos(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17951.getValue());
			reg.getLlave().put(InventarioparqueautomotorControladorEnum.KEY_ARCHIVO.getValue(),
					reg.getCampos().get(InventarioparqueautomotorControladorEnum.RUTA.getValue()));
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			File aux = new File(SysmanFunciones
					.nvl(reg.getCampos().get(InventarioparqueautomotorControladorEnum.RUTA.getValue()), "").toString());
			if (aux.delete()) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2490"));
			} else {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB2491"));
			}
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaImagenvehiculos();
	}

	public void cancelarEdicionImagenvehiculos() {
		cargarListaImagenvehiculos();
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
	}

	public void cambiarCondAccidC(int indice) {
		/* metodo no implementado */
	}

	public void agregarRegistroSubSecundario160() {
		try {
			registroSubSecundario160.getCampos().remove("FECHA_ACCIDENTEM");
			registroSubSecundario160.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSubSecundario160.getCampos().put(GeneralParameterEnum.PLACA.getName(),
					registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
			registroSubSecundario160.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			registroSubSecundario160.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17952.getValue());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubSecundario160.getCampos());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_INGRESADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		registroSubSecundario160.setCampos(new HashMap<String, Object>());
		cargarListaSecundario160();
	}

	public void editarRegSubSecundario160(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().remove("FECHA_ACCIDENTEM");
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17953.getValue());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());
			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_MODIFICADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaSecundario160();
	}

	public void eliminarRegSubSecundario160(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17954.getValue());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_ELIMINADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaSecundario160();
	}

	public void cancelarEdicionSecundario160() {
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
	}

	public void agregarRegistroSubFotosaccidentes() throws SystemException {
		registroSubfotosaccidentes.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registroSubfotosaccidentes.getCampos().put(InventarioparqueautomotorControladorEnum.REGISTRO.getValue(),
				registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		registroSubfotosaccidentes.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(),
				SysmanFunciones.getParteFecha(new Date(), Calendar.YEAR));
		registroSubfotosaccidentes.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
		registroSubfotosaccidentes.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

		UrlBean urlCreate = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17955.getValue());
		requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubfotosaccidentes.getCampos());
		registroSubfotosaccidentes.setCampos(new HashMap<String, Object>());
		cargarListaFotosaccidentes();
	}

	public void editarRegSubFotosaccidentes(RowEditEvent event) {
		/* Metodo no implementado */
	}

	public void eliminarRegSubFotosaccidentes(Registro reg) {
		try {

			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17956.getValue());

			reg.getLlave().put(InventarioparqueautomotorControladorEnum.KEY_ARCHIVO.getValue(),
					reg.getCampos().get(InventarioparqueautomotorControladorEnum.RUTA.getValue()));

			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			File aux = new File(SysmanFunciones
					.nvl(reg.getCampos().get(InventarioparqueautomotorControladorEnum.RUTA.getValue()), "").toString());
			if (aux.delete()) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2490"));
			} else {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB2491"));
			}
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaFotosaccidentes();
	}

	public void cancelarEdicionFotosaccidentes() {
		cargarListaFotosaccidentes();
	}

	public void oprimirTipoBoton() {
		// <CODIGO_DESARROLLADO>
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);
		if (css != null) {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("rid", css);
			parametros.put(txtopcion, opcion);
			SessionUtil.setFlash(parametros);
		}

		SessionUtil.cargarModalDatosModal(
				Integer.toString(GeneralCodigoFormaEnum.TIPODEAUTOMOTORS_CONTROLADOR.getCodigo()), modulo);
		// </CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirMarcaBoton() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);
		if (css != null) {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("rid", css);
			parametros.put(txtopcion, opcion);
			SessionUtil.setFlash(parametros);
		}

		SessionUtil.cargarModalDatosModal(Integer.toString(GeneralCodigoFormaEnum.MARCA_CONTROLADOR.getCodigo()),
				modulo);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirLugarParqueoBoton() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);
		Direccionador direccionador = new Direccionador();
		if (css != null) {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("rid", css);
			parametros.put(txtopcion, opcion);
			direccionador.setParametros(parametros);
		}
		direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.LUGARPARQUEOS_CONTROLADOR.getCodigo()));
		SessionUtil.redireccionarForma(direccionador, modulo);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirBtnHerramientas() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);
		Direccionador direccionador = new Direccionador();
		Map<String, Object> parametros = new HashMap<>();
		if (css != null) {
			parametros.put("rid", css);
		}

		parametros.put(txtopcion, opcion);
		parametros.put(GeneralParameterEnum.PLACA.getName(),
				String.valueOf(registro.getCampos().get(GeneralParameterEnum.PLACA.getName())));
		direccionador.setParametros(parametros);
		direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.VEHICULOACCESORIOS_CONTROLADOR.getCodigo()));
		SessionUtil.redireccionarForma(direccionador, modulo);

		// </CODIGO_DESARROLLADO>
	}

	public void oprimirTipoCombustibleBoton() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);
		if (css != null) {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("rid", css);
			parametros.put(txtopcion, opcion);
			SessionUtil.setFlash(parametros);
		}

		SessionUtil.cargarModalDatosModal(
				Integer.toString(GeneralCodigoFormaEnum.TIPODECOMBUSTIBLES_CONTROLADOR.getCodigo()), modulo);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirCmdCopiarModelo(ActionEvent ac) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirCargarRuta1(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirAbrir(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirComando12(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirCmdScaner(ActionEvent ac) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirCargarRuta12(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirAbrir2(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirComando122(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirComando166(ActionEvent ac) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarPais() {
		// <CODIGO_DESARROLLADO>
		cargarListaDepartamento();
		cargarListaMunicipiodeplacas();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarDepartamento() {
		// <CODIGO_DESARROLLADO>
		cargarListaMunicipiodeplacas();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarNumeroPoliza() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarLugarParqueo() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("SUCURSAL_PARQUEO",
				service.buscarEnLista(registro.getCampos().get("LUGAR_PARQUEO").toString(),
						GeneralParameterEnum.CODIGO.getName(), "SUCURSAL_CODIGO", listaLugarParqueo));
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarFechaFinalC(int rowNum) {
		listaSecundario160.get(rowNum).getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registroauxSucursalAccidentalidad);
	}

	public void cambiarAseguradoraC(int rowNum) {
		listaSecundario160.get(rowNum).getCampos().put("SUCURSAL_ASEGURADORA", registroauxSucursalAseguradora);
	}

	public void cambiarNITC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		listaVehiculoseguro.get(rowNum).getCampos().put(GeneralParameterEnum.NOMBRE.getName(), registroauxNombreSeguro);
		listaVehiculoseguro.get(rowNum).getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registroauxSucursalSeguro);
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarNumeroPolizaC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		listaSecundario141.get(rowNum).getCampos().put("NOMBRECENTRO", registroauxNombreGases);
		listaSecundario141.get(rowNum).getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registroauxSucursalGases);
		// </CODIGO_DESARROLLADO>
	}

	public void FILAFilaNIT(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubVehiculoSeguro.getCampos().put(InventarioparqueautomotorControladorEnum.NIT.getValue(),
				registroAux.getCampos().get(InventarioparqueautomotorControladorEnum.NIT.getValue()));
		registroSubVehiculoSeguro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
		registroSubVehiculoSeguro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
	}

	public void seleccionarFilaNITE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get(InventarioparqueautomotorControladorEnum.NIT.getValue()), "")
				.toString();
		registroauxNombreSeguro = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
		registroauxSucursalSeguro = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "").toString();
	}

	public void seleccionarFilaNumeroPoliza(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubSecundario141.getCampos().put("NUMERODOCUMENTO",
				registroAux.getCampos().get(InventarioparqueautomotorControladorEnum.NIT.getValue()));
		registroSubSecundario141.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		registroSubSecundario141.getCampos().put("NOMBRECENTRO",
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}

	public void seleccionarFilaNumeroPolizaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get(InventarioparqueautomotorControladorEnum.NIT.getValue()), "")
				.toString();
		registroauxNombreGases = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
		registroauxSucursalGases = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "").toString();
	}

	public void seleccionarFilaCondAccid(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubSecundario160.getCampos().put(InventarioparqueautomotorControladorEnum.CONDUCTOR.getValue(),
				registroAux.getCampos().get(InventarioparqueautomotorControladorEnum.NIT.getValue()));
		registroSubSecundario160.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
	}

	public void seleccionarFilaCondAccidE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get(InventarioparqueautomotorControladorEnum.NIT.getValue()), "")
				.toString();
		registroauxSucursalAccidentalidad = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "").toString();
	}

	public void seleccionarFilaConductor(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(InventarioparqueautomotorControladorEnum.CONDUCTOR.getValue(),
				registroAux.getCampos().get(InventarioparqueautomotorControladorEnum.NIT.getValue()));
		registro.getCampos().put(InventarioparqueautomotorControladorEnum.NOMBRECONDUCTOR.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
		registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
	}

	public void seleccionarFilaCodigoelemento(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_ELEMENTO",
				registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()));
		codigoElemento = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()), "").toString();
		cargarListaSerieelemento();
	}

	public void seleccionarFilaEntidadcomodataria(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("ENTIDAD_COMODATARIA",
				registroAux.getCampos().get(InventarioparqueautomotorControladorEnum.NIT.getValue()));
		registro.getCampos().put(InventarioparqueautomotorControladorEnum.NOMBRECOMODATARIA.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
		registro.getCampos().put("SUCURSAL_COMODATARIA",
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
	}

	public void seleccionarFilaAseguradora(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubSecundario160.getCampos().put("ASEGURADORA",
				registroAux.getCampos().get(InventarioparqueautomotorControladorEnum.NITASEGURADORA.getValue()));
		registroSubSecundario160.getCampos().put("SUCURSAL_ASEGURADORA",
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
	}

	public void seleccionarFilaAseguradoraE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get(InventarioparqueautomotorControladorEnum.NITASEGURADORA.getValue()),
						"")
				.toString();
		registroauxSucursalAseguradora = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "").toString();
	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public void iniciarListasSubNulo() {
		listaVehiculoseguro = null;
		listaVehiculotanque = null;
		listaSecundario141 = null;
		listaSubvehiculopartes = null;
		listaImagenvehiculos = null;
		listaSecundario160 = null;
		listaFotosaccidentes = null;
	}

	@Override
	public void iniciarListasSub() {
		cargarListaVehiculoseguro();
		cargarListaVehiculotanque();
		cargarListaSecundario141();
		cargarListaSubvehiculopartes();
		cargarListaImagenvehiculos();
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
		cargarListaDepartamento();
		cargarListaMunicipiodeplacas();
		cargarListaSerieelemento();
	}

	@Override
	public void iniciarListas() {
		cargarListaNIT();
		cargarListaNITE();
		cargarListaNumeroPoliza();
		cargarListaNumeroPolizaE();
		cargarListaCondAccid();
		cargarListaCondAccidE();
		cargarListaAseguradora();
		cargarListaAseguradoraE();
		cargarListaConductor();
		cargarListaCodigoelemento();
		cargarListaEntidadcomodataria();

		cargarListaTipo();
		cargarListaMarca();
		cargarListaAsignacion();
		cargarListaLugarParqueo();
		cargarListaCmbCentroCosto();

		cargarListaPais();
		cargarListaDepartamento();
		cargarListaMunicipiodeplacas();
		cargarListaSerieelemento();
		cargarListaCodProcedencia();
		cargarListaPlacasAnteriores();
		cargarListaTipodecombustible();
		cargarListaParteFuncional();
	}

	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		nombreConductorAux = SysmanFunciones
				.nvl(registro.getCampos().get(InventarioparqueautomotorControladorEnum.NOMBRECONDUCTOR.getValue()), "")
				.toString();
		nombreComodatariaAux = SysmanFunciones
				.nvl(registro.getCampos().get(InventarioparqueautomotorControladorEnum.NOMBRECOMODATARIA.getValue()),
						"")
				.toString();
		registro.getCampos().put("ES_AUTOMOTOR", -1);
		registro.getCampos().remove(InventarioparqueautomotorControladorEnum.NOMBRECONDUCTOR.getValue());
		registro.getCampos().remove(InventarioparqueautomotorControladorEnum.NOMBRECOMODATARIA.getValue());
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);

		return actualizarAntes();

		// </CODIGO_DESARROLLADO>
	}

	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("ES_AUTOMOTOR", -1);
		registro.getCampos().put(InventarioparqueautomotorControladorEnum.NOMBRECONDUCTOR.getValue(),
				nombreConductorAux);
		registro.getCampos().put(InventarioparqueautomotorControladorEnum.NOMBRECOMODATARIA.getValue(),
				nombreComodatariaAux);

		try {
			ejbMantenimientoActivosCero.insertarParteFuncional(compania,
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.PLACA.getName()), "").toString(),
					usuario);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
		registro = new Registro(new HashMap<String, Object>());
		return true;
	}

	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		if (ACCION_MODIFICAR.equals(accion)) {
			registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			registro.getCampos().remove(InventarioparqueautomotorControladorEnum.NOMBRECONDUCTOR.getValue());
			registro.getCampos().remove(InventarioparqueautomotorControladorEnum.NOMBRECOMODATARIA.getValue());
			registro.getCampos().remove(".ES_AUTOMOTOR");
			registro.getCampos().remove(GeneralParameterEnum.PLACA.getName());

		} else {

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(InventarioparqueautomotorControladorEnum.SERIE_ELEMENTO.getValue(),
					registro.getCampos().get(InventarioparqueautomotorControladorEnum.SERIE_ELEMENTO.getValue()));
			param.put("ES_AUTOMOTOR", -1);

			try {
				Registro reg = RegistroConverter.toRegistro(requestManager.get(
						UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										InventarioparqueautomotorControladorUrlEnum.URL17961.getValue())
								.getUrl(),
						param));

				if (reg != null) {
					JsfUtil.agregarMensajeError(idioma.getString("TB_TB3861")
							.replace("s$serie$s", registro.getCampos()
									.get(InventarioparqueautomotorControladorEnum.SERIE_ELEMENTO.getValue()).toString())
							.replace("s$placa$s",
									reg.getCampos().get(GeneralParameterEnum.PLACA.getName()).toString()));
					return false;
				}
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}

		registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.ESTADO.getName()), "A"));
		registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(), SysmanFunciones
				.nvl(registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()), "99999999999999999999"));

		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	public void retornarFormularioTipoBoton() {
		// <CODIGO_DESARROLLADO>
		cargarListaTipo();
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormularioMarcaBoton() {
		// <CODIGO_DESARROLLADO>
		cargarListaMarca();
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormularioTipoCombustibleBoton() {
		// <CODIGO_DESARROLLADO>
		cargarListaTipodecombustible();
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirDescargar(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		File file = new File(SysmanFunciones
				.nvl(reg.getCampos().get(InventarioparqueautomotorControladorEnum.RUTA.getValue()), "").toString());

		try (FileInputStream filein = new FileInputStream(file)) {
			byte[] vec = new byte[(int) file.length()];
			filein.read(vec, 0, vec.length);
			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(vec),
					SysmanFunciones
							.nvl(reg.getCampos().get(InventarioparqueautomotorControladorEnum.ARCHIVO.getValue()), "")
							.toString());
		} catch (IOException | JRException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeAlerta(ex.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirDescargar2(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		File file = new File(SysmanFunciones
				.nvl(reg.getCampos().get(InventarioparqueautomotorControladorEnum.RUTA.getValue()), "").toString());
		try (FileInputStream filein = new FileInputStream(file)) {
			byte[] vec = new byte[(int) file.length()];
			filein.read(vec, 0, vec.length);
			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(vec),
					SysmanFunciones
							.nvl(reg.getCampos().get(InventarioparqueautomotorControladorEnum.ARCHIVO.getValue()), "")
							.toString());
		} catch (IOException | JRException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cargarArchivoLector(FileUploadEvent event) {
		// <CODIGO_DESARROLLADO>
		try {
			String ruta = generarRuta(parametroVehiculo,
					String.valueOf(registro.getCampos().get(GeneralParameterEnum.PLACA.getName())));
			if (ruta != null) {
				String nombreArch = event.getFile().getFileName();
				nombreArch = nombreArch.contains(File.separator)
						? nombreArch.substring(nombreArch.lastIndexOf(File.separator) + 1, nombreArch.length())
						: nombreArch;
				registroSubImagenVehiculos.getCampos().put(InventarioparqueautomotorControladorEnum.ARCHIVO.getValue(),
						SysmanFunciones.concatenar(ruta, nombreArch));
				agregarRegistroSubImagenvehiculos();
				JsfUtil.upload(event.getFile().getInputstream(), nombreArch, ruta);
			} else {
				JsfUtil.agregarMensajeAlerta(
						idioma.getString("TB_TB2492").replace("#$parametrovehiculo$#", parametroVehiculo));
			}
		} catch (IOException | SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cargarArchivoLector2(FileUploadEvent event) {
		// <CODIGO_DESARROLLADO>
		try {
			String ruta = generarRuta(parametroAccidentes,
					String.valueOf(registro.getCampos().get(GeneralParameterEnum.PLACA.getName())));

			if (ruta != null) {
				String nombreArch = event.getFile().getFileName();
				nombreArch = nombreArch.contains(File.separator)
						? nombreArch.substring(nombreArch.lastIndexOf(File.separator) + 1, nombreArch.length())
						: nombreArch;
				registroSubfotosaccidentes.getCampos().put(InventarioparqueautomotorControladorEnum.ARCHIVO.getValue(),
						SysmanFunciones.concatenar(ruta, nombreArch));
				agregarRegistroSubFotosaccidentes();
				JsfUtil.upload(event.getFile().getInputstream(), nombreArch, ruta);
			} else {
				JsfUtil.agregarMensajeAlerta(
						idioma.getString("TB_TB2493").replace("#$parametroaccidentes$#", parametroAccidentes));
			}
		} catch (IOException | SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public String generarRuta(String parametro, String documento) {
		String strRuta;
		String placa = documento.replace(" ", "");
		String aux = null;
		try {
			aux = ejbSysmanUtil.consultarParametro(compania, parametro, modulo, new Date(), true);
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		if (aux != null) {
			if (!aux.substring(aux.length() - 1, aux.length()).equals(File.separator)) {
				aux = SysmanFunciones.concatenar(aux, File.separator);
			}
			strRuta = SysmanFunciones.concatenar(String.valueOf(aux), placa, File.separator);

			File folder = new File(strRuta);
			folder.mkdirs();
			return strRuta;
		} else {
			return null;
		}
	}

	public Registro getRegistroSubVehiculoSeguro() {
		return registroSubVehiculoSeguro;
	}

	public void setRegistroSubVehiculoSeguro(Registro registroSubVehiculoSeguro) {
		this.registroSubVehiculoSeguro = registroSubVehiculoSeguro;
	}

	public Registro getRegistroSubVehiculoTanque() {
		return registroSubVehiculoTanque;
	}

	public void setRegistroSubVehiculoTanque(Registro registroSubVehiculoTanque) {
		this.registroSubVehiculoTanque = registroSubVehiculoTanque;
	}

	public Registro getRegistroSubSecundario141() {
		return registroSubSecundario141;
	}

	public void setRegistroSubSecundario141(Registro registroSubSecundario141) {
		this.registroSubSecundario141 = registroSubSecundario141;
	}

	public Registro getRegistroSubSubVehiculoPartes() {
		return registroSubSubVehiculoPartes;
	}

	public void setRegistroSubSubVehiculoPartes(Registro registroSubSubVehiculoPartes) {
		this.registroSubSubVehiculoPartes = registroSubSubVehiculoPartes;
	}

	public Registro getRegistroSubImagenVehiculos() {
		return registroSubImagenVehiculos;
	}

	public void setRegistroSubImagenVehiculos(Registro registroSubImagenVehiculos) {
		this.registroSubImagenVehiculos = registroSubImagenVehiculos;
	}

	public Registro getRegistroSubSecundario160() {
		return registroSubSecundario160;
	}

	public void setRegistroSubSecundario160(Registro registroSubSecundario160) {
		this.registroSubSecundario160 = registroSubSecundario160;
	}

	public Registro getRegistroSubfotosaccidentes() {
		return registroSubfotosaccidentes;
	}

	public void setRegistroSubfotosaccidentes(Registro registroSubfotosaccidentes) {
		this.registroSubfotosaccidentes = registroSubfotosaccidentes;
	}

	public List<Registro> getListaMunicipiodeplacas() {
		return listaMunicipiodeplacas;
	}

	public void setListaMunicipiodeplacas(List<Registro> listaMunicipiodeplacas) {
		this.listaMunicipiodeplacas = listaMunicipiodeplacas;
	}

	public List<Registro> getListaTipo() {
		return listaTipo;
	}

	public void setListaTipo(List<Registro> listaTipo) {
		this.listaTipo = listaTipo;
	}

	public List<Registro> getListaMarca() {
		return listaMarca;
	}

	public void setListaMarca(List<Registro> listaMarca) {
		this.listaMarca = listaMarca;
	}

	public List<Registro> getListaAsignacion() {
		return listaAsignacion;
	}

	public void setListaAsignacion(List<Registro> listaAsignacion) {
		this.listaAsignacion = listaAsignacion;
	}

	public List<Registro> getListaLugarParqueo() {
		return listaLugarParqueo;
	}

	public void setListaLugarParqueo(List<Registro> listaLugarParqueo) {
		this.listaLugarParqueo = listaLugarParqueo;
	}

	public List<Registro> getListaCmbCentroCosto() {
		return listaCmbCentroCosto;
	}

	public void setListaCmbCentroCosto(List<Registro> listaCmbCentroCosto) {
		this.listaCmbCentroCosto = listaCmbCentroCosto;
	}

	public List<Registro> getListaSerieelemento() {
		return listaSerieelemento;
	}

	public void setListaSerieelemento(List<Registro> listaSerieelemento) {
		this.listaSerieelemento = listaSerieelemento;
	}

	public List<Registro> getListaPais() {
		return listaPais;
	}

	public void setListaPais(List<Registro> listaPais) {
		this.listaPais = listaPais;
	}

	public List<Registro> getListaDepartamento() {
		return listaDepartamento;
	}

	public void setListaDepartamento(List<Registro> listaDepartamento) {
		this.listaDepartamento = listaDepartamento;
	}

	public List<Registro> getListaCodProcedencia() {
		return listaCodProcedencia;
	}

	public void setListaCodProcedencia(List<Registro> listaCodProcedencia) {
		this.listaCodProcedencia = listaCodProcedencia;
	}

	public List<Registro> getListaPlacasAnteriores() {
		return listaPlacasAnteriores;
	}

	public void setListaPlacasAnteriores(List<Registro> listaPlacasAnteriores) {
		this.listaPlacasAnteriores = listaPlacasAnteriores;
	}

	public List<Registro> getListaTipodecombustible() {
		return listaTipodecombustible;
	}

	public void setListaTipodecombustible(List<Registro> listaTipodecombustible) {
		this.listaTipodecombustible = listaTipodecombustible;
	}

	public List<Registro> getListaParteFuncional() {
		return listaParteFuncional;
	}

	public void setListaParteFuncional(List<Registro> listaParteFuncional) {
		this.listaParteFuncional = listaParteFuncional;
	}

	public List<Registro> getListaEscojerScanner() {
		return listaEscojerScanner;
	}

	public void setListaEscojerScanner(List<Registro> listaEscojerScanner) {
		this.listaEscojerScanner = listaEscojerScanner;
	}

	public List<Registro> getListaCuadrocombinado161() {
		return listaCuadrocombinado161;
	}

	public void setListaCuadrocombinado161(List<Registro> listaCuadrocombinado161) {
		this.listaCuadrocombinado161 = listaCuadrocombinado161;
	}

	public List<Registro> getListaVehiculoseguro() {
		return listaVehiculoseguro;
	}

	public void setListaVehiculoseguro(List<Registro> listaVehiculoseguro) {
		this.listaVehiculoseguro = listaVehiculoseguro;
	}

	public List<Registro> getListaVehiculotanque() {
		return listaVehiculotanque;
	}

	public void setListaVehiculotanque(List<Registro> listaVehiculotanque) {
		this.listaVehiculotanque = listaVehiculotanque;
	}

	public List<Registro> getListaSecundario141() {
		return listaSecundario141;
	}

	public void setListaSecundario141(List<Registro> listaSecundario141) {
		this.listaSecundario141 = listaSecundario141;
	}

	public List<Registro> getListaSubvehiculopartes() {
		return listaSubvehiculopartes;
	}

	public void setListaSubvehiculopartes(List<Registro> listaSubvehiculopartes) {
		this.listaSubvehiculopartes = listaSubvehiculopartes;
	}

	public List<Registro> getListaImagenvehiculos() {
		return listaImagenvehiculos;
	}

	public void setListaImagenvehiculos(List<Registro> listaImagenvehiculos) {
		this.listaImagenvehiculos = listaImagenvehiculos;
	}

	public List<Registro> getListaSecundario160() {
		return listaSecundario160;
	}

	public void setListaSecundario160(List<Registro> listaSecundario160) {
		this.listaSecundario160 = listaSecundario160;
	}

	public List<Registro> getListaFotosaccidentes() {
		return listaFotosaccidentes;
	}

	public void setListaFotosaccidentes(List<Registro> listaFotosaccidentes) {
		this.listaFotosaccidentes = listaFotosaccidentes;
	}

	public RegistroDataModelImpl getListaNIT() {
		return listaNIT;
	}

	public void setListaNIT(RegistroDataModelImpl listaNIT) {
		this.listaNIT = listaNIT;
	}

	public RegistroDataModelImpl getListaNITE() {
		return listaNITE;
	}

	public void setListaNITE(RegistroDataModelImpl listaNITE) {
		this.listaNITE = listaNITE;
	}

	public String getAuxiliar() {
		return auxiliar;
	}

	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	public RegistroDataModelImpl getListaNumeroPoliza() {
		return listaNumeroPoliza;
	}

	public void setListaNumeroPoliza(RegistroDataModelImpl listaNumeroPoliza) {
		this.listaNumeroPoliza = listaNumeroPoliza;
	}

	public RegistroDataModelImpl getListaNumeroPolizaE() {
		return listaNumeroPolizaE;
	}

	public void setListaNumeroPolizaE(RegistroDataModelImpl listaNumeroPolizaE) {
		this.listaNumeroPolizaE = listaNumeroPolizaE;
	}

	public RegistroDataModelImpl getListaCondAccid() {
		return listaCondAccid;
	}

	public void setListaCondAccid(RegistroDataModelImpl listaCondAccid) {
		this.listaCondAccid = listaCondAccid;
	}

	public RegistroDataModelImpl getListaCondAccidE() {
		return listaCondAccidE;
	}

	public void setListaCondAccidE(RegistroDataModelImpl listaCondAccidE) {
		this.listaCondAccidE = listaCondAccidE;
	}

	public RegistroDataModelImpl getListaConductor() {
		return listaConductor;
	}

	public void setListaConductor(RegistroDataModelImpl listaConductor) {
		this.listaConductor = listaConductor;
	}

	public RegistroDataModelImpl getListaCodigoelemento() {
		return listaCodigoelemento;
	}

	public void setListaCodigoelemento(RegistroDataModelImpl listaCodigoelemento) {
		this.listaCodigoelemento = listaCodigoelemento;
	}

	public RegistroDataModelImpl getListaEntidadcomodataria() {
		return listaEntidadcomodataria;
	}

	public void setListaEntidadcomodataria(RegistroDataModelImpl listaEntidadcomodataria) {
		this.listaEntidadcomodataria = listaEntidadcomodataria;
	}

	public String getCodigoElemento() {
		return codigoElemento;
	}

	public void setCodigoElemento(String codigoElemento) {
		this.codigoElemento = codigoElemento;
	}

	public String getOpcion() {
		return opcion;
	}

	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}

	public String getRegistroauxSucursalAccidentalidad() {
		return registroauxSucursalAccidentalidad;
	}

	public void setRegistroauxSucursalAccidentalidad(String registroauxSucursalAccidentalidad) {
		this.registroauxSucursalAccidentalidad = registroauxSucursalAccidentalidad;
	}

	public String getRegistroauxSucursalSeguro() {
		return registroauxSucursalSeguro;
	}

	public void setRegistroauxSucursalSeguro(String registroauxSucursalSeguro) {
		this.registroauxSucursalSeguro = registroauxSucursalSeguro;
	}

	public String getRegistroauxNombreSeguro() {
		return registroauxNombreSeguro;
	}

	public void setRegistroauxNombreSeguro(String registroauxNombreSeguro) {
		this.registroauxNombreSeguro = registroauxNombreSeguro;
	}

	public String getRegistroauxSucursalGases() {
		return registroauxSucursalGases;
	}

	public void setRegistroauxSucursalGases(String registroauxSucursalGases) {
		this.registroauxSucursalGases = registroauxSucursalGases;
	}

	public String getRegistroauxNombreGases() {
		return registroauxNombreGases;
	}

	public void setRegistroauxNombreGases(String registroauxNombreGases) {
		this.registroauxNombreGases = registroauxNombreGases;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public String getParametroVehiculo() {
		return parametroVehiculo;
	}

	public void setParametroVehiculo(String parametroVehiculo) {
		this.parametroVehiculo = parametroVehiculo;
	}

	public String getParametroAccidentes() {
		return parametroAccidentes;
	}

	public void setParametroAccidentes(String parametroAccidentes) {
		this.parametroAccidentes = parametroAccidentes;
	}

	public RegistroDataModelImpl getListaAseguradora() {
		return listaAseguradora;
	}

	public void setListaAseguradora(RegistroDataModelImpl listaAseguradora) {
		this.listaAseguradora = listaAseguradora;
	}

	public RegistroDataModelImpl getListaAseguradoraE() {
		return listaAseguradoraE;
	}

	public void setListaAseguradoraE(RegistroDataModelImpl listaAseguradoraE) {
		this.listaAseguradoraE = listaAseguradoraE;
	}

	public String getRegistroauxSucursalAseguradora() {
		return registroauxSucursalAseguradora;
	}

	public void setRegistroauxSucursalAseguradora(String registroauxSucursalAseguradora) {
		this.registroauxSucursalAseguradora = registroauxSucursalAseguradora;
	}

}
