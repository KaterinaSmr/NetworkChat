package server;

public interface AuthorizationService {

    String getNameByLoginPass(String login, String pass);
}
