package at.fhtw.mtcg.classes;

import at.fhtw.mtcg.classes.Monsters.Dragons.*;
import at.fhtw.mtcg.classes.Monsters.Elves.*;
import at.fhtw.mtcg.classes.Monsters.Goblins.*;
import at.fhtw.mtcg.classes.Monsters.Knights.*;
import at.fhtw.mtcg.classes.Monsters.Kraken.*;
import at.fhtw.mtcg.classes.Monsters.Orks.*;
import at.fhtw.mtcg.classes.Monsters.Wizards.*;
import at.fhtw.mtcg.classes.Spells.*;
import at.fhtw.mtcg.models.Card;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CardToClassMapper {
    private static final Map<String, Supplier<Card>> cardRegistry = new HashMap<>();

    static {

        // Alle Dragons
        cardRegistry.put("Dragon", NormalDragon::new);
        cardRegistry.put("WaterDragon", WaterDragon::new);
        cardRegistry.put("FireDragon", FireDragon::new);

        // Alle Elves
        cardRegistry.put("Elf", NormalElf::new);
        cardRegistry.put("WaterElf", WaterElf::new);
        cardRegistry.put("FireElf", FireElf::new);

        // Alle Goblins
        cardRegistry.put("Goblin", NormalGoblin::new);
        cardRegistry.put("WaterGoblin", WaterGoblin::new);
        cardRegistry.put("FireGoblin", FireGoblin::new);

        // Alle Knights
        cardRegistry.put("Knight", NormalKnight::new);
        cardRegistry.put("WaterKnight", WaterKnight::new);
        cardRegistry.put("FireKnight", FireKnight::new);

        // Kraken
        cardRegistry.put("Kraken", Kraken::new);

        // Alle Orks
        cardRegistry.put("Ork", NormalOrk::new);
        cardRegistry.put("WaterOrk", WaterOrk::new);
        cardRegistry.put("FireOrk", FireOrk::new);

        // Alle Wizzards
        cardRegistry.put("Wizard", NormalWizard::new);
        cardRegistry.put("WaterWizard", WaterWizard::new);
        cardRegistry.put("FireWizard", FireWizard::new);

        // Alle Spells
        cardRegistry.put("RegularSpell", NormalSpell::new);
        cardRegistry.put("WaterSpell", WaterSpell::new);
        cardRegistry.put("FireSpell", FireSpell::new);

    }

    public static Card createCard(String cardType) {
        Supplier<Card> cardSupplier = cardRegistry.get(cardType);
        if (cardSupplier != null) {
            return cardSupplier.get();
        }
        throw new IllegalArgumentException("Unbekannter Karten-Typ: " + cardType);
    }


    public static Card createRealCard(Card card) {
        Card newCard = createCard(card.getName());
        newCard.setId(card.getID());
        newCard.setName(card.getName());
        newCard.setDamage(card.getDamage());
        newCard.setElement(card.getElement());
        newCard.setHp(card.getHp());
        return newCard;
    }
}



