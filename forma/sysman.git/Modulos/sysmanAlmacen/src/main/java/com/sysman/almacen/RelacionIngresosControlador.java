/*-
 * RelacionIngresosControlador.java
 *
 * 1.0
 * 
 * 05/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.application.resource.StreamedContentHandler;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

import com.sysman.almacen.enums.RelacionIngresosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Formulario que permite generar informes almacen como 
 * Relacion de ingresos de devolutivos y consumo de almacen
 * @version 1.0, 05/07/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class  RelacionIngresosControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;

    private String mes;
    /**
     * guarda valor seleccionado en combo anio
     */
    private String ano;
    //</DECLARAR_ATRIBUTOS>
    
    private String titulo;
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    /**
     * caarga lista de los anios
     */
    private List<Registro> listaAno;
    
    private String menuActual;
    
    private boolean especial;
    
    private String reporte;
    
    private String reporteConsulta;
    
    private StreamedContent archivoDescarga;
    
    @EJB
    private EjbSysmanUtil ejbSysmanUtil;
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RelacionIngresosControlador
     */
    public RelacionIngresosControlador() {
        super();
        compania = SessionUtil.getCompania();
        menuActual = SessionUtil.getMenuActual();
        try {
            //1850
            numFormulario = GeneralCodigoFormaEnum.FRM_RELACIONINGRESOS_CONTROLADOR.getCodigo();
            validarPermisos();
            //<INI_ADICIONAL>
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
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
    public void inicializar(){
        //<CARGAR_LISTA>
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CREAR_ARBOLES>
        //</CREAR_ARBOLES>
        abrirFormulario();
        cargarListaAno();
        cargarValores();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    //<METODOS_CARGAR_LISTA>

    public void cargarListaAno(){
        
        try {
            
            Map<String, Object> parametros = new HashMap<>();
            
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            
            listaAno = RegistroConverter.toListRegistro(requestManager.
                            getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                                            RelacionIngresosControladorUrlEnum.URL7682.getValue()).getUrl(), 
                                            parametros));
            
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
                        
    }
    
    public void cargarValores() {
        if ("1004020309".equals(SessionUtil.getMenuActual())) {
            titulo = "RELACION DE INGRESOS DE DEVOLUTIVOS Y CONSUMO DE ALMACEN";
        } else if ("1004020310".equals(SessionUtil.getMenuActual())) {
            titulo = "RELACION DE EGRESOS DE DEVOLUTIVOS Y CONSUMO DE ALMACEN";
        }
        
        
    }

    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf
     * en la vista
     *
     */
    public void oprimirPdf()
    {
        //<CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excell
     * en la vista
     *
     */
    public void oprimirExcell() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        //</CODIGO_DESARROLLADO>
    }
    
    public void generarInforme(ReportesBean.FORMATOS formato) {
        
        try {
        
      validarInforme();
        
        Map<String, Object> reemplazos = new HashMap<>();
        reemplazos.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        reemplazos.put("s$ano$s", ano);
        reemplazos.put("s$mes$s", mes);
        
        String sql = Reporteador.resuelveConsulta(reporteConsulta, Integer.parseInt(SessionUtil.getModulo()), reemplazos);
        
        Map<String, Object> parametros = new HashMap<>();
        
        
        parametros.put("PR_TITULO", titulo);
        parametros.put("PR_ESPECIAL", especial);
        parametros.put("PR_FIRMACUENTADANTE", ejbSysmanUtil.consultarParametro(compania, "FIRMA CUENTADANTE", SessionUtil.getModulo(), new Date(), false));
        parametros.put("PR_FIRMAFISCAL", ejbSysmanUtil.consultarParametro(compania, "FIRMA FISCAL", SessionUtil.getModulo(), new Date(), false));
        parametros.put("PR_NAMECONTADOR", ejbSysmanUtil.consultarParametro(compania, "NAME CONTADOR", SessionUtil.getModulo(), new Date(), false));
        parametros.put("PR_STRSQL", sql);
        
        archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SystemException | JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }

    public void validarInforme() {
        if ("1004020309".equals(menuActual)){
            reporteConsulta = reporte = "001824IIngresos";
        } else { //1004020310
            if (especial) {
                reporte = "001826IEgresosCC";
                
            } else {
                reporte = "001825IEgresos";
            } 
            reporteConsulta = "001825IEgresos";
        }
    }
    /**
     * Retorna la variable mes
     * 
     * @return  mes
     */
    public String getMes() {
        return mes;
    }
    /**
     * Asigna la variable  mes
     * 
     * @param  mes
     * Variable a asignar en  mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }
    /**
     * Retorna la variable ano
     * 
     * @return  ano
     */
    public String getAno() {
        return ano;
    }
    /**
     * Asigna la variable  ano
     * 
     * @param  ano
     * Variable a asignar en  ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
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
     * Variable a asignar en  listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public boolean isEspecial() {
        return especial;
    }
    public void setEspecial(boolean especial) {
        this.especial = especial;
    }
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
    
    
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
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
    //</SET_GET_LISTAS_COMBO_GRANDE>
}
