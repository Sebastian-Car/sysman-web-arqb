package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCeroRemote;
import com.sysman.bancoproyectos.enums.FrmanulanovedadControladorEnum;
import com.sysman.bancoproyectos.enums.FrmanulanovedadControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * @version 1, 30/09/2015
 * @modified jguerrero
 * @version 2. 14/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped

public class FrmanulanovedadControlador extends BeanBaseModal {

    private final String compania;

    private final String cCodigoNov;
    private final String cTipoT;
    private final String cGrTipoT;

    private String tipot;
    private String dependencia;
    private String nombreTipot;
    private String nombregrTipot;
    private String codigoNov;
    private String claset;
    private Registro registro;
    private Date fecha;
    private String grTipot;
    private String codDependencia;
    private RegistroDataModelImpl listaTipoNovedad;
    private RegistroDataModelImpl listaNovedad;
    private RegistroDataModelImpl listaGrTipoNovedad;
    private List<Registro> listaDependencia;
    private StreamedContent archivoDescarga;

    @EJB
    EjbBancoProyectoCeroRemote ejbBancoProyCero;

    /**
     * Creates a new instance of FrmanulanovedadControlador
     */
    public FrmanulanovedadControlador() {
        super();
        compania = SessionUtil.getCompania();

        cCodigoNov = FrmanulanovedadControladorEnum.CODIGONOV.getValue();
        cTipoT = FrmanulanovedadControladorEnum.TIPOT.getValue();
        cGrTipoT = FrmanulanovedadControladorEnum.GRTIPOT.getValue();
        try {
            registro = new Registro(new HashMap<String, Object>());
            numFormulario = GeneralCodigoFormaEnum.FRMANULANOVEDAD_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmanulanovedadControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        cargarListaTIPONOVEDAD();
        cargarListaGrTipoNovedad();
        abrirFormulario();
    }

    public void cargarListaTIPONOVEDAD() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmanulanovedadControladorUrlEnum.URL3116
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoNovedad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cTipoT);

