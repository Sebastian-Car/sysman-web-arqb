/*-
 * CAlmacenContabilidadTraCcNiifControlador.java
 *
 * 1.0
 * 
 * 12/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilizar.enums.CAlmacenContabilidadTraCcControladorUrlEnum;
import com.sysman.contabilizar.enums.CAlmacenContabilidadTraCcNiifControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.contabilizar.ejb.EjbContabilizarAlmacenCeroRemote;
/**
 * Formulario que cambia la interfaz a Niif por centro de costo
 * transaccion
 *
 * @version 1.0, 12/09/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped
public class CAlmacenContabilidadTraCcNiifControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el numero del modulo
     * por el cual se ingreso a la aplicaci�n
     */

    private final String modulo;

    /**
     * Constante a nivel de clase que almacena el centro de costo
     * seleccionado en el formulario
     */
    private String centroCosto;
    /**
     * Constante a nivel de clase que almacena el codigo del elemento
     * seleccionado en el formulario CAlmacenContabilidad
     */
    private String codigoElemento;
    /**
     * Constate a nivel de clase que almacena el nombre del codigo del
     * elemento seleccionado del formulario anterior
     * CAlmacenContabilidad
     */
    private String nombre;
    /**
     * Atributo que almacena el valor del anio por el momento
     */
    private String anio;

    /**
     * Atributo que almacena el tipo heredado del formulario que abre
     * la clase
     */
    private String tipo;

    /**
     * Atributo que administra la visibilidad de las columnas de
     * interfaz de almacen
     */
    private boolean verInterfazAlmacen;

    /**
     * Atributo que administra �a visibilidad de las columnas de iva
     * de interfaz almacen
     */

    private boolean verInterfaz;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los detalles del combo CentroCosto (CB6342).
     */
    private RegistroDataModelImpl listaCentroCosto;
    /**
     * Lista que contiene los detalles del combo CentroCosto (CB6342).
     */
    private RegistroDataModelImpl listaCentroCostoE;
    /**
     * Lista que contiene los detalles del combo TipoMovimiento
     * (CB6350).
     */
    private RegistroDataModelImpl listaTipoMovimiento;
    /**
     * Lista que contiene los detalles del combo TipoMovimiento
     * (CB6350).
     */
    private RegistroDataModelImpl listaTipoMovimientoE;
    /**
     * Lista que contiene los detalles del combo CuentaDebito
     * (CB6351).
     */
    private RegistroDataModelImpl listaCuentaDebito;
    /**
     * Lista que contiene los detalles del combo CuentaDebito
     * (CB6351).
     */
    private RegistroDataModelImpl listaCuentaDebitoE;
    /**
     * Lista que contiene los detalles del combo CuentaCredito
     * (CB6352).
     */
    private RegistroDataModelImpl listaCuentaCredito;
    /**
     * Lista que contiene los detalles del combo CuentaCredito
     * (CB6352).
     */
    private RegistroDataModelImpl listaCuentaCreditoE;
    /**
     * Lista que contiene los detalles del combo CuentaDebitoIVA
     * (CB6353).
     */
    private RegistroDataModelImpl listaCuentaDebitoIVA;
    /**
     * Lista que contiene los detalles del combo CuentaDebitoIVA
     * (CB6353).
     */
    private RegistroDataModelImpl listaCuentaDebitoIVAE;
    /**
     * Lista que contiene los detalles del combo CuentaCreditoIVA
     * (CB6354)
     */
    private RegistroDataModelImpl listaCuentaCreditoIVA;
    /**
     * Lista que contiene los detalles del combo CuentaCreditoIVA
     * (CB6354).
     */
    private RegistroDataModelImpl listaCuentaCreditoIVAE;
    /**
     * Lista que contiene los detalles del combo CuentaDebitoBase
     * (CB6355).
     */
    private RegistroDataModelImpl listaCuentaDebitoBase;
    /**
     * Lista que contiene los detalles del combo CuentaDebitoBase
     * (CB6355).
     */
    private RegistroDataModelImpl listaCuentaDebitoBaseE;
    /**
     * Lista que contiene los detalles del combo CuentaCreditoBase
     * (CB6356).
     */
    private RegistroDataModelImpl listaCuentaCreditoBase;
    /**
     * Lista que contiene los detalles del combo CuentaCreditoBase
     * (CB6356).
     */
    private RegistroDataModelImpl listaCuentaCreditoBaseE;
    
    private RegistroDataModelImpl listaFuenteRecurso;
	private RegistroDataModelImpl listaFuenteRecursoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    private Map<String, Object> ridP;

    private Map<String, Object> parametrosEntrada;
    
    private String fuenteRecurso;
	private boolean visibleCentroC;
	private boolean visibleFuente;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * CAlmacenContabilidadTraCcNiifControlador
     */

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    @EJB
    private EjbContabilizarAlmacenCeroRemote ejbContabilizarAlmacenCero;

    public CAlmacenContabilidadTraCcNiifControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 1918
            numFormulario = GeneralCodigoFormaEnum.C_ALMACEN_CONTABILIDAD_TRA_CC_NIIF_CONTROLADOR
                            .getCodigo();
            parametrosEntrada = SessionUtil.getFlash();

            centroCosto = SysmanConstantes.CONS_CENTRO;
            fuenteRecurso = SysmanConstantes.CONS_FUENTE;

            if (parametrosEntrada != null) {

                ridP = (HashMap<String, Object>) parametrosEntrada.get("rid");

                codigoElemento = parametrosEntrada.get("codigoElemento")
                                .toString();

                anio = parametrosEntrada.get("anio")
                                .toString();

                tipo = parametrosEntrada.get("tipo")
                                .toString();

                nombre = parametrosEntrada.get("nombre")
                                .toString();
                
                visibleCentroC = (boolean) parametrosEntrada.get("centroCostoNiif");
				
				visibleFuente = (boolean) parametrosEntrada.get("fuenteRecursoNiif");

            }
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

        enumBase = GenericUrlEnum.ALMACENCONTABILIDADCC;
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCentroCosto();
        cargarListaCentroCostoE();
        cargarListaTipoMovimiento();
        cargarListaTipoMovimientoE();
        cargarListaCuentaDebito();
        cargarListaCuentaDebitoE();
        cargarListaCuentaCredito();
        cargarListaCuentaCreditoE();
        cargarListaCuentaDebitoIVA();
        cargarListaCuentaDebitoIVAE();
        cargarListaCuentaCreditoIVA();
        cargarListaCuentaCreditoIVAE();
        cargarListaCuentaDebitoBase();
        cargarListaCuentaDebitoBaseE();
        cargarListaCuentaCreditoBase();
        cargarListaCuentaCreditoBaseE();
        cargarListaFuenteRecurso();
		cargarListaFuenteRecursoE();
        // </CARGAR_LISTA_COMBO_GRANDE>
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
        
        //   actualizarCuentas(); CC790 se comenta dado que ya se esta actualizando bajo el API 745005

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        codigoElemento);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        centroCosto);
        parametrosListado.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
				fuenteRecurso);
        
        try {
            int ano=Integer.parseInt(anio);
            ejbContabilizarAlmacenCero.insertaAlmacenContabilidadCC(compania, codigoElemento, tipo, centroCosto, fuenteRecurso, ano);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0002
                                                        .getValue());
        
        urlActualizacion =  UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0006
                                                .getValue());
        }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista: <code>listaCentroCosto</code> asociada al combo
     * CentroCosto (CB6342).
     */
    public void cargarListaCentroCosto() {
        // <CODIGO_DESARROLLADO>

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

        // </CODIGO_DESARROLLADO>

    }

    /**
     * 
     * Carga la lista: <code>listaCentroCostoE</code> asociada al
     * combo CentroCosto (CB6342).
     */
    public void cargarListaCentroCostoE() {
        // <CODIGO_DESARROLLADO>
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCentroCostoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        // </CODIGO_DESARROLLADO>

    }

    /**
     * 
     * Carga la lista: <code>listaTipoMovimiento</code> asociada al
     * combo TipoMovimiento (CB6350).
     */
    public void cargarListaTipoMovimiento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put("TIPO",
                        tipo);

        listaTipoMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista: <code>listaTipoMovimientoE</code> asociada al
     * combo TipoMovimiento (CB6350).
     */
    public void cargarListaTipoMovimientoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put("TIPO",
                        tipo);

        listaTipoMovimientoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista: <code>listaCuentaDebito</code> asociada al
     * combo CuentaDebito (CB6351).
     */
    public void cargarListaCuentaDebito() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaDebito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista: <code>listaCuentaDebitoE</code> asociada al
     * combo CuentaDebito (CB6351).
     */
    public void cargarListaCuentaDebitoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaDebitoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista: <code>listaCuentaCredito</code> asociada al
     * combo CuentaCredito (CB6352).
     */
    public void cargarListaCuentaCredito() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaCredito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista: <code>listaCuentaCreditoE</code> asociada al
     * combo CuentaCredito (CB6352).
     */
    public void cargarListaCuentaCreditoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaCreditoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista: <code>listaCuentaDebitoIVA</code> asociada al
     * combo CuentaCredito (CB6353).
     */
    public void cargarListaCuentaDebitoIVA() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaDebitoIVA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista: <code>listaCuentaDebitoIVAE</code> asociada al
     * combo CuentaCredito (CB6353).
     */
    public void cargarListaCuentaDebitoIVAE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaDebitoIVAE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista: <code>listaCuentaCreditoIVA</code> asociada al
     * combo CuentaCreditoIVA (CB6354).
     */
    public void cargarListaCuentaCreditoIVA() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaCreditoIVA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista: <code>listaCuentaCreditoIVA</code> asociada al
     * combo CuentaCreditoIVAE (CB6354).
     */
    public void cargarListaCuentaCreditoIVAE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaCreditoIVAE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     *
     * Carga la lista: <code>listaCuentaDebitoBase</code> asociada al
     * combo CuentaCreditoBase (CB6355).
     */
    public void cargarListaCuentaDebitoBase() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaDebitoBase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista: <code>listaCuentaCreditoBase</code> asociada al
     * combo CuentaCreditoBase (CB6356).
     */
    public void cargarListaCuentaDebitoBaseE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaDebitoBaseE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista: <code>listaCuentaCreditoBase</code> asociada al
     * combo CuentaCreditoBase (CB6356).
     */
    public void cargarListaCuentaCreditoBase() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaCreditoBase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista: <code>listaCuentaCreditoBase</code> asociada al
     * combo CuentaCreditoBase (CB6356).
     */
    public void cargarListaCuentaCreditoBaseE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcNiifControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaCreditoBaseE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }
    
    /**
	 * 
	 * Carga la lista listaFuenteRecurso
	 *
	 */
	public void cargarListaFuenteRecurso(){
		
		String urlEnum = CAlmacenContabilidadTraCcControladorUrlEnum.URL1223.getValue();
	    UrlBean urlBean = UrlServiceUtil.getInstance()
	                    .getUrlServiceByUrlByEnumID(urlEnum);
	    Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ANO.getName(), anio);
	
	    listaFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
	                    urlBean.getUrlConteo().getUrl(), param, true,
	                    "CODIGO");
	    
	}
	/**
	 * 
	 * Carga la lista listaFuenteRecurso
	 *
	 */
	public void  cargarListaFuenteRecursoE(){
		listaFuenteRecursoE = listaFuenteRecurso;
	}

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CentroCosto en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCentroCostoC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TipoMovimiento en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipoMovimientoC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.NOMBRE.getName(), registro
                                        .getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));
        // </CODIGO_DESARROLLADO>
    }
    
    /**
	 * Metodo ejecutado al cambiar el control FuenteRecurso en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarFuenteRecursoC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCosto
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCosto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();

        reasignarOrigen();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCosto
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCosto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();

        reasignarOrigen();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoMovimiento
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoMovimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOMOVIMIENTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoMovimiento
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoMovimientoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebito
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIIF_CUENTADEBITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebito
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCredito
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIIF_CUENTACREDITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCredito
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoIVA
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoIVA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIIF_DEBITO_IVA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoIVA
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoIVAE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoIVA
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoIVA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIIF_CREDITO_IVA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoIVA
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoIVAE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoBase
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoBase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIIF_DEBITO_BASE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoBase
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoBaseE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoBase
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoBase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIIF_CREDITO_IVA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoBase
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoBaseE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }
    
    /**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteRecurso
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteRecurso(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteRecurso = SysmanFunciones
				.nvl(registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()),
						"")
				.toString();
	
		reasignarOrigen();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteRecurso
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteRecursoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGO");
	}

    // </METODOS_COMBOS_GRANDES>
	
	public void actualizarCuentas() {
		try
	    {
	
			   UrlBean urlUpdate = UrlServiceUtil.getInstance()
	                   .getUrlServiceByUrlByEnumID(CAlmacenContabilidadTraCcControladorUrlEnum.URL745006.getValue());
	   Map<String, Object> fields = new TreeMap<>();
	   fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	   fields.put(GeneralParameterEnum.ANO.getName(), anio);
	   fields.put(GeneralParameterEnum.ELEMENTO.getName(), codigoElemento);
	   fields.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroCosto);
	   fields.put(GeneralParameterEnum.FUENTE_RECURSO.getName(), fuenteRecurso);
	   /*fields.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
	   fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());*/
	   Parameter parameter = new Parameter();
	   parameter.setFields(fields);
	   requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
	   
	    }
	    catch (SystemException e)
	    {
	        JsfUtil.agregarMensajeError(e.getMessage());
	        logger.error(e.getMessage(), e);
	
	    }
	}
	
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        String interfazAlmacen;
        try {
            interfazAlmacen = ejbSysmanUtil.consultarParametro(compania,
                            "VALOR DE IVA DISCRIMINADO EN INTERFAZ DIARIA DE ALMACEN",
                            modulo, new Date(), false);

            if (interfazAlmacen.equals("SI")) {

                verInterfazAlmacen = false;
                verInterfaz = true;

            }
            else {

                verInterfazAlmacen = false;
                verInterfaz = false;

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * 
     * @return true
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
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("NIIF_CUENTADEBITOAJUSTE", null);
        registro.getCampos().put("NIIF_CUENTACREDITOAJUSTE", null);
       //JM INI CC 2619 25/09/2025
        if(!visibleCentroC) {
        	registro.getCampos().put("KEY_CENTRO_COSTO", SysmanConstantes.CONS_CENTRO);
        }
        if(!visibleFuente) {
        	registro.getCampos().put("KEY_FUENTEDERECURSO", SysmanConstantes.CONS_FUENTE);
        }
        //JM FIN CC 2619 25/09/2025
        //JM INI 02/12/2024 CC417 quitar las llaves 
        registro.getCampos().remove("FUENTEDERECURSO");
        registro.getCampos().remove("ANO");
        registro.getCampos().remove("CODIGOELEMENTO");
        registro.getCampos().remove("TIPOMOVIMIENTO");
        registro.getCampos().remove("CENTRO_COSTO"); 
        //JM FIN 02/12/2024 CC417
        return true;
        // </CODIGO_DESARROLLADO>

    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return true
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
     * @return true
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
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put("rid", ridP);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put("codigoElemento", registro.getCampos()
                        .get(GeneralParameterEnum.CODIGOELEMENTO.getName()));

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.CALMACEN_CONTABILIDADS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable centroCosto
     * 
     * @return centroCosto
     */
    public String getCentroCosto() {
        return centroCosto;
    }

    /**
     * Asigna la variable centroCosto
     * 
     * @param centroCosto
     * Variable a asignar en centroCosto
     */
    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    /**
     * Retorna la variable codigoElemento
     * 
     * @return codigoElemento
     */
    public String getCodigoElemento() {
        return codigoElemento;
    }

    /**
     * Asigna la variable codigoElemento
     * 
     * @param codigoElemento
     * Variable a asignar en codigoElemento
     */
    public void setCodigoElemento(String codigoElemento) {
        this.codigoElemento = codigoElemento;
    }

    /**
     * Retorna la variable nombre
     * 
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna la variable nombre
     * 
     * @param nombre
     * Variable a asignar en nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCentroCosto
     * 
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCosto() {
        return listaCentroCosto;
    }

    /**
     * Asigna la lista listaCentroCosto
     * 
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
        this.listaCentroCosto = listaCentroCosto;
    }

    /**
     * Retorna la lista listaCentroCosto
     * 
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCostoE() {
        return listaCentroCostoE;
    }

    /**
     * Asigna la lista listaCentroCosto
     * 
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCostoE(RegistroDataModelImpl listaCentroCostoE) {
        this.listaCentroCostoE = listaCentroCostoE;
    }

    /**
     * Retorna la lista listaTipoMovimiento
     * 
     * @return listaTipoMovimiento
     */
    public RegistroDataModelImpl getListaTipoMovimiento() {
        return listaTipoMovimiento;
    }

    /**
     * Asigna la lista listaTipoMovimiento
     * 
     * @param listaTipoMovimiento
     * Variable a asignar en listaTipoMovimiento
     */
    public void setListaTipoMovimiento(
        RegistroDataModelImpl listaTipoMovimiento) {
        this.listaTipoMovimiento = listaTipoMovimiento;
    }

    /**
     * Retorna la lista listaTipoMovimiento
     * 
     * @return listaTipoMovimiento
     */
    public RegistroDataModelImpl getListaTipoMovimientoE() {
        return listaTipoMovimientoE;
    }

    /**
     * Asigna la lista listaTipoMovimiento
     * 
     * @param listaTipoMovimiento
     * Variable a asignar en listaTipoMovimiento
     */
    public void setListaTipoMovimientoE(
        RegistroDataModelImpl listaTipoMovimientoE) {
        this.listaTipoMovimientoE = listaTipoMovimientoE;
    }

    /**
     * Retorna la lista listaCuentaDebito
     * 
     * @return listaCuentaDebito
     */
    public RegistroDataModelImpl getListaCuentaDebito() {
        return listaCuentaDebito;
    }

    /**
     * Asigna la lista listaCuentaDebito
     * 
     * @param listaCuentaDebito
     * Variable a asignar en listaCuentaDebito 2
     * 
     * public void setListaCuentaDebito(RegistroDataModelImpl
     * listaCuentaDebito) { this.listaCuentaDebito =
     * listaCuentaDebito; }
     * 
     * /** Retorna la lista listaCuentaDebito
     * 
     * @return listaCuentaDebito
     */
    public RegistroDataModelImpl getListaCuentaDebitoE() {
        return listaCuentaDebitoE;
    }

    /**
     * Asigna la lista listaCuentaDebito
     * 
     * @param listaCuentaDebito
     * Variable a asignar en listaCuentaDebito
     */
    public void setListaCuentaDebitoE(
        RegistroDataModelImpl listaCuentaDebitoE) {
        this.listaCuentaDebitoE = listaCuentaDebitoE;
    }

    /**
     * Retorna la lista listaCuentaCredito
     * 
     * @return listaCuentaCredito
     */
    public RegistroDataModelImpl getListaCuentaCredito() {
        return listaCuentaCredito;
    }

    /**
     * Asigna la lista listaCuentaCredito
     * 
     * @param listaCuentaCredito
     * Variable a asignar en listaCuentaCredito
     */
    public void setListaCuentaCredito(
        RegistroDataModelImpl listaCuentaCredito) {
        this.listaCuentaCredito = listaCuentaCredito;
    }

    /**
     * Retorna la lista listaCuentaCredito
     * 
     * @return listaCuentaCredito
     */
    public RegistroDataModelImpl getListaCuentaCreditoE() {
        return listaCuentaCreditoE;
    }

    /**
     * Asigna la lista listaCuentaCredito
     * 
     * @param listaCuentaCredito
     * Variable a asignar en listaCuentaCredito
     */
    public void setListaCuentaCreditoE(
        RegistroDataModelImpl listaCuentaCreditoE) {
        this.listaCuentaCreditoE = listaCuentaCreditoE;
    }

    /**
     * Retorna la lista listaCuentaDebitoIVA
     * 
     * @return listaCuentaDebitoIVA
     */
    public RegistroDataModelImpl getListaCuentaDebitoIVA() {
        return listaCuentaDebitoIVA;
    }

    /**
     * Asigna la lista listaCuentaDebitoIVA
     * 
     * @param listaCuentaDebitoIVA
     * Variable a asignar en listaCuentaDebitoIVA
     */
    public void setListaCuentaDebitoIVA(
        RegistroDataModelImpl listaCuentaDebitoIVA) {
        this.listaCuentaDebitoIVA = listaCuentaDebitoIVA;
    }

    /**
     * Retorna la lista listaCuentaDebitoIVA
     * 
     * @return listaCuentaDebitoIVA
     */
    public RegistroDataModelImpl getListaCuentaDebitoIVAE() {
        return listaCuentaDebitoIVAE;
    }

    /**
     * Asigna la lista listaCuentaDebitoIVA
     * 
     * @param listaCuentaDebitoIVA
     * Variable a asignar en listaCuentaDebitoIVA
     */
    public void setListaCuentaDebitoIVAE(
        RegistroDataModelImpl listaCuentaDebitoIVAE) {
        this.listaCuentaDebitoIVAE = listaCuentaDebitoIVAE;
    }

    /**
     * Retorna la lista listaCuentaCreditoIVA
     * 
     * @return listaCuentaCreditoIVA
     */
    public RegistroDataModelImpl getListaCuentaCreditoIVA() {
        return listaCuentaCreditoIVA;
    }

    /**
     * Asigna la lista listaCuentaCreditoIVA
     * 
     * @param listaCuentaCreditoIVA
     * Variable a asignar en listaCuentaCreditoIVA
     */
    public void setListaCuentaCreditoIVA(
        RegistroDataModelImpl listaCuentaCreditoIVA) {
        this.listaCuentaCreditoIVA = listaCuentaCreditoIVA;
    }

    /**
     * Retorna la lista listaCuentaCreditoIVA
     * 
     * @return listaCuentaCreditoIVA
     */
    public RegistroDataModelImpl getListaCuentaCreditoIVAE() {
        return listaCuentaCreditoIVAE;
    }

    /**
     * Asigna la lista listaCuentaCreditoIVA
     * 
     * @param listaCuentaCreditoIVA
     * Variable a asignar en listaCuentaCreditoIVA
     */
    public void setListaCuentaCreditoIVAE(
        RegistroDataModelImpl listaCuentaCreditoIVAE) {
        this.listaCuentaCreditoIVAE = listaCuentaCreditoIVAE;
    }

    /**
     * Retorna la lista listaCuentaDebitoBase
     * 
     * @return listaCuentaDebitoBase
     */
    public RegistroDataModelImpl getListaCuentaDebitoBase() {
        return listaCuentaDebitoBase;
    }

    /**
     * Asigna la lista listaCuentaDebitoBase
     * 
     * @param listaCuentaDebitoBase
     * Variable a asignar en listaCuentaDebitoBase
     */
    public void setListaCuentaDebitoBase(
        RegistroDataModelImpl listaCuentaDebitoBase) {
        this.listaCuentaDebitoBase = listaCuentaDebitoBase;
    }

    /**
     * Retorna la lista listaCuentaDebitoBase
     * 
     * @return listaCuentaDebitoBase
     */
    public RegistroDataModelImpl getListaCuentaDebitoBaseE() {
        return listaCuentaDebitoBaseE;
    }

    /**
     * Asigna la lista listaCuentaDebitoBase
     * 
     * @param listaCuentaDebitoBase
     * Variable a asignar en listaCuentaDebitoBase
     */
    public void setListaCuentaDebitoBaseE(
        RegistroDataModelImpl listaCuentaDebitoBaseE) {
        this.listaCuentaDebitoBaseE = listaCuentaDebitoBaseE;
    }

    /**
     * Retorna la lista listaCuentaCreditoBase
     * 
     * @return listaCuentaCreditoBase
     */
    public RegistroDataModelImpl getListaCuentaCreditoBase() {
        return listaCuentaCreditoBase;
    }

    /**
     * Asigna la lista listaCuentaCreditoBase
     * 
     * @param listaCuentaCreditoBase
     * Variable a asignar en listaCuentaCreditoBase
     */
    public void setListaCuentaCreditoBase(
        RegistroDataModelImpl listaCuentaCreditoBase) {
        this.listaCuentaCreditoBase = listaCuentaCreditoBase;
    }

    /**
     * Retorna la lista listaCuentaCreditoBase
     * 
     * @return listaCuentaCreditoBase
     */
    public RegistroDataModelImpl getListaCuentaCreditoBaseE() {
        return listaCuentaCreditoBaseE;
    }

    /**
     * Asigna la lista listaCuentaCreditoBase
     * 
     * @param listaCuentaCreditoBase
     * Variable a asignar en listaCuentaCreditoBase
     */
    public void setListaCuentaCreditoBaseE(
        RegistroDataModelImpl listaCuentaCreditoBaseE) {
        this.listaCuentaCreditoBaseE = listaCuentaCreditoBaseE;
    }

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

    /**
     * @return the verInterfazAlmacen
     */
    public boolean isVerInterfazAlmacen() {
        return verInterfazAlmacen;
    }

    /**
     * @param verInterfazAlmacen
     * the verInterfazAlmacen to set
     */
    public void setVerInterfazAlmacen(boolean verInterfazAlmacen) {
        this.verInterfazAlmacen = verInterfazAlmacen;
    }

    /**
     * @return the verInterfaz
     */
    public boolean isVerInterfaz() {
        return verInterfaz;
    }

    /**
     * @param verInterfaz
     * the verInterfaz to set
     */
    public void setVerInterfaz(boolean verInterfaz) {
        this.verInterfaz = verInterfaz;
    }

	/**
	 * @return the listaFuenteRecurso
	 */
	public RegistroDataModelImpl getListaFuenteRecurso() {
		return listaFuenteRecurso;
	}

	/**
	 * @param listaFuenteRecurso the listaFuenteRecurso to set
	 */
	public void setListaFuenteRecurso(RegistroDataModelImpl listaFuenteRecurso) {
		this.listaFuenteRecurso = listaFuenteRecurso;
	}

	/**
	 * @return the listaFuenteRecursoE
	 */
	public RegistroDataModelImpl getListaFuenteRecursoE() {
		return listaFuenteRecursoE;
	}

	/**
	 * @param listaFuenteRecursoE the listaFuenteRecursoE to set
	 */
	public void setListaFuenteRecursoE(RegistroDataModelImpl listaFuenteRecursoE) {
		this.listaFuenteRecursoE = listaFuenteRecursoE;
	}

	/**
	 * @return the fuenteRecurso
	 */
	public String getFuenteRecurso() {
		return fuenteRecurso;
	}

	/**
	 * @param fuenteRecurso the fuenteRecurso to set
	 */
	public void setFuenteRecurso(String fuenteRecurso) {
		this.fuenteRecurso = fuenteRecurso;
	}

	/**
	 * @return the visibleCentroC
	 */
	public boolean isVisibleCentroC() {
		return visibleCentroC;
	}

	/**
	 * @param visibleCentroC the visibleCentroC to set
	 */
	public void setVisibleCentroC(boolean visibleCentroC) {
		this.visibleCentroC = visibleCentroC;
	}

	/**
	 * @return the visibleFuente
	 */
	public boolean isVisibleFuente() {
		return visibleFuente;
	}

	/**
	 * @param visibleFuente the visibleFuente to set
	 */
	public void setVisibleFuente(boolean visibleFuente) {
		this.visibleFuente = visibleFuente;
	}
    
    

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
