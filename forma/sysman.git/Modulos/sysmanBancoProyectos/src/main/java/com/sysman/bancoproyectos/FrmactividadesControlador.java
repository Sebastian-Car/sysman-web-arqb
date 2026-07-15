package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmactividadesControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author esarmiento
 * @version 1, 21/08/2015
 * 
 * @version 2, 13/09/2017, <strong>pespitia</strong>
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class FrmactividadesControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>UNIDAD</code>
     */
    private final String cUnidad;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    private RegistroDataModelImpl listaUnidadE;
    private RegistroDataModelImpl listaUnidad;
    private String auxiliar;

    /**
     * Variable que recibe los parametros enviados desde el formulario
     * desde el cual se abre este formulario
     */
    private Map<String, Object> parametrosEntrada;

    /**
     * Variable que controla si el formulario permite crear, editar y
     * eliminar registros.
     */
    private boolean permiteCrud;

    /**
     * Creates a new instance of FrmactividadesControlador
     */
    public FrmactividadesControlador() {
        super();

        compania = SessionUtil.getCompania();

        cUnidad = GeneralParameterEnum.UNIDAD.getName();
        cCompania = GeneralParameterEnum.COMPANIA.getName();

        permiteCrud = true;

        try {
            // 130
            numFormulario = GeneralCodigoFormaEnum.FRMACTIVIDADES_CONTROLADOR
                            .getCodigo();

            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null && (!SessionUtil.getMenuActual().equals("520117"))) {
                permiteCrud = ACCION_VER.equals(
                                parametrosEntrada.get("accion").toString())
                                    ? false
                                    : true;
            }
            else {
                permiteCrud = true;
            }

            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(FrmactividadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BP_ACTIVIDADES;
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        reasignarOrigen();
        cargarListaUnidad();
        cargarListaUnidadE();
        abrirFormulario();
    }

    public void cargarListaUnidad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmactividadesControladorUrlEnum.URL2189
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaUnidad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cUnidad);
    }

    public void cargarListaUnidadE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmactividadesControladorUrlEnum.URL2189
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaUnidadE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cUnidad);
    }

    public void seleccionarFilaUnidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cUnidad,
                        registroAux.getCampos().get(cUnidad));
    }

    public void seleccionarFilaUnidadE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cUnidad), "")
                        .toString();

        registro.getCampos().put(cUnidad, auxiliar);
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
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);
        // </CODIGO_DESARROLLADO>
        return true;
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

    public RegistroDataModelImpl getListaUnidadE() {
        return listaUnidadE;
    }

    public void setListaUnidadE(RegistroDataModelImpl listaUnidadE) {
        this.listaUnidadE = listaUnidadE;
    }

    public void cerrarFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario. Cuando la variable: {@code parametrosEntrada} tiene
     * valor nulo redirecciona al menu, de lo contrario redirecciona
     * al formulario:
     * {@code GeneralCodigoFormaEnum.FRMCOMPONENTESACTIVIDADES_CONTROLADOR}
     * .
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        if (parametrosEntrada != null) {
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.FRMCOMPONENTESACTIVIDADES_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametrosEntrada);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else {
            SessionUtil.redireccionarMenu();
        }
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

    public RegistroDataModelImpl getListaUnidad() {
        return listaUnidad;
    }

    public void setListaUnidad(RegistroDataModelImpl listaUnidad) {
        this.listaUnidad = listaUnidad;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public boolean isPermiteCrud() {
        return permiteCrud;
    }

    public void setPermiteCrud(boolean permiteCrud) {
        this.permiteCrud = permiteCrud;
    }

}