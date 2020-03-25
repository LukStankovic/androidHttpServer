package com.stankovic.lukas.httpserver.Controller;

import android.net.Uri;

import com.stankovic.lukas.httpserver.Http.Response.HttpStatusCode;
import com.stankovic.lukas.httpserver.Http.Response.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class CgiController extends BaseController {

    private String uri;

    private String command = "";

    private String arguments = "";

    public CgiController() {
        super();
    }

    public CgiController(Response response, String uri) {
        super();
        this.response = response;
        this.uri = uri;
    }

    @Override
    public void render() throws IOException {
        loadCommand();
        loadArguments();

        if (command.equals("")) {
            response.setHttpStatusCode(HttpStatusCode.NOT_FOUND);
        } else {
            ProcessBuilder processBuilder = new ProcessBuilder();

            if (arguments.equals("")) {
                processBuilder.command(command);
            } else {
                processBuilder.command(command, arguments);
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(processBuilder.start().getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            response.setHttpStatusCode(HttpStatusCode.OK);
            response.setBody(stringBuilder.toString());
        }

        response.returnResponse();

    }

    private void loadArguments() {
        Uri commandUri = Uri.parse(uri);
        commandUri.getPath();
        String[] split = uri.split("%20");
        if (split.length > 1) {
            arguments = split[1];

            if (arguments.substring(arguments.length() - 1).equals("/")) {
                arguments = arguments.substring(0, arguments.length() - 1);
            }
        }
    }

    private void loadCommand() {
        String[] split = uri.split("/");

        if (split.length >= 3) {
            command = split[2].split("%20")[0];
        }
    }

}
