/*-
 * LMatriculasControlador.java
 *
 * 1.0
 * 
 * 27/09/2016
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.LMatriculasControladorUrlEnum;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario LMatriculas.
 *
 * @version 1.0, 27/09/2016
 * @author Pablo A. Espitia.
 * 
 * @version 2, 07/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class LMatriculasControlador extends BeanBaseModal {
    private final String compania;
 
    // <DECLARAR_ATRIBUTOS>
    /** Util para conocer el item seleccionado del combo mostrar. */
    private String mostrar;

    /** Útil para conocer el item seleccionado del combo formato. */
    private String opcionFormato;

    /** Fecha inicial del formulario. */
    private Date fechaInicial;

    /** Fecha fin respecto a la inicial del formulario. */
    private Date fechaFinal;

    /** Nombre de quien elaboro el listado de matriculas. */
    private String elaboro;

    /** Nombre del director comercial. */
    private String director;

    /** Cargo del director comercial. */
    private String cargo;
    
    private boolean visibleTipo;
    
    private boolean formatoCalidad;
    
    private StreamedContent archivoDescarga; 

    /** Lista del combo MOSTRAR. */
    private List<Registro> listacmbMostrar;   
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    /**
     * Creates a new instance of LMatriculasControlador
     */
    public LMatriculasControlador() {
        super();
        compania = SessionUtil.getCompania();


        /* Por omision tome el formato 1 */
        opcionFormato = "1";

        /* Por omision tome el Item 1 del combo MOSTRAR */
        mostrar = "1";

        try {
            numFormulario = GeneralCodigoFormaEnum.L_MATRICULAS_CONTROLADOR.getCodigo();        
            validarPermisos();        
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListacmbMostrar();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        formatoCalidad = "SI".equalsIgnoreCase(obtenerParametro("FORMATO CALIDAD"));
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListacmbMostrar() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            listacmbMostrar = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LMatriculasControladorUrlEnum.URL5164
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirEXCEL() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Formato del documento.
     */
    private void getReporte(FORMATOS formato) {

        String reporte="";
        try {  

            /* Recupera el nombre de quien elaboro la solicitud. */
            elaboro = obtenerParametro("NOMBRE ELABORO SOLICITUD SERVICIOS");
            /* Recupera el nombre del director comercial. */
            director = obtenerParametro("NOMBRE DIRECTOR COMERCIAL");
            /* Recupera el cargo del director comercial. */
            cargo = obtenerParametro("CARGO DIRECTOR COMERCIAL");

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reporte = seleccionarReporte();

            /* Reemplaza fechaInicial en la consulta del reporte. */
            reemplazar.put("fechaInicial",
                            // Formatea la fecha a DD/MM/YYYY.
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("compania", compania);
            reemplazar.put("mostrar", mostrar);

            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_ELABORO", elaboro);
            parametros.put("PR_DIRECTOR", director);
            parametros.put("PR_CARGO", cargo);

            /* Envia la fecha inicial como parametro al reporte. */
            parametros.put("PR_FECHAINICIAL",
                            // Casting de DATE a String.
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaFinal));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(LMatriculasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        } 
        catch (JRException | IOException  ex) {
            Logger.getLogger(LMatriculasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException|ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }     
       
    }

    /**
     * Define que reporte se debe generar respecto a la opcion
     * seleccionada en el combo mostrar.
     * 
     * @return Nombre del reporte a generar.
     */
    public String seleccionarReporte() {
        String reporte;

        /*
         * Verifica si el parametro FORMATO CALIDAD tiene valor SI.
         */
        formatoCalidad = "SI"
                        .equalsIgnoreCase(obtenerParametro("FORMATO CALIDAD"));

        /* Ingresa la opcion seleccionada del combo formato. */
        switch (opcionFormato) {
        case "1":
            reporte = formatoCalidad ? "001090LSolicitudes1COS"
                : "001103LSolicitudes1";
            break;
        case "2":
            reporte = formatoCalidad ? "001091LSolicitudes2COS"
                : "001105LSolicitudes2";
            break;
        case "3":
            reporte = formatoCalidad ? "001094LSolicitudes3COS"
                : "001106LSolicitudes3";
            break;
        default:
            reporte = formatoCalidad ? "001101rptMatriculas"
                : obtenerParametro("FORMATO 4 SOLICITUDES MATRICULA");
            break;
        }

        return reporte;
    }
    
    public String obtenerParametro(String nombre){
        String aux = null;
        try {
            
             aux= SysmanFunciones.nvlStr(ejbSysmanUtilRemote
                                            .consultarParametro(compania,
                                                            nombre,
                                                            SessionUtil.getModulo(),
                                                            new Date(), false),
                                            " ");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return aux;
    }

    // </METODOS_BOTONES>
  //<METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control cmbFormato
     * 
     */
    public void cambiarcmbFormato() {
        //<CODIGO_DESARROLLADO>
        switch (opcionFormato) {
        case "1":
        case "4":
            visibleTipo=false;
            break;
        case "2":
            visibleTipo=true;
            break;
        case "3":
            visibleTipo = formatoCalidad ? false : true;
            break;
        default:
            break;
        }
        
        //</CODIGO_DESARROLLADO>
    }
//</METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public String getMostrar() {
        return mostrar;
    }

    public void setMostrar(String mostrar) {
        this.mostrar = mostrar;
    }

    public String getOpcionFormato() {
        return opcionFormato;
    }

    public void setOpcionFormato(String formato) {
        this.opcionFormato = formato;
    }

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

    public String getElaboro() {
        return elaboro;
    }

    public void setElaboro(String elaboro) {
        this.elaboro = elaboro;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListacmbMostrar() {
        return listacmbMostrar;
    }

    public void setListacmbMostrar(List<Registro> listacmbMostrar) {
        this.listacmbMostrar = listacmbMostrar;
    }

    public boolean isVisibleTipo() {
        return visibleTipo;
    }

    public void setVisibleTipo(boolean visibleTipo) {
        this.visibleTipo = visibleTipo;
    }
    
    
}
