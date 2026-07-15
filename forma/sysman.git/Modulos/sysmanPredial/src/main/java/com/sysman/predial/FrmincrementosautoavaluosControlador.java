package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.FrmincrementosautoavaluosControladorEnum;
import com.sysman.predial.enums.FrmincrementosautoavaluosControladorUrlEnum;
import com.sysman.util.SysmanConstantes;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dmaldonado
 * @version 2, 24/05/2016 08:19:34 -- Modificado por dmaldonado
 * @author asana
 * @version 3, 10/07/2017 Se realiza refactoring.
 */
@ManagedBean
@ViewScoped
public class FrmincrementosautoavaluosControlador
                extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmincrementosautoavaluosControlador
     */
    public FrmincrementosautoavaluosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMINCREMENTOSAUTOAVALUOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmincrementosautoavaluosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        tabla = FrmincrementosautoavaluosControladorEnum.PARAM0.getValue();
        buscarLlave();
        reasignarOrigen();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmincrementosautoavaluosControladorUrlEnum.URL18760
                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmincrementosautoavaluosControladorUrlEnum.URL18761
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirbtnactualizar() {
        // <CODIGO_DESARROLLADO>
        long resultado = 0;
        try {
            resultado = ejbPredialOcho.actualizarIncrAutoavaluo(compania,
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL,
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB973") + " "
                + resultado + " " + idioma.getString("TB_TB974"));
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtngenerar() {
        // <CODIGO_DESARROLLADO>
        boolean result;
        try {
            result = ejbPredialOcho.generarAutoavaluo(compania,
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL,
                            SessionUtil.getCompaniaIngreso().getNombre());

            if (result) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB975"));
            }
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

    }

    // </CODIGO_DESARROLLADO>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
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
        registro.getCampos().remove("CODIGO");
        registro.getCampos().remove("NUMERO_ORDEN");
        registro.getCampos().remove("ANO");
        registro.getCampos().remove("AVALUO_ANT");
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove("NUM_RADICACION");
        registro.getCampos().remove("AVALUO");
        registro.getCampos().remove("AUTOAVAL");

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
        // Metodo no implementado
    }

    @Override
    public void asignarValoresRegistro() {

        // Metodo no implementado
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
