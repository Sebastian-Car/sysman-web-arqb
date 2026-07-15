package com.sysman.predial;

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
import com.sysman.predial.enums.AnopredialsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dmaldonado
 * @version 2, 23/05/2016 10:14:56 -- Modificado por dmaldonado
 *
 * @author 15/02/2017 10:17:05 -- Modificado por lcortes
 * 
 * @version 4.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Refactoring.<br>
 * Manejo de EJBs.<br>
 * Se modificaron los llamados a otros formularios para incluir el
 * enumerado del formulario.
 */
@ManagedBean
@ViewScoped
public class AnopredialsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String modulo;
    private final String numeroCons;
    /**
     * Constante que identifica el campo MESESAMNISTIA_PREDIAL
     */
    private final String campoMesesAmnisitia;
    // <DECLARAR_ATRIBUTOS>
    private boolean visibleCmdProyectos;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaNumero;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // <DECLARAR_EJBs>
    /**
     * Gestiona los llamados al paquete <code>PCK_SYSMAN_UTL</code>
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_EJBs>

    /**
     * Creates a new instance of AnopredialsControlador
     */
    public AnopredialsControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        numeroCons = "NUMERO";
        campoMesesAmnisitia = "MESESAMNISTIA_PREDIAL";

        try {
            numFormulario = GeneralCodigoFormaEnum.ANOPREDIALS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(AnopredialsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.ANO.getTable();

        buscarLlave();
        reasignarOrigen();

        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaNumero();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getUrlBeanById(
                        AnopredialsControladorUrlEnum.URL0002.getValue());

        urlCreacion = UrlServiceUtil.getUrlBeanById(
                        AnopredialsControladorUrlEnum.URL0001.getValue());

        urlActualizacion = UrlServiceUtil.getUrlBeanById(
                        AnopredialsControladorUrlEnum.URL0003.getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaNumero() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaNumero = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AnopredialsControladorUrlEnum.URL3664
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdProyectos(Registro reg, int rowNum) {
        // <CODIGO_DESARROLLADO>
        /* Verifica que exista el codigo */
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        reg.getCampos().get(numeroCons).toString());
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        SysmanConstantes.CONS_FUENTE);

        Registro regFuente = null;

        try {
            regFuente = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AnopredialsControladorUrlEnum.URL0004
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (regFuente == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB144")
                            .replace("#FUENTE#", SysmanConstantes.CONS_FUENTE)
                            .replace("#ANIO#", reg.getCampos().get(numeroCons)
                                            .toString()));
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ano", reg.getCampos().get(numeroCons).toString());

        SessionUtil.setFlash(parametros);

        SessionUtil.cargarModalDatos(Integer
                        .toString(GeneralCodigoFormaEnum.PROYECTOSAPORTESVOLS_CONTROLADOR
                                        .getCodigo()),
                        modulo);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            /* Obtener valor de bd */
            String valor = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA APORTE VOLUNTARIO", modulo, new Date(),
                            true);

            /* Validar parametro */
            valor = validarParametro("MANEJA APORTE VOLUNTARIO", valor) ? valor
                : "NO";

            visibleCmdProyectos = "SI".equals(valor);
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Util para verificar que el parametro {@code nomPar} existe en
     * la base de datos. De lo contrario muestra un mensaje
     * informativo.
     * 
     * @param nomPar
     * Nombre del parametro.
     * @param valor
     * Valor asignado al parametro en la base de datos.
     * @return {@code true}: si el parametro existe y tiene valor
     * diferente a nulo.
     */
    private boolean validarParametro(String nomPar, String valor) {
        if (SysmanFunciones.validarVariableVacio(valor)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2908")
                            .replace("#PAR#", nomPar));
            return false;
        }

        return true;
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get("NUMERO").toString().length() < 4) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2833"));
            return false;
        }
        if ((Integer.valueOf(registro.getCampos().get(campoMesesAmnisitia)
                        .toString()) > 12)
            || (Integer.valueOf(registro.getCampos()
                            .get(campoMesesAmnisitia).toString()) < 0)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2834"));
            return false;
        }

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

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
        if ((Integer.valueOf(registro.getCampos().get(campoMesesAmnisitia)
                        .toString()) > 12)
            || (Integer.valueOf(registro.getCampos()
                            .get(campoMesesAmnisitia).toString()) < 0)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2834"));
            return false;
        }
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo encargado de asignarvalores al registro.
    }

    // <SET_GET_ATRIBUTOS>

    public boolean isVisibleCmdProyectos() {
        return visibleCmdProyectos;
    }

    public void setVisibleCmdProyectos(boolean visibleCmdProyectos) {
        this.visibleCmdProyectos = visibleCmdProyectos;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaNumero() {
        return listaNumero;
    }

    public void setListaNumero(List<Registro> listaNumero) {
        this.listaNumero = listaNumero;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
