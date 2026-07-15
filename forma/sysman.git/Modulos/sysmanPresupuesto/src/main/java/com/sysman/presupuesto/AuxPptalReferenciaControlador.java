package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.AuxPptalReferenciaControladorEnum;
import com.sysman.presupuesto.enums.AuxPptalReferenciaControladorUrlEnum;
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
 * @author jrodriguezr
 * @version 1, 06/07/2016
 * 
 * @version 2, 17/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class AuxPptalReferenciaControlador extends BeanBaseModal {
    private final String compania;
    private final String codigo;
    // <DECLARAR_ATRIBUTOS>
    private String tipoInicial;
    private String tipoFinal;
    private String referenciaInicial;
    private String referenciaFinal;
    private String cuentaInicial;
    private String cuentaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaReferenciaInicial;
    private RegistroDataModelImpl listaReferenciaFinal;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String nombreRefInicial;
    private String nombreRefFinal;

    /**
     * Creates a new instance of AuxPptalReferenciaControlador
     */
    public AuxPptalReferenciaControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.AUX_PPTAL_REFERENCIA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(AuxPptalReferenciaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        fechaInicial = fechaFinal = new Date();
        cargarListaTipoInicial();
        cargarListaReferenciaInicial();
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTipoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxPptalReferenciaControladorUrlEnum.URL3663
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListaTipoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxPptalReferenciaControladorUrlEnum.URL4414
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AuxPptalReferenciaControladorEnum.PARAM0.getValue(),
                        tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListaReferenciaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxPptalReferenciaControladorUrlEnum.URL5487
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(fechaInicial));

        listaReferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListaReferenciaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxPptalReferenciaControladorUrlEnum.URL6666
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(AuxPptalReferenciaControladorEnum.PARAM0.getValue(),
                        referenciaInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(fechaInicial));

        listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);

    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxPptalReferenciaControladorUrlEnum.URL8028
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(fechaInicial));

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxPptalReferenciaControladorUrlEnum.URL9056
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(fechaInicial));
        param.put(AuxPptalReferenciaControladorEnum.PARAM1.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarVacio() {

        if ((cuentaInicial == null) || "".equals(cuentaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB268"));
            return false;
        }
        if ((cuentaFinal == null) || "".equals(cuentaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB269"));
            return false;
        }
        if ((tipoInicial == null) || "".equals(tipoInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB270"));
            return false;
        }

        return true;

    }

    public boolean validarVacioDos() {

        if ((tipoFinal == null) || "".equals(tipoFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB271"));
            return false;
        }

        if ((referenciaInicial == null) || "".equals(referenciaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB272"));
            return false;
        }
        if ((referenciaFinal == null) || "".equals(referenciaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB273"));
            return false;
        }
        return true;
    }

    public boolean validarVacioTres() {
        if (fechaInicial == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB274"));
            return false;
        }
        if (fechaFinal == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB275"));
            return false;
        }
        return true;
    }

    private void generaReporte(FORMATOS formato) {
        try {
            if (!validarVacio() || !validarVacioDos() || !validarVacioTres()) {

                return;
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000978LisAuxPptalReferencias";

            String fechaIni = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);
            String fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);

            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("fechaInicial", fechaIni);
            reemplazar.put("fechaFinal", fechaFin);
            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoFinal", tipoFinal);
            reemplazar.put("referenciaInicial", referenciaInicial);
            reemplazar.put("referenciaFinal", referenciaFinal);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_REFERENCIAINICIAL",
                            referenciaInicial + " - " + nombreRefInicial);
            parametros.put("PR_REFERENCIAFINAL",
                            referenciaFinal + " - " + nombreRefFinal);
            parametros.put("PR_CUENTAINICIAL", cuentaInicial);
            parametros.put("PR_CUENTAFINAL", cuentaFinal);
            parametros.put("PR_FECHAINICIAL", fechaIni);
            parametros.put("PR_FECHAFINAL", fechaFin);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = cuentaFinal = referenciaInicial = referenciaFinal = null;
        cargarListaCuentaInicial();
        cargarListaReferenciaInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        if (((fechaInicial != null) && (fechaFinal != null))
            && fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB276"));
            fechaFinal = null;
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = registroAux.getCampos().get(codigo).toString();
        cargarListaTipoFinal();
        tipoFinal = null;
    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = registroAux.getCampos().get(codigo).toString();
    }

    public void seleccionarFilaReferenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaInicial = registroAux.getCampos().get(codigo).toString();
        nombreRefInicial = registroAux.getCampos().get("NOMBRE").toString();
        cargarListaReferenciaFinal();
        referenciaFinal = null;
    }

    public void seleccionarFilaReferenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaFinal = registroAux.getCampos().get(codigo).toString();
        nombreRefFinal = registroAux.getCampos().get("NOMBRE").toString();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("ID").toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("ID").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTipoInicial() {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
    }

    public String getReferenciaInicial() {
        return referenciaInicial;
    }

    public void setReferenciaInicial(String referenciaInicial) {
        this.referenciaInicial = referenciaInicial;
    }

    public String getReferenciaFinal() {
        return referenciaFinal;
    }

    public void setReferenciaFinal(String referenciaFinal) {
        this.referenciaFinal = referenciaFinal;
    }

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal() {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
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

    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaReferenciaFinal() {
        return listaReferenciaFinal;
    }

    public void setListaReferenciaFinal(
        RegistroDataModelImpl listaReferenciaFinal) {
        this.listaReferenciaFinal = listaReferenciaFinal;
    }

    public RegistroDataModelImpl getListaReferenciaInicial() {
        return listaReferenciaInicial;
    }

    public void setListaReferenciaInicial(
        RegistroDataModelImpl listaReferenciaInicial) {
        this.listaReferenciaInicial = listaReferenciaInicial;
    }

    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
