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
package com.ongres.pgdeploy.core.router;

import com.ongres.pgdeploy.core.RelativeRoute;
import net.jcip.annotations.Immutable;

import java.nio.file.Path;
import java.util.Arrays;

@Immutable
public class DefaultRouter implements Router {

  protected final RelativeRoute postgresqlConfRoute =
      new RelativeRoute(Arrays.asList("postgresql.conf"));
  protected final RelativeRoute pgHbaConfRoute = new RelativeRoute(Arrays.asList("pg_hba.conf"));
  protected final RelativeRoute initDbRoute = new RelativeRoute(Arrays.asList("bin", "initdb"));
  protected final RelativeRoute pgCtlRoute = new RelativeRoute(Arrays.asList("bin", "pg_ctl"));

  protected DefaultRouter() {
  }

  public static DefaultRouter getInstance() {
    return SingletonHolder.INSTANCE;
  }


  @Override
  public Path routeToPostgresqlConf(Path basePath) {
    return postgresqlConfRoute.asPath(basePath);
  }

  @Override
  public Path routeToPgHbaConf(Path basePath) {
    return pgHbaConfRoute.asPath(basePath);
  }

  @Override
  public Path routeToInitDb(Path basePath) {
    return initDbRoute.asPath(basePath);
  }

  @Override
  public Path routeToPgCtl(Path basePath) {
    return pgCtlRoute.asPath(basePath);
  }


  private static class SingletonHolder {
    private static final DefaultRouter INSTANCE = new DefaultRouter();
  }
}
