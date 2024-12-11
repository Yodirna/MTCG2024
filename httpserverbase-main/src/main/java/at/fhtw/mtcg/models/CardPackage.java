package at.fhtw.mtcg.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CardPackage {
    @Getter @Setter
    private List<Card> cards;

    // constructor der eine liste entgegennnimmt
    public CardPackage(List<Card> cards) {
        if (cards == null || cards.size() != 5) {
            throw new IllegalArgumentException("Liste muss genau 5 Karten enthalten");
        }
        this.cards = new ArrayList<>(cards);
    }

    // Getter und setter
    public Card getCard1() {
        return cards.get(0);
    }

    public void setCard1(Card card) {
        cards.set(0, card);
    }

    public Card getCard2() {
        return cards.get(1);
    }

    public void setCard2(Card card) {
        cards.set(1, card);
    }

    public Card getCard3() {
        return cards.get(2);
    }

    public void setCard3(Card card) {
        cards.set(2, card);
    }

    public Card getCard4() {
        return cards.get(3);
    }

    public void setCard4(Card card) {
        cards.set(3, card);
    }

    public Card getCard5() {
        return cards.get(4);
    }

    public void setCard5(Card card) {
        cards.set(4, card);
    }

    // um ein CardPackage in Liste von Karten zu verwandeln
    public List<Card> toList() {
        return new ArrayList<>(cards);
    }
}
