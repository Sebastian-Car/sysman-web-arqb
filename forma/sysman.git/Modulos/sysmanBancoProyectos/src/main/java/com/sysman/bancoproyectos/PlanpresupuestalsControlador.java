package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.PlanpresupuestalsControladorEnum;
import com.sysman.bancoproyectos.enums.PlanpresupuestalsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 24/05/2016
 * 
 * @version 2, 27/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla.
 * 
 */

@ManagedBean
@ViewScoped
public class PlanpresupuestalsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private String anio;
    private String auxiliar;
    private List<Registro> listaCuadroCombinado289;

    /**
     * Creates a new instance of PlanpresupuestalsControlador
     */
    public PlanpresupuestalsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PLANPRESUPUESTALS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PlanpresupuestalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
            tabla = PlanpresupuestalsControladorEnum.TABLA.getValue();
            anio = String.valueOf(SysmanFunciones.getParteFecha(
                            new Date(),
                            Calendar.YEAR));
            reasignarOrigen();
            registro = new Registro();
            cargarListaCuadroCombinado289();
            abrirFormulario();
        
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        PlanpresupuestalsControladorUrlEnum.URL10567.getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
    }

    public void cargarListaCuadroCombinado289() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            listaCuadroCombinado289 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlanpresupuestalsControladorUrlEnum.URL7133
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    
    public void cambiarCuadroCombinado289() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCuadroCombinado289C(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
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
    public void removerCombos() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }
    
    public List<Registro> getListaCuadroCombinado289() {
        return listaCuadroCombinado289;
    }

    public void setListaCuadroCombinado289(List<Registro> listaCuadroCombinado289) {
        this.listaCuadroCombinado289 = listaCuadroCombinado289;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

}