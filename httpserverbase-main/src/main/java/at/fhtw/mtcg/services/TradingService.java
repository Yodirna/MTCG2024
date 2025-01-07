package at.fhtw.mtcg.services;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.controllers.TradingController;

public class TradingService implements RestController {
    private final TradingController tradingController;

    public TradingService() {
        this.tradingController = new TradingController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST)  {
            if (request.getPathParts().size() > 1){
                return this.tradingController.handlePostRequest(request);
            }else {
                return this.tradingController.createTrade(request);
            }

        }else if (request.getMethod() == Method.GET)  {
            return this.tradingController.getTrades();
        } else if (request.getMethod() == Method.DELETE){
            return this.tradingController.deleteTrade(request);
        }

            return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}