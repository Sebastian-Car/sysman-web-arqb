package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCuatroRemote;
import com.sysman.contabilidad.enums.ReversarCierresControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author sdaza
 * @version 1, 28/04/2016
 * @version 2, 12/04/2017 Refactorizaci�n mzanguna
 * @version 3, 20/04/2017 Cambio EJB.
 */
@ManagedBean
@ViewScoped

public class ReversarCierresControlador extends BeanBaseModal {
    private final String compania;
    private String modulo;
    private String ano;
    private String tipoComprobante;
    private String nroComprobante;
    private List<Registro> listaAno;

    @EJB
    private EjbContabilidadCuatroRemote ejbContabilidadCuatro;

    /**
     * Creates a new instance of ReversarCierresControlador
     */
    public ReversarCierresControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.REVERSAR_CIERRES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaAno();
        abrirFormulario();
        tipoComprobante = "CIE";
        cambiarAno();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ReversarCierresControladorUrlEnum.URL4680
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        boolean mensaje = false;        
        String mensajeTiempo = " ";
        long minutos = 0 ;
        long segundos = 0 ;
        try {
        	LocalTime tiempoInicial = LocalTime.now();
            mensaje = ejbContabilidadCuatro.reversarCierreContable(compania,
                            Integer.parseInt(ano), tipoComprobante,
                            BigInteger.valueOf(Long.parseLong(nroComprobante)),
                            modulo);
            LocalTime tiempoFinal = LocalTime.now();
            Duration diferencia = Duration.between(tiempoInicial, tiempoFinal);	            
            minutos = diferencia.toMinutes() % 60;
            segundos = diferencia.getSeconds() % 60;
            if(minutos == 0) {
	            mensajeTiempo = "Se ha eliminado exitosamente el comprobante tipo " + tipoComprobante + " numero " + nroComprobante + " en un tiempo de " + segundos + " segundos";
	            }else { 
		        mensajeTiempo = "Se ha eliminado exitosamente el comprobante tipo " + tipoComprobante + " numero " + nroComprobante + " en un tiempo de " + minutos + " minutos " + segundos + " segundos";
	            }
            }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (mensaje) {
        	JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1754"));
        	JsfUtil.agregarMensajeInformativo(mensajeTiempo);         
        	}      	
        
        // </CODIGO_DESARROLLADO>
    }
    public void cambiarAno() {
        Date fechaInterface;
        try {
            if (ano != null) {
                fechaInterface = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                                .convertirAFecha("31/12/" + ano));
                nroComprobante = SysmanFunciones.padl(ano, 4, "0") + "12"
                    + SysmanFunciones.padl(
                                    String.valueOf(SysmanFunciones
                                                    .dia(fechaInterface)),
                                    4, "0");
            }
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getNroComprobante() {
        return nroComprobante;
    }

    public void setNroComprobante(String nroComprobante) {
        this.nroComprobante = nroComprobante;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
}
