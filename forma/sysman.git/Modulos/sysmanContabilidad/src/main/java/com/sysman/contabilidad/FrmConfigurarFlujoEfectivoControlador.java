/*-
 * FrmConfigurarFlujoEfectivoControlador.java
 *
 * 1.0
 * 
 * 23 abr. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.FrmConfigurarFlujoEfectivoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbPrepararAnoRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Formulario que permite configurar la estructura del informe de
 * flujo de efectivo de forma jerarquica seleccionando los registros
 * que tienen movimiento.
 *
 * @version 1.0, 23/04/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmConfigurarFlujoEfectivoControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los anios
     */
    private List<Registro> listaAno;
    
    private List<Registro> listaanioBase;    
    private List<Registro> listaanioDestino;
    private String anioBase;
    /**
     */
    private String anioDestino;
    /**
     */

    private boolean anioBaseVisible;
    
    @EJB
    private EjbPrepararAnoRemote ejbPrepararAno;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmConfigurarFlujoEfectivoControlador
     */
    public FrmConfigurarFlujoEfectivoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 2064
            numFormulario = GeneralCodigoFormaEnum.FRM_CONFIGURAR_PLAN_FLUJO_EFECTIVO
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
        enumBase = GenericUrlEnum.PLAN_FLUJO_EFECTIVO;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaanioBase();
        cargarListaanioDestino();
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
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmConfigurarFlujoEfectivoControladorUrlEnum.URL4366
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void cargarListaanioBase()
    {
        listaanioBase = listaAno;
    }

    public void cargarListaanioDestino()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioBase);

        try
        {
            listaanioDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		FrmConfigurarFlujoEfectivoControladorUrlEnum.URL4367
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton prepararAnio en la vista
     *
     *
     */
    public void oprimirprepararAnio() 
    {
    	anioBaseVisible = true;
    }
    
    /**
     * Metodo ejecutado al cambiar el control anioBase
     *
     *
     */
    public void cambiaranioBase()
    {
    	anioDestino = null;
        cargarListaanioDestino();
    }
    
    public void aceptaranioBase()
    {
        if ("".equals(anioBase) || (anioBase == null))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2606"));
            return;
        }
        if ("".equals(anioDestino) || (anioDestino == null))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2607"));
            return;
        }

        prepararAnoSiguiente();
    }
    
    public void cancelaranioBase()
    {
    	anioBase = null;
        anioDestino = null;
        anioBaseVisible = false;
    }
    
    private void prepararAnoSiguiente()
    {
        try
        {

            ejbPrepararAno.copiarPlanFlujoEfectivo(compania,
                            Integer.parseInt(anioDestino),
                            Integer.parseInt(anioBase), compania);
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1637"));
            anioBase = null;
            anioDestino = null;

        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally
        {
            anioBaseVisible = false;
        }

    }
    
    /**
    *
    * Metodo ejecutado al oprimir el boton Aceptar del dialogo
    * AnioDestino en la vista
    *
    *
    */
   public void aceptarAnioDestino()
   {

       // </CODIGO_DESARROLLADO>
   }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() 
    {
		anioBase = String.valueOf(SysmanFunciones
		                .ano(new Date()));
		reasignarOrigen();
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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

        registro.getCampos().remove("NOMBRE_NATURALEZA");

        registro.getCampos().remove("NOMBRE_ACTIVIDAD");
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_UTILIZADO
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * @return the listaanioBase
	 */
	public List<Registro> getListaanioBase() {
		return listaanioBase;
	}

	/**
	 * @param listaanioBase the listaanioBase to set
	 */
	public void setListaanioBase(List<Registro> listaanioBase) {
		this.listaanioBase = listaanioBase;
	}

	/**
	 * @return the listaanioDestino
	 */
	public List<Registro> getListaanioDestino() {
		return listaanioDestino;
	}

	/**
	 * @param listaanioDestino the listaanioDestino to set
	 */
	public void setListaanioDestino(List<Registro> listaanioDestino) {
		this.listaanioDestino = listaanioDestino;
	}

	/**
	 * @return the anioBase
	 */
	public String getAnioBase() {
		return anioBase;
	}

	/**
	 * @param anioBase the anioBase to set
	 */
	public void setAnioBase(String anioBase) {
		this.anioBase = anioBase;
	}

	/**
	 * @return the anioDestino
	 */
	public String getAnioDestino() {
		return anioDestino;
	}

	/**
	 * @param anioDestino the anioDestino to set
	 */
	public void setAnioDestino(String anioDestino) {
		this.anioDestino = anioDestino;
	}

	/**
	 * @return the anioBaseVisible
	 */
	public boolean isAnioBaseVisible() {
		return anioBaseVisible;
	}

	/**
	 * @param anioBaseVisible the anioBaseVisible to set
	 */
	public void setAnioBaseVisible(boolean anioBaseVisible) {
		this.anioBaseVisible = anioBaseVisible;
	}
}
