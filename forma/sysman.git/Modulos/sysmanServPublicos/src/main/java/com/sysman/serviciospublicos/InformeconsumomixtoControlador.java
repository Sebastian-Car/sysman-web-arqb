/*-
 * InformeconsumomixtoControlador.java
 *
 * 1.0
 * 
 * 26/10/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.InformeconsumomixtoControladorEnum;
import com.sysman.serviciospublicos.enums.InformeconsumomixtoControladorUrlEnum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;
/**
 * Controlador del formulario  InformeconsumomixtoControlador
 *
 * @version 1.0, 26/10/2016
 * @author cperez
 * 
 * @version 2, 02/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */

@ManagedBean
@ViewScoped
public class  InformeconsumomixtoControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    //<DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el ciclo de la consulta
     */
    private String ciclo;
    /**
     * Obtiene el codigoInicial de la consulta
     */
    private String codigoIncial;
    /**
     * Obtiene el codigoFinal de la consulta
     */
    private String codigoFinal;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    /* 
     * 
     */
    private String codigoRutaConstante;
    private StreamedContent archivoDescarga;
    /**
     * Cambia el estado de visible a false del codigo final y el inicial
     */
    private boolean bloquearCodigo;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    /**
     * Obtiene el ciclo de la consulta
     */
    private List<Registro> listaCiclo;
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista el combo de codigo inicial 
     */
    private RegistroDataModelImpl listacmbCodigoInicial;
    /**
     * Lista el combo de codigo final
     */
    private RegistroDataModelImpl listaCmbCodigoFinal;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformeconsumomixtoControlador
     */
    public InformeconsumomixtoControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoRutaConstante= "CODIGORUTA";
        try {
            numFormulario=GeneralCodigoFormaEnum.INFORMECONSUMOMIXTO_CONTROLADOR.getCodigo();
            validarPermisos();
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        }finally{
            SessionUtil.cleanFlash();
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
        cargarListaCiclo();  
        cargarListacmbCodigoInicial(); 
        cargarListaCmbCodigoFinal();  
        abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        bloquearCodigo = true;
        //</CODIGO_DESARROLLADO>
    }
    //<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCiclo
     * 
     */
    public void cargarListaCiclo(){
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeconsumomixtoControladorUrlEnum.URL4935
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    /**
     * 
     * Carga la lista listacmbCodigoInicial
     */
    public void cargarListacmbCodigoInicial(){
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(InformeconsumomixtoControladorUrlEnum.URL5343.getValue());    
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.CICLO.getName(),ciclo);

        listacmbCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,true,codigoRutaConstante);
    }
    /**
     * 
     * Carga la lista listaCmbCodigoFinal
     */
    public void cargarListaCmbCodigoFinal(){
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(InformeconsumomixtoControladorUrlEnum.URL5956.getValue());    
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.CICLO.getName(),ciclo);
        param.put(InformeconsumomixtoControladorEnum.PARAM0.getValue(),codigoIncial);

        listaCmbCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,true,codigoRutaConstante);
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf
     * en la vista
     *
     */
    public void oprimirPdf() {
        //<CODIGO_DESARROLLADO>  
        archivoDescarga=null;       
        genInforme(FORMATOS.PDF, "001176rptConsumoMixto");
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel
     * en la vista
     *
     */
    public void oprimirExcel() {
        //<CODIGO_DESARROLLADO>      
        archivoDescarga=null;  
        genInforme(FORMATOS.EXCEL, "001176rptConsumoMixto");
        //</CODIGO_DESARROLLADO>
    }
    //</METODOS_BOTONES>
    public void genInforme(ReportesBean.FORMATOS formato, String reporte)
    {


        try
        {
            String cicloInicial;
            String cicloFinal;
            String codigoInicialSeleccion;
            String codigoFinalSeleccion;


            codigoInicialSeleccion = codigoIncial;
            codigoFinalSeleccion = codigoFinal;
            cicloInicial= ciclo;
            cicloFinal = ciclo;
            archivoDescarga = null;       
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoInicialSeleccion", codigoInicialSeleccion);
            reemplazar.put("codigoFinalSeleccion", codigoFinalSeleccion);
            reemplazar.put("cicloInicial", cicloInicial);
            reemplazar.put("cicloFinal", cicloFinal);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_INFORMECONSUMOMIXTO_CICLO", ciclo);
            parametros.put("PR_FORMS_INFORMECONSUMOMIXTO_CMBCODIGOINICIAL", codigoInicialSeleccion);
            parametros.put("PR_FORMS_INFORMECONSUMOMIXTO_CMBCODIGOFINAL", codigoFinalSeleccion);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(InformeconsumomixtoControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        } 

        catch ( JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + " "
                                            + ex.getMessage());
            Logger.getLogger(InformeconsumomixtoControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {        
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }


    }
    //<METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     * 
     */
    public void cambiarCiclo() {
        //<CODIGO_DESARROLLADO>
        codigoIncial="";
        codigoFinal="";
        cargarListacmbCodigoInicial();
        cargarListaCmbCodigoFinal();

        //</CODIGO_DESARROLLADO>
    }
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoInicial
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoIncial= registroAux.getCampos().get(codigoRutaConstante).toString();
        cargarListaCmbCodigoFinal();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbCodigoFinal
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal= registroAux.getCampos().get(codigoRutaConstante).toString();
    }
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>
    //</METODOS_ARBOL>
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     * 
     * @return  ciclo
     */

    public String getCiclo() {
        return ciclo;
    }
    public boolean isBloquearCodigo()
    {
        return bloquearCodigo;
    }
    public void setBloquearCodigo(boolean bloquearCodigo)
    {
        this.bloquearCodigo = bloquearCodigo;
    }
    /**
     * Asigna la variable  ciclo
     * 
     * @param  ciclo
     * Variable a asignar en  ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }
    /**
     * Retorna la variable codigoIncial
     * 
     * @return  codigoIncial
     */
    public String getCodigoIncial() {
        return codigoIncial;
    }
    /**
     * Asigna la variable  codigoIncial
     * 
     * @param  codigoIncial
     * Variable a asignar en  codigoIncial
     */
    public void setCodigoIncial(String codigoIncial) {
        this.codigoIncial = codigoIncial;
    }
    /**
     * Retorna la variable codigoFinal
     * 
     * @return  codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }
    /**
     * Asigna la variable  codigoFinal
     * 
     * @param  codigoFinal
     * Variable a asignar en  codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }
    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en  listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>     


    public RegistroDataModelImpl getListacmbCodigoInicial() {
        return listacmbCodigoInicial;
    }
    public void setListacmbCodigoInicial(
        RegistroDataModelImpl listacmbCodigoInicial) {
        this.listacmbCodigoInicial = listacmbCodigoInicial;
    }
    public RegistroDataModelImpl getListaCmbCodigoFinal() {
        return listaCmbCodigoFinal;
    }
    public void setListaCmbCodigoFinal(RegistroDataModelImpl listaCmbCodigoFinal) {
        this.listaCmbCodigoFinal = listaCmbCodigoFinal;
    }

    //</SET_GET_LISTAS_COMBO_GRANDE>
}
