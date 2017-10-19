package com.pomodonkey.web;


import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import rx.Single;

public class PomodonkeyWeb extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(PomodonkeyWeb.class);

  private static final int DEFAULT_PORT = 8080;
  private static final String CONFIG_HTTP_PORT_SERVER = "http.server.port";

  @Override
  public void start(Future<Void> startFuture) {
    Router router = initializeRouter();
    int portNumber = config().getInteger(CONFIG_HTTP_PORT_SERVER, DEFAULT_PORT);

    initializeHttpServer(startFuture, router, portNumber);
  }

  private void initializeHttpServer(Future<Void> startFuture, Router router, int portNumber) {
    Single<HttpServer> httpServerObs =
        vertx
            .createHttpServer()
            .requestHandler(router::accept)
            .rxListen(portNumber);

    httpServerObs
        .subscribe(
            server -> {
              LOGGER.info("HTTP server running on port " + portNumber);
              startFuture.complete();
            },
            failure -> {
              LOGGER.error("Could not start the HTTP server", failure.getCause());
              startFuture.fail(failure);
            }
        );
  }

  private Router initializeRouter() {
    Router apiRouter = Router.router(vertx);
    // TODO: 10/18/17 add some routes to the api router

    return Router.router(vertx).mountSubRouter("/api", apiRouter);
  }
}
