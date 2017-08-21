/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import java.net.URI;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.web.reactive.function.BodyExtractors;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 *
 * @author hantsy
 */
public class PostRoutes {

    private final PostRepository posts;

    public PostRoutes(PostRepository posts) {
        this.posts = posts;
    }

    public RouterFunction<ServerResponse> routes() {
        return route(GET("/posts"), this::all)
            .andRoute(POST("/posts").and(contentType(APPLICATION_JSON)), this::create)
            .andRoute(GET("/posts/{id}"), this::get);
    }

    private Mono<ServerResponse> all(ServerRequest req) {
        return ServerResponse.ok().body(this.posts.findAll(), Post.class);
    }

    private Mono<ServerResponse> create(ServerRequest req) {
        return req.body(BodyExtractors.toMono(Post.class))
            .flatMap(post -> this.posts.save(post))
            .flatMap(p -> ServerResponse.created(URI.create("/posts/" + p.getId())).build());
    }

    private Mono<ServerResponse> get(ServerRequest req) {
        return this.posts.findById(Long.valueOf(req.pathVariable("id")))
            .flatMap(post -> ServerResponse.ok().body(Mono.just(post), Post.class))
            .switchIfEmpty(ServerResponse.notFound().build());
    }
}
