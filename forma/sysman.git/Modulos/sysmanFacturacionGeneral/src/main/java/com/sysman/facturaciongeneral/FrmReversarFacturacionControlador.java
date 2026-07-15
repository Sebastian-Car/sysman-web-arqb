/*-
 * FrmReversarFacturacionControlador.java
 *
 * 1.0
 *
 * 17/11/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCeroRemote;
import com.sysman.facturaciongeneral.enums.FrmReversarFacturacionControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmReversarFacturacionControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite ejecutar el proceso de reversar facturas. Se
 * accede desde la ruta Panel Principal\Facturacion
 * General\Procesos\Reversar Facturacion
 *
 * @version 1.0, 17,20,21/11/2017
 * @author lcortes
 */
@ManagedBean
@ViewScoped
public class FrmReversarFacturacionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que identifica el numero de factura seleccionado.
     */
    private String factura;

    /**
     * Variable que identifica el codigo cobro de la factura
     * seleccionada.
     */
    private String codigoCobro;

    /**
     * Variable que identifica el tipo de cobro seleccionado al
     * ingresar al modulo de facturacion general.
     */
    private String tipoCobro;
    /**
     * Variable que identifica el anio seleccionado al ingresar al
     * modulo de facturacion general.
     */
    private String anio;
    /**
     * Variable que identifica si el tipo de cobro
     *  tiene selecionado el campo NOAPLICACAUSACION de la tabla SF_TIPO_COBRO
     */
    private boolean aplicaCausacion=false;
    /**
     * Variable que almacena el valor del campo FACURARECAUDADA
     */
    private boolean facturaRecaudada;
    /**
     * Variable que almacena el tipo de recaudo
     */
    private String  tipoRecaudo;
    /**
     * Variable que almacena el NUNMERO DE FACTURA asociada al recaudo
     */
    private String numerorecaudo;
    @EJB
    private EjbFacturacionGeneralCeroRemote ejbFactGeneral;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de las facturas
     */
    private RegistroDataModelImpl listaFacturaInicial;
    private Registro regTipoCobro;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmReversarFacturacionControlador
     */
    public FrmReversarFacturacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1456
            numFormulario = GeneralCodigoFormaEnum.FRM_REVERSAR_FACTURACION_CONTROLADOR
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
        tipoCobro = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue())
                        .toString();
        anio = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue())
                        .toString();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaFacturaInicial();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaFacturaInicial
     *
     */
    public void cargarListaFacturaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmReversarFacturacionControladorUrlEnum.URL3595
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmReversarFacturacionControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        listaFacturaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmReversarFacturacionControladorEnum.NUMERO_FACTURA
                                        .getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     * Realiza la ejecucion del procedimiento PR_REVERSARFACTURACION.
     *
     */
    public void validarCausacion(){
        Map<String, Object> paramC = new TreeMap<>();
    	paramC.put("COMPANIA", compania);
    	paramC.put("ANO", anio); 
    	paramC.put("TIPOCOBRO", tipoCobro);
	try {
		regTipoCobro=RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(FrmReversarFacturacionControladorUrlEnum.URL3596.getValue())
                                            .getUrl(),
                            paramC));
		
			
			if (regTipoCobro != null) {
				String causacion =SysmanFunciones.nvl(regTipoCobro.getCampos().get("NOAPLICACAUSACION"), false).toString();
			if(causacion.equals("0")) {
				aplicaCausacion=true;
			}else {
				aplicaCausacion=false;
			}

			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }
    /**
    *Valida si la fatura ya se encuentra cobrada,
    * si esta cobrada indica que la factura no se puede reversar
    */
//    public void validarFacturaReacudada() {
//    Map<String, Object> paramC = new TreeMap<>();
//	paramC.put("COMPANIA", compania);
//	paramC.put("ANO", anio); 
//	paramC.put("TIPOCOBRO", tipoCobro);
//try {
//	numerorecaudo=RegistroConverter.toRegistro(requestManager.get(
//                        UrlServiceUtil.getInstance()
//                                        .getUrlServiceByUrlByEnumID(FrmReversarFacturacionControladorUrlEnum.URL3597.getValue())
//                                        .getUrl(),
//                        paramC));
//	
//		
//		if (numerorecaudo != null) {
//			String factura =SysmanFunciones.nvl(regTipoCobro.getCampos().get("INDPAGO"), false).toString();
//		if(factura.equals("0")) {
//			facturaRecaudada=true;
//    	JsfUtil.agregarMensajeInformativo("La factura "+factura+" ya se encuentra recaudada. No se puede reversar");    
//		}else {
//			facturaRecaudada=false;
//		}
//
//		}
//	} catch (SystemException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//    }
    public void oprimirAceptar() {
//    	validarCausacion();
//    	validarFacturaReacudada();
        try {
            ejbFactGeneral.reversarFacturacion(compania,
                            Integer.parseInt(anio), tipoCobro,
                            new BigInteger(factura),
                            new BigInteger(codigoCobro),
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3800"));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(),
                            e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        }
        // </CODIGO_DESARROLLADO>
    

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFacturaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFacturaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        factura = registroAux.getCampos()
                        .get(FrmReversarFacturacionControladorEnum.NUMERO_FACTURA
                                        .getValue())
                        .toString();
        codigoCobro = registroAux.getCampos()
                        .get(FrmReversarFacturacionControladorEnum.CODIGO_COBRO
                                        .getValue())
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable factura
     *
     * @return factura
     */
    public String getFactura() {
        return factura;
    }

    /**
     * Asigna la variable factura
     *
     * @param factura
     * Variable a asignar en factura
     */
    public void setFactura(String factura) {
        this.factura = factura;
    }

    /**
     * Retorna la variable codigoCobro
     *
     * @return codigoCobro
     */

    public String getCodigoCobro() {
        return codigoCobro;
    }

    /**
     * Asigna la variable codigoCobro
     *
     * @param codigoCobro
     *
     * Variable a asignar en codigoCobro
     */
    public void setCodigoCobro(String codigoCobro) {
        this.codigoCobro = codigoCobro;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFacturaInicial
     *
     * @return listaFacturaInicial
     */
    public RegistroDataModelImpl getListaFacturaInicial() {
        return listaFacturaInicial;
    }

    /**
     * Asigna la lista listaFacturaInicial
     *
     * @param listaFacturaInicial
     * Variable a asignar en listaFacturaInicial
     */
    public void setListaFacturaInicial(
        RegistroDataModelImpl listaFacturaInicial) {
        this.listaFacturaInicial = listaFacturaInicial;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

	public boolean isAplicaCausacion() {
		return aplicaCausacion;
	}

	public void setAplicaCausacion(boolean aplicaCausacion) {
		this.aplicaCausacion = aplicaCausacion;
	}
}
