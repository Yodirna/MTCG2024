package at.fhtw.mtcg.models;

import at.fhtw.mtcg.cards.Monsters.Dragons.Dragon;
import at.fhtw.mtcg.cards.Monsters.Elves.Elf;
import at.fhtw.mtcg.cards.Monsters.Goblins.Goblin;
import at.fhtw.mtcg.cards.Monsters.Knights.Knight;
import at.fhtw.mtcg.cards.Monsters.Kraken.Kraken;
import at.fhtw.mtcg.cards.Monsters.Orks.Ork;
import at.fhtw.mtcg.cards.Monsters.Wizards.Wizard;
import at.fhtw.mtcg.cards.Spells.Spell;
import at.fhtw.mtcg.cards.Spells.WaterSpell;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles all the battle logic between cards.
 * It covers spell vs. spell, spell vs. monster, and monster vs. monster scenarios,
 * including special cases and conditions.
 */
public class GameLogic {

    // Holds the logs of all battle events
    private final List<String> battleLog = new ArrayList<>();

    /* -------------------------------------------------------------------------
     *                          LOGGING METHODS
     * ------------------------------------------------------------------------- */

    /**
     * Adds a message entry to the battle log.
     */
    public void addLogEntry(String message) {
        battleLog.add(message);
    }

    /**
     * @return the entire battle log as a list of strings.
     */
    public List<String> getBattleLog() {
        return battleLog;
    }

    /**
     * Prints all events that occurred during the battle to the console.
     * NOT IN USE CURRENTLY! (for debugging purposes)
     */
    public void printBattleLog() {
        System.out.println("Battle Log:");
        for (String logEntry : battleLog) {
            System.out.println(logEntry);
        }
    }

    /* -------------------------------------------------------------------------
     *                          MAIN FIGHT METHOD
     * ------------------------------------------------------------------------- */

    /**
     * Initiates a fight between two cards.
     *
     * @param firstCard the first card in the fight.
     * @param secondCard the second card in the fight.
     */
    public void fight(Card firstCard, Card secondCard) {
        // If both cards are spells: Spell vs. Spell
        if (firstCard instanceof Spell && secondCard instanceof Spell) {
            spellVsSpellFight(firstCard, secondCard);
            addLogEntry("\n");
            return;
        }

        // If one of them is a spell: Spell vs. Monster
        if (firstCard instanceof Spell || secondCard instanceof Spell) {
            spellVsMonsterFight(firstCard, secondCard);
            addLogEntry("\n");
        } else {
            // Otherwise, Monster vs. Monster
            monsterVsMonsterFight(firstCard, secondCard);
            addLogEntry("\n");
        }
    }

    /* -------------------------------------------------------------------------
     *                      SPELL VS. SPELL METHODS
     * ------------------------------------------------------------------------- */

    /**
     * Handles a fight where both cards are spells. It checks which spell is at an advantage
     * or disadvantage based on their elements.
     *
     * @param card1 the first spell.
     * @param card2 the second spell.
     */
    public void spellVsSpellFight(Card card1, Card card2) {
        int result = spellVsSpell(card1, card2);

        // Card 1 is at a disadvantage
        if (result == 1) {
            dealDamage(card1, card2, card1.getDamage() / 2, card2.getDamage() * 2);
        }
        // Card 2 is at a disadvantage
        else if (result == 2) {
            dealDamage(card1, card2, card1.getDamage() * 2, card2.getDamage() / 2);
        }
        // Both cards are of the same element
        else if (result == 3) {
            dealDamage(card1, card2, 0, 0);
        }
        // Error
        else if (result == 5) {
            addLogEntry("An error occurred");
            throw new RuntimeException("An error occurred during damage calculation");
        }
        addLogEntry("\n");
    }

    /**
     * Determines the disadvantage/advantage for two spells based on their elements.
     * YOU CAN FIND THIS LEGEND IN THE DOCUMENTATION AS WELL!
     *
     * @param card1 the first spell card.
     * @param card2 the second spell card.
     * @return an integer representing who is at a disadvantage or whether there's an error/same element.
     *         1 -> Card 1 at disadvantage,
     *         2 -> Card 2 at disadvantage,
     *         3 -> Same element,
     *         5 -> Error (none of the above conditions).
     */
    public int spellVsSpell(Card card1, Card card2) {
        // If both cards have the same element
        if (card1.getElement() == card2.getElement()) {
            return 3;
        }

        // Check combinations
        if (card1.getElement() == Card.Elements.Fire) {
            if (card2.getElement() == Card.Elements.Normal) {
                return 2; // Card 2 is at a disadvantage
            } else if (card2.getElement() == Card.Elements.Water) {
                return 1; // Card 1 is at a disadvantage
            }
        } else if (card1.getElement() == Card.Elements.Water) {
            if (card2.getElement() == Card.Elements.Fire) {
                return 2;
            } else if (card2.getElement() == Card.Elements.Normal) {
                return 1;
            }
        } else if (card1.getElement() == Card.Elements.Normal) {
            if (card2.getElement() == Card.Elements.Water) {
                return 2;
            } else if (card2.getElement() == Card.Elements.Fire) {
                return 1;
            }
        }
        return 5;
    }

