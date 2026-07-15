package com.sysman.almacen;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author OTORRES
 * @version 1, 03/02/2016
 *
 * -- Modificado por lcortes 10/05/2017. Refactorizacion de codigo
 * para utilizar dss, revision de Sonar y se ajusta los llamados a
 * metodos de la clase Acciones.
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * reemplaza el metodo redireccionar por el metodo
 * redireccionarPorFormulario para manejar el numero de formulario con
 * el enumerado correspondiente.
 */
@ManagedBean
@ViewScoped
public class TipoEventosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String cCodigo;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of TipoEventosControlador
     */
    public TipoEventosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.TIPO_EVENTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPO_EVENTO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    public void oprimirRequisitos(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        String form = Integer
                        .toString(GeneralCodigoFormaEnum.TIPO_EVENTO_REQ_CONTROLADOR
                                        .getCodigo());
        String[] campos = { "tipoEvento" };
        String[] valores = { String.valueOf(reg.getCampos().get(cCodigo)) };
        SessionUtil.redireccionarPorFormulario(SessionUtil.getModulo(), form,
                        campos, valores, false);
        // </CODIGO_DESARROLLADO>
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

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            registro.getCampos().put(cCodigo,
                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                            tabla, " COMPANIA = ''" + compania
                                                + "'' ",
                                            cCodigo, "1"));

        }
        catch (SystemException e) {
            Logger.getLogger(TipoEventosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
