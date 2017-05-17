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
package com.ongres.pgdeploy.core;

import com.ongres.pgdeploy.core.pgversion.PostgresMajorVersion;

import net.jcip.annotations.Immutable;

@Immutable
public class PostgresInstallationSupplierFeatures {
  private final PostgresMajorVersion major;
  private final int minor;
  private final Platform platform;
  private final String extraVersion;

  public PostgresInstallationSupplierFeatures(
      PostgresMajorVersion major, int minor, Platform platform, String extraVersion) {
    this.major = major;
    this.minor = minor;
    this.platform = platform;
    this.extraVersion = extraVersion;
  }

  public PostgresInstallationSupplierFeatures(
      PostgresMajorVersion major, int minor, Platform platform) {
    this.major = major;
    this.minor = minor;
    this.platform = platform;
    this.extraVersion = null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PostgresInstallationSupplierFeatures that = (PostgresInstallationSupplierFeatures) o;

    if (minor != that.minor) {
      return false;
    }
    if (major != null ? !major.equals(that.major) : that.major != null) {
      return false;
    }
    if (platform != null ? !platform.equals(that.platform) : that.platform != null) {
      return false;
    }
    return extraVersion != null
        ? extraVersion.equals(that.extraVersion) : that.extraVersion == null;
  }

  @Override
  public int hashCode() {
    int result = major != null ? major.hashCode() : 0;
    result = 31 * result + minor;
    result = 31 * result + (platform != null ? platform.hashCode() : 0);
    result = 31 * result + (extraVersion != null ? extraVersion.hashCode() : 0);
    return result;
  }
}
