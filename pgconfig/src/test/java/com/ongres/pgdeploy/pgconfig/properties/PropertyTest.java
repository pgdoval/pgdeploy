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
import java.util.Collection;
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
  public PropertyValue<?> objectReceived;

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
    result.add(new Object []{DOUBLE, Unit.noneList, PropertyValue.from(9.3D), 9.3D, NONE, null});
    result.add(new Object []{INTEGER, Unit.noneList, PropertyValue.from(9.3D), 9.3D, NONE, WrongTypePropertyException.class});
    result.add(new Object []{STRING, Unit.noneList, PropertyValue.from(9.3D), 9.3D, NONE, WrongTypePropertyException.class});
    result.add(new Object []{BOOLEAN, Unit.noneList, PropertyValue.from(9.3D), 9.3D, NONE, WrongTypePropertyException.class});
    //Integer comes
    result.add(new Object []{INTEGER, Unit.noneList, PropertyValue.from(9), 9L, NONE, null});
    result.add(new Object []{DOUBLE, Unit.noneList, PropertyValue.from(9), 9L, NONE, WrongTypePropertyException.class});
    result.add(new Object []{STRING, Unit.noneList, PropertyValue.from(9), 9L, NONE, WrongTypePropertyException.class});
    result.add(new Object []{BOOLEAN, Unit.noneList, PropertyValue.from(9), 9L, NONE, WrongTypePropertyException.class});
    //String comes
    result.add(new Object []{STRING, Unit.noneList, PropertyValue.from("value"), "value", NONE, null});
    result.add(new Object []{DOUBLE, Unit.noneList, PropertyValue.from("value"), "value", NONE, WrongTypePropertyException.class});
    result.add(new Object []{INTEGER, Unit.noneList, PropertyValue.from("value"), "value", NONE, WrongTypePropertyException.class});
    result.add(new Object []{BOOLEAN, Unit.noneList, PropertyValue.from("value"), "value", NONE, WrongTypePropertyException.class});
    //Float comes
    result.add(new Object []{DOUBLE, Unit.noneList, PropertyValue.from(9.3F), 9.3D, NONE, null});
    result.add(new Object []{INTEGER, Unit.noneList, PropertyValue.from(9.3F), 9.3D, NONE, WrongTypePropertyException.class});
    result.add(new Object []{STRING, Unit.noneList, PropertyValue.from(9.3F), 9.3D, NONE, WrongTypePropertyException.class});
    result.add(new Object []{BOOLEAN, Unit.noneList, PropertyValue.from(9.3F), 9.3D, NONE, WrongTypePropertyException.class});
    //Long comes
    result.add(new Object []{INTEGER, Unit.noneList, PropertyValue.from(9L), 9L, NONE, null});
    result.add(new Object []{DOUBLE, Unit.noneList, PropertyValue.from(9L), 9L, NONE, WrongTypePropertyException.class});
    result.add(new Object []{STRING, Unit.noneList, PropertyValue.from(9L), 9L, NONE, WrongTypePropertyException.class});
    result.add(new Object []{BOOLEAN, Unit.noneList, PropertyValue.from(9L), 9L, NONE, WrongTypePropertyException.class});
    //Boolean comes
    result.add(new Object []{BOOLEAN, Unit.noneList, PropertyValue.from(true), true, NONE, null});
    result.add(new Object []{DOUBLE, Unit.noneList, PropertyValue.from(true), true, NONE, WrongTypePropertyException.class});
    result.add(new Object []{STRING, Unit.noneList, PropertyValue.from(true), true, NONE, WrongTypePropertyException.class});
    result.add(new Object []{INTEGER, Unit.noneList, PropertyValue.from(true), true, NONE, WrongTypePropertyException.class});


    //Basic unit checking
    //NonString comes and a unit is required
    result.add(new Object []{DOUBLE, Unit.byteList, PropertyValue.from(9.3D), 9.3D, NONE, UnitNotAvailableForPropertyException.class});
    result.add(new Object []{INTEGER, Unit.byteList, PropertyValue.from(9L), 9L, NONE, UnitNotAvailableForPropertyException.class});
    //Different units
    result.add(new Object []{DOUBLE, Unit.byteList, PropertyValue.ms(9.3D), 9.3D, MS, UnitNotAvailableForPropertyException.class});
    result.add(new Object []{DOUBLE, Unit.timeList, PropertyValue.tb(9.3D), 9.3D, TB, UnitNotAvailableForPropertyException.class});
    //Right units
    //Double
    result.add(new Object []{DOUBLE, Unit.timeList, PropertyValue.ms(9.3D), 9.3D, MS, null});
    result.add(new Object []{DOUBLE, Unit.timeList, PropertyValue.sec(9.3D), 9.3D, S, null});
    result.add(new Object []{DOUBLE, Unit.timeList, PropertyValue.min(9.3D), 9.3D, MIN, null});
    result.add(new Object []{DOUBLE, Unit.timeList, PropertyValue.hh(9.3D), 9.3D, H, null});
    result.add(new Object []{DOUBLE, Unit.timeList, PropertyValue.day(9.3D), 9.3D, D, null});
    result.add(new Object []{DOUBLE, Unit.byteList, PropertyValue.tb(9.3D), 9.3D, TB, null});
    result.add(new Object []{DOUBLE, Unit.byteList, PropertyValue.gb(9.3D), 9.3D, GB, null});
    result.add(new Object []{DOUBLE, Unit.byteList, PropertyValue.mb(9.3D), 9.3D, MB, null});
    result.add(new Object []{DOUBLE, Unit.byteList, PropertyValue.kb(9.3D), 9.3D, KB, null});
    //Float
    result.add(new Object []{DOUBLE, Unit.timeList, PropertyValue.ms(9.3F), 9.3D, MS, null});
    result.add(new Object []{DOUBLE, Unit.timeList, PropertyValue.sec(9.3F), 9.3D, S, null});
    result.add(new Object []{DOUBLE, Unit.timeList, PropertyValue.min(9.3F), 9.3D, MIN, null});
    result.add(new Object []{DOUBLE, Unit.timeList, PropertyValue.hh(9.3F), 9.3D, H, null});
    result.add(new Object []{DOUBLE, Unit.timeList, PropertyValue.day(9.3F), 9.3D, D, null});
    result.add(new Object []{DOUBLE, Unit.byteList, PropertyValue.tb(9.3F), 9.3D, TB, null});
    result.add(new Object []{DOUBLE, Unit.byteList, PropertyValue.gb(9.3F), 9.3D, GB, null});
    result.add(new Object []{DOUBLE, Unit.byteList, PropertyValue.mb(9.3F), 9.3D, MB, null});
    result.add(new Object []{DOUBLE, Unit.byteList, PropertyValue.kb(9.3F), 9.3D, KB, null});
    //Integer
    result.add(new Object []{INTEGER, Unit.timeList, PropertyValue.ms(9), 9L, MS, null});
    result.add(new Object []{INTEGER, Unit.timeList, PropertyValue.sec(9), 9L, S, null});
    result.add(new Object []{INTEGER, Unit.timeList, PropertyValue.min(9), 9L, MIN, null});
    result.add(new Object []{INTEGER, Unit.timeList, PropertyValue.hh(9), 9L, H, null});
    result.add(new Object []{INTEGER, Unit.timeList, PropertyValue.day(9), 9L, D, null});
    result.add(new Object []{INTEGER, Unit.byteList, PropertyValue.tb(9), 9L, TB, null});
    result.add(new Object []{INTEGER, Unit.byteList, PropertyValue.gb(9), 9L, GB, null});
    result.add(new Object []{INTEGER, Unit.byteList, PropertyValue.mb(9), 9L, MB, null});
    result.add(new Object []{INTEGER, Unit.byteList, PropertyValue.kb(9), 9L, KB, null});
    //Long
    result.add(new Object []{INTEGER, Unit.timeList, PropertyValue.ms(9L), 9L, MS, null});
    result.add(new Object []{INTEGER, Unit.timeList, PropertyValue.sec(9L), 9L, S, null});
    result.add(new Object []{INTEGER, Unit.timeList, PropertyValue.min(9L), 9L, MIN, null});
    result.add(new Object []{INTEGER, Unit.timeList, PropertyValue.hh(9L), 9L, H, null});
    result.add(new Object []{INTEGER, Unit.timeList, PropertyValue.day(9L), 9L, D, null});
    result.add(new Object []{INTEGER, Unit.byteList, PropertyValue.tb(9L), 9L, TB, null});
    result.add(new Object []{INTEGER, Unit.byteList, PropertyValue.gb(9L), 9L, GB, null});
    result.add(new Object []{INTEGER, Unit.byteList, PropertyValue.mb(9L), 9L, MB, null});
    result.add(new Object []{INTEGER, Unit.byteList, PropertyValue.kb(9L), 9L, KB, null});

    //Nulls
    result.add(new Object []{INTEGER, Unit.byteList, null, 9, TB, IllegalArgumentException.class});


    return result;
  }


  @Test
  public void parse() throws Exception {

    if (expectedException != null) {
      exception.expect(expectedException);
    }

    parser.validate(
        objectReceived,new Property("name", false, type, availableUnits));

    if (objInResult instanceof Double) {
      assertEquals((Double)objInResult, (Double)objectReceived.getValue(), 0.1);
    } else {
      assertEquals(objInResult, objectReceived.getValue());
    }
    assertEquals(unitInResult, objectReceived.getUnit());
  }

}