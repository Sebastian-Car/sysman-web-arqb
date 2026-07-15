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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.PrerequisitosetapasControladorEnum;
import com.sysman.precontractual.enums.PrerequisitosetapasControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author lcortes
 * @version 1, 22/12/2015
 * 
 * @author jcrodriguez,Refactoring y Depuracion
 * @version 2, 01/09/2017
 * 
 * @author amonroy, Redireccionamiento a formulario "Transacciones"
 * @version 3, 13/09/2017
 */
@ManagedBean
@ViewScoped
public class PrerequisitosetapasControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;

    private String tipoContrato;
    private String consecutivop;
    private String consecutivoDetalle;
    private String etapa;
    private String nombreEtapa;
    private String titulo;
    private RegistroDataModelImpl listaPRERREQUISITO;
    private RegistroDataModelImpl listaPRERREQUISITOE;
    private String auxiliar;
    private String tipoPrerrequisito;
    private String nomTipoPre;
    private String prerrequisito;
    private boolean modificar;

    private String estadoEtapa;
    /**
     * Estructura que almacena los parametros que han sido enviados
     * desde el formulario "Transaccion"(419)
     */
    private Map<String, Object> parametrosEntrada;

    /**
     * Creates a new instance of PrerequisitosetapasControlador
     */
    public PrerequisitosetapasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.PREREQUISITOSETAPAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            tipoContrato = validarParametroCadena(parametrosEntrada,
                            "tipoContrato");
            consecutivop = validarParametroCadena(parametrosEntrada,
                            "consecutivop");
            consecutivoDetalle = validarParametroCadena(parametrosEntrada,
                            "consecutivoDetalle");
            etapa = validarParametroCadena(parametrosEntrada, "etapa");
            nombreEtapa = validarParametroCadena(parametrosEntrada,
                            "nombreEtapa");
            modificar = Boolean.parseBoolean(validarParametroCadena(
                            parametrosEntrada, "modificar"));
            estadoEtapa = validarParametroCadena(parametrosEntrada,
                            "estadoEtapa");

        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @PostConstruct
    public void inicializar()
    {

        enumBase = GenericUrlEnum.PRE_REQUISITOS_ETAPA;
        titulo = SysmanFunciones.concatenar(
                        PrerequisitosetapasControladorEnum.ETAPA.getValue(),
                        " ", etapa,
                        SysmanFunciones.validarVariableVacio(nombreEtapa) ? ""
                            : " - ",
                        SysmanFunciones.validarVariableVacio(nombreEtapa) ? ""
                            : nombreEtapa.toUpperCase());
        if (!"A".equals(estadoEtapa))
        {
            modificar = false;
        }
        else
        {
            modificar = true;
        }
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaPRERREQUISITO();
        cargarListaPRERREQUISITOE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivop);
        parametrosListado
                        .put(PrerequisitosetapasControladorEnum.CONSECUTIVODETALLE
                                        .getValue(), consecutivoDetalle);

    }

    private String validarParametroCadena(Map<String, Object> campos,
        String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
            : campos.get(var).toString();
    }

    public void cargarListaPRERREQUISITO()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrerequisitosetapasControladorUrlEnum.URL4770
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);
        param.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivop);
        param.put(PrerequisitosetapasControladorEnum.CONSECUTIVODETALLE
                        .getValue(), consecutivoDetalle);

        listaPRERREQUISITO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PrerequisitosetapasControladorEnum.PRERREQUISITO
                                        .getValue());

    }

    public void cargarListaPRERREQUISITOE()
    {
        listaPRERREQUISITOE = listaPRERREQUISITO;
    }

    public void seleccionarFilaPRERREQUISITO(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        // <CODIGO_DESARROLLADO> //

        registro.getCampos()
                        .put(PrerequisitosetapasControladorEnum.PRERREQUISITO
                                        .getValue(), validarParametroCadena(registroAux.getCampos(),
                                                        PrerequisitosetapasControladorEnum.PRERREQUISITO
                                                                        .getValue()));
        registro.getCampos().put(
                        PrerequisitosetapasControladorEnum.NOMBREPRE.getValue(),
                        validarParametroCadena(registroAux.getCampos(),
                                        PrerequisitosetapasControladorEnum.NOMBREPRE
                                                        .getValue()));

        registro.getCampos().put(
                        PrerequisitosetapasControladorEnum.TIPO.getValue(),
                        validarParametroCadena(registroAux.getCampos(),
                                        PrerequisitosetapasControladorEnum.TIPO.getValue()));

        registro.getCampos().put(PrerequisitosetapasControladorEnum.TIPOPRE.getValue(), validarParametroCadena(registroAux.getCampos(),
                        PrerequisitosetapasControladorEnum.TIPOPRE.getValue()));
    }

    public void seleccionarFilaPRERREQUISITOE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarParametroCadena(registroAux.getCampos(),
                        PrerequisitosetapasControladorEnum.NOMBREPRE
                                        .getValue());
        prerrequisito = validarParametroCadena(registroAux.getCampos(),
                        PrerequisitosetapasControladorEnum.PRERREQUISITO
                                        .getValue());
        tipoPrerrequisito = validarParametroCadena(registroAux.getCampos(),
                        PrerequisitosetapasControladorEnum.TIPO.getValue());
        nomTipoPre = validarParametroCadena(registroAux.getCampos(),
                        PrerequisitosetapasControladorEnum.TIPOPRE.getValue());
    }

    public void cambiarPRERREQUISITOC(int rowNum)
    {

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        PrerequisitosetapasControladorEnum.TIPOPRE.getValue(),
                        nomTipoPre);
    }

    public boolean validarPrerrequisitoProponente()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);
        param.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivop);
        param.put(PrerequisitosetapasControladorEnum.CONSECUTIVODETALLE
                        .getValue(), consecutivoDetalle);
        try
        {
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrerequisitosetapasControladorUrlEnum.URL6244
                                                                            .getValue())
                                            .getUrl(), param));

            String prerequisitoProponente = SysmanFunciones
                            .nvl(reg.getCampos()
                                            .get(PrerequisitosetapasControladorEnum.PRERREQUISITO
                                                            .getValue()),
                                            "0")
                            .toString();
            /*
             * '********* Verificar que el prerrequisito no est�
             * relacionado con un proponente
             **************/
            if (!"0".equals(prerequisitoProponente))
            {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2219"));
                return false;
            }
            else
            {
                return true;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;

    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        registro.getCampos()
                        .put(PrerequisitosetapasControladorEnum.CONSECUTIVODETALLE
                                        .getValue(), consecutivoDetalle);
        registro.getCampos().put(PrerequisitosetapasControladorEnum.TRANSACCION
                        .getValue(), consecutivop);

        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(
                        PrerequisitosetapasControladorEnum.TIPOPRE.getValue());
        registro.getCampos().remove(PrerequisitosetapasControladorEnum.NOMBREPRE.getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        cargarListaPRERREQUISITO();
        cargarListaPRERREQUISITOE();
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().remove(
                        PrerequisitosetapasControladorEnum.TIPOLB.getValue());
        registro.getCampos().remove(
                        PrerequisitosetapasControladorEnum.TIPOPRE.getValue());
        registro.getCampos().remove(PrerequisitosetapasControladorEnum.NOMBREPRE
                        .getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        cargarListaPRERREQUISITO();
        cargarListaPRERREQUISITOE();
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        return validarPrerrequisitoProponente() ? true : false;
    }

    @Override
    public boolean eliminarDespues()
    {

        cargarListaPRERREQUISITO();
        cargarListaPRERREQUISITOE();
        return true;
    }

    public void cerrarFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos()
    {
        // heredado del bean base
        registro.getCampos().remove(PrerequisitosetapasControladorEnum.NOMBREPRE
                        .getValue());
        registro.getCampos().remove(
                        PrerequisitosetapasControladorEnum.TIPOPRE.getValue());
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     * Redirecciona al formulario Transaccion(419)
     */
    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.TRANSACCIONS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getTipoContrato()
    {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato)
    {
        this.tipoContrato = tipoContrato;
    }

    public String getConsecutivop()
    {
        return consecutivop;
    }

    public void setConsecutivop(String consecutivop)
    {
        this.consecutivop = consecutivop;
    }

    public String getConsecutivoDetalle()
    {
        return consecutivoDetalle;
    }

    public void setConsecutivoDetalle(String consecutivoDetalle)
    {
        this.consecutivoDetalle = consecutivoDetalle;
    }

    public String getEtapa()
    {
        return etapa;
    }

    public void setEtapa(String etapa)
    {
        this.etapa = etapa;
    }

    public String getNombreEtapa()
    {
        return nombreEtapa;
    }

    public void setNombreEtapa(String nombreEtapa)
    {
        this.nombreEtapa = nombreEtapa;
    }

    public RegistroDataModelImpl getListaPRERREQUISITO()
    {
        return listaPRERREQUISITO;
    }

    public void setListaPRERREQUISITO(
        RegistroDataModelImpl listaPRERREQUISITO)
    {
        this.listaPRERREQUISITO = listaPRERREQUISITO;
    }

    public RegistroDataModelImpl getListaPRERREQUISITOE()
    {
        return listaPRERREQUISITOE;
    }

    public void setListaPRERREQUISITOE(
        RegistroDataModelImpl listaPRERREQUISITOE)
    {
        this.listaPRERREQUISITOE = listaPRERREQUISITOE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public boolean isModificar()
    {
        return modificar;
    }

    public void setModificar(boolean modificar)
    {
        this.modificar = modificar;
    }

}
