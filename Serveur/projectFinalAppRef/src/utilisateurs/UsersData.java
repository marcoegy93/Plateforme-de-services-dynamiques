package utilisateurs;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class UsersData {
    private List<User> utilisateurs;

    public UsersData() {
        this.utilisateurs = Collections.synchronizedList(new Vector<>());
    }

    public User connexion(String co) throws Exception {
        String[] lgmdp = co.split("-");
        assert(lgmdp.length == 2);

        User s = null;
        for (User tmp : utilisateurs) {
            if(tmp.getLogin().equals(lgmdp[0]) && tmp.connect(lgmdp[1]))
                s = tmp;
        }

        if(s == null)
            throw new Exception("Mot de passe / Login incorrect");
        return s;
    }

    public boolean userExists(String name) {
        boolean exists = false;
        String[] lgmdp = name.split("-");
        for (User tmp : utilisateurs) {
            if(tmp.getLogin().equals(lgmdp[0]))
                exists = true;
        }
        return exists;
    }

    public User inscription(String readLine) throws MalformedURLException {
        String[] lgmdp = readLine.split("-");
        assert(lgmdp.length == 3);
        User tmp = new User(lgmdp[0],lgmdp[1],lgmdp[2]);
        utilisateurs.add(tmp);

        return tmp;
    }
}
