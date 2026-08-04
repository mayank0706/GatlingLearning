package gatlingFeeders;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class CSVFeeder extends Simulation {

    // Http Protocol
    private HttpProtocolBuilder protocol = http.baseUrl("https://videogamedb.uk/api").acceptHeader("application/json");

    // CSV Feeder
    private static FeederBuilder.FileBased<String> csvFeeder = csv("data/games.csv").circular();

    // Chain Builder
    private static ChainBuilder getSpecificGames =
            feed(csvFeeder)
                    .exec(http("Get video game with id: #{gameId}")
                    .get("/videogame/#{gameId}")
                    .check(jmesPath("name").isEL("#{gameName}")));

    // Scenario Definition
    private ScenarioBuilder scn = scenario("Video Game - CSV Feeder")
            .repeat(10).on(
                    exec(getSpecificGames)
                            .pause(1)
            );

    // Load Simulation
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(protocol);
    }
}
