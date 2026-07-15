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
 * @version 2, 29/09/2017
 * @author jreina se realizaron los cambios de refactoring en el
 * origen de grilla.
 * 
 */

@ManagedBean
@ViewScoped
public class DocumentosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    private final String modulo;

    private boolean visibleCodigo;

    private boolean visibleCodExogena;

    private boolean mostrarSiglaUno;

    private boolean mostrarSiglaDos;

    private boolean mostrarInsertar;

    private boolean bloquearDescripcionE;

    private boolean mostrarEliminar;

    private boolean mostarCune;

    /**
     * Creates a new instance of DocumentosControlador
     */
    public DocumentosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        visibleCodigo = "21".equals(SessionUtil.getModulo()) ? false : true;
        visibleCodExogena = "60".equals(SessionUtil.getModulo());
        try {
            numFormulario = GeneralCodigoFormaEnum.DOCUMENTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(DocumentosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPOS_DOCUMENTOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        if ("6012003".equals(SessionUtil.getMenuActual())
            || "21010105".equals(SessionUtil.getMenuActual())) {
            mostrarSiglaUno = true;
            mostrarSiglaDos = true;
            mostrarInsertar = true;
            bloquearDescripcionE = false;
            mostrarEliminar = true;
        }
        else {
            mostrarSiglaUno = false;
            mostrarSiglaDos = false;
            visibleCodigo = true;
            mostrarInsertar = false;
            bloquearDescripcionE = true;
            mostrarEliminar = false;

        }
        mostarCune = "60604".equals(SessionUtil.getMenuActual());

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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
    public void removerCombos() {
        // Metodo heredado
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        if ("60506".equals(SessionUtil.getMenuActual()))
            SessionUtil.redireccionarMenuFormulario("60506");
        else
            SessionUtil.redireccionar("/menu.sysman");

        // </CODIGO_DESARROLLADO>
    }

    public boolean isVisibleCodigo() {
        return visibleCodigo;
    }

    public void setVisibleCodigo(boolean visibleCodigo) {
        this.visibleCodigo = visibleCodigo;
    }

    public boolean isMostrarSiglaUno() {
        return mostrarSiglaUno;
    }

    public void setMostrarSiglaUno(boolean mostrarSiglaUno) {
        this.mostrarSiglaUno = mostrarSiglaUno;
    }

    public boolean isMostrarSiglaDos() {
        return mostrarSiglaDos;
    }

    public void setMostrarSiglaDos(boolean mostrarSiglaDos) {
        this.mostrarSiglaDos = mostrarSiglaDos;
    }

    public boolean isMostrarInsertar() {
        return mostrarInsertar;
    }

    public void setMostrarInsertar(boolean mostrarInsertar) {
        this.mostrarInsertar = mostrarInsertar;
    }

    public boolean isBloquearDescripcionE() {
        return bloquearDescripcionE;
    }

    public void setBloquearDescripcionE(boolean bloquearDescripcionE) {
        this.bloquearDescripcionE = bloquearDescripcionE;
    }

    public boolean isMostrarEliminar() {
        return mostrarEliminar;
    }

    public void setMostrarEliminar(boolean mostrarEliminar) {
        this.mostrarEliminar = mostrarEliminar;
    }

    public boolean isVisibleCodExogena() {
        return visibleCodExogena;
    }

    public void setVisibleCodExogena(boolean visibleCodExogena) {
        this.visibleCodExogena = visibleCodExogena;
    }

    public boolean isMostarCune() {
        return mostarCune;
    }

    public void setMostarCune(boolean mostarCune) {
        this.mostarCune = mostarCune;
    }

}
