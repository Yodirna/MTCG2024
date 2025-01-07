package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.models.Card;
import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DeckRepository {
    private final UnitOfWork unitOfWork;

    public DeckRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    // Return all card IDs of the deck
    public Vector<String> getDeckCardsIds(int userID) {
        Vector<String> cardIDs = new Vector<>();
        String query = "SELECT * FROM decks WHERE fk_decks_user_id = ?";

        try (PreparedStatement stmt = unitOfWork.prepareStatement(query)) {
            stmt.setInt(1, userID);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                cardIDs.add(resultSet.getString("fk_decks_card_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cardIDs;
    }

    // Get all cards of the user's deck
    public List<Card> getDeck(String username) {
        List<Card> allDeckCards = new ArrayList<>();

        try {
            CardRepository cardRepository = new CardRepository(new UnitOfWork());
            UserRepository userRepository = new UserRepository(new UnitOfWork());
            int userID = userRepository.getUserID(username);

            Vector<String> deckCardIds = getDeckCardsIds(userID);

            for (String cardId : deckCardIds) {
                allDeckCards.add(cardRepository.getCardData(cardId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allDeckCards;
    }

    public boolean configureDeck(String username, Vector<String> cardsToConfigure) {
        UserRepository userRepository = new UserRepository(new UnitOfWork());
        int userID = userRepository.getUserID(username);

        try {
            for (String cardId : cardsToConfigure) {
                String query = "INSERT INTO decks (fk_decks_user_id, fk_decks_card_id) VALUES (?, ?)";
                PreparedStatement stmt = unitOfWork.prepareStatement(query);
                stmt.setInt(1, userID);
                stmt.setString(2, cardId);
                unitOfWork.registerNew(stmt);
            }
            unitOfWork.commitTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
        }
        return false;
    }

    // Check if the user owns all cards provided
    public boolean checkProvidedCardsWithAcquiredCardsInDB(int userID, Vector<String> cardsToProcess) {
        int matchingCards = 0;

        try {
            for (String cardId : cardsToProcess) {
                String query = "SELECT * FROM acquired_cards WHERE fk_acquired_cards_user_id = ?";
                try (PreparedStatement stmt = unitOfWork.prepareStatement(query)) {
                    stmt.setInt(1, userID);
                    ResultSet resultSet = stmt.executeQuery();

                    while (resultSet.next()) {
                        if (resultSet.getString("fk_acquired_cards_card_id").equals(cardId)) {
                            matchingCards++;
                        }
                    }
                }
            }
            return matchingCards == cardsToProcess.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete the user's deck
    public void deleteUserDeck(int userID) {
        String query = "DELETE FROM decks WHERE fk_decks_user_id = ?";

        try (PreparedStatement stmt = unitOfWork.prepareStatement(query)) {
            stmt.setInt(1, userID);
            stmt.executeUpdate();
        } catch (Exception e) {
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
            jsonBuilder.append("\"damage\": ").append(card.getDamage()).append(",");
            jsonBuilder.append("\"element\": \"").append(card.getElement()).append("\"");
            jsonBuilder.append("}");
            jsonList.add(jsonBuilder.toString());
        }

        return jsonList;
    }
}
