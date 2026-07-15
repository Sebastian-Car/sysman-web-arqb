package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 09/09/2016
 * 
 * @author eamaya
 * @version 2.0, Proceso de Refactoring y Manejo de EJBs, las
 * consultas quemadas no se cambiaron por DSS sino se crearon las
 * listas por medio de Registros
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped

public class LmatriculasactivadasControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private final String cEstado;
    // <DECLARAR_ATRIBUTOS>
    private String mostrar;
    private String titulo;
    private String estado;
    private Date fechaInicial;
    private Date fechaFinal;
    private String codigoMostrar;
    private String codigoFormato;
    private StreamedContent archivoDescarga;

    private String tipoFormulario;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listacmbMostrar;
    private List<Registro> listacmbFormato;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of LmatriculasactivadasControlador
     */
    public LmatriculasactivadasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cEstado = "estado";
        try {
            numFormulario = GeneralCodigoFormaEnum.LMATRICULASACTIVADAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LmatriculasactivadasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        cargarListacmbMostrar();
        cargarListacmbFormato();

    }

    @Override
    public void abrirFormulario() {

        if ("74070119".equals(SessionUtil.getMenuActual())) {

            titulo = idioma.getString("TB_TB3193");

            tipoFormulario = "1";

        }
        else {
            titulo = idioma.getString("TB_TB3195");

            tipoFormulario = "0";

        }
        fechaInicial = new Date();
        fechaFinal = new Date();
        codigoMostrar = "";
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListacmbMostrar() {

        if (!parametro()) {
            listacmbMostrar = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                            codigoMostrar);
        }

    }

    public void cargarListacmbFormato() {
        if ("1".equals(tipoFormulario)) {
            HashMap<String, Object> hashm1 = new HashMap<>();
            hashm1.put(GeneralParameterEnum.NOMBRE.getName(), "Activadas");
            hashm1.put(GeneralParameterEnum.CODIGO.getName(), "-1");

            HashMap<String, Object> hashm2 = new HashMap<>();
            hashm2.put(GeneralParameterEnum.NOMBRE.getName(), "Desactivadas");
            hashm2.put(GeneralParameterEnum.CODIGO.getName(), "0");
            Registro reg = new Registro(hashm1);
            Registro reg1 = new Registro(hashm2);

            listacmbFormato = new ArrayList<>();

            listacmbFormato.add(reg);
            listacmbFormato.add(reg1);
        }
        else {

            HashMap<String, Object> hashm1 = new HashMap<>();
            hashm1.put(GeneralParameterEnum.NOMBRE.getName(), "Aprobadas");
            hashm1.put(GeneralParameterEnum.CODIGO.getName(), "1");

            HashMap<String, Object> hashm2 = new HashMap<>();
            hashm2.put(GeneralParameterEnum.NOMBRE.getName(), "Rechazadas");
            hashm2.put(GeneralParameterEnum.CODIGO.getName(), "2");

            HashMap<String, Object> hashm3 = new HashMap<>();
            hashm3.put(GeneralParameterEnum.NOMBRE.getName(), "Pendientes");
            hashm3.put(GeneralParameterEnum.CODIGO.getName(), "3");

            Registro reg = new Registro(hashm1);
            Registro reg1 = new Registro(hashm2);
            Registro reg2 = new Registro(hashm3);

            listacmbFormato = new ArrayList<>();

            listacmbFormato.add(reg);
            listacmbFormato.add(reg1);
            listacmbFormato.add(reg2);

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            String tituloReporte;
            String condicion;
            String conEst;
            String estt;
            String est;
            if ("05".equals(mostrar)) {
                condicion = " ";

            }
            else {
                condicion = "AND SP_SOLICITUDSERVICIO.CLASESOLICITUD = '"
                    + mostrar + "'";

            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("condicion", condicion);
            if ("1".equals(tipoFormulario)) {
                if ("0".equals(estado)) {

                    conEst = "0";
                    estt = "AND SP_SOLICITUDSERVICIO.ACTIVADO  = " + conEst
                        + "";
                    reemplazar.put(cEstado, estt);
                    est = "Desactivadas";

                }
                else {

                    conEst = "-1";
                    estt = "AND SP_SOLICITUDSERVICIO.ACTIVADO  = " + conEst
                        + "";
                    reemplazar.put(cEstado, estt);
                    est = "Activadas";

                }

                tituloReporte = idioma.getString("TB_TB3194");
            }
            else {
                if ("1".equals(estado)) {
                    conEst = "1";
                    estt = "  AND SP_SOLICITUDSERVICIO.ESTADOAPROBADO  = "
                        + conEst + "";
                    reemplazar.put(cEstado, estt);
                    est = "Aprobadas";

                }
                else if ("2".equals(estado)) {
                    conEst = "2";
                    estt = "AND SP_SOLICITUDSERVICIO.ESTADOAPROBADO  = "
                        + conEst + "";
                    reemplazar.put(cEstado, estt);
                    est = "Rechazadas";
                }
                else {
                    conEst = "3";
                    estt = "AND SP_SOLICITUDSERVICIO.ESTADOAPROBADO  = "
                        + conEst + "";
                    reemplazar.put(cEstado, estt);
                    est = "Pendientes";
                }

                tituloReporte = idioma.getString("TB_TB3195");
            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_ENCABEZADO", "de "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial) + " a "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal) + " Tipo: "
                + est);
            parametros.put("PR_TITULO", tituloReporte);

            Reporteador.resuelveConsulta("001066LSolicitudesActivadas",
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001066LSolicitudesActivadas", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SysmanException | ParseException | IOException
                        | JRException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(LmatriculasactivadasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void mensajesInicioModal() {
        parametro();
    }

    // <METODOS_CAMBIAR>
    public boolean parametro() {
        String strNombreServicio = "";
        String strNombreServicio1 = "";
        String strNombreServicio2 = "";
        try {
            String strParamServicio = ejbSysmanUtil.consultarParametro(compania,
                            "CAMBIAR NOMBRE SERVICIO ACUEDUCTO", modulo,
                            new Date(), false);

            String paramAcueducto = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE SERVICIO A REMPLAZAR ACUEDUCTO", modulo,
                            new Date(), false);

            String strParamServicio1 = ejbSysmanUtil.consultarParametro(
                            compania,
                            "CAMBIAR NOMBRE SERVICIO ALCANTARILLADO", modulo,
                            new Date(), false);

            String alcantarillado = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE SERVICIO A REMPLAZAR ALCANTARILLADO",
                            modulo,
                            new Date(), false);

            String strParamServicio2 = ejbSysmanUtil.consultarParametro(
                            compania,
                            "CAMBIAR NOMBRE SERVICIO ASEO",
                            modulo,
                            new Date(), false);

            String aseo = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE SERVICIO A REMPLAZAR ASEO",
                            modulo,
                            new Date(), false);

            if (strParamServicio == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1470"));
                return false;
            }
            else {
                strNombreServicio = validaParametro1(strParamServicio,
                                paramAcueducto);
                if (strNombreServicio == null) {
                    return false;
                }
            }
            if (strParamServicio1 == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1472"));
                return false;
            }
            else {
                strNombreServicio1 = validaParametro2(strParamServicio1,
                                alcantarillado);
                if (strNombreServicio1 == null) {
                    return false;
                }
            }
            if (strParamServicio2 == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1475"));
                return false;
            }
            else {
                strNombreServicio2 = validaParametro3(strParamServicio2,
                                aseo);
                if (strNombreServicio2 == null) {
                    return false;
                }
            }

            HashMap<String, Object> hashm1 = new HashMap<>();
            hashm1.put(GeneralParameterEnum.NOMBRE.getName(),
                            strNombreServicio);
            hashm1.put(GeneralParameterEnum.CODIGO.getName(), "01");

            HashMap<String, Object> hashm2 = new HashMap<>();
            hashm2.put(GeneralParameterEnum.NOMBRE.getName(),
                            strNombreServicio1);
            hashm2.put(GeneralParameterEnum.CODIGO.getName(), "02");

            HashMap<String, Object> hashm3 = new HashMap<>();
            hashm3.put(GeneralParameterEnum.NOMBRE.getName(),
                            strNombreServicio2);
            hashm3.put(GeneralParameterEnum.CODIGO.getName(), "03");

            HashMap<String, Object> hashm4 = new HashMap<>();
            hashm4.put(GeneralParameterEnum.NOMBRE.getName(),
                            "Varios");
            hashm4.put(GeneralParameterEnum.CODIGO.getName(), "04");

            HashMap<String, Object> hashm5 = new HashMap<>();
            hashm5.put(GeneralParameterEnum.NOMBRE.getName(),
                            "Todos");
            hashm5.put(GeneralParameterEnum.CODIGO.getName(), "05");

            Registro reg = new Registro(hashm1);
            Registro reg1 = new Registro(hashm2);
            Registro reg2 = new Registro(hashm3);
            Registro reg3 = new Registro(hashm4);
            Registro reg4 = new Registro(hashm5);

            listacmbMostrar = new ArrayList<>();

            listacmbMostrar.add(reg);
            listacmbMostrar.add(reg1);
            listacmbMostrar.add(reg2);
            listacmbMostrar.add(reg3);
            listacmbMostrar.add(reg4);
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(LmatriculasactivadasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        return true;

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    private String validaParametro3(String strParamServicio2, String aseo) {
        if ("SI".equals(strParamServicio2)) {
            if (aseo == null) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB1476"));
                return null;
            }
            else {
                return aseo;
            }
        }
        else {
            return "Aseo";
        }
    }

    private String validaParametro2(String strParamServicio1,
        String alcantarillado) {
        if ("SI".equals(strParamServicio1)) {
            if (alcantarillado == null) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB1474"));
                return null;
            }
            else {
                return alcantarillado;
            }
        }
        else {
            return "Alcantarillado";
        }
    }

    private String validaParametro1(String strParamServicio,
        String paramAcueducto) {
        if ("SI".equals(strParamServicio)) {
            if (paramAcueducto == null) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB1471"));
                return null;
            }
            else {
                return paramAcueducto;
            }
        }
        else {
            return "Acueducto";
        }
    }

    public String getMostrar() {
        return mostrar;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setMostrar(String mostrar) {
        this.mostrar = mostrar;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getCodigoFormato() {
        return codigoFormato;
    }

    public void setCodigoFormato(String codigoFormato) {
        this.codigoFormato = codigoFormato;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    public List<Registro> getListacmbMostrar() {
        return listacmbMostrar;
    }

    public List<Registro> getListacmbFormato() {
        return listacmbFormato;
    }

    public void setListacmbFormato(List<Registro> listacmbFormato) {
        this.listacmbFormato = listacmbFormato;
    }

    public void setListacmbMostrar(List<Registro> listacmbMostrar) {
        this.listacmbMostrar = listacmbMostrar;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
