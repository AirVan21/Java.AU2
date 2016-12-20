package ru.spbau.javacourse.torrent.database.enity;

public class User {
    private final String host;
    private final int port;

    public User(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        final String portStr = Integer.toString(port);
        return host + ":"  + portStr;
    }
}
