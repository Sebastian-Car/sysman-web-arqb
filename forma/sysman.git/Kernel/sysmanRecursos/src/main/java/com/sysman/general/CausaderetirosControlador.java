package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author cmanrique
 *
 * --Modificado por lcortes 15/03/2017 16:44 --> Ajustes de buenas
 * practicas SonarLint.
 * 
 * @version 3, 22/08/2017, <strong>pespitia</strong>:<br>
 * Se reemplazo el numero del formulario por enumerado.<br>
 * Refactoring de sentencias SQL.<br>
 * Se aplicaron las recomendaciones de SonarLint
 */
@ManagedBean
@ViewScoped
public class CausaderetirosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Creates a new instance of CausaderetirosControlador
     */
    public CausaderetirosControlador() {
        super();

        compania = SessionUtil.getCompania();
        cCompania = GeneralParameterEnum.COMPANIA.getName();

        try {
            // 16
            numFormulario = GeneralCodigoFormaEnum.CAUSADERETIROS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(CausaderetirosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CAUSARETIRO;
        registro = new Registro();

        buscarLlave();
        reasignarOrigen();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cCompania);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(cCompania, compania);
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
