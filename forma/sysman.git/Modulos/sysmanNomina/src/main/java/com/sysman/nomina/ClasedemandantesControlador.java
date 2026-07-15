package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ClasedemandantesControladorUrlEnum;

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
 * @author ngomez
 * @version 1, 22/07/2015
 * 
 * @version 2, 22/08/2017, <strong>pespitia</strong>:<br>
 * Se reemplazo el numero del formulario por enumerado.<br>
 * Refactoring de sentencias SQL.
 */
@ManagedBean
@ViewScoped
public class ClasedemandantesControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    private HashMap<String, Object> rid;
    private List<Registro> listaIdEmbargo;

    /**
     * Creates a new instance of ClasedemandantesControlador
     */
    @SuppressWarnings("unchecked")
    public ClasedemandantesControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCompania = GeneralParameterEnum.COMPANIA.getName();

        try {
            // 75
            numFormulario = GeneralCodigoFormaEnum.CLASEDEMANDANTES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (HashMap<String, Object>) parametrosEntrada.get("rid");
            }
        }
        catch (Exception ex) {
            Logger.getLogger(ClasedemandantesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CLASE_DEMANDANTE;
        registro = new Registro();

        buscarLlave();
        reasignarOrigen();
        cargarListaIdEmbargo();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);

    }

    public List<Registro> getListaIdEmbargo() {
        return listaIdEmbargo;
    }

    public void setListaIdEmbargo(List<Registro> listaIdEmbargo) {
        this.listaIdEmbargo = listaIdEmbargo;
    }

    public void cargarListaIdEmbargo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaIdEmbargo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasedemandantesControladorUrlEnum.URL3179
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

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(cCompania, compania);

        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove("TIPO_EMBARGO_NOM");

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    public void ejecutarrcCerrar() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", rid);

        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.EMBARGOS_CONTROLADOR
                                        .getCodigo()));

        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cCompania);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
