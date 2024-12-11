package at.fhtw.mtcg.models;


import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.TradingRepository;

public class DoTrade {
    private UnitOfWork unitOfWork;
    public DoTrade(UnitOfWork unitOfWork){
        this.unitOfWork = unitOfWork;
    }

    public Response handleTradingWithId(String tradeID, String tradeAcceptorCardID, int tradeAcceptorID) {

        try{
            // erstellt diverse Manager
            TradingRepository tradingRepository = new TradingRepository(new UnitOfWork());


            //erstellt einen Trade model, mit den Daten des trades
            Trade trade = tradingRepository.getTheDataOfTradeOffer(tradeID);

            // leserliche Variablen
            int tradeOffererUserID = trade.getCreated_by();
            String tradeOffererCardID = trade.getCardToTrade();


            boolean isUserTradingWithHimself = tradeAcceptorID == tradeOffererUserID;

            //wenn der User versucht mit sich selbst zu traden --> Error
            if (isUserTradingWithHimself){
                String response = "Trading with self is not allowed.";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }

            // check ob die Karte vom Acceptor in seinem Deck locked ist
            boolean isAcceptorCardLockedInDeck = tradingRepository.checkIfCardIsLockedInUsersDeck(tradeOffererCardID, tradeAcceptorID);
            if (isAcceptorCardLockedInDeck){
                String response = "Trade Acceptor Card is locked in Deck";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);

            }


            tradingRepository.tradeCards(tradeOffererUserID, tradeAcceptorCardID, unitOfWork);
            tradingRepository.tradeCards(tradeAcceptorID, tradeOffererCardID, unitOfWork);
            tradingRepository.deleteTrade(tradeID, unitOfWork);
            unitOfWork.commitTransaction();

            String response = "trade succesfully done!";
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);


        }catch (Exception e){
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
        }
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Something went wrong");
    }
}
