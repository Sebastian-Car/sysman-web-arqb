/*-
 * Relaciondeingresosupc.java
 *
 * 1.0
 * 
 * 30/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
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

import net.sf.jasperreports.engine.JRException;

/**
 * Clase reservada para el formulario que genera la Relación de
 * Ingresos UPC.
 *
 * @version 1.0, 30/05/2018
 * @author dnino
 */
@ManagedBean
@ViewScoped
public class RelaciondeingresosupcControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    
    @EJB
	EjbSysmanUtil ejbSysmanUtil;
    private String nombretesorero;
    private String cargotesorero;
    /**
     * Variable que almacena la final inicial seleccionada
     */
    private Date fechaInicial;
    /**
     * Variable que almacena la final final seleccionada
     */
    private Date fechaFinal;
    
   

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Variable que almacena el valor del indicador de Consignación.
     */
    private boolean conConsignacion;
    /**
     * Variable que almacena el valor del indicador de Firmas.
     */
    private boolean conFirmas;

    private int indicador;
    /**
     * Variable que almacena el valor del indicador formato especial excel
     */
    private boolean especialExcel;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de Relaciondeingresosupc
     */
    public RelaciondeingresosupcControlador() {
        super();
        modulo = SessionUtil.getModulo();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RELACION_DE_INGRESOS_UPC_CONTROLADOR
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
        fechaInicial = fechaFinal = new Date();
        
        try {
			nombretesorero = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE TESORERO", modulo, new Date(), true), "");
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
		}
        
        try {
			cargotesorero = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"CARGO TESORERO", modulo, new Date(), true), "");
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
		}
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarPDF en la vista
     *
     *
     */
    public void oprimirGenerarPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarExcel en la vista
     *
     *
     */
    public void oprimirGenerarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que contiene la logica para imprimir los reportes en
     * formato pdf y excel
     * 
     * @param formato
     */
    private void generarInforme(FORMATOS formato) {
    	String reporte = "";
        Map<String, Object> reemplazos = new HashMap<>();
        reemplazos.put("compania", compania);
        if (conConsignacion == true) {
            indicador = 1;
        }
        else {
            indicador = 0;
        }
        reemplazos.put("indicador", indicador);
        reemplazos.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));
        reemplazos.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));
        

        if (FORMATOS.EXCEL.equals(formato) && especialExcel) {
        	reporte = "002867RelacionIngresosEspecial";
        } else {
        	reporte = "001787RelacionDeIngresos";
        }

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                        .getNombre().toUpperCase());
        
        try {
            parametros.put("PR_FORMS_RELACIONDEINGRESOS_UPC_FECHAINI",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial,
                                            "dd/MM/yyyy"));
           
            parametros.put("PR_NOMBRE_TESORERO",nombretesorero );
            
            parametros.put("PR_CARGO_TESORERO", cargotesorero);
            
            parametros.put("PR_FORMS_RELACIONDEINGRESOS_UPC_FECHAFIN",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaFinal,
                                            "dd/MM/yyyy"));
        }
        catch (ParseException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()), reemplazos,
                        parametros);
        try {
            archivoDescarga = JsfUtil
                            .exportarStreamed(reporte,
                                            parametros,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ",
                            reporte));
            Logger.getLogger(RelaciondeingresosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * @return the fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * @param fechaInicial
     * the fechaInicial to set
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * @return the fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * @param fechaFinal
     * the fechaFinal to set
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * @return the conConsignacion
     */
    public boolean isConConsignacion() {
        return conConsignacion;
    }

    /**
     * @param conConsignacion
     * the conConsignacion to set
     */
    public void setConConsignacion(boolean conConsignacion) {
        this.conConsignacion = conConsignacion;
    }

    /**
     * @return the conFirmas
     */
    public boolean isConFirmas() {
        return conFirmas;
    }

    /**
     * @param conFirmas
     * the conFirmas to set
     */
    public void setConFirmas(boolean conFirmas) {
        this.conFirmas = conFirmas;
    }

    /**
     * @return the especialExcel
     */
    public boolean isEspecialExcel() {
        return especialExcel;
    }

    /**
     * @param especialExcel
     * the especialExcel to set
     */
    public void setEspecialExcel(boolean especialExcel) {
        this.especialExcel = especialExcel;
    }
    
    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * @return the indicador
     */
    public int getIndicador() {
        return indicador;
    }

    /**
     * @param indicador
     * the indicador to set
     */
    public void setIndicador(int indicador) {
        this.indicador = indicador;
    }

	public String getNombretesorero() {
		return nombretesorero;
	}

	public void setNombretesorero(String nombretesorero) {
		this.nombretesorero = nombretesorero;
	}

	public String getCargotesorero() {
		return cargotesorero;
	}

	public void setCargotesorero(String cargotesorero) {
		this.cargotesorero = cargotesorero;
	}

	

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
