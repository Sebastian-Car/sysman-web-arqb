/*-
 * FrmListadoCausadaSinRecControlador.java
 *
 * 1.0
 * 
 * 07/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que contiene la logica de la forma:
 * <strong>frmlistadocausadasinrec</strong>.
 *
 * @version 1.0, 07/11/2017
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmListadoCausadaSinRecControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String modulo = SessionUtil.getModulo();

    /**
     * Constante a nivel de clase que aloja el nombre de la compania
     * desde la cual el usuario inicio sesion.
     */
    private final String nombreCompania = SessionUtil.getCompaniaIngreso()
                    .getNombre();

    // <DECLARAR_ATRIBUTOS>
    /** Atributo que contiene el valor del campo fecha inicial. */
    private Date fechaInicial;

    /** Atributo que contiene el valor del campo fecha final. */
    private Date fechaFinal;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    /**
     * Variable que contiene el nombre del tipo de cobro seleccionado
     * al iniciar sesion en el modulo.
     */
    private String nomTipoCobro;

    /**
     * Variable que contiene el tipo de cobro seleccionado al iniciar
     * sesion en el modulo.
     */
    private String tipoCobro;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmListadoCausadaSinRecControlador
     */
    public FrmListadoCausadaSinRecControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1432
            numFormulario = GeneralCodigoFormaEnum.FRM_LISTADO_CAUSADA_SIN_REC_CONTROLADOR
                            .getCodigo();

            // Variables de sesion
            tipoCobro = SessionUtil.getSessionVar(
                            ConstantesFacturacionGenEnum.TIPOCOBRO.getValue())
                            .toString();

            nomTipoCobro = SessionUtil
                            .getSessionVar(ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO
                                            .getValue())
                            .toString();

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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /** Metodo ejecutado al oprimir el boton BtPdf en la vista. */
    public void oprimirBtPdf() {
        // <CODIGO_DESARROLLADO>
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /** Metodo ejecutado al oprimir el boton BtExcel en la vista. */
    public void oprimirBtExcel() {
        // <CODIGO_DESARROLLADO>
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        String reporte = "001481InfLisFacSinRecaudo";

        archivoDescarga = null;

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("tipoCobro", "'".concat(tipoCobro).concat("'"));

        reemplazar.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));

        reemplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));

        // </REEMPLAZAR VARIABLES EN CONSULTA>

        try {
            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
            parametros.put("PR_NOMTIPOCOBRO", nomTipoCobro);

            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));

            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);

            /*-aqui reporte hace referencia al nombre del reporte*/

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <SET_GET_ATRIBUTOS>

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

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
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
