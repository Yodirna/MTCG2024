package at;

import at.fhtw.mtcg.cards.CardToClassMapper;
import at.fhtw.mtcg.cards.Monsters.Dragons.FireDragon;
import at.fhtw.mtcg.cards.Monsters.Dragons.NormalDragon;
import at.fhtw.mtcg.cards.Monsters.Dragons.WaterDragon;
import at.fhtw.mtcg.cards.Monsters.Elves.FireElf;
import at.fhtw.mtcg.cards.Monsters.Goblins.FireGoblin;
import at.fhtw.mtcg.cards.Monsters.Goblins.NormalGoblin;
import at.fhtw.mtcg.cards.Monsters.Goblins.WaterGoblin;
import at.fhtw.mtcg.cards.Monsters.Knights.FireKnight;
import at.fhtw.mtcg.cards.Monsters.Knights.NormalKnight;
import at.fhtw.mtcg.cards.Monsters.Knights.WaterKnight;
import at.fhtw.mtcg.cards.Monsters.Kraken.Kraken;
import at.fhtw.mtcg.cards.Monsters.Orks.FireOrk;
import at.fhtw.mtcg.cards.Monsters.Orks.NormalOrk;
import at.fhtw.mtcg.cards.Monsters.Orks.WaterOrk;
import at.fhtw.mtcg.cards.Monsters.Wizards.FireWizard;
import at.fhtw.mtcg.cards.Monsters.Wizards.NormalWizard;
import at.fhtw.mtcg.cards.Monsters.Wizards.WaterWizard;
import at.fhtw.mtcg.cards.Spells.FireSpell;
import at.fhtw.mtcg.cards.Spells.NormalSpell;
import at.fhtw.mtcg.cards.Spells.WaterSpell;
import at.fhtw.mtcg.models.Card;
import at.fhtw.mtcg.models.Game;
import at.fhtw.mtcg.security.Hash;
import org.junit.jupiter.api.Test;


import static at.fhtw.mtcg.security.Hash.hashPassword;
import static org.junit.jupiter.api.Assertions.*;


public class Tests {

    Game Game = new Game();

    @Test
    public void testSpellsWithSameElement_water(){
        WaterSpell waterSpell_1 = new WaterSpell();
        WaterSpell waterSpell_2 = new WaterSpell();
        int waterspell_1HP = waterSpell_1.getHp();
        int waterspell_2HP = waterSpell_2.getHp();
        Game.fight(waterSpell_1, waterSpell_2);
        int waterspell_1AfterHP = waterSpell_1.getHp();
        int waterspell_2AfterHP = waterSpell_2.getHp();
        assertEquals(waterspell_1HP, waterspell_1AfterHP, "Water Spell 1 hat damage bekommen");
        assertEquals(waterspell_2HP, waterspell_2AfterHP, "Water Spell 2 hat damage bekommen");
    }

    @Test
    public void testSpellsWithSameElement_fire(){
        FireSpell fireSpell_1 = new FireSpell();
        FireSpell fireSpell_2 = new FireSpell();
        int fireSpell_1HP = fireSpell_1.getHp();
        int fireSpell_2HP = fireSpell_2.getHp();
        Game.fight(fireSpell_1, fireSpell_2);
        int fireSpell_1AfterHP = fireSpell_1.getHp();
        int fireSpell_2AfterHP = fireSpell_2.getHp();
        assertEquals(fireSpell_1HP, fireSpell_1AfterHP, "Water Spell 1 hat damage bekommen");
        assertEquals(fireSpell_2HP, fireSpell_2AfterHP, "Water Spell 2 hat damage bekommen");
    }

    @Test
    public void testSpellsWithSameElement_regular(){
        NormalSpell normalSpell_1 = new NormalSpell();
        NormalSpell normalSpell_2 = new NormalSpell();
        int normalSpell_1HP = normalSpell_1.getHp();
        int normalSpell_2HP = normalSpell_2.getHp();
        Game.fight(normalSpell_1, normalSpell_2);
        int normalSpell_1AfterHP = normalSpell_1.getHp();
        int normalSpell_2AfterHP = normalSpell_2.getHp();
        assertEquals(normalSpell_1HP, normalSpell_1AfterHP, "Water Spell 1 hat damage bekommen");
        assertEquals(normalSpell_2HP, normalSpell_2AfterHP, "Water Spell 2 hat damage bekommen");
    }


