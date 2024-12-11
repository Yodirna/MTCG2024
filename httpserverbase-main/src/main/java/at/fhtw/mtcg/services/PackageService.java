package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.classes.MonsterCards.MonsterCard;
import at.fhtw.mtcg.models.Card;
import at.fhtw.mtcg.models.CardPackage;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.PackagesRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;


import static at.fhtw.mtcg.models.Card.getCardElement;
import static at.fhtw.mtcg.hash.BearerToken.validateToken;

public class PackageService {

    private PackagesRepository packagesRepository;
    public PackageService() {

        packagesRepository = new PackagesRepository(new UnitOfWork());
    }
    public Response handlePostRequest(Request request) {

        try {
            //token holen
            String token = request.getAuthorizationToken();

            boolean allTokenChecks = validateToken(token);
            if (!allTokenChecks){
                String response = "Access token is missing or invalid";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }

            // isAdmin vom token extrahieren
            boolean isAdmin = token.contains("admin");

            // nur wenn der Benutzer ein admin ist, dann kann man einen package erstellen
            if (isAdmin){
                // body lesen
                String bodyString = request.getBody();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode requestBody = mapper.readTree(bodyString);


                try {
                    // erstellt einen Liste von cards
                    List<Card> cards = new ArrayList<>();

                    for (JsonNode node : requestBody) {
                        //holt die daten der Karten
                        String cardID = node.get("Id").asText();
                        String cardName = node.get("Name").asText();
                        int cardDamage = node.get("Damage").asInt();
                        Card.Elements cardElement = getCardElement(cardName);

                        // erstellt aus dem request body karten und fügt sie im vector ein
                        Card cardToAdd = new MonsterCard(cardName, cardElement, cardDamage, 100);

                        // das unten ist extra wegen constructor
                        cardToAdd.setId(cardID);
                        cards.add(cardToAdd);
                    }
                    // erstelle einen package model
                    CardPackage packageProvided = new CardPackage(cards);

                    if (cards.size() < 5){
                        String response = "Beim Package erstellen müssen 5 karten gesetzt sein!";
                        return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, response);
                    }


                    // macht repo auf, und erstellt die Karten in der DB und dann anschließend das Package
                    boolean checkIfAnyCardIsInDB = packagesRepository.checkIfAnyPackageCardsAlreadyExists(cards);
                    if (checkIfAnyCardIsInDB){
                        String response = "At Least One of the provided Cards are already in Database";
                        return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);
                    }


                    boolean cardsToDb = packagesRepository.addPackageCardsToDB(cards);
                    if (!cardsToDb){
                        String response = "Couldnt add the cards to the Database";
                        return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);
                    }

                    boolean packageToDb = packagesRepository.addPackageToDB(cards);
                    if (!packageToDb){
                        String response = "Couldnt create the Package in the Database";
                        return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);
                    }

                    String response = "Package and cards successfully created";
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);
                }catch (Exception e){
                    e.printStackTrace();
                    String response = "Ein fehler ist beim erstellen der Karten oder package ist unterlaufen!";
                    return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);
                }
            }else{
                String response = "Provided user is not admin\n";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }

        }catch (Exception e){
            e.printStackTrace();
            String response = "Ein unerwarteter Fehler!";
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, response);
        }

    }

}
