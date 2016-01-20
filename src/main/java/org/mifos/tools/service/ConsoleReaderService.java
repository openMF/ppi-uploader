/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifos.tools.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.mifos.tools.data.Country;
import org.mifos.tools.rest.data.LookupTable;
import org.mifos.tools.rest.data.Survey;
import org.mifos.tools.util.PPIUploaderConstant;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

@Service
public class ConsoleReaderService  implements CommandLineRunner, ApplicationContextAware {

    private final Environment environment;
    private final Gson gson;
    private final PPIUploaderService ppiUploaderService;
    private final Properties properties;

    private ApplicationContext applicationContext;

    private List<Country> countries;

    @Autowired
    public ConsoleReaderService(final Environment environment,
                                final Gson gson,
                                final PPIUploaderService ppiUploaderService) {
        super();
        this.environment = environment;
        this.gson = gson;
        this.ppiUploaderService = ppiUploaderService;
        this.properties = new Properties();
        this.init();
    }

    @Override
    public void run(String... args) throws Exception {
        final Scanner console = new Scanner(System.in);

        while (true) {
            System.out.print("ppiul> ");
            final String command = console.nextLine();
            if (command.equals("quit")) {
                System.out.println("Bye!");
                break;
            } else if (command.startsWith("show")) {
                if (command.contains("config")) {
                    this.printProperties();
                } else if (command.contains("countries")) {
                    this.printCountries();
                } else {
                    this.unknownCommand();
                }
            } else if (command.startsWith("set")) {
                this.setProperty(command);
            } else if (command.startsWith("upload")) {
                final String[] message = command.split(" ");
                this.ppiUploaderService.upload(this.properties, message[1]);
            } else if (command.startsWith("help")) {
                this.printHelp();
            } else {
                this.unknownCommand();
            }
        }
    }

    private void init() {
        this.properties.setProperty(
                PPIUploaderConstant.PROPERTY_URI,
                this.environment.getProperty(PPIUploaderConstant.PROPERTY_URI));
        this.properties.setProperty(
                PPIUploaderConstant.PROPERTY_TENANT,
                this.environment.getProperty(PPIUploaderConstant.PROPERTY_TENANT));
        this.properties.setProperty(
                PPIUploaderConstant.PROPERTY_TOKEN,
                this.environment.getProperty(PPIUploaderConstant.PROPERTY_TOKEN));
    }

    private void printProperties() {
        final String format = "%-10s%s%n";
        System.out.printf(format, "URI:", this.properties.getProperty(PPIUploaderConstant.PROPERTY_URI));
        System.out.printf(format, "Tenant:", this.properties.getProperty(PPIUploaderConstant.PROPERTY_TENANT));
        System.out.printf(format, "Token:", this.properties.getProperty(PPIUploaderConstant.PROPERTY_TOKEN));
    }

    private void setProperty(final String command) {
        if (command.contains(PPIUploaderConstant.PROPERTY_URI)) {
            final String[] content = command.split(" ");
            this.properties.setProperty(PPIUploaderConstant.PROPERTY_URI, content[2]);
        } else if (command.contains(PPIUploaderConstant.PROPERTY_TENANT)) {
            final String[] content = command.split(" ");
            this.properties.setProperty(PPIUploaderConstant.PROPERTY_TENANT, content[2]);
        } else if (command.contains(PPIUploaderConstant.PROPERTY_TOKEN)) {
            final String[] content = command.split(" ");
            this.properties.setProperty(PPIUploaderConstant.PROPERTY_TOKEN, content[2]);
        } else {
            System.out.println("Unknown property!");
        }
    }

    private void printCountries() {
        if (this.countries == null) {
            this.countries = new ArrayList<>();
            final Resource countriesResource = this.applicationContext.getResource("classpath:template/countries.json");

            try {
                final JsonReader reader = new JsonReader(Files.newBufferedReader(Paths.get(countriesResource.getURI())));
                final List<Country> foundCountries = this.gson.fromJson(reader, new TypeToken<List<Country>>(){}.getType());
                this.countries.addAll(foundCountries);
            } catch (IOException e) {
                System.out.println("Could not load available countries, exit!");
                System.exit(-1);
            }
        }
        for (Country country : this.countries) {
            System.out.println(country.getCountryCode() + " (" + country.getName() + ")");
        }
    }

    private void unknownCommand() {
        System.out.println("Unknown command!");
    }

    private void printHelp() {
        System.out.println("Available commands:");
        final String format = "%-25s%s%n";
        System.out.printf(format, "help", "Displays this help text");
        System.out.printf(format, "quit", "Quit PPI Uploader");
        System.out.printf(format, "show [option]", "Displays the content of option");
        System.out.printf(format, "", "config: the current values for URI, tenant, and token");
        System.out.printf(format, "", "countries: available countries and their code");
        System.out.printf(format, "set [param] [value]", "Sets the value for a specific parameter");
        System.out.printf(format, "", "uri: the endpoint of Mifos X");
        System.out.printf(format, "", "tenant: the tenant within Mifos X");
        System.out.printf(format, "", "token: the token to authorize for a given tenant");
        System.out.printf(format, "upload [code]", "Uploads the survey and lookup tables for the specific country");
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }
}
