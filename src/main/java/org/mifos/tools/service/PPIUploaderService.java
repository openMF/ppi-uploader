/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifos.tools.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.mifos.tools.provider.RestAdapterProvider;
import org.mifos.tools.rest.MifosRestResource;
import org.mifos.tools.rest.data.LookupTable;
import org.mifos.tools.rest.data.Survey;
import org.mifos.tools.util.PPIUploaderConstant;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

@Service
public class PPIUploaderService implements ApplicationContextAware {

    private final Logger logger;
    private final Gson gson;
    private final RestAdapterProvider restAdapterProvider;

    private ApplicationContext applicationContext;

    @Autowired
    public PPIUploaderService(final Logger logger,
                              final Gson gson,
                              final RestAdapterProvider restAdapterProvider) {
        super();
        this.logger = logger;
        this.gson = gson;
        this.restAdapterProvider = restAdapterProvider;
    }

    public void upload(final Properties properties, final String countryCode) {
        final MifosRestResource restResource = this.restAdapterProvider.get(properties).create(MifosRestResource.class);

        if (this.uploadSurvey(properties, restResource, countryCode)) {
            final List<Survey> surveys = restResource.getSurveys(
                    this.authToken(properties),
                    properties.getProperty("tenant"),
                    PPIUploaderConstant.CONTENT_TYPE
            );

            for (Survey survey : surveys) {
                if (survey.getCountryCode().equalsIgnoreCase(countryCode)) {
                    this.uploadLookupTable(properties, restResource, countryCode, survey.getId());
                    break;
                }
            }
        }
    }

    private boolean uploadSurvey(final Properties properties, final MifosRestResource restResource, final String countryCode) {
        final Resource surveyResource = this.applicationContext.getResource("classpath:template/" + countryCode.toUpperCase() + "/survey.json");

        if (surveyResource.exists()) {
            try {
                final JsonReader reader = new JsonReader(Files.newBufferedReader(Paths.get(surveyResource.getURI())));
                final Survey survey = this.gson.fromJson(reader, Survey.class);
                restResource.createSurvey(
                        this.authToken(properties),
                        properties.getProperty("tenant"),
                        PPIUploaderConstant.CONTENT_TYPE,
                        survey
                );
                return true;
            } catch (Throwable th) {
                this.logger.error("Error while uploading survey!", th);
                System.out.println("Could not upload survey! See logfile for more information.");
            }
        } else {
            System.out.println("Unknown country code " + countryCode + "!");
        }
        return false;
    }

    private void uploadLookupTable(final Properties properties, final MifosRestResource restResource, final String countryCode, final Long surveyId) {
        final Resource lookupTableResource = this.applicationContext.getResource("classpath:template/" + countryCode.toUpperCase() + "/lookuptable.json");

        if (lookupTableResource.exists()) {
            try {
                final JsonReader reader = new JsonReader(Files.newBufferedReader(Paths.get(lookupTableResource.getURI())));
                final List<LookupTable> lookupTables = this.gson.fromJson(reader, new TypeToken<List<LookupTable>>(){}.getType());
                if (lookupTables != null && !lookupTables.isEmpty()) {
                    for (final LookupTable lookupTable : lookupTables) {
                        restResource.createLookupTable(
                                this.authToken(properties),
                                properties.getProperty("tenant"),
                                PPIUploaderConstant.CONTENT_TYPE,
                                surveyId,
                                lookupTable
                        );
                    }
                }
            } catch (Throwable th) {
                this.logger.error("Error while uploading survey!", th);
                System.out.println("Could not upload survey! See logfile for more information.");
            }
        } else {
            System.out.println("Unknown country code " + countryCode + "!");
        }
    }

    private String authToken(final Properties properties) {
        return "Basic " + properties.getProperty("token");
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }
}
