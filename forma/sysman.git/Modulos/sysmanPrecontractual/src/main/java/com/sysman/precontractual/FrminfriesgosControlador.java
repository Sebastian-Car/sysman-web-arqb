package com.sysman.precontractual;

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
import com.sysman.persistencia.ConectorPool;
import com.sysman.precontractual.enums.FrminfriesgosControladorEnum;
import com.sysman.precontractual.enums.FrminfriesgosControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 02/12/2015
 *
 * @author lcortes
 * @version 2, 29/08/2017. Refactorizacion de codigo para usar dss.
 */
@ManagedBean
@ViewScoped
public class FrminfriesgosControlador extends BeanBaseModal {

    private final String compania;
    private final String cCodEstudio;
    private String estudioUno;
    private String estudioDos;
    private String tipoEstudio;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaEstudioInicial;
    private RegistroDataModelImpl listaEstudioFinal;

    /**
     * Creates a new instance of FrminfriesgosControlador
     */
    public FrminfriesgosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCodEstudio = "COD_ESTUDIO";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMINFRIESGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrminfriesgosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        abrirFormulario();
    }

    public void cargarListaEstudioInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfriesgosControladorUrlEnum.URL2532
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrminfriesgosControladorEnum.TIPO_DIA.getValue(),
                        tipoEstudio);

        listaEstudioInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodEstudio);
    }

    public void cargarListaEstudioFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfriesgosControladorUrlEnum.URL3369
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrminfriesgosControladorEnum.ESTUDIO_INI.getValue(),
                        estudioUno);
        param.put(FrminfriesgosControladorEnum.TIPO_DIA.getValue(),
                        tipoEstudio);

        listaEstudioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodEstudio);
    }

    public void oprimirbtnAceptar(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtnExcel(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {

            HashMap<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("estudioInicial", estudioUno);
            reemplazar.put("estudioFinal", estudioDos);
            reemplazar.put("tipoEstudio", tipoEstudio);

            parametros.put("PR_ESTUDIO_INICIAL", estudioUno);
            parametros.put("PR_ESTUDIO_FINAL", estudioDos);
            parametros.put("PR_TIPO_ESTUDIO", ("F").equals(tipoEstudio)
                ? "Funcionamiento"
                : "Proyecto");

            Reporteador.resuelveConsulta("000576INFRIESGOS",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000576INFRIESGOS",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SysmanException | JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarTipoEstudio() {
        cargarListaEstudioInicial();
        estudioUno = estudioDos = null;
        listaEstudioFinal = null;
    }

    public void seleccionarFilaEstudioInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        estudioUno = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodEstudio), "")
                        .toString();
        estudioDos = null;
        cargarListaEstudioFinal();
    }

    public void seleccionarFilaEstudioFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        estudioDos = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodEstudio), "")
                        .toString();
    }

    public String getEstudioUno() {
        return estudioUno;
    }

    public void setEstudioUno(String estudioUno) {
        this.estudioUno = estudioUno;
    }

    public String getEstudioDos() {
        return estudioDos;
    }

    public void setEstudioDos(String estudioDos) {
        this.estudioDos = estudioDos;
    }

    public String getTipoEstudio() {
        return tipoEstudio;
    }

    public void setTipoEstudio(String tipoEstudio) {
        this.tipoEstudio = tipoEstudio;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaEstudioInicial() {
        return listaEstudioInicial;
    }

    public void setListaEstudioInicial(
        RegistroDataModelImpl listaEstudioInicial) {
        this.listaEstudioInicial = listaEstudioInicial;
    }

    public RegistroDataModelImpl getListaEstudioFinal() {
        return listaEstudioFinal;
    }

    public void setListaEstudioFinal(RegistroDataModelImpl listaEstudioFinal) {
        this.listaEstudioFinal = listaEstudioFinal;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

}
