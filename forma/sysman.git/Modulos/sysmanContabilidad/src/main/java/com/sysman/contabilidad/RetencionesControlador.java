package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.enums.RetencionesControladorEnum;
import com.sysman.contabilidad.enums.RetencionesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.faces.context.FacesContext;

import org.primefaces.component.panel.Panel;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author lcortes
 * @version 1, 07/03/2016
 * 
 * @author jlramirez
 * @version 2, 11/04/2017, proceso de Refactoring y modificaciones
 * segun especificaciones de SONARLINT
 * @version 3, 20/04/2017,Cambios de sysdate por new Date().
 * 
 * @author spina
 * @version 4, 30/05/2017, se refactoriza dss, depuracion sonar y ejb
 */
@ManagedBean
@ViewScoped
public class RetencionesControlador extends BeanBaseDatosAcmeImpl {

	private final String compania;
	/*
	 * Variable para almacenar el modulo, se asigna en el constructor con el valor
	 */
	private final String modulo;
	private final String cCuentaD;
	private final String cCodAux;
	private final String cCuentaC;
	private final String cNombAux;
	private final String cNombCentro;
	private final String cCodRef; 
	private final String cNombREF;
	private final String cCodfuenteR; 
	private final String cNombfuenteR;
	private List<Registro> listaAno;
	private RegistroDataModelImpl listaCuentaDebito;
	private RegistroDataModelImpl listaCuentaCredito;
	private RegistroDataModelImpl listaCentroCosto;
	private RegistroDataModelImpl listaCuentaDebito1;
	private RegistroDataModelImpl listaCuentaCredito1;
	private RegistroDataModelImpl listaCodAuxiliar;
	private RegistroDataModelImpl listaTipo;
	private RegistroDataModelImpl listaCodReferencia;
	private RegistroDataModelImpl listaCodFuenteR;
	private RegistroDataModelImpl listaCuentaCreditos;
    private RegistroDataModelImpl listaCuentaCreditosE;
	private boolean centroCosto;
	private boolean manejaAuxiliar;
	private boolean preparaAnio;
	private boolean referencia;
	private boolean fuenteR;
	
	private boolean varVolver;
	
	private Registro registroSub;
	
	private List<Registro> listaFrmprorrateo;
	/**
	 * Variable para habilitar la visibilidad del campo concepto
	 */
	private boolean visibleConcepto;
	private String anio;
	private String tipoRetencion;
	private String nombreCuenta;
	private String manejaCentro;
	private String centroDeCosto;
	private String nomCentroCosto;
	private String nomAuxiliar;
	private String anioPreparar;
	private String nomReferencia;
	private String manejareferencia;
	private String nomFuenteR;
	@EJB
	private EjbSysmanUtilRemote sysmanUtil;

	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;
	private Boolean prorrateado;

	public RetencionesControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		cCuentaD = "CUENTA_DEBITO";
		cCodAux = "COD_AUXILIAR";
		cCuentaC = "CUENTA_CREDITO";
		cNombAux = "NOMBREAUXILIAR";
		cNombCentro = "NOMBRECENTRO";
		cCodRef = "COD_REFERENCIA"; 
		cNombREF = "NOMBREREFERENCIA";
		cCodfuenteR = "COD_FUENTER" ; 
		cNombfuenteR = "NOMBREFUENTER" ;

		try {
			numFormulario = GeneralCodigoFormaEnum.RETENCIONES_CONTROLADOR
					.getCodigo();
			validarPermisos();
			registroSub = new Registro(new HashMap<String, Object>());
		}
		catch (Exception ex) {
			Logger.getLogger(RetencionesControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@Override
	public void iniciarListas() {
		cargarListaAno();
		cargarListaTipo();
	}

	@Override
	public void iniciarListasSub() {
		anio = registro.getCampos().get("ANO").toString();
		tipoRetencion = registro.getCampos().get("TIPO").toString();
		cargarListaCuentaDebito();
		cargarListaCuentaCredito();
		cargarListaCuentaDebito1();
		cargarListaCuentaCredito1();
		cargarListaCuentaCreditos(); 
		cargarListaCuentaCreditosE();
		cargarListaFrmprorrateo();

	}

	@Override
	public void iniciarListasSubNulo() {
		// <CODIGO_DESARROLLADO>
		listaFrmprorrateo = null;
		// </CODIGO_DESARROLLADO>
	}

	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.RETENCIONES;
		buscarLlave();
		asignarOrigenDatos();
	}

	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
	}
	
