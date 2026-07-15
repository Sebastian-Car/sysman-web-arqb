package com.sysman.almacen;

import com.sysman.almacen.enums.InventarioMinimaMaximaControladorEnum;
import com.sysman.almacen.enums.InventarioMinimaMaximaControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
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
 *
 * @author OTORRES
 * @version 1, 03/02/2016
 * 
 * @author eamaya
 * @version 2, 02/05/2017 Proceso de Refactoring y Correcciones Sonar
 * Lint
 * 
 */
@ManagedBean
@ViewScoped

public class InventarioMinimaMaximaControlador extends BeanBaseModal {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    private final String reporte;
    // DECLARAR ATRIBUTOS
    /**
     * Atributo que contiene el valor asignado al control de "mostrar"
     * del formulario.
     */
    private String mostrar;
    /**
     * Atributo que contiene el valor asignado al control de "ordenado
     * por" del formulario.
     */
    private String ordenar;
    private boolean existencias;
    private String elementoDesde;
    private String elementoHasta;
    private String nombreDesde;
    private String nombreHasta;
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
    private RegistroDataModelImpl listaCmbElementoDesde;
    private RegistroDataModelImpl listaCmbElementoHasta;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of InventarioMinimaMaximaControlador
     */
    public InventarioMinimaMaximaControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.INVENTARIO_MINIMA_MAXIMA_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        mostrar = "1";
        ordenar = "1";
        reporte = "000504InventarioMinimaMaxima";
        try {
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(InventarioMinimaMaximaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListacmbElementoDesde();
        abrirFormulario();
    }

    public void cargarListacmbElementoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InventarioMinimaMaximaControladorUrlEnum.URL3733
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListacmbElementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InventarioMinimaMaximaControladorUrlEnum.URL4513
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(InventarioMinimaMaximaControladorEnum.PARAM0.getValue(),
                        elementoDesde);

        listaCmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        try {
            String existencia = " ";
            String condicionMostrar = "";
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String strSQL;
            reemplazar.put("elementoDesde", elementoDesde);
            reemplazar.put("elementoHasta", elementoHasta);
            reemplazar.put("compania", compania);
            if (("2").equals(mostrar)) {
                condicionMostrar = "  AND TIENEMOVIMIENTO NOT IN(0) ";
            }
            reemplazar.put("condicionMostrar", condicionMostrar);
            if (existencias) {
                existencia = "  AND EXISTENCIA < NVL(CANTIDADMINIMA,0) AND EXISTENCIA NOT IN (0)";
            }
            reemplazar.put("condicionExistencia", existencia);
            if (("1").equals(ordenar)) {
                reemplazar.put("orden", "ORDER BY NOMBRELARGO");
            }
            else if (("2").equals(ordenar)) {
                reemplazar.put("orden", "ORDER BY CODIGOELEMENTO");
            }
            strSQL = Reporteador.resuelveConsulta(
                            reporte,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strSQL);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaCmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();

        elementoHasta = null;
        nombreHasta = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilaCmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable mostrar
     * 
     * @return mostrar
     */
    public String getMostrar() {
        return mostrar;
    }

    /**
     * Asigna la variable mostrar
     * 
     * @param mostrar
     * Variable a asignar en mostrar
     */
    public void setMostrar(String mostrar) {
        this.mostrar = mostrar;
    }

    /**
     * Retorna la variable ordenar
     * 
     * @return ordenar
     */
    public String getOrdenar() {
        return ordenar;
    }

    /**
     * Asigna la variable ordenar
     * 
     * @param ordenar
     * Variable a asignar en ordenar
     */
    public void setOrdenar(String ordenar) {
        this.ordenar = ordenar;
    }

    /**
     * Retorna la variable existencias
     * 
     * @return existencias
     */
    public boolean getExistencias() {
        return existencias;
    }

    /**
     * Asigna la variable existencias
     * 
     * @param existencias
     * Variable a asignar en existencias
     */
    public void setExistencias(boolean existencias) {
        this.existencias = existencias;
    }

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
     * Retorna la variable nombreDesde
     * 
     * @return nombreDesde
     */
    public String getNombreDesde() {
        return nombreDesde;
    }

    /**
     * Asigna la variable nombreDesde
     * 
     * @param nombreDesde
     * Variable a asignar en nombreDesde
     */
    public void setNombreDesde(String nombreDesde) {
        this.nombreDesde = nombreDesde;
    }

    /**
     * Retorna la variable nombreHasta
     * 
     * @return nombreHasta
     */
    public String getNombreHasta() {
        return nombreHasta;
    }

    /**
     * Asigna la variable nombreHasta
     * 
     * @param nombreHasta
     * Variable a asignar en nombreHasta
     */
    public void setNombreHasta(String nombreHasta) {
        this.nombreHasta = nombreHasta;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
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
     * Retorna la lista listacmbElementoDesde
     * 
     * @return listacmbElementoDesde
     */
    public RegistroDataModelImpl getListaCmbElementoDesde() {
        return listaCmbElementoDesde;
    }

    /**
     * Asigna la lista listacmbElementoDesde
     * 
     * @param listacmbElementoDesde
     * Variable a asignar en listacmbElementoDesde
     */
    public void setListaCmbElementoDesde(
        RegistroDataModelImpl listaCmbElementoDesde) {
        this.listaCmbElementoDesde = listaCmbElementoDesde;
    }

    /**
     * Retorna la lista listacmbElementoHasta
     * 
     * @return listacmbElementoHasta
     */
    public RegistroDataModelImpl getListaCmbElementoHasta() {
        return listaCmbElementoHasta;
    }

    /**
     * Asigna la lista listacmbElementoHasta
     * 
     * @param listacmbElementoHasta
     * Variable a asignar en listacmbElementoHasta
     */
    public void setListaCmbElementoHasta(
        RegistroDataModelImpl listaCmbElementoHasta) {
        this.listaCmbElementoHasta = listaCmbElementoHasta;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
