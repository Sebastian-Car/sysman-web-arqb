package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RelacionchequesControladorEnum;
import com.sysman.contabilidad.enums.RelacionchequesControladorUrlEnum;
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
 * @author lcortes
 * @version 1, 25/04/2016
 * @author yrojas
 * @version 2, 10/04/2017 Se cambiaron las consultas por la invocacion
 * de los DSS. Se cambio controlador segun especificaciones del
 * SonarLint.
 *
 * -- Modificado por lcortes 10,11,12/05/2017. Se agrega validacion de
 * la fecha para que correspondan al mismo anio. Se modifican los
 * parametros enviados a las consultas de los dss de las listas
 * inicial y final para que tambien envie el anio respectivo.
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * suprimen las conexiones a la base de datos.
 */
@ManagedBean
@ViewScoped
public class RelacionchequesControlador extends BeanBaseModal {
    private final String compania;
    private String bancoInicial;
    private String bancoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreBanInicial;
    private String nombreBanFinal;
    private int anio;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaBancoInicial;
    private RegistroDataModelImpl listaBancoFinal;

    /**
     * Creates a new instance of RelacionchequesControlador
     */
    public RelacionchequesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RELACIONCHEQUES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RelacionchequesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        abrirFormulario();
        cargarListaBancoInicial();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = new Date();
        fechaFinal = new Date();
        anio = SysmanFunciones.ano(fechaInicial);
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaBancoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionchequesControladorUrlEnum.URL2831
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CLASE.getName(), "B");

        listaBancoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaBancoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionchequesControladorUrlEnum.URL3620
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CLASE.getName(), "B");
        param.put(RelacionchequesControladorEnum.PARAM1.getValue(),
                        bancoInicial);

        listaBancoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!SysmanFunciones.comparaFechas(fechaInicial, fechaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB560"));
            fechaInicial = null;
            return;
        }
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!SysmanFunciones.comparaFechas(fechaInicial, fechaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB560"));
            fechaInicial = null;
            return;
        }
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaBancoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
        nombreBanInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        " ")
                        .toString();
        bancoFinal = nombreBanFinal = null;
        cargarListaBancoFinal();

    }

    public void seleccionarFilaBancoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
        nombreBanFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        " ")
                        .toString();
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        if (!validarAniosFechas()) {
            return;
        }
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String fechaIni = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial, "dd/MM/yyyy");
            String fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal,
                            "dd/MM/yyyy");
            // Reemplazos consulta del reporte
            reemplazar.put("fechaInicial", fechaIni);
            reemplazar.put("fechaFinal", fechaFin);
            reemplazar.put("bancoInicial", bancoInicial);
            reemplazar.put("bancoFinal", bancoFinal);
            // Par�metros del reporte
            parametros.put("PR_FORMS_RELACIONCHEQUES_FECHAINICIAL", fechaIni);
            parametros.put("PR_FORMS_RELACIONCHEQUES_FECHAFINAL", fechaFin);
            parametros.put("PR_FORMS_RELACIONCHEQUES_BANCOINICIAL",
                            bancoInicial);
            parametros.put("PR_FORMS_RELACIONCHEQUES_BANCOFINAL", bancoFinal);

            Reporteador.resuelveConsulta("000669RelacionCheques",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000669RelacionCheques",
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al cambiar el control FechaInicial
     *
     */
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        if (!validarAniosFechas()) {
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFinal
     *
     */
    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        if (!validarAniosFechas()) {
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarAniosFechas() {
        if ((fechaFinal != null)
            && (SysmanFunciones.ano(fechaInicial) != SysmanFunciones
                            .ano(fechaFinal))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3181"));
            fechaFinal = null;
            bancoInicial = bancoFinal = nombreBanInicial = nombreBanFinal = null;
            listaBancoInicial = listaBancoFinal = null;
            return false;
        }
        anio = SysmanFunciones.ano(fechaInicial);
        cargarListaBancoInicial();
        return true;

    }

    public String getBancoInicial() {
        return bancoInicial;
    }

    public void setBancoInicial(String bancoInicial) {
        this.bancoInicial = bancoInicial;
    }

    public String getBancoFinal() {
        return bancoFinal;
    }

    public void setBancoFinal(String bancoFinal) {
        this.bancoFinal = bancoFinal;
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

    public String getNombreBanInicial() {
        return nombreBanInicial;
    }

    public void setNombreBanInicial(String nombreBanInicial) {
        this.nombreBanInicial = nombreBanInicial;
    }

    public String getNombreBanFinal() {
        return nombreBanFinal;
    }

    public void setNombreBanFinal(String nombreBanFinal) {
        this.nombreBanFinal = nombreBanFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaBancoInicial() {
        return listaBancoInicial;
    }

    public void setListaBancoInicial(RegistroDataModelImpl listaBancoInicial) {
        this.listaBancoInicial = listaBancoInicial;
    }

    public RegistroDataModelImpl getListaBancoFinal() {
        return listaBancoFinal;
    }

    public void setListaBancoFinal(RegistroDataModelImpl listaBancoFinal) {
        this.listaBancoFinal = listaBancoFinal;
    }
}
