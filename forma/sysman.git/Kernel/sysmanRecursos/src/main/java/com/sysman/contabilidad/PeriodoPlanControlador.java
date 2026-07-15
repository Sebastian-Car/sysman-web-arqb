package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.PeriodoPlanControladorEnum;
import com.sysman.contabilidad.enums.PeriodoPlanControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author cmanrique
 * @version 1
 *
 * @author jrodrigueza
 * @version 2, 20/04/2017 Proceso de refactoring.
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class PeriodoPlanControlador extends BeanBaseModal {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania del usuario, el valor de esta constante es asignado en
     * el constructor a la variable de sesion correspondiente
     */
    private final String compania;
    /**
     * Atributo que almacena el anio ingresado en el formulario
     */
    private String anio;
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>

    /**
     * Crea una nueva instancia de PeriodoPlanControlador
     */
    public PeriodoPlanControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PERIODO_PLAN_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PeriodoPlanControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        anio = String.valueOf(SysmanFunciones.getParteFecha(new Date(),
                        Calendar.YEAR));
        cargarListaAno();
        // </CARGAR_LISTA>
        abrirFormulario();

    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(anio)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB0"));
            return;
        }
        else {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("anio", anio);
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.PLAN_CONTABLE_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            RequestContext.getCurrentInstance().closeDialog(direccionador);
        }
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(PeriodoPlanControladorEnum.PARAM0.getValue(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoPlanControladorUrlEnum.URL3404
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    // </METODOS_CARGAR_LISTA>

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    // <SET_GET_LISTAS>
    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
    // </SET_GET_LISTAS>

}
