/*-
 * ListaAcuerdoControlador.java
 *
 * 1.0
 * 
 * 14/06/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.general.enums.ValorizacionListaAcuerdoControladorUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 14/06/2019
 * @author obarragan
 */
@ManagedBean
@ViewScoped
public class  ListaAcuerdoControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    private final String modulo; //
    //<DECLARAR_ATRIBUTOS>

    private String terceroInicial;
    private String tercerFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreTerceroInicial;
    private String nombreTerceroFinal;
    private boolean ckDetallado;
    private boolean ckIndicadorPago;
    private Registro rsAplicacion;
    private String nombreAplicacion;
    private final String  NIT = "NIT";
    private final String  APLICACION = "APLICACION";
    
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaCB_terceroInicial;
	private RegistroDataModelImpl listaCB_terceroFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ListaAcuerdoControlador
     */
    public ListaAcuerdoControlador() {
		super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
    		numFormulario=GeneralCodigoFormaEnum.FRM_VALORIZACION_LISTA_ACUERDO.getCodigo(); 
            validarPermisos();
        	fechaInicial = new Date();
        	fechaFinal = new Date();            
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
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCB_terceroInicial();
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
     * Carga la lista listaCB_terceroInicial
     *

     */
public void cargarListaCB_terceroInicial(){
	   Map<String, Object> param = new TreeMap<>();
       param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
       param.put(APLICACION, modulo);
       

       UrlBean urlBean = UrlServiceUtil.getInstance()
                       .getUrlServiceByUrlByEnumID(
                       		ValorizacionListaAcuerdoControladorUrlEnum.URL0001
                                                       .getValue());

       listaCB_terceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                       urlBean.getUrlConteo().getUrl(), param, true,
                       NIT);	
}
    /**
     * 
     * Carga la lista listaCB_terceroFinal
     *
     */
public void cargarListaCB_terceroFinal(){
    Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    param.put(APLICACION, modulo);

    UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                    		ValorizacionListaAcuerdoControladorUrlEnum.URL0001
                                                    .getValue());

    listaCB_terceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                    urlBean.getUrlConteo().getUrl(), param, true,
                    NIT);
}
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BT_pdf
     * en la vista 
     *
     *
     */
