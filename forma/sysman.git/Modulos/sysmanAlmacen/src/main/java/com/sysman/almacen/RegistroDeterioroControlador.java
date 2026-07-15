/*-
 * IndicadorDeterioroControlador.java
 *
 * 1.0
 * 
 * 24/02/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.RegistroDeterioroControladorEnum;
import com.sysman.almacen.enums.RegistroDeterioroControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import co.com.sysman.comun.excepcion.NegocioExcepcion;
import sysman.util.consumo.AuditoriaProcesador2;
import sysman.util.consumo.IAuditoria;
import sysman.util.consumo.enums.EnumAccionAuditoria;
import sysman.util.consumo.enums.EnumAcciones;
import sysman.util.consumo.enums.EnumCodAuditoria;
import sysman.util.consumo.pojo.PojoAuditoria;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario de almacen que permite gestionar los registros de
 * deterioro
 *
 * @version 1.0, 24/02/2020
 * @author cochoa
 */
@ManagedBean
@ViewScoped
public class RegistroDeterioroControlador extends BeanBaseContinuoAcmeImpl implements IAuditoria{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * atributo en el cual se almacena el valor del elemento
     */
    private String elemento;
    /**
     * atributo en el cual se almacena el valor de la serie
     */
    private String serie;
    /**
     * atributo en el cual se almacena el valor de la fecha
     */
    private Date fechaCambio;
    /**
     * Lista que almacena el elemento
     */
    private RegistroDataModelImpl listaElemento;
    /**
     * Lista que almacena el elemento
     */
    private RegistroDataModelImpl listaElementoE;
    /**
     * Lista que almacena la placa
     */
    private RegistroDataModelImpl listaPlaca;
    /**
     * Lista que almacena la placa
     */
    private RegistroDataModelImpl listaPlacaE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se almacena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
	 * Objetos estaticos
	 */
    private static final String CAMPO_COMPANIA = "COMPANIA";
    private static final String CAMPO_ELEMENTO = "ELEMENTO";
    private static final String CAMPO_SERIE = "SERIE";
    private static final String CAMPO_FECHACAMBIO = "FECHACAMBIO";
    private static final String CAMPO_VIDAUTIL = "VIDAUTIL";
    private static final String CAMPO_DETERIORO = "DETERIORO";
    private static final String CAMPO_HORA = "HORA";
    private static final String CAMPO_NIIF_VLRLIBROS = "NIIF_VLRLIBROS";
    private static final String CAMPO_NIIF_VALOR_TOTAL = "NIIF_VALOR_TOTAL";
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private String accionAuditoria;
    
    //Procesos Auditoria 
    private Map<String, Object> valAnterior = new HashMap<>();
    private Map<String, Object> valActual = new HashMap<>();
    /**
     * Crea una nueva instancia de IndicadorDeterioroControlador
     */
    public RegistroDeterioroControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 2164
            numFormulario = GeneralCodigoFormaEnum.REGISTRO_DETERIORO_CONTROLADOR
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
        enumBase = GenericUrlEnum.REGISTRO_DETERIORO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaElemento();
        cargarListaElementoE();
        cargarListaPlaca();
        cargarListaPlacaE();
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaElemento
     *
     */
    public void cargarListaElemento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroDeterioroControladorUrlEnum.URL112154
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put("COMPANIA", String.valueOf(compania));

        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, RegistroDeterioroControladorEnum.CODIGOELEMENTO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaElemento
     *
     */
    public void cargarListaElementoE() {
        listaElementoE = listaElemento;
    }

