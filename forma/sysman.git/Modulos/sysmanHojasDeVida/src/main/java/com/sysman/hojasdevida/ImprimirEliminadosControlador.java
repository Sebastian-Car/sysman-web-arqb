package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.ImprimirEliminadosControladorEnum;
import com.sysman.hojasdevida.enums.ImprimirEliminadosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

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
 * Formulario que permite visualizar e imprimir empleados eliminados
 *
 * @version 1.0, 27/12/2017
 * @author jromero
 */
@ManagedBean
@ViewScoped

public class ImprimirEliminadosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que almacena descripcion de la vista
     */
    private String descripcionPrueba;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena numero de convocatoria de la vista
     */
    private String convocatoria;
    /**
     * Atributo que almacena prueba de la vista
     */
    private String prueba;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Constante a nivel de clase que almacena el numero de modulo al
     * cual accedio el usuario
     */
    private String modulo;
    private RegistroDataModelImpl listaCmbConvocatoria;
    private RegistroDataModelImpl listaCmbPrueba;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private StreamedContent archivoDescarga;

    /**
     * Crea una nueva instancia de ImprimirEliminadosControlador
     */

    public ImprimirEliminadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.IMPRIMIR_ELIMINADOS_CONTROLADOR
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
        cargarListaCmbConvocatoria();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
         * FR1550-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCmbConvocatoria
     *
     */
    public void cargarListaCmbConvocatoria() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimirEliminadosControladorUrlEnum.URL3853
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbConvocatoria = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NRO_CONVOCATORIA");
    }

    /**
     * 
     * Carga la lista listaCmbPrueba
     *
     */
    public void cargarListaCmbPrueba() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimirEliminadosControladorUrlEnum.URL4582
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(ImprimirEliminadosControladorEnum.PARAM1.getValue(),
                        convocatoria);

        listaCmbPrueba = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "PRUEBA");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
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
        generarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(FORMATOS formato) {
        try {

            Map<String, Object> reemplazos = new TreeMap<>();
            Map<String, Object> parametros = new TreeMap<>();

            reemplazos.put("compania", compania);
            reemplazos.put("convocatoria", convocatoria);
            reemplazos.put("prueba", prueba);

            parametros.put("PR_COMPANIA.NOMBRE",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_FORMS_IMPRIMIRELIMINADOS_CMBPRUEBA_COLUMN(1)",
                            descripcionPrueba.toUpperCase());

            Reporteador.resuelveConsulta("001609ListaEliminados",
                            Integer.parseInt(modulo), reemplazos, parametros);

            archivoDescarga = JsfUtil.exportarStreamed("001609ListaEliminados",
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbConvocatoria
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbConvocatoria(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        convocatoria = registroAux.getCampos().get("NRO_CONVOCATORIA")
                        .toString();
        prueba = null;
        cargarListaCmbPrueba();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbPrueba
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbPrueba(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        prueba = registroAux.getCampos().get("PRUEBA").toString();
        descripcionPrueba = registroAux.getCampos().get("DESCRIPCION")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable convocatoria
     * 
     * @return convocatoria
     */
    public String getConvocatoria() {
        return convocatoria;
    }

    /**
     * Asigna la variable convocatoria
     * 
     * @param convocatoria
     * Variable a asignar en convocatoria
     */
    public void setConvocatoria(String convocatoria) {
        this.convocatoria = convocatoria;
    }

    /**
     * Retorna la variable prueba
     * 
     * @return prueba
     */
    public String getPrueba() {
        return prueba;
    }

    /**
     * Asigna la variable prueba
     * 
     * @param prueba
     * Variable a asignar en prueba
     */
    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCmbConvocatoria
     * 
     * @return listaCmbConvocatoria
     */
    public RegistroDataModelImpl getListaCmbConvocatoria() {
        return listaCmbConvocatoria;
    }

    /**
     * Asigna la lista listaCmbConvocatoria
     * 
     * @param listaCmbConvocatoria
     * Variable a asignar en listaCmbConvocatoria
     */
    public void setListaCmbConvocatoria(
        RegistroDataModelImpl listaCmbConvocatoria) {
        this.listaCmbConvocatoria = listaCmbConvocatoria;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCmbPrueba
     * 
     * @return listaCmbPrueba
     */
    public RegistroDataModelImpl getListaCmbPrueba() {
        return listaCmbPrueba;
    }

    /**
     * Asigna la lista listaCmbPrueba
     * 
     * @param listaCmbPrueba
     * Variable a asignar en listaCmbPrueba
     */
    public void setListaCmbPrueba(RegistroDataModelImpl listaCmbPrueba) {
        this.listaCmbPrueba = listaCmbPrueba;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public String getCompania() {
        return compania;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getDescripcionPrueba() {
        return descripcionPrueba;
    }

    public void setDescripcionPrueba(String descripcionPrueba) {
        this.descripcionPrueba = descripcionPrueba;
    }
}
