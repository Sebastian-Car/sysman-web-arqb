package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FuenterecursosppsControladorEnum;
import com.sysman.general.enums.FuenterecursosppsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroGeneralRemote;
import com.sysman.recursos.ejb.EjbPrepararAnoRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
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
 *
 * @author acaceres
 * @version 1, 16/06/2016
 *
 * @author ybecerra
 * @version 2, 18/04/2017 Revision Sonar y Refactoring
 * 
 * @author asana
 * @version 3, 13/06/2017 Se implementa enum en formulario
 * 
 * @author jromero
 * @version 4, 28/02/2018 Se crean atributos mostrar tipo y botonaux
 * para llamar el formulario en otra opcion de menu
 */
@ManagedBean
@ViewScoped

public class FuenterecursosppsControlador extends BeanBaseContinuoAcmeImpl {
	private final String compania;

	private int ano;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Atributo que valida si el combo de tipo de fuente SIA se hace
	 * visible o no
	 */
	private boolean mostrarTipoPOAI;
	/**
	 * 
	 */
	private String anioBase;
	/**
	 */
	private String anioDestino;
	/**
	 */

	private boolean anioBaseVisible;
	/**
	 * Atributo que oculta la vista en la grilla
	 *
	 *
	 */
	private boolean mostrarTipo;
	/**
	 * Atributo que oculta boton
	 *
	 *
	 */
	private boolean botonaux;
	
	/**
	 * Variable para habilitar los campos de equivalente CUIPO
	 */
	private boolean manejaEquivalenteCUIPO;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	private List<Registro> listaTipo;
	private List<Registro> listaANO;
	private List<Registro> listaanioBase;
	/**
	 */
	private List<Registro> listaanioDestino;

	/**
	 * 
	 */
	private List<Registro> listaCbCodigoSIA;

	/**
	 * Lista de registros de la tabla tipo fuente poai
	 */
	private List<Registro> listaTipoFuentePOAI;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Lista de registros de la tabla fuente_equivalente_dnp
	 */
	private RegistroDataModelImpl listaCodigoDnp;
	/**
	 * Lista de registros de la tabla fuente_equivalente_dnp
	 */
	private RegistroDataModelImpl listaCodigoDnpE;

	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaEquivalentecuipo;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaEquivalentecuipoE;
	
    private RegistroDataModelImpl listacodigoCleo;
    
    private RegistroDataModelImpl listacodigoCleoE;
	 /**
	     * TODO DOCUMENTACION NECESARIA
	     */
	 private RegistroDataModelImpl listaReservaApropiacionEquivalente;
	    /**
	     * TODO DOCUMENTACION NECESARIA
	     */
	 private RegistroDataModelImpl listaReservaApropiacionEquivalenteE;
	    /**
	     * TODO DOCUMENTACION NECESARIA
	     */
	 private RegistroDataModelImpl listaReservaCajaEquivalente;
	    /**
	     * TODO DOCUMENTACION NECESARIA
	     */
	 private RegistroDataModelImpl listaReservaCajaEquivalenteE;
	
		/**
	 * Esta variable se usa como auxiliar para subformularios y en
	 * esta se alamcena el identificador del registro que se
	 * selecciono
	 */
	private String auxiliar;
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	@EJB
	private EjbPrepararAnoRemote ejbPrepararAno;

	@EJB
	private EjbPresupuestoCeroGeneralRemote ejbPresupuestoCeroGeneral;
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;


