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
package com.ongres.pgdeploy.pgconfig;

import com.ongres.pgdeploy.pgconfig.properties.DataType;
import com.ongres.pgdeploy.pgconfig.properties.Property;
import com.ongres.pgdeploy.pgconfig.properties.Unit;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by pablo on 3/05/17.
 */
public class MockedParser implements PropertyParser{

  @Override
  public Optional<Property> parse(String property) {
    if (Objects.equals(property, "prop1")) {
      return Optional.of(new Property("prop1", false, DataType.STRING, Unit.byteList));
    }
    if (Objects.equals(property, "prop2")) {
      return Optional.of(new Property("prop2", true, DataType.INTEGER, Unit.byteList));
    }
    if (Objects.equals(property, "prop3")) {
      return Optional.of(new Property("prop3", true, DataType.DOUBLE, Unit.noneList));
    }
    return Optional.empty();
  }
}