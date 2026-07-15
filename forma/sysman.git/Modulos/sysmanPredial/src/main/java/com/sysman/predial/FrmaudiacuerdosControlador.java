package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrmaudiacuerdosControladorEnum;
import com.sysman.predial.enums.FrmaudiacuerdosControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 25/05/2016
 *
 * @author 15/02/2017 15:17:58 -- Modificado por lcortes
 * 
 * @author eamaya
 * @version 2.0, 29/06/2017, Proceso de Refactoring DSS y cambio de
 * Sysdate por new Date()
 * 
 */
@ManagedBean
@ViewScoped

public class FrmaudiacuerdosControlador extends BeanBaseModal {

    /**
     * Constante que almacenara el numero de la entidad
     */
    private final String compania;

    /**
     * Constante que almacenara el numero del modulo
     */
    private final String modulo;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que almacenara el codigo del acuerdo inicial
     */
    private String acuerdoInicial;

    /**
     * Atributo que almacenara el codigo final del acuerdo
     */
    private String acuerdoFinal;

    /**
     * Atributo que almacenara la fecha inicial del acuerdo
     */
    private Date fechaInicial;

    /**
     * Atributo que almacenara la fecha final del acuerdo
     */
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCmbAcuerdoInicial;
    private RegistroDataModelImpl listaCmbAcuerdoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmaudiacuerdosControlador
     */
    public FrmaudiacuerdosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMAUDIACUERDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmaudiacuerdosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCmbAcuerdoInicial();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCmbAcuerdoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmaudiacuerdosControladorUrlEnum.URL4244
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCmbAcuerdoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOACUERDO.getName());
    }

    public void cargarListaCmbAcuerdoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmaudiacuerdosControladorUrlEnum.URL4926
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmaudiacuerdosControladorEnum.PARAM0.getValue(),
                        acuerdoInicial);

        listaCmbAcuerdoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOACUERDO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("acuerdoInicial", "'" + acuerdoInicial + "'");
            reemplazar.put("acuerdoFinal", "'" + acuerdoFinal + "'");
            reemplazar.put("fechaInicial", "'"
                + SysmanFunciones.convertirAFechaCadena(fechaInicial) + "'");
            reemplazar.put("fechaFinal", "'"
                + SysmanFunciones.convertirAFechaCadena(fechaFinal) + "'");

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_FECHAS", "ENTRE EL "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial) + " Y EL "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta("000821INFAUDIACUERDOS",
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000821INFAUDIACUERDOS",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCmbAcuerdoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        acuerdoInicial = SysmanFunciones.nvlStr(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOACUERDO.getName())
                        .toString(), "");
        acuerdoFinal = null;
        cargarListaCmbAcuerdoFinal();
    }

    public void seleccionarFilaCmbAcuerdoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        acuerdoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOACUERDO.getName())
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getAcuerdoInicial() {
        return acuerdoInicial;
    }

    public void setAcuerdoInicial(String acuerdoInicial) {
        this.acuerdoInicial = acuerdoInicial;
    }

    public String getAcuerdoFinal() {
        return acuerdoFinal;
    }

    public void setAcuerdoFinal(String acuerdoFinal) {
        this.acuerdoFinal = acuerdoFinal;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCmbAcuerdoInicial() {
        return listaCmbAcuerdoInicial;
    }

    public void setListaCmbAcuerdoInicial(
        RegistroDataModelImpl listaCmbAcuerdoInicial) {
        this.listaCmbAcuerdoInicial = listaCmbAcuerdoInicial;
    }

    public RegistroDataModelImpl getListaCmbAcuerdoFinal() {
        return listaCmbAcuerdoFinal;
    }

    public void setListaCmbAcuerdoFinal(
        RegistroDataModelImpl listaCmbAcuerdoFinal) {
        this.listaCmbAcuerdoFinal = listaCmbAcuerdoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
