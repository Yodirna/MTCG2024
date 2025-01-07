package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.models.Card;
import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PackagesRepository {
    private UnitOfWork unitOfWork;

    public PackagesRepository(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }


    // Add cards to the DB
    public boolean addPackageCardsToDB(List<Card> cards){
        String query = "INSERT INTO \"MonsterCards\" (name, damage, element, c_uuid) VALUES (?,?,?,?)";
        try{
            for (Card card : cards){
                PreparedStatement pstmt = unitOfWork.prepareStatement(query);
                pstmt.setString(1, card.getName());
                pstmt.setInt(2, card.getDamage());
                pstmt.setInt(3, card.getElement().ordinal());
                pstmt.setString(4, card.getID());
                unitOfWork.registerNew(pstmt);
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            return false;
        }
    }

    // Add package to the DB
    public boolean addPackageToDB(List<Card> cards){
/*        List<Card> cards = cardPackage.toList();*/
        String query = "INSERT INTO \"packages\" (card_1_id, card_2_id, card_3_id, card_4_id, card_5_id)" +
                " VALUES (?,?,?,?,?)";

        Card card1 = cards.get(0);
        Card card2 = cards.get(1);
        Card card3 = cards.get(2);
        Card card4 = cards.get(3);
        Card card5 = cards.get(4);

        try (PreparedStatement pstmt = unitOfWork.prepareStatement(query)) {
            pstmt.setString(1, card1.getID());
            pstmt.setString(2, card2.getID());
            pstmt.setString(3, card3.getID());
            pstmt.setString(4, card4.getID());
            pstmt.setString(5, card5.getID());
            unitOfWork.registerNew(pstmt);
            unitOfWork.commitTransaction();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            return false;
        }
    }


    // check if any card already exists in the DB
    public boolean checkIfCardAlreadyExists(List<Card> cards) {
        String query = "SELECT * FROM \"MonsterCards\" WHERE c_uuid IN (%s)";
        String placeholders = String.join(",", cards.stream().map(c -> "?").toList());

        try (PreparedStatement pstmt = unitOfWork.prepareStatement(String.format(query, placeholders))) {
            int index = 1;
            for (Card card : cards) {
                pstmt.setString(index++, card.getID());
            }

            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.next(); // True if any card exists
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}
