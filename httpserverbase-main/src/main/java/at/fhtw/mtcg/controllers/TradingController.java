package at.fhtw.mtcg.controllers;

import at.fhtw.httpserver.httpconfig.ContentType;
import at.fhtw.httpserver.httpconfig.HttpStatus;
import at.fhtw.httpserver.httpconfig.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.mtcg.services.TradingService;

public class TradingController implements RestController {
    private final TradingService tradingService;

    public TradingController() {
        this.tradingService = new TradingService();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST)  {
            if (request.getPathParts().size() > 1){
                return this.tradingService.handlePostRequest(request);
            }else {
                return this.tradingService.createTrade(request);
            }

        }else if (request.getMethod() == Method.GET)  {
            return this.tradingService.getTrades();
        } else if (request.getMethod() == Method.DELETE){
            return this.tradingService.deleteTrade(request);
        }

            return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}