public void oprimirBT_pdf() {
         //<CODIGO_DESARROLLADO>
         archivoDescarga=null;   
         generarInforme(FORMATOS.PDF);
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton BT_excel
     * en la vista
     *
     *
     */
public void oprimirBT_excel() {
         //<CODIGO_DESARROLLADO>
         archivoDescarga=null;
         generarInforme(FORMATOS.EXCEL97);
        //</CODIGO_DESARROLLADO>
    }

// <METODO GENERAR INFORME>
public void generarInforme(FORMATOS formato){
    if (!validarFechas()) {
        return;
    }
	try {
        HashMap<String, Object> reemplazar = new HashMap<>();
        HashMap<String, Object> param = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        
        
        reemplazar.put("modulo", modulo);

        reemplazar.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
        reemplazar.put("fechaFinal",SysmanFunciones.convertirAFechaCadena(fechaFinal));

        reemplazar.put("terceroInicial", terceroInicial);
        reemplazar.put("terceroFinal", tercerFinal);
        
        reemplazar.put("detallado", ckDetallado ? "1" : "0");
        reemplazar.put("indicadorPago", ckIndicadorPago ? "1" : "0");
        
        param.put("MODULO", modulo);
        try {
			rsAplicacion = RegistroConverter.toRegistro(
			        requestManager.get(UrlServiceUtil
			                        .getInstance()
			                        .getUrlServiceByUrlByEnumID(
			                        		ValorizacionListaAcuerdoControladorUrlEnum.URL0005
			                                                        .getValue())
			                        .getUrl(),
			                        param));
		} catch (SystemException e) {

			e.printStackTrace();
		}

        if (rsAplicacion != null) {
        	nombreAplicacion = SysmanFunciones.nvl(rsAplicacion.getCampos().get("NOMBRE"),"").toString();
        }


        Reporteador.resuelveConsulta("002016ListaAcuerdo",Integer.parseInt(modulo), reemplazar, parametros);

        // Parametros diseño reporte
        parametros.put("PR_COMPANIA", compania);
        parametros.put("PR_APLICACION", nombreAplicacion.toUpperCase());
        
        parametros.put("PR_FECHA_INICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
        parametros.put("PR_FECHA_FINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
        parametros.put("PR_TERCERO_INICIAL", terceroInicial);
        parametros.put("PR_TERCERO_FINAL", tercerFinal);
        parametros.put("PR_DETALLADO", ckDetallado ? true:false);
        parametros.put("PR_INDICADOR_PAGO", ckIndicadorPago ? true:false); 

        // Parametros diseño reporte

        archivoDescarga = JsfUtil.exportarStreamed(
                        "002016ListaAcuerdo",
                        parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
    }
    catch (JRException | IOException | ParseException | SysmanException e) {
        logger.error(e.getMessage(), e);
        JsfUtil.agregarMensajeError(e.getMessage()); 
    }
}

// </METODO GENERAR INFORME>

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCB_terceroInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCB_terceroInicial(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
    terceroInicial= registroAux.getCampos().get("NIT").toString();
    nombreTerceroInicial = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
    cargarListaCB_terceroFinal();
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCB_terceroFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCB_terceroFinal(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
    tercerFinal= registroAux.getCampos().get("NIT").toString();
    nombreTerceroFinal = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
    
    
}
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ckDetallado
     * 
     * @return  ckDetallado
     */
public boolean getCkDetallado() {
        return ckDetallado;
    }
    /**
     * Asigna la variable  ckDetallado
     * 
     * @param  ckDetallado
     * Variable a asignar en  ckDetallado
     */
    public void setCkDetallado(boolean ckDetallado) {
        this.ckDetallado = ckDetallado;
    }
    /**
     * Retorna la variable ckIndicadorPago
     * 
     * @return  ckIndicadorPago
     */
public boolean getCkIndicadorPago() {
        return ckIndicadorPago;
    }
    /**
     * Asigna la variable  ckIndicadorPago
     * 
     * @param  ckIndicadorPago
     * Variable a asignar en  ckIndicadorPago
     */
    public void setCkIndicadorPago(boolean ckIndicadorPago) {
        this.ckIndicadorPago = ckIndicadorPago;
    }
    /**
     * Retorna la variable terceroInicial
     * 
     * @return  terceroInicial
     */
public String getTerceroInicial() {
        return terceroInicial;
    }
    /**
     * Asigna la variable  terceroInicial
     * 
     * @param  terceroInicial
     * Variable a asignar en  terceroInicial
     */
    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }
    /**
     * Retorna la variable tercerFinal
     * 
     * @return  tercerFinal
     */
public String getTercerFinal() {
        return tercerFinal;
    }
    /**
     * Asigna la variable  tercerFinal
     * 
     * @param  tercerFinal
     * Variable a asignar en  tercerFinal
     */
    public void setTercerFinal(String tercerFinal) {
        this.tercerFinal = tercerFinal;
    }
    /**
     * Retorna la variable fechaInicial
     * 
     * @return  fechaInicial
     */
public Date getFechaInicial() {
        return fechaInicial;
    }
    /**
     * Asigna la variable  fechaInicial
     * 
     * @param  fechaInicial
     * Variable a asignar en  fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }
    /**
     * Retorna la variable fechaFinal
     * 
     * @return  fechaFinal
     */
public Date getFechaFinal() {
        return fechaFinal;
    }
    /**
     * Asigna la variable  fechaFinal
     * 
     * @param  fechaFinal
     * Variable a asignar en  fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }
    /**
     * Retorna la variable nombreTerceroInicial
     * 
     * @return  nombreTerceroInicial
     */
public String getNombreTerceroInicial() {
        return nombreTerceroInicial;
    }
    /**
     * Asigna la variable  nombreTerceroInicial
     * 
     * @param  nombreTerceroInicial
     * Variable a asignar en  nombreTerceroInicial
     */
    public void setNombreTerceroInicial(String nombreTerceroInicial) {
        this.nombreTerceroInicial = nombreTerceroInicial;
    }
    /**
     * Retorna la variable nombreTerceroFinal
     * 
     * @return  nombreTerceroFinal
     */
public String getNombreTerceroFinal() {
        return nombreTerceroFinal;
    }
    /**
     * Asigna la variable  nombreTerceroFinal
     * 
     * @param  nombreTerceroFinal
     * Variable a asignar en  nombreTerceroFinal
     */
    public void setNombreTerceroFinal(String nombreTerceroFinal) {
        this.nombreTerceroFinal = nombreTerceroFinal;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
	public StreamedContent getArchivoDescarga() {
	        return archivoDescarga;
	    }
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaCB_terceroInicial
     * 
     * @return listaCB_terceroInicial
     */
    public RegistroDataModelImpl getListaCB_terceroInicial() {
        return listaCB_terceroInicial;
    }
    /**
     * Asigna la lista listaCB_terceroInicial
     * 
     * @param listaCB_terceroInicial
     * Variable a asignar en  listaCB_terceroInicial
     */
    public void setListaCB_terceroInicial(RegistroDataModelImpl listaCB_terceroInicial) {
        this.listaCB_terceroInicial = listaCB_terceroInicial;
    }
    /**
     * Retorna la lista listaCB_terceroFinal
     * 
     * @return listaCB_terceroFinal
     */
    public RegistroDataModelImpl getListaCB_terceroFinal() {
        return listaCB_terceroFinal;
    }
    /**
     * Asigna la lista listaCB_terceroFinal
     * 
     * @param listaCB_terceroFinal
     * Variable a asignar en  listaCB_terceroFinal
     */
    public void setListaCB_terceroFinal(RegistroDataModelImpl listaCB_terceroFinal) {
        this.listaCB_terceroFinal = listaCB_terceroFinal;
    }
    
//</SET_GET_LISTAS_COMBO_GRANDE>
    
	private boolean validarFechas() {
        boolean rta = true;
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB75"));
            rta = false;
        }
        return rta;
    }    
}
