/*-
 * DevolutivosPorFechaAdquisicion.java
 *
 * 1.0
 * 
 * 26/01/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase que genera 2 informes de devoutivos del inventario del modulo
 * de Almacen.
 * 
 * @version 1.0, 26/01/2017
 * @author jguerrero
 * 
 * @author jlramirez
 * @version 2,27/04/2017, Manejo EJBs
 */
@ManagedBean
@ViewScoped
public class DevolutivosPorFechaAdquisicion extends BeanBaseModal {

    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena temporalmente el dato de la fecha inicial
     * digitada del formulario
     */
    private Date fechaInicial;
    /**
     * Variable que almacena temporalmente el dato de la fecha Final
     * digitada del formulario
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */

    private StreamedContent archivoDescarga;
    /**
     * Atributo usado para almacenar temporalmente lo seleccionado del
     * tipo de formato de la interfaz grafica del formulario vista
     */

    private String tipoFormato;
    /**
     * Atributo usado para almacenar temporalmente el resultado del
     * parametro DIGITOS AGRUPACION INVENTARIO
     * 
     */
    
    private final String modulo;
    
    private boolean ckExcelPlano; 
    
    private boolean visibleExcel;
    
    private String reporteExcel;
    
    private String digitosAgrupacion;
    
    @EJB    
    private EjbSysmanUtilRemote sysmanUtil;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
	 /**
     * Crea una nueva instancia de DevolutivosPorFechaAdquisicion
     */
    public DevolutivosPorFechaAdquisicion() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        tipoFormato = "2";
        ckExcelPlano = false;
        try {
            numFormulario = GeneralCodigoFormaEnum.DEVOLUTIVOS_POR_FECHA_ADQUISICION
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
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
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        try {
            digitosAgrupacion = SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "DIGITOS AGRUPACION INVENTARIO",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO")
                            .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
    	// <CODIGO_DESARROLLADO>
    	try {
    	String valorParametro = consultarParametro(
    			"INFORME ALMACEN EXCEL PLANO", false);

    	visibleExcel = valorParametro.equals("SI")?true:false;

    } catch (SystemException e) {
    	logger.error(e.getMessage(), e);
    	JsfUtil.agregarMensajeError(e.getMessage());
    } 
    			
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton oprimirPdf en la vista Y
     * exporta el informe en formato pdf
     */
    public void oprimiroprimirPdf() {
        // <CODIGO_DESARROLLADO>
        if (!validarFecha()) {
            return;
        }
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton oprimirExcel en la vista Y
     * exporta el informe en formato pdf
     */
    public void oprimiroprimirExcel() {
        // <CODIGO_DESARROLLADO>

        if (!validarFecha()) {
            return;
        }
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    public String getTipoFormato() {
        return tipoFormato;
    }

    public void setTipoFormato(String tipoFormato) {
        this.tipoFormato = tipoFormato;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    
    /**     
     * @return  ckExcelPlano
     */
public boolean getCkExcelPlano() {
        return ckExcelPlano;
    }
    /**
     * @param  ckExcelPlano Variable a asignar en  ckExcelPlano
     */
    public void setCkExcelPlano(boolean ckExcelPlano) {
        this.ckExcelPlano = ckExcelPlano;
    }
    
    

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
	 * @return the visibleExcel
	 */
	public boolean isVisibleExcel() {
		return visibleExcel;
	}

	/**
	 * @param visibleExcel the visibleExcel to set
	 */
	public void setVisibleExcel(boolean visibleExcel) {
		this.visibleExcel = visibleExcel;
	}

	public void genInforme(ReportesBean.FORMATOS formato) {
    	archivoDescarga = null;

    	String reporte = nombreReporte();
    	try {

    		HashMap<String, Object> reemplazar = new HashMap<>();
    		reemplazar.put("parametroAgrupacion", digitosAgrupacion);
    		reemplazar.put("fechaInicial",
    				SysmanFunciones.formatearFecha(fechaInicial));
    		reemplazar.put("fechaFinal",
    				SysmanFunciones.formatearFecha(fechaFinal));

    		Map<String, Object> parametros = new HashMap<>();

    		if(ckExcelPlano) {

    			String sql = Reporteador.resuelveConsulta(reporteExcel,
    					Integer.parseInt(SessionUtil.getModulo()), reemplazar);
    			
    			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato, reporteExcel);

    		}else {

    			Reporteador.resuelveConsulta(reporte,
    					Integer.parseInt(SessionUtil.getModulo()),
    					reemplazar, parametros);
    			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
    					ConectorPool.ESQUEMA_SYSMAN, formato);
    		}

    	}
    	catch (FileNotFoundException | SQLException | DRException ex) {
    		JsfUtil.agregarMensajeInformativo(
    				idioma.getString("MSM_INFORME_NO_EXISTE") + " "
    						+ ex.getMessage() + " " + reporte);
    		Logger.getLogger(DevolutivosPorFechaAdquisicion.class.getName())
    		.log(Level.SEVERE, null, ex);
    	}

    	catch (JRException | IOException ex) {
    		JsfUtil.agregarMensajeError(
    				idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
    				+ ex.getMessage());
    		Logger.getLogger(DevolutivosPorFechaAdquisicion.class.getName())
    		.log(Level.SEVERE, null, ex);
    	}
    	catch (SysmanException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}

    }

    private String nombreReporte() {
        String nombre;
        
        if ("1".equals(tipoFormato)) {
            nombre = "001370DevolutivosPorFechaDeAdquisicion1";
            reporteExcel = "800591DevolutivosPorFechaDeAdquisicion1";

        }
        else {
            nombre = "001376DevolutivosPorFechaDeAdquisicion";
            reporteExcel = "800592DevolutivosPorFechaDeAdquisicion";
        }

        return nombre;
    }

    private boolean validarFecha() {
        boolean respuesta = true;
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2789"));
            fechaInicial = null;
            respuesta = false;
        }
        return respuesta;
    }

    /**
     * Metodo ejecutado al cambiar el control ckExcelPlano    
     */
    public void cambiarckExcelPlano() {
    	//<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    
    private String consultarParametro(String nombre, boolean mayus)
			throws SystemException {
		return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
				new Date(), mayus);
	}
    
    
    
}


