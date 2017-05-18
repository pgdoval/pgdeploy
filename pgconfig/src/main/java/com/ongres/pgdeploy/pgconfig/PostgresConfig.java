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

import com.ongres.pgdeploy.pgconfig.properties.Property;
import com.ongres.pgdeploy.pgconfig.properties.PropertyValue;
import com.ongres.pgdeploy.pgconfig.properties.exceptions.PropertyNotFoundException;
import com.ongres.pgdeploy.pgconfig.properties.exceptions.UnitNotAvailableForPropertyException;
import com.ongres.pgdeploy.pgconfig.properties.exceptions.WrongTypePropertyException;
import net.jcip.annotations.Immutable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Immutable
public class PostgresConfig {

  private final Map<Property, PropertyValue> innerMap;

  private PostgresConfig(Map<Property, PropertyValue> innerMap) {
    this.innerMap = innerMap;
  }

  public Stream<Map.Entry<Property, PropertyValue>> asStream() {
    return innerMap.entrySet().stream();
  }

  /**
   * Reusable builder. After calling build(), it erases its internal state.
   */
  public static class Builder {

    private PropertyParser parser;
    private Map<Property, PropertyValue> innerMap;

    public Builder(PropertyParser parser) {
      this.parser = parser;
      innerMap = new HashMap<>();
    }

    /** Adds the specified property/value pair to the map of properties. If the property doesn't
     * exist, or the value provided is not suitable for it, an exception is thrown.
     * @throws WrongTypePropertyException When there is a type mismatch between the property and
     *     the value.
     * @throws UnitNotAvailableForPropertyException When there is a unit mismatch between the
     *     property and the value.
     * @throws PropertyNotFoundException When the specified property doesn't exist.
     */
    public Builder withProperty(String key, PropertyValue value)
        throws WrongTypePropertyException, UnitNotAvailableForPropertyException,
        PropertyNotFoundException {
      addProperty(key, value);
      return this;
    }

    /** Calls {@link PostgresConfig.Builder#withProperty(String, PropertyValue)}
     *  with value {@link PropertyValue#from(int)}.
     */
    public Builder withProperty(String key, int value)
        throws WrongTypePropertyException, UnitNotAvailableForPropertyException,
        PropertyNotFoundException {
      return withProperty(key, PropertyValue.from(value));
    }

    /** Calls {@link PostgresConfig.Builder#withProperty(String, PropertyValue)}
     *  with value {@link PropertyValue#from(long)}.
     */
    public Builder withProperty(String key, long value)
        throws WrongTypePropertyException, UnitNotAvailableForPropertyException,
        PropertyNotFoundException {
      return withProperty(key, PropertyValue.from(value));
    }

    /** Calls {@link PostgresConfig.Builder#withProperty(String, PropertyValue)}
     *  with value {@link PropertyValue#from(float)}.
     */
    public Builder withProperty(String key, float value)
        throws WrongTypePropertyException, UnitNotAvailableForPropertyException,
        PropertyNotFoundException {
      return withProperty(key, PropertyValue.from(value));
    }

    /** Calls {@link PostgresConfig.Builder#withProperty(String, PropertyValue)}
     *  with value {@link PropertyValue#from(double)}.
     */
    public Builder withProperty(String key, double value)
        throws WrongTypePropertyException, UnitNotAvailableForPropertyException,
        PropertyNotFoundException {
      return withProperty(key, PropertyValue.from(value));
    }

    /** Calls {@link PostgresConfig.Builder#withProperty(String, PropertyValue)}
     *  with value {@link PropertyValue#from(String)}.
     */
    public Builder withProperty(String key, String value)
        throws WrongTypePropertyException, UnitNotAvailableForPropertyException,
        PropertyNotFoundException {
      return withProperty(key, PropertyValue.from(value));
    }

    /** Calls {@link PostgresConfig.Builder#withProperty(String, PropertyValue)}
     *  with value {@link PropertyValue#from(boolean)}.
     */
    public Builder withProperty(String key, boolean value)
        throws WrongTypePropertyException, UnitNotAvailableForPropertyException,
        PropertyNotFoundException {
      return withProperty(key, PropertyValue.from(value));
    }

