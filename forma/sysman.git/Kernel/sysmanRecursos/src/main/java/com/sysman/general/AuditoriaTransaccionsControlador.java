/*-
 * AuditoriaTransaccionsControlador.java
 *
 * 1.0
 * 
 * 18/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para auditar las transacciones
 *
 * @version 1.0, 18/06/2018
 * @author ybecerra
 */
/**
 *
 */
@ManagedBean
@ViewScoped
public class AuditoriaTransaccionsControlador extends BeanBaseModal {

    /**
     * Atributo que almacena el valor del modulo por el cual se
     * ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor de la casilla de verificacion
     * creador
     */
    private boolean creador;
    /**
     * Atributo que almacena el valor de la casilla de verificacion
     * modificador
     */
    private boolean modificador;
    /**
     * Atributo que valida que mensaje se visualiza al usuario
     */
    private boolean validarFecha;
    /**
     * Atributo que almacena el valor del campo Tabla
     */
    private String tabla;
    /**
     * Atributo que almacena el valor del campo Fecha Inicial
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena el valor del campo Fecha Final
     */
    private Date fechaFinal;
    /**
     * Atributo que almacena el valor del campo Cedula
     */
    private String cedula;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de AuditoriaTransaccionsControlador
     */
    public AuditoriaTransaccionsControlador() {
        super();
        modulo = SessionUtil.getModulo();
        try {
            // 1826
            numFormulario = GeneralCodigoFormaEnum.AUDITORIA_TRANSACCION_CONTROLADOR
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
        creador = modificador = true;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();
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
     * Metodo que al hacer llamado al evento del boton excel se
     * ejecuta
     */
    public void generarExcel() {
        try {

            if (fechaFinal.before(fechaInicial)) {
                validarFecha = true;
                ejecutarmensaje();
                return;
            }

            String condicion = null;
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tabla", tabla);

            if (creador && !modificador) {
                condicion = SysmanFunciones.concatenar("CREATED_BY = '", cedula,
                                "' ", " AND DATE_CREATED BETWEEN ",
                                SysmanFunciones.formatearFecha(fechaInicial),
                                "  AND ",
                                SysmanFunciones.formatearFecha(fechaFinal));
            }
            else if (!creador && modificador) {
                condicion = SysmanFunciones.concatenar("MODIFIED_BY = '",
                                cedula, "' ", " AND DATE_MODIFIED BETWEEN ",
                                SysmanFunciones.formatearFecha(fechaInicial),
                                " AND ",
                                SysmanFunciones.formatearFecha(fechaFinal));
            }
            else {
                condicion = SysmanFunciones.concatenar("(CREATED_BY = '",
                                cedula, "' ", " AND DATE_CREATED BETWEEN ",
                                SysmanFunciones.formatearFecha(fechaInicial),
                                " AND  ",
                                SysmanFunciones.formatearFecha(fechaFinal),
                                " OR ",
                                SysmanFunciones.concatenar("MODIFIED_BY = '",
                                                cedula, "' ",
                                                " AND DATE_MODIFIED BETWEEN ",
                                                SysmanFunciones.formatearFecha(
                                                                fechaInicial),
                                                " AND ",
                                                SysmanFunciones.formatearFecha(
                                                                fechaFinal)),
                                ")");
            }

            reemplazar.put("condicion", condicion);
            if (!creador && !modificador) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4132"));
                return;
            }

            String strSql = Reporteador.resuelveConsulta(
                            "800149TransaccionAuditoria",
                            Integer.parseInt(modulo), reemplazar);
            long existeDatos = service.getConteoConsulta(strSql);

            if (existeDatos != 0) {
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL);
            }
            else {
                validarFecha = false;
                ejecutarmensaje();
            }
        }
        catch (SystemException | JRException | IOException | SQLException
                        | DRException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto actualizaAlerta
     * en la vista
     *
     */
    public void ejecutarmensaje() {
        // <CODIGO_DESARROLLADO>
        if (validarFecha) {

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB147"));
        }
        else {
            JsfUtil.agregarMensajeError(idioma.getString(
                            "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
        }

        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable creador
     * 
     * @return creador
     */
    public boolean isCreador() {
        return creador;
    }

    /**
     * Asigna la variable creador
     * 
     * @param creador
     * Variable a asignar en creador
     */
    public void setCreador(boolean creador) {
        this.creador = creador;
    }

    /**
     * Retorna la variable modificador
     * 
     * @return modificador
     */
    public boolean isModificador() {
        return modificador;
    }

    /**
     * Asigna la variable modificador
     * 
     * @param modificador
     * Variable a asignar en modificador
     */

    public void setModificador(boolean modificador) {
        this.modificador = modificador;
    }

    /**
     * Retorna la variable tabla
     * 
     * @return tabla
     */
    public String getTabla() {
        return tabla;
    }

    /**
     * Asigna la variable tabla
     * 
     * @param tabla
     * Variable a asignar en tabla
     */
    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

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
     * Retorna la variable cedula
     * 
     * @return cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Asigna la variable cedula
     * 
     * @param cedula
     * Variable a asignar en cedula
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
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
