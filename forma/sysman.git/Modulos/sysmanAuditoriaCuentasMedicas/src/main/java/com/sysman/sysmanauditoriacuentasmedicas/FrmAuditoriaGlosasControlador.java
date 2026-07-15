/*-
 * FrmAuditoriaGlosasControlador.java
 *
 * 1.0
 * 
 * 3/02/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.sysmanauditoriacuentasmedicas;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.TercerosControlador;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmAuditoriaGlosasControladorUrlEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que realiza la auditoria de glosas
 *
 * @version 1.0, 03/02/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmAuditoriaGlosasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>    
    /**
     * registro con el cual se podra tener acceso a los datos de los
     * detalles del movimiento
     */
    private Registro registroSub;
    
    /**
     * Lista que carga los codigos de glosa
     */
    private RegistroDataModelImpl listaCodigoGlosa;   
        
    /**
     * Lista que carga los codigos de glosas asociados a un codigo general y un codigo especifico
     */
    private RegistroDataModelImpl listaCodigoGlosas;
    
    /**
     * Lista que carga los codigos de glosas E asociados a un codigo general y un codigo especifico
     */
    private RegistroDataModelImpl listaCodigoGlosasE;
    
    /**
     * Lista que carga los registros de las glosas por factura
     */
    private List<Registro> listaSubdauditoriaglosas;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>

   

	/**
     * Atributo que contiene los diversos parametros de entrada para
     * el filtro de la informacion
     */
    private Map<String, Object> parametrosEntrada;

    /**
     * Variable que almacena el cosecutivo del formulario anterior
     */
    private String consecutivo;
    
    /**
	 * variable auxiliar
	 */
	private String auxiliar;
	/**
	 * Toma la fecha actual para realizar comparaciones con la fecha de las glosas
	 */
	private Date fecha;
	
	/**
	 * Maneja el estado del check de devolucion de factura para mostrar u ocultar campos y listas relacionados a este proceso
	 */
	private boolean devolucionFactura;
    // </DECLARAR_ADICIONALES>
	
    /**
     * Crea una nueva instancia de FrmAuditoriaGlosasControlador
     */
    public FrmAuditoriaGlosasControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();
        try {

            // 2155
            numFormulario = GeneralCodigoFormaEnum.FRM_AUDITORIA_GLOSAS
                            .getCodigo();

            if (parametrosEntrada != null) {

                consecutivo = parametrosEntrada.get("consecutivo").toString();
            }

            validarPermisos();
            registroSub = new Registro(new HashMap<String, Object>());
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoGlosa();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() 
    {
    	cargarListaCodigoGlosas();
    	cargarListaCodigoGlosasE();
    	
    	cargarListaSubdauditoriaglosas();
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.CM_AUDITORIA_GLOSAS;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCodigoGlosa
     *
     */
    public void cargarListaCodigoGlosa() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAuditoriaGlosasControladorUrlEnum.URL6485
                                                        .getValue());
        listaCodigoGlosa = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, "ID");
    }
    
    /**
	 * Carga la lista listaCodigoGlosas
	 */
	public void cargarListaCodigoGlosas() 
	{	
		if (devolucionFactura) {
		    UrlBean urlBean = UrlServiceUtil.getInstance()
		            .getUrlServiceByUrlByEnumID(
		                    FrmAuditoriaGlosasControladorUrlEnum.URL1835005.getValue());

		    listaCodigoGlosas = new RegistroDataModelImpl(
		            urlBean.getUrl(),
		            urlBean.getUrlConteo().getUrl(),
		            null, true, "ID_GLOSA");  
			
		}else {
			UrlBean urlBean = UrlServiceUtil.getInstance()
	                .getUrlServiceByUrlByEnumID(
	                                FrmAuditoriaGlosasControladorUrlEnum.URL6486
	                                                .getValue());
			listaCodigoGlosas = new RegistroDataModelImpl(urlBean.getUrl(),
	                urlBean.getUrlConteo().getUrl(), null,
	                true, "ID_GLOSA");
		}
		
		
 	}
	
	/**
	 * Carga la lista listaCodigoGlosas
	 */
	public void cargarListaCodigoGlosasE() 
	{			
		listaCodigoGlosasE = listaCodigoGlosas;
	}
    
    /**
     * 
     * Carga la lista listaSubdauditoriaglosas
     *
     */
    public void cargarListaSubdauditoriaglosas() {
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.CM_CODIGOS_GLOSAS_FACTURA.getGridKey());
			Map<String, Object> parametros = new TreeMap<>();
			parametros.put(GeneralParameterEnum.COMPANIA.getName(),compania);
			parametros.put("CONSECUTIVO_ARCHIVO",registro.getCampos().get("CONSECUTIVO_ARCHIVO"));
			parametros.put("CONSECUTIVO_FACTURA",registro.getCampos().get("CONSECUTIVO_FACTURA"));
			parametros.put("NUM_FACTURA",registro.getCampos().get("NUM_FACTURA"));

			listaSubdauditoriaglosas = RegistroConverter.toListRegistro(
					requestManager.getList(urlBean.getUrl(), parametros), CacheUtil.getLlaveServicio(urlConexionCache,
							GenericUrlEnum.CM_CODIGOS_GLOSAS_FACTURA.getTable()));					
					
				
		} catch ( SysmanException | SystemException e) {
			e.printStackTrace();
		}

	}

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control GlosaAceptadaIPS
     * 
     * 
     */
    public void cambiarGlosaAceptadaIPS() {
        // <CODIGO_DESARROLLADO>

        double valorAceptado = Double.parseDouble(registro.getCampos()
                        .get("VALOR_NETO_A_PAGAR_ENTI_CONTR").toString())
            -
            Double.parseDouble(registro.getCampos().get("GLOSA_ACEPTADA_IPS")
                            .toString());
        
        double valorAFavorIPS = Double.parseDouble(registro.getCampos()
                .get("VALOR_OBJECION").toString())
        		-
        		Double.parseDouble(registro.getCampos().get("GLOSA_ACEPTADA_IPS")
        					.toString());

        registro.getCampos().put("TOTAL_APROBADO_PAGAR", valorAceptado);
        
        // Actualiza el campo "VALOR_A_FAVOR_IPS" con el nuevo valor calculado
        registro.getCampos().put("VALOR_A_FAVOR_IPS", valorAFavorIPS);
        // </CODIGO_DESARROLLADO>
    }
      
    public void cambiarValorObjecion() {
        //<CODIGO_DESARROLLADO>
        double valorNetoAPagar = Double.parseDouble(registro.getCampos()
            .get("VALOR_NETO_A_PAGAR_ENTI_CONTR").toString());
        
        if(devolucionFactura) {
        //Actualiza el campo "VALOR_OBJECION" con el valor del campo "VALOR_NETO_A_PAGAR_ENTI_CONTR"
            registro.getCampos().put("VALOR_OBJECION", valorNetoAPagar);
        }else {
        	
        double valorObjecion = Double.parseDouble(registro.getCampos()
            .get("VALOR_OBJECION").toString());
        double valorGlosaAceptada = Double.parseDouble(registro.getCampos()
                .get("GLOSA_ACEPTADA_IPS").toString());

        // Calcula la diferencia entre los dos valores
        double valorAceptado2 = valorNetoAPagar - valorObjecion;
        
     // Calcula la diferencia entre el valor de la objecion y el valor de la glosa aceptada
        double valorAFavorIPS = valorObjecion - valorGlosaAceptada;

        // Actualiza el campo "VALOR_ACEPTADO_PAGADOR" con el nuevo valor calculado
        registro.getCampos().put("VALOR_ACEPTADO_PAGADOR", valorAceptado2);
        
     // Actualiza el campo "VALOR_A_FAVOR_IPS" con el nuevo valor calculado
        registro.getCampos().put("VALOR_A_FAVOR_IPS", valorAFavorIPS);
        //</CODIGO_DESARROLLADO>
        }
   }
    
    public void cambiarCheckDevolucionFactura() {
    	if(devolucionFactura) {
            fecha= new Date();
            registro.getCampos().put("FECHA", fecha);
            
            //Cambiar el valor de objecion al valor neto a pagar
            cambiarValorObjecion();

            // Al marcar: asignar el check devolucionFactura pone los siguientes campos en 0 y carga la lista de glosas de devolucion de factura
            registro.getCampos().put("GLOSA_ACEPTADA_IPS", 0);
            registro.getCampos().put("VALOR_ACEPTADO_PAGADOR", 0);
            registro.getCampos().put("TOTAL_APROBADO_PAGAR", 0);
            
            cargarListaCodigoGlosas();
            

        } else {
            // Al desmarcar: limpiar fecha y recalcular y coloca los valores normales
        	fecha = null; 
            registro.getCampos().put("FECHA", null);
            registro.getCampos().put("VALOR_OBJECION", 0);
            // Recalcula el flujo normal
            cambiarValorObjecion();
            cargarListaCodigoGlosas();
        }
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoGlosa
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoGlosa(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_GLOSA",
                        registroAux.getCampos().get("ID"));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoGlosas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoGlosas(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("ID_GLOSA",
                        registroAux.getCampos().get("ID_GLOSA"));
        registroSub.getCampos().put("NOMBRE",
                registroAux.getCampos().get("NOMBRE"));
        registroSub.getCampos().put("COD_GEN_GLOSA",
                registroAux.getCampos().get("CODIGO_GENERAL"));
        registroSub.getCampos().put("COD_ESP_GLOSA",
                registroAux.getCampos().get("CODIGO_ESPECIAL"));
        registroSub.getCampos().put("CONCEPTO_APLICACION",
                registroAux.getCampos().get("CONCEPTO_APLICACION"));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoGlosas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoGlosasE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("ID_GLOSA").toString();
        registroSub.getCampos().put("NOMBRE",
                registroAux.getCampos().get("NOMBRE"));
        registroSub.getCampos().put("COD_GEN_GLOSA", 
        		registroAux.getCampos().get("CODIGO_GENERAL"));
        registroSub.getCampos().put("COD_ESP_GLOSA", 
        		registroAux.getCampos().get("CODIGO_ESPECIAL"));
        registroSub.getCampos().put("CONCEPTO_APLICACION",
                registroAux.getCampos().get("CONCEPTO_APLICACION"));
    }
    
    public void cambiarCodigoGlosasC(int rowNum) {
    	listaSubdauditoriaglosas.get(rowNum).getCampos().put("ID_GLOSA",
				registroSub.getCampos().get("ID_GLOSA"));
    	listaSubdauditoriaglosas.get(rowNum).getCampos().put("COD_GEN_GLOSA",
				registroSub.getCampos().get("COD_GEN_GLOSA"));
    	listaSubdauditoriaglosas.get(rowNum).getCampos().put("COD_ESP_GLOSA",
				registroSub.getCampos().get("COD_ESP_GLOSA"));
    	listaSubdauditoriaglosas.get(rowNum).getCampos().put("CONCEPTO_APLICACION",
				registroSub.getCampos().get("CONCEPTO_APLICACION"));
    	listaSubdauditoriaglosas.get(rowNum).getCampos().put("OBSERVACIONES",
				registroSub.getCampos().get("OBSERVACIONES"));
	}
    
    public void editarRegSubSubdauditoriaglosas(RowEditEvent event) 
    {
    	Registro reg = (Registro) event.getObject();
		try {
			
			reg.getCampos().remove("NOMBRE");
			reg.getCampos().remove("COD_PREST_SERV_SALUD");
			reg.getCampos().remove("COMPANIA");
			reg.getCampos().remove("NUM_FACTURA");
			reg.getCampos().remove("CONSECUTIVO_FACTURA");
			reg.getCampos().remove("CONSECUTIVO_ARCHIVO");
			reg.getCampos().remove("KEY_COMPANIA");
			reg.getCampos().remove("KEY_CONSECUTIVO_ARCHIVO");
			reg.getCampos().remove("KEY_CONSECUTIVO_FACTURA");
			reg.getCampos().remove("KEY_NUM_FACTURA");
			reg.getCampos().remove("KEY_ID_GLOSA");
				
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
				
			// Se actualizan los datos
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.CM_CODIGOS_GLOSAS_FACTURA.getUpdateKey());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(),reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

		} catch (SystemException ex) {
			JsfUtil.agregarMensajeError(idioma.getString("ERROR AL MODIFICAR: ") + ex.getMessage());
			Logger.getLogger(TercerosControlador.class.getName()).log(Level.SEVERE, null, ex);

		} finally {
			cargarListaSubdauditoriaglosas();
		}		
	}
    
    public void eliminarRegSubSubdauditoriaglosas(Registro reg) 
    {
    	try {    
				UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.CM_CODIGOS_GLOSAS_FACTURA.getDeleteKey());

				requestManager.delete(urlDelete.getUrl(), reg.getLlave());
			
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
			
				//cargarListaSubdauditoriaglosas();
		} catch (SystemException ex) {
			JsfUtil.agregarMensajeError(idioma.getString("ERROR AL ELIMINAR: ") + ex.getMessage());
			Logger.getLogger(TercerosControlador.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			cargarListaSubdauditoriaglosas();
		}
    	
    }
    
    public boolean agregarRegistroSubSubdauditoriaglosas() 
    {
    	try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put(GeneralParameterEnum.COMPANIA.getName(),compania);
			parametros.put("CONSECUTIVO_ARCHIVO",registro.getCampos().get("CONSECUTIVO_ARCHIVO"));
			parametros.put("CONSECUTIVO_FACTURA",registro.getCampos().get("CONSECUTIVO_FACTURA"));
			parametros.put("NUM_FACTURA",registro.getCampos().get("NUM_FACTURA"));
			parametros.put("COD_PREST_SERV_SALUD",registro.getCampos().get("COD_PREST_SERV_SALUD"));    	
			parametros.put("COD_GEN_GLOSA",registroSub.getCampos().get("COD_GEN_GLOSA").toString());
			parametros.put("COD_ESP_GLOSA",registroSub.getCampos().get("COD_ESP_GLOSA").toString());
			parametros.put("CONCEPTO_APLICACION",registroSub.getCampos().get("CONCEPTO_APLICACION").toString());
			parametros.put("ID_GLOSA",registroSub.getCampos().get("ID_GLOSA").toString());
			parametros.put("OBSERVACIONES",registroSub.getCampos().get("OBSERVACIONES").toString());
			parametros.put(GeneralParameterEnum.DATE_CREATED.getName(),new Date());
			parametros.put(GeneralParameterEnum.CREATED_BY.getName(),SessionUtil.getUser().getCodigo());
			
			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.CM_CODIGOS_GLOSAS_FACTURA.getCreateKey());

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), parametros);   	
    	
			cargarListaSubdauditoriaglosas();
    	}
    	catch (SystemException e) 
    	{
			Logger.getLogger(FrmAuditoriaGlosasControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError("El registro ya existe");
    	}
    	finally {
    		registroSub = new Registro(new HashMap<String, Object>());
		}
    	return true;
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
       
        Object devFact = registro.getCampos().get("DEVOLUCION_FACTURA");
        if (devFact != null) {
            devolucionFactura = Boolean.parseBoolean(devFact.toString());
        } else {
            devolucionFactura = false;
        }
        
        Object fechaGuardada = registro.getCampos().get("FECHA");
        if (fechaGuardada != null && fechaGuardada instanceof Date) {
            fecha = (Date) fechaGuardada;
        } else {
            fecha = null;
        }
        
        double Valor_aceptado_pagador = Double.parseDouble(registro.getCampos().get("VALOR_ACEPTADO_PAGADOR").toString());
        double Valor_objecion = Double.parseDouble(registro.getCampos().get("VALOR_OBJECION").toString());
        double valorAFavorIPS =  (Double.parseDouble(registro.getCampos().get("VALOR_A_FAVOR_IPS")
				.toString()));
        if (valorAFavorIPS == 0) 
        {
	        valorAFavorIPS = Double.parseDouble(registro.getCampos()
	                .get("VALOR_OBJECION").toString())
	        		-
	        		Double.parseDouble(registro.getCampos().get("GLOSA_ACEPTADA_IPS")
	        					.toString());
	        // Actualiza el campo "VALOR_A_FAVOR_IPS" con el nuevo valor calculado
	        registro.getCampos().put("VALOR_A_FAVOR_IPS", valorAFavorIPS);
        }
        
        if(Valor_aceptado_pagador==0 && Valor_objecion==0){
        	try {
        	registro.getCampos().put("VALOR_ACEPTADO_PAGADOR",registro.getCampos().get("VALOR_NETO_A_PAGAR_ENTI_CONTR"));
        	Map<String, Object> param = new TreeMap<>();
   			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
   			param.put(GeneralParameterEnum.CONSECUTIVO.getName(), registro.getCampos().get("CONSECUTIVO_ARCHIVO").toString());
   			param.put(GeneralParameterEnum.FACTURA.getName(), registro.getCampos().get("CONSECUTIVO_FACTURA").toString());
   			param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get("COD_PREST_SERV_SALUD").toString());
   			param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get("NUM_FACTURA").toString());
   			param.put(GeneralParameterEnum.VALOR.getName(), registro.getCampos().get("VALOR_NETO_A_PAGAR_ENTI_CONTR").toString());

   				UrlBean urlUpdateValor = UrlServiceUtil.getInstance()
   						.getUrlServiceByUrlByEnumID(FrmAuditoriaGlosasControladorUrlEnum.URL1836001.getValue());

   				Parameter parameter = new Parameter();
   				parameter.setFields(param);
   				requestManager.update(urlUpdateValor.getUrl(),
   						urlUpdateValor.getMetodo(),
   						parameter);

   			 }catch (SystemException e) 
		    	{
 				e.printStackTrace();
			}
        	
        }
        
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        
        registro.getCampos().put("DEVOLUCION_FACTURA", devolucionFactura ? "-1" : "0");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        // </CODIGO_DESARROLLADO>
        registro.getCampos().put("FECHA", fecha);
        registro.getCampos().put("DEVOLUCION_FACTURA", devolucionFactura ? "-1" : "0");
        //Detiene el proceso e indicada que se necesita agregar un codigo de glosa cuando se ha marcado el check de devolucion de factura
        if (devolucionFactura && listaSubdauditoriaglosas != null && listaSubdauditoriaglosas.isEmpty()) {
        	
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4510"));

            return false;  
        }

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PRECAUSACION_RIPS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoGlosa
     * 
     * @return listaCodigoGlosa
     */
    public RegistroDataModelImpl getListaCodigoGlosa() {
        return listaCodigoGlosa;
    }

    /**
     * Asigna la lista listaCodigoGlosa
     * 
     * @param listaCodigoGlosa
     * Variable a asignar en listaCodigoGlosa
     */
    public void setListaCodigoGlosa(RegistroDataModelImpl listaCodigoGlosa) {
        this.listaCodigoGlosa = listaCodigoGlosa;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
	
	/**
	 * @return the listaCodigoGlosas
	 */
	public RegistroDataModelImpl getListaCodigoGlosas() {
		return listaCodigoGlosas;
	}

	/**
	 * @param listaCodigoGlosas the listaCodigoGlosas to set
	 */
	public void setListaCodigoGlosas(RegistroDataModelImpl listaCodigoGlosas) {
		this.listaCodigoGlosas = listaCodigoGlosas;
	}

	/**
	 * @return the listaCodigoGlosasE
	 */
	public RegistroDataModelImpl getListaCodigoGlosasE() {
		return listaCodigoGlosasE;
	}

	/**
	 * @param listaCodigoGlosasE the listaCodigoGlosasE to set
	 */
	public void setListaCodigoGlosasE(RegistroDataModelImpl listaCodigoGlosasE) {
		this.listaCodigoGlosasE = listaCodigoGlosasE;
	}

	/**
	 * @return the auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}

	/**
	 * @param auxiliar the auxiliar to set
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}
	
	/**
	 * @return the registroSub
	 */
	public Registro getRegistroSub() {
		return registroSub;
	}

	/**
	 * @param registroSub the registroSub to set
	 */
	public void setRegistroSub(Registro registroSub) {
		this.registroSub = registroSub;
	}

	/**
	 * @return the listaSubdauditoriaglosas
	 */
	public List<Registro> getListaSubdauditoriaglosas() {
		return listaSubdauditoriaglosas;
	}

	/**
	 * @param listaSubdauditoriaglosas the listaSubdauditoriaglosas to set
	 */
	public void setListaSubdauditoriaglosas(List<Registro> listaSubdauditoriaglosas) {
		this.listaSubdauditoriaglosas = listaSubdauditoriaglosas;
	}
	
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	
	public boolean isDevolucionFactura() {
		return devolucionFactura;
	}

	public void setDevolucionFactura(boolean devolucionFactura) {
		this.devolucionFactura = devolucionFactura;
	}

}