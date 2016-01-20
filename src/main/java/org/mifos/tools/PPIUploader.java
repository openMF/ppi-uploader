/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifos.tools;

import org.mifos.tools.config.PPIUploaderConfiguration;
import org.springframework.boot.SpringApplication;

public class PPIUploader {

    public PPIUploader() {
        super();
    }

    public static void main(String[] args) {
        SpringApplication.run(PPIUploaderConfiguration.class, args);
    }
}
