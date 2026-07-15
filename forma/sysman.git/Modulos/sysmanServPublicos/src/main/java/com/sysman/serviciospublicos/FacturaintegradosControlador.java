package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

/**
 *
 * @author acaceres
 * @version 1, 06/09/2016
 *
 * @author spina - refactorizo conexiones
 * @version 2, 12/06/2017
 *
 * -- Modificado por lcortes 20/06/2017. Se cambia el codigo del
 * formulario a direccionar del boton PQR para que redireccione al
 * formulario usuarios.
 */
@ManagedBean
@ViewScoped
public class FacturaintegradosControlador extends BeanBaseDatosAcme {
    private final String compania;
    private final String modulo;
    private String ciclo;
    private String codigoRuta;
    private String ano;
    private String periodo;
    private String notacredito;
    private String bancoperproceso;
    private String txtFimm = "";
    private String codigoInterno;
    private String numeroFactura = "";
    private String lectura;
    private String periodosNoCobradosFac;
    private String periodosNoCobroFin;
    private String fechaPagoPerProceso;
    private double totFacturaPerActual;
    private boolean formSaldoCredito;
    private boolean autorizarBorrado;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    @SuppressWarnings("unchecked")
    public FacturaintegradosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FACTURAINTEGRADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ciclo = (String) parametrosEntrada.get("ciclo");
                codigoRuta = (String) parametrosEntrada.get("codigoruta");
                ano = (String) parametrosEntrada.get("anio");
                rid = (HashMap<String, Object>) parametrosEntrada.get("rid");
                if ((rid != null) && !rid.isEmpty()) {
                    ciclo = rid.get("CICLO").toString();
                }
            }

        }
        catch (Exception ex) {
            Logger.getLogger(FacturaintegradosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        tabla = "SP_USUARIO";
        buscarLlave();
        asignarOrigenDatos();
        reasignarOrigenGrilla();
    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "SELECT SP_USUARIO.COMPANIA, " +
            "        SP_USUARIO.CICLO, " +
            "        SP_USUARIO.CODIGORUTA, " +
            "        SP_USUARIO.ANO, " +
            "        SP_USUARIO.PERIODO," +
            "        SP_USUARIO.NOTACREDITO, " +
            "        SP_USUARIO.BANCOPERPROCESO, " +
            "        SP_USUARIO.CODIGOINTERNO, " +
            "        SP_USUARIO.PERIODOSNOCOBROFAC, " +
            "        SP_USUARIO.LECTURA, " +
            "        SP_USUARIO.PERIODOSNOCOBROFIN, " +
            "        SP_USUARIO.FECHAPAGOPERPROCESO " +
            " FROM   SP_USUARIO ";
    }

    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = "SELECT SP_USUARIO.COMPANIA, " +
            "        SP_USUARIO.CICLO, " +
            "        SP_USUARIO.CODIGORUTA, " +
            "        SP_USUARIO.ANO, " +
            "        SP_USUARIO.PERIODO" +
            " FROM   SP_USUARIO " +
            " WHERE  SP_USUARIO.COMPANIA = '" + compania + "'" +
            " AND    SP_USUARIO.CICLO = " + ciclo + "";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenGrilla);
        }
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    /**
     * Metodo usado para enviar los parametros necesarios al
     * redireccionar al formulario Financiables
     */
    public void oprimirCalcular() {
        // <CODIGO_DESARROLLADO>
        codigoRuta = registro.getCampos().get("CODIGORUTA").toString();
        ano = registro.getCampos().get("ANO").toString();
        periodo = registro.getCampos().get("PERIODO").toString();
        codigoInterno = registro.getCampos().get("CODIGOINTERNO").toString();
        bancoperproceso = registro.getCampos().get("BANCOPERPROCESO") == null
            ? ""
            : registro.getCampos().get("BANCOPERPROCESO")
                            .toString();
        periodosNoCobradosFac = registro.getCampos()
                        .get("PERIODOSNOCOBROFAC") == null ? ""
                            : registro.getCampos().get("PERIODOSNOCOBROFAC")
                                            .toString();
        lectura = registro.getCampos().get("LECTURA") == null ? ""
            : registro.getCampos().get("LECTURA").toString();
        periodosNoCobroFin = registro.getCampos()
                        .get("PERIODOSNOCOBROFIN") == null ? ""
                            : registro.getCampos()
                                            .get("PERIODOSNOCOBROFIN")
                                            .toString();
        String[] campos = { "ciclo", "codigoRuta", "ano", "periodo",
                            "codigoInterno", "bancoperproceso",
                            "periodosNoCobradosFac", "txtFimm",
                            "numeroFactura", "lectura", "periodosNoCobroFin" };
        String[] valores = { ciclo, codigoRuta, ano, periodo, codigoInterno,
                             bancoperproceso, periodosNoCobradosFac, txtFimm,
                             numeroFactura, lectura, periodosNoCobroFin };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.FINANCIABLESFACTURAS_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo usado para enviar los parametros necesarios para
     * redireccionar al formulario PQR.
     */
    public void oprimirPQR() {
        // <CODIGO_DESARROLLADO>
        String parametro;
        try {
            parametro = Acciones.getParametroOriginal(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "MANEJA HISTORICOS DE PROBLEMAS AFORO", modulo,
                            "SYSDATE");

            if (parametro == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1631"));
                return;

            }
        }
        catch (NamingException | SQLException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        codigoRuta = registro.getCampos().get("CODIGORUTA").toString();
        ano = registro.getCampos().get("ANO").toString();
        periodo = registro.getCampos().get("PERIODO").toString();
        String[] campos = { "ciclo", "codigoRuta", "ano", "periodo" };
        String[] valores = { ciclo, codigoRuta, ano, periodo };
        String form = Integer
                        .toString(GeneralCodigoFormaEnum.USUARIOS_CONTROLADOR
                                        .getCodigo());

        // SessionUtil.cargarModalDatosFlashCerrar(String.valueOf(GeneralCodigoFormaEnum.PQRFACTURAS_CONTROLADOR.getCodigo()),
        // modulo, campos,
        // valores);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(form);
        SessionUtil.redireccionarPorFormulario(modulo, form, campos, valores,
                        true);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Envio de parametros para realizar la carga al formulario
     * "Facturasaldocredito" (1087)
     */
    public void oprimirSaldoCredito() {
        codigoRuta = registro.getCampos().get("CODIGORUTA").toString();
        ano = registro.getCampos().get("ANO").toString();
        periodo = registro.getCampos().get("PERIODO").toString();
        notacredito = registro.getCampos().get("NOTACREDITO").toString();
        bancoperproceso = SysmanFunciones
                        .nvl(registro.getCampos().get("BANCOPERPROCESO"), " ")
                        .toString();
        String[] campos = { "ciclo", "codigoruta", "ano", "periodo",
                            "notacredito", "bancoperproceso" };
        Object[] valores = { ciclo, codigoRuta, ano, periodo, notacredito,
                             bancoperproceso };
        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.FACTURASALDOCREDITOS_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos,
                        valores);
    }

    /**
     * Envio de parametros para realizar la carga al formulario
     * "FacturaOtros" (1113)
     */
    public void oprimirOtros() {
        codigoRuta = registro.getCampos().get("CODIGORUTA").toString();
        ano = registro.getCampos().get("ANO").toString();
        periodo = registro.getCampos().get("PERIODO").toString();
        lectura = registro.getCampos().get("LECTURA") == null
            ? ""
            : registro.getCampos().get("LECTURA").toString();
        String[] campos = { "ciclo", "codigoRuta", "ano", "periodo",
                            "lectura" };
        Object[] valores = { ciclo, codigoRuta, ano, periodo, lectura };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.FACTURA_OTROS_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos,
                        valores);
    }

    /**
     * Metodo usado para enviar parametros y redireccionar al
     * formulario Abonos
     */
    public void oprimirAbonos() {
        // <CODIGO_DESARROLLADO>
        autorizarBorrado = false;
        totFacturaPerActual = 0;
        codigoRuta = registro.getCampos().get("CODIGORUTA").toString();
        ano = registro.getCampos().get("ANO").toString();
        periodo = registro.getCampos().get("PERIODO").toString();
        bancoperproceso = SysmanFunciones
                        .nvl(registro.getCampos().get("BANCOPERPROCESO"), " ")
                        .toString();
        codigoInterno = registro.getCampos().get("CODIGOINTERNO").toString();

        String[] campos = { "ciclo", "codigoruta", "ano", "periodo",
                            "bancoperproceso", "codigoInterno", "txtFimm",
                            "autorizarBorrado", "totFacturaPerActual" };
        Object[] valores = { ciclo, codigoRuta, ano, periodo, bancoperproceso,
                             codigoInterno, txtFimm, autorizarBorrado,
                             totFacturaPerActual };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.ABONOSFACTURAS_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Envio de parametros para realizar la carga al formulario
     * "Facturaconvenios" (1131)
     */
    public void oprimirConvenios() {
        codigoRuta = registro.getCampos().get("CODIGORUTA").toString();
        ano = registro.getCampos().get("ANO").toString();
        periodo = registro.getCampos().get("PERIODO").toString();
        periodo = registro.getCampos().get("PERIODO").toString();
        fechaPagoPerProceso = registro.getCampos().get("FECHAPAGOPERPROCESO")
                        .toString();
        String[] campos = { "ciclo", "codigoruta", "ano", "periodo",
                            "fechaPagoPerProceso" };
        Object[] valores = { ciclo, codigoRuta, ano, periodo,
                             fechaPagoPerProceso };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.FACTURA_CONVENIOS_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos,
                        valores);
    }

    public void oprimirAuditoria() {
        codigoRuta = registro.getCampos().get("CODIGORUTA").toString();
        String[] campos = { "ciclo", "codigoruta" };
        Object[] valores = { ciclo, codigoRuta };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.AUDITORIAUSUARIOS_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos,
                        valores);
    }

    public void oprimirHistoricosServicio() {
        codigoRuta = registro.getCampos().get("CODIGORUTA").toString();
        String[] campos = { "ciclo", "codigoruta" };
        Object[] valores = { ciclo, codigoRuta };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.HISTORIANOVEDADES_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos,
                        valores);
    }

    public void oprimirMultiusuario() {
        codigoRuta = registro.getCampos().get("CODIGORUTA").toString();
        String[] campos = { "ciclo", "codigoruta" };
        Object[] valores = { ciclo, codigoRuta };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.MULTIUSUARIOS_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos,
                        valores);
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
    // <SET_GET_ATRIBUTOS>

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getNotacredito() {
        return notacredito;
    }

    public void setNotacredito(String notacredito) {
        this.notacredito = notacredito;
    }

    public String getBancoperproceso() {
        return bancoperproceso;
    }

    public void setBancoperproceso(String bancoperproceso) {
        this.bancoperproceso = bancoperproceso;
    }

    public boolean isFormSaldoCredito() {
        return formSaldoCredito;
    }

    public void setFormSaldoCredito(boolean formSaldoCredito) {
        this.formSaldoCredito = formSaldoCredito;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getPeriodosNoCobradosFac() {
        return periodosNoCobradosFac;
    }

    public void setPeriodosNoCobradosFac(String periodosNoCobradosFac) {
        this.periodosNoCobradosFac = periodosNoCobradosFac;
    }

    public String getTxtFimm() {
        return txtFimm;
    }

    public void setTxtFimm(String txtFimm) {
        this.txtFimm = txtFimm;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getFechaPagoPerProceso() {
        return fechaPagoPerProceso;
    }

    public void setFechaPagoPerProceso(String fechaPagoPerProceso) {
        this.fechaPagoPerProceso = fechaPagoPerProceso;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
