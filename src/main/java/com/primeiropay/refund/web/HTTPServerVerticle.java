package com.primeiropay.refund.web;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;

import com.primeiropay.refund.model.RefundRequestModel;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class HTTPServerVerticle extends AbstractVerticle {

    private final Logger LOGGER = LoggerFactory.getLogger(HTTPServerVerticle.class);

    private final Integer serverPort;
    
    public HTTPServerVerticle(final Integer serverPort) {
        this.serverPort = serverPort;
    }
    
    @Override
    public void start(final Future<Void> startFuture) {
    	final HttpServer httpServer = vertx.createHttpServer();

    	final Router router =  Router.router(vertx);
    	router.post().handler(BodyHandler.create());
    	router.post("/refund/:id").handler(this::doRefund);
    	
    	final Integer port = getServerPort();
    	httpServer.requestHandler(router)
    	.listen(port, listenHandler -> {
    		if(listenHandler.failed()) {
    			LOGGER.error("HTTP Server error", listenHandler.cause());
    			return;
    		}
    		LOGGER.info("HTTP Server started on port " + port);
    	});
    }

    private Integer getServerPort() {
        return serverPort;
    }
    
    private String getRefundParam() {
    	return "&paymentType=RF";
    }
    
    private void doRefund(RoutingContext rc) {
        RefundRequestModel refundRequest = rc.getBodyAsJson().mapTo(RefundRequestModel.class);
    	String authId = rc.request().getParam("id");
    	
        JsonObject jsonResponse = new JsonObject();
        try {
        	jsonResponse = refundToPrimeiroPay(refundRequest.toString() + getRefundParam(),
        			authId, rc.request().getHeader("Authorization"));
		} catch (IOException e) {
			LOGGER.error("HTTP Post error", e.getCause());
		}
        
        rc.response()
            .setStatusCode(200)
            .putHeader("content-type", 
              "application/json; charset=utf-8")
            .end(Json.encodePrettily(jsonResponse));
    }
    
    private JsonObject refundToPrimeiroPay(String payload, String authId, String authorizationToken) throws IOException {
    	URL url = new URL("https://test.oppwa.com/v1/payments/"+authId);

    	HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
    	conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", authorizationToken);
    	conn.setDoInput(true);
    	conn.setDoOutput(true);

    	DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
    	wr.writeBytes(payload);
    	wr.flush();
    	wr.close();
    	int responseCode = conn.getResponseCode();
    	InputStream is;

    	JsonObject jsonResponse;
    	
    	if (responseCode >= 400) {
    		is = conn.getErrorStream();
    		jsonResponse = new JsonObject(IOUtils.toString(is));
    	}
    	else {
    		is = conn.getInputStream();
    		jsonResponse = new JsonObject(IOUtils.toString(is));
    		
    		JsonObject jsonSubset = new JsonObject();
            jsonSubset.put("id", jsonResponse.getString("id"));
            jsonSubset.put("result", jsonResponse.getJsonObject("result"));
            
            jsonResponse = jsonSubset;
    	}
    	
    	return jsonResponse;
    }
}
