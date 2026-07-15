/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.beanbase;

import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.logica.Formulario;
import com.sysman.services.FormContinuoService;
import com.sysman.util.SysmanConstantes;

import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author cmanrique
 */
public abstract class AbstractBeanBase {

    /**
     * Vextor de permisos del formulario, el orden es el siguiente
     * [INSERTAR,BORRAR,MODIFICAR,CONSULTAR,EXPORTAR]
     */
    protected boolean[] permisos;
    protected int numFormulario;
    protected ResourceBundle idioma;
    protected ResourceBundle parametros;
    protected UrlServiceCache urlConexionCache;
    /**
     * Este indicador se utiliza para beans en los que se necesita
     * cambiar los permisos en tiempo de ejecución. Si se cambia a
     * true, se clonará el objeto de permisos con el fin de que no
     * sobreescriba la referencia original, en caso contrario se
     * tomará la referencia original.
     */
    protected boolean indicadorClonarPermisos;

    protected RequestManager requestManager;

    protected FormContinuoService service;

    protected final Log logger = LogFactory.getLog(this.getClass());

    public AbstractBeanBase() {
        try {
            service = FormContinuoService.getInstance();
            SessionUtil.cargarSessionPrincipal();
            urlConexionCache = UrlServiceCache.SYSMANDSUNIST;
            idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
            parametros = ResourceBundle
                            .getBundle(SysmanConstantes.RUTA_PARAMETROS);
            requestManager = new RequestManager();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    public void validarPermisos() throws SysmanException {
        if (permisos == null) {
            Formulario form = SessionUtil.cargarFormulario(
                            numFormulario + "," + SessionUtil.getModulo());
            if (form == null) {
                throw new SysmanException(
                                idioma.getString("MSM_PERMISOS_ACCEDER"));
            }
            permisos = indicadorClonarPermisos ? form.getPermisos().clone()
                : form.getPermisos();
            if (permisos == null || !permisos[3]) {
                throw new SysmanException(
                                idioma.getString("MSM_PERMISOS_ACCEDER"));
            }
        }
    }

    public abstract void abrirFormulario();

    public boolean[] getPermisos() {
        return permisos;
    }

    public void setPermisos(boolean[] permisos) {
        this.permisos = permisos;
    }

    public int getNumFormulario() {
        return numFormulario;
    }

    public void setNumFormulario(int numFormulario) {
        this.numFormulario = numFormulario;
    }

    public FormContinuoService getService() {
        return service;
    }

    public void setService(FormContinuoService service) {
        this.service = service;
    }

}
