package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.models.DoTrade;
import at.fhtw.mtcg.models.Trade;
import at.fhtw.mtcg.security.Token;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.CardRepository;
import at.fhtw.mtcg.database.repository.TradingRepository;
import at.fhtw.mtcg.database.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Vector;

public class TradingService {
    private TradingRepository tradingRepository;
    public TradingService() {
        tradingRepository = new TradingRepository(new UnitOfWork());
    }

    public Response handlePostRequest(Request request) {
        // Extract the token from the request header for authorization
        String token = request.getAuthorizationToken();


        // Validate the extracted token to ensure it is correct and not expired
        if (token == null || !Token.validateToken(token)) {
            String response = "Invalid Token!";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
        }
        // Retrieve the user’s ID based on the token’s information
        UserRepository userRepository = new UserRepository(new UnitOfWork());


        List<String> parts = request.getPathParts();
        // If we reach this point, it means we are trying to execute a trade
        // The trade ID will be taken from the path

        // Extract the trade ID from the path to identify which trade to execute
        String tradeID = parts.get(1);

        // Check if the specified trade deal exists in the database
        boolean checkIfDealExists = tradingRepository.checkIfDealExists(tradeID);

        // If the trade deal does not exist, respond with an appropriate error
        if (!checkIfDealExists){
            String response = "The provided deal ID was not found!";
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);
        }

        // Extract the card ID from the request body. This is the card offered by the user who accepts the deal
        String acceptorCardID = request.getBody();


        int tradeAcceptorID = -1;

        // Determine the user’s ID based on which username their token contains
        if (token.contains("altenhof")){
            tradeAcceptorID = userRepository.getUserID("altenhof");
        } else if (token.contains("kienboec")) {
            tradeAcceptorID = userRepository.getUserID("kienboec");

        }


        // Execute the trade process with the given trade ID, the offered card ID, and the user’s ID
        DoTrade doTrade = new DoTrade(new UnitOfWork());
        return doTrade.handleTrading(tradeID, acceptorCardID, tradeAcceptorID);

    }

    // This method is responsible for creating a new trade offer
    public Response createTrade(Request request) {
        UserRepository userRepository = new UserRepository(new UnitOfWork());
        try{
            // Extract the token from the request header for authorization
            String token = request.getAuthorizationToken();


            // Validate the token to ensure the user is authorized to create a trade
            if (token == null || !Token.validateToken(token)) {
                String response = "Invalid Token!";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }

            String bodyString = request.getBody();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode requestBody = mapper.readTree(bodyString);

            // Create a Trade object based on the JSON request data
            Trade trade = new Trade();
            trade.setId(requestBody.get("Id").asText());
            trade.setCardToTrade(requestBody.get("CardToTrade").asText());
            trade.setType(requestBody.get("Type").asText());

            // Extract the trade details from the request
            String tradeID = requestBody.get("Id").asText();
            String cardToTradeID = requestBody.get("CardToTrade").asText();
            String type = requestBody.get("Type").asText();
            int minimumDamage = requestBody.get("MinimumDamage").asInt();

            // Retrieve the user ID of the user who is creating this trade offer,
            // identified by the token they provided
            int userID = -1;


            if (token.contains("altenhof")){
                userID = userRepository.getUserID("altenhof");
            } else if (token.contains("kienboec")) {
                userID = userRepository.getUserID("kienboec");

            }
            CardRepository cardRepository = new CardRepository(new UnitOfWork());
            // Retrieve all cards owned by the user to verify that the user actually owns
            // the card they are trying to offer in the trade
            Vector<String> allPlayerCards = cardRepository.getAllAcquiredCardsIds(userID);


            // We perform three checks before creating the trade:
            // 1. Does the user actually own the card they want to trade?
            boolean doesUserOwnCard = false;

            for (String playersCard : allPlayerCards) {
                if (playersCard.equals(cardToTradeID)) {
                    doesUserOwnCard = true;
                    break;
                }
            }
            // 2. Is the card locked in the user’s deck? (If it’s locked, it cannot be traded.)
            boolean isCardLockedInUsersDeck = tradingRepository.checkIfCardIsLockedInUsersDeck(cardToTradeID, userID);

            // 3. Is the trade ID already in use by another trade? (Trade IDs must be unique.)
            boolean isDealIdTaken = tradingRepository.checkIfDealExists(tradeID);

            // If any of these checks fail, respond with the appropriate error messages
            if (isDealIdTaken){
                String response = "A deal with this deal ID already exists!";
                return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);
            }

            if (isCardLockedInUsersDeck){
                String response = "The Card is locked in the user's Deck!";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);

            }

            if (!doesUserOwnCard){
                String response = "User does not own the specified card!";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }

            // If all checks have passed, proceed to create the trade in the database
            boolean isTradeCreated = tradingRepository.createTrade(tradeID, cardToTradeID, type, minimumDamage, userID);

            // Respond accordingly based on whether the trade was successfully created
            if (isTradeCreated){
                String response = "Trading deal successfully created!";
                return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, response);
            }else {
                String response = "Trade deal could not be created!";
                return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, response);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // If an unexpected error occurs, inform the client
        String response = "Unexpected error occurred!";
        return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, response);
    }

    public Response getTrades() {
        // Retrieve all trades from the database
        Vector<String> allTrades = tradingRepository.getAllTrades();
        // If the request was successful but no trades are found, inform the client
        if (allTrades.toString().equals("[]")){
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "No Deals found!");
        }

        // Otherwise, return the list of all trades as a response
        String response = allTrades.toString();
        return new Response(HttpStatus.OK, ContentType.HTML, response);
    }

    public Response deleteTrade(Request request) {

        CardRepository cardRepository = new CardRepository(new UnitOfWork());
        UserRepository userRepository = new UserRepository(new UnitOfWork());
        // Extract parts of the path from the request to find the trade ID
        List<String> pathParts = request.getPathParts();
        // Extract the trade ID from the path
        String tradeID = pathParts.get(1);
        // Retrieve all cards the user owns to confirm that they own the card associated with the trade being deleted
        int userIdFromToken = -1;
        String token = request.getAuthorizationToken();
        if (token.contains("altenhof")){
            userIdFromToken = userRepository.getUserID("altenhof");
        } else if (token.contains("kienboec")) {
            userIdFromToken = userRepository.getUserID("kienboec");

        }
        Vector<String> allPlayerCards = cardRepository.getAllAcquiredCardsIds(userIdFromToken);

        // Two checks before deletion:
        // 1. Does the trade deal exist?
        boolean checkIfdealExists = tradingRepository.checkIfDealExists(tradeID);

        // 2. Does the user own the card that is part of the deal they are trying to delete?
        boolean doesUserOwnCard = false;
        String cardToTradeID = tradingRepository.getTheCardOfTrade(tradeID);
        for (String playersCard : allPlayerCards) {
            if (playersCard.equals(cardToTradeID)) {
                doesUserOwnCard = true;
                break;
            }
        }

        // If any of these checks fail, respond with an appropriate error
        if (!checkIfdealExists){
            String response = "The provided deal ID was not found!";
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, response);

        }

        if (!doesUserOwnCard){
            String response = "The deal contains a card that is not owned by the user!";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);

        }

        // If all checks pass, proceed to delete the trade from the database
        boolean deleteTrade = tradingRepository.deleteTrade(tradeID, new UnitOfWork());
        if (deleteTrade){
            String response = "Trading deal successfully deleted!";
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);

        }else{
            String response = "An unexpected error occurred while deleting the deal!";
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, response);

        }
    }
}
