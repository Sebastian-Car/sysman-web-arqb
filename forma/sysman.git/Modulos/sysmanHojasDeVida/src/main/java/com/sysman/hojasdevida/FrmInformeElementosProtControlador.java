/*-
s * FrmInformeElementosProtControlador.java
 *
 * 1.0
 * 
 * 18/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.FrmInformeElementosProtControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite la generacion del reporte relacionado con elementos de proteccion y dotacion personal
 *
 * @version 1.0, 18/01/2018
 * @author jromero
 */
@ManagedBean
@ViewScoped

public class FrmInformeElementosProtControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo elemento del nombre seleccionado en la vista
     */
    private String codigo;
    /**
     * Atributo que almacena el codigo del nombre seleccionado en la vista
     */
    private String dependencia;
    /**
     * Atributo que almacena el codigo del cargo seleccionado en la Vista
     */
    private String cargo;
    /**
     * Atributo que almacena la fecha inicial de fecha entrega
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena la fecha final de fecha entrega
     */
    private Date fechaFinal;
    /** 
     *Atributo que almacena el orden del formulario 
     */
    private int ordenar;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listas que almacenan codigo elemento
     */
    private RegistroDataModelImpl listacmbCodigoElemento;
    private RegistroDataModelImpl listacmbCodigoElementoFinal;
    /**
     * Listas que almacenan el nombre de dependencia
     */
    private RegistroDataModelImpl listacmbDependencia;
    private RegistroDataModelImpl listacmbDependenciaFinal;
    /**
     * Lista que almacena nombre cargo
     */
    private RegistroDataModelImpl listacmbCargo;
    
    private String modulo;
    private String codigoDependencia;
    private String codigoCargo;
    private String codigoElemento;
    private String codigoElementoFinal;
    private String codigoDependenciaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmInformeElementosProtControlador
     */
    public FrmInformeElementosProtControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_INFORME_ELEMENTOS_PROT_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbCodigoElemento();
        cargarListacmbCodigoElementoFinal();
        cargarListacmbDependencia();
        cargarListacmbDependenciaFinal();
        cargarListacmbCargo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    	ordenar = 1; 
    	fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga las listas listacmbCodigoElemento y listacmbCodigoElementoFinal
     *
     */
    public void cargarListacmbCodigoElemento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInformeElementosProtControladorUrlEnum.URL4068
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbCodigoElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOELEMENTO");
    }

    public void cargarListacmbCodigoElementoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInformeElementosProtControladorUrlEnum.URL4069
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGOELEMENTOS", codigoElemento);
        
        listacmbCodigoElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOELEMENTO");
    }
    /**
     * 
     * Carga las listas listacmbDependencia y listacmbDependenciaFinal
     *
     */
    public void cargarListacmbDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInformeElementosProtControladorUrlEnum.URL4931
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        
        listacmbDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }
    
    public void cargarListacmbDependenciaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInformeElementosProtControladorUrlEnum.URL4932
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGOD", codigoDependencia);
        
        listacmbDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    /**
     * 
     * Carga la lista listacmbCargo
     *
     */
    public void cargarListacmbCargo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInformeElementosProtControladorUrlEnum.URL5589
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbCargo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID_DE_CARGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
    	archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.EXCEL);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PDF en la vista
     *
     *
     */
    public void oprimirPdf() {
    	archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
    }

    private void generarReporte(FORMATOS formato) {
        archivoDescarga = null;

        try {
        	
            Map<String, Object> reemplazos = new TreeMap<>();
            Map<String, Object> parametros = new TreeMap<>();

            reemplazos.put("compania", compania);
            reemplazos.put("codigoElemento", codigoElemento);
            reemplazos.put("codigoElementoFinal", codigoElementoFinal);
            reemplazos.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazos.put("codigoDependencia", codigoDependencia);
            reemplazos.put("codigoDependenciaFinal", codigoDependenciaFinal);
            reemplazos.put("codigoCargo", "'"+codigoCargo+"'");
            reemplazos.put("ordenar",ordenar);

            Reporteador.resuelveConsulta("001648RPTINFORMEELEMENTOSPROT",
                            Integer.parseInt(modulo), reemplazos, parametros);
        	

        	archivoDescarga = JsfUtil.exportarStreamed(
                            "001648RPTINFORMEELEMENTOSPROT",
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbCodigoElemento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoElemento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoElemento = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGOELEMENTO"),"").toString();
        codigoElementoFinal = null;
        cargarListacmbCodigoElementoFinal();
    }
    
    public void seleccionarFilacmbCodigoElementoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoElementoFinal = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGOELEMENTO"),"").toString();
        
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbDependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoDependencia = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"),"").toString();
        codigoDependenciaFinal = null;
        cargarListacmbDependenciaFinal();
    }
    
    public void seleccionarFilacmbDependenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoDependenciaFinal =  SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"),"").toString();
        
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbCargo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCargo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoCargo = SysmanFunciones.nvl(registroAux.getCampos().get("ID_DE_CARGO"),"").toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigo
     * 
     * @return codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Asigna la variable codigo
     * 
     * @param codigo
     * Variable a asignar en codigo
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Retorna la variable dependencia
     * 
     * @return dependencia
     */
    public String getDependencia() {
        return dependencia;
    }

    /**
     * Asigna la variable dependencia
     * 
     * @param dependencia
     * Variable a asignar en dependencia
     */
    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    /**
     * Retorna la variable cargo
     * 
     * @return cargo
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * Asigna la variable cargo
     * 
     * @param cargo
     * Variable a asignar en cargo
     */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
   

    /**
     * Retorna la lista listacmbCargo
     * 
     * @return listacmbCargo
     */
    public RegistroDataModelImpl getListacmbCargo() {
        return listacmbCargo;
    }

    /**
     * Asigna la lista listacmbCargo
     * 
     * @param listacmbCargo
     * Variable a asignar en listacmbCargo
     */
    public void setListacmbCargo(RegistroDataModelImpl listacmbCargo) {
        this.listacmbCargo = listacmbCargo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public String getCompania() {
        return compania;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getCodigoDependencia() {
        return codigoDependencia;
    }

    public void setCodigoDependencia(String codigoDependencia) {
        this.codigoDependencia = codigoDependencia;
    }

    public String getCodigoCargo() {
        return codigoCargo;
    }

    public void setCodigoCargo(String codigoCargo) {
        this.codigoCargo = codigoCargo;
    }

    public String getCodigoElemento() {
        return codigoElemento;
    }

    public void setCodigoElemento(String codigoElemento) {
        this.codigoElemento = codigoElemento;
    }


	public int getOrdenar() {
		return ordenar;
	}

	public void setOrdenar(int ordenar) {
		this.ordenar = ordenar;
	}

	public RegistroDataModelImpl getListacmbCodigoElemento() {
		return listacmbCodigoElemento;
	}

	public void setListacmbCodigoElemento(RegistroDataModelImpl listacmbCodigoElemento) {
		this.listacmbCodigoElemento = listacmbCodigoElemento;
	}

	public RegistroDataModelImpl getListacmbCodigoElementoFinal() {
		return listacmbCodigoElementoFinal;
	}

	public void setListacmbCodigoElementoFinal(RegistroDataModelImpl listacmbCodigoElementoFinal) {
		this.listacmbCodigoElementoFinal = listacmbCodigoElementoFinal;
	}

	public RegistroDataModelImpl getListacmbDependencia() {
		return listacmbDependencia;
	}

	public void setListacmbDependencia(RegistroDataModelImpl listacmbDependencia) {
		this.listacmbDependencia = listacmbDependencia;
	}

	public RegistroDataModelImpl getListacmbDependenciaFinal() {
		return listacmbDependenciaFinal;
	}

	public void setListacmbDependenciaFinal(RegistroDataModelImpl listacmbDependenciaFinal) {
		this.listacmbDependenciaFinal = listacmbDependenciaFinal;
	}

	public String getCodigoElementoFinal() {
		return codigoElementoFinal;
	}

	public void setCodigoElementoFinal(String codigoElementoFinal) {
		this.codigoElementoFinal = codigoElementoFinal;
	}

	public String getCodigoDependenciaFinal() {
		return codigoDependenciaFinal;
	}

	public void setCodigoDependenciaFinal(String codigoDependenciaFinal) {
		this.codigoDependenciaFinal = codigoDependenciaFinal;
	}
    
	

}