	/**
	 * Metodo ejecutado desde un comando remoto en el boton volver del
	 * formulario
	 * 
	 */
	public void ejecutarrcVolver(){
		//<CODIGO_DESARROLLADO>
		if(varVolver) {
           validarPorcentaje();
		}
		//</CODIGO_DESARROLLADO>
	}
	/**
     * 
     * Carga la lista listaFrmprorrateo
     *
     */
	public void cargarListaFrmprorrateo(){
		
		try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PRORRATEADOS
                                                            .getGridKey());
            
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.TIPO.getName(), registro.getCampos().get(GeneralParameterEnum.TIPO.getName()));
            param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
            param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
            
            listaFrmprorrateo = RegistroConverter.toListRegistro(
                    requestManager.getList(urlBean.getUrl(), param),
                    CacheUtil.getLlaveServicio(urlConexionCache,"PRORRATEADOS"));
		  }
        catch (SystemException | SysmanException e) {
        	logger.error(e.getMessage(),e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaAno() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAno = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									RetencionesControladorUrlEnum.URL3567
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			Logger.getLogger(RetencionesControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaCuentaDebito() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RetencionesControladorUrlEnum.URL3679
						.getValue());
		listaCuentaDebito = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCuentaCredito() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RetencionesControladorUrlEnum.URL3679
						.getValue());
		listaCuentaCredito = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCentroCosto() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RetencionesControladorUrlEnum.URL4190
						.getValue());
		listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCuentaDebito1() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RetencionesControladorUrlEnum.URL3679
						.getValue());
		listaCuentaDebito1 = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCuentaCredito1() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RetencionesControladorUrlEnum.URL3679
						.getValue());
		listaCuentaCredito1 = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCodAuxiliar() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RetencionesControladorUrlEnum.URL4899
						.getValue());
		listaCodAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaTipo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RetencionesControladorUrlEnum.URL5125
						.getValue());
		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), null,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	
	public void oprimirCargarPlantilla() {
	    String[] campos = { "modulo" };
	    Object[] valores = { modulo };

	    SessionUtil.cargarModalDatosFlash(
	        Integer.toString(GeneralCodigoFormaEnum.FRM_CARGUE_CODIGOS_RETENCION.getCodigo()),
	        modulo,
	        campos,
	        valores
	    );
	}
	
	public void retornarFormularioCargarPlantilla(SelectEvent event) {
	    listaInicial.load();
	}
	
	//ncardenas
	public void cargarListaCodReferencia() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RetencionesControladorUrlEnum.URL4900
						.getValue());
		listaCodReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCodFuenteR() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RetencionesControladorUrlEnum.URL4901
						.getValue());
		listaCodFuenteR = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCuentaCreditos
	 *
	 */
	public void cargarListaCuentaCreditos(){

		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RetencionesControladorUrlEnum.URL3679
						.getValue());
		listaCuentaCreditos = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaCuentaCreditos
	 *
	 */
	public void  cargarListaCuentaCreditosE(){

		listaCuentaCreditosE = listaCuentaCreditos;

	}



	public void oprimirPreparar() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = true;
		if (!anio.isEmpty()) {
			anioPreparar = String.valueOf(Integer.parseInt(anio) + 1);

		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarAno() {
		// <CODIGO_DESARROLLADO>
		tipoRetencion = "";
		verificarAnio(anio);
		// </CODIGO_DESARROLLADO>
	}

	public void aceptarDialogoPrepararAnio() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = false;
		if (SysmanFunciones.validarVariableVacio(anioPreparar)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB448"));
			return;
		}
		if (!prepararAnioAux()) {
			return;
		}
		// Verifica que el anio a preparar tenga plan de cuentas
		// definido para esa vigencia.
		if (!verificarAnio(anioPreparar)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB451"));
			return;
		}

		if (!revisarConfig()) {
			return;
		}
		JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB455"));
		// </CODIGO_DESARROLLADO>
	}

	public boolean revisarConfig() {
		if (!revisarAnoPreparar()) {
			return false;
		}

		if (!revisarConfiguracion()) {
			return false;
		}

		if (!insertarRetenciones()) {
			return false;
		}
		return true;
	}

	public boolean insertarRetenciones() {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put(RetencionesControladorEnum.ANOPREPARAR.getValue(),
				anioPreparar);
		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametros.put(GeneralParameterEnum.ANO.getName(), anio);
		parametros.put(GeneralParameterEnum.CREATED_BY.getName(),
				SessionUtil.getUser().getCodigo());
		parametros.put(GeneralParameterEnum.DATE_CREATED.getName(),
				new Date());

		UrlBean urlCreate = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						RetencionesControladorUrlEnum.URL8298
						.getValue());
		try {
			int rta = requestManager.saveCount(urlCreate.getUrl(),
					urlCreate.getMetodo(), parametros);
			if (rta <= 0) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB454"));
				return false;
			}
		}
		catch (SystemException e) {
			Logger.getLogger(RetencionesControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB456"));
		}
		return true;
	}

	public boolean revisarConfiguracion() {
		// Mira que la configuracion a preparar no este preparada
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(RetencionesControladorEnum.ANOPREPARAR.getValue(),
				anioPreparar);
		Registro retencion;
		try {
			retencion = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									RetencionesControladorUrlEnum.URL4663
									.getValue())
							.getUrl(), param));
			String numRetencion = retencion.getCampos()
					.get("RETENCION").toString();
			if (!"0".equals(numRetencion)) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB453"));
				return false;
			}
		}
		catch (SystemException e) {
			Logger.getLogger(RetencionesControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return true;
	}

	public boolean revisarAnoPreparar() {
		// Verificar que el anio esta creado
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(RetencionesControladorEnum.PREPARAANO.getValue(),
				anioPreparar);
		Registro pAnio;
		try {
			pAnio = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									RetencionesControladorUrlEnum.URL4425
									.getValue())
							.getUrl(), param));

			int numAnio = Integer.parseInt(
					"" + pAnio.getCampos().get("NUMERO") + "");
			if (0 == numAnio) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB452"));
				return false;
			}
		}
		catch (SystemException e) {
			Logger.getLogger(RetencionesControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return true;
	}

	public boolean prepararAnioAux() {

		SimpleDateFormat anioAux = new SimpleDateFormat("YYYY");
		anioAux.setLenient(false);
		try {
			anioAux.parse(anioPreparar.trim());
		}
		catch (ParseException e) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB450"));
			return false;
		}
		return true;
	}

	public void cancelarDialogoPrepararAnio() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = false;
		// </CODIGO_DESARROLLADO>
	}

	public void seleccionarFilaTipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoRetencion = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
		registro.getCampos().put("TIPO", tipoRetencion);
		registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
		cargarListaCuentaDebito();
		cargarListaCuentaCredito();
		cargarListaCuentaDebito1();
		cargarListaCuentaCredito1();
	}

	public void seleccionarFilaCuentaDebito(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cCuentaD,
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
		registro.getCampos().put(cCuentaC, "");
		registro.getCampos().put(cCodAux, "");
		nombreCuenta = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		evaluarAuxiliar(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaCuentaCredito(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cCuentaC,
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
		nombreCuenta = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		registro.getCampos().put(cCuentaD, "");

		registro.getCampos().put(cCodAux, "");
		evaluarAuxiliar(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaCuentaDebito1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTA_DEBITO1",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

	}

	public void seleccionarFilaCuentaCredito1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTA_CREDITO1",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

	}

	public void seleccionarFilaCodAuxiliar(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cCodAux,
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName())
				.toString());
		nomAuxiliar = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		registro.getCampos().put(cNombAux, nomAuxiliar);
	}

	public void seleccionarFilaCentroCosto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroDeCosto = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
		nomCentroCosto = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
				centroDeCosto);
		registro.getCampos().put(cNombCentro, nomCentroCosto);
	}

	//NCARDENAS
	public void seleccionarFilaCodReferencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cCodRef,
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName())
				.toString());
		nomReferencia = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		registro.getCampos().put(cNombREF, nomReferencia);

	}

	public void seleccionarFilaCodFuenteR(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cCodfuenteR,
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName())
				.toString());
		nomFuenteR = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		registro.getCampos().put(cNombfuenteR, nomFuenteR);


	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaCreditos
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaCreditos(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("CUENTA_CREDITO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaCreditos
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaCreditosE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

	//<METODOS_SUBFORM>	
	/**
	 * Metodo de insercion del formulario Frmprorrateo
	 * 
	 */   
	public void agregarRegistroSubFrmprorrateo() {
		try {
			
			registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSub.getCampos().put(GeneralParameterEnum.TIPO.getName(), registro.getCampos().get(GeneralParameterEnum.TIPO.getName()));
			registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
			registroSub.getCampos().put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
			registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
			registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.PRORRATEADOS
							.getCreateKey());

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
					registroSub.getCampos());
			cargarListaFrmprorrateo();

			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_INGRESADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(),ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}finally{
			registroSub = new Registro(new HashMap<String, Object>());
		} 
	}
	/**
	 * Metodo de edicion del formulario Frmprorrateo
	 * 
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubFrmprorrateo(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {

			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.PRORRATEADOS
							.getUpdateKey());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
					reg.getCampos(),
					reg.getLlave());

			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_MODIFICADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(),ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}finally{
			cargarListaFrmprorrateo();     
		}
	}
	/**
	 * Metodo de eliminacion del formulario Frmprorrateo
	 * 
	 * 
	 * @param reg
	 * registro seleccionado en el subformulario
	 */
	public void eliminarRegSubFrmprorrateo(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.PRORRATEADOS
							.getDeleteKey());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_ELIMINADO"));

			cargarListaFrmprorrateo();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(),ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 * para el subformulario Frmprorrateo
	 *
	 */
	public void cancelarEdicionFrmprorrateo(){
		cargarListaFrmprorrateo();
	}
	//</METODOS_SUBFORM>

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		if (css != null) {
			anio = registro.getCampos().get("ANO").toString();
			tipoRetencion = registro.getCampos().get("TIPO").toString();
			evaluarCuentas();
			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), anio);
			param.put(GeneralParameterEnum.CODIGO.getName(), registro
					.getCampos()
					.get(GeneralParameterEnum.CENTRO_COSTO.getName())
					.toString());
			Registro centroCos;

			try {
				centroCos = RegistroConverter.toRegistro(
						requestManager.get(UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										RetencionesControladorUrlEnum.URL3947
										.getValue())
								.getUrl(), param));
				centroDeCosto = registro.getCampos()
						.get(GeneralParameterEnum.CENTRO_COSTO
								.getName())
						.toString();
				nomCentroCosto = centroCos.getCampos()
						.get(GeneralParameterEnum.NOMBRE.getName())
						.toString();
				registro.getCampos().put(cNombCentro, nomCentroCosto);
				varVolver = true;
			}
			catch (SystemException e) {
				Logger.getLogger(RetencionesControlador.class.getName())
				.log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
		else {
			anio = String.valueOf(SysmanFunciones.ano(
					new Date()));
			tipoRetencion = "";
			nombreCuenta = "";
			registro.getCampos().put("VALOR_APLICAR", 0);
			registro.getCampos().put("FACTORREDONDEO", 0);
			manejaAuxiliar = false;
			Object cuentaDebito = registro.getCampos().get(cCuentaD);
			Object cuentaCredito = registro.getCampos().get(cCuentaC);
			if (cuentaDebito != null) {
				evaluarAuxiliar(cuentaDebito);
			}
			if (cuentaCredito != null) {
				evaluarAuxiliar(cuentaCredito);
			}
		}

		try {
			manejaCentro = sysmanUtil.consultarParametro(compania,
					"MANEJA RETENCIONES POR CENTRO DE COSTO",
					SessionUtil.getModulo(), new Date(), true);
			manejaCentro = manejaCentro == null ? "NO" : manejaCentro;
			if (("SI").equals(manejaCentro)) {
				centroCosto = true;
				cargarListaCentroCosto();
			}
			manejareferencia = sysmanUtil.consultarParametro(compania,
					"MANEJA REFERENCIA Y FUENTE EN RETENCIONES",
					SessionUtil.getModulo(), new Date(), true);
			manejareferencia = manejareferencia == null ? "NO" : manejareferencia;
			if (("SI").equals(manejareferencia)) {
				referencia = true;
				cargarListaCodReferencia();
				fuenteR = true;
				cargarListaCodFuenteR();
			}
			visibleConcepto = "SI".equals(SysmanFunciones
					.nvl(sysmanUtil.consultarParametro(compania, "MANEJA EQUIVALENTE DE RETENCIONES",
							SessionUtil.getModulo(), new Date(), true), "NO"));
		}


		catch (SystemException ex) {
			Logger.getLogger(RetencionesControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}
		// </CODIGO_DESARROLLADO>




	}

	public void evaluarCuentas() {
		String cuentaDebito = registro.getCampos()
				.get(cCuentaD) == null ? null
						: registro.getCampos()
						.get(cCuentaD).toString();
		String cuentaCredito = registro.getCampos()
				.get(cCuentaC) == null ? null
						: registro.getCampos()
						.get(cCuentaC).toString();
		if (cuentaDebito != null) {
			evaluarAuxiliar(cuentaDebito);
		}
		if (cuentaCredito != null) {
			evaluarAuxiliar(cuentaCredito);
		}
	}

	public boolean verificarAnio(String anioAux) {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioAux);
		Registro cuenta;

		try {
			cuenta = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									RetencionesControladorUrlEnum.URL3948
									.getValue())
							.getUrl(), param));
			String numCuenta = cuenta.getCampos().get("CUENTA").toString();
			if ("0".equals(numCuenta)) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB461"));
				return false;
			}
		}
		catch (SystemException e) {
			Logger.getLogger(RetencionesControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return true;
	}

	public void evaluarAuxiliar(Object codCuenta) {
		if (codCuenta != null) {
			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), anio);
			param.put(GeneralParameterEnum.CODIGO.getName(), codCuenta);
			Registro cuenta;

			try {
				cuenta = RegistroConverter.toRegistro(
						requestManager.get(UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										RetencionesControladorUrlEnum.URL3949
										.getValue())
								.getUrl(), param));
				nombreCuenta = SysmanFunciones
						.nvl(cuenta.getCampos()
								.get(GeneralParameterEnum.NOMBRE
										.getName()),
								"")
						.toString();
				String manAuxiliar = sysmanUtil.consultarParametro(compania,
						"MANEJA RETENCIONES POR AUXILIAR",
						SessionUtil.getModulo(), new Date(), true);
				if ("SI".equals(manAuxiliar)) {
					manejaAuxiliar = true;
					cargarListaCodAuxiliar();
					if (registro.getCampos().get(cCodAux) != null
							&& !registro.getCampos().get(cCodAux).toString()
							.isEmpty()) {
						Map<String, Object> param2 = new HashMap<>();
						param2.put(GeneralParameterEnum.COMPANIA.getName(),
								compania);
						param2.put(GeneralParameterEnum.ANO.getName(), anio);
						param2.put(GeneralParameterEnum.CODIGO.getName(),
								registro
								.getCampos()
								.get(cCodAux)
								.toString());
						Registro auxiliar = RegistroConverter.toRegistro(
								requestManager.get(
										UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												RetencionesControladorUrlEnum.URL3950
												.getValue())
										.getUrl(),
										param2));
						nomAuxiliar = auxiliar.getCampos()
								.get(GeneralParameterEnum.NOMBRE
										.getName())
								.toString();
						registro.getCampos().put(cNombAux, nomAuxiliar);
					}
				}
				else {
					manejaAuxiliar = false;
				}
			}
			catch (SystemException e) {
				Logger.getLogger(RetencionesControlador.class.getName())
				.log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}

	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		if (SysmanFunciones.validarVariableVacio(
				registro.getCampos()
				.get(GeneralParameterEnum.CENTRO_COSTO
						.getName())
				.toString())) {
			registro.getCampos().put(
					GeneralParameterEnum.CENTRO_COSTO.getName(),
					SysmanConstantes.CONS_CENTRO);
		}
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>

		registro.getCampos()
		.remove(RetencionesControladorEnum.PCT_APLICARLEY1607
				.getValue());
		if (accion.equals(ACCION_MODIFICAR))
		{
			registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		}
		if (!manejaAuxiliar) {
			registro.getCampos().put(cCodAux,
					SysmanConstantes.CONS_AUXILIAR);
		}
		//ncardenas

		if(!referencia) {
			registro.getCampos().put(cCodRef,
					SysmanConstantes.CONS_REFERENCIA);
			registro.getCampos().put(cCodfuenteR,
					SysmanConstantes.CONS_FUENTE);
		}

		registro.getCampos().remove(cNombAux);
		registro.getCampos().remove(cNombCentro);
		registro.getCampos().remove(cNombREF);
		registro.getCampos().remove(cNombfuenteR); 
		if ("SI".equals(manejaCentro) && "".equals(centroDeCosto)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB463"));
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(cNombAux, nomAuxiliar);
		registro.getCampos().put(cNombCentro, nomCentroCosto);
		registro.getCampos().put(cNombREF, nomReferencia);
		registro.getCampos().put(cNombfuenteR, nomFuenteR);
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
	
	public void validarPorcentaje(){
		try{
			prorrateado = (Boolean) registro.getCampos().get("PRORRATEADO");

			if(prorrateado) {

				Registro rsValor;
				Double valor = null;
				Map<String, Object> param = new HashMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
				param.put(GeneralParameterEnum.TIPO.getName(), registro.getCampos().get(GeneralParameterEnum.TIPO.getName()));
				param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

				rsValor = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										RetencionesControladorUrlEnum.URL1937002.getValue())
								.getUrl(),
								param));

				if (rsValor != null)
				{

					valor = Double.parseDouble(
							SysmanFunciones.nvl(rsValor.getCampos().get("VALOR"), "0").toString());
				}
				
				if(valor != 100) {
					
					JsfUtil.agregarMensajeError("Se ha detectado que el porcentaje en la pestaña Prorrateo no alcanza el 100%. La suma de los registros actuales es de "+valor+"%.");
		            varVolver = true;
					
				}else {
				    accion = null;
					varVolver = false;
				}

			}else {
				accion = null;
				varVolver = false;
			}
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public List<Registro> getListaAno() {
		return listaAno;
	}

	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	public RegistroDataModelImpl getListaCuentaDebito() {
		return listaCuentaDebito;
	}

	public void setListaCuentaDebito(RegistroDataModelImpl listaCuentaDebito) {
		this.listaCuentaDebito = listaCuentaDebito;
	}

	public RegistroDataModelImpl getListaCuentaCredito() {
		return listaCuentaCredito;
	}

	public void setListaCuentaCredito(
			RegistroDataModelImpl listaCuentaCredito) {
		this.listaCuentaCredito = listaCuentaCredito;
	}

	public RegistroDataModelImpl getListaCentroCosto() {
		return listaCentroCosto;
	}

	public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
		this.listaCentroCosto = listaCentroCosto;
	}

	public RegistroDataModelImpl getListaCuentaDebito1() {
		return listaCuentaDebito1;
	}

	public void setListaCuentaDebito1(
			RegistroDataModelImpl listaCuentaDebito1) {
		this.listaCuentaDebito1 = listaCuentaDebito1;
	}

	public RegistroDataModelImpl getListaCuentaCredito1() {
		return listaCuentaCredito1;
	}

	public void setListaCuentaCredito1(
			RegistroDataModelImpl listaCuentaCredito1) {
		this.listaCuentaCredito1 = listaCuentaCredito1;
	}

	public RegistroDataModelImpl getListaCodAuxiliar() {
		return listaCodAuxiliar;
	}

	public void setListaCodAuxiliar(RegistroDataModelImpl listaCodAuxiliar) {
		this.listaCodAuxiliar = listaCodAuxiliar;
	}


	//ncardenas
	public RegistroDataModelImpl getListaCodReferencia() {
		return listaCodReferencia;
	}

	public void setListaCodReferencia(RegistroDataModelImpl listaCodReferencia) {
		this.listaCodReferencia = listaCodReferencia;
	}

	public RegistroDataModelImpl getListaCodFuenteR() {
		return listaCodFuenteR;
	}

	public void setListaCodFuenteR(RegistroDataModelImpl listaCodFuenteR) {
		this.listaCodFuenteR = listaCodFuenteR;
	}
	//
	public RegistroDataModelImpl getListaTipo() {
		return listaTipo;
	}

	public void setListaTipo(RegistroDataModelImpl listaTipo) {
		this.listaTipo = listaTipo;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public String getTipoRetencion() {
		return tipoRetencion;
	}

	public void setTipoRetencion(String tipoRetencion) {
		this.tipoRetencion = tipoRetencion;
	}

	public String getNombreCuenta() {
		return nombreCuenta;
	}

	public void setNombreCuenta(String nombreCuenta) {
		this.nombreCuenta = nombreCuenta;
	}

	public boolean isCentroCosto() {
		return centroCosto;
	}

	public void setCentroCosto(boolean centroCosto) {
		this.centroCosto = centroCosto;
	}

	public boolean isreferencia() {
		return referencia;
	}

	public void setreferencia(boolean referencia) {
		this.referencia = referencia;
	}

	public boolean isfuenteR() {
		return fuenteR;
	}

	public void setfuenteR(boolean fuenteR) {
		this.fuenteR = fuenteR;
	}

	public boolean isManejaAuxiliar() {
		return manejaAuxiliar;
	}

	public void setManejaAuxiliar(boolean manejaAuxiliar) {
		this.manejaAuxiliar = manejaAuxiliar;
	}

	public boolean isPreparaAnio() {
		return preparaAnio;
	}

	public void setPreparaAnio(boolean preparaAnio) {
		this.preparaAnio = preparaAnio;
	}

	public String getAnioPreparar() {
		return anioPreparar;
	}

	public void setAnioPreparar(String anioPreparar) {
		this.anioPreparar = anioPreparar;
	}

	/**
	 * @return the visibleConcepto
	 */
	public boolean isVisibleConcepto() {
		return visibleConcepto;
	}

	/**
	 * @param visibleConcepto the visibleConcepto to set
	 */
	public void setVisibleConcepto(boolean visibleConcepto) {
		this.visibleConcepto = visibleConcepto;
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
		this.auxiliar= auxiliar;
	}

	public RegistroDataModelImpl getListaCuentaCreditos() {
		return listaCuentaCreditos;
	}

	public void setListaCuentaCreditos(RegistroDataModelImpl listaCuentaCreditos) {
		this.listaCuentaCreditos = listaCuentaCreditos;
	}

	public RegistroDataModelImpl getListaCuentaCreditosE() {
		return listaCuentaCreditosE;
	}

	public void setListaCuentaCreditosE(RegistroDataModelImpl listaCuentaCreditosE) {
		this.listaCuentaCreditosE = listaCuentaCreditosE;
	}

	public List<Registro> getListaFrmprorrateo() {
		return listaFrmprorrateo;
	}

	public void setListaFrmprorrateo(List<Registro> listaFrmprorrateo) {
		this.listaFrmprorrateo = listaFrmprorrateo;
	}
	 /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }
    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

	public boolean isVarVolver() {
		return varVolver;
	}

	public void setVarVolver(boolean varVolver) {
		this.varVolver = varVolver;
	}
    
	
}