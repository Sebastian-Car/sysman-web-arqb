package com.sysman.almacen;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ngomez
 * @version 1, 19/10/2015
 * @version 2, 10/05/2017 - spina se refactoriza para dss
 */
@ManagedBean
@ViewScoped
public class UnidadControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    private String rid;
    private static final String UNIDAD = "UNIDAD";

    /**
     * Creates a new instance of UnidadControlador
     */
    public UnidadControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.UNIDAD_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.UNIDAD;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public void cambiarUnidad() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(UNIDAD, registro.getCampos().get(UNIDAD)
                        .toString().replace('\'', 'p'));
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarUnidadC(int rowNum) {

        String aux = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(UNIDAD).toString().replace('\'', 'p');
        String aux2 = aux.replace('\"', 'p');
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(UNIDAD,
                        aux);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(UNIDAD,
                        aux2);
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
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().put(UNIDAD, registro.getCampos().get(UNIDAD)
                        .toString().replace('\'', 'p'));
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_CREATED.getName());
        registro.getCampos().remove(GeneralParameterEnum.MODIFIED_BY.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_MODIFIED.getName());

        return true;
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        if ("100101".equals(SessionUtil.getMenuActual())
            || "400101".equals(SessionUtil.getMenuActual())) {

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.INVENTARIOS_CONTROLADOR
                                            .getCodigo()));
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            SessionUtil.cleanFlash();
            SessionUtil.redireccionarMenuPermisos();
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // heredado del bean base
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
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
        // heredado del bean base
    }

    public String getCompania() {
        return compania;
    }

}
