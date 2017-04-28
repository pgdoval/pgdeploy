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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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


  public Property(String name, boolean needToRestart, DataType type, List<Unit> availableUnits) {
    this.name = name;
    this.needToRestart = needToRestart;
    this.type = type;
    this.availableUnits = availableUnits;
  }

  public PropertyValue parse(Object obj)
      throws WrongTypePropertyException, UnitNotAvailableForPropertyException {

    String realClassName = null;
    if (obj instanceof Integer) {
      //Non String values cannot have units. To express a value with unit, concat them in a String
      if (!availableUnits.contains(Unit.NONE)) {
        throw UnitNotAvailableForPropertyException.fromValues(
            availableUnits, name, obj.toString());
      }

      if (type == DataType.INTEGER) {
        return parseInteger((Integer) obj);
      } else {
        realClassName = "Integer";
      }
    }
    if (obj instanceof Double) {
      //Non String values cannot have units. To express a value with unit, concat them in a String
      if (!availableUnits.contains(Unit.NONE)) {
        throw UnitNotAvailableForPropertyException.fromValues(
            availableUnits, name, obj.toString());
      }

      if (type == DataType.DOUBLE) {
        return parseDouble((Double) obj);
      } else {
        realClassName = "Double";
      }
    }
    if (obj instanceof String) {
      return parseString((String) obj);
    }

    if (realClassName == null) {
      realClassName = obj.getClass().getSimpleName();
    }

    throw WrongTypePropertyException.fromValues(type.getClazz(), realClassName, name, obj);
  }

  private PropertyValue<Integer> parseInteger(Integer value) {
    return parseInteger(value, Unit.NONE);
  }

  private PropertyValue<Integer> parseInteger(Integer value, Unit unit) {
    return new PropertyValue<>(value,unit);
  }

  private PropertyValue<Double> parseDouble(Double value) {
    return parseDouble(value,Unit.NONE);
  }

  private PropertyValue<Double> parseDouble(Double value, Unit unit) {
    return new PropertyValue<>(value,unit);
  }

  private PropertyValue parseString(String value)
      throws UnitNotAvailableForPropertyException, WrongTypePropertyException {
    Optional<Unit> chosenUnitOptional = availableUnits.stream()
        .filter(unit -> value.endsWith(unit.getUnitName()))
        .sorted(Comparator.comparingInt(unit -> -unit.getUnitName().length()))
        .findFirst();

    if (!chosenUnitOptional.isPresent()) {
      throw UnitNotAvailableForPropertyException.fromValues(availableUnits, name, value);
    }

    Unit chosenUnit = chosenUnitOptional.get();

    String realValue = value.substring(0,value.lastIndexOf(chosenUnit.getUnitName()));

    try {

      switch (type) {
        case DOUBLE:
          return parseDouble(Double.valueOf(realValue), chosenUnit);
        case INTEGER:
          return parseInteger(Integer.valueOf(realValue), chosenUnit);
        case STRING:
          return new PropertyValue<>(realValue, chosenUnit);
        default:
          return null; //should never happen
      }
    } catch (NumberFormatException e) {
      if ((chosenUnit == Unit.NONE)
          && Stream.of(Unit.values())
          .filter(unit -> value.endsWith(unit.getUnitName()))
          .count() > 1) {
        //We assume that in this case, there's a unit in the value but the unit isn't allowed
        //for the property, and so chosenUnit is none. We throw a wrong unit exception
        throw UnitNotAvailableForPropertyException.fromValues(availableUnits, name, value);
      } else {
        //In this case, the user may have written "9l5TB" instead "9.5TB"
        throw WrongTypePropertyException.fromValues(type.getClazz(), "String", name, realValue);
      }
    }
  }

}
