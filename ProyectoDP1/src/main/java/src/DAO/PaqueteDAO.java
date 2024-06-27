package src.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

import src.model.*;

@Repository
public class PaqueteDAO {

    private static final String URL = "jdbc:mysql://db-redex-1a.c01nmehbjhju.us-east-1.rds.amazonaws.com:3306/Sergio";
    private static final String USER = "admin";
    private static final String PASSWORD = "adaviladp1";

    public void insertPaquetes(List<PaqueteDTO> paquetes) {
        String sql = "INSERT INTO paquete_dto (IdPaquete, codigoIATAOrigen, codigoIATADestino, vuelos) VALUES (?, ?, ?, ?)" +
                     "ON DUPLICATE KEY UPDATE codigoIATAOrigen = VALUES(codigoIATAOrigen), " +
                     "codigoIATADestino = VALUES(codigoIATADestino), vuelos = VALUES(vuelos)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (PaqueteDTO paquete : paquetes) {
                statement.setString(1, paquete.getIdPaquete());
                statement.setString(2, paquete.getCodigoIATAOrigen());
                statement.setString(3, paquete.getCodigoIATADestino());
                statement.setString(4, paquete.getVuelos());
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllPaquetes() {
        String sql = "DELETE FROM paquete_dto";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getVuelosByIdPaquete(String idPaquete) {
        String sql = "SELECT vuelos FROM paquete_dto WHERE IdPaquete = ?";
        String vuelos = null;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, idPaquete);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    vuelos = resultSet.getString("vuelos");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vuelos;
    }
}