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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

  private static final List<PropertyValueParsingUtils<?>> directTransformations = Arrays.asList(
      new PropertyValueParsingUtils<Integer>(Integer.class, DataType.INTEGER, "Integer"),
      new PropertyValueParsingUtils<Long>(Long.class, DataType.INTEGER, "Long"),
      new PropertyValueParsingUtils<Double>(Double.class, DataType.DOUBLE, "Double"),
      new PropertyValueParsingUtils<Float>(Float.class, DataType.DOUBLE, "Float")
  );

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

  /** Parses a given object into a {@link PropertyValue} representing it.
   * @param obj The object to be parsed. It cannot be null and has to be compliant to one of these
   *            rules:
   *            <ul>
   *            <li>Be an integer, a long, a float or a double. Depending on the property's
   *            <tt>type</tt>, this will be parsed correctly or throw a
   *            {@link WrongTypePropertyException}
   *            <li>Be a String in one of the following styles:
   *            <ul>
   *              <li> <tt>"12"</tt>: Depending on the property's <tt>type</tt>, this will
   *              return a {@link PropertyValue} containing either 12L, 12D or "12".
   *              <li> <tt>"12MB"</tt>: It will return a {@link PropertyValue} whose value is
   *              12D, 12L or even "12", and whose {@link Unit} will be MB.
   *            </ul>
   *            <li>More examples of valid values are: 1, 1L, 1.2F, 1.2D, "1", "2.3", "value",
   *            "1TB", "2.2TB" or " 2 TB"
   *            </ul>
   * @return An instance of {@link PropertyValue} representing the received object
   * @throws WrongTypePropertyException When the type of the property is different from that of the
   *     value. Example: trying to parse the value 1.3F with  a Property whose <tt>type</tt> is
   *     <tt>INTEGER</tt>.
   * @throws UnitNotAvailableForPropertyException The object specifies a unit that is not contained
   *     in the property's <tt>availableUnits</tt>. This includes trying to parse a value without
   *     a unit from a property whose <tt>availableUnits</tt> don't include
   *     {@link Unit}<tt>.NONE</tt>
   */
  public PropertyValue parse(@Nonnull Object obj)
      throws WrongTypePropertyException, UnitNotAvailableForPropertyException {

    //Necessary for mvn tests to pass
    if (obj == null) {
      throw new IllegalArgumentException("Illegal null argument for PropertyValue.parse");
    }

    //Non String values cannot have units. To express a value with unit, concat them in a String
    if (!(obj instanceof String) && !availableUnits.contains(Unit.NONE)) {
      throw UnitNotAvailableForPropertyException.fromValues(
          availableUnits, name, obj.toString());
    }


    //First, seek for a direct transformation
    Optional<PropertyValueParsingUtils<?>> optionalDirectTransformation =
        directTransformations.stream().filter(it -> it.clazz == obj.getClass()).findFirst();

    if (optionalDirectTransformation.isPresent()) {
      PropertyValueParsingUtils<?> directTransformation = optionalDirectTransformation.get();
      return tryToParse(obj, directTransformation);
    }

    //If it comes as a String, the parsing is totally different
    if (obj instanceof String) {
      return parseString((String) obj);
    }

    throw WrongTypePropertyException.fromValues(
        type.getClazz(), obj.getClass().getSimpleName(), name, obj);
  }


  private <T> PropertyValue<T> tryToParse(Object obj, PropertyValueParsingUtils utils)
      throws WrongTypePropertyException {

    if (obj.getClass() == utils.clazz) {

      if (type == utils.acceptableType) {
        return new PropertyValue<T>((T) obj, Unit.NONE);
      } else {
        throw WrongTypePropertyException.fromValues(
            type.getClazz(), utils.realClassName, name, obj);
      }
    }

    return null;
  }


  private PropertyValue parseString(String value)
      throws UnitNotAvailableForPropertyException, WrongTypePropertyException {

    //The chosen unit is the longest one included at the end of the string
    //This way, if KB and B are acceptable, KB will be chosen for "9KB" and B for "9B"
    Optional<Unit> chosenUnitOptional = availableUnits.stream()
        .filter(unit -> value.endsWith(unit.getUnitName()))
        .sorted(Comparator.comparingInt(unit -> -unit.getUnitName().length()))
        .findFirst();

    if (!chosenUnitOptional.isPresent()) {
      throw UnitNotAvailableForPropertyException.fromValues(availableUnits, name, value);
    }

    Unit chosenUnit = chosenUnitOptional.get();

    String realValue = value.substring(0,value.lastIndexOf(chosenUnit.getUnitName())).trim();

    try {

      switch (type) {
        case DOUBLE:
          return new PropertyValue<>(Double.valueOf(realValue), chosenUnit);
        case INTEGER:
          return new PropertyValue<>(Long.valueOf(realValue), chosenUnit);
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

  private static class PropertyValueParsingUtils<T> {
    Class<T> clazz;
    DataType acceptableType;
    String realClassName;

    private PropertyValueParsingUtils(
        Class<T> clazz, DataType acceptableType, String realClassName) {
      this.clazz = clazz;
      this.acceptableType = acceptableType;
      this.realClassName = realClassName;
    }
  }

}
