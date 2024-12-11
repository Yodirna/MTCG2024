package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TransactionRepository {
    private UnitOfWork unitOfWork;
    private int packageID;
    public TransactionRepository(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }



    public boolean addCardsToPlayer(int userID, String[] cardIds){

        //karten dem user gutschreiben
        try {
            Connection conn = unitOfWork.getConnection();
            int cardIdsLength = cardIds.length;
            for (int i = 0; i <= 4; i++) {
                String cardsIntoDeckQuery = "INSERT INTO \"acquired_cards\" (fk_acquired_cards_user_id, fk_acquired_cards_card_id) VALUES (?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(cardsIntoDeckQuery);
                pstmt.setInt(1, userID);
                pstmt.setString(2, cardIds[i]);
                unitOfWork.registerNew(pstmt);
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public int getPackageID(){
        return packageID;
    }
    public String[] getPackagesFromDB(){
        try{
            //package sql
            String packageSelectQuery = "SELECT * FROM \"packages\" LIMIT 1";
            PreparedStatement selectStmt = unitOfWork.prepareStatement(packageSelectQuery);
            ResultSet resultSet = selectStmt.executeQuery();
            //package daten
            if (resultSet.next()) {
                this.packageID = resultSet.getInt("package_id");
                String card1_id = resultSet.getString("card_1_id");
                String card2_id = resultSet.getString("card_2_id");
                String card3_id = resultSet.getString("card_3_id");
                String card4_id = resultSet.getString("card_4_id");
                String card5_id = resultSet.getString("card_5_id");
                String[] cardIds = {card1_id, card2_id, card3_id, card4_id, card5_id};
                return cardIds;
            }else{
                //keine packages mehr
                return new String[1];
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new String[0];
    }



    public boolean delete_package(int packageID){
        try{
            Connection conn = unitOfWork.getConnection();
            //package löschen
            String deleteQuery = "DELETE FROM \"packages\" WHERE package_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, packageID);
            unitOfWork.registerNew(deleteStmt);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public int getUserCoins(int userID){
        try {
            String selectUserQuery = "SELECT * FROM \"user\" where fk_user_id = ?";
            PreparedStatement selectUserStmt = unitOfWork.prepareStatement(selectUserQuery);
            selectUserStmt.setInt(1, userID);
            ResultSet coinResultSet = selectUserStmt.executeQuery();

            if (coinResultSet.next()) {
                int currecnt_coins = coinResultSet.getInt("coins");
                return currecnt_coins;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public boolean reallyReduceCoins(int currecntUserCoins, int userID){
        try{
            Connection conn = unitOfWork.getConnection();
            int should_coins = currecntUserCoins - 5;
            String deleteQuery = "Update \"user\" set coins = ? where fk_user_id = ? ";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, should_coins);
            deleteStmt.setInt(2, userID);
            unitOfWork.registerNew(deleteStmt);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    // return legende: 0 --> unerwarteter Fehler. return 1 --> alles gut. return 2 --> es gibt keine packages
    // return 3 --> user hat zu wenige coins
    public int acquirePackage(String username) {

        try{
            UserRepository userRepository = new UserRepository(new UnitOfWork());
            int userID = userRepository.getUserID(username);
            String[] cardIds = getPackagesFromDB();

            // wenn cards länge 0 --> fehler in der funktion
            if (cardIds.length == 0){
                /*                return "Fehler bei packages from database";*/
                return 0;
                // wenn cards länge 1 --> es gibt keine packages
            }else if (cardIds.length == 1){
                /*                return "Es gibt keine packages mehr";*/
                return 2;
            } else{

                int userCoins = getUserCoins(userID);

                if (userCoins == -1){
                    /*                    return "Couldnt get user Coins from Database!";*/
                    return 0;
                }else if (userCoins < 5){
                    /*                    return "Not enough money for buying a card package";*/
                    return 3;
                }

                int packageID = getPackageID();
                if (addCardsToPlayer(userID, cardIds) &&
                        delete_package(packageID) &&
                        reallyReduceCoins(userCoins, userID)){
                    unitOfWork.commitTransaction();
                    /*                    return "Karten dem Player zugeschrieben";*/
                    return 1;
                }else {
                    unitOfWork.rollbackTransaction();
                    /*                    return "Karten nicht dem Player zugeschrieben";*/
                    return 0;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            return 0;
        }
        // unit of work muss nicht geschlossen werden, weil es auto closeable ist


    }
}
