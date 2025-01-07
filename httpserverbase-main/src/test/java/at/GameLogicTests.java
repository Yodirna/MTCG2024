package at;

import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.cards.Monsters.Elves.FireElf;
import at.fhtw.mtcg.cards.Monsters.Goblins.FireGoblin;
import at.fhtw.mtcg.cards.Monsters.Goblins.WaterGoblin;
import at.fhtw.mtcg.cards.Monsters.Wizards.NormalWizard;
import at.fhtw.mtcg.controllers.DeckController;
import at.fhtw.mtcg.controllers.RewardsController;
import at.fhtw.mtcg.models.*;
import at.fhtw.mtcg.cards.*;
import at.fhtw.mtcg.cards.Monsters.Dragons.*;
import at.fhtw.mtcg.cards.Monsters.Knights.*;
import at.fhtw.mtcg.cards.Monsters.Orks.*;
import at.fhtw.mtcg.cards.Monsters.Kraken.Kraken;
import at.fhtw.mtcg.cards.Spells.*;
import at.fhtw.mtcg.security.Hash;
import at.fhtw.mtcg.models.GameLogic;
import org.junit.jupiter.api.Test;

import java.util.List;

import static at.fhtw.mtcg.security.Hash.hashPassword;
import static org.junit.jupiter.api.Assertions.*;

public class GameLogicTests {

    private final GameLogic gameLogic = new GameLogic();


    @Test
    public void testCreateWaterDragon() {
        Card card = CardRegistry.createCard("WaterDragon");
        assertNotNull(card, "WaterDragon should be created.");
        assertEquals("Water Dragon", card.getName());
        assertEquals(Card.Elements.Water, card.getElement());
    }

    @Test
    public void testGetCardElement() {
        assertEquals(Card.Elements.Water, Card.getCardElement("WaterDragon"), "Card element should be Water.");
        assertEquals(Card.Elements.Fire, Card.getCardElement("FireSpell"), "Card element should be Fire.");
        assertEquals(Card.Elements.Normal, Card.getCardElement("RegularSpell"), "Card element should be Normal.");
        assertEquals(Card.Elements.Neutral, Card.getCardElement("Kraken"), "Card element should be Neutral.");
    }

    @Test
    public void testNeutralElementDamageCalculation() {
        NormalDragon dragon = new NormalDragon();
        NormalOrk ork = new NormalOrk();

        int dragonInitialHp = dragon.getHp();
        int orkInitialHp = ork.getHp();

        gameLogic.fight(dragon, ork);

        assertEquals(dragonInitialHp - ork.getDamage(), dragon.getHp(), "Dragon should lose HP equal to Ork's damage.");
        assertEquals(orkInitialHp - dragon.getDamage(), ork.getHp(), "Ork should lose HP equal to Dragon's damage.");
    }


    @Test
    public void testBattleLogEmptyInitially() {
        List<String> logs = gameLogic.getBattleLog();
        assertTrue(logs.isEmpty(), "Battle log should be empty initially.");
    }

    @Test
    public void testSameElementSpells_NoDamage() {
        WaterSpell waterSpell1 = new WaterSpell();
        WaterSpell waterSpell2 = new WaterSpell();

        gameLogic.fight(waterSpell1, waterSpell2);

        assertEquals(100, waterSpell1.getHp(), "WaterSpell1 should not take damage");
        assertEquals(100, waterSpell2.getHp(), "WaterSpell2 should not take damage");
    }

    @Test
    public void testElementAdvantage_FireVsWater() {
        FireSpell fireSpell = new FireSpell();
        WaterSpell waterSpell = new WaterSpell();

        gameLogic.fight(fireSpell, waterSpell);

        assertEquals(60, fireSpell.getHp(), "FireSpell should take double damage");
        assertEquals(90, waterSpell.getHp(), "WaterSpell should take reduced damage");
    }



    @Test
    public void testKnightDrownsAgainstWaterSpell() {
        WaterSpell waterSpell = new WaterSpell();
        NormalKnight normalKnight = new NormalKnight();

        gameLogic.fight(waterSpell, normalKnight);

        assertEquals(0, normalKnight.getHp(), "Knight should drown when attacked by WaterSpell");
        assertEquals(100, waterSpell.getHp(), "WaterSpell should not take damage");
    }


    @Test
    public void testPasswordHashing() {
        String rawPassword = "securePassword123";
        String hashedPassword = hashPassword(rawPassword);

        Hash hash = new Hash();
        assertTrue(hash.verifyPassword(rawPassword, hashedPassword), "Password verification should pass");
        assertFalse(hash.verifyPassword("wrongPassword", hashedPassword), "Password verification should fail with wrong input");
    }


    @Test
    public void testCardRegistryCreatesValidCards() {
        Card fireDragon = CardRegistry.createCard("FireDragon");
        assertNotNull(fireDragon, "FireDragon should be created");
        assertEquals("Fire Dragon", fireDragon.getName(), "FireDragon name should match");

        Card waterGoblin = CardRegistry.createCard("WaterGoblin");
        assertNotNull(waterGoblin, "WaterGoblin should be created");
        assertEquals("Water Goblin", waterGoblin.getName(), "WaterGoblin name should match");
    }

