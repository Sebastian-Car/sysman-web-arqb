/*-
 * CAlmacenContabilidadTraCcControlador.java
 *
 * 1.0
 *
 * 4/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilizar.ejb.EjbContabilizarAlmacenCeroRemote;
import com.sysman.contabilizar.enums.CAlmacenContabilidadTraCcControladorEnum;
import com.sysman.contabilizar.enums.CAlmacenContabilidadTraCcControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
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

/**
 * Formulario que cambia la interfaz de almacen a contabilidad por centro de costo transaccion
 *
 * @version 1.0, 04/01/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class CAlmacenContabilidadTraCcControlador
                extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el centro de costo seleccionado
     */
    private String centroCosto;
    /**
     * Atributo que guarda el codigo del elemnteo extraido como parametro
     */
    private String codigoElemento;
    /**
     * Atributo que guarda el nombre extraido como parametro
     */
    private String nombre;

    /**
     * Atributo que almacena el valor del anio por el momento
     */
    private String anio;

    /**
     * Atributo que almacena el tipo heredado del formulario que abre la clase
     */
    private String tipo;

    /**
     * Atributo que administra la visibilidad de las columnas de interfaz de almacen
     */
    private boolean verInterfazAlmacen;
    
    private String fuenteRecurso;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacena los centros de costo
     */
    private RegistroDataModelImpl listaCentroCosto;
    /**
     * Lista que almacena los centros de costo en la grilla
     */
    private RegistroDataModelImpl listaCentroCostoE;
    /**
     * Lista que almacena los tipos de movimiento
     */
    private RegistroDataModelImpl listaTipoMovimiento;
    /**
     * Lista que almacena los tipos de movimiento en la grilla
     */
    private RegistroDataModelImpl listaTipoMovimientoE;
    /**
     * Lista que almacena las cuentas debito
     */
    private RegistroDataModelImpl listaCuentaDebito;
    /**
     * Lista que almacena las cuentas debito en la grilla
     */
    private RegistroDataModelImpl listaCuentaDebitoE;
    /**
     * Lista que almacena las cuentas credito
     */
    private RegistroDataModelImpl listaCuentaCredito;
    /**
     * Lista que almacena las cuentas credito en la grilla
     */
    private RegistroDataModelImpl listaCuentaCreditoE;
    /**
     * Lista que almacena las cuentas debito IVA
     */
    private RegistroDataModelImpl listaCuentaDebitoIVA;
    /**
     * Lista que almacena las cuentas debito IVA en la grilla
     */
    private RegistroDataModelImpl listaCuentaDebitoIVAE;
    /**
     * Lista que almacena las cuentas credito IVA
     */
    private RegistroDataModelImpl listaCuentaCreditoIVA;
    /**
     * Lista que almacena las cuentas credito IVA en la grilla
     */
    private RegistroDataModelImpl listaCuentaCreditoIVAE;
    /**
     * Lista que almacena las cuentas debito sin IVA
     */
    private RegistroDataModelImpl listaCuentaDebitoSinIVA;
    /**
     * Lista que almacena las cuentas debito sin IVA e n la grilla
     */
    private RegistroDataModelImpl listaCuentaDebitoSinIVAE;
    /**
     * Lista que almacena las cuentas credito sin IVA
     */
    private RegistroDataModelImpl listaCuentaCreditoSinIVA;
    /**
     * Lista que almacena las cuentas credito sin IVA en la grilla
     */
    private RegistroDataModelImpl listaCuentaCreditoSinIVAE;
    
    private RegistroDataModelImpl listaFuenteRecurso;
	private RegistroDataModelImpl listaFuenteRecursoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;

    private Map<String, Object> ridP;

    private Map<String, Object> parametrosEntrada;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbContabilizarAlmacenCeroRemote ejbContabilizarAlmacenCero;
    
    private boolean visibleFuente;
	
	private boolean visibleCentroC;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CAlmacenContabilidadTraCCControlador
     */
    public CAlmacenContabilidadTraCcControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CALMACEN_CONTABILIDAD_TRACC_CONTROLADOR
                            .getCodigo();

            parametrosEntrada = SessionUtil.getFlash();

            centroCosto = SysmanConstantes.CONS_CENTRO;
            fuenteRecurso = SysmanConstantes.CONS_FUENTE;

            if (parametrosEntrada != null)
            {

                ridP = (HashMap<String, Object>) parametrosEntrada.get("rid");

                codigoElemento = parametrosEntrada.get("codigoElemento")
                                .toString();

                anio = parametrosEntrada.get("anio")
                                .toString();

                tipo = parametrosEntrada.get("tipo")
                                .toString();

                nombre = parametrosEntrada.get("nombre")
                                .toString();
                
                visibleFuente = (boolean) parametrosEntrada.get("fuenteRecursos");
				
				visibleCentroC = (boolean) parametrosEntrada.get("centroCosto");

            }

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
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
        cargarListaCuentaDebitoSinIVA();
        cargarListaCuentaDebitoSinIVAE();
        cargarListaCuentaCreditoSinIVA();
        cargarListaCuentaCreditoSinIVAE();
        cargarListaFuenteRecurso(); 
		cargarListaFuenteRecursoE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la consulta del formulario. Tambien carga la lista del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        codigoElemento);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        centroCosto);
        parametrosListado.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
				fuenteRecurso);

        try
        {
            int ano = Integer.parseInt(anio);
            ejbContabilizarAlmacenCero.insertaAlmacenContabilidadCC(compania, codigoElemento, tipo, centroCosto, fuenteRecurso, ano);
        }
        catch (SystemException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcControladorUrlEnum.URL9999
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>

    /**
     *
     * Carga la lista listaCuentaDebito
     *
     */
    public void cargarListaCuentaDebito()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcControladorUrlEnum.URL12254
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
     * Carga la lista listaCuentaDebito
     *
     */
    public void cargarListaCuentaDebitoE()
    {
        listaCuentaDebitoE = listaCuentaDebito;

    }

    /**
     *
     * Carga la lista listaCuentaCredito
     *
     */
    public void cargarListaCuentaCredito()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcControladorUrlEnum.URL16355
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
     * Carga la lista listaCuentaCredito
     *
     */
    public void cargarListaCuentaCreditoE()
    {
        listaCuentaCreditoE = listaCuentaCredito;
    }

    /**
     *
     * Carga la lista listaCuentaDebitoIVA
     *
     */
    public void cargarListaCuentaDebitoIVA()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcControladorUrlEnum.URL10559
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
     *
     * Carga la lista listaCuentaDebitoIVA
     *
     */
    public void cargarListaCuentaDebitoIVAE()
    {
        listaCuentaDebitoIVAE = listaCuentaDebitoIVA;
    }

    /**
     *
     * Carga la lista listaCuentaCreditoIVA
     *
     */
    public void cargarListaCuentaCreditoIVA()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcControladorUrlEnum.URL13108
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
     *
     * Carga la lista listaCuentaCreditoIVA
     *
     */
    public void cargarListaCuentaCreditoIVAE()
    {
        listaCuentaCreditoIVAE = listaCuentaCreditoIVA;
    }

    /**
     *
     * Carga la lista listaCuentaDebitoSinIVA
     *
     */
    public void cargarListaCuentaDebitoSinIVA()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcControladorUrlEnum.URL11404
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaDebitoSinIVA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCuentaDebitoSinIVA
     *
     */
    public void cargarListaCuentaDebitoSinIVAE()
    {
        listaCuentaDebitoSinIVAE = listaCuentaDebitoSinIVA;
    }

    /**
     *
     * Carga la lista listaCuentaCreditoSinIVA
     *
     */
    public void cargarListaCuentaCreditoSinIVA()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcControladorUrlEnum.URL15730
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaCreditoSinIVA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCuentaCreditoSinIVA
     *
     */
    public void cargarListaCuentaCreditoSinIVAE()
    {
        listaCuentaCreditoSinIVAE = listaCuentaCreditoSinIVA;
    }

    /**
     *
     * Carga la lista listaCentroCosto
     *
     */
    public void cargarListaCentroCosto()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcControladorUrlEnum.URL17871
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCentroCosto
     *
     */
    public void cargarListaCentroCostoE()
    {
        listaCentroCostoE = listaCentroCosto;
    }

    /**
     *
     * Carga la lista listaTipoMovimiento
     *
     */
    public void cargarListaTipoMovimiento()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraCcControladorUrlEnum.URL17066
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(CAlmacenContabilidadTraCcControladorEnum.TIPO.getValue(),
                        tipo);

        listaTipoMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaTipoMovimiento
     *
     */
    public void cargarListaTipoMovimientoE()
    {
        listaTipoMovimientoE = listaTipoMovimiento;
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
     * Metodo ejecutado al cambiar el control TipoMovimiento en la fila seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipoMovimientoC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.NOMBRE.getName(), registro
                                        .getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCentroCosto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista *
     */
    public void seleccionarFilaCentroCosto(SelectEvent event) throws SystemException
    {
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
     * Metodo ejecutado al seleccionar una fila de la lista listaCentroCosto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaTipoMovimiento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoMovimiento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOMOVIMIENTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaTipoMovimiento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoMovimientoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("TIPOMOVIMIENTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }
    
    /**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuenteRecurso
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista *
	 */
	public void seleccionarFilaFuenteRecurso(SelectEvent event) throws SystemException
	{
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
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuenteRecurso
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteRecursoE(SelectEvent event)
	{
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos()
				.get(GeneralParameterEnum.CODIGO.getName());
	}

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaDebito
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebito(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaDebito
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(
                GeneralParameterEnum.CODIGO.getName()),"").toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaCredito
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCredito(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaCredito
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        /*
         * 01/04/2022 no estaba capturando el valor seleccionado por pantalla.
         *
         * auxiliar = (String) registroAux.getCampos() .get(GeneralParameterEnum.CODIGO.getName());
         * 
         * se cambia por la siguiente linea de codigo
         */

        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(
                GeneralParameterEnum.CODIGO.getName()),"").toString();

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaDebitoIVA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoIVA(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEBITO_IVA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaDebitoIVA
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoIVAE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(
                GeneralParameterEnum.CODIGO.getName()),"").toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaCreditoIVA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoIVA(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CREDITO_IVA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaCreditoIVA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoIVAE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(
                GeneralParameterEnum.CODIGO.getName()),"").toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaDebitoSinIVA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoSinIVA(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEBITO_BASE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaDebitoSinIVA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoSinIVAE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(
                GeneralParameterEnum.CODIGO.getName()),"").toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaCreditoSinIVA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoSinIVA(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CREDITO_BASE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaCreditoSinIVA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoSinIVAE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(
                                GeneralParameterEnum.CODIGO.getName()),"").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        String interfazAlmacen;
        try
        {
            interfazAlmacen = ejbSysmanUtil.consultarParametro(compania,
                            "VALOR DE IVA DISCRIMINADO EN INTERFAZ DIARIA DE ALMACEN",
                            modulo, new Date(), false);

            verInterfazAlmacen = "SI".equals(interfazAlmacen) ? true : false;
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        registro.getCampos().put("CUENTADEBITOAJUSTE", null);
        registro.getCampos().put("CUENTACREDITOAJUSTE", null);
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     */
    @Override
    public boolean eliminarDespues()
    {
        // METODO_NO_IMPLEMENTADO
        listaInicial.load();
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
     *
     */
    public void ejecutarrcCerrar()
    {

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
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        listaInicial.load();
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable centroCosto
     *
     * @return centroCosto
     */
    public String getCentroCosto()
    {
        return centroCosto;
    }

    /**
     * Asigna la variable centroCosto
     *
     * @param centroCosto
     * Variable a asignar en centroCosto
     */
    public void setCentroCosto(String centroCosto)
    {
        this.centroCosto = centroCosto;
    }

    /**
     * Retorna la variable codigoElemento
     *
     * @return codigoElemento
     */
    public String getCodigoElemento()
    {
        return codigoElemento;
    }

    /**
     * Asigna la variable codigoElemento
     *
     * @param codigoElemento
     * Variable a asignar en codigoElemento
     */
    public void setCodigoElemento(String codigoElemento)
    {
        this.codigoElemento = codigoElemento;
    }

    /**
     * Retorna la variable nombre
     *
     * @return nombre
     */
    public String getNombre()
    {
        return nombre;
    }

    /**
     * Asigna la variable nombre
     *
     * @param nombre
     * Variable a asignar en nombre
     */
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    public boolean isVerInterfazAlmacen()
    {
        return verInterfazAlmacen;
    }

    public void setVerInterfazAlmacen(boolean verInterfazAlmacen)
    {
        this.verInterfazAlmacen = verInterfazAlmacen;
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
    public RegistroDataModelImpl getListaCentroCosto()
    {
        return listaCentroCosto;
    }

    /**
     * Asigna la lista listaCentroCosto
     *
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto)
    {
        this.listaCentroCosto = listaCentroCosto;
    }

    /**
     * Retorna la lista listaCentroCosto
     *
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCostoE()
    {
        return listaCentroCostoE;
    }

    /**
     * Asigna la lista listaCentroCosto
     *
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCostoE(RegistroDataModelImpl listaCentroCostoE)
    {
        this.listaCentroCostoE = listaCentroCostoE;
    }

    /**
     * Retorna la lista listaTipoMovimiento
     *
     * @return listaTipoMovimiento
     */
    public RegistroDataModelImpl getListaTipoMovimiento()
    {
        return listaTipoMovimiento;
    }

    /**
     * Asigna la lista listaTipoMovimiento
     *
     * @param listaTipoMovimiento
     * Variable a asignar en listaTipoMovimiento
     */
    public void setListaTipoMovimiento(
        RegistroDataModelImpl listaTipoMovimiento)
    {
        this.listaTipoMovimiento = listaTipoMovimiento;
    }

    /**
     * Retorna la lista listaTipoMovimiento
     *
     * @return listaTipoMovimiento
     */
    public RegistroDataModelImpl getListaTipoMovimientoE()
    {
        return listaTipoMovimientoE;
    }

    /**
     * Asigna la lista listaTipoMovimiento
     *
     * @param listaTipoMovimiento
     * Variable a asignar en listaTipoMovimiento
     */
    public void setListaTipoMovimientoE(
        RegistroDataModelImpl listaTipoMovimientoE)
    {
        this.listaTipoMovimientoE = listaTipoMovimientoE;
    }

    /**
     * Retorna la lista listaCuentaDebito
     *
     * @return listaCuentaDebito
     */
    public RegistroDataModelImpl getListaCuentaDebito()
    {
        return listaCuentaDebito;
    }

    /**
     * Asigna la lista listaCuentaDebito
     *
     * @param listaCuentaDebito
     * Variable a asignar en listaCuentaDebito
     */
    public void setListaCuentaDebito(RegistroDataModelImpl listaCuentaDebito)
    {
        this.listaCuentaDebito = listaCuentaDebito;
    }

    /**
     * Retorna la lista listaCuentaDebito
     *
     * @return listaCuentaDebito
     */
    public RegistroDataModelImpl getListaCuentaDebitoE()
    {
        return listaCuentaDebitoE;
    }

    /**
     * Asigna la lista listaCuentaDebito
     *
     * @param listaCuentaDebito
     * Variable a asignar en listaCuentaDebito
     */
    public void setListaCuentaDebitoE(
        RegistroDataModelImpl listaCuentaDebitoE)
    {
        this.listaCuentaDebitoE = listaCuentaDebitoE;
    }

    /**
     * Retorna la lista listaCuentaCredito
     *
     * @return listaCuentaCredito
     */
    public RegistroDataModelImpl getListaCuentaCredito()
    {
        return listaCuentaCredito;
    }

    /**
     * Asigna la lista listaCuentaCredito
     *
     * @param listaCuentaCredito
     * Variable a asignar en listaCuentaCredito
     */
    public void setListaCuentaCredito(
        RegistroDataModelImpl listaCuentaCredito)
    {
        this.listaCuentaCredito = listaCuentaCredito;
    }

    /**
     * Retorna la lista listaCuentaCredito
     *
     * @return listaCuentaCredito
     */
    public RegistroDataModelImpl getListaCuentaCreditoE()
    {
        return listaCuentaCreditoE;
    }

    /**
     * Asigna la lista listaCuentaCredito
     *
     * @param listaCuentaCredito
     * Variable a asignar en listaCuentaCredito
     */
    public void setListaCuentaCreditoE(
        RegistroDataModelImpl listaCuentaCreditoE)
    {
        this.listaCuentaCreditoE = listaCuentaCreditoE;
    }

    /**
     * Retorna la lista listaCuentaDebitoIVA
     *
     * @return listaCuentaDebitoIVA
     */
    public RegistroDataModelImpl getListaCuentaDebitoIVA()
    {
        return listaCuentaDebitoIVA;
    }

    /**
     * Asigna la lista listaCuentaDebitoIVA
     *
     * @param listaCuentaDebitoIVA
     * Variable a asignar en listaCuentaDebitoIVA
     */
    public void setListaCuentaDebitoIVA(
        RegistroDataModelImpl listaCuentaDebitoIVA)
    {
        this.listaCuentaDebitoIVA = listaCuentaDebitoIVA;
    }

    /**
     * Retorna la lista listaCuentaDebitoIVA
     *
     * @return listaCuentaDebitoIVA
     */
    public RegistroDataModelImpl getListaCuentaDebitoIVAE()
    {
        return listaCuentaDebitoIVAE;
    }

    /**
     * Asigna la lista listaCuentaDebitoIVA
     *
     * @param listaCuentaDebitoIVA
     * Variable a asignar en listaCuentaDebitoIVA
     */
    public void setListaCuentaDebitoIVAE(
        RegistroDataModelImpl listaCuentaDebitoIVAE)
    {
        this.listaCuentaDebitoIVAE = listaCuentaDebitoIVAE;
    }

    /**
     * Retorna la lista listaCuentaCreditoIVA
     *
     * @return listaCuentaCreditoIVA
     */
    public RegistroDataModelImpl getListaCuentaCreditoIVA()
    {
        return listaCuentaCreditoIVA;
    }

    /**
     * Asigna la lista listaCuentaCreditoIVA
     *
     * @param listaCuentaCreditoIVA
     * Variable a asignar en listaCuentaCreditoIVA
     */
    public void setListaCuentaCreditoIVA(
        RegistroDataModelImpl listaCuentaCreditoIVA)
    {
        this.listaCuentaCreditoIVA = listaCuentaCreditoIVA;
    }

    /**
     * Retorna la lista listaCuentaCreditoIVA
     *
     * @return listaCuentaCreditoIVA
     */
    public RegistroDataModelImpl getListaCuentaCreditoIVAE()
    {
        return listaCuentaCreditoIVAE;
    }

    /**
     * Asigna la lista listaCuentaCreditoIVA
     *
     * @param listaCuentaCreditoIVA
     * Variable a asignar en listaCuentaCreditoIVA
     */
    public void setListaCuentaCreditoIVAE(
        RegistroDataModelImpl listaCuentaCreditoIVAE)
    {
        this.listaCuentaCreditoIVAE = listaCuentaCreditoIVAE;
    }

    /**
     * Retorna la lista listaCuentaDebitoSinIVA
     *
     * @return listaCuentaDebitoSinIVA
     */
    public RegistroDataModelImpl getListaCuentaDebitoSinIVA()
    {
        return listaCuentaDebitoSinIVA;
    }

    /**
     * Asigna la lista listaCuentaDebitoSinIVA
     *
     * @param listaCuentaDebitoSinIVA
     * Variable a asignar en listaCuentaDebitoSinIVA
     */
    public void setListaCuentaDebitoSinIVA(
        RegistroDataModelImpl listaCuentaDebitoSinIVA)
    {
        this.listaCuentaDebitoSinIVA = listaCuentaDebitoSinIVA;
    }

    /**
     * Retorna la lista listaCuentaDebitoSinIVA
     *
     * @return listaCuentaDebitoSinIVA
     */
    public RegistroDataModelImpl getListaCuentaDebitoSinIVAE()
    {
        return listaCuentaDebitoSinIVAE;
    }

    /**
     * Asigna la lista listaCuentaDebitoSinIVA
     *
     * @param listaCuentaDebitoSinIVA
     * Variable a asignar en listaCuentaDebitoSinIVA
     */
    public void setListaCuentaDebitoSinIVAE(
        RegistroDataModelImpl listaCuentaDebitoSinIVAE)
    {
        this.listaCuentaDebitoSinIVAE = listaCuentaDebitoSinIVAE;
    }

    /**
     * Retorna la lista listaCuentaCreditoSinIVA
     *
     * @return listaCuentaCreditoSinIVA
     */
    public RegistroDataModelImpl getListaCuentaCreditoSinIVA()
    {
        return listaCuentaCreditoSinIVA;
    }

    /**
     * Asigna la lista listaCuentaCreditoSinIVA
     *
     * @param listaCuentaCreditoSinIVA
     * Variable a asignar en listaCuentaCreditoSinIVA
     */
    public void setListaCuentaCreditoSinIVA(
        RegistroDataModelImpl listaCuentaCreditoSinIVA)
    {
        this.listaCuentaCreditoSinIVA = listaCuentaCreditoSinIVA;
    }

    /**
     * Retorna la lista listaCuentaCreditoSinIVA
     *
     * @return listaCuentaCreditoSinIVA
     */
    public RegistroDataModelImpl getListaCuentaCreditoSinIVAE()
    {
        return listaCuentaCreditoSinIVAE;
    }

    /**
     * Asigna la lista listaCuentaCreditoSinIVA
     *
     * @param listaCuentaCreditoSinIVA
     * Variable a asignar en listaCuentaCreditoSinIVA
     */
    public void setListaCuentaCreditoSinIVAE(
        RegistroDataModelImpl listaCuentaCreditoSinIVAE)
    {
        this.listaCuentaCreditoSinIVAE = listaCuentaCreditoSinIVAE;
    }

    /**
     * Retorna la variable auxiliar
     *
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     *
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
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
    
    
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
