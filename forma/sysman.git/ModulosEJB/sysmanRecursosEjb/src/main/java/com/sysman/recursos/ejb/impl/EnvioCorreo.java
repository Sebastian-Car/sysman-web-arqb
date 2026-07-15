package com.sysman.recursos.ejb.impl;

import static co.com.sysman.conector.DriverType.Database;
import static com.sysman.recursos.ejb.impl.QueueProperties.CANNOT_SEND_EMAIL;
import static com.sysman.recursos.ejb.impl.QueueProperties.JAVA_JBOSS_MAIL_GMAIL;
import static com.sysman.recursos.ejb.impl.QueueProperties.KEY_SQL_1;
import static com.sysman.recursos.ejb.impl.QueueProperties.MAIL_SMTP_USER;
import static com.sysman.recursos.ejb.impl.QueueProperties.REPORTE_NULL;
import static com.sysman.recursos.ejb.impl.QueueProperties.TEXT_HTML_CHARSET_UTF_8;

import java.util.Date;
import java.util.Map;

// import com.itextpdf.text.Document;
// import com.itextpdf.text.pdf.PdfWriter;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import co.com.sysman.acceso.IDatoServicio;
import co.com.sysman.colas.procesador.EnumParamProcesador;
import co.com.sysman.comun.excepcion.PersistenciaExcepcion;
import co.com.sysman.conector.DriverConnector;
import co.com.sysman.conector.driver.DBIrisConector;

/**
 * Clase que envia Emails por medio del protocolo de transferecia
 * simple de correo smtp
 */

public class EnvioCorreo {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RecibidorColas.class);
    /**
     * Metodo genera y procesa el correo electronico teniendo encuenta
     * los parametros de entrada como destino, asunto y el cuerpo del
     * mensaje
     */
    @Resource(name = JAVA_JBOSS_MAIL_GMAIL)
    private Session session;
    /**
     * Atributo que accede a la persistencia con el fin de hacer
     * transacciones con la BD
     */

    @Inject
    private IDatoServicio servicio;
    /**
     * Conector a la base de datos en la que se haran transacciones
     */
    @Inject
    private @DriverConnector(type = Database) DBIrisConector conector;

    /**
     * Metodo encargado de enviar correo electroncio por medio del
     * resource
     * 
     * @param addres
     * direccion de correo electronico de destino
     * @param subject
     * asunto del correo
     * @param text
     * Cuerpo del correo
     */

    public void send(Map<String, Object> mapMessage) {
        LOG.info("Entro a: <<EnvioCorreogenerateAndSendEmail>> parametros / message  ->> {} ", mapMessage);

        try {

            String address = (String) mapMessage.get(EnumParamProcesador.KEY_DESTINO.name());
            String subject = (String) mapMessage.get(EnumParamProcesador.KEY_ASUNTO.name());
            String text = (String) mapMessage.get(EnumParamProcesador.KEY_CUERPO_CORREO.name());
            String attached = (String) mapMessage.get(EnumParamProcesador.KEY_RUTA_ADJUNTO.name());
            byte[] bytes = (byte[]) mapMessage.get(EnumParamProcesador.KEY_SERIALIZADO.name());
            String report = (String) mapMessage.get(EnumParamProcesador.KEY_NOMBRE_REPORTE.name());

            address = address.replace("\"", "");

            // if (!SysmanFunciones.validarEmail(address) ||
            // address.isEmpty() || address == null ||
            // subject.isEmpty() || subject == null
            // || text.isEmpty()
            // || text == null) {
            // throw new RuntimeException(CORREO_INVALIDO);
            //
            // }
            if (bytes != null && (report == null || report.isEmpty())) {
                throw new RuntimeException(REPORTE_NULL);
            }

            BodyPart texto = new MimeBodyPart();
            texto.setContent(text, TEXT_HTML_CHARSET_UTF_8);

            MimeMultipart multiparte = new MimeMultipart();
            multiparte.addBodyPart(texto);

            BodyPart adjunto = new MimeBodyPart();
            if (attached != null && !attached.isEmpty()) {

                adjunto.setDataHandler(new DataHandler(new FileDataSource(attached)));
                adjunto.setFileName(attached);
                multiparte.addBodyPart(adjunto);
            }

            if (bytes != null) {
                LOG.info("Entro a:  output != null->> {} ", bytes);

                DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");

                MimeBodyPart pdfBodyPart = new MimeBodyPart();
                pdfBodyPart.setDataHandler(new DataHandler(dataSource));

                pdfBodyPart.setFileName(report);
                multiparte.addBodyPart(pdfBodyPart);
            }

            Message message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address));
            message.setSubject(subject);
            message.setContent(multiparte);

            Transport.send(message);

            auditoriaCorreos(session, address, subject, text);
            LOG.info("Salio de: <<EnvioCorreogenerateAndSendEmail>> parametros / message  ->> {} ", mapMessage);

        }
        catch (MessagingException e) {
            Logger.getLogger(EnvioCorreo.class.getName()).log(Level.WARNING, CANNOT_SEND_EMAIL, e);
        }
        catch (Exception e) {
            Logger.getLogger(EnvioCorreo.class.getName()).log(Level.WARNING, CANNOT_SEND_EMAIL, e);
        }
    }

    // public void writePdf(OutputStream outputStream) throws
    // Exception {
    // Document document = new Document();
    // PdfWriter.getInstance(document, outputStream);
    //
    // }

    /**
     * Metodo encargado de hacer una insercion en la tabla
     * auditoriaCorreos teniendo encuenta los siguientes parametros
     * 
     * @param session
     * -> Se obtiene el origen del correo que se esta enviando
     * @param correoDestino
     * @param asunto
     * -> Asunto del correo
     * @param cuerpoCorreo
     * @param fechaEnvio
     * @throws PersistenciaExcepcion
     */
    private void auditoriaCorreos(Session session, String correoDestino, String asunto, String cuerpoCorreo) {
        servicio.setManejadorEntidad(conector.conectar());

        try {
            servicio.ejecutarSentenciaNativaPorId(KEY_SQL_1, session.getProperties().get(MAIL_SMTP_USER), correoDestino, asunto,
                            cuerpoCorreo, new Date());
        }
        catch (PersistenciaExcepcion e) {
            System.out.println("logger" + e);
        }

        // LOG.info("Error en la audiotoria de correos:
        // <<EnvioCorreogenerateAndSendEmail>> message ->> {} ", ex);

    }
}
