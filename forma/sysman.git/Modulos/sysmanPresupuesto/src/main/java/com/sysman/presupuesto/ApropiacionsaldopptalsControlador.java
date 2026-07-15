package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.ApropiacionsaldopptalsControladorEnum;
import com.sysman.presupuesto.enums.ApropiacionsaldopptalsControladorUrlEnum;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 17/06/2016
 * @author yrojas
 * @version 2, 18/04/2017 Se cambiaron las consultas por la invocacion
 * de los DSS.
 */
@ManagedBean
@ViewScoped
public class ApropiacionsaldopptalsControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String titulo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private Map<String, Object> rid;
    private String anio;
    private String codigo;
    private String nombre;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ApropiacionsaldopptalsControlador
     */
    @SuppressWarnings("unchecked")
    public ApropiacionsaldopptalsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.APROPIACIONSALDOPPTALS_CONTROLADOR
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
            Logger.getLogger(ApropiacionsaldopptalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        tabla = ApropiacionsaldopptalsControladorEnum.PARAM0.getValue();
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
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionsaldopptalsControladorUrlEnum.URL13959
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
        titulo = idioma.getString("TB_TB249") + " " + nombre.toUpperCase();
        // </CODIGO_DESARROLLADO>
    }

    @Override
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
        // Actualmente no se requiere remover ningun combo.
    }

    @Override
    public void asignarValoresRegistro() {
        // Actualmente no se requiere asignar ningun valor.
    }

    // <SET_GET_ATRIBUTOS>

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

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
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

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        String[] campos = { "rid", "anio" };
        Object[] valores = { rid, anio };
        SessionUtil.redireccionarPorFormulario(SessionUtil.getModulo(),
                        Integer.toString(
                                        GeneralCodigoFormaEnum.PLANPRESUPUESTALPTOS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);
        // </CODIGO_DESARROLLADO>
    }
}
