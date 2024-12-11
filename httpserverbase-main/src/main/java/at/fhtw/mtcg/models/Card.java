package at.fhtw.mtcg.models;
import java.lang.*;


public class Card {
    private int hp;
    private int damage;
    private String name;
    private Elements element;
    private String id;

    public static enum Elements{
        Normal,
        Water,
        Fire,
        Neutral
    }

    public static Card.Elements getCardElement(String cardName){
        if (cardName.contains("Water")) {
            return Card.Elements.Water;
        } else if (cardName.contains("Fire")) {
            return Card.Elements.Fire;
        } else if (cardName.contains("Regular")) {
            return Card.Elements.Normal;
        }
        else {
            //throw new IllegalArgumentException("Unknown card name: " + cardName);
            return Card.Elements.Neutral;
        }
    }

    public Card(String cardName, Elements cardElement, int cardDamage, int CardHp) {
        this.name = cardName;
        this.element = cardElement;
        this.damage = cardDamage;
        this.hp = CardHp;
    }

    public void setId(String id) {
        this.id = id;
    }

    //Getter
    public String getName(){
        return this.name;
    }
    public Elements getElement(){
        return this.element;
    }

    public int getHp(){
        return this.hp;
    }
    public int getDamage(){
        return this.damage;
    }
    public String getID(){
        return this.id;
    }

    //Setter
    public void setName(String name){
        this.name = name;
    }
    public void setElement(Elements element){
        this.element = element;
    }
    public void setHp(int HP){
        this.hp = HP;
    }
    public void setDamage(int damage){
        this.damage = damage;
    }

}
