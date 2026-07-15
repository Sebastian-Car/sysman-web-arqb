package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroRemote;
import com.sysman.presupuesto.ejb.EjbPresupuestoDosRemote;
import com.sysman.presupuesto.enums.CuadresaldosptoControladorUrlEnum;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author NGOMEZ
 * @version 1, 14/06/2016
 * @modified jsforero
 * @version 2. 18/04/2017 Se realizo el refactory.
 * 
 * @author jlramirez
 * @version 3, 24/04/2017, Manejo de EJBs
 */
@ManagedBean
@ViewScoped
public class CuadresaldosptoControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String mesInicial;
    private String mesFinal;
    private String anio;
    @EJB
    private EjbPresupuestoCeroRemote presupuestoCero;
    @EJB
    private EjbPresupuestoDosRemote presupuestoDos;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of CuadresaldosptoControlador
     */
    public CuadresaldosptoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CUADRESALDOSPTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(CuadresaldosptoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        UrlBean urlLisA = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuadresaldosptoControladorUrlEnum.URL2586
                                                        .getValue());
        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(urlLisA.getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(CuadresaldosptoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        try {
            if (!presupuestoCero.verificarPeriodoPresupuestal(
                            compania, Integer.parseInt(anio),
                            Integer.parseInt(mesInicial))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB446"));
                return;
            }
            if (!presupuestoCero.verificarPeriodoPresupuestal(
                            compania, Integer.parseInt(anio),
                            Integer.parseInt(mesFinal))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB447"));
                return;
            }
            presupuestoDos.cuadrarSaldosPpto(compania,
                            Integer.parseInt(mesInicial),
                            Integer.parseInt(mesFinal), Integer.parseInt(anio));
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1089"));

        }
        catch (SystemException e) {
            Logger.getLogger(CuadresaldosptoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
