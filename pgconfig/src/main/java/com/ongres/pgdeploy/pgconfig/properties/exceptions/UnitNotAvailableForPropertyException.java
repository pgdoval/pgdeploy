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
package com.ongres.pgdeploy.pgconfig.properties.exceptions;

import com.ongres.pgdeploy.pgconfig.properties.Unit;

import java.util.List;
import java.util.stream.Collectors;


public class UnitNotAvailableForPropertyException extends Exception {

  public UnitNotAvailableForPropertyException(String s) {
    super(s);
  }

  public static UnitNotAvailableForPropertyException fromValues(
      List<Unit> availableUnits, String propertyName, String propertyValue) {

    StringBuilder sb = new StringBuilder();

    if ((availableUnits.contains(Unit.NONE) && availableUnits.size() == 1)
            || availableUnits.isEmpty()) {

      sb.append("Expected no unit for value ");
      sb.append(propertyValue);
      sb.append(" for property ");
      sb.append(propertyName);

    } else {

      String collected = availableUnits.stream()
          .map(Enum::name)
          .collect(Collectors.joining(", "));

      sb.append("Expected that the unit of the value ");
      sb.append(propertyValue);
      sb.append(" for property ");
      sb.append(propertyName);
      sb.append(" would be one of these: ");
      sb.append(collected);
    }

    return new UnitNotAvailableForPropertyException(sb.toString());
  }
}
