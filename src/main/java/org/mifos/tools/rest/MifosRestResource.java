/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifos.tools.rest;

import org.mifos.tools.rest.data.LookupTable;
import org.mifos.tools.rest.data.Survey;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.List;

public interface MifosRestResource {

    @POST("/surveys")
    Response createSurvey(@Header("Authorization") final String authorization,
                          @Header("Fineract-Platform-TenantId") final String tenantIdentifier,
                          @Header("Content-Type") final String contentType,
                          @Body final Survey content);

    @GET("/surveys")
    List<Survey> getSurveys(@Header("Authorization") final String authorization,
                            @Header("Fineract-Platform-TenantId") final String tenantIdentifier,
                            @Header("Accept") final String contentType);

    @POST("/surveys/{id}/lookuptables")
    Response createLookupTable(@Header("Authorization") final String authorization,
                             @Header("Fineract-Platform-TenantId") final String tenantIdentifier,
                             @Header("Content-Type") final String contentType,
                             @Path("id") final Long surveyId,
                             @Body final LookupTable content);
}
