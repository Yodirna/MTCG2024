package at.fhtw.mtcg.models;

import java.util.Vector;
public class User {
    String username;
    int coins;
    int mmr;
    int spent_coins;
    int allWins;
    int allLosses;
    UserCredentials userCredentials;
    Vector<Card> stack;

    public User(String username, String password){
        this.username = username;
        this.stack = new Vector<>();
    }

    public String getUsername(){
        return this.username;
    }
    public Vector<Card> getStack(){
        return this.stack;
    }

    public void addCardToStack(Card card){
        this.stack.add(card);
    }

    public void removeCardFromStack(Card card){
        boolean cardInDeck = false;

        for (Card stackCard : stack){
            if (stackCard.getName().equals(card.getName())){
                cardInDeck = true;
                System.out.println("Card found in Stack");
            }else{
                System.out.println("Couldn't find card in Stack");
            }
        }
        if (cardInDeck){
            this.stack.remove(card);
        }
    }

    public void printStack(){
        System.out.println("\n" + this.username + " Card Stack:");
        for (Card card : stack) {

            System.out.println("Name: " + card.getName() + " Damage: " + card.getDamage() + " , HP: " + card.getHp()+ ", Class: " + card.getClass());
        }
    }


    private void handleLoss(){
        this.mmr -=3;
        System.out.println("neues mmr: " + this.mmr);
    }

    private void handleWin(){
        this.mmr +=3;
        System.out.println("neues mmr: " + this.mmr);

    }
}
