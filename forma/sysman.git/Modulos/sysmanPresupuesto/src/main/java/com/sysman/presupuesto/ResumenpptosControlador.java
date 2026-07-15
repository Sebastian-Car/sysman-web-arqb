package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.ResumenpptosControladorEnum;
import com.sysman.presupuesto.enums.ResumenpptosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author NGOMEZ
 * @version 1, 23/06/2016
 * 
 * @author eamaya
 * @version 2, 20/04/2017, Proceso de Refactoring
 * 
 * @author eamaya
 * @version 2.1, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario
 */
@ManagedBean
@ViewScoped

public class ResumenpptosControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String op1;
    private String op2;
    private String op3;
    private String op4;
    private String op5;
    private String mesInicial;
    private String mesFinal;
    private String apropiado;
    private String adicion;
    private String reduccion;
    private String traslado;
    private String aplazamiento;
    private String aprDefinitiva;
    private String pagosf;
    private String disponibilidadf;
    private String saldoDisp;
    private String porcDisponib;
    private String dispXReg;
    private String registrosf;
    private String regXPagar;
    private String pactotalf;
    private String pacprogramadof;
    private String rezago;
    private String reintegrosacum;
    private String disponibleAcumulado;
    private String saldoPacAcum;
    private String reof;
    private String apropiacionVigente;
    private String porPagar;
    private String disponibilidadp;
    private String disponibilidada;
    private String registrosp;
    private String registrosa;
    private String reop;
    private String reoa;
    private String pagosp;
    private String pagosa;
    private String pactotalp;
    private String pactotala;
    private String pacApropiadop;
    private String modifpacp;
    private String pacejecutadof;
    private String pacejecutadoP;
    private String pacejecutadoa;
    private String titulo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String anio;
    private String codigo;
    private String nombre;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ResumenpptosControlador
     */
    public ResumenpptosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RESUMENPPTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                anio = (String) parametrosEntrada.get("anio");
                codigo = (String) parametrosEntrada.get("codigo");
                nombre = (String) parametrosEntrada.get("nombre");
                rid = (Map<String, Object>) parametrosEntrada
                                .get("rid");
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ResumenpptosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        mesInicial = "1";
        mesFinal = String
                        .valueOf(SysmanFunciones.getParteFecha(
                                        new Date(),
                                        Calendar.MONTH)
                            + 1);
        titulo = idioma.getString("TB_TB231") + "\n " + codigo + "\n "
            + nombre.toUpperCase();
        cargarDatos();
        // </CODIGO_DESARROLLADO>
    }

    public void cargarModal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     *
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("window.print()");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        if ((mesInicial != null) && !mesInicial.isEmpty() && (mesFinal != null)
            && !mesFinal.isEmpty()) {
            if (Integer.parseInt(mesInicial) <= Integer.parseInt(mesFinal)) {
                cargarDatos();
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB232"));
                limpiarCampos();
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        if ((mesInicial != null) && !mesInicial.isEmpty() && (mesFinal != null)
            && !mesFinal.isEmpty()) {
            if (Integer.parseInt(mesInicial) <= Integer.parseInt(mesFinal)) {
                cargarDatos();
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB232"));
                limpiarCampos();
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public String getApropiado() {
        return apropiado;
    }

    public void setApropiado(String apropiado) {
        this.apropiado = apropiado;
    }

    public String getAdicion() {
        return adicion;
    }

    public void setAdicion(String adicion) {
        this.adicion = adicion;
    }

    public String getReduccion() {
        return reduccion;
    }

    public void setReduccion(String reduccion) {
        this.reduccion = reduccion;
    }

    public String getTraslado() {
        return traslado;
    }

    public void setTraslado(String traslado) {
        this.traslado = traslado;
    }

    public String getAplazamiento() {
        return aplazamiento;
    }

    public void setAplazamiento(String aplazamiento) {
        this.aplazamiento = aplazamiento;
    }

    public String getAprDefinitiva() {
        return aprDefinitiva;
    }

    public void setAprDefinitiva(String aprDefinitiva) {
        this.aprDefinitiva = aprDefinitiva;
    }

    public String getPagosf() {
        return pagosf;
    }

    public void setPagosf(String pagosf) {
        this.pagosf = pagosf;
    }

    public String getDisponibilidadf() {
        return disponibilidadf;
    }

    public void setDisponibilidadf(String disponibilidadf) {
        this.disponibilidadf = disponibilidadf;
    }

    public String getSaldoDisp() {
        return saldoDisp;
    }

    public void setSaldoDisp(String saldoDisp) {
        this.saldoDisp = saldoDisp;
    }

    public String getPorcDisponib() {
        return porcDisponib;
    }

    public void setPorcDisponib(String porcDisponib) {
        this.porcDisponib = porcDisponib;
    }

    public String getDispXReg() {
        return dispXReg;
    }

    public void setDispXReg(String dispXReg) {
        this.dispXReg = dispXReg;
    }

    public String getRegistrosf() {
        return registrosf;
    }

    public void setRegistrosf(String registrosf) {
        this.registrosf = registrosf;
    }

    public String getRegXPagar() {
        return regXPagar;
    }

    public void setRegXPagar(String regXPagar) {
        this.regXPagar = regXPagar;
    }

    public String getPactotalf() {
        return pactotalf;
    }

    public void setPactotalf(String pactotalf) {
        this.pactotalf = pactotalf;
    }

    public String getPacprogramadof() {
        return pacprogramadof;
    }

    public void setPacprogramadof(String pacprogramadof) {
        this.pacprogramadof = pacprogramadof;
    }

    public String getRezago() {
        return rezago;
    }

    public void setRezago(String rezago) {
        this.rezago = rezago;
    }

    public String getReintegrosacum() {
        return reintegrosacum;
    }

    public void setReintegrosacum(String reintegrosacum) {
        this.reintegrosacum = reintegrosacum;
    }

    public String getDisponibleAcumulado() {
        return disponibleAcumulado;
    }

    public void setDisponibleAcumulado(String disponibleAcumulado) {
        this.disponibleAcumulado = disponibleAcumulado;
    }

    public String getSaldoPacAcum() {
        return saldoPacAcum;
    }

    public void setSaldoPacAcum(String saldoPacAcum) {
        this.saldoPacAcum = saldoPacAcum;
    }

    public String getReof() {
        return reof;
    }

    public void setReof(String reof) {
        this.reof = reof;
    }

    public String getApropiacionVigente() {
        return apropiacionVigente;
    }

    public void setApropiacionVigente(String apropiacionVigente) {
        this.apropiacionVigente = apropiacionVigente;
    }

    public String getPorPagar() {
        return porPagar;
    }

    public void setPorPagar(String porPagar) {
        this.porPagar = porPagar;
    }

    public String getDisponibilidadp() {
        return disponibilidadp;
    }

    public void setDisponibilidadp(String disponibilidadp) {
        this.disponibilidadp = disponibilidadp;
    }

    public String getDisponibilidada() {
        return disponibilidada;
    }

    public void setDisponibilidada(String disponibilidada) {
        this.disponibilidada = disponibilidada;
    }

    public String getRegistrosp() {
        return registrosp;
    }

    public void setRegistrosp(String registrosp) {
        this.registrosp = registrosp;
    }

    public String getRegistrosa() {
        return registrosa;
    }

    public void setRegistrosa(String registrosa) {
        this.registrosa = registrosa;
    }

    public String getReop() {
        return reop;
    }

    public void setReop(String reop) {
        this.reop = reop;
    }

    public String getReoa() {
        return reoa;
    }

    public void setReoa(String reoa) {
        this.reoa = reoa;
    }

    public String getPagosp() {
        return pagosp;
    }

    public void setPagosp(String pagosp) {
        this.pagosp = pagosp;
    }

    public String getPagosa() {
        return pagosa;
    }

    public void setPagosa(String pagosa) {
        this.pagosa = pagosa;
    }

    public String getPactotalp() {
        return pactotalp;
    }

    public void setPactotalp(String pactotalp) {
        this.pactotalp = pactotalp;
    }

    public String getPactotala() {
        return pactotala;
    }

    public void setPactotala(String pactotala) {
        this.pactotala = pactotala;
    }

    public String getPacApropiadop() {
        return pacApropiadop;
    }

    public void setPacApropiadop(String pacApropiadop) {
        this.pacApropiadop = pacApropiadop;
    }

    public String getModifpacp() {
        return modifpacp;
    }

    public void setModifpacp(String modifpacp) {
        this.modifpacp = modifpacp;
    }

    public String getPacejecutadof() {
        return pacejecutadof;
    }

    public void setPacejecutadof(String pacejecutadof) {
        this.pacejecutadof = pacejecutadof;
    }

    public String getPacejecutadoP() {
        return pacejecutadoP;
    }

    public void setPacejecutadoP(String pacejecutadoP) {
        this.pacejecutadoP = pacejecutadoP;
    }

    public String getPacejecutadoa() {
        return pacejecutadoa;
    }

    public void setPacejecutadoa(String pacejecutadoa) {
        this.pacejecutadoa = pacejecutadoa;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getOp1() {
        return op1;
    }

    public void setOp1(String op1) {
        this.op1 = op1;
    }

    public String getOp2() {
        return op2;
    }

    public void setOp2(String op2) {
        this.op2 = op2;
    }

    public String getOp3() {
        return op3;
    }

    public void setOp3(String op3) {
        this.op3 = op3;
    }

    public String getOp4() {
        return op4;
    }

    public void setOp4(String op4) {
        this.op4 = op4;
    }

    public String getOp5() {
        return op5;
    }

    public void setOp5(String op5) {
        this.op5 = op5;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void cargarDatos() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        param.put(ResumenpptosControladorEnum.PARAM0.getValue(), mesInicial);
        param.put(ResumenpptosControladorEnum.PARAM1.getValue(), mesFinal);

        Registro regAux;
        try {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenpptosControladorUrlEnum.URL6969
                                                                            .getValue())
                                            .getUrl(), param));

            apropiado = regAux.getCampos().get("APRDEFINITIVA").toString();
            adicion = regAux.getCampos().get("ADICION").toString();
            reduccion = regAux.getCampos().get("REDUCCION").toString();
            traslado = regAux.getCampos().get("TRASLADO").toString();
            aprDefinitiva = regAux.getCampos().get("APRDEFINITIVACAMPO")
                            .toString();
            aplazamiento = regAux.getCampos().get("APLAZAMIENTO").toString();
            apropiacionVigente = regAux.getCampos().get("APROPIACIONVIGENTE")
                            .toString();
            reintegrosacum = regAux.getCampos().get("REINTEGROSACUM")
                            .toString();
            disponibleAcumulado = regAux.getCampos().get("DISPONIBLEACUMULADO")
                            .toString();
            pactotala = regAux.getCampos().get("PACTOTALA").toString();
            pacApropiadop = regAux.getCampos().get("PAC_APROPIADOP").toString();
            modifpacp = regAux.getCampos().get("MODIFPACP").toString();
            pactotalp = regAux.getCampos().get("PACTOTALP").toString();
            pactotalf = regAux.getCampos().get("PACTOTALF").toString();
            saldoPacAcum = regAux.getCampos().get("SALDOPACACUMULADO")
                            .toString();
            pacprogramadof = regAux.getCampos().get("PACPROGRAMADOF")
                            .toString();
            rezago = regAux.getCampos().get("SALDOPACXINCORPORAR").toString();
            disponibilidada = regAux.getCampos().get("DISPONIBILIDADA")
                            .toString();
            disponibilidadp = regAux.getCampos().get("DISPONIBILIDADP")
                            .toString();
            disponibilidadf = regAux.getCampos().get("DISPONIBILIDADF")
                            .toString();
            porcDisponib = regAux.getCampos().get("PORUTILIZACION").toString();
            saldoDisp = regAux.getCampos().get("SALDODISPONIBLE").toString();
            dispXReg = regAux.getCampos().get("DISPXREG").toString();
            registrosa = regAux.getCampos().get("REGISTROSA").toString();
            registrosp = regAux.getCampos().get("REGISTROSP").toString();
            registrosf = regAux.getCampos().get("REGISTROSF").toString();
            regXPagar = regAux.getCampos().get("REGXPAGAR").toString();
            reoa = regAux.getCampos().get("REOA").toString();
            reop = regAux.getCampos().get("REOP").toString();
            reof = regAux.getCampos().get("REOF").toString();
            porPagar = regAux.getCampos().get("PORPAGAR").toString();
            pagosa = regAux.getCampos().get("PAGOSA").toString();
            pagosp = regAux.getCampos().get("PAGOSP").toString();
            pagosf = regAux.getCampos().get("PAGOSF").toString();
            pacejecutadoa = regAux.getCampos().get("PACEJECUTADOA").toString();
            pacejecutadoP = regAux.getCampos().get("PACEJECUTADO_P").toString();
            pacejecutadof = regAux.getCampos().get("PACEJECUTADOF").toString();

        }
        catch (

        SystemException e)

        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void limpiarCampos() {

        apropiado = null;
        adicion = null;
        reduccion = null;
        traslado = null;
        aprDefinitiva = null;
        aplazamiento = null;
        apropiacionVigente = null;
        reintegrosacum = null;
        disponibleAcumulado = null;
        pactotala = null;
        pacApropiadop = null;
        modifpacp = null;
        pactotalp = null;
        pactotalf = null;
        saldoPacAcum = null;
        pacprogramadof = null;
        rezago = null;
        disponibilidada = null;
        disponibilidadp = null;
        disponibilidadf = null;
        porcDisponib = null;
        saldoDisp = null;
        dispXReg = null;
        registrosa = null;
        registrosp = null;
        registrosf = null;
        regXPagar = null;
        reoa = null;
        reop = null;
        reof = null;
        porPagar = null;
        pagosa = null;
        pagosp = null;
        pagosf = null;
        pacejecutadoa = null;
        pacejecutadoP = null;
        pacejecutadof = null;

    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>

    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>

    }

    @Override
    public void iniciarListasSub() {
        // <CODIGO_DESARROLLADO>

    }

    @Override
    public void iniciarListas() {
        // <CODIGO_DESARROLLADO>

    }

    @Override
    public void asignarOrigenDatos() {
        // <CODIGO_DESARROLLADO>

    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
        return false;
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.redireccionar("/planpresupuestalpto.sysman");
        // <CODIGO_DESARROLLADO>
    }
}
