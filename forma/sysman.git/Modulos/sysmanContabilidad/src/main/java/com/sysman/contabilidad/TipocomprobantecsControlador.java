package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.TipocomprobantecsControladorEnum;
import com.sysman.contabilidad.enums.TipocomprobantecsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

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

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author vmolano
 * @version 1, 07/03/2016
 * @modifier amonroy
 * @version 2, 12/04/2017 Proceso de Refactoring y Revision de buenas
 * practicas sugeridas por la herramienta SonarLint
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio código formulario y actualización de
 * ConnectorPool
 */
@ManagedBean
@ViewScoped
public class TipocomprobantecsControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;
    private boolean guardaRegistro;
    private Registro registroSub;
    private List<Registro> listaTipoDocumento;
    private List<Registro> listaClaseContable;
    private List<Registro> listaCompRelacionado;
    private List<Registro> listaCodigo;
    private List<Registro> listaCuadrocombinado23;
    private List<Registro> listaSubconsecutivo;
    private String controlDocumental;
    private boolean visibleResolucionDian;
    private boolean aplicaReintegro;
    

 

	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    public TipocomprobantecsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.TIPOCOMPROBANTECS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(TipocomprobantecsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {

        cargarListaTipoDocumento();
        cargarListaClaseContable();

        cargarListaCodigo();
        cargarListaCuadrocombinado23();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaCompRelacionado();
        cargarListaSubconsecutivo();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubconsecutivo = null;
    }

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.TIPO_COMPROBANTE;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    	
    }

    public void cargarListaSubconsecutivo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(TipocomprobantecsControladorEnum.PARAM1.getValue(),
                        registro.getCampos().get("CODIGO"));
        try {

            listaSubconsecutivo = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            GenericUrlEnum.CONSECUTIVOTC
                                                                                            .getGridKey())
                                                            .getUrl(),
                                            param),
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.CONSECUTIVOTC
                                                            .getTable()));

        }
        catch (SystemException | SysmanException ex) {
            Logger.getLogger(TipocomprobantecsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());

        }

    }

    public void cargarListaTipoDocumento() {
        try {
            listaTipoDocumento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipocomprobantecsControladorUrlEnum.URL4194
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaClaseContable() {
        try {
            listaClaseContable = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipocomprobantecsControladorUrlEnum.URL4484
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCompRelacionado() {
        Map<String, Object> param = new TreeMap<>();
        param.put(TipocomprobantecsControladorEnum.PARAM0.getValue(),
                        registro.getCampos().get("CLASE_CONTABLE"));

        try {
            listaCompRelacionado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipocomprobantecsControladorUrlEnum.URL5012
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
  //<METODOS_CAMBIAR>	
    /**
     * Metodo ejecutado al cambiar el control ckDocSoporte
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
public void cambiarckDocSoporte() {
         //<CODIGO_DESARROLLADO>
	if(visibleResolucionDian) {
		visibleResolucionDian =  false;
	}else {
		visibleResolucionDian =  true;
	}
	
        //</CODIGO_DESARROLLADO>
    }

	public void cambiarClaseContable() 
	{    
		if("A".equals(registro.getCampos().get("CLASE_CONTABLE"))
			|| "T".equals(registro.getCampos().get("CLASE_CONTABLE")))
        {
        	aplicaReintegro = true;
        }
        else
        {
        	aplicaReintegro = false;
        }
	}
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>	
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>	
//</METODOS_ARBOL>
//<METODOS_BOTONES>	
    /**
     * 
     * Metodo ejecutado al oprimir el boton ResolucionDian
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirResolucionDian() {
         //<CODIGO_DESARROLLADO>
	  // <CODIGO_DESARROLLADO>

    Map<String, Object> parametros = new HashMap<>();

	parametros.put("CODIGO", registro.getCampos().get("CODIGO"));


	Direccionador direccionador = new Direccionador();
	direccionador.setNumForm(Integer.toString(
			GeneralCodigoFormaEnum.RESOLUCIONDIAN_CT
			.getCodigo()));
	direccionador.setParametros(parametros);

	SessionUtil.redireccionarForma(direccionador,
			SessionUtil.getModulo());
    
        //</CODIGO_DESARROLLADO>
    }
//</METODOS_BOTONES>


    public void cargarListaCodigo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCodigo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipocomprobantecsControladorUrlEnum.URL5587
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuadrocombinado23() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCuadrocombinado23 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipocomprobantecsControladorUrlEnum.URL6151
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void agregarRegistroSubSubconsecutivo() {
        try {
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos()
                            .put(TipocomprobantecsControladorEnum.PARAM2
                                            .getValue(),
                                            registro.getCampos().get("CODIGO"));
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONSECUTIVOTC
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaSubconsecutivo();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(TipocomprobantecsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubconsecutivo(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().put(
                            GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(
                            GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONSECUTIVOTC
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(TipocomprobantecsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubconsecutivo();
        }
    }

    public void eliminarRegSubSubconsecutivo(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONSECUTIVOTC
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubconsecutivo();
        }
        catch (SystemException ex) {
            Logger.getLogger(TipocomprobantecsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    private void cargarParamtero() {

        try {
            controlDocumental = ejbSysmanUtilRemote.consultarParametro(compania,
                            "PERMITE CONTROL DOCUMENTAL", modulo, new Date(),
                            true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelarEdicionSubconsecutivo() {
        cargarListaSubconsecutivo();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarParamtero();
       
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        visibleResolucionDian =  (boolean) registro.getCampos().get("DOCSOPORTE");
        if("A".equals(registro.getCampos().get("CLASE_CONTABLE"))
        		|| "T".equals(registro.getCampos().get("CLASE_CONTABLE")))
        {
        	aplicaReintegro = true;
        }
        else
        {
        	aplicaReintegro = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
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
        registro.getCampos().remove("CONSECUTIVOINICIAL");
        registro.getCampos().remove("DETALLEBANCO");
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

    public List<Registro> getListaTipoDocumento() {
        return listaTipoDocumento;
    }

    public void setListaTipoDocumento(List<Registro> listaTipoDocumento) {
        this.listaTipoDocumento = listaTipoDocumento;
    }

    public List<Registro> getListaClaseContable() {
        return listaClaseContable;
    }

    public void setListaClaseContable(List<Registro> listaClaseContable) {
        this.listaClaseContable = listaClaseContable;
    }

    public List<Registro> getListaCompRelacionado() {
        return listaCompRelacionado;
    }

    public void setListaCompRelacionado(List<Registro> listaCompRelacionado) {
        this.listaCompRelacionado = listaCompRelacionado;
    }

    public List<Registro> getListaCodigo() {
        return listaCodigo;
    }

    public void setListaCodigo(List<Registro> listaCodigo) {
        this.listaCodigo = listaCodigo;
    }

    public List<Registro> getListaCuadrocombinado23() {
        return listaCuadrocombinado23;
    }

    public void setListaCuadrocombinado23(
        List<Registro> listaCuadrocombinado23) {
        this.listaCuadrocombinado23 = listaCuadrocombinado23;
    }

    public List<Registro> getListaSubconsecutivo() {
        return listaSubconsecutivo;
    }

    public void setListaSubconsecutivo(List<Registro> listaSubconsecutivo) {
        this.listaSubconsecutivo = listaSubconsecutivo;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    /**
     * @return the controlDocumental
     */
    public String getControlDocumental() {
        return controlDocumental;
    }

    /**
     * @param controlDocumental
     * the controlDocumental to set
     */
    public void setControlDocumental(String controlDocumental) {
        this.controlDocumental = controlDocumental;
    }
    public boolean isVisibleResolucionDian() {
 		return visibleResolucionDian;
 	}

 	public void setVisibleResolucionDian(boolean visibleResolucionDian) {
 		this.visibleResolucionDian = visibleResolucionDian;
 	}

	public boolean isAplicaReintegro() {
		return aplicaReintegro;
	}

	public void setAplicaReintegro(boolean aplicaReintegro) {
		this.aplicaReintegro = aplicaReintegro;
	}

}
