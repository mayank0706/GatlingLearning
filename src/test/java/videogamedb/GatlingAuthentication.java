package videogamedb;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class GatlingAuthentication extends Simulation {

    // Http Protocol
    private HttpProtocolBuilder protocol = http.baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Chain Builder
    private static ChainBuilder authentication =
            exec(http("Authenticate ")
                    .post("/authenticate")
                    .body(StringBody("{\n" +
                            "  \"password\": \"admin\",\n" +
                            "  \"username\": \"admin\"\n" +
                            "}"))
                    .check(status().is(200))
                    .check(jmesPath("token").saveAs("jwtToken")));

    private static ChainBuilder createNewGame =
            exec(http("Create New Game")
                    .post("/videogame")
                    .header("Authorization", "Bearer #{jwtToken}")
                    .body(StringBody(
                            "{\n" +
                                    "  \"id\": 0,\n" +
                                    "  \"name\": \"Mario\",\n" +
                                    "  \"releaseDate\": \"2012-05-04\",\n" +
                                    "  \"reviewScore\": 85,\n" +
                                    "  \"category\": \"Platform\",\n" +
                                    "  \"rating\": \"Mature\"\n" +
                                    "}"
                    ))
                    .check(status().is(200))
                    .check(jmesPath("id").saveAs("createdGameId")));

    // Scenario Builder
    private ScenarioBuilder scn = scenario("VideoGame with Authentication")
            .exec(authentication)
            .pause(Duration.ofMillis(2000))
            .exec(createNewGame)
            .exec(
                    session -> {
                        System.out.println("Created Game Id is: " + session.getString("createdGameId"));
                        return session;
                    }
            );

    // Load Simulator
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(protocol);
    }
}
