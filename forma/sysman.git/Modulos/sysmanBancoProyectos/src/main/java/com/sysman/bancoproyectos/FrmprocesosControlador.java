package com.sysman.bancoproyectos;

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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 12/08/2015
 * 
 * @version 2, 25/09/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 * <li>Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class FrmprocesosControlador extends BeanBaseContinuoAcmeImpl
{

    /** Atributo que contiene la cadena: <code>CONSECUTIVO</code> */
    private final String cConsecutivo = GeneralParameterEnum.CONSECUTIVO
                    .getName();

    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FrmprocesosControlador
     */
    public FrmprocesosControlador()
    {
        super();

        try
        {
            // 112
            numFormulario = GeneralCodigoFormaEnum.FRMPROCESOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            logger.error(ex.getMessage(), ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PALABRAS;
        registro = new Registro();

        buscarLlave();
        reasignarOrigen();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        generarConsecutivo();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
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
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        generarConsecutivo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Asigna el siguiente valor consecutivo ascendente del campo:
     * <code>CONSECUTIVO</code> de la tabla: <code>PALABRAS</code> al
     * campo consecutivo en la forma.
     */
    private void generarConsecutivo() {
        try
        {
            long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            enumBase.getTable(), "", cConsecutivo, "1");

            registro.getCampos().put(cConsecutivo, consecutivo);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
}