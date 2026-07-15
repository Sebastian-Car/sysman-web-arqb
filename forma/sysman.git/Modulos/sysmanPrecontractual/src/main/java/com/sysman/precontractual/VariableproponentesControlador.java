package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.ejb.EjbPrecontractualCeroRemote;
import com.sysman.precontractual.enums.VariableproponentesControladorEnum;
import com.sysman.precontractual.enums.VariableproponentesControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dcastro
 * @version 1, 04/12/2015
 * 
 * @version 2, 05/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla.
 * 
 */

@ManagedBean
@ViewScoped
public class VariableproponentesControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String consPrederterminado;
    private int indice;
    private final String precontractual;
    private List<Registro> listavrPredeterminado;
    private String vrPredeterminado;
    
    @EJB
    private EjbPrecontractualCeroRemote ejbPrecontractualCero;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of VariableproponentesControlador
     */
    public VariableproponentesControlador() {
        super();
        compania = SessionUtil.getCompania();
        precontractual = SessionUtil.getModulo();
        consPrederterminado="VRPREDETERMINADO";
        try {
            numFormulario = GeneralCodigoFormaEnum.VARIABLEPROPONENTES_CONTROLADOR.getCodigo();
            validarPermisos();
        } catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.VARIABLE_PROPONENTE;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public int getIndice() {
        return indice;
    }

    public void oprimirActualizarVariables() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbPrecontractualCero.actualizarVariablesPrponentes(compania,
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(VariableproponentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarvariable() {
        if ("Formula".equals(vrPredeterminado)) {
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.INPUTFORMULAVAR_CONTROLADOR.getCodigo()));
            SessionUtil.redireccionarForma(direccionador, precontractual);
        }
    }

    public void cambiartipo() {
        //<CODIGO_DESARROLLADO>
        vrPredeterminado = null;
        cargarListavrPredeterminado(registro);
        //</CODIGO_DESARROLLADO>
    }

    public void cambiartipoC(int rowNum) {
        cargarListavrPredeterminado(listaInicial.getDatasource().get(rowNum % 10));

    }

    public void cambiarvrPredeterminado() {
        vrPredeterminado = (String) registro.getCampos().get(consPrederterminado);
    }

    public void cambiarvrPredeterminadoC(int rowNum) {

        vrPredeterminado = (String) listaInicial.getDatasource().get(rowNum % 10).getCampos().get(consPrederterminado);

    }

    @Override
    public void abrirFormulario() {
        //<CODIGO_DESARROLLADO>

        //</CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                            tabla,
                                            " COMPANIA = ''" + compania + "''",
                                            GeneralParameterEnum.CODIGO.getName(), "1"));
        }
        catch (SystemException ex) {
            Logger.getLogger(VariableproponentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        //<CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRETIPO");

        vrPredeterminado = SysmanFunciones.nvl(registro.getCampos().get(consPrederterminado), " ").toString();
        switch (vrPredeterminado) {
        case "Año Actual":
            registro.getCampos().put(consPrederterminado, "TO_CHAR(SYSDATE,'YYYY')");
            break;
        case "Mes Actual":
            registro.getCampos().put(consPrederterminado, "TO_CHAR(SYSDATE,'MM')");
            break;

        case "Fecha Actual":
            registro.getCampos().put(consPrederterminado, "SYSDATE");
            break;
        case "Hora Actual":
            registro.getCampos().put(consPrederterminado, "TO_CHAR(SYSDATE,'HH24:MI:ss')");
            break;

        case "Formula":
            registro.getCampos().put(consPrederterminado, "=Formula('" + SessionUtil.getSessionVar("variable") + "')");
            break;
        case " ":
            registro.getCampos().put(consPrederterminado, " ");
            break;
        default:
            break;
        }

        //</CODIGO_DESARROLLADO>
        return true;
    }

    public List<Registro> getListavrPredeterminado() {
        return listavrPredeterminado;
    }

    public void setListavrPredeterminado(List<Registro> listavrPredeterminado) {
        this.listavrPredeterminado = listavrPredeterminado;
    }

    public void cargarListavrPredeterminado(Registro r) {
        int codUno = 0;
        int codDos = 0;
        if (r.getCampos().get(VariableproponentesControladorEnum.PARAM2
                        .getValue()) != null) {
            if ("3".equals(r.getCampos()
                            .get(VariableproponentesControladorEnum.PARAM2
                                            .getValue()))) {
                codUno = 4;
                codDos = 6;
            }
            else if ("4".equals(r.getCampos()
                            .get(VariableproponentesControladorEnum.PARAM2
                                            .getValue()))) {
                codUno = codDos = 0;
            }
            else if ("6".equals(r.getCampos()
                            .get(VariableproponentesControladorEnum.PARAM2
                                            .getValue()))) {
                codUno = codDos = 1;
            }
            else if ("5".equals(r.getCampos()
                            .get(VariableproponentesControladorEnum.PARAM2
                                            .getValue()))) {
                codUno = codDos = 2;
            }
            else if ("1".equals(r.getCampos()
                            .get(VariableproponentesControladorEnum.PARAM2
                                            .getValue()))) {
                codUno = codDos = 3;
            }

            try {
                Map<String, Object> param = new TreeMap<>();
                param.put(VariableproponentesControladorEnum.PARAM0.getValue(),
                                codUno);
                param.put(VariableproponentesControladorEnum.PARAM1.getValue(),
                                codDos);

                listavrPredeterminado = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                VariableproponentesControladorUrlEnum.URL3991
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    @Override
    public boolean actualizarDespues() {
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void asignarValoresRegistro() {
        //METODO NO IMPLEMENTADO
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
    }
}
