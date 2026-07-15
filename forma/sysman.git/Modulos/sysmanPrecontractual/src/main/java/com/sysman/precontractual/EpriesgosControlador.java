package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.EpriesgosControladorEnum;
import com.sysman.precontractual.enums.EpriesgosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author acaceres
 * @version 1, 24/11/2015
 * 
 * @version 2, 23/08/2017, <strong>pespitia</strong>:<br>
 * Reemplazo del numero del formulario por enumerado.<br>
 * Refactoring de sentencias SQL.<br>
 * Se aplicaron las recomendaciones de SonarLint.
 * 
 * * @version 3, 30/07/2019, <strong>mvenegas</strong>: Se cambio el
 * formulario de continuo a datos y se agrega la funcionalidad 6
 * nuevos botones.
 */
@ManagedBean
@ViewScoped
public class EpriesgosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COD_T_RIESGO</code>
     */
    private final String cCodTRiesgo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TRIESGO</code>
     */
    private final String cTRiesgo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COD_RIESGO</code>
     */
    private final String cCodRiesgo;
    /**
     * combo que almacena el tipo de riesgo
     */
    private List<Registro> listacmbTipoRiesgo;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of EpriesgosControlador
     */
    public EpriesgosControlador() {
        super();

        compania = SessionUtil.getCompania();
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cTRiesgo = EpriesgosControladorEnum.TRIESGO.getValue();
        cCodTRiesgo = EpriesgosControladorEnum.COD_T_RIESGO.getValue();
        cCodRiesgo = EpriesgosControladorEnum.COD_RIESGO.getValue();

        try {
            // 373
            numFormulario = GeneralCodigoFormaEnum.EPRIESGOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListacmbTipoRiesgo();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.ES_RIESGO;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
    }

    @Override
    public void cargarRegistro() {
        // Auto-generated method stub

    }

    public void cargarListacmbTipoRiesgo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listacmbTipoRiesgo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EpriesgosControladorUrlEnum.URL2230
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al cambiar el control cmbTipoRiesgo
     * 
     * 
     */
    public void cambiarcmbTipoRiesgo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);
        registro.getCampos().put(cCodRiesgo, generarConsecutivo());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos().remove(cCompania);
            registro.getCampos().remove(cCodTRiesgo);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cTRiesgo, registro.getCampos().get(cCodTRiesgo).toString());

        param.put(EpriesgosControladorEnum.RIESGO.getValue(),
                        registro.getCampos().get(cCodRiesgo).toString());

        Registro registroConsulta = null;

        try {
            registroConsulta = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EpriesgosControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        /* Si el riesgo tiene proyectos asociados */
        if (registroConsulta != null && !"0".equals(
                        registroConsulta.getCampos().get("CANT").toString())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2199"));

            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public String generarConsecutivo() {

        String consecutivoSalida = "0";
        try {
            long consecutivoGenerado = ejbSysmanUtil
                            .generarSiguienteConsecutivo(
                                            "ES_RIESGO",
                                            SysmanFunciones.concatenar("COMPANIA = ''", compania, "'' AND COD_T_RIESGO =",
                                                            registro.getCampos().get(cCodTRiesgo).toString()),
                                            cCodRiesgo);

            consecutivoSalida = String.valueOf(consecutivoGenerado);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivoSalida;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public List<Registro> getListacmbTipoRiesgo() {
        return listacmbTipoRiesgo;
    }

    public void setListacmbTipoRiesgo(List<Registro> listacmbTipoRiesgo) {
        this.listacmbTipoRiesgo = listacmbTipoRiesgo;
    }

}
