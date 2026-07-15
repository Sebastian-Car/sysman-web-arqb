/*-
 * FrmseleccionarprocesosjudicialesControlador.java
 *
 * 1.0
 * 
 * 04/07/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Calendar;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.ibm.icu.text.SimpleDateFormat;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisRemote;
import com.sysman.contabilidad.enums.ComprobanteCntAfectarControladorEnum;
import com.sysman.contabilidad.enums.FrmseleccionarprocesosjudicialesControladorUrlEnum;
import com.sysman.contabilidad.enums.SubdetallecomprobantecntsControladorEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 *
 * @version 1.0, 04/07/2024
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrmseleccionarprocesosjudicialesControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private static final String TB_TB686 = "TB_TB686";
	private final String compania;
    private final String cRowIdComprobante;
    private String anoComprobante;
    private String tipoComprobante;
    private String numeroComprobante;
    private String fechaComprobante;
    private List<Registro> listaSeleccionados;    
    
    private String terceroComprobante;
    private String sucursalComprobante;;
    double valorDocumento;
    
    private boolean vuelve;
//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaListacr;

//</DECLARAR_LISTAS_COMBO_GRANDE>
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
    
    @EJB
    private EjbContabilidadSeisRemote ejbContabilidadSeis;
    
	/**
	 * Crea una nueva instancia de FrmseleccionarprocesosjudicialesControlador
	 */
	public FrmseleccionarprocesosjudicialesControlador() 
	{
		super();
        compania = SessionUtil.getCompania();
        cRowIdComprobante = "rowIdComprobante";

        try {
            numFormulario = GeneralCodigoFormaEnum.COMPROBANTE_CNT_AFECTAR_CONTROLADOR
                            .getCodigo();
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                if (parametros.get(cRowIdComprobante) == null) {
                    vuelve = true;
                    return;
                }
               
                anoComprobante = validarCampos(parametros, "anoComprobante");
                tipoComprobante = validarCampos(parametros, "tipoComprobante");
                numeroComprobante = validarCampos(parametros,
                                "numeroComprobante");
                fechaComprobante = validarCampos(parametros,
                                "fechaComprobante");
                terceroComprobante = validarCampos(parametros,
                                "terceroComprobante");
                sucursalComprobante = validarCampos(parametros,
                                "sucursalComprobante");
            }
            validarPermisos();

            paramConsultaLista = new TreeMap<>();

        }
        catch (Exception ex) {
            Logger.getLogger(ComprobanteCntAfectarControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
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
		
		if (vuelve) {
            oprimirCancelar();
            return;
        }
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		Date fecha;
		try {
			fecha = formato.parse(fechaComprobante);		

			urlconsultaLista = UrlServiceUtil.getInstance()
	                .getUrlServiceByUrlByEnumID(
	                		FrmseleccionarprocesosjudicialesControladorUrlEnum.URL1935005
	                                                .getValue());
			paramConsultaLista.put(
			                GeneralParameterEnum.COMPANIA.getName(),
			                compania);
			paramConsultaLista.put(
					GeneralParameterEnum.MES.getName(),
					SysmanFunciones
	                .getParteFecha(fecha, Calendar.MONTH)+1);
			paramConsultaLista.put(
			                ComprobanteCntAfectarControladorEnum.TERCEROCOMPROBANTE
			                                .getValue(),
			                terceroComprobante);
			paramConsultaLista.put(
					GeneralParameterEnum.ANO
	                                .getName(),anoComprobante);
			paramConsultaLista.put(
			                ComprobanteCntAfectarControladorEnum.SUCURSALCOMPROBANTE
			                                .getValue(),
			                sucursalComprobante);		
			paramConsultaLista.put(
			                ComprobanteCntAfectarControladorEnum.FECHACOMPROBANTE
			                                .getValue(),
			                fechaComprobante);
			cargarListaListacr();
			abrirFormulario();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() 
	{
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaListacr
	 *
	 */
	public void cargarListaListacr() 
	{
		try {
            listaListacr = new RegistroDataModelImpl(urlconsultaLista.getUrl(),
                            urlconsultaLista.getUrlConteo().getUrl(),
                            paramConsultaLista, false,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "PROCESOS_JUDICIALES"),
                            true);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar en la vista
	 *
	 *
	 */
	public void oprimirAceptar() {
		 if (listaListacr.getSeleccionados().isEmpty()) {
	            JsfUtil.agregarMensajeInformativo(idioma.getString(TB_TB686));
	            return;
		 }
	     else {
	            listaSeleccionados = listaListacr.getSeleccionados();
	     }
		 try {
				ejbContabilidadSeis.generarIngresoOST(compania, Integer.parseInt(anoComprobante), tipoComprobante,
							new BigInteger(numeroComprobante), terceroComprobante, sucursalComprobante,
							listaComprobanteAfectar(listaSeleccionados), 
							SysmanFunciones.convertirAFecha(fechaComprobante),
							SessionUtil.getUser().getCodigo());
				
	            JsfUtil.agregarMensajeInformativo(
	                    idioma.getString("MSM_PROCESO_EJECUTADO"));
				RequestContext.getCurrentInstance().closeDialog(null);

			} catch (NumberFormatException | SystemException | ParseException e) {

				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
	}
	
	private String listaComprobanteAfectar(List<Registro> listaAfectar) {
        String comprobante;
        StringBuilder rta = new StringBuilder();

        for (Registro reg : listaAfectar) {
            rta.append("(''"
                + reg.getCampos().get(GeneralParameterEnum.COMPANIA.getName())
                + "'',")
                            .append("''" + reg.getCampos().get(
                            		GeneralParameterEnum.CODIGO.getName())
                                + "'',")
                            .append("''" + reg.getCampos().get(SubdetallecomprobantecntsControladorEnum.NUMEROPROCESO
                                    .getValue()) + "'')")
                            .append(",");
        }
        comprobante = rta.toString();
        comprobante = comprobante.substring(0, comprobante.length() - 1);

        return comprobante;
    }

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirCancelar() {
		Map<String, Object> parametros = new HashMap<>();
        parametros.put("accion", "0");
        SessionUtil.setFlash(parametros);
        RequestContext.getCurrentInstance().closeDialog(null);
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaListacr
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaListacr(SelectEvent event) {
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
	 * Retorna la lista listaListacr
	 * 
	 * @return listaListacr
	 */
	public RegistroDataModelImpl getListaListacr() {
		return listaListacr;
	}

	/**
	 * Asigna la lista listaListacr
	 * 
	 * @param listaListacr Variable a asignar en listaListacr
	 */
	public void setListaListacr(RegistroDataModelImpl listaListacr) {
		this.listaListacr = listaListacr;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>
}
