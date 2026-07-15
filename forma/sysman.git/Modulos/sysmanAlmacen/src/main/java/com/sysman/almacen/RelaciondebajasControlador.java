/*-
 * RelaciondebajasControlador.java
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite seleccionar las fechas inicial y final para
 * generar el informe de relaci�n de elementos de baja. Se accede
 * desde la ruta Panel
 * Principal\Almacen\Informes\Especiales\Relaci�n de Bajas de
 * Devolutivos
 *
 * @version 1.0, 26/01/2017
 * @author lcortes
 * 
 * @version 2, 04/04/2017
 * @author jreina se implemento el llamado al EjbSysmanUtilRemote .
 */
@ManagedBean
@ViewScoped
public class RelaciondebajasControlador extends BeanBaseModal {
    // <DECLARAR_ATRIBUTOS>
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que identifica la fecha inicial seleccionada.
     */
    private Date fechaInicial;
    /**
     * Atributo que identifica la fecha final seleccionada.
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RelaciondebajasControlador
     */
    public RelaciondebajasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RELACIONDEBAJAS_CONTROLADOR
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
        fechaInicial = new Date();
        fechaFinal = new Date();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton BtonPreliminar en la vista
     *
     */
    public void oprimirBtonPreliminar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarVacios() || !validarFechas()) {
            return;
        }
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton BtonExcel en la vista
     *
     */
    public void oprimirBtonExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarVacios() || !validarFechas()) {
            return;
        }
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarVacios() {
        if ((fechaInicial == null) || (fechaFinal == null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2773"));
            return false;
        }
        return true;
    }

    private boolean validarFechas() {
        if (fechaInicial.after(fechaFinal)) {
            fechaFinal = null;
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB147"));
            return false;
        }
        return true;
    }

    private void generarInforme(FORMATOS formato) {
        try {
            String parBodegaInservibles = ejbSysmanUtilRemote
                            .consultarParametro(compania, "BODEGA INSERVIBLES",
                                            SessionUtil.getModulo(), new Date(),
                                            false);
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("parBodegaInservibles", parBodegaInservibles);
            String strSql = Reporteador.resuelveConsulta(
                            "001367ElementosDeBaja",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_DESDE", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_HASTA",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            archivoDescarga = JsfUtil.exportarStreamed("001367ElementosDeBaja",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e) {
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
     * Retorna la variable fechaInicial
     *
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
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
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
