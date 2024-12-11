package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.PreparedStatement;
import java.util.Vector;

public class BattleRepository {

    private UnitOfWork unitOfWork;
    public BattleRepository(UnitOfWork unitOfWork){
        this.unitOfWork = unitOfWork;
    }


    public boolean updateWinnerStats(String username, UnitOfWork unitOfWork){
        UserRepository userRepository = new UserRepository(new UnitOfWork());
        int userID = userRepository.getUserID(username);
        try {
            String query = "Update \"user\" set wins = wins + 1, mmr = mmr+5 where fk_user_id = ?";
            PreparedStatement pstmt = unitOfWork.prepareStatement(query);
            pstmt.setInt(1, userID);
            pstmt.executeUpdate();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateLoserStats(String username, UnitOfWork unitOfWork){
        UserRepository userRepository = new UserRepository(new UnitOfWork());
        int userID = userRepository.getUserID(username);
        try {
            String query = "Update \"user\" set losses = losses + 1, mmr = mmr-5 where fk_user_id = ?";
            PreparedStatement pstmt = unitOfWork.prepareStatement(query);
            pstmt.setInt(1, userID);
            pstmt.executeUpdate();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;

    };
    public boolean winnerTakseLoserCards(String winnerUsername, String loserUsername, UnitOfWork unitOfWork){
        try {

            UserRepository userRepository = new UserRepository(new UnitOfWork());
            int winnerUserID = userRepository.getUserID(winnerUsername);
            int loserUserID = userRepository.getUserID(loserUsername);

            // Delete loser deck
            deleteLoserDeck(loserUserID, unitOfWork);


            DeckRepository deckRepository = new DeckRepository(new UnitOfWork());
            // Get loser deck
            Vector<String> loserDeckIds =  deckRepository.getDeckCardsIds(loserUserID);

            // Winner takes loser cards
            for (String cardId: loserDeckIds) {
                winnerTakesCardsInDB(winnerUserID, cardId, unitOfWork);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    private boolean deleteLoserDeck(int loserUserID, UnitOfWork unitOfWork){
        try {

            String deleteLoserDeckQuery = "Delete from \"decks\" where fk_decks_user_id = ?";
            PreparedStatement deleteLoserDeckPstmt = unitOfWork.prepareStatement(deleteLoserDeckQuery);
            deleteLoserDeckPstmt.setInt(1, loserUserID);
            unitOfWork.registerNew(deleteLoserDeckPstmt);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    private boolean winnerTakesCardsInDB(int winnerUserID, String cardId, UnitOfWork unitOfWork){
        // acquired cards auf dem winner übertragen
        try {
            // Transfer cards to winner
            String winnerTakesAcquiredCards = "Update\"acquired_cards\" set fk_acquired_cards_user_id = ? where fk_acquired_cards_card_id = ?";
            PreparedStatement winnerTakesAcquiredCardsPstmt = unitOfWork.prepareStatement(winnerTakesAcquiredCards);
            winnerTakesAcquiredCardsPstmt.setInt(1, winnerUserID);
            winnerTakesAcquiredCardsPstmt.setString(2, cardId);
            unitOfWork.registerNew(winnerTakesAcquiredCardsPstmt);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}

