/*-
 * FrmDevolutivosenDonacionControlador.java
 *
 * 1.0
 * 
 * 09/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.FrmDevolutivosenDonacionControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 09/08/2018
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmDevolutivosenDonacionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * variable que almacena el elemento inicial
     */
    private String elementoDesde;
    /**
     * variable que almacena el elemento final
     */
    private String elementoHasta;
    /**
     * variable que almacena el nombre del elemento inicial
     */
    private String nombreElementoIni;
    /**
     * variable que almacena el nombre del elemento final
     */
    private String nombreElementoFin;

    /**
     * variable que almacena el parametro de los digitos
     */

    private String digitos;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmDevolutivosenDonacionControlador
     */

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public FrmDevolutivosenDonacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_DEVOLUTIVOS_EN_DONACION_CONTROLADOR
                            .getCodigo();
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

        try {

            digitos = ejbSysmanUtil.consultarParametro(compania,
                            "DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),
                            false);

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarListacmbElementoDesde();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
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
        /*
         * FR1884-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 10, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbElementoDesde
     *
     */
    public void cargarListacmbElementoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmDevolutivosenDonacionControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        // param.put("DIGITOS", digitos);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOELEMENTO");
    }

    /**
     * 
     * Carga la lista listacmbElementoHasta
     */
    public void cargarListacmbElementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmDevolutivosenDonacionControladorUrlEnum.URL0002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ELEMENTO", elementoDesde);
        param.put("DIGITOS", digitos);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOELEMENTO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;

        generarInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdPantalla en la vista
     *
     *
     */
    public void oprimircmdPantalla() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {

        try {
            String reporte = "001859DevolutivosEnDonacion";
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("digitos", digitos);
            reemplazar.put("elementoDesde", elementoDesde);
            reemplazar.put("elementoHasta", elementoHasta);
            reemplazar.put("compania", compania);

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbElementoDesde
     * 
     */
    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOELEMENTO"), "")
                        .toString();

        nombreElementoIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRECORTO"), "")
                        .toString();
        elementoHasta = null;
        nombreElementoFin = null;
        cargarListacmbElementoHasta();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbElementoHasta
     *
     */
    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOELEMENTO"), "")
                        .toString();

        nombreElementoFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRECORTO"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable elementoDesde
     * 
     * @return elementoDesde
     */
    public String getElementoDesde() {
        return elementoDesde;
    }

    /**
     * Asigna la variable elementoDesde
     * 
     * @param elementoDesde
     * Variable a asignar en elementoDesde
     */
    public void setElementoDesde(String elementoDesde) {
        this.elementoDesde = elementoDesde;
    }

    /**
     * Retorna la variable elementoHasta
     * 
     * @return elementoHasta
     */
    public String getElementoHasta() {
        return elementoHasta;
    }

    /**
     * Asigna la variable elementoHasta
     * 
     * @param elementoHasta
     * Variable a asignar en elementoHasta
     */
    public void setElementoHasta(String elementoHasta) {
        this.elementoHasta = elementoHasta;
    }

    /**
     * Retorna la variable nombreElementoIni
     * 
     * @return nombreElementoIni
     */
    public String getNombreElementoIni() {
        return nombreElementoIni;
    }

    /**
     * Asigna la variable nombreElementoIni
     * 
     * @param nombreElementoIni
     * Variable a asignar en nombreElementoIni
     */
    public void setNombreElementoIni(String nombreElementoIni) {
        this.nombreElementoIni = nombreElementoIni;
    }

    /**
     * Retorna la variable nombreElementoFin
     * 
     * @return nombreElementoFin
     */
    public String getNombreElementoFin() {
        return nombreElementoFin;
    }

    /**
     * Asigna la variable nombreElementoFin
     * 
     * @param nombreElementoFin
     * Variable a asignar en nombreElementoFin
     */
    public void setNombreElementoFin(String nombreElementoFin) {
        this.nombreElementoFin = nombreElementoFin;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /*
     * Retorna la lista listacmbElementoDesde
     * 
     * @return listacmbElementoDesde
     */
    public RegistroDataModelImpl getListacmbElementoDesde() {
        return listacmbElementoDesde;
    }

    /**
     * Asigna la lista listacmbElementoDesde
     * 
     * @param listacmbElementoDesde
     * Variable a asignar en listacmbElementoDesde
     */
    public void setListacmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listacmbElementoDesde = listacmbElementoDesde;
    }

    /**
     * Retorna la lista listacmbElementoHasta
     * 
     * @return listacmbElementoHasta
     */
    public RegistroDataModelImpl getListacmbElementoHasta() {
        return listacmbElementoHasta;
    }

    /**
     * Asigna la lista listacmbElementoHasta
     * 
     * @param listacmbElementoHasta
     * Variable a asignar en listacmbElementoHasta
     */
    public void setListacmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listacmbElementoHasta = listacmbElementoHasta;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * @return the digitos
     */
    public String getDigitos() {
        return digitos;
    }

    /**
     * @param digitos
     * the digitos to set
     */
    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