	/**
	 * Creates a new instance of FuenterecursosppsControlador
	 */
	public FuenterecursosppsControlador() {
		super();
		compania = SessionUtil.getCompania();

		try {
			numFormulario = GeneralCodigoFormaEnum.FUENTERECURSOSPPS_CONTROLADOR
					.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>

		}
		catch (Exception ex) {
			Logger.getLogger(FuenterecursosppsControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {

		tabla = GenericUrlEnum.FUENTE_RECURSOS.getTable();
		buscarLlave();
		reasignarOrigen();
		registro = new Registro();
		abrirFormulario();
		// <CARGAR_LISTA>
		cargarListaANO();
		cargarListaTipo();
		cargarListaanioBase();
		cargarListaanioDestino();
		cargarListaCbCodigoSIA();
		cargarListaTipoFuentePOAI();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCodigoDnp();
		cargarListaCodigoDnpE();
		cargarListaEquivalentecuipo(); 
		cargarListaEquivalentecuipoE();
		cargarListaReservaApropiacionEquivalente(); 
		cargarListaReservaApropiacionEquivalenteE();
		cargarListaReservaCajaEquivalente(); 
		cargarListaReservaCajaEquivalenteE();
		cargarListacodigoCleo();
        cargarListacodigoCleoE();
		// </CARGAR_LISTA_COMBO_GRANDE>

	}

	@Override
	public void reasignarOrigen() {

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		parametrosListado.put(
				FuenterecursosppsControladorEnum.PARAM0.getValue(),
				String.valueOf(ano));
		urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
				FuenterecursosppsControladorUrlEnum.URL128
				.getValue());
		urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
				FuenterecursosppsControladorUrlEnum.URL119
				.getValue());
		urlActualizacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FuenterecursosppsControladorUrlEnum.URL122
						.getValue());
		urlEliminacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FuenterecursosppsControladorUrlEnum.URL126
						.getValue());

	}

	// <METODOS_CARGAR_LISTA>

	public void cargarListaTipo() {
		try {
			listaTipo = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FuenterecursosppsControladorUrlEnum.URL4660
									.getValue())
							.getUrl(), null));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	
	public void cargarListacodigoCleo() {
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					FuenterecursosppsControladorUrlEnum.URL1929001
    					.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				ano);

    	listacodigoCleo = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param,
    			true, "CODIGO");
    }
    
    
    private void cargarListacodigoCleoE() {
    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					FuenterecursosppsControladorUrlEnum.URL1929001
    					.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				ano);

    	listacodigoCleoE = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param,
    			true, "CODIGO");
    }
    
    
	public void cargarListaANO() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaANO = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FuenterecursosppsControladorUrlEnum.URL4989
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cargarListaanioBase() {
		listaanioBase = listaANO;
	}

	public void cargarListaanioDestino() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioBase);

		try {
			listaanioDestino = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FuenterecursosppsControladorUrlEnum.URL5987
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
	 * Carga la lista listaCbCodigoSIA
	 */
	public void cargarListaCbCodigoSIA() {

		try {
			listaCbCodigoSIA = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FuenterecursosppsControladorUrlEnum.URL001
									.getValue())
							.getUrl(), null));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaTipoFuentePOAI
	 *
	 */
	public void cargarListaTipoFuentePOAI() {
		try {
			listaTipoFuentePOAI = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FuenterecursosppsControladorUrlEnum.URL282
									.getValue())
							.getUrl(), null));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * 
	 * Carga la lista listaCodigoDnp
	 *
	 */
	public void cargarListaCodigoDnp() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FuenterecursosppsControladorUrlEnum.URL271
						.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.ANO.getName(),
				ano);

		listaCodigoDnp = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCodigoDnp
	 *
	 */
	public void cargarListaCodigoDnpE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FuenterecursosppsControladorUrlEnum.URL271
						.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.ANO.getName(),
				ano);

		listaCodigoDnpE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaEquivalentecuipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaEquivalentecuipo(){



		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FuenterecursosppsControladorUrlEnum.URL341
						.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				ano);

		listaEquivalentecuipo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

		//listaEquivalentecuipo = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR906:TBCB7939","SELECT  CODIGO, NOMBRE, CODIGO_EQUIVALENTE FROM CUIPO_FUENTES WHERE COMPANIA = :COMPANIA AND ANO =:ANO",true,"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaEquivalentecuipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListaEquivalentecuipoE(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FuenterecursosppsControladorUrlEnum.URL341
						.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				ano);

		listaEquivalentecuipoE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		//listaEquivalentecuipoE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR906:TBCB7939","SELECT  CODIGO, NOMBRE, CODIGO_EQUIVALENTE FROM CUIPO_FUENTES WHERE COMPANIA = :COMPANIA AND ANO =:ANO",true,"CODIGO");
	}
	
	   /**
	     * 
	     * Carga la lista listaReservaApropiacionEquivalente
	     *
	     * TODO DOCUMENTACION ADICIONAL
	     */
	public void cargarListaReservaApropiacionEquivalente(){
	    
	    UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FuenterecursosppsControladorUrlEnum.URL341
                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            ano);

            listaReservaApropiacionEquivalente = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());
	}
	    /**
	     * 
	     * Carga la lista listaReservaApropiacionEquivalente
	     *
	     * TODO DOCUMENTACION ADICIONAL
	     */
	public void  cargarListaReservaApropiacionEquivalenteE(){
	    UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FuenterecursosppsControladorUrlEnum.URL341
                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            ano);

            listaReservaApropiacionEquivalenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());
	
	}
	    /**
	     * 
	     * Carga la lista listaReservaCajaEquivalente
	     *
	     * TODO DOCUMENTACION ADICIONAL
	     */
	public void cargarListaReservaCajaEquivalente(){
	
	    UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FuenterecursosppsControladorUrlEnum.URL341
                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            ano);

            listaReservaCajaEquivalente = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());
	    
	}
	    /**
	     * 
	     * Carga la lista listaReservaCajaEquivalente
	     *
	     * TODO DOCUMENTACION ADICIONAL
	     */
	public void  cargarListaReservaCajaEquivalenteE(){
	
	    UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FuenterecursosppsControladorUrlEnum.URL341
                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            ano);

            listaReservaCajaEquivalenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());
            
	}
	


	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	public void oprimirIniciar() {
		// <CODIGO_DESARROLLADO>
		anioBaseVisible = true;
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirComando18() {
		// <CODIGO_DESARROLLADO>

		try {

			String usuario = SessionUtil.getUser().getCodigo();

			ejbPresupuestoCeroGeneral.insertarAuxiliarenPresupuesto(
					compania, Integer.valueOf(ano), usuario);

			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1637"));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	public void cambiarANO() {
		// <CODIGO_DESARROLLADO>
		if (ano == 0) {
			JsfUtil.agregarMensajeAlerta(
					idioma.getString("TB_TB2680").replace("#ANIO#",
							Integer.toString(ano)));
		}
		reasignarOrigen();
		cargarListaCodigoDnp();
		cargarListaCodigoDnpE();
		cargarListaEquivalentecuipo();
		cargarListaEquivalentecuipoE();
		cargarListacodigoCleo();
        cargarListacodigoCleoE();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control anioBase
	 *
	 *
	 */
	public void cambiaranioBase() {
		// <CODIGO_DESARROLLADO>
		anioDestino = null;
		cargarListaanioDestino();

		// </CODIGO_DESARROLLADO>
	}

	public void aceptaranioBase() {

		// <CODIGO_DESARROLLADO>

		if ("".equals(anioBase) || (anioBase == null)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2606"));
			return;
		}
		if ("".equals(anioDestino) || (anioDestino == null)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2607"));
			return;
		}

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioDestino);

		try {
			Registro reg = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FuenterecursosppsControladorUrlEnum.URL199
									.getValue())
							.getUrl(), param));

			if (Integer.parseInt(
					reg.getCampos().get("VALIDACION").toString()) > 0) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4095"));
				return;
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}

		prepararAnoSiguiente();

		// </CODIGO_DESARROLLADO>
	}

	private void prepararAnoSiguiente() {
		try {

			ejbPrepararAno.copiarFuenteRecurso(compania,
					Integer.parseInt(anioDestino),
					Integer.parseInt(anioBase), compania);
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1637"));
			anioBase = null;
			anioDestino = null;
			anioBaseVisible = false;

		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar del dialogo
	 * anioBase en la vista
	 *
	 *
	 */
	public void cancelaranioBase() {
		// <CODIGO_DESARROLLADO>
		anioBaseVisible = false;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 *
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo
	 * AnioDestino en la vista
	 *
	 *
	 */
	public void aceptarAnioDestino() {

		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoDnp
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoDnp(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_DNP",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoDnp
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoDnpE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaEquivalentecuipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEquivalentecuipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();	
		registro.getCampos().put("EQUIVALENTECUIPO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaEquivalentecuipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEquivalentecuipoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGO");
	}
	
	/**
	     * 
	     * Metodo ejecutado al seleccionar una fila de la lista
	     * listaReservaApropiacionEquivalente
	     *
	     * TODO DOCUMENTACION ADICIONAL
	     *
	     * @param event
	     * objeto que encapsula la accion proveniente de la vista
	     */
	public void seleccionarFilaReservaApropiacionEquivalente(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	        registro.getCampos().put("EQUIVALENTEAPROPIACION", registroAux.getCampos().get("CODIGO"));
	}
	    /**
	     * 
	     * Metodo ejecutado al seleccionar una fila de la lista
	     * listaReservaApropiacionEquivalente
	     *
	     * TODO DOCUMENTACION ADICIONAL
	     *
	     * @param event
	     * objeto que encapsula la accion proveniente de la vista
	     */
	public void seleccionarFilaReservaApropiacionEquivalenteE(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	       auxiliar =  (String) registroAux.getCampos().get("CODIGO");
	}
	    /**
	     * 
	     * Metodo ejecutado al seleccionar una fila de la lista
	     * listaReservaCajaEquivalente
	     *
	     * TODO DOCUMENTACION ADICIONAL
	     *
	     * @param event
	     * objeto que encapsula la accion proveniente de la vista
	     */
	public void seleccionarFilaReservaCajaEquivalente(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	        registro.getCampos().put("EQUIVALENTECAJA", registroAux.getCampos().get("CODIGO"));
	}
	    /**
	     * 
	     * Metodo ejecutado al seleccionar una fila de la lista
	     * listaReservaCajaEquivalente
	     *
	     * TODO DOCUMENTACION ADICIONAL
	     *
	     * @param event
	     * objeto que encapsula la accion proveniente de la vista
	     */
	public void seleccionarFilaReservaCajaEquivalenteE(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	       auxiliar =  (String) registroAux.getCampos().get("CODIGO");
	}
	
	 public void seleccionarFilacodigoCleo(SelectEvent event){
	    	Registro registroAux = (Registro) event.getObject();
	        registro.getCampos().put(FuenterecursosppsControladorEnum.CODIGO_CLEOPATRA.getValue(), registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	    }
	    
	    
	    public void seleccionarFilacodigoCleoE(SelectEvent event){
	    	Registro registroAux = (Registro) event.getObject();
	        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()),"").toString();
	    }


	// </METODOS_COMBOS_GRANDES>
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		ano = SysmanFunciones
				.ano(new Date());
		anioBase = String.valueOf(SysmanFunciones
				.ano(new Date()));

		if ("670180".equals(SessionUtil.getMenuActual())) {
			mostrarTipo = botonaux = false;
			mostrarTipoPOAI = true;

		}
		else {
			mostrarTipo = botonaux = true;
			mostrarTipoPOAI = false;
		}
		
		// 7732930 mperez - Permite verificar si se deben habilitar los campos de equivalente CUIPO		         			
		try {
			manejaEquivalenteCUIPO = ("SI".equals(SysmanFunciones
						.nvl(ejbSysmanUtil.consultarParametro(compania, 
								"MANEJA EQUIVALENTE CUIPO EN FUENTES PARA VIGENCIA ANTERIORES",
								SessionUtil.getModulo(), new Date(), true), "NO")));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		reasignarOrigen();

		/*
		 * FR906-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
		 * formularioAbrir 3, Me.Name DoCmd.Restore Me.Requery End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
		registro.getCampos().remove("NOMBRETIPO");
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
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		anioBaseVisible = false;
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

	@Override
	public void removerCombos() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
		registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
		registro.getCampos().remove("NOMBRETIPO");
		registro.getCampos().remove("NOMBREPOAI");

		// </CODIGO_DESARROLLADO>
	}

	@Override
	public void asignarValoresRegistro() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// <SET_GET_ATRIBUTOS>
	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public String getAnioBase() {
		return anioBase;
	}

	public void setAnioBase(String anioBase) {
		this.anioBase = anioBase;
	}

	public String getAnioDestino() {
		return anioDestino;
	}

	public void setAnioDestino(String anioDestino) {
		this.anioDestino = anioDestino;
	}

	public boolean isAnioBaseVisible() {
		return anioBaseVisible;
	}

	public void setAnioBaseVisible(boolean anioBaseVisible) {
		this.anioBaseVisible = anioBaseVisible;
	}

	public List<Registro> getListaanioBase() {
		return listaanioBase;
	}

	public void setListaanioBase(List<Registro> listaanioBase) {
		this.listaanioBase = listaanioBase;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>

	public List<Registro> getListaANO() {
		return listaANO;
	}

	public List<Registro> getListaTipo() {
		return listaTipo;
	}

	public void setListaTipo(List<Registro> listaTipo) {
		this.listaTipo = listaTipo;
	}

	public void setListaANO(List<Registro> listaANO) {
		this.listaANO = listaANO;
	}

	/**
	 * Retorna la lista listaCbCodigoSIA
	 * 
	 * @return listaCbCodigoSIA
	 */
	public List<Registro> getListaCbCodigoSIA() {
		return listaCbCodigoSIA;
	}

	/**
	 * Asigna la lista listaCbCodigoSIA
	 * 
	 * @param listaCbCodigoSIA
	 * Variable a asignar en listaCbCodigoSIA
	 */
	public void setListaCbCodigoSIA(List<Registro> listaCbCodigoSIA) {
		this.listaCbCodigoSIA = listaCbCodigoSIA;
	}

	public List<Registro> getListaanioDestino() {
		return listaanioDestino;
	}

	public void setListaanioDestino(List<Registro> listaanioDestino) {
		this.listaanioDestino = listaanioDestino;
	}

	public boolean isMostrarTipo() {
		return mostrarTipo;
	}

	public void setMostrarTipo(boolean mostrarTipo) {
		this.mostrarTipo = mostrarTipo;
	}

	public boolean isBotonaux() {
		return botonaux;
	}

	public void setBotonaux(boolean botonaux) {
		this.botonaux = botonaux;
	}

	public boolean isMostrarTipoPOAI() {
		return mostrarTipoPOAI;
	}

	public void setMostrarTipoPOAI(boolean mostrarTipoPOAI) {
		this.mostrarTipoPOAI = mostrarTipoPOAI;
	}

	/**
	 * Retorna la lista listaTipoFuentePOAI
	 * 
	 * @return listaTipoFuentePOAI
	 */
	public List<Registro> getListaTipoFuentePOAI() {
		return listaTipoFuentePOAI;
	}

	/**
	 * Asigna la lista listaTipoFuentePOAI
	 * 
	 * @param listaTipoFuentePOAI
	 * Variable a asignar en listaTipoFuentePOAI
	 */
	public void setListaTipoFuentePOAI(List<Registro> listaTipoFuentePOAI) {
		this.listaTipoFuentePOAI = listaTipoFuentePOAI;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaCodigoDnp
	 * 
	 * @return listaCodigoDnp
	 */
	public RegistroDataModelImpl getListaCodigoDnp() {
		return listaCodigoDnp;
	}

	/**
	 * Asigna la lista listaCodigoDnp
	 * 
	 * @param listaCodigoDnp
	 * Variable a asignar en listaCodigoDnp
	 */
	public void setListaCodigoDnp(RegistroDataModelImpl listaCodigoDnp) {
		this.listaCodigoDnp = listaCodigoDnp;
	}

	/**
	 * Retorna la lista listaCodigoDnp
	 * 
	 * @return listaCodigoDnp
	 */
	public RegistroDataModelImpl getListaCodigoDnpE() {
		return listaCodigoDnpE;
	}

	/**
	 * Retorna la lista listaEquivalentecuipo
	 * 
	 * @return listaEquivalentecuipo
	 */
	public RegistroDataModelImpl getListaEquivalentecuipo() {
		return listaEquivalentecuipo;
	}
	/**
	 * Asigna la lista listaEquivalentecuipo
	 * 
	 * @param listaEquivalentecuipo
	 * Variable a asignar en  listaEquivalentecuipo
	 */
	public void setListaEquivalentecuipo(RegistroDataModelImpl listaEquivalentecuipo) {
		this.listaEquivalentecuipo = listaEquivalentecuipo;
	}
	/**
	 * Retorna la lista listaEquivalentecuipo
	 * 
	 * @return listaEquivalentecuipo
	 */
	public RegistroDataModelImpl getListaEquivalentecuipoE() {
		return listaEquivalentecuipoE;
	}
	/**
	 * Asigna la lista listaEquivalentecuipo
	 * 
	 * @param listaEquivalentecuipo
	 * Variable a asignar en  listaEquivalentecuipo
	 */
	public void setListaEquivalentecuipoE(RegistroDataModelImpl listaEquivalentecuipoE) {
		this.listaEquivalentecuipoE = listaEquivalentecuipoE;
	}
	
	/**
	     * Retorna la lista listaReservaApropiacionEquivalente
	     * 
	     * @return listaReservaApropiacionEquivalente
	     */
	    public RegistroDataModelImpl getListaReservaApropiacionEquivalente() {
	        return listaReservaApropiacionEquivalente;
	    }
	    /**
	     * Asigna la lista listaReservaApropiacionEquivalente
	     * 
	     * @param listaReservaApropiacionEquivalente
	     * Variable a asignar en  listaReservaApropiacionEquivalente
	     */
	    public void setListaReservaApropiacionEquivalente(RegistroDataModelImpl listaReservaApropiacionEquivalente) {
	        this.listaReservaApropiacionEquivalente = listaReservaApropiacionEquivalente;
	    }
	    /**
	     * Retorna la lista listaReservaApropiacionEquivalente
	     * 
	     * @return listaReservaApropiacionEquivalente
	     */
	    public RegistroDataModelImpl getListaReservaApropiacionEquivalenteE() {
	        return listaReservaApropiacionEquivalenteE;
	    }
	    /**
	     * Asigna la lista listaReservaApropiacionEquivalente
	     * 
	     * @param listaReservaApropiacionEquivalente
	     * Variable a asignar en  listaReservaApropiacionEquivalente
	     */
	    public void setListaReservaApropiacionEquivalenteE(RegistroDataModelImpl listaReservaApropiacionEquivalenteE) {
	        this.listaReservaApropiacionEquivalenteE = listaReservaApropiacionEquivalenteE;
	    }
	    /**
	     * Retorna la lista listaReservaCajaEquivalente
	     * 
	     * @return listaReservaCajaEquivalente
	     */
	    public RegistroDataModelImpl getListaReservaCajaEquivalente() {
	        return listaReservaCajaEquivalente;
	    }
	    /**
	     * Asigna la lista listaReservaCajaEquivalente
	     * 
	     * @param listaReservaCajaEquivalente
	     * Variable a asignar en  listaReservaCajaEquivalente
	     */
	    public void setListaReservaCajaEquivalente(RegistroDataModelImpl listaReservaCajaEquivalente) {
	        this.listaReservaCajaEquivalente = listaReservaCajaEquivalente;
	    }
	    /**
	     * Retorna la lista listaReservaCajaEquivalente
	     * 
	     * @return listaReservaCajaEquivalente
	     */
	    public RegistroDataModelImpl getListaReservaCajaEquivalenteE() {
	        return listaReservaCajaEquivalenteE;
	    }
	    /**
	     * Asigna la lista listaReservaCajaEquivalente
	     * 
	     * @param listaReservaCajaEquivalente
	     * Variable a asignar en  listaReservaCajaEquivalente
	     */
	    public void setListaReservaCajaEquivalenteE(RegistroDataModelImpl listaReservaCajaEquivalenteE) {
	        this.listaReservaCajaEquivalenteE = listaReservaCajaEquivalenteE;
	    }
	
	
	
	/**
	 * Asigna la lista listaCodigoDnp
	 * 
	 * @param listaCodigoDnp
	 * Variable a asignar en listaCodigoDnp
	 */
	public void setListaCodigoDnpE(RegistroDataModelImpl listaCodigoDnpE) {
		this.listaCodigoDnpE = listaCodigoDnpE;
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
	// </SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * @return the manejaEquivalenteCUIPO
	 */
	public boolean isManejaEquivalenteCUIPO() {
		return manejaEquivalenteCUIPO;
	}

	/**
	 * @param manejaEquivalenteCUIPO the manejaEquivalenteCUIPO to set
	 */
	public void setManejaEquivalenteCUIPO(boolean manejaEquivalenteCUIPO) {
		this.manejaEquivalenteCUIPO = manejaEquivalenteCUIPO;
	}

	/**
	 * @return the listacodigoCleo
	 */
	public RegistroDataModelImpl getListacodigoCleo() {
		return listacodigoCleo;
	}

	/**
	 * @param listacodigoCleo the listacodigoCleo to set
	 */
	public void setListacodigoCleo(RegistroDataModelImpl listacodigoCleo) {
		this.listacodigoCleo = listacodigoCleo;
	}

	/**
	 * @return the listacodigoCleoE
	 */
	public RegistroDataModelImpl getListacodigoCleoE() {
		return listacodigoCleoE;
	}

	/**
	 * @param listacodigoCleoE the listacodigoCleoE to set
	 */
	public void setListacodigoCleoE(RegistroDataModelImpl listacodigoCleoE) {
		this.listacodigoCleoE = listacodigoCleoE;
	}
}
