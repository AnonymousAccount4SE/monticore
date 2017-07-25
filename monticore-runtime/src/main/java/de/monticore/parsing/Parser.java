/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.parsing;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import de.monticore.ast.ASTNode;

import com.google.common.io.CharSource;
import com.google.common.io.Files;

import de.monticore.MontiCoreConstants;
import de.se_rwth.commons.logging.Log;

/**
 * A base class for parsers generated by MontiCore.
 * 
 * @author (last commit) $Author$
 */
public abstract class Parser {
  
  /**
   * Parses the given {@link CharSource}.
   * 
   * @return the ast of the resulting model.
   */
  public abstract ASTNode parse(CharSource model);

  /**
   * Parses the given {@link CharSequence}.
   * 
   * @return the ast of the resulting model.
   */
  public final ASTNode parse(CharSequence model) {
    return parse(CharSource.wrap(Log.errorIfNull(model)));
  }
  
  /**
   * Parses the given {@link InputStream}.
   * 
   * @return the ast of the resulting model.
   */
  public final ASTNode parse(final InputStream model) {
    return parse(new CharSource() {
      @Override
      public Reader openStream() throws IOException {
        return new InputStreamReader(model);
      }
    });
  }

  /**
   * Parses the given {@link File}.
   * 
   * @return the ast of the resulting model.
   */
  public final ASTNode parse(File modelFile) {
    Log.errorIfNull(modelFile);
    checkArgument(modelFile.exists());
    return parse(Files.asCharSource(modelFile, getCharSet()));
  }
  
  /**
   * @return the {@link Charset} used by this parser when reading files.
   */
  protected Charset getCharSet() {
    return MontiCoreConstants.DEFAULT_MODELFILE_CHARSET;
  }

}
