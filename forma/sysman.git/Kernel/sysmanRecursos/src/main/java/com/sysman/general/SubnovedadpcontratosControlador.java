package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 03/12/2015
 * @modified jguerrero
 * @version 2. 05/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario
 */
@ManagedBean
@ViewScoped

public class SubnovedadpcontratosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String numeroOrden;
    private String claseOrden;
	private Map<String, Object> parametroswf;
	private String modulo;

    /**
     * Creates a new instance of SubnovedadpcontratosControlador
     */
    public SubnovedadpcontratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        
        try {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "9");
        	}
        	modulo = SessionUtil.getModulo();
        	
        	numFormulario = GeneralCodigoFormaEnum.SUBNOVEDADPCONTRATOS_CONTROLADOR
                    .getCodigo();
        	validarPermisos();
        }
        catch (SysmanException | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = "BORRA_TEMPNOVEDADES";
        enumBase = GenericUrlEnum.NOVEDADCONTRATO;

        numeroOrden = JsfUtil.getParametros().get("numeroOrden");
        claseOrden = JsfUtil.getParametros().get("claseOrden");
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("CLASE", claseOrden);
        parametrosListado.put("NUMORDEN", numeroOrden);

    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public String getClaseOrden() {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden) {
        this.claseOrden = claseOrden;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("RNUM");
        registro.getCampos().remove("RID");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

}