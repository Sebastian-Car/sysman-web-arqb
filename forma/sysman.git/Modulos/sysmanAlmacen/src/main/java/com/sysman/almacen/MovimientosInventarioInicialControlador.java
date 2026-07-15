/*-
 * MovimientosInventarioInicial.java
 *
 * 1.0
 * 
 * 19/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCuatroRemote;
import com.sysman.almacen.enums.MovimientosInventarioInicialControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

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
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Formulario que permite generar todos los comprobantes del
 * inventario inicial.
 *
 * @version 1.0, 19/04/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class MovimientosInventarioInicialControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private boolean visibleDialogo;
    private String etiquetaMensaje;
    private String tituloMensajes;
    private StringBuilder comprobantes;
    private String nombreProceso;
    private boolean bloqProceso;
    private Object idPR;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    
    private static final String INICIADO = "INICIADO";

    @EJB
    private EjbAlmacenCuatroRemote ejbAlmacenCuatro;

    /**
     * Crea una nueva instancia de MovimientosInventarioInicial
     */
    public MovimientosInventarioInicialControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.MOVIMIENTOS_INVENTARIOINICIAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    	nombreProceso = "Movimientos inventario inical";
    	mensajesInicioModal();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BT3083 en la vista
     *
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        if (verificarExisteDevolutivo()) {
            etiquetaMensaje = idioma.getString("TB_TB4068").replace(
                            "s#comprobantes#s", comprobantes.toString());
            visibleDialogo = true;
        }
        else {
            generarMovimientos();
        }

        // </CODIGO_DESARROLLADO>
    }

    public void generarMovimientos() {
        
    	boolean error = false;
    	
    	try {
            
        	if(consultarEstado(nombreProceso)) {
        		return;
        	}
        	
        	agregarControl(nombreProceso);
        	Map<String, Object> param = new HashMap<>();
    		param.put(GeneralParameterEnum.ESTADO.getName(), INICIADO);
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.NOM_PROCESO.getName(), nombreProceso.toUpperCase());

    		Registro rs = RegistroConverter
    				.toRegistro(requestManager.get(
    						UrlServiceUtil.getInstance()
    						.getUrlServiceByUrlByEnumID(
    								MovimientosInventarioInicialControladorUrlEnum.URL1984001.getValue())
    						.getUrl(),
    						param));
    		idPR = rs.getCampos().get("ID_PROCESO");
    		
        	String respuesta = ejbAlmacenCuatro.generarMovInventarioInicial(
                            compania, "ODI",
                            SessionUtil.getUser().getCodigo());

            if ("-1".equals(respuesta)) {
            	error = true;
            	String[] campos = { "claseOrden" };
                String[] valores = { "ODI" };
                SessionUtil.cargarModalDatosFlash(
                                String.valueOf(GeneralCodigoFormaEnum.ERRORALMACENINVENTARIOINICIAL_CONTROLADOR
                                                .getCodigo()),
                                SessionUtil.getModulo(), campos, valores);
            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB4069"));
            }

        }
        catch (SystemException e) {
        	error = true;
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }finally {

        	UrlBean updateEstado = UrlServiceUtil.getInstance()
        			.getUrlServiceByUrlByEnumID(
        					MovimientosInventarioInicialControladorUrlEnum.URL1984002.getValue());

        	Map<String, Object> parametros = new HashMap<>();
        	parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        	parametros.put(GeneralParameterEnum.NOM_PROCESO.getName(), nombreProceso.toUpperCase());
        	parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
        	parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
        	parametros.put(GeneralParameterEnum.NO_PROCESO.getName(), idPR);
        	parametros.put(GeneralParameterEnum.ESTADO.getName(),error ? "ERRORES" : "FINALIZADO");

        	Parameter parameter = new Parameter();
        	parameter.setFields(parametros);

        	try {
        		requestManager.update(updateEstado.getUrl(),
        				updateEstado.getMetodo(),parameter);
        	} catch (SystemException e) {
        		logger.error(e.getMessage(), e);
        	}

        }
    }

    public void aceptarDialogo() {
        visibleDialogo = false;
        generarMovimientos();
    }

    /**
     * metodo que verifica si el comprobante inicial ya teine
     * comprobante de almacen relacionado, si es asi retorna verdadeo
     * y modifica lel atributo permiteModificar a falso con el fin de
     * realizar las validaciones graficas
     */
    private boolean verificarExisteDevolutivo() {
        boolean retorno = false;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ORIGEN", "EDI,ECI");
        try {
            List<Registro> lista = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MovimientosInventarioInicialControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
            comprobantes = new StringBuilder();
            if (!lista.isEmpty()) {
                for (int i = 0; i < lista.size(); i++) {
                    comprobantes.append(lista.get(i).getCampos()
                                    .get("NUMEROORIGEN")).append(", \n");
                }
                retorno = true;
            }
            else {
                retorno = false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(OrdeninventarioinicialsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return retorno;

    }
    
    public boolean consultarEstado(String proceso) {
    	boolean enProceso = false;
    	try {
    		Map<String, Object> param = new HashMap<>();
    		param.put(GeneralParameterEnum.ESTADO.getName(), INICIADO);
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.NOM_PROCESO.getName(), proceso.toUpperCase());

    		Registro rs = RegistroConverter
    				.toRegistro(requestManager.get(
    						UrlServiceUtil.getInstance()
    						.getUrlServiceByUrlByEnumID(
    								MovimientosInventarioInicialControladorUrlEnum.URL1984001.getValue())
    						.getUrl(),
    						param));

    		if (rs != null)
    		{ 			
    			bloqProceso = true;
    			enProceso = true;
    			String mensaje = String.format("Proceso %s ya se encuentra en ejecución, debe esperar su finalizacin para iniciar un nuevo proceso.", proceso);
    			JsfUtil.agregarMensajeError(mensaje);
    		}
    	} catch (SystemException e) {
    		logger.error(e.getMessage(),e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
    	return enProceso;
    }
    
    public void agregarControl(String proceso) {
    	try {
    		
    		String ip = obtenerIpCliente();
    		String userName = System.getProperty("user.name");
    		String pcName = System.getenv("COMPUTERNAME"); // Windows
    		if (pcName == null) {
    		    pcName = System.getenv("HOSTNAME"); // Linux/Mac
    		}
    		Registro registroControl = new Registro();
    		registroControl.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		registroControl.getCampos().put("NOMBRE_PROCESO", proceso.toUpperCase());
    		registroControl.getCampos().put("ESTADO_EJECUCION", INICIADO);
    		registroControl.getCampos().put("FECHA_INICIO", new Date());
    		registroControl.getCampos().put("USUARIO_EJECUTOR", SessionUtil.getUser().getCodigo());
    		registroControl.getCampos().put("MAQUINA_INSTANCIA", "Usuario: " + userName + " / Equipo: " + pcName  + " / Ip: " + ip );
    		registroControl.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
    		registroControl.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
    		
    		UrlBean urlCreate = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(GenericUrlEnum.CONTROL_PROCESOS.getCreateKey());

    		requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
    				registroControl.getCampos());

    		JsfUtil.agregarMensajeInformativo(
    				idioma.getString("MSM_REGISTRO_INGRESADO"));
    	} catch (SystemException ex) {
    		logger.error(ex.getMessage(),ex);
    		JsfUtil.agregarMensajeError(ex.getMessage());
    	}
    }
    
    public String obtenerIpCliente() {
    	HttpServletRequest request = (HttpServletRequest) 
    			FacesContext.getCurrentInstance().getExternalContext().getRequest();

    	String ip = request.getHeader("X-FORWARDED-FOR");
    	if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
    		ip = request.getRemoteAddr();
    	}
    	return ip;
    }
    
    public void mensajesInicioModal() {

    	consultarEstado(nombreProceso);

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public boolean isVisibleDialogo() {
        return visibleDialogo;
    }

    public void setVisibleDialogo(boolean visibleDialogo) {
        this.visibleDialogo = visibleDialogo;
    }

    public String getEtiquetaMensaje() {
        return etiquetaMensaje;
    }

    public void setEtiquetaMensaje(String etiquetaMensaje) {
        this.etiquetaMensaje = etiquetaMensaje;
    }

    public String getTituloMensajes() {
        return tituloMensajes;
    }

    public void setTituloMensajes(String tituloMensajes) {
        this.tituloMensajes = tituloMensajes;
    }
    
    public boolean isBloqProceso()
    {
        return bloqProceso;
    }

    public void setBloqProceso(boolean bloqProceso)
    {
        this.bloqProceso = bloqProceso;
    } 
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
