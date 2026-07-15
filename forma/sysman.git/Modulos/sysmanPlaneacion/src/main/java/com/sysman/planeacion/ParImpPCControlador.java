package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.planeacion.enums.ParImpPCControladorEnum;
import com.sysman.planeacion.enums.ParImpPCControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * @author dmaldonado
 * @version 1, 20/01/2016
 * @modified jguerrero
 * @version 2. 08/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped

public class ParImpPCControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private boolean ckEspecial1;
    private String subProyecto4;

    private String depInicial3;
    private String depFinal3;
    private String tipoInforme;

    private String mesInicial2;
    private String mesFinal2;

    private String nomSubProyecto4;
    private String nomDepInicial3;
    private String nomDepFinal3;
    private String top;
    private StreamedContent archivoDescarga;
    private List<Registro> listaSelTipoInforme;

    private RegistroDataModelImpl listaSubProyecto4;
    private RegistroDataModelImpl listaSelDepInicial3;
    private RegistroDataModelImpl listaSelDepFinal3;

    private boolean visibleCuatro;
    private boolean visibleTresDepIni;
    private boolean visibleDos;
    private boolean visibleUno;
    private String anoPlan;
    private String codigoPlan;
    private String nombreReporte;
    private String entreDependencias;
    private boolean visibleMesFinal;
    private boolean visibleMesInicial;
    private boolean visibleDependencia;
    private boolean visibleSubProy;
    private boolean visibleMes;
    private boolean visibleDependenciaIni;
    private boolean visibleDependenciaFin;
    private boolean visibleTresDepFin;
    private final String reporte1477;
    private String dependenciaAux;

    /**
     * Creates a new instance of ParImpPCControlador
     */
    public ParImpPCControlador() {
        super();
        reporte1477 = "001477IPlanCompras";
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            anoPlan = (String) parametros.get("anoPlan");
            codigoPlan = (String) parametros.get("codigoPlan");
            dependenciaAux = (String) parametros.get("dependencia");

        }
        try {
            numFormulario = GeneralCodigoFormaEnum.PAR_IMP_PCCONTROLADOR
                            .getCodigo();
            validarPermisos();
            tipoInforme = "1";
            nombreReporte = reporte1477;
            visibleDependencia = false;
            visibleDependenciaFin = false;
            visibleTresDepFin = false;
            visibleDependenciaIni = false;
            visibleMes = false;
            visibleUno = true;
            visibleDos = false;
            visibleTresDepIni = false;
            visibleCuatro = false;
            depFinal3 = dependenciaAux;
            depInicial3 = dependenciaAux;

        }
        catch (Exception ex) {
            Logger.getLogger(ParImpPCControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        cargarListaSelTipoInforme();

        cargarListaSubProyecto4();
        cargarListaSelDepInicial3();
        cargarListaSelDepFinal3();
        abrirFormulario();
        cargarNombreDep();
    }

    public void cargarModal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaSelTipoInforme() {

        try {
            listaSelTipoInforme = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParImpPCControladorUrlEnum.URL8024
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarSelTipoInforme() {
        // <CODIGO_DESARROLLADO>
        ckEspecial1 = false;
        mesInicial2 = null;
        mesFinal2 = null;

        nomSubProyecto4 = null;
        nombreReporte = validacionesForma();
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaSubProyecto4() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ParImpPCControladorUrlEnum.URL7321
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaSubProyecto4 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOSUBPROYECTO");

        // 545002
    }

    public void cargarListaSelDepInicial3() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ParImpPCControladorUrlEnum.URL5956
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaSelDepInicial3 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

        // 62002
    }

    public void cargarListaSelDepFinal3() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ParImpPCControladorUrlEnum.URL6653
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGOINICIAL", depInicial3);

        listaSelDepFinal3 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

        // 62011
    }

    public void cambiarMesInicial2() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMesFinal2() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IEspecial1
     * 
     * 
     */
    public void cambiarIEspecial1() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        archivoDescarga = null;
        // <CODIGO_DESARROLLADO>
        if (!validacionComboPrincipal()) {
            return;
        }

        generarReporte(ReportesBean.FORMATOS.EXCEL);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validacionComboPrincipal()) {
            return;
        }
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaSubProyecto4(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        subProyecto4 = registroAux.getCampos().get("CODIGOSUBPROYECTO")
                        .toString();
        nomSubProyecto4 = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName())
                                            .toString();
    }

    public void seleccionarFilaSelDepInicial3(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        depInicial3 = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        nomDepInicial3 = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
        depFinal3 = null;
        cargarListaSelDepFinal3();
    }

    public void seleccionarFilaSelDepFinal3(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        depFinal3 = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        nomDepFinal3 = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
    }

    public boolean isCkEspecial1() {
        return ckEspecial1;
    }

    public void setCkEspecial1(boolean ckEspecial1) {
        this.ckEspecial1 = ckEspecial1;
    }

    public String getSubProyecto4() {
        return subProyecto4;
    }

    public void setSubProyecto4(String subProyecto4) {
        this.subProyecto4 = subProyecto4;
    }

    public String getDepInicial3() {
        return depInicial3;
    }

    public void setDepInicial3(String depInicial3) {
        this.depInicial3 = depInicial3;
    }

    public String getDepFinal3() {
        return depFinal3;
    }

    public void setDepFinal3(String depFinal3) {
        this.depFinal3 = depFinal3;
    }

    public String getTipoInforme() {
        return tipoInforme;
    }

    public void setTipoInforme(String tipoInforme) {
        this.tipoInforme = tipoInforme;
    }

    public String getMesInicial2() {
        return mesInicial2;
    }

    public void setMesInicial2(String mesInicial2) {
        this.mesInicial2 = mesInicial2;
    }

    public String getMesFinal2() {
        return mesFinal2;
    }

    public void setMesFinal2(String mesFinal2) {
        this.mesFinal2 = mesFinal2;
    }

    public String getNomSubProyecto4() {
        return nomSubProyecto4;
    }

    public void setNomSubProyecto4(String nomSubProyecto4) {
        this.nomSubProyecto4 = nomSubProyecto4;
    }

    public String getNomDepInicial3() {
        return nomDepInicial3;
    }

    public void setNomDepInicial3(String nomDepInicial3) {
        this.nomDepInicial3 = nomDepInicial3;
    }

    public String getNomDepFinal3() {
        return nomDepFinal3;
    }

    public void setNomDepFinal3(String nomDepFinal3) {
        this.nomDepFinal3 = nomDepFinal3;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public List<Registro> getListaSelTipoInforme() {
        return listaSelTipoInforme;
    }

    public void setListaSelTipoInforme(List<Registro> listaSelTipoInforme) {
        this.listaSelTipoInforme = listaSelTipoInforme;
    }

    public RegistroDataModelImpl getListaSubProyecto4() {
        return listaSubProyecto4;
    }

    public void setListaSubProyecto4(RegistroDataModelImpl listaSubProyecto4) {
        this.listaSubProyecto4 = listaSubProyecto4;
    }

    public RegistroDataModelImpl getListaSelDepInicial3() {
        return listaSelDepInicial3;
    }

    public void setListaSelDepInicial3(
        RegistroDataModelImpl listaSelDepInicial3) {
        this.listaSelDepInicial3 = listaSelDepInicial3;
    }

    public RegistroDataModelImpl getListaSelDepFinal3() {
        return listaSelDepFinal3;
    }

    public void setListaSelDepFinal3(RegistroDataModelImpl listaSelDepFinal3) {
        this.listaSelDepFinal3 = listaSelDepFinal3;
    }

    public boolean isVisibleCuatro() {
        return visibleCuatro;
    }

    public void setVisibleCuatro(boolean visibleCuatro) {
        this.visibleCuatro = visibleCuatro;
    }

    public boolean isVisibleTres() {
        return visibleTresDepIni;
    }

    public void setVisibleTres(boolean visibleTres) {
        this.visibleTresDepIni = visibleTres;
    }

    public boolean isVisibleDos() {
        return visibleDos;
    }

    public void setVisibleDos(boolean visibleDos) {
        this.visibleDos = visibleDos;
    }

    public boolean isVisibleUno() {
        return visibleUno;
    }

    public void setVisibleUno(boolean visibleUno) {
        this.visibleUno = visibleUno;
    }

    public String getAnoPlan() {
        return anoPlan;
    }

    public void setAnoPlan(String anoPlan) {
        this.anoPlan = anoPlan;
    }

    public String getCodigoPlan() {
        return codigoPlan;
    }

    public void setCodigoPlan(String codigoPlan) {
        this.codigoPlan = codigoPlan;
    }

    public String getNombreReporte() {
        return nombreReporte;
    }

    public void setNombreReporte(String nombreReporte) {
        this.nombreReporte = nombreReporte;
    }

    public String getEntreDependencias() {
        return entreDependencias;
    }

    public void setEntreDependencias(String entreDependencias) {
        this.entreDependencias = entreDependencias;
    }

    public boolean isVisibleMesFinal() {
        return visibleMesFinal;
    }

    public void setVisibleMesFinal(boolean visibleMesFinal) {
        this.visibleMesFinal = visibleMesFinal;
    }

    public boolean isVisibleMesInicial() {
        return visibleMesInicial;
    }

    public void setVisibleMesInicial(boolean visibleMesInicial) {
        this.visibleMesInicial = visibleMesInicial;
    }

    public boolean isVisibleDependencia() {
        return visibleDependencia;
    }

    public void setVisibleDependencia(boolean visibleDependencia) {
        this.visibleDependencia = visibleDependencia;
    }

    public boolean isVisibleSubProy() {
        return visibleSubProy;
    }

    public void setVisibleSubProy(boolean visibleSubProy) {
        this.visibleSubProy = visibleSubProy;
    }

    public boolean isVisibleMes() {
        return visibleMes;
    }

    public void setVisibleMes(boolean visibleMes) {
        this.visibleMes = visibleMes;
    }

    public boolean isVisibleDependenciaIni() {
        return visibleDependenciaIni;
    }

    public void setVisibleDependenciaIni(boolean visibleDependenciaIni) {
        this.visibleDependenciaIni = visibleDependenciaIni;
    }

    public boolean isVisibleDependenciaFin() {
        return visibleDependenciaFin;
    }

    public void setVisibleDependenciaFin(boolean visibleDependenciaFin) {
        this.visibleDependenciaFin = visibleDependenciaFin;
    }

    public String getCompania() {
        return compania;
    }

    public String getModulo() {
        return modulo;
    }

    public boolean isVisibleTresDepIni() {
        return visibleTresDepIni;
    }

    public void setVisibleTresDepIni(boolean visibleTresDepIni) {
        this.visibleTresDepIni = visibleTresDepIni;
    }

    public boolean isVisibleTresDepFin() {
        return visibleTresDepFin;
    }

    public void setVisibleTresDepFin(boolean visibleTresDepFin) {
        this.visibleTresDepFin = visibleTresDepFin;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    @Override
    public void abrirFormulario() {
        // metodo herado del bean base
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    private String validacionesForma() {
        String reporte = null;
        if ("1".equals(tipoInforme)) {
            visibleUno = true;
            visibleDos = false;
            visibleTresDepIni = false;
            visibleTresDepFin = false;
            visibleSubProy = false;
            visibleCuatro = false;
            visibleMesFinal = false;
            visibleMesInicial = false;
            visibleDependencia = false;
            visibleSubProy = false;
            visibleMes = false;
            visibleDependenciaIni = false;
            visibleDependenciaFin = false;
            top = "79px";
            if (ckEspecial1) {

                reporte = ParImpPCControladorEnum.REPORTE470.getValue();
                entreDependencias = "";
            }
            else {
                reporte = reporte1477;
                entreDependencias = "";
            }
        }
        if ("2".equals(tipoInforme)) {
            visibleDos = true;
            visibleTresDepIni = true;
            visibleMesFinal = true;
            visibleMesInicial = true;
            visibleDependencia = true;
            visibleUno = false;
            visibleTresDepFin = false;
            visibleSubProy = false;
            visibleCuatro = false;
            visibleSubProy = false;
            visibleMes = false;
            visibleDependenciaIni = false;
            visibleDependenciaFin = false;
            reporte = "000471IPlanComprasDepe";
            entreDependencias = "";
            top = "79px";
        }
        if ("3".equals(tipoInforme)) {
            visibleDependenciaIni = true;
            visibleDependenciaFin = true;
            visibleTresDepIni = true;
            visibleTresDepFin = true;
            visibleUno = false;
            visibleDos = false;
            visibleSubProy = false;
            visibleCuatro = false;
            visibleMesFinal = false;
            visibleMesInicial = false;
            visibleDependencia = false;
            visibleSubProy = false;
            visibleMes = false;
            reporte = "000472IPlanComprasEntreDep";
            entreDependencias = SysmanFunciones.concatenar("ENTRE ",
                            nomDepInicial3, " Y ", nomDepFinal3);
            top = "79px";
        }
        if ("4".equals(tipoInforme)) {
            visibleDependencia = true;
            visibleSubProy = true;
            visibleSubProy = true;
            visibleMesInicial = true;
            visibleMes = true;
            visibleDos = false;
            visibleTresDepIni = true;
            visibleTresDepFin = false;
            visibleUno = false;
            visibleCuatro = false;
            visibleMesFinal = false;
            visibleDependenciaIni = false;
            visibleDependenciaFin = false;
            top = "102px";

            reporte = "000474IPlanComprasEntreSub";
            entreDependencias = SysmanFunciones.concatenar("ENTRE DEPENDENCIA ",
                            nomDepInicial3, " Y ACTIVIDAD ", nomSubProyecto4);
        }
        return reporte;

    }

    private void generarReporte(ReportesBean.FORMATOS formato) {

        try {
            archivoDescarga = null;
            nombreReporte = validacionesForma();

            HashMap<String, Object> reemplazar = new HashMap<>();
            String strSql;

            reemplazar.put("codigo", codigoPlan);
            reemplazar.put("ano", anoPlan);
            reemplazar.put("mesInicial",
                            SysmanFunciones.nvlStr(mesInicial2, ""));
            reemplazar.put("mesFinal", SysmanFunciones.nvlStr(mesFinal2, ""));
            reemplazar.put("depInicial",
                            SysmanFunciones.nvlStr(depInicial3, ""));

            reemplazar.put("depFinal", SysmanFunciones.nvlStr(depFinal3, ""));
            reemplazar.put("subproyecto",
                            SysmanFunciones.nvlStr(subProyecto4, ""));

            strSql = Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar);
            Date fecha = new Date();
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("PR_AHORA", fecha);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_TITULO", SysmanFunciones
                            .concatenar("Plan de Compras de ", anoPlan));
            parametros.put("PR_ENTREDEPENDENCIAS", entreDependencias);

            archivoDescarga = JsfUtil.exportarStreamed(
                            nombreReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    private void cargarNombreDep() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependenciaAux);

        try {
            Registro nombreDep = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParImpPCControladorUrlEnum.URL8694
                                                                            .getValue())
                                            .getUrl(), param));

            nomDepFinal3 = retornarString(nombreDep,
                            GeneralParameterEnum.NOMBRE.getName());
            nomDepInicial3 = retornarString(nombreDep,
                            GeneralParameterEnum.NOMBRE.getName());

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validacionComboPrincipal() {

        if (SysmanFunciones.validarVariableVacio(tipoInforme)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3781"));
            return false;
        }
        return true;
    }

}
