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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.ongres.pgdeploy.pgconfig.properties.DataType.*;
import static com.ongres.pgdeploy.pgconfig.properties.Unit.*;
import static org.junit.Assert.*;

/**
 * Created by pablo on 28/04/17.
 */

@RunWith(Parameterized.class)
public class PropertyTest {

  @Parameterized.Parameter(0)
  public DataType type;

  @Parameterized.Parameter(1)
  public List<Unit> availableUnits;

  @Parameterized.Parameter(2)
  public Object objectReceived;

  @Parameterized.Parameter(3)
  public Object objInResult;

  @Parameterized.Parameter(4)
  public Unit unitInResult;

  @Parameterized.Parameter(5)
  public Class<Exception> expectedException;

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  private final PropertyValueParser parser = DefaultPropertyValueParser.getInstance();


  @Parameterized.Parameters
  public static Collection getParams() {
    List<Object[]> result = new ArrayList<>();

    List<Integer> emptyList = new ArrayList<>();

    //Basic type checking without units
    //Double comes
    result.add(new Object []{DOUBLE, Unit.noneList, 9.3D, 9.3D, NONE, null});
    result.add(new Object []{INTEGER, Unit.noneList, 9.3D, 9.3D, NONE, WrongTypePropertyException.class});
    result.add(new Object []{STRING, Unit.noneList, 9.3D, 9.3D, NONE, WrongTypePropertyException.class});
    result.add(new Object []{BOOLEAN, Unit.noneList, 9.3D, 9.3D, NONE, WrongTypePropertyException.class});
    //Integer comes
    result.add(new Object []{INTEGER, Unit.noneList, 9, 9, NONE, null});
    result.add(new Object []{DOUBLE, Unit.noneList, 9, 9, NONE, WrongTypePropertyException.class});
    result.add(new Object []{STRING, Unit.noneList, 9, 9, NONE, WrongTypePropertyException.class});
    result.add(new Object []{BOOLEAN, Unit.noneList, 9, 9, NONE, WrongTypePropertyException.class});
    //String comes
    result.add(new Object []{STRING, Unit.noneList, "value", "value", NONE, null});
    result.add(new Object []{DOUBLE, Unit.noneList, "value", "value", NONE, WrongTypePropertyException.class});
    result.add(new Object []{INTEGER, Unit.noneList, "value", "value", NONE, WrongTypePropertyException.class});
    result.add(new Object []{BOOLEAN, Unit.noneList, "value", "value", NONE, WrongTypePropertyException.class});
    //Unexpected type
    result.add(new Object []{INTEGER, Unit.noneList, emptyList, "value", NONE, WrongTypePropertyException.class});
    result.add(new Object []{DOUBLE, Unit.noneList, emptyList, "value", NONE, WrongTypePropertyException.class});
    result.add(new Object []{BOOLEAN, Unit.noneList, emptyList, "value", NONE, WrongTypePropertyException.class});
    //Float comes
    result.add(new Object []{DOUBLE, Unit.noneList, 9.3F, 9.3F, NONE, null});
    result.add(new Object []{INTEGER, Unit.noneList, 9.3F, 9.3F, NONE, WrongTypePropertyException.class});
    result.add(new Object []{STRING, Unit.noneList, 9.3F, 9.3F, NONE, WrongTypePropertyException.class});
    result.add(new Object []{BOOLEAN, Unit.noneList, 9.3F, 9.3F, NONE, WrongTypePropertyException.class});
    //Long comes
    result.add(new Object []{INTEGER, Unit.noneList, 9L, 9L, NONE, null});
    result.add(new Object []{DOUBLE, Unit.noneList, 9L, 9L, NONE, WrongTypePropertyException.class});
    result.add(new Object []{STRING, Unit.noneList, 9L, 9L, NONE, WrongTypePropertyException.class});
    result.add(new Object []{BOOLEAN, Unit.noneList, 9L, 9L, NONE, WrongTypePropertyException.class});
    //Boolean comes
    result.add(new Object []{BOOLEAN, Unit.noneList, true, true, NONE, null});
    result.add(new Object []{DOUBLE, Unit.noneList, true, true, NONE, WrongTypePropertyException.class});
    result.add(new Object []{STRING, Unit.noneList, true, true, NONE, WrongTypePropertyException.class});
    result.add(new Object []{INTEGER, Unit.noneList, true, true, NONE, WrongTypePropertyException.class});


    //Basic unit checking
    //NonString comes and a unit is required
    result.add(new Object []{DOUBLE, Unit.byteList, 9.3D, 9.3D, NONE, UnitNotAvailableForPropertyException.class});
    result.add(new Object []{INTEGER, Unit.byteList, 9.3D, 9.3D, NONE, UnitNotAvailableForPropertyException.class});
    //Valid unitless values encapsulated in the string
    result.add(new Object []{INTEGER, Unit.noneList, "9", 9L, NONE, null});
    result.add(new Object []{DOUBLE, Unit.noneList, "9", 9D, NONE, null});
    result.add(new Object []{DOUBLE, Unit.noneList, "9.5", 9.5D, NONE, null});
    result.add(new Object []{BOOLEAN, Unit.noneList, "true", true, NONE, null});
    result.add(new Object []{BOOLEAN, Unit.noneList, "on", true, NONE, null});
    result.add(new Object []{BOOLEAN, Unit.noneList, "1", true, NONE, null});
    result.add(new Object []{BOOLEAN, Unit.noneList, "false", false, NONE, null});
    result.add(new Object []{BOOLEAN, Unit.noneList, "off", false, NONE, null});
    result.add(new Object []{BOOLEAN, Unit.noneList, "0", false, NONE, null});
    //Units encapsulated in the string
    result.add(new Object []{INTEGER, Unit.byteList, "9TB", 9L, TB, null});
    result.add(new Object []{DOUBLE, Unit.byteList, "9TB", 9D, TB, null});
    result.add(new Object []{DOUBLE, Unit.byteList, "9.5TB", 9.5D, TB, null});
    result.add(new Object []{STRING, Unit.byteList, "MBTB", "MB", TB, null});
    //Invalid values encapsulated in a string
    result.add(new Object []{DOUBLE, Unit.noneList, "9.5TB", 9.5D, TB, UnitNotAvailableForPropertyException.class});
    result.add(new Object []{DOUBLE, Unit.byteList, "9ert5TB", 9.5D, TB, WrongTypePropertyException.class});
    result.add(new Object []{INTEGER, Unit.byteList, "9caucaucau", 9.3D, NONE, UnitNotAvailableForPropertyException.class});
    //In case of 2 units available, the longest one available is chosen (ms over s)
    result.add(new Object []{INTEGER, Unit.timeList, "9ms", 9L, MS, null});


    //Trimming
    result.add(new Object []{INTEGER, Unit.byteList, " 9 TB", 9L, TB, null});
    result.add(new Object []{INTEGER, Unit.noneList, " 9 ", 9L, NONE, null});

    //Nulls
    result.add(new Object []{INTEGER, Unit.byteList, null, 9, TB, IllegalArgumentException.class});


    return result;
  }


  @Test
  public void parse() throws Exception {

    if (expectedException != null) {
      exception.expect(expectedException);
    }

    PropertyValue propertyValue = parser.parse(
        objectReceived,new Property("name", false, type, availableUnits));

    assertEquals(objInResult, propertyValue.getValue());
    assertEquals(unitInResult, propertyValue.getUnit());
  }

}