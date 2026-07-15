/*-
 * RptManualFuncionesControlador.java
 *
 * 1.0
 * 
 * 27/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma rptmanualfunciones asociada al formulario
 * Manuales de funciones. Que permite generar un informe, en pdf o
 * excel de las funciones, criterios de desempe�o, conocimientos
 * basicos, estudios, experiencia y equivalencias de un cargo (clase
 * de trabajo).
 * 
 * @version 1.0, 27/02/2017
 * @author jlramirez
 */
@ManagedBean
@ViewScoped
public class RptManualFuncionesControlador extends BeanBaseModal {
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo del nivel del cargo del que se
     * quiere conocer el manual de funciones.
     */
    private String codigo;
    /**
     * Atributo que almacena el grado del cargo del que se quiere
     * conocer el manual de funciones.
     */
    private String grado;
    /**
     * Atributo que almacena la fecha de creacion del cargo del que se
     * quiere conocer el manual de funciones.
     */
    private String fecha;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que almacena la denominacion del cargo, traida de la
     * combo de nivel
     */
    private String cargo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que contiene la informaci�n de los detalles del combo
     * del grado del cargo.
     */
    private List<Registro> listaGrado;
    /**
     * Lista que contiene la informaci�n de los detalles del combo
     * de la fecha de creacion del cargo.
     */
    private List<Registro> listaFechaCreacion;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene la informaci�n de los detalles del combo
     * del nivel del cargo.
     */
    private RegistroDataModel listacodigo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RptManualFuncionesControlador
     */
    public RptManualFuncionesControlador() {
        super();
        try {
            numFormulario = 1312;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RptManualFuncionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
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
        cargarListacodigo();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listacodigo
     */
    public void cargarListacodigo() {
        listacodigo = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR1312_nuevo:TBCB4313",
                        "SELECT   DISTINCT NAT_HVCARGOS.CODIGO,"
                            + "   NAT_HVCARGOS.DENOMINACION " +
                            " FROM     NAT_HVCARGOS " +
                            " ORDER BY NAT_HVCARGOS.CODIGO",
                        true, "CODIGO");
    }

    /**
     * Carga la lista listaGrado
     */
    public void cargarListaGrado() {
        listaGrado = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "SELECT DISTINCT NAT_HVCARGOS.GRADO,"
                            + "    NAT_HVCARGOS.DENOMINACION  " +
                            " FROM   NAT_HVCARGOS " +
                            " WHERE  NAT_HVCARGOS.CODIGO = " + codigo);
    }

    /**
     * 
     * Carga la lista listaFechaCreacion
     *
     */
    public void cargarListaFechaCreacion() {
        listaFechaCreacion = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "SELECT DISTINCT TO_CHAR(NAT_HVCARGOS.FECHACREACION,'DD/MM/YYYY') FECHACREACION "
                            +
                            " FROM NAT_HVCARGOS " +
                            " WHERE NAT_HVCARGOS.CODIGO = " + codigo +
                            "   AND NAT_HVCARGOS.GRADO = " + grado);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * M�todo que genera un reporte con el formato elegido por el
     * usuario.
     * 
     * @param formato
     * Extension o tipo de reporte a generar.
     */
    private void generarReporte(FORMATOS formato) {

        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        String reporte = "001423RhvManualFunciones";
        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("fecha", "'" + fecha + "'");
        reemplazar.put("grado", grado);
        reemplazar.put("codigo", codigo);
        // </REEMPLAZAR VARIABLES EN CONSULTA>
        // <ENVIAR PARAMETROS AL REPORTE>
        parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        // </ENVIAR PARAMETROS AL REPORTE>
        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()), reemplazar,
                        parametros);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }

        catch (FileNotFoundException ex) {
            String msj = idioma.getString("MSM_INFORME_VAR_NO_EXISTE");
            msj = msj.replace("s$reporte$s", reporte + " y 001424Funciones");
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + msj);
            Logger.getLogger(RptManualFuncionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(RptManualFuncionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control Grado
     */
    public void cambiarGrado() {
        // <CODIGO_DESARROLLADO>
        cargarListaFechaCreacion();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigo = (String) registroAux.getCampos().get("CODIGO");
        cargo = (String) registroAux.getCampos().get("DENOMINACION");
        cargarListaGrado();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigo
     * 
     * @return codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Asigna la variable codigo
     * 
     * @param codigo
     * Variable a asignar en codigo
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Retorna la variable grado
     * 
     * @return grado
     */
    public String getGrado() {
        return grado;
    }

    /**
     * Asigna la variable grado
     * 
     * @param grado
     * Variable a asignar en grado
     */
    public void setGrado(String grado) {
        this.grado = grado;
    }

    /**
     * Retorna la variable fecha
     * 
     * @return fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Asigna la variable fecha
     * 
     * @param fecha
     * Variable a asignar en fecha
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable cargo
     * 
     * @return cargo
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * Asigna la variable cargo
     * 
     * @param cargo
     * Variable a asignar en cargo
     */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaGrado
     * 
     * @return listaGrado
     */
    public List<Registro> getListaGrado() {
        return listaGrado;
    }

    /**
     * Asigna la lista listaGrado
     * 
     * @param listaGrado
     * Variable a asignar en listaGrado
     */
    public void setListaGrado(List<Registro> listaGrado) {
        this.listaGrado = listaGrado;
    }

    /**
     * Retorna la lista listaFechaCreacion
     * 
     * @return listaFechaCreacion
     */
    public List<Registro> getListaFechaCreacion() {
        return listaFechaCreacion;
    }

    /**
     * Asigna la lista listaFechaCreacion
     * 
     * @param listaFechaCreacion
     * Variable a asignar en listaFechaCreacion
     */
    public void setListaFechaCreacion(List<Registro> listaFechaCreacion) {
        this.listaFechaCreacion = listaFechaCreacion;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacodigo
     * 
     * @return listacodigo
     */
    public RegistroDataModel getListacodigo() {
        return listacodigo;
    }

    /**
     * Asigna la lista listacodigo
     * 
     * @param listacodigo
     * Variable a asignar en listacodigo
     */
    public void setListacodigo(RegistroDataModel listacodigo) {
        this.listacodigo = listacodigo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
