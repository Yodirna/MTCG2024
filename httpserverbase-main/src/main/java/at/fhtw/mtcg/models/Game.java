package at.fhtw.mtcg.models;

import at.fhtw.mtcg.classes.Monsters.Dragons.Dragon;
import at.fhtw.mtcg.classes.Monsters.Elves.Elf;
import at.fhtw.mtcg.classes.Monsters.Goblins.Goblin;
import at.fhtw.mtcg.classes.Monsters.Knights.Knight;
import at.fhtw.mtcg.classes.Monsters.Kraken.Kraken;
import at.fhtw.mtcg.classes.Monsters.Orks.Ork;
import at.fhtw.mtcg.classes.Monsters.Wizards.Wizard;
import at.fhtw.mtcg.classes.Spells.Spell;
import at.fhtw.mtcg.classes.Spells.WaterSpell;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private List<String> battleLog = new ArrayList<>();

    // getter fürs battle log
    public List<String> getBattleLog() {
        return battleLog;
    }

    // Funktion um einen Log entry hinzufügen
    public void addLogEntry(String message) {
        battleLog.add(message);
    }

    // battle log printen
    public void printBattleLog() {
        System.out.println("Battle Log:");
        for (String logEntry : battleLog) {
            System.out.println(logEntry);
        }
    }

    /////////////////////////////////////////////////////Fight Logic////////////////////////////////////////////////////////
    public void fight(Card card1, Card card2){

        // Wenn beide Karten Spells sind
        if (card1 instanceof Spell && card2 instanceof Spell) {
            spellVsSpellFight(card1, card2);
            addLogEntry("\n");
            return;
        }

        // sonst checkt es ob mindestens eine der karten einen Spell ist
        if (card1 instanceof Spell || card2 instanceof Spell){
            // wenn es einen spell gibt, dann Spell Vs Monster
            spellVsMonsterFight(card1, card2);
            addLogEntry("\n");

            // sonst Monster Vs Monster
        }else{

            MonsterVsMonster(card1, card2);
            addLogEntry("\n");
        }
        // und einen zeilenumbruch in den logs hinzufügen

    }

    // funktion um den kampf durchzuführen bei spell vs spell
    public void spellVsSpellFight(Card card1, Card card2){
        int spellVsSpell = spellVsSpell(card1, card2);

        // karte 1 im nachteil
        if (spellVsSpell == 1){
            DealDamage(card1, card2, card1.getDamage() / 2, card2.getDamage() * 2);
            // karte 2 im nachteil
        } else if (spellVsSpell == 2) {
            DealDamage(card1, card2, card1.getDamage() * 2, card2.getDamage() /2);
            //gleichstand (beide haben dasselbe Element)
        } else if (spellVsSpell == 3) {
            DealDamage(card1, card2, 0, 0);
            // sonst ein fehler
        }else if (spellVsSpell == 5){
            addLogEntry("ein fehler ist aufgetreten");
            throw new RuntimeException("Ein fehler ist bei der Schadenkalkulation aufgetaucht");
        }
        addLogEntry("\n");
    }

    // funktion um den kampf durchzuführen bei spell vs monster
    public void spellVsMonsterFight(Card card1, Card card2){
        // Sonst ist eine Karte Monster
        int returnn = SpellVsMonster(card1, card2);

        // karte 1 im nachteil
        if (returnn == 1){
            DealDamage(card1, card2, card1.getDamage() / 2, card2.getDamage() * 2);
            // karte 2 im nachteil
        } else if (returnn == 2) {
            DealDamage(card1, card2, card1.getDamage() * 2, card2.getDamage() /2);
            //karte 1 kein dmg
        }else if (returnn == -1){
            DealDamage(card1, card2, 0, card2.getDamage()); // das für karte 1
            //Karte 2 kein dmg
        }else if (returnn == -2){
            DealDamage(card1, card2, card1.getDamage(), 0); // das für karte 2
            // bei einem kraken
        } else if (returnn == 3) {
            //spell macht kein dmg
            if (card1 instanceof Spell){
                addLogEntry("The kraken is immune to Spells");
                DealDamage(card1, card2, 0, card2.getDamage()); // das für karte 1
            }else{
                addLogEntry("The kraken is immune to Spells");
                DealDamage(card1, card2, card1.getDamage(), 0); // das für karte 2
            }
        } else if (returnn == 4) {
            if (card1 instanceof WaterSpell){
                addLogEntry("The Knight drowns because of the water Spell");
                card2.setHp(0);

            } else if (card2 instanceof WaterSpell) {
                addLogEntry("The Knight drowns because of the water Spell");
                card1.setHp(0);
            }
        }
    }

    // Die Funktion berechnet anhand der Elemente welche karte im Vor - und Nachteil ist.
    public int spellVsSpell(Card card1, Card card2){
        // wenn beide karten dasselbe element haben, dann beide karten kein dmg
        if (card1.getElement() == card2.getElement()){
            return 3;
        }

        // wenn karte 1 feuer ist
        if (card1.getElement() == Card.Elements.Fire){
            // gegen regular, ist karte 2 im nachteil
            if (card2.getElement() == Card.Elements.Normal){
                return 2;
                // gegen wasser, ist karte 1 im nachteil
            } else if (card2.getElement() == Card.Elements.Water) {
                return 1;
            }

            // Water
        } else if (card1.getElement() == Card.Elements.Water) {
            // gegen feuer, ist karte 2 im nachteil
            if (card2.getElement() == Card.Elements.Fire){
                return 2;
                // gegen normal, ist karte 1 im nachteil
            } else if (card2.getElement() == Card.Elements.Normal) {
                return 1;
            }

            // Regular
        } else if (card1.getElement() == Card.Elements.Normal) {
            // gegen wasser, karte 2 im nachteil
            if ( card2.getElement() == Card.Elements.Water){
                return 2;
                // gegen feuer, karte 1 im nachteil
            } else if (card2.getElement() == Card.Elements.Fire){
                return 1;
            }
        }

        return 5;
    }

    // Funktion um monster gegen monster kämpfen zu lassen
    public void MonsterVsMonster(Card card1, Card card2){
        // dann die spezial fälle durchgehen
        boolean goblinvsdragon = goblinVsDragonCheck(card1, card2);
        boolean wizzardVsOrk = wizzardVsOrk(card1, card2);
        boolean fireElvesVsDragon = fireElvesVsDragon(card1, card2);

        // wenn ein goblin gegen einen drachen kämpft --> special case, goblin hat angst
        if (goblinvsdragon){
            addLogEntry("The Goblin is too afraid to attack the Dragon!");
            // wenn karte 1 der Goblin ist, dann kein dmg
            if (card1 instanceof Goblin){
                DealDamage(card1, card2, 0, card2.getDamage());
                //sonst karte 2 kein dmg
            }else{
                DealDamage(card1, card2, card1.getDamage(), 0);
            }

            // wenn ein Wizzard gegen einen Ork kämpft --> special case, Wizzard controls ork
        } else if (wizzardVsOrk) {
            addLogEntry("Wizzards can control Orks!");
            // wenn karte 1 der Ork ist, dann kein dmg
            if (card1 instanceof Ork){
                DealDamage(card1, card2, 0, card2.getDamage());
                //sonst karte 2 kein dmg
            }else{
                DealDamage(card1, card2, card1.getDamage(), 0);
            }

            // wenn ein Fire elf gegen einen drachen kämpft --> special case, fire elf evades dragon attack
        } else if (fireElvesVsDragon) {
            addLogEntry("The Fire Elf evaded the Dragon's Attack!");
            // wenn karte 1 der Drache ist, dann kein dmg
            if (card1 instanceof Dragon){
                DealDamage(card1, card2, 0, card2.getDamage());
                //sonst karte 2 kein dmg
            }else{
                DealDamage(card1, card2, card1.getDamage(), 0);
            }

            // sonst ganz normaler kampf
        }else {
            DealDamage(card1, card2, card1.getDamage(), card2.getDamage());
        }
    }

    // Funktion wenn ein monster gegen einen Spell kämpft
    public  int SpellVsMonster(Card card1, Card card2){

        // Kraken vs Spell special Case --> Kraken ignoriert dmg
        boolean krakenCheck = krakenVsSpells(card1, card2);
        if (krakenCheck){
            return 3;
        }

        // Knights vs WaterSpell special case --> Knight ertrinkt
        boolean KnightVsWaterSpell = waterSpellVsKnight(card1, card2);
        if (KnightVsWaterSpell){
            return 4;
        }

        // wenn zb ein feuer monster einen feuerspell angreift, dann kriegt der spell kein dmg
        if (card1 instanceof Spell){
            if (card1.getElement() == card2.getElement()){
                return -1;
            }
        }

        // wenn zb ein feuer monster einen feuerspell angreift, dann kriegt der spell kein dmg
        if (card2 instanceof Spell){
            if (card2.getElement() == card1.getElement()){
                return -2;
            }
        }


        // sonst alle elemente einzeln überprüfen
        if (card1.getElement() == Card.Elements.Fire){
            if (card2.getElement() == Card.Elements.Normal){
                return 2;
            } else if (card2.getElement() == Card.Elements.Water) {
                return 1;
            }
            // Water vs Feuer -> dmg doppel
        } else if (card1.getElement() == Card.Elements.Water) {
            if (card2.getElement() == Card.Elements.Fire){
                return 2;
            } else if (card2.getElement() == Card.Elements.Normal) {
                return 1;
            }

            // Normal vs Water -> dmg doppel
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
