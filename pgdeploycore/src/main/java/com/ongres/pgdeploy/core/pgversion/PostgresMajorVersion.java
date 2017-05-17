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
package com.ongres.pgdeploy.core.pgversion;

import net.jcip.annotations.Immutable;

import java.util.Optional;

@Immutable
public abstract class PostgresMajorVersion {
  protected final int first;
  protected final int second;

  PostgresMajorVersion(int first, int second) {
    this.first = first;
    this.second = second;
  }

  /** Returns an instance of the class following a received String
   * @param s The string received. It should comply to one of these two patterns:
   *          <ul>
   *          <li>"number": A Post10PostgresMajorVersion with that version number is returned</li>
   *          <li>"n1.n2": A Pre10PostgresMajorVersion with those version numbers is returned</li>
   *          </ul>
   *          For any other type of String, the result will be Optional.empty()
   * @return
   */
  public static Optional<PostgresMajorVersion> fromString(String s) {
    String [] star = s.split("\\.");

    try {
      if (star.length == 1) {
        return Optional.of(new Post10PostgresMajorVersion(Integer.parseInt(star[0])));
      }
      if (star.length == 2) {
        return Optional.of(
            new Pre10PostgresMajorVersion(Integer.parseInt(star[0]), Integer.parseInt(star[1])));
      }
    } catch (NumberFormatException e) {
      return Optional.empty();
    }

    return Optional.empty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PostgresMajorVersion that = (PostgresMajorVersion) o;

    if (first != that.first) {
      return false;
    }
    return second == that.second;
  }

  @Override
  public int hashCode() {
    int result = first;
    result = 31 * result + second;
    return result;
  }
}
