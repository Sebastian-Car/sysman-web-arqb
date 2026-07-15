/*-
 * ImprimirHvGeneralControlador.java
 *
 * 1.0
 * 
 * 13/12/2017
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
import com.sysman.hojasdevida.enums.ImprimirHvGeneralControladorEnum;
import com.sysman.hojasdevida.enums.ImprimirHvGeneralControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite visualizar e imprimir la hoja de vida
 * general de los empleados
 * 
 * @version 1.0, 13/12/2017
 * @author jromero
 */
@ManagedBean
@ViewScoped
public class ImprimirHvGeneralControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private boolean listadoCondicion;
    /**
     * atributo que almacena el codigo de empleado inicial
     * seleccionado en la vista
     */
    private String empleadoinicial;
    private String empleadofinal;
    private String empleadoIni;
    private String empleadoFin;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private String modulo;
    /**
     * lista que almacena empleado inicial
     */
    private RegistroDataModelImpl listaCarpetaInicial;
    /**
     * lista que almacena empleado final
     */
    private RegistroDataModelImpl listaCarpetaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private StreamedContent archivoDescarga;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    public ImprimirHvGeneralControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.IMPRIMIR_HV_GENERAL_CONTROLADOR
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
        cargarListaCarpetaInicial();
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
         * FR1500-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }
    // <METODOS_CARGAR_LISTA>

    public void cargarListaCarpetaInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimirHvGeneralControladorUrlEnum.URL4674
                                                        .getValue());

        listaCarpetaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO_DCTO");
    }

    public void cargarListaCarpetaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimirHvGeneralControladorUrlEnum.URL5345
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(ImprimirHvGeneralControladorEnum.PARAM0.getValue(),
                        empleadoinicial);

        listaCarpetaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO_DCTO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton CmdPrevia en la vista
     */
    public void oprimirPDF() {

        generarReporte(FORMATOS.PDF);
    }

    /**
     * Metodo ejecutado al oprimir el boton CmdImprimir en la vista
     */
    public void oprimirEXCEL() {

        generarReporte(FORMATOS.EXCEL);
    }

    private void generarReporte(FORMATOS formato) {
        setArchivoDescarga(null);

        if (!validarCamposVacios()) {
            return;
        }

        try {
            Map<String, Object> reemplazos = new TreeMap<>();
            Map<String, Object> parametros = new TreeMap<>();

            String reporte;

            if (listadoCondicion) {
                reporte = "001559hvgen";
            }
            else {
                reporte = "001565HojasDeVidaGeneral";
            }
            reemplazos.put("compania", compania);
            reemplazos.put("empleadoInicial", empleadoinicial);
            reemplazos.put("empleadoFinal", empleadofinal);

            parametros.put("PR_COMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazos, parametros);

            setArchivoDescarga(JsfUtil.exportarStreamed(
                            reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato));
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarCamposVacios() {
        if (SysmanFunciones.validarVariableVacio(compania)) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB165"));
            return false;
        }
        return true;
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCarpetaInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCarpetaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        // Toma el valor NUMERO_DCTO para mostrarlo en el ComboBox por
        // medio de la variable empleadoIni.
        empleadoinicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO_DCTO
                                        .getName()) == null
                                            ? ""
                                            : registroAux.getCampos()
                                                            .get(GeneralParameterEnum.NUMERO_DCTO
                                                                            .getName())
                                                            .toString();

        empleadoIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        cargarListaCarpetaFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCarpetaFinal
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCarpetaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        empleadofinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO_DCTO
                                        .getName()) == null
                                            ? ""
                                            : registroAux.getCampos()
                                                            .get(GeneralParameterEnum.NUMERO_DCTO
                                                                            .getName())
                                                            .toString();

        empleadoFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable empleadoinicial
     * 
     * @return empleadoinicial
     */
    public String getEmpleadoinicial() {
        return empleadoinicial;
    }

    public String getEmpleadoIni() {
        return empleadoIni;
    }

    public void setEmpleadoIni(String empleadoIni) {
        this.empleadoIni = empleadoIni;
    }

    public String getEmpleadoFin() {
        return empleadoFin;
    }

    public void setEmpleadoFin(String empleadoFin) {
        this.empleadoFin = empleadoFin;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getCompania() {
        return compania;
    }

    /**
     * Asigna la variable empleadoinicial
     * 
     * @param empleadoinicial
     * Variable a asignar en empleadoinicial
     */
    public void setEmpleadoinicial(String empleadoinicial) {
        this.empleadoinicial = empleadoinicial;
    }

    /**
     * Retorna la variable empleadofinal
     * 
     * @return empleadofinal
     */
    public String getEmpleadofinal() {
        return empleadofinal;
    }

    /**
     * Asigna la variable empleadofinal
     * 
     * @param empleadofinal
     * Variable a asignar en empleadofinal
     */
    public void setEmpleadofinal(String empleadofinal) {
        this.empleadofinal = empleadofinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCarpetaInicial
     * 
     * @return listaCarpetaInicial
     */
    public RegistroDataModelImpl getListaCarpetaInicial() {
        return listaCarpetaInicial;
    }

    /**
     * Asigna la lista listaCarpetaInicial
     * 
     * @param listaCarpetaInicial
     * Variable a asignar en listaCarpetaInicial
     */
    public void setListaCarpetaInicial(
        RegistroDataModelImpl listaCarpetaInicial) {
        this.listaCarpetaInicial = listaCarpetaInicial;
    }

    /**
     * Retorna la lista listaCarpetaFinal
     * 
     * @return listaCarpetaFinal
     */
    public RegistroDataModelImpl getListaCarpetaFinal() {
        return listaCarpetaFinal;
    }

    /**
     * Asigna la lista listaCarpetaFinal
     * 
     * @param listaCarpetaFinal
     * Variable a asignar en listaCarpetaFinal
     */
    public void setListaCarpetaFinal(RegistroDataModelImpl listaCarpetaFinal) {
        this.listaCarpetaFinal = listaCarpetaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean getListadoCondicion() {
        return listadoCondicion;
    }

    public void setListadoCondicion(boolean listadoCondicion) {
        this.listadoCondicion = listadoCondicion;
    }
}
