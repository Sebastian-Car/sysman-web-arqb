/*-
 * CarterafinanciablesControlador.java
 *
 * 1.0
 * 
 * 22/04/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;

public class CarterafinanciablesControlador extends BeanBaseDatosAcme{
  
    private final String compania ; 

private List<Registro> listalinea;

private RegistroDataModel listacuenta;

private RegistroDataModel listaCListaTCartera;

	public CarterafinanciablesControlador() {
		super();
	    	compania = SessionUtil.getCompania();
 try {
			numFormulario = 2263;
  validarPermisos();
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
	 cargarListacuenta();
	 cargarListaCListaTCartera();
	//</CARGAR_LISTA_COMBO_GRANDE>
	//<CARGAR_LISTA>
	 cargarListalinea();
	//</CARGAR_LISTA>
	}
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
	@Override
	public void iniciarListasSub(){
	//<CARGAR_LISTAS_SUBFORM>
	listaCListaTCartera.setSeleccionados("");
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
	tabla="";
	 buscarLlave();
	asignarOrigenDatos();
       	reasignarOrigenGrilla();
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
origenDatos="";	
}
    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
@Override
  public void reasignarOrigenGrilla() {
	origenGrilla="";
  if (listaInicial != null) {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenGrilla);
        }
}
//<METODOS_CARGAR_LISTA>	
    /**
     * 
     * Carga la lista listalinea
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListalinea(){

}
    /**
     * 
     * Carga la lista listacuenta
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListacuenta(){
}

public void cargarListaCListaTCartera(){
}
//</METODOS_CARGAR_LISTA>
//<METODOS_CAMBIAR>	
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>	
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacuenta
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilacuenta(SelectEvent event) {

}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCListaTCartera
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
 public void seleccionarFilaCListaTCartera(SelectEvent event) {
}

public void oprimirImprimir() {
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando79
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirComando79() {
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton btnCorregir
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirbtnCorregir() {
         //<CODIGO_DESARROLLADO>
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
	  }
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
    /**
     * Retorna la lista listalinea
     * 
     * @return listalinea
     */
public List<Registro> getListalinea() {
        return listalinea;
    }
    /**
     * Asigna la lista listalinea
     * 
     * @param listalinea
     * Variable a asignar en  listalinea
     */
public void setListalinea(List<Registro> listalinea) {
        this.listalinea = listalinea;
    }
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listacuenta
     * 
     * @return listacuenta
     */
    public RegistroDataModel getListacuenta() {
        return listacuenta;
    }
    /**
     * Asigna la lista listacuenta
     * 
     * @param listacuenta
     * Variable a asignar en  listacuenta
     */
    public void setListacuenta(RegistroDataModel listacuenta) {
        this.listacuenta = listacuenta;
    }
    /**
     * Retorna la lista listaCListaTCartera
     * 
     * @return listaCListaTCartera
     */
    public RegistroDataModel getListaCListaTCartera() {
        return listaCListaTCartera;
    }
    /**
     * Asigna la lista listaCListaTCartera
     * 
     * @param listaCListaTCartera
     * Variable a asignar en  listaCListaTCartera
     */
    public void setListaCListaTCartera(RegistroDataModel listaCListaTCartera) {
        this.listaCListaTCartera = listaCListaTCartera;
    }

}
