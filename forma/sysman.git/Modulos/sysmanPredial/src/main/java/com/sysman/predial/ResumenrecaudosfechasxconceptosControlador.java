package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 08/06/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @modifier amonroy
 * @version 3, 17/07/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para las funciones y procedimientos que son
 * llamadas en el controlador
 */
@ManagedBean
@ViewScoped
public class ResumenrecaudosfechasxconceptosControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    /**
     * Implementacion del EJB de EjbPredialCeroRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_PREDIAL
     */
    @EJB
    private EjbPredialCeroRemote ejbPredialCero;
    // <DECLARAR_ATRIBUTOS>
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of
     * ResumenrecaudosfechasxconceptosControlador
     */
    public ResumenrecaudosfechasxconceptosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.RESUMENRECAUDOSFECHASXCONCEPTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ResumenrecaudosfechasxconceptosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            String entreFechas = idioma.getString("TB_TB3321");
            entreFechas = entreFechas.replace("s$fechaInicial$s",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            entreFechas = entreFechas.replace("s$fechaFinal$s",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_MUNICIPIO", SysmanFunciones.concatenar(
                            idioma.getString("TB_TB3322"), " ",
                            SessionUtil.getCompaniaIngreso()
                                            .getCiudad()
                                            .toUpperCase()));
            parametros.put("PR_DEPARTAMENTO", SysmanFunciones.concatenar(
                            idioma.getString("TB_TB3320"), " ", SessionUtil
                                            .getCompaniaIngreso()
                                            .getDepartamento().toUpperCase()));
            parametros.put("PR_FECHAS", entreFechas);
            parametros.put("PR_CONCEPTO1", consultarEncabezado(1));
            parametros.put("PR_CONCEPTO2", consultarEncabezado(2));
            parametros.put("PR_CONCEPTO3", consultarEncabezado(3));
            parametros.put("PR_CONCEPTO4", consultarEncabezado(4));

            Reporteador.resuelveConsulta("000882PREDIALRECAGRUPADOVIG",
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000882PREDIALRECAGRUPADOVIG", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (ParseException | IOException | JRException
                        | SysmanException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(ResumenrecaudosfechasxconceptosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Obtiene el valor del encabezado para el concepto que ingresa
     * por parametro
     * 
     * @param concepto
     * numero del concepto a consultar
     * @return valor del encabezado
     */
    private String consultarEncabezado(int concepto) {
        String encabezado = "";
        try {
            encabezado = ejbPredialCero.consultarEncabezadoDeColumna(compania,
                            concepto);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return encabezado;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

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
