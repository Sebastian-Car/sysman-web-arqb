package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.AseguradorasControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
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
 * @author dsuesca
 * @version 1, 15/09/2015
 * @author amonroy
 * @version 2, 04/04/2017 Proceso de Refactoring y Revision de buenas
 * practicas SonarLint
 * 
 * @author ybecerra
 * @version 3, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class AseguradorasControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NOMBRE en el formulario, almacena el texto
     * NOMBRE el cual es un campo del registro
     */
    private final String nombreTxt;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo SUCURSAL en el formulario, almacena el texto
     * SUCURSAL el cual es un campo del registro
     */
    private final String cSucursal;
    private RegistroDataModelImpl listaTexto4;
    private RegistroDataModelImpl listaTexto4E;
    private String auxiliar;
    private String nombre;

    /**
     * Creates a new instance of AseguradorasControlador
     */
    public AseguradorasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        nombreTxt = GeneralParameterEnum.NOMBRE.getName();
        cSucursal = GeneralParameterEnum.SUCURSAL.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ASEGURADORAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(AseguradorasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.ASEGURADORA;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaTexto4();
        cargarListaTexto4E();
        abrirFormulario();
    }

    public RegistroDataModelImpl getListaTexto4()
    {
        return listaTexto4;
    }

    public void setListaTexto4(RegistroDataModelImpl listaTexto4)
    {
        this.listaTexto4 = listaTexto4;
    }

    public RegistroDataModelImpl getListaTexto4E()
    {
        return listaTexto4E;
    }

    public void setListaTexto4E(RegistroDataModelImpl listaTexto4E)
    {
        this.listaTexto4E = listaTexto4E;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public void cargarListaTexto4()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AseguradorasControladorUrlEnum.URL2948
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTexto4 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTexto4E()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AseguradorasControladorUrlEnum.URL3350
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTexto4E = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void seleccionarFilaTexto4(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NITASEGURADORA",
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursal));
        registro.getCampos().put(nombreTxt,
                        registroAux.getCampos().get(nombreTxt));
    }

    public void seleccionarFilaTexto4E(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
        nombre = !SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        nombreTxt)
                            ? registroAux.getCampos().get(nombreTxt).toString()
                            : "";

    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Texto4 en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTexto4C(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(nombreTxt,
                        registro.getCampos().get(nombreTxt));
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(nombreTxt, nombre);
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>

    @Override
    public void abrirFormulario()
    {
        // CODIGO DESARROLLADO
        // OPENFORM
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public void removerCombos()
    {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(cSucursal);
    }

    @Override
    public boolean insertarAntes()
    {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        return true;
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
    }

    @Override
    public void asignarValoresRegistro()
    {
        // NO SE IMPLEMENTA
    }
}
