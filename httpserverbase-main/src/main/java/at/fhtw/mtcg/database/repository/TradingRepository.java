package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.models.Trade;
import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class TradingRepository {
    private UnitOfWork unitOfWork;

    public TradingRepository(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }


    // Holt alle Trades Aus der DB
    public Vector<String> getAllTrades(){
        try {
            String statsQuery = "Select * from \"trades\"";
            PreparedStatement statsStmt = unitOfWork.prepareStatement(statsQuery);
            ResultSet resultSet = statsStmt.executeQuery();
            // Tut die Trades anschließend in einem Vektor
            Vector<String> allTrades = new Vector<>();
            while (resultSet.next()){
                String tradeID = resultSet.getString("trade_id");
                String cardToTradeID = resultSet.getString("card_to_trade_id");
                String Type = resultSet.getString("type");
                int damage = resultSet.getInt("minimum_damage");
                String data = "{"
                        + "\"Id\": \"" + tradeID + "\", "
                        + "\"CardToTrade\": " + cardToTradeID + ", "
                        + "\"Type\": " + Type + ", "
                        + "\"MinimumDamage\": " + damage + "}";

                allTrades.add(data);
            }
            return allTrades;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //löscht einen Trade
    public boolean deleteTrade(String tradeID, UnitOfWork unitOfWork){
        // einmal verwende ich einen unitof work und einmal nicht --> also habe ich es optional gemacht
            try {
                String statsQuery = "Delete From  \"trades\" where trade_id = ?";
                PreparedStatement PrepStmt = unitOfWork.prepareStatement(statsQuery);
                unitOfWork.registerNew(PrepStmt);
                PrepStmt.setString(1, tradeID);
                int result = PrepStmt.executeUpdate();
                if (result == 1){
                    unitOfWork.commitTransaction();
                    return true;
                };
            }catch (Exception e){
                unitOfWork.rollbackTransaction();
                e.printStackTrace();
            }

        return false;
    }
    // erstellt einen Trade
    public boolean createTrade(String tradeID, String cardtoTradeID, String type, int minimumDamage, int userID){
        // da ist eun unit of work unnötig, weil nicht mehrere sqls hintereinander exekutiert werden
        try {
            String statsQuery = "INSERT INTO \"trades\" (trade_id, card_to_trade_id, type, minimum_damage, created_by) " +
                    "values(?, ?, ?, ?, ?)";
            PreparedStatement PrepStmt = unitOfWork.prepareStatement(statsQuery);
            unitOfWork.registerNew(PrepStmt);
            PrepStmt.setString(1, tradeID);
            PrepStmt.setString(2, cardtoTradeID);
            PrepStmt.setString(3, type);
            PrepStmt.setInt(4, minimumDamage);
            PrepStmt.setInt(5, userID);
            unitOfWork.commitTransaction();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //überprüft ob ein Deal bereits existiert
    public boolean checkIfDealExists(String dealID){
        try {
            String statsQuery = "Select * from \"trades\" where trade_id = ?";
            PreparedStatement prepStmt = unitOfWork.prepareStatement(statsQuery);
            prepStmt.setString(1, dealID);
            ResultSet resultSet = prepStmt.executeQuery();
            if (resultSet.next()){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    // überprüft ob die Karte in dem Deck des Spielers ist
    public boolean checkIfCardIsLockedInUsersDeck(String cardID, int userID){
        try {
            String statsQuery = "Select * from \"decks\" where fk_decks_card_id = ? and fk_decks_user_id = ?";
            PreparedStatement prepStmt = unitOfWork.prepareStatement(statsQuery);
            prepStmt.setString(1, cardID);
            prepStmt.setInt(2, userID);
            ResultSet resultSet = prepStmt.executeQuery();
            if (resultSet.next()){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    // Holt die Karte eines aktiven Trades
    public String getTheCardOfTrade(String tradeID){
        try {
            String statsQuery = "Select * from \"trades\" where trade_id = ?";
            PreparedStatement prepStmt = unitOfWork.prepareStatement(statsQuery);
            prepStmt.setString(1, tradeID);
            ResultSet resultSet = prepStmt.executeQuery();
            if (resultSet.next()){
                String cardID = resultSet.getString("card_to_trade_id");
                return cardID;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // holt die Daten eines Offers und gebt sie zurück
    public Trade getTheDataOfTradeOffer(String tradeID){
        try {
            String statsQuery = "Select * from \"trades\" where trade_id = ?";
            PreparedStatement prepStmt = unitOfWork.prepareStatement(statsQuery);
            prepStmt.setString(1, tradeID);
            ResultSet resultSet = prepStmt.executeQuery();
            if (resultSet.next()){
                String cardID = resultSet.getString("card_to_trade_id");
                String type = resultSet.getString("type");
                int minimumDamage = resultSet.getInt("minimum_damage");
                int created_by = resultSet.getInt("created_by");
                Trade trade = new Trade();
                trade.setId(tradeID);
                trade.setCardToTrade(cardID);
                trade.setCreated_by(created_by);
                trade.setType(type);
                trade.setDamage(minimumDamage);
                return trade;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    // holt dem Besitzer einer bestimmten Karte
    public int getUserIDFromCardID(String cardID, UnitOfWork unitOfWork){
        try {
            String statsQuery = "Select * from \"acquired_cards\" where fk_acquired_cards_card_id = ?";
            PreparedStatement prepStmt = unitOfWork.prepareStatement(statsQuery);
            System.out.println(cardID);
            prepStmt.setString(1, cardID);
            ResultSet resultSet = prepStmt.executeQuery();
            System.out.println(resultSet.getFetchSize());

            if (resultSet.next()){
                int userid = resultSet.getInt("fk_acquired_cards_user_id");
                return userid;
            }else{
                return 3;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return 5;
        }
    }

    // Die funktion schreibt die User ID in der DB um, um einen User eine Karte vom aktuellen Besitzer geben
    public boolean tradeCards(int userID, String cardID, UnitOfWork unitOfWork){
        String query = "update \"acquired_cards\" set fk_acquired_cards_user_id = ? where fk_acquired_cards_card_id = ?";

            try{
                PreparedStatement pstmt = unitOfWork.prepareStatement(query);
                pstmt.setInt(1, userID);
                pstmt.setString(2,cardID);
                pstmt.executeUpdate();
                unitOfWork.registerNew(pstmt);
            } catch (SQLException e) {
                e.printStackTrace();
                unitOfWork.rollbackTransaction();
            }

            return true;

    }

}
