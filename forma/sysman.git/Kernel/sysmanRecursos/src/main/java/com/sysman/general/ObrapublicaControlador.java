package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.ObrapublicaControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author jrodriguezr
 * @version 1, 11/12/2015
 * @version 2, 10/08/2017 jrodriguezr Se refactoriza el c�digo SQL de
 * las listas para utilizar DSS. Tambi�n los llamados a funciones,
 * procedimientos y m�todos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties. Cambio el numero del formulario al
 * enumerado.
 *
 */
@ManagedBean
@ViewScoped
public class ObrapublicaControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private String numeroContrato;
    private String tipoContrato;

    public ObrapublicaControlador() {
        compania = SessionUtil.getCompania();
        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                tipoContrato = parametrosEntrada.get("claseOrden").toString();
                numeroContrato = parametrosEntrada.get("numeroOrden")
                                .toString();
            }
            registro = new Registro(new HashMap<String, Object>());
            numFormulario = GeneralCodigoFormaEnum.OBRAPUBLICA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(ObrapublicaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.ORDENDECOMPRA.getTable();
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos() {
        css = new HashMap<>();
        css.put("KEY_COMPANIA", compania);
        css.put("KEY_CLASEORDEN", tipoContrato);
        css.put("KEY_NUMERO", numeroContrato);
        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ObrapublicaControladorUrlEnum.URL0001
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ObrapublicaControladorUrlEnum.URL0002
                                                        .getValue());
        cargarRegistro(css, ACCION_MODIFICAR);
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        //
        registroIni = new HashMap<>(registro.getCampos());
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
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
        registro.getCampos().remove(GeneralParameterEnum.CLASEORDEN.getName());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());

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
    public void cargarRegistro() {
        // Metodo heredado
    }

    @Override
    public void iniciarListasSubNulo() {
        // Metodo heredado
    }

    @Override
    public void iniciarListasSub() {
        // Metodo heredado
    }

    @Override
    public void iniciarListas() {
        // Metodo heredado
    }

    @Override
    public boolean eliminarAntes() {
        return false;

    }

    @Override
    public boolean eliminarDespues() {
        return false;

    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }
}
