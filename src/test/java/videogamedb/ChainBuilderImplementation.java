package videogamedb;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ChainBuilderImplementation extends Simulation {

    // Http Configuration
    private HttpProtocolBuilder protocol = http.baseUrl("https://videogamedb.uk/api").acceptHeader("application/json");

    // Chain Builders
    private static ChainBuilder getAllVideoGames =
            exec(http("Get all video games")
                    .get("/videogame")
                    .check(status().not(404), status().not(500)));

    private static ChainBuilder getSpecificVideoGame =
            exec(http("Get specific video game")
                    .get("/videogame/" + java.util.concurrent.ThreadLocalRandom.current().nextInt(10))
                    .check(status().in(200, 201, 202, 203)));

    // Scenario Builder
    private ScenarioBuilder scn = scenario("Get Video Games - Chain Builder")
            .exec(getAllVideoGames)
            .pause(Duration.ofMillis(5000))
            .exec(getSpecificVideoGame)
            .pause(Duration.ofMillis(5000))
            .exec(getAllVideoGames)
            .pause(Duration.ofMillis(5000));

    // Load Simulation
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(protocol);
    }
}
