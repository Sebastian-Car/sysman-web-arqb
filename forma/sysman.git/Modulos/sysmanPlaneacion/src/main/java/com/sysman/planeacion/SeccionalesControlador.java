package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.planeacion.enums.SeccionalesControladorEnum;
import com.sysman.planeacion.enums.SeccionalesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 17/02/2016
 * 
 * @version 2, 08/09/2017. <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class SeccionalesControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SUCURSAL</code>
     */
    private final String cSucursal;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBRE</code>
     */
    private final String cNombre;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBREDIRECTOR</code>
     */
    private final String cNombreDirector;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NIT</code>
     */
    private final String cNit;

    private RegistroDataModelImpl listaDirectorSeccional;
    private RegistroDataModelImpl listaDirectorSeccionalE;
    private String auxiliar;
    private String nombreDirectorC;
    private String sucursalC;

    /**
     * Creates a new instance of SeccionalesControlador
     */
    public SeccionalesControlador() {
        super();

        compania = SessionUtil.getCompania();

        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cSucursal = GeneralParameterEnum.SUCURSAL.getName();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cNombreDirector = SeccionalesControladorEnum.NOMBREDIRECTOR.getValue();
        cNit = SeccionalesControladorEnum.NIT.getValue();

        try {
            // 468
            numFormulario = GeneralCodigoFormaEnum.SECCIONALES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SECCIONALES;
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        reasignarOrigen();
        cargarListaDirectorSeccional();
        cargarListaDirectorSeccionalE();
        abrirFormulario();
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public void cargarListaDirectorSeccional() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SeccionalesControladorUrlEnum.URL3209
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaDirectorSeccional = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNit);
    }

    public void cargarListaDirectorSeccionalE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SeccionalesControladorUrlEnum.URL3209
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaDirectorSeccionalE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNit);

    }

    public void cambiarDirectorSeccionalC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombreDirector, nombreDirectorC);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cSucursal, sucursalC);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaDirectorSeccional(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("DIRECTOR",
                        registroAux.getCampos().get(cNit));

        registro.getCampos().put(cNombreDirector,
                        registroAux.getCampos().get(cNombre));

        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursal));
    }

    public void seleccionarFilaDirectorSeccionalE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cNit), "")
                        .toString();

        nombreDirectorC = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();

        sucursalC = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cSucursal), "")
                        .toString();

        registro.getCampos().put(cSucursal, sucursalC);
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el valor del campo Codigo al
     * registrar un nuevo elemento.
     * 
     */
    public void cambiarCodigo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cNombreDirector);
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
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
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

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListaDirectorSeccional() {
        return listaDirectorSeccional;
    }

    public void setListaDirectorSeccional(
        RegistroDataModelImpl listaDirectorSeccional) {
        this.listaDirectorSeccional = listaDirectorSeccional;
    }

    public RegistroDataModelImpl getListaDirectorSeccionalE() {
        return listaDirectorSeccionalE;
    }

    public void setListaDirectorSeccionalE(
        RegistroDataModelImpl listaDirectorSeccionalE) {
        this.listaDirectorSeccionalE = listaDirectorSeccionalE;
    }
}
