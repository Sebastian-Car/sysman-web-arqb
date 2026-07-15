/*-
 * FrmcartservusosControlador.java
 *
 * 1.0
 * 
 * 15/11/2016
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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.FrmcartservusosControladorEnum;
import com.sysman.serviciospublicos.enums.FrmcartservusosControladorUrlEnum;

import java.io.IOException;
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
 * Controlador del formulario Frmcartservusos
 *
 * @version 1.0, 15/11/2016
 * @author cperez
 * 
 * @author eamaya
 * @version 2, 24/05/2017 Proceso de Refactoring y Manejp de EJBS
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Se cambi� el llamado del c�digo del
 * formulario y actualizaci�n de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped

public class FrmcartservusosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /*
     * variable para asiganar el nombre del tipo de error
     * msm_Trans_Interrumpida
     */
    private String msmTransInterrumpida;
    /*
     * variable para dejar visible o algunos objetos de la vista
     */
    private boolean parFacturacionVisible;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el estado del check Suspendidos y Retirados
     */
    private boolean checkIncluirSuspendidosRetiros;
    /**
     * Obtiene el estado del chec Distriminar Cartera
     */
    private boolean checkDiscriminarCartera;
    /**
     * Obtiene el codigo Inicial de la consulta
     */
    private String cicloInicial;
    /**
     * Obtiene el codigo Final de la consulta
     */
    private String cicloFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Necesario para obtener y mandar la lista del Inicial
     */
    private List<Registro> listacicloInicial;
    /**
     * Necesario para obtener y mandar la lista del Ciclo Final
     */
    private List<Registro> listacicloFinal;

    @EJB
    private EjbSysmanUtilRemote ejbPArametro;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmcartservusosControlador
     */
    public FrmcartservusosControlador() {
        super();
        compania = SessionUtil.getCompania();
        msmTransInterrumpida = "MSM_TRANS_INTERRUMPIDA";

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMCARTSERVUSOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
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
        cargarListacicloInicial();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /*
     * Para saber si el par�metro �INFORME DE CARTERA POR EDADES
     * DISCRIMINADO 180-360� es igual a si o a no
     * 
     * @return parFacturacion
     */
    public String parFacturacion() {
        String parFacturacion = null;
        try {
            parFacturacion = ejbPArametro.consultarParametro(compania,
                            "INFORME DE CARTERA POR EDADES DISCRIMINADO 180-360",
                            SessionUtil.getModulo(), new Date(), false);

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(idioma.getString(msmTransInterrumpida)
                + ex.getMessage());
            Logger.getLogger(FrmcartservusosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return parFacturacion;
    }

    /*
     * Para saber si el par�metro �
     * "FRECUENCIA PERIODOS DE FACTURACION"� es igual a si o a no
     * 
     * @return parFrecuenciaPeriodosDeFacturacion
     */
    public String parFrecuenciaPeriodosDeFacturacion() {
        String parFrecuenciaPeriodosDeFacturacion = null;
        try {
            parFrecuenciaPeriodosDeFacturacion = ejbPArametro
                            .consultarParametro(compania,
                                            "FRECUENCIA PERIODOS DE FACTURACION",
                                            SessionUtil.getModulo(), new Date(),
                                            false);

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(idioma.getString(msmTransInterrumpida)
                + ex.getMessage());
            Logger.getLogger(FrmcartservusosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return parFrecuenciaPeriodosDeFacturacion;
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        if ("SI".equals(parFacturacion())) {
            parFacturacionVisible = true;
        }
        else {
            parFacturacionVisible = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacicloInicial
     */
    public void cargarListacicloInicial() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listacicloInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcartservusosControladorUrlEnum.URL7326
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listacicloFinal
     */
    public void cargarListacicloFinal() {

        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(FrmcartservusosControladorEnum.PARAM0.getValue(),
                            cicloInicial);

            listacicloFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcartservusosControladorUrlEnum.URL7918
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
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     */
    public void oprimirPdf() {

        archivoDescarga = null;

        seleccionReporte(FORMATOS.PDF);

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {

        archivoDescarga = null;

        seleccionReporte(FORMATOS.EXCEL);

    }

    private void seleccionReporte(FORMATOS formato) {
        long cicloIni = Long.parseLong(cicloInicial);
        long cicloFin = Long.parseLong(cicloFinal);
        if (cicloIni > cicloFin) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1823"));
        }
        else {
            if (!checkDiscriminarCartera) {
                genInforme(formato, "001258InfUsos");
            }
            else {
                genInforme(formato, "001257infUsosDiscriminado");
            }
        }

    }

    /**
     * Genera el reporte de Excel y de Pdf
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {
        archivoDescarga = null;
        String conceptoIn = null;
        String inPivot = null;
        String incluirSuspendidosRetiros = null;
        try {

            if (!checkIncluirSuspendidosRetiros) {
                incluirSuspendidosRetiros = " AND SP_USUARIO.ESTADO NOT IN ('S','R')"
                    + " ";

            }
            else {
                incluirSuspendidosRetiros = "";
            }
            if (!checkDiscriminarCartera) {
                if ("B".equals(parFrecuenciaPeriodosDeFacturacion())) {
                    conceptoIn = "  CASE WHEN 60*PERIODOSATRASO>=360 THEN 'C361' ELSE CASE WHEN 60*PERIODOSATRASO>=180 THEN 'C181' ELSE 'C' || 60*PERIODOSATRASO END END  "
                        + " ";
                }
                else {
                    conceptoIn = " CASE WHEN 30*PERIODOSATRASO>360 THEN 'C361' ELSE CASE WHEN 30*PERIODOSATRASO>180 THEN 'C181' ELSE 'C'  || 30*PERIODOSATRASO END END "
                        + " ";
                }
                inPivot = " IN('C0' C0,'C30' C30,'C60' C60,'C90' C90,'C120' C120,'C150' C150,'C180' C180,'C181' C181,'C361' C361) "
                    + " ";
            }
            else {
                if ("B".equals(parFrecuenciaPeriodosDeFacturacion())) {
                    conceptoIn = " CASE WHEN 60*PERIODOSATRASO>=360 THEN 'C361' ELSE CASE WHEN 60*PERIODOSATRASO>=330 THEN 'C331' ELSE 'C' || 60*PERIODOSATRASO  END END "
                        + " ";
                }
                else {
                    conceptoIn = " CASE WHEN 30*PERIODOSATRASO>360 THEN 'C361' ELSE CASE WHEN 30*PERIODOSATRASO>330 THEN 'C331' ELSE 'C' || 30*PERIODOSATRASO END END "
                        + " ";
                }
                inPivot = " IN('C0' C0,'C30' C30,'C60' C60,'C90' C90,'C120' C120,'C150' C150,'C180' C180,'C210' C210,'C240' C240,'C270' C270,'C300' C300,'C330' C330,'C331' C331,'C361' C361)"
                    + " ";
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("cicloInicial", cicloInicial);
            reemplazar.put("cicloFinal", cicloFinal);
            reemplazar.put("conceptoIn", conceptoIn);
            reemplazar.put("inPivot", inPivot);
            reemplazar.put("incluirSuspendidosRetiros",
                            incluirSuspendidosRetiros);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_FRMCARTSERVUSOS_CICLO", cicloInicial);
            parametros.put("PR_FORMS_FRMCARTSERVUSOS_CMBCICLOF", cicloFinal);
            Reporteador.resuelveConsulta("001257infUsosDiscriminado",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control cicloInicial
     */
    public void cambiarcicloInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListacicloFinal();
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable checkIncluirSuspendidosRetiros
     * 
     * @return checkIncluirSuspendidosRetiros
     */
    public boolean getCheckIncluirSuspendidosRetiros() {
        return checkIncluirSuspendidosRetiros;
    }

    /**
     * Asigna la variable parFacturacionVisible
     * 
     * @param parFacturacionVisible
     * Variable a asignar en parFacturacionVisible
     */
    public boolean getParFacturacionVisible() {
        return parFacturacionVisible;
    }

    /**
     * Retorna la variable parFacturacionVisible
     * 
     */
    public void setParFacturacionVisible(boolean parFacturacionVisible) {
        this.parFacturacionVisible = parFacturacionVisible;
    }

    /**
     * Asigna la variable checkIncluirSuspendidosRetiros
     * 
     * @param checkIncluirSuspendidosRetiros
     * Variable a asignar en checkIncluirSuspendidosRetiros
     */
    public void setCheckIncluirSuspendidosRetiros(
        boolean checkIncluirSuspendidosRetiros) {
        this.checkIncluirSuspendidosRetiros = checkIncluirSuspendidosRetiros;
    }

    /**
     * Retorna la variable checkDiscriminarCartera
     * 
     * @return checkDiscriminarCartera
     */
    public boolean getCheckDiscriminarCartera() {
        return checkDiscriminarCartera;
    }

    /**
     * Asigna la variable checkDiscriminarCartera
     * 
     * @param checkDiscriminarCartera
     * Variable a asignar en checkDiscriminarCartera
     */
    public void setCheckDiscriminarCartera(boolean checkDiscriminarCartera) {
        this.checkDiscriminarCartera = checkDiscriminarCartera;
    }

    /**
     * Retorna la variable cicloInicial
     * 
     * @return cicloInicial
     */
    public String getCicloInicial() {
        return cicloInicial;
    }

    /**
     * Asigna la variable cicloInicial
     * 
     * @param cicloInicial
     * Variable a asignar en cicloInicial
     */
    public void setCicloInicial(String cicloInicial) {
        this.cicloInicial = cicloInicial;
    }

    /**
     * Retorna la variable cicloFinal
     * 
     * @return cicloFinal
     */
    public String getCicloFinal() {
        return cicloFinal;
    }

    /**
     * Asigna la variable cicloFinal
     * 
     * @param cicloFinal
     * Variable a asignar en cicloFinal
     */
    public void setCicloFinal(String cicloFinal) {
        this.cicloFinal = cicloFinal;
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
    /**
     * Retorna la lista listacicloInicial
     * 
     * @return listacicloInicial
     */
    public List<Registro> getListacicloInicial() {
        return listacicloInicial;
    }

    /**
     * Asigna la lista listacicloInicial
     * 
     * @param listacicloInicial
     * Variable a asignar en listacicloInicial
     */
    public void setListacicloInicial(List<Registro> listacicloInicial) {
        this.listacicloInicial = listacicloInicial;
    }

    /**
     * Retorna la lista listacicloFinal
     * 
     * @return listacicloFinal
     */
    public List<Registro> getListacicloFinal() {
        return listacicloFinal;
    }

    /**
     * Asigna la lista listacicloFinal
     * 
     * @param listacicloFinal
     * Variable a asignar en listacicloFinal
     */
    public void setListacicloFinal(List<Registro> listacicloFinal) {
        this.listacicloFinal = listacicloFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
