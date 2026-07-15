package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.SubpactesoreriasControladorEnum;
import com.sysman.contabilidad.enums.SubpactesoreriasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 18/05/2016
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 12/04/2017
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped

public class SubpactesoreriasControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    private int indice;
    // <DECLARAR_ATRIBUTOS>
    private String cuenta;
    private String nombreCuenta;
    private boolean editar;
    private double pacTesoreriaAnt;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String anio;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaId;
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of SubpactesoreriasControlador
     */
    public SubpactesoreriasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.SUBPACTESORERIAS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            editar = false;
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                anio = parametrosEntrada.get("anio").toString();
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(SubpactesoreriasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        tabla = SubpactesoreriasControladorEnum.PARAM0.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaId();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen()
    {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), cuenta);

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpactesoreriasControladorUrlEnum.URL124
                                                        .getValue());
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpactesoreriasControladorUrlEnum.URL118
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaId()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpactesoreriasControladorUrlEnum.URL6213
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaId = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaId(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuenta = registroAux.getCampos().get("ID") == null ? ""
            : registroAux.getCampos().get("ID").toString();
        nombreCuenta = registroAux.getCampos().get("NOMBRE") == null ? ""
            : registroAux.getCampos().get("NOMBRE").toString();
        editar = !("0".equals(
                        registroAux.getCampos().get("MOVIMIENTO") == null ? ""
                            : registroAux.getCampos().get("MOVIMIENTO")
                                            .toString()))
                                                ? true : false;
        reasignarOrigen();
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
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
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CODIGO.getName(), cuenta);
        Registro aux;
        try
        {
            aux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpactesoreriasControladorUrlEnum.URL202
                                                                            .getValue())
                                            .getUrl(), param));
            double tApropiacion = Double.parseDouble(
                            aux.getCampos().get("TOTALAPROPIACION").toString());
            double nApropiacion = (Double.parseDouble(
                            registro.getCampos().get("SALDO").toString())
                + Double.parseDouble(registro.getCampos()
                                .get(SubpactesoreriasControladorEnum.PARAM2
                                                .getValue())
                                .toString()))
                - pacTesoreriaAnt;

            if (tApropiacion < nApropiacion)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB718")
                    + registro.getCampos().get("MESN")
                    + " -> "
                    + new java.text.DecimalFormat(
                                    SubpactesoreriasControladorEnum.PARAM1
                                                    .getValue())
                                                                    .format(nApropiacion)
                    + idioma.getString("TB_TB719")
                    + new java.text.DecimalFormat(
                                    SubpactesoreriasControladorEnum.PARAM1
                                                    .getValue())
                                                                    .format(tApropiacion)
                    + " Excede en -> "
                    + new java.text.DecimalFormat(
                                    SubpactesoreriasControladorEnum.PARAM1
                                                    .getValue())
                                                                    .format(nApropiacion
                                                                        - tApropiacion));
                return false;
            }
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubpactesoreriasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        registro.getCampos().remove("SALDO");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpactesoreriasControladorUrlEnum.URL11380
                                                        .getValue());
        Map<String, Object> fields = new TreeMap<>();
        fields.put(SubpactesoreriasControladorEnum.PARAM2.getValue(),
                        registro.getCampos()
                                        .get(SubpactesoreriasControladorEnum.PARAM2
                                                        .getValue()));
        fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
        fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        fields.put(GeneralParameterEnum.ANO.getName(), anio);
        fields.put(SubpactesoreriasControladorEnum.PARAM3.getValue(),
                        registro.getCampos()
                                        .remove(SubpactesoreriasControladorEnum.PARAM3
                                                        .getValue()));
        fields.put(GeneralParameterEnum.CODIGO.getName(), cuenta);
        fields.put(SubpactesoreriasControladorEnum.PARAM4.getValue(),
                        registro.getCampos()
                                        .get(SubpactesoreriasControladorEnum.PARAM5
                                                        .getValue()));
        Parameter parameter = new Parameter();
        parameter.setFields(fields);
        try
        {
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubpactesoreriasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos().remove(
                        SubpactesoreriasControladorEnum.PARAM4.getValue());
        registro.getCampos().remove("MESN");
        registro.getCampos().remove("TOTALAPROPIACION");
        registro.getCampos().remove("EJECUCIONES");
        registro.getCampos().remove("EJECUCIONESCNT");
        registro.getCampos().remove("SALDOPACTESORERIAMENSUAL");

        // </CODIGO_DESARROLLADO>
    }

    public void activarEdicion(Registro registro)
    {
        indice = listaInicial.getRowIndex();
        pacTesoreriaAnt = Double.parseDouble(
                        registro.getCampos().get("PACTESORERIA").toString());
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getCuenta()
    {
        return cuenta;
    }

    public void setCuenta(String cuenta)
    {
        this.cuenta = cuenta;
    }

    public String getNombreCuenta()
    {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta)
    {
        this.nombreCuenta = nombreCuenta;
    }

    public boolean isEditar()
    {
        return editar;
    }

    public void setEditar(boolean editar)
    {
        this.editar = editar;
    }

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    public double getPacTesoreriaAnt()
    {
        return pacTesoreriaAnt;
    }

    public void setPacTesoreriaAnt(double pacTesoreriaAnt)
    {
        this.pacTesoreriaAnt = pacTesoreriaAnt;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaId()
    {
        return listaId;
    }

    public void setListaId(RegistroDataModelImpl listaId)
    {
        this.listaId = listaId;
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
