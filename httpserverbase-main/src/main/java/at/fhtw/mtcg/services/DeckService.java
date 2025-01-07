package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.models.Card;
import at.fhtw.mtcg.security.Token;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.DeckRepository;
import at.fhtw.mtcg.database.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Vector;

public class DeckService {
    private final DeckRepository deckRepository;
    public DeckService() {
        deckRepository = new DeckRepository(new UnitOfWork());
    }


    public Response handleGetReq(Request request) {
        // Get the token from the header
        String token = request.getAuthorizationToken();

        // Validate the token
        if (!Token.validateToken(token)) {
            String response = "Invalid Token";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
        }

        if (token.contains("altenhof")){

            // gets a list of cards from the given username
            List<Card> deck = deckRepository.getDeck("altenhof");
            String format = request.getFormat();
            // no deck found, return 204
            if (deck.toString().equals("[]")){
                return new Response(HttpStatus.NO_CONTENT);
            }

            String response;
            // if format plain is given, call formatAsPlainText function
            if (format == null){
                response = deckRepository.createJsonForAllCards(deck).toString();
                return new Response(HttpStatus.OK, ContentType.JSON, response);
            }
            else if (format.equals("plain")) {
                response = formatAsPlainText(deck);
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);
            } else {
                response = deckRepository.createJsonForAllCards(deck).toString();
                return new Response(HttpStatus.OK, ContentType.JSON, response);

            }


        }else if (token.contains("kienboec")){
            // gets a list of cards from the given username
            List<Card> deck = deckRepository.getDeck("kienboec");

            // no deck found, return 204
            if (deck.toString().equals("[]")){
                return new Response(HttpStatus.NO_CONTENT, ContentType.PLAIN_TEXT, "No Deck");
            }

            String response;
            String format = request.getFormat();
            // if format plain is given, call formatAsPlainText function
            if ("plain".equals(format)) {
                response = formatAsPlainText(deck);
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);
            } else {
                response = deckRepository.createJsonForAllCards(deck).toString();
                return new Response(HttpStatus.NO_CONTENT, ContentType.JSON, response);

            }


        }else{
            String response = "Unauthroized Token ";
            return new Response(HttpStatus.NO_CONTENT, ContentType.PLAIN_TEXT, response);
        }
    }


    // function to format the deck as plain text
    private String formatAsPlainText(List<Card> deck) {
        StringBuilder response = new StringBuilder();
        for (Card card : deck) {
            response.append("ID: ").append(card.getID()).append(", ");
            response.append("Name: ").append(card.getName()).append(", ");
            response.append("Damage: ").append(card.getDamage()).append(", ");
            response.append("Element: ").append(card.getElement()).append(", ");
            response.append("\n");
        }
        return response.toString();
    }

    public Response handlePutReq(Request request) {
        try {
            String bodyString = request.getBody();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode requestBody = mapper.readTree(bodyString);
            // save ids from body in vector
            Vector<String> cardsToConfigure = new Vector<>();

            // loop to iterate ids
            if (requestBody.isArray()) {
                for (JsonNode element : requestBody) {
                    if (element.isTextual()) {
                        cardsToConfigure.add(element.asText());
                    }
                }
            }

            // if the size of the cards to configure is not 4, return 400
            if (cardsToConfigure.size() != 4){
                String response = "The provided deck did not include the required amount of cards";
                return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, response);
            }


            // get the token from the header
            String token = request.getAuthorizationToken();

            // validate the token
            if (!Token.validateToken(token)) {
                String response = "Invalid Token";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }

            // created usermanager to get the user id
            UserRepository userManager = new UserRepository(new UnitOfWork());

            if (token.contains("altenhof")){

                // check if the user owns all the cards
                int userID = userManager.getUserID("altenhof");
                boolean checkIfUserOwnsAllCards = deckRepository.checkProvidedCardsWithAcquiredCardsInDB(userID,cardsToConfigure);

                if (!checkIfUserOwnsAllCards){
                    String response = "At least one of the provided cards does not belong to the user or is not available.";
                    return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
                }

                List<String> userDeckIds = deckRepository.getDeckCardsIds(userID);
                for (String deckCardId : userDeckIds){
                    for (String cardToConfigureId : cardsToConfigure){
                        if (deckCardId.equals(cardToConfigureId)){
                            String response = "At least one of the provided cards are already in Deck!";
                            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
                        }
                    }
                }
                // configure deck function is called
                boolean response = deckRepository.configureDeck("altenhof", cardsToConfigure);
                // send back a response based on the response value
                if (response){

                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Cards have been added to deck");
                }else{
                    return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "Cards have not been added to deck");
                }

            }else if (token.contains("kienboec")){
                // check if the user owns all the cards
                int userID = userManager.getUserID("kienboec");
                boolean checkIfUserOwnsAllCards = deckRepository.checkProvidedCardsWithAcquiredCardsInDB(userID,cardsToConfigure);

                if (!checkIfUserOwnsAllCards){
                    String response = "At least one of the provided cards does not belong to the user or is not available.";
                    return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
                }
                // configure deck function is called
                boolean response = deckRepository.configureDeck("kienboec", cardsToConfigure);
                // send back a response based on the response value
                if (response){
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Cards succesffully added to deck");
                }
                String msg = "Unexpected Error";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, msg);

            }else{
                String response = "Unexpected Error";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "An unexpected error occurred");

            }
        }catch (Exception e){
            e.printStackTrace();
            String msg = "Unexpected Error";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, msg);
        }

    }

}
