/*-
 * FrmlistadofoncepControlador.java
 *
 * 1.0
 * 
 * 21/08/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 21/08/2020
 * @author lmosquera
 */
@ManagedBean
@ViewScoped
public class  FrmlistadofoncepControlador extends BeanBaseModal{
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
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
	private final String modulo;
	private String proceso;
	private String ano;
	private String mes;
	private String periodo;
	private String observaciones;
	private String nombrePeriodoNomina;
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtilRemote;
	
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmlistadofoncepControlador
     */
    public FrmlistadofoncepControlador() {
    	super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		proceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
		ano = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
		mes = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
		periodo = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
		nombrePeriodoNomina = SysmanFunciones.nvl(SessionUtil.getSessionVar("nombrePeriodoNomina"), "").toString();
        try {
        	// 2181
        	numFormulario = GeneralCodigoFormaEnum.FRM_LISTADO_FONCEP_CONTROLADOR.getCodigo();
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
        //</CODIGO_DESARROLLADO>
    }
//<METODOS_CARGAR_LISTA>
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton PDF
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirPDF() {
         //<CODIGO_DESARROLLADO>
         archivoDescarga=null;     
         generarInforme(ReportesBean.FORMATOS.PDF);
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton EXCEL
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirEXCEL() {
         //<CODIGO_DESARROLLADO>
         archivoDescarga=null; 
         generarInforme(ReportesBean.FORMATOS.EXCEL);
        //</CODIGO_DESARROLLADO>
    }

	public void generarInforme(ReportesBean.FORMATOS formato) {
		try {
			HashMap<String, Object> reemplazos = new HashMap<>();
			HashMap<String, Object> parametros = new HashMap<>();
			String reporte = "002121LISTADOFAVIDIIDI";
			
			reemplazos.put("compania", compania);
			reemplazos.put("anioNomina", ano);
			reemplazos.put("mesNomina", mes);
			reemplazos.put("procesoNomina", proceso);
	
			 parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());
			String jefeDesarrolloHumano = ejbSysmanUtilRemote.consultarParametro(compania, "NOMBRE JEFE DESARROLLO HUMANO", modulo, 
					new Date(), false);
			String jefeNomina = ejbSysmanUtilRemote.consultarParametro(compania, "NOMBRE JEFE NOMINA", modulo, 
					new Date(), false);
			String cargoJefeDesarrolloHumano = ejbSysmanUtilRemote.consultarParametro(compania, "CARGO JEFE DESARROLLO HUMANO", modulo, 
					new Date(), false);
			String cargoResponsableNomina = ejbSysmanUtilRemote.consultarParametro(compania, "CARGO RESPONSABLE DE NOMINA", modulo, 
					new Date(), false);
			
			parametros.put("PR_OBS", observaciones);
			parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeDesarrolloHumano);
			parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
			parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
			parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);
			parametros.put("PR_NOMBRE_PERIODO", nombrePeriodoNomina);
			
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazos, parametros);
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SystemException e) {
			//e.printStackTrace();
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
     * Retorna la variable observaciones
     * 
     * @return  observaciones
     */
public String getObservaciones() {
        return observaciones;
    }
    /**
     * Asigna la variable  observaciones
     * 
     * @param  observaciones
     * Variable a asignar en  observaciones
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}


	
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
