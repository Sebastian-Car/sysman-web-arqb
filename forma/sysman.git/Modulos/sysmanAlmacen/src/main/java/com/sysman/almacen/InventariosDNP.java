/*-
 * InventariosDNP.java
 *
 * 1.0
 * 
 * 4/02/2020
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.EJB;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que imprime los inventarios DNP
 *
 * @version 1.0, 04/02/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class InventariosDNP extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Cadena que guarda el titulo del formulario
     */
    private String tituloFormulario;
    
   // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InventariosDNP
     */
    @EJB
   	private EjbSysmanUtilRemote ejbSysmanUtil;
       
    public InventariosDNP() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 2156
            numFormulario = GeneralCodigoFormaEnum.INVENTARIOS_DNP_CONTROLADOR
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

        if ("10040336".equals(SessionUtil.getMenuActual())) {

            tituloFormulario = idioma.getString("TB_TB4347");
        }
        else {
            tituloFormulario = idioma.getString("TB_TB4348");
        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarExcel(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generarExcel(FORMATOS formato) {

        String nombreArchivo;
        String consulta;
        String valPar = "NO";

        Map<String, Object> reemplazar = new TreeMap<>();

        reemplazar.put("compania", compania);

        if ("10040336".equals(SessionUtil.getMenuActual())) {

            consulta = "800357InventarioDNP";
            nombreArchivo = "InventarioDNPMuebles";
        }
        else {
        	try {
        		valPar = ejbSysmanUtil.consultarParametro(compania,
        								"VISUALIZAR COLUMNA PRECIO INVENTARIO INMUEBLES DNP",
        								SessionUtil.getModulo(), new Date(), true);
        		}
            catch ( SystemException e)
        	{
            	logger.error(e.getMessage(), e);
            	JsfUtil.agregarMensajeError(e.getMessage());
        	}
            
        	if (valPar.equals("NO")) {
        		consulta = "800358InventarioInmueblesDNP";
        		nombreArchivo = "InventarioDNPInmuebles";
        		
        	} else {
        		consulta = "800573InventarioInmueblesDNPvlr";
        		nombreArchivo = "InventarioDNPInmuebles";
        	}
        }

        String sql = Reporteador.resuelveConsulta(consulta,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);

        try {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato, nombreArchivo);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
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
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getTituloFormulario() {
        return tituloFormulario;
    }

    public void setTituloFormulario(String tituloFormulario) {
        this.tituloFormulario = tituloFormulario;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
