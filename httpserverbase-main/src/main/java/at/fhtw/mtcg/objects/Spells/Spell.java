package at.fhtw.mtcg.objects.Spells;


import at.fhtw.mtcg.models.Card;

public class Spell extends Card {
    public Spell(String name, Elements element, int dmg, int hp){
        super(name, element, dmg, hp);
    }
}
