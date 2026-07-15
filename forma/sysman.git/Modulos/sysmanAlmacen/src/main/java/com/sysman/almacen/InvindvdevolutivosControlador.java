package com.sysman.almacen;

import com.sysman.almacen.enums.InvindvdevolutivosControladorEnum;
import com.sysman.almacen.enums.InvindvdevolutivosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 27/01/2016
 *
 * @author ybecerra
 * @version 2, 05/05/2017 y Refactoring
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * suprimen las conexiones a la base de datos.
 */
@ManagedBean
@ViewScoped

public class InvindvdevolutivosControlador extends BeanBaseModal {

    private final String compania;
    private String seleccionado;
    private String cmbElementoDesde;
    private String cmbElementoHasta;
    private String nombreElementoDesde;
    private String nombreElementoHasta;
    private String entrega;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;

    private StreamedContent archivoDescarga;
    @EJB
    private EjbSysmanUtilRemote ejbAlmacenCero;

    /**
     * Creates a new instance of InvindvdevolutivosControlador
     */
    public InvindvdevolutivosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.INVINDVDEVOLUTIVOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InvindvdevolutivosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListacmbElementoDesde();

        abrirFormulario();
    }

    public void cargarListacmbElementoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvindvdevolutivosControladorUrlEnum.URL2524
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());

    }

    public void cargarListacmbElementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvindvdevolutivosControladorUrlEnum.URL3392
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        cmbElementoDesde);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());

    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarCodigos()) {
            generarInforme(FORMATOS.PDF);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarCodigos()) {
            generarInforme(FORMATOS.EXCEL);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarUltimoMov() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(FORMATOS formato) {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        String reporte = "000488IInvIndivDevolu";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String strSql;
            Registro ciudad;

            ciudad = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InvindvdevolutivosControladorUrlEnum.URL149
                                                                            .getValue())
                                            .getUrl(), param));

            String nomCiudad = (String) ciudad.getCampos().get("NOMBRE");
            reemplazar.put("elementoInicial", cmbElementoDesde);
            reemplazar.put("elementoFinal", cmbElementoHasta);
            strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_FORMS_INVINDVDEVOLUTIVOS_ENTREGA", entrega);
            parametros.put("PR_CIUDADCOMPANIAFECHA",
                            nomCiudad + SysmanFunciones.convertirAFechaCadena(
                                            new Date(),
                                            "dd/MM/YYYY HH:mm:ss"));

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | SystemException
                        | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cmbElementoDesde = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                        .getName()) == null ? ""
                                            : registroAux.getCampos()
                                                            .get(GeneralParameterEnum.CODIGOELEMENTO
                                                                            .getName())
                                                            .toString();
        nombreElementoDesde = registroAux.getCampos()
                        .get(InvindvdevolutivosControladorEnum.PARAM0
                                        .getValue()) == null ? ""
                                            : registroAux.getCampos()
                                                            .get(InvindvdevolutivosControladorEnum.PARAM0
                                                                            .getValue())
                                                            .toString();
        cmbElementoHasta = null;
        nombreElementoHasta = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cmbElementoHasta = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                        .getName()) == null ? ""
                                            : registroAux.getCampos()
                                                            .get(GeneralParameterEnum.CODIGOELEMENTO
                                                                            .getName())
                                                            .toString();
        nombreElementoHasta = registroAux.getCampos()
                        .get(InvindvdevolutivosControladorEnum.PARAM0
                                        .getValue()) == null ? ""
                                            : registroAux.getCampos()
                                                            .get(InvindvdevolutivosControladorEnum.PARAM0
                                                                            .getValue())
                                                            .toString();
    }

    public boolean validarCodigos() {
        Double elementoIni = Double.parseDouble(cmbElementoDesde);
        Double elementoFin = Double.parseDouble(cmbElementoHasta);
        if (elementoFin < elementoIni) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1873"));
            return false;
        }
        else {
            return true;
        }
    }

    public String getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(String seleccionado) {
        this.seleccionado = seleccionado;
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

    public String getNombreElementoDesde() {
        return nombreElementoDesde;
    }

    public void setNombreElementoDesde(String nombreElementoDesde) {
        this.nombreElementoDesde = nombreElementoDesde;
    }

    public String getNombreElementoHasta() {
        return nombreElementoHasta;
    }

    public void setNombreElementoHasta(String nombreElementoHasta) {
        this.nombreElementoHasta = nombreElementoHasta;
    }

    public String getEntrega() {
        return entrega;
    }

    public void setEntrega(String entrega) {
        this.entrega = entrega;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            entrega = ejbAlmacenCero.consultarParametro(compania,
                            "NOMBRE RESPONSABLE DEVOLUTIVO",
                            SessionUtil.getModulo(), new Date(), true);
            entrega = entrega == null ? "" : entrega;
        }
        catch (SystemException ex) {
            Logger.getLogger(InvindvdevolutivosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        // </CODIGO_DESARROLLADO>
    }
}
