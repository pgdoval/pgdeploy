/*
 * This copy of Woodstox XML processor is licensed under the
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 *
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing Woodstox, in file "ASL2.0", under the same directory
 * as this file.
 */
package com.ongres.pgdeploy.installations;


import java.nio.file.Path;

/**
 * Created by pablo on 25/04/17.
 */
public class BadClusterCreationException extends Exception {

  public BadClusterCreationException(String s) {
    super(s);
  }

  public static BadClusterCreationException fromPath(Path path) {

    StringBuilder sb = new StringBuilder();

    sb.append("File ");
    sb.append(path.toString());
    sb.append(" should have been created, but it hasn't");

    return new BadClusterCreationException(sb.toString());
  }
}
