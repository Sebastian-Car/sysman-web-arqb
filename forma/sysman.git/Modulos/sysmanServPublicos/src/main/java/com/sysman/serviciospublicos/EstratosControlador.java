package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.EstratosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author amonroy
 * @version 1, 01/08/2016
 * @author spina
 * @version 2, 18/05/2017 spina - se realiza refactoring dss,
 * depuracion y ejb
 */
@ManagedBean
@ViewScoped
public class EstratosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String codigoCons;

    // <DECLARAR_ATRIBUTOS>
    private String uso;
    private String nombreUso;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private boolean manejaEstratoHomologacion;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaUso;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of EstratosControlador
     */
    public EstratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCons = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.ESTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(EstratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        try {
            manejaEstratoHomologacion = "SI".equalsIgnoreCase(
                            SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            "MANEJA ESTRATO HOMOLOGACION",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            "NO").toString());
            enumBase = GenericUrlEnum.SP_ESTRATOS;
            buscarLlave();
            registro = new Registro();
            // <CARGAR_LISTA>
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            cargarListaUso();
            // </CARGAR_LISTA_COMBO_GRANDE>
            reasignarOrigen();
            abrirFormulario();
        }
        catch (SystemException ex) {
            Logger.getLogger(EstratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("USO", uso);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaUso() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstratosControladorUrlEnum.URL7905
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaUso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        codigoCons);

        listaUso.load();
        if (!listaUso.getDatasource().isEmpty()) {
            uso = listaUso.getDatasource().get(1).getCampos().get(codigoCons)
                            .toString();
            nombreUso = listaUso.getDatasource().get(1).getCampos()
                            .get("NOMBRE").toString();
            registro.getCampos().put("USO", uso);
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaUso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        uso = registroAux.getCampos().get(codigoCons).toString();
        nombreUso = registroAux.getCampos().get("NOMBRE").toString();
        reasignarOrigen();

    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("USO", uso);
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
        // Metodo Heredado de la clase BeanBase
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("USO");
    }

    public void cerrarFormulario() {

        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado de la clase BeanBase
    }

    // <SET_GET_ATRIBUTOS>
    public String getUso() {
        return uso;
    }

    public void setUso(String uso) {
        this.uso = uso;
    }

    public String getNombreUso() {
        return nombreUso;
    }

    public void setNombreUso(String nombreUso) {
        this.nombreUso = nombreUso;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    public boolean isManejaEstratoHomologacion() {
        return manejaEstratoHomologacion;
    }

    public void setManejaEstratoHomologacion(
        boolean manejaEstratoHomologacion) {
        this.manejaEstratoHomologacion = manejaEstratoHomologacion;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaUso() {
        return listaUso;
    }

    public void setListaUso(RegistroDataModelImpl listaUso) {
        this.listaUso = listaUso;
    }

}
