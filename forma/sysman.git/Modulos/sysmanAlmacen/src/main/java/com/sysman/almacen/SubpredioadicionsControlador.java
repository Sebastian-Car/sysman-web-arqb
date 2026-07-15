package com.sysman.almacen;

import com.sysman.almacen.enums.SubpredioadicionsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 23/02/2016
 * 
 * @author eamaya
 * @version 2, 09/05/2017 Proceso de Refactoring
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario
 * 
 */
@ManagedBean
@ViewScoped
public class SubpredioadicionsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String predio;

    /**
     * Creates a new instance of SubpredioadicionsControlador
     */
    public SubpredioadicionsControlador() {
        super();
        compania = SessionUtil.getCompania();

        numFormulario = GeneralCodigoFormaEnum.SUBPREDIOADICIONS_CONTROLADOR
                        .getCodigo();

        try {
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                predio = (String) parametrosEntrada.get("predio");
            }
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {

            SessionUtil.cleanFlash();
        }

    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.ADICIONES.getTable();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpredioadicionsControladorUrlEnum.URL6969
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.PREDIO.getName(), predio);

    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIA", compania);
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void removerCombos() {
        // Metodo heredado de la clase BeanBase
    }
}
