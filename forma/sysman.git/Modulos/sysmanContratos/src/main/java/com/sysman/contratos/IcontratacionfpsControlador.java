package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.IcontratacionfpsControladorEnum;
import com.sysman.contratos.enums.IcontratacionfpsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 10/12/2015
 * 
 * @author asana
 * @version 2, 09/08/2017 Se realiza refactoring
 */
@ManagedBean
@ViewScoped
public class IcontratacionfpsControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String rubroCons;

    private boolean consolidado;
    private String rubroInicial;
    private String rubroFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String parReporte;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaRubroInicial;
    private RegistroDataModelImpl listaRubroFinal;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Creates a new instance of IcontratacionfpsControlador
     */
    public IcontratacionfpsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        rubroCons = "RUBRO";
        try {
            numFormulario = GeneralCodigoFormaEnum.ICONTRATACIONFPS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(IcontratacionfpsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        fechaInicial = new Date();
        fechaFinal = new Date();
        cargarListaRubroInicial();

        abrirFormulario();
    }

    public void cargarListaRubroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IcontratacionfpsControladorUrlEnum.URL3060
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaRubroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, rubroCons);
    }

    public void cargarListaRubroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IcontratacionfpsControladorUrlEnum.URL3485
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(IcontratacionfpsControladorEnum.PARAM1.getValue(),
                        rubroInicial);

        listaRubroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, rubroCons);
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaRubroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        rubroInicial = registroAux.getCampos().get(rubroCons).toString();
        cargarListaRubroFinal();

    }

    public void seleccionarFilaRubroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        rubroFinal = registroAux.getCampos().get(rubroCons).toString();
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {

        try {
            Integer detalle;

            if (consolidado) {
                detalle = 0;
                parReporte = "000431IContratacionRubroFP";
            }
            else {
                detalle = 1;
                parReporte = "000431IContratacionRubroFP";
            }

            HashMap<String, Object> remplazar = new HashMap<>();

            remplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            remplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            remplazar.put("rubroInicial",
                            SysmanFunciones.concatenar("'", rubroInicial, "'"));
            remplazar.put("rubroFinal",
                            SysmanFunciones.concatenar("'", rubroFinal, "'"));

            String strsql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), remplazar);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_STRSQL", strsql);
            parametros.put("PR_NOMBRECOMPANIA",
                            SysmanFunciones.concatenar(SessionUtil
                                            .getCompaniaIngreso().getNombre(),
                                            " - ",
                                            SessionUtil.getCompaniaIngreso()
                                                            .getSigla()));
            parametros.put("PR_DESDE", SysmanFunciones.convertirAFechaCadena(
                            fechaInicial, " dd 'de' MMMM  'de' yyyy"));
            parametros.put("PR_HASTA", SysmanFunciones.convertirAFechaCadena(
                            fechaFinal, " dd 'de' MMMM  'de' yyyy"));
            parametros.put("PR_DETALLE", detalle);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException ex) {

            JsfUtil.agregarMensajeInformativo(
                            SysmanFunciones.concatenar(
                                            idioma.getString(
                                                            Constantes.MSM_INFORME_NO_EXISTE),
                                            " ", ex.getMessage()));
            Logger.getLogger(IcontratacionfpsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException
                        | ParseException ex) {

            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(IcontratacionfpsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            String parametro;

            parametro = ejbSysmanUtilRemote.consultarParametro(compania,
                            "MANEJA CONSOLIDADO EN INF PLENAS POR RUBRO",
                            SessionUtil.getModulo(), new Date(), true);
            consolidado = (parametro != null) && "SI".equals(parametro);
        }

        catch (SystemException ex) {
            Logger.getLogger(IcontratacionfpsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2118"));
        }

        // </CODIGO_DESARROLLADO>
    }

    public boolean isConsolidado() {
        return consolidado;
    }

    public void setConsolidado(boolean consolidado) {
        this.consolidado = consolidado;
    }

    public String getRubroInicial() {
        return rubroInicial;
    }

    public void setRubroInicial(String rubroInicial) {
        this.rubroInicial = rubroInicial;
    }

    public String getRubroFinal() {
        return rubroFinal;
    }

    public void setRubroFinal(String rubroFinal) {
        this.rubroFinal = rubroFinal;
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

    public RegistroDataModelImpl getListaRubroInicial() {
        return listaRubroInicial;
    }

    public void setListaRubroInicial(RegistroDataModelImpl listaRubroInicial) {
        this.listaRubroInicial = listaRubroInicial;
    }

    public RegistroDataModelImpl getListaRubroFinal() {
        return listaRubroFinal;
    }

    public void setListaRubroFinal(RegistroDataModelImpl listaRubroFinal) {
        this.listaRubroFinal = listaRubroFinal;
    }

}
