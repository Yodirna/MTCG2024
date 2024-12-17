package at.fhtw.mtcg.models;

import at.fhtw.mtcg.objects.Monsters.Dragons.Dragon;
import at.fhtw.mtcg.objects.Monsters.Elves.Elf;
import at.fhtw.mtcg.objects.Monsters.Goblins.Goblin;
import at.fhtw.mtcg.objects.Monsters.Knights.Knight;
import at.fhtw.mtcg.objects.Monsters.Kraken.Kraken;
import at.fhtw.mtcg.objects.Monsters.Orks.Ork;
import at.fhtw.mtcg.objects.Monsters.Wizards.Wizard;
import at.fhtw.mtcg.objects.Spells.Spell;
import at.fhtw.mtcg.objects.Spells.WaterSpell;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private List<String> battleLog = new ArrayList<>();

    // Battle Log Getter
    public List<String> getBattleLog() {
        return battleLog;
    }

    // Function to add a log entry
    public void addLogEntry(String message) {
        battleLog.add(message);
    }

    // Print the battle log
    public void printBattleLog() {
        System.out.println("Battle Log:");
        for (String logEntry : battleLog) {
            System.out.println(logEntry);
        }
    }

    public void fight(Card card1, Card card2){

        // If both cards are spells, then spell vs spell
        if (card1 instanceof Spell && card2 instanceof Spell) {
            spellVsSpellFight(card1, card2);
            addLogEntry("\n");
            return;
        }

        // If one of the cards is a spell, then spell vs monster
        if (card1 instanceof Spell || card2 instanceof Spell){

            spellVsMonsterFight(card1, card2);
            addLogEntry("\n");

        }else{
            // otherwise monster vs monster
            MonsterVsMonster(card1, card2);
            // Add a new line to the battle log
            addLogEntry("\n");
        }

    }

    // Function to calculate the fight between two spells
    public void spellVsSpellFight(Card card1, Card card2){
        int spellVsSpell = spellVsSpell(card1, card2);

        // Card 1 is at a disadvantage
        if (spellVsSpell == 1){
            DealDamage(card1, card2, card1.getDamage() / 2, card2.getDamage() * 2);
            // Card 2 is at a disadvantage
        } else if (spellVsSpell == 2) {
            DealDamage(card1, card2, card1.getDamage() * 2, card2.getDamage() /2);
            // Both cards are of the same element
        } else if (spellVsSpell == 3) {
            DealDamage(card1, card2, 0, 0);
            // Error
        }else if (spellVsSpell == 5){
            addLogEntry("An error occurred");
            throw new RuntimeException("An error occurred during damage calculation");
        }
        addLogEntry("\n");
    }

    // Function to calculate the fight between a spell and a monster
    public void spellVsMonsterFight(Card card1, Card card2){
        // Otherwise, calculate the fight between a spell and a monster
        int cases = SpellVsMonster(card1, card2);

        // Card 1 is at a disadvantage
        if (cases == 1){
            DealDamage(card1, card2, card1.getDamage() / 2, card2.getDamage() * 2);
            // Card 2 is at a disadvantage
        } else if (cases == 2) {
            DealDamage(card1, card2, card1.getDamage() * 2, card2.getDamage() /2);
            // Card 1 deals no damage
        }else if (cases == -1){
            DealDamage(card1, card2, 0, card2.getDamage()); // das für karte 1
            // Card 2 deals no damage
        }else if (cases == -2){
            DealDamage(card1, card2, card1.getDamage(), 0); // das für karte 2
            // Kraken special case
        } else if (cases == 3) {
            // Spell vs Kraken --> Kraken is immune to spells
            if (card1 instanceof Spell){
                addLogEntry("The Kraken is immune to spells");
                DealDamage(card1, card2, 0, card2.getDamage()); // for card 1
            }else{
                addLogEntry("The Kraken is immune to spells");
                DealDamage(card1, card2, card1.getDamage(), 0); // for card 2
            }
        } else if (cases == 4) {
            if (card1 instanceof WaterSpell){
                addLogEntry("The Knight drowns due to the water spell");
                card2.setHp(0);

            } else if (card2 instanceof WaterSpell) {
                addLogEntry("The Knight drowns due to the water spell");
                card1.setHp(0);
            }
        }
    }

    // Function calculates who is at a disadvantage in a spell vs spell fight
    public int spellVsSpell(Card card1, Card card2){
        // if both cards are of the same element, then both are at a disadvantage
        if (card1.getElement() == card2.getElement()){
            return 3;
        }

        // if card 1 is fire
        if (card1.getElement() == Card.Elements.Fire){
            // against normal, card 2 is at a disadvantage
            if (card2.getElement() == Card.Elements.Normal){
                return 2;
                // against water, card 1 is at a disadvantage
            } else if (card2.getElement() == Card.Elements.Water) {
                return 1;
            }

            // Water
        } else if (card1.getElement() == Card.Elements.Water) {
            // against fire, card 2 is at a disadvantage
            if (card2.getElement() == Card.Elements.Fire){
                return 2;
                // against normal, card 1 is at a disadvantage
            } else if (card2.getElement() == Card.Elements.Normal) {
                return 1;
            }

            // Normal
        } else if (card1.getElement() == Card.Elements.Normal) {
            // against water, card 2 is at a disadvantage
            if ( card2.getElement() == Card.Elements.Water){
                return 2;
                // against fire, card 1 is at a disadvantage
            } else if (card2.getElement() == Card.Elements.Fire){
                return 1;
            }
        }

        return 5;
    }

    // Function to calculate the fight between two monsters
    public void MonsterVsMonster(Card card1, Card card2){
        // Special cases
        boolean goblinvsdragon = goblinVsDragonCheck(card1, card2);
        boolean wizzardVsOrk = wizzardVsOrk(card1, card2);
        boolean fireElvesVsDragon = fireElvesVsDragon(card1, card2);

        // Goblin vs Dragon special case --> Goblin is too afraid to attack the Dragon
        if (goblinvsdragon){
            addLogEntry("The Goblin is too afraid to attack the Dragon!");
            // If card 1 is the Goblin, then it deals no damage
            if (card1 instanceof Goblin){
                DealDamage(card1, card2, 0, card2.getDamage());
                // otherwise card 2 deals no damage
            }else{
                DealDamage(card1, card2, card1.getDamage(), 0);
            }

            // If a wizard fights an Ork, the wizard can control the Ork
        } else if (wizzardVsOrk) {
            addLogEntry("Wizzards can control Orks!");
            // If card 1 is the Ork, then it deals no damage
            if (card1 instanceof Ork){
                DealDamage(card1, card2, 0, card2.getDamage());
                // otherwise card 2 deals no damage
            }else{
                DealDamage(card1, card2, card1.getDamage(), 0);
            }

            // If a fire elf fights a dragon, the fire elf can evade the dragon's attack
        } else if (fireElvesVsDragon) {
            addLogEntry("The Fire Elf evaded the Dragon's Attack!");
            // If card 1 is the dragon, then it deals no damage
            if (card1 instanceof Dragon){
                DealDamage(card1, card2, 0, card2.getDamage());
                // otherwise card 2 deals no damage
            }else{
                DealDamage(card1, card2, card1.getDamage(), 0);
            }

            // otherwise normal fight
        }else {
            DealDamage(card1, card2, card1.getDamage(), card2.getDamage());
        }
    }

    // Function to calculate the fight between a spell and a monster
    public  int SpellVsMonster(Card card1, Card card2){

        // Kraken vs Spell special Case --> Kraken is immune to spells
        boolean krakenCheck = krakenVsSpells(card1, card2);
        if (krakenCheck){
            return 3;
        }

        // Knights vs WaterSpell special case --> Knight drowns due to the water spell
        boolean KnightVsWaterSpell = waterSpellVsKnight(card1, card2);
        if (KnightVsWaterSpell){
            return 4;
        }

        // If a fire monster attacks a fire spell, the spell takes no damage
        if (card1 instanceof Spell){
            if (card1.getElement() == card2.getElement()){
                return -1;
            }
        }

        // If a fire spell attacks a fire spell, the spell takes no damage
        if (card2 instanceof Spell){
            if (card2.getElement() == card1.getElement()){
                return -2;
            }
        }


        // check elements one by one
        if (card1.getElement() == Card.Elements.Fire){
            if (card2.getElement() == Card.Elements.Normal){
                return 2;
            } else if (card2.getElement() == Card.Elements.Water) {
                return 1;
            }
            // water against fire -> double dmg
        } else if (card1.getElement() == Card.Elements.Water) {
            if (card2.getElement() == Card.Elements.Fire){
                return 2;
            } else if (card2.getElement() == Card.Elements.Normal) {
                return 1;
            }

            // normal against water -> double dmg
        } else if (card1.getElement() == Card.Elements.Normal) {
            if ( card2.getElement() == Card.Elements.Water){
                return 2;
            } else if (card2.getElement() == Card.Elements.Fire){
                return 1;
            }
        }

        // return legende: 1-> karte 1 im nachteil, 2-> karte 2 im nachteil,
        // -1 -> karte 1 kein dmg, -2 -> karte 2 kein dmg,
        // 3 ->kraken, 4 -> Knight,  5 -> error
        return 5;
    }


    public  void printHitType(Card card, int cardDamage){

        // wenn der card 1 damage, geringer ist als der dmg der wkl angestellt wird --> dann hat card 1 einen buff --> critical
        if (card.getDamage() < cardDamage){
            addLogEntry("Critical Hit!");
            // wenn der card 1 damage, mehr ist als der dmg der wkl angestellt wird --> dann hat card 1 einen nerf --> inefficient
        } else if (card.getDamage() > cardDamage) {
            addLogEntry("Inefficient Hit!");
            // wenn der dmg 0 ist, dann ists einen ineffective hit
        } else if (cardDamage == 0) {
            addLogEntry("Ineffective Hit!");
        }
    }

    public void DealDamage(Card card1, Card card2, int card1Damage, int card2Damage){

        //karte 1 greift an ausgeben
        addLogEntry(card1.getName() + " is attacking " + card2.getName());

        //Art der treffer berechnen und ausgeben : Kritisch, inefficient, ineffective
        this.printHitType(card1, card1Damage);

        // battle log hinzufügen
        addLogEntry(card1.getName() + " dealt " + card1Damage + " damage to " + card2.getName());

        // card 2 hp niedriger machen (um den schaden von card 1)
        card2.setHp(card2.getHp()- card1Damage);

        // Die neue HP von card 2 ausgeben
        addLogEntry(card2.getName() + " now has " + card2.getHp() + " HP");


        //card 2 greift an ausgeben
        addLogEntry(card2.getName() + " is attacking " + card1.getName());

        //Art der treffer berechnen und ausgeben : Kritisch, inefficient, ineffective
        this.printHitType(card2, card2Damage);

        //logs hinzufügen
        addLogEntry(card2.getName() + " dealt " + card2Damage + " damage to " + card1.getName());

        // Card 1 hp niederiger machen (um den schaden von card2)
        card1.setHp(card1.getHp() - card2Damage);

        // Die neue HP von card 2 ausgeben
        addLogEntry(card1.getName() + " now has " + card1.getHp() + " HP");
    }




    ///////////////////////////////////////////// Special Checks ///////////////////////////////////////////////////
    // dragon vs Goblin
    public boolean goblinVsDragonCheck(Card card1, Card card2){
        if(card1 instanceof Goblin && card2 instanceof Dragon || card2 instanceof Goblin && card1 instanceof Dragon){
            return true;
        }else {
            return false;
        }
    }

    // wizzards vs Ork
    public boolean wizzardVsOrk(Card card1, Card card2){
        if(card1 instanceof Wizard && card2 instanceof Ork || card2 instanceof Wizard && card1 instanceof Ork){
            return true;
        }else {
            return false;
        }
    }

    // Fire Elves vs Dragon
    public boolean fireElvesVsDragon(Card card1, Card card2){
        if(card1 instanceof Elf && card2 instanceof Dragon || card2 instanceof Elf && card1 instanceof Dragon){
            return true;
        }else {
            return false;
        }
    }
    public boolean krakenVsSpells(Card card1, Card card2){
        // wenn es einen krake gibt, dann spell ignorieren
        if (card1 instanceof Kraken && card2 instanceof Spell || card1 instanceof Spell && card2 instanceof Kraken){
            return true;
        }
        return false;
    }
    public boolean waterSpellVsKnight(Card card1, Card card2){
        // wenn es einen waterspell und knight gibt
        if (card1 instanceof WaterSpell && card2 instanceof Knight || card1 instanceof Knight && card2 instanceof WaterSpell){
            return true;
        }
        return false;
    }




}
