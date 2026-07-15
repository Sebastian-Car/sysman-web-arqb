/*-
 * ImpuestoCreeControlador.java
 *
 * 1.0
 * 
 * 09/11/2016
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.ejb.impl.EjbServiciosPublicosCero;
import com.sysman.serviciospublicos.enums.ImpuestoCreeControladorEnum;
import com.sysman.serviciospublicos.enums.ImpuestoCreeControladorUrlEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario que permite generar
 * el listado de los usuarios con Autoretenciones CREE, en Access
 * "FrmImpuestoCREE", el cual es llamado desde
 * Facturacion\Informes\Recaudos\Listado Autoretenciones CREE
 * 
 * @version 1.0, 09/11/2016
 * @author amonroy
 * @version 2.0, 02/06/2017
 * @author jcrodriguez descripcion:-depuracion del controlador
 * -Refactoring y creacion de dss
 */
@ManagedBean
@ViewScoped
public class ImpuestoCreeControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo del modulo de servicios
     * publicos
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del ciclo seleccionado en el
     * formulario
     */
    private String ciclo;
    /**
     * Atributo que almacena el anio relacionado con el ciclo
     * seleccionado
     */
    private String anio;
    /**
     * Atributo que almacena el periodo relacionado con el ciclo
     * seleccionado
     */
    private String periodo;
    /**
     * Atributo que almacena la seleccion del usuario en el listado
     * mostrar, dependiendo su valor se agrega un filtro a la consulta
     * que genera el reporte
     */
    private String filtro;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * EJB
     */
    @EJB
    private EjbServiciosPublicosCero ejbServiciosPublicosCero;
    /**
     * Listado de registros para el comboBox de ciclo
     */
    private List<Registro> listaCiclo;

    /**
     * Crea una nueva instancia de ImpuestoCreeControlador
     */
    public ImpuestoCreeControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.IMPUESTO_CREE_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            ciclo = "T";
            filtro = "1";
            anio = "0";
            periodo = "0";
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
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean padre
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImpuestoCreeControladorUrlEnum.URL4948
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Define las acciones a ejecutar cuando se oprime el boton de
     * generar informe en formato EXCEL
     *
     */
    public void oprimirBtnExcel() {
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
    }

    /**
     * Define las acciones a ejecutar cuando se oprime el boton de
     * generar informe en formato PDF
     *
     */
    public void oprimirBtnPdf() {
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Llama el metodo para obtener el anio y periodo correspondientes
     * al ciclo seleccionado
     */
    public void cambiarCiclo() {
        consultarAnioPeriodo();
    }

    /**
     * Realiza la consulta del anio y periodo de acuerdo al ciclo
     * seleccionado en el formulario
     */
    public void consultarAnioPeriodo() {
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        Registro rs;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImpuestoCreeControladorUrlEnum.URL4922
                                                                            .getValue())
                                            .getUrl(), param));
            anio = rs.getCampos().get(GeneralParameterEnum.ANO.getName())
                            .toString();
            periodo = rs.getCampos().get(GeneralParameterEnum.PERIODO.getName())
                            .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(FORMATOS formato) {
        // Definicion de los valores que se enviaran por parametro
        try {
            String condicionCiclo = ("T").equalsIgnoreCase(ciclo)
                ? ""
                : "  AND USUARIO.CICLO   = " + ciclo;
            String agrupamiento = ("2").equalsIgnoreCase(filtro)
                ? "HAVING SUM(FACTURADO.VALOR_FACTURADO + FACTURADO.VALORFINACT + FACTURADO.VALORABONOACT) > 107000"
                : "";
            String parCiclo = ("T").equalsIgnoreCase(ciclo)
                ? "Todos"
                : ciclo;

            String nombrePeriodo = ejbServiciosPublicosCero
                            .asignarNombrePeriodo(compania,
                                            Integer.parseInt(anio),
                                            periodo, "");

            // HashMap reemplazar es para que reemplace en la
            // consulta almacenada en la tabla CONSULTAS
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("condicionCiclo", condicionCiclo);
            reemplazar.put("agrupamiento", agrupamiento);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CICLO", parCiclo);
            parametros.put("PR_NOMBREPERIODO", nombrePeriodo.replace("/0", ""));

            Reporteador.resuelveConsulta(
                            ImpuestoCreeControladorEnum.REPORTE001225
                                            .getValue(),
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            ImpuestoCreeControladorEnum.REPORTE001225
                                            .getValue(),
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1750"));
        }
        catch (JRException | IOException | SysmanException
                        | NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Retorna la variable filtro
     * 
     * @return filtro
     */
    public String getFiltro() {
        return filtro;
    }

    /**
     * Asigna la variable filtro
     * 
     * @param filtro
     * Variable a asignar en filtro
     */
    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getPeriodo() {
        return periodo;
    }

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
}
