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

import java.util.concurrent.Callable;
import java.util.function.Consumer;

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
  DetailedReportCategory detail(final @Nonnull String key, final @Nullable Object value);

  /**
   * Sets a complex detail.
   *
   * @param key the key
   * @param consumer the category consumer
   * @return this category
   */
  @Nonnull
  DetailedReportCategory complexDetail(final @Nonnull String key, final @Nonnull Consumer<DetailedReportCategory> consumer);

  /**
   * Sets a detail.
   *
   * @param key the key
   * @param value the value supplier
   * @return this category
   */
  @Nonnull
  default DetailedReportCategory detail(final @Nonnull String key, final @Nonnull Callable<String> value) {
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
