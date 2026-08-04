package videogamedb;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;
import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameTests extends Simulation {

    // Http Configuration
    private HttpProtocolBuilder protocol = http
            .baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json");

    // Scenario Definition
    private ScenarioBuilder scn = scenario("Video Game DB Test")
            .exec(http("Get all games - first call")
                    .get("/videogame")
                    .check(status().is(200))
                    .check(jsonPath("$[?(@.id==1)].category").is("Shooter"))
                    .check(jmesPath("[" + java.util.concurrent.ThreadLocalRandom.current().nextInt(1, 11) + "].id").saveAs("gameId")))
            .pause(5)

            .exec(http("Get specific game with id - #{gameId}")
                    .get("/videogame/#{gameId}")
                    .check(status().in(200, 201, 203))
                    .check(jmesPath("name").is("Tetris")))
            .pause(1, 20)

            .exec(http("Get all games - second call")
                    .get("/videogame")
                    .check(status().not(404), status().not(500))
                    .check(jsonPath("$[?(@.category=='RPG')].name").is("Final Fantasy VII")))
            .pause(Duration.ofMillis(5000));

    // Load Simulation
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(protocol);
    }
}
