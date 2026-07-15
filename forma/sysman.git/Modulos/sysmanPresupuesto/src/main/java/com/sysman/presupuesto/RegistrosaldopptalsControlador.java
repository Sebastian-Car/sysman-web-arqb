package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.RegistrosaldopptalsControladorUrlEnum;

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
 *
 * -- Modificado por lcortes 20/04/2017 11:08. Ajustes refactorizacion
 * de codigo.
 */
@ManagedBean
@ViewScoped
public class RegistrosaldopptalsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String titulo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String anio;
    private String codigo;
    private String nombre;
    private Map<String, Object> rid;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of RegistrosaldopptalsControlador
     */
    public RegistrosaldopptalsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.REGISTROSALDOPPTALS_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                anio = parametrosEntrada.get("anio").toString();
                codigo = parametrosEntrada.get("codigo").toString();
                nombre = parametrosEntrada.get("nombre").toString();
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RegistrosaldopptalsControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
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

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        parametrosListado.put(GeneralParameterEnum.ID_PLAN.getName(),
                        codigo);
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        RegistrosaldopptalsControladorUrlEnum.URL119
                        .getValue());
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
        titulo = idioma.getString("TB_TB248") + " " + nombre.toUpperCase();
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
        // NO EST� IMPLEMENTADO
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid", "anio" };
        Object[] valores = { rid, anio };
        SessionUtil.redireccionar("/planpresupuestalpto.sysman", campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // NO EST� IMPLEMENTADO
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
}
