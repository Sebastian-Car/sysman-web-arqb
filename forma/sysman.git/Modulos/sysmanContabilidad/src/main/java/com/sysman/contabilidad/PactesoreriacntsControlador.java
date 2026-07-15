package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.PactesoreriacntsControladorEnum;
import com.sysman.contabilidad.enums.PactesoreriacntsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 4, 11/05/2016 16:49:48 -- Modificado por dmaldonado
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 11/04/2017
 * 
 * @version 3.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Remover y/o reemplazar los llamados a ConectorPool por el esquema.
 */
@ManagedBean
@ViewScoped
public class PactesoreriacntsControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    /**
     * Constante definida que almacena el valor de
     * GeneralParameterEnum.CODIGO.getName()
     */
    private final String cCodigo;
    /**
     * Constante definida que almacena el valor de
     * PactesoreriacntsControladorEnum.PARAM0.getValue()
     */
    private final String cValorCredito;
    /**
     * Constante definida que almacena el valor de
     * PactesoreriacntsControladorEnum.PARAM1.getValue()
     */
    private final String cValorDebito;
    /**
     * Constante definida que almacena el valor de
     * PactesoreriacntsControladorEnum.PARAM2.getValue()
     */
    private final String cSaldoNeto;
    // <DECLARAR_ATRIBUTOS>
    private String titulo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuenta;
    private RegistroDataModelImpl listaCuentaE;
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String ano;
    private String tipoComp;
    private String nombreComp;
    private Date fechaComp;
    private String numeroComp;
    @EJB
    private EjbSysmanUtilRemote ejbContabilidadCero;

    /**
     * Creates a new instance of PactesoreriacntsControlador
     */
    public PactesoreriacntsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cValorCredito = PactesoreriacntsControladorEnum.PARAM0.getValue();
        cValorDebito = PactesoreriacntsControladorEnum.PARAM1.getValue();
        cSaldoNeto = PactesoreriacntsControladorEnum.PARAM2.getValue();

        try
        {
            // 695
            numFormulario = GeneralCodigoFormaEnum.PACTESORERIACNTS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(PactesoreriacntsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init()
    {
        enumBase = GenericUrlEnum.PACTESORERIA;

        buscarLlave();
        cargarFlash();
        reasignarOrigen();

        registro = new Registro();
        registro.getCampos().put(cValorCredito, 0);
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuenta();
        cargarListaCuentaE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    public void cargarFlash()
    {
        try
        {
            Map<String, Object> parametrosEntrada = SessionUtil
                            .getFlashLocal();
            if (parametrosEntrada != null)
            {
                ano = parametrosEntrada.get("ano").toString();
                tipoComp = parametrosEntrada.get("tipoComp").toString();
                nombreComp = parametrosEntrada.get("nombreComp").toString();
                fechaComp = (Date) parametrosEntrada.get("fechaComp");
                numeroComp = parametrosEntrada.get("numeroComp").toString();
            }

            titulo = nombreComp + " -  NO. " + numeroComp;
        }
        catch (Exception e)
        {
            Logger.getLogger(PactesoreriacntsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put(GeneralParameterEnum.TIPO_CPTE.getName(),
                        tipoComp);
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(),
                        numeroComp);

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCuenta()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PactesoreriacntsControladorUrlEnum.URL5403
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void cargarListaCuentaE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PactesoreriacntsControladorUrlEnum.URL6439
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        listaCuentaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarValorDebito()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .put(cSaldoNeto,
                                        SysmanFunciones.nvlDbl(
                                                        registro.getCampos()
                                                                        .get(cValorDebito),
                                                        0)
                                            - SysmanFunciones.nvlDbl(
                                                            registro.getCampos()
                                                                            .get(cValorCredito),
                                                            0));
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorCredito()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .put(cSaldoNeto,
                                        SysmanFunciones.nvlDbl(
                                                        registro.getCampos()
                                                                        .get(cValorDebito),
                                                        0)
                                            - SysmanFunciones.nvlDbl(
                                                            registro.getCampos()
                                                                            .get(cValorCredito),
                                                            0));
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorDebitoC(int rowNum)
    {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        cSaldoNeto,
                        SysmanFunciones.nvlDbl(listaInicial.getDatasource()
                                        .get(rowNum % 10).getCampos()
                                        .get(cValorDebito), 0)
                            - SysmanFunciones.nvlDbl(
                                            listaInicial.getDatasource()
                                                            .get(rowNum % 10)
                                                            .getCampos()
                                                            .get(cValorCredito),
                                            0));

        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorCreditoC(int rowNum)
    {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        cSaldoNeto,
                        SysmanFunciones.nvlDbl(listaInicial.getDatasource()
                                        .get(rowNum % 10).getCampos()
                                        .get(cValorDebito), 0)
                            - SysmanFunciones.nvlDbl(
                                            listaInicial.getDatasource()
                                                            .get(rowNum % 10)
                                                            .getCampos()
                                                            .get(cValorCredito),
                                            0));

        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuenta(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA",
                        registroAux.getCampos().get(cCodigo));
    }

    public void seleccionarFilaCuentaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cCodigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR695-AL_ABRIR Private Sub Form_Load() DoCmd.Restore End
         * Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR695-ANTES_INSERTAR Private Sub Form_BeforeInsert(Cancel
         * As Integer) Anterior End Sub
         */
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().put(
                        PactesoreriacntsControladorEnum.PARAM3.getValue(),
                        tipoComp);
        registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                        numeroComp);
        registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                        fechaComp);

        try
        {
            String[] parametros = { "COMPANIA = ''", compania,
                                    "'' AND TIPO = ''", tipoComp,
                                    "'' AND NUMERO = ", numeroComp, "" };
            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            ejbContabilidadCero
                                            .generarConsecutivoConValorInicial(
                                                            "PACTESORERIA",
                                                            SysmanFunciones.concatenar(
                                                                            parametros),
                                                            GeneralParameterEnum.CONSECUTIVO
                                                                            .getName(),
                                                            "1"));

        }
        catch (SystemException e)
        {

            Logger.getLogger(PactesoreriacntsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro.getCampos().remove(cSaldoNeto);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        param.put(GeneralParameterEnum.MES.getName(),
                        SysmanFunciones.mes(fechaComp));
        param.put(GeneralParameterEnum.CUENTA.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.CUENTA.getName()));
        try
        {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PactesoreriacntsControladorUrlEnum.URL397
                                                                            .getValue())
                                            .getUrl(), param));

            if (!rs.getCampos().isEmpty())
            {
                int totalPac = (int) rs.getCampos().get("TOTALPAC");
                int ejecucionCnt = (int) rs.getCampos()
                                .get("EJECUCIONCNT");
                int valorDebito = Integer.parseInt(registro.getCampos()
                                .get("VALOR_DEBITO").toString());
                if ((totalPac - ejecucionCnt) < valorDebito)
                {
                    int saldoDisponible = totalPac - ejecucionCnt;
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB3330")
                                    .replace("s$saldoDisponibles$s",
                                                    Integer.toString(
                                                                    saldoDisponible)));
                    return false;
                }
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3331"));
                return false;
            }
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        /*
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>

        double valorDebito = (double) registro.getCampos().get("VALOR_DEBITO");

        double valorCredito = (double) registro.getCampos()
                        .get("VALOR_CREDITO");

        if (valorDebito > 0.0)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3328"));
            return false;
        }
        if (valorCredito > 0.0)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3329"));
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(
                        PactesoreriacntsControladorEnum.PARAM3.getValue());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
        registro.getCampos().remove(GeneralParameterEnum.FECHA.getName());
        registro.getCampos().remove(
                        PactesoreriacntsControladorEnum.PARAM2.getValue());

        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cValorCredito, 0);
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCuenta()
    {
        return listaCuenta;
    }

    public void setListaCuenta(RegistroDataModelImpl listaCuenta)
    {
        this.listaCuenta = listaCuenta;
    }

    public RegistroDataModelImpl getListaCuentaE()
    {
        return listaCuentaE;
    }

    public void setListaCuentaE(RegistroDataModelImpl listaCuentaE)
    {
        this.listaCuentaE = listaCuentaE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
