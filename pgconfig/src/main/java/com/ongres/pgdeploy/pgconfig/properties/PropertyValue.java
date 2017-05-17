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

import net.jcip.annotations.Immutable;

 
@Immutable
public class PropertyValue<T> {
  private final T value;
  private final Unit unit;

  private PropertyValue(T value, Unit unit) {
    this.value = value;
    this.unit = unit;
  }

  public T getValue() {
    return value;
  }

  public Unit getUnit() {
    return unit;
  }

  public String toWritableString() {
    return value.toString() + unit.getUnitName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PropertyValue<?> that = (PropertyValue<?>) o;

    if (!value.equals(that.value)) {
      return false;
    }
    return unit == that.unit;
  }

  @Override
  public int hashCode() {
    int result = value.hashCode();
    result = 31 * result + unit.hashCode();
    return result;
  }

  public static PropertyValue<Long> tb(int i) {
    long l = (long) i;
    return tb(l);
  }

  public static PropertyValue<Long> tb(long i) {
    return new PropertyValue<>(i, Unit.TB);
  }

  public static PropertyValue<Double> tb(float i) {
    double l = (double) i;
    return tb(l);
  }

  public static PropertyValue<Double> tb(double i) {
    return new PropertyValue<>(i, Unit.TB);
  }

  public static PropertyValue<Long> gb(int i) {
    long l = (long) i;
    return gb(l);
  }

  public static PropertyValue<Long> gb(long i) {
    return new PropertyValue<>(i, Unit.GB);
  }

  public static PropertyValue<Double> gb(float i) {
    double l = (double) i;
    return gb(l);
  }

  public static PropertyValue<Double> gb(double i) {
    return new PropertyValue<>(i, Unit.GB);
  }

  public static PropertyValue<Long> mb(int i) {
    long l = (long) i;
    return mb(l);
  }

  public static PropertyValue<Long> mb(long i) {
    return new PropertyValue<>(i, Unit.MB);
  }

  public static PropertyValue<Double> mb(float i) {
    double l = (double) i;
    return mb(l);
  }

  public static PropertyValue<Double> mb(double i) {
    return new PropertyValue<>(i, Unit.MB);
  }

  public static PropertyValue<Long> kb(int i) {
    long l = (long) i;
    return kb(l);
  }

  public static PropertyValue<Long> kb(long i) {
    return new PropertyValue<>(i, Unit.KB);
  }

  public static PropertyValue<Double> kb(float i) {
    double l = (double) i;
    return kb(l);
  }

  public static PropertyValue<Double> kb(double i) {
    return new PropertyValue<>(i, Unit.KB);
  }

  public static PropertyValue<Long> ms(int i) {
    long l = (long) i;
    return ms(l);
  }

  public static PropertyValue<Long> ms(long i) {
    return new PropertyValue<>(i, Unit.MS);
  }

  public static PropertyValue<Double> ms(float i) {
    double l = (double) i;
    return ms(l);
  }

  public static PropertyValue<Double> ms(double i) {
    return new PropertyValue<>(i, Unit.MS);
  }

  public static PropertyValue<Long> sec(int i) {
    long l = (long) i;
    return sec(l);
  }

  public static PropertyValue<Long> sec(long i) {
    return new PropertyValue<>(i, Unit.S);
  }

  public static PropertyValue<Double> sec(float i) {
    double l = (double) i;
    return sec(l);
  }

  public static PropertyValue<Double> sec(double i) {
    return new PropertyValue<>(i, Unit.S);
  }

  public static PropertyValue<Long> min(int i) {
    long l = (long) i;
    return min(l);
  }

  public static PropertyValue<Long> min(long i) {
    return new PropertyValue<>(i, Unit.MIN);
  }

  public static PropertyValue<Double> min(float i) {
    double l = (double) i;
    return min(l);
  }

  public static PropertyValue<Double> min(double i) {
    return new PropertyValue<>(i, Unit.MIN);
  }

  public static PropertyValue<Long> hh(int i) {
    long l = (long) i;
    return hh(l);
  }

  public static PropertyValue<Long> hh(long i) {
    return new PropertyValue<>(i, Unit.H);
  }

  public static PropertyValue<Double> hh(float i) {
    double l = (double) i;
    return hh(l);
  }

  public static PropertyValue<Double> hh(double i) {
    return new PropertyValue<>(i, Unit.H);
  }

  public static PropertyValue<Long> day(int i) {
    long l = (long) i;
    return day(l);
  }

  public static PropertyValue<Long> day(long i) {
    return new PropertyValue<>(i, Unit.D);
  }

  public static PropertyValue<Double> day(float i) {
    double l = (double) i;
    return day(l);
  }

  public static PropertyValue<Double> day(double i) {
    return new PropertyValue<>(i, Unit.D);
  }

  public static PropertyValue<Long> from(int i) {
    long l = (long) i;
    return from(l);
  }

  public static PropertyValue<Long> from(long i) {
    return new PropertyValue<>(i, Unit.NONE);
  }

  public static PropertyValue<Double> from(float i) {
    double l = (double) i;
    return from(l);
  }

  public static PropertyValue<Double> from(double i) {
    return new PropertyValue<>(i, Unit.NONE);
  }

  public static PropertyValue<String> from(String i) {
    return new PropertyValue<>(i, Unit.NONE);
  }

  public static PropertyValue<Boolean> from(boolean i) {
    return new PropertyValue<>(i, Unit.NONE);
  }


}
