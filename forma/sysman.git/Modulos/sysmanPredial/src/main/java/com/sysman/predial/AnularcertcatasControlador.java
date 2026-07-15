package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialSieteRemote;
import com.sysman.predial.enums.AnularcertcatasControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author NGOMEZ
 * @version 1, 20/05/2016
 *
 * @author JGuerrero
 * @version 2.0, 23/06/2017 Refactoring a la clase y creacion de la
 * funcion en PL/SQL FC_ANULARCERT_CATASTRAL.
 * 
 * @version 3, 11/10/2017, <strong>pespitia</strong>: Validar nulos al
 * seleccionar items en los combos.
 */
@ManagedBean
@ViewScoped
public class AnularcertcatasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String nOrden;
    // <DECLARAR_ATRIBUTOS>
    private String codigo;
    private String certificado;
    private String textoEtiqueta;
    private boolean dialogoVisible;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoCatastral;
    private RegistroDataModelImpl listaCodigoCertificado;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbPredialSieteRemote ejbPredialSiete;

    /**
     * Creates a new instance of AnularcertcatasControlador
     */
    public AnularcertcatasControlador() {
        super();
        compania = SessionUtil.getCompania();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        try {
            numFormulario = GeneralCodigoFormaEnum.ANULARCERTCATAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(AnularcertcatasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        cargarListaCodigoCatastral();
        // </CARGAR_LISTA_COMBO_GRANDE>
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
     * Carga la lista listaCodigoCatastral.
     */
    public void cargarListaCodigoCatastral() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnularcertcatasControladorUrlEnum.URL3962
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);

        listaCodigoCatastral = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    /**
     *
     * Carga la lista listaCodigoCertificado.
     */
    public void cargarListaCodigoCertificado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnularcertcatasControladorUrlEnum.URL4786
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(), codigo);

        listaCodigoCertificado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMCER");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Anular en la vista.
     *
     */
    public void oprimirAnular() {
        // <CODIGO_DESARROLLADO>
        textoEtiqueta = idioma.getString("TB_TB1066");
        textoEtiqueta = textoEtiqueta.replace("s$codCert$s", certificado);
        textoEtiqueta = textoEtiqueta.replace("s$codPredio$s", codigo);
        dialogoVisible = true;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * DialogoRp en la vista.
     *
     */
    public void aceptarDialogoRp() {
        String msj;

        try {
            ejbPredialSiete.anularCertificadoCatastral(compania, certificado,
                            SessionUtil.getUser().getCodigo());

            msj = idioma.getString("TB_TB1067");
            msj = msj.replace("s$certificado$s", certificado);
            JsfUtil.agregarMensajeInformativo(msj);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        dialogoVisible = false;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * DialogoRp en la vista.
     *
     */
    public void cancelarDialogoRp() {
        dialogoVisible = false;
    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoCatastral.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoCatastral(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigo = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        certificado = null;

        cargarListaCodigoCertificado();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoCertificado.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoCertificado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        certificado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMCER"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigo
     *
     * @return codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Asigna la variable codigo
     *
     * @param codigo
     * Variable a asignar en codigo
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Retorna la variable certificado
     *
     * @return certificado
     */
    public String getCertificado() {
        return certificado;
    }

    /**
     * Asigna la variable certificado
     *
     * @param certificado
     * Variable a asignar en certificado
     */
    public void setCertificado(String certificado) {
        this.certificado = certificado;
    }

    public String getTextoEtiqueta() {
        return textoEtiqueta;
    }

    public void setTextoEtiqueta(String textoEtiqueta) {
        this.textoEtiqueta = textoEtiqueta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isDialogoVisible() {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible) {
        this.dialogoVisible = dialogoVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoCatastral
     *
     * @return listaCodigoCatastral
     */
    public RegistroDataModelImpl getListaCodigoCatastral() {
        return listaCodigoCatastral;
    }

    /**
     * Asigna la lista listaCodigoCatastral
     *
     * @param listaCodigoCatastral
     * Variable a asignar en listaCodigoCatastral
     */
    public void setListaCodigoCatastral(RegistroDataModelImpl listacodcatas) {
        this.listaCodigoCatastral = listacodcatas;
    }

    /**
     * Retorna la lista listaCodigoCertificado
     *
     * @return listaCodigoCertificado
     */
    public RegistroDataModelImpl getListaCodigoCertificado() {
        return listaCodigoCertificado;
    }

    /**
     * Asigna la lista listaCodigoCertificado
     *
     * @param listaCodigoCertificado
     * Variable a asignar en listaCodigoCertificado
     */
    public void setListaCodigoCertificado(RegistroDataModelImpl listacodcert) {
        this.listaCodigoCertificado = listacodcert;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
