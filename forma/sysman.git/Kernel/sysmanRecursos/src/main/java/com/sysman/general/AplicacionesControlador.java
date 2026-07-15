package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author cmanrique
 * @version 1, 18/04/2016
 *
 * -- Modificado por lcortes 03/04/2017 08:25. Ajustes Refactoring.
 */
@ManagedBean
@ViewScoped
public class AplicacionesControlador extends BeanBaseContinuoAcmeImpl {

    /**
     * Creates a new instance of AplicacionesControlador
     */
    public AplicacionesControlador() {
        super();
        try {
            numFormulario = GeneralCodigoFormaEnum.APLICACIONES_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AplicacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.APLICACIONES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
    }

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
        String ruta = registro.getCampos().get("RUTA_ARCHIVOS").toString();
        ruta = !ruta.endsWith(File.separator) && !ruta.endsWith("/")
            ? ruta + File.separator : ruta;
        File carpeta = new File(ruta);
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB706"));
            return false;
        }

        String[] directorios = { "informes", "plantillas", "plantillasword" };
        File subcarpeta;
        for (String directorio : directorios) {
            subcarpeta = new File(ruta + directorio);
            if (!subcarpeta.exists()) {
                subcarpeta.mkdirs();
            }
        }
        registro.getCampos().put("RUTA_ARCHIVOS", ruta);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        try {
            SessionUtil.setApplicationVarContainer("recargarRutas", true);
        }
        catch (NamingException e) {
            Logger.getLogger(AplicacionesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }
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
        registro.getCampos().remove("APLICACION");
        registro.getCampos().remove("NOMBRE");
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }
}
