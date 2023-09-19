
# EGTS Protocol

Java server that implements communication using the EGTS protocol and a client that simulates communication with the server.


## Tech Stack

- Java 17
- Maven
- Docker

## Install

```bash
  git clone https://github.com/greenblat17/egts-protocol.git
  cd egts-protocol
```

## Docker

Start server

```bash
  docker run greenblat17/egts-server
```

Start server and client

```bash
  docker run --network-alias server --network egts  egts-server
  docker run --network egts egts-client  
```

Docker Compose

```bash
  docker compose up
```
## Usage only Java EGTS Library

Example for decoding packet

```java
public class EgtsServerApplication {
    public static void main(String[] args) {
        var data = new byte[]{1, 0, 3, 11, 0, 3, 84, 0, 0, 74, 21, 56, 0, 51, 84};
        var result = new Package();
        var ans = result.decode(data);
        System.out.println(ans);
    }

}
```

