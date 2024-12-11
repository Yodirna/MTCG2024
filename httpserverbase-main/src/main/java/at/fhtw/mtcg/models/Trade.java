package at.fhtw.mtcg.models;


import lombok.Getter;
import lombok.Setter;

public class Trade {
    String id;
    String cardToTrade;
    @Setter @Getter
    String offererCardToTrade;
    String type;
    int damage;
    int created_by;
    User tradeOfferedBy;
    User tradeAcceptedBy;
    public String getId(){
        return this.id;
    }
    public String getCardToTrade(){
        return this.cardToTrade;
    }
    public String getType(){
        return this.type;
    }
    public int getCreated_by(){
        return this.created_by;
    }
    public int getDamage(){
        return this.damage;
    }
    public void setId(String id){
        this.id = id;
    }
    public void setCardToTrade(String cardToTrade){
        this.cardToTrade = cardToTrade;
    }
    public void setType(String type){
        this.type = type;
    }
    public void setCreated_by(int userID){
        this.created_by = userID;
    }
    public void setDamage(int damage){
        this.damage = damage;
    }
}