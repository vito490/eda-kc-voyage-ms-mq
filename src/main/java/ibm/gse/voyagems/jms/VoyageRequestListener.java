package ibm.gse.voyagems.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import ibm.gse.voyagems.domain.model.EventBase;
import ibm.gse.voyagems.domain.model.voyage.VoyageAssignedEvent;
import ibm.gse.voyagems.domain.model.voyage.VoyageAssignmentPayload;
import ibm.gse.voyagems.domain.model.voyage.VoyageCanceledEvent;
import ibm.gse.voyagems.domain.model.voyage.VoyageCanceledPayload;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A bean consuming prices from the JMS queue.
 */
@ApplicationScoped
public class VoyageRequestListener implements Runnable {

    @Inject
    ConnectionFactory connectionFactory;

    @Inject
    JMSQueueWriter<EventBase> jmsQueueWriter;


    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    private static final Logger log = Logger.getLogger(VoyageRequestListener.class);

    void onStart(@Observes StartupEvent ev) {
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        log.info("Connecting to message queue " + System.getenv("VOYAGE_REQUEST_QUEUE"));
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            javax.jms.JMSConsumer consumer = context.createConsumer(
                    context.createQueue(System.getenv("VOYAGE_REQUEST_QUEUE")));
            while (true) {
                Message message = consumer.receive();
                if (message == null) {
                    return;
                }
                log.info("received message from queue... " + message.getBody(String.class));
                processMessage(message.getBody(String.class));
            }
        } catch (JMSException e) {
            log.error("error parsing message..", e);
        }
    }

    public void processMessage(String rawMessageBody) {

        try {

            ObjectMapper mapper = new ObjectMapper();
            log.debug("received message from queue... " + rawMessageBody);
            JsonObject rawEvent = new JsonObject(rawMessageBody);
            EventBase responseEvent = null;
            if(rawEvent.getString("type").equals(EventBase.ORDER_CREATED_TYPE)) {

                UUID voyageId = UUID.randomUUID();
                log.debug("Generated new voyage ID: " + voyageId);

                String orederId = rawEvent.getJsonObject("payload").getString("orderID");

                VoyageAssignmentPayload voyageAssignmentPayload =
                        new VoyageAssignmentPayload(orederId, voyageId.toString());
                responseEvent = new VoyageAssignedEvent(System.currentTimeMillis(), "1.0", voyageAssignmentPayload);

            } else if(rawEvent.getString("type").equals(EventBase.ORDER_CANCELLED_TYPE) ||
                    rawEvent.getString("type").equals(EventBase.TYPE_CONTAINER_NOT_FOUND)) {

                log.info("Canceling voyage schedule..");
                /*
                SIMULATE ROLLBACK VOYAGE SCHEDULE TRANSACTION
                 */
                Thread.sleep(3000);

                log.info("Canceled voyage schedule..");
                String orederId = rawEvent.getJsonObject("payload").getString("orderID");
                String reason = rawEvent.getJsonObject("payload").getString("reason");

                VoyageCanceledPayload voyageCanceledPayload = new VoyageCanceledPayload(orederId, reason);
                responseEvent = new VoyageCanceledEvent(System.currentTimeMillis(), "1.0",
                        voyageCanceledPayload);
            }

            jmsQueueWriter.sendMessage(responseEvent, System.getenv("VOYAGE_RESPONSE_QUEUE"));

        } catch (Exception e) {
            log.error("error processing message...", e);
        }
    }
}
