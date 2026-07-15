/*-
 * FrmreclasificacionmensualControlador.java
 *
 * 1.0
 * 
 * 29/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.enums.CambioscodigosControladorEnum;
import com.sysman.contabilidad.enums.ReclasificacionMensualControladorEnum;
import com.sysman.contabilidad.enums.ReclasificacionMensualControladorUrlEnum;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
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
import com.sysman.util.SysmanFunciones;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 29/06/2023
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrmreclasificacionmensualControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 * Constante definida por el numero de veces que se realiza el llamado a la
	 * tabla "RECLASIFICAR_NIIF_MENSUAL" en el formulario.
	 */
	private final String strTabla;
	/**
	 * Constante definida por el numero de veces que se realiza el llamado al campo
	 * CODIGO en el formulario, almacena el texto CODIGO el cual es un campo del
	 * registro
	 */
	private final String strCodigo;
	/**
	 * Constante definida por el numero de veces que se realiza el llamado a la
	 * tabla "D_RECLASIFICAR_NIIF_MENSUAL" en el formulario.
	 */
	private final String strTablaDetalles;
	/**
	 * Constante definida por el numero de veces que se realiza el llamado al campo
	 * NOMBRE en el formulario.
	 */
	private final String strNombre;
	/**
	 * Constante definida por el numero de veces que se realiza el llamado al campo
	 * NUMERO en el formulario.
	 */
	private final String strNumero;
	/**
	 * Atributo que almacena el anio del plan contable seleccionado en el formulario
	 */
	private String anio;
	
	/**
     * Variable que indica si el cobro ya fue facturado para habilitar
     * edici�n de formulario
     */
    private boolean realizado;
    
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAno;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoAnterior;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoAnteriorE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoNuevo;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoNuevoE;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaTipo;
	
	private RegistroDataModelImpl listaTipoE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	/**
     * Este atributo se usa como auxiliar para subformularios y
     * almacena el nombre del codigo anterior que se selecciona
     */
    private String nombreAnterior;
    /**
     * Este atributo se usa como auxiliar para subformularios y
     * almacena el nombre del codigo nuevo que se selecciona
     */
    private String nombreNuevo;
    
	private RegistroDataModelImpl listaRelacioncodigos;

	/**
	 * Atributo de referencia para el subformulario
	 */
	private Registro registroSub;
	
