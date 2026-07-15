/*-
 * FrcierreContableControlador.java
 *
 * 1.0
 * 
 * 07/12/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
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
import java.util.Map;

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

import com.sysman.almacen.enums.RIngresosConsumoControladorEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
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
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 07/12/2022
 * @author avega
 */
@ManagedBean
@ViewScoped
public class  FrcierreContableControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    private final String moduloAlmacen;
    private String reporte;


//<DECLARAR_ATRIBUTOS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private Date fechainicial;

    /**
     * TODO DOCUMENTACION NECESARIA
     */
private Date fechafinal;
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
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrcierreContableControlador
     */
    public FrcierreContableControlador() {
  super();
            compania = SessionUtil.getCompania();
    		moduloAlmacen = SessionUtil.getModulo();


        try {
        	numFormulario = GeneralCodigoFormaEnum.FR_CIERRE_CONTABLE_CONTROLADOR
                    .getCodigo();
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
	  	fechainicial = new Date();
		fechafinal = new Date();
        //</CODIGO_DESARROLLADO>
    }
//<METODOS_CARGAR_LISTA>
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BTPdf
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirBTPdf() {
         //<CODIGO_DESARROLLADO>
         archivoDescarga=null;  
					generarInforme(ReportesBean.FORMATOS.PDF);

        //</CODIGO_DESARROLLADO>
    }


private void generarInforme(FORMATOS formato){
		// TODO Auto-generated method stub
	 try {
		 reporte = "002418CIERRECONTABLECONSUMO";

         HashMap<String, Object> reemplazos = new HashMap<>();
         HashMap<String, Object> parametros = new HashMap<>();
         
         reemplazos.put("compania", compania);
	     reemplazos.put("fechaini", SysmanFunciones.convertirAFechaCadena(fechainicial));
	     reemplazos.put("fechafin", SysmanFunciones.convertirAFechaCadena(fechafinal));
	        
	     Reporteador.resuelveConsulta(reporte,
                 Integer.parseInt(moduloAlmacen), reemplazos, parametros);

	     
	     
         archivoDescarga = JsfUtil.exportarStreamed("002418CIERRECONTABLECONSUMO", parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

     }
     catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | ParseException e) {
         logger.error(e.getMessage(), e);
         JsfUtil.agregarMensajeError(e.getMessage());
     }
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
     * Retorna la variable fechainicial
     * 
     * @return  fechainicial
     */
public Date getFechainicial() {
        return fechainicial;
    }
    /**
     * Asigna la variable  fechainicial
     * 
     * @param  fechainicial
     * Variable a asignar en  fechainicial
     */
    public void setFechainicial(Date fechainicial) {
        this.fechainicial = fechainicial;
    }
    /**
     * Retorna la variable fechafinal
     * 
     * @return  fechafinal
     */
public Date getFechafinal() {
        return fechafinal;
    }
    /**
     * Asigna la variable  fechafinal
     * 
     * @param  fechafinal
     * Variable a asignar en  fechafinal
     */
    public void setFechafinal(Date fechafinal) {
        this.fechafinal = fechafinal;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
	/**
	 * @return the compania
	 */
	public String getCompania() {
		return compania;
	}
	

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
