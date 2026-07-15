package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenUnoRemote;
import com.sysman.almacen.enums.FrmdocasociadomovimientosControladorEnum;
import com.sysman.almacen.enums.FrmdocasociadomovimientosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author sdaza
 * @version 1, 04/04/2016
 *
 * @version 2, 28/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos, en el origen de grilla.
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor de la variable numFormulario por el enumerado correspondiente y se reemplaza el metodo redireccionar por el metodo redireccionarForma
 * para manejar el numero de formulario con el enumerado correspondiente.
 * 
 * @author asana, 22/08/2018, 1. Se cambia campo CANTIDAD por SALDOCANT dado que validaba sobre el total de los elementos de la orden de compra y no sobre el saldoen método cambiarcantCargarC(int
 * rowNum)
 * 
 * 2. Se cambia parámetro tipoDocAsociado por tipo, porque en funcion no se encontraba registros cuando se realizaba filtro, en método seleccionarFilaNumeroDocAsociado()
 */
@ManagedBean
@ViewScoped
public class FrmdocasociadomovimientosControlador
                extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private final String modulo;
    private final String cCantidadAfect;
    private final String cTipoMovAsoc;
    private RegistroDataModelImpl listaNumeroDocAsociado;
    private RegistroDataModelImpl listaNumeroDocAsociadoIdi;
    private String anoMov;
    private String tipoMov;
    private String nroMov;
    private String claseMov;
    private String tipoElementoMov;
    private String claseDocAsoc;
    private String nombreDocAsociado;
    private String nroDocAsociado;
    private Map<String, Object> rid;
    private String tipoDocAsociado;
    private String docAsociado;
    private UrlBean urlBean;
    private String nombreColUno;
    private String tipo;
    private boolean verMensaje;
    private String nroDocAsociadoIdi;
    private boolean cargarEntradaIdi;
    private boolean cargarEntrada;
    private String manEntradaPepsIdi;
    private String proyecto;
    private String operacion;
    private String auxAlmacen;
    private String rueda;
    private String fuenteR;
    private String referencia;
    private String auxiliar;
    private String centroCosto;
    private String bodega;
    private String cptoMov;
    private String manejaAux;

    @EJB
    private EjbAlmacenUnoRemote ejbAlmacenUnoRemote;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FrmdocasociadomovimientosControlador
     */
    public FrmdocasociadomovimientosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCantidadAfect = "CANTIDAD_AFECT";
        cTipoMovAsoc = "TIPOMOVASOCIADO";
        verMensaje = false;
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMDOCASOCIADOMOVIMIENTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmdocasociadomovimientosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null)
        {
            anoMov = parametrosEntrada.get("anoMov").toString();
            tipoMov = parametrosEntrada.get("tipoMov").toString();
            nroMov = parametrosEntrada.get("nroMov").toString();
            claseMov = parametrosEntrada.get("claseMov").toString();
            tipoElementoMov = parametrosEntrada.get("tipoElementoMov").toString();
            claseDocAsoc = parametrosEntrada.get("claseDocAsoc").toString();
            nombreDocAsociado = parametrosEntrada
                            .get("nombreDocAsociado").toString();
            proyecto = SysmanFunciones.nvl(parametrosEntrada.get("proyecto"), " ").toString();
            operacion = SysmanFunciones.nvl(parametrosEntrada.get("operacion"), " ").toString();
            auxAlmacen = SysmanFunciones.nvl(parametrosEntrada.get("auxAlmacen"), " ").toString();
            rueda = SysmanFunciones.nvl(parametrosEntrada.get("rueda"), " ").toString();
            fuenteR = SysmanFunciones.nvl(parametrosEntrada.get("fuenteR"), " ").toString();
            referencia = SysmanFunciones.nvl(parametrosEntrada.get("referencia"), " ").toString();
            auxiliar = SysmanFunciones.nvl(parametrosEntrada.get("auxiliar"), " ").toString();
            centroCosto = SysmanFunciones.nvl(parametrosEntrada.get("centroCosto"), " ").toString();
            bodega = SysmanFunciones.nvl(parametrosEntrada.get("bodega"), " ").toString();
            cptoMov = SysmanFunciones.nvl(parametrosEntrada.get("cptoMov"), " ").toString();
            rid = (Map<String, Object>) parametrosEntrada.get("rid");
        }

    }

    @PostConstruct
    public void inicializar()
    {
        tabla = FrmdocasociadomovimientosControladorEnum.TABLA.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaNumeroDocAsociadoIdi();
        abrirFormulario();
        
        manEntradaPepsIdi = getParametro(
        		"MANEJA PEPS CONSUMO DE ALMACEN IDIPRON",
        		"NO");
        
        manejaAux = getParametro(
        		"MANEJA SALDO POR BODEGA Y AUXILIARES EN ALMACEN",
        		"NO");
        
        cargarListaNumeroDocAsociado();
        
        if(manEntradaPepsIdi.equals("SI") 
        		&& "S".equals(claseMov) 
        		&& "C".equals(tipoElementoMov))
        {
        	cargarEntrada = false;
        	cargarEntradaIdi = true;
        }
        else
        {
        	cargarEntrada = true;
        	cargarEntradaIdi = false;
        }

    }

    @Override
    public void reasignarOrigen()
    {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmdocasociadomovimientosControladorUrlEnum.URL3238
                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdocasociadomovimientosControladorUrlEnum.URL5588
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdocasociadomovimientosControladorUrlEnum.URL3388
                                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(
                        FrmdocasociadomovimientosControladorEnum.CLASEASOCIADO
                                        .getValue(),
                        claseDocAsoc);
        parametrosListado.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(),
                        tipoMov);
        parametrosListado.put(GeneralParameterEnum.MOVIMIENTO.getName(),
                        nroMov);
        parametrosListado.put(FrmdocasociadomovimientosControladorEnum.PARAM3
                        .getValue(), nombreDocAsociado);
    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(),
                            true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }
    
    public void cargarListaNumeroDocAsociado()
    {

        urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmdocasociadomovimientosControladorUrlEnum.URL4991
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        
        if (manejaAux.equals("SI") && (claseMov.equals("S") || cptoMov.equals("TB"))) {
        	cargaListaAux(param);        	
        } else {
	        if ((!"O".equals(claseDocAsoc))
	                        && ("E".equals(claseMov) || "S".equals(claseMov)))
	        {
	            if ("R".equals(claseDocAsoc))
	            {
	                urlBean = UrlServiceUtil.getInstance()
	                                .getUrlServiceByUrlByEnumID(
	                                                FrmdocasociadomovimientosControladorUrlEnum.URL5683
	                                                                .getValue());
	                param.put(FrmdocasociadomovimientosControladorEnum.DOCASOCIADO
	                                .getValue(), claseDocAsoc);
	                registro.getCampos().put(cTipoMovAsoc, "R");
	            }
	            else if (validaDocClaseDoc())
	            {
	                docAsociado = "CLASEORDEN ";
	                param.put(GeneralParameterEnum.CLASEORDEN.getName(), "ODC");
	                param.put(FrmdocasociadomovimientosControladorEnum.DOCASOCIADO
	                                .getValue(), docAsociado);
	                registro.getCampos().put(cTipoMovAsoc, "ODC");
	            }
	            else if ((claseSDocCds())
	                            || ("E".equals(claseMov) && ("CDS".equals(claseDocAsoc)
	                                            || "S".equals(claseDocAsoc))))
	            {
	                docAsociado = " CLASEORDEN";
	                param.put(GeneralParameterEnum.CLASEORDEN.getName(), "CDS");
	                param.put(FrmdocasociadomovimientosControladorEnum.DOCASOCIADO
	                                .getValue(), docAsociado);
	                registro.getCampos().put(cTipoMovAsoc, "CDS");
	            }
	            else
	            {
	                cargaStrWhere(param);
	            }
	        }
	        else
	        {
	            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
	                            FrmdocasociadomovimientosControladorUrlEnum.URL5683
	                                            .getValue());
	            param.put(FrmdocasociadomovimientosControladorEnum.DOCASOCIADO
	                            .getValue(), claseDocAsoc);
	        }
    	}
    
        listaNumeroDocAsociado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");

    }

    private void cargaStrWhere(Map<String, Object> param)
    {
        if ("CAD".equals(claseDocAsoc))
        {
            docAsociado = "  CLASEORDEN";
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), "CAD");
            param.put(FrmdocasociadomovimientosControladorEnum.DOCASOCIADO
                            .getValue(), docAsociado);

            registro.getCampos().put(cTipoMovAsoc, "CAD");
        }
        else if ((claseSclaseDocCdc())
                        || ("E".equals(claseMov) && ("CDC".equals(claseDocAsoc)
                                        || "V".equals(claseDocAsoc))))
        {
            docAsociado = "CLASEORDEN  ";
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), "CDC");
            param.put(FrmdocasociadomovimientosControladorEnum.DOCASOCIADO
                            .getValue(), docAsociado);

            registro.getCampos().put(cTipoMovAsoc, "CDC");
        }
        else if ("M".equals(claseDocAsoc))
        {
            docAsociado = "TIPOMOVIMIENTO";
            String clase = "E".equals(claseMov) ? "S" : "E";

            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            FrmdocasociadomovimientosControladorUrlEnum.URL5787
                                            .getValue());
            param.put(FrmdocasociadomovimientosControladorEnum.CLASE
                            .getValue(), clase);
            param.put(FrmdocasociadomovimientosControladorEnum.DOCASOCIADO
                            .getValue(), tipoMov);
            param.put(FrmdocasociadomovimientosControladorEnum.TIPOELEMENTO
                            .getValue(), tipoElementoMov);

            registro.getCampos().put(cTipoMovAsoc, "M");
        }
        else if ("E".equals(claseMov) && "J".equals(claseDocAsoc))
        {
            docAsociado = "CLASEORDEN";
            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            FrmdocasociadomovimientosControladorUrlEnum.URL4055
                                            .getValue());
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), "CCM");
            param.put(FrmdocasociadomovimientosControladorEnum.DOCASOCIADO
                            .getValue(), docAsociado);
            registro.getCampos().put(cTipoMovAsoc, "J");
        }
        else
        {
            claseMovE(param);
        }
    }
    
    private void cargaListaAux(Map<String, Object> param) {
    	String clase = "E".equals(claseMov) ? "S" : "E";

    	urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
    			FrmdocasociadomovimientosControladorUrlEnum.URL41027.getValue());
    	
        param.put(FrmdocasociadomovimientosControladorEnum.CLASE
        				.getValue(), clase);
        param.put(FrmdocasociadomovimientosControladorEnum.TIPOELEMENTO
                        .getValue(), tipoElementoMov);
        param.put(FrmdocasociadomovimientosControladorEnum.FUENTER
                		.getValue(), fuenteR);
        param.put(GeneralParameterEnum.REFERENCIA.getName(), referencia);
        param.put(GeneralParameterEnum.AUXILIAR.getName(), auxiliar);    
        param.put(GeneralParameterEnum.CENTRODECOSTO.getName(), centroCosto);
        param.put(GeneralParameterEnum.PROYECTO.getName(), proyecto);
        param.put(FrmdocasociadomovimientosControladorEnum.BODEGA
        				.getValue(), bodega);
    }
    
    public void cargarListaNumeroDocAsociadoIdi()
    {
    	String urlEnumId;
    	
    	docAsociado = "TIPOMOVIMIENTO";
    	String clase = "E".equals(claseMov) ? "S" : "E";
    	
        urlEnumId = FrmdocasociadomovimientosControladorUrlEnum.URL41024.getValue();

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmdocasociadomovimientosControladorEnum.CLASE
                .getValue(), clase);
        param.put(FrmdocasociadomovimientosControladorEnum.TIPOELEMENTO
                .getValue(), tipoElementoMov);
        
        registro.getCampos().put(cTipoMovAsoc, "M");
        
        listaNumeroDocAsociadoIdi = new RegistroDataModelImpl(urlBean.getUrl(),
                        		  urlBean.getUrlConteo().getUrl(), param, true,
                        		  "NUMERO");
    }

    private void claseMovE(Map<String, Object> param)
    {
        if ("E".equals(claseMov))
        {
            docAsociado = "CLASEORDEN";
            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            FrmdocasociadomovimientosControladorUrlEnum.URL4055
                                            .getValue());
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseDocAsoc);
            param.put(FrmdocasociadomovimientosControladorEnum.DOCASOCIADO
                            .getValue(), docAsociado);

            registro.getCampos().put(cTipoMovAsoc, claseDocAsoc);
        }
    }

    private boolean claseSclaseDocCdc()
    {
        return "S".equals(claseMov) && "CDC".equals(claseDocAsoc);
    }

    private boolean claseSDocCds()
    {
        return "S".equals(claseMov) && "CDS".equals(claseDocAsoc);
    }

    private boolean validaDocClaseDoc()
    {
        return (claseMovSAsoODC())
                        || ("E".equals(claseMov) && ("ODC".equals(claseDocAsoc)
                                        || "C".equals(claseDocAsoc)));
    }

    private boolean claseMovSAsoODC()
    {
        return "S".equals(claseMov) && "ODC".equals(claseDocAsoc);
    }

    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        if (tipoDocAsociado == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1936"));
            return;
        }
        
        if(manEntradaPepsIdi.equals("SI") 
        		&& "S".equals(claseMov) 
        		&& "C".equals(tipoElementoMov))
        {
        	nroDocAsociado = nroDocAsociadoIdi;
        }
        
        String msg = null;
        try
        {
            msg = ejbAlmacenUnoRemote.afectarMovimientoDocAsociado(compania,
                            Integer.parseInt(modulo), claseDocAsoc,
                            tipo, Long.parseLong(nroDocAsociado),
                            tipoMov, Long.parseLong(nroMov),
                            SessionUtil.getUser().getCodigo(),
                            proyecto, operacion, auxAlmacen,
                            rueda, fuenteR, referencia,
                            auxiliar, centroCosto);
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(msg);
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaNumeroDocAsociado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        nroDocAsociado = registroAux.getCampos().get("NUMERO")
                        .toString();
        tipoDocAsociado = registroAux.getCampos().get("CLASEDOC").toString();
        tipo = registroAux.getCampos().get("CLASEDOC").toString();
        String msg = null;
        try
        {
            // validar

            msg = ejbAlmacenUnoRemote.cargarMovimientoDocAsociado(compania,
                            Integer.parseInt(modulo), claseDocAsoc,
                            tipo, Long.parseLong(nroDocAsociado),
                            tipoMov, new BigInteger(nroMov), 0,
                            proyecto, fuenteR, referencia, auxiliar, 
                            centroCosto, bodega);
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(msg);
        reasignarOrigen();

    }
    
    public void seleccionarFilaNumeroDocAsociadoIdi(SelectEvent event) 
    {
    	Registro registroAux = (Registro) event.getObject();
    	nroDocAsociadoIdi = registroAux.getCampos().get("NUMERO")
                .toString();
    	tipoDocAsociado = registroAux.getCampos().get("CLASEDOC").toString();
        tipo = registroAux.getCampos().get("CLASEDOC").toString();
        String msg = null;
        try
        {
            // validar

            msg = ejbAlmacenUnoRemote.cargarMovimientoDocAsociado(compania,
                            Integer.parseInt(modulo), claseDocAsoc,
                            tipo, Long.parseLong(nroDocAsociadoIdi),
                            tipoMov, new BigInteger(nroMov), 0,
                            proyecto, fuenteR, referencia, auxiliar, 
                            centroCosto, bodega);
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(msg);
        reasignarOrigen();
    }

    public void cambiarNumeroDocAsociado()

    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarNumeroDocAsociadoIdi()

    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcantCargarC(int rowNum)
    {
        verMensaje = false;
        // <CODIGO_DESARROLLADO>
        if (Double.parseDouble(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(cCantidadAfect).toString()) > Double
                                        .parseDouble(listaInicial
                                                        .getDatasource()
                                                        .get(rowNum % 10)
                                                        .getCampos()
                                                        .get("SALDOCANT")
                                                        .toString()))
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cCantidadAfect, "1");
            verMensaje = true;
        }
        Double totalCantAfec = Double
                        .parseDouble(listaInicial.getDatasource()
                                        .get(rowNum % 10).getCampos()
                                        .get(cCantidadAfect).toString())
                        * Double.parseDouble(listaInicial.getDatasource()
                                        .get(rowNum % 10).getCampos()
                                        .get("VALORUNITARIO").toString());
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("VALORTOTAL", totalCantAfec);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        nombreColUno = "Clase Doc.";

        if ("M".equals(claseDocAsoc))
        {
            nombreColUno = "Tipo Movimiento";
        }
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcCerrar()
    {
        Map<String, Object> parametros = SessionUtil.getFlash();
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.MOVIMIENTOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
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
    	registro.getCampos().put("VALORBASE", registro.getCampos().get("VALORTOTAL"));
        registro.getCampos().remove("SALDOCANT");
        if (verMensaje)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1935"));
        }
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

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListaNumeroDocAsociado()
    {
        return listaNumeroDocAsociado;
    }

    public void setListaNumeroDocAsociado(
                    RegistroDataModelImpl listaNumeroDocAsociado)
    {
        this.listaNumeroDocAsociado = listaNumeroDocAsociado;
    }
    
    public RegistroDataModelImpl getListaNumeroDocAsociadoIdi() 
    {
        return listaNumeroDocAsociadoIdi;
    }

    public void setListaNumeroDocAsociadoIdi(
    				RegistroDataModelImpl listaNumeroDocAsociadoIdi) 
    {
        this.listaNumeroDocAsociadoIdi = listaNumeroDocAsociadoIdi;
    }

    public String getClaseDocAsoc()
    {
        return claseDocAsoc;
    }

    public void setClaseDocAsoc(String claseDocAsoc)
    {
        this.claseDocAsoc = claseDocAsoc;
    }

    public String getNombreDocAsociado()
    {
        return nombreDocAsociado;
    }

    public void setNombreDocAsociado(String nombreDocAsociado)
    {
        this.nombreDocAsociado = nombreDocAsociado;
    }

    public String getNroDocAsociado()
    {
        return nroDocAsociado;
    }

    public void setNroDocAsociado(String nroDocAsociado)
    {
        this.nroDocAsociado = nroDocAsociado;
    }

    public String getAnoMov()
    {
        return anoMov;
    }

    public void setAnoMov(String anoMov)
    {
        this.anoMov = anoMov;
    }

    public Map<String, Object> getRid()
    {
        return rid;
    }

    public void setRid(Map<String, Object> rid)
    {
        this.rid = rid;
    }

    public String getNombreColUno()
    {
        return nombreColUno;
    }

    public void setNombreColUno(String nombreColUno)
    {
        this.nombreColUno = nombreColUno;
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }
    
    public String getNroDocAsociadoIdi() 
    {
        return nroDocAsociadoIdi;
    }

    public void setNroDocAsociadoIdi(String nroDocAsociadoIdi) 
    {
        this.nroDocAsociadoIdi = nroDocAsociadoIdi;
    }
    
    public boolean isCargarEntradaIdi() {
        return cargarEntradaIdi;
    }

    public void setCargarEntradaIdi(boolean cargarEntradaIdi) {
        this.cargarEntradaIdi = cargarEntradaIdi;
    }
    
    public boolean isCargarEntrada() {
        return cargarEntrada;
    }

    public void setCargarEntrada(boolean cargarEntrada) {
        this.cargarEntrada = cargarEntrada;
    }

    public String getProyecto()
    {
        return proyecto;
    }

    public void setProyecto(String proyecto)
    {
        this.proyecto = proyecto;
    }
    
    public String getOperacion()
    {
        return operacion;
    }

    public void setOperacion(String operacion)
    {
        this.operacion = operacion;
    }
    
    public String getAuxAlmacen()
    {
        return auxAlmacen;
    }

    public void setAuxAlmacen(String auxAlmacen)
    {
        this.auxAlmacen = auxAlmacen;
    }
    
    public String getRueda()
    {
        return rueda;
    }

    public void setRueda(String rueda)
    {
        this.rueda = rueda;
    }
    
    public String getFuenteR()
    {
        return fuenteR;
    }

    public void setFuenteR(String fuenteR)
    {
        this.fuenteR = fuenteR;
    }
    
    public String getReferencia()
    {
        return referencia;
    }

    public void setReferencia(String referencia)
    {
        this.referencia = referencia;
    }
    
    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }
    
    public String getCentroCosto()
    {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto)
    {
        this.centroCosto = centroCosto;
    }
    
    public String getBodega()
    {
        return bodega;
    }

    public void setBodega(String bodega)
    {
        this.bodega = bodega;
    }
    
    public String getCptoMov()
    {
        return cptoMov;
    }

    public void setCptoMov(String cptoMov)
    {
        this.cptoMov = cptoMov;
    }
}
