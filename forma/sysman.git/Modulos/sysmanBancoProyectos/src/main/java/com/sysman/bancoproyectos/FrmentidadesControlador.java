package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmentidadesControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author lcortes
 * @version 1, 10/09/2015
 * 
 * @version 2, 14/09/2017, <strong>pespitia</strong>
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class FrmentidadesControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    private List<Registro> listaSectorDNP;

    /** Lista que contiene los items del combo Tipo Entidad */
    private List<Registro> listaTipoEntidad;

    /** Lista que contiene los items del combo Orden */
    private List<Registro> listaOrden;

    /**
     * Creates a new instance of FrmentidadesControlador
     */
    public FrmentidadesControlador() {
        super();

        compania = SessionUtil.getCompania();

        cCompania = GeneralParameterEnum.COMPANIA.getName();

        try {
            // 111
            numFormulario = GeneralCodigoFormaEnum.FRMENTIDADES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmentidadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BP_ENTIDADES;
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        reasignarOrigen();

        cargarListaOrden();
        cargarListaSectorDNP();
        cargarListaTipoEntidad();

        abrirFormulario();
    }

    /**
     * Carga la lista: <code>listaOrden</code> asociada al combo Orden
     */
    public void cargarListaOrden() {
        Map<String, Object> param = new TreeMap<>();

        try {
            listaOrden = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmentidadesControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaTipoEntidad</code> asociada al combo
     * Tipo Entidad.
     */
    public void cargarListaTipoEntidad() {
        Map<String, Object> param = new TreeMap<>();

        try {
            listaTipoEntidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmentidadesControladorUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public List<Registro> getListaSectorDNP() {
        return listaSectorDNP;
    }

    public void setListaSectorDNP(List<Registro> listaSectorDNP) {
        this.listaSectorDNP = listaSectorDNP;
    }

    public void cargarListaSectorDNP() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaSectorDNP = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmentidadesControladorUrlEnum.URL2139
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el valor del control SectorDNP.
     */
    public void cambiarSectorDNP() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el valor del control SectorDNP en
     * la fila seleccionada dentro de la grilla.
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarSectorDNPC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);

        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("ORDENLB");
        registro.getCampos().remove("TIPOENTIDADLB");
        registro.getCampos().remove("NOMBSECTORDNP");

        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cCompania);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListaOrden() {
        return listaOrden;
    }

    public void setListaOrden(List<Registro> listaOrden) {
        this.listaOrden = listaOrden;
    }

    public List<Registro> getListaTipoEntidad() {
        return listaTipoEntidad;
    }

    public void setListaTipoEntidad(List<Registro> listaTipoEntidad) {
        this.listaTipoEntidad = listaTipoEntidad;
    }
}
