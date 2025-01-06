package at.fhtw.mtcg.objects;

import at.fhtw.mtcg.objects.Monsters.Dragons.*;
import at.fhtw.mtcg.objects.Monsters.Elves.*;
import at.fhtw.mtcg.objects.Monsters.Goblins.*;
import at.fhtw.mtcg.objects.Monsters.Knights.*;
import at.fhtw.mtcg.objects.Monsters.Kraken.*;
import at.fhtw.mtcg.objects.Monsters.Orks.*;
import at.fhtw.mtcg.objects.Monsters.Wizards.*;
import at.fhtw.mtcg.objects.Spells.*;
import at.fhtw.mtcg.models.Card;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CardToClassMapper {
    private static final Map<String, Supplier<Card>> cardRegistry = new HashMap<>();

    static {

        // All Dragons
        cardRegistry.put("Dragon", NormalDragon::new);
        cardRegistry.put("WaterDragon", WaterDragon::new);
        cardRegistry.put("FireDragon", FireDragon::new);

        // All Elves
        cardRegistry.put("Elf", NormalElf::new);
        cardRegistry.put("WaterElf", WaterElf::new);
        cardRegistry.put("FireElf", FireElf::new);

        // All Goblins
        cardRegistry.put("Goblin", NormalGoblin::new);
        cardRegistry.put("WaterGoblin", WaterGoblin::new);
        cardRegistry.put("FireGoblin", FireGoblin::new);

        // All Knights
        cardRegistry.put("Knight", NormalKnight::new);
        cardRegistry.put("WaterKnight", WaterKnight::new);
        cardRegistry.put("FireKnight", FireKnight::new);

        // Kraken
        cardRegistry.put("Kraken", Kraken::new);

        // All Orks
        cardRegistry.put("Ork", NormalOrk::new);
        cardRegistry.put("WaterOrk", WaterOrk::new);
        cardRegistry.put("FireOrk", FireOrk::new);

        // All Wizzards
        cardRegistry.put("Wizard", NormalWizard::new);
        cardRegistry.put("WaterWizard", WaterWizard::new);
        cardRegistry.put("FireWizard", FireWizard::new);

        // All Spells
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



