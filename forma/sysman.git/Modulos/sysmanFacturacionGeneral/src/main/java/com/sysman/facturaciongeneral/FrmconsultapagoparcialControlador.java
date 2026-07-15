/*-
 * FrmconsultapagoparcialControlador.java
 *
 * 1.0
 * 
 * 31/01/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmconsultapagoparcialControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 31/01/2025
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class FrmconsultapagoparcialControlador extends BeanBaseDatosAcmeImpl{

	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ; 
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	//<DECLARAR_LISTAS_SUBFORM>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaSubfrmconsultapagosp;
	//</DECLARAR_LISTAS_SUBFORM>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_ADICIONALES>
	/**
	 * Atributo de referencia para el subformulario 
	 */
	private Registro registroSub;
	/**
     * Variable encargada de almacenar temporalmente el tipo de cobro
     * con el que se accesde al modulo de facturacion general
     */
    private String tipo;
    /**
     * Variable encargada de almacenar temporalmente el ano de cobro
     * con el que se accesde al modulo de facturacion general
     */
    private String ano;
	//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de FrmconsultapagoparcialControlador
	 */
	public FrmconsultapagoparcialControlador() {
		super();

		Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            rid = (Map<String, Object>) parametros.get("rid");
        }
        compania = SessionUtil.getCompania();
        ano = SysmanFunciones.nvl(SessionUtil.getSessionVar(
                ConstantesFacturacionGenEnum.ANIO.getValue()), "").toString();
        tipo = SysmanFunciones.nvl(SessionUtil.getSessionVar(
                ConstantesFacturacionGenEnum.TIPOCOBRO.getValue()), "").toString();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRMCONSULTAPAGOSPARCIALES_CONTROLADOR.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			registroSub = new Registro(new HashMap<String, Object>());
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas(){
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub(){
		//<CARGAR_LISTAS_SUBFORM>
		cargarListaSubfrmconsultapagosp();
		//</CARGAR_LISTAS_SUBFORM>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
	}
	/**
	 * En este metodo se iguala a null todas las listas de los
	 * subformularios
	 */
	@Override
	public void iniciarListasSubNulo(){
		//<CARGAR_LISTAS_SUBFORM_NULL>
		listaSubfrmconsultapagosp = null;
		//</CARGAR_LISTAS_SUBFORM_NULL>
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
		enumBase = GenericUrlEnum.SF_PAGOS_PARCIALES;
		buscarLlave();
		asignarOrigenDatos();
	}
	/**
	 * Se realiza la asignacion de la variable origenDatos por la
	 * consulta correspondiente del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {

		buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        parametrosListado.put(GeneralParameterEnum.TIPO.getName(), tipo);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);	
	}
	
	/**
	 * 
	 * Carga la lista listaSubfrmconsultapagosp
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaSubfrmconsultapagosp(){
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPO.getName(), tipo);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put("NUMERO_FACTURA", registro.getCampos().get("NUMERO_FACTURA"));
		try {
			listaSubfrmconsultapagosp = RegistroConverter.toListRegistro(
                    requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                            		FrmconsultapagoparcialControladorUrlEnum.URL1955001
                                                            .getValue())
                            .getUrl(), param),CacheUtil.getLlaveServicio(urlConexionCache,"SF_DETALLE_ABONO"));
		}
		catch (SystemException | SysmanException e)
		{
			logger.error(e.getMessage(),e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//<METODOS_CARGAR_LISTA>	
	//</METODOS_CARGAR_LISTA>
	//<METODOS_CAMBIAR>	
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>	
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>	
	//</METODOS_ARBOL>
	//<METODOS_BOTONES>	
	//</METODOS_BOTONES>	
	//<METODOS_SUBFORM>	
	/**
	 * Metodo de insercion del formulario Subfrmconsultapagosp
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */   
	public void agregarRegistroSubSubfrmconsultapagosp() {
		
	}
	/**
	 * Metodo de edicion del formulario Subfrmconsultapagosp
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubSubfrmconsultapagosp(RowEditEvent event) {
		
	}
	/**
	 * Metodo de eliminacion del formulario Subfrmconsultapagosp
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param reg
	 * registro seleccionado en el subformulario
	 */
	public void eliminarRegSubSubfrmconsultapagosp(Registro reg) {
		
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 * para el subformulario Subfrmconsultapagosp
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cancelarEdicionSubfrmconsultapagosp(){
		cargarListaSubfrmconsultapagosp();
	}
	//</METODOS_SUBFORM>	
	//<METODOS_ADICIONALES>	
	//</METODOS_ADICIONALES>
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
		 * Metodo ejecutado en el momento despues de cargar el registro
		 * 
		 * TODO DOCUMENTACION ADICIONAL
		 */
		@Override
		public void cargarRegistro() {
			//<CODIGO_DESARROLLADO>
			precargarRegistro();
			//</CODIGO_DESARROLLADO>
		}
		/**
		 * Metodo ejecutado antes de realizar la insercion del registro
		 * TODO DOCUMENTACION ADICIONAL
		 * 
		 * @return TODO VARIABLE
		 */
		@Override
		public boolean insertarAntes(){
			//<CODIGO_DESARROLLADO>
			registro.getCampos().put("COMPANIA", compania);
			//</CODIGO_DESARROLLADO>
			return true;
		}
		/**
		 * Metodo ejecutado despues de realizar la insercion del registro
		 * TODO DOCUMENTACION ADICIONAL
		 * 
		 * @return TODO VARIABLE
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
		 * TODO DOCUMENTACION ADICIONAL
		 * 
		 * @return TODO VARIABLE
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
		 * TODO DOCUMENTACION ADICIONAL
		 * 
		 * @return TODO VARIABLE
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
		 * TODO DOCUMENTACION ADICIONAL
		 * 
		 * @return TODO VARIABLE
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
		 * TODO DOCUMENTACION ADICIONAL
		 * 
		 * @return TODO VARIABLE
		 */
		@Override
		public boolean eliminarDespues(){
			//<CODIGO_DESARROLLADO>
			//</CODIGO_DESARROLLADO>
			return true;
		}
		//<SET_GET_ATRIBUTOS>
		//</SET_GET_ATRIBUTOS>
		//<SET_GET_LISTAS>
		//</SET_GET_LISTAS>
		//<SET_GET_LISTAS_COMBO_GRANDE>	
		//</SET_GET_LISTAS_COMBO_GRANDE>
		//<SET_GET_LISTAS_SUBFORM>
		/**
		 * Retorna la lista listaSubfrmconsultapagosp
		 * 
		 * @return listaSubfrmconsultapagosp
		 */
		public List<Registro> getListaSubfrmconsultapagosp() {
			return listaSubfrmconsultapagosp;
		}
		/**
		 * Asigna la lista listaSubfrmconsultapagosp
		 * 
		 * @param listaSubfrmconsultapagosp
		 * Variable a asignar en  listaSubfrmconsultapagosp
		 */
		public void setListaSubfrmconsultapagosp(List<Registro> listaSubfrmconsultapagosp) {
			this.listaSubfrmconsultapagosp = listaSubfrmconsultapagosp;
		}
		//</SET_GET_LISTAS_SUBFORM>
		//<SET_GET_PARAMETROS>
		//</SET_GET_PARAMETROS>
		//<SET_GET_ADICIONALES>	
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
		//</SET_GET_ADICIONALES>
	}
