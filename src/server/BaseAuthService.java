package server;

import java.util.ArrayList;

public class BaseAuthService implements AuthorizationService {
    private static class Entry {
        String login;
        String password;
        String name;

        public Entry(String login, String password, String name) {
            this.login = login;
            this.password = password;
            this.name = name;
        }
    }

    ArrayList <Entry> entries;

    public BaseAuthService() {
        entries = new ArrayList<>();
        entries.add(new Entry("login1", "pass1", "name1"));
        entries.add(new Entry("login2", "pass2", "name2"));
        entries.add(new Entry("login3", "pass3", "name3"));
    }

    @Override
    public String getNameByLoginPass(String login, String pass) {
        for (Entry e:entries) {
            if (e.login.equals(login) && e.password.equals(pass)){
                return e.name;
            }
        }
        return null;
    }
}
