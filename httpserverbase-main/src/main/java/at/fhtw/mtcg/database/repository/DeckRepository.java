package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.models.Card;
import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DeckRepository {
    private UnitOfWork unitOfWork;

    public DeckRepository(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    // Return all card IDs of the deck
    public Vector<String> getDeckCardsIds(int userID ){
        try{
            String getCardsQuery = "Select * from decks where fk_decks_user_id = ?";
            PreparedStatement getCardsStmt = unitOfWork.prepareStatement(getCardsQuery);
            getCardsStmt.setInt(1, userID);
            ResultSet ResultSet = getCardsStmt.executeQuery();
            Vector<String> cardIDs = new Vector<>();
            while(ResultSet.next()){
                String currentCardID = ResultSet.getString("fk_decks_card_id");
                cardIDs.add(currentCardID);
            }
            return cardIDs;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Get all cards of the deck of the user
    public List<Card> getDeck(String username)  {
        try {

            CardRepository cardRepository = new CardRepository(new UnitOfWork());
            UserRepository userRepository = new UserRepository(new UnitOfWork());
            int userID = userRepository.getUserID(username);
            // holt sich die Ids der Deck Karten von der Person
            Vector<String> deckCardsIds = getDeckCardsIds(userID);
            int len = deckCardsIds.size();

            // erstellt einen leeren Vector, wo man die Karten reintut
            Vector<Card> allDeckCards = new Vector<Card>();
            //iteration, um die Karten Informationen jede Karte zu holen
            for (int i = 0; i < len; i++) {
                Card card = cardRepository.getCardData(deckCardsIds.get(i));
                allDeckCards.add(card);
            }

            // returnt die Deck Karten
            return allDeckCards;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean configureDeck(String username, Vector<String> cardsToConfigure) {
        UserRepository userRepository = new UserRepository(new UnitOfWork());
        int userID = userRepository.getUserID(username);

        getDeckCardsIds(userID);

        int cardIdsLength = cardsToConfigure.size();
        try {
            for (int i = 0; i < cardIdsLength; i++) {
                String cardsIntoDeckQuery = "INSERT INTO \"decks\" (fk_decks_user_id, fk_decks_card_id) VALUES (?, ?)";
                PreparedStatement pstmt = unitOfWork.prepareStatement(cardsIntoDeckQuery);
                pstmt.setInt(1, userID);
                pstmt.setString(2, cardsToConfigure.get(i));
                unitOfWork.registerNew(pstmt);
            }
            unitOfWork.commitTransaction();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
        }
        return false;
    }



    // Check if the user owns all cards that are provided in the body
    public boolean checkProvidedCardsWithAcquiredCardsInDB(int userID, Vector<String> cardsToConfigure){
        try {
            // 4 cards are provided
            int cardLength = cardsToConfigure.size();
            // acquired cards and cards in the body
            int cardsInDbAndConfiguration = 0;
            for (int i = 0; i < cardLength; i++) {
                String currentCardID = cardsToConfigure.get(i);

                String getCardsQuery = "SELECT * FROM \"acquired_cards\" where fk_acquired_cards_user_id = ?";
                PreparedStatement getCardsStmt = unitOfWork.prepareStatement(getCardsQuery);
                getCardsStmt.setInt(1, userID);
                ResultSet resultSet = getCardsStmt.executeQuery();

                while (resultSet.next()){
                    String DatabaseCard = resultSet.getString("fk_acquired_cards_card_id");

                    if (DatabaseCard.equals(currentCardID)){
                        // if the card in the body is the same as the card in the database then increment the counter
                        cardsInDbAndConfiguration++;
                    }
                }
            }
            // if player has all cards then return true
            return cardsInDbAndConfiguration == (cardsToConfigure.size());

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    // Delete the deck of the user
    public void deleteUserDeck(int userID){
        try {
            String query = "Delete From \"decks\" where fk_decks_user_id = ?";
            PreparedStatement pstmt = unitOfWork.prepareStatement(query);
            pstmt.setInt(1, userID);
            pstmt.executeUpdate();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Returns the cards in the form of a JSON
    public List<String> createJsonForAllCards(List<Card> allCards) {
        List<String> jsonList = new ArrayList<>();

        for (Card card : allCards) {

            StringBuilder jsonBuilder = new StringBuilder("\n{");
            jsonBuilder.append("\"id\": \"").append(card.getID()).append("\",");
            jsonBuilder.append("\"name\": \"").append(card.getName()).append("\",");
            jsonBuilder.append("\"damage\": ").append(card.getDamage());
            jsonBuilder.append("\"element\": ").append(card.getElement());
            jsonBuilder.append("}");
            jsonList.add(jsonBuilder.toString());
        }

        return jsonList;
    }

}
