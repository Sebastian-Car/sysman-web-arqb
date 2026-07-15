package com.sysman.almacen;

import com.sysman.almacen.enums.ImpresionPorLotesRequisicionesControladorEnum;
import com.sysman.almacen.enums.ImpresionPorLotesRequisicionesControladorUrlEnum;
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
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
 *
 * @author jrodriguezr
 * @version 1, 29/01/2016
 * 
 * @version 2, 02/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */
@ManagedBean
@ViewScoped
public class ImpresionPorLotesRequisicionesControlador extends BeanBaseModal {

    private final String compania;
    private final String msgTransInterrumpida;
    private String numeroInicial;
    private String numeroFinal;
    private StreamedContent archivoDescarga;
    private List<Registro> listaNumeroInicial;
    private List<Registro> listaNumeroFinal;

    /**
     * Creates a new instance of
     * ImpresionPorLotesRequisicionesControlador
     */
    public ImpresionPorLotesRequisicionesControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.IMPRESION_POR_LOTES_REQUISICIONES_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        msgTransInterrumpida = "MSM_TRANS_INTERRUMPIDA";
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ImpresionPorLotesRequisicionesControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaNumeroInicial();
        abrirFormulario();
    }

    public void cargarListaNumeroInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaNumeroInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImpresionPorLotesRequisicionesControladorUrlEnum.URL2458
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNumeroFinal() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(ImpresionPorLotesRequisicionesControladorEnum.PARAM0
                            .getValue(), numeroInicial);
            listaNumeroFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImpresionPorLotesRequisicionesControladorUrlEnum.URL2992
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPresentar() {
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel() {
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(numeroInicial)
            || SysmanFunciones.validarVariableVacio(numeroFinal)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB1856"));
            return;
        }
        String reporte = "000495requisicionesporlote";
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DE REEMPLAZO
            reemplazar.put("numeroInicial", numeroInicial);
            reemplazar.put("numeroFinal", numeroFinal);

            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_STRSQL", strSql);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE") + " "
                                + ex.getMessage() + " " + reporte);
            Logger.getLogger(ImpresionPorLotesRequisicionesControlador.class
                            .getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(msgTransInterrumpida) + " "
                                + ex.getMessage());
            Logger.getLogger(ImpresionPorLotesRequisicionesControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarNumeroInicial() {
        // <CODIGO_DESARROLLADO>
        numeroFinal = null;
        cargarListaNumeroFinal();
        // </CODIGO_DESARROLLADO>
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaNumeroInicial() {
        return listaNumeroInicial;
    }

    public void setListaNumeroInicial(List<Registro> listaNumeroInicial) {
        this.listaNumeroInicial = listaNumeroInicial;
    }

    public List<Registro> getListaNumeroFinal() {
        return listaNumeroFinal;
    }

    public void setListaNumeroFinal(List<Registro> listaNumeroFinal) {
        this.listaNumeroFinal = listaNumeroFinal;
    }

    @Override
    public void abrirFormulario() {
        //
    }

}
