/*-
 * FrmLisMovDiarioIngresosControlador.java
 *
 * 1.0
 * 
 * 28/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.FrmLisMovDiarioIngresosControladorEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * 
 * 
 * 
 * 
 * @version 1.0, 28/05/2018
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmLisMovDiarioIngresosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    /**
     * variable que almacena la compañia
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private String modulo = SessionUtil.getModulo();
    /**
     * variable que almacena la fecha
     */
    private Date fecha;
    /**
     * variable que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Crea una nueva instancia de FrmLisMovDiarioIngresosControlador
     */
    public FrmLisMovDiarioIngresosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_LIST_MOV_DIARIO_INGRESOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            fecha = new Date();

        }
        catch (Exception ex) {
            Logger.getLogger(FrmLisMovDiarioIngresosControladorEnum.class
                            .getName())
                            .log(Level.SEVERE, null, ex);
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
        /*
         * FR1799-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }
    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista para
     * obtener el reporte
     * 
     * @throws ParseException
     *
     */
    public void oprimirImprimir() throws ParseException {

        archivoDescarga = null;
        generarInfome(ReportesBean.FORMATOS.PDF);

    }

    /**
     * Metodo que permite generar el reporte en pdf
     * 
     */
    public void generarInfome(ReportesBean.FORMATOS formato)
                    throws ParseException {

        try {

            String reporte = "001774LisMovDiarioIngresos";

            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("compania", compania);

            reemplazar.put("fecha", SysmanFunciones.formatearFecha(fecha));

            Map<String, Object> parametros = new HashMap<>();

            parametros.put(FrmLisMovDiarioIngresosControladorEnum.PR_COMPANIA
                            .getValue(), compania);

            parametros.put(FrmLisMovDiarioIngresosControladorEnum.PR_FECHA
                            .getValue(), fecha);

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            parametros.put("PR_FORMS_LISMOVDIARIOINGRESOS_FECHA",
                            SysmanFunciones.convertirAFechaCadena(fecha));
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Retorna la variable fecha
     * 
     * @return fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Asigna la variable fecha
     * 
     * @param fecha
     * Variable a asignar en fecha
     */
    public void setFecha(Date fecha) {

        this.fecha = fecha;
    }

    public void setArchivoDescarga(StreamedContent descargaArchivo) {
        this.archivoDescarga = descargaArchivo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

}
