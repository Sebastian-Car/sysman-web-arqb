package com.sysman.presupuesto;

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
import com.sysman.presupuesto.enums.ReconocimientosControladorUrlEnum;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 20/06/2016
 * 
 * @author eamaya
 * @version 2, 19/04/2017 Proceso de Refactoring y Correcciones
 * SonarLint
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 */
@ManagedBean
@ViewScoped
public class ReconocimientosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String anio;
    private String codigo;
    private String nombre;
    private String valorTotal;
    private String titulo;
    private Map<String, Object> rid;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ReconocimientosControlador
     */
    public ReconocimientosControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 927
            numFormulario = GeneralCodigoFormaEnum.RECONOCIMIENTOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                anio = (String) parametrosEntrada.get("anio");
                codigo = (String) parametrosEntrada.get("codigo");
                nombre = (String) parametrosEntrada.get("nombre");
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ReconocimientosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        tabla = GenericUrlEnum.PLAN_PRESUPUESTAL.getTable();

        buscarLlave();
        reasignarOrigen();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        ReconocimientosControladorUrlEnum.URL6969
                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        titulo = idioma.getString("TB_TB261") + " " + nombre.toUpperCase();
        actualizarTotales();
    }

    @Override
    // </CODIGO_DESARROLLADO>
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
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
        // METODO_NO_IMPLEMENTADO
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public void actualizarTotales() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        codigo);

        Registro regAux;
        try {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReconocimientosControladorUrlEnum.URL8317
                                                                            .getValue())
                                            .getUrl(), param));
            valorTotal = regAux.getCampos().get("TOTAL").toString();

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.redireccionar("/planpresupuestalpto.sysman");
        // </CODIGO_DESARROLLADO>
    }

}
