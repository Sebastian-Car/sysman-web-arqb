/*-
 * FrmclasificadoresControlador.java
 *
 * 1.0
 * 
 * 21/06/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;


import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FrmclasificadoresControladorEnum;
import com.sysman.presupuesto.enums.FrmclasificadoresControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;



/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 21/06/2022
 * @author cperez2
 */
@ManagedBean
@ViewScoped
public class  FrmclasificadoresControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	/**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
	private int indice;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String anioPreparar;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String anio;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAno;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */	
	private boolean preparaAnio;
	private boolean bloqOrden;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmclasificadoresControlador
	 */
	public FrmclasificadoresControlador() {
		super();
		compania = SessionUtil.getCompania();
		anio  =  String.valueOf(SysmanFunciones.ano(
				new Date()));
		try {
			numFormulario = GeneralCodigoFormaEnum.CLASIFICADORES_CONTROLADOR.getCodigo();
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

			enumBase =  GenericUrlEnum.CLASIFICADORES;
			anio  =  String.valueOf(SysmanFunciones.ano(
					new Date()));
			buscarLlave();
			reasignarOrigen();
			registro = new Registro();
			//<CARGAR_LISTA>
			cargarListaAno();
			//</CARGAR_LISTA>
			//<CARGAR_LISTA_COMBO_GRANDE>
			//</CARGAR_LISTA_COMBO_GRANDE>
			abrirFormulario();
	        
		
	}
	
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
    public void cargarListaAno() {
    	

    	Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		FrmclasificadoresControladorUrlEnum.URL3567
                                                    .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        
    	
       
    }
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Preparar
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPreparar() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = true;
		if (!anio.isEmpty()) {
			anioPreparar = String.valueOf(Integer.parseInt(anio) + 1);

		}
		// </CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarAno() {
		//<CODIGO_DESARROLLADO>
		anio = registro.getCampos().get("ANO").toString();
		reasignarOrigen();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * del dialogo DialogoPrepararAnio en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void aceptarDialogoPrepararAnio() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = false;
		
		
		if (SysmanFunciones.validarVariableVacio(anioPreparar)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB448"));
			return;
		}
		
		insertarClasificadores();
		
		JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB455"));
		// </CODIGO_DESARROLLADO>
	}
	 public boolean insertarClasificadores() {
	        Map<String, Object> parametros = new HashMap<>();
	        parametros.put(FrmclasificadoresControladorEnum.ANOPREPARAR.getValue(),
	                        anioPreparar);
	        anio =  String.valueOf(Integer.valueOf(anioPreparar) -1);
	        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        parametros.put(GeneralParameterEnum.ANO.getName(), anio);
	        parametros.put(GeneralParameterEnum.CREATED_BY.getName(),
	                        SessionUtil.getUser().getCodigo());
	        parametros.put(GeneralParameterEnum.DATE_CREATED.getName(),
	                        new Date());

	        UrlBean urlCreate = UrlServiceUtil.getInstance()
	                        .getUrlServiceByUrlByEnumID(
	                        		FrmclasificadoresControladorUrlEnum.URL8298
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
	            JsfUtil.agregarMensajeError(idioma.getString("TB_TB456"));
	        }
	        
	        return true;
	    }
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar
	 * del dialogo DialogoPrepararAnio en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	
	public void cancelarDialogoPrepararAnio() {
		//<CODIGO_DESARROLLADO>
		preparaAnio = false;
		//</CODIGO_DESARROLLADO>
	}
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
		/*
FR546-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
   formularioAbrir 1, Me.Name
   If Nz(par("MANEJA RETENCIONES POR CENTRO DE COSTO"), "NO") = "SI" Then
      Me.Centro_costo.visible = True
   End If
End Sub
		 *//*
FR546-AL_ABRIR
Private Sub Form_Load()
   DoCmd.Restore
End Sub
		  */
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 * TODO DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
    public boolean insertarAntes()
    {
    	//<CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
		registro.getCampos().remove("NOMBREAPLICACION");
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
        registro.getCampos().remove("NOMAPLIINGRE");
        registro.getCampos().remove("NOMBREAPLICACION");
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove("CODIGO");
        registro.getCampos().remove("ANO");
        registro.getCampos().remove("NOMBRE");
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
	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion,
	 * en el se pueden remover valores auxiliares que no se desee o se
	 * deban enviar en el registro
	 */
	@Override
	public void removerCombos() {
	}
    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
    	bloqOrden = false;
        indice = listaInicial.getRowIndex();
        String aplicacion = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get("APLICACION"),
                "").toString();
		
		if(!aplicacion.equals("1")) {
			bloqOrden = true;
		}
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
	/**
	 * Retorna la variable anioPreparar
	 * 
	 * @return  anioPreparar
	 */
	public String getAnioPreparar() {
		return anioPreparar;
	}
	/**
	 * Asigna la variable  anioPreparar
	 * 
	 * @param  anioPreparar
	 * Variable a asignar en  anioPreparar
	 */
	public void setAnioPreparar(String anioPreparar) {
		this.anioPreparar = anioPreparar;
	}
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
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
	public boolean isPreparaAnio() {
		return preparaAnio;
	}

	public void setPreparaAnio(boolean preparaAnio) {
		this.preparaAnio = preparaAnio;
	}
	
	public boolean isbloqOrden() {
		return bloqOrden;
	}

	public void setbloqOrden(boolean bloqOrden) {
		this.bloqOrden = bloqOrden;
	}
	
	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
	@Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put("COMPAIA",
                        compania);
    }

	
}
