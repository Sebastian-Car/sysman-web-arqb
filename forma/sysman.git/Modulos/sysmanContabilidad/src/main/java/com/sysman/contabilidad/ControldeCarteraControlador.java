/*-
 * ControldeCarteraControlador.java
 *
 * 1.0
 * 
 * 16/04/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.contabilidad.enums.ControldeCarteraControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.FuenterecursosControlador;
import com.sysman.general.enums.FuenterecursosControladorUrlEnum;
import com.sysman.general.enums.SeleccionModuloControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 16/04/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class ControldeCarteraControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private String nombreCuenta;
    private int modulo;
    private BigInteger cuenta;
    private String resultado;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaModulo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaCuenta;
    private RegistroDataModelImpl listaCuentaE;
    private RegistroDataModelImpl listaTipoComprobante;
    private RegistroDataModelImpl listaTipoComprobanteE;

    private List<Registro> listaAno;

    @EJB
    EjbContabilidadSieteRemote ejbContabilidadSieteRemote;
    
	@EJB
	private EjbContabilidadCincoRemote ejbContabilidadCinco;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
	private boolean preparaAnio;
	private String anio;
	private String anioPreparar;
	private String anioFuente;
	private final String usuario;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ControldeCarteraControlador
     */
    public ControldeCarteraControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio  =  String.valueOf(SysmanFunciones.ano(
				new Date()));
		usuario = SessionUtil.getUser().getCodigo();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONTROL_DE_CARTERA_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
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
        enumBase = GenericUrlEnum.CARTERA_CUENTA;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaModulo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaAno();
        cargarListaCuenta();
        cargarListaCuentaE();
        cargarListaTipoComprobante();
        cargarListaTipoComprobanteE();
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
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaModulo
     *
     */
    public void cargarListaModulo() {
        try {
            listaModulo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SeleccionModuloControladorUrlEnum.URL3642
                                                                            .getValue())
                                            .getUrl(),
                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCuenta
     *
     */
    public void cargarListaCuenta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ControldeCarteraControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuenta
     *
     */
    public void cargarListaCuentaE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ControldeCarteraControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        listaCuentaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaTipoComprobante
     *
     */
    public void cargarListaTipoComprobante() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ControldeCarteraControladorUrlEnum.URL003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTipoComprobante
     *
     */
    public void cargarListaTipoComprobanteE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ControldeCarteraControladorUrlEnum.URL003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoComprobanteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		ControldeCarteraControladorUrlEnum.URL004
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(FuenterecursosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }  
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </CODIGO_DESARROLLADO>
    public void carteraCuenta() {

        try {

            modulo = Integer.parseInt(SysmanFunciones
                            .nvl(registro.getCampos().get("MODULO"), "")
                            .toString());

            resultado = ejbContabilidadSieteRemote
                            .carteraCuenta(compania, modulo, cuenta.toString());

        }
        catch (SystemException e) {
            logger.error(e.getMessage(),
                            e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
	public void oprimirIniciar() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = true;
		if (!anio.isEmpty()) {
			anioPreparar = String.valueOf(Integer.parseInt(anio));
			anioFuente = String.valueOf(Integer.parseInt(anio) - 1);

		}
		JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
		// </CODIGO_DESARROLLADO>
	}
	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * del dialogo DialogoPrepararAnio en la vista
	 *
	 *
	 */
	public void aceptarDialogoPrepararAnio() {
		// <CODIGO_DESARROLLADO>
		preparaAnio = false;		
		if (SysmanFunciones.validarVariableVacio(anioPreparar)&& SysmanFunciones.validarVariableVacio(anioFuente)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB448"));
			return;
		}
		
		insertarAnio();

		JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB455"));
		JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
		// </CODIGO_DESARROLLADO>
	}
	
    // </METODOS_BOTONES>
	public void insertarAnio() {
		try {
			ejbContabilidadCinco.actualizarAnoControlCartera(compania, Integer.parseInt(anioFuente),
					Integer.parseInt(anioPreparar), usuario);
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
}
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Cuenta en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCuentaC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // listaInicial.getDatasource().get(rowNum %
        // 10).getCampos().put("FECHALARGA", "hola ");
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ");
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        "NOMBRE_CUENTA", nombreCuenta);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuenta
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        cuenta = (BigInteger) SysmanFunciones.nvl(registroAux.getCampos().get(
                        GeneralParameterEnum.CODIGO.getName()), "");

        registro.getCampos().put("ANO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.ANO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuenta
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();

        nombreCuenta = (String) registroAux.getCampos().get(
                        GeneralParameterEnum.NOMBRE.getName());

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoComprobante
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoComprobante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_COMPROBANTE",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoComprobante
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoComprobanteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGO");
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

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
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
        // <CODIGO_DESARROLLADO>.
        carteraCuenta();
        if (resultado.equals("FALSE")) {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().remove("APLICACION");
            registro.getCampos().remove("NOMBRE_CUENTA");

            // </CODIGO_DESARROLLADO>
            return true;
        }
        else {

            String mensaje = idioma.getString("TB_TB4296");
            JsfUtil.agregarMensajeAlerta(mensaje.replace("s$resultado$s",
                            resultado));

            return false;
        }
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
        registro.getCampos().remove("APLICACION");
        registro.getCampos().remove("NOMBRE_CUENTA");
        // </CODIGO_DESARROLLADO>
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
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaModulo
     * 
     * @return listaModulo
     */
    public List<Registro> getListaModulo() {
        return listaModulo;
    }

    /**
     * Asigna la lista listaModulo
     * 
     * @param listaModulo
     * Variable a asignar en listaModulo
     */
    public void setListaModulo(List<Registro> listaModulo) {
        this.listaModulo = listaModulo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @return the listaCuenta
     */
    public RegistroDataModelImpl getListaCuenta() {
        return listaCuenta;
    }

    /**
     * @param listaCuenta
     * the listaCuenta to set
     */
    public void setListaCuenta(RegistroDataModelImpl listaCuenta) {
        this.listaCuenta = listaCuenta;
    }

    /**
     * @return the listaCuentaE
     */
    public RegistroDataModelImpl getListaCuentaE() {
        return listaCuentaE;
    }

    /**
     * @param listaCuentaE
     * the listaCuentaE to set
     */
    public void setListaCuentaE(RegistroDataModelImpl listaCuentaE) {
        this.listaCuentaE = listaCuentaE;
    }

    /**
     * @return the listaTipoComprobante
     */
    public RegistroDataModelImpl getListaTipoComprobante() {
        return listaTipoComprobante;
    }

    /**
     * @param listaTipoComprobante
     * the listaTipoComprobante to set
     */
    public void setListaTipoComprobante(
        RegistroDataModelImpl listaTipoComprobante) {
        this.listaTipoComprobante = listaTipoComprobante;
    }

    /**
     * @return the listaTipoComprobanteE
     */
    public RegistroDataModelImpl getListaTipoComprobanteE() {
        return listaTipoComprobanteE;
    }

    /**
     * @param listaTipoComprobanteE
     * the listaTipoComprobanteE to set
     */
    public void setListaTipoComprobanteE(
        RegistroDataModelImpl listaTipoComprobanteE) {
        this.listaTipoComprobanteE = listaTipoComprobanteE;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>

	public List<Registro> getListaAno() {
		return listaAno;
	}

	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	public boolean isPreparaAnio() {
		return preparaAnio;
	}

	public void setPreparaAnio(boolean preparaAnio) {
		this.preparaAnio = preparaAnio;
	}

	public String getAnioPreparar() {
		return anioPreparar;
	}

	public void setAnioPreparar(String anioPreparar) {
		this.anioPreparar = anioPreparar;
	}

	public String getAnioFuente() {
		return anioFuente;
	}

	public void setAnioFuente(String anioFuente) {
		this.anioFuente = anioFuente;
	}
	
	
}