    /* -------------------------------------------------------------------------
     *                    SPELL VS. MONSTER METHODS
     * ------------------------------------------------------------------------- */

    /**
     * Fight scenario -> where one card is a spell and the other is a monster.
     *
     * @param card1 the first card.
     * @param card2 the second card.
     */
    public void spellVsMonsterFight(Card card1, Card card2) {
        int spellMonsterResult = spellVsMonster(card1, card2);

        // Card 1 is at a disadvantage
        if (spellMonsterResult == 1) {
            dealDamage(card1, card2, card1.getDamage() / 2, card2.getDamage() * 2);
        }
        // Card 2 is at a disadvantage
        else if (spellMonsterResult == 2) {
            dealDamage(card1, card2, card1.getDamage() * 2, card2.getDamage() / 2);
        }
        // Card 1 deals no damage
        else if (spellMonsterResult == -1) {
            dealDamage(card1, card2, 0, card2.getDamage());
        }
        // Card 2 deals no damage
        else if (spellMonsterResult == -2) {
            dealDamage(card1, card2, card1.getDamage(), 0);
        }
        // Kraken special case (immune to spells)
        else if (spellMonsterResult == 3) {
            addLogEntry("The Kraken is immune to spells");
            if (card1 instanceof Spell) {
                dealDamage(card1, card2, 0, card2.getDamage());
            } else {
                dealDamage(card1, card2, card1.getDamage(), 0);
            }
        }
        // Knight vs. WaterSpell special case (knight drowns)
        else if (spellMonsterResult == 4) {
            if (card1 instanceof WaterSpell) {
                addLogEntry("The Knight drowns due to the water spell");
                card2.setHp(0);
            } else if (card2 instanceof WaterSpell) {
                addLogEntry("The Knight drowns due to the water spell");
                card1.setHp(0);
            }
        }
    }

    /**
     * Determines the disadvantage/advantage/special cases for a spell vs. monster fight.
     * YOU CAN FIND THIS LEGEND IN THE DOCUMENTATION AS WELL!
     *
     * @param card1 the first card.
     * @param card2 the second card.
     * @return integer code describing how the fight resolves:
     *         1 -> card1 disadvantage,
     *         2 -> card2 disadvantage,
     *        -1 -> card1 deals no damage,
     *        -2 -> card2 deals no damage,
     *         3 -> Kraken special case (immune to spells),
     *         4 -> Knight vs. WaterSpell special case,
     *         5 -> General error case (no recognized condition).
     *
     */
    public int spellVsMonster(Card card1, Card card2) {
        // Kraken immunity
        if (isKrakenVsSpell(card1, card2)) {
            return 3;
        }
        // Knight vs. WaterSpell
        if (isKnightVsWaterSpell(card1, card2)) {
            return 4;
        }

        // If card1 is a spell and shares the same element as card2 => card2 takes no damage
        if (card1 instanceof Spell && card1.getElement() == card2.getElement()) {
            return -1;
        }

        // If card2 is a spell and shares the same element as card1 => card1 takes no damage
        if (card2 instanceof Spell && card2.getElement() == card1.getElement()) {
            return -2;
        }

        // Standard elemental checks
        if (card1.getElement() == Card.Elements.Fire) {
            if (card2.getElement() == Card.Elements.Normal) {
                return 2;
            } else if (card2.getElement() == Card.Elements.Water) {
                return 1;
            }
        } else if (card1.getElement() == Card.Elements.Water) {
            if (card2.getElement() == Card.Elements.Fire) {
                return 2;
            } else if (card2.getElement() == Card.Elements.Normal) {
                return 1;
            }
        } else if (card1.getElement() == Card.Elements.Normal) {
            if (card2.getElement() == Card.Elements.Water) {
                return 2;
            } else if (card2.getElement() == Card.Elements.Fire) {
                return 1;
            }
        }
        return 5;
    }

    /* -------------------------------------------------------------------------
     *                    MONSTER VS. MONSTER METHODS
     * ------------------------------------------------------------------------- */

