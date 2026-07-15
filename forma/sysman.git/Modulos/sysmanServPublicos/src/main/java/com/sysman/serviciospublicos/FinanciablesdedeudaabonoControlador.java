package com.sysman.serviciospublicos;

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
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.enums.FinanciablesdedeudaabonoControladorUrlEnum;
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

import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 09/09/2016
 *
 * -- Modificado por lcortes 23/05/2017. Refactorizacion de codigo de
 * las listas para utilizar dss. Reemplazo de llamados a la clase
 * Acciones por ejb respectivo.
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class FinanciablesdedeudaabonoControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String codigoRuta;
    private String ciclo;
    private String vrAFinanciar;
    private Date fecha;
    private String noCuotas;
    private String txtValorCuota;
    private String txtNombres;
    private String consecutivo;
    private String txtCuotas;
    private String ano;
    private String periodo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaCmbCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoRuta;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbServiciosPublicosOchoRemote ejbFacturacionOcho;

    /**
     * Creates a new instance of FinanciablesdedeudaabonoControlador
     */
    public FinanciablesdedeudaabonoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FINANCIABLESDEDEUDAABONO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(
                            FinanciablesdedeudaabonoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaCmbCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoRuta();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCmbCiclo()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaCmbCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablesdedeudaabonoControladorUrlEnum.URL3055
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoRuta()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesdedeudaabonoControladorUrlEnum.URL3532
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdImprimir()
    {
        // <CODIGO_DESARROLLADO>
        if (Integer.parseInt(noCuotas) < Integer.parseInt(txtCuotas))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3175"));
            txtCuotas = null;
            return;
        }

        try
        {
            String rta = ejbFacturacionOcho.realizarAbonoCuotasFinanciable(
                            compania, Integer.valueOf(txtCuotas),
                            Integer.valueOf(ciclo), codigoRuta, periodo,
                            Integer.valueOf(ano),
                            SessionUtil.getUser().getCodigo(), consecutivo);

            if (Integer.parseInt(rta) > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1463"));
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3174"));
                return;
            }
        }
        catch (NumberFormatException | SystemException e)
        {
            Logger.getLogger(
                            FinanciablesdedeudaabonoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarCmbCiclo()
    {
        // <CODIGO_DESARROLLADO>
        codigoRuta = null;
        txtNombres = null;
        consecutivo = null;
        vrAFinanciar = null;
        fecha = null;
        noCuotas = null;
        txtValorCuota = null;
        txtCuotas = null;
        cargarListaCodigoRuta();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigoRuta(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        String aux;
        if (SysmanFunciones.validarVariableVacio(codigoRuta))
        {
            aux = null;
        }
        else
        {
            aux = codigoRuta;
        }
        codigoRuta = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGORUTA.getName())
                        .toString();

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
        Registro regAux;
        try
        {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablesdedeudaabonoControladorUrlEnum.URL4830
                                                                            .getValue())
                                            .getUrl(), param));

            if (("0").equals(regAux.getCampos()
                            .get(GeneralParameterEnum.CUENTA.getName())))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1464"));
                codigoRuta = aux;
                return;
            }

            if ((registroAux.getCampos().get("BANCOPERPROCESO") != null)
                && !("").equals(registroAux.getCampos().get("BANCOPERPROCESO")
                                .toString()))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1465"));
                codigoRuta = aux;
                return;
            }

            vrAFinanciar = registroAux.getCampos().get("TOTFACTURAPERACTUAL")
                            .toString();
            ano = registroAux.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()).toString();
            periodo = registroAux.getCampos()
                            .get(GeneralParameterEnum.PERIODO.getName())
                            .toString();
            txtNombres = registroAux.getCampos().get("EXPR1").toString();
            consecutivo = registroAux.getCampos().get("PRIMERODECONSECUTIVO")
                            .toString();
            if (registroAux.getCampos()
                            .get(GeneralParameterEnum.FECHA.getName()) != null)
            {
                fecha = (Date) registroAux.getCampos()
                                .get(GeneralParameterEnum.FECHA.getName());
            }
            noCuotas = registroAux.getCampos().get("NOCUOTAS").toString();
            txtValorCuota = registroAux.getCampos().get("VALORCUOTA")
                            .toString();

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCodigoRuta()
    {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta)
    {
        this.codigoRuta = codigoRuta;
    }

    public String getCiclo()
    {
        return ciclo;
    }

    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    public String getVrAFinanciar()
    {
        return vrAFinanciar;
    }

    public void setVrAFinanciar(String vrAFinanciar)
    {
        this.vrAFinanciar = vrAFinanciar;
    }

    public Date getFecha()
    {
        return fecha;
    }

    public void setFecha(Date fecha)
    {
        this.fecha = fecha;
    }

    public String getNoCuotas()
    {
        return noCuotas;
    }

    public void setNoCuotas(String noCuotas)
    {
        this.noCuotas = noCuotas;
    }

    public String getTxtValorCuota()
    {
        return txtValorCuota;
    }

    public void setTxtValorCuota(String txtValorCuota)
    {
        this.txtValorCuota = txtValorCuota;
    }

    public String getTxtNombres()
    {
        return txtNombres;
    }

    public void setTxtNombres(String txtNombres)
    {
        this.txtNombres = txtNombres;
    }

    public String getConsecutivo()
    {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    public String getTxtCuotas()
    {
        return txtCuotas;
    }

    public void setTxtCuotas(String txtCuotas)
    {
        this.txtCuotas = txtCuotas;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public String getPeriodo()
    {
        return periodo;
    }

    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaCmbCiclo()
    {
        return listaCmbCiclo;
    }

    public void setListaCmbCiclo(List<Registro> listaCmbCiclo)
    {
        this.listaCmbCiclo = listaCmbCiclo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCodigoRuta()
    {
        return listaCodigoRuta;
    }

    public void setListaCodigoRuta(RegistroDataModelImpl listaCodigoRuta)
    {
        this.listaCodigoRuta = listaCodigoRuta;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
