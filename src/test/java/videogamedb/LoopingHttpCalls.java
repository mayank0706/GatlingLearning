package videogamedb;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class LoopingHttpCalls extends Simulation {

    // HTTP Configuration
    private HttpProtocolBuilder protocol = http.baseUrl("https://videogamedb.uk/api").acceptHeader("application/json");

    // Chain Builders with Looping
    private static ChainBuilder getAllVideoGames =
            repeat(3).on(
                    exec(http("Get all video games")
                            .get("/videogame")
                            .check(status().not(404), status().not(500)))
            );

    private static ChainBuilder getSpecificVideoGame =
            repeat(4, "myCounter").on(
                    exec(http("Get specific video game with id - #{myCounter}")
                            .get("/videogame/#{myCounter}")
                            .check(status().not(404), status().not(500)))
            );

    // Scenario Builder
    private ScenarioBuilder scn = scenario("Video Game Http Calls Looping")
            .exec(getAllVideoGames)
            .pause(Duration.ofMillis(4000))
            .exec(getSpecificVideoGame)
            .pause(Duration.ofMillis(4000))
            .repeat(2).on(
                    exec(getAllVideoGames)
            )
            .pause(Duration.ofMillis(4000));

    // Load Simulation
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(protocol);
    }
}
