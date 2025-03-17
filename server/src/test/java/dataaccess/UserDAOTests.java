package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class UserDAOTests {

    private static UserDAO userDAO;

    private void addSomeUsers() throws DataAccessException {
        String[] usernames = {"bernard", "kdot", "gretchen"};
        String[] passwords = {"b1zn1zz", "1overB0y", "ggP0zers"};
        String[] emails = {"ber@aol.com", "trey@yahoo.com", "gma@gmail.com"};

        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {

                for (int i = 0; i < 3; i++) {
                    ps.setString(1, usernames[i]);
                    ps.setString(2, BCrypt.hashpw(passwords[i], BCrypt.gensalt()));
                    ps.setString(3, emails[i]);
                    ps.executeUpdate();
                }

            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
