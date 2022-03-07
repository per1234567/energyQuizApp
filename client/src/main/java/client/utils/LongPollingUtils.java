package client.utils;

import static client.utils.ServerUtils.applicationJson;

import client.scenes.MainCtrl;
import client.scenes.QuestionFrameCtrl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import javax.inject.Inject;
import org.glassfish.jersey.client.ClientConfig;

/**
 * Controls client-side functionality related to long-polling
 */
public class LongPollingUtils {

    public boolean test = false;

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    public final QuestionFrameCtrl questionFrameCtrl;
    private final ObjectMapper mapper = new ObjectMapper();
    public boolean active;

    /**
     * Constructor
     *
     * @param serverUtils       ServerUtils object
     * @param questionFrameCtrl QuestionFrameCtrl object
     */
    @Inject
    public LongPollingUtils(ServerUtils serverUtils, MainCtrl mainCtrl, QuestionFrameCtrl questionFrameCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.questionFrameCtrl = questionFrameCtrl;

        this.active = false;
    }

    /**
     * Changes whether polling is active on front-end
     *
     * @param pollingActive Whether polling must be active on front-end
     */
    public void setPollingActive(boolean pollingActive) {
        if (!active && pollingActive && !test) {
            new Thread(() -> {
                while (active) {
                    sendPoll();
                }
            }).start();
        }
        active = pollingActive;
    }

    /**
     * Executes long-polling loop
     */
    private void sendPoll() {
        String json = ClientBuilder.newClient(new ClientConfig())
            .target(serverUtils.getServerIP()).path("poll")
            .request(applicationJson)
            .accept(applicationJson)
            .get(new GenericType<>() {
            });

        try {
            if (active) {
                performAction(mapper.readTree(json));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.err.println("Error while parsing JSON from long polling on client-side");
        }
    }

    /**
     * Calls appropriate method based on received JSON string
     *
     * @param response An object generated from the parsed JSON string that allows for value retrieval
     */
    public void performAction(JsonNode response) {
        switch (response.get("type").asText()) {
            case "EMOJI":
                String name = response.get("name").asText();
                String reaction = response.get("reaction").asText();
                questionFrameCtrl.displayNewEmoji(name, reaction);
                break;
            case "HALVE_TIME":
                mainCtrl.halveRemainingTime();
                break;
            case "DISCONNECT":
                String player = response.get("name").asText();
                mainCtrl.playerLeavesLobby(player);
                break;
            default:
                System.err.println("Unknown long polling response type");
                break;
        }
    }
}
