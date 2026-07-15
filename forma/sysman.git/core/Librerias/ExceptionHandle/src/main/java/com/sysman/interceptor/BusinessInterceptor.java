package com.sysman.interceptor;

import com.sysman.exc.kernel.api.commons.util.Constans;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.log4j.Logger;

public class BusinessInterceptor {

    private String ejbName;
    private String ejbMethod;
    private Object[] ejbparams;
    static final Logger logger = Logger.getLogger(BusinessInterceptor.class);

    /**
     * method to intercept the ejb call and log the transaction
     * 
     * @param ctx
     * @return transaction object
     * @throws Exception
     */
    @AroundInvoke
    public Object methodInterceptor(InvocationContext ctx) throws Exception {
        logEntry(ctx);
        try {
            return ctx.proceed();
        } finally {
            logExit(ctx);
        }
    }

    /**
     * method to log the init of transaction
     * 
     * @param ctx
     */
    private void logEntry(InvocationContext ctx) {

        ejbName = ctx.getTarget().getClass().getSimpleName();
        ejbMethod = ctx.getMethod().getName();
        ejbparams = Arrays.copyOf(ctx.getParameters(),
                        ctx.getParameters().length);

        logger.info("*** Intercepting call to " + ejbName + " method: "
            + ejbMethod + " with params " + Arrays.toString(ejbparams));
        logNegocio(ejbName, ejbMethod, Arrays.toString(ejbparams), "IN");

    }

    /**
     * method to log the end of transaction
     * 
     * @param ctx
     */
    private void logExit(InvocationContext ctx) {
        ejbName = ctx.getTarget().getClass().getSimpleName();
        ejbMethod = ctx.getMethod().getName();
        ejbparams = Arrays.copyOf(ctx.getParameters(),
                        ctx.getParameters().length);

        logger.info("*** Intercepting call to " + ejbName + " method: "
            + ejbMethod + " with params " + Arrays.toString(ejbparams));
        logNegocio(ejbName, ejbMethod, Arrays.toString(ejbparams), "OUT");
    }

    /**
     *
     * Metodo putMessageOnQueue envia un mensaje a la cola para ser
     * procesado como auditoria.
     * 
     * @Param Map<String,String> log
     **/
    public void putMessageOnQueue(Map<String, String> log) {
        // InitialContext ic = null;
        // Connection jmsConnection = null;
        // try {
        // ic = new InitialContext();
        // ConnectionFactory cf = (ConnectionFactory) ic
        // .lookup(Constans.CONNECTION_FACTORY_QUEUE);
        // Queue queue = (Queue)
        // ic.lookup(Constans.BUSINESS_MESSAGE_QUEUE);
        // Session session = null;
        // MessageProducer messageProducer = null;
        //
        // try {
        //
        // jmsConnection = cf.createConnection();
        // session = jmsConnection.createSession(false,
        // Session.AUTO_ACKNOWLEDGE);
        // messageProducer = session.createProducer(queue);
        // ObjectMessage message = session
        // .createObjectMessage((Serializable) log);
        // messageProducer.send(message);
        // }
        // finally {
        // if (messageProducer != null) {
        // messageProducer.close();
        // }
        // if (session != null) {
        // session.close();
        // }
        // if (jmsConnection != null) {
        // jmsConnection.close();
        // }
        // }
        // }
        // catch (NamingException e) {
        // logger.error(e);
        // }
        // catch (JMSException e) {
        // logger.error(e);
        // }

    }

    /**
     * 
     * @param ejbName
     * @param ejbMetod
     * @param ejbparams
     * @param type
     * type of the transaction (IN,OUT)
     */
    public void logNegocio(String ejbName, String ejbMetod, String ejbparams,
        String type) {
        Map<String, String> log = new HashMap<>();

        log.put(Constans.EJB_METHOD, ejbMetod);
        log.put(Constans.EJB_NAME, ejbName);
        log.put(Constans.EJB_PARAMS, ejbparams);
        log.put(Constans.EJB_TYPE, type);

        putMessageOnQueue(log);
    }

}
