package com.sysman.almacen;

import com.sysman.almacen.enums.SubdevolutivosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ngomez
 * @version 1, 23/10/2015
 * @version 2, 11/05/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss. Tambien los llamados a funciones,
 * procedimientos y metodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 * 
 * @author asana
 * @version 3, 13/06/2017 Se implementa enum en formulario.
 * Modificación ruta redireccionador
 */
@ManagedBean
@ViewScoped
public class SubdevolutivosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del módulo desde
     * el cual el usuario inicio session
     */
    private final String modulo;
    private Map<String, Object> rid;
    private String elemento;
    private List<Registro> listaEstado;

    /**
     * Creates a new instance of SubdevolutivosControlador
     */
    public SubdevolutivosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.SUBDEVOLUTIVOS_CONTROLADOR
                            .getCodigo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                elemento = parametrosEntrada.get("elemento").toString();
            }
            SessionUtil.cleanFlash();
            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.DEVOLUTIVO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaEstado();
        abrirFormulario();
    }

    public List<Registro> getListaEstado() {
        return listaEstado;
    }

    public void setListaEstado(List<Registro> listaEstado) {
        this.listaEstado = listaEstado;
    }

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    public void cargarListaEstado() {
        try {
            listaEstado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdevolutivosControladorUrlEnum.URL2808
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        if (!cargado) {
            registro.getCampos().put("ELEMENTO", elemento);
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ELEMENTO.getName());
        registro.getCampos().remove(GeneralParameterEnum.PLACA.getName());
        registro.getCampos().remove(GeneralParameterEnum.SERIE.getName());
        registro.getCampos().remove(GeneralParameterEnum.VALOR.getName());
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

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        HashMap<String, Object> param = new HashMap<>();
        param.put("rid", rid);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.INVENTARIOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, modulo);

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ELEMENTO.getName(),
                        elemento);
        buscarUrls();
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado de la clase BeanBase
    }
}
