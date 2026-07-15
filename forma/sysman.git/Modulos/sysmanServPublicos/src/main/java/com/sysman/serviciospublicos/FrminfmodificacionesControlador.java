/*-
 * FrminfmodificacionesControlador.java
 *
 * 1.0
 *
 * 25/10/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.FrminfmodificacionesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del informe Frmmofificaciones, en el cual se genera el
 * informe infModificaciones
 *
 * @version 1.0, 25/10/2016
 * @author NGOMEZ
 * 
 * @version 2.0, 26/05/2017, <strong>pespitia:</strong><br>
 * Refactoring.
 */
@ManagedBean
@ViewScoped
public class FrminfmodificacionesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que representa si el informe se genera por facturado o
     * por deuda
     */
    private String tipoInforme;
    /**
     * Variable que representa el ciclo en que se va a generar el
     * informe. Si toma T equivale a Todos.
     */
    private String ciclo;
    /**
     * Variable que representa la fecha inicial para generar el
     * informe
     */
    private Date fechaInicial;
    /**
     * Variable que representa la fecha final para generar el informe
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Lista del combo ciclo
     */
    private List<Registro> listaCiclo; 
    /**
     * Crea una nueva instancia de FrminfmodificacionesControlador
     */
    public FrminfmodificacionesControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMINFMODIFICACIONES_CONTROLADOR.getCodigo();
            validarPermisos();    
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
        tipoInforme = "2";
        cargarListaCiclo();
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
     *
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminfmodificacionesControladorUrlEnum.URL4727
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton verificarConceptos en la
     * vista
     *
     * La funcion delm boton ya no aplica debido a que en el nuevo
     * modelo el servicio es obligatorio
     */
    public void oprimirverificarConceptos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInformeUno(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInformeUno(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }
    /**
     * Proceso en que se genera el reporte
     *
     * @param formato
     * Formato en que se desea generar el reporte
     *
     */
    public void genInformeUno(ReportesBean.FORMATOS formato) {

        String reporte = "001162InfModificaciones";
        try {


            String fechaInicialAux = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);
            String fechaFinalAux = SysmanFunciones
                            .convertirAFechaCadena(fechaFinal);

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", "T".equals(ciclo) ? ""
                : "AND SP_MODIFICACIONES.CICLO=" + ciclo);
            reemplazar.put("fechaInicial", fechaInicialAux);
            reemplazar.put("fechaFinal", fechaFinalAux);
            reemplazar.put("tipo", tipoInforme);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_FRMINFMODIFICACIONES_TIPOINFORME",
                            tipoInforme);
            parametros.put("PR_FORMS_FRMINFMODIFICACIONES_TXTFECHAINICIAL",
                            fechaInicialAux);
            parametros.put("PR_FORMS_FRMINFMODIFICACIONES_TXTFECHAFINAL",
                            fechaFinalAux);
            parametros.put("PR_FORMS_FRMINFMODIFICACIONES_CICLO",
                            "T".equals(ciclo) ? "Todos" : ciclo);
            parametros.put("PR_VIGILADOPOR", SessionUtil.getCompaniaIngreso()
                            .getRutaVigiladoPor());

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SysmanException|ParseException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrminfmodificacionesControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(FrminfmodificacionesControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        } 
        catch ( JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                            + " "
                            + ex.getMessage());
            Logger.getLogger(FrminfmodificacionesControlador.class.getName())
            .log(Level.SEVERE, null, ex);
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
     * Retorna la variable tipoInforme
     *
     * @return tipoInforme
     */
    public String getTipoInforme() {
        return tipoInforme;
    }

    /**
     * Asigna la variable tipoInforme
     *
     * @param tipoInforme
     * Variable a asignar en tipoInforme
     */
    public void setTipoInforme(String tipoInforme) {
        this.tipoInforme = tipoInforme;
    }

    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
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
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    } 


}
