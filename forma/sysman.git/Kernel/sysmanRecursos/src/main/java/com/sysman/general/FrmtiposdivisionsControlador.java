package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.FormContinuoService;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author lcortes
 * @version 1, 18/09/2015
 *
 *
 * @author ybecerra
 * @version 2, 03/04/2017 Revision Sonar y Refactoring
 *
 * --Modificado por lcortes 12/06/2017. Se reemplaza el valor de la
 * variable numero de formulario por el enumerado correspondiente.
 */
@ManagedBean
@ViewScoped
public class FrmtiposdivisionsControlador extends BeanBaseContinuoAcmeImpl {

	/**
	 * variable que permite la visibilidad del botón Recursos Naturales
	 */
	private boolean manejaRecursosNat;
	
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	/**
     * Creates a new instance of FrmtiposdivisionsControlador
     */
    public FrmtiposdivisionsControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.FRMTIPOSDIVISIONS_CONTROLADOR
                        .getCodigo();
        try {
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(FrmtiposdivisionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPOSDIVISION;
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
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    @Override
    public void abrirFormulario() {
    	try {
			// CC3345 mperez - Permite verificar si se debe mostrar la columna "Es Cuenca"
			setManejaRecursosNat("SI".equals(SysmanFunciones
						.nvl(ejbSysmanUtil.consultarParametro(SessionUtil.getCompania(), "ENTIDAD MANEJA CUENCAS Y RECURSOS",
								SessionUtil.getModulo(), new Date(), true), "NO")));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

	public boolean isManejaRecursosNat() {
		return manejaRecursosNat;
	}

	public void setManejaRecursosNat(boolean manejaRecursosNat) {
		this.manejaRecursosNat = manejaRecursosNat;
	}
}
