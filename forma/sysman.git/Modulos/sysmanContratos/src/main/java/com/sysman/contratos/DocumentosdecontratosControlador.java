package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.util.HashMap;
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
 * @version 1, 09/10/2015
 * @modified jguerrero
 * @version 2. 08/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped
public class DocumentosdecontratosControlador extends BeanBaseContinuoAcmeImpl {

    /**
     * Creates a new instance of DocumentosdecontratosControlador
     */
    public DocumentosdecontratosControlador() {
        super();
        try {
            numFormulario = GeneralCodigoFormaEnum.DOCUMENTOSDECONTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(DocumentosdecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }

    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.DOCUMENTOS_CONTRATOS.getTable();
        enumBase = GenericUrlEnum.DOCUMENTOS_CONTRATOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

    }

    public void oprimirdocumento() {
        SessionUtil.cargarModalDatos(String.valueOf(
                        GeneralCodigoFormaEnum.CLASIFICARDOCUMENTOS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo());
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

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