    @Test
    public void TestElementAdvantageFireVsWater_SpellVsSpell(){
        WaterSpell waterSpell = new WaterSpell();
        FireSpell fireSpell = new FireSpell();
        int fireSpellMaxHp = fireSpell.getHp();
        int waterSpellMaxHp = waterSpell.getHp();
        Game.fight(waterSpell, fireSpell);
        assertEquals(fireSpell.getHp(), (fireSpellMaxHp - (waterSpell.getDamage()*2)),
                "Schaden von Water Spell nicht verdoppelt");

        assertEquals(fireSpell.getDamage() / 2, (waterSpellMaxHp - waterSpell.getHp() ),
                "Schaden von Fire Wizzard nicht halbiert");
    }


    @Test
    public void TestElementAdvantageFireVsNormal_SpellVsSpell(){
        FireSpell fireSpell = new FireSpell();
        NormalSpell normalSpell = new NormalSpell();
        int normalSpellMaxHp = normalSpell.getHp();
        int fireSpellMaxHp = fireSpell.getHp();

        Game.fight(fireSpell, normalSpell);
        assertEquals(normalSpell.getHp(), (normalSpellMaxHp - (fireSpell.getDamage()*2)),
                "Schaden von Fire Spell nicht verdoppelt");

        assertEquals(normalSpell.getDamage() / 2, (fireSpellMaxHp - fireSpell.getHp() ),
                "Schaden von Normal nicht halbiert");
    }
    @Test
    public void TestElementAdvantageNormalVsWater_SpellVsMonseter(){
        NormalSpell normalSpell = new NormalSpell();
        WaterGoblin waterGoblin = new WaterGoblin();
        int waterGoblinMaxHp = waterGoblin.getHp();
        int normalSpellMaxHp = normalSpell.getHp();

        Game.fight(normalSpell, waterGoblin);
        assertEquals(waterGoblin.getHp(), (waterGoblinMaxHp - (normalSpell.getDamage()*2)),
                "Schaden von Normal Spell nicht verdoppelt");

        assertEquals(waterGoblin.getDamage() / 2, (normalSpellMaxHp - normalSpell.getHp() ),
                "Schaden von Water Goblin nicht halbiert");
    }


    @Test
    public void TestElementAdvantageFireVsWater_SpellVsMonseter(){
        WaterSpell waterSpell = new WaterSpell();
        FireWizard fireWizard = new FireWizard();
        int fireWizzardMaxHp = fireWizard.getHp();
        int waterSpellMaxHp = waterSpell.getHp();
        Game.fight(waterSpell, fireWizard);
        assertEquals(fireWizard.getHp(), (fireWizzardMaxHp - (waterSpell.getDamage()*2)),
                "Schaden von Water Spell nicht verdoppelt");

        assertEquals(fireWizard.getDamage() / 2, (waterSpellMaxHp - waterSpell.getHp() ),
                "Schaden von Fire Spell nicht halbiert");
    }
    @Test
    public void TestElementAdvantageFireVsNormal_SpellVsMonseter(){
        FireSpell fireSpell = new FireSpell();
        NormalKnight normalKnight = new NormalKnight();
        int normalKnightMaxHp = normalKnight.getHp();
        int fireSpellMaxHp = fireSpell.getHp();

        Game.fight(fireSpell, normalKnight);
        assertEquals(normalKnight.getHp(), (normalKnightMaxHp - (fireSpell.getDamage()*2)),
                "Schaden von Fire Spell nicht verdoppelt");

        assertEquals(normalKnight.getDamage() / 2, (fireSpellMaxHp - fireSpell.getHp() ),
                "Schaden von Normal Spell nicht halbiert");
    }
    @Test
    public void TestElementAdvantageNormalVsWater_SpellVsSpell(){
        NormalSpell normalSpell = new NormalSpell();
        WaterSpell waterSpell = new WaterSpell();
        int waterSpellMaxHp = waterSpell.getHp();
        int normalSpellMaxHp = normalSpell.getHp();

        Game.fight(normalSpell, waterSpell);
        assertEquals(waterSpell.getHp(), (waterSpellMaxHp - (normalSpell.getDamage()*2)),
                "Schaden von Normal Spell nicht verdoppelt");

        assertEquals(waterSpell.getDamage() / 2, (normalSpellMaxHp - normalSpell.getHp() ),
                "Schaden von Water Spell nicht halbiert");
    }
    @Test
    public void fireElfEvadesAllDragonsAttack() {
        FireElf fireElf = new FireElf();
        WaterDragon waterDragon = new WaterDragon();
        NormalDragon normalDragon = new NormalDragon();
        FireDragon fireDragon = new FireDragon();

        int fireElfMaxHP = fireElf.getHp();
        Game.fight(fireElf, waterDragon);
        assertEquals(fireElfMaxHP, fireElf.getHp(), "FireElf took damage from WaterDragon!");

        Game.fight(fireElf, fireDragon);
        assertEquals(fireElf.getHp(), fireElfMaxHP, "FireElf took damage from FireDragon!");

        Game.fight(fireElf, normalDragon);
        assertEquals(fireElf.getHp(), fireElfMaxHP, "FireElf took damage from NormalDragon!");
    }

