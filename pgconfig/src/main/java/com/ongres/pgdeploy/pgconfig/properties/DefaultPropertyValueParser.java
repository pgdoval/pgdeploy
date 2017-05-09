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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by pablo on 9/05/17.
 */
public class DefaultPropertyValueParser implements PropertyValueParser {


  private static final List<PropertyValueParsingUtils<?>> directTransformations = Arrays.asList(
      new PropertyValueParsingUtils<Boolean>(Boolean.class, DataType.BOOLEAN, "Boolean"),
      new PropertyValueParsingUtils<Integer>(Integer.class, DataType.INTEGER, "Integer"),
      new PropertyValueParsingUtils<Long>(Long.class, DataType.INTEGER, "Long"),
      new PropertyValueParsingUtils<Double>(Double.class, DataType.DOUBLE, "Double"),
      new PropertyValueParsingUtils<Float>(Float.class, DataType.DOUBLE, "Float")
  );

  private static final Map<String,Boolean> boolFromString = boolFromString();

  private static final Map<String,Boolean> boolFromString() {
    Map<String, Boolean> result = new HashMap<>(6);

    result.put("true", true);
    result.put("on", true);
    result.put("1", true);
    result.put("false", false);
    result.put("off", false);
    result.put("0", false);

    return result;
  }

  private DefaultPropertyValueParser() {
  }

  public static DefaultPropertyValueParser getInstance() {
    return DefaultPropertyValueParser.SingletonHolder.INSTANCE;
  }



  @Override
  public PropertyValue parse(Object obj, Property property)
      throws WrongTypePropertyException, UnitNotAvailableForPropertyException {
    if (obj == null) {
      throw new IllegalArgumentException("Illegal null argument for PropertyValue.parse");
    }

    //Non String values cannot have units. To express a value with unit, concat them in a String
    if (!(obj instanceof String) && !property.getAvailableUnits().contains(Unit.NONE)) {
      throw UnitNotAvailableForPropertyException.fromValues(
          property.getAvailableUnits(), property.getName(), obj.toString());
    }


    //First, seek for a direct transformation
    Optional<PropertyValueParsingUtils<?>> optionalDirectTransformation =
        directTransformations.stream().filter(it -> it.clazz == obj.getClass()).findFirst();

    if (optionalDirectTransformation.isPresent()) {
      PropertyValueParsingUtils<?> directTransformation = optionalDirectTransformation.get();
      return tryToParse(obj, directTransformation, property);
    }

    //If it comes as a String, the parsing is totally different
    if (obj instanceof String) {
      return parseString((String) obj, property);
    }

    throw WrongTypePropertyException.fromValues(
        property.getType().getClazz(), obj.getClass().getSimpleName(), property.getName(), obj);
  }


  private <T> PropertyValue<T> tryToParse(
      Object obj, PropertyValueParsingUtils utils, Property property)
      throws WrongTypePropertyException {

    if (obj.getClass() == utils.clazz) {

      if (property.getType() == utils.acceptableType) {
        return new PropertyValue<T>((T) obj, Unit.NONE);
      } else {
        throw WrongTypePropertyException.fromValues(
            property.getType().getClazz(), utils.realClassName, property.getName(), obj);
      }
    }

    return null;
  }

  private boolean parseBoolean(String value) {
    Boolean result = boolFromString.get(value);

    if (result != null) {
      return result;
    } else {
      throw new NumberFormatException();
    }
  }

  private PropertyValue parseString(String value, Property property)
      throws UnitNotAvailableForPropertyException, WrongTypePropertyException {

    //The chosen unit is the longest one included at the end of the string
    //This way, if KB and B are acceptable, KB will be chosen for "9KB" and B for "9B"
    Optional<Unit> chosenUnitOptional = property.getAvailableUnits().stream()
        .filter(unit -> value.endsWith(unit.getUnitName()))
        .sorted(Comparator.comparingInt(unit -> -unit.getUnitName().length()))
        .findFirst();

    if (!chosenUnitOptional.isPresent()) {
      throw UnitNotAvailableForPropertyException.fromValues(
          property.getAvailableUnits(), property.getName(), value);
    }

    Unit chosenUnit = chosenUnitOptional.get();

    String realValue = value.substring(0,value.lastIndexOf(chosenUnit.getUnitName())).trim();

    try {

      switch (property.getType()) {
        case DOUBLE:
          return new PropertyValue<>(Double.valueOf(realValue), chosenUnit);
        case INTEGER:
          return new PropertyValue<>(Long.valueOf(realValue), chosenUnit);
        case BOOLEAN:
          return new PropertyValue<>(parseBoolean(realValue), chosenUnit);
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
        throw UnitNotAvailableForPropertyException.fromValues(
            property.getAvailableUnits(), property.getName(), value);
      } else {
        //In this case, the user may have written "9l5TB" instead "9.5TB"
        throw WrongTypePropertyException.fromValues(
            property.getType().getClazz(), "String", property.getName(), realValue);
      }
    }
  }

  private static class SingletonHolder {
    private static final DefaultPropertyValueParser INSTANCE = new DefaultPropertyValueParser();
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
