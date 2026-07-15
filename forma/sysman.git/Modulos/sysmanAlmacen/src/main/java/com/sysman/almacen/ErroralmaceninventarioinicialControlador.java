package com.sysman.almacen;

import com.sysman.almacen.enums.ErroralmaceninventarioinicialControladorEnum;
import com.sysman.almacen.enums.ErroralmaceninventarioinicialControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoNAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author dsuesca
 * @version 1, 28/03/2016
 * 
 * @author jlramirez
 * @version 2, 27/04/2017, Se realizo refactoring
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.
 */
@ManagedBean
@ViewScoped
public class ErroralmaceninventarioinicialControlador
                extends BeanBaseContinuoNAcmeImpl {

    private final String compania;

    private String claseOrden;

    /**
     * Creates a new instance of
     * ErroralmaceninventarioinicialControlador
     */
    public ErroralmaceninventarioinicialControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                claseOrden = parametros.get("claseOrden").toString();
            }
            SessionUtil.cleanFlash();

            // 598
            numFormulario = GeneralCodigoFormaEnum.ERRORALMACENINVENTARIOINICIAL_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ErroralmaceninventarioinicialControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        tabla = ErroralmaceninventarioinicialControladorEnum.PARAM0.getValue();
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ErroralmaceninventarioinicialControladorUrlEnum.URL2613
                                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        registro = new Registro();
        abrirFormulario();
    }

    public void cerrarFormulario() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