    @Test
    public void FireWizzardControlsAllOrks() {
        FireWizard fireWizard = new FireWizard();
        FireOrk fireOrk = new FireOrk();
        NormalOrk normalOrk = new NormalOrk();
        WaterOrk waterOrk = new WaterOrk();
        int wizzardMaxHP = fireWizard.getHp();
        //gegen feuer ork
        Game.fight(fireOrk, fireWizard);
        assertEquals(fireWizard.getHp(), wizzardMaxHP, "Fire Wizzard took damage from Fire Ork!");
        //gegen normal ork
        Game.fight(normalOrk, fireWizard);
        assertEquals(fireWizard.getHp(), wizzardMaxHP, "Fire Wizzard took damage from normal Ork!");
        // gegen wasser ork
        Game.fight(waterOrk, fireWizard);
        assertEquals(fireWizard.getHp(), wizzardMaxHP, "Fire Wizzard took damage from water Ork!");
    }
    @Test
    public void WaterWizzardControlsAllOrks() {
        WaterWizard waterWizard = new WaterWizard();
        FireOrk fireOrk = new FireOrk();
        NormalOrk normalOrk = new NormalOrk();
        WaterOrk waterOrk = new WaterOrk();
        int wizzardMaxHP = waterWizard.getHp();
        //gegen feuer ork
        Game.fight(fireOrk, waterWizard);
        assertEquals(waterWizard.getHp(), wizzardMaxHP, "Water Wizzard took damage from Fire Ork!");
        //gegen normal ork
        Game.fight(normalOrk, waterWizard);
        assertEquals(waterWizard.getHp(), wizzardMaxHP, "Water Wizzard took damage from normal Ork!");
        // gegen wasser ork
        Game.fight(waterOrk, waterWizard);
        assertEquals(waterWizard.getHp(), wizzardMaxHP, "Water Wizzard took damage from water Ork!");
    }

