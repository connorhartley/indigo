/*
 * This file is part of indigo, licensed under the MIT License.
 *
 * Copyright (c) 2017 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.indigo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A friendly report.
 */
public interface DetailedReport {

  /**
   * Creates a new friendly report.
   *
   * @param message the message
   * @return the report
   */
  @Nonnull
  static DetailedReport create(@Nonnull final String message) {
    return new DetailedReportImpl(message, null);
  }

  /**
   * Creates a new friendly report.
   *
   * @param message the message
   * @param throwable the throwable
   * @return the report
   */
  @Nonnull
  static DetailedReport create(@Nonnull final String message, @Nullable final Throwable throwable) {
    final DetailedReport report;
    if(throwable instanceof DetailedReportedException) {
      report = ((DetailedReportedException) throwable).report();
    } else {
      report = new DetailedReportImpl(message, throwable);
    }
    return report;
  }

  /**
   * Gets the message.
   *
   * @return the message
   */
  @Nonnull
  String message();

  /**
   * Gets the throwable.
   *
   * @return the throwable
   */
  @Nullable
  Throwable throwable();

  /**
   * Gets a category by name.
   *
   * @param name the name
   * @return the category
   */
  @Nonnull
  DetailedReportCategory category(@Nonnull final String name);

  /**
   * Throws an {@link DetailedReportedException}.
   */
  void raise();

  /**
   * Gets a string representation of this report.
   *
   * @return the string
   */
  @Nonnull
  @Override
  String toString();
}
