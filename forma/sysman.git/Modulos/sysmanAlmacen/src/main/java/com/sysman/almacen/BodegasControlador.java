package com.sysman.almacen;

import com.sysman.almacen.enums.BodegasControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author vmolano
 * @version 1, 02/02/2016
 * @version 2, 26/04/2017 modificado por jcrodriguez
 * descripcion:--depuracion del controaldor --creacion de dss para el
 * formulario
 */
@ManagedBean
@ViewScoped
public class BodegasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * variable que almacena la compañia
     */
    private final String compania;
    /**
     * variable que almacena el nombre de la bodega
     */
    private static final String NOMBODEGA = "NOMBODEGA";
    /**
     * variable que lista los textos
     */
    private List<Registro> listaTexto8;
    /**
     * variable que permite que carguen los auxiliares
     */
    private boolean cargaAux;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of BodegasControlador
     */
    public BodegasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.BODEGAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(BodegasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * metodo que se llama al inicializar el formulario
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BODEGA;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaTexto8();
        abrirFormulario();
        
        try {
			cargaAux = "SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA SALDO POR BODEGA Y AUXILIARES EN ALMACEN", SessionUtil.getModulo(), new Date(),
			        true),"NO"));
		} catch (SystemException e) {
			e.printStackTrace();
		}
    }

    /**
     * metodo que se llama al resignar el origen
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    /**
     * metodo que carga la lista
     */
    public void cargarListaTexto8() {
        try {
            listaTexto8 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BodegasControladorUrlEnum.URL3449
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se llama cuando se cancela la edicion
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo utilizado para remover objetos
     */
    @Override
    public void removerCombos() {
        // NO SE IMPLEMENTA
        registro.getCampos().remove(NOMBODEGA);
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_CREATED.getName());
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo get y set
     */
    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    public List<Registro> getListaTexto8() {
        return listaTexto8;
    }

    public void setListaTexto8(List<Registro> listaTexto8) {
        this.listaTexto8 = listaTexto8;
    }
    
	public boolean isCargaAux() {
		return cargaAux;
	}

	public void setCargaAux(boolean cargaAux) {
		this.cargaAux = cargaAux;
	}
}
