package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.objects.Monsters.MonsterCard;
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
            // Get Token from header
            String token = request.getAuthorizationToken();

            boolean allTokenChecks = validateToken(token);
            if (!allTokenChecks){
                String response = "Access token is missing or invalid!";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }

            // extra check if the user is an admin
            boolean isAdmin = token.contains("admin");

            // Create package from an array of cards
            // only admins can create packages
            if (isAdmin){
                // read the body of the request
                String bodyString = request.getBody();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode requestBody = mapper.readTree(bodyString);


                try {
                    // create a vector for cards
                    List<Card> cards = new ArrayList<>();


                    for (JsonNode node : requestBody) {
                        // get the card information from the request body
                        String cardID = node.get("Id").asText();
                        String cardName = node.get("Name").asText();
                        int cardDamage = node.get("Damage").asInt();
                        Card.Elements cardElement = getCardElement(cardName);

                        // creates cards out of the information
                        Card cardToAdd = new MonsterCard(cardName, cardElement, cardDamage, 100);

                        // constructs the card
                        cardToAdd.setId(cardID);
                        cards.add(cardToAdd);
                    }

                    CardPackage packageProvided = new CardPackage(cards);

                    if (cards.size() < 5){
                        String response = "When creating a package, you need to provide at least 5 cards!";
                        return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, response);
                    }

                    // Opens repository, creates the cards in the DB and then the package

                    boolean checkIfAnyCardIsInDB = packagesRepository.checkIfAnyPackageCardsAlreadyExists(cards);
                    if (checkIfAnyCardIsInDB){
                        String response = "At least one of the cards already exists in the Database!";
                        return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);
                    }


                    boolean cardsToDb = packagesRepository.addPackageCardsToDB(cards);
                    if (!cardsToDb){
                        String response = "Couldn't add the cards to the Database!";
                        return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);
                    }

                    boolean packageToDb = packagesRepository.addPackageToDB(cards);
                    if (!packageToDb){
                        String response = "Couldn't create the package in the Database!";
                        return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);
                    }

                    String response = "Package and cards successfully created!";
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, response);
                }catch (Exception e){
                    e.printStackTrace();
                    String response = "An error occurred while creating the package or cards!";
                    return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, response);
                }
            }else{
                String response = "Provided user is not admin\n";
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, response);
            }

        }catch (Exception e){
            e.printStackTrace();
            String response = "An unexpected error occurred!";
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, response);
        }

    }

}
