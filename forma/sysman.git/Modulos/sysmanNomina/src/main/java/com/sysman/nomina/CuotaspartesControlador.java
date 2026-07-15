package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.CuotaspartesControladorEnum;
import com.sysman.nomina.enums.CuotaspartesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author cmanrique
 * 
 * @version 2, 05/09/2017, <strong>pespitia</strong>
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class CuotaspartesControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBREENTIDAD_LB</code>
     */
    private final String cNombreEntidadLb;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SUCURSAL</code>
     */
    private final String cSucursal;

    private Registro registroSelecionado;
    private RegistroDataModelImpl listaNIT;
    private RegistroDataModelImpl listaNITE;
    private String auxiliar;

    /** Variable que contiene la sucursal del tercero seleccionado */
    private String sucursal;

    /**
     * Creates a new instance of CuotaspartesControlador
     */
    public CuotaspartesControlador() {
        super();

        compania = SessionUtil.getCompania();

        cCompania = GeneralParameterEnum.COMPANIA.getName();

        cNombreEntidadLb = CuotaspartesControladorEnum.NOMBREENTIDAD_LB
                        .getValue();

        cSucursal = GeneralParameterEnum.SUCURSAL.getName();

        try {
            // 26
            numFormulario = GeneralCodigoFormaEnum.CUOTASPARTES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CUOTASPARTES;
        registro = new Registro();

        buscarLlave();
        reasignarOrigen();
        cargarListaNIT();
        cargarListaNITE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    public void seleccionarFilaNIT(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("NIT", registroAux.getCampos().get("NIT"));

        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cSucursal), "")
                        .toString();

        registro.getCampos().put(cSucursal, sucursal);

        registro.getCampos().put(cNombreEntidadLb,
                        registroAux.getCampos().get("NOMBRE"));
    }

    public void seleccionarFilaNITE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();

        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cSucursal), "")
                        .toString();

        registro.getCampos().put(cSucursal, sucursal);

        registro.getCampos().put(cNombreEntidadLb,
                        registroAux.getCampos().get("NOMBRE"));
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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

        registro.getCampos().remove(cNombreEntidadLb);
        registro.getCampos().remove("NOMBRETIPO");

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

    public Registro getRegistroSelecionado() {
        return registroSelecionado;
    }

    public void setRegistroSelecionado(Registro registroSelecionado) {
        this.registroSelecionado = registroSelecionado;
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        if (!SysmanFunciones.validarVariableVacio(sucursal)) {
            registro.getCampos().put(cSucursal, sucursal);
        }

        registro.getCampos().remove(cCompania);
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaNIT() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuotaspartesControladorUrlEnum.URL5828
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaNIT = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaNITE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuotaspartesControladorUrlEnum.URL5828
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaNITE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * Metodo ejecutado al cambiar el control NIT en la fila
     * seleccionada dentro de la grilla. Muestra en tiempo real los
     * cambios de valores entre campos.
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarNITC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        cNombreEntidadLb,
                        registro.getCampos().get(cNombreEntidadLb));
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListaNIT() {
        return listaNIT;
    }

    public void setListaNIT(RegistroDataModelImpl listaNIT) {
        this.listaNIT = listaNIT;
    }

    public RegistroDataModelImpl getListaNITE() {
        return listaNITE;
    }

    public void setListaNITE(RegistroDataModelImpl listaNITE) {
        this.listaNITE = listaNITE;
    }
}
