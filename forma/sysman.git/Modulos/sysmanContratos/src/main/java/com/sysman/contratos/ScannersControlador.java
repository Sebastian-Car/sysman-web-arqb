package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ybecerra
 * @version 1, 11/09/2015
 * 
 * @author asana No se realiza refactoring dado que opción se eliminó.
 */
@ManagedBean
@ViewScoped
public class ScannersControlador extends BeanBaseContinuoAcme {

    private final String compania;

    /**
     * Creates a new instance of ScannersControlador
     */
    public ScannersControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SCANNERS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ScannersControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = "SCANNER";
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        origenDatos = "SELECT "
            + "     SCANNER.COMPANIA, "
            + "     SCANNER.CODIGO, "
            + "     SCANNER.PARAMETROSCANNER, "
            + "     SCANNER.DESCRIPCION "
            + " FROM "
            + "     SCANNER "
            + " WHERE "
            + "     SCANNER.COMPANIA = '" + compania + "' ";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenDatos);
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
        String criterio = "'COMPANIA = ''" + compania + "'''";
        int codigo;
        try {
            codigo = (int) Acciones.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SYSMAN_UTL.FC_GENCONSECUTIVO",
                            "'" + tabla + "' ," + criterio + ", 'CODIGO'",
                            Types.INTEGER);
            registro.getCampos().put("CODIGO", codigo);
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            Logger.getLogger(ScannersControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        registro.getCampos().put("COMPANIA", compania);
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
        // METODO_NO_APLICADO
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_APLICADO
    }

}
