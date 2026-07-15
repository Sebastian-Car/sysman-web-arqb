package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.ejb.EjbContratosCeroRemote;
import com.sysman.contratos.enums.AfectarPlanCompraLotesControladorEnum;
import com.sysman.contratos.enums.AfectarPlanCompraLotesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

/**
 *
 * @author sdaza
 * @version 1, 29/10/2015
 * 
 * @author eamaya
 * @version 2.0, 02/08/2017, Proceso de Refactoring DSS , Manejo de
 * EJBs y cambio de enum del numero del formulario
 * 
 */
@ManagedBean
@ViewScoped

public class AfectarPlanCompraLotesControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String usuario;
    private String tipoContrato;
    private String numeroIni;
    private String numeroFin;
    private String ano;
    private StreamedContent archivoDescarga;
    private List<Registro> listaTipoContratoInicial;
    private List<Registro> listaNumeroInicial;
    private List<Registro> listaNumeroFinal;
    /**
     */
    private List<Registro> listaAno;

    @EJB
    private EjbContratosCeroRemote ejbContratoCero;

    /**
     * Creates a new instance of AfectarPlanCompraLotesControlador
     */
    public AfectarPlanCompraLotesControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.AFECTAR_PLAN_COMPRA_LOTES_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AfectarPlanCompraLotesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAno();
        cargarListaTipoContratoInicial();
        abrirFormulario();
    }

    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AfectarPlanCompraLotesControladorUrlEnum.URL5073
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTipoContratoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaTipoContratoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AfectarPlanCompraLotesControladorUrlEnum.URL3169
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNumeroInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        try {
            listaNumeroInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AfectarPlanCompraLotesControladorUrlEnum.URL3196
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNumeroFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        param.put(AfectarPlanCompraLotesControladorEnum.NUMEROINICIAL
                        .getValue(),
                        numeroIni);

        try {
            listaNumeroFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AfectarPlanCompraLotesControladorUrlEnum.URL4068
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirImprimir() {
        try {
            if ((ano == null) || (tipoContrato == null) || (numeroIni == null)
                || (numeroFin == null)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2040"));
                return;
            }

            ejbContratoCero.actualizarPlanDeCompras(compania,
                            tipoContrato,
                            Long.parseLong(numeroIni),
                            Long.parseLong(numeroFin), Integer.parseInt(modulo),
                            Integer.parseInt(ano), usuario);

            ejbContratoCero.actualizarImpresoPlanDeComprasOrdenDeCompra(
                            compania, tipoContrato, Long.parseLong(numeroIni),
                            Long.parseLong(numeroFin),
                            usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (NumberFormatException
                        | SystemException ex) {
            Logger.getLogger(AfectarPlanCompraLotesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
        }
    }

    public void cambiarTipoContratoInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaNumeroInicial();
        cargarListaNumeroFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarNumeroInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaNumeroFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cargarListaNumeroInicial();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getNumeroIni() {
        return numeroIni;
    }

    public void setNumeroIni(String numeroIni) {
        this.numeroIni = numeroIni;
    }

    public String getNumeroFin() {
        return numeroFin;
    }

    public void setNumeroFin(String numeroFin) {
        this.numeroFin = numeroFin;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaTipoContratoInicial() {
        return listaTipoContratoInicial;
    }

    public void setListaTipoContratoInicial(
        List<Registro> listaTipoContratoInicial) {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    public List<Registro> getListaNumeroInicial() {
        return listaNumeroInicial;
    }

    public void setListaNumeroInicial(List<Registro> listaNumeroInicial) {
        this.listaNumeroInicial = listaNumeroInicial;
    }

    public List<Registro> getListaNumeroFinal() {
        return listaNumeroFinal;
    }

    public void setListaNumeroFinal(List<Registro> listaNumeroFinal) {
        this.listaNumeroFinal = listaNumeroFinal;
    }

    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
}
