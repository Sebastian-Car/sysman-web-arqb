package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.enums.IcontratacionrubroControladorEnum;
import com.sysman.planeacion.enums.IcontratacionrubroControladorUrlEnum;
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
 * @author ybecerra
 * @version 1, 20/01/2016
 * 
 * @author eamaya
 * @version 2.0, 07/09/2017, Proceso de Refactoring DSS, cambio de
 * SYSDATE por new Date, cambio de numero de formulario por enum y
 * correciones SonarLint
 * 
 */
@ManagedBean
@ViewScoped

public class IcontratacionrubroControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String opcion;
    private boolean especial;
    private String formalidades;
    private String rubroInicial;
    private String rubroFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreInicial;
    private String nombreFinal;
    private boolean especialVisible;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaRubroInicial;
    private RegistroDataModelImpl listaRubroFinal;

    /**
     * Creates a new instance of IcontratacionrubroControlador
     */
    public IcontratacionrubroControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.ICONTRATACIONRUBRO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(IcontratacionrubroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        fechaInicial = Acciones.getSysDate(ConectorPool.ESQUEMA_SYSMAN);
        fechaFinal = Acciones.getSysDate(ConectorPool.ESQUEMA_SYSMAN);
        formalidades = "1";
        cargarListaRubroInicial();
        opcion = "1";
        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {

        especialVisible = true;
    }

    public void cargarListaRubroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IcontratacionrubroControladorUrlEnum.URL3294
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaRubroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        IcontratacionrubroControladorEnum.RUBROPPTO.getValue());
    }

    public void cargarListaRubroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IcontratacionrubroControladorUrlEnum.URL4772
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(IcontratacionrubroControladorEnum.RUBROINICIAL.getValue(),
                        rubroInicial);

        listaRubroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        IcontratacionrubroControladorEnum.RUBROPPTO.getValue());

    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPorFechaDe() {
        especialVisible = "1".equals(opcion);
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;

        String contrato;
        String parReporte;
        String strSql;

        switch (formalidades) {
        case "2":
            contrato = " AND TIPOORDENDECOMPRA.GENERACONTRATO = -1 ";
            break;
        case "3":
            contrato = " AND TIPOORDENDECOMPRA.GENERACONTRATO = 0 ";
            break;
        default:
            contrato = " ";
            break;
        }

        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("rubroInicial", "'" + rubroInicial + "'");
            reemplazar.put("rubroFinal", "'" + rubroFinal + "'");
            reemplazar.put("generaContrato", contrato);

            HashMap<String, Object> parametros = new HashMap<>();
            if ("1".equals(opcion)) {
                strSql = "000467IContratacionRubro";
                if (especial) {
                    parReporte = "000468IContratacionRubroEspecial";

                }
                else {
                    parReporte = "000467IContratacionRubro";

                }
            }
            else {
                strSql = "000473IContratacionRubroAF";
                parReporte = "000473IContratacionRubroAF";

            }

            parametros.put("PR_FECHAS", SysmanFunciones.concatenar(
                            SysmanFunciones.convertirAFechaCadena(fechaInicial),
                            " a ", SysmanFunciones.convertirAFechaCadena(
                                            fechaFinal)));
            parametros.put("PR_NOMBRECOMPANIA", SysmanFunciones.concatenar(
                            SessionUtil.getCompaniaIngreso().getNombre(), " - ",
                            SessionUtil.getCompaniaIngreso().getSigla()));

            Reporteador.resuelveConsulta(strSql, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (ParseException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(IcontratacionrubroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaRubroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        rubroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(IcontratacionrubroControladorEnum.RUBROPPTO
                                                        .getValue()),
                                        "")
                        .toString();
        nombreInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        rubroFinal = null;
        nombreFinal = null;
        cargarListaRubroFinal();
    }

    public void seleccionarFilaRubroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        rubroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(IcontratacionrubroControladorEnum.RUBROPPTO
                                                        .getValue()),
                                        "")
                        .toString();
        nombreFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean getEspecial() {
        return especial;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    public String getFormalidades() {
        return formalidades;
    }

    public void setFormalidades(String formalidades) {
        this.formalidades = formalidades;
    }

    public String getRubroInicial() {
        return rubroInicial;
    }

    public void setRubroInicial(String rubroInicial) {
        this.rubroInicial = rubroInicial;
    }

    public boolean isEspecialVisible() {
        return especialVisible;
    }

    public void setEspecialVisible(boolean especialVisible) {
        this.especialVisible = especialVisible;
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
