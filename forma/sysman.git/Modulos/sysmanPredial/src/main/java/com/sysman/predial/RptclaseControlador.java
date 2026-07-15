package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.RptclaseControladorEnum;
import com.sysman.predial.enums.RptclaseControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;

import java.io.IOException;
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
 * @author NGOMEZ
 * @version 1, 31/05/2016
 *
 * @author lcortes
 * @version 2, 18/07/2017. Refactorizacion de codigo a las consultas
 * de las listas para usar dss y revision de observacion herramienta
 * SonarLint.
 */
@ManagedBean
@ViewScoped
public class RptclaseControlador extends BeanBaseModal {
    private final String compania;
    private final String nOrden;
    private final String codigo;
    // <DECLARAR_ATRIBUTOS>
    private String claseInicial;
    private String codigoInicial;
    private String codigoFinal;
    private String claseFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaclase1;
    private RegistroDataModelImpl listacodigoini;
    private RegistroDataModelImpl listacodigofin;
    private RegistroDataModelImpl listaCLASE2;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of RptclaseControlador
     */
    public RptclaseControlador() {
        super();
        compania = SessionUtil.getCompania();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        codigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.RPTCLASE_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RptclaseControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaclase1();
        cargarListacodigoini();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaclase1() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptclaseControladorUrlEnum.URL3306
                                                        .getValue());
        listaclase1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, codigo);
    }

    public void cargarListacodigoini() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptclaseControladorUrlEnum.URL3739
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);

        listacodigoini = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListacodigofin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptclaseControladorUrlEnum.URL4586
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);
        param.put(RptclaseControladorEnum.PARAM1.getValue(), codigoInicial);

        listacodigofin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListaCLASE2() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptclaseControladorUrlEnum.URL5550
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(RptclaseControladorEnum.PARAM0.getValue(), claseInicial);

        listaCLASE2 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("claseInicial", claseInicial);
            reemplazar.put("claseFinal", claseFinal);
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000846PREDIALPORCLASES";
            Reporteador.resuelveConsulta(reporte,
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
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaclase1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        claseInicial = registroAux.getCampos().get(codigo).toString();
        cargarListaCLASE2();
    }

    public void seleccionarFilacodigoini(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigo).toString();
        cargarListacodigofin();
    }

    public void seleccionarFilacodigofin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigo).toString();
    }

    public void seleccionarFilaCLASE2(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        claseFinal = registroAux.getCampos().get(codigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getClaseInicial() {
        return claseInicial;
    }

    public void setClaseInicial(String claseInicial) {
        this.claseInicial = claseInicial;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getClaseFinal() {
        return claseFinal;
    }

    public void setClaseFinal(String claseFinal) {
        this.claseFinal = claseFinal;
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
    public RegistroDataModelImpl getListaclase1() {
        return listaclase1;
    }

    public void setListaclase1(RegistroDataModelImpl listaclase1) {
        this.listaclase1 = listaclase1;
    }

    public RegistroDataModelImpl getListacodigoini() {
        return listacodigoini;
    }

    public void setListacodigoini(RegistroDataModelImpl listacodigoini) {
        this.listacodigoini = listacodigoini;
    }

    public RegistroDataModelImpl getListacodigofin() {
        return listacodigofin;
    }

    public void setListacodigofin(RegistroDataModelImpl listacodigofin) {
        this.listacodigofin = listacodigofin;
    }

    public RegistroDataModelImpl getListaCLASE2() {
        return listaCLASE2;
    }

    public void setListaCLASE2(RegistroDataModelImpl listaCLASE2) {
        this.listaCLASE2 = listaCLASE2;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
