/*-
 * SeleccionRubros.java
 *
 * 1.0
 * 
 * 31/10/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseContinuoNAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.SeleccionRubrosUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 31/10/2017
 * @author cmanrique Formulario de seleccion de rubros presupuestales
 * desde el plan presupuestal, se invoca desde todos los botones del
 * panel superioor de dicho formulario.
 * 
 * Busca listar ordenadamente los rubros presupuestales segun los
 * indicadores que maneje la cuenta base y permitir todos los filtros
 * por auxiliares al usuario
 */
@ManagedBean
@ViewScoped
public class SeleccionRubrosControlador extends BeanBaseContinuoNAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String strCodigo;
    private final String strNaturaleza;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    private String titulo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>

    /**
     * Objeto direccionar obtenido por parametro el cual contiene la
     * inforacion del formulario al cual debe refireccionar al oprimir
     * el boton ver rubro
     */
    private Direccionador direccionador;

    /**
     * Identificador del rubro presupuestal optenido del formulario de
     * plan presupuestal
     */
    private Map<String, Object> rid;

    /**
     * Anio del rubro presupuestal que ingresa por parametro
     */
    private String anio;

    /**
     * Anio del rubro presupuestal que ingresa por parametro
     */
    private String codigo;

    /**
     * Anio del rubro presupuestal que ingresa por parametro
     */
    private String nombre;

    /**
     * Naturaleza del rubro presupuestal que ingresa por parametro
     */
    private String naturaleza;

    /**
     * Codigo del formulario al que se debe redireccionar
     */
    private String formulario;

    /**
     * Codigo del reporte que debe descargar en caso de que la
     * funcionalidad sea esta
     */
    private String reporte;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SeleccionRubros
     */
    public SeleccionRubrosControlador() {
        super();
        compania = SessionUtil.getCompania();
        strCodigo = "codigo";
        strNaturaleza = "naturaleza";

        try {
            numFormulario = GeneralCodigoFormaEnum.SELECCION_RUBROS.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                direccionador = (Direccionador) parametrosEntrada
                                .get("direccionador");
                rid = (Map<String, Object>) direccionador.getParametros()
                                .get("rid");
                anio = SysmanFunciones.nvl(
                                direccionador.getParametros().get("anio"), "")
                                .toString();
                codigo = SysmanFunciones
                                .nvl(direccionador.getParametros()
                                                .get(strCodigo), "")
                                .toString();
                nombre = SysmanFunciones
                                .nvl(direccionador.getParametros()
                                                .get("nombre"), "")
                                .toString();
                naturaleza = SysmanFunciones
                                .nvl(direccionador.getParametros()
                                                .get(strNaturaleza), "")
                                .toString();
                formulario = SysmanFunciones
                                .nvl(direccionador.getNumForm(), "")
                                .toString();
                reporte = SysmanFunciones
                                .nvl(direccionador.getParametros()
                                                .get("reporte"), "")
                                .toString();
            }
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

        reasignarOrigen();
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
    public void reasignarOrigen() {
        if (Integer.toString(GeneralCodigoFormaEnum.PROGRAMARPACS_CONTROLADOR
                        .getCodigo()).equals(formulario)) {
            // not in 0
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SeleccionRubrosUrlEnum.URL94119
                                                            .getValue());
        }
        else {
            // sin el not in 0
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SeleccionRubrosUrlEnum.URL94117
                                                            .getValue());
        }
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton VerRubro
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirVerRubro(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String id = reg.getCampos().get("ID").toString();
        if (!reporte.isEmpty()) {
            try {

                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put(strCodigo, id);
                reemplazar.put("anio", anio);
                reemplazar.put(strNaturaleza, naturaleza);
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("PR_TITULO", id + " " + nombre);
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {
            direccionador.getParametros().put(strCodigo, id);
            RequestContext.getCurrentInstance().closeDialog(direccionador);
        }
        // </CODIGO_DESARROLLADO>
    }

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
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */

    // <SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    public String getFormulario() {
        return formulario;
    }

    public void setFormulario(String formulario) {
        this.formulario = formulario;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo
     * the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
