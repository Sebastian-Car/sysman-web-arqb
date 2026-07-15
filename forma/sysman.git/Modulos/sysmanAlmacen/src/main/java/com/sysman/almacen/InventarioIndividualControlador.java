/*-
 * InventarioIndividualControlador.java
 *
 * 1.0
 *
 * 26/01/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.InventarioIndividualControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que tiene la funcionalidad de generar el reporte de
 * Inventario Individual de Almacen
 *
 * @version 1.0, 26/01/2017
 * @author jrodriguezr
 * 
 * @author eamaya
 * @version 2, 02/05/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 */
@ManagedBean
@ViewScoped

public class InventarioIndividualControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el valor en caracteres
     * del reporte 1375
     */
    private final String consReporte;
    /**
     * Constante a nivel de clase que almacena el valor del nombre del
     * parametro FORMATO INVENTARIO INDIVIDUAL en caracteres
     */
    private final String consFormatoInv;

    /**
     * Constante a nivel de clase que almacena el valor de
     * "899999717-1" en caracteres
     */
    private final String consNitDefensa;
    // <DECLARAR_ATRIBUTOS>
    /**
     * variable que alcena si el reporte lleva o no firma del
     * ordenador de almacen
     */
    private boolean conFirma;
    /**
     * responsable seleccionado para generar el reporte
     */
    private String responsable;
    /**
     * Observaciones a ser agregadas al reporte
     */
    private String observaciones;
    /**
     * fecha necesaria para el filtro del reporte
     */
    private Date fecha;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * variable que alcena si el reporte lleva o no firma del
     * ordenador de almacen
     */
    private boolean fechaFinalVisible;
    
    /**
     * variable que almacena si el reporte maneja ubicacion
     */
    private boolean manejaUbicacion;
    
    /**
     * variable que almacena si el reporte maneja el campo ubicación fisica 
     */
    private boolean manejaUbicacionFisica;    
    
    /**
     * variable que almacena si el reporte maneja valor libros
     */
    private boolean manejaVlrLibros;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que permite seleccionnar a un responsable
     */
    private RegistroDataModelImpl listaResponsable;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InventarioIndividualControlador
     */

    @EJB

    private EjbSysmanUtilRemote ejbParametro;
	private Map<String, Object> param;
	private String numeroDoc;
	private Registro registroAux;

    public InventarioIndividualControlador() {
        super();
        compania = SessionUtil.getCompania();
        consReporte = "001375INVINDIVIDUALCORT";
        consFormatoInv = "FORMATO INVENTARIO INDIVIDUAL";
        consNitDefensa = "899999717-1";
        try {
            numFormulario = GeneralCodigoFormaEnum.INVENTARIO_INDIVIDUAL_CONTROLADOR
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
        cargarListaResponsable();
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
        String defensaCivil = null;
        try {
            defensaCivil = ejbParametro.consultarParametro(compania,
                            "DEFENSA CIVIL MANEJA NUEVAS COLUMNAS",
                            SessionUtil.getModulo(), new Date(), false);
            
            manejaUbicacion = "SI".equals(SysmanFunciones.nvl(
            					ejbParametro.consultarParametro(compania,
            							"MANEJA DEPENDENCIA-UBICACION",
            							SessionUtil.getModulo(), new Date(),true), "NO"));
            
            manejaVlrLibros = "SI".equals(SysmanFunciones.nvl(
            					ejbParametro.consultarParametro(compania,
            							"GENERAR INFORMES DE DEVOLUTIVOS CON VALOR EN LIBROS",
            							SessionUtil.getModulo(),new Date(),true),"NO"));
            
            manejaUbicacionFisica = "SI".equals(SysmanFunciones.nvl(
            					ejbParametro.consultarParametro(compania,
            							"MANEJA UBICACION FISICA EN INVENTARIO INDIVIDUAL",
            							SessionUtil.getModulo(), new Date(),true), "NO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (consNitDefensa.equals(SessionUtil.getCompaniaIngreso().getNit())
            && "SI".equals(defensaCivil)) {
            fechaFinalVisible = true;
        }
        else {
            fechaFinalVisible = false;
        }
        
        if("2109010104".equals(SessionUtil.getMenuActual())) {
          	try {
          	 registroAux = (Registro) listaResponsable.getRegistroUnico(param);
          	responsable = SysmanFunciones.nvl(registroAux.getCampos().get("CEDULA").toString(),"").toString();
          	// verCodigo = true;
  			} catch (SystemException e) {
  				  logger.error(e.getMessage(), e);
  		            JsfUtil.agregarMensajeError(e.getMessage());
  			}
          
          }else {
    	//  verCodigo = false;
          }
        if("10040338".equals(SessionUtil.getMenuActual())) {
        	
        	 fechaFinalVisible=true;
    	
            }else {
          	  
           fechaFinalVisible=false;
            }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaResponsable
     *
     */
    public void cargarListaResponsable() {
    	
    	 param = new TreeMap<>();
     	String urlEnumId;
     	
     	if("2109010104".equals(SessionUtil.getMenuActual())) {
     		

            numeroDoc = SessionUtil.getUser().getCedula();
            
            param.put(GeneralParameterEnum.DOCNUM.getName(), numeroDoc);

    		urlEnumId = InventarioIndividualControladorUrlEnum.URL2855.getValue();
    		
    	}else {
    		
    		urlEnumId = InventarioIndividualControladorUrlEnum.URL6302.getValue();
    	       
        }
    	

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CEDULA");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista genera el
     * reporte en PDF
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista genera
     * el reporte en excel
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generaInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generaInforme(FORMATOS formato) {
        archivoDescarga = null;
        try {
            String reporte;
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("responsable", responsable);

            String defensaCivil = ejbParametro.consultarParametro(compania,
                            "DEFENSA CIVIL MANEJA NUEVAS COLUMNAS",
                            SessionUtil.getModulo(), new Date(), false);

            String formatoIndiv = ejbParametro.consultarParametro(compania,
                            consFormatoInv, SessionUtil.getModulo(), new Date(),
                            false);

            String nombreAdmin = ejbParametro.consultarParametro(compania,
                            "NOMBRE ADMINISTRADOR Y FINANZAS",
                            SessionUtil.getModulo(), new Date(), false);

            String cargoAdmin = ejbParametro.consultarParametro(compania,
                            "SUBDIRECCION ADMINISTRATIVA Y FINANCIERA",
                            SessionUtil.getModulo(), new Date(), false);

            String cargoCoordinador = ejbParametro.consultarParametro(compania,
                            "CARGO COORDINADOR ALMACEN",
                            SessionUtil.getModulo(), new Date(), false);

            String firmaOrdenador = ejbParametro.consultarParametro(compania,
                            "ORDENADOR ALMACEN", SessionUtil.getModulo(),
                            new Date(), false);

            String coordinadorAlmacen = ejbParametro.consultarParametro(
                            compania, "COORDINADOR ALMACEN",
                            SessionUtil.getModulo(), new Date(), false);

            String clausula = ejbParametro.consultarParametro(compania,
                            "CLAUSULA INFORME INVENTARIO INDIVIDUAL",
                            SessionUtil.getModulo(), new Date(), false);
            
            String cargoAlmacenista = ejbParametro.consultarParametro(compania, 
            				"CARGO ALMACENISTA", 
            				SessionUtil.getModulo(),new Date(), false);

            if (consNitDefensa.equals(SessionUtil.getCompaniaIngreso().getNit())
                && "SI".equals(defensaCivil) && (fecha == null)) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("MSM_DEBE_FECHA_INI"));
                return;
            }
            else if (consNitDefensa
                            .equals(SessionUtil.getCompaniaIngreso().getNit())
                && "SI".equals(defensaCivil)) {
                cargarParametrosReporte1374(parametros, formatoIndiv);
                reporte = "001374CInvIndivDevoluRespon2";
                reemplazar.put("fecha",
                                SysmanFunciones.convertirAFechaCadena(fecha));
                parametros.put("PR_FECHAFINAL",
                                SysmanFunciones.convertirAFechaCadena(fecha,
                                                "dd ' DE ' MMMM ' DE ' yyyy ")
                                                .toUpperCase());
            }
            else if (consReporte.equals(SysmanFunciones
                            .nvlStr(formatoIndiv, consReporte))) {
                reporte = manejaUbicacionFisica ? "002958INVINDIVIDUALCORTCONUBI" : consReporte;

                cargarParametrosInvindCort(parametros);
            }
            else {
            	if("10040338".equals(SessionUtil.getMenuActual())) {
            		 reporte = "002249CInvIndivDevoluResponsableFecha";
            		 reemplazar.put("fecha",
                             SysmanFunciones.convertirAFechaCadena(fecha));
            		   parametros.put("PR_MOSTAR", conFirma);
                       parametros.put("PR_ORDENADOR_ALMACEN", firmaOrdenador);
                       parametros.put("PR_SUBDIRECCION_ADMINISTRATIVA_Y_FINANCIERA",
                                       cargoAdmin);
                       parametros.put("PR_NOMBRE_ADMINISTRADOR_Y_FINANZAS",
                                       nombreAdmin);
                       parametros.put("PR_CLAUSULA_INFORME_INVENTARIO_INDIVIDUAL",
                                       clausula);
                       parametros.put("PR_COORDINADOR_ALMACEN", coordinadorAlmacen);
                       parametros.put("PR_CARGO_COORDINADOR_ALMACEN",
                                       cargoCoordinador);
                       
                   }else {
                	   if (formatoIndiv != null 
                			   && !formatoIndiv.trim().isEmpty() 
                			   && !formatoIndiv.equalsIgnoreCase("FORMATO INVENTARIO INDIVIDUAL")
                			   && !conFirma) {
                		   reporte = formatoIndiv.trim();
                		    
                	   } else {
                		   
                		   reporte = manejaUbicacion 		? "002611INVENTARIOINDIVIDUAL"
                				   	:manejaUbicacionFisica 	? "002955CInvIndivDevoluResponConUbi"
                				   							: "000012CInvIndivDevoluRespon";
                	                 	   
                   		 
                	   if(manejaVlrLibros) {
                			   reporte = "002727CInvIndivDevoluResponFl";
                			   reemplazar.put("fechaCorte",SysmanFunciones.formatearFecha(new Date()));
                		   	}
                	   }
                       parametros.put("PR_MOSTAR", conFirma);
                       parametros.put("PR_ORDENADOR_ALMACEN", firmaOrdenador);
                       parametros.put("PR_SUBDIRECCION_ADMINISTRATIVA_Y_FINANCIERA",
                                       cargoAdmin);
                       parametros.put("PR_NOMBRE_ADMINISTRADOR_Y_FINANZAS",
                                       nombreAdmin);
                       parametros.put("PR_CLAUSULA_INFORME_INVENTARIO_INDIVIDUAL",
                                       clausula);
                       parametros.put("PR_COORDINADOR_ALMACEN", coordinadorAlmacen);
                       parametros.put("PR_CARGO_COORDINADOR_ALMACEN",
                                       cargoCoordinador);
                       parametros.put("PR_CARGO_ALMACENISTA", cargoAlmacenista);
                   }
            	
            	
              
            }
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            boolean verSupervisor = SysmanFunciones.nvl(ejbParametro.consultarParametro(compania,
    				"MANEJA RESPONSABLE Y CONTRATISTA EN DEVOLUTIVOS", SessionUtil.getModulo(), 
    				new Date(), false), "NO").equals("SI");
            
            parametros.put("PR_NOMBREORDENADOR", nombreAdmin);
            parametros.put("PR_CARGOORDENADOR", cargoAdmin);
            parametros.put("PR_FIRMAORDENADOR", firmaOrdenador);
            parametros.put("PR_COORDINADORALMACEN", coordinadorAlmacen);
            parametros.put("PR_CARGOCOORDINADOR", cargoCoordinador);
            parametros.put("PR_OBSERVACIONES", observaciones);
            parametros.put("PR_VER_SUPERVISOR", verSupervisor);

            
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (OutOfMemoryError | JRException | IOException | ParseException
                        | SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void cargarParametrosInvindCort(
        Map<String, Object> parametros) {
        try {
            String responsabilidadUno = ejbParametro.consultarParametro(
                            compania, "RESPONSABILIDAD I INV INDIVIDUAL",
                            SessionUtil.getModulo(), new Date(), false);

            String responsabilidadDos = ejbParametro.consultarParametro(
                            compania, "RESPONSABILIDAD II INV INDIVIDUAL",
                            SessionUtil.getModulo(), new Date(), false);

            parametros.put("PR_RESPONSABILIDAD_I", responsabilidadUno);
            parametros.put("PR_RESPONSABILIDAD_II", responsabilidadDos);
            parametros.put("PR_CONFIRMA", conFirma);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void cargarParametrosReporte1374(
        Map<String, Object> parametros, String formatoIndiv) {
        if (!conFirma) {
            parametros.put("PR_CONFIRMA", false);
            parametros.put("PR_CONFIRMAORD", false);
            parametros.put("PR_CONNOMCARGOORD", false);
        }
        if (consFormatoInv
                        .equals(SysmanFunciones.nvlStr(formatoIndiv,
                                        consFormatoInv))) {
            parametros.put("PR_COORDINA", true);
            if (conFirma) {
                parametros.put("PR_CONFIRMAORD", false);
                parametros.put("PR_CONNOMCARGOORD", true);
            }
        }
        else {
            parametros.put("PR_COORDINA", false);
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable el valor seleccionado se almacena en la
     * variable responsable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        responsable = registroAux.getCampos().get("CEDULA").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable fechaFinalVisible
     *
     * @return fechaFinalVisible
     */
    public boolean isFechaFinalVisible() {
        return fechaFinalVisible;
    }

    /**
     * Asigna la variable fechaFinalVisible
     *
     * @param fechaFinalVisible
     * Variable a asignar en fechaFinalVisible
     */
    public void setFechaFinalVisible(boolean fechaFinalVisible) {
        this.fechaFinalVisible = fechaFinalVisible;
    }

    /**
     * Retorna la variable conFirma
     *
     * @return conFirma
     */
    public boolean getConFirma() {
        return conFirma;
    }

    /**
     * Asigna la variable conFirma
     *
     * @param conFirma
     * Variable a asignar en conFirma
     */
    public void setConFirma(boolean conFirma) {
        this.conFirma = conFirma;
    }

    /**
     * Retorna la variable responsable
     *
     * @return responsable
     */
    public String getResponsable() {
        return responsable;
    }

    /**
     * Asigna la variable responsable
     *
     * @param responsable
     * Variable a asignar en responsable
     */
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    /**
     * Retorna la variable observaciones
     *
     * @return observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Asigna la variable observaciones
     *
     * @param observaciones
     * Variable a asignar en observaciones
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Retorna la variable fecha
     *
     * @return fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Asigna la variable fecha
     *
     * @param fecha
     * Variable a asignar en fecha
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaResponsable
     *
     * @return listaResponsable
     */
    public RegistroDataModelImpl getListaResponsable() {
        return listaResponsable;
    }

    /**
     * Asigna la lista listaResponsable
     *
     * @param listaResponsable
     * Variable a asignar en listaResponsable
     */
    public void setListaResponsable(RegistroDataModelImpl listaResponsable) {
        this.listaResponsable = listaResponsable;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

	public boolean isManejaUbicacion() {
		return manejaUbicacion;
	}

	public void setManejaUbicacion(boolean manejaUbicacion) {
		this.manejaUbicacion = manejaUbicacion;
	}
}
