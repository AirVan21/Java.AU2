package ru.spbau.javacourse.torrent.database.enity;

public class User {
    private final String host;
    private final short port;

    public User(String host, short port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public short getPort() {
        return port;
    }

    public String getAddress() {
        final String portStr = Integer.toString(port);
        return host + ":"  + portStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (port != user.port) return false;
        return host.equals(user.host);
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }
}
