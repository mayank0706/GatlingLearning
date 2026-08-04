package gatlingFeeders;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class JsonFeeder extends Simulation {

    // Http Protocol
    private HttpProtocolBuilder protocol = http.baseUrl("https://videogamedb.uk/api").acceptHeader(
            "application/json");

    // JSON Feeder
    private static FeederBuilder.FileBased<Object> jsonFeeder = jsonFile("data/games.json").random();

    // Chain Builder
    private static ChainBuilder getSpecificGame =
            feed(jsonFeeder)
                    .exec(http("Get video game with id: #{id}")
                            .get("/videogame/#{id}")
                            .check(jmesPath("name").isEL("#{name}"))
                            .check(status().is(200)));

    // Scenario Definition
    private ScenarioBuilder scn = scenario("Video Game - JSON Feeder")
            .repeat(15).on(
                    exec(getSpecificGame)
                            .pause(2)
            );

    // Load Simulation
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(protocol);
    }
}

