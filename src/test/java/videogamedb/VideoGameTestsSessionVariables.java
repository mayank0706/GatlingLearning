package videogamedb;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameTestsSessionVariables extends Simulation {
    // Http Protocol
    private HttpProtocolBuilder protocol = http.baseUrl("https://videogamedb.uk/api").acceptHeader("application/json");

    // Scenario Definition
    private ScenarioBuilder scn = scenario("Video Game Session Variables")
            .exec(http("Print Get All Games Response Using Session Variables")
                    .get("/videogame")
                    .check(status().is(200))
                    .check(bodyString().saveAs("response")))
            .pause(Duration.ofMillis(2000))

            .exec(
                    session -> {
                        System.out.println("Response body: " + session.getString("response"));
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