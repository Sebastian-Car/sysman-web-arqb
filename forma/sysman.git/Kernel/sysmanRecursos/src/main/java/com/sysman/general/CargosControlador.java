package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.CargosControladorEnum;
import com.sysman.general.enums.CargosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author cmanrique
 *
 * @modifier lcortes
 * @version 2, 15/03/2017 16:20 Ajustes de buenas practicas SonarLint.
 * 
 * @modifier amonroy
 * @version 3, 22/08/2017 Se realiza el Proceso de Refactoring en las operaciones CRUD del formulario y el listado del combo escalafon
 * 
 * @modifier jcrodriguez
 * @version 4, 03/01/2017 actualizacion de controlador y forma se agrega el campo factor de riesgo campo y combo se cambia el controlador de modulo de nomina a generales
 */
@ManagedBean
@ViewScoped
public class CargosControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Listado para el combo de escalafon
     */
    private List<Registro> listaEscalafon;
    /**
     * Lista de registros de la tabla CRG_TIPO_VINCULACION
     */
    private List<Registro> listaTipoVinculacionCGR;
    /**
     * listado de riesgos
     */
    private RegistroDataModelImpl listafactorRiesgo;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;

    /**
     * Crea una nueva instancia de CargosControlador
     */
    public CargosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.CARGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registro = new Registro(new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(CargosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * 
     * Carga la lista listafactorRiesgo
     * 
     */
    public void cargarListafactorRiesgo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CargosControladorUrlEnum.URL002
                                                        .getValue());

        listafactorRiesgo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        CargosControladorEnum.ID_RIESGO
                                        .getValue());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listafactorRiesgo *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafactorRiesgo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CargosControladorEnum.FACTOR_RIESGO.getValue(),
                        registroAux.getCampos()
                                        .get(CargosControladorEnum.ID_RIESGO
                                                        .getValue()));
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.CARGOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        if ("60109".equals(SessionUtil.getMenuActual())) {

            urlCreacion = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CargosControladorUrlEnum.URL143
                                                            .getValue());
        }

    }

    @Override
    public void iniciarListas()
    {
        cargarListaEscalafon();
        cargarListafactorRiesgo();
        cargarListaTipoVinculacionCGR();
    }

    public void cargarListaEscalafon()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaEscalafon = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CargosControladorUrlEnum.URL2288
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
     * Carga la lista listaTipoVinculacionCGR
     *
     */
    public void cargarListaTipoVinculacionCGR()
    {

        Map<String, Object> param = new TreeMap<>();

        try {
            listaTipoVinculacionCGR = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CargosControladorUrlEnum.URL185
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(CargosControladorEnum.VACANTES.getValue());
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        if (css != null) {
            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
        }
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListaEscalafon()
    {
        return listaEscalafon;
    }

    public void setListaEscalafon(List<Registro> listaEscalafon)
    {
        this.listaEscalafon = listaEscalafon;
    }

    /**
     * Retorna la lista listaTipoVinculacionCGR
     * 
     * @return listaTipoVinculacionCGR
     */
    public List<Registro> getListaTipoVinculacionCGR()
    {
        return listaTipoVinculacionCGR;
    }

    /**
     * Asigna la lista listaTipoVinculacionCGR
     * 
     * @param listaTipoVinculacionCGR
     * Variable a asignar en listaTipoVinculacionCGR
     */
    public void setListaTipoVinculacionCGR(List<Registro> listaTipoVinculacionCGR)
    {
        this.listaTipoVinculacionCGR = listaTipoVinculacionCGR;
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSub()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListafactorRiesgo()
    {
        return listafactorRiesgo;
    }

    public void setListafactorRiesgo(RegistroDataModelImpl listafactorRiesgo)
    {
        this.listafactorRiesgo = listafactorRiesgo;
    }

    public String getModulo()
    {
        return modulo;
    }

}
