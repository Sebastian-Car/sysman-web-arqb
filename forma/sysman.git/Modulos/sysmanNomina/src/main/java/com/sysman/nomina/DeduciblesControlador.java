package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.DeduciblesControladorEnum;
import com.sysman.nomina.enums.DeduciblesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
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
 * @author ngomez
 * @version 1, 03/08/2015
 * @author jcrodriguez,Refactoring y Depuracion
 * @version 2, 06/09/2017
 */
@ManagedBean
@ViewScoped
public class DeduciblesControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private RegistroDataModelImpl listaIdDeConcepto;
    private RegistroDataModelImpl listaIdDeConceptoE;
    private RegistroDataModelImpl listaIdDeEmpleado;
    private RegistroDataModelImpl listaIdDeEmpleadoE;
    private String auxiliar;
    private String registroauxConcepto;
    private String registroauxEmpleado;

    /**
     * Creates a new instance of DeduciblesControlador
     */
    public DeduciblesControlador()
    {
        super();

        compania = SessionUtil.getCompania();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.DEDUCIBLES_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(DeduciblesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.NOVEDADES;
        buscarLlave();
        reasignarOrigen();

        registro = new Registro(new HashMap<String, Object>());
        cargarListaIdDeConcepto();
        cargarListaIdDeConceptoE();
        cargarListaIdDeEmpleado();
        cargarListaIdDeEmpleadoE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public void cargarListaIdDeConcepto()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(DeduciblesControladorUrlEnum.URL3223.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaIdDeConcepto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, DeduciblesControladorEnum.ID_DE_CONCEPTO.getValue());

    }

    public void cargarListaIdDeConceptoE()
    {
        listaIdDeConceptoE = listaIdDeConcepto;
    }

    public void cargarListaIdDeEmpleado()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(DeduciblesControladorUrlEnum.URL4648.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaIdDeEmpleado = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, DeduciblesControladorEnum.ID_DE_EMPLEADO.getValue());
    }

    public void cargarListaIdDeEmpleadoE()
    {
        listaIdDeEmpleadoE = listaIdDeEmpleado;
    }

    public void cambiarIdDeConceptoC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(DeduciblesControladorEnum.NOMBRECONCEPTO.getValue(), registroauxConcepto);

    }

    public void cambiarIdDeEmpleadoC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(DeduciblesControladorEnum.NOMBRECOMPLETO.getValue(), registroauxEmpleado);

    }

    private String validarParametroCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    public void seleccionarFilaIdDeConcepto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(DeduciblesControladorEnum.ID_DE_CONCEPTO.getValue(),
                        registroAux.getCampos().get(DeduciblesControladorEnum.ID_DE_CONCEPTO.getValue()));
        registro.getCampos().put(DeduciblesControladorEnum.NOMBRECONCEPTO.getValue(),
                        registroAux.getCampos().get(DeduciblesControladorEnum.NOMBRE_CONCEPTO.getValue()));
    }

    public void seleccionarFilaIdDeConceptoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarParametroCadena(registroAux.getCampos(), DeduciblesControladorEnum.ID_DE_CONCEPTO.getValue());
        registroauxConcepto = validarParametroCadena(registroAux.getCampos(), DeduciblesControladorEnum.NOMBRE_CONCEPTO.getValue());
    }

    public void seleccionarFilaIdDeEmpleado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(DeduciblesControladorEnum.ID_DE_EMPLEADO.getValue(),
                        registroAux.getCampos().get(DeduciblesControladorEnum.ID_DE_EMPLEADO.getValue()));
        registro.getCampos().put(DeduciblesControladorEnum.NOMBRECOMPLETO.getValue(),
                        registroAux.getCampos().get(DeduciblesControladorEnum.NOMBRECOMPLETO.getValue()));
    }

    public void seleccionarFilaIdDeEmpleadoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarParametroCadena(registroAux.getCampos(), DeduciblesControladorEnum.ID_DE_EMPLEADO.getValue());

        registroauxEmpleado = validarParametroCadena(registroAux.getCampos(), DeduciblesControladorEnum.NOMBRECOMPLETO.getValue());
    }

    @Override
    public void abrirFormulario()
    {
        registro.getCampos().put(DeduciblesControladorEnum.ID_DE_EMPLEADO.getValue(), "");
        registro.getCampos().put(DeduciblesControladorEnum.NOMBRECOMPLETO.getValue(), "");

    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes()
    {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);

        registro.getCampos().put(DeduciblesControladorEnum.ID_DE_PROCESO.getValue(), "00");
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), "0000");
        registro.getCampos().put(GeneralParameterEnum.MES.getName(), "00");
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), "00");
        registro.getCampos().remove(DeduciblesControladorEnum.NOMBRECONCEPTO.getValue());
        registro.getCampos().remove(DeduciblesControladorEnum.NOMBRECOMPLETO.getValue());
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        registro.getCampos().remove(DeduciblesControladorEnum.NOMBRECONCEPTO.getValue());
        registro.getCampos().remove(DeduciblesControladorEnum.NOMBRECOMPLETO.getValue());
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {

        registro.getCampos().remove(DeduciblesControladorEnum.NOMBRECONCEPTO.getValue());
        registro.getCampos().remove(DeduciblesControladorEnum.NOMBRECOMPLETO.getValue());
        return true;

    }

    @Override
    public boolean actualizarDespues()
    {

        registro.getCampos().remove(DeduciblesControladorEnum.NOMBRECONCEPTO.getValue());
        registro.getCampos().remove(DeduciblesControladorEnum.NOMBRECOMPLETO.getValue());
        return true;

    }

    @Override
    public boolean eliminarAntes()
    {

        registro.getCampos().remove(DeduciblesControladorEnum.NOMBRECONCEPTO.getValue());
        registro.getCampos().remove(DeduciblesControladorEnum.NOMBRECOMPLETO.getValue());
        return true;

    }

    @Override
    public boolean eliminarDespues()
    {

        registro.getCampos().remove(DeduciblesControladorEnum.NOMBRECONCEPTO.getValue());
        registro.getCampos().remove(DeduciblesControladorEnum.NOMBRECOMPLETO.getValue());
        return true;

    }

    @Override
    public void removerCombos()
    {
        // Metodo heredado
    }

    @Override
    public void asignarValoresRegistro()
    {
        // Metodo heredado
    }

    public RegistroDataModelImpl getListaIdDeConcepto()
    {
        return listaIdDeConcepto;
    }

    public void setListaIdDeConcepto(RegistroDataModelImpl listaIdDeConcepto)
    {
        this.listaIdDeConcepto = listaIdDeConcepto;
    }

    public RegistroDataModelImpl getListaIdDeConceptoE()
    {
        return listaIdDeConceptoE;
    }

    public void setListaIdDeConceptoE(RegistroDataModelImpl listaIdDeConceptoE)
    {
        this.listaIdDeConceptoE = listaIdDeConceptoE;
    }

    public RegistroDataModelImpl getListaIdDeEmpleado()
    {
        return listaIdDeEmpleado;
    }

    public void setListaIdDeEmpleado(RegistroDataModelImpl listaIdDeEmpleado)
    {
        this.listaIdDeEmpleado = listaIdDeEmpleado;
    }

    public RegistroDataModelImpl getListaIdDeEmpleadoE()
    {
        return listaIdDeEmpleadoE;
    }

    public void setListaIdDeEmpleadoE(RegistroDataModelImpl listaIdDeEmpleadoE)
    {
        this.listaIdDeEmpleadoE = listaIdDeEmpleadoE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

}
