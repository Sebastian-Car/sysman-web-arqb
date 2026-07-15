package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.RelcontratosfechasControladorEnum;
import com.sysman.contratos.enums.RelcontratosfechasControladorUrlEnum;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
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
 * @author dcastro
 * @version 1, 07/10/2015
 * 
 * @author asana
 * @version 2, 10/08/2017 Se realiza refactoring.
 */
@ManagedBean
@ViewScoped
public class RelcontratosfechasControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String terceroInicial;
    private String terceroFinal;
    private String nombreInicial;
    private String nombreFinal;
    private String tipoContratoInicial;
    private String tipoContratoFinal;
    private String dependenciaInicial;
    private String dependeciaFinal;
    private String depInicial;
    private String depFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private List<Registro> listaTipoContratoInicial;
    private List<Registro> listaTipoContratoFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listaDepinicial;
    private RegistroDataModelImpl listaDepfinal;
    private StreamedContent archivoDescarga;

    private static final String CODIGO = "CODIGO";
    private static final String NOMBRE = "NOMBRE";

    /**
     * Creates a new instance of RelcontratosfechasControlador
     */
    public RelcontratosfechasControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.RELCONTRATOSFECHAS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RelcontratosfechasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoContratoInicial();
        cargarListaTipoContratoFinal();
        cargarListaTerceroInicial();

        cargarListadepinicial();

        fechaInicial = new Date();
        fechaFinal = new Date();
        abrirFormulario();
    }

    public void cargarListaTipoContratoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoContratoInicial = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            RelcontratosfechasControladorUrlEnum.URL5158
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTipoContratoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(RelcontratosfechasControladorEnum.PARAM0.getValue(),
                        tipoContratoInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaTipoContratoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RelcontratosfechasControladorUrlEnum.URL7562
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTerceroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelcontratosfechasControladorUrlEnum.URL5811
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTerceroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelcontratosfechasControladorUrlEnum.URL4182
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(RelcontratosfechasControladorEnum.PARAM1.getValue(),
                        nombreInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    public void cargarListadepinicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelcontratosfechasControladorUrlEnum.URL6843
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDepinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListadepfinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelcontratosfechasControladorUrlEnum.URL6842
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(RelcontratosfechasControladorEnum.PARAM2.getValue(),
                        dependenciaInicial);

        listaDepfinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);

    }

    public void oprimircmdPantalla() {
        archivoDescarga = null;
        if (validarNoNulos()) {
            if (!validarFechas(fechaInicial, fechaFinal)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB49"));
                return;
            }
            generarInforme(ReportesBean.FORMATOS.PDF);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    private boolean compararNulos(Object valor1, Object valor2) {
        return valor1 != null && valor2 != null;
    }

    private boolean validarNoNulos() {
        return compararNulos(tipoContratoFinal, tipoContratoInicial) &&
            compararNulos(terceroFinal, terceroInicial) &&
            compararNulos(dependeciaFinal, dependenciaInicial) &&
            compararNulos(fechaFinal, fechaInicial);
    }

    public void oprimircmdExcel() {
        archivoDescarga = null;
        if (validarNoNulos()) {
            if (!validarFechas(fechaInicial, fechaFinal)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB49"));
                return;
            }
            generarInforme(ReportesBean.FORMATOS.EXCEL97);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void cambiarTipoContratoInicial() {
        cargarListaTipoContratoFinal();
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NIT").toString();
        nombreInicial = registroAux.getCampos().get(NOMBRE).toString();
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NIT").toString();
        nombreFinal = registroAux.getCampos().get(NOMBRE).toString();
    }

    public void seleccionarFilaDepinicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaInicial = registroAux.getCampos().get(CODIGO).toString();
        depInicial = registroAux.getCampos().get(NOMBRE).toString()+"";
        cargarListadepfinal();
    }

    public void seleccionarFilaDepfinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependeciaFinal = registroAux.getCampos().get(CODIGO).toString();
        depFinal = registroAux.getCampos().get(NOMBRE).toString();
    }

    private void generarInforme(ReportesBean.FORMATOS formatos) {
        try {
            String parReporte = "000261RelContratosFechas";

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("nombreInicial", SysmanFunciones.concatenar("'",
                            nombreInicial, "'"));
            reemplazar.put("nombreFinal",
                            SysmanFunciones.concatenar("'", nombreFinal, "'"));
            reemplazar.put("contratoInicial", SysmanFunciones.concatenar("'",
                            tipoContratoInicial, "'"));
            reemplazar.put("contratoFinal", SysmanFunciones.concatenar("'",
                            tipoContratoFinal, "'"));
            reemplazar.put("dependenciaInicial", SysmanFunciones.concatenar("'",
                            dependenciaInicial, "'"));
            reemplazar.put("dependenciaFinal", SysmanFunciones.concatenar("'",
                            dependeciaFinal, "'"));

            String strSql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), reemplazar);
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_STRSQL", strSql);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_INFORME_NO_EXISTE),
                            " ", ex.getMessage()));
            Logger.getLogger(RelcontratosfechasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            Logger.getLogger(RelcontratosfechasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
                            ex.getMessage()));
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

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

    public String getNombreInicial() {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal() {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    public String getTipoContratoInicial() {
        return tipoContratoInicial;
    }

    public void setTipoContratoInicial(String tipoContratoInicial) {
        this.tipoContratoInicial = tipoContratoInicial;
    }

    public String getTipoContratoFinal() {
        return tipoContratoFinal;
    }

    public void setTipoContratoFinal(String tipoContratoFinal) {
        this.tipoContratoFinal = tipoContratoFinal;
    }

    public String getDependenciaInicial() {
        return dependenciaInicial;
    }

    public void setDependenciaInicial(String dependenciaInicial) {
        this.dependenciaInicial = dependenciaInicial;
    }

    public String getDependeciaFinal() {
        return dependeciaFinal;
    }

    public void setDependeciaFinal(String dependeciaFinal) {
        this.dependeciaFinal = dependeciaFinal;
    }

    public String getDepInicial() {
        return depInicial;
    }

    public void setDepInicial(String depInicial) {
        this.depInicial = depInicial;
    }

    public String getDepFinal() {
        return depFinal;
    }

    public void setDepFinal(String depFinal) {
        this.depFinal = depFinal;
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

    public List<Registro> getListaTipoContratoInicial() {
        return listaTipoContratoInicial;
    }

    public void setListaTipoContratoInicial(
        List<Registro> listaTipoContratoInicial) {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    public List<Registro> getListaTipoContratoFinal() {
        return listaTipoContratoFinal;
    }

    public void setListaTipoContratoFinal(
        List<Registro> listaTipoContratoFinal) {
        this.listaTipoContratoFinal = listaTipoContratoFinal;
    }

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

    public RegistroDataModelImpl getlistaDepinicial() {
        return listaDepinicial;
    }

    public void setlistaDepinicial(RegistroDataModelImpl listaDepinicial) {
        this.listaDepinicial = listaDepinicial;
    }

    public RegistroDataModelImpl getlistaDepfinal() {
        return listaDepfinal;
    }

    public void setlistaDepfinal(RegistroDataModelImpl listaDepfinal) {
        this.listaDepfinal = listaDepfinal;
    }

    public boolean validarFechas(Date fechaInicial, Date fechaFinal) {
        return fechaInicial.compareTo(fechaFinal) <= 0;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
}
