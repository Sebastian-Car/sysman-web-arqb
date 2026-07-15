/*-
 * FrmseleccionmoduloprControlador.java
 *
 * 1.0
 * 
 * 09/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.SeleccionModuloControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
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
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 09/12/2020
 * @author jalfonso
 */
@ManagedBean
@ViewScoped
public class  FrmseleccionmoduloprControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
//<DECLARAR_ATRIBUTOS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private String aplicacion;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private List<Registro> listaCBaplicacion;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmseleccionmoduloprControlador
     */
    public FrmseleccionmoduloprControlador() {
  super();
            compania = SessionUtil.getCompania();
        try {
numFormulario=2219;
SessionUtil.getModulo();
            validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        }finally{
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
//<CARGAR_LISTA>
		 cargarListaCBaplicacion();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
    }
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
//<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCBaplicacion
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListaCBaplicacion(){
	  try {
		  listaCBaplicacion = RegistroConverter.toListRegistro(
                          requestManager.getList(UrlServiceUtil.getInstance()
                                          .getUrlServiceByUrlByEnumID(
                                                          SeleccionModuloControladorUrlEnum.URL3642
                                                                          .getValue())
                                          .getUrl(),
                                          null));
      }
      catch (SystemException e) {
          logger.error(e.getMessage(), e);
          JsfUtil.agregarMensajeError(e.getMessage());
      }
}
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BTaceptar
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirBTaceptar() {
	if (SysmanFunciones.validarVariableVacio(aplicacion)) {
        JsfUtil.agregarMensajeError(idioma.getString("TB_TB3934"));
        return;
    }

    Direccionador direccionador = new Direccionador();
    try {
        SessionUtil.setSessionVarContainer("aplicacion",
                        aplicacion);

        SessionUtil.setSessionVarContainer("menu", "50001");

        direccionador.setRuta("/menu.sysman");

    }
    catch (NamingException e) {
        logger.error(e.getMessage(), e);
        JsfUtil.agregarMensajeError(e.getMessage());
    }

    RequestContext.getCurrentInstance().closeDialog(direccionador);
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton BTcancelar
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirBTcancelar() {
	 RequestContext.getCurrentInstance().closeDialog(null);
    }
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable aplicacion
     * 
     * @return  aplicacion
     */
public String getAplicacion() {
        return aplicacion;
    }
    /**
     * Asigna la variable  aplicacion
     * 
     * @param  aplicacion
     * Variable a asignar en  aplicacion
     */
    public void setAplicacion(String aplicacion) {
        this.aplicacion = aplicacion;
    }
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
    /**
     * Retorna la lista listaCBaplicacion
     * 
     * @return listaCBaplicacion
     */
public List<Registro> getListaCBaplicacion() {
        return listaCBaplicacion;
    }
    /**
     * Asigna la lista listaCBaplicacion
     * 
     * @param listaCBaplicacion
     * Variable a asignar en  listaCBaplicacion
     */
public void setListaCBaplicacion(List<Registro> listaCBaplicacion) {
        this.listaCBaplicacion = listaCBaplicacion;
    }
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
