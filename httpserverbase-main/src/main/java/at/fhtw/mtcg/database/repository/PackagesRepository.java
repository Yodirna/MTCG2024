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


    // Die funktion erstellt die Karten in der DB
    public boolean addPackageCardsToDB(List<Card> cards){
/*        List<Card> cards = cardPackage.toList();*/
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

/*            unitOfWork.commitTransaction();*/
            return true;
        }catch (Exception e){
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            return false;
        }
    }

    // Die Funktion erstellt einen package in der DB
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


    // Die funktion erstellt die Karten in der DB
    public boolean checkIfAnyPackageCardsAlreadyExists(List<Card> cards){
/*        List<Card> cards = cardPackage.toList();*/
        String query = "SELECT * From \"MonsterCards\" where c_uuid = ?";
        try{
            for (Card card : cards){
                PreparedStatement pstmt = unitOfWork.prepareStatement(query);
                pstmt.setString(1, card.getID());
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()){
                    return true;
                }
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }


}
