/*-
 * FrmConfigurarMovimientosControlador.java
 *
 * 1.0
 * 
 * 24 abr. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.FrmConfigurarMovimientosControladorUrlEnum;
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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite configurar los codigos de flujo de efectivo
 * para las cuentas que tienen activo el indicador mostrar en flujo
 *
 * @version 1.0, 24/04/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class FrmConfigurarMovimientosControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    /**
     * Atributo que almacena el anio del comprobante para cargar la
     * lista de codigos de flujo efectivo
     */
    private String anioComprobante;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los codigos de flujo
     */
    private RegistroDataModelImpl listaCodigoFlujoEfectivo;
    /**
     * Lista que carga los codigos de flujo en la grilla
     */
    private RegistroDataModelImpl listaCodigoFlujoEfectivoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    
    /**
     * Atributo que almacena el numero de ano seleccionado en el combo
     * ano del formulario
     */
    private int ano;
    /**
     * Atributo que almacena el numero de mes seleccionado en el combo
     * mes inicial del formulario
     */
    private String mesInicial;
    
    /**
     * Atributo que almacena el numero de mes seleccionado en el combo
     * mes final del formulario
     */
    private String mesFinal;
    private String naturaleza;
    private String valorDebito;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    
    /**
     * Lista que contiene los detalles del combo ano
     */
    private List<Registro> listaAno;
    /**
     * Lista de contiene los detalles del combo mes Inicial
     */
    private List<Registro> listaMesInicial;

    /**
     * Lista de contiene los detalles del combo mes final
     */
    private List<Registro> listaMesFinal;
    /**
     * Crea una nueva instancia de FrmConfigurarMovimientosControlador
     */
    public FrmConfigurarMovimientosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
        	//2065
            numFormulario = GeneralCodigoFormaEnum.FRM_CONFIGURAR_MOVIMIENTOS.getCodigo();;
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
        enumBase = GenericUrlEnum.DETALLECOMPROBANTECNT;
        cargarListaAno();
        cargarListaMesInicial();
        cargarListaMesFinal();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

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
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        
        parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                ano);
        
        parametrosListado.put(GeneralParameterEnum.MESINICIAL.getName(),
                mesInicial);
        
        parametrosListado.put(GeneralParameterEnum.MESFINAL.getName(),
                mesFinal);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfigurarMovimientosControladorUrlEnum.URL3215
                                                        .getValue());
        
        
        
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfigurarMovimientosControladorUrlEnum.URL4587
                                                        .getValue());
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCodigoFlujoEfectivo
     *
     */
    public void cargarListaCodigoFlujoEfectivo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfigurarMovimientosControladorUrlEnum.URL7761
                                                        .getValue());
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.NATURALEZA.getName(),
                naturaleza);
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anioComprobante);

        listaCodigoFlujoEfectivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoFlujoEfectivo
     *
     */
    public void cargarListaCodigoFlujoEfectivoE() {
    	
    	

         UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfigurarMovimientosControladorUrlEnum.URL8271
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        anioComprobante);
        param.put(GeneralParameterEnum.NATURALEZA.getName(),
                naturaleza);
        
        listaCodigoFlujoEfectivoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    public void cargarListaMesInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		FrmConfigurarMovimientosControladorUrlEnum.URL4588
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesFinal() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		FrmConfigurarMovimientosControladorUrlEnum.URL4589
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		FrmConfigurarMovimientosControladorUrlEnum.URL4590
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    // <METODOS_BOTONES>  
    public void oprimirimportarConfiguracion()
    {
    	SessionUtil.cargarModal("2370",
                SessionUtil.getModulo());
    }
    
    
    // </METODOS_BOTONES>
    
    // <METODOS_CAMBIAR>
    public void cambiarAno() {

        mesInicial = null;
        mesFinal = null;
        cargarListaMesInicial();
        reasignarOrigen();

    }

    public void cambiarMesInicial() {
        mesFinal = null;
        cargarListaMesFinal();
        reasignarOrigen();

    }
    
    public void cambiarMesFinal() {
    	reasignarOrigen();

    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFlujoEfectivo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFlujoEfectivo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_FLUJO_EFECTIVO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFlujoEfectivo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFlujoEfectivoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
    	
    	ano = SysmanFunciones.ano(new Date());
        // <CODIGO_DESARROLLADO>
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.TIPO_CPTE.getName());
        registro.getCampos().remove(GeneralParameterEnum.COMPROBANTE.getName());
        registro.getCampos().remove(GeneralParameterEnum.CUENTA.getName());
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
        registro.getCampos().remove(GeneralParameterEnum.FECHA.getName());
        registro.getCampos().remove(GeneralParameterEnum.VALOR_DEBITO.getName());
        registro.getCampos().remove(GeneralParameterEnum.VALOR_CREDITO.getName());
        registro.getCampos().remove(GeneralParameterEnum.MESINICIAL.getName());
        registro.getCampos().remove(GeneralParameterEnum.MESFINAL.getName());

    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();

        anioComprobante = registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString();
        
        valorDebito= registro.getCampos()
                .get(GeneralParameterEnum.VALOR_DEBITO.getName()).toString();
        
        if (valorDebito.equals("0") ) {
    		naturaleza= "C";
    	} else {
    		naturaleza= "D";
    	}
        
        cargarListaCodigoFlujoEfectivo();        
        cargarListaCodigoFlujoEfectivoE();
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_ASIGNADO
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoFlujoEfectivo
     * 
     * @return listaCodigoFlujoEfectivo
     */
    public RegistroDataModelImpl getListaCodigoFlujoEfectivo() {
        return listaCodigoFlujoEfectivo;
    }

    /**
     * Asigna la lista listaCodigoFlujoEfectivo
     * 
     * @param listaCodigoFlujoEfectivo
     * Variable a asignar en listaCodigoFlujoEfectivo
     */
    public void setListaCodigoFlujoEfectivo(
        RegistroDataModelImpl listaCodigoFlujoEfectivo) {
        this.listaCodigoFlujoEfectivo = listaCodigoFlujoEfectivo;
    }

    /**
     * Retorna la lista listaCodigoFlujoEfectivo
     * 
     * @return listaCodigoFlujoEfectivo
     */
    public RegistroDataModelImpl getListaCodigoFlujoEfectivoE() {
        return listaCodigoFlujoEfectivoE;
    }

    /**
     * Asigna la lista listaCodigoFlujoEfectivo
     * 
     * @param listaCodigoFlujoEfectivo
     * Variable a asignar en listaCodigoFlujoEfectivo
     */
    public void setListaCodigoFlujoEfectivoE(
        RegistroDataModelImpl listaCodigoFlujoEfectivoE) {
        this.listaCodigoFlujoEfectivoE = listaCodigoFlujoEfectivoE;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>	

	/**
	 * @return the mesInicial
	 */
	public String getMesInicial() {
		return mesInicial;
	}

	/**
	 * @param mesInicial the mesInicial to set
	 */
	public void setMesInicial(String mesInicial) {
		this.mesInicial = mesInicial;
	}

	/**
	 * @return the mesFinal
	 */
	public String getMesFinal() {
		return mesFinal;
	}

	/**
	 * @param mesFinal the mesFinal to set
	 */
	public void setMesFinal(String mesFinal) {
		this.mesFinal = mesFinal;
	}

	/**
	 * @return the listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}

	/**
	 * @param listaAno the listaAno to set
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	/**
	 * @return the listaMesInicial
	 */
	public List<Registro> getListaMesInicial() {
		return listaMesInicial;
	}

	/**
	 * @param listaMesInicial the listaMesInicial to set
	 */
	public void setListaMesInicial(List<Registro> listaMesInicial) {
		this.listaMesInicial = listaMesInicial;
	}

	/**
	 * @return the listaMesFinal
	 */
	public List<Registro> getListaMesFinal() {
		return listaMesFinal;
	}

	/**
	 * @param listaMesFinal the listaMesFinal to set
	 */
	public void setListaMesFinal(List<Registro> listaMesFinal) {
		this.listaMesFinal = listaMesFinal;
	}

	/**
	 * @return the ano
	 */
	public int getAno() {
		return ano;
	}

	/**
	 * @param ano the ano to set
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}
}


