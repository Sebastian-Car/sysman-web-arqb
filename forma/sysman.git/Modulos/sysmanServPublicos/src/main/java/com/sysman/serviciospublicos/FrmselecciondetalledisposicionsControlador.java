/*-
 * FrmselecciondetalledisposicionsControlador.java
 *
 * 1.0
 * 
 * 02/11/2016
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.FrmselecciondetalledisposicionsControladorEnum;
import com.sysman.serviciospublicos.enums.FrmselecciondetalledisposicionsControladorUrlEnum;

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
 * Clase que se encarga de generar informes de detalle de disposicion
 * totalizando el pesoaseo segun lo seleccionado desde el formulario
 *
 * @version 1.0, 02/11/2016
 * @author jguerrero
 * 
 * @version 2, 01/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */
@ManagedBean
@ViewScoped
public class FrmselecciondetalledisposicionsControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante encargada de almancenar el String CODIGORUTA
     */
    private final String codigoRutaCons;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que se encarga de almacenar temporalmente lo
     * seleccionado del check de totalizado desde el formulario
     */
    private String checkTotalizado;
    /**
     * Variable que almacena temporalmente lo seleccionado del combo
     * de codigoRutaInicial del formulario
     */
    private String codigoRutaInicial;
    /**
     * Variable que almacena temporalmente lo seleccionado del combo
     * de ciclo del formulario
     */
    private String ciclo;
    /**
     * Variable que almacena temporalmente lo seleccionado del combo
     * de codigoRutaFinal del formulario
     */
    private String codigoRutaFinal;
    /**
     * Variable que almacena temporalmente lo seleccionado del combo
     * de año del formulario
     */
    private String ano;
    /**
     * Variable que almacena temporalmente lo seleccionado del combo
     * periodo del formulario
     */
    private String periodo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Atributo que se encarga de almacenar temporalmente los datos de
     * la consulta del informe a reemplazar
     */

    HashMap<String, Object> reemplazar;
    /**
     * Atributo que se encarga de almacenar temporalmente los
     * parametros del informe a reemplazar
     */

    HashMap<String, Object> parametrosBean;

    /**
     * Atributo que se encarga de almacenar temporalmente los datos
     * del resultado de laconsulta a la base de datos del combo ciclo
     */
    private List<Registro> listaCiclo;
    /**
     * Atributo que se encarga de almacenar temporalmente los datos
     * del resultado de laconsulta a la base de datos del combo ano
     */
    private List<Registro> listaAnio;
    /**
     * Atributo que se encarga de almacenar temporalmente los datos
     * del resultado de laconsulta a la base de datos del combo
     * periodo
     */
    private List<Registro> listaPeriodo;
    /**
     * Atributo que se encarga de almacenar temporalmente los datos
     * del resultado de laconsulta a la base de datos del combo
     * codigoRuta
     */
    private RegistroDataModelImpl listaCodigoruta;
    /**
     * Atributo que se encarga de almacenar temporalmente los datos
     * del resultado de laconsulta a la base de datos del
     * codigoRutaFinal
     */
    private RegistroDataModelImpl listaCodFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmselecciondetalledisposicionsControlador
     */
    public FrmselecciondetalledisposicionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        reemplazar = new HashMap<>();
        parametrosBean = new HashMap<>();
        codigoRutaCons = "CODIGORUTA";

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMSELECCIONDETALLEDISPOSICIONS_CONTROLADOR.getCodigo();
            validarPermisos();   
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
        cargarListaCiclo();
        cargarListaAnio();      
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
     * Carga la lista listaCiclo
     *
     * Metodo que se encarga de hacer la consutla de la base de datos
     * del combo ciclo
     */
    public void cargarListaCiclo() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmselecciondetalledisposicionsControladorUrlEnum.URL6681
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
     * Carga la lista listaAnio
     *
     * Metodo que se encarga de hacer la consutla de la base de datos
     * del combo ano
     */
    public void cargarListaAnio() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmselecciondetalledisposicionsControladorUrlEnum.URL7113
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
     * Carga la lista listaPeriodo
     *
     * Metodo que se encarga de hacer la consutla de la base de datos
     * del combo periodo
     */
    public void cargarListaPeriodo() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.ANO.getName(),ano);
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmselecciondetalledisposicionsControladorUrlEnum.URL7660
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
     * Carga la lista listaCodigoruta
     *
     * Metodo que se encarga de hacer la consutla de la base de datos
     * del combo codigoRuta
     */
    public void cargarListaCodigoruta() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmselecciondetalledisposicionsControladorUrlEnum.URL8266.getValue());        
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CICLO.getName(),ciclo);
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaCodigoruta = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoRutaCons);
    }

    /**
     * 
     * Carga la lista listaCodFinal
     *
     * Metodo que se encarga de hacer la consutla de la base de datos
     * del combo CodigoFinal
     */
    public void cargarListaCodFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmselecciondetalledisposicionsControladorUrlEnum.URL9191.getValue());        
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CICLO.getName(),ciclo);
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(FrmselecciondetalledisposicionsControladorEnum.PARAM0.getValue(),codigoRutaInicial);

        listaCodFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoRutaCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     * Metodo encargado de exportar el informe en formato excel
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        logicaInforme();
        genInforme(ReportesBean.FORMATOS.EXCEL);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     * Metodo encargado de exportar el informe en formato excel
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        logicaInforme();
        genInforme(ReportesBean.FORMATOS.PDF);

    }
    private void logicaInforme() {
        if ("true".equals(checkTotalizado)) {

            reemplazar.put("sumPesoAseo",
                            " SUM(SP_DETALLEDISPOSICION.PESOASEO)");
            reemplazar.put("ano", ano);
            reemplazar.put("periodo", periodo);
            reemplazar.put("codigoRutaInicial", codigoRutaInicial);
            reemplazar.put("codigoRutaFinal", codigoRutaFinal);
            reemplazar.put("groupBy", "GROUP BY SP_USUARIO.COMPANIA," +
                            "  SP_USUARIO.NOMBRES," +
                            "  SP_USUARIO.PRIMERAPELLIDO," +
                            "  SP_USUARIO.SEGUNDOAPELLIDO," +
                            "  SP_USUARIO.CICLO," +
                            "  SP_DETALLEDISPOSICION.ANO," +
                            "  SP_DETALLEDISPOSICION.PERIODO," +
                            "  SP_USUARIO.CODIGORUTA");

        }
        else {
            reemplazar.put("sumPesoAseo", " SP_DETALLEDISPOSICION.PESOASEO");
            reemplazar.put("ano", ano);
            reemplazar.put("periodo", periodo);
            reemplazar.put("codigoRutaInicial", codigoRutaInicial);
            reemplazar.put("codigoRutaFinal", codigoRutaFinal);
            reemplazar.put("groupBy", "");
        }
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        String reporte = "001194InfTotalesDisposicionFinal";
        try {       
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametrosBean);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametrosBean,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(FrmselecciondetalledisposicionsControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        } 
        catch (JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                            + ex.getMessage());
            Logger.getLogger(FrmselecciondetalledisposicionsControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {     
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }      

    }
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     * 
     * Metodo que se ejecuta cuando se cambia el combo ciclo en el
     * formulario
     * 
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoRutaInicial = null;
        codigoRutaFinal = null;
        cargarListaCodigoruta();
        listaCodFinal = null;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     * Metodo que se ejecuta cuando se cambia el combo ano en el
     * formulario
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        periodo = null;

        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoruta
     *
     * Metodo que se ejecuta cuando se cambia el combo
     * codigoRutainicial en el formulario
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoruta(SelectEvent event) {
        codigoRutaFinal = null;
        Registro registroAux = (Registro) event.getObject();
        codigoRutaInicial = registroAux.getCampos().get(codigoRutaCons)
                        .toString();

        cargarListaCodFinal();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodFinal
     *
     * Metodo que se ejecuta cuando se cambia el combo codigoRutaFinal
     * en el formulario
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoRutaFinal = registroAux.getCampos().get(codigoRutaCons)
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable checkTotalizado
     * 
     * @return checkTotalizado
     */
    public String getCheckTotalizado() {
        return checkTotalizado;
    }

    /**
     * Asigna la variable checkTotalizado
     * 
     * @param checkTotalizado
     * Variable a asignar en checkTotalizado
     */
    public void setCheckTotalizado(String checkTotalizado) {
        this.checkTotalizado = checkTotalizado;
    }

    /**
     * Retorna la variable codigoRutaInicial
     * 
     * @return codigoRutaInicial
     */
    public String getCodigoRutaInicial() {
        return codigoRutaInicial;
    }

    /**
     * Asigna la variable codigoRutaInicial
     * 
     * @param codigoRutaInicial
     * Variable a asignar en codigoRutaInicial
     */
    public void setCodigoRutaInicial(String codigoRutaInicial) {
        this.codigoRutaInicial = codigoRutaInicial;
    }

    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable codigoRutaFinal
     * 
     * @return codigoRutaFinal
     */
    public String getCodigoRutaFinal() {
        return codigoRutaFinal;
    }

    /**
     * Asigna la variable codigoRutaFinal
     * 
     * @param codigoRutaFinal
     * Variable a asignar en codigoRutaFinal
     */
    public void setCodigoRutaFinal(String codigoRutaFinal) {
        this.codigoRutaFinal = codigoRutaFinal;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }   
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
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    /**
     * Retorna la lista listaPeriodo
     * 
     * @return listaPeriodo
     */
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     * 
     * @param listaPeriodo
     * Variable a asignar en listaPeriodo
     */
    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoruta
     * 
     * @return listaCodigoruta
     */


    /**
     * Retorna la lista listaCodFinal
     * 
     * @return listaCodFinal
     */


    public RegistroDataModelImpl getListaCodigoruta() {
        return listaCodigoruta;
    }

    public void setListaCodigoruta(RegistroDataModelImpl listaCodigoruta) {
        this.listaCodigoruta = listaCodigoruta;
    }


    // </SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaCodFinal() {
        return listaCodFinal;
    }

    public void setListaCodFinal(RegistroDataModelImpl listaCodFinal) {
        this.listaCodFinal = listaCodFinal;
    }



}
