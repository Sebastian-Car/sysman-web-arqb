/*-
 * InformeDianControlador.java
 *
 * 1.0
 * 
 * 16/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.InformeDianControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;

/**
 *
 * @version 1.0, 16/02/2017
 * @author spina
 * 
 * @modifier amonroy
 * @version 2, 06/07/2017 Se realiza el Proceso de Refactoring
 */
@ManagedBean
@ViewScoped
public class InformeDianControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    /**
     * codigo de la compania
     */
    private final String compania;

    /**
     * anio gravable
     */
    private int anio;

    /**
     * del intervalo, el valor minimo
     */
    private String valor;
    /**
     * del intervalo, el valor maximo
     */
    private String valormax;

    /**
     * check para mostrar o no a los propietarios del predio en el
     * reporte
     */
    private boolean propietarios;

    /**
     * Atributo usado para descargar contenidos de archivos
     */
    private StreamedContent archivoDescarga;
    /**
     * listado de anios para el combo simple
     */
    private List<Registro> listaanio;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformeDianControlador
     */
    public InformeDianControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORME_DIAN_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            anio = SysmanFunciones.ano(new Date());
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        cargarListaanio();
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
        valor = "0.0";
        valormax = "0.0";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public void cargarListaanio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeDianControladorUrlEnum.URL4352
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
     * Metodo ejecutado al oprimir el boton cmdExcel en la vista
     *
     *
     */
    public void oprimircmdExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    private void generarExcel() {
        try {
            if (Integer.parseInt(valor) > Integer.parseInt(valormax)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2899"));
                return;
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            HashMap<String, Object> reemplazar2 = new HashMap<>();
            String strSql;
            String strSql2;

            reemplazar.put("anio", anio);
            reemplazar.put("compania", compania);

            if (!propietarios) {
                reemplazar2.put("condicion", SysmanFunciones.concatenar(
                                " AND IP_FACTURADOS.AVALUO <= ", valormax,
                                " AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = '",
                                SysmanConstantes.NUMERO_ORDEN_PREDIAL, "'"));
            }
            else {
                reemplazar2.put("condicion",
                                " AND IP_FACTURADOS.AVALUO <= " + valormax);
            }

            reemplazar2.put("anio", anio);
            reemplazar2.put("valor", valor);

            strSql = Reporteador.resuelveConsulta("001304InformeDian_hoja1",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            strSql2 = Reporteador.resuelveConsulta("001304InformeDian_hoja2",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar2);

            String[] consultas = { strSql, strSql2 };

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(consultas,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
                            "Hoja1", "Hoja2");
        }
        catch (IOException | DRException | SysmanException
                        | OutOfMemoryError e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getValormax() {
        return valormax;
    }

    public void setValormax(String valormax) {
        this.valormax = valormax;
    }

    public boolean isPropietarios() {
        return propietarios;
    }

    public void setPropietarios(boolean propietarios) {
        this.propietarios = propietarios;
    }

    public String getCompania() {
        return compania;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public List<Registro> getListaanio() {
        return listaanio;
    }

    public void setListaanio(List<Registro> listaanio) {
        this.listaanio = listaanio;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
