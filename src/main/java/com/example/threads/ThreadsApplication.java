package com.example.threads;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.function.*;

import java.util.Map;

@SpringBootApplication
public class ThreadsApplication {

	@Bean("restClient")
	RestClient restClient(RestClient.Builder  builder, @Value("${httpbin.url}") String url){
		return builder.
				baseUrl(url).
				build();
	}

	@Bean
	RouterFunction<ServerResponse> httpEndpoints(RestClient restClient){
		var log = LoggerFactory.getLogger(getClass());
		return RouterFunctions.route().
				GET("/{seconds}", new HandlerFunction<ServerResponse>() {
					@Override
					public ServerResponse handle(ServerRequest request) throws Exception {
						var seconds = request.pathVariable("seconds");
						var reqToHttpbin =  restClient.get()
								.uri("/delay/" + seconds)
								.retrieve()
								.toEntity(String.class);
						log.info( "{} on  {}" ,reqToHttpbin.getStatusCode() , Thread.currentThread());
						return ServerResponse.ok().body(Map.of("done", true));
					}
				}).
				build();
	}

	public static void main(String[] args) {
		SpringApplication.run(ThreadsApplication.class, args);
	}

}
