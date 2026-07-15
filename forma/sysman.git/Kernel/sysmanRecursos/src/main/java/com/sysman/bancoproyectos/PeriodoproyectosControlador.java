package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.PeriodoproyectosControladorEnum;
import com.sysman.bancoproyectos.enums.PeriodoproyectosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
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
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;

/**
 *
 * @author dmaldonado
 * @version 1, 24/08/2015
 * 
 * @author asana
 * @version 2, 29/09/2017 Se realiza refactoring de controlador.
 */
@ManagedBean
@ViewScoped
public class PeriodoproyectosControlador extends BeanBaseModal {

    private final String compania;
    private String intAnioIni;
    private String intAnioFin;
    private List<Registro> listaAno;
    private List<Registro> listaAno2;
    private final String moduloBancos = SessionUtil.getModulo();

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    private Map<String,Object> parametroswf;
    /**
     * Creates a new instance of PeriodoproyectosControlador
     */
    public PeriodoproyectosControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.PERIODOPROYECTOS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
			if(parametroswf != null) {
				SessionUtil.setSessionVar("modulo", "52");
			}
            validarPermisos();
        }
        catch (NamingException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        } finally {
        	try {
				SessionUtil.removeSessionVarContainer("parametroswf");
			} catch(NamingException e) {
				e.printStackTrace();
			}
        }
    }

    @PostConstruct
    public void inicializar() {
        try {

            intAnioIni = ejbSysmanUtil.consultarParametro(
                            compania,
                            "VIGENCIA GUBERNAMENTAL ACTUAL",
                            moduloBancos,
                            new Date(), true);
            intAnioFin = String.valueOf(SysmanFunciones.getParteFecha(
                            new Date(),
                            Calendar.YEAR));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        cargarListaAno();
        cargarListaAno2();
        abrirFormulario();
    }

    public void cargarListaAno() {

        Map<String, Object> parametros = new TreeMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(requestManager
                            .getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoproyectosControladorUrlEnum.URL3511

                                                                            .getValue())
                                            .getUrl(), parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaAno2() {

        Map<String, Object> parametros = new TreeMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(PeriodoproyectosControladorEnum.PARAM1.getValue(),
                        intAnioIni);

        try {
            listaAno2 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PeriodoproyectosControladorUrlEnum.URL3129
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        try {
            if (!("0").equals(SysmanFunciones.nvl(intAnioIni, "0"))
                && !("0").equals(SysmanFunciones.nvl(intAnioFin, "0"))) {

                String strControlaDependencia = ejbSysmanUtil
                                .consultarParametro(
                                                compania,
                                                "CONTROLAR DEPENDENCIA EN BPPIM",
                                                SessionUtil.getModulo(),
                                                new Date(), true);
                strControlaDependencia = strControlaDependencia == null
                    ? "NO" : strControlaDependencia;
                if (("SI").equalsIgnoreCase(strControlaDependencia)
                    && (SessionUtil.getNivelUsuario(moduloBancos) != 9)) {

                    validaDependencia();

                }
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("anoIni", intAnioIni);
                parametros.put("anoFin", intAnioFin);
                Direccionador direccionador = new Direccionador();
                direccionador.setNumForm(Integer.toString(
                                GeneralCodigoFormaEnum.FRMPROYECTOS_CONTROLADOR
                                                .getCodigo()));
                direccionador.setParametros(parametros);
                if(parametroswf != null) {
                	try {
                		parametros.put("parametroswf",parametroswf);
        				SessionUtil.setSessionVarContainer("parametros",parametros);
        			} catch(NamingException e) {
        				e.printStackTrace();
        			}
                	JsfUtil.ejecutarJavaScript("window.location.href='/sysmanBancoProyectos/frmproyectos.sysman';");
                } else {
                	RequestContext.getCurrentInstance().closeDialog(direccionador);
                }
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2257"));
            }
        }
        catch (NullPointerException | SystemException e) {
            Logger.getLogger(PeriodoproyectosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2257"));
        }

        // </CODIGO_DESARROLLADO>
    }

    public void validaDependencia() {
        if (SessionUtil.getUser().getDependencia()
                        .getCodigo() == null
            || ("").equals(SessionUtil.getUser().getDependencia()
                            .getCodigo())) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2255"));
            return;
        }
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        intAnioFin = null;
        cargarListaAno2();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public String getIntAnioIni() {
        return intAnioIni;
    }

    public void setIntAnioIni(String intAnioIni) {
        this.intAnioIni = intAnioIni;
    }

    public String getIntAnioFin() {
        return intAnioFin;
    }

    public void setIntAnioFin(String intAnioFin) {
        this.intAnioFin = intAnioFin;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

}
