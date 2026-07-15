/*-
 * FrmComprobanteCntAfecAnticipoControlador.java
 *
 * 1.0
 * 
 * 09/10/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisRemote;
import com.sysman.contabilidad.enums.ComprobanteCntAfectarControladorEnum;
import com.sysman.contabilidad.enums.ComprobanteCntAfectarControladorUrlEnum;
import com.sysman.contabilidad.enums.FrmComprobanteCntAfecAnticipoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
/**
 *
 * @version 1.0, 09/10/2025
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class FrmComprobanteCntAfecAnticipoControlador extends BeanBaseDatosAcme{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private static final String VLR_DOCUMENTO = "vlrDocumento";
	private static final String TB_TB686 = "TB_TB686";
	private final String modulo;
	private final String cNumero;
	private final String cRowIdComprobante;
	private HashMap<String, Object> rowIdComprobante;
	private String claseComprobante;
	private String anoComprobante;
	private String tipoComprobante;
	private String numeroComprobante;
	private String fechaComprobante;
	private String vlrDocumento;
	private boolean visibleDialogo;
	private String terceroComprobante;
	private String sucursalComprobante;
	private String compRelacionado;
	private String terceroEnEncabezado;
	private String centroCosto;
	private String tipoCpteAfect;
	private String auxiliarComprobante;
	private String mesComprobante;
	private boolean visibleOrdenador;
	private String controlChequera;
	private String vlrGirar;
	private String opcionMenu;
	private String nombreFormulario;
	double valorDocumento;
	private boolean vuelve;
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaListaFacturacion;
	private RegistroDataModelImpl listaListaAnticipo;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	//<DECLARAR_LISTAS_SUBFORM>
	//</DECLARAR_LISTAS_SUBFORM>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_ADICIONALES>
	//</DECLARAR_ADICIONALES>

	private String titulo;
	private String tipos;
	private Double saldoF;
	private Double saldoA;
	private Registro saldoRegistro;

	/**
	 * Define la URL que obtiene los registros que se cargan en la lista
	 * principal del formulario
	 */
	UrlBean urlconsultaLista;
	/**
	 * Almacena los parametros a enviar al servicio, dependiendo la URL que se
	 * defina en el atributo urlconsultaLista
	 */
	Map<String, Object> paramConsultaLista;
	private double saldoRubro;
	private List<Registro> listaSeleccionadosA;
	private List<Registro> listaSeleccionadosF;
	
	
	@EJB
	private EjbContabilidadSeisRemote ejbContabilidadSeis;
	
	/**
	 * Crea una nueva instancia de FrmComprobanteCntAfecAnticipoControlador
	 */
	public FrmComprobanteCntAfecAnticipoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		cNumero = GeneralParameterEnum.NUMERO.getName();
		cRowIdComprobante = "rowIdComprobante";
		tipos = "V";
		titulo = "COMPROBANTES A AFECTAR CUENTAS POR COBRAR";
		try {
			numFormulario = 2544;
			validarPermisos();
			//<INI_ADICIONAL>
			Map<String, Object> parametros = SessionUtil.getFlash();
			if (parametros != null) {
				if (parametros.get(cRowIdComprobante) == null) {
					vuelve = true;
					return;
				}
				rowIdComprobante = (HashMap<String, Object>) parametros
						.get(cRowIdComprobante);
				anoComprobante = validarCampos(parametros, "anoComprobante");
				tipoComprobante = validarCampos(parametros, "tipoComprobante");
				numeroComprobante = validarCampos(parametros,
						"numeroComprobante");
				claseComprobante = validarCampos(parametros,
						"claseComprobante");
				vlrDocumento = validarCampos(parametros, VLR_DOCUMENTO);
				fechaComprobante = validarCampos(parametros,
						"fechaComprobante");
				terceroComprobante = validarCampos(parametros,
						"terceroComprobante");
				sucursalComprobante = validarCampos(parametros,
						"sucursalComprobante");
				compRelacionado = validarCampos(parametros, "compRelacionado");
				terceroEnEncabezado = validarCampos(parametros,
						"terceroEncabezado");
				centroCosto = validarCampos(parametros, "centroCosto");
				tipoCpteAfect = validarCampos(parametros, "tipoCpteAfect");
				mesComprobante = validarCampos(parametros, "mesComprobante");
				auxiliarComprobante = validarCampos(parametros,
						"auxiliarComprobante");
				controlChequera = validarCampos(parametros, "controlChequera");
				vlrGirar = validarCampos(parametros, "vlrGirar");
				opcionMenu = validarCampos(parametros, "opcionMenu");

			}
			paramConsultaLista = new TreeMap<>();
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas(){
		//<CARGAR_LISTA_COMBO_GRANDE>
		inicializarComprobanteAfect();
		cargarListaListaFacturacion();
		inicializarComprobanteAfectAnticipo();
		cargarListaListaAnticipo();
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub(){
		//<CARGAR_LISTAS_SUBFORM>
		//</CARGAR_LISTAS_SUBFORM>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
	}
	/**
	 * En este metodo se iguala a null todas las listas de los
	 * subformularios
	 */
	@Override
	public void iniciarListasSubNulo(){
		//<CARGAR_LISTAS_SUBFORM_NULL>
		//</CARGAR_LISTAS_SUBFORM_NULL>
	}
	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar(){
		tabla="";
		asignarOrigenDatos();
		iniciarListas();
	}

	public void inicializarComprobanteAfect() {
		urlconsultaLista = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmComprobanteCntAfecAnticipoControladorUrlEnum.URL002
						.getValue());
		paramConsultaLista.put(
				GeneralParameterEnum.COMPANIA.getName(),
				compania);
		paramConsultaLista.put(
				ComprobanteCntAfectarControladorEnum.TERCEROCOMPROBANTE
				.getValue(),
				terceroComprobante);
		paramConsultaLista.put(
				ComprobanteCntAfectarControladorEnum.SUCURSALCOMPROBANTE
				.getValue(),
				sucursalComprobante);
		paramConsultaLista.put(
				ComprobanteCntAfectarControladorEnum.COMPRELACIONADO
				.getValue(),
				tipos);
		paramConsultaLista.put(
				ComprobanteCntAfectarControladorEnum.FECHACOMPROBANTE
				.getValue(),
				fechaComprobante);
		paramConsultaLista.put(
				ComprobanteCntAfectarControladorEnum.ANOCOMPROBANTE
				.getValue(),
				anoComprobante);
	}
	
	public void inicializarComprobanteAfectAnticipo() {
		urlconsultaLista = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmComprobanteCntAfecAnticipoControladorUrlEnum.URL001
						.getValue());
		paramConsultaLista.put(
				GeneralParameterEnum.COMPANIA.getName(),
				compania);
		paramConsultaLista.put(
				ComprobanteCntAfectarControladorEnum.TERCEROCOMPROBANTE
				.getValue(),
				terceroComprobante);
		paramConsultaLista.put(
				ComprobanteCntAfectarControladorEnum.SUCURSALCOMPROBANTE
				.getValue(),
				sucursalComprobante);
		paramConsultaLista.put(
				ComprobanteCntAfectarControladorEnum.FECHACOMPROBANTE
				.getValue(),
				fechaComprobante);
		paramConsultaLista.put(
				ComprobanteCntAfectarControladorEnum.COMPRELACIONADO
				.getValue(),
				tipos);
		paramConsultaLista.put(
				ComprobanteCntAfectarControladorEnum.ANOCOMPROBANTE
				.getValue(),
				anoComprobante);
	}
	/**
	 * Se realiza la asignacion de la variable origenDatos por la
	 * consulta correspondiente del formulario
	 * 
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
	}
	/**
	 * Se realiza la asignacion de la variable origenGrilla por la
	 * consulta correspondiente de la grilla del formulario, se hace
	 * la asignacion de dicha consulta a los objetos listaInicial y
	 * listaInicialF
	 * 
	 * 
	 */
	@Override
	public void reasignarOrigenGrilla() {
	}
	//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 * Carga la lista listaListaFacturacion
	 *
	 */
	public void cargarListaListaFacturacion(){
		try {
			listaListaFacturacion = new RegistroDataModelImpl(urlconsultaLista.getUrl(),
					urlconsultaLista.getUrlConteo().getUrl(),
					paramConsultaLista, false,
					CacheUtil.getLlaveServicio(urlConexionCache,
							"COMPROBANTE_CNT"),
					true);
		}
		catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	/**
	 * 
	 * Carga la lista listaListaAnticipo
	 *
	 */
	public void cargarListaListaAnticipo(){
		try {
			listaListaAnticipo = new RegistroDataModelImpl(urlconsultaLista.getUrl(),
					urlconsultaLista.getUrlConteo().getUrl(),
					paramConsultaLista, false,
					CacheUtil.getLlaveServicio(urlConexionCache,
							"COMPROBANTE_CNT"),
					true);
		}
		catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_CAMBIAR>	
	/**
	 * Metodo ejecutado al cambiar el control tipos
	 * 
	 * 
	 */
	public void cambiartipos() {
		//<CODIGO_DESARROLLADO>
		switch (tipos) {
		case "V":
			titulo = "COMPROBANTES A AFECTAR CUENTAS POR COBRAR";
			break;
		case "P":
			titulo = "COMPROBANTES A AFECTAR CUENTAS POR PAGAR";
			break;
		default:
			break;
		}
		
		iniciarListas();
		//</CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * del dialogo SaldoF en la vista
	 *
	 *
	 */
	public void aceptarSaldoF() {
		//<CODIGO_DESARROLLADO>
		 List<Registro> listaSeleccion = listaListaFacturacion.getSeleccionados();
	        for (int i = 0; i < listaSeleccion.size(); i++) {
	    
	        	Registro registro = listaSeleccion.get(i);
	        	saldoRubro =  Double.parseDouble(registro.getCampos().get("SALDO").toString().replace(",", ""));
	        	if(registro.getLlave()== saldoRegistro.getLlave()) {
	        		
	        		if (saldoF <= saldoRubro) {
	        			DecimalFormat df = new DecimalFormat("#.##");
	        			String saldoStr = df.format(saldoF).replace(",", ".");
	        		    registro.getCampos().put(GeneralParameterEnum.SALDO.getName(), saldoStr);
	        		} else {
	        			JsfUtil.agregarMensajeAlerta("No se puede insertar un valor mayor al saldo del rubro");
	                	return;
	                }
	            }
			}
	        saldoF = 0.0;
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * del dialogo SaldoA en la vista
	 *
	 *
	 */
	public void aceptarSaldoA() {
		//<CODIGO_DESARROLLADO>
		 List<Registro> listaSeleccion = listaListaAnticipo.getSeleccionados();
	        for (int i = 0; i < listaSeleccion.size(); i++) {
	    
	        	Registro registro = listaSeleccion.get(i);
	        	saldoRubro =  Double.parseDouble(registro.getCampos().get("SALDO").toString().replace(",", ""));
	        	if( registro.getLlave()== saldoRegistro.getLlave()) {
	        		
	        		if (saldoA <= saldoRubro) {
	        			DecimalFormat df = new DecimalFormat("#.##");
	        			String saldoStr = df.format(saldoA).replace(",", ".");
	        		    registro.getCampos().put(GeneralParameterEnum.SALDO.getName(), saldoStr);
	        		} else {
	        			JsfUtil.agregarMensajeAlerta("No se puede insertar un valor mayor al saldo del rubro");
	                	return;
	                }
	            }
			}
	        saldoA = 0.0;
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	 /**
     * Metodo que se ejecuta al momento cambiar el saldo para guardar el registro a modificar
     * 
     * @param reg registro seleccionado para cambiar el saldo
     */
	public void cambiarSaldoF(int index) {  

		List<Registro> listaSeleccion = listaListaFacturacion.getSeleccionados();
		Registro registro = listaSeleccion.get(index);
		saldoRegistro = registro;        
	}

	/**
	 * Metodo que se ejecuta al momento cambiar el saldo para guardar el registro a modificar
	 * 
	 * @param reg registro seleccionado para cambiar el saldo
	 */
	public void cambiarSaldoA(int index) {  

		List<Registro> listaSeleccion = listaListaAnticipo.getSeleccionados();
		Registro registro = listaSeleccion.get(index);
		saldoRegistro = registro;        
	}
	//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaListaFacturacion
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaListaFacturacion(SelectEvent event) {
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaListaAnticipo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaListaAnticipo(SelectEvent event) {
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>	
	//</METODOS_ARBOL>
	//<METODOS_BOTONES>	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * en la vista
	 *
	 *
	 */
	public void oprimirAceptar() {
		//<CODIGO_DESARROLLADO>
		try {
		 if (listaListaAnticipo.getSeleccionados().isEmpty() && listaListaFacturacion.getSeleccionados().isEmpty()) {
	            JsfUtil.agregarMensajeInformativo(idioma.getString(TB_TB686));
	            return;
	        }
	        else {
	        	BigDecimal totalAnticipos = BigDecimal.ZERO;
	        	BigDecimal totalFacturas = BigDecimal.ZERO;

	        	// Sumar valores
	        	listaSeleccionadosA = listaListaAnticipo.getSeleccionados();
	        	listaSeleccionadosF = listaListaFacturacion.getSeleccionados();
	        	for (Registro anticipo : listaSeleccionadosA) {
	        	    BigDecimal valor = new BigDecimal(anticipo.getCampos().get(GeneralParameterEnum.SALDO.getName()).toString());
	        	    totalAnticipos = totalAnticipos.add(valor);
	        	}

	        	for (Registro factura : listaSeleccionadosF) {
	        	    BigDecimal valor = new BigDecimal(factura.getCampos().get(GeneralParameterEnum.SALDO.getName()).toString());
	        	    totalFacturas = totalFacturas.add(valor);
	        	}

	        	if (totalAnticipos.compareTo(totalFacturas) != 0) {
	        	    JsfUtil.agregarMensajeInformativo(
	        	        "Los saldos no coinciden entre los comprobantes de anticipo (" + totalAnticipos +
	        	        ") y facturación (" + totalFacturas + "). Los valores deben ser iguales para realizar la afectación."
	        	    );
	        	    return;
	        	}

	        	for (Registro anticipo : listaSeleccionadosA) {
	        	    Date fechaA = SysmanFunciones.convertirAFecha(
	        	        SysmanFunciones.toString(anticipo.getCampos().get("FECHA"))
	        	    );
	        	    String tipoA = SysmanFunciones.toString(anticipo.getCampos().get("TIPO"));
	        	    String numeroA = SysmanFunciones.toString(anticipo.getCampos().get("NUMERO"));

	        	    for (Registro factura : listaSeleccionadosF) {
	        	        Date fechaF = SysmanFunciones.convertirAFecha(
	        	            SysmanFunciones.toString(factura.getCampos().get("FECHA"))
	        	        );
	        	        String tipoF = SysmanFunciones.toString(factura.getCampos().get("TIPO"));
	        	        String numeroF = SysmanFunciones.toString(factura.getCampos().get("NUMERO"));

	        	        if (fechaA != null && fechaF != null && fechaA.compareTo(fechaF) > 0) {
	        	            JsfUtil.agregarMensajeInformativo(
	        	                "La fecha del comprobante (" + tipoA + "-" + numeroA +
	        	                ") es posterior a la fecha del comprobante (" +
	        	                tipoF + "-" + numeroF + "). Verifique las fechas de los comprobantes."
	        	            );
	        	            return;
	        	        }
	        	    }
	        	}

			ejbContabilidadSeis.generarAnticipo(compania, Integer.parseInt(anoComprobante), tipoComprobante,
						new BigInteger(numeroComprobante), terceroComprobante, sucursalComprobante,
						listaComprobanteAfectar(listaSeleccionadosF), listaComprobanteAfectar(listaSeleccionadosA), SysmanFunciones.convertirAFecha(fechaComprobante),
						tipos, totalAnticipos, SessionUtil.getUser().getCodigo());
			JsfUtil.agregarMensajeInformativo(
                    idioma.getString("MSM_PROCESO_EJECUTADO"));
			
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("rid", rowIdComprobante);
			parametros.put("ano", String.valueOf(anoComprobante));
			parametros.put("mes", mesComprobante);
			parametros.put("tipoMov", tipoComprobante);
			parametros.put("opcionMenu", opcionMenu);

			Direccionador direccionador = new Direccionador();
			direccionador.setParametros(parametros);
			direccionador.setNumForm(Integer
					.toString(GeneralCodigoFormaEnum.COMPROBANTECNTS_CONTROLADOR
							.getCodigo()));
			SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
			
	        }
		} catch (NumberFormatException | SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar
	 * en la vista
	 *
	 *
	 */
	public void oprimirCancelar() {
		//<CODIGO_DESARROLLADO>
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("rid", rowIdComprobante);
		parametros.put("ano", String.valueOf(anoComprobante));
		parametros.put("mes", mesComprobante);
		parametros.put("tipoMov", tipoComprobante);
		parametros.put("opcionMenu", opcionMenu);

		Direccionador direccionador = new Direccionador();
		direccionador.setParametros(parametros);
		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.COMPROBANTECNTS_CONTROLADOR
						.getCodigo()));
		SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>	
	//<METODOS_SUBFORM>	
	//</METODOS_SUBFORM>	
	//<METODOS_ADICIONALES>	
	//</METODOS_ADICIONALES>

	/**
     * Arma la estructura en la que se envian las llaves de los comprobantes que
     * se van a afectar
     * 
     * @param listaAfectar
     * el listado de los comprobantes a afectar que han sido seleccionados en el
     * formulario
     * @return Cadena con la informacion de los comprobantes a afectar
     */
    private String listaComprobanteAfectar(List<Registro> listaAfectar) {
        String comprobante;
        StringBuilder rta = new StringBuilder();

        for (Registro reg : listaAfectar) {
            rta.append("(''"
                + reg.getCampos().get(GeneralParameterEnum.ANO.getName())
                + "'',")
                            .append("''" + reg.getCampos().get(
                                            ComprobanteCntAfectarControladorEnum.TIPO
                                                            .getValue())
                                + "'',")
                            .append("''" + reg.getCampos().get(cNumero) + "''")
                            .append(",")
                            .append("''" + reg.getCampos().get(GeneralParameterEnum.SALDO.getName()) + "'')")
                            .append(",");

        }
        comprobante = rta.toString();
        comprobante = comprobante.substring(0, comprobante.length() - 1);

        return comprobante;
    }
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 */
	@Override
	public void cargarRegistro() {
		//<CODIGO_DESARROLLADO>
		precargarRegistro();
		//</CODIGO_DESARROLLADO>
	}

	/**
	 * Evalua si el valor del campo que ingresa por parametro se encuentra vacio
	 * dentro del Map que tambien es enviado por parametro
	 * 
	 * @param parametros
	 * Estructura que almacena los parametros que han sido enviados desde otros
	 * formularios
	 * @param campo
	 * El campo a evaluar dentro de la estructura de Map
	 * @return El valor del campo o cadena vacia si su valor es nulo
	 */
	private String validarCampos(Map<String, Object> parametros, String campo) {
		return SysmanFunciones.nvl(parametros.get(campo), "").toString();

	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado cuando se cierra el formulario
	 * 
	 */
	public void cerrarFormulario() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}
	/**
	 * Metodo ejecutado cuando se cierra el formulario
	 * 
	 */
	public void ejecutarrcCerrar(){
		//<CODIGO_DESARROLLADO>
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("rid", rowIdComprobante);
		parametros.put("ano", String.valueOf(anoComprobante));
		parametros.put("mes", mesComprobante);
		parametros.put("tipoMov", tipoComprobante);
		parametros.put("opcionMenu", opcionMenu);

		Direccionador direccionador = new Direccionador();
		direccionador.setParametros(parametros);
		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.COMPROBANTECNTS_CONTROLADOR
						.getCodigo()));
		SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
		//</CODIGO_DESARROLLADO>
	}
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaListaFacturacion
	 * 
	 * @return listaListaFacturacion
	 */
	public RegistroDataModelImpl getListaListaFacturacion() {
		return listaListaFacturacion;
	}
	/**
	 * Asigna la lista listaListaFacturacion
	 * 
	 * @param listaListaFacturacion
	 * Variable a asignar en  listaListaFacturacion
	 */
	public void setListaListaFacturacion(RegistroDataModelImpl listaListaFacturacion) {
		this.listaListaFacturacion = listaListaFacturacion;
	}
	/**
	 * Retorna la lista listaListaAnticipo
	 * 
	 * @return listaListaAnticipo
	 */
	public RegistroDataModelImpl getListaListaAnticipo() {
		return listaListaAnticipo;
	}
	/**
	 * Asigna la lista listaListaAnticipo
	 * 
	 * @param listaListaAnticipo
	 * Variable a asignar en  listaListaAnticipo
	 */
	public void setListaListaAnticipo(RegistroDataModelImpl listaListaAnticipo) {
		this.listaListaAnticipo = listaListaAnticipo;
	}
	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}
	/**
	 * @param titulo the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	/**
	 * @return the tipos
	 */
	public String getTipos() {
		return tipos;
	}
	/**
	 * @param tipos the tipos to set
	 */
	public void setTipos(String tipos) {
		this.tipos = tipos;
	}
	/**
	 * @return the saldoF
	 */
	public Double getSaldoF() {
		return saldoF;
	}
	/**
	 * @param saldoF the saldoF to set
	 */
	public void setSaldoF(Double saldoF) {
		this.saldoF = saldoF;
	}
	/**
	 * @return the saldoA
	 */
	public Double getSaldoA() {
		return saldoA;
	}
	/**
	 * @param saldoA the saldoA to set
	 */
	public void setSaldoA(Double saldoA) {
		this.saldoA = saldoA;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	//<SET_GET_LISTAS_SUBFORM>
	//</SET_GET_LISTAS_SUBFORM>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_ADICIONALES>	
	//</SET_GET_ADICIONALES>
}
