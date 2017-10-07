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

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A report category.
 */
public interface DetailedReportCategory {

  /**
   * Sets a detail.
   *
   * @param key the key
   * @param value the value
   * @return this category
   */
  @Nonnull
  DetailedReportCategory detail(@Nonnull final String key, @Nullable final Object value);

  /**
   * Sets a detail.
   *
   * @param key the key
   * @param value the value supplier
   * @return this category
   */
  @Nonnull
  default DetailedReportCategory detail(@Nonnull final String key, @Nonnull final Callable<String> value) {
    try {
      return this.detail(key, value.call());
    } catch(final Throwable t) {
      return this.detail(key, t);
    }
  }

  /**
   * Gets the report.
   *
   * @return the report
   */
  @Nonnull
  DetailedReport then();
}
