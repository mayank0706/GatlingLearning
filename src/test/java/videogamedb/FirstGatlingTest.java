package videogamedb;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class FirstGatlingTest extends Simulation{

    // Http Configuration
    private HttpProtocolBuilder protocol = http.
            baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json");

    // Scenario Definition
    private ScenarioBuilder scn = scenario("Get All Games Test")
            .exec(http("Get all games")
            .get("/videogame"));

    // Load Simulation
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(protocol);
    }

}