    @Test
    public void NormalWizzardControlsAllOrks() {
        NormalWizard normalWizard = new NormalWizard();
        FireOrk fireOrk = new FireOrk();
        NormalOrk normalOrk = new NormalOrk();
        WaterOrk waterOrk = new WaterOrk();
        int wizzardMaxHP = normalWizard.getHp();
        //gegen feuer ork
        Game.fight(fireOrk, normalWizard);
        assertEquals(normalWizard.getHp(), wizzardMaxHP, "Normal Wizzard took damage from Fire Ork!");
        //gegen normal ork
        Game.fight(normalOrk, normalWizard);
        assertEquals(normalWizard.getHp(), wizzardMaxHP, "Normal Wizzard took damage from normal Ork!");
        // gegen wasser ork
        Game.fight(waterOrk, normalWizard);
        assertEquals(normalWizard.getHp(), wizzardMaxHP, "Normal Wizzard took damage from water Ork!");
    }
    @Test
    public void WaterGoblinTooAfraidOfDragons(){
        WaterGoblin waterGoblin = new WaterGoblin();
        WaterDragon waterDragon = new WaterDragon();
        NormalDragon normalDragon = new NormalDragon();
        FireDragon fireDragon = new FireDragon();
        int waterDragonMaxHP = waterDragon.getHp();
        int normalDragonMaxHP = normalDragon.getHp();
        int fireDragonMaxHP = fireDragon.getHp();

        Game.fight(waterGoblin, waterDragon);
        assertEquals(waterDragonMaxHP, waterDragon.getHp(), "Water Dragon took Damage");

        Game.fight(waterGoblin, normalDragon);
        assertEquals(normalDragonMaxHP, normalDragon.getHp(), "normal Dragon took Damage");

        Game.fight(waterGoblin, fireDragon);
        assertEquals(fireDragonMaxHP, fireDragon.getHp(), "fire Dragon took Damage");
    }
    @Test
    public void FireGoblinTooAfraidOfDragons(){
        FireGoblin fireGoblin = new FireGoblin();
        WaterDragon waterDragon = new WaterDragon();
        NormalDragon normalDragon = new NormalDragon();
        FireDragon fireDragon = new FireDragon();
        int waterDragonMaxHP = waterDragon.getHp();
        int normalDragonMaxHP = normalDragon.getHp();
        int fireDragonMaxHP = fireDragon.getHp();

        Game.fight(fireGoblin, waterDragon);
        assertEquals(waterDragonMaxHP, waterDragon.getHp(), "Water Dragon took Damage");

        Game.fight(fireGoblin, normalDragon);
        assertEquals(normalDragonMaxHP, normalDragon.getHp(), "Normal Dragon took Damage");

        Game.fight(fireGoblin, fireDragon);
        assertEquals(fireDragonMaxHP, fireDragon.getHp(), "Fire Dragon took Damage");
    }
    @Test
    public void NormalGoblinTooAfraidOfDragons(){
        NormalGoblin normalGoblin = new NormalGoblin();
        WaterDragon waterDragon = new WaterDragon();
        NormalDragon normalDragon = new NormalDragon();
        FireDragon fireDragon = new FireDragon();
        int waterDragonMaxHP = waterDragon.getHp();
        int normalDragonMaxHP = normalDragon.getHp();
        int fireDragonMaxHP = fireDragon.getHp();

        Game.fight(normalGoblin, waterDragon);
        assertEquals(waterDragonMaxHP, waterDragon.getHp(), "Water Dragon took Damage");

        Game.fight(normalGoblin, normalDragon);
        assertEquals(normalDragonMaxHP, normalDragon.getHp(), "Normal Dragon took Damage");

        Game.fight(normalGoblin, fireDragon);
        assertEquals(fireDragonMaxHP, fireDragon.getHp(), "Fire Dragon took Damage");
    }
    @Test
    public void AllKnightsDrownAgainstWaterSpell(){
        Game Game = new Game();
        WaterSpell waterSpell = new WaterSpell();
        WaterKnight waterKnight = new WaterKnight();
        NormalKnight normalKnight = new NormalKnight();
        FireKnight fireKnight = new FireKnight();

        Game.fight(waterSpell, waterKnight);
        assertEquals(0, waterKnight.getHp(), "Water Knight did not Die!");

        Game.fight(waterSpell, normalKnight);
        assertEquals(0, normalKnight.getHp(), "Normal Knight did not Die!");

        Game.fight(waterSpell, fireKnight);
        assertEquals(0, fireKnight.getHp(), "Fire Knight did not Die!");
    }

    @Test
    public void KrakenImmunityVsAllSpells(){
        Kraken kraken = new Kraken();
        WaterSpell waterSpell = new WaterSpell();
        FireSpell fireSpell = new FireSpell();
        NormalSpell normalSpell = new NormalSpell();
        int krakenMaxHP = kraken.getHp();

        Game.fight(kraken, fireSpell);
        assertEquals(krakenMaxHP, kraken.getHp(), "Kraken immunity test failed against fireSpell");

        Game.fight(kraken, waterSpell);
        assertEquals(krakenMaxHP, kraken.getHp(), "Kraken immunity test failed against waterSpell");

        Game.fight(kraken, normalSpell);
        assertEquals(krakenMaxHP, kraken.getHp(), "Kraken immunity test failed against normalSpell");
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

    @Test void checkCardToClassMapper(){
        CardToClassMapper cardToClassMapper = new CardToClassMapper();
        Card card = new Card("WaterGoblin", Card.Elements.Water, 20, 100);
        Class cardClassBefore = card.getClass();
        card = cardToClassMapper.createRealCard(card);
        Class cardClassAfter = card.getClass();
        // die klasse sollte sich ändern und deswegen assertfalse
        assertFalse(cardClassBefore == cardClassAfter, "Fehlgeschalgen");
    }

}
