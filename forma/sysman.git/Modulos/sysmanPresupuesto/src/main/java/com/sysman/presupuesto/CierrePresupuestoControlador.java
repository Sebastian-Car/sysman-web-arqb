/*-
 * CierrePresupuestoControlador.java
 *
 * 1.0
 * 
 * 30/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoCierreRemote;
import com.sysman.presupuesto.enums.CierrePresupuestoControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

/**
 * Clase migrada para ejecutar el cierre presupuestal
 *
 * @version 1.0, 30/11/2018
 * @author asana
 */
/**
 * @author ybecerra
 *
 */
@ManagedBean
@ViewScoped
public class CierrePresupuestoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Año a generar cierre
     */
    private int ano;
    /**
     * Nombre de la compañia de Ingreso que se ejecuta cierre
     */
    private String nombreCompania;
    /**
     * Atributo que almacena el valor del indicador Cierre Vigencias
     * Futuras.
     */
    private boolean cierreFuturas;
    /**
     * Atributo que almacena el valor del indicador Cierre Vigencias
     * Futuras a Pasivos Exigibles
     */
    private boolean cierreFuturasPasivos;
    /**
     * Atributo que almacean el valor del indicador Cierre Regalias
     */
    private boolean cierreRegalias;
    /**
     * Atributo que almacena el valor del indicador Cierre Vigencia
     */
    private boolean cierreVigencia;
    /**
     * Atributo que almacena el valor del indicador Cierre Pasivos
     * Exigibles
     */
    private boolean cierrePasivos;
    /**
     * Atributo que almacena el valor del indicador Cofinanciados
     */
    private boolean cierreCofinanciados;
    
    /*
     * 
     */
    private boolean visibleEjecutar;
    
    /**
     * Permite hacer visible el di&aacute;logo confirmarCierre.
     */
    private boolean confirmarCierre;   


	/**
     * Año a generar cierre
     */
    private Date fechaCierre;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * lista de años de la compañia
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbPresupuestoCierreRemote ejbPresupuestoCierre;

    /**
     * Crea una nueva instancia de CierrePresupuestoControlador
     */
    public CierrePresupuestoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1999
            numFormulario = GeneralCodigoFormaEnum.CIERRE_PRESUPUESTO_CONTROLADOR
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
        // <CARGAR_LISTA>
        try {
        	visibleEjecutar =  false;
            nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
            ano = SysmanFunciones.ano(new Date()) - 1;

            fechaCierre = SysmanFunciones.convertirAFecha(
                            "01/01/" + SysmanFunciones.ano(new Date()));

            cargarListaAno();
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            // </CARGAR_LISTA_COMBO_GRANDE>
            // <CREAR_ARBOLES>
            // </CREAR_ARBOLES>
            abrirFormulario();
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     *
     * Carga las listas de años de la compañia
     */
    public void cargarListaAno() {

        Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {

            listaAno = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            CierrePresupuestoControladorUrlEnum.URL7252
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            parametros));
        }
        catch (SystemException e) {
            Logger.getLogger(PlanpresupuestalptosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    
    
    public void aceptarconfirmaEjecutar() {
        // <CODIGO_DESARROLLADO>
    	 confirmarCierre =  false; 
    	 archivoDescarga = null;
         try {
             ejbPresupuestoCierre.cierrePresupuestalCb(compania, ano,
                             SessionUtil.getUser().getCodigo(), cierreVigencia,
                             cierrePasivos, cierreFuturas, cierreFuturasPasivos,
                             cierreRegalias, fechaCierre, cierreCofinanciados);
             JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

         }
         catch (SystemException e) {

             Logger.getLogger(PlanpresupuestalptosControlador.class.getName())
                             .log(Level.SEVERE, null, e);
             JsfUtil.agregarMensajeError(e.getMessage());
         }

       
        // </CODIGO_DESARROLLADO>
    }
    
    public void cancelarconfirmaEjecutar() {
        // <CODIGO_DESARROLLADO>
    	   confirmarCierre =  false;       
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     * Proceso que genera proceso
     *
     */
    public void oprimirEjecutar() {
        // <CODIGO_DESARROLLADO>
    	archivoDescarga = null;
    	confirmarCierre =  true;
        // </CODIGO_DESARROLLADO>
    }
    
    

    /**
     * 
     * Metodo ejecutado al oprimir el boton Revisar en la vista
     *
     *
     */
    public void oprimirRevisar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            String revisarCierre = ejbPresupuestoCierre.validarCierrePlan(
                            compania, ano, cierreVigencia, cierrePasivos,
                            cierreFuturas, cierreFuturasPasivos,
                            cierreRegalias, cierreCofinanciados);
            String separadorRegistros = SysmanConstantes.SEPARADOR_REG;
            String separadorColumnas = SysmanConstantes.SEPARADOR_COL;
            String separadorHojas = SysmanConstantes.SEPARADOR_HOJ;

            String nombreDocumento = "Revisar Cierre Presupuestal";
            archivoDescarga = JsfUtil.armarExcelconHoja(revisarCierre,
                            separadorHojas, separadorRegistros,
                            separadorColumnas, nombreDocumento);
            JsfUtil.agregarMensajeInformativo("Proceso terminado Con exito.");
            visibleEjecutar =  false;
        }
        catch (SystemException e) {

            Logger.getLogger(PlanpresupuestalptosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Reversar en la vista
     *
     *
     */
    public void oprimirReversar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        try {
            fechaCierre = SysmanFunciones.convertirAFecha(
                            "01/01/" +(ano+1));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void cambiarFechaCierre() {
        if(SysmanFunciones.ano(fechaCierre)!=(ano+1)) {
            fechaCierre=null;
           JsfUtil.agregarMensajeError(idioma.getString("TB_TB4292")); 
        }
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable nombreCompania
     * 
     * @return nombreCompania
     */
    public String getNombreCompania() {
        return nombreCompania;
    }

    /**
     * Asigna la variable nombreCompania
     * 
     * @param nombreCompania
     * Variable a asignar en nombreCompania
     */
    public void setNombreCompania(String nombreCompania) {
        this.nombreCompania = nombreCompania;
    }

    /**
     * Retorna la variable cierreFuturas
     * 
     * @return cierreFuturas
     */
    public boolean isCierreFuturas() {
        return cierreFuturas;
    }

    /**
     * Asigna la variable cierreFuturas
     * 
     * @param cierreFuturas
     * Variable a asignar en cierreFuturas
     */
    public void setCierreFuturas(boolean cierreFuturas) {
        this.cierreFuturas = cierreFuturas;
    }

    /**
     * Retorna la variable cierreFuturasPasivos
     * 
     * @return cierreFuturasPasivos
     */
    public boolean isCierreFuturasPasivos() {
        return cierreFuturasPasivos;
    }

    /**
     * Asigna la variable cierreFuturasPasivos
     * 
     * @param cierreFuturasPasivos
     * Variable a asignar en cierreFuturasPasivos
     */
    public void setCierreFuturasPasivos(boolean cierreFuturasPasivos) {
        this.cierreFuturasPasivos = cierreFuturasPasivos;
    }

    /**
     * Retorna la variable cierreRegalias
     * 
     * @return cierreRegalias
     */
    public boolean isCierreRegalias() {
        return cierreRegalias;
    }

    /**
     * Asigna la variable cierreRegalias
     * 
     * @param cierreRegalias
     * Variable a asignar en cierreRegalias
     */
    public void setCierreRegalias(boolean cierreRegalias) {
        this.cierreRegalias = cierreRegalias;
    }

    /**
     * Retorna la variable cierreVigencia
     * 
     * @return cierreVigencia
     */
    public boolean isCierreVigencia() {
        return cierreVigencia;
    }

    /**
     * Asigna la variable cierreVigencia
     * 
     * @param cierreVigencia
     * Variable a asignar en cierreVigencia
     */
    public void setCierreVigencia(boolean cierreVigencia) {
        this.cierreVigencia = cierreVigencia;
    }

    /**
     * Retorna la variable cierrePasivos
     * 
     * @return cierrePasivos
     */
    public boolean isCierrePasivos() {
        return cierrePasivos;
    }

    /**
     * Asigna la variable cierrePasivos
     * 
     * @param cierrePasivos
     * Variable a asignar en cierrePasivos
     */
    public void setCierrePasivos(boolean cierrePasivos) {
        this.cierrePasivos = cierrePasivos;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    /**
     * Retorna la variable cierreCofinanciados
     * 
     * @return  cierreCofinanciados
     */
    public boolean getCierreCofinanciados() {
    	return cierreCofinanciados;
    }
    /**
     * Asigna la variable  cierreCofinanciados
     * 
     * @param  cierreCofinanciados
     * Variable a asignar en  cierreCofinanciados
     */
    public void setCierreCofinanciados(boolean cierreCofinanciados) {
    	this.cierreCofinanciados = cierreCofinanciados;
    }

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

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
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
    
    public boolean isVisibleEjecutar() {
		return visibleEjecutar;
	}

	public void setVisibleEjecutar(boolean visibleEjecutar) {
		this.visibleEjecutar = visibleEjecutar;
	}

	public boolean isConfirmarCierre() {
		return confirmarCierre;
	}

	public void setConfirmarCierre(boolean confirmarCierre) {
		this.confirmarCierre = confirmarCierre;
	}

	
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
