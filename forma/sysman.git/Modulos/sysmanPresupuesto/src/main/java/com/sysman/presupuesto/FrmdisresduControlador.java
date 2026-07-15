package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FrmdisresduControladorEnum;
import com.sysman.presupuesto.enums.FrmdisresduControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 20/06/2016
 * @modified jsforero
 * @version 2. 18/04/2017 Se realizo el refactory.
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class FrmdisresduControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private int ano;
    private String nombreCuentaIni;
    private String nombreCuentaFin;
    private Date fechaInicial;
    private Date fechaFinal;
    private boolean formatoEspecialExcel;
    private boolean conId;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private static final String CODIGO = "CODIGO";

    /**
     * Creates a new instance of FrmdisresduControlador
     */
    public FrmdisresduControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = 933;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmdisresduControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        ano = SysmanFunciones.ano(new Date());

        // <CARGAR_LISTA>
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = fechaFinal = new Date();

        cuentaInicial = "0";
        cuentaFinal = "9";

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        UrlBean urlListAno = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdisresduControladorUrlEnum.URL3940
                                                        .getValue());
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListAno.getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(FrmdisresduControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdisresduControladorUrlEnum.URL4371
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);

    }

    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdisresduControladorUrlEnum.URL5293
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmdisresduControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    private void obtenerReporte(FORMATOS formatos) {

        String reporte = "000929DISRESDU";

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("codigoInicial", cuentaInicial);
            reemplazar.put("codigoFinal", cuentaFinal);
            reemplazar.put("ano", ano);
            reemplazar.put("conId", conId ? "S" : "N");
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();

            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_CUENTAFINAL", cuentaFinal);
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal));
            parametros.put("PR_CUENTAINICIAL", cuentaInicial);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FORMATO_ESPECIAL_EXCEL", formatoEspecialExcel);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (ParseException
                        | OutOfMemoryError | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control FechaInicial. Gestiona
     * los eventos que se deben ejecutar al cambiar su valor.
     */
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        if ((fechaInicial != null) && (ano != 0)
            && (SysmanFunciones.ano(fechaInicial) != ano)) {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB3202")
                            .replace("#POS#", "inicial"));

            fechaInicial = fechaFinal = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFinal. Gestiona los
     * eventos que se deben ejecutar al cambiar su valor.
     */
    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        if ((fechaFinal != null) && (ano != 0)
            && (SysmanFunciones.ano(fechaFinal) != ano)) {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB3202")
                            .replace("#POS#", "final"));

            fechaFinal = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = fechaFinal = null;
        listaCuentaInicial = listaCuentaFinal = null;
        cuentaInicial = cuentaFinal = null;
        nombreCuentaIni = nombreCuentaFin = "";

        cargarListaCuentaInicial();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();

        nombreCuentaIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

        cuentaFinal = null;
        nombreCuentaFin = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();

        nombreCuentaFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getNombreCuentaIni() {
        return nombreCuentaIni;
    }

    public void setNombreCuentaIni(String nombreCuentaIni) {
        this.nombreCuentaIni = nombreCuentaIni;
    }

    public String getNombreCuentaFin() {
        return nombreCuentaFin;
    }

    public void setNombreCuentaFin(String nombreCuentaFin) {
        this.nombreCuentaFin = nombreCuentaFin;
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

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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

    /**
     * @return the ckFormatoEspecial
     */
    public boolean isformatoEspecialExcel() {
        return formatoEspecialExcel;
    }

    /**
     * @param ckFormatoEspecial
     * the ckFormatoEspecial to set
     */
    public void setformatoEspecialExcel(boolean formatoEspecialExcel) {
        this.formatoEspecialExcel = formatoEspecialExcel;
    }
    
    public boolean isConId() {
        return conId;
    }

    public void setConId(boolean conId) {
        this.conId = conId;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
