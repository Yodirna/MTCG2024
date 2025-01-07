package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg.cards.Monsters.MonsterCard;
import at.fhtw.mtcg.models.Card;
import at.fhtw.mtcg.database.UnitOfWork;
import at.fhtw.mtcg.database.repository.PackagesRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static at.fhtw.mtcg.models.Card.getCardElement;
import static at.fhtw.mtcg.security.Token.validateToken;

/**
 * Handles requests related to creating packages of cards.
 * Only admin users are authorized to create packages.
 */
public class PackageController {

    private final PackagesRepository packagesRepository;

    public PackageController() {
        packagesRepository = new PackagesRepository(new UnitOfWork());
    }

    /**
     * Handles POST requests for creating a package of cards.
     *
     * @param request The HTTP request containing the package creation details.
     * @return A response indicating the success or failure of the operation.
     */
    public Response handlePostRequest(Request request) {
        try {
            // Get the authorization token from the request header
            String token = request.getAuthorizationToken();

            // Validate the token
            if (!validateToken(token)) {
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "Access token is missing or invalid!");
            }

            // Check if the user is an admin
            boolean isAdmin = token.contains("admin");

            if (isAdmin) {
                try {
                    // Parse the request body to extract card details
                    String bodyString = request.getBody();
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode requestBody = mapper.readTree(bodyString);

                    // List to store the cards being added to the package
                    List<Card> cards = new ArrayList<>();

                    // Iterate over the provided card data
                    for (JsonNode node : requestBody) {
                        String cardID = node.get("Id").asText();
                        String cardName = node.get("Name").asText();
                        int cardDamage = node.get("Damage").asInt();
                        Card.Elements cardElement = getCardElement(cardName);

                        // Create a MonsterCard object and add it to the list
                        Card cardToAdd = new MonsterCard(cardName, cardElement, cardDamage, 100);
                        cardToAdd.setId(cardID);
                        cards.add(cardToAdd);
                    }

                    // Validate the number of cards in the package
                    if (cards.size() < 5) {
                        return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "When creating a package, you need to provide at least 5 cards!");
                    }

                    // Check if any card in the package already exists in the database
                    if (packagesRepository.checkIfCardAlreadyExists(cards)) {
                        return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "At least one of the cards already exists in the Database!");
                    }

                    // Add the cards to the database
                    if (!packagesRepository.addPackageCardsToDB(cards)) {
                        return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "Couldn't add the cards to the Database!");
                    }

                    // Add the package to the database
                    if (!packagesRepository.addPackageToDB(cards)) {
                        return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "Couldn't create the package in the Database!");
                    }

                    // Successfully created the package and cards
                    return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Package and cards successfully created!");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "An error occurred while creating the package or cards!");
                }
            } else {
                // User is not an admin
                return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "Provided user is not admin\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "An unexpected error occurred!");
        }
    }
}
