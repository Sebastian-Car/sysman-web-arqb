/*-
 * FrmproductodistribucioncostosControlador.java
 *
 * 1.0
 * 
 * 03/07/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.FrmproductodistribucioncostosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
/**
 * @version 1.0, 03/07/2024
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  FrmproductodistribucioncostosControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	//<DECLARAR_ATRIBUTOS>
	private int ano;
	private String sumaTotalFactor;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaANO;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmproductodistribucioncostosControlador
	 */
	public FrmproductodistribucioncostosControlador() {
		super();
		compania = SessionUtil.getCompania();
		ano = SysmanFunciones.ano(new Date());
		try {
				numFormulario = GeneralCodigoFormaEnum.FRM_PRODUCTO_DISTRIBUCION_COSTOS
	                    .getCodigo();
				validarPermisos();
			}
			catch (SysmanException ex)
			{
	            SessionUtil.redireccionarMenuPermisos();
	            Logger.getLogger(FrmproductodistribucioncostosControlador.class.getName())
	                            .log(Level.SEVERE, null, ex);
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
		
			enumBase = GenericUrlEnum.DC_PRODUCTO_SUI;
			buscarLlave();
			reasignarOrigen();		    
			registro= new Registro();
			abrirFormulario();
			cargarListaANO();
	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){
		
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                ano);
	}
	
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaANO
	 */
	public void cargarListaANO(){
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaANO = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmproductodistribucioncostosControladorUrlEnum.URL4001
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_CARGAR_LISTA>

	/**
	 * Metodo ejecutado al cambiar el control ANO
	 */
	public void cambiarANO() {
		
		if (ano == 0) {
			JsfUtil.agregarMensajeAlerta(
					idioma.getString("TB_TB2680").replace("#ANIO#",
							Integer.toString(ano)));
		}
		reasignarOrigen();
		cargarValores();
		
	}
	
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		cargarValores();		
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
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                compania);
		registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                ano);
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		cargarValores();
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                ano);
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * @return TODO VARIABLE
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		cargarValores();
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
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
	 * @return TODO VARIABLE
	 */
	@Override   
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		cargarValores();
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
		
	}
	
	/**
     * @param valor
     * @return
     */
    private void cargarValores()
    {
    	Registro valTotal;
    	Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        
        try
        {
            valTotal = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		FrmproductodistribucioncostosControladorUrlEnum.URL1938001
                                                                            .getValue())
                                            .getUrl(), param));

            if (valTotal != null)
            {
                sumaTotalFactor = SysmanFunciones.nvl(valTotal.getCampos()
                        .get("TOTAL_FACTOR"), "0.0").toString();
            }
            else
            {
            	sumaTotalFactor = "0.0";
            }

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }
	
	//<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     * 
     * @return  ano
     */
    public int getAno() {
    	return ano;
    }
    /**
     * Asigna la variable  ano
     * 
     * @param  ano
     * Variable a asignar en  ano
     */
    public void setAno(int ano) {
    	this.ano = ano;
    }
    
    
    /**
     * Retorna la variable sumaCostoTotal
     * 
     * @return sumaCostoTotal
     */
    public String getsumaTotalFactor()
    {
        return sumaTotalFactor;
    }

    /**
     * Asigna la variable sumaValorProgramado
     * 
     * @param sumaCostoTotal
     * Variable a asignar en sumaCostoTotal
     */
    public void setsumaTotalFactor(String sumaTotalFactor)
    {
        this.sumaTotalFactor = sumaTotalFactor;
    }
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
    /**
     * Retorna la lista listaANO
     * 
     * @return listaANO
     */
    public List<Registro> getListaANO() {
    	return listaANO;
    }
    /**
     * Asigna la lista listaANO
     * 
     * @param listaANO
     * Variable a asignar en  listaANO
     */
    public void setListaANO(List<Registro> listaANO) {
    	this.listaANO = listaANO;
    }
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
