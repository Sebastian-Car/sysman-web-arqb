package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroRemote;
import com.sysman.contabilidad.enums.CuadresaldosControladorEnum;
import com.sysman.contabilidad.enums.CuadresaldosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
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
 * @author dsuesca
 * @version 6, 19/05/2016 17:19:27 -- Modificado por dsuesca
 *
 * @version 7, 06/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Se aplico el Refactoring.<br>
 * Manejo de EJBs.
 * 
 * @author eamaya
 * @version 8, 26/05/2017, Se adicionaron los campos Mes Inicial y Mes
 * Final a la vista, modificación en el método oprimirAceptar()
 * 
 * @author eamaya
 * @version 9.0, 12/06/2017 Cambio código formulario
 * 
 */
@ManagedBean
@ViewScoped
public class CuadresaldosControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String anio;
    private String mesInicial;
    private String mesFinal;
    private boolean mostrarDialogo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMesInicial;
    private List<Registro> listaMesFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbContabilidadCeroRemote ejbContabilidadCero;

    /**
     * Creates a new instance of CuadresaldosControlador
     */
    public CuadresaldosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CUADRESALDOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            anio = String.valueOf(SysmanFunciones.ano(new Date()));
            mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
            mesFinal = String.valueOf(SysmanFunciones.mes(new Date()) + 1);
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(CuadresaldosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>

        cargarListaAno();
        cargarListaMesInicial();
        cargarListaMesFinal();
        // </CARGAR_LISTA>

        abrirFormulario();

        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR704-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 1, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CuadresaldosControladorUrlEnum.URL3216
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CuadresaldosControladorUrlEnum.URL3491
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(CuadresaldosControladorEnum.PARAM0.getValue(), mesInicial);

        try {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CuadresaldosControladorUrlEnum.URL3829
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        try {

            // PCK_CONTABILIDAD.PR_CUADRECONTA_AUX
            ejbContabilidadCero.corregirSaldosAuxiliaresContables(compania,
                            Integer.parseInt(anio),
                            Integer.parseInt(mesInicial),
                            Integer.parseInt(mesFinal), "0",
                            SysmanConstantes.CONS_MAX_ID);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException
                        | SystemException e) {
            Logger.getLogger(CuadresaldosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mesInicial = mesFinal = null;
        cargarListaMesInicial();
        cargarListaMesFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMesInicial() {
        // <CODIGO_DESARROLLADO>
        mesFinal = null;

        if (!SysmanFunciones.validarVariableVacio(mesInicial)) {
            cargarListaMesFinal();
        }
        else {
            listaMesFinal = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

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

    // </SET_GET_ATRIBUTOS>
    public boolean isMostrarDialogo() {
        return mostrarDialogo;
    }

    public void setMostrarDialogo(boolean mostrarDialogo) {
        this.mostrarDialogo = mostrarDialogo;
    }

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }

    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
    }

    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }

    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
