/*-
 * ReversarcierrepptalControlador.java
 *
 * 1.0
 * 
 * 03/02/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoCierreRemote;
import com.sysman.presupuesto.enums.CierrePresupuestoControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 03/02/2026
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class ReversarcierrepptalControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Año a generar cierre
	 */
	private int ano;
	/**
	 * Nombre de la compañia de Ingreso que se ejecuta cierre
	 */
	private String nombreCompania;
	/**
	 * Atributo que almacena el valor del indicador Cierre Vigencias
	 * Futuras.
	 */
	private boolean cierreFuturas;
	/**
	 * Atributo que almacena el valor del indicador Cierre Vigencias
	 * Futuras a Pasivos Exigibles
	 */
	private boolean cierreFuturasPasivos;
	/**
	 * Atributo que almacean el valor del indicador Cierre Regalias
	 */
	private boolean cierreRegalias;
	/**
	 * Atributo que almacena el valor del indicador Cierre Vigencia
	 */
	private boolean cierreVigencia;
	/**
	 * Atributo que almacena el valor del indicador Cierre Pasivos
	 * Exigibles
	 */
	private boolean cierrePasivos;
	/**
	 * Atributo que almacena el valor del indicador Cofinanciados
	 */
	private boolean cierreCofinanciados;

	/**
	 * Permite hacer visible el di&aacute;logo confirmarCierre.
	 */
	private boolean confirmarCierre;   

	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;

	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * lista de años de la compañia
	 */
	private List<Registro> listaAno;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	@EJB
	private EjbPresupuestoCierreRemote ejbPresupuestoCierre;

	//<DECLARAR_ADICIONALES>
	//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de ReversarcierrepptalControlador
	 */
	public ReversarcierrepptalControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.REVERSAR_CIERRE_PPTAL_CONTROLADOR
                    .getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
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
		
		nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
		ano = SysmanFunciones.ano(new Date()) - 1;

		cargarListaAno();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
        
	}

	//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaAno(){
		Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {

            listaAno = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            CierrePresupuestoControladorUrlEnum.URL7252
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            parametros));
        }
        catch (SystemException e) {
            Logger.getLogger(PlanpresupuestalptosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_CAMBIAR>	

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * del dialogo confirmarEjecutar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void aceptarconfirmarEjecutar() {
		//<CODIGO_DESARROLLADO>
		confirmarCierre =  false; 
		archivoDescarga = null;
		try {
			String reversarCierre = ejbPresupuestoCierre.eliminarCierrePlan(compania, ano,
					SessionUtil.getUser().getCodigo(), cierreVigencia,
					cierrePasivos, cierreFuturas, cierreFuturasPasivos,
					cierreRegalias, cierreCofinanciados);

			if(reversarCierre.equalsIgnoreCase("1"))
			{
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));  
			} 

		}
		catch (SystemException e) {

			Logger.getLogger(PlanpresupuestalptosControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	
	public void cancelarconfirmarEjecutar() {
        // <CODIGO_DESARROLLADO>
    	   confirmarCierre =  false;       
        // </CODIGO_DESARROLLADO>
    }
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>	
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>	
	//</METODOS_ARBOL>
	//<METODOS_BOTONES>	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Revisar
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirRevisar() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
        try {
            String revisarCierre = ejbPresupuestoCierre.validarReversarCierre(
                            compania, ano, cierreVigencia, cierrePasivos,
                            cierreFuturas, cierreFuturasPasivos,
                            cierreRegalias, cierreCofinanciados);
            String separadorRegistros = SysmanConstantes.SEPARADOR_REG;
            String separadorColumnas = SysmanConstantes.SEPARADOR_COL;
            String separadorHojas = SysmanConstantes.SEPARADOR_HOJ;

            String nombreDocumento = "Revisar Datos Reversar Cierre";
            archivoDescarga = JsfUtil.armarExcelconHoja(revisarCierre,
                            separadorHojas, separadorRegistros,
                            separadorColumnas, nombreDocumento);
            JsfUtil.agregarMensajeInformativo("Proceso terminado Con exito.");
        }
        catch (SystemException e) {

            Logger.getLogger(PlanpresupuestalptosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }           
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Ejecutar
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirEjecutar() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
    	confirmarCierre =  true;           
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>	
	//<METODOS_SUBFORM>	
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

	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable cierreVigencia
	 * 
	 * @return  cierreVigencia
	 */
	public boolean getCierreVigencia() {
		return cierreVigencia;
	}
	/**
	 * Asigna la variable  cierreVigencia
	 * 
	 * @param  cierreVigencia
	 * Variable a asignar en  cierreVigencia
	 */
	public void setCierreVigencia(boolean cierreVigencia) {
		this.cierreVigencia = cierreVigencia;
	}
	/**
	 * Retorna la variable cierrePasivos
	 * 
	 * @return  cierrePasivos
	 */
	public boolean getCierrePasivos() {
		return cierrePasivos;
	}
	/**
	 * Asigna la variable  cierrePasivos
	 * 
	 * @param  cierrePasivos
	 * Variable a asignar en  cierrePasivos
	 */
	public void setCierrePasivos(boolean cierrePasivos) {
		this.cierrePasivos = cierrePasivos;
	}
	/**
	 * Retorna la variable cierreRegalias
	 * 
	 * @return  cierreRegalias
	 */
	public boolean getCierreRegalias() {
		return cierreRegalias;
	}
	/**
	 * Asigna la variable  cierreRegalias
	 * 
	 * @param  cierreRegalias
	 * Variable a asignar en  cierreRegalias
	 */
	public void setCierreRegalias(boolean cierreRegalias) {
		this.cierreRegalias = cierreRegalias;
	}
	/**
	 * Retorna la variable cierreFuturas
	 * 
	 * @return  cierreFuturas
	 */
	public boolean getCierreFuturas() {
		return cierreFuturas;
	}
	/**
	 * Asigna la variable  cierreFuturas
	 * 
	 * @param  cierreFuturas
	 * Variable a asignar en  cierreFuturas
	 */
	public void setCierreFuturas(boolean cierreFuturas) {
		this.cierreFuturas = cierreFuturas;
	}
	/**
	 * Retorna la variable cierreFuturasPasivos
	 * 
	 * @return  cierreFuturasPasivos
	 */
	public boolean getCierreFuturasPasivos() {
		return cierreFuturasPasivos;
	}
	/**
	 * Asigna la variable  cierreFuturasPasivos
	 * 
	 * @param  cierreFuturasPasivos
	 * Variable a asignar en  cierreFuturasPasivos
	 */
	public void setCierreFuturasPasivos(boolean cierreFuturasPasivos) {
		this.cierreFuturasPasivos = cierreFuturasPasivos;
	}
	/**
	 * Retorna la variable cierreCofinanciados
	 * 
	 * @return  cierreCofinanciados
	 */
	public boolean getCierreCofinanciados() {
		return cierreCofinanciados;
	}
	/**
	 * Asigna la variable  cierreCofinanciados
	 * 
	 * @param  cierreCofinanciados
	 * Variable a asignar en  cierreCofinanciados
	 */
	public void setCierreCofinanciados(boolean cierreCofinanciados) {
		this.cierreCofinanciados = cierreCofinanciados;
	}
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
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_LISTAS>
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
	 * @param listaAno
	 * Variable a asignar en  listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}
	
	/**
     * Retorna la variable nombreCompania
     * 
     * @return nombreCompania
     */
    public String getNombreCompania() {
        return nombreCompania;
    }

    /**
     * Asigna la variable nombreCompania
     * 
     * @param nombreCompania
     * Variable a asignar en nombreCompania
     */
    public void setNombreCompania(String nombreCompania) {
        this.nombreCompania = nombreCompania;
    }
    
    public boolean isConfirmarCierre() {
		return confirmarCierre;
	}

	public void setConfirmarCierre(boolean confirmarCierre) {
		this.confirmarCierre = confirmarCierre;
	}


	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
	//<SET_GET_LISTAS_SUBFORM>
	//</SET_GET_LISTAS_SUBFORM>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_ADICIONALES>	
	//</SET_GET_ADICIONALES>
}
