package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.models.Card;
import at.fhtw.mtcg.hash.BearerToken;
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
        // Token von header holen
        String token = request.getAuthorizationToken();

        // Token validieren
        if (!BearerToken.validateToken(token)) {
            String response = "Invalid Token";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
        }

        if (token.contains("altenhof")){

            // Holt eine Liste mit Karten vom gegebenen Username
            List<Card> deck = deckRepository.getDeck("altenhof");
            String format = request.getFormat();
            // wenn es keinen deck gibt dann 204 zurück
            if (deck.toString().equals("[]")){
                return new Response(HttpStatus.NO_CONTENT);
            }

            String response;
            // Wenn das Format Plain angegeben wurde --> formatAsPlainText funktion wird aufgerufen
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
            // Holt eine Liste mit Karten vom gegebenen Username
            List<Card> deck = deckRepository.getDeck("kienboec");

            // wenn es keinen deck gibt dann 204 zurück
            if (deck.toString().equals("[]")){
                return new Response(HttpStatus.NO_CONTENT, ContentType.PLAIN_TEXT, "No Deck");
            }

            String response;
            String format = request.getFormat();
            // Wenn das Forma't Plain angegeben wurde --> formatAsPlainText funktion wird aufgerufen
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


    //Funktion um dem Plain Test zu schaffen
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
            // einen String Vector erstellen, um die IDS aus dem Body zu speichern
            Vector<String> cardsToConfigure = new Vector<>();

            // for schleife um die Ids zu iterieren
            if (requestBody.isArray()) {
                for (JsonNode element : requestBody) {
                    if (element.isTextual()) {
                        cardsToConfigure.add(element.asText());
                    }
                }
            }

            // wenn die anzahl der karten nicht 4 ist
            if (cardsToConfigure.size() != 4){
                String response = "The provided deck did not include the required amount of cards";
                return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, response);
            }


            // Token von header holen
            String token = request.getAuthorizationToken();

            // Token validieren
            if (!BearerToken.validateToken(token)) {
                String response = "Invalid Token";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }

            // user manager erstellt um die user id zu holen
            UserRepository userManager = new UserRepository(new UnitOfWork());

            if (token.contains("altenhof")){

                //überprüfen, ob der user alle karten besitzt
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
                // Configure Deck funktion wird aufgerufen
                boolean response = deckRepository.configureDeck("altenhof", cardsToConfigure);
                //je nach Response, etwas zurückschicken
                if (response){

                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Cards have been added to deck");
                }else{
                    return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "Cards have not been added to deck");
                }

            }else if (token.contains("kienboec")){
                //überprüfen, ob der user alle karten besitzt
                int userID = userManager.getUserID("kienboec");
                boolean checkIfUserOwnsAllCards = deckRepository.checkProvidedCardsWithAcquiredCardsInDB(userID,cardsToConfigure);

                if (!checkIfUserOwnsAllCards){
                    String response = "At least one of the provided cards does not belong to the user or is not available.";
                    return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
                }
                // Configure Deck funktion wird aufgerufen
                boolean response = deckRepository.configureDeck("kienboec", cardsToConfigure);
                //je nach Response, etwas zurückschicken
                if (response){
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "cards succesffully added to deck");
                }
                String msg = "Unerwarteter Fehler";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, msg);

            }else{
                String response = "Unerwarteter Fehler";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "Ein unerwarteter fehler ist aufgetreten");

            }
        }catch (Exception e){
            e.printStackTrace();
            String msg = "Unerwarteter Fehler";
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, msg);
        }

    }

}