//</DECLARAR_ADICIONALES>

	/**
	 * variable EJB
	 */
	@EJB
	EjbSysmanUtilRemote ejbSysmanUtil;
	/**
	 * variable EJB
	 */
	@EJB
	EjbContabilidadTresRemote ejbContabilidadTres;

	/**
	 * Crea una nueva instancia de FrmreclasificacionmensualControlador
	 */
	public FrmreclasificacionmensualControlador() {
		super();
		compania = SessionUtil.getCompania();
		strTabla = ReclasificacionMensualControladorEnum.RECLASIFICAR_NIIF_MENSUAL.getValue();
		strTablaDetalles = ReclasificacionMensualControladorEnum.D_RECLASIFICAR_NIIF_MENSUAL.getValue();
		strCodigo = GeneralParameterEnum.CODIGO.getName();
		strNombre = GeneralParameterEnum.NOMBRE.getName();
		strNumero = GeneralParameterEnum.NUMERO.getName();
		realizado = false;
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_RECLASIFICACION_MENSUAL_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			registroSub = new Registro(new HashMap<String, Object>());

			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		cargarListaAno();
		cargarListaTipo();
		cargarListaTipoE();
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		anio = registro.getCampos().get(GeneralParameterEnum.ANO.getName())
                .toString();
		cargarListaRelacioncodigos();
		cargarListaCodigoAnterior();
		cargarListaCodigoAnteriorE();		
		cargarListaCodigoNuevo();
		cargarListaCodigoNuevoE();
	}

	/**
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		listaRelacioncodigos = null;
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.RECLASIFICAR_NIIF_MENSUAL;
		buscarLlave();
		asignarOrigenDatos();
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
	}

	/**
	 * 
	 * Carga la lista listaD_cambiosdenombres, la cual almacena los registros del
	 * suformulario
	 */
	public void cargarListaRelacioncodigos() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
		param.put(ReclasificacionMensualControladorEnum.STRNUMERO.getValue(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		
		try {
			String urlEnumId = GenericUrlEnum.D_RECLASIFICAR_NIIF_MENSUAL.getGridKey();
			UrlBean urlSelectSub = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
			
			String[] rowKey = CacheUtil.getLlaveServicio(urlConexionCache,  GenericUrlEnum.D_RECLASIFICAR_NIIF_MENSUAL.getTable());
			
			listaRelacioncodigos = new RegistroDataModelImpl(
					urlSelectSub.getUrl(), 
					urlSelectSub.getUrlConteo().getUrl(),
                    param, rowKey);
		} catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista de registros de anios
	 */
	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ReclasificacionMensualControladorUrlEnum.URL10958.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Carga la lista listaCodigoAnterior
	 */
	public void cargarListaCodigoAnterior() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionMensualControladorUrlEnum.URL12212.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ReclasificacionMensualControladorEnum.ANIO.getValue(), anio);

		listaCodigoAnterior = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				strCodigo);
	}

	/**
	 * 
	 * Carga la lista listaCodigoAnterior
	 */
	public void cargarListaCodigoAnteriorE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionMensualControladorUrlEnum.URL12212.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ReclasificacionMensualControladorEnum.ANIO.getValue(), anio);

		listaCodigoAnteriorE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				strCodigo);
	}

	/**
	 * 
	 * Carga el listado con los tipos de comprobantes
	 */
	public void cargarListaTipo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionMensualControladorUrlEnum.URL11528.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CLASECONTABLE.getName(), "C");

		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				strCodigo);

	}

	/**
	 * 
	 * Carga la lista listaTipo al realizar una edicion en el subformulario
	 */
	public void cargarListaTipoE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionMensualControladorUrlEnum.URL11528.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTipoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				strCodigo);
	}

	/**
	 * 
	 * Carga la lista listaCodigoNuevo
	 */
	public void cargarListaCodigoNuevo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionMensualControladorUrlEnum.URL12212.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ReclasificacionMensualControladorEnum.ANIO.getValue(), anio);

		listaCodigoNuevo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				strCodigo);

	}

	/**
	 * 
	 * Carga la lista listaCodigoNuevo
	 */
	public void cargarListaCodigoNuevoE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionMensualControladorUrlEnum.URL12212.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ReclasificacionMensualControladorEnum.ANIO.getValue(), anio);

		listaCodigoNuevoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				strCodigo);

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>
	public void cambiarAno() {
		anio = registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString();
		try {

			String[] parametros = { "COMPANIA = ''" + compania + "'' AND ANO = " + anio };			
			String consecutivo = Long.toString(ejbSysmanUtil.generarConsecutivoConValorInicial(strTabla,
					SysmanFunciones.concatenar(parametros), strNumero, "1"));
			if(consecutivo.equals("1"))
			{
				consecutivo = anio+StringUtils.leftPad(consecutivo, 6, '0');
			}				
			registro.getCampos().put(strNumero, consecutivo );
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Metodo ejecutado al cambiar el control CodigoAnterior en la fila seleccionada
	 * dentro de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarCodigoAnteriorC(int rowNum) {

		listaRelacioncodigos.getDatasource().get(rowNum % 10).getCampos()
				.put(ReclasificacionMensualControladorEnum.NOMBREANTERIOR.getValue(), nombreAnterior);
	}

	/**
	 * Metodo ejecutado al cambiar el control CodigoNuevo en la fila seleccionada
	 * dentro de la grilla
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarCodigoNuevoC(int rowNum) {
		listaRelacioncodigos.getDatasource().get(rowNum % 10).getCampos().put(ReclasificacionMensualControladorEnum.NOMBRENUEVO.getValue(),
				nombreNuevo);
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoAnterior.
	 * Actualiza los valores de los campos CODIGOANTERIOR y NOMBREANTERIOR en el
	 * registro, de acuerdo a las opciones seleccionadas para realizar la insercion
	 * de un registro en el subformulario
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoAnterior(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put(ReclasificacionMensualControladorEnum.CODIGOANTERIOR.getValue(),
				registroAux.getCampos().get(strCodigo));
		registroSub.getCampos().put(ReclasificacionMensualControladorEnum.NOMBREANTERIOR.getValue(),
				registroAux.getCampos().get(strNombre));
	}

	/**
	 * metodo que valida el casteo a toString
	 * 
	 * @param campos
	 * @param var
	 * @return
	 */
	private String cadenaVacia(Registro campos, String var) {
		return SysmanFunciones.validarCampoVacio(campos.getCampos(), var) ? null
				: campos.getCampos().get(var).toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoAnterior.
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoAnteriorE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = cadenaVacia(registroAux, strCodigo);
		nombreAnterior = cadenaVacia(registroAux, strNombre);
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipo *
	 * 
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(ReclasificacionMensualControladorEnum.TIPO.getValue(),
				registroAux.getCampos().get(strCodigo));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipo cuandos se
	 * esta modificando un registro en el subformulario
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = cadenaVacia(registroAux, strCodigo);
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoNuevo.
	 * Actualiza los valores de los campos CODIGONUEVO y NOMBRENUEVO en el registro,
	 * de acuerdo a las opciones seleccionadas para realizar la insercion de un
	 * registro en el subformulario
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoNuevo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put(ReclasificacionMensualControladorEnum.CODIGONUEVO.getValue(),
				registroAux.getCampos().get(strCodigo));
		registroSub.getCampos().put(ReclasificacionMensualControladorEnum.NOMBRENUEVO.getValue(),
				registroAux.getCampos().get(strNombre));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoNuevo
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoNuevoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = cadenaVacia(registroAux, strCodigo);
		nombreNuevo = cadenaVacia(registroAux, strNombre);
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	/**
	 * Valida si una reclasificacion ha sido realizada o no mediante el
	 */
	public void validarRealizado() {

		if (css != null) {
			if ((Boolean) registro.getCampos().get(ReclasificacionMensualControladorEnum.REALIZADO.getValue())) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2686"));
			}
		} else {
			agregarRegistroNuevo(false);
		}

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cambio en la vista
	 *
	 * Crea la cuenta contable cuando no existe
	 *
	 */
	public void oprimirCambio() {
		// <CODIGO_DESARROLLADO>
		if (!SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.ANO.getName())) {
			validarRealizado();
			if (listaRelacioncodigos.getDatasource().isEmpty()) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2711"));
			} else {
				procedimiento();
				cargarRegistro(registro.getLlave(), accion, registro.getIndice());
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1637"));

			}
		}

	}

	private void procedimiento() {
		try {
			String ano = registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString();
			String numero = registro.getCampos().get(strNumero).toString();
			Date fecha = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
			String usuario = registro.getCampos().get(GeneralParameterEnum.USUARIO.getName()).toString();
			String tipo = registro.getCampos().get(GeneralParameterEnum.TIPO.getName()).toString();
		
			ejbContabilidadTres.reclasificarNiifMensual(compania, Integer.parseInt(ano), tipo, Integer.parseInt(numero),
					Long.parseLong("1"), fecha, usuario);

		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Metodo de insercion del subformulario D_cambiosdenombres
	 * 
	 */
	public void agregarRegistroSubRelacioncodigos() {
		try {
			registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(),
					registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
			registroSub.getCampos().put(strNumero, registro.getCampos().get(strNumero));
			registroSub.getCampos().put(ReclasificacionMensualControladorEnum.CONSECUTIVO.getValue(),
					ejbSysmanUtil.generarConsecutivoConValorInicial(strTablaDetalles, "COMPANIA=''" + compania + "'' AND ANO="+ anio +" AND NUMERO="+registro.getCampos().get(strNumero).toString(),
							ReclasificacionMensualControladorEnum.CONSECUTIVO.getValue(), "1"));

			registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
			registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
			registroSub.getCampos().remove(GeneralParameterEnum.DATE_MODIFIED.getName());
			registroSub.getCampos().remove(GeneralParameterEnum.MODIFIED_BY.getName());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.D_RECLASIFICAR_NIIF_MENSUAL.getCreateKey());

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSub.getCampos());
			cargarListaRelacioncodigos();
			JsfUtil.agregarMensajeInformativo(
					idioma.getString(ReclasificacionMensualControladorEnum.MSM_REGISTRO_INGRESADO.getValue()));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			registroSub = new Registro(new HashMap<String, Object>());
		}
	}

	/**
	 * Metodo de edicion del subformulario D_cambiosdenombres
	 * 
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubRelacioncodigos(RowEditEvent event) 
	{
		Registro reg = (Registro) event.getObject();
        try
        {
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().remove(ReclasificacionMensualControladorEnum.KEY_COMPANIA
                            .getValue());
            reg.getCampos().remove(
            		ReclasificacionMensualControladorEnum.KEY_ANO.getValue());
            reg.getCampos().remove(ReclasificacionMensualControladorEnum.KEY_NUMERO
                            .getValue());
            reg.getCampos().remove(ReclasificacionMensualControladorEnum.KEY_CONSECUTIVO
                            .getValue());
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.ANO.getName());
            reg.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
            reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
            reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_RECLASIFICAR_NIIF_MENSUAL
                                                            .getUpdateKey());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                            		ReclasificacionMensualControladorEnum.MSM_REGISTRO_MODIFICADO
                                                            .getValue()));

        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaRelacioncodigos();
        }
	}

	/**
	 * Metodo de eliminacion del suformulario D_cambiosdenombres
	 * 
	 * @param reg registro seleccionado en el subformulario
	 */
	public void eliminarRegSubRelacioncodigos(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.D_RECLASIFICAR_NIIF_MENSUAL.getDeleteKey());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(
					idioma.getString(ReclasificacionMensualControladorEnum.MSM_REGISTRO_ELIMINADO.getValue()));

			cargarListaRelacioncodigos();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario D_cambiosdenombres
	 *
	 */
	public void cancelarEdicionRelacioncodigos() {
		cargarListaRelacioncodigos();
	}

	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>
	// </METODOS_ADICIONALES>
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
	 * Metodo ejecutado en el momento despues de cargar el registro Asigna valores
	 * por omision cuando se va a insetar un nuevo registro
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>

		precargarRegistro();
		realizado = false;				
		
		if (css != null) {
			anio = registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString();
			if ((boolean) registro.getCampos().get(ReclasificacionMensualControladorEnum.REALIZADO.getValue()))
	        {
	            realizado = true;
	        }
		} else {
			try {
				anio = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
				String[] parametros = { "COMPANIA = ''" + compania + "'' AND ANO = " + anio };				
				registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
				String consecutivo = Long.toString(ejbSysmanUtil.generarConsecutivoConValorInicial(strTabla,
						SysmanFunciones.concatenar(parametros), strNumero, "1"));
				if(consecutivo.equals("1"))
				{
					consecutivo = anio+StringUtils.leftPad(consecutivo, 6, '0');
				}				
				registro.getCampos().put(strNumero, consecutivo );
				registro.getCampos().put("FECHA", new Date());
				registro.getCampos().put(GeneralParameterEnum.USUARIO.getName(), SessionUtil.getUser().getCodigo());				
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 * @return VARIABLE
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		if (ACCION_INSERTAR.equals(accion)) {
			registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registro.getCampos().remove(GeneralParameterEnum.MODIFIED_BY.getName());
			registro.getCampos().remove(GeneralParameterEnum.DATE_MODIFIED.getName());
			registro.getCampos().remove("KEY_COMPANIA");
			registro.getCampos().remove("KEY_NUMERO");
			registro.getCampos().remove("KEY_ANO");
			registro.getCampos().remove("REALIZADO");

		}
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 * @return VARIABLE
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 * 
	 * @return VARIABLE
	 */
	@Override
	public boolean actualizarAntes() {
		if (ACCION_MODIFICAR.equals(accion)) {
			registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
			registro.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
			registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		}

		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * @return VARIABLE
	 */
	@Override
	public boolean actualizarDespues() {
		// heredado del bean base
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 * @return VARIABLE
	 */
	@Override
	public boolean eliminarAntes() {
		// heredado del bean base
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 * @return VARIABLE
	 */
	@Override
	public boolean eliminarDespues() {
		// heredado del bean base
		return true;
	}

	// set y get
	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

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
	 * Retorna la lista listaCodigoAnterior
	 * 
	 * @return listaCodigoAnterior
	 */
	public RegistroDataModelImpl getListaCodigoAnterior() {
		return listaCodigoAnterior;
	}

	/**
	 * Asigna la lista listaCodigoAnterior
	 * 
	 * @param listaCodigoAnterior Variable a asignar en listaCodigoAnterior
	 */
	public void setListaCodigoAnterior(RegistroDataModelImpl listaCodigoAnterior) {
		this.listaCodigoAnterior = listaCodigoAnterior;
	}

	/**
	 * Retorna la lista listaCodigoAnterior
	 * 
	 * @return listaCodigoAnterior
	 */
	public RegistroDataModelImpl getListaCodigoAnteriorE() {
		return listaCodigoAnteriorE;
	}

	/**
	 * Asigna la lista listaCodigoAnterior
	 * 
	 * @param listaCodigoAnterior Variable a asignar en listaCodigoAnterior
	 */
	public void setListaCodigoAnteriorE(RegistroDataModelImpl listaCodigoAnteriorE) {
		this.listaCodigoAnteriorE = listaCodigoAnteriorE;
	}

	/**
	 * Retorna la lista listaTipo
	 * 
	 * @return listaTipo
	 */
	public RegistroDataModelImpl getListaTipo() {
		return listaTipo;
	}

	/**
	 * Asigna la lista listaTipo
	 * 
	 * @param listaTipo Variable a asignar en listaTipo
	 */
	public void setListaTipo(RegistroDataModelImpl listaTipo) {
		this.listaTipo = listaTipo;
	}

	/**
	 * Retorna la lista listaTipo
	 * 
	 * @return listaTipo
	 */
	public RegistroDataModelImpl getListaTipoE() {
		return listaTipoE;
	}

	/**
	 * Asigna la lista listaTipo
	 * 
	 * @param listaTipo Variable a asignar en listaTipo
	 */
	public void setListaTipoE(RegistroDataModelImpl listaTipoE) {
		this.listaTipoE = listaTipoE;
	}

	/**
	 * Retorna la lista listaCodigoNuevo
	 * 
	 * @return listaCodigoNuevo
	 */
	public RegistroDataModelImpl getListaCodigoNuevo() {
		return listaCodigoNuevo;
	}

	/**
	 * Asigna la lista listaCodigoNuevo
	 * 
	 * @param listaCodigoNuevo Variable a asignar en listaCodigoNuevo
	 */
	public void setListaCodigoNuevo(RegistroDataModelImpl listaCodigoNuevo) {
		this.listaCodigoNuevo = listaCodigoNuevo;
	}

	/**
	 * Retorna la lista listaCodigoNuevo
	 * 
	 * @return listaCodigoNuevo
	 */
	public RegistroDataModelImpl getListaCodigoNuevoE() {
		return listaCodigoNuevoE;
	}

	/**
	 * Asigna la lista listaCodigoNuevo
	 * 
	 * @param listaCodigoNuevo Variable a asignar en listaCodigoNuevo
	 */
	public void setListaCodigoNuevoE(RegistroDataModelImpl listaCodigoNuevoE) {
		this.listaCodigoNuevoE = listaCodigoNuevoE;
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
	 * @param auxiliar Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>
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
	 * @param registroSub Variable a asignar en registroSub
	 */
	public void setRegistroSub(Registro registroSub) {
		this.registroSub = registroSub;
	}

	/**
	 * @return the listaRelacioncodigos
	 */
	public RegistroDataModelImpl getListaRelacioncodigos() {
		return listaRelacioncodigos;
	}

	/**
	 * @param listaRelacioncodigos the listaRelacioncodigos to set
	 */
	public void setListaRelacioncodigos(RegistroDataModelImpl listaRelacioncodigos) {
		this.listaRelacioncodigos = listaRelacioncodigos;
	}
	
	public boolean isRealizado() {
		return realizado;
	}

	public void setRealizado(boolean realizado) {
		this.realizado = realizado;
	}
}
