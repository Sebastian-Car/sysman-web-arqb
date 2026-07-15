/*-
 * FrmNotasCreditoControlador.java
 *
 * 1.0
 * 
 * 08/05/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmNotasCreditoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;
/**
 *
 * @version 1.0, 08/05/2024
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmNotasCreditoControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private String claseorden;
	private String ordendecompra;
	private Registro listaValorTotal;
	@EJB
    private EjbSysmanUtilRemote sysmanUtil;
	private String vigencia;
	private String titulo;
	private HashMap<String, Object> ridContrato;
	private Map<String, Object> parametrosEntrada;
	private boolean aportantes;
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmNotasCreditoControlador
	 */
	public FrmNotasCreditoControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//2461
			numFormulario = GeneralCodigoFormaEnum.FRM_NOTAS_CREDITO_CONTROLADOR.getCodigo();
			validarPermisos();
			cargarFlash();
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
		enumBase = GenericUrlEnum.VLR_NOTA_CREDITO;
        buscarLlave();
        reasignarOrigen();
		registro= new Registro();
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){
		 buscarUrls();
		 parametrosListado.put("COMPANIA", compania);
		 parametrosListado.put("CLASEORDEN", claseorden);
		 parametrosListado.put("ORDENDECOMPRA", ordendecompra);
	}
	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	//</METODOS_BOTONES>
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
		registro.getCampos().put("FECHA", new Date());
		registro.getCampos().put("USUARIO", SessionUtil.getUser().getCodigo());
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
		 try {
			long cons = sysmanUtil.generarConsecutivoConValorInicial(
			         "VLR_NOTA_CREDITO",
			         "COMPANIA =''"+ compania + "''"
			             + " AND CLASEORDEN = ''" + claseorden + 
			             "'' AND ORDENDECOMPRA =" + ordendecompra,
			         "CODIGO", "1");

		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("CLASEORDEN", claseorden);
		registro.getCampos().put("ORDENDECOMPRA", ordendecompra);
		registro.getCampos().put("CODIGO", cons);
		
			} catch (SystemException e) {
				JsfUtil.agregarMensajeError(e.getMessage());
	            logger.error(e.getMessage(), e);
			}
		 
		   if (!validarValorTotal()) {

		        return false;
		    }
		 
		//</CODIGO_DESARROLLADO>
		return true;
	}
	
	
	public boolean validarValorTotal() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get("ORDENDECOMPRA"));
		param.put(GeneralParameterEnum.CLASEORDEN.getName(), registro.getCampos().get("CLASEORDEN"));

		try {
			listaValorTotal = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmNotasCreditoControladorUrlEnum.URL003.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}

		double valorNota = Double.parseDouble(listaValorTotal.getCampos().get("VALORNOTA").toString());
		double valorTotal = Double.parseDouble(listaValorTotal.getCampos().get("VALORTOTAL").toString());
		double valorCredito = Double.parseDouble(registro.getCampos().get("VALOR").toString());

		double suma = valorNota + valorCredito;

		if (suma > valorTotal) {
			
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB4462"));
			 return false;
		}
		return true;
	}
	
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		actualizarvalor();
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
		if (!validarValorTotal()) {
	        return false;
	    }
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		actualizarvalor();
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
		actualizarvalor();
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
	 * Metodo ejecutado cuando se cierra el formulario
	 * 
	 */
	public void cerrarFormulario() {

	}
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
		registro.getCampos().put("FECHA", new Date());
		registro.getCampos().put("USUARIO", SessionUtil.getUser().getCodigo());
		
	}

	/**
	 * Captura de las variables enviadas por flash.
	 */
	public void cargarFlash() {
		try {
			parametrosEntrada = SessionUtil.getFlash();

			           if (parametrosEntrada != null) {
			               claseorden = parametrosEntrada.get("claseorden")
			                               .toString();
			               ordendecompra = parametrosEntrada.get("ordendecompra")
			                               .toString();
			               vigencia = parametrosEntrada.get("vigencia")
	                               .toString();
			               titulo = parametrosEntrada.get("titulo")
	                               .toString();
			               
			               aportantes = Boolean.parseBoolean(SysmanFunciones.nvl(parametrosEntrada.get("aportantes"), 0)
                                   .toString());
			               
			               ridContrato = (HashMap<String, Object>) parametrosEntrada.get("ridPcontrato");

			           }
			           else {
			               JsfUtil.agregarMensajeError(idioma.getString("TB_TB3050"));
			               RequestContext.getCurrentInstance().closeDialog(null);
			           }
		}
		finally {
			SessionUtil.cleanFlash();
		}
	}
	
	public void actualizarvalor() {
		try {
		Double valor = null ;

        Map<String, Object> paramDisminuido = new HashMap<>();
        paramDisminuido.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        paramDisminuido.put(GeneralParameterEnum.CLASEORDEN.getName(), claseorden);
        paramDisminuido.put("ORDENDECOMPRA", ordendecompra);

        Registro rsReg;

			rsReg = RegistroConverter
			                .toRegistro(requestManager.get(
			                                UrlServiceUtil.getInstance()
			                                                .getUrlServiceByUrlByEnumID(
			                                                		FrmNotasCreditoControladorUrlEnum.URL001.getValue())
			                                                .getUrl(),
			                                paramDisminuido));
		

        if (rsReg != null)
        {

            valor = Double.parseDouble(
                            SysmanFunciones.nvl(rsReg.getCampos().get("VALOR"), "0").toString());
        }

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmNotasCreditoControladorUrlEnum.URL002.getValue());
        
        Map<String, Object> fields = new TreeMap<>();
        fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        fields.put("VALOR", valor);
        fields.put(GeneralParameterEnum.CLASEORDEN.getName(), claseorden);
        fields.put("ORDENDECOMPRA", ordendecompra);
        Parameter parameter = new Parameter();
        parameter.setFields(fields);
        requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
        
		} catch (SystemException e) {
			  JsfUtil.agregarMensajeError(e.getMessage());
	            logger.error(e.getMessage(), e);
		}
		
	}
	
	/**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar()
    {
    	  SessionUtil.cleanFlash();
          String ruta = "/pcontrato.sysman";
          Map<String, Object> parametros = new HashMap<>();
          parametros.put("vigencia", vigencia);
          parametros.put("claseF", claseorden);
          parametros.put("titulo", titulo);
          parametros.put("ridPcontrato", ridContrato);
          parametros.put("convenio", aportantes);

          Direccionador direccionador = new Direccionador();
          direccionador.setRuta(ruta);
          direccionador.setParametros(parametros);
          SessionUtil.redireccionar(direccionador);

    }
	/**
	 * @return the parametrosEntrada
	 */
	public Map<String, Object> getParametrosEntrada() {
		return parametrosEntrada;
	}
	/**
	 * @param parametrosEntrada the parametrosEntrada to set
	 */
	public void setParametrosEntrada(Map<String, Object> parametrosEntrada) {
		this.parametrosEntrada = parametrosEntrada;
	}
	public Registro getListaValorTotal() {
		return listaValorTotal;
	}
	public void setListaValorTotal(Registro listaValorTotal) {
		this.listaValorTotal = listaValorTotal;
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
