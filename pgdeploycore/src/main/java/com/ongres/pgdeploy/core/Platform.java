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

import net.jcip.annotations.Immutable;

import java.util.Locale;

@Immutable
public class Platform {
  private final String os;
  private final String architecture;


  /** Both params are transformed to uppercase (using English locale), just in case a user enters
   * linux and Linux in two different moments. They have to be case-independent
   */
  public Platform(String os, String architecture) {
    this.os = os.toLowerCase(Locale.ENGLISH);
    this.architecture = architecture.toLowerCase(Locale.ENGLISH);
  }

  public String toFileString() {
    return os + "_" + architecture;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Platform platform1 = (Platform) o;

    if (this.os != null ? !this.os.equals(platform1.os) : platform1.os != null) {
      return false;
    }
    return architecture != null
        ? architecture.equals(platform1.architecture) : platform1.architecture == null;
  }

  @Override
  public int hashCode() {
    int result = os != null ? os.hashCode() : 0;
    result = 31 * result + (architecture != null ? architecture.hashCode() : 0);
    return result;
  }
}
