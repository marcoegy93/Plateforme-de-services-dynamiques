package utilisateurs;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class User {
    private URLClassLoader classLoader;
    private String login;
    private String mdp;

    public User(String login, String mdp, String url) throws MalformedURLException {
        this(login,mdp);
        this.classLoader = URLClassLoader.newInstance(new URL[]{new URL((url+"/"))});
    }

    public User(String login, String mdp) {
        this.login = login;
        this.mdp = mdp;
    }

    public URLClassLoader getURLClassLoader() {
        return classLoader;
    }

    public void setURL(String url) throws MalformedURLException{
        this.classLoader = new URLClassLoader(new URL[]{new URL(url)});
    }

    public String getLogin() {
        return login;
    }

    public boolean connect(String mdp) {
        return this.mdp.equals(mdp);
    }

    public void reloadURLClassLoader() {
        URL[] tmp = this.classLoader.getURLs();
        this.classLoader = null;
        System.gc();
        this.classLoader = URLClassLoader.newInstance(tmp);
    }
}