    /**
     * Handles a fight scenario where both cards are monsters.
     *
     * @param card1 the first monster card.
     * @param card2 the second monster card.
     */
    public void monsterVsMonsterFight(Card card1, Card card2) {
        // Special checks
        boolean goblinVsDragon = isGoblinFightingDragon(card1, card2);
        boolean wizardVsOrk = isWizardFightingOrk(card1, card2);
        boolean fireElfVsDragon = isFireElfFightingDragon(card1, card2);

        // Goblin vs Dragon special case
        if (goblinVsDragon) {
            addLogEntry("The Goblin is too afraid to attack the Dragon!");
            if (card1 instanceof Goblin) {
                // card1 deals no damage
                dealDamage(card1, card2, 0, card2.getDamage());
            } else {
                // card2 deals no damage
                dealDamage(card1, card2, card1.getDamage(), 0);
            }
        }
        // Wizard vs Ork special case
        else if (wizardVsOrk) {
            addLogEntry("Wizards can control Orks!");
            if (card1 instanceof Ork) {
                dealDamage(card1, card2, 0, card2.getDamage());
            } else {
                dealDamage(card1, card2, card1.getDamage(), 0);
            }
        }
        // Fire Elf vs Dragon special case
        else if (fireElfVsDragon) {
            addLogEntry("The Fire Elf evaded the Dragon's Attack!");
            if (card1 instanceof Dragon) {
                dealDamage(card1, card2, 0, card2.getDamage());
            } else {
                dealDamage(card1, card2, card1.getDamage(), 0);
            }
        }
        // Otherwise, no special cases -> normal damage exchange
        else {
            dealDamage(card1, card2, card1.getDamage(), card2.getDamage());
        }
    }

    /* -------------------------------------------------------------------------
     *                    DAMAGE CALCULATION & HIT TYPE
     * ------------------------------------------------------------------------- */

    /**
     * Deals damage between card1 and card2, factoring in card1Damage and card2Damage.
     * Logs the attacks, damage dealt, and remaining HP of both cards.
     *
     * @param card1       the first card attacking and defending.
     * @param card2       the second card attacking and defending.
     * @param card1Damage the damage card1 inflicts on card2.
     * @param card2Damage the damage card2 inflicts on card1.
     */
    public void dealDamage(Card card1, Card card2, int card1Damage, int card2Damage) {
        // Card1's attack
        addLogEntry(card1.getName() + " is attacking " + card2.getName());
        printHitType(card1, card1Damage);
        addLogEntry(card1.getName() + " dealt " + card1Damage + " damage to " + card2.getName());
        card2.setHp(card2.getHp() - card1Damage);
        addLogEntry(card2.getName() + " now has " + card2.getHp() + " HP");

        // Card2's attack
        addLogEntry(card2.getName() + " is attacking " + card1.getName());
        printHitType(card2, card2Damage);
        addLogEntry(card2.getName() + " dealt " + card2Damage + " damage to " + card1.getName());
        card1.setHp(card1.getHp() - card2Damage);
        addLogEntry(card1.getName() + " now has " + card1.getHp() + " HP");
    }

    /**
     * Determines the type of the hit (critical, inefficient, ineffective).
     *
     * @param card       the card delivering the hit.
     * @param cardDamage the damage that card actually inflicts after modifiers.
     */
    public void printHitType(Card card, int cardDamage) {
        if (cardDamage > card.getDamage()) {
            addLogEntry("Critical Hit!");
        } else if (cardDamage < card.getDamage() && cardDamage > 0) {
            addLogEntry("Inefficient Hit!");
        } else if (cardDamage == 0) {
            addLogEntry("Ineffective Hit!");
        }
    }

    /* -------------------------------------------------------------------------
     *                    SPECIAL CASE CHECKS
     * ------------------------------------------------------------------------- */

    /**
     * Checks if a Goblin is fighting a Dragon.
     */
    public boolean isGoblinFightingDragon(Card card1, Card card2) {
        return (card1 instanceof Goblin && card2 instanceof Dragon)
                || (card2 instanceof Goblin && card1 instanceof Dragon);
    }

    /**
     * Checks if a Wizard is fighting an Ork.
     */
    public boolean isWizardFightingOrk(Card card1, Card card2) {
        return (card1 instanceof Wizard && card2 instanceof Ork)
                || (card2 instanceof Wizard && card1 instanceof Ork);
    }

    /**
     * Checks if a Fire Elf is fighting a Dragon.
     */
    public boolean isFireElfFightingDragon(Card card1, Card card2) {
        return (card1 instanceof Elf && card2 instanceof Dragon)
                || (card2 instanceof Elf && card1 instanceof Dragon);
    }

    /**
     * Checks if a Kraken is fighting a Spell (Kraken is immune to spells).
     */
    public boolean isKrakenVsSpell(Card card1, Card card2) {
        return (card1 instanceof Kraken && card2 instanceof Spell)
                || (card1 instanceof Spell && card2 instanceof Kraken);
    }

    /**
     * Checks if a Knight is fighting a WaterSpell (knight drowns instantly).
     */
    public boolean isKnightVsWaterSpell(Card card1, Card card2) {
        return (card1 instanceof Knight && card2 instanceof WaterSpell)
                || (card2 instanceof Knight && card1 instanceof WaterSpell);
    }
}
