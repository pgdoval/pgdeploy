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

/**
 * Created by pablo on 9/05/17.
 */
public interface PropertyValueParser {

  /** Parses a given object into a {@link PropertyValue} representing it.
   * @param obj The object to be parsed. It cannot be null and has to be compliant to one of these
   *            rules:
   *            <ul>
   *            <li>Be an integer, a long, a float, a double, or a boolean. Depending on the
   *            property's <tt>type</tt>, this will be parsed correctly or throw a
   *            {@link WrongTypePropertyException}
   *            <li>Be a String in one of the following styles:
   *            <ul>
   *              <li> <tt>"12"</tt>: Depending on the property's <tt>type</tt>, this will
   *              return a {@link PropertyValue} containing either 12L, 12D or "12".
   *              <li> <tt>"12MB"</tt>: It will return a {@link PropertyValue} whose value is
   *              12D, 12L or even "12", and whose {@link Unit} will be MB.
   *              <li> Boolean values encapsulated as strings have special rules: on, true and 1
   *              will be parsed as true; off, false and 0 will be parsed as false. Any other value
   *              will fail.
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
  PropertyValue parse(Object obj, Property property)
      throws WrongTypePropertyException, UnitNotAvailableForPropertyException;
}
