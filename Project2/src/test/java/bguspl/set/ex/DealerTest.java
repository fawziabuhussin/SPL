package bguspl.set.ex;

import static org.junit.jupiter.api.Assertions.*;
import bguspl.set.Config;
import bguspl.set.Env;
import bguspl.set.UserInterface;
import bguspl.set.Util;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;
import java.util.logging.Logger;



@ExtendWith(MockitoExtension.class)
class DealerTest {

    @Mock
    Player player1;
    @Mock
    Player player2;
    @Mock
    Util util;
    @Mock
    private UserInterface ui;
    @Mock
    private Table table;

    private Dealer dealer;

    @Mock
    private Logger logger;

    void assertInvariants() {
        assertFalse(dealer.terminate);
    }

    @BeforeEach
    void setUp() {

        Properties properties = new Properties();
        properties.put("Rows", "2");
        properties.put("Columns", "2");
        properties.put("FeatureSize", "3");
        properties.put("FeatureCount", "4");
        properties.put("TableDelaySeconds", "0");
        properties.put("PlayerKeys1", "81,87,69,82");
        properties.put("PlayerKeys2", "85,73,79,80");
        properties.put("HumanPlayers", "2");

        // purposely do not find the configuration files (use defaults here).
        Env env = new Env(logger, new Config(logger, properties), ui, util);
        player1 = new Player(env, dealer, table, 0, false);
        player2 = new Player(env, dealer, table, 1, false);
        dealer = new Dealer(env, table, new Player[] { player1, player2 });
        assertInvariants();
    }

    @AfterEach
    void tearDown() {
        // assertInvariants();
    }

    @Test
    void terminate() {

        Dealer MockingDealer = Mockito.spy(dealer);
        boolean check = MockingDealer.terminate;
        MockingDealer.terminate();
        assertNotEquals(MockingDealer.terminate, check);

    }

    @Test
    void getHighestScore() {

        Dealer MockingDealer = Mockito.spy(dealer);
        MockingDealer.players[0].point();
        MockingDealer.players[0].point();
        MockingDealer.players[1].point();
        int highest = MockingDealer.getHighestScore();
        assertEquals(highest, 2);

    }

}