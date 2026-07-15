package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.EgrycomafectadosControladorEnum;
import com.sysman.contabilidad.enums.EgrycomafectadosControladorUrlEnum;
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
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 04/05/2016
 * 
 * @version 2, 07/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos y correcciones sonar.
 */
@ManagedBean
@ViewScoped
public class EgrycomafectadosControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private int ano;
    private String comprobante;
    private String tipo;
    private String nombretipo;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaComprobante;
    private RegistroDataModelImpl listaTipo;

    /**
     * Creates a new instance of EgrycomafectadosControlador
     */
    public EgrycomafectadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.EGRYCOMAFECTADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipo();
        abrirFormulario();
        cargarListaAno();
    }

    @Override
    public void abrirFormulario() {
        ano = SysmanFunciones
                        .ano(new Date());
    }

    public void cargarListaAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EgrycomafectadosControladorUrlEnum.URL2772
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaComprobante() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EgrycomafectadosControladorUrlEnum.URL3246
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EgrycomafectadosControladorEnum.PARAM0.getValue(), tipo);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    public void cargarListaTipo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EgrycomafectadosControladorUrlEnum.URL4366
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
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
        generarInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        comprobante = null;
        cargarListaComprobante();
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipo", "'" + tipo + "'");
            reemplazar.put("comprobante", "'" + comprobante + "'");
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_ENCABEZADO", comprobante + " de " + ano);
            Reporteador.resuelveConsulta("000737EGRyCOMAfectados",
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed("000737EGRyCOMAfectados",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SysmanException | JRException | IOException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void seleccionarFilaComprobante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        comprobante = registroAux.getCampos().get("NUMERO").toString();
    }

    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipo = registroAux.getCampos().get("CODIGO").toString();
        nombretipo = registroAux.getCampos().get("NOMBRE").toString();
        comprobante = null;
        cargarListaComprobante();
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListaComprobante() {
        return listaComprobante;
    }

    public void setListaComprobante(RegistroDataModelImpl listaComprobante) {
        this.listaComprobante = listaComprobante;
    }

    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }

    public String getNombretipo() {
        return nombretipo;
    }

    public void setNombretipo(String nombretipo) {
        this.nombretipo = nombretipo;
    }

}
