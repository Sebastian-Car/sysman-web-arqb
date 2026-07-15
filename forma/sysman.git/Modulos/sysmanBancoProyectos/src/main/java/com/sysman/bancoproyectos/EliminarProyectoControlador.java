package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoTresRemote;
import com.sysman.bancoproyectos.enums.EliminarProyectoControladorEnum;
import com.sysman.bancoproyectos.enums.EliminarProyectoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author esarmiento
 * @version 1, 19/09/2015
 * @modified jguerrero
 * @version 2. 13/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped

public class EliminarProyectoControlador extends BeanBaseModal {

    private final String compania;
    private String proyecto;
    private String nombreProy;
    private RegistroDataModelImpl listaProyectoinicial;

    @EJB
    EjbBancoProyectoTresRemote ejbBancoProytres;

    /**
     * Creates a new instance of EliminarProyectoControlador
     */
    public EliminarProyectoControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.ELIMINAR_PROYECTO_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(EliminarProyectoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaProyectoinicial();
        abrirFormulario();
    }

    public void cargarListaProyectoinicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EliminarProyectoControladorUrlEnum.URL1959
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaProyectoinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        try {

            String resultado = ejbBancoProytres.eliminarProyecto(compania,
                            proyecto,
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(resultado);
            proyecto = null;
            nombreProy = null;
            cargarListaProyectoinicial();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>

    }

    public void seleccionarFilaProyectoinicial(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        proyecto = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        nombreProy = retornarString(registroAux,
                        EliminarProyectoControladorEnum.NOMBREPROYECTO
                                        .getValue());
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getNombreProy() {
        return nombreProy;
    }

    public void setNombreProy(String nombreProy) {
        this.nombreProy = nombreProy;
    }

    public RegistroDataModelImpl getListaProyectoinicial() {
        return listaProyectoinicial;
    }

    public void setListaProyectoInicial(
        RegistroDataModelImpl listaProyectoinicial) {
        this.listaProyectoinicial = listaProyectoinicial;
    }

    @Override
    public void abrirFormulario() {
        // METODO NO IMPLEMENTADO
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

}