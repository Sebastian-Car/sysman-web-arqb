package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroGeneralRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.CambiosdenitsControladorEnum;
import com.sysman.general.enums.CambiosdenitsControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 2, 16/05/2016 09:01:35 -- Modificado por dsuesca
 * @version 3, 05/04/2017 -- Modificado por jcrodriguez se adicionaron
 * los servicios para el formulario de datos depuracion del
 * controlador
 */
@ManagedBean
@ViewScoped
public class CambiosdenitsControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * variable que alamcena la compañia
	 */
	private final String compania;
	/**
	 * varaible que alamcena el estado del registrado
	 */
	private boolean registradoVisible;
	/**
	 * variable que alamcena el estado al revisar
	 */
	private boolean revisarVisible;
	/**
	 * lista los registros del nit anterior
	 */
	private RegistroDataModelImpl listaNitAnterior;
	/**
	 * lista los registros del nit anterior auxiliar
	 */
	private RegistroDataModelImpl listaNitAnteriorE;
	/**
	 * variable auxiliar que alamcena un registro
	 */
	private String auxiliar;
	/**
	 * lista los registro del subformulario
	 */
	private RegistroDataModelImpl listaDcambiosdenit;
	/**
	 * variable que almacena los registros
	 */
	private Registro registroSub;
	/**
	 * variable allowEditsSub que almacena el estado del campo del
	 * subformulario
	 */
	private boolean allowEditsSub;
	/**
	 * variable allowEditsSub que almacena el estado del campo del
	 * subformulario
	 */
	private boolean allowDeletionsSub;
	/**
	 * variable allowEditsSub que almacena el estado del campo del
	 * subformulario
	 */
	private boolean allowAdditionsSub;
	/**
	 * variable allowEditsSub que almacena el estado del campo del
	 * subformulario
	 */
	private String sucursalAuxiliar;

	/**
	 * Atributo que administra la visibilidad del boton de realizar
	 * cambio
	 */
	private boolean verCambio;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Atributo que valida si el boton de inconsistencias esta activo
	 * o inactivo
	 */
	private boolean activarInconsistencias = true;
	/**
	 * Atributo permite acceder a la informacion del archivo que ha
	 * sido cargado
	 */
	private Workbook workbook;
	/**
	 * Atributo que permite la lectura de los datos contenidos en el
	 * archivo que se carga
	 */
	private String extension;
	/**
	 * Atributo que almacena los registros del archivo excel que no se
	 * registraron en la tabla D_CAMBIOSNIT
	 */
	private String cadena;
	/**
	 * Atributo que almacena los registros del archivo excel que no se
	 * registraron en la tabla D_CAMBIOSNIT
	 */
	private String cadenaDos;
	/**
	 * Atributo que se activara al ingresar al metodo de importarDatos
	 */
	private boolean cadUno = false;
	/**
	 * Atributo que se activara al ingresar al metodo de importarDatos
	 */
	private boolean cadDos = false;

	@EJB
	private EjbContabilidadCeroGeneralRemote ejbContabilidadCeroGeneral;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	public CambiosdenitsControlador() {
		super();
		compania = SessionUtil.getCompania();
		verCambio = true;
		try {
			numFormulario = GeneralCodigoFormaEnum.CAMBIOSDENITS_CONTROLADOR
					.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			registroSub = new Registro(new HashMap<String, Object>());
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			Logger.getLogger(CambiosdenitsControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * metodo que carla las listas para el formulario
	 */
	@Override
	public void iniciarListas() {
		cargarListaNitAnterior();
		cargarListaNitAnteriorE();
	}

	/**
	 * metodo que carga las lista del subformuarlio
	 */
	@Override
	public void iniciarListasSub() {
		cargarListaDcambiosdenit();
	}

	/**
	 * metodo inicializa la lista de la grilla del subformualrio
	 */
	@Override
	public void iniciarListasSubNulo() {
		listaDcambiosdenit = null;
	}

	/**
	 * metodo que inicializa los datos del formulario
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.CAMBIOSDENIT;
		buscarLlave();
		asignarOrigenDatos();
	}

	/**
	 * metodo que asigna el origen de datos
	 */
	@Override
	public void asignarOrigenDatos() {
		parametrosListado.put("COMPANIA", compania);
		buscarUrls();
	}

	/**
	 * metodo que carga la lista del subformualrio
	 */

	public void cargarListaDcambiosdenit() {
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.D_CAMBIOSDENIT
							.getGridKey());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.NUMERO.getName(), registro
					.getCampos()
					.get(GeneralParameterEnum.NUMERO.getName()));

			listaDcambiosdenit = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					CacheUtil.getLlaveServicio(urlConexionCache,
							CambiosdenitsControladorEnum.D_CAMBIOSDENIT
							.getValue()));
		}

		catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * metodo que carga la lista del nit anterior
	 */
	public void cargarListaNitAnterior() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CambiosdenitsControladorUrlEnum.URL5926
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaNitAnterior = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, CambiosdenitsControladorEnum.PARAM1.getValue());
	}

	/**
	 * metodo que carga la lista del nit anterior
	 */
	public void cargarListaNitAnteriorE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CambiosdenitsControladorUrlEnum.URL5926
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaNitAnteriorE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, CambiosdenitsControladorEnum.PARAM1.getValue());
	}

	/**
	 * Metodo ejecutado al cambiar el control NitNuevo
	 * 
	 * 
	 */
	public void cambiarNitNuevo() {
		// <CODIGO_DESARROLLADO>
		if (!SysmanFunciones.validarCampoVacio(registroSub.getCampos(),
				"NITNUEVO")
				&& !isNumeric(registroSub.getCampos().get("NITNUEVO")
						.toString())) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4234"));
			registroSub.getCampos().put("NITNUEVO", null);

		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control SucursalNueva
	 * 
	 * 
	 */
	public void cambiarSucursalNueva() {
		// <CODIGO_DESARROLLADO>
		if (!SysmanFunciones.validarCampoVacio(registroSub.getCampos(),
				"SUCURSALNUEVA")
				&& !isNumeric(registroSub.getCampos().get("SUCURSALNUEVA")
						.toString())) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4234"));
			registroSub.getCampos().put("SUCURSALNUEVA", null);

		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control NitNuevo en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarNitNuevoC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		if (!SysmanFunciones.validarCampoVacio(
				listaDcambiosdenit.getDatasource().get(rowNum % 10)
				.getCampos(),
				"NITNUEVO")
				&& !isNumeric(listaDcambiosdenit.getDatasource().get(rowNum % 10)
						.getCampos().get("NITNUEVO")
						.toString())) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4234"));
			listaDcambiosdenit.getDatasource().get(rowNum % 10).getCampos()
			.put("NITNUEVO", null);

		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control SucursalNueva en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarSucursalNuevaC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		if (!SysmanFunciones.validarCampoVacio(
				listaDcambiosdenit.getDatasource().get(rowNum % 10)
				.getCampos(),
				"SUCURSALNUEVA")
				&& !isNumeric(listaDcambiosdenit.getDatasource().get(rowNum % 10)
						.getCampos().get("SUCURSALNUEVA")
						.toString())) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4234"));
			listaDcambiosdenit.getDatasource().get(rowNum % 10).getCampos()
			.put("SUCURSALNUEVA", null);

		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que cambia el nit anterior
	 *
	 * @param rowNum
	 */
	public void cambiarNitAnteriorC(int rowNum) {
		listaDcambiosdenit.getDatasource().get(rowNum).getCampos().put(
				CambiosdenitsControladorEnum.NITANTERIOR.getValue(),
				auxiliar);
		listaDcambiosdenit.getDatasource().get(rowNum).getCampos().put(
				CambiosdenitsControladorEnum.SUCURSALANTERIOR
				.getValue(),
				sucursalAuxiliar);
	}

	public void cambiarRegistrado() {
		// heredado del bean base
	}

	/**
	 * metodo que selecciona la fila del nit anterior
	 *
	 * @param event
	 */
	public void seleccionarFilaNitAnterior(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put(
				CambiosdenitsControladorEnum.NITANTERIOR.getValue(),
				registroAux.getCampos()
				.get(CambiosdenitsControladorEnum.NIT
						.getValue()));
		registroSub.getCampos()
		.put(CambiosdenitsControladorEnum.SUCURSALANTERIOR
				.getValue(),
				registroAux.getCampos()
				.get(GeneralParameterEnum.SUCURSAL
						.getName()));

	}

	/**
	 * metodo que selecciona la fila del nit anteiror
	 *
	 * @param event
	 */
	public void seleccionarFilaNitAnteriorE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos()
				.get(CambiosdenitsControladorEnum.NIT.getValue());
		sucursalAuxiliar = (String) registroAux.getCampos()
				.get(GeneralParameterEnum.SUCURSAL.getName());
	}

	/**
	 * metodo auxiliar que devuelve un registro
	 *
	 * @param regAux
	 * @return
	 * @throws SystemException 
	 */
	private void procedimiento(Registro regAux) throws SystemException {

		ejbContabilidadCeroGeneral.cambiarNitTerceros(compania,
				regAux.getCampos()
				.get(CambiosdenitsControladorEnum.NITNUEVO
						.getValue())
				.toString(),
				regAux.getCampos()
				.get(CambiosdenitsControladorEnum.SUCURSALNUEVA
						.getValue())
				.toString(),
				compania, regAux.getCampos()
				.get(CambiosdenitsControladorEnum.NITANTERIOR
						.getValue())
				.toString(),
				regAux.getCampos()
				.get(CambiosdenitsControladorEnum.SUCURSALANTERIOR
						.getValue())
				.toString(),
				SessionUtil.getUser().getCodigo());

	}

	/**
	 * metodo que se ejecuta cuando se oprime el boton cambio
	 */
	public void oprimirCambio() {
		int cambios = 0;
		int errados = 0;
		String textoError="";
		String textoErrorEnc="";
		// <CODIGO_DESARROLLADO>
		if (validarBotonCambio()) {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.NUMERO.getName(), registro
					.getCampos()
					.get(GeneralParameterEnum.NUMERO.getName()));
			List<Registro> nits;
			try {
				nits= RegistroConverter.toListRegistro(
						requestManager.getList(UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										CambiosdenitsControladorUrlEnum.URL903
										.getValue())
								.getUrl(), param));

				for (int i = 0; i < nits.size(); i++) {
					cambios++;
					textoErrorEnc=SysmanFunciones.concatenar(new String [] {textoError,"\n\r\n\r",
							SysmanFunciones.convertirAFechaCadena(new Date(),"dd/MM/yyyy HH:mm:ss"), 
							" Tercero Nuevo: ", 
							SysmanFunciones.padl(nits.get(i).getCampos().get("NITNUEVO").toString(),18," "),
							"  --  Tercero Anterior: ", 
							SysmanFunciones.padl(nits.get(i).getCampos().get("NITANTERIOR").toString(),18," ")});
						
					try {

						procedimiento(nits.get(i));
						textoError= SysmanFunciones.concatenar(new String [] {textoErrorEnc,"Cambio Correcto"});
					} catch (SystemException e) {
						errados++;
						String rta = e.getMessage();
						if (rta.contains("@#INI#@")) {
							rta = rta.substring(e.getMessage().indexOf("@#INI#@"));
							rta = rta.substring(rta.indexOf("Log:"), rta.indexOf("@#FIN#@"));
						}
						textoError= SysmanFunciones.concatenar(new String [] {textoErrorEnc, rta});
					}
				}

			}
			catch (SystemException | ParseException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
			JsfUtil.agregarMensajeInformativoDialogo(SysmanFunciones.concatenar(new String [] {"Se cambiarón " , Integer.toString(cambios-errados)," de ", Integer.toString(cambios),"\n"}));
		}

		if (errados==0) {
			registro.getCampos().put(
					CambiosdenitsControladorEnum.REGISTRADO
					.getValue(),
					true);
			allowAdditionsSub = allowDeletionsSub = allowEditsSub = false;
			agregarRegistroNuevo(false);
		}else {
			archivoDescarga = null;
			try {
				archivoDescarga = JsfUtil
						.getArchivoDescarga(JsfUtil.serializarPlano(textoError),
								"LogInconsistencias.txt");
			}
			catch (JRException | IOException e) {
				JsfUtil.agregarMensajeError(e.getMessage());
				logger.error(e.getMessage(), e);

			}
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton inconsistencias en la
	 * vista
	 *
	 *
	 */
	public void oprimirinconsistencias() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		try {
			archivoDescarga = JsfUtil
					.getArchivoDescarga(JsfUtil.serializarPlano(
							SysmanFunciones.concatenar(cadena,
									cadenaDos)),
							"Inconsistencias.txt");
		}
		catch (JRException | IOException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo para validar la informacion cuando se presiona el boton
	 * de cambio
	 *
	 * @return
	 */
	public boolean validarBotonCambio() {
		if ((Boolean) registro.getCampos().get(
				CambiosdenitsControladorEnum.REGISTRADO.getValue())) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB960"));
			return false;
		}

		if (0 == listaDcambiosdenit.getRowCount()) {
			JsfUtil.agregarMensajeAlerta(
					idioma.getString("TB_TB961").replace("#numero#",
							registro.getCampos()
							.get(GeneralParameterEnum.NUMERO
									.getName())
							.toString()));
			return false;
		}

		return true;
	}

	/**
	 * Metodo que valida si un cadena es numerica o no
	 * 
	 * @param cadena
	 * @return
	 */
	private boolean isNumeric(String cadena) {
		return NumberUtils.isNumber(cadena);
	}

	/**
	 * metodo que se ejectua cuando se oprime el boton numero
	 */

	public void oprimirnumero() {
		if (ACCION_INSERTAR.equals(accion)) {
			agregarRegistroNuevo(true);
		}
		else {
			agregarRegistroNuevo(false);
		}
		cargarRegistro(rid, ACCION_MODIFICAR);
	}

	public void oprimirRevisarTer() {
		// heredado del bean base
	}

	/**
	 * metodo que se llama cuando se crea un nuevo registro del
	 * subformulario
	 */
	public void agregarRegistroSubDcambiosdenit() {
		try {
			registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
					compania);
			registroSub.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
					registro.getCampos().get(GeneralParameterEnum.NUMERO
							.getName()));
			registroSub.getCampos().put(
					GeneralParameterEnum.CREATED_BY.getName(),
					SessionUtil.getUser().getCodigo());
			registroSub.getCampos().put(
					GeneralParameterEnum.DATE_CREATED.getName(),
					new Date());
			registroSub.getCampos()
			.remove(GeneralParameterEnum.MODIFIED_BY.getName());
			registroSub.getCampos().remove(
					GeneralParameterEnum.DATE_MODIFIED.getName());
			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.D_CAMBIOSDENIT
							.getCreateKey());
			try {
				requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
						registroSub.getCampos());
				cargarListaDcambiosdenit();
			}
			catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}
		finally {
			registroSub = new Registro(new HashMap<String, Object>());
		}
	}

	/**
	 * metodo que se llama cuando se edita registro del subformulario
	 */
	public void editarRegSubDcambiosdenit(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();

		try {
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
					SessionUtil.getUser().getCodigo());
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
					new Date());
			reg.getCampos().remove(
					CambiosdenitsControladorEnum.RNUM.getValue());
			reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			reg.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
			reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
			reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
			reg.getCampos().remove("NOMBRENUEVO");
			reg.getCampos().remove("NOMBREANTERIOR");
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.D_CAMBIOSDENIT
							.getUpdateKey());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
					reg.getCampos(),
					reg.getLlave());
			JsfUtil.agregarMensajeInformativo(
					idioma.getString(
							CambiosdenitsControladorEnum.MSM_REGISTRO_MODIFICADO
							.getValue()));

		}
		catch (SystemException ex) {
			Logger.getLogger(CambiosdenitsControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}

		finally {
			cargarListaDcambiosdenit();
		}

	}

	/**
	 * metodo que se llama cuando se elimina un registro del
	 * subformulario
	 */
	public void eliminarRegSubDcambiosdenit(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.D_CAMBIOSDENIT
							.getDeleteKey());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(
					idioma.getString(
							CambiosdenitsControladorEnum.MSM_REGISTRO_ELIMINADO
							.getValue()));
			cargarListaDcambiosdenit();
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que se llama cuando cambia el nit de la lista del
	 * combobox
	 */
	public void cancelarEdicionDcambiosdenit() {
		// METODO NO IMPLEMENTADO
	}

	/**
	 * 
	 * Metodo ejecutado al cargar un archivo desde el control Excel
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 *
	 */
	public void cargarArchivoExcel(FileUploadEvent event) {
		// <CODIGO_DESARROLLADO>
		workbook = null;
		try {
			InputStream is = event.getFile().getInputstream();
			if (is == null) {
				return;
			}
			String rutaArchivo = event.getFile().getFileName();
			extension = FilenameUtils.getExtension(rutaArchivo);
			// Inicializa el workbook de acuerdo a la extension del
			// archivo (xls o xlsx)
			if (workbook == null) {
				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(is);
				}
				else {
					workbook = new XSSFWorkbook(is);
				}
			}

			importarDatos(workbook);
		}

		catch (IOException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Recorre la hoja en la que se encuentran los datos que se van a
	 * importar y realiza el proceso de insercion de los mismos
	 * 
	 * @param workbook
	 * Contenido del archivo seleccionado
	 */
	private void importarDatos(Workbook workbook) {
		Sheet sheet = workbook.getSheetAt(0);
		HashMap<String, Object> campos = new HashMap<>();
		HashMap<String, Object> nombreCampos = new HashMap<>();
		cadUno = cadDos = true;

		cadena = idioma.getString("TB_TB4183");

		for (int i = 0; i < 4; i++) {
			Row r = sheet.getRow(0);
			nombreCampos.put(String.valueOf(i),
					r.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
					.getStringCellValue()
					.toUpperCase());
		}
		for (int rowNum = 1; rowNum <= sheet
				.getLastRowNum(); rowNum++) {

			for (int column = 0; column < 4; column++) {
				if (validarFilaCeldaVacia(sheet, rowNum, column)) {
					Row r = sheet.getRow(rowNum);
					Cell cell = r.getCell(column,
							Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
					String valor = "";
					cell.setCellType(Cell.CELL_TYPE_STRING);
					valor = "";
					if (validarCampos(campos, column)) {
						valor = !SysmanFunciones.validarVariableVacio(
								cell.getStringCellValue())
								? cell.getStringCellValue()
										: null;
					}

					campos.put(nombreCampos.get(String.valueOf(column))
							.toString(), valor);
				}
				else {
					return;
				}

			}
			insertarDatos(campos);
			campos = new HashMap<>();

		}

		cargarListaDcambiosdenit();
		activarInconsistencias = false;
	}

	/**
	 * Valida si una celda especifica dentro de la hoja de datos viene
	 * nula o esta en blanco
	 * 
	 * @param sheet
	 * Hoja de datos que se va a analizar
	 * @param rowNum
	 * Numero de fila que se evaluara
	 * @param column
	 * Numero de la columna dentro de la fila que se evaluara
	 * @return Verdadero si la celda posee valor
	 */
	private boolean validarFilaCeldaVacia(Sheet sheet, int rowNum, int column) {
		boolean respuesta = false;
		Row rAux = sheet.getRow(rowNum);
		if (rAux != null) {
			Cell cellAux = rAux.getCell(column,
					Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

			if (cellAux != null) {
				cellAux.setCellType(Cell.CELL_TYPE_STRING);
				if (!SysmanFunciones.validarVariableVacio(
						cellAux.getStringCellValue())) {
					respuesta = true;

				}
			}

		}
		return respuesta;
	}

	/**
	 * Permite validar si un campo que viene vacio cuando se esta
	 * importando no sea la primera columna
	 * 
	 * @param campos
	 * estructura que almacena los campo que se van a insertar
	 * @param column
	 * numero de columna dentro de la fila que se esta leyebdo
	 * @return Verdadero si la estructura que almacena los campos no
	 * esta vacia
	 */
	private boolean validarCampos(Map<String, Object> campos, int column) {
		return !campos.isEmpty() || column >= 0;
	}

	/**
	 * Realiza el llamado al metodo de Insertar ubicado en la clase
	 * acciones, para realizar el registro de la informacion que se
	 * envia por parametro
	 * 
	 * @param archivo
	 * nombre de la tabla en la que se realizara la insercion
	 * @param campos
	 * con sus repectivos valores a insertar
	 */
	private void insertarDatos(HashMap<String, Object> campos) {
		UrlBean urlCreate = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						GenericUrlEnum.D_CAMBIOSDENIT
						.getCreateKey());
		campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		campos.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO
						.getName()));
		campos.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
		campos.put(GeneralParameterEnum.CREATED_BY.getName(),
				SessionUtil.getUser().getCodigo());

		if (!validarTerceroAnterior(
				campos.get(CambiosdenitsControladorEnum.NITANTERIOR
						.getValue()).toString(),
				campos.get(CambiosdenitsControladorEnum.SUCURSALANTERIOR
						.getValue()).toString())) {

			if (cadUno) {
				cadena = SysmanFunciones.concatenar(cadena, "\r\n",
						idioma.getString("TB_TB4184"));
			}
			cadUno = false;

			armarCadena(campos, "1");
		}
		else if (verificarTercero(
				campos.get(CambiosdenitsControladorEnum.NITANTERIOR
						.getValue()).toString(),
				campos.get(CambiosdenitsControladorEnum.SUCURSALANTERIOR
						.getValue()).toString())) {
			if (cadDos) {
				cadenaDos = idioma.getString("TB_TB4185");
			}
			cadDos = false;
			armarCadena(campos, "2");
		}
		else {
			Parameter parameter = new Parameter();
			parameter.setFields(campos);
			try {
				requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
						parameter);
			}
			catch (SystemException e) {
				JsfUtil.agregarMensajeError(e.getMessage());
				logger.error(e.getMessage(), e);

			}
		}

	}

	/**
	 * Metodo que arma la cadena a generar en el archivo plano
	 * 
	 * @param campos
	 */
	public void armarCadena(HashMap<String, Object> campos, String opcion) {
		String cadenaBase = SysmanFunciones.concatenar(
				opcion.equals("1") ? cadena : cadenaDos,
						"NIT ANTERIOR: ",
						SysmanFunciones.padr(campos.get(
								CambiosdenitsControladorEnum.NITANTERIOR
								.getValue())
								.toString(), 18,
								" "),
						"\t", "SUCURSAL ANTERIOR: ",
						campos.get(CambiosdenitsControladorEnum.SUCURSALANTERIOR
								.getValue()).toString(),
						"\r\n",
						SysmanFunciones.concatenar("NIT NUEVO",
								SysmanFunciones.padl(": ", 5, " ")),

						SysmanFunciones.padr(campos.get(
								CambiosdenitsControladorEnum.NITNUEVO
								.getValue())
								.toString(), 18,
								" "),
						"\t",
						SysmanFunciones.concatenar("SUCURSAL NUEVA",
								SysmanFunciones.padl(": ", 5, " ")),
						campos.get(CambiosdenitsControladorEnum.SUCURSALNUEVA
								.getValue()).toString(),
				"\r\n");
		;

		if ("1".equals(opcion)) {
			cadena = cadenaBase;
		}
		else {
			cadenaDos = cadenaBase;
		}

	}

	/**
	 * Metodo que valida si el nit anterior del excel cargado se
	 * encuentra registrado en la base de datos en la tabla TERCERO
	 * 
	 * @param nit
	 * @param sucursal
	 */
	public boolean validarTerceroAnterior(String nit, String sucursal) {

		boolean respuesta = false;
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(CambiosdenitsControladorEnum.NIT.getValue(), nit);
		param.put(GeneralParameterEnum.SUCURSAL.getName(),
				sucursal);

		try {
			Registro rsTercero = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									CambiosdenitsControladorUrlEnum.URL912
									.getValue())
							.getUrl(), param));

			if (!rsTercero.getCampos().get("EXISTE").toString().equals("0")) {
				respuesta = true;
			}
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

		return respuesta;
	}

	/**
	 * Metodo que valida si el nit anterior del excel cargado se
	 * encuentra registrado en la base de datos en la tabla
	 * D_CAMBIOSNIT
	 * 
	 * @param nit
	 * @param sucursal
	 */
	public boolean verificarTercero(String nit, String sucursal) {

		boolean respuesta = false;
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos()
				.get(GeneralParameterEnum.NUMERO.getName()));
		param.put(CambiosdenitsControladorEnum.NIT.getValue(), nit);
		param.put(GeneralParameterEnum.SUCURSAL.getName(),
				sucursal);

		try {
			Registro rsExiste = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									CambiosdenitsControladorUrlEnum.URL955
									.getValue())
							.getUrl(), param));

			if (!rsExiste.getCampos().get("EXISTE").toString().equals("0")) {
				respuesta = true;
			}
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

		return respuesta;
	}

	/**
	 * metodo que es llamado cuando se abre el formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>

		try {

			if (("SI".equals(SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(
							compania,
							CambiosdenitsControladorEnum.PAMETRO
							.getValue(),
							SessionUtil.getModulo(),
							new Date(), true),
					"NO")))
					&& CambiosdenitsControladorEnum.VALOR.getValue()
					.equals(SessionUtil.getMenuActual())) {
				revisarVisible = true;
			}
			else {
				revisarVisible = false;
			}

			registradoVisible = !CambiosdenitsControladorEnum.VALOR.getValue()
					.equals(SessionUtil.getMenuActual());

		}
		catch (SystemException e) {
			Logger.getLogger(CambiosdenitsControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que se llama para cargar los registros
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();

		if (css == null) {
			registro.getCampos().put(GeneralParameterEnum.USUARIO.getName(),
					SessionUtil.getUser().getCodigo());
			registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
					new Date());
			allowAdditionsSub = allowDeletionsSub = allowEditsSub = false;

			verCambio = false;
		}
		else {
			if (!SessionUtil.getMenuActual().equals(
					CambiosdenitsControladorEnum.VALOR.getValue())) {
				allowAdditionsSub = allowDeletionsSub = allowEditsSub = !(Boolean) registro
						.getCampos()
						.get(CambiosdenitsControladorEnum.REGISTRADO
								.getValue());
			}
			else {
				allowAdditionsSub = allowDeletionsSub = allowEditsSub = true;
			}

			verCambio = true;
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que se llama cuando se crea un nuevo registro del
	 * formulario
	 */
	@Override
	public boolean insertarAntes() {

		registro.getCampos().put("COMPANIA", compania);
		try {

			registro.getCampos().put("NUMERO",
					ejbSysmanUtil.generarSiguienteConsecutivo(
							CambiosdenitsControladorEnum.CAMBIOSDENIT
							.getValue(),
							SysmanFunciones.concatenar(
									" COMPANIA = ''",
									compania, "'' "),
							"NUMERO"));
		}
		catch (SystemException e) {
			Logger.getLogger(CambiosdenitsControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return true;
	}

	@Override
	public boolean insertarDespues() {
		// heredado del bean base
		return true;
	}

	/**
	 * metodo que se llama cuando se actualiza un registro del
	 * formulario
	 */
	@Override
	public boolean actualizarAntes() {
		if (ACCION_MODIFICAR.equals(accion)) {
			registro.getCampos()
			.remove(GeneralParameterEnum.COMPANIA.getName());
		}
		return true;
	}

	/**
	 * metodo que se llama despues de actualizar un registro del
	 * formulario
	 */
	@Override
	public boolean actualizarDespues() {
		// heredado del bean base
		return true;
	}

	@Override
	public boolean eliminarAntes() {
		// heredado del bean base
		return true;
	}

	@Override
	public boolean eliminarDespues() {
		// heredado del bean base
		return true;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>

	public boolean isRegistradoVisible() {
		return registradoVisible;
	}

	public void setRegistradoVisible(boolean registradoVisible) {
		this.registradoVisible = registradoVisible;
	}

	public boolean isRevisarVisible() {
		return revisarVisible;
	}

	public void setRevisarVisible(boolean revisarVisible) {
		this.revisarVisible = revisarVisible;
	}

	public RegistroDataModelImpl getListaNitAnterior() {
		return listaNitAnterior;
	}

	public void setListaNitAnterior(RegistroDataModelImpl listaNitAnterior) {
		this.listaNitAnterior = listaNitAnterior;
	}

	public RegistroDataModelImpl getListaNitAnteriorE() {
		return listaNitAnteriorE;
	}

	public void setListaNitAnteriorE(RegistroDataModelImpl listaNitAnteriorE) {
		this.listaNitAnteriorE = listaNitAnteriorE;
	}

	public String getAuxiliar() {
		return auxiliar;
	}

	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	public RegistroDataModelImpl getListaDcambiosdenit() {
		return listaDcambiosdenit;
	}

	public void setListaDcambiosdenit(
			RegistroDataModelImpl listaDcambiosdenit) {
		this.listaDcambiosdenit = listaDcambiosdenit;
	}

	public Registro getRegistroSub() {
		return registroSub;
	}

	public void setRegistroSub(Registro registroSub) {
		this.registroSub = registroSub;
	}

	public boolean isAllowEditsSub() {
		return allowEditsSub;
	}

	public void setAllowEditsSub(boolean allowEditsSub) {
		this.allowEditsSub = allowEditsSub;
	}

	public boolean isAllowDeletionsSub() {
		return allowDeletionsSub;
	}

	public void setAllowDeletionsSub(boolean allowDeletionsSub) {
		this.allowDeletionsSub = allowDeletionsSub;
	}

	public boolean isAllowAdditionsSub() {
		return allowAdditionsSub;
	}

	public void setAllowAdditionsSub(boolean allowAdditionsSub) {
		this.allowAdditionsSub = allowAdditionsSub;
	}

	public boolean isVerCambio() {
		return verCambio;
	}

	public void setVerCambio(boolean verCambio) {
		this.verCambio = verCambio;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * Retorna la variable activarInconsistencias
	 * 
	 * @return activarInconsistencias
	 */
	public boolean isActivarInconsistencias() {
		return activarInconsistencias;
	}

	/**
	 * Asigna la variable activarInconsistencias
	 * 
	 * @param activarInconsistencias
	 * Variable a asignar en activarInconsistencias
	 */
	public void setActivarInconsistencias(boolean activarInconsistencias) {
		this.activarInconsistencias = activarInconsistencias;
	}

}
