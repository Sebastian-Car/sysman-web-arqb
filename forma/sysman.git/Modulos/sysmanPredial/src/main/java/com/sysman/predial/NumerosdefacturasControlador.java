package com.sysman.predial;

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
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.NumerosdefacturasControladorEnum;
import com.sysman.predial.enums.NumerosdefacturasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
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
 * @author dsuesca
 * @version 1, 24/05/2016
 *
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 *
 * @version 3, 10/07/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class NumerosdefacturasControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String act;
    private final String consec;
    private final String secuen;
    // <DECLARAR_ATRIBUTOS>
    private boolean visibleTipo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private List<Registro> listaTIPO;
    private boolean advertenciaVisible;
    private int indice;

    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    private String tipo;
    private String consecutivo;
    private String nombreLista;
    private Registro registroAct;

    /**
     * Creates a new instance of NumerosdefacturasControlador
     */
    public NumerosdefacturasControlador() {
        super();
        compania = SessionUtil.getCompania();
        act = "ACTIVO";
        consec = GeneralParameterEnum.CONSECUTIVO.getName();
        secuen = "SECUENCIA";
        try {
            numFormulario = GeneralCodigoFormaEnum.NUMEROSDEFACTURAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(NumerosdefacturasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.IP_NUMEROSDEFACTURA;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaTIPO();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTIPO() {
        try {
            listaTIPO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NumerosdefacturasControladorUrlEnum.URL4436
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarActivo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarActivoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (Boolean.parseBoolean(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(act).toString())) {
            advertenciaVisible = true;
        }
        else {
            listaInicial.getDatasource().get(rowNum).getCampos().put(act,
                            true);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1016"));
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void aceptaradvertencia() {
        // <CODIGO_DESARROLLADO>
        try {
            if (ejbPredialOcho.validarConsecutivo(compania,
                            new BigInteger(registroAct.getCampos()
                                            .get(secuen)
                                            .toString()),
                            consecutivo,
                            tipo)
                && ejbPredialOcho.actualizarIndicadorActivo(compania,
                                tipo,
                                (boolean) registroAct.getCampos()
                                                .get(act),
                                SessionUtil.getUser().getCodigo())) {
                registroAct.getCampos()
                                .put(NumerosdefacturasControladorEnum.CONSECUTIVOREAL
                                                .getValue(), consecutivo);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        advertenciaVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cancelaradvertencia() {
        // <CODIGO_DESARROLLADO>
        advertenciaVisible = false;
        // </CODIGO_DESARROLLADO>
    }

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
        registro.getCampos().put(act, true);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.USUARIO.getName(),
                        SessionUtil.getUser().getCodigo());
        registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                        new Date());
        try {
            registro.getCampos().put("HORA",
                            SysmanFunciones.convertirHoraAFecha(
                                            SysmanFunciones.convertirAFechaCadena(
                                                            new Date(),
                                                            "HH:mm")));
            registro.getCampos().put(secuen,
                            ejbSysmanUtil.generarSiguienteConsecutivo(
                                            GenericUrlEnum.IP_NUMEROSDEFACTURA
                                                            .getTable(),
                                            SysmanFunciones.concatenar(
                                                            " COMPANIA = ''",
                                                            compania,
                                                            "''"),
                                            secuen));

            if (ejbPredialOcho.validarConsecutivo(compania,
                            new BigInteger(registro.getCampos()
                                            .get(secuen)
                                            .toString()),
                            registro.getCampos()
                                            .get(consec)
                                            .toString(),
                            registro.getCampos()
                                            .get("TIPO")
                                            .toString())
                && ejbPredialOcho.actualizarIndicadorActivo(compania,
                                registro.getCampos().get("TIPO").toString(),
                                (boolean) registro.getCampos()
                                                .get(act),
                                SessionUtil.getUser().getCodigo())) {
                registro.getCampos()
                                .put(NumerosdefacturasControladorEnum.CONSECUTIVOREAL
                                                .getValue(),
                                                registro.getCampos()
                                                                .get(consec)
                                                                .toString());
            }
            else {
                return false;
            }
        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }
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
        if ((registroAct != null) && !registroAct.getCampos().isEmpty()) {
            registro = new Registro(registroAct.getCampos());
        }
        nombreLista = registro.getCampos().get("NOMBRETIPO") == null
            ? service.buscarEnLista(
                            registro.getCampos().get("TIPO").toString(),
                            "CODIGO",
                            "NOMBRE", listaTIPO)
            : registro.getCampos().get("NOMBRETIPO").toString();
        registro.getCampos().remove("NOMBRETIPO");
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeInformativoDialogo(
                        SysmanFunciones.concatenar(
                                        idioma.getString("TB_TB1017"), " ",
                                        nombreLista));
        registroAct = new Registro();
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(secuen);
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
        registro.getCampos().remove(GeneralParameterEnum.USUARIO.getName());
        registro.getCampos().remove(GeneralParameterEnum.FECHA.getName());
        registro.getCampos().remove("HORA");
        registro.getCampos()
                        .remove(NumerosdefacturasControladorEnum.CONSECUTIVOREAL
                                        .getValue());
        registro.getCampos().remove("TIPO");

    }

    public void activarEdicion(Registro registroAux) {
        indice = listaInicial.getRowIndex();
        tipo = registroAux.getCampos().get("TIPO").toString();
        consecutivo = registroAux.getCampos().get(consec).toString();
        registroAct = new Registro(registroAux.getCampos());
        nombreLista = service.buscarEnLista(
                        registro.getCampos()
                                        .get("TIPO")
                                        .toString(),
                        "CODIGO", "NOMBRE",
                        listaTIPO);
    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA
    }

    // <SET_GET_ATRIBUTOS>

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public boolean isVisibleTipo() {
        return visibleTipo;
    }

    public void setVisibleTipo(boolean visibleTipo) {
        this.visibleTipo = visibleTipo;
    }

    public boolean isAdvertenciaVisible() {
        return advertenciaVisible;
    }

    public void setAdvertenciaVisible(boolean advertenciaVisible) {
        this.advertenciaVisible = advertenciaVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaTIPO() {
        return listaTIPO;
    }

    public void setListaTIPO(List<Registro> listaTIPO) {
        this.listaTIPO = listaTIPO;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
