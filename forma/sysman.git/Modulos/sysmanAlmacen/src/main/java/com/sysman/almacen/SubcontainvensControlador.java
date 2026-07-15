package com.sysman.almacen;

import com.sysman.almacen.enums.SubcontainvensControladorEnum;
import com.sysman.almacen.enums.SubcontainvensControladorUrlEnum;
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
 * @author ngomez
 * @version 1, 27/10/2015
 * 
 * @author eamaya
 * @version 2, 09/05/2017 Proceso de Refactoring y Correcciones
 * SonarLint
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class SubcontainvensControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    /**
     * Constante que identifica el nombre del campo CODIGO
     */

    private Map<String, Object> rid;
    private String codigo;
    private String anio;
    private String tipo;
    private String nombre;
    private String cuentaActivo;
    private RegistroDataModelImpl listaTipoMovimiento;
    private RegistroDataModelImpl listaTipoMovimientoE;
    private RegistroDataModelImpl listaCuentaDebito;
    private RegistroDataModelImpl listaCuentaDebitoE;
    private RegistroDataModelImpl listaCuentaCredito;
    private RegistroDataModelImpl listaCuentaCreditoE;
    private String auxiliar;
    private String modulo;

    /**
     * Creates a new instance of SubcontainvensControlador
     */
    public SubcontainvensControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.SUBCONTAINVENS_CONTROLADOR
                            .getCodigo();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                codigo = (String) parametrosEntrada.get("codigo");
                anio = (String) parametrosEntrada.get("anio");
                tipo = (String) parametrosEntrada.get("tipo");
                nombre = (String) parametrosEntrada.get("nombre");
                cuentaActivo = (String) parametrosEntrada.get("cuentaActivo");
            }
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(SubcontainvensControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ALMACEN_CONTABILIDAD;

        buscarLlave();
        reasignarOrigen();
        cargarListaTipoMovimiento();
        cargarListaTipoMovimientoE();
        cargarListaCuentaDebito();
        cargarListaCuentaDebitoE();
        cargarListaCuentaCredito();
        cargarListaCuentaCreditoE();

        registro = new Registro(new HashMap<String, Object>());
        registro.getCampos().put("ANO", anio);

        abrirFormulario();
    }

    public void cargarListaTipoMovimiento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubcontainvensControladorUrlEnum.URL6300
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubcontainvensControladorEnum.PARAM0.getValue(), tipo);

        listaTipoMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTipoMovimientoE() {
        listaTipoMovimientoE = listaTipoMovimiento;
    }

    public void cargarListaCuentaDebito() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubcontainvensControladorUrlEnum.URL8028
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaDebito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCuentaDebitoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubcontainvensControladorUrlEnum.URL8028
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaDebitoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCuentaCredito() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubcontainvensControladorUrlEnum.URL8028
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaCredito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCuentaCreditoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubcontainvensControladorUrlEnum.URL8028
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaCreditoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control TipoMovimiento
     *
     *
     */
    public void cambiarTipoMovimiento() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TipoMovimiento en la
     * fila seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipoMovimientoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CuentaDebito en la fila
     * seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCuentaDebitoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("CUENTADEBITO", listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CuentaCredito en la fila
     * seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCuentaCreditoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>

    public void seleccionarFilaTipoMovimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOMOVIMIENTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaTipoMovimientoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        " ")
                        .toString();
    }

    public void seleccionarFilaCuentaDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaCuentaDebitoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        " ")
                        .toString();
    }

    public void seleccionarFilaCuentaCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaCuentaCreditoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        " ")
                        .toString();
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

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", rid);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.INVENTARIOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        parametrosListado.put("ANIO", anio);
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("CODIGOELEMENTO", codigo);
        registro.getCampos().put("ANO", anio);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove("NOMBRE");
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

    @Override
    public void asignarValoresRegistro() {
        registro.getCampos().put("ANO", anio);
    }

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public RegistroDataModelImpl getListaTipoMovimiento() {
        return listaTipoMovimiento;
    }

    public void setListaTipoMovimiento(
        RegistroDataModelImpl listaTipoMovimiento) {
        this.listaTipoMovimiento = listaTipoMovimiento;
    }

    public RegistroDataModelImpl getListaTipoMovimientoE() {
        return listaTipoMovimientoE;
    }

    public void setListaTipoMovimientoE(
        RegistroDataModelImpl listaTipoMovimientoE) {
        this.listaTipoMovimientoE = listaTipoMovimientoE;
    }

    public RegistroDataModelImpl getListaCuentaDebito() {
        return listaCuentaDebito;
    }

    public void setListaCuentaDebito(RegistroDataModelImpl listaCuentaDebito) {
        this.listaCuentaDebito = listaCuentaDebito;
    }

    public RegistroDataModelImpl getListaCuentaDebitoE() {
        return listaCuentaDebitoE;
    }

    public void setListaCuentaDebitoE(
        RegistroDataModelImpl listaCuentaDebitoE) {
        this.listaCuentaDebitoE = listaCuentaDebitoE;
    }

    public RegistroDataModelImpl getListaCuentaCredito() {
        return listaCuentaCredito;
    }

    public void setListaCuentaCredito(
        RegistroDataModelImpl listaCuentaCredito) {
        this.listaCuentaCredito = listaCuentaCredito;
    }

    public RegistroDataModelImpl getListaCuentaCreditoE() {
        return listaCuentaCreditoE;
    }

    public void setListaCuentaCreditoE(
        RegistroDataModelImpl listaCuentaCreditoE) {
        this.listaCuentaCreditoE = listaCuentaCreditoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getCuentaActivo() {
        return cuentaActivo;
    }

    public void setCuentaActivo(String cuentaActivo) {
        this.cuentaActivo = cuentaActivo;
    }
}
