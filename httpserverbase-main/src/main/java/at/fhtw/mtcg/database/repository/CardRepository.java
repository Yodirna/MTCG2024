package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.models.Card;
import at.fhtw.mtcg.database.UnitOfWork;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CardRepository {
    private UnitOfWork  unitOfWork;
    public CardRepository(UnitOfWork unitOfWork){
        this.unitOfWork = unitOfWork;
    }


    public List<String> getAllCards(String username)  {
        try {

            // Get the User ID
            UserRepository userRepository = new UserRepository(new UnitOfWork());
            int userID = userRepository.getUserID(username);

            // Get all the Card IDs of the user
            Vector<String> allCardIds = getAllAcquiredCardsIds(userID);
            int len = allCardIds.size();

            // Get the data of all the cards
            Vector<Card> allCards = new Vector<Card>();

            for (int i = 0; i < len; i++) {
                // For each card ID, get the card data
                Card card = getCardData(allCardIds.get(i));
                // Cards added to the list
                allCards.add(card);

            }
            // Create a JSON response for all the cards
            List<String> jsonList = createJsonForAllCards(allCards);
            return jsonList;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // A function to create a JSON response for all the cards
    public  List<String>  createJsonForAllCards(Vector<Card> allCards ){
        ObjectMapper mapper = new ObjectMapper();
        try {
            int len = allCards.size();
            List<String> jsonList=new ArrayList<String>();
            for (int i = 0; i < len; i++) {
                String json = mapper.writeValueAsString(allCards.get(i));
                jsonList.add(json + "\n");
            }
            return jsonList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // Get the card data from the database
    public Card getCardData(String cardID){
        try{
            String getCardsQuery = "Select * from  \"MonsterCards\" where c_uuid = ?";
            PreparedStatement getCardsStmt = unitOfWork.prepareStatement(getCardsQuery);
            getCardsStmt.setString(1, cardID);
            ResultSet ResultSet = getCardsStmt.executeQuery();

            if(ResultSet.next()){
                String cardName = ResultSet.getString("name");
                int cardDamage = ResultSet.getInt("damage");
                int cardElement= ResultSet.getInt("element");
                Card card = new Card(cardName, Card.Elements.values()[cardElement], cardDamage, 100);
                card.setId(cardID);
                return card;
            }
        }catch (Exception e){
            e.printStackTrace();

        }
        return new Card("0", Card.Elements.Fire,0,0);
    }

    // Get all the card IDs of the user
    public Vector<String> getAllAcquiredCardsIds(int userID ){
        try{
            String getCardsQuery = "Select * from acquired_cards where fk_acquired_cards_user_id = ?";
            PreparedStatement getCardsStmt = unitOfWork.prepareStatement(getCardsQuery);
            getCardsStmt.setInt(1, userID);
            ResultSet ResultSet = getCardsStmt.executeQuery();
            //speichert die Card IDS in einem String vektor
            Vector<String> cardIDs = new Vector<>();

            while(ResultSet.next()){
                String currentCardID = ResultSet.getString("fk_acquired_cards_card_id");
                cardIDs.add(currentCardID);
            }
            return cardIDs;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
