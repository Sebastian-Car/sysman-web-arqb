package com.sysman.presupuesto;

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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FrmregistrcompromisosduControladorEnum;
import com.sysman.presupuesto.enums.FrmregistrcompromisosduControladorUrlEnum;
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
 * @author lcortes
 * @version 1, 20/06/2016
 * 
 * @version 2, 18/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Se aplico Refactoring.<br>
 * Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class FrmregistrcompromisosduControlador extends BeanBaseModal {
    private final String compania;
    /**
     * Constante definida para almacenar "CODIGO"
     */
    private final String codigo;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;

    /**
     * Atributo que controla el valor del combo anio en la forma
     */
    private int anio;
    private String nomCuentaInicial;
    private String nomCuentaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private boolean conId;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmregistrcompromisosduControlador
     */
    public FrmregistrcompromisosduControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMREGISTRCOMPROMISOSDU_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmregistrcompromisosduControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        anio = SysmanFunciones.ano(new Date());

        // <CARGAR_LISTA>
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        fechaFinal = fechaInicial = new Date();
        // <CODIGO_DESARROLLADO>
        /*
         * FR928-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Metodo que carga de elementos la lista: <code>listAnio</code>
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmregistrcompromisosduControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmregistrcompromisosduControladorUrlEnum.URL3891
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmregistrcompromisosduControladorUrlEnum.URL4830
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");
        param.put(FrmregistrcompromisosduControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirinforme() {
        generarInforme(ReportesBean.FORMATOS.PDF);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdExcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {
            String reporte = "000924RegistroCompromisosObligaciones";

            if (anio != 0 || SysmanFunciones.validarVariableVacio(cuentaInicial)
                || SysmanFunciones.validarVariableVacio(cuentaFinal)) {

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB74"));
            }
            if ((fechaInicial == null) || (fechaFinal == null)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB74"));
            }

            HashMap<String, Object> reemplazos = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            // Reemplazos valores consulta reporte
            reemplazos.put("cuentaInicial", cuentaInicial);
            reemplazos.put("cuentaFinal", cuentaFinal);
            reemplazos.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazos.put("conId", conId ? "S" : "N");
            
            // Inicio Par�metros Reporte
            parametros.put("PR_CUENTAINICIAL", cuentaInicial);
            parametros.put("PR_CUENTAFINAL", cuentaFinal);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Gestiona los eventos que se deben ejecutar al interactuar con
     * el combo anio en la forma
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = "";
        nomCuentaInicial = "";
        cuentaFinal = "";
        nomCuentaFinal = "";
        listaCuentaInicial = null;
        listaCuentaFinal = null;
        fechaFinal = fechaInicial = null;

        cargarListaCuentaInicial();
        validarFechas();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Gestiona los eventos que se deben ejecutar al cambiar el valor
     * del campo fecha inicial en la forma
     */
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        fechaFinal = null;

        validarFechas();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que gestiona los eventos que se deben ejecutar al
     * cambiar el valor de la fecha final en la forma
     */
    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        validarFechas();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = asignarValorCampo(registroAux, codigo);
        nomCuentaInicial = asignarValorCampo(registroAux, "NOMBRE");
        cuentaFinal = "";
        nomCuentaFinal = "";

        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = asignarValorCampo(registroAux, codigo);
        nomCuentaFinal = asignarValorCampo(registroAux, "NOMBRE");
    }

    // </METODOS_COMBOS_GRANDES>

    private void validarFechas() {
        if (fechaInicial != null && SysmanFunciones.ano(fechaInicial) != anio) {
            fechaInicial = fechaFinal = null;

            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB3192")
                            .replace("#POS#", "inicial")
                            .replace("#ANIO#", Integer.toString(anio)));
            return;
        }

        if (fechaFinal != null && SysmanFunciones.ano(fechaFinal) != anio) {
            fechaFinal = null;

            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB3192")
                            .replace("#POS#", "final")
                            .replace("#ANIO#", Integer.toString(anio)));
        }
    }

    /**
     * Verifica que el registro <code>reg</code> tenga una coleccion
     * de campos que no sea nula.
     * 
     * @param reg
     * @param campo
     * El campo a evaluar en la coleccion.
     * @return El valor del campo segun la coleccion.
     */
    private String asignarValorCampo(Registro reg, String campo) {
        return reg.getCampos().isEmpty() ? ""
            : SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                : reg.getCampos().get(campo).toString();
    }

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

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getNomCuentaInicial() {
        return nomCuentaInicial;
    }

    public void setNomCuentaInicial(String nomCuentaInicial) {
        this.nomCuentaInicial = nomCuentaInicial;
    }

    public String getNomCuentaFinal() {
        return nomCuentaFinal;
    }

    public void setNomCuentaFinal(String nomCuentaFinal) {
        this.nomCuentaFinal = nomCuentaFinal;
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

    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
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
    public boolean isConId() {
        return conId;
    }

    public void setConId(boolean conId) {
        this.conId = conId;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