    /**
     * 
     * Carga la lista listaPlaca
     *
     */
    public void cargarListaPlaca() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroDeterioroControladorUrlEnum.URL112156
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ELEMENTO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ELEMENTO.getName()));

        listaPlaca = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.SERIE.getName());
    }

    /**
     * 
     * Carga la lista listaPlaca
     *
     */
    public void cargarListaPlacaE() {
        listaPlacaE = listaPlaca;
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control Deterioro
     * 
     */
    public void cambiarDeterioro() {

        BigDecimal valorLibros = new BigDecimal(SysmanFunciones.nvl(registro
                        .getCampos()
                        .get(RegistroDeterioroControladorEnum.NIIF_VLRLIBROS
                                        .getValue()),
                        "0").toString());

        BigDecimal valorDeterioro = new BigDecimal(SysmanFunciones.nvl(registro
                        .getCampos()
                        .get(RegistroDeterioroControladorEnum.DETERIORO
                                        .getValue()),
                        "0").toString());

        if (valorDeterioro.compareTo(valorLibros) > 0) {
            registro.getCampos().put(RegistroDeterioroControladorEnum.DETERIORO
                            .getValue(), null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4351"));
        }

        elemento = String.valueOf(registro.getCampos()
                        .get(GeneralParameterEnum.ELEMENTO.getName()));
        serie = String.valueOf(registro.getCampos()
                        .get(GeneralParameterEnum.SERIE.getName()));

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);

        param.put(GeneralParameterEnum.SERIE.getName(), serie);

        param.put("FECHA_CAMBIO", fechaCambio);

        try {
            double deter = 0;
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            RegistroDeterioroControladorUrlEnum.URL369
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rs != null) {
                deter = Double.parseDouble(rs.getCampos()
                                .get("SUMA_DETERIORO").toString());
            }

            if (valorDeterioro.doubleValue() < 0 &&
                valorDeterioro.doubleValue() <= (deter * -1)) {
                registro.getCampos()
                                .put(RegistroDeterioroControladorEnum.DETERIORO
                                                .getValue(), null);
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4352"));
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al cambiar el control FechaCambio
     * 
     * 
     */
    public void cambiarFechaCambio() {
        fechaCambio = (Date) registro.getCampos()
                        .get(RegistroDeterioroControladorEnum.FECHACAMBIO
                                        .getValue());
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaElemento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */

    public void seleccionarFilaElemento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.ELEMENTO.getName(),
                        registroAux.getCampos().get(
                                        RegistroDeterioroControladorEnum.CODIGOELEMENTO
                                                        .getValue()));

        registro.getCampos().put(RegistroDeterioroControladorEnum.FECHACAMBIO
                        .getValue(), null);
        registro.getCampos().put(
                        RegistroDeterioroControladorEnum.DETERIORO.getValue(),
                        "");
        registro.getCampos().put(GeneralParameterEnum.SERIE.getName(), "");
        registro.getCampos()
                        .put(RegistroDeterioroControladorEnum.NIIF_VALOR_TOTAL
                                        .getValue(), "");
        registro.getCampos()
                        .put(RegistroDeterioroControladorEnum.NIIF_VLRLIBROS
                                        .getValue(), "");
        registro.getCampos().put(
                        RegistroDeterioroControladorEnum.VIDAUTIL.getValue(),
                        "");

        registro.getCampos().put(
                        RegistroDeterioroControladorEnum.NOMBRELARGO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        RegistroDeterioroControladorEnum.NOMBRELARGO
                                                        .getValue()));

        cargarListaPlaca();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaElementoE
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElementoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGOELEMENTO");

        registro.getCampos().put(GeneralParameterEnum.SERIE.getName(), "");
        registro.getCampos()
                        .put(RegistroDeterioroControladorEnum.NIIF_VALOR_TOTAL
                                        .getValue(), "");
        registro.getCampos()
                        .put(RegistroDeterioroControladorEnum.NIIF_VLRLIBROS
                                        .getValue(), "");

        registro.getCampos().put(
                        RegistroDeterioroControladorEnum.NOMBRELARGO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        RegistroDeterioroControladorEnum.NOMBRELARGO
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaPlaca
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlaca(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.SERIE.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SERIE
                                                        .getName()));

        registro.getCampos().put(
                        RegistroDeterioroControladorEnum.NIIF_VALOR_TOTAL
                                        .getValue(),

                        String.valueOf(registroAux.getCampos()
                                        .get(RegistroDeterioroControladorEnum.NIIF_VALOR_TOTAL
                                                        .getValue())));

        registro.getCampos().put(
                        RegistroDeterioroControladorEnum.NIIF_VLRLIBROS
                                        .getValue(),

                        String.valueOf(registroAux.getCampos()
                                        .get(RegistroDeterioroControladorEnum.NIIF_VLRLIBROS
                                                        .getValue())));
        
        registro.getCampos().put("ORIGEN", String.valueOf(registroAux.getCampos().get("ORIGEN")));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaPlaca
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlacaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("SERIE");
        registro.getCampos().put(
                        RegistroDeterioroControladorEnum.NIIF_VALOR_TOTAL
                                        .getValue(),

                        String.valueOf(registroAux.getCampos()
                                        .get(RegistroDeterioroControladorEnum.NIIF_VALOR_TOTAL
                                                        .getValue())));

        registro.getCampos().put(
                        RegistroDeterioroControladorEnum.NIIF_VLRLIBROS
                                        .getValue(),

                        String.valueOf(registroAux.getCampos()
                                        .get(RegistroDeterioroControladorEnum.NIIF_VLRLIBROS
                                                        .getValue())));
    }

    // </METODOS_COMBOS_GRANDES>
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

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put("HORA", new Date());
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
    	/**Proceso auitoria*/
        accionAuditoria = EnumAcciones.INSERT.getValue();
        ejecutarAuditoria();
        
        return true;
    }

    @Override
    public boolean actualizarAntes() {
 
        BigDecimal valorLibros = new BigDecimal(SysmanFunciones.nvl(registro
                        .getCampos()
                        .get(RegistroDeterioroControladorEnum.NIIF_VLRLIBROS
                                        .getValue()),
                        "0").toString());

        BigDecimal valorDeterioro = new BigDecimal(SysmanFunciones.nvl(registro
                        .getCampos()
                        .get(RegistroDeterioroControladorEnum.DETERIORO
                                        .getValue()),
                        "0").toString());

        fechaCambio = (Date) registro.getCampos()
                        .get(RegistroDeterioroControladorEnum.FECHACAMBIO
                                        .getValue());

        if (valorDeterioro.compareTo(valorLibros) > 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4351"));
            registro.getCampos().put(RegistroDeterioroControladorEnum.DETERIORO
                            .getValue(), null);
            return false;
        }

        elemento = String.valueOf(registro.getCampos()
                        .get(GeneralParameterEnum.ELEMENTO.getName()));
        serie = String.valueOf(registro.getCampos()
                        .get(GeneralParameterEnum.SERIE.getName()));

        Map<String, Object> param = new TreeMap<>();
        try {
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);

            param.put(GeneralParameterEnum.SERIE.getName(), serie);

            param.put("FECHA_CAMBIO",
                            SysmanFunciones.convertirAFechaCadena(fechaCambio));

            double deter = 0;
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            RegistroDeterioroControladorUrlEnum.URL369
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rs != null) {
                deter = Double.parseDouble(SysmanFunciones.nvl(rs.getCampos()
                                .get("SUMA_DETERIORO"), "0").toString());
            }

            if (valorDeterioro.doubleValue() < 0 &&
                valorDeterioro.doubleValue() <= (deter * -1)) {
                registro.getCampos()
                                .put(RegistroDeterioroControladorEnum.DETERIORO
                                                .getValue(), null);
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4352"));

                return false;
            }

        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
     
        obtenerValoresAnteriores();
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
    	/**Proceso auitoria*/
    	if (!valAnterior.isEmpty()) {
    		accionAuditoria = EnumAcciones.UPDATE.getValue();
    		ejecutarAuditoria();
    	}
	    
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>    
    	obtenerValoresAnteriores();
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
    	/**Proceso auitoria*/
        accionAuditoria = EnumAcciones.DELETE.getValue();
        ejecutarAuditoria();
        
        return true;
    }

    @Override
    public void removerCombos() {

        registro.getCampos().remove(
                        GeneralParameterEnum.COMPANIA
                                        .getName());

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaElemento
     * 
     * @return listaElemento
     */
    public RegistroDataModelImpl getListaElemento() {
        return listaElemento;
    }

    /**
     * Asigna la lista listaElemento
     * 
     * @param listaElemento
     * Variable a asignar en listaElemento
     */
    public void setListaElemento(RegistroDataModelImpl listaElemento) {
        this.listaElemento = listaElemento;
    }

    /**
     * Retorna la lista listaElemento
     * 
     * @return listaElemento
     */
    public RegistroDataModelImpl getListaElementoE() {
        return listaElementoE;
    }

    /**
     * Asigna la lista listaElemento
     * 
     * @param listaElemento
     * Variable a asignar en listaElemento
     */
    public void setListaElementoE(RegistroDataModelImpl listaElementoE) {
        this.listaElementoE = listaElementoE;
    }

    /**
     * Retorna la lista listaPlaca
     * 
     * @return listaPlaca
     */
    public RegistroDataModelImpl getListaPlaca() {
        return listaPlaca;
    }

    /**
     * Asigna la lista listaPlaca
     * 
     * @param listaPlaca
     * Variable a asignar en listaPlaca
     */
    public void setListaPlaca(RegistroDataModelImpl listaPlaca) {
        this.listaPlaca = listaPlaca;
    }

    /**
     * Retorna la lista listaPlaca
     * 
     * @return listaPlaca
     */
    public RegistroDataModelImpl getListaPlacaE() {
        return listaPlacaE;
    }

    /**
     * Asigna la lista listaPlaca
     * 
     * @param listaPlaca
     * Variable a asignar en listaPlaca
     */
    public void setListaPlacaE(RegistroDataModelImpl listaPlacaE) {
        this.listaPlacaE = listaPlacaE;
    }
    /**
    * Obtiene el codigo del proceso que se va a ejecutar en la auditoria
    **/
    public String obtenerCodProceso() {
    	String codProceso = "0";
    	
    	try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get("ORIGEN"));
			
			Registro proceso = RegistroConverter.toRegistro(
				requestManager.get(UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
												RegistroDeterioroControladorUrlEnum.URL1749006
														.getValue())
								.getUrl(),param));
			
			if (proceso != null) {
				switch (proceso.getCampos().get("TIPOELEMENTO").toString()) {
	    		case "D":
	    			codProceso = EnumCodAuditoria.DETERIORO_ELEMENTOS_DEVOLUTIVOS.getValue();
	    			break;
	    		case "N":
	    			codProceso = EnumCodAuditoria.DETERIORO_ELEMENTOS_INMUEBLES.getValue();
	    			break;
	    		default:
	    			break;
	    		}
			}
	    } catch (SystemException e) {
			e.printStackTrace();
		} 	
    	
    	return codProceso;
    }
    /**
     * Arma la referencia del registro
     **/
	public String armarReferencia() {
		String referencia = "";
		
		try {
			referencia = "DETERIORO: " + "Origen: " + registro.getCampos().get("ORIGEN") +
						 " Elemento: "+ registro.getCampos().get(CAMPO_ELEMENTO) +
					     " Serie: " + registro.getCampos().get(CAMPO_SERIE) +
					     " Fecha Cambio: " + SysmanFunciones.convertirAFechaCadena(
					    		 (Date)registro.getCampos().get(CAMPO_FECHACAMBIO),"dd/MM/yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return referencia;
	}
    /**
     * Ejecuta los procesos de auditoria
     **/
	@Override
	public void ejecutarAuditoria() {
		try {
			PojoAuditoria pojoAudit = new PojoAuditoria();
			AuditoriaProcesador2 auditoria = new AuditoriaProcesador2();
			
			pojoAudit.setCodproceso(obtenerCodProceso());
			pojoAudit.setUsuario(SessionUtil.getUser().getCodigo());
			pojoAudit.setCodCompania(compania);
			pojoAudit.setCodEntidad(auditoria.obtenerCodEntidad(compania));
			pojoAudit.setEquipo(InetAddress.getLocalHost().getHostName());
			pojoAudit.setIp(InetAddress.getLocalHost().getHostAddress());
			pojoAudit.setReferencia(armarReferencia());

			if (accionAuditoria.equals(EnumAcciones.INSERT.getValue())) {	
				pojoAudit.setAccionAuditar(EnumAccionAuditoria.CREAR.getValue());
				obtenerValorActual();
				pojoAudit.setValActual(valActual);
			} else if (accionAuditoria.equals(EnumAcciones.UPDATE.getValue())) {
				pojoAudit.setAccionAuditar(EnumAccionAuditoria.ACTUALIZAR.getValue());
				pojoAudit.setValAnterior(valAnterior);  
				obtenerValorActual();
				pojoAudit.setValActual(valActual);
			} else if (accionAuditoria.equals(EnumAcciones.DELETE.getValue())) {
				pojoAudit.setAccionAuditar(EnumAccionAuditoria.BORRAR.getValue());
				pojoAudit.setValAnterior(valAnterior);
			}
			auditoria.validarDatos(pojoAudit);
		} catch (NegocioExcepcion | UnknownHostException e) {
			logger.error("Error ejecutar auditoria Motivo {}  ", e );
			e.printStackTrace();
		}
	}
	/**Campos auditables, quedan registrados como valActual
	 */
	@Override
	public void obtenerValorActual(Object... datos) {
		try {	
			valActual = new HashMap<>(); 
			valActual.put(CAMPO_COMPANIA, compania);
			valActual.put(CAMPO_ELEMENTO, registro.getCampos().get(CAMPO_ELEMENTO));
			valActual.put(CAMPO_SERIE, registro.getCampos().get(CAMPO_SERIE));
			valActual.put(CAMPO_FECHACAMBIO, SysmanFunciones.convertirAFechaCadena(
					(Date)registro.getCampos().get(CAMPO_FECHACAMBIO), "dd/MM/yyyy"));
			valActual.put(CAMPO_VIDAUTIL, registro.getCampos().get(CAMPO_VIDAUTIL));
			valActual.put(CAMPO_DETERIORO, registro.getCampos().get(CAMPO_DETERIORO));
			valActual.put(GeneralParameterEnum.CREATED_BY.getName(), registro.getCampos()
					.get(GeneralParameterEnum.CREATED_BY.getName()));
			valActual.put(GeneralParameterEnum.MODIFIED_BY.getName(), registro.getCampos()
					.get(GeneralParameterEnum.MODIFIED_BY.getName()));
			valActual.put(GeneralParameterEnum.DATE_CREATED.getName(), registro.getCampos()
					.get(GeneralParameterEnum.DATE_CREATED.getName()));
			valActual.put(GeneralParameterEnum.DATE_MODIFIED.getName(), registro.getCampos()
					.get(GeneralParameterEnum.DATE_MODIFIED.getName()));
			valActual.put(CAMPO_HORA, SysmanFunciones.convertirAFechaCadena(
					(Date)registro.getCampos().get(CAMPO_HORA), "HH:mm:ss"));
			valActual.put(CAMPO_NIIF_VLRLIBROS, registro.getCampos().get(CAMPO_NIIF_VLRLIBROS));
			valActual.put(CAMPO_NIIF_VALOR_TOTAL, registro.getCampos().get(CAMPO_NIIF_VALOR_TOTAL));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	/**Consulta los valores que se encuentran en la base antes de actualizar 
	 * para el registro que tiene como referencia
	 */
	@Override
	public void obtenerValoresAnteriores(Object... datos) {
		try {
			valAnterior = new HashMap<>();
			Registro rs = null;
			Map<String, Object> param = new TreeMap<>();
			
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ELEMENTO.getName(), registro.getCampos().get(CAMPO_ELEMENTO));
			param.put(GeneralParameterEnum.SERIE.getName(), registro.getCampos().get(CAMPO_SERIE));
			param.put(CAMPO_FECHACAMBIO, SysmanFunciones.convertirAFechaCadena(
					(Date)registro.getCampos().get(CAMPO_FECHACAMBIO),"dd/MM/yyyy HH:mm:ss"));
			
			rs = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
													RegistroDeterioroControladorUrlEnum.URL1749005
															.getValue())
									.getUrl(),param));
			
			if (rs == null) {
				valAnterior = Collections.emptyMap();
			} else {
				valAnterior = rs.getCampos();
			}
		} catch (SystemException | ParseException e) {
			e.printStackTrace();
		}
	}
}