        // 218005
    }

    public void cargarListaNOVEDAD() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmanulanovedadControladorUrlEnum.URL3454
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmanulanovedadControladorEnum.TIPOT.getValue(), tipot);
        param.put(FrmanulanovedadControladorEnum.CLASET.getValue(), claset);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNovedad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoNov);

        // 130009 TIPOT CLASET
    }

    public void cargarListaGrTipoNovedad() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmanulanovedadControladorUrlEnum.URL10257
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaGrTipoNovedad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cGrTipoT);

    }

    public void oprimirAnular() {
        // <CODIGO_DESARROLLADO>

        try {

            String anulada = ejbBancoProyCero.anularSolicitudBancoProyecto(
                            compania,
                            tipot, claset, Long.parseLong(codigoNov),
                            codDependencia,
                            Integer.parseInt(SessionUtil.getModulo()),
                            SessionUtil.getUser().getCodigo());

            if (FrmanulanovedadControladorEnum.ANULADA.getValue()
                            .equals(anulada)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB837"));
                codigoNov = null;
                dependencia = null;
                codDependencia = null;
                nombreTipot = null;
                tipot = null;

                claset = null;
                cargarListaTIPONOVEDAD();
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2323"));
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmanulanovedadControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirGrAnular() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try {

            String cadenaArchivo = ejbBancoProyCero
                            .anularSolicitudesBancoProyectoRes(
                                            fecha, compania, grTipot,
                                            Integer.parseInt(SessionUtil
                                                            .getModulo()),
                                            SessionUtil.getUser().getCodigo());

            if ("NOANULADA".equalsIgnoreCase(cadenaArchivo.trim())) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2318")
                                .replace("#$nombregrTipot#$",
                                                nombregrTipot)
                                .replace("#$fecha#$",
                                                SysmanFunciones.convertirAFechaCadena(
                                                                fecha,
                                                                "dd-MM-YYYY")));
                nombregrTipot = "";
                grTipot = (String) registro.getCampos().put(cGrTipoT, "");
                fecha = null;
                cargarListaGrTipoNovedad();
                return;
            }
            else {
                String nombreArchivo = "RESUMEN_DE_ANULACION_" + grTipot
                    + SysmanFunciones.convertirAFechaCadena(fecha, "MMddYYYY")
                    + ".TXT";
                ByteArrayInputStream archivo = JsfUtil
                                .serializarPlano(cadenaArchivo);
                archivoDescarga = JsfUtil.getArchivoDescarga(archivo,
                                nombreArchivo);
                nombregrTipot = "";
                registro.getCampos().put(cGrTipoT, "");
                fecha = null;
            }
            cargarListaGrTipoNovedad();
        }
        catch (JRException | SystemException | ParseException
                        | IOException ex) {
            Logger.getLogger(FrmanulanovedadControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoNovedad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoNov = (String) registroAux.getCampos().put(cCodigoNov, "");
        dependencia = "";
        tipot = retornarString(registroAux, cTipoT);
        claset = retornarString(registroAux,
                        FrmanulanovedadControladorEnum.CLASET.getValue());

        nombreTipot = retornarString(registroAux,
                        FrmanulanovedadControladorEnum.NOMBRETIPO.getValue());
        cargarListaNOVEDAD();
    }

    public void seleccionarFilaNovedad(SelectEvent event) {
        try {
            Registro registroAux = (Registro) event.getObject();
            codigoNov = registroAux.getCampos().get(cCodigoNov).toString();

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registroAux.getCampos()
                                            .get(FrmanulanovedadControladorEnum.DEPNOV
                                                            .getValue()));

            listaDependencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmanulanovedadControladorUrlEnum.URL4532
                                                                            .getValue())
                                            .getUrl(), param));

            dependencia = retornarString(listaDependencia.get(0),
                            FrmanulanovedadControladorEnum.NOMBREDEP
                                            .getValue());
            codDependencia = retornarString(listaDependencia.get(0),
                            FrmanulanovedadControladorEnum.CODIGODEP
                                            .getValue());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaGrTipoNovedad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        grTipot = retornarString(registroAux, cGrTipoT);
        nombregrTipot = retornarString(registroAux,
                        FrmanulanovedadControladorEnum.NOMGRTIPO.getValue());
    }

    public String getTipot() {
        return tipot;
    }

    public void setTipot(String tipot) {
        this.tipot = tipot;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getCodigoNov() {
        return codigoNov;
    }

    public void setCodigoNov(String codigoNov) {
        this.codigoNov = codigoNov;
    }

    public String getClaset() {
        return claset;
    }

    public void setClaset(String claset) {
        this.claset = claset;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getGrTipot() {
        return grTipot;
    }

    public void setGrTipot(String grTipot) {
        this.grTipot = grTipot;
    }

    public String getCodDependencia() {
        return codDependencia;
    }

    public void setCodDependencia(String codDependencia) {
        this.codDependencia = codDependencia;
    }

    public String getNombreTipot() {
        return nombreTipot;
    }

    public void setNombreTipot(String nombreTipot) {
        this.nombreTipot = nombreTipot;
    }

    public String getNombregrTipot() {
        return nombregrTipot;
    }

    public void setNombregrTipot(String nombregrTipot) {
        this.nombregrTipot = nombregrTipot;
    }

    public RegistroDataModelImpl getListaTipoNovedad() {
        return listaTipoNovedad;
    }

    public void setListaTipoNovedad(RegistroDataModelImpl listaTipoNovedad) {
        this.listaTipoNovedad = listaTipoNovedad;
    }

    public RegistroDataModelImpl getListaNovedad() {
        return listaNovedad;
    }

    public void setListaNovedad(RegistroDataModelImpl listaNovedad) {
        this.listaNovedad = listaNovedad;
    }

    public RegistroDataModelImpl getListaGrTipoNovedad() {
        return listaGrTipoNovedad;
    }

    public void setListaGrTipoNovedad(
        RegistroDataModelImpl listaGrTipoNovedad) {
        this.listaGrTipoNovedad = listaGrTipoNovedad;
    }

    public List<Registro> getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(List<Registro> listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    @Override
    public void abrirFormulario() {
        // METODO_NO_IMPLEMENTADO
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

}