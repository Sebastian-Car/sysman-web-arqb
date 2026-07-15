package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contratos.enums.ClasificadocumentosControladorUrlEnum;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dsuesca
 * @version 1, 13/10/2015
 * 
 * @modifier amonroy
 * @version 2, 08/08/2017 Se realiza el Proceso de Refactoring para
 * las operaciones CRUD y el listado del combo "Descripcion"
 */
@ManagedBean
@ViewScoped
public class ClasificadocumentosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String tipoContrato;
    private String nombreContrato;
    private String codigo;
    private String nombre;
    private List<Registro> listaDescripcion;

    /**
     * Creates a new instance of ClasificadocumentosControlador
     */
    public ClasificadocumentosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CLASIFICADOCUMENTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                tipoContrato = (String) parametrosEntrada.get("tipoContrato");
                nombreContrato = (String) parametrosEntrada
                                .get("nombreContrato");
            }

        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ClasificadocumentosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CLASIFICA_DOCUMENTOS;
        buscarLlave();
        nombre = nombreContrato;
        codigo = tipoContrato;
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaDescripcion();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
    }

    public List<Registro> getListaDescripcion() {
        return listaDescripcion;
    }

    public void setListaDescripcion(List<Registro> listaDescripcion) {
        this.listaDescripcion = listaDescripcion;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getNombreContrato() {
        return nombreContrato;
    }

    public void setNombreContrato(String nombreContrato) {
        this.nombreContrato = nombreContrato;
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

    public void cargarListaDescripcion() {
        try {
            listaDescripcion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasificadocumentosControladorUrlEnum.URL4024
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
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CLASEORDEN.getName(),
                        tipoContrato);
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

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
