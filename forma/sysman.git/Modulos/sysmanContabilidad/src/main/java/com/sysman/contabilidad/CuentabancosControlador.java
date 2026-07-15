package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroRemote;
import com.sysman.contabilidad.enums.CuentabancosControladorEnum;
import com.sysman.contabilidad.enums.CuentabancosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author vmolano
 * @version 1, 07/03/2016
 * 
 * @version 2, 17/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla, de datos.
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */

@ManagedBean
@ViewScoped
public class CuentabancosControlador extends BeanBaseDatosAcmeImpl
{

    private final String compania;
    private final String consEstado;
    private List<Registro> listaBANCO;
    private RegistroDataModelImpl listaRECURSOS;
    private String idContableActual;
    private String nombre;
    private int anoActual;
    private String banco;
    private String naturaleza;
    private boolean varVolver;
    
    @EJB
    private EjbContabilidadCeroRemote ejbContabilidadCeroRemote;

    public CuentabancosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        consEstado="ESTADO";
        try
        {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            idContableActual = (String) parametrosEntrada.get("CUENTA");
            anoActual = Integer.parseInt((String) parametrosEntrada.get("ANO"));
            nombre = (String) parametrosEntrada.get("NOMBRE");
            naturaleza = (String) parametrosEntrada.get("NATURALEZA");
            banco = (String) parametrosEntrada.get("BANCO");

            numFormulario = GeneralCodigoFormaEnum.CUENTABANCOS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(CuentabancosControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void iniciarListas()
    {
        cargarListaBANCO();
        cargarListaRECURSOS();
    }

    @Override
    public void iniciarListasSub()
    {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void iniciarListasSubNulo()
    {
     // METODO NO IMPLEMENTADO
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase=GenericUrlEnum.CUENTABANCOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(CuentabancosControladorEnum.PARAM0.getValue(), idContableActual);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anoActual);
    }

    public void cargarListaBANCO()
    {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            listaBANCO = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(CuentabancosControladorUrlEnum.URL3433.getValue()).getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaRECURSOS()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(CuentabancosControladorUrlEnum.URL3923.getValue());     
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),SysmanFunciones.ano(new Date()));
        
        listaRECURSOS = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param, true, "CODIGO");
    }

    @Override
    public void abrirFormulario()
    {
        try
        {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(CuentabancosControladorEnum.PARAM0.getValue(), idContableActual);
            parametros.put(GeneralParameterEnum.ANO.getName(), anoActual);

            Registro regTemp = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(CuentabancosControladorUrlEnum.URL5252.getValue()).getUrl(),parametros));
            if (regTemp == null)
            {

                Map<String, Object> campos = new HashMap<>();
                campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                campos.put(CuentabancosControladorEnum.PARAM0.getValue(), idContableActual);
                campos.put(CuentabancosControladorEnum.PARAM1.getValue(), "09/03/2016");
                campos.put(CuentabancosControladorEnum.PARAM2.getValue(), "A");
                campos.put(GeneralParameterEnum.ESTADO.getName(), "A");
                campos.put(GeneralParameterEnum.ANO.getName(), anoActual);
                campos.put(GeneralParameterEnum.BANCO.getName(), banco);
                campos.put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
                campos.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
                Parameter parameter = new Parameter();
                parameter.setFields(campos);
                
                UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(CuentabancosControladorUrlEnum.URL5251.getValue());
                rid = requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), parameter);
                
                
                cargarRegistro(rid, "m", -2);
                varVolver = true;
            }
        }
        catch (SystemException ex)
        {
            Logger.getLogger(CuentabancosControlador.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cambiarESTADO()
    {
        String estadoActual = registro.getCampos().get(consEstado).toString();

        if ("I".equalsIgnoreCase(estadoActual))
        {
            try
            {
                BigDecimal saldo =  ejbContabilidadCeroRemote.consultarSaldosCaja(compania, new Date(), idContableActual, naturaleza);
                
                if (saldo.compareTo(BigDecimal.ZERO) > 0)
                {
                    DecimalFormat df = new DecimalFormat("#,###.00");
                    StringBuilder aux= new StringBuilder();
                    aux.append(idioma.getString("TB_TB539")).append(nombre).append(idioma.getString("TB_TB540")).append(df.format(saldo)).append("\n- No se puede inactivar. ");
                    JsfUtil.agregarMensajeError(aux.toString());
                    registro.getCampos().put(consEstado, registroIni.get(consEstado));
                }
                else
                {
                    registro.getCampos().put("FECHACANCELACION", new Date());
                }

            }
            catch (SystemException ex)
            {
                Logger.getLogger(CuentabancosControlador.class
                                .getName()).log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRECURSOS
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRECURSOS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RECURSOS", registroAux.getCampos().get("CODIGO"));
    }
    
    
    public void ejecutarrcVolver()
    {
        // <CODIGO_DESARROLLADO>
        if (varVolver)
        {
            accion = null;
            varVolver = false;
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
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
        // </CODIGO_DESARROLLADO>
        return true;
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

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public List<Registro> getListaBANCO()
    {
        return listaBANCO;
    }

    public void setListaBANCO(List<Registro> listaBANCO)
    {
        this.listaBANCO = listaBANCO;
    }

   

    public RegistroDataModelImpl getListaRECURSOS() {
        return listaRECURSOS;
    }

    public void setListaRECURSOS(RegistroDataModelImpl listaRECURSOS) {
        this.listaRECURSOS = listaRECURSOS;
    }

    public String getIdContableActual()
    {
        return idContableActual;
    }

    public void setIdContableActual(String idContableActual)
    {
        this.idContableActual = idContableActual;
    }

    public int getAnoActual()
    {
        return anoActual;
    }

    public void setAnoActual(int anoActual)
    {
        this.anoActual = anoActual;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getNaturaleza()
    {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza)
    {
        this.naturaleza = naturaleza;
    }

    public String getBanco()
    {
        return banco;
    }

    public void setBanco(String banco)
    {
        this.banco = banco;
    }

    public boolean isVarVolver()
    {
        return varVolver;
    }

    public void setVarVolver(boolean varVolver)
    {
        this.varVolver = varVolver;
    }

}
