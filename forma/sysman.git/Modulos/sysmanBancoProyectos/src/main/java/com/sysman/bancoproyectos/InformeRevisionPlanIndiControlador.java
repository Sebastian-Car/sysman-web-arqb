package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCuatroRemote;
import com.sysman.bancoproyectos.enums.InformeRevisionPlanIndiControladorUrlEnum;
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 19/11/2015
 *
 * @author spina
 * @version 2, 25/09/2017 - se refactoriza para dss, depuracion sonar
 * y ejbs
 */
@ManagedBean
@ViewScoped
public class InformeRevisionPlanIndiControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    /**
     * Constante definida para almacenar la palabra
     * "MSM_TRANS_INTERRUMPIDA"
     */
    private String vigencia;
    private String informe;
    private StreamedContent archivoDescarga;
    private List<Registro> listaVigenciaInicial;

    @EJB
    private EjbBancoProyectoCuatroRemote ejbBancoProyectoCuatro;

    /**
     * Creates a new instance of InformeRevisionPlanIndiControlador
     */
    public InformeRevisionPlanIndiControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORME_REVISION_PLAN_INDI_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InformeRevisionPlanIndiControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaVIGENCIAINICIAL();
        abrirFormulario();
    }

    public void cargarListaVIGENCIAINICIAL() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaVigenciaInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeRevisionPlanIndiControladorUrlEnum.URL3410
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirComando6() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(informe)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2417"));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(vigencia)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2256"));
            return;
        }
        switch (informe) {
        case "1":
            ponderacionPlanIndicativo(FORMATOS.PDF);
            break;
        case "2":
            indicadoresPonderacion(FORMATOS.PDF);
            break;
        default:
            break;

        }
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(informe)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2417"));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(vigencia)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2256"));
            return;
        }
        switch (informe) {
        case "1":
            ponderacionPlanIndicativo(FORMATOS.EXCEL97);
            break;
        case "2":
            indicadoresPonderacion(FORMATOS.EXCEL97);
            break;
        default:
            break;
        }
    }

    public void ponderacionPlanIndicativo(FORMATOS formato) {
        Map<String, Object> parametros = new HashMap<>();
        String nombreReporte = "000208rptCompPonderacion";
        try {
            String strSql = Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo),
                            new HashMap<String, Object>());
            parametros.put("PR_STRSQL", strSql);
            ejbBancoProyectoCuatro.mayorizarPonderacion(compania,
                            Integer.parseInt(vigencia),
                            true,
                            null, false, SessionUtil.getUser().getCodigo());
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000208rptCompPonderacion", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException
                        | SysmanException | NumberFormatException
                        | SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(InformeRevisionPlanIndiControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void indicadoresPonderacion(FORMATOS formato) {
        String nombreReporte = "000204rptPondeMayor100";
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("vigencia", vigencia);
        String strSql = Reporteador.resuelveConsulta(nombreReporte,
                        Integer.parseInt(modulo), reemplazar);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("PR_STRSQL", strSql);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(InformeRevisionPlanIndiControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaVigenciaInicial() {
        return listaVigenciaInicial;
    }

    public void setListaVigenciaInicial(List<Registro> listaVigenciaInicial) {
        this.listaVigenciaInicial = listaVigenciaInicial;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

}