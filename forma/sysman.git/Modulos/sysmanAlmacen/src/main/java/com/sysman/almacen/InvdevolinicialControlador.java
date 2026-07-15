package com.sysman.almacen;

import com.sysman.almacen.enums.InvdevolinicialControladorEnum;
import com.sysman.almacen.enums.InvdevolinicialControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 29/01/2016
 *
 * @author eamaya
 * @version 2, 02/05/2017 Proceso de Refactoring, Manejo de EJBs y
 * Correcciones SonarLint
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * suprimen las conexiones a la base de datos.
 */
@ManagedBean
@ViewScoped

public class InvdevolinicialControlador extends BeanBaseModal {

    private final String compania;

    private String cmbElementoDesde;
    private String cmbElementoHasta;
    private String responsableInicial;
    private String responsableFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String elementoIni;
    private String elementoFin;
    private String nomRespInicial;
    private String nomRespFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;
    private RegistroDataModelImpl listaResponsableInicial;
    private RegistroDataModelImpl listaResponsableFinal;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of InvdevolinicialControlador
     */
    public InvdevolinicialControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.INVDEVOLINICIAL_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InvdevolinicialControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {

        cargarListacmbElementoDesde();
        cargarListaResponsableInicial();
        fechaInicial = new Date();
        abrirFormulario();
    }

    public void cargarListacmbElementoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvdevolinicialControladorUrlEnum.URL3050
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOELEMINI");
    }

    public void cargarListacmbElementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvdevolinicialControladorUrlEnum.URL4105
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(InvdevolinicialControladorEnum.PARAM1.getValue(),
                        cmbElementoDesde);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOELEMFIN");
    }

    public void cargarListaResponsableInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvdevolinicialControladorUrlEnum.URL5279
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaResponsableInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, InvdevolinicialControladorEnum.PARAM3.getValue());
    }

    public void cargarListaResponsableFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvdevolinicialControladorUrlEnum.URL6206
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(InvdevolinicialControladorEnum.PARAM5.getValue(),
                        responsableInicial);

        listaResponsableFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, InvdevolinicialControladorEnum.PARAM3.getValue());
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarVacios()) {
            generarInforme(FORMATOS.PDF);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarVacios()) {
            generarInforme(FORMATOS.EXCEL97);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cmbElementoDesde = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("CODIGOELEMINI"), " ").toString();

        elementoIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMELEMENTOINI"), " ")
                        .toString();
        cmbElementoHasta = null;
        elementoFin = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cmbElementoHasta = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("CODIGOELEMFIN"), "").toString();

        elementoFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMELEMENTOFIN"), "")
                        .toString();
    }

    public void seleccionarFilaResponsableInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        responsableInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(InvdevolinicialControladorEnum.PARAM3.getValue()),
                        "").toString();

        nomRespInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("NOMBRE"), " ").toString();

        responsableFinal = null;
        nomRespFinal = null;
        cargarListaResponsableFinal();
    }

    public void seleccionarFilaResponsableFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        responsableFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(InvdevolinicialControladorEnum.PARAM3
                                                        .getValue()),
                                        " ")
                        .toString();

        nomRespFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), " ")
                        .toString();
    }

    public void cambiarDesde() {
        fechaFinal = null;
    }

    public void generarInforme(FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String strSql;
            String digitosAgrupa;
            String cargoAlmacen;
            String jefeAlmacen;
            digitosAgrupa = ejbParametro.consultarParametro(compania,
                            "DIGITOS AGRUPACION INVENTARIO",
                            SessionUtil.getModulo(), new Date(), false);

            digitosAgrupa = digitosAgrupa == null ? "NO" : digitosAgrupa;
            if ("NO".equals(digitosAgrupa)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1905"));
                return;
            }
            cargoAlmacen = ejbParametro.consultarParametro(compania,
                            "CARGO ORDENADOR ALMACEN", SessionUtil.getModulo(),
                            new Date(), false);

            cargoAlmacen = cargoAlmacen == null ? "" : cargoAlmacen;

            jefeAlmacen = ejbParametro.consultarParametro(compania,
                            "JEFE DE ALMACEN", SessionUtil.getModulo(),
                            new Date(), false);
            jefeAlmacen = jefeAlmacen == null ? "" : jefeAlmacen;
            reemplazar.put("digitosAgrup", "" + digitosAgrupa);
            reemplazar.put("elementoInicial", cmbElementoDesde);
            reemplazar.put("elementoFinal", cmbElementoHasta);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("responsableInicial", responsableInicial);
            reemplazar.put("responsableFinal", responsableFinal);
            strSql = Reporteador.resuelveConsulta("000509InventarioInicialID",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_JEFE_DE_ALMACEN", jefeAlmacen);
            parametros.put("PR_CARGO_ORDENADOR_ALMACEN", cargoAlmacen);
            parametros.put("PR_FORMS_INVDEVOLINICIAL_DESDE", fechaInicial);
            parametros.put("PR_FORMS_INVDEVOLINICIAL_HASTA", fechaFinal);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000509InventarioInicialID", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public boolean validarVacios() {
        if ("".equals(cmbElementoDesde) || "".equals(cmbElementoHasta)
            || "".equals(responsableInicial) || "".equals(responsableFinal)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1906"));
            return false;
        }
        return true;
    }

    public String getCmbElementoDesde() {
        return cmbElementoDesde;
    }

    public void setCmbElementoDesde(String cmbElementoDesde) {
        this.cmbElementoDesde = cmbElementoDesde;
    }

    public String getCmbElementoHasta() {
        return cmbElementoHasta;
    }

    public void setCmbElementoHasta(String cmbElementoHasta) {
        this.cmbElementoHasta = cmbElementoHasta;
    }

    public String getResponsableInicial() {
        return responsableInicial;
    }

    public void setResponsableInicial(String responsableInicial) {
        this.responsableInicial = responsableInicial;
    }

    public String getResponsableFinal() {
        return responsableFinal;
    }

    public void setResponsableFinal(String responsableFinal) {
        this.responsableFinal = responsableFinal;
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

    public String getElementoIni() {
        return elementoIni;
    }

    public void setElementoIni(String elementoIni) {
        this.elementoIni = elementoIni;
    }

    public String getElementoFin() {
        return elementoFin;
    }

    public void setElementoFin(String elementoFin) {
        this.elementoFin = elementoFin;
    }

    public String getNomRespInicial() {
        return nomRespInicial;
    }

    public void setNomRespInicial(String nomRespInicial) {
        this.nomRespInicial = nomRespInicial;
    }

    public String getNomRespFinal() {
        return nomRespFinal;
    }

    public void setNomRespFinal(String nomRespFinal) {
        this.nomRespFinal = nomRespFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListacmbElementoDesde() {
        return listacmbElementoDesde;
    }

    public void setListacmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listacmbElementoDesde = listacmbElementoDesde;
    }

    public RegistroDataModelImpl getListacmbElementoHasta() {
        return listacmbElementoHasta;
    }

    public void setListacmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listacmbElementoHasta = listacmbElementoHasta;
    }

    public RegistroDataModelImpl getListaResponsableInicial() {
        return listaResponsableInicial;
    }

    public void setListaResponsableInicial(
        RegistroDataModelImpl listaResponsableInicial) {
        this.listaResponsableInicial = listaResponsableInicial;
    }

    public RegistroDataModelImpl getListaResponsableFinal() {
        return listaResponsableFinal;
    }

    public void setListaResponsableFinal(
        RegistroDataModelImpl listaResponsableFinal) {
        this.listaResponsableFinal = listaResponsableFinal;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

}
