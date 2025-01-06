package at.fhtw.mtcg.database.repository;

import at.fhtw.mtcg.database.UnitOfWork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Handles transactions related to card packages and coins in the database.
 */
public class TransactionRepository {
    private final UnitOfWork unitOfWork;
    private int packageID;

    public TransactionRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    /**
     * Adds the provided cards to the specified player.
     *
     * @param userID  the ID of the player
     * @param cardIds an array of card IDs to be assigned to the player
     * @return true if successful, false otherwise
     */
    public boolean addCardsToPlayer(int userID, String[] cardIds) {
        // Assign cards to the player
        try {
            Connection conn = unitOfWork.getConnection();
            // Since we know there are exactly 5 cards in a package, we can loop through them
            for (int i = 0; i < cardIds.length; i++) {
                String cardsIntoDeckQuery = "INSERT INTO \"acquired_cards\" " +
                        "(fk_acquired_cards_user_id, fk_acquired_cards_card_id) " +
                        "VALUES (?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(cardsIntoDeckQuery);
                pstmt.setInt(1, userID);
                pstmt.setString(2, cardIds[i]);
                unitOfWork.registerNew(pstmt);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @return the package ID retrieved from the database
     */
    public int getPackageID() {
        return packageID;
    }

    /**
     * Retrieves one package (with its card IDs) from the database.
     *
     * @return an array of card IDs if a package exists; an array of size 1 if no package remains;
     *         an empty array if an error occurs
     */
    public String[] getPackagesFromDB() {
        try {
            // Select the first available package
            String packageSelectQuery = "SELECT * FROM \"packages\" LIMIT 1";
            PreparedStatement selectStmt = unitOfWork.prepareStatement(packageSelectQuery);
            ResultSet resultSet = selectStmt.executeQuery();

            // If there is at least one package
            if (resultSet.next()) {
                this.packageID = resultSet.getInt("package_id");
                String card1_id = resultSet.getString("card_1_id");
                String card2_id = resultSet.getString("card_2_id");
                String card3_id = resultSet.getString("card_3_id");
                String card4_id = resultSet.getString("card_4_id");
                String card5_id = resultSet.getString("card_5_id");
                return new String[]{card1_id, card2_id, card3_id, card4_id, card5_id};
            } else {
                // If there is no package left
                return new String[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    /**
     * Deletes the package with the specified ID from the database.
     *
     * @param packageID the ID of the package to be deleted
     * @return true if successful, false otherwise
     */
    public boolean deletePackage(int packageID) {
        try {
            Connection conn = unitOfWork.getConnection();
            // Delete the package from the database
            String deleteQuery = "DELETE FROM \"packages\" WHERE package_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, packageID);
            unitOfWork.registerNew(deleteStmt);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the current number of coins for the specified user.
     *
     * @param userID the ID of the user
     * @return the current number of coins, or -1 if an error occurred
     */
    public int getUserCoins(int userID) {
        try {
            String selectUserQuery = "SELECT * FROM \"user\" WHERE fk_user_id = ?";
            PreparedStatement selectUserStmt = unitOfWork.prepareStatement(selectUserQuery);
            selectUserStmt.setInt(1, userID);
            ResultSet coinResultSet = selectUserStmt.executeQuery();

            if (coinResultSet.next()) {
                return coinResultSet.getInt("coins");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Reduces the user's coins by 5 after a successful package purchase.
     *
     * @param currentUserCoins the current number of coins the user has
     * @param userID           the ID of the user
     * @return true if successful, false otherwise
     */
    public boolean reallyReduceCoins(int currentUserCoins, int userID) {
        try {
            Connection conn = unitOfWork.getConnection();
            int updatedCoins = currentUserCoins - 5;
            String updateQuery = "UPDATE \"user\" SET coins = ? WHERE fk_user_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, updatedCoins);
            updateStmt.setInt(2, userID);
            unitOfWork.registerNew(updateStmt);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Acquires a package for the specified user if they have enough coins and packages are available.
     *
     * <p>Return legend:
     * <ul>
     *     <li>0 -> Unexpected error</li>
     *     <li>1 -> Successful purchase</li>
     *     <li>2 -> No packages available</li>
     *     <li>3 -> Not enough coins</li>
     * </ul>
     *
     * @param username the name of the user
     * @return an integer representing the result status (0,1,2,3)
     */
    public int acquirePackage(String username) {
        try {
            UserRepository userRepository = new UserRepository(new UnitOfWork());
            int userID = userRepository.getUserID(username);
            String[] cardIds = getPackagesFromDB();

            // If error occurred in fetching packages
            if (cardIds.length == 0) {
                return 0;
            }
            // If no packages remain
            else if (cardIds.length == 1) {
                return 2;
            } else {
                int userCoins = getUserCoins(userID);

                // Could not retrieve coins
                if (userCoins == -1) {
                    return 0;
                }
                // Not enough coins
                else if (userCoins < 5) {
                    return 3;
                }

                int packageID = getPackageID();
                boolean cardsAssigned = addCardsToPlayer(userID, cardIds);
                boolean packageDeleted = deletePackage(packageID);
                boolean coinsReduced = reallyReduceCoins(userCoins, userID);

                // If all operations succeed
                if (cardsAssigned && packageDeleted && coinsReduced) {
                    unitOfWork.commitTransaction();
                    return 1;
                } else {
                    // If any operation fails, roll back
                    unitOfWork.rollbackTransaction();
                    return 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            return 0;
        }
        // No need to explicitly close UnitOfWork since it is AutoCloseable
    }
}
