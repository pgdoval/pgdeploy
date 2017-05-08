/*
 * Copyright 2017, OnGres.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ongres.pgdeploy.clusters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pablo on 8/05/17.
 */
public class PostgresClusterCreationOptions {

  private static final String encodingFlag = "-E";
  private static final String localeFlag = "--locale";
  private static final String superUserFlag = "-U";
  private static final String dataChecksumsFlag = "-k";

  private String encoding;
  private String locale;
  private String superUser;
  private boolean dataChecksums;

  private PostgresClusterCreationOptions(
      String encoding, String locale, String superUser, boolean dataChecksums) {
    this.encoding = encoding;
    this.locale = locale;
    this.superUser = superUser;
    this.dataChecksums = dataChecksums;
  }

  public List<String> toArgumentList() {
    List<String> result = new ArrayList<>();

    result = addIfNotNull(result, encoding, encodingFlag);
    result = addIfNotNull(result, locale, localeFlag);
    result = addIfNotNull(result, superUser, superUserFlag);
    result = addIfTrue(result, dataChecksums, dataChecksumsFlag);

    return result;
  }

  private List<String> addIfNotNull(List<String> args, String value, String flag) {
    if (value != null) {
      args.add(flag);
      args.add(value);
    }
    return args;
  }

  private List<String> addIfTrue(List<String> args, boolean value, String flag) {
    if (value) {
      args.add(flag);
    }
    return args;
  }

  public static EncodingPreBuilder fromDefault() {
    return new EncodingPreBuilder();
  }


  public static class EncodingPreBuilder {

    public LocalePreBuilder defaultEncoding() {
      return withEncoding(null);
    }

    public LocalePreBuilder withEncoding(String encoding) {
      return new LocalePreBuilder(encoding);
    }

  }

  public static class LocalePreBuilder {

    private final String encoding;

    public LocalePreBuilder(String encoding) {
      this.encoding = encoding;
    }

    public SuperUserPreBuilder defaultLocale() {
      return withLocale(null);
    }

    public SuperUserPreBuilder withLocale(String locale) {
      return new SuperUserPreBuilder(encoding, locale);
    }
  }


  public static class SuperUserPreBuilder {

    private final String encoding;
    private final String locale;

    public SuperUserPreBuilder(String encoding, String locale) {
      this.encoding = encoding;
      this.locale = locale;
    }

    public DataChecksumsPreBuilder defaultSuperUser() {
      return withSuperUser(null);
    }

    public DataChecksumsPreBuilder withSuperUser(String superUser) {
      return new DataChecksumsPreBuilder(encoding, locale, superUser);
    }
  }

  public static class DataChecksumsPreBuilder {

    private final String encoding;
    private final String locale;
    private final String superUser;

    public DataChecksumsPreBuilder(String encoding, String locale, String superUser) {
      this.encoding = encoding;
      this.locale = locale;
      this.superUser = superUser;
    }

    public PostgresClusterCreationOptions withDataChecksums() {
      return new PostgresClusterCreationOptions(encoding, locale, superUser, true);
    }

    public PostgresClusterCreationOptions withoutDataChecksums() {
      return new PostgresClusterCreationOptions(encoding, locale, superUser, false);
    }

  }


}
