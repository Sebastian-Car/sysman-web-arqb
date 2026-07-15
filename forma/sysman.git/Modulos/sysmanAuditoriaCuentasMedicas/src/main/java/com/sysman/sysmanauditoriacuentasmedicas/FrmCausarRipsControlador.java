/*-
 * FrmCausarRipsControlador.java
 *
 * 1.0
 * 
 * 02/12/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sysmanauditoriacuentasmedicas;

import com.sysman.auditoriacuentasmedicas.ejb.EjbAuditoriaCuentasMedicasCeroLocal;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmCausarRipsControladorEnum;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmCausarRipsControladorUrlEnum;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmImportarRipsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.persistencia.ConectorPool;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que aprobar y causar las facturas de los archivos RIPS
 *
 * @version 1.0, 02/12/2019
 * @author jpulido
 */
@ManagedBean
@ViewScoped
public class FrmCausarRipsControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	/**
	 * Atributo auxliar el cual es asiganado en el momento que se activa la edicion
	 * de un registro. Toma el valor del indice dentro de la grilla del registro
	 * seleccionado para editar
	 */
	private int indice;

	/**
	 * Atributo que almacena el nombre del usuario que esta manipulando el sistema
	 */
	private String usuario;

	/**
	 * Atributo que almacena la lista de tipos de comprobante extraidos desde una
	 * consulta en la base ded datos
	 */
	private RegistroDataModelImpl listaTipoComprobante;

	/**
	 * Atributo auxiliar que almacena la lista de tipos de comprobante extraidos
	 * desde una consulta en la base ded datos
	 */
	private RegistroDataModelImpl listaTipoComprobanteE;

	/**
	 * Lista que carga los nit de los prestadorez de servicio
	 */
	private RegistroDataModel listanitPrestadorServicio;
	/**
	 * Lista que carga los nit de los prestadorez de servicio en la grilla
	 */
	private RegistroDataModel listanitPrestadorServicioE;

	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;

	/**
	 * Atributo que contiene los diversos parametros de entrada para el filtro de la
	 * informacion
	 */
	private Map<String, Object> parametrosEntrada;

	/**
	 * Variable que almacena el cosecutivo del formulario anterior
	 */
	private String consecutivo;

	private boolean insertando;
	
	/**
	 * Variable que controla si el botón Causar cuenta esta bloqueado o no
	 */
	private boolean bloqueaBotonCausar;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	@EJB
	private EjbAuditoriaCuentasMedicasCeroLocal ejbAuditoriaCuentasMedicasCero;

	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmCausarRipsControlador
	 */
	public FrmCausarRipsControlador() {
		super();
		compania = SessionUtil.getCompania();
		usuario = SessionUtil.getUser().getCodigo();
		parametrosEntrada = SessionUtil.getFlash();
		insertando = false;
		try {
			// 2137
			numFormulario = GeneralCodigoFormaEnum.FRM_CAUSAR_RIPS.getCodigo();

			if (parametrosEntrada != null) {

				consecutivo = parametrosEntrada.get("consecutivo").toString();
			}
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.CM_ARCHIVO_TRANSACCIONES;

		reasignarOrigen();
		buscarLlave();

		registro = new Registro();
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>

		cargarListanitPrestadorServicio();
		cargarListanitPrestadorServicioE();
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();

	}

	@Override
	public void reasignarOrigen() {

		buscarUrls();

		parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
	}
	// <METODOS_CARGAR_LISTA>

	/**
	 * 
	 * Carga la lista listaTipoComprobante
	 *
	 */
	public void cargarListaTipoComprobante() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCausarRipsControladorUrlEnum.URL4392.getValue());
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTipoComprobante = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"COMPROBANTE");

	}

	/**
	 * 
	 * Carga la lista listaTipoComprobante L
	 */
	public void cargarListaTipoComprobanteE() {
		listaTipoComprobanteE = listaTipoComprobante;
	}

	/**
	 * 
	 * Carga la lista listanitPrestadorServicio
	 *
	 */
	public void cargarListanitPrestadorServicio() {
		listanitPrestadorServicio = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR2137:TBCB7449",
				"SELECT NIT, NOMBRE, SUCURSAL FROM TERCERO WHERE COMPANIA = '" + compania + "' ORDER BY NIT", true,
				"NIT");
	}

	/**
	 * 
	 * Carga la lista listanitPrestadorServicio
	 *
	 */
	public void cargarListanitPrestadorServicioE() {
		listanitPrestadorServicioE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR2137:TBCB7449",
				"SELECT NIT, NOMBRE, SUCURSAL FROM TERCERO WHERE COMPANIA = '" + compania + "' ORDER BY NIT", true,
				"NIT");
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>

	/**
     * 
     * Metodo ejecutado al oprimir el boton CausarCuenta en la vista
     *
     *
     */
    public void oprimirCausarCuenta() {
        // <CODIGO_DESARROLLADO>
        List<Registro> rs = null;
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmCausarRipsControladorUrlEnum.URL368
                                        .getValue());
        
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);

        try {
            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(url.getUrl(), params));

            String CausacionAgrupada = ejbSysmanUtil.consultarParametro(
                    compania,
                    "CAUSACION DE CUENTAS MEDICAS AGRUPADO",
                    SessionUtil.getModulo(), new Date(),
                    false);
	            
            if (!rs.isEmpty()) 
            {
            	if(CausacionAgrupada.equals("NO"))
	            {	            
	                for (int i = 0; i < rs.size(); i++) {  
	                	
	                    String rta = ejbAuditoriaCuentasMedicasCero
	                                    .causacionCuentasMedicas(compania,
	                                                    rs.get(i).getCampos().get(
	                                                                    FrmCausarRipsControladorEnum.NUM_FACTURA
	                                                                                    .getValue())
	                                                                    .toString(),
	                                                    rs.get(i).getCampos().get(
	                                                                    FrmCausarRipsControladorEnum.COD_PREST_SERV_SALUD 
	                                                                                    .getValue())
	                                                                    .toString(),
	                                                    rs.get(i).getCampos().get(
	                                                                    FrmCausarRipsControladorEnum.NOMBRE_TIPO_COMPROBANTE
	                                                                                    .getValue())
	                                                                    .toString(),
	                                                    SysmanFunciones.ano(
	                                                                    new Date()),
	                                                    new Date(),
	                                                    rs.get(i).getCampos().get(
	                                                                    FrmCausarRipsControladorEnum.TIPO_COMPROBANTE
	                                                                                    .getValue())
	                                                                    .toString(),
	                                                    usuario,
	                                                    rs.get(i).getCampos()
	                                                                    .get(FrmCausarRipsControladorEnum.RADICADO
	                                                                                    .getValue())
	                                                                    .toString(),
	                                                    Integer.parseInt(
	                                                                    consecutivo),0);
	
	                    String[] variables = rta.split(",");
	                    registro.getCampos().put(
	                                    "COMPANIA_COMPROBANTE",
	                                    variables[0].split(":")[1].trim());
	                    registro.getCampos().put("NUMERO_COMPROBANTE",
	                                    variables[2].split(":")[1].trim());
	
	                    asignarMensajesEstado(SysmanFunciones.concatenar("Factura ",
	                                    rs.get(i).getCampos().get(
	                                                    FrmCausarRipsControladorEnum.NUM_FACTURA
	                                                                    .getValue())
	                                                    .toString(),
	                                    " Causada"), "E");
	
	                    facturado();
	
	                    actualizarCausarFactura(rs.get(i).getCampos().get(
	                                    FrmCausarRipsControladorEnum.NUM_FACTURA
	                                                    .getValue())
	                                    .toString(),
	                                    rs.get(i).getCampos().get(
	                                                    FrmCausarRipsControladorEnum.COD_PREST_SERV_SALUD
	                                                                    .getValue())
	                                                    .toString(),
	                                    SysmanFunciones.nvl(registro.getCampos()
	                                    				.get("NUMERO_COMPROBANTE"),"0").toString());
	
	                    JsfUtil.agregarMensajeInformativo(
	                                    idioma.getString("MSM_PROCESO_EJECUTADO"));
	
	                }
	            }
            	else
                {
                	String rta = ejbAuditoriaCuentasMedicasCero
                            .causacionCuentasMedicas(compania,
                                            null,
                                            rs.get(0).getCampos().get(
                                                            FrmCausarRipsControladorEnum.COD_PREST_SERV_SALUD 
                                                                            .getValue())
                                                            .toString(),
                                            rs.get(0).getCampos().get(
                                                            FrmCausarRipsControladorEnum.NOMBRE_TIPO_COMPROBANTE
                                                                            .getValue())
                                                            .toString(),
                                            SysmanFunciones.ano(
                                                            new Date()),
                                            new Date(),
                                            rs.get(0).getCampos().get(
                                                            FrmCausarRipsControladorEnum.TIPO_COMPROBANTE
                                                                            .getValue())
                                                            .toString(),
                                            usuario,
                                            rs.get(0).getCampos()
                                                            .get(FrmCausarRipsControladorEnum.RADICADO
                                                                            .getValue())
                                                            .toString(),
                                            Integer.parseInt(
                                                            consecutivo),1);

    		        String[] variables = rta.split(",");
    		        registro.getCampos().put(
    		                        "COMPANIA_COMPROBANTE",
    		                        variables[0].split(":")[1].trim());
    		        registro.getCampos().put("NUMERO_COMPROBANTE",
    		                        variables[2].split(":")[1].trim());
    		
    		        asignarMensajesEstado(SysmanFunciones.concatenar("Cuenta ",consecutivo," Causada"), "T"); 	        
    		        
    		        facturado();
    		        
    		        actualizarCausarFactura(null,
                            rs.get(0).getCampos().get(
                                            FrmCausarRipsControladorEnum.COD_PREST_SERV_SALUD
                                                            .getValue())
                                            .toString(),
                            SysmanFunciones.nvl(registro.getCampos()
                                    		.get("NUMERO_COMPROBANTE"),"0").toString());

    		        JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
                }
            }
	        else 
	        {
	        	JsfUtil.agregarMensajeAlerta(
	        		"No olvide seleccionar un tipo de comprobante y/o aprobar la factura a causar");
	        }  
            
            reasignarOrigen();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

	private void actualizarCausarFactura(String numFactura, String codPrestSerSalud, String numeroComp) {

		UrlBean urlUpdate = new UrlBean();
		Map<String, Object> fields = new TreeMap<>();
		fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		fields.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
		fields.put(FrmCausarRipsControladorEnum.COD_PREST_SERV_SALUD.getValue(), codPrestSerSalud);		
		fields.put("CAUSADO_POR", usuario);
		fields.put(FrmCausarRipsControladorEnum.CAUSADO_FECHA.getValue(), new Date());
		fields.put(FrmCausarRipsControladorEnum.CAUSADO.getValue(), "-1");
		fields.put(FrmCausarRipsControladorEnum.NUMERO_COMP.getValue(), numeroComp);
		
		if(numFactura == null) 
		{
			urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmCausarRipsControladorUrlEnum.URL655.getValue());
		}
		else			
		{
			fields.put("NUM_FACTURA", numFactura);
			urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmCausarRipsControladorUrlEnum.URL654.getValue());
		}

		Parameter parameter = new Parameter();
		parameter.setFields(fields);

		try {
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>

	/**
	 * Metodo ejecutado cuando se activa la edicion de un registro del formulario
	 * 
	 *
	 * @param registro registro del cual se activo la edicion
	 */
	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();
	}

	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
	 * 
	 */
	public void ejecutarrcCerrar() {
		Direccionador direccionador = new Direccionador();
		direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.PRECAUSACION_RIPS_CONTROLADOR.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
	}

	public void cambiaraprobarC(int rowNum) {

		// Para el cambio en una fila selecciona (PARA FORMULARIOS
		// CONTINUOS) se realiza como lo muestra la siguiente linea
		// listaInicial.getDatasource().get(rowNum %
		// 10).getCampos().put("FECHALARGA", "hola ");

		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarcausarC(int rowNum) {
		// Para el cambio en una fila selecciona (PARA FORMULARIOS
		// CONTINUOS) se realiza como lo muestra la siguiente linea
		// listaInicial.getDatasource().get(rowNum %
		// 10).getCampos().put("FECHALARGA", "hola ");
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipoComprobante
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoComprobante(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		registro.getCampos().put("COMPROBANTE",
				registroAux.getCampos().get("COMPROBANTE"));
		registro.getCampos().put(FrmCausarRipsControladorEnum.NOMBRE_TIPO_COMPROBANTE.getValue(),
				registroAux.getCampos().get(FrmCausarRipsControladorEnum.NOMBRE_TIPO_COMPROBANTE.getValue()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipoComprobante
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoComprobanteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get(FrmCausarRipsControladorEnum.NOMBRE_TIPO_COMPROBANTE.getValue());
		registro.getCampos().put("COMPROBANTE",
				registroAux.getCampos().get("COMPROBANTE"));
		registro.getCampos().put(FrmCausarRipsControladorEnum.NOMBRE_TIPO_COMPROBANTE.getValue(),
				registroAux.getCampos().get(FrmCausarRipsControladorEnum.NOMBRE_TIPO_COMPROBANTE.getValue()));

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listanitPrestadorServicio
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilanitPrestadorServicio(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		registro.getCampos().put("NUM_IDENTIF_PRESTADOR",
				registroAux.getCampos().get(GeneralParameterEnum.NIT.getName()));

		registro.getCampos().put("NOMBRE_RAZON_SOCIAL",
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listanitPrestadorServicio
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilanitPrestadorServicioE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "").toString();
	}

	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
	@Override
	public void asignarValoresRegistro() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>

	}

	// </METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() 
	{		
		Registro rs = null;
		cargarListaTipoComprobante();
		cargarListaTipoComprobanteE();
	
		Map<String, Object> params = new TreeMap<>();
		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
		
	    try 
	    {
			rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmCausarRipsControladorUrlEnum.URL4391.getValue())
											.getUrl(),
											params));
			
			if(Integer.parseInt(rs.getCampos().get("POR_CAUSAR").toString()) == 0)
			{
				setBloqueaBotonCausar(true);
			}
			else
			{
				setBloqueaBotonCausar(false);
			}
		} 
	    catch (SystemException e) 
	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void removerCombos() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>

	}

	@Override
	public void cancelarEdicion(RowEditEvent event) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>

	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		insertando = true;
		try {
			registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);

			registro.getCampos().put("CONSECUTIVO_RIPS", consecutivo);

			registro.getCampos().put("TIPO_IDENTI_PREST_SERV_SALUD", "NI");

			registro.getCampos().put(FrmCausarRipsControladorEnum.APROBADO.getValue(), -1);
			registro.getCampos().put(FrmCausarRipsControladorEnum.CAUSADO.getValue(), 0); // 7715077 mperez

			registro.getCampos().put("APROBADO_POR", usuario);
			registro.getCampos().put("APROBADO_FECHA", new Date());

			registro.getCampos().put("TIPO_COMPROBANTE", ejbSysmanUtil.consultarParametro(compania,
					"COMPROBANTE PARA CAUSACION DE CUENTAS MEDICAS", "84", new Date(), false));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues() {
		actualizarEstadoImportarRip();
		abrirFormulario();
		return true;
	}

	private void actualizarEstadoImportarRip() {

		Map<String, Object> fields = new TreeMap<>();
		fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		fields.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
		fields.put(GeneralParameterEnum.ESTADO.getName(), "C");
		fields.put(GeneralParameterEnum.MENSAJES.getName(), "Creaciï¿½n de detalle sin importaciï¿½n de RIP");
		fields.put(GeneralParameterEnum.USUARIO.getName(), SessionUtil.getUser().getCodigo());

		UrlBean urlUpdate = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCausarRipsControladorUrlEnum.URL437.getValue());

		Parameter parameter = new Parameter();
		parameter.setFields(fields);

		try {
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 * 
	 */
	@Override
	public boolean actualizarAntes() {

		if (!insertando) {

			if ((boolean) registro.getCampos().get(FrmCausarRipsControladorEnum.APROBADO.getValue())
					&& (boolean) registro.getCampos().get(FrmCausarRipsControladorEnum.CAUSADO // MAPS
							.getValue())) {
				registro.getCampos().put("APROBADO_POR", usuario);
				registro.getCampos().put("APROBADO_FECHA", new Date());

				asignarMensajesEstado(SysmanFunciones.concatenar("Factura ",
						registro.getCampos().get(FrmCausarRipsControladorEnum.NUM_FACTURA.getValue()).toString(),
						" Aprobada"), "E");
			}
			
			registro.getCampos().put(FrmCausarRipsControladorEnum.TIPO_COMPROBANTE.getValue(), auxiliar);
			registro.getCampos().remove("COMPROBANTE");
			registro.getCampos().remove(FrmCausarRipsControladorEnum.RADICADO.getValue());
			registro.getCampos().remove(FrmCausarRipsControladorEnum.NOMBRE_TIPO_COMPROBANTE.getValue());
		}
		insertando = false;
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Realiza el guardado de un estado y/o mensaje sobre el paquete RIP
	 * 
	 * @param mensaje Mensaje a guardar en le log
	 * @param estado  Esatdo actual del cargue de archivos
	 */
	private void asignarMensajesEstado(String mensaje, String estado) {
		Registro miReg = null;

		Map<String, Object> fields = new TreeMap<>();
		fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		fields.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);

		try {
			miReg = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmImportarRipsControladorUrlEnum.URL4393.getValue())
											.getUrl(),
									fields));

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmImportarRipsControladorUrlEnum.URL4392.getValue());

			fields.put(GeneralParameterEnum.ESTADO.getName(), estado);

			fields.put(GeneralParameterEnum.MENSAJES.getName(), SysmanFunciones.concatenar(
					miReg.getCampos().get(GeneralParameterEnum.MENSAJES.getName()).toString(), mensaje, "\n"));
			fields.put(GeneralParameterEnum.USUARIO.getName(), SessionUtil.getUser().getCodigo());

			Parameter parameter = new Parameter();
			parameter.setFields(fields);

			int actualizadas = requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
			if (actualizadas > 0)
				JsfUtil.agregarMensajeInformativo(mensaje);
		} catch (SystemException e1) {
			logger.error(e1.getMessage(), e1);
			JsfUtil.agregarMensajeError(e1.getMessage());

		}

	}

	/**
	 * Realiza la validacion de estado actual de facturas por causar para al momento
	 * de estar todas las facturas causadas cambiar el estado del paquete a
	 * facturado
	 */
	public void facturado() {
		Registro miReg = null;

		Map<String, Object> fields = new TreeMap<>();
		fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		fields.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
		try {
			miReg = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmCausarRipsControladorUrlEnum.URL4391.getValue())
											.getUrl(),
									fields));
			if (Integer.parseInt(miReg.getCampos().get("POR_CAUSAR").toString()) == 1) {
				asignarMensajesEstado("Paquete facturado exitosamente", "T");
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro *
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
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 */
	@Override
	public boolean eliminarDespues() {
		abrirFormulario();
		return true;
	}

	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro
	 */
	/**
	 * @return the indice
	 */
	public int getIndice() {
		return indice;
	}

	/**
	 * @param indice the indice to set
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}

	/**
	 * @return the listaTipoComprobante
	 */

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
	 * @return the listaTipoComprobante
	 */
	public RegistroDataModelImpl getListaTipoComprobante() {
		return listaTipoComprobante;
	}

	/**
	 * @param listaTipoComprobante the listaTipoComprobante to set
	 */
	public void setListaTipoComprobante(RegistroDataModelImpl listaTipoComprobante) {
		this.listaTipoComprobante = listaTipoComprobante;
	}

	/**
	 * @return the listaTipoComprobanteE
	 */
	public RegistroDataModelImpl getListaTipoComprobanteE() {
		return listaTipoComprobanteE;
	}

	/**
	 * @param listaTipoComprobanteE the listaTipoComprobanteE to set
	 */
	public void setListaTipoComprobanteE(RegistroDataModelImpl listaTipoComprobanteE) {
		this.listaTipoComprobanteE = listaTipoComprobanteE;
	}

	/**
	 * Retorna la lista listanitPrestadorServicio
	 * 
	 * @return listanitPrestadorServicio
	 */
	public RegistroDataModel getListanitPrestadorServicio() {
		return listanitPrestadorServicio;
	}

	/**
	 * Asigna la lista listanitPrestadorServicio
	 * 
	 * @param listanitPrestadorServicio Variable a asignar en
	 *                                  listanitPrestadorServicio
	 */
	public void setListanitPrestadorServicio(RegistroDataModel listanitPrestadorServicio) {
		this.listanitPrestadorServicio = listanitPrestadorServicio;
	}

	/**
	 * Retorna la lista listanitPrestadorServicio
	 * 
	 * @return listanitPrestadorServicio
	 */
	public RegistroDataModel getListanitPrestadorServicioE() {
		return listanitPrestadorServicioE;
	}

	/**
	 * Asigna la lista listanitPrestadorServicio
	 * 
	 * @param listanitPrestadorServicio Variable a asignar en
	 *                                  listanitPrestadorServicio
	 */
	public void setListanitPrestadorServicioE(RegistroDataModel listanitPrestadorServicioE) {
		this.listanitPrestadorServicioE = listanitPrestadorServicioE;
	}

	/**
	 * @return the bloqueaBotonCausar
	 */
	public boolean isBloqueaBotonCausar() {
		return bloqueaBotonCausar;
	}

	/**
	 * @param bloqueaBotonCausar the bloqueaBotonCausar to set
	 */
	public void setBloqueaBotonCausar(boolean bloqueaBotonCausar) {
		this.bloqueaBotonCausar = bloqueaBotonCausar;
	}

}
