package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 05/01/2016
 * @author amonroy
 * @version 1.2, 17/01/2017 --> Se realizan ajustes en el metodo agregarRegistro() adicionando el envio del consecutivo
 *
 * @version 1.3, 03/04/2017, pespitia : <br>
 * Se realizo el refactoring y se cambio el texto de los mensajes quemados por texto en bean.
 *
 * @version 2, 12/06/2017 jrodriguezr Se refactoriza el código: Se pasa el numero del formulario al enumerado, se eliminan conexiones y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class DigitalizacionContratosControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante que almacenara la cadena "ARCHIVO"
     */
    private final String archivoC;

    /**
     * Constante a nivel de clase que aloja el valor {@code REGISTRO}
     */
    private final String cRegistro;

    /**
     * Constante a nivel de clase que aloja el valor {@code COMPANIA}
     */
    private final String cCompania;

    private String claseOrden;
    private String numeroOrden;
    private StreamedContent archivoDescarga;
    private String rutafinal;
    public String menuActual;

    @EJB
    private EjbSysmanUtilRemote sysmanUtil;

	private String aseguradora;
	private String sucursal;
	private String numeroPoliza;
	private boolean poliza;
	
	private String modulo;
	private String ordenDeCompra;   
    private String novedad;         
    private String claseT;          
    private String tipoT; 
	private Map<String, Object> parametroswf;

    /**
     * Creates a new instance of DigitalizacionContratosControlador
     */
    public DigitalizacionContratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        archivoC = "ARCHIVO";
        cRegistro = "REGISTRO";
        cCompania = GeneralParameterEnum.COMPANIA.getName();

        try {
        	
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "9");
        	}
            modulo = SessionUtil.getModulo();
            
            numFormulario = GeneralCodigoFormaEnum.DIGITALIZACION_CONTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
            	aseguradora = parametrosEntrada.get("aseguradora") != null
            	        ? parametrosEntrada.get("aseguradora").toString() : "";

            	sucursal = parametrosEntrada.get("sucursal") != null
            	        ? parametrosEntrada.get("sucursal").toString() : "";

            	numeroPoliza = parametrosEntrada.get("numeroPoliza") != null
            	        ? parametrosEntrada.get("numeroPoliza").toString() : "";

            	Object polizaObj = parametrosEntrada.get("poliza");
            	poliza = polizaObj != null && Boolean.parseBoolean(polizaObj.toString());
            	
            	claseOrden = parametrosEntrada.get("claseOrden") != null
            	        ? parametrosEntrada.get("claseOrden").toString() : "";
            	    numeroOrden = parametrosEntrada.get("numeroOrden") != null
            	        ? parametrosEntrada.get("numeroOrden").toString() : "";
            	    ordenDeCompra = parametrosEntrada.get("ordenDeCompra") != null
            	        ? parametrosEntrada.get("ordenDeCompra").toString() : "";
            	    novedad = parametrosEntrada.get("novedad") != null
            	        ? parametrosEntrada.get("novedad").toString() : "";
            	    claseT = parametrosEntrada.get("claseT") != null
            	        ? parametrosEntrada.get("claseT").toString() : "";
            	    tipoT = parametrosEntrada.get("tipoT") != null
            	        ? parametrosEntrada.get("tipoT").toString() : "";
            	
            }
        }
        catch (SysmanException | NamingException ex) {
            Logger.getLogger(DigitalizacionContratosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
    	menuActual = SysmanFunciones.toString(SessionUtil.getMenuActual());
    	try {
    		if(menuActual.equals("100210")) {
    			enumBase = GenericUrlEnum.DI_POLIZAS_ACTIVOS;
    			buscarLlave();

    			rutafinal = sysmanUtil.consultarParametro(
    					compania, "RUTA DIGITALIZADO ALMACEN POLIZAS",
    					SessionUtil.getModulo(),
    					new Date(),
    					false);
    		}
    		else if(menuActual.startsWith("1002")) {    		
    			enumBase = GenericUrlEnum.DI_MOVIMIENTO;
    			buscarLlave();

    			rutafinal = sysmanUtil.consultarParametro(
    					compania, "RUTA DIGITALIZADO ALMACEN",
    					SessionUtil.getModulo(),
    					new Date(),
    					false);
    			
    		}
    		else if (menuActual.startsWith("90203") && !menuActual.equals("9020312")) {		
    			enumBase = GenericUrlEnum.DI_NOVEDADES;
    			buscarLlave();

    			rutafinal = sysmanUtil.consultarParametro(
    					compania, "RUTA DIGITALIZADO CONTRATOS",
    					SessionUtil.getModulo(),
    					new Date(),
    					false);
    		}else {
    			enumBase = GenericUrlEnum.DI_CONTRATO;
    			buscarLlave();

    			rutafinal = sysmanUtil.consultarParametro(
    					compania, "RUTA DIGITALIZADO CONTRATOS", 
    					SessionUtil.getModulo(),
    					new Date(),
    					false);

    		}
    	}
    	catch (SystemException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
        if(!poliza) {
    	claseOrden = JsfUtil.getParametros().get("claseOrden");
    	numeroOrden = JsfUtil.getParametros().get("numeroOrden");

          if (menuActual.startsWith("90203") && !menuActual.equals("9020312")) {

    	    ordenDeCompra = JsfUtil.getParametros().get("ordenDeCompra");  
    	    novedad = JsfUtil.getParametros().get("novedad");              
    	    claseT = JsfUtil.getParametros().get("claseT");                
    	    tipoT = JsfUtil.getParametros().get("tipoT");                  
    	}
    	
        }
    	reasignarOrigen();
    	registro = new Registro(new HashMap<String, Object>());
    	abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
    	buscarUrls();
    	parametrosListado.put(cCompania, compania);
    	if(poliza) {
    		parametrosListado.put("ASEGURADORA", aseguradora);
    		parametrosListado.put("SUCURSAL", sucursal);
    		parametrosListado.put("NUMERO_POLIZA", numeroPoliza);
    	}
    	
    	else if (menuActual.startsWith("90203") && !menuActual.equals("9020312")) {
            parametrosListado.put("CLASEORDEN", claseOrden);
            parametrosListado.put("NUMERO", numeroOrden);          
            parametrosListado.put("ORDENDECOMPRA", ordenDeCompra);
            parametrosListado.put("NOVEDAD", novedad);
            parametrosListado.put("CLASET", claseT);
            parametrosListado.put("TIPOT", tipoT);
        }

    	else {
    		parametrosListado.put("CLASEORDEN", claseOrden);
    		parametrosListado.put(cRegistro, numeroOrden);
    	}
    }

    public String getClaseOrden() {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden) {
        this.claseOrden = claseOrden;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void oprimirDescargar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        FileInputStream inputStream = null;

        /*-El try con recursos genera un archivo corrupto, se debe verificar.*/
        try {
            String documento;

            // Determina que campo usar según el tipo de menu
            if (poliza) {
                documento = reg.getCampos().get("NUMERO_POLIZA").toString();
            } else if (menuActual.startsWith("90203") && !menuActual.equals("9020312")) {

                // Para novedades usar el campo NUMERO
                documento = reg.getCampos().get("NUMERO").toString();
            } else {
                // Para otros casos usar REGISTRO
                documento = reg.getCampos().get(cRegistro).toString();
            }
            
            File file = new File(generarRuta(rutafinal, documento)
                            + reg.getCampos().get(archivoC).toString());
            
            inputStream = new FileInputStream(file);
            
            archivoDescarga = JsfUtil.getArchivoDescarga(inputStream,
                            reg.getCampos().get(archivoC).toString());
        }

        catch (JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException e1) {
                logger.error(e1.getMessage(), e1);
                JsfUtil.agregarMensajeError(e1.getMessage());
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cargarArchivoSubirContrato(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        try {
            // <CODIGO_DESARROLLADO>
            String nombreArch = event.getFile().getFileName();
            nombreArch = nombreArch.contains(File.separator)
                            ? nombreArch.substring(
                                            nombreArch.lastIndexOf(File.separator) + 1,
                                            nombreArch.length())
                            : nombreArch;
            String ruta = generarRuta(rutafinal, poliza ? numeroPoliza : numeroOrden);
            if (ruta != null) {
                JsfUtil.upload(event.getFile().getInputstream(), nombreArch,
                                ruta);
                registro.getCampos().put(archivoC, nombreArch);
                agregarRegistro();
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3042")
                                .replace("#PARAMETER#", rutafinal));
            }
            // </CODIGO_DESARROLLADO>
        }
        catch (IOException ex) {
            Logger.getLogger(DigitalizacionContratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    public String generarRuta(String parametro, String documento) {
        String strRuta;
        String placa = documento.replace(" ", "");
        if (parametro != null) {
            strRuta = parametro + placa + File.separator;
            File folder = new File(strRuta);
            folder.mkdirs();
            return strRuta;
        }
        else {
            return null;
        }
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
        registro.getCampos().put(cCompania, compania);
        registro.getCampos().put("CREATED_BY",
                        SessionUtil.getUser().getCodigo());
        registro.getCampos().put("DATE_CREATED", new Date());
        actualizarAntes();
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
        registro.getCampos().put("MODIFIED_BY",
                        SessionUtil.getUser().getCodigo());
        registro.getCampos().put("DATE_MODIFIED", new Date());
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
    	if(menuActual.startsWith("1002")) { 
    		registro.getLlave().put("KEY_NUMERO_MOVIMIENTO", numeroOrden);
    		registro.getLlave().put("KEY_TIPO_MOVIMIENTO", claseOrden);
    	}
    	
    	if (menuActual.startsWith("90203") && !menuActual.equals("9020312")) {

    		registro.getLlave().put("KEY_COMPANIA", compania);
            registro.getLlave().put("KEY_CLASEORDEN", claseOrden);
            registro.getLlave().put("KEY_NUMERO", numeroOrden);
            registro.getLlave().put("KEY_ORDENDECOMPRA", ordenDeCompra);
            registro.getLlave().put("KEY_NOVEDAD", novedad);
            registro.getLlave().put("KEY_CLASET", claseT);
            registro.getLlave().put("KEY_TIPOT", tipoT);
            registro.getLlave().put("KEY_CONSECUTIVO", registro.getCampos().get("CONSECUTIVO"));
    	}
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
    	if(menuActual.startsWith("1002")) { 
    		registro.getLlave().remove("KEY_NUMERO_MOVIMIENTO");
    		registro.getLlave().remove("KEY_TIPO_MOVIMIENTO");
    	}
    	
    	if (menuActual.startsWith("90203") && !menuActual.equals("9020312")) {

    		registro.getLlave().remove("KEY_COMPANIA");
            registro.getLlave().remove("KEY_CLASEORDEN");
            registro.getLlave().remove("KEY_NUMERO");
            registro.getLlave().remove("KEY_ORDENDECOMPRA");
            registro.getLlave().remove("KEY_NOVEDAD");
            registro.getLlave().remove("KEY_CLASET");
            registro.getLlave().remove("KEY_TIPOT");
            registro.getLlave().remove("KEY_CONSECUTIVO");
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    private void agregarRegistro() {
    	try {
    		if(menuActual.equals("100210")) {
    			StringBuilder builder = new StringBuilder();
    			builder.append("COMPANIA = ''").append(compania)
    			.append("'' AND ASEGURADORA = ''").append(aseguradora)
    			.append("'' AND SUCURSAL = ''").append(sucursal)
    			.append("'' AND NUMERO_POLIZA = ''").append(numeroPoliza).append("''");
    			long consecutivo = sysmanUtil.generarConsecutivoConValorInicial(
    					"DI_POLIZAS_ACTIVOS", builder.toString(), "CONSECUTIVO",
    					"1");
    			registro.getCampos().put(cCompania, compania);
    			registro.getCampos().put("CONSECUTIVO", consecutivo);
    			registro.getCampos().put("ASEGURADORA", aseguradora);
    			registro.getCampos().put("SUCURSAL", sucursal);
    			registro.getCampos().put("NUMERO_POLIZA", numeroPoliza);
    			registro.getCampos().put("CREATED_BY",
    					SessionUtil.getUser().getCodigo());
    			registro.getCampos().put("DATE_CREATED", new Date());

    			UrlBean urlCreate = UrlServiceUtil.getInstance()
    					.getUrlServiceByUrlByEnumID(
    							GenericUrlEnum.DI_POLIZAS_ACTIVOS
    							.getCreateKey());

    			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
    					registro.getCampos());

    			reasignarOrigen();
    		}
    		else if(menuActual.startsWith("1002")) {    		

    			StringBuilder builder = new StringBuilder();
    			builder.append("COMPANIA = ''").append(compania)
    			.append("'' AND NUMERO_MOVIMIENTO = ").append(numeroOrden);
    			long consecutivo = sysmanUtil.generarConsecutivoConValorInicial(
    					"DI_MOVIMIENTO", builder.toString(), "CONSECUTIVO",
    					"1");
    			registro.getCampos().put(cCompania, compania);
    			registro.getCampos().put("NUMERO_MOVIMIENTO", numeroOrden);
    			registro.getCampos().put("CONSECUTIVO", consecutivo);
    			registro.getCampos().put("TIPO_MOVIMIENTO", claseOrden);
    			registro.getCampos().put("CREATED_BY",
    					SessionUtil.getUser().getCodigo());
    			registro.getCampos().put("DATE_CREATED", new Date());

    			UrlBean urlCreate = UrlServiceUtil.getInstance()
    					.getUrlServiceByUrlByEnumID(
    							GenericUrlEnum.DI_MOVIMIENTO
    							.getCreateKey());

    			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
    					registro.getCampos());

    			reasignarOrigen();
			
    		}else if (menuActual.startsWith("90203") && !menuActual.equals("9020312")) { 		
                //  GENERAR CONSECUTIVO CON TODAS LAS LLAVES PRIMARIAS
                StringBuilder builder = new StringBuilder();
                builder.append("COMPANIA = ''").append(compania)
                .append("'' AND CLASEORDEN = ''").append(claseOrden)
                .append("'' AND NUMERO = ").append(numeroOrden)
                .append(" AND ORDENDECOMPRA = ").append(ordenDeCompra)
                .append(" AND NOVEDAD = ").append(novedad)
                .append(" AND CLASET = ''").append(claseT)
                .append("'' AND TIPOT = ''").append(tipoT).append("''");
                
                long consecutivo = sysmanUtil.generarConsecutivoConValorInicial(
                        "DI_NOVEDADES", builder.toString(), "CONSECUTIVO",
                        "1");
                
                //  GUARDAR TODAS LAS LLAVES PRIMARIAS + CONSECUTIVO
                registro.getCampos().put(cCompania, compania);
                registro.getCampos().put("CLASEORDEN", claseOrden);
                registro.getCampos().put("NUMERO", numeroOrden);
                registro.getCampos().put("ORDENDECOMPRA", ordenDeCompra);
                registro.getCampos().put("NOVEDAD", novedad);
                registro.getCampos().put("CLASET", claseT);
                registro.getCampos().put("TIPOT", tipoT);
                registro.getCampos().put("ANO", new Date().getYear() + 1900);
                registro.getCampos().put("CONSECUTIVO", consecutivo);
                registro.getCampos().put("CREATED_BY",
                        SessionUtil.getUser().getCodigo());
                registro.getCampos().put("DATE_CREATED", new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                GenericUrlEnum.DI_NOVEDADES
                                .getCreateKey());

                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                        registro.getCampos());

                reasignarOrigen();  			
    		}else {
    			try {
    				StringBuilder builder = new StringBuilder();
    				builder.append("COMPANIA = ''").append(compania)
    				.append("'' AND REGISTRO = ").append(numeroOrden);
    				long consecutivo = sysmanUtil.generarConsecutivoConValorInicial(
    						"DI_CONTRATO", builder.toString(), "CONSECUTIVO",
    						"1");
    				registro.getCampos().put(cCompania, compania);
    				registro.getCampos().put(cRegistro, numeroOrden);
    				registro.getCampos().put("CONSECUTIVO", consecutivo);
    				registro.getCampos().put("CLASEORDEN", claseOrden);
    				registro.getCampos().put("CREATED_BY",
    						SessionUtil.getUser().getCodigo());
    				registro.getCampos().put("DATE_CREATED", new Date());

    				UrlBean urlCreate = UrlServiceUtil.getInstance()
    						.getUrlServiceByUrlByEnumID(
    								GenericUrlEnum.DI_CONTRATO
    								.getCreateKey());

    				requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
    						registro.getCampos());

    				reasignarOrigen();
    			}
    			catch (SystemException ex) {
    				Logger.getLogger(DigitalizacionContratosControlador.class
    						.getName()).log(Level.SEVERE, null, ex);
    				JsfUtil.agregarMensajeError(ex.getMessage());
    			}
    			finally {
    				registro = new Registro(new HashMap<String, Object>());
    			}
    		}

    	}
    	catch (SystemException ex) {
    		Logger.getLogger(DigitalizacionContratosControlador.class
    				.getName()).log(Level.SEVERE, null, ex);
    		JsfUtil.agregarMensajeError(ex.getMessage());
    	}
    	finally {
    		registro = new Registro(new HashMap<String, Object>());
    	}

    }

    @Override
    public void removerCombos() {
        // Metodo que se hereda desde el bean base

    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo que se hereda desde el bean base

    }

}
