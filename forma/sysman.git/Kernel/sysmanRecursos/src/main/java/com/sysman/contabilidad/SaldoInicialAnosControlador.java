package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroGeneralRemote;
import com.sysman.contabilidad.enums.SaldoInicialAnosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.io.IOException;
import java.sql.Clob;
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
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 1, 04/03/2016
 * 
 * @author eamaya
 * @version 2, 06/04/2017 Proceso de Refactoring y Correciones
 * SonarLint. Se crearon las funciones
 * PCK_CONTABILIDAD.FC_VERIFICAPERIODO y
 * PCK_CONTABILIDAD.FC_VERIFICAINCONSISTENCIAS
 * 
 * @author eamaya 3 ,15/05/2017 Se cambió el llamado de
 * PCK_CONTABILIDAD.FC_VERIFICAPERIODO por
 * PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOANO
 * 
 * @version 4.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Remover y/o reemplazar los llamados a ConectorPool por el esquema.
 * 
 */
@ManagedBean
@ViewScoped
public class SaldoInicialAnosControlador extends BeanBaseModal {

    private final String compania;
    private String ano;
    private String rta;

    private boolean existeInconsistencias;
    private int contador;
    private List<Registro> listaano;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbContabilidadCeroGeneralRemote ejbContabilidadCeroGeneral;

    /**
     * Creates a new instance of SaldoInicialAnosControlador
     */
    public SaldoInicialAnosControlador() {
        super();

        // 547
        numFormulario = GeneralCodigoFormaEnum.SALDO_INICIAL_ANOS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        contador = 0;

        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(SaldoInicialAnosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        existeInconsistencias = false;
        cargarListaano();
        abrirFormulario();
    }

    public void cargarListaano() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SaldoInicialAnosControladorUrlEnum.URL2502
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirAceptar() {

        existeInconsistencias = false;
        oprimirverificarInconsistencias();

        if (existeInconsistencias && (contador <= 1)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            "TB_TB925"));

            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ano", ano);

        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm("554");
        direccionador.setParametros(parametros);
        RequestContext.getCurrentInstance().closeDialog(direccionador);

    }

    public void oprimirverificarInconsistencias() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        Clob cadena;
        String aux;

        try {

            aux = ejbContabilidadCeroGeneral.verificarInconsistencias(compania,
                            Integer.parseInt(ano));
            contador = contador + 1;

            if ("0".equals(aux)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString(
                                "TB_TB924"));
            }
            else {

                existeInconsistencias = true;
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                JsfUtil.serializarPlano(aux),
                                "Inconsistencias_saldos_iniciales.dat");
            }

        }
        catch (NumberFormatException | SystemException | JRException
                        | IOException e) {
            logger.error(e.getMessage(),
                            e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarano() {
        // <CODIGO_DESARROLLADO>
        contador = 0;
        existeInconsistencias = false;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getRta() {
        return rta;
    }

    public void setRta(String rta) {
        this.rta = rta;
    }

    public List<Registro> getListaano() {
        return listaano;
    }

    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
