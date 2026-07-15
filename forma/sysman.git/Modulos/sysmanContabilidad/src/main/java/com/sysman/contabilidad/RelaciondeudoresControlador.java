package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RelaciondeudoresControladorEnum;
import com.sysman.contabilidad.enums.RelaciondeudoresControladorUrlEnum;
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
 * @author acaceres
 * @version 1, 17/05/2016
 * @version 2, 18/04/2017 Se refactoriza el codigo SQL de las listas
 * para utilizar dss.
 * @version 3, 20/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamdos a funciones, procedimiento y metodos de la
 * clase Acciones a llamados a EJB.
 */
@ManagedBean
@ViewScoped
public class RelaciondeudoresControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private boolean agrupadoPorTercero;
    private String terceroInicial;
    private String terceroFinal;
    private Date fechaInicial;
    private String nomTerceroInicial;
    private String nomTerceroFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of RelaciondeudoresControlador
     */
    public RelaciondeudoresControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RELACIONDEUDORES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RelaciondeudoresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        fechaInicial = new Date();
        cargarListaTerceroInicial();
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
        //
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelaciondeudoresControladorUrlEnum.URL3607
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NOMBRE.getName());
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelaciondeudoresControladorUrlEnum.URL4273
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(RelaciondeudoresControladorEnum.TERCEROINICIAL.getValue(),
                        nomTerceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NOMBRE.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generaInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generaInforme(FORMATOS formato) {
        archivoDescarga = null;

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("terceroInicial", nomTerceroInicial);
            reemplazar.put("terceroFinal", nomTerceroFinal);
            reemplazar.put("fechaCorte", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));

            Map<String, Object> parametros = new HashMap<>();

            String reporte = agrupadoPorTercero ? "001448RelacionDeudores1"
                : "001447RelacionDeudores";
            Reporteador.resuelveConsulta("001447RelacionDeudores",
                            Integer.valueOf(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);
            parametros.put("PR_TERCEROINICIAL", nomTerceroInicial);
            parametros.put("PR_TERCEROFINAL", nomTerceroFinal);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_CARGO_TESORERO",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO TESORERO",
                                            SessionUtil.getModulo(), new Date(),
                                            true));
            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(RelaciondeudoresControladorEnum.NIT
                                                        .getValue()),
                                        "")
                        .toString();
        nomTerceroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        nomTerceroFinal = null;
        terceroFinal = null;
        cargarListaTerceroFinal();

    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(RelaciondeudoresControladorEnum.NIT
                                                        .getValue()),
                                        "")
                        .toString();
        nomTerceroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public boolean getAgrupadoPorTercero() {
        return agrupadoPorTercero;
    }

    public void setAgrupadoPorTercero(boolean agrupadoPorTercero) {
        this.agrupadoPorTercero = agrupadoPorTercero;
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

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNomTerceroInicial() {
        return nomTerceroInicial;
    }

    public void setNomTerceroInicial(String nomTerceroInicial) {
        this.nomTerceroInicial = nomTerceroInicial;
    }

    public String getNomTerceroFinal() {
        return nomTerceroFinal;
    }

    public void setNomTerceroFinal(String nomTerceroFinal) {
        this.nomTerceroFinal = nomTerceroFinal;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
