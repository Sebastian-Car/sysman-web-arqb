package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.PagosdoblesdosControladorEnum;
import com.sysman.predial.enums.PagosdoblesdosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
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
 * @version 1, 08/06/2016
 * @author jcrodriguez=>Refactoring y depuracion del controlador
 * @version 2, 07/07/2017
 */
@ManagedBean
@ViewScoped

public class PagosdoblesdosControlador extends BeanBaseModal
{
    private final String compania;
    /**
     * Constante que identifica el contenido del texto en bean
     * TB_TB481
     */
    private final String tbPredioCheck;
    /**
     * Constante que identifica el contenido del texto en bean
     * TB_TB482
     */
    private final String tbTituloPredio;
    /**
     * Constante que identifica el contenido del texto en bean
     * TB_TB477
     */
    private final String tbMens;
    private String barras;
    private String predio;
    private String banco;
    private String anioPago;
    private String codigo;
    private String factura;
    private Date fecha;
    private String paquete;
    private String nombreBanco;
    private String tituloPredio;
    private String tituloPredioCheck;
    private boolean facturaVisible;
    private RegistroDataModelImpl listabanco;
    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;

    /**
     * Creates a new instance of Pagosdobles2Controlador
     */
    public PagosdoblesdosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        tbTituloPredio = "TB_TB482";
        tbPredioCheck = "TB_TB481";
        tbMens = "TB_TB477";
        tituloPredio = idioma.getString(tbTituloPredio);
        tituloPredioCheck = idioma.getString(tbTituloPredio);
        facturaVisible = true;
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PAGOSDOBLESDOS_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                banco = parametrosEntrada.get("banco").toString();
                nombreBanco = parametrosEntrada.get("nombrebanco").toString();
                paquete = parametrosEntrada.get("paquete").toString();
                fecha = SysmanFunciones.convertirAFecha(
                                parametrosEntrada.get("fecha").toString());
            }
            anioPago = String
                            .valueOf(SysmanFunciones.getParteFecha(
                                            new Date(),
                                            Calendar.YEAR)
                                + 1);

        }
        catch (Exception ex)
        {
            Logger.getLogger(PagosdoblesdosControlador.class.getName())
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
        cargarListabanco();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListabanco()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(PagosdoblesdosControladorUrlEnum.URL4857.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listabanco = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, PagosdoblesdosControladorEnum.CODIGOBANCO.getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirComando21()
    {
        // <CODIGO_DESARROLLADO>
        if (("true").equals(barras))
        {
            if (codigo.length() != 72)
            {
                JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(idioma.getString("TB_TB476"), " ",
                                String.valueOf(codigo.length()), " ", idioma.getString(tbMens)));
                return;
            }

            registrarPagoDoble();
        }
        else
        {
            if (!validarVacios())
            {
                return;
            }
            registrarPagoDoble();
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    private boolean validarVacios()
    {
        if (SysmanFunciones.validarVariableVacio(factura))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB478"));
            return false;
        }
        if (codigo.length() != 15)
        {
            JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(idioma.getString("TB_TB479"), " ",
                            String.valueOf(codigo.length()), " ", idioma.getString(tbMens)));
            return false;
        }
        if ((factura.length() < 9) || (factura.length() > 10))
        {
            JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(idioma.getString("TB_TB480"), " ",
                            String.valueOf(factura.length()), " ", idioma.getString(tbMens)));
            return false;
        }
        return true;
    }

    public void cambiarbarras()
    {
        // <CODIGO_DESARROLLADO>
        if (("true").equals(barras))
        {
            predio = "false";
            tituloPredio = idioma.getString(tbPredioCheck);
            tituloPredioCheck = idioma.getString(tbPredioCheck);
            facturaVisible = false;
        }
        else
        {
            tituloPredio = idioma.getString(tbTituloPredio);
            tituloPredioCheck = idioma.getString(tbTituloPredio);
            predio = "true";
            facturaVisible = true;
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilabanco(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        banco = registroAux.getCampos().get(PagosdoblesdosControladorEnum.CODIGOBANCO.getValue()).toString();
    }

    public void registrarPagoDoble()
    {
        try
        {
            int num = ejbPredialOcho.registrarPagosDobles(SessionUtil.getUser().getCodigo(),
                            compania,
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL,
                            codigo,
                            factura,
                            banco,
                            paquete,
                            Integer.parseInt(anioPago),
                            fecha);
            if (num == -11)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1341"));
            }
            else if (num == -10)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1343"));
            }
            else if (num >= 0)
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1342"));
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getBarras()
    {
        return barras;
    }

    public void setBarras(String barras)
    {
        this.barras = barras;
    }

    public String getPredio()
    {
        return predio;
    }

    public void setPredio(String predio)
    {
        this.predio = predio;
    }

    public String getBanco()
    {
        return banco;
    }

    public void setBanco(String banco)
    {
        this.banco = banco;
    }

    public String getAnioPago()
    {
        return anioPago;
    }

    public void setAnioPago(String anioPago)
    {
        this.anioPago = anioPago;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public String getFactura()
    {
        return factura;
    }

    public void setFactura(String factura)
    {
        this.factura = factura;
    }

    public Date getFecha()
    {
        return fecha;
    }

    public void setFecha(Date fecha)
    {
        this.fecha = fecha;
    }

    public String getPaquete()
    {
        return paquete;
    }

    public void setPaquete(String paquete)
    {
        this.paquete = paquete;
    }

    public String getNombreBanco()
    {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco)
    {
        this.nombreBanco = nombreBanco;
    }

    public String getTituloPredio()
    {
        return tituloPredio;
    }

    public void setTituloPredio(String tituloPredio)
    {
        this.tituloPredio = tituloPredio;
    }

    public boolean isFacturaVisible()
    {
        return facturaVisible;
    }

    public void setFacturaVisible(boolean facturaVisible)
    {
        this.facturaVisible = facturaVisible;
    }

    public String getTituloPredioCheck()
    {
        return tituloPredioCheck;
    }

    public void setTituloPredioCheck(String tituloPredioCheck)
    {
        this.tituloPredioCheck = tituloPredioCheck;
    }

    public RegistroDataModelImpl getListabanco()
    {
        return listabanco;
    }

    public void setListabanco(RegistroDataModelImpl listabanco)
    {
        this.listabanco = listabanco;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
