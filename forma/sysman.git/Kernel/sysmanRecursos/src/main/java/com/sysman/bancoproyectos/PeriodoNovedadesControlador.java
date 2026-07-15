package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.PeriodoNovedadesControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;

/**
 *
 * @author acaceres
 * @version 1, 05/11/2015
 * 
 * @author jcrodriguez, Refactoring y depuracion
 * @version 2, 29/09/2017
 */
@ManagedBean
@ViewScoped
public class PeriodoNovedadesControlador extends BeanBaseModal {

    private final String compania;
    private String vigenciaPeriodo;
    private List<Registro> listaAno;
    
    private Map<String,Object> parametroswf;

    public PeriodoNovedadesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PERIODO_NOVEDADES_CONTROLADOR
                            .getCodigo();
            parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
			if(parametroswf != null) {
				SessionUtil.setSessionVar("modulo", "52");
			}
            validarPermisos();

        }
        catch (Exception ex) {
            Logger.getLogger(PeriodoNovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
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
        cargarListaAno();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoNovedadesControladorUrlEnum.URL3723
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar() {

        if (!SysmanFunciones.validarVariableVacio(vigenciaPeriodo)) {
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.FRM_SOLICITUD_CDP_CONTROLADOR
                                            .getCodigo()));

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("vigenciaPeriodo", vigenciaPeriodo);
            direccionador.setParametros(parametros);
            if(parametroswf != null) {
            	try {
            		parametros.put("parametroswf",parametroswf);
    				SessionUtil.setSessionVarContainer("parametros",parametros);
    			} catch(NamingException e) {
    				e.printStackTrace();
    			}
            	JsfUtil.ejecutarJavaScript("window.location.href='/sysmanBancoProyectos/frmsolicitudcdp.sysman';");
            } else {
            	RequestContext.getCurrentInstance().closeDialog(direccionador);
            }
        }
        else {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2256"));
        }

    }

    public void oprimirCancelar() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public String getVigenciaPeriodo() {
        return vigenciaPeriodo;
    }

    public void setVigenciaPeriodo(String vigenciaPeriodo) {
        this.vigenciaPeriodo = vigenciaPeriodo;
    }

}
