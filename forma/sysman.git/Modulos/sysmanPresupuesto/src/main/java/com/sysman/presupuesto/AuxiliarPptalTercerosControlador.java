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
import com.sysman.presupuesto.enums.AuxiliarPptalTercerosControladorEnum;
import com.sysman.presupuesto.enums.AuxiliarPptalTercerosControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * @version 1, 06/07/2016 17:01:53 -- Modificado por jrodriguezr
 * 
 * @version 2, 17/04/2017, <strong>pespitia</strong> :<br>
 * Se aplico Refactoring.
 */
@ManagedBean
@ViewScoped
public class AuxiliarPptalTercerosControlador extends BeanBaseModal {
    private final String compania;
    private final String cod;
    // <DECLARAR_ATRIBUTOS>
    private String terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String tipoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String tipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private Date fechaInicial;
    private Date fechaFinal;
    private boolean formatoEspecialExcel;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of AuxiliarPptalTercerosControlador
     */
    public AuxiliarPptalTercerosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cod = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.AUXILIAR_PPTAL_TERCEROS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(AuxiliarPptalTercerosControlador.class.getName())
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
        cargarListaTerceroInicial();
        cargarListaTipoInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarPptalTercerosControladorUrlEnum.URL3640
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarPptalTercerosControladorUrlEnum.URL4377
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AuxiliarPptalTercerosControladorEnum.NITINICIAL.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTipoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarPptalTercerosControladorUrlEnum.URL5264
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    public void cargarListaTipoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarPptalTercerosControladorUrlEnum.URL5993
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AuxiliarPptalTercerosControladorEnum.CODIGOINICIAL.getValue(),
                        tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
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
        generaReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarVacios() {
        if (SysmanFunciones.validarVariableVacio(terceroInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB618"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(terceroFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB620"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(tipoInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB614"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(tipoFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB616"));
            return false;
        }

        return true;
    }

    public boolean validarNulos() {
        if (fechaInicial == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB621"));
            return false;
        }
        if (fechaFinal == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB622"));
            return false;
        }
        return true;
    }

    private void generaReporte(FORMATOS formato) {
        String reporte = "000982LisAuxPptalTerceros";
        try {

            if (!validarVacios() || !validarNulos()) {
                return;
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            String fechaIni;
            String fechaFin;
            fechaIni = SysmanFunciones.formatearFecha(fechaInicial);
            fechaFin = SysmanFunciones.formatearFecha(fechaFinal);
            reemplazar.put("fechaInicial", fechaIni);
            reemplazar.put("fechaFinal", fechaFin);
            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoFinal", tipoFinal);
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_FORMATO_ESPECIAL_EXCEL", formatoEspecialExcel);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE") + " "
                                + ex.getMessage() + " " + reporte);
            Logger.getLogger(AuxiliarPptalTercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException ex) {
            Logger.getLogger(AuxiliarPptalTercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        if (((fechaInicial != null) && (fechaFinal != null))
            && fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1002"));
            fechaFinal = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = asignarValorCampo(registroAux, "NIT");
        terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = asignarValorCampo(registroAux, "NIT");
    }

    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = asignarValorCampo(registroAux, cod);
        tipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        cargarListaTipoFinal();

    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = asignarValorCampo(registroAux, cod);
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

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

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
    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

    /**
     * @return the formatoEspecialExcel
     */
    public boolean isFormatoEspecialExcel() {
        return formatoEspecialExcel;
    }

    /**
     * @param formatoEspecialExcel
     * the formatoEspecialExcel to set
     */
    public void setFormatoEspecialExcel(boolean formatoEspecialExcel) {
        this.formatoEspecialExcel = formatoEspecialExcel;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
