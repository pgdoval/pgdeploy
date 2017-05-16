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
package com.ongres.pgdeploy.pgconfig.properties;

import com.ongres.pgdeploy.pgconfig.properties.exceptions.UnitNotAvailableForPropertyException;
import com.ongres.pgdeploy.pgconfig.properties.exceptions.WrongTypePropertyException;
import net.jcip.annotations.Immutable;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

/**
 * Created by pablo on 27/04/17.
 */
@Immutable
public class Property {

  private final String name;
  private final boolean needToRestart;
  private final DataType type;
  private final List<Unit> availableUnits;


  public String getName() {
    return name;
  }

  public boolean isNeedToRestart() {
    return needToRestart;
  }

  public DataType getType() {
    return type;
  }

  public List<Unit> getAvailableUnits() {
    return availableUnits;
  }

  public Property(String name, boolean needToRestart, DataType type, List<Unit> availableUnits) {
    this.name = name;
    this.needToRestart = needToRestart;
    this.type = type;
    this.availableUnits = availableUnits;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Property property = (Property) o;

    if (needToRestart != property.needToRestart) {
      return false;
    }
    if (!name.equals(property.name)) {
      return false;
    }
    if (type != property.type) {
      return false;
    }
    return availableUnits.equals(property.availableUnits);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + (needToRestart ? 1 : 0);
    result = 31 * result + type.hashCode();
    result = 31 * result + availableUnits.hashCode();
    return result;
  }

  /** Validates that the received {@link PropertyValue} is acceptable by the property.
   * @param value The {@link PropertyValue} to be parsed. It cannot be null.
   * @throws WrongTypePropertyException When the type of the property is different from that of the
   *     value. Example: trying to validate the value 1.3F with  a Property whose <tt>type</tt> is
   *     <tt>INTEGER</tt>.
   * @throws UnitNotAvailableForPropertyException The object specifies a unit that is not contained
   *     in the property's <tt>availableUnits</tt>. This includes trying to validate a value without
   *     a unit from a property whose <tt>availableUnits</tt> don't include
   *     {@link Unit}<tt>.NONE</tt>
   */
  public void validate(PropertyValue value) 
      throws WrongTypePropertyException, UnitNotAvailableForPropertyException {
    if (value == null) {
      throw new IllegalArgumentException("Illegal null argument for PropertyValue.validate");
    }

    if (!availableUnits.contains(value.getUnit())) {
      throw UnitNotAvailableForPropertyException.fromValues(
          availableUnits, name, value.getValue().toString());
    }

    if (!(value.getValue().getClass().isAssignableFrom(type.getClazz()))) {
      throw WrongTypePropertyException.fromValues(
          type.getClazz(), value.getValue().getClass().toString(),
          name, value.getValue().toString());
    }
  }
}
