/*
 * This file is part of Indigo, licensed under the MIT License (MIT).
 *
 * Copyright (c) IchorPowered <http://ichorpowered.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ichorpowered.indigo;

import com.google.gson.JsonObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
  static @NonNull DetailedReport create(final @NonNull String message) {
    return new DetailedReportImpl(message, null);
  }

  /**
   * Creates a new friendly report.
   *
   * @param message the message
   * @param throwable the throwable
   * @return the report
   */
  static @NonNull DetailedReport create(final @NonNull String message, final @Nullable Throwable throwable) {
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
  @NonNull String message();

  /**
   * Gets the throwable.
   *
   * @return the throwable
   */
  @Nullable Throwable throwable();

  /**
   * Gets a category by name.
   *
   * @param name the name
   * @return the category
   */
  @NonNull DetailedReportCategory category(final @NonNull String name);

  /**
   * Throws an {@link DetailedReportedException}.
   */
  void raise();

  /**
   * Gets a JSON representation of this report.
   *
   * @return the json object
   */
  @NonNull JsonObject toJson();

  /**
   * Gets a string representation of this report.
   *
   * @return the string
   */
  @Override
  @NonNull String toString();
}