    /** Adds the specified property/value pair to the map of properties. If the property doesn't
     * exist, it is created. There is also no validation of the value.
     */
    public Builder withPropertyUnsafe(String key, PropertyValue value) {
      addPropertyUnsafe(key, value);
      return this;
    }

    /** Calls {@link PostgresConfig.Builder#withPropertyUnsafe(String, PropertyValue)}
     *  with value {@link PropertyValue#from(int)}.
     */
    public Builder withPropertyUnsafe(String key, int value) {
      return withPropertyUnsafe(key, PropertyValue.from(value));
    }

    /** Calls {@link PostgresConfig.Builder#withPropertyUnsafe(String, PropertyValue)}
     *  with value {@link PropertyValue#from(long)}.
     */
    public Builder withPropertyUnsafe(String key, long value) {
      return withPropertyUnsafe(key, PropertyValue.from(value));
    }

    /** Calls {@link PostgresConfig.Builder#withPropertyUnsafe(String, PropertyValue)}
     *  with value {@link PropertyValue#from(float)}.
     */
    public Builder withPropertyUnsafe(String key, float value) {
      return withPropertyUnsafe(key, PropertyValue.from(value));
    }

    /** Calls {@link PostgresConfig.Builder#withPropertyUnsafe(String, PropertyValue)}
     *  with value {@link PropertyValue#from(double)}.
     */
    public Builder withPropertyUnsafe(String key, double value) {
      return withPropertyUnsafe(key, PropertyValue.from(value));
    }

    /** Calls {@link PostgresConfig.Builder#withPropertyUnsafe(String, PropertyValue)}
     *  with value {@link PropertyValue#from(String)}.
     */
    public Builder withPropertyUnsafe(String key, String value) {
      return withPropertyUnsafe(key, PropertyValue.from(value));
    }

    /** Calls {@link PostgresConfig.Builder#withPropertyUnsafe(String, PropertyValue)}
     *  with value {@link PropertyValue#from(boolean)}.
     */
    public Builder withPropertyUnsafe(String key, boolean value) {
      return withPropertyUnsafe(key, PropertyValue.from(value));
    }


    public Builder fromPropertyMap(Map<String, PropertyValue> properties)
        throws WrongTypePropertyException, UnitNotAvailableForPropertyException,
        PropertyNotFoundException {

      Iterator<Map.Entry<String, PropertyValue>> iterator = properties.entrySet().iterator();

      Map.Entry<String, PropertyValue> next;

      while (iterator.hasNext()) {
        next = iterator.next();
        addProperty(next.getKey(), next.getValue());
      }

      return this;
    }

    public Builder fromPropertyMapUnsafe(Map<String, PropertyValue> properties)
        throws WrongTypePropertyException, UnitNotAvailableForPropertyException {

      Iterator<Map.Entry<String, PropertyValue>> iterator = properties.entrySet().iterator();

      Map.Entry<String, PropertyValue> next;

      while (iterator.hasNext()) {
        next = iterator.next();
        addPropertyUnsafe(next.getKey(), next.getValue());
      }

      return this;
    }

    private void addProperty(String key, PropertyValue value)
        throws WrongTypePropertyException, UnitNotAvailableForPropertyException,
        PropertyNotFoundException {
      Optional<Property> propertyOptional = parser.parse(key);

      if (!propertyOptional.isPresent()) {
        throw new PropertyNotFoundException("Property " + key + " not found");
      }

      Property property = propertyOptional.get();
      property.validate(value);
      innerMap.put(property, value);
    }

    private void addPropertyUnsafe(String key, PropertyValue value) {
      Optional<Property> propertyOptional = parser.parse(key);

      Property property = propertyOptional.orElseGet(() -> Property.fromName(key));
      innerMap.put(property, value);
    }

    public PostgresConfig build() {
      Map<Property, PropertyValue> map = innerMap;
      innerMap = new HashMap<>();
      return new PostgresConfig(map);
    }


  }
}
