/*-
c * GenerarcptepptalCausacionControlador.java
 *
 * 1.0
 * 
 * 18/06/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
import com.sysman.contabilidad.enums.ComprobanteCntAfectarControladorEnum;
import com.sysman.contabilidad.enums.GenerarcptepptalCausacionControladorEnum;
import com.sysman.contabilidad.enums.GenerarcptepptalCausacionControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 18/06/2024
 * @author lvega
 */
@ManagedBean
@ViewScoped
public class  GenerarcptepptalCausacionControlador extends BeanBaseDatosAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	
	private Double saldo;
	private Double saldoRubro;
	private RegistroDataModelImpl listaLista;
	   /**
     * Define la URL que obtiene los registros que se cargan en la lista principal del formulario
     */
    UrlBean urlconsultaLista;
    /**
     * Almacena los parametros a enviar al servicio, dependiendo la URL que se defina en el atributo urlconsultaLista
     */
    Map<String, Object> paramConsultaLista;
    
    /**
     * Implementacion del EJB de EjbContabilidadCincoRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y se
     * encuentran almacenadas en el paquete PCK_CONTABILIDAD5
     */
    @EJB
    private EjbContabilidadCincoRemote ejbContabilidadCinco;
    
	private Registro saldoRegistro;

	private Map<String, Object> ridComprobante;

	private String mes;

	private String ano;

	private String tipoComp;

	private String numeroComp;

	private String tercero;
	
	private String nombreComprobante;

	private double vlrBaseIva;
	
	private double vlrBase;

	private String opcionMenu;

	private String sucursal;
	
	private Date fecha;
	
	int varCausar;

    private List<Registro> listaSeleccionados;

	private BigDecimal totalDebito;

	private BigDecimal totalSaldo;

	private BigDecimal totalPagar;

	private BigDecimal totalDetDebito;

	private BigDecimal totalDetPagar;

	private BigDecimal totalDetSaldo;
    
    private static final String TB_TB686 = "TB_TB686";



	/**
	 * Crea una nueva instancia de GenerarcptepptalCausacionControlador
	 */
	public GenerarcptepptalCausacionControlador() {
		super();
		SessionUtil.setSessionVar("modulo", "1");
        saldoRegistro = new Registro();
		compania = SessionUtil.getCompania();
		saldo = 0.0;
		saldoRubro = 0.0;
		try {
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null)
			{
				ridComprobante = (Map<String, Object>) parametrosEntrada.get("rid");
				mes = extraerString(parametrosEntrada.get("mes"));
				ano = extraerString(parametrosEntrada.get("ano"));
				tipoComp = extraerString(parametrosEntrada.get("tipoComp"));
				numeroComp = extraerString(parametrosEntrada.get("numeroComp"));
				tercero = extraerString(parametrosEntrada.get("tercero"));
				nombreComprobante = extraerString(parametrosEntrada.get("nombreComprobante"));
				String vlrBaseIvaE = parametrosEntrada.get("vlrBaseIva").toString();
				vlrBaseIva = Double.parseDouble(vlrBaseIvaE);
				String vlrBaseE = parametrosEntrada.get("vlrBase").toString();
				vlrBase = Double.parseDouble(vlrBaseE);
				opcionMenu = extraerString(parametrosEntrada.get("opcionMenu"));
				sucursal = extraerString(parametrosEntrada.get("sucursal"));
				fecha = (Date) parametrosEntrada.get("fecha");
		
			}
			else{
				SessionUtil.redireccionarMenuPermisos();
			} 
			numFormulario = GeneralCodigoFormaEnum.FRM_GENERAR_CPTEPPTAL_CAUSACION_CONTROLADOR.getCodigo();
			validarPermisos();
			paramConsultaLista = new TreeMap<>();
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
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
	public void inicializar(){
		inicializarComprobanteCntAfectar();
		cargarListaLista();
		abrirFormulario();
		JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('height','770px');");
		JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('width','1830px');");
		JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('top','45px');");
		JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('left','125px');");
		
		JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('width','1800px');");
		JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('height','550px');");

	}
	
	
	private void inicializarComprobanteCntAfectar() {
		
		SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
		urlconsultaLista = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		GenerarcptepptalCausacionControladorUrlEnum.URL38069
                        .getValue());
		
		paramConsultaLista.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		paramConsultaLista.put(GenerarcptepptalCausacionControladorEnum.FECHAINI.getValue(), formatFecha.format(fecha));
		paramConsultaLista.put(GenerarcptepptalCausacionControladorEnum.TERCERO.getValue(), tercero);//tercero
		paramConsultaLista.put(GenerarcptepptalCausacionControladorEnum.ANO.getValue(), ano);
				
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		CalcularTotales();
	}
	
	/**
	 * 
	 * Carga la lista listaLista
	 *
	 */
	public void cargarListaLista(){
		
			try {
				
				listaLista = new RegistroDataModelImpl(urlconsultaLista.getUrl(),
						urlconsultaLista.getUrlConteo().getUrl(),
						paramConsultaLista,
						false,
						CacheUtil.getLlaveServicio(urlConexionCache,
								GenerarcptepptalCausacionControladorEnum.DETALLE_COMPROBANTE_PPTAL.getValue()),
						true);
			} catch (SysmanException e) {
				 logger.error(e.getMessage(), e);
		            JsfUtil.agregarMensajeError(e.getMessage());
		  }     
		
	}
	
	
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * en la vista
	 *
	 *
	 */
	public void oprimirAceptar() {
try {
        if (listaLista.getSeleccionados().isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(TB_TB686));
            return;
        }
        else {
            listaSeleccionados = listaLista.getSeleccionados();
        }
         
        if(generarComprobantePptalvarios()) {
        	
        	JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_INGRESADO"));
        	//RequestContext.getCurrentInstance().closeDialog(null);
        	
        }
		

		}catch (NumberFormatException e) {
             logger.error(e.getMessage(), e);
             JsfUtil.agregarMensajeError(e.getMessage());

         }      
 		
    }

    /**
     * Desde un registro de comprobantes contables, realiza la generacion de un
     * comprobante presupuestal asociado.
     * 
     * @param comprobante
     * Comprobante a afectar
     * @return
     */
    public boolean generarComprobantePptalvarios() {
    	varCausar=1;
        try {   

            ejbContabilidadCinco.generarComprobantePresupuestalVarios(compania,
                            Integer.parseInt(ano),
                            tipoComp,
                            new BigInteger(numeroComp), true,
                            listaComprobanteAfectar(listaSeleccionados),
                            "(''" + tercero + "'',''"
                                + sucursal + "'')",
                            SessionUtil.getUser().getCodigo(), listaComprobanteAfectarCAuto(listaSeleccionados),varCausar);
            return true;
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }
    }


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
                + SysmanFunciones.ano((Date) reg.getCampos().get(GeneralParameterEnum.FECHA.getName()))
                + "'',")
                            .append("''" + reg.getCampos().get(
                                            GeneralParameterEnum.TIPO_CPTE
                                                            .getName())
                                + "'',")
                            .append("''" + reg.getCampos().get(GenerarcptepptalCausacionControladorEnum.COMPROBANTE.getValue()) + "'')")
                            .append(",");

        }
        comprobante = rta.toString();
        comprobante = comprobante.substring(0, comprobante.length() - 1);

        return comprobante;
    }
    
    
    private String listaComprobanteAfectarCAuto(List<Registro> listaAfectar) {
        String comprobante;
        StringBuilder rta = new StringBuilder();
        
        for (Registro reg : listaAfectar) {
        	String consecutivo=(String.format("%05d", reg.getCampos().get("CONSECUTIVO")).replace(' ', '0'));
        	String vlrPagar = reg.getCampos().get("VALOR_A_PAGAR").toString().trim().replaceAll("\\s+", "").replace(",", "");
        	
        	rta.append(".col.").append("(.reg."
                + SysmanFunciones.ano((Date) reg.getCampos().get(GeneralParameterEnum.FECHA.getName()))
                + ".reg.,")
                            .append(".reg." + reg.getCampos().get(
                                            GeneralParameterEnum.TIPO_CPTE
                                                            .getName())
                                + ".reg.,")
                            .append(".reg." + consecutivo + ".reg.,")
                            .append(".reg." + reg.getCampos().get(GenerarcptepptalCausacionControladorEnum.COMPROBANTE.getValue()) + ".reg.,")
                            .append(".reg." + vlrPagar + ".reg.)");
                            //.append(".col.");

        }
        comprobante = rta.append(".col.").toString();
     //   comprobante = comprobante.substring(0, comprobante.length() - 1);

        return comprobante;
    }
    
	public void cerrarFormulario() {
		
	}
	
    /**
     * Metodo que se ejecuta al momento cambiar el saldo para guardar el registro a modificar
     * 
     * @param reg registro seleccionado para cambiar el saldo
     */
    public void cambiarSaldo(int index) {  
    	
    	 List<Registro> listaSeleccion = listaLista.getSeleccionados();
    	 Registro registro = listaSeleccion.get(index);
        saldoRegistro = registro;        
    }
    
    /**
     * Actualiza el valor del saldo cambiado desde el dialog al la lista de seleccionados
     */
    public void aceptarcambiaSaldo() {
    	
        List<Registro> listaSeleccion = listaLista.getSeleccionados();
        for (int i = 0; i < listaSeleccion.size(); i++) {
    
        	Registro registro = listaSeleccion.get(i);
        	saldoRubro =  Double.parseDouble(registro.getCampos().get("SALDO").toString().replace(",", ""));
        	if( registro.getLlave()== saldoRegistro.getLlave()) {
        		
        		if (saldo <= saldoRubro) {
        			DecimalFormat df = new DecimalFormat("#.##");
        			double saldoRestante = saldoRubro - saldo;
        			String saldoRestanteStr = df.format(saldoRestante).replace(",", ".");
        			String saldoStr = df.format(saldo).replace(",", ".");

        		    registro.getCampos().put(GenerarcptepptalCausacionControladorEnum.VALOR_A_PAGAR.getValue(), saldoStr);
        		    registro.getCampos().put(GenerarcptepptalCausacionControladorEnum.SALDO.getValue(), saldoRestanteStr);
        		} else {
        			JsfUtil.agregarMensajeAlerta("No se puede insertar un valor mayor al saldo del rubro");
                	return;
                }
            }
		}
        
        calcularTotalSeleccionados();
    }

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaLista
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaLista(SelectEvent event) {
        listaSeleccionados = listaLista.getSeleccionados();
        calcularTotalSeleccionados();
	}
	
	/*
	 * Este codigo se agrego pero se debe tener cuidado con la forma, ya que no existe ningun cambio en el sysmanK porque no se puede asignar. 
	 */
	public void calcularTotalSeleccionados() {
	    totalDetDebito = BigDecimal.ZERO;
	    totalDetSaldo = BigDecimal.ZERO;
	    totalDetPagar = BigDecimal.ZERO;

	    if (listaSeleccionados != null) {
	        for (Registro reg : listaSeleccionados) {
	            Object valorTotalDebito = reg.getCampos().get(GeneralParameterEnum.VALOR_DEBITO.getName());
	            Object valorTotalSaldo = reg.getCampos().get(GeneralParameterEnum.SALDO.getName());
	            Object valorTotalPagar = reg.getCampos().get("VALOR_A_PAGAR");
	            
	            if (valorTotalDebito instanceof BigDecimal) {
	            	totalDetDebito = totalDetDebito.add((BigDecimal) valorTotalDebito);
	            } else if (valorTotalDebito instanceof Number) {
	            	totalDetDebito = totalDetDebito.add(BigDecimal.valueOf(((Number) valorTotalDebito).doubleValue()));
	            }
	            else if (valorTotalDebito instanceof String) {
	            	String valorDebito = valorTotalDebito.toString().replace(",", "");
	            	totalDetDebito = totalDetDebito.add(BigDecimal.valueOf(Double.parseDouble(valorDebito)));
	            }
	            
	            if (valorTotalSaldo instanceof BigDecimal) {
	            	totalDetSaldo = totalDetSaldo.add((BigDecimal) valorTotalSaldo);
	            } else if (valorTotalSaldo instanceof Number) {
	            	totalDetSaldo = totalDetSaldo.add(BigDecimal.valueOf(((Number) valorTotalSaldo).doubleValue()));
	            } else if (valorTotalSaldo instanceof String) {
	            	String valorSaldo = valorTotalSaldo.toString().replace(",", "");
	            	totalDetSaldo = totalDetSaldo.add(BigDecimal.valueOf(Double.parseDouble(valorSaldo)));
	            }
	            
	            if (valorTotalPagar instanceof BigDecimal) {
	            	totalDetPagar = totalDetPagar.add((BigDecimal) valorTotalPagar);
	            } else if (valorTotalPagar instanceof Number) {
	            	totalDetPagar = totalDetPagar.add(BigDecimal.valueOf(((Number) valorTotalPagar).doubleValue()));
	            }
	            else if (valorTotalPagar instanceof String) {
	            	String valorTotal = valorTotalPagar.toString().replace(",", "");
	            	totalDetPagar = totalDetPagar.add(BigDecimal.valueOf(Double.parseDouble(valorTotal)));
	            }
	        }
	    }
	}
	
	/*
	 * Este codigo se agrego pero se debe tener cuidado con la forma, ya que no existe ningun cambio en el sysmanK porque no se puede asignar. 
	 */
	public void CalcularTotales() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();

			SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GenerarcptepptalCausacionControladorEnum.FECHAINI.getValue(), formatFecha.format(fecha));
			param.put(GenerarcptepptalCausacionControladorEnum.TERCERO.getValue(), tercero);
			param.put(GenerarcptepptalCausacionControladorEnum.ANO.getValue(), ano);


			Registro reg = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GenerarcptepptalCausacionControladorUrlEnum.URL38072.getValue()).getUrl(), param));

			if(reg != null) {

				totalDebito = new BigDecimal(SysmanFunciones.toString(reg.getCampos().get("SUMA_DEBITO")));
				totalSaldo = new BigDecimal(SysmanFunciones.toString(reg.getCampos().get("SUMA_SALDO")));
				totalPagar = new BigDecimal(SysmanFunciones.toString(reg.getCampos().get("SUMA_PAGAR")));

			}else {
				totalDebito = BigDecimal.ZERO;
				totalSaldo = BigDecimal.ZERO;
				totalPagar = BigDecimal.ZERO;
			}
			
			totalDetDebito = BigDecimal.ZERO;
		    totalDetSaldo = BigDecimal.ZERO;
		    totalDetPagar = BigDecimal.ZERO;

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}


	}
	
	/*
	 * Este codigo se agrego pero se debe tener cuidado con la forma, ya que no existe ningun cambio en el sysmanK porque no se puede asignar. 
	 */
	
	public void deseleccionarRegistro(Registro reg) {
	    listaLista.deselecionar(reg);
	    calcularTotalSeleccionados();
	}

	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaLista
	 * 
	 * @return listaLista
	 */
	public RegistroDataModelImpl getListaLista() {
		return listaLista;
	}
	/**
	 * Asigna la lista listaLista
	 * 
	 * @param listaLista
	 * Variable a asignar en  listaLista
	 */
	public void setListaLista(RegistroDataModelImpl listaLista) {
		this.listaLista = listaLista;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	@Override
	public void cargarRegistro() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void iniciarListasSubNulo() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void iniciarListasSub() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void iniciarListas() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void asignarOrigenDatos() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean insertarAntes() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean insertarDespues() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean actualizarAntes() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean actualizarDespues() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean eliminarAntes() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean eliminarDespues() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private String extraerString(Object object)
	{
		return object != null ? object.toString() : null;
	}
	
	/**
	 * @return the saldo
	 */
	public Double getSaldo() {
		return saldo;
	}
	/**
	 * @param saldo the saldo to set
	 */
	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}
	public List<Registro> getListaSeleccionados() {
		return listaSeleccionados;
	}
	public void setListaSeleccionados(List<Registro> listaSeleccionados) {
		this.listaSeleccionados = listaSeleccionados;
	}
	/**
	 * @return the totalDebito
	 */
	public BigDecimal getTotalDebito() {
		return totalDebito;
	}
	/**
	 * @param totalDebito the totalDebito to set
	 */
	public void setTotalDebito(BigDecimal totalDebito) {
		this.totalDebito = totalDebito;
	}
	/**
	 * @return the totalSaldo
	 */
	public BigDecimal getTotalSaldo() {
		return totalSaldo;
	}
	/**
	 * @param totalSaldo the totalSaldo to set
	 */
	public void setTotalSaldo(BigDecimal totalSaldo) {
		this.totalSaldo = totalSaldo;
	}
	/**
	 * @return the totalPagar
	 */
	public BigDecimal getTotalPagar() {
		return totalPagar;
	}
	/**
	 * @param totalPagar the totalPagar to set
	 */
	public void setTotalPagar(BigDecimal totalPagar) {
		this.totalPagar = totalPagar;
	}
	/**
	 * @return the totalDetDebito
	 */
	public BigDecimal getTotalDetDebito() {
		return totalDetDebito;
	}
	/**
	 * @param totalDetDebito the totalDetDebito to set
	 */
	public void setTotalDetDebito(BigDecimal totalDetDebito) {
		this.totalDetDebito = totalDetDebito;
	}
	/**
	 * @return the totalDetPagar
	 */
	public BigDecimal getTotalDetPagar() {
		return totalDetPagar;
	}
	/**
	 * @param totalDetPagar the totalDetPagar to set
	 */
	public void setTotalDetPagar(BigDecimal totalDetPagar) {
		this.totalDetPagar = totalDetPagar;
	}
	/**
	 * @return the totalDetSaldo
	 */
	public BigDecimal getTotalDetSaldo() {
		return totalDetSaldo;
	}
	/**
	 * @param totalDetSaldo the totalDetSaldo to set
	 */
	public void setTotalDetSaldo(BigDecimal totalDetSaldo) {
		this.totalDetSaldo = totalDetSaldo;
	}
	
	
	
}
