package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.AccionesCContratoControladorEnum;
import com.sysman.contratos.enums.AccionesCContratoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 21/09/2015
 * 
 * @version 2, 01/08/2017, <strong>pespitia</strong>:<br>
 * Reemplazar los llamados a las conexiones por
 * <code>ConectorPool.ESQUEMA_SYSMAN</code><br>
 * Refactoring.<br>
 * Reemplazo de <code>com.sysman.persistencia.Acciones</code> por su
 * correpondiente equivalente.<br>
 * Reemplazo del numero del formulario por su correpondiente
 * enumerado.
 */
@ManagedBean
@ViewScoped
public class AccionesCContratoControlador extends BeanBaseModal {

    private final String cCodigo;

    private String compania;
    private String modulo;
    private String tipoInicial;
    private String tipoFinal;
    private String nombreInicial;
    private String nombreFinal;
    private Date fechaInicial;
    private Date fechaFinal;

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los items del combo
     * <code>Tipo de Contrato Inicial</code>
     */
    private RegistroDataModelImpl listaTipoContratoInicial;

    /**
     * Lista que contiene los items del combo
     * <code>Tipo de Contrato Final</code>
     */
    private RegistroDataModelImpl listaTipoContratoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of AccionesccontratoControlador
     */
    public AccionesCContratoControlador() {
        super();

        cCodigo = GeneralParameterEnum.CODIGO.getName();

        try {
            numFormulario = GeneralCodigoFormaEnum.ACCIONES_CCONTRATO_CONTROLADOR
                            .getCodigo();

            compania = SessionUtil.getCompania();
            modulo = SessionUtil.getModulo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AccionesCContratoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        fechaInicial = fechaFinal = new Date();

        cargarListaTipoContratoInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista <code>listaTipoContratoInicial</code> asociada
     * al combo tipo de contrato inicial.
     */
    public void cargarListaTipoContratoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AccionesCContratoControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Carga la lista <code>listaTipoContratoFinal</code> asociada al
     * combo tipo de contrato final.
     */
    public void cargarListaTipoContratoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AccionesCContratoControladorUrlEnum.URL0002
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AccionesCContratoControladorEnum.CODIGOINI.getValue(),
                        tipoInicial);

        listaTipoContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }
    // </METODOS_CARGAR_LISTA>

    public void oprimircmdExcel() {
        archivoDescarga = null;

        try {
            Map<String, Object> remplazar = new HashMap<>();
            remplazar.put("tipoContratoInicial", tipoInicial);
            remplazar.put("tipoContratoFinal", tipoFinal);

            remplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            remplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            String strSql = Reporteador.resuelveConsulta(
                            "800038AccionesControlContratos",
                            Integer.parseInt(modulo), remplazar);

            if (service.getConteoConsulta(strSql) == 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3346"));
                return;
            }

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaTipoContratoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        tipoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        nombreInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

        tipoFinal = nombreFinal = null;

        cargarListaTipoContratoFinal();
    }

    public void seleccionarFilaTipoContratoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        tipoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        nombreFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    public void oprimircmdPresentar(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoContratoInicial
     * 
     * @return listaTipoContratoInicial
     */
    public RegistroDataModelImpl getListaTipoContratoInicial() {
        return listaTipoContratoInicial;
    }

    /**
     * Asigna la lista listaTipoContratoInicial
     * 
     * @param listaTipoContratoInicial
     * Variable a asignar en listaTipoContratoInicial
     */
    public void setListaTipoContratoInicial(
        RegistroDataModelImpl listaTipoContratoInicial) {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    /**
     * Retorna la lista listaTipoContratoFinal
     * 
     * @return listaTipoContratoFinal
     */
    public RegistroDataModelImpl getListaTipoContratoFinal() {
        return listaTipoContratoFinal;
    }

    /**
     * Asigna la lista listaTipoContratoFinal
     * 
     * @param listaTipoContratoFinal
     * Variable a asignar en listaTipoContratoFinal
     */
    public void setListaTipoContratoFinal(
        RegistroDataModelImpl listaTipoContratoFinal) {
        this.listaTipoContratoFinal = listaTipoContratoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
