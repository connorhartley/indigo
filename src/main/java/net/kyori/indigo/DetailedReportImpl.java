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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class DetailedReportImpl implements DetailedReport {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private final String message;
  @Nullable private final Throwable throwable;
  private final Map<String, DetailedReportCategoryImpl> categories = new LinkedHashMap<>();

  DetailedReportImpl(@Nonnull final String message, @Nullable final Throwable throwable) {
    this.message = message;
    this.throwable = throwable;
  }

  @Nonnull
  @Override
  public String message() {
    return this.message;
  }

  @Nullable
  @Override
  public Throwable throwable() {
    return this.throwable;
  }

  @Nonnull
  @Override
  public DetailedReportCategory category(@Nonnull final String name) {
    return this.categories.computeIfAbsent(name, key -> new DetailedReportCategoryImpl(name));
  }

  @Override
  public void raise() {
    throw new DetailedReportedException(this);
  }

  @Nonnull
  @Override
  public String toString() {
    final JsonObject object = new JsonObject();
    object.addProperty("instant", Instant.now().toString());
    object.addProperty("message", this.message);
    if(this.throwable != null) {
      object.add("throwable", throwable(this.throwable));
    }
    final JsonObject details = new JsonObject();
    this.appendCategories(details);
    object.add("details", details);
    return GSON.toJson(object);
  }

  private static JsonObject throwable(final Throwable throwable) {
    final JsonObject object = new JsonObject();
    object.addProperty("type", throwable.getClass().getName());
    object.addProperty("message", throwable.getMessage());
    final JsonArray stack = new JsonArray();
    for(final StackTraceElement element : throwable.getStackTrace()) {
      stack.add(element.toString());
    }
    object.add("stack", stack);
    final Throwable cause = throwable.getCause();
    if(cause != null) {
      object.add("cause", throwable(cause));
    }
    return object;
  }

  private void appendCategories(final JsonObject sb) {
    for(final DetailedReportCategoryImpl category : this.categories.values()) {
      sb.add(category.name, category.write());
    }
  }

  private final class DetailedReportCategoryImpl implements DetailedReportCategory {

    private final String name;
    private final List<Entry> entries = new ArrayList<>();

    DetailedReportCategoryImpl(final String name) {
      this.name = name;
    }

    @Nonnull
    @Override
    public DetailedReportCategory detail(@Nonnull final String key, @Nullable final Object value) {
      this.entries.add(new Entry(key, value));
      return this;
    }

    @Nonnull
    @Override
    public DetailedReport then() {
      return DetailedReportImpl.this;
    }

    JsonObject write() {
      final JsonObject object = new JsonObject();
      for(final Entry entry : this.entries) {
        if (entry.value instanceof JsonElement) {
          object.add(entry.key, (JsonElement) entry.value);
        } else {
          object.addProperty(entry.key, String.valueOf(entry.value));
        }
      }
      return object;
    }

    private final class Entry {

      private final String key;
      private final Object value;

      Entry(final String key, @Nullable final Object value) {
        this.key = key;

        // Get the string representation of the value now, as the object may change when actually outputting
        if(value == null) {
          this.value = "~~NULL~~";
        } else if(value instanceof Throwable) {
          this.value = throwable((Throwable) value);
        } else {
          this.value = value.toString();
        }
      }
    }
  }
}
