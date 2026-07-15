package com.sysman.almacen;

import com.sysman.almacen.enums.InvFiscalDevolutivosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * @author esarmiento
 * @version 1, 27/01/2016
 * 
 * @author ybecerra
 * @version 2, 03/05/2017 Refactoring
 *
 */
@ManagedBean
@ViewScoped

public class InvFiscalDevolutivosControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String cod;
    private final String prNombreCompania;
    private final String prStrsql;
    private final String depFinal;
    private final String depInicial;
    private String tipoInforme;
    private boolean basico;
    private String dependenciaInicial;
    private String dependenciaFinal;
    private String nombreDepInicial;
    private String nombreDepFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaDependenciaInicial;
    private RegistroDataModelImpl listaDependenciaFinal;

    @EJB
    private EjbSysmanUtilRemote ejbAlmacenCero;

    /**
     * Creates a new instance of InvFiscalDevolutivosControlador
     */
    public InvFiscalDevolutivosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cod = GeneralParameterEnum.CODIGO.getName();
        prNombreCompania = "PR_NOMBRECOMPANIA";
        prStrsql = "PR_STRSQL";
        depFinal = "depFinal";
        depInicial = "depInicial";
        try {
            numFormulario = GeneralCodigoFormaEnum.INV_FISCAL_DEVOLUTIVOS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InvFiscalDevolutivosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaDependenciaInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        //heredado del bean padre
    }

    private RegistroDataModelImpl generarDependenciasInicial(boolean inicial) {
        RegistroDataModelImpl listaSalida = null;
        String parametro = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            parametro = ejbAlmacenCero.consultarParametro(compania,
                            "PRESENTAR BODEGAS ESPECIALES EN INVENTARIO FISCAL",
                            modulo, new Date(), true);
            if (!SysmanFunciones.validarVariableVacio(parametro)
                && ("SI").equals(parametro)) {

                if (inicial) {
                    UrlBean urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    InvFiscalDevolutivosControladorUrlEnum.URL3726
                                                                    .getValue());

                    listaSalida = new RegistroDataModelImpl(urlBean.getUrl(),
                                    urlBean.getUrlConteo().getUrl(), param,
                                    true,
                                    cod);
                }
                else {
                    param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                                    dependenciaInicial);
                    UrlBean urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    InvFiscalDevolutivosControladorUrlEnum.URL131
                                                                    .getValue());

                    listaSalida = new RegistroDataModelImpl(urlBean.getUrl(),
                                    urlBean.getUrlConteo().getUrl(), param,
                                    true,
                                    cod);
                }

            }
            else {

                if (inicial) {
                    UrlBean urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    InvFiscalDevolutivosControladorUrlEnum.URL4168
                                                                    .getValue());

                    listaSalida = new RegistroDataModelImpl(urlBean.getUrl(),
                                    urlBean.getUrlConteo().getUrl(), param,
                                    true, cod);
                }
                else {
                    param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                                    dependenciaInicial);
                    UrlBean urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    InvFiscalDevolutivosControladorUrlEnum.URL159
                                                                    .getValue());

                    listaSalida = new RegistroDataModelImpl(urlBean.getUrl(),
                                    urlBean.getUrlConteo().getUrl(), param,
                                    true,
                                    cod);
                }

            }
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(InvFiscalDevolutivosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return listaSalida;
    }

    public void cargarListaDependenciaInicial() {
        listaDependenciaInicial = generarDependenciasInicial(true);
    }

    public void cargarListaDependenciaFinal() {
        listaDependenciaFinal = generarDependenciasInicial(false);
    }

    public void oprimirpdf() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarVacios() {
        if (SysmanFunciones.validarVariableVacio(dependenciaInicial)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1909"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(dependenciaFinal)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1910"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(tipoInforme)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1914"));
            return false;
        }
        return true;
    }

    private void generarInforme(FORMATOS formato) {
        archivoDescarga = null;
        if (!validarVacios()) {
            return;
        }
        String nombreReporte = "";
        String strsql = "";
    
        try {
   
            if (("2").equals(tipoInforme)) {
                nombreReporte = "000478InventarioFiscalElem";
                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put(depInicial, dependenciaInicial);
                reemplazar.put(depFinal, dependenciaFinal);
                strsql = Reporteador.resuelveConsulta(nombreReporte,
                                Integer.parseInt(modulo), reemplazar);

                Map<String, Object> parametros = new HashMap<>();
                parametros.put(prNombreCompania,
                                SessionUtil.getCompaniaIngreso().getNombre());
                parametros.put(prStrsql, strsql);

                archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                                parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            else {
                if (basico) {
                    nombreReporte = "000480InventarioFiscalbasico";
                    HashMap<String, Object> reemplazar = new HashMap<>();
                    reemplazar.put(depInicial, dependenciaInicial);
                    reemplazar.put(depFinal, dependenciaFinal);
                    strsql = Reporteador.resuelveConsulta(nombreReporte,
                                    Integer.parseInt(modulo), reemplazar);

                    Map<String, Object> parametros = new HashMap<>();
                    parametros.put(prNombreCompania, SessionUtil
                                    .getCompaniaIngreso().getNombre());
                    parametros.put(prStrsql, strsql);

                    archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                                    parametros,ConectorPool.ESQUEMA_SYSMAN, formato);
                }
                else {
                    nombreReporte = "000483InventarioFiscal";
                    HashMap<String, Object> reemplazar = new HashMap<>();
                    reemplazar.put(depInicial, dependenciaInicial);
                    reemplazar.put(depFinal, dependenciaFinal);
                    strsql = Reporteador.resuelveConsulta(nombreReporte,
                                    Integer.parseInt(modulo), reemplazar);

                    Map<String, Object> parametros = new HashMap<>();
                    parametros.put(prNombreCompania, SessionUtil
                                    .getCompaniaIngreso().getNombre());
                    parametros.put(prStrsql, strsql);
                    archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                                    parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
                }
            }
        }
        catch (FileNotFoundException ex) {
      
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+nombreReporte);
            Logger.getLogger(InvFiscalDevolutivosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }         
        catch (IOException| JRException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(InvFiscalDevolutivosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {   
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
   
    }

    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaDependenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaInicial = registroAux.getCampos().get(cod) == null ? ""
            : registroAux.getCampos().get(cod).toString();
        nombreDepInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == null
                            ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName())
                                            .toString();
        dependenciaFinal = null;
        nombreDepFinal = null;
        cargarListaDependenciaFinal();
    }

    public void seleccionarFilaDependenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaFinal = registroAux.getCampos().get(cod) == null ? ""
            : registroAux.getCampos().get(cod).toString();
        nombreDepFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == null
                            ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName())
                                            .toString();
    }

    public String getTipoInforme() {
        return tipoInforme;
    }

    public void setTipoInforme(String tipoInforme) {
        this.tipoInforme = tipoInforme;
    }

    public boolean isBasico() {
        return basico;
    }

    public void setBasico(boolean basico) {
        this.basico = basico;
    }

    public String getDependenciaInicial() {
        return dependenciaInicial;
    }

    public void setDependenciaInicial(String dependenciaInicial) {
        this.dependenciaInicial = dependenciaInicial;
    }

    public String getDependenciaFinal() {
        return dependenciaFinal;
    }

    public void setDependenciaFinal(String dependenciaFinal) {
        this.dependenciaFinal = dependenciaFinal;
    }

    public String getNombreDepInicial() {
        return nombreDepInicial;
    }

    public void setNombreDepInicial(String nombreDepInicial) {
        this.nombreDepInicial = nombreDepInicial;
    }

    public String getNombreDepFinal() {
        return nombreDepFinal;
    }

    public void setNombreDepFinal(String nombreDepFinal) {
        this.nombreDepFinal = nombreDepFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaDependenciaInicial() {
        return listaDependenciaInicial;
    }

    public void setListaDependenciaInicial(
        RegistroDataModelImpl listaDependenciaInicial) {
        this.listaDependenciaInicial = listaDependenciaInicial;
    }

    public RegistroDataModelImpl getListaDependenciaFinal() {
        return listaDependenciaFinal;
    }

    public void setListaDependenciaFinal(
        RegistroDataModelImpl listaDependenciaFinal) {
        this.listaDependenciaFinal = listaDependenciaFinal;
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }
}
