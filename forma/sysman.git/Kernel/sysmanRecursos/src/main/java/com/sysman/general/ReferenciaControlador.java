package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ReferenciaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

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

/**
 *
 * @author jrodriguezr
 * @version 1, 04/03/2016
 * 
 * @author eamaya
 * @version 2, 11/04/2017 Proceso de Refactoring y Correciones
 * SonarLint
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n de
 * ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped

public class ReferenciaControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private List<Registro> listacbAnio;
    private List<Registro> listaAnio;
    private String mesInicial;
    private String mesFinal;
    private String anio;
    private String codigoActual;
    private boolean bloqueaAnio;
    private List<Registro> listaequivalenteSigVig;
    
    
   

	/**
     * Variable para habilitar la visibilidad del boton de imprimir
     * factura electronica
     */
    private boolean visibleActualizarProyecto;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    

    public ReferenciaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.REFERENCIA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ReferenciaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        cargarListacbAnio();
        cargarListaAnio();
    }

    @Override
    public void iniciarListasSub() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void iniciarListasSubNulo() {
        // METODO_NO_IMPLEMENTADO
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.REFERENCIAS;
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    public void cargarListacbAnio() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listacbAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReferenciaControladorUrlEnum.URL2544
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaAnio() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReferenciaControladorUrlEnum.URL2975
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    
    public void cargarlistaequivalenteSigVig() {
   	 Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        int anonom = Integer.parseInt(registro.getCampos().get("ANO").toString());
        
        param.put(GeneralParameterEnum.ANO.getName(),
       		 anonom + 1);
      
       try {
       	listaequivalenteSigVig = RegistroConverter.toListRegistro(
                           requestManager.getList(UrlServiceUtil.getInstance()
                                           .getUrlServiceByUrlByEnumID(
                                        		   ReferenciaControladorUrlEnum.URL13050
                                                                           .getValue())
                                           .getUrl(), param));
       }
       catch (SystemException e) {
           logger.error(e.getMessage(), e);
           JsfUtil.agregarMensajeError(e.getMessage());
       }

   }

    public void oprimirContables() {
        // <CODIGO_DESARROLLADO>
        if (validarVacios()) {
            return;
        }

        int mesInicio = Integer.parseInt(mesInicial);
        int mesFin = Integer.parseInt(mesFinal);
        if (mesInicio > mesFin) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB26"));
            return;
        }
        String[] campos = { "rid", "codReferencia", "nombreReferencia",
                            "mesInicialQr", "mesFinalQr", "anoQr",
                            "formulario" };
        Object[] valores = { css, (String) registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()),
                             (String) registro.getCampos().get("NOMBRE"),
                             mesInicial,
                             mesFinal, registro.getCampos().get("ANO"),
                             "referencia" };
        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.SUBFORMCENTROS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarVacios() {

        if ((registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()) == null)
            || "".equals(registro.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB24"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(mesInicial)
            || SysmanFunciones.validarVariableVacio(mesFinal)
            || (registro.getCampos().get("ANO") == null)
            || "".equals(registro.getCampos().get("ANO"))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB25"));
            return true;
        }
        return false;
    }

    public void oprimirPresupuestales() {
        // <CODIGO_DESARROLLADO>
        if (validarVacios()) {
            return;
        }
        int mesInicio = Integer.parseInt(mesInicial);
        int mesFin = Integer.parseInt(mesFinal);
        if (mesInicio > mesFin) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB26"));
            return;
        }
        String[] campos = { "rid", "codReferencia", "nombreReferencia",
                            "mesInicialQr", "mesFinalQr", "anoQr",
                            "formulario" };
        Object[] valores = { css, (String) registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()),
                             (String) registro.getCampos().get("NOMBRE"),
                             mesInicial,
                             mesFinal, registro.getCampos().get("ANO"),
                             "referencia" };
        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.SUBFORMCENTROPS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);

        // </CODIGO_DESARROLLADO>
    }
    
    public void oprimirActualizaProyectos() 
    {
    	try
        {	
    		Registro registroProyecto = new Registro(new HashMap<String, Object>());
            Map<String, Object> param = new TreeMap<>();
            String codigo = registro.getCampos().get("CODIGO").toString();
            if(codigo.indexOf("-") > 0)
            {
            	codigo = codigo.substring(0, codigo.indexOf("-"));
            }
            String nombre = registro.getCampos().get("NOMBRE").toString();
            if(nombre.indexOf("/") > 0)
            {
            	nombre = nombre.substring(0, nombre.indexOf("/"));
            }
            param.put("KEY_COMPANIA", compania);
            if(codigoActual == null)
            {	
            	param.put("KEY_CODIGO", codigo);
            }
            else
            {
            	if(codigoActual.indexOf("-") > 0)
            	{
            		codigoActual = codigoActual.substring(0, codigoActual.indexOf("-"));
            	}
            	param.put("KEY_CODIGO", codigoActual);
            }
            
            Registro rs = RegistroConverter
                    .toRegistro(requestManager.get(
                                    UrlServiceUtil.getInstance()
                                                    .getUrlServiceByUrlByEnumID(
                                                    		GenericUrlEnum.PROYECTOS
                                                            .getReadKey())
                                                    .getUrl(),
                                    param));
            if(rs == null)
            {
            	registroProyecto.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
            	registroProyecto.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        codigo);
            	registroProyecto.getCampos().put("CODIGOBPIM",
                        codigo);
            	registroProyecto.getCampos().put("NOMBREPROYECTO",
                        nombre);
            	registroProyecto.getCampos().put("VIGENCIAINICIO",
                        anio);
            	registroProyecto.getCampos().put("VIGENCIAFIN",
                        anio);
            	registroProyecto.getCampos().put(
                        GeneralParameterEnum.CREATED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
            	registroProyecto.getCampos().put(
		                        GeneralParameterEnum.DATE_CREATED.getName(),
		                        new Date());	
            	
            	UrlBean urlCreate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.PROYECTOS
                                                        .getCreateKey());

            	
            	requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
		        		registroProyecto.getCampos());
		        
		        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
            else
            {   
            	Map<String, Object> parametros = new HashMap<>(); 
            	parametros.put("CODIGO", codigo);
            	parametros.put("KEY_CODIGO", codigoActual);
            	parametros.put("CODIGOBPIM", codigo);
				parametros.put("KEY_COMPANIA", compania);
				parametros.put("NOMBREPROYECTO",nombre);
				parametros.put("VIGENCIAINICIO",anio);
				parametros.put("VIGENCIAFIN",anio);
				parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
				Parameter parameter = new Parameter();
	
				parameter.setFields(parametros);
				UrlBean urlUpdate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(ReferenciaControladorUrlEnum.URL2976.getValue());
				int conteo = requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
				
				if (conteo > 0)
		        {
		            JsfUtil.agregarMensajeInformativo(
		                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
		        }
				codigoActual = registro.getCampos().get("CODIGO").toString();
            }            
		}
    	catch (SystemException e)
        {
    		codigoActual = registro.getCampos().get("CODIGO").toString();
    		logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }    	
    }

    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        anio = registro.getCampos().get("ANO").toString();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMovimiento() {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /*
     */
    @Override
    public void abrirFormulario() {
    	// 7711609 mperez - Permite verificar si se debe mostrar el botón Actualizar a Proyectos
    	try {
			visibleActualizarProyecto = "SI".equals(SysmanFunciones
				.nvl(ejbSysmanUtil.consultarParametro(compania, "PROYECTO COMO AUXILIAR DE ALMACEN SIN BANCO DE PROYECTOS",
					SessionUtil.getModulo(), new Date(), true), "NO"));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        codigoActual = registro.getCampos().get("CODIGO").toString();
        if (registro.getCampos().get("ANO") != null) {
            anio = registro.getCampos().get("ANO").toString();
        }

        if ("m".equals(accion)) {
            bloqueaAnio = true;
        }
        else {
            bloqueaAnio = false;
        }
        
        cargarlistaequivalenteSigVig();

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
    public boolean eliminarAntes() 
    {
    	 if(visibleActualizarProyecto)
    	 {
    		 try
             {
                 Registro registroProyecto = new Registro(new HashMap<String, Object>());
                 String codigo = registro.getCampos().get("CODIGO").toString();
                 if(codigo.indexOf("-") > 0)
                 {
                	 codigo = codigo.substring(0, codigo.indexOf("-"));
                 }
                 registroProyecto.getLlave().put("KEY_COMPANIA", compania);
             	 registroProyecto.getLlave().put("KEY_CODIGO", codigo);
                 UrlBean urlDelete = UrlServiceUtil.getInstance()
                                 .getUrlServiceByUrlByEnumID(
                                                 GenericUrlEnum.PROYECTOS
                                                                 .getDeleteKey());
                 int conteo = requestManager.delete(urlDelete.getUrl(), registroProyecto.getLlave());

                 if (conteo > 0)
                 {
                     JsfUtil.agregarMensajeInformativo(
                                     idioma.getString("MSM_REGISTRO_ELIMINADO"));
                 }             
             }
             catch (SystemException ex)
             {
                 logger.error(ex.getMessage(), ex);
                 JsfUtil.agregarMensajeError(ex.getMessage());
             } 
    	 }
    	
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public List<Registro> getListacbAnio() {
        return listacbAnio;
    }

    public void setListacbAnio(List<Registro> listacbAnio) {
        this.listacbAnio = listacbAnio;
    }

    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public boolean isBloqueaAnio() {
        return bloqueaAnio;
    }

    public void setBloqueaAnio(boolean bloqueaAnio) {
        this.bloqueaAnio = bloqueaAnio;
    }

	/**
	 * @return the visibleActualizarProyecto
	 */
	public boolean isVisibleActualizarProyecto() {
		return visibleActualizarProyecto;
	}

	/**
	 * @param visibleActualizarProyecto the visibleActualizarProyecto to set
	 */
	public void setVisibleActualizarProyecto(boolean visibleActualizarProyecto) {
		this.visibleActualizarProyecto = visibleActualizarProyecto;
	}

	/**
	 * @return the codigoActual
	 */
	public String getCodigoActual() {
		return codigoActual;
	}

	/**
	 * @param codigoActual the codigoActual to set
	 */
	public void setCodigoActual(String codigoActual) {
		this.codigoActual = codigoActual;
	}
	
	 public List<Registro> getListaequivalenteSigVig() {
			return listaequivalenteSigVig;
		}

	public void setListaequivalenteSigVig(List<Registro> listaequivalenteSigVig) {
			this.listaequivalenteSigVig = listaequivalenteSigVig;
		}

}
