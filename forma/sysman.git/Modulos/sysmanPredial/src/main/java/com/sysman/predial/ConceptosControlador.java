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
import com.sysman.predial.enums.ConceptosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author acaceres
 * @version 1, 23/05/2016
 *
 * @author JGuerrero
 * @version 2.0, 27/07/2017 Se realiza refactoring al controlador.
 */
@ManagedBean
@ViewScoped
public class ConceptosControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    private final String modulo;

    /** Constante a nivel de clase que aloja el valor MINIMO */
    private final String minimo;

    /** Constante a nivel de clase que aloja el valor MINIMO1 */
    private final String minimoUno;

    /** Constante a nivel de clase que aloja el valor VIGANT */
    private final String vigant;

    /** Constante a nivel de clase que aloja el valor VIGDFR */
    private final String vigdfr;

    /** Constante a nivel de clase que aloja el valor PRIORIDAD_ABON_EN_ACU */
    private final String prioridadAbonEnAcu;

    /**
     * Constante definida para almacenar la cadena "NUMERO"
     */
    private final String cNumero;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    // <DECLARAR_ATRIBUTOS>
    private int ano;
    private String mesAdministia;
    private boolean informativoVisible;
    private boolean prioridadVisible;
    // </DECLARAR_ATRIBUTOS>

    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ConceptosControlador
     */
    public ConceptosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        informativoVisible = false;
        prioridadVisible = false;

        minimo = "MINIMO";
        minimoUno = "MINIMO1";
        vigant = "VIGANT";
        vigdfr = "VIGDFR";
        cNumero = "NUMERO";
        prioridadAbonEnAcu = "PRIORIDAD_ABON_EN_ACU";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.CONCEPTOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(ConceptosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        tabla = GenericUrlEnum.IP_CONCEPTOS.getTable();
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
    public void reasignarOrigen()
    {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosControladorUrlEnum.URL7445
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getUrlBeanById(
                        ConceptosControladorUrlEnum.URL0003
                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosControladorUrlEnum.URL0001
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosControladorUrlEnum.URL0002
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAno = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ConceptosControladorUrlEnum.URL6565.getValue()).getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>

        mesAdministia = service.buscarEnLista(String.valueOf(ano), cNumero,
                        "MESESAMNISTIA_PREDIAL", listaAno);

        reasignarOrigen();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        ano = SysmanFunciones.ano(new Date());
        cargarListaAno();
        mesAdministia = service.buscarEnLista(String.valueOf(ano), cNumero,
                        "MESESAMNISTIA_PREDIAL", listaAno);
        reasignarOrigen();

        try
        {
            // Si el parametro tiene valor SI, se muestra la colmuna de prioridad en abonos
            String parManejaPrioridad = SysmanFunciones.nvlStr(ejbSysmanUtl
                            .consultarParametro(compania,
                                            "MANEJA PRIORIDAD DE ABONOS DE ACUERDOS EN CONF CONSULTAS",
                                            modulo, new Date(), false),
                            "");

            if (parManejaPrioridad == null)
            {
                parManejaPrioridad = "";
            }

            if ("SI".equals(parManejaPrioridad))
            {
                prioridadVisible = true;
                registro.getCampos().put(prioridadAbonEnAcu, "");
            }
            else
            {
                prioridadVisible = false;
                registro.getCampos().put(prioridadAbonEnAcu, 0);
            }
        }
        catch (SystemException e)
        {
            Logger.getLogger(ConceptosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());
        }

        String siglaCompania = SessionUtil.getCompaniaIngreso().getSigla();

        if (siglaCompania.equals(idioma.getString("TB_TB2760")))
        {
            informativoVisible = true;
        }
        else
        {
            informativoVisible = false;
        }

        registro.getCampos().put(minimo, 0);
        registro.getCampos().put(minimoUno, 0);
        registro.getCampos().put(vigant, 0);
        registro.getCampos().put(vigdfr, 0);

        /*
         * FR760-AL_ABRIR Private Sub Form_Open(Cancel As Integer) formularioAbrir 60, Me.Name If GetNivel > 6 Then Me!Ano_Conceptos.Locked = False End If End Sub
         *//*
           * FR760-AL_ABRIR Private Sub Form_Load() Me!Estado = Me!ANO.Column(1) Me!MesesAmnistia = Me!ANO.Column(2) End Sub
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().put(prioridadAbonEnAcu, 0);

        // </CODIGO_DESARROLLADO>
        return true;

    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(minimo, 0);
        registro.getCampos().put(minimoUno, 0);
        registro.getCampos().put(vigant, 0);
        registro.getCampos().put(vigdfr, 0);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>

        if (!validarNulos())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB173"));
            return false;
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean validarNulos()
    {
        boolean key = true;

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        minimoUno)
            || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            GeneralParameterEnum.NOMBRE.getName())
            || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            vigant))
        {
            key = false;
        }

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        minimo)
            || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            vigdfr))
        {
            key = false;
        }

        return key;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
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
        // Codigo
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.VALOR.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos().remove(GeneralParameterEnum.PORCENTAJE.getName());
        registro.getCampos().remove("CAUSACIONDB");
        registro.getCampos().remove("CAUSACIONCR");
        registro.getCampos().remove("RECAUDODB");
        registro.getCampos().remove("TABLAAMODIFICAR");
        registro.getCampos().remove("CAMPOAMODIFICAR");
        registro.getCampos().remove("RECAUDOCR");
        registro.getCampos().remove("FORMULA_COPIA");
        registro.getCampos().remove("INFORMATIVO");
        registro.getCampos().remove("PRORATIAR");
        registro.getCampos().remove("SUMAORESTA");
        registro.getCampos().remove("PRIORIDAD_ABON_EN_ACU");
    }

    @Override
    public void asignarValoresRegistro()
    {
        registro.getCampos().put(minimo, 0);
        registro.getCampos().put(minimoUno, 0);
        registro.getCampos().put(vigant, 0);
        registro.getCampos().put(vigdfr, 0);
        registro.getCampos().put(prioridadAbonEnAcu, 0);

    }

    // <SET_GET_ATRIBUTOS>

    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public String getMesAdministia()
    {
        return mesAdministia;
    }

    public void setMesAdministia(String mesAdministia)
    {
        this.mesAdministia = mesAdministia;
    }

    // </SET_GET_ATRIBUTOS>

    public boolean isInformativoVisible()
    {
        return informativoVisible;
    }

    public void setInformativoVisible(boolean informativoVisible)
    {
        this.informativoVisible = informativoVisible;
    }

    public boolean isPrioridadVisible()
    {
        return prioridadVisible;
    }

    public void setPrioridadVisible(boolean prioridadVisible)
    {
        this.prioridadVisible = prioridadVisible;
    }

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

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