    @Test
    public void testFireSpellAdvantageAgainstNormalSpell() {
        FireSpell fireSpell = new FireSpell();
        NormalSpell normalSpell = new NormalSpell();

        int normalSpellHpBefore = normalSpell.getHp();
        gameLogic.fight(fireSpell, normalSpell);

        assertTrue(normalSpell.getHp() < normalSpellHpBefore, "Fire Spell should have an advantage!");
    }



    @Test
    public void testKrakenImmuneToSpells() {
        Kraken kraken = new Kraken();
        FireSpell fireSpell = new FireSpell();

        int krakenHpBefore = kraken.getHp();
        gameLogic.fight(kraken, fireSpell);

        assertEquals(krakenHpBefore, kraken.getHp(), "Kraken should be immune to spells!");
    }

    @Test
    public void testDamageCalculationFireSpellVsNormalSpell() {
        FireSpell fireSpell = new FireSpell();
        NormalSpell normalSpell = new NormalSpell();

        int initialHp = normalSpell.getHp();
        gameLogic.fight(fireSpell, normalSpell);

        assertEquals(initialHp - fireSpell.getDamage() * 2, normalSpell.getHp(),
                "Damage calculation with fire advantage should be accurate.");
    }

    @Test
    public void testDamageCalculationEqualSpells() {
        WaterSpell waterSpell1 = new WaterSpell();
        WaterSpell waterSpell2 = new WaterSpell();

        gameLogic.fight(waterSpell1, waterSpell2);

        assertEquals(100, waterSpell1.getHp(), "Equal elements should not reduce HP.");
        assertEquals(100, waterSpell2.getHp(), "Equal elements should not reduce HP.");
    }

    @Test
    public void testGoblinTooAfraidToAttackDragon() {
        WaterGoblin waterGoblin = new WaterGoblin();
        FireDragon fireDragon = new FireDragon();

        gameLogic.fight(waterGoblin, fireDragon);

        assertEquals(100, fireDragon.getHp(), "FireDragon should not take damage from Goblin.");
        assertTrue(waterGoblin.getHp() < 100, "Goblin should still take damage from Dragon.");
    }

    @Test
    public void testFireElfEvadesDragonAttack() {
        FireElf fireElf = new FireElf();
        NormalDragon normalDragon = new NormalDragon();

        gameLogic.fight(fireElf, normalDragon);

        assertEquals(100, fireElf.getHp(), "FireElf should evade attacks from Dragon.");
        assertTrue(normalDragon.getHp() < 100, "Dragon should take damage from FireElf.");
    }

    @Test
    public void testWizardControlsOrk() {
        NormalWizard wizard = new NormalWizard();
        FireOrk ork = new FireOrk();

        gameLogic.fight(wizard, ork);

        assertEquals(100, wizard.getHp(), "Wizard should remain unharmed when controlling Ork.");
        assertTrue(ork.getHp() < 100, "Ork should still take damage from Wizard.");
    }

    @Test
    public void testDeckContainsUniqueCards() {
        Card card1 = CardRegistry.createCard("FireDragon");
        Card card2 = CardRegistry.createCard("WaterDragon");
        Card card3 = CardRegistry.createCard("NormalOrk");
        Card card4 = CardRegistry.createCard("FireOrk");

        assertNotEquals(card1.getName(), card2.getName(), "Cards in the deck should be unique.");
        assertNotEquals(card3.getName(), card4.getName(), "Cards in the deck should be unique.");
    }

    @Test
    public void testValidTokenHighMMR() {
        RewardsController rewardsController = new RewardsController();

        // Mock request with valid token for a user with high MMR
        Request request = new Request();
        request.setHeaderMap(new HeaderMap());
        request.getHeaderMap().addHeader("Authorization", "Bearer altenhof");

        Response response = rewardsController.handleGetReq(request);

        // Compare the status code directly to HttpStatus.OK.code
        assertEquals(HttpStatus.OK.code, response.getStatusCode(), "Response status should be OK for high MMR.");
        assertTrue(response.get().contains("received 30 coins!"), "User should receive 30 coins.");
    }


    @Test
    public void verifyPasswordCheck(){
        try {
            String securePassword = hashPassword("Test Password");
            Hash passwordHasher = new Hash();
            boolean passwordCheck = passwordHasher.verifyPassword("Test Password", securePassword);
            assertTrue(passwordCheck, "Password couldnt be verified");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testBattleLogAfterFight() {
        FireSpell fireSpell = new FireSpell();
        NormalSpell normalSpell = new NormalSpell();

        gameLogic.fight(fireSpell, normalSpell);

        assertFalse(gameLogic.getBattleLog().isEmpty(), "Battle log should contain fight details.");
        assertTrue(gameLogic.getBattleLog().stream().anyMatch(log -> log.contains("Fire Spell")),
                "Battle log should mention Fire Spell.");
    }
}
