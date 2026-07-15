package com.sysman.almacen;

import com.sysman.almacen.enums.ClasesdebodegasControladorEnum;
import com.sysman.almacen.enums.ClasesdebodegasControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author vmolano
 * @version 1, 02/02/2016
 * 
 * @version 2, 26/04/2017
 * @author jreina se realizaron los cambios de refactoring en el origen de grilla.
 */
@ManagedBean
@ViewScoped
public class ClasesdebodegasControlador extends BeanBaseContinuoAcmeImpl {

    /**
     * Creates a new instance of ClasesdebodegasControlador
     */
    public ClasesdebodegasControlador() {
        super();
        try {
            numFormulario = GeneralCodigoFormaEnum.CLASESDEBODEGAS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            ClasesdebodegasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = ClasesdebodegasControladorEnum.TABLA.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // No es necesaria ninguna validaci�n actualmente.
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public void reasignarOrigen() {
        urlListado=UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ClasesdebodegasControladorUrlEnum.URL2397.getValue());
    }

    @Override
    public void removerCombos() {
        // Actualmente no se requiere remover ningun combo.
    }

    @Override
    public boolean insertarAntes() {
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void asignarValoresRegistro() {

        // Actualmente no se requiere asignar ningun valor.
    }

}
