/*-
 * FrmMonitortramitesadminsControlador.java
 *
 * 1.0
 * 
 * 26/04/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.workflow.enums.FrmMonitortramitesadminsControladorUrlEnum;
import com.sysman.workflow.enums.FrmTramitesControladorEnum;
import com.sysman.workflow.enums.FrmmonitortramitesControladorEnum;
import com.sysman.workflow.enums.FrmmonitortramitesControladorUrlEnum;
/**
 *
 * @version 1.0, 26/04/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmMonitortramitesadminsControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String usuario;
	private String dependencia;

	/**
	 * Constante a nivel de clase que aloja la cadena: USUARIO_INTERNO
	 */
	private final String cUsuarioInterno = FrmmonitortramitesControladorEnum.USUARIO_INTERNO
			.getValue();

	/** Constante a nivel de clase que aloja la cadena: PROCESOS */
	private final String cProcesos = FrmmonitortramitesControladorEnum.PROCESOS
			.getValue();

	/**
	 * Constante a nivel de clase que aloja la cadena: TIPO_TRAMITE
	 */
	private final String cTipoTramite = GeneralParameterEnum.TIPO_TRAMITE
			.getName();

	/** Constante a nivel de clase que aloja la cadena: NUMERO */
	private final String cNumero = GeneralParameterEnum.NUMERO.getName();
	private String anio;
	private String fechaInicial;
	private String fechaFinal;
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmMonitortramitesadminsControlador
	 */
	public FrmMonitortramitesadminsControlador() {
		super();
		compania = SessionUtil.getCompania();
		usuario = SessionUtil.getUser().getCodigo();
		try {
			numFormulario=2264;
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
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
		traerParametrosEntrada();
		tabla = GenericUrlEnum.TRAMITES.getTable();
		registro = new Registro();
		reasignarOrigen();
		buscarLlave();
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}
	
	/**
     * Trae parametros enviados por flash.
     */
    private void traerParametrosEntrada() {
        Map<String, Object> parametrosEntrada = new HashMap<>();
        parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            anio = extraerString(parametrosEntrada.get("anio"));
            fechaInicial = extraerString(parametrosEntrada.get("fechaInicial"));
            fechaFinal = extraerString(parametrosEntrada.get("fechaFinal"));
        }
    }
        
        
        /**
         * Extrae la cadena que representa al objeto, solo si es diferente
         * de nulo.
         *
         * @param object
         * Un Objeto
         * @return String que representa al objeto
         */
        private String extraerString(Object object) {
            return object != null ? object.toString() : null;
        }
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ANIO.getName(), anio);
		parametrosListado.put("FECHAINI", fechaInicial);
		parametrosListado.put("FECHAFIN", fechaFinal);

		if(nivelU().equals("1")) {

			urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
					FrmMonitortramitesadminsControladorUrlEnum.URL002.getValue());

		}else {

			buscarJefeDependencia();
			parametrosListado.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);
			urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
					FrmMonitortramitesadminsControladorUrlEnum.URL001.getValue());

		}

	}
	//<METODOS_CARGAR_LISTA>
	public void buscarJefeDependencia() {
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CODIGO.getName(), usuario);

		try {
			Registro rsDependencia = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmMonitortramitesadminsControladorUrlEnum.URL003.getValue())
							.getUrl(),
							param));

			if (rsDependencia != null) {
				dependencia = rsDependencia.getCampos().get(GeneralParameterEnum.DEPENDENCIA.getName()).toString();
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}  

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * Metodo ejecutado al oprimir el boton Ver
	 * 
	 * 
	 * @param reg
	 * registro en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 * @param indice
	 * indice en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 */
	public void oprimirVer(Registro reg, int indice) {
		//<CODIGO_DESARROLLADO>
		Registro registroTramites = new Registro();
		Direccionador direccionador = new Direccionador();

		Map<String, Object> param = new TreeMap<>();

		registroTramites.getCampos()
		.put(FrmmonitortramitesControladorEnum.KEY_COMPANIA
				.getValue(), compania);
		registroTramites.getCampos()
		.put(FrmmonitortramitesControladorEnum.KEY_PROCESOS
				.getValue(),
				reg.getCampos().get(cProcesos));
		registroTramites.getCampos()
		.put(FrmmonitortramitesControladorEnum.KEY_TIPO_TRAMITE
				.getValue(),
				reg.getCampos().get(cTipoTramite));
		registroTramites.getCampos()
		.put(FrmmonitortramitesControladorEnum.KEY_NUMERO
				.getValue(),
				reg.getCampos().get(cNumero));
		param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(),
				registroTramites.getCampos());

		param.put(FrmTramitesControladorEnum.PR_VER_DESDE_MONITOR.getValue(),
				true);

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR
						.getCodigo()));

		direccionador.setParametros(param);

		SessionUtil.redireccionarForma(direccionador,
				SessionUtil.getModulo());
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton IndicadorDHabil
	 * 
	 * 
	 * @param reg
	 * registro en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 * @param indice
	 * indice en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 */
	public void oprimirIndicadorDHabil(Registro reg, int indice) {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton IndicadorDCalend
	 * 
	 * 
	 * @param reg
	 * registro en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 * @param indice
	 * indice en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 */
	public void oprimirIndicadorDCalend(Registro reg, int indice) {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	public String nivelU() {
		Map<String, Object> param = new HashMap<>();
		param.put(cUsuarioInterno, usuario);
		String nivel="9";

		try {
			Registro rsUsuario = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmMonitortramitesadminsControladorUrlEnum.URL004.getValue())
							.getUrl(),
							param));

			if (rsUsuario != null) {
				nivel = rsUsuario.getCampos().get("NIVEL_USUARIO").toString();
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}  
		return nivel;
	}
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
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
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override    
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override   
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion,
	 * en el se pueden remover valores auxiliares que no se desee o se
	 * deban enviar en el registro
	 */
	@Override
	public void removerCombos() {
	}
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
		// TODO Auto-generated method stub
	}
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
