/*-
 * FrmUsuarioTerceControlador.java
 *
 * 1.0
 * 
 * 28/07/2017
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.FrmUsuarioTerceControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que imprime los reportes de los suscriptores de aseo
 * conjunto a partir de distintos indicadores.
 *
 * @version 1.0, 28/07/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class FrmUsuarioTerceControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    /**
     * Constante a nivel de clase que almacena el valor de la compania
     * de inicio de sesion
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el numero de modulo de
     * inicio de sesion
     */
    private final String modulo;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el valor de la opcion seleccionada en la
     * vista
     */
    private String opcion;
    /**
     * Variable que almacena el numero del ciclo seleccionado
     */
    private String ciclo;
    /**
     * Variable que almacena el codigo de la empresa seleccionada en
     * el combo
     */
    private String empresa;
    /**
     * Variable que almacena el anio del ciclo seleccionado
     */
    private String anio;
    /**
     * Variable que almacena el periodo del ciclo seleccionado
     */
    private String periodo;

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
     */
    private List<Registro> listaEmpresa;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     */
    private RegistroDataModelImpl listaCiclo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmUsuarioTerceControlador
     */
    public FrmUsuarioTerceControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMUSUARIOTERCERO_CONTROLADOR
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
        cargarListaEmpresa();
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
        // METODO_NO_IMLEMENTADO
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmUsuarioTerceControladorUrlEnum.URL4835
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    /**
     * 
     * Carga la lista listaEmpresa
     *
     */
    public void cargarListaEmpresa() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaEmpresa = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmUsuarioTerceControladorUrlEnum.URL5681
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(opcion)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2314"));
            return;
        }
        genererarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(opcion)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2314"));
            return;
        }
        genererarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void genererarReporte(FORMATOS pdf) {

        String reporte = seleccionarReporte();

        String condicionEmpresa;
        if ("T".equals(empresa)) {
            condicionEmpresa = "AND SP_USUARIO.EMPRESAASEOEXT IS NOT NULL ";
        }
        else {
            condicionEmpresa = "AND SP_USUARIO.EMPRESAASEOEXT = " + empresa
                + " ";
        }

        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        reemplazos.put("compania", compania);
        reemplazos.put("ciclo", ciclo);
        reemplazos.put("anio", anio);
        reemplazos.put("periodo", periodo);
        reemplazos.put("condicionEmpresa", condicionEmpresa);

        parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        parametros.put("PR_CICLO", ciclo);

        Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                        reemplazos, parametros);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, pdf);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private String seleccionarReporte() {
        String reporte = null;

        switch (opcion) {
        case "1":
            reporte = "001458INFUSUARIOTERCSINNOV";
            break;
        case "2":
            reporte = "001462INFUSUARIOTERCESINFAC";
            break;
        case "3":
            reporte = "001461INFUSUARIOTERCEFAC";
            break;
        case "4":
            reporte = "001460INFUSUARIOTERCESINAA";
            break;
        default:
            break;
        }
        return reporte;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = SysmanFunciones.nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();
        anio = SysmanFunciones.nvl(registroAux.getCampos().get("ANO"), "")
                        .toString();
        periodo = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PERIODO"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable opcion
     * 
     * @return opcion
     */
    public String getOpcion() {
        return opcion;
    }

    /**
     * Asigna la variable opcion
     * 
     * @param opcion
     * Variable a asignar en opcion
     */
    public void setOpcion(String opcion) {
        this.opcion = opcion;
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
     * Retorna la variable empresa
     * 
     * @return empresa
     */
    public String getEmpresa() {
        return empresa;
    }

    /**
     * Asigna la variable empresa
     * 
     * @param empresa
     * Variable a asignar en empresa
     */
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaEmpresa
     * 
     * @return listaEmpresa
     */
    public List<Registro> getListaEmpresa() {
        return listaEmpresa;
    }

    /**
     * Asigna la lista listaEmpresa
     * 
     * @param listaEmpresa
     * Variable a asignar en listaEmpresa
     */
    public void setListaEmpresa(List<Registro> listaEmpresa) {
        this.listaEmpresa = listaEmpresa;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